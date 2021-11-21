package main.builders;

import main.util.Converter;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class AABuilder extends Builder {
    
    public byte[] aaVerifyBuild(String ip, ArrayList<ArrayList<Byte>> parsedBody, ArrayList<String> f48){

        ArrayList<ArrayList<Byte>> packetList = createEmptyPacketList();

        packetList.set(2, makeStringFieldSplited(Converter.bytesToString(parsedBody.get(35)))); //PAN 16
        packetList.set(3, parsedBody.get(4)); //AMOUNT 12
        packetList.set(4, makeStringFieldLen(f48.get(1))); //MOBILE ACCOUNT 11 ? (99365123456)

        //SET MAP
        packetList.set(1, mapRebuild(packetList));
        //SET MTI
        packetList.set(0, mtiRebuild("VERF"));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(packetList);
        //COMPLETE BUILDING

        return completeBuilding(ip, Converter.bytesToString(parsedBody.get(41)), "VERF", fLen, packetList);

    }

    public byte[] aaGenPayBuild(String ip, ArrayList<ArrayList<Byte>> parsedBody, ArrayList<String> f48){

        ArrayList<ArrayList<Byte>> packetList = createEmptyPacketList();

        packetList.set(2, parsedBody.get(2)); //PAN 16
        //packetList.set(2, makeStringFieldSplited(Converter.bytesToString(parsedBody.get(35)))); //PAN 16 (FOR TEST)
        packetList.set(3, parsedBody.get(4)); //AMOUNT 12
        packetList.set(4, makeStringFieldLen(f48.get(1))); //MOBILE ACCOUNT 11 (99365123456)
        //packetList.set(6, makeStringField("123456789012")); // RRN (FOR TEST)
        packetList.set(6, parsedBody.get(37)); // RRN 12

        //SET MAP
        packetList.set(1, mapRebuild(packetList));
        //SET MTI
        packetList.set(0, mtiRebuild("GPAY"));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(packetList);
        //COMPLETE BUILDING
        return completeBuilding(ip, Converter.bytesToString(parsedBody.get(41)), "GPAY", fLen, packetList);
    }

}
