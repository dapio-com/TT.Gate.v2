package main.data;

import java.util.ArrayList;

public class DataMap {

    private ArrayList<Byte> map;



    public DataMap(byte[] bytes) {

        map = new ArrayList<>();
        for (int i = 8; i < 16; i++) {
            map.add(bytes[i]);
        }

        /*
        StringBuilder sb = new StringBuilder();

        for (byte byte_ : map) {
            sb.append(String.format("%8s", Integer.toBinaryString(byte_ & 0xFF)).replace(' ', '0'));
        }*/

    }

//    public ArrayList<Byte> getMap() {
//        return map;
//    }

    public String mapToString(){
        StringBuilder sb = new StringBuilder();

        for (byte byte_ : map) {
            sb.append(String.format("%8s", Integer.toBinaryString(byte_ & 0xFF)).replace(' ', '0'));
        }
        return String.valueOf(sb);
    }



}
