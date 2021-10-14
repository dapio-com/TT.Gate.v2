package main.repo;


import org.apache.log4j.Logger;

import java.sql.*;

public class TerminalRepo {

    private Logger log = Logger.getLogger(TerminalRepo.class);

    private DBConnection connection;

    public TerminalRepo(DBConnection connection){

        this.connection = connection;

    }





    public boolean isTerminalNotActive(String tid, String ip){

            ResultSet requestResult = null;
            //int tidInteger = Integer.parseInt(tid);
            String query = String.format("SELECT terminal_status FROM terminal WHERE terminal_tid = '%s'", tid);
            //"SELECT term_status FROM terminals WHERE term_tid = " + tidInteger;
            int status = 0;
            try {
                requestResult = connection.getConnectionStatement().executeQuery(query);
                //System.out.println(requestResult);
                while (requestResult.next()) {

                    status = Integer.parseInt(requestResult.getString("terminal_status"));

                }


                if(status == 0){
                    log.info(ip + " [TID] " + tid + " STATUS IS NOT ACTIVE. ABORTING.");
                }
                return status == 0;

            } catch (Exception e) {
                log.error(ip + " ERROR: TERMINAL STATUS CHECKING FAIL ! SOMETHING WRONG WITH DB ! :( \n", e);
                System.out.println(ip + " ERROR: TERMINAL STATUS CHECKING FAIL ! SOMETHING WRONG WITH DB ! :( \n" + e);
                SaveError.errorToDb(ip, tid, "ERROR: TERMINAL STATUS CHECKING FAIL ! SOMETHING WRONG WITH DB ! :(");
                closeConnections(requestResult);
                return true;
            } finally {
                closeConnections(requestResult);
            }


    }

    private void closeConnections(ResultSet resultSet){

        try {
            connection.closeConnection();
            if(resultSet != null && !resultSet.isClosed()){
                resultSet.close();
            }
        } catch (Exception e) {
            System.out.println("ERROR: CLOSE CONNECTION TO DATABASE");
            e.printStackTrace();
        }
    }



}
