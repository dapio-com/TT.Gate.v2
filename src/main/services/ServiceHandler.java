package main.services;

import org.apache.log4j.Logger;
import main.TermHandleThread;
import main.builders.SendBackBuilder;
import main.connections.ToHostConnection;
import main.util.Sender;


import java.nio.charset.StandardCharsets;

import static main.TermHandleThread.*;
import static main.connections.ConnectionTarget.*;

public class ServiceHandler {


    private static final Logger log = Logger.getLogger(ServiceHandler.class);

    public static String sendBackRRN = null;
    //public static boolean error = false;

    public static boolean serviceTask(TerminalService terminalService, long startTimeN){


        terminalService.parseField48();

        switch (terminalService.getParsedField48().get(0)){
            case "DAYHAN_SERVICE" : {
                serviceName = "DAYHAN_SERVICE";
                ToHostConnection toDayhanConnection = new ToHostConnection(terminalService.getParsedBody(), TermHandleThread.IP, DAYHAN_SERVICE, DAYHAN_SERVICE_IP, DAYHAN_SERVICE_PORT, 2000, 2000, terminalService.getOs(), startTimeN);
                if(toDayhanConnection.connect() == null){return false;} // break;}
                DayhanService dayhanService = new DayhanService(toDayhanConnection, terminalService);
                dayhanService.sendTo(terminalService.getParsedBody(), "VERIFY", startTimeN);
                if(dayhanService.readBytes(startTimeN) == null){return false;} // break;}
                String dayhanResponse = new String(dayhanService.getBytes(), StandardCharsets.UTF_8);
                if(!dayhanResponse.equals("OK")){
                    log.info(TermHandleThread.IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + dayhanResponse + ". CAN`T PAY. VERIFY NOT PASS");
                    SendBackBuilder sendBackBuilder = new SendBackBuilder();
                    Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), TermHandleThread.IP, dayhanResponse), TermHandleThread.IP, "0210", terminalService.getTid(), "TERMINAL", startTimeN);
                    dayhanService.closeConnection();
                    //terminalService.closeConnection();
                    return false;
                } else {
                    log.info(TermHandleThread.IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + dayhanResponse + " RECEIVED");
                    dayhanService.closeConnection();
                }

                break;
            }

            case "AA_SERVICE" : {
                serviceName = "AA_SERVICE";
                ToHostConnection toAAConnection = new ToHostConnection(terminalService.getParsedBody(), TermHandleThread.IP, AA_SERVICE, AA_SERVICE_IP, AA_SERVICE_PORT, 2000, 2000, terminalService.getOs(), startTimeN);
                if(toAAConnection.connect() == null){return false;} // break;}
                AAService aaService = new AAService(toAAConnection, terminalService);
                aaService.sendTo(terminalService.getParsedBody(), "VERIFY", startTimeN);
                if(aaService.readBytes(startTimeN) == null){return false;} // break;}
                String aaResponse = new String(aaService.getBytes(), StandardCharsets.UTF_8);
                if(!aaResponse.equals("OK")){
                    log.info(TermHandleThread.IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + aaResponse + ". CAN`T PAY. VERIFY FILED");
                    SendBackBuilder sendBackBuilder = new SendBackBuilder();
                    Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), TermHandleThread.IP, "715"), TermHandleThread.IP, "0210", terminalService.getTid(), "TERMINAL", startTimeN);
                    aaService.closeConnection();
                    //terminalService.closeConnection();
                    return false;
                } else {
                    log.info(TermHandleThread.IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + aaResponse + " RECEIVED");
                    aaService.closeConnection();
                }
                break;
            }

            case "FUEL_SERVICE" : {
                serviceName = "FUEL_SERVICE";
                ToHostConnection toFuelConnection = new ToHostConnection(terminalService.getParsedBody(), TermHandleThread.IP, FUEL_SERVICE, FUEL_SERVICE_IP, FUEL_SERVICE_PORT, 2000, 2000, terminalService.getOs(), startTimeN);
                if(toFuelConnection.connect() == null){return false;} //break;}
                FuelService fuelService = new FuelService(toFuelConnection, terminalService);
                fuelService.sendTo(terminalService.getParsedBody(), null, terminalService.getParsedField48().get(1), startTimeN);
                if(fuelService.readBytes(startTimeN) == null){return false;} //break;}
                String fuelResponse = new String(fuelService.getBytes(), StandardCharsets.UTF_8);
//                System.out.println(fuelResponse);
//                System.out.println(fuelResponse.substring(0, 2));
                if(!fuelResponse.substring(0, 2).equals("OK") && fuelResponse.length() > 2){
                    log.info(TermHandleThread.IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + fuelResponse + ". OPERATION FAILED");
                    SendBackBuilder sendBackBuilder = new SendBackBuilder();
                    Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), TermHandleThread.IP, fuelResponse), TermHandleThread.IP, "0210", terminalService.getTid(), "TERMINAL", startTimeN);
                    fuelService.closeConnection();
                    //terminalService.closeConnection();
                    return false;

                } else if (fuelResponse.length() > 2){
                    sendBackRRN = fuelResponse.substring(2);
                    log.info(TermHandleThread.IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + fuelResponse + " RECEIVED");
                    SendBackBuilder sendBackBuilder = new SendBackBuilder();
                    Sender.sendTo(terminalService.getOs(), sendBackBuilder.sendBack0210Build(terminalService.getParsedBody(), TermHandleThread.IP, "000"), TermHandleThread.IP, "0210", terminalService.getTid(), "TERMINAL", startTimeN);
                    fuelService.closeConnection();
                    //terminalService.closeConnection();
                    return false;
                } else if(fuelResponse.equals("OK")){
                    log.info(TermHandleThread.IP + " [TID] " + terminalService.getTid() + " " + terminalService.getParsedField48().get(0) + " RESPONSE : " + fuelResponse + " RECEIVED");
                    fuelService.closeConnection();
                }
                break;
            }
            default : {
                serviceName = "";
                break;
                //return true;
            }

        }

        return true;

    }

}
