package main.repo;


import main.connections.ToHostConnection;
import main.services.*;
import main.util.Converter;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;

import static main.connections.ConnectionTarget.*;

import static main.TermHandleThread.*;

public class SaveOp {

    private final static Logger log = Logger.getLogger(SaveOp.class);

    private static void savePurchase(HostService hostService, TerminalService terminalService, long startTime) throws SQLException, InterruptedException {

        if (Converter.bytesToString(hostService.getParsedBody().get(39)).equals("000")) {
            DBConnection dbConnection = new DBConnection(DB_IP, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD);
            OperationRepo operationRepo = new OperationRepo(dbConnection);
            operationRepo.savePurchaseToDB(IP, hostService.getParsedBody(), serviceName, terminalService.getParsedField48());
            if (serviceName.equals("DAYHAN_SERVICE")) {
                ToHostConnection toDayhanConnection = new ToHostConnection(hostService.getParsedBody(), IP, DAYHAN_SERVICE, DAYHAN_SERVICE_IP, DAYHAN_SERVICE_PORT, 2000, 2000, null, startTime);
                toDayhanConnection.connect();
                DayhanService dayhanService = new DayhanService(toDayhanConnection, terminalService);
                dayhanService.sendTo(hostService.getParsedBody(), "PAID", startTime);
                Thread.sleep(2000);
                dayhanService.closeConnection();
            }
            if (serviceName.equals("AA_SERVICE")) {
                ToHostConnection toAAConnection = new ToHostConnection(hostService.getParsedBody(), IP, AA_SERVICE, AA_SERVICE_IP, AA_SERVICE_PORT, 2000, 2000, null, startTime);
                toAAConnection.connect();
                AAService aaService = new AAService(toAAConnection, terminalService);
                aaService.sendTo(hostService.getParsedBody(), "GPAY", startTime);
                Thread.sleep(2000);
                aaService.closeConnection();
            }
            if (serviceName.equals("FUEL_SERVICE")){
                ToHostConnection toFuelConnection = new ToHostConnection(hostService.getParsedBody(), IP, FUEL_SERVICE, FUEL_SERVICE_IP, FUEL_SERVICE_PORT, 2000, 2000, null, startTime);
                toFuelConnection.connect();
                FuelService fuelService = new FuelService(toFuelConnection, terminalService);
                fuelService.sendTo(terminalService.getParsedBody(), hostService.getParsedBody(), "PAID", startTime);
                Thread.sleep(2000);
                fuelService.closeConnection();
            }
        }

    }


    private static void saveCancel(HostService hostService, TerminalService terminalService, long startTime) throws SQLException {

        if (Converter.bytesToString(hostService.getParsedBody().get(39)).equals("000")) {
            DBConnection dbConnection = new DBConnection(DB_IP, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD);
            OperationRepo operationRepo = new OperationRepo(dbConnection);
            operationRepo.saveCancelToDB(IP, hostService.getParsedBody());
            if(DAYHAN_SERVICE_ENABLED){
                ToHostConnection toDayhanConnection = new ToHostConnection(hostService.getParsedBody(), IP, DAYHAN_SERVICE, DAYHAN_SERVICE_IP, DAYHAN_SERVICE_PORT, 2000, 2000, null, startTime);
                toDayhanConnection.connect();
                DayhanService dayhanService = new DayhanService(toDayhanConnection, terminalService);
                dayhanService.sendTo(hostService.getParsedBody(), "CANCEL", startTime);
                dayhanService.closeConnection();
            }
            if(FUEL_SERVICE_ENABLED){
                ToHostConnection toFuelConnection = new ToHostConnection(hostService.getParsedBody(), IP, FUEL_SERVICE, FUEL_SERVICE_IP, FUEL_SERVICE_PORT, 2000, 2000, null, startTime);
                toFuelConnection.connect();
                FuelService fuelService = new FuelService(toFuelConnection, terminalService);
                fuelService.sendTo(terminalService.getParsedBody(), hostService.getParsedBody(), "CANCEL", startTime);
                fuelService.closeConnection();
            }

        }


    }

    public static void saveOperationToDB(HostService hostService, TerminalService terminalService, long startTime){

        try {
            if (terminalService.getMti().equals("0200")) {
                savePurchase(hostService, terminalService, startTime);
            }
            if (terminalService.getMti().equals("0400")) {
                saveCancel(hostService, terminalService, startTime);
            }
        } catch (SQLException e) {
            log.error(IP + " ERROR: TRYING TO SAVE " + terminalService.getMti() + " OPERATION ! :(", e);
            System.err.printf("%s ERROR: TRYING TO SAVE %s OPERATION ! :(%s%n", IP, terminalService.getMti(), Arrays.toString(e.getStackTrace()));
        } catch (InterruptedException e1) {
            log.error(IP + " ERROR: TRYING TO SLEEP FOR A WILE ", e1);
            System.err.printf("%s ERROR: TRYING TO SLEEP FOR A WILE :(%s%n", IP, Arrays.toString(e1.getStackTrace()));
        }

    }


}
