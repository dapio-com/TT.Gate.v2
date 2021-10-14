package main;

import main.connections.ToHostConnection;
import main.repo.SaveOp;
import main.services.*;
import main.util.Converter;
import org.apache.log4j.Logger;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import static main.connections.ConnectionTarget.*;


public class TermHandleThread extends Thread {

    private String TName;
    public static String IP;
    private Socket terminalConnection;

    //public static String sendBackBalance;

    private boolean OUT_OF_SERVICE;
    private String HOST_IP;
    private int HOST_PORT;
    private int HOST_CONNECT_TIMEOUT;
    private int HOST_READ_TIMEOUT;

    public static String DB_IP;
    public static String DB_PORT;
    public static String DB_NAME;
    public static String DB_USER;
    public static String DB_PASSWORD;

    public static boolean AA_SERVICE_ENABLED;
    public static String AA_SERVICE_IP;
    public static int AA_SERVICE_PORT;
    public static String AA_SERVICE_TERM;

    public static boolean DAYHAN_SERVICE_ENABLED;
    public static String DAYHAN_SERVICE_IP;
    public static int DAYHAN_SERVICE_PORT;

    public static boolean FUEL_SERVICE_ENABLED;
    public static String FUEL_SERVICE_IP;
    public static int FUEL_SERVICE_PORT;

    public static String serviceName = "";

//    public static boolean MAILING_ENABLED;
//    public static String MAIL_HOST;
//    public static String MAIL_PORT;
//    public static String MAIL_USERNAME;
//    public static String MAIL_PASSWORD;
//    public static String MAIL_INET_ADDRESS_FROM;
//    public static String MAIL_INET_ADDRESS_TO;
//    public static String MAIL_SUBJECT;

    private boolean threadRun = true;

    //public TermHandleThread(HashMap mailProp, Socket termConnected, int term_read_timeout, boolean out_of_service, String host_ip, int host_port, int host_connect_timeout, int host_read_timeout, String db_ip, String aa_ip, int aa_port, String aa_term, String tName){
    //public TermHandleThread(HashMap<String, Serializable> PROPERTIES, Socket termConnected, String tName){
    public TermHandleThread(HashMap<String, String> PROPERTIES, Socket termConnected, String tName){

        this.terminalConnection = termConnected;
        //private static final Logger log = Logger.getLogger(TermHandleThread.class);

        try {
            this.terminalConnection.setSoTimeout(Integer.parseInt(PROPERTIES.get("TERM_READ_TIMEOUT")));
        } catch (SocketException err) {
            Logger log = Logger.getLogger(TermHandleThread.class);
            log.error("CAN`T SET TIMEOUT ON TERMINAL SOCKET !", err);
        }

        try {
            terminalConnection.setTcpNoDelay(true);
        } catch (SocketException err) {
            Logger log = Logger.getLogger(TermHandleThread.class);
            log.error("CAN`T SET TCP_NO_DELAY ON TERMINAL SOCKET !", err);
        }


        //this.sendBackBalance = null;
        IP                          = String.valueOf(terminalConnection.getRemoteSocketAddress()).substring(1);
        this.OUT_OF_SERVICE         = Boolean.parseBoolean(PROPERTIES.get("OUT_OF_SERVICE"));
        this.HOST_IP                = PROPERTIES.get("HOST_IP");
        this.HOST_PORT              = Integer.parseInt(PROPERTIES.get("HOST_PORT"));
        this.HOST_CONNECT_TIMEOUT   = Integer.parseInt(PROPERTIES.get("HOST_CONNECT_TIMEOUT"));
        this.HOST_READ_TIMEOUT      = Integer.parseInt(PROPERTIES.get("HOST_READ_TIMEOUT"));
        DB_IP                       = PROPERTIES.get("DB_IP");
        DB_PORT                     = PROPERTIES.get("DB_PORT");
        DB_NAME                     = PROPERTIES.get("DB_NAME");
        DB_USER                     = PROPERTIES.get("DB_USER");
        DB_PASSWORD                 = PROPERTIES.get("DB_PASSWORD");
        this.TName                  = tName;


        if(PROPERTIES.containsKey("AA_SERVICE_ENABLED")) AA_SERVICE_ENABLED = Boolean.parseBoolean(PROPERTIES.get("AA_SERVICE_ENABLED"));
        if(AA_SERVICE_ENABLED) {
            AA_SERVICE_IP = PROPERTIES.get("AA_SERVICE_IP");
            AA_SERVICE_PORT = Integer.parseInt(PROPERTIES.get("AA_SERVICE_PORT"));
            AA_SERVICE_TERM = PROPERTIES.get("AA_SERVICE_TERM");
        }

        if(PROPERTIES.containsKey("DAYHAN_SERVICE_ENABLED")) DAYHAN_SERVICE_ENABLED = Boolean.parseBoolean(PROPERTIES.get("DAYHAN_SERVICE_ENABLED"));
        if(DAYHAN_SERVICE_ENABLED){
            DAYHAN_SERVICE_IP = PROPERTIES.get("DAYHAN_SERVICE_IP");
            DAYHAN_SERVICE_PORT = Integer.parseInt(PROPERTIES.get("DAYHAN_SERVICE_PORT"));
        }

        if(PROPERTIES.containsKey("FUEL_SERVICE_ENABLED")) FUEL_SERVICE_ENABLED = Boolean.parseBoolean(PROPERTIES.get("FUEL_SERVICE_ENABLED"));
        if(FUEL_SERVICE_ENABLED){
            FUEL_SERVICE_IP = PROPERTIES.get("FUEL_SERVICE_IP");
            FUEL_SERVICE_PORT = Integer.parseInt(PROPERTIES.get("FUEL_SERVICE_PORT"));
        }


//        if(PROPERTIES.containsKey("MAILING_ENABLED")) MAILING_ENABLED = Boolean.parseBoolean(PROPERTIES.get("MAILING_ENABLED"));
//        if(MAILING_ENABLED){
//            MAIL_HOST               = PROPERTIES.get("MAIL_HOST");
//            MAIL_PORT               = PROPERTIES.get("MAIL_PORT");
//            MAIL_USERNAME           = PROPERTIES.get("MAIL_USERNAME");
//            MAIL_PASSWORD           = PROPERTIES.get("MAIL_PASSWORD");
//            MAIL_INET_ADDRESS_FROM  = PROPERTIES.get("MAIL_INET_ADDRESS_FROM");
//            MAIL_INET_ADDRESS_TO    = PROPERTIES.get("MAIL_INET_ADDRESS_TO");
//            MAIL_SUBJECT            = PROPERTIES.get("MAIL_SUBJECT");
//        }



    }

