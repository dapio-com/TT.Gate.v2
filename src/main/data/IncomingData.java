package main.data;



import java.util.ArrayList;


public class IncomingData {

//    private DataLen dataLen;
//    private DataMti dataMti;
//    private DataMap dataMap;
//    private DataFields dataFields;
//    private DataBody dataBody;
    private ArrayList<ArrayList<Byte>> parsedBody;


    public IncomingData(String ip, String fromWho, String step, byte[] bytes){
        DataLen dataLen = new DataLen(bytes, ip);
        DataMti dataMti = new DataMti(bytes);
        DataMap dataMap = new DataMap(bytes);
        DataFields dataFields = new DataFields(dataMap);
        DataBody dataBody = new DataBody(bytes);
        this.parsedBody = DataParser.parseBody(ip, fromWho, step, dataLen, dataMti, dataFields, dataBody);

    }




//    public DataLen getDataLen() {
//        return dataLen;
//    }
//
//    public DataMti getDataMti() {
//        return dataMti;
//    }
//
//    public DataMap getDataMap() {
//        return dataMap;
//    }
//
//    public DataBody getDataBody() {
//        return dataBody;
//    }

    public ArrayList<ArrayList<Byte>> getParsedBody() {return parsedBody;}
}
