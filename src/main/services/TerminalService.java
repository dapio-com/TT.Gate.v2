package main.services;

import main.builders.SendBackBuilder;
import main.builders.UtilityPaymentBuilder;
import main.data.IncomingData;
import main.repo.DBConnection;
import main.repo.SaveError;
import main.repo.TerminalRepo;
import main.util.Converter;
import main.data.DataParser;
import main.util.Reader;
import main.util.Sender;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class TerminalService {

    private final Logger log = Logger.getLogger(TerminalService.class);

    private String ip;
    private Socket terminalConnection;
    private String mti;
    private boolean uPayment;
    private String tid;
    private String step;
    private String aaServiceTerminal;
    private byte[] bytes;

    private ArrayList<ArrayList<Byte>> parsedBody;
    private ArrayList<String> parsedField48;



    public TerminalService(String ip, Socket terminalConnection, String aaServiceTerminal) {
        this.ip = ip;
        this.terminalConnection = terminalConnection;
        this.tid = null;
        this.mti = null;
        this.uPayment = false;
        this.step = "";
        this.parsedField48 = null;
        this.aaServiceTerminal = aaServiceTerminal;
    }

    public void setTid() {
        this.tid = Converter.bytesToString(parsedBody.get(41));
    }
    public void setMti() {
        this.mti = Converter.bytesToString(parsedBody.get(1));
    }

    public boolean isUPayment() {
        return uPayment;
    }

    public String getTid() {
        return tid;
    }
    public String getMti() {
       return mti;
    }

    public void setStep(String step) {
        this.step = step;
    }



    public void closeConnection() {
        try {
            this.terminalConnection.close();
        } catch (IOException e) {
            log.error(ip + " ERROR: CAN`T CLOSE TERMINAL CONNECTION");
            SaveError.errorToDb(ip, null, "CAN`T CLOSE TERMINAL CONNECTION");
        }
    }

    public InputStream getIs() {
        try {
            //is = terminalConnection.getInputStream();
            return terminalConnection.getInputStream();
        } catch (Exception err) {
            log.error(ip + " ERROR: CAN`T GET STREAMS FROM TERMINAL", err);
            SaveError.errorToDb(ip, null, "CAN`T GET STREAMS FROM TERMINAL");
            closeConnection();
            return null;
        }
    }

    public OutputStream getOs() {
        try {
            //os = terminalConnection.getOutputStream();
            return terminalConnection.getOutputStream();
        } catch (Exception err) {
            log.error(ip + " ERROR: CAN`T GET STREAMS FROM TERMINAL", err);
            SaveError.errorToDb(ip, null, "CAN`T GET STREAMS FROM TERMINAL");
            closeConnection();
            return null;
        }
    }



    public byte[] readBytes() {

        try {
            bytes = Reader.readFrom(getIs(), ip, "TERMINAL", tid, step);
            return bytes;
        } catch (Exception err) {
            log.error(ip + " ERROR: READ BYTES FROM TERMINAL ERROR\n", err);
            SaveError.errorToDb(ip, null, "CAN`T READ BYTES FROM TERMINAL ERROR");
            closeConnection();
            return null;
        }

    }

    public ArrayList<ArrayList<Byte>> parseBytes() {
        IncomingData data = new IncomingData(ip, "TERMINAL", step, bytes);
        try {
            parsedBody = data.getParsedBody();
            uPayment = parsedBody.get(3) != null && Converter.bytesToString(parsedBody.get(3)).equals("500000");
            return parsedBody;
        } catch (Exception err) {
            log.error(ip + " ERROR: CAN`T PARSE BYTES FROM TERMINAL " + step + " !\n", err);
            SaveError.errorToDb(ip, null, "CAN`T PARSE BYTES FROM TERMINAL");
            closeConnection();
            return null;
        }

    }

    public void parseField48(){

        parsedField48 = DataParser.parseField48(Converter.bytesToString(parsedBody.get(48)));

    }

    public ArrayList<String> getParsedField48() {

        return parsedField48;

    }

    public boolean abortRetailing(long startTime) {

        if (Converter.bytesToString(parsedBody.get(1)).equals("0200") &&
                Converter.bytesToString(parsedBody.get(3)).equals("000000") &&
                Converter.bytesToString(parsedBody.get(41)).equals(aaServiceTerminal)
        ) {

            SendBackBuilder sendBackBuilder = new SendBackBuilder();
            Sender.sendTo(getOs(), sendBackBuilder.sendBack0210Build(parsedBody, ip, "711"), ip, "0210", tid, "TERMINAL", startTime);
            log.info(ip + " [TID] " + aaServiceTerminal + " RETAIL IS PROHIBITED FOR AA TERMINAL (ABORT) !");
            closeConnection();
            return true;


        }
        return false;

    }

    public boolean abortCancelation(long startTime){

        if (getMti().equals("0400") && getTid().equals(aaServiceTerminal)
        ){
            SendBackBuilder sendBackBuilder = new SendBackBuilder();
            Sender.sendTo(getOs(), sendBackBuilder.sendBack0210Build(parsedBody, ip, "712"), ip, "0210", tid, "TERMINAL", startTime);
            log.info(ip + " [TID] " + aaServiceTerminal + " CANCELATION IS ABORTED !");
            closeConnection();
            return true;
        }

        return false;
    }



    private byte[] uPayment0210Build(ArrayList<ArrayList<Byte>> parsedBodyH){
        UtilityPaymentBuilder utilityPaymentBuilder = new UtilityPaymentBuilder();
        return utilityPaymentBuilder.uPayment0210Build(parsedBodyH, ip, getTid());
    }

    private byte[] uPaymentCancel0410Build(ArrayList<ArrayList<Byte>> parsedBodyH){
        UtilityPaymentBuilder utilityPaymentBuilder = new UtilityPaymentBuilder();
        return utilityPaymentBuilder.uPaymentCancel0410Build(parsedBodyH, ip, getTid());
    }

    public void sendTo(byte[] bytes, long startTime, ArrayList<ArrayList<Byte>> parsedBodyH, String mti){

        if (mti.equals("0210") && uPayment) {
            bytes = uPayment0210Build(parsedBodyH);
        }

        if (mti.equals("0410") && uPayment) {
            bytes = uPaymentCancel0410Build(parsedBodyH);
        }

        Sender.sendTo(
                getOs(),
                bytes,
                ip,
                mti,
                tid,
                "TERMINAL",
                startTime
        );
    }

    public ArrayList<ArrayList<Byte>> getParsedBody() {
        return parsedBody;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public boolean isOutOfService(String tid, String ip, String dbIp, String dbPort, String dbName, String dbUser, String dbPassword){

        try {
            DBConnection dbConnection = new DBConnection(dbIp, dbPort, dbName, dbUser, dbPassword);
            TerminalRepo terminalRepo = new TerminalRepo(dbConnection);
            return terminalRepo.isTerminalNotActive(tid, ip);
        } catch (Exception e){
            log.error(ip + " [TID] ERROR. OUT OF SERVICE CHECKING. UNABLE CONNECT TO DATABASE ! :(", e);
            return true;
        }

    }


}