    private void threadStop(){
        threadRun = false;
    }


//TODO ??
//    private boolean abortPaymentIfTIDisNotValid(String TID, Sender Sender, Parser Parser, long startTimeN){
//
//        if(!parsedBODY_Ts[41].equals(TID)){
//            Builder Builder = new Builder();
//            Sender.sendTo(toTERM, Builder.Error_0210_build(PARSED_BODY_T, Parser, IP, "713"), IP, "ERROR 0210", parsedBODY_Ts[41],"TERMINAL", startTimeN);
//            LOG.info(IP + " [TID] " + parsedBODY_Ts[41] + " OPERATION FOR THIS TERMINAL IS PROHIBITED ! OPERATION IS ABORTED");
//            closeConnections(IP, false);
//            return true;
//        }
//
//        return false;
//    }


//    private boolean isFuel(String currencyCode){
//        if(currencyCode != null){
//            switch (currencyCode){
//                case "700" :
//                case "701" :
//                case "702" :
//                case "703" :
//                case "704" : {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    @Override
    public void run(){

        long startTimeN = System.nanoTime();
        //System.out.println(startTimeN);


        Thread.currentThread().setName(TName);

        while (threadRun) {

            // CREATE TERMINAL_SERVICE OBJ (CONNECT, IS, OS, etc...)
            TerminalService terminalService = new TerminalService(IP, terminalConnection, AA_SERVICE_TERM);

            if (terminalService.readBytes() == null) {
                terminalService.closeConnection();
                break;
            }

            if (terminalService.parseBytes() == null) {
                terminalService.closeConnection();
                break;
            }
            terminalService.setMti();
            terminalService.setTid();



            /// FOR ARTUR TEST


//            SendBackBuilder sendBackBuilder = new SendBackBuilder();
//            Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), TermHandleThread.IP, "000"), TermHandleThread.IP, "0210", terminalService.getTid(), "TERMINAL", startTimeN);
//            terminalService.closeConnection();
//            if(Converter.bytesToString(terminalService.getParsedBody().get(41)).equals("60009296")){
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }



            /// FOR ARTUR TEST



            // TODO BALANCE CHECK FOR FUEL
            //BALANCE CHECK FROM FUEL TERMINAL
//            if(isFuel(Converter.bytesToString(terminalService.getParsedBody().get(49)))){
//
//                if(terminalService.getMti().equals("0100") &&
//                        Converter.bytesToString(terminalService.getParsedBody().get(3)).equals("310000") &&
//                        Converter.bytesToString(terminalService.getParsedBody().get(24)).equals("100")){
//
//
//                    ToHostConnection toFuelConnection = new ToHostConnection(terminalService.getParsedBody(), IP, FUEL_SERVICE, FUEL_SERVICE_IP, FUEL_SERVICE_PORT, 2000, 2000, terminalService.getOs(), startTimeN);
//                    if(toFuelConnection.connect() == null){break;}
//                    FuelService fuelService = new FuelService(toFuelConnection, terminalService);
//
//                    fuelService.sendTo_orgBalanceRequest(terminalService.getParsedBody(), startTimeN);
//                    if(fuelService.readBytes(startTimeN) == null){break;}
//                    String fuelResponse = new String(fuelService.getBytes(), StandardCharsets.UTF_8);
//                    System.out.println(fuelResponse);
//                    System.out.println(fuelResponse.substring(0, 2));
//                    if(!fuelResponse.substring(0, 2).equals("OK")){
//                        log.info(IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + fuelResponse + ". OPERATION FILED");
//                        SendBackBuilder sendBackBuilder = new SendBackBuilder();
//                        Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0110Build(terminalService.getParsedBody(), IP, fuelResponse), IP, "0210", terminalService.getTid(), "TERMINAL", startTimeN);
//                    } else {
//                        sendBackBalance = fuelResponse.substring(2);
//                        log.info(IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + fuelResponse + " RECEIVED");
//                        SendBackBuilder sendBackBuilder = new SendBackBuilder();
//                        Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0110Build(terminalService.getParsedBody(), IP, "000"), IP, "0210", terminalService.getTid(), "TERMINAL", startTimeN);
//                    }
//
//                    fuelService.closeConnection();
//                    terminalService.closeConnection();
//
//                    break;
//
//                }
//
//            }



            // IF AA TERMINAL TRYING TO PROCEED RETAIL OPERATION
            if(terminalService.abortRetailing(startTimeN)){break;}


//            // IF AA TERMINAL TRYING TO PROCEED CANCEL OPERATION
//            if(terminalService.abortCancelation(startTimeN)){break;}


            if(OUT_OF_SERVICE){
                if(terminalService.isOutOfService(terminalService.getTid(), IP, DB_IP, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD)){
                    terminalService.closeConnection();

                    break;
                }
            }


            if(terminalService.getParsedBody().get(48) != null){

                if(!ServiceHandler.serviceTask(terminalService, startTimeN)){
                    terminalService.closeConnection(); break;
                }
//                terminalService.parseField48();
//
//                boolean error = false;
//                switch (terminalService.getParsedField48().get(0)){
//                    case "DAYHAN_SERVICE" : {
//                        billName = "DAYHAN_SERVICE";
//                        ToHostConnection toDayhanConnection = new ToHostConnection(terminalService.getParsedBody(), IP, DAYHAN_SERVICE, DAYHAN_SERVICE_IP, DAYHAN_SERVICE_PORT, 2000, 2000, terminalService.getOs(), startTimeN);
//                        if(toDayhanConnection.connect() == null){error = true; break;}
//                        DayhanService dayhanService = new DayhanService(toDayhanConnection, terminalService);
//                        dayhanService.sendTo(terminalService.getParsedBody(), "VERIFY", startTimeN);
//                        if(dayhanService.readBytes(startTimeN) == null){error = true; break;}
//                        String dayhanResponse = new String(dayhanService.getBytes(), StandardCharsets.UTF_8);
//                        if(!dayhanResponse.equals("OK")){
//                            log.info(IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + dayhanResponse + ". CAN`T PAY. VERIFY NOT PASS");
//                            SendBackBuilder sendBackBuilder = new SendBackBuilder();
//                            Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), IP, "722"), IP, "0210", terminalService.getTid(), "TERMINAL", startTimeN);
//                            dayhanService.closeConnection();
//                            //terminalService.closeConnection();
//                            error = true;
//                        } else {
//                            log.info(IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + dayhanResponse + " RECEIVED");
//                            dayhanService.closeConnection();
//                        }
//
//                        break;
//                    }
//
//                    case "AA_SERVICE" : {
//                        billName = "AA_SERVICE";
//                        ToHostConnection toAAConnection = new ToHostConnection(terminalService.getParsedBody(), IP, AA_SERVICE, AA_SERVICE_IP, AA_SERVICE_PORT, 2000, 2000, terminalService.getOs(), startTimeN);
//                        if(toAAConnection.connect() == null){error = true; break;}
//                        AAService aaService = new AAService(toAAConnection, terminalService);
//                        aaService.sendTo(terminalService.getParsedBody(), "VERIFY", startTimeN);
//                        if(aaService.readBytes(startTimeN) == null){error = true; break;}
//                        String aaResponse = new String(aaService.getBytes(), StandardCharsets.UTF_8);
//                        if(!aaResponse.equals("OK")){
//                            log.info(IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + aaResponse + ". CAN`T PAY. VERIFY FILED");
//                            SendBackBuilder sendBackBuilder = new SendBackBuilder();
//                            Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), IP, "715"), IP, "0210", terminalService.getTid(), "TERMINAL", startTimeN);
//                            aaService.closeConnection();
//                            //terminalService.closeConnection();
//                            error = true;
//                        } else {
//                            log.info(IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + aaResponse + " RECEIVED");
//                            aaService.closeConnection();
//                        }
//                        break;
//                    }
//
//                    case "FUEL_SERVICE" : {
//                        billName = "FUEL_SERVICE";
//                        ToHostConnection toFuelConnection = new ToHostConnection(terminalService.getParsedBody(), IP, FUEL_SERVICE, FUEL_SERVICE_IP, FUEL_SERVICE_PORT, 2000, 2000, terminalService.getOs(), startTimeN);
//                        if(toFuelConnection.connect() == null){error = true; break;}
//                        FuelService fuelService = new FuelService(toFuelConnection, terminalService);
//                        fuelService.sendTo(terminalService.getParsedBody(), terminalService.getParsedField48().get(1), startTimeN);
//                        if(fuelService.readBytes(startTimeN) == null){error = true; break;}
//                        String fuelResponse = new String(fuelService.getBytes(), StandardCharsets.UTF_8);
//                        System.out.println(fuelResponse);
//                        System.out.println(fuelResponse.substring(0, 2));
//                        if(!fuelResponse.substring(0, 2).equals("OK")){
//                            log.info(IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + fuelResponse + ". OPERATION FILED");
//                            SendBackBuilder sendBackBuilder = new SendBackBuilder();
//                            Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), IP, fuelResponse), IP, "0210", terminalService.getTid(), "TERMINAL", startTimeN);
//                            fuelService.closeConnection();
//                            //terminalService.closeConnection();
//                            error = true;
//
//                        } else {
//                            sendBackRRN = fuelResponse.substring(2);
//                            log.info(IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + fuelResponse + " RECEIVED");
//                            SendBackBuilder sendBackBuilder = new SendBackBuilder();
//                            Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), IP, "000"), IP, "0210", terminalService.getTid(), "TERMINAL", startTimeN);
//                            fuelService.closeConnection();
//                            //terminalService.closeConnection();
//                            error = true;
//                        }
//                        break;
//                    }
//
//                }

 ////   ZAGLUSKA  //////////////////



                // FOR TEST
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//
//                    e.printStackTrace();
//                }
//
//
//                ToHostConnection toDayhanConnection = new ToHostConnection(terminalService.getParsedBody(), IP, DAYHAN_SERVICE, DAYHAN_SERVICE_IP, DAYHAN_SERVICE_PORT, 2000, 2000, terminalService.getOs(), startTimeN);
//                toDayhanConnection.connect();
//                DayhanService dayhanService = new DayhanService(toDayhanConnection, terminalService);
//                dayhanService.sendTo(terminalService.getParsedBody(), "PAID", startTimeN);
//
//
//                terminalService.closeConnection();
//                break;
                // FOR TEST
            }


            // CREATE CONNECTION
            ToHostConnection toHostConnection = new ToHostConnection(terminalService.getParsedBody(), IP, HOST, HOST_IP, HOST_PORT, HOST_CONNECT_TIMEOUT, HOST_READ_TIMEOUT, terminalService.getOs(), startTimeN);
            if(toHostConnection.connect() == null){break;}
            // CREATE HOST_SERVICE OBJ (CONNECT, IS, OS, etc...)
            HostService hostService = new HostService(toHostConnection, terminalService);
            hostService.sendTo(terminalService.getBytes(), startTimeN);



            if (hostService.readBytes(startTimeN) == null) {
                terminalService.closeConnection();
                break;
            }

            if(hostService.parseBytes(startTimeN) == null){
                terminalService.closeConnection();
                break;
            }
            hostService.setMti();


            if(Converter.bytesToString(hostService.getParsedBody().get(1)).equals("0530")){

                terminalService.sendTo(hostService.getBytes(), startTimeN, hostService.getParsedBody(), hostService.getMti());
                terminalService.setStep(" 2nd");
                // READING FROM TERMINAL

                if (terminalService.readBytes() == null) {
                    hostService.closeConnection();
                    break;
                }

                // PARSING FROM TERMINAL
                if(terminalService.parseBytes() == null){
                    hostService.closeConnection();
                    break;
                }
                terminalService.setMti();
                // SENT TO HOST 2nd REQUEST
                hostService.setStep(" 2nd");
                hostService.sendTo(terminalService.getBytes(), startTimeN);

                // READING FROM HOST 2nd
                if (hostService.readBytes(startTimeN) == null) {
                    terminalService.closeConnection();
                    break;
                }

                // PARSING FROM HOST 2nd
                if(hostService.parseBytes(startTimeN) == null){
                    terminalService.closeConnection();
                    break;
                }
                hostService.setMti();

                // SEND TO TERMINAL 2nd RESPONSE
                terminalService.sendTo(hostService.getBytes(), startTimeN, hostService.getParsedBody(), hostService.getMti());

            } else {
                // SEND TO TERMINAL 1st RESPONSE
                terminalService.sendTo(hostService.getBytes(), startTimeN, hostService.getParsedBody(), hostService.getMti());
            }

            hostService.closeConnection();
            terminalService.closeConnection();


            SaveOp.saveOperationToDB(hostService, terminalService, startTimeN);


            threadStop();

        }/// WHILE

    }/// RUN




