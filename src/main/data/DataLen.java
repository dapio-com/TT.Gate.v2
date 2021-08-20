package main.data;

import org.apache.log4j.Logger;

import java.util.ArrayList;

public class DataLen {
    private final Logger log = Logger.getLogger(DataLen.class);

    private ArrayList<Byte> len;


    public DataLen(byte[] bytes, String ip){

        len = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                len.add(bytes[i]);

            }


    }

    public ArrayList<Byte> getLen() {
        return len;
    }



}
