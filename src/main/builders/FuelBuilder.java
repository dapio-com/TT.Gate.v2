package main.builders;

import main.util.Converter;

import java.util.ArrayList;

public class FuelBuilder extends Builder{

    public byte[] fuelPacketBuild(String ip, String mti, ArrayList<ArrayList<Byte>> parsedBody, ArrayList<String> f48){

        ArrayList<ArrayList<Byte>> packetList = createEmptyPacketList();

        packetList.set(2, makeStringFieldSplited(Converter.bytesToString(parsedBody.get(35)))); //PAN 16
        packetList.set(3, parsedBody.get(4)); //AMOUNT (LITRES)12
        // ========== OPERATIONS ==========
        // FUEL_ORG_CREDIT
        // FUEL_ORG_CREDIT_BACK
        // FUEL_CARD_CREDIT
        // FUEL_CARD_CREDIT_BACK
        // FUEL_CARD_VERIFY // VERIFY CARD LIMIT FOR PROCEED PAYMENT
        // ========== OPERATIONS ==========
        packetList.set(4, makeStringFieldLen(f48.get(1))); //OPERATION 0
        packetList.set(5, parsedBody.get(49)); //CURRENCY (FUEL TYPE) 3
        packetList.set(7, parsedBody.get(41)); //TID 8
        //SET MAP
        packetList.set(1, mapRebuild(packetList));
        //SET MTI
        packetList.set(0, mtiRebuild(mti));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(packetList);
        //COMPLETE BUILDING

        return completeBuilding(ip, Converter.bytesToString(parsedBody.get(41)), mti, fLen, packetList);

    }

    //TODO FOR BALANCE REQUEST
//    public byte[] fuelBalanceRequestPacketBuild(String ip, String mti, ArrayList<ArrayList<Byte>> parsedBody){
//
//        ArrayList<ArrayList<Byte>> packetList = createEmptyPacketList();
//
//        packetList.set(2, makeStringFieldSplited(Converter.bytesToString(parsedBody.get(35)))); //PAN 16
//        packetList.set(4, makeStringFieldLen("BALANCE_REQUEST")); //OPERATION 0
//        packetList.set(5, parsedBody.get(49)); //CURRENCY (FUEL TYPE) 3
//
//        //SET MAP
//        packetList.set(1, mapRebuild(packetList));
//        //SET MTI
//        packetList.set(0, mtiRebuild(mti));
//        //GETTING LENGTH
//        ArrayList<Byte> fLen = fLenRebuild(packetList);
//        //COMPLETE BUILDING
//
//        return completeBuilding(ip, Converter.bytesToString(parsedBody.get(41)), mti, fLen, packetList);
//
//    }


    public byte[] fuelPaidBuild(String ip, String mti, ArrayList<ArrayList<Byte>> parsedBodyT, ArrayList<ArrayList<Byte>> parsedBodyH, ArrayList<String> f48){

        ArrayList<ArrayList<Byte>> packetList = createEmptyPacketList();

        packetList.set(2, parsedBodyH.get(2)); //PAN 16
        //packetList.set(2, makeStringFieldSplited(Converter.bytesToString(parsedBody.get(35)))); //PAN 16 (FOR TEST)
        packetList.set(3, parsedBodyH.get(4)); //AMOUNT 12
        packetList.set(4, makeStringFieldLen(f48.get(1))); //MOBILE ACCOUNT 11 (99365123456)
        packetList.set(5, parsedBodyT.get(49)); //CURRENCY (FUEL TYPE) 3
        //packetList.set(6, makeStringField("123456789012")); // RRN (FOR TEST)
        packetList.set(6, parsedBodyH.get(37)); // RRN 12
        packetList.set(7 , parsedBodyH.get(41)); //TID 8

        //SET MAP
        packetList.set(1, mapRebuild(packetList));
        //SET MTI
        packetList.set(0, mtiRebuild(mti));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(packetList);
        //COMPLETE BUILDING
        return completeBuilding(ip, Converter.bytesToString(parsedBodyH.get(41)), mti, fLen, packetList);
    }

}
