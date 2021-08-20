package main.builders;


import main.util.Converter;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static main.services.ServiceHandler.sendBackRRN;

public class SendBackBuilder extends Builder {



    private final Logger log = Logger.getLogger(SendBackBuilder.class);

    private ArrayList<Byte> makeF7(){

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("ddMMHHmmss");
        String currentDate = df.format(date);
        ArrayList<Byte> f7 = new ArrayList<>();
        for (byte byte_ : currentDate.getBytes()) {
            f7.add(byte_);

        }

        return f7;
    }

    private ArrayList<Byte> makeF12(){

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyddMMHHmmss");
        String currentDate = df.format(date);
        ArrayList<Byte> f12 = new ArrayList<>();
        for (byte byte_ : currentDate.getBytes()) {
            f12.add(byte_);

        }

        return f12;
    }



    private ArrayList<Byte> f2Fromf35Rebuild (ArrayList<Byte> f35al){
        byte[] f2_Size = "16".getBytes();
        ArrayList<Byte> f2 = new ArrayList<>();
        for (byte f2_Size_b : f2_Size) {
            f2.add(f2_Size_b);
        }

        for (int i = 0; i < 16; i++) {
            f2.add(f35al.get(i));
        }

        //System.out.println(f2);
        return f2;
    }
    private ArrayList<Byte> f39Rebuild(String errorCode){
        ArrayList<Byte> f39 = new ArrayList<>();
        for (byte byte_ : errorCode.getBytes()) {
            f39.add(byte_);
        }
        return f39;
    }

    private byte[] completeBuilding(String ip, String tid, ArrayList<Byte> fLen, ArrayList<ArrayList<Byte>> errorPacket){
        ArrayList<Byte> build = new ArrayList<>(fLen);
        for (ArrayList<Byte> item : errorPacket) {
            if(item != null)
                build.addAll(item);
        }
        //Parser Parser = new Parser();
        log.info(ip + " [TID] " + tid + " " + "SEND_BACK 0210 (" + Converter.bytesToString(errorPacket.get(39)) + ")" + " BUILD COMPLETED");

        //return Parser.ArrayListToByteArray(build);
        return Converter.ArrayListToByteArray(build);
    }


    public byte[] sendBack0210Build(ArrayList<ArrayList<Byte>> parsedBody, String ip, String errorCode){

        String forF37;
        if(sendBackRRN == null){
            forF37 = "000000000000";
        } else {
            forF37 = sendBackRRN;
        }
        ArrayList<ArrayList<Byte>> sendBackPacket = new ArrayList<>();
        for (int i = 0; i < 65; i++) {
            sendBackPacket.add(null);
        }

        //f2 rebuild
        sendBackPacket.set(2, f2Fromf35Rebuild(parsedBody.get(35)));
        //f3 rebuild
        sendBackPacket.set(3, parsedBody.get(3));
        //f4 rebuild
        sendBackPacket.set(4, parsedBody.get(4));
        //f7 rebuild (current DateTime - ddMMHHmmss)
//        Date date = new Date();
//        SimpleDateFormat df = new SimpleDateFormat("ddMMHHmmss");
//        String currentDate = df.format(date);
//        ArrayList<Byte> currentDateBytes = new ArrayList<>();
//        for (byte byte_ : currentDate.getBytes()) {
//            currentDateBytes.add(byte_);
//        }
        sendBackPacket.set(7, makeF7());
        //f11 rebuild
        sendBackPacket.set(11, parsedBody.get(11));
        //f12 rebuild
        sendBackPacket.set(12, makeF12());
        //f37 rebuild

        byte[] forF37B = forF37.getBytes();
        ArrayList<Byte> f37ForPacket = new ArrayList<>();
        for (byte forF37_b : forF37B) {
            f37ForPacket.add(forF37_b);
        }
        sendBackPacket.set(37, f37ForPacket);
        //f39 rebuild
        sendBackPacket.set(39, f39Rebuild(errorCode));
        //f41 rebuild
        sendBackPacket.set(41, parsedBody.get(41));
        //f49 rebuild
        sendBackPacket.set(49, parsedBody.get(49));
        //SET MAP
        sendBackPacket.set(1, mapRebuild(sendBackPacket));
        //SET MTI
        sendBackPacket.set(0, mtiRebuild("0210"));
        //GETTING LENGTH
        ArrayList<Byte> fLen = fLenRebuild(sendBackPacket);
        //COMPLETE BUILDING
        return completeBuilding(ip, Converter.bytesToString(parsedBody.get(41)), fLen, sendBackPacket);
    }

