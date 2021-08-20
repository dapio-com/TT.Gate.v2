package main.services;


import main.builders.SendBackBuilder;
import main.builders.UtilityPaymentBuilder;
import main.connections.ToHostConnection;
import main.data.IncomingData;
import main.repo.SaveError;
import main.util.Converter;
import main.util.Reader;
import main.util.Sender;
import org.apache.log4j.Logger;

import java.util.ArrayList;


public class HostService {

    private final Logger log = Logger.getLogger(HostService.class);

    private ToHostConnection toHostConnection;
    private byte[] bytes;
    private String mti;
    private String tid;
    private String step;
    private ArrayList<ArrayList<Byte>> parsedBody;
    private TerminalService terminalService;



    public HostService (ToHostConnection toHostConnection, TerminalService terminalService){
        this.toHostConnection = toHostConnection;
        this.mti = null;
        this.tid = Converter.bytesToString(toHostConnection.getParsedBody().get(41));
        this.step = "";
        this.terminalService = terminalService;
    }

    public String getTid(){return tid;}
    public String getMti(){return mti;}

    public void setMti(){this.mti = Converter.bytesToString(getParsedBody().get(1));}

    public void setStep(String step) {
        this.step = step;
    }

    public void closeConnection() {
        this.toHostConnection.closeConnection();
    }


    private byte[] purchase0200Build(){
        UtilityPaymentBuilder utilityPaymentBuilder = new UtilityPaymentBuilder();
        return utilityPaymentBuilder.purchase0200Build(toHostConnection.getParsedBody(), toHostConnection.getIp());

    }

    private byte[] cancel0400Build(){
        UtilityPaymentBuilder utilityPaymentBuilder = new UtilityPaymentBuilder();
        return utilityPaymentBuilder.cancel0400Build(toHostConnection.getParsedBody(), toHostConnection.getIp());
    }

    public void sendTo(byte[] bytes, long startTime){

        if (terminalService.getMti().equals("0200") && terminalService.isUPayment()) {
            bytes = purchase0200Build();
        }

        if (terminalService.getMti().equals("0400") && terminalService.isUPayment()) {
            bytes = cancel0400Build();
        }


        Sender.sendTo(
                toHostConnection.getOs(),
                bytes,
                toHostConnection.getIp(),
                terminalService.getMti(),
                tid,
                "HOST",
                startTime
        );
    }

    public byte[] readBytes(long startTime) {


        try{
            bytes = Reader.readFrom(
                    toHostConnection.getIs(),
                    toHostConnection.getIp(),
                    "HOST",
                    tid,
                    step
            );

            if(!isAuthorized(bytes, startTime)){
                return null;
            }

            return bytes;
        } catch (Exception err){
            log.error(toHostConnection.getIp() + " [TID] " + terminalService.getTid() + " ERROR: READ BYTES FROM HOST ERROR\n", err);
            SaveError.errorToDb(toHostConnection.getIp(), terminalService.getTid(), "CAN`T READ BYTES FROM HOST");
            sendErrorToTerminal("701", startTime);
            closeConnection();
            return null;
        }



    }

    public ArrayList<ArrayList<Byte>> parseBytes(long startTime) {
        IncomingData data = new IncomingData(toHostConnection.getIp(), "HOST", step, bytes);

        try {
            parsedBody = data.getParsedBody();
            return parsedBody;
        } catch (Exception err) {
            log.error(toHostConnection.getIp() + " ERROR: CAN`T PARSE BYTES FROM HOST " + step + " !\n", err);
            SaveError.errorToDb(toHostConnection.getIp(), null, "CAN`T PARSE BYTES FROM HOST");
            sendErrorToTerminal("702", startTime);
            closeConnection();
            return null;
        }

    }

    public ArrayList<ArrayList<Byte>> getParsedBody() {
        return parsedBody;
    }

    public byte[] getBytes() {
        return bytes;
    }

    private boolean isAuthorized(byte[] bytes, long startTime){

        if(bytes.length <= 17){
            log.info(toHostConnection.getIp() +
                    " [TID] " +
                    tid +
                    " " +
                    bytes.length +
                    " BYTES FROM HOST RECEIVED ! NOT AUTHORIZED !");
            if(Converter.bytesToString(toHostConnection.getParsedBody().get(1)).equals("0200")){
                sendErrorToTerminal("703", startTime);
            }
            closeConnection();
            return false;
        }
        return true;
    }

    private void sendErrorToTerminal(String errCode, long startTime){
        SendBackBuilder sendBackBuilder = new SendBackBuilder();
        Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), toHostConnection.getIp(), errCode), toHostConnection.getIp(), "0210", tid, "TERMINAL", startTime);

    }
}
