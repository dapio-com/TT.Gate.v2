package main.builders;

import main.util.Converter;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Builder {


    private final Logger log = Logger.getLogger(Builder.class);

    protected byte[] getfieldSize (ArrayList<Byte> fieldAL){
        return String.valueOf(fieldAL.size()).getBytes();
    }
    protected ArrayList<Byte> f2Rebuild (ArrayList<Byte> f2al){
        byte[] f2_Size = getfieldSize(f2al);
        ArrayList<Byte> f2 = new ArrayList<>();
        for (byte f2_Size_b : f2_Size) {
            f2.add(f2_Size_b);
        }
        f2.addAll(f2al);
        return f2;
    }
    protected ArrayList<Byte> f3Rebuild(boolean toHost){
        ArrayList<Byte> f3 = new ArrayList<>();
        if(toHost){
            for (int i = 0; i < 6; i++) {
                f3.add((byte) 48);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                if(i == 0){
                    f3.add((byte) 53);
                } else {
                    f3.add((byte) 48);
                }
            }
        }
        return  f3;
    }
    protected ArrayList<Byte> f35Rebuild (ArrayList<Byte> f35al){
        byte[] f35_Size = getfieldSize(f35al);
        ArrayList<Byte> f35 = new ArrayList<>();
        for (byte f35_Size_b : f35_Size) {
            f35.add(f35_Size_b);
        }
        f35.addAll(f35al);
        return f35;
    }
    protected byte[] byteMapCreate(final String mapBIT){

        int digitNumber = 1;
        int sum = 0;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < mapBIT.length(); i++){
            int bitToInt = Integer.parseInt(mapBIT.charAt(i) + "");
            if(digitNumber == 1){
                sum += bitToInt * 8;
            } else if(digitNumber == 2){
                sum += bitToInt * 4;
            } else if(digitNumber == 3){
                sum += bitToInt * 2;
            } else if(digitNumber == 4 || i < mapBIT.length() + 1){
                sum += bitToInt;
                sum *= 1;
                digitNumber = 0;
                if(sum < 10)
                    sb.append(sum);
                else if(sum == 10)
                    sb.append('A');
                else if(sum == 11)
                    sb.append('B');
                else if(sum == 12)
                    sb.append('C');
                else if(sum == 13)
                    sb.append('D');
                else if(sum == 14)
                    sb.append('E');
                else if(sum == 15)
                    sb.append("F");
                sum=0;
            }
            digitNumber++;
        }

        byte[] messMAP = new byte[8];
        int b = 0;
        for (int i = 0; i < String.valueOf(sb).length(); i += 2) {
            String str = String.valueOf(sb).substring(i, i + 2);
            messMAP[b] = (byte) (char) Integer.parseInt(str, 16);
            b++;
        }
        return messMAP;
    }
    protected ArrayList<Byte> mapRebuild (ArrayList<ArrayList<Byte>> parsedBodyAL){

        StringBuilder sb_for_map = new StringBuilder();
        for (int i = 1; i < 65; i++) {
            if(parsedBodyAL.get(i) == null){
                sb_for_map.append("0");
            } else {
                sb_for_map.append("1");
            }
        }
        byte[] messMAP = byteMapCreate(String.valueOf(sb_for_map));
        ArrayList<Byte> map = new ArrayList<>();
        for (byte messMAP_b : messMAP) {
            map.add(messMAP_b);
        }
        return map;
    }
    protected ArrayList<Byte> mtiRebuild(String mtiStr){
        byte[] mtiStrB = mtiStr.getBytes();
        ArrayList<Byte> mti = new ArrayList<>();
        for (byte mtiStr_b : mtiStrB) {
            mti.add(mtiStr_b);
        }
        return mti;
    }
    protected ArrayList<Byte> fLenRebuild (ArrayList<ArrayList<Byte>> parsedBodyAL){

        int i = 0;
        for (ArrayList<Byte> item : parsedBodyAL) {
            if(item != null){
                i += item.size();
            }
        }

        byte[] fLenB;
        if(i < 100) {
            fLenB = ("00" + i).getBytes();
        } else {
            fLenB = ("0" + i).getBytes();
        }
        ArrayList<Byte> fLen = new ArrayList<>();
        for (byte fLen_b : fLenB) {
            fLen.add(fLen_b);
        }
        return fLen;
    }
    protected byte[] completeBuilding(String ip, String tid, String mti, ArrayList<Byte> fLen, ArrayList<ArrayList<Byte>> packetList){
        ArrayList<Byte> build = new ArrayList<>(fLen);
        for (ArrayList<Byte> item : packetList) {
            if(item != null)
                build.addAll(item);
        }
        log.info(ip + " [TID] " + tid + " " + mti + " BUILD COMPLETED");
        return Converter.ArrayListToByteArray(build);
    }


    protected ArrayList<Byte> makeStringFieldSplited(String content){

        String[] splited = content.split("=");
        ArrayList<Byte> field = new ArrayList<>();
        for (byte byte_ : splited[0].getBytes()) {
            field.add(byte_);
        }
        return field;
    }
    protected ArrayList<Byte> makeStringFieldLen(String content){
        int len = content.length();
        String lenS;
        if (len < 10){
            lenS = "0" + len;
        } else {
            lenS = String.valueOf(len);
        }
        // ADD LEN
        ArrayList<Byte> field = new ArrayList<>();
        for (byte byte_ : lenS.getBytes()) {
            field.add(byte_);
        }
        // ADD CONTENT
        for (byte byte_ : content.getBytes()) {
            field.add(byte_);
        }
        return field;
    }
    protected ArrayList<ArrayList<Byte>> createEmptyPacketList(){
        ArrayList<ArrayList<Byte>> packetList = new ArrayList<>();
        for (int i = 0; i < 65; i++) {
            packetList.add(null);
        }
        return packetList;
    }

}