//    private void savePurchase(HostService hostService, TerminalService terminalService, long startTime) throws SQLException {
//
//        if (Converter.bytesToString(hostService.getParsedBody().get(39)).equals("000")) {
//            DBConnection dbConnection = new DBConnection(DB_IP, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD);
//            OperationRepo operationRepo = new OperationRepo(dbConnection);
//            operationRepo.savePurchaseToDB(IP, hostService.getParsedBody(), serviceName, terminalService.getParsedField48());
//            if (serviceName.equals("DAYHAN_SERVICE")) {
//                ToHostConnection toDayhanConnection = new ToHostConnection(hostService.getParsedBody(), IP, DAYHAN_SERVICE, DAYHAN_SERVICE_IP, DAYHAN_SERVICE_PORT, 2000, 2000, null, startTime);
//                toDayhanConnection.connect();
//                DayhanService dayhanService = new DayhanService(toDayhanConnection, terminalService);
//                dayhanService.sendTo(hostService.getParsedBody(), "PAID", startTime);
//                dayhanService.closeConnection();
//            }
//            if (serviceName.equals("AA_SERVICE")) {
//                ToHostConnection toAAConnection = new ToHostConnection(hostService.getParsedBody(), IP, AA_SERVICE, AA_SERVICE_IP, AA_SERVICE_PORT, 2000, 2000, null, startTime);
//                toAAConnection.connect();
//                AAService aaService = new AAService(toAAConnection, terminalService);
//                aaService.sendTo(hostService.getParsedBody(), "GPAY", startTime);
//                aaService.closeConnection();
//            }
//            if (serviceName.equals("FUEL_SERVICE")){
//                ToHostConnection toFuelConnection = new ToHostConnection(hostService.getParsedBody(), IP, FUEL_SERVICE, FUEL_SERVICE_IP, FUEL_SERVICE_PORT, 2000, 2000, null, startTime);
//                toFuelConnection.connect();
//                FuelService fuelService = new FuelService(toFuelConnection, terminalService);
//                fuelService.sendTo(terminalService.getParsedBody(), hostService.getParsedBody(), "PAID", startTime);
//                fuelService.closeConnection();
//            }
//        }
//
//    }
//
//
//    private void saveCancel(HostService hostService, TerminalService terminalService, long startTime) throws SQLException {
//
//        if (Converter.bytesToString(hostService.getParsedBody().get(39)).equals("000")) {
//            DBConnection dbConnection = new DBConnection(DB_IP, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD);
//            OperationRepo operationRepo = new OperationRepo(dbConnection);
//            operationRepo.saveCancelToDB(IP, hostService.getParsedBody());
//            if(DAYHAN_SERVICE_ENABLED){
//                ToHostConnection toDayhanConnection = new ToHostConnection(hostService.getParsedBody(), IP, DAYHAN_SERVICE, DAYHAN_SERVICE_IP, DAYHAN_SERVICE_PORT, 2000, 2000, null, startTime);
//                toDayhanConnection.connect();
//                DayhanService dayhanService = new DayhanService(toDayhanConnection, terminalService);
//                dayhanService.sendTo(hostService.getParsedBody(), "CANCEL", startTime);
//                dayhanService.closeConnection();
//            }
//            if(FUEL_SERVICE_ENABLED){
//                ToHostConnection toFuelConnection = new ToHostConnection(hostService.getParsedBody(), IP, FUEL_SERVICE, FUEL_SERVICE_IP, FUEL_SERVICE_PORT, 2000, 2000, null, startTime);
//                toFuelConnection.connect();
//                FuelService fuelService = new FuelService(toFuelConnection, terminalService);
//                fuelService.sendTo(terminalService.getParsedBody(), hostService.getParsedBody(), "CANCEL", startTime);
//                fuelService.closeConnection();
//            }
//
//        }
//
//
//    }
//
//    private void saveOperationToDB(HostService hostService, TerminalService terminalService, long startTime){
//
//        //System.out.println(hostService.getParsedBody());
//        //String mti = terminalService.getMti();
//
//
//        try {
//            if (terminalService.getMti().equals("0200")) {
//                savePurchase(hostService, terminalService, startTime);
//            }
//            if (terminalService.getMti().equals("0400")) {
//                saveCancel(hostService, terminalService, startTime);
//            }
//        } catch (Exception e) {
//            log.error(IP + " ERROR: TRYING TO SAVE " + terminalService.getMti() + " OPERATION ! :(", e);
//            System.err.printf("%s ERROR: TRYING TO SAVE %s OPERATION ! :(%s%n", IP, terminalService.getMti(), e.toString());
//        }
//
//    }

