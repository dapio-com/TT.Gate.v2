package main;

import main.util.KeyUtil;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.RSADigestSigner;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class CoreProperties {


    //700 - 709 HOST ERRORS
    //700 ERROR CONNECTING TO HOST
    //701 ERROR GET BYTES FROM HOST (READ ERROR)
    //702 ERROR PARSE BYTES FROM HOST
    //703 ERROR NOT AUTHORIZED


    //710 - 719 AA_SERVICE ERRORS
    //710 ERROR CONNECTING TO SERVICE
    //711 ERROR RETAIL IS PROHIBITED FOR AA TERMINAL (ABORT)
    //712 ERROR CANCEL IS PROHIBITED FOR AA TERMINAL (ABORT)
    //713 ERROR OPERATION IS PROHIBITED FOR THIS TERMINAL (IF IT`S NOT AA PAYMENT TERMINAL)
    //714 ERROR GET BYTES FROM AA SERVICE
    //715 ERROR BILLING VERIFY

    //720 - 729 DAYHAN_SERVICE ERRORS
    //720 INFO CONNECTING TO DAYHAN_SERVICE
    //721 ERROR SERVICE NOT RESPONDING
    //722 ERROR SQL/DB PROBLEM
    //723 ERROR ADDING NEW PAN // EMBED
    //724 INFO LIMIT EXCEEDED
    //725 INFO AREA IS TO BIG (HA)
    //726 INFO PAN IS REJECTED
    //727 INFO ACCOUNT AND PAN MISMATCH
    //728 INFO PAYMENT AND PAN MISMATCH
    //729 INFO ACCOUNT AND PAYMENT MISMATCH


    //730 - 739 FUEL_SERVICE ERRORS
    //730 ERROR CONNECTING TO FUEL SERVICE
    //731 ERROR CAN`T GET BYTES FROM FUEL SERVICE
    //732 ERROR SQL ERROR
    //733 ERROR ORG CREDIT                          // TODO
    //734 ORG DOES NOT EXIST                        // TODO
    //735 ERROR CREDIT BACK. VALUE IS TO BIG        // TODO
    //736 CARD DOES NOT EXIST                       // TODO


    //private static final Logger log = Logger.getLogger(CoreProperties.class);
    private final Logger log = Logger.getLogger(CoreProperties.class);
    private final String PUBLIC_FILENAME = "LICENSE/lic_public_key.pem";
    private final String LICENSE_FILENAME = "LICENSE/license.lic";

    public String executeCommand() {
        StringBuilder output = new StringBuilder();
        Process p;
        try {

            p = Runtime.getRuntime().exec("/usr/sbin/dmidecode -s system-uuid");

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine())!= null) {
                output.append(line);
            }
            p.waitFor();
            p.destroy();

        } catch (Exception e) {
            log.error("COMMAND EXECUTION ERROR", e);
            //e.printStackTrace();
        }

        //return output.toString().replace("\n", "");
        return output.toString();
    }

    public boolean licenseCheck() throws Exception {

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("wind")) {

            return true;

        } else {

            byte[] stringForSignB = executeCommand().getBytes();

            KeyUtil KeyUtil = new KeyUtil();

            //System.out.println("VERIFYING USING LICENSE FILE AND PUBLIC KEY...");

            File lic_file = new File(LICENSE_FILENAME);
            FileInputStream lic_fis = new FileInputStream(LICENSE_FILENAME);
            byte[] lic_bytes = new byte[(int)lic_file.length()];
            lic_fis.read(lic_bytes);
            lic_fis.close();

            FileInputStream public_fis = new FileInputStream(PUBLIC_FILENAME);
            AsymmetricKeyParameter publicKey = KeyUtil.loadPublicKey(public_fis);

            RSADigestSigner signer = new RSADigestSigner(new SHA512Digest());
            signer.init(false, publicKey);
            signer.update(stringForSignB, 0, stringForSignB.length);

            return signer.verifySignature(lic_bytes);

        }
    }

    /*
    public HashMap<String, String> getMailProp(HashMap Prop){
        HashMap<String, String> mailProp= new HashMap<>();

        mailProp.put("MAIL_HOST", (String)Prop.get("MAIL_HOST"));
        mailProp.put("MAIL_PORT", (String)Prop.get("MAIL_PORT"));
        mailProp.put("MAIL_USERNAME", (String)Prop.get("MAIL_USERNAME"));
        mailProp.put("MAIL_INET_ADDRESS_FROM", (String)Prop.get("MAIL_INET_ADDRESS_FROM"));
        mailProp.put("MAIL_INET_ADDRESS_TO", (String)Prop.get("MAIL_INET_ADDRESS_TO"));
        mailProp.put("MAIL_SUBJECT", (String)Prop.get("MAIL_SUBJECT"));

        return mailProp;
    }*/

    public HashMap<String, String> getProp(Date date, SimpleDateFormat dateFormat) throws Exception {

        HashMap<String, String> coreProperties = new HashMap<>();
        ////////////////////////////


        Properties config = new Properties();
        config.load(new FileInputStream("config.properties"));

        coreProperties.put("GATE_IP",                  config.getProperty("GATE_IP"));
        coreProperties.put("GATE_PORT",                config.getProperty("GATE_PORT"));
        coreProperties.put("OUT_OF_SERVICE",           config.getProperty("OUT_OF_SERVICE"));
        coreProperties.put("HOST_IP",                  config.getProperty("HOST_IP"));
        coreProperties.put("HOST_PORT",                config.getProperty("HOST_PORT"));
        coreProperties.put("TERM_READ_TIMEOUT",        config.getProperty("TERM_READ_TIMEOUT"));
        coreProperties.put("HOST_CONNECT_TIMEOUT",     config.getProperty("HOST_CONNECT_TIMEOUT"));
        coreProperties.put("HOST_READ_TIMEOUT",        config.getProperty("HOST_READ_TIMEOUT"));
        coreProperties.put("DB_IP",                    config.getProperty("DB_IP"));
        coreProperties.put("DB_PORT",                  config.getProperty("DB_PORT"));
        coreProperties.put("DB_NAME",                  config.getProperty("DB_NAME"));
        coreProperties.put("DB_USER",                  config.getProperty("DB_USER"));
        coreProperties.put("DB_PASSWORD",              config.getProperty("DB_PASSWORD"));

        if(config.containsKey("AA_SERVICE_ENABLED")){
            coreProperties.put("AA_SERVICE_ENABLED",   config.getProperty("AA_SERVICE_ENABLED"));
            coreProperties.put("AA_SERVICE_IP",        config.getProperty("AA_SERVICE_IP"));
            coreProperties.put("AA_SERVICE_PORT",      config.getProperty("AA_SERVICE_PORT"));
            coreProperties.put("AA_SERVICE_TERM",      config.getProperty("AA_SERVICE_TERM"));
        }


        if(config.containsKey("DAYHAN_SERVICE_ENABLED")){
            coreProperties.put("DAYHAN_SERVICE_ENABLED",    config.getProperty("DAYHAN_SERVICE_ENABLED"));
            coreProperties.put("DAYHAN_SERVICE_IP",         config.getProperty("DAYHAN_SERVICE_IP"));
            coreProperties.put("DAYHAN_SERVICE_PORT",       config.getProperty("DAYHAN_SERVICE_PORT"));
        }

        if(config.containsKey("FUEL_SERVICE_ENABLED")){
            coreProperties.put("FUEL_SERVICE_ENABLED",      config.getProperty("FUEL_SERVICE_ENABLED"));
            coreProperties.put("FUEL_SERVICE_IP",           config.getProperty("FUEL_SERVICE_IP"));
            coreProperties.put("FUEL_SERVICE_PORT",         config.getProperty("FUEL_SERVICE_PORT"));
        }

        if(config.containsKey("MAILING_ENABLED")){
            coreProperties.put("MAILING_ENABLED",           config.getProperty("MAILING_ENABLED"));
            coreProperties.put("MAIL_HOST",                 config.getProperty("MAIL_HOST"));
            coreProperties.put("MAIL_PORT",                 config.getProperty("MAIL_PORT"));
            coreProperties.put("MAIL_INET_ADDRESS_FROM",    config.getProperty("MAIL_INET_ADDRESS_FROM"));
            coreProperties.put("MAIL_INET_ADDRESS_TO",      config.getProperty("MAIL_INET_ADDRESS_TO"));
            coreProperties.put("MAIL_SUBJECT",              config.getProperty("MAIL_SUBJECT"));
            coreProperties.put("MAIL_USERNAME",             config.getProperty("MAIL_USERNAME"));
            coreProperties.put("MAIL_PASSWORD",             config.getProperty("MAIL_PASSWORD"));
        }



        // OUT

        File std_err = new File("std_err");
        if (!std_err.exists()) {
            std_err.mkdir();
        }

        File std_out = new File("std_out");
        if (!std_out.exists()) {
            std_out.mkdir();
        }

        boolean consoleOut = Boolean.parseBoolean(config.getProperty("CONSOLE_OUT"));
        if(!consoleOut){

            try {
                System.setErr(new PrintStream(new FileOutputStream("std_err/std_err_" + dateFormat.format(date) + ".log")));
                System.setOut(new PrintStream(new FileOutputStream("std_out/std_out_" + dateFormat.format(date) + ".log")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }



        ////////////////////////////
        return coreProperties;
    }



}
