package main.util;

import org.apache.log4j.Logger;

import java.io.InputStream;


public class Reader {

    //private static final Logger log = Logger.getLogger(Reader.class);
    private static final Logger log = Logger.getLogger(Reader.class);


    public static byte[] readFrom(InputStream fromStream, String ip, String fromWho, String tid, String step) throws Exception {

        int bytesCount;
        byte[] bytesFromStream = new byte[512];
        if (tid != null) {
            log.info(ip + " [TID] " + tid + " RECEIVING FROM " + fromWho + step + " ...");
        } else {
            log.info(ip + " RECEIVING FROM " + fromWho + step + " ...");
        }


        bytesCount = fromStream.read(bytesFromStream);
        //System.out.println("BYTES COUNT : " + bytesCount);
        if (bytesCount > 0) {
            byte[] bytesFromStream_trimmed = new byte[bytesCount];
            System.arraycopy(bytesFromStream, 0, bytesFromStream_trimmed, 0, bytesCount);
            if (tid != null) {
                log.info(ip + " [TID] " + tid + " " + bytesFromStream_trimmed.length + " BYTES FROM " + fromWho + step + " RECEIVED");

            } else {
                log.info(ip + " " + bytesFromStream_trimmed.length + " BYTES FROM " + fromWho + step + " RECEIVED");

            }
            return bytesFromStream_trimmed;
        } else {
            if (tid != null) {
                log.error(ip + " [TID] " + tid + " ERROR: READ SOURCE ( " + fromWho + " ) HAS LINK DROP DOWN OR READ ERROR !");
                log.error(ip + " [TID] " + tid + " ERROR: CAN`T GET BYTES FROM " + fromWho + step + " !\n");
            } else {
                log.error(ip + " ERROR: READ SOURCE ( " + fromWho + " ) HAS LINK DROP DOWN OR READ ERROR !");
                log.error(ip + " ERROR: CAN`T GET BYTES FROM " + fromWho + step + " !\n");
            }
            throw new Exception("RETURNED " + bytesCount);
        }


    }

}