//    private void writeToDB(Sender Sender, long startTimeN){
//
//
//        DBWorker DBWorker = new DBWorker();
//
//        if (parsedBODY_Hs[39].equals("000")){
//            if(parsedBODY_Hs[1].equals("0210")){
//                /// SEND REQUEST TO PAY FOR AA
//                if(parsedField48 != null && parsedField48[0].equals("AA_TOLEG")){
//                    sendTo("AA_SERVICE", "GEN_PAY", Sender, startTimeN);
//                } else if (parsedField48 != null && parsedField48[0].equals("DAYHAN_TOLEG")){
//                    sendTo("DAYHAN_SERVICE", "PAID", Sender, startTimeN);
//                }
//                /// SEND REQUEST TO PAY FOR AA END
//                try {
//                    DBWorker.DBWriter(DB_IP, parsedBODY_Hs, billName, parsedField48, IP, null);
//                } catch (Exception err) {
//                    LOG.error(IP + " [TID] " + parsedBODY_Hs[41] + " ERROR: CAN`T WRITE TO DB (0210) :(\n", err);
//                    closeConnections(IP, true);
//                    //break;
//                }
//            } else if (parsedBODY_Hs[1].equals("0410")){
//                try {
//                    DBWorker.DBWriter(DB_IP, parsedBODY_Hs, billName, parsedField48, IP, "canceled");
//                    sendTo("DAYHAN_SERVICE", "CANCEL", Sender, startTimeN);
//                } catch (Exception err) {
//                    LOG.error(IP + " [TID] " + parsedBODY_Hs[41] + " ERROR: CAN`T WRITE TO DB (0410) :(\n", err);
//                    closeConnections(IP, true);
//                    //break;
//                }
//            }
//        }
//
//
//    }





}
