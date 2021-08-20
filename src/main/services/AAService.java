package main.services;


import main.builders.AABuilder;
import main.builders.SendBackBuilder;
import main.connections.ToHostConnection;
import main.util.Converter;
import main.util.Reader;
import main.util.Sender;
import org.apache.log4j.Logger;
import java.util.ArrayList;

public class AAService {


    private final Logger log = Logger.getLogger(AAService.class);

    private ToHostConnection toAAConnection;
    private byte[] bytes;
    private String tid;

    private ArrayList<String> f48;
    private TerminalService terminalService;


    public AAService (ToHostConnection toHostConnection, TerminalService terminalService){
        this.toAAConnection = toHostConnection;

        this.tid = Converter.bytesToString(terminalService.getParsedBody().get(41));
        this.f48 = terminalService.getParsedField48();
        this.terminalService = terminalService;



    }

    public void closeConnection() {
        this.toAAConnection.closeConnection();
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

        AABuilder aaBuilder = new AABuilder();

        if(operation.equals("VERIFY")){
            mti = "VERF";
            bytes = aaBuilder.aaVerifyBuild(toAAConnection.getIp(), parsedBody, f48);
        }

        if(operation.equals("GPAY")){
            mti = "GPAY";
            bytes = aaBuilder.aaGenPayBuild(toAAConnection.getIp(), parsedBody, f48);
        }

        //System.out.println("BUILDED " + Arrays.toString(bytes));
        if(bytes != null){
            Sender.sendTo(
                    toAAConnection.getOs(),
                    bytes,
                    toAAConnection.getIp(),
                    mti,
                    tid,
                    "AA_SERVICE",
                    startTime
            );

        }
    }


    public byte[] readBytes(long startTime) {

        try{
            bytes = Reader.readFrom(
                    toAAConnection.getIs(),
                    toAAConnection.getIp(),
                    "AA_SERVICE",
                    tid,
                    ""
            );

            return bytes;
        } catch (Exception err){
            log.error(toAAConnection.getIp() + " [TID] " + tid + " ERROR: READ BYTES FROM AA_SERVICE ERROR\n", err);
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
        Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), toAAConnection.getIp(), "714"), toAAConnection.getIp(), "0210", tid, "TERMINAL", startTime);

    }
}
