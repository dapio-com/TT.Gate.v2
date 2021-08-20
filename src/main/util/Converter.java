package main.util;

import java.util.ArrayList;

public class Converter {


    public static String bytesToString(ArrayList<Byte> in) {

        byte[] bytes = new byte[in.size()];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (in.get(i) != null) {
                byte b_ = in.get(i);
                sb.append((char) b_);
            }
        }


        return String.valueOf(sb);
    }



    public static String bytesToHex(byte[] bytes, String block) {

        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        String str = new String(hexChars);
        if (block.equals("all")) {
            str = str.replaceAll("(.{32})", "$1\n");
            str = str.replaceAll("(.{2})", "$1 ");
            str = "\n" + str;
        } else if (block.equals("emv")) {

            //System.out.println("EMV STRING : " + str);
            str = str.replaceAll("(.{2})", "$1 ");
        }

        //str = "\n" + str;
        //return str;

        return str.substring(0, str.length() - 1);
    }


    public static byte[] ArrayListToByteArray(ArrayList<Byte> in) {

        byte[] bytes = new byte[in.size()];
        for (int i = 0; i < bytes.length; i++) {
            if (in.get(i) != null) {
                bytes[i] = in.get(i);
            }
        }
        return bytes;
    }
}
