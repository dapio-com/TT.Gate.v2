package main.repo;

import main.util.Converter;
import org.apache.log4j.Logger;

import java.sql.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OperationRepo {


    private Logger log = Logger.getLogger(OperationRepo.class);

    private DBConnection connection;

    public OperationRepo(DBConnection connection){

        this.connection = connection;

    }



    private String[] getOrgFromDB(final String ip, String tid) {
        //System.out.println("IN THE METHOD - GetOrgID");

        ResultSet requestResult = null;
        String[] org = new String[3];
        try{
            String query = String.format(Locale.US, "SELECT terminal_org_id, terminal_tsp " +
                    "FROM terminal WHERE terminal_tid = %s", tid);

//            String query = String.format(Locale.US, "SELECT organizations.org_gr_id, terminals.org_id, terminals.term_tsp " +
//                    "FROM terminals, organizations WHERE terminals.term_tid = %s AND terminals.org_id = organizations.id", tid);
//            String query = "SELECT organizations.org_gr_id, terminals.org_id, terminals.term_tsp " +
//                    "FROM terminals, organizations " +
//                    "WHERE terminals.term_tid = " + tid + " AND terminals.org_id = organizations.id";

            requestResult = connection.getConnectionStatement().executeQuery(query);
            while (requestResult.next()) {

                //org[0] = requestResult.getString("org_gr_id");
                org[1] = requestResult.getString("org_id");
                org[2] = requestResult.getString("term_tsp");

            }
        } catch (Exception e) {
            e.printStackTrace();
            closeConnections(ip, requestResult);
            log.error(ip + " ERROR: UNABLE TO GET ORG_ID SOMETHING WRONG WITH DB ! :( \n" + e);
            System.err.println(ip + " ERROR: UNABLE TO GET ORG_ID SOMETHING WRONG WITH DB ! :( \n" + e);


        }
        return org;
    }



    public void savePurchaseToDB(String opIp, ArrayList<ArrayList<Byte>> parsedBodyH, String billName, ArrayList<String> f48) throws SQLException {
//        Date date = new Date();
//        String dateS = String.format("%tF", date);
//        String timeS = String.format("%tT", date);
        String[] org = getOrgFromDB(opIp, Converter.bytesToString(parsedBodyH.get(41)));
        String opTID = Converter.bytesToString(parsedBodyH.get(41));
        String opMTI = "0200";
        String opSTAN = Converter.bytesToString(parsedBodyH.get(11));
        String opRRN = Converter.bytesToString(parsedBodyH.get(37));
        String opAuthCode = Converter.bytesToString(parsedBodyH.get(38));
        String opCard = Converter.bytesToString(parsedBodyH.get(2));
        double opAmountD = Integer.parseInt(Converter.bytesToString(parsedBodyH.get(4))) / 100.d;




        String query;

        if (f48 != null) {

            //String queryBegin = "op_date, op_time, op_gr_id, op_org_id, op_tsp, op_ip, op_tid, op_mti, op_stan, op_rrn, op_auth_code, op_card, op_amount, billname, ";
            String queryBegin = "op_status, op_date_time, op_org_id, op_tsp, op_ip, op_tid, op_mti, op_stan, op_rrn, op_auth_code, op_card_num, op_amount, op_bill_name, ";
            StringBuilder f48Values = new StringBuilder();
            StringBuilder sb = new StringBuilder(queryBegin);

            for (int i = 0; i < f48.size(); i++) {
                String columnName = "op_xadd0";
                if(i > 9){
                    columnName = "op_xadd";
                }
                sb.append(columnName).append(i+1);
                f48Values.append("'").append(f48.get(i)).append("'");
                if(f48.size() - 1 != i){
                   sb.append(", ");
                   f48Values.append(", ");
                }

            }

//            query = String.format(Locale.US, "INSERT INTO operations (" + sb.toString() + ")" +
//                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %.2f, '%s', %s)",
//                    dateS, timeS, org[0], org[1], org[2], opIp, opTID, opMTI, opSTAN, opRRN, opAuthCode, opCard, opAmountD, billName, f48Values);
            query = String.format(Locale.US, "INSERT INTO operation (" + sb.toString() + ")" +
                            "VALUES (1, CURRENT_TIMESTAMP(3), '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %.2f, '%s', %s)",
                    org[1], org[2], opIp, opTID, opMTI, opSTAN, opRRN, opAuthCode, opCard, opAmountD, billName, f48Values);


        } else {

//            query = String.format(Locale.US, "INSERT INTO operations " +
//                            "(op_date, op_time, op_gr_id, op_org_id, op_tsp, op_ip, op_tid, op_mti, op_stan, op_rrn, op_auth_code, op_card, op_amount)" +
//                            " VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %.2f)",
//                    dateS, timeS, org[0], org[1], org[2], opIp, opTID, opMTI, opSTAN, opRRN, opAuthCode, opCard, opAmountD);
            query = String.format(Locale.US, "INSERT INTO operation " +
                            "(op_status, op_date_time, op_org_id, op_tsp, op_ip, op_tid, op_mti, op_stan, op_rrn, op_auth_code, op_card_num, op_amount)" +
                            " VALUES (1, CURRENT_TIMESTAMP(3), '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %.2f)",
                    org[1], org[2], opIp, opTID, opMTI, opSTAN, opRRN, opAuthCode, opCard, opAmountD);

        }

        //COUNTER
        //String queryCount = String.format(Locale.US, "UPDATE counters SET total_operations = total_operations + 1");



        //System.out.println("QUERY SAVE_PURCHASE TO DB : \n" + query);
        Statement st = connection.getConnectionStatement();
        st.execute(query);
        // COUNTER
        // st.execute(queryCount);
        log.info(opIp + " [TID] " + opTID + " SAVING OPERATION " + opMTI + " " + opRRN + " TO DATABASE SUCCESS\n");
        closeConnections(opIp, null);

    }

    public void saveCancelToDB(String opIp, ArrayList<ArrayList<Byte>> parsedBodyH) throws SQLException {

//        Date date = new Date();
//        String dateS = String.format("%tF", date);
//        String timeS = String.format("%tT", date);
        String[] org = getOrgFromDB(opIp, Converter.bytesToString(parsedBodyH.get(41)));
        String opTID = Converter.bytesToString(parsedBodyH.get(41));
        String opMTI = "0400";
        String opSTAN = Converter.bytesToString(parsedBodyH.get(11));
        String opRRN = Converter.bytesToString(parsedBodyH.get(37));
        String opAuthCode = Converter.bytesToString(parsedBodyH.get(38));
        String opCard = Converter.bytesToString(parsedBodyH.get(2));
        double opAmountD = Integer.parseInt(Converter.bytesToString(parsedBodyH.get(4))) / 100.d;


//        String cancelQuery = String.format(Locale.US, "INSERT INTO operations " +
//                        "(op_date, op_time, op_gr_id, op_org_id, op_tsp, op_ip, op_tid, op_mti, op_stan, op_rrn, op_auth_code, op_card, op_amount) " +
//                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %.2f)",
//                        dateS, timeS, org[0], org[1], org[2], opIp, opTID, opMTI, opSTAN, opRRN, opAuthCode, opCard, opAmountD);

        String cancelQuery = String.format(Locale.US, "INSERT INTO operation " +
                        "(op_date_time, op_org_id, op_tsp, op_ip, op_tid, op_mti, op_stan, op_rrn, op_auth_code, op_card_num, op_amount) " +
                        "VALUES (CURRENT_TIMESTAMP(3), '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %.2f)",
                org[1], org[2], opIp, opTID, opMTI, opSTAN, opRRN, opAuthCode, opCard, opAmountD);

//        String updateQuery = String.format(Locale.US, "UPDATE operations " +
//                "SET status = 'canceled' WHERE op_mti = '0200' AND op_tid = '%s' AND op_rrn = '%s'", opTID, opRRN);

        String updateQuery = String.format(Locale.US, "UPDATE operation " +
                "SET op_status = 0 WHERE op_mti = '0200' AND op_tid = '%s' AND op_rrn = '%s'", opTID, opRRN);


        // COUNTER
        String queryCount = String.format(Locale.US, "UPDATE counter SET total_operations = total_operations + 1");

        //System.out.println("QUERY CANCEL_PURCHASE TO DB : \n" + cancelQuery);
        //System.out.println("QUERY UPDATE_STATUS TO DB : \n" + updateQuery);

        Statement st = connection.getConnectionStatement();
        st.execute(cancelQuery);
        st.execute(updateQuery);

        // COUNTER
        //st.execute(queryCount);

        log.info(opIp + " [TID] " + opTID + " SAVING OPERATION " + opMTI + " " + opRRN + " TO DATABASE SUCCESS\n");
        closeConnections(opIp, null);


    }



    private void closeConnections(String ip, ResultSet resultSet){

        try {
            connection.closeConnection();
            if(resultSet != null && !resultSet.isClosed()){
                resultSet.close();
            }
        } catch (Exception e) {
            System.err.println("ERROR: CLOSE CONNECTION TO DATABASE");
            log.error(ip + "ERROR: CLOSE CONNECTION TO DATABASE\n", e);
        }
    }


}
