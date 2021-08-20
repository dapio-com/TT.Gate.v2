package main.builders;

import main.util.Converter;


import java.util.ArrayList;

public class DayhanBuilder extends Builder {


    public byte[] dayhanVerifyBuild(String ip, ArrayList<ArrayList<Byte>> parsedBody, ArrayList<String> f48){

        ArrayList<ArrayList<Byte>> packetList = createEmptyPacketList();


        packetList.set(2, makeStringFieldSplited(Converter.bytesToString(parsedBody.get(35)))); //PAN 16
        //packetList.set(3, makeStringField(Converter.bytesToString(parsedBody.get(4)))); //AMOUNT 12
        packetList.set(3, parsedBody.get(4)); //AMOUNT 12
        packetList.set(4, makeStringFieldLen(f48.get(1))); //SERVICE 0
        packetList.set(5, makeStringFieldLen(f48.get(2))); //GEKTAR 0
        packetList.set(7, parsedBody.get(41)); // TID

        //SET MAP
        packetList.set(1, mapRebuild(packetList));
        //SET MTI
        packetList.set(0, mtiRebuild("VERF"));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(packetList);
        //COMPLETE BUILDING

        return completeBuilding(ip, Converter.bytesToString(parsedBody.get(41)), "VERF", fLen, packetList);

    }

    public byte[] dayhanPaidBuild(String ip, ArrayList<ArrayList<Byte>> parsedBody, ArrayList<String> f48){

        ArrayList<ArrayList<Byte>> packetList = createEmptyPacketList();

        packetList.set(2, parsedBody.get(2)); //PAN 16
        //packetList.set(2, makeStringFieldSplited(Converter.bytesToString(parsedBody.get(35)))); //PAN 16 (FOR TEST)
        packetList.set(3, parsedBody.get(4)); //AMOUNT 12
        packetList.set(4, makeStringFieldLen(f48.get(1))); //SERVICE 0
        packetList.set(5, makeStringFieldLen(f48.get(2))); //GEKTAR 0
        //packetList.set(6, makeStringField("123456789012")); // RRN (FOR TEST)
        packetList.set(6, parsedBody.get(37)); // RRN
        packetList.set(7, parsedBody.get(41)); // TID

        //SET MAP
        packetList.set(1, mapRebuild(packetList));
        //SET MTI
        packetList.set(0, mtiRebuild("PAID"));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(packetList);
        //COMPLETE BUILDING
        return completeBuilding(ip, Converter.bytesToString(parsedBody.get(41)), "PAID", fLen, packetList);
    }

    public byte[] dayhanCancelBuild(String ip, ArrayList<ArrayList<Byte>> parsedBody){
        //System.out.println("CANCEL BUILDER ENTERED");
        ArrayList<ArrayList<Byte>> packetList = createEmptyPacketList();

        packetList.set(2, parsedBody.get(2)); // PAN 16
        //packetList.set(2, makeStringFieldSplited(Converter.bytesToString(parsedBody.get(35)))); // PAN 16 (FOR TEST)
        packetList.set(3, parsedBody.get(4)); //AMOUNT 12
        //packetList.set(6, makeStringField("123456789012")); // RRN (FOR TEST)
        packetList.set(6, parsedBody.get(37)); // RRN
        packetList.set(7, parsedBody.get(41)); // TID

        //SET MAP
        packetList.set(1, mapRebuild(packetList));
        //SET MTI
        packetList.set(0, mtiRebuild("CNCL"));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(packetList);
        //COMPLETE BUILDING
        return completeBuilding(ip, Converter.bytesToString(parsedBody.get(41)), "CNCL", fLen, packetList);

    }


}
