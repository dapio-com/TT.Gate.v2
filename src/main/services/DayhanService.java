package main.services;


import main.builders.DayhanBuilder;
import main.builders.SendBackBuilder;
import main.connections.ToHostConnection;
import main.util.Converter;
import main.util.Reader;
import main.util.Sender;
import org.apache.log4j.Logger;
import java.util.ArrayList;

public class DayhanService {


    private final Logger log = Logger.getLogger(DayhanService.class);

    private ToHostConnection toDayhanConnection;
    private byte[] bytes;
    private String tid;

    private ArrayList<String> f48;
    private TerminalService terminalService;


    public DayhanService (ToHostConnection toHostConnection, TerminalService terminalService){
        this.toDayhanConnection = toHostConnection;

        this.tid = Converter.bytesToString(terminalService.getParsedBody().get(41));
        this.f48 = terminalService.getParsedField48();
        this.terminalService = terminalService;



    }

    public void closeConnection() {
        this.toDayhanConnection.closeConnection();
    }


//    private byte[] verifyBuild(){
//        Builder builder = new Builder();
//        return builder.dayhanVerifyBuild(toDayhanConnection.getParsedBody(), f48);
//    }
//
//    private byte[] payBuild(){
//        Builder builder = new Builder();
//        return builder.dayhanPaidBuild(toDayhanConnection.getParsedBody(), f48);
//    }
//
//
//    private byte[] cancelBuild(){
//        Builder builder = new Builder();
//        return builder.dayhanCancelBuild(toDayhanConnection.getParsedBody());
//    }




    public void sendTo(ArrayList<ArrayList<Byte>> parsedBody, String operation, long startTime){

        String mti = null;

        DayhanBuilder dayhanBuilder = new DayhanBuilder();

        if(operation.equals("VERIFY")){
            mti = "VERF";
            bytes = dayhanBuilder.dayhanVerifyBuild(toDayhanConnection.getIp(), parsedBody, f48);
        }

        if(operation.equals("PAID")){
            mti = "PAID";
            bytes = dayhanBuilder.dayhanPaidBuild(toDayhanConnection.getIp(), parsedBody, f48);
        }

        if(operation.equals("CANCEL")){
            mti = "CNCL";
            bytes = dayhanBuilder.dayhanCancelBuild(toDayhanConnection.getIp(), parsedBody);
        }

        //System.out.println("BUILDED " + Arrays.toString(bytes));
        if(bytes != null){
            Sender.sendTo(
                    toDayhanConnection.getOs(),
                    bytes,
                    toDayhanConnection.getIp(),
                    mti,
                    tid,
                    "DAYHAN_SERVICE",
                    startTime
            );

        }
    }


    public byte[] readBytes(long startTime) {

        try{
            bytes = Reader.readFrom(
                    toDayhanConnection.getIs(),
                    toDayhanConnection.getIp(),
                    "DAYHAN_SERVICE",
                    tid,
                    ""
            );

            return bytes;
        } catch (Exception err){
            log.error(toDayhanConnection.getIp() + " [TID] " + tid + " ERROR: READ BYTES FROM DAYHAN_SERVICE ERROR\n", err);
            //sendErrorToTerminal("721", startTime);
            sendErrorToTerminal(startTime);
            closeConnection();
            return null;
        }



    }

    public byte[] getBytes() {
        return bytes;
    }


    //private void sendErrorToTerminal(String errCode, long startTime){
    private void sendErrorToTerminal(long startTime){
        SendBackBuilder sendBackBuilder = new SendBackBuilder();
        Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), toDayhanConnection.getIp(), "721"), toDayhanConnection.getIp(), "0210", tid, "TERMINAL", startTime);

    }
}
