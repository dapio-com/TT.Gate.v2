package main.util;



import org.apache.log4j.Logger;

import java.io.OutputStream;

public class Sender {

    //private static final Logger log = Logger.getLogger(Sender.class);
    private static  final Logger log = Logger.getLogger(Sender.class);


    public static void sendTo(OutputStream messReceiver, byte[] mess, String ip, String mti, String tid, String to, long startTimeN) {

        try {
            messReceiver.write(mess);
            messReceiver.flush();
            log.info(ip + " [TID] " + tid + " " + mti + " = " + mess.length + " BYTES >>> TO " + to + "\n" + ForLogging.logFormatForPackages(Converter.bytesToHex(mess, "all")) + "\r\t\t\t\t\t\t\t\t\t\t\t\tTO " + to + " OK ! " + (double)((System.nanoTime() - startTimeN)/1000000)/1000 + " sec.\n");
        } catch (Exception err){
            log.error(ip + " [TID] " + tid + " " + mti + " = " + mess.length + " BYTES >>>\n" + ForLogging.logFormatForPackages(Converter.bytesToHex(mess, "all")) + "\r\t\t\t\t\t\t\t\t\t\t\t\tCAN`T SEND TO " + to + " !", err);
        }
    }
}
