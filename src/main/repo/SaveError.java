package main.repo;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import static main.TermHandleThread.*;

public class SaveError {

    private static DBConnection dbConnection;
    private static Logger log = Logger.getLogger(SaveError.class);


    public static void errorToDb(String ip, String tid, String error_text) {

        try {
            dbConnection = new DBConnection(DB_IP, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnections(ip, null);
            log.error(ip + " ERROR: UNABLE TO WRITE ERROR. SOMETHING WRONG WITH DB ! :( \n" + e);
            System.err.println(ip + " ERROR: UNABLE TO WRITE ERROR. SOMETHING WRONG WITH DB ! :( \n" + e);
        }

        ResultSet requestResult = null;
        String[] select = new String[2];
        try{
            String query = String.format(Locale.US, "SELECT org.org_name, terminal.terminal_org_id, terminal.terminal_tsp " +
                    "FROM terminal, org WHERE terminal.terminal_tid = '%s' AND org.id = terminal.terminal_org_id", tid);
//            String query = "SELECT organizations.org_gr_id, terminals.org_id, terminals.term_tsp " +
//                    "FROM terminals, organizations " +
//                    "WHERE terminals.term_tid = " + tid + " AND terminals.org_id = organizations.id";

            //System.out.println(query);
            requestResult = dbConnection.getConnectionStatement().executeQuery(query);
            while (requestResult.next()) {

                select[0] = requestResult.getString("org_name");
                select[1] = requestResult.getString("terminal_tsp");

            }
        } catch (Exception e) {
            e.printStackTrace();
            closeConnections(ip, requestResult);
            log.error(ip + " ERROR: UNABLE TO GET ORG_NAME IN ERROR WRITE METHOD. SOMETHING WRONG WITH DB ! :( \n" + e);
            System.err.println(ip + " ERROR: UNABLE TO GET ORG_NAME IN ERROR WRITE METHOD. SOMETHING WRONG WITH DB ! :( \n" + e);


        }

        String query2 = String.format(Locale.US, "INSERT INTO error (error_ip, error_org_name, error_text, error_tid, error_date_time, error_tsp) VALUES ('%s', '%s', '%s', '%s', CURRENT_TIMESTAMP(3), '%s')", ip, select[0], error_text, tid, select[1]);

        Statement st = dbConnection.getConnectionStatement();
        try {
            st.execute(query2);
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnections(ip, null);
            log.error(ip + " ERROR: UNABLE TO WRITE ERROR. SOMETHING WRONG WITH DB ! :( \n" + e);
            System.err.println(ip + " ERROR: UNABLE TO WRITE ERROR. SOMETHING WRONG WITH DB ! :( \n" + e);
        }
        // COUNTER
        // st.execute(queryCount);
        closeConnections(ip, null);

    }


    private static void closeConnections(String ip, ResultSet resultSet){

        try {
            dbConnection.closeConnection();
            if(resultSet != null && !resultSet.isClosed()){
                resultSet.close();
            }
        } catch (Exception e) {
            System.err.println("ERROR: CLOSE CONNECTION TO DATABASE");
            log.error(ip + "ERROR: CLOSE CONNECTION TO DATABASE\n", e);
        }
    }




}
