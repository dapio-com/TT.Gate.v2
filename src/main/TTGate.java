package main;


import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TTGate {



    private static final Logger log = Logger.getLogger(TTGate.class);
    private static HashMap<String, String> PROP;
    public static boolean WORKING = true;

    public static boolean MAILING_ENABLED;
    public static String MAIL_HOST;
    public static String MAIL_PORT;
    public static String MAIL_INET_ADDRESS_FROM;
    public static String MAIL_INET_ADDRESS_TO;
    public static String MAIL_SUBJECT;
    public static String MAIL_USERNAME;
    public static String MAIL_PASSWORD;





    public static void main(String[] args) {


        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

        CoreProperties CoreProperties = new CoreProperties();

        try {
            boolean licenceCheck = CoreProperties.licenseCheck();
            if(!licenceCheck){
                log.error("ERROR. LICENSE NOT VALID");
                System.exit(0);
            }
        } catch (Exception err) {
            log.error("ERROR. CAN`T CHECK LICENSE", err);
            System.exit(0);
        }

        //System.out.println("LICENSE CHECKED");

        try {
            PROP = CoreProperties.getProp(date, dateFormat);
        } catch (Exception err) {
            log.error("ERROR. CAN`T GET PROPERTIES", err);
            System.exit(0);
        }


        MAILING_ENABLED = Boolean.parseBoolean(PROP.get("MAIL_ENABLED"));
        MAIL_HOST = PROP.get("MAIL_HOST");
        MAIL_PORT = PROP.get("MAIL_PORT");
        MAIL_INET_ADDRESS_FROM = PROP.get("MAIL_INET_ADDRESS_FROM");
        MAIL_INET_ADDRESS_TO = PROP.get("MAIL_INET_ADDRESS_TO");
        MAIL_SUBJECT = PROP.get("MAIL_SUBJECT");
        MAIL_USERNAME = PROP.get("MAIL_USERNAME");
        MAIL_PASSWORD = PROP.get("MAIL_PASSWORD");


        /*
        if((boolean)PROP.get("MAILING")){
            try{
                MAIL_PROP = CoreProperties.getMailProp(PROP);
            } catch (Exception err){
                log.error("ERROR. CAN`T GET MAIL PROPERTIES", err);
            }
        }*/

        //System.out.println("PROPERTIES OK");

        try (
                //ServerSocket gate = new ServerSocket((int) PROP.get("GATE_PORT"), 0, (InetAddress) PROP.get("GATE_IP"))
                ServerSocket gate = new ServerSocket(Integer.parseInt(PROP.get("GATE_PORT")), 0, InetAddress.getByName(PROP.get("GATE_IP")))
        ) {

            /*
                    "########## ##########       ######     ######   ########## ##########" + "\n" +
                    "########## ##########     ########## ########## ########## ##########" + "\n" +
                    "   ####       ####        ###    ### ###    ###    ####    ###       " + "\n" +
                    "   ####       ####        ###    ### ###    ###    ####    ###       " + "\n" +
                    "   ####       ####        ###        ###    ###    ####    ###       " + "\n" +
                    "   ####       ####        ###  ##### ##########    ####    ##########" + "\n" +
                    "   ####       ####        ###  ##### ##########    ####    ##########" + "\n" +
                    "   ####       ####        ###    ### ###    ###    ####    ###       " + "\n" +
                    "   ####       ####    ##  ########## ###    ###    ####    ##########" + "\n" +
                    "  ######     ######   ##    ######   ###    ###   ######   ##########" + "\n" +

             */



            ArrayList<String> propByKeys = new ArrayList<>(PROP.keySet());
            Collections.sort(propByKeys);
            StringBuilder startInfo = new StringBuilder();

            startInfo.append("\n\n");
            startInfo.append(String.format("%s %n",             "======================================================"));
            startInfo.append(String.format("%s %n %n",          "TT.Gate v 2.0.0 (ISO 8583)"));
            startInfo.append(String.format("%s %tF %tT %n %n",  "STARTED AT", new Date(), new Date()));

            for (String item : propByKeys) {
                startInfo.append(String.format("%-23s %s %-15s %n", item, ":", PROP.get(item)));
            }

            startInfo.append(String.format("%s %n",             "======================================================"));


            log.info(startInfo);

            //ExecutorService es = Executors.newFixedThreadPool(4);
            ExecutorService es = Executors.newCachedThreadPool();

            es.submit(new StopThread(es));
            //System.out.println(es.isShutdown());

            while (WORKING) {
                try {
                    Socket termConnected = gate.accept();

                    String ip = String.valueOf(termConnected.getRemoteSocketAddress()).substring(1);

                    //System.out.println("ACCEPTED !");
                    log.info("NEW CONNECTION FROM: " + ip);
                    String tName = "T " + ip;

                    try {
                        //es.submit(new TermHandleThread(PROP, termConnected, tName));
                        es.submit(new TermHandleThread(PROP, termConnected, tName));
                        /*
                        es.submit(new TermHandleThread(MAIL_PROP, termConnected,
                                (int) PROP.get("TERM_READ_TIMEOUT"),
                                (boolean) PROP.get("OUT_OF_SERVICE"),
                                (String) PROP.get("HOST_IP"),
                                (int) PROP.get("HOST_PORT"),
                                (int) PROP.get("HOST_CONNECT_TIMEOUT"),
                                (int) PROP.get("HOST_READ_TIMEOUT"),
                                (String) PROP.get("DB_IP"),
                                (String) PROP.get("AA_PAYMENTS_IP"),
                                (int) PROP.get("AA_PAYMENTS_PORT"),
                                (String) PROP.get("AA_PAYMENTS_TERM"),
                                tName));

                         */

                    } catch (Exception err) {
                        log.error(ip + " THREAD START ERROR ", err);
                    }

                } catch (Exception err) {
                    err.printStackTrace();
                }

            } // WHILE

            try {
                System.out.println("GOING TO SLEEP");
                log.info("GOING TO SLEEP");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                log.error("DELAY PROBLEM.", e);
            }
            log.info("SERVER IS DOWN BY OPERATOR");
            System.out.println("SERVER IS DOWN BY OPERATOR");

        } catch (IOException err) {
            log.error("CAN`T BIND ADDRESS OR PORT !!!", err);
        }

    }

}
