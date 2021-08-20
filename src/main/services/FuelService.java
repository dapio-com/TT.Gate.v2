package main.services;

import main.builders.SendBackBuilder;
import main.builders.FuelBuilder;
import main.connections.ToHostConnection;
import main.util.Converter;
import main.util.Reader;
import main.util.Sender;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class FuelService {


    private final Logger log = Logger.getLogger(FuelService.class);

    private ToHostConnection toFuelConnection;
    private byte[] bytes_to;
    private byte[] bytes_from;
    private String tid;
    private String mti;

    private ArrayList<String> f48;
    private TerminalService terminalService;


    public FuelService (ToHostConnection toHostConnection, TerminalService terminalService){
        this.toFuelConnection = toHostConnection;

        this.tid = Converter.bytesToString(terminalService.getParsedBody().get(41));
        this.f48 = terminalService.getParsedField48();
        this.terminalService = terminalService;
        this.mti = null;



    }

    public void closeConnection() {
        this.toFuelConnection.closeConnection();
    }


    public void sendTo(ArrayList<ArrayList<Byte>> parsedBodyT, ArrayList<ArrayList<Byte>> parsedBodyH, String operation, long startTime){

        //String mti = null;


        FuelBuilder fuelBuilder = new FuelBuilder();

        if(operation.equals("FUEL_ORG_CREDIT")){
            mti = "FOCR"; // FuelOrgCRedit
            bytes_to = fuelBuilder.fuelPacketBuild(toFuelConnection.getIp(), mti, parsedBodyT, f48);
        }

        if(operation.equals("FUEL_ORG_CREDIT_BACK")){
            mti = "FOCB"; // FuelOrgUNcredit
            bytes_to = fuelBuilder.fuelPacketBuild(toFuelConnection.getIp(), mti, parsedBodyT, f48);
        }

        if(operation.equals("FUEL_ORG_BALANCE")){
            mti = "FOBA"; // FuelOrgBAlance
            bytes_to = fuelBuilder.fuelPacketBuild(toFuelConnection.getIp(), mti, parsedBodyT, f48);
        }

        if(operation.equals("FUEL_CARD_CREDIT")){
            mti = "FCCR"; // FuelCardCRedit
            bytes_to = fuelBuilder.fuelPacketBuild(toFuelConnection.getIp(), mti, parsedBodyT, f48);
        }

        if(operation.equals("FUEL_CARD_CREDIT_BACK")){
            mti = "FCCB"; // FuelCardCreditBack
            bytes_to = fuelBuilder.fuelPacketBuild(toFuelConnection.getIp(), mti, parsedBodyT, f48);
        }

        if(operation.equals("FUEL_CARD_VERIFY")){
            mti = "FVER"; // FuelVerify
            bytes_to = fuelBuilder.fuelPacketBuild(toFuelConnection.getIp(), mti, parsedBodyT, f48);
        }

        if(operation.equals("PAID")){
            mti = "FPAY"; // FuelPAID
            bytes_to = fuelBuilder.fuelPaidBuild(toFuelConnection.getIp(), mti, parsedBodyT, parsedBodyH, f48);
        }



//        if(operation.equals("FUEL_CARD_CREDIT")){
//            mti = "FCCR"; // FuelCardCRedit
//            bytes_to = fuelBuilder.fuelCardCreditBuild(toFuelConnection.getIp(), parsedBody, f48);
//        }

        //System.out.println("BUILDED " + Arrays.toString(bytes));
        if(bytes_to != null){
            Sender.sendTo(
                    toFuelConnection.getOs(),
                    bytes_to,
                    toFuelConnection.getIp(),
                    mti,
                    tid,
                    "FUEL_SERVICE",
                    startTime
            );

        }
    }

    //TODO FOR BALANCE REQUEST
//    public void sendTo_orgBalanceRequest(ArrayList<ArrayList<Byte>> parsedBody, long startTime){
//
//        FuelBuilder fuelBuilder = new FuelBuilder();
//
//        mti = "FOBA";
//        bytes_to = fuelBuilder.fuelPacketBuild(toFuelConnection.getIp(), mti, parsedBody, f48);
//
//        if(bytes_to != null){
//            Sender.sendTo(
//                    toFuelConnection.getOs(),
//                    bytes_to,
//                    toFuelConnection.getIp(),
//                    mti,
//                    tid,
//                    "FUEL_SERVICE",
//                    startTime
//            );
//
//        }
//    }




    public byte[] readBytes(long startTime) {

        try{
            bytes_from = Reader.readFrom(
                    toFuelConnection.getIs(),
                    toFuelConnection.getIp(),
                    "FUEL_SERVICE",
                    tid,
                    ""
            );

            return bytes_from;
        } catch (Exception err){
            log.error(toFuelConnection.getIp() + " [TID] " + tid + " ERROR: READ BYTES FROM FUEL_SERVICE ERROR\n", err);
            sendErrorToTerminal(startTime);
            closeConnection();
            return null;
        }



    }

    public byte[] getBytes() {
        return bytes_from;
    }


    //private void sendErrorToTerminal(String errCode, long startTime){
    private void sendErrorToTerminal(long startTime){
        SendBackBuilder sendBackBuilder = new SendBackBuilder();
        Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), toFuelConnection.getIp(), "731"), toFuelConnection.getIp(), "0210", tid, "TERMINAL", startTime);

    }


}
