package main.builders;

import main.util.Converter;

import java.util.ArrayList;

public class UtilityPaymentBuilder extends Builder {

    public byte[] purchase0200Build (ArrayList<ArrayList<Byte>> parsedBody, String ip) {

        ArrayList<ArrayList<Byte>> packetList = new ArrayList<>(parsedBody);

        packetList.set(0, null);
        packetList.set(1, null);
        //f3 rebuild
        packetList.set(3, f3Rebuild(true));
        //f35 rebuild
        packetList.set(35, f35Rebuild(parsedBody.get(35)));
        //f48 delete
        packetList.set(48, null);
        //SET MAP
        packetList.set(1, mapRebuild(packetList));
        //SET MTI
        packetList.set(0, mtiRebuild("0200"));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(packetList);
        //COMPLETE BUILDING
        //System.out.println("PACKET SET IN BUILDER : " + parsedBody);
        return completeBuilding(ip, Converter.bytesToString(parsedBody.get(41)), "0200", fLen, packetList);
    }


    public byte[] cancel0400Build(ArrayList<ArrayList<Byte>> parsedBody, String ip){

        ArrayList<ArrayList<Byte>> packetList = new ArrayList<>(parsedBody);

        packetList.set(0, null);
        packetList.set(1, null);

        //f3 rebuild
        packetList.set(3, f3Rebuild(true));
        //f35 rebuild
        packetList.set(35, f35Rebuild(parsedBody.get(35)));
        //SET MAP
        packetList.set(1, mapRebuild(packetList));
        //SET MTI
        packetList.set(0, mtiRebuild("0400"));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(packetList);
        //COMPLETE BUILDING
        return completeBuilding(ip, Converter.bytesToString(parsedBody.get(41)), "0400", fLen, packetList);
    }

    public byte[] uPayment0210Build(ArrayList<ArrayList<Byte>> parsedBody, String ip, String tid){

        ArrayList<ArrayList<Byte>> packetList = new ArrayList<>(parsedBody);

        packetList.set(0, null);
        packetList.set(1, null);

        //f2 rebuild
        packetList.set(2, f2Rebuild(parsedBody.get(2)));
        //f3 rebuild
        packetList.set(3, f3Rebuild(false));
        //SET MAP
        packetList.set(1, mapRebuild(packetList));
        //SET MTI
        packetList.set(0, mtiRebuild("0210"));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(packetList);
        //COMPLETE BUILDING
        return completeBuilding(ip, tid, "0210", fLen, packetList);
    }

    public byte[] uPaymentCancel0410Build(ArrayList<ArrayList<Byte>> parsedBody, String ip, String tid){


        ArrayList<ArrayList<Byte>> packetList = new ArrayList<>(parsedBody);

        packetList.set(0, null);
        packetList.set(1, null);

        //f2 rebuild
        packetList.set(2, f2Rebuild(parsedBody.get(2)));
        //f3 rebuild
        packetList.set(3, f3Rebuild(false));
        //SET MAP
        packetList.set(1, mapRebuild(packetList));
        //SET MTI
        packetList.set(0, mtiRebuild("0410"));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(packetList);
        //COMPLETE BUILDING
        return completeBuilding(ip, tid, "0410", fLen, packetList);
    }

}