    //TODO
    //BALANCE FOR FUEL
//    public byte[] sendBack0110Build(ArrayList<ArrayList<Byte>> parsedBody, String ip, String errorCode){
//
//        String forF4;
//        if(TermHandleThread.sendBackBalance == null){
//            forF4 = "000000000000";
//        } else {
//            forF4 = TermHandleThread.sendBackBalance;
//        }
//        ArrayList<ArrayList<Byte>> sendBackPacket = new ArrayList<>();
//        for (int i = 0; i < 65; i++) {
//            sendBackPacket.add(null);
//        }
//
//        //f2 rebuild
//        sendBackPacket.set(2, f2Fromf35Rebuild(parsedBody.get(35)));
//        //f3 rebuild
//        sendBackPacket.set(3, parsedBody.get(3));
//        //f4 rebuild
//        byte[] forF4B = forF4.getBytes();
//        ArrayList<Byte> f4ForPacket = new ArrayList<>();
//        for (byte forF4_b : forF4B) {
//            f4ForPacket.add(forF4_b);
//        }
//        sendBackPacket.set(4, f4ForPacket);
//        //f7 rebuild
//        sendBackPacket.set(7, makeF7());
//        //f11 rebuild
//        sendBackPacket.set(11, parsedBody.get(11));
//        //f12 rebuild
//        sendBackPacket.set(12, makeF12());
//        //f37 rebuild
//
//        String forF37 = "000000000000";
//        byte[] forF37B = forF37.getBytes();
//        ArrayList<Byte> f37ForPacket = new ArrayList<>();
//        for (byte forF37_b : forF37B) {
//            f37ForPacket.add(forF37_b);
//        }
//        sendBackPacket.set(37, f37ForPacket);
//
//        if(TermHandleThread.sendBackBalance != null){
//            //f38 rebuild
//            String forF38 = "000000";
//            byte[] forF38B = forF38.getBytes();
//            ArrayList<Byte> f38ForPacket = new ArrayList<>();
//            for (byte forF38_b : forF38B) {
//                f37ForPacket.add(forF38_b);
//            }
//            sendBackPacket.set(38, f38ForPacket);
//        }
//        //f39 rebuild
//        sendBackPacket.set(39, f39Rebuild(errorCode));
//        //f41 rebuild
//        sendBackPacket.set(41, parsedBody.get(41));
//        //f49 rebuild
//        sendBackPacket.set(49, parsedBody.get(49));
//
//        /*//f55 rebuild
//        if(parsedBody.get(55) != null){
//            //byte[] deniedEMV = decodeHexString("0016910AC0D7AA258C4F79C900008A023535");
//            byte[] deniedEMV = hexStringToByteArray("001600000000000000000000000000000000");
//            System.out.println(Arrays.toString(deniedEMV));
//            ArrayList<Byte> f55ForPacket = new ArrayList<>();
//            for (byte byte_ : deniedEMV) {
//                f55ForPacket.add(byte_);
//            }
//            errorPacket.set(55, f55ForPacket);
//        }*/
//
//        //SET MAP
//        sendBackPacket.set(1, mapRebuild(sendBackPacket));
//        //SET MTI
//        sendBackPacket.set(0, mtiRebuild("0110"));
//        //GETTING LENGTH
//        ArrayList<Byte> fLen = fLenRebuild(sendBackPacket);
//        //COMPLETE BUILDING
//        return completeBuilding(ip, Converter.bytesToString(parsedBody.get(41)), fLen, sendBackPacket);
//    }



}
