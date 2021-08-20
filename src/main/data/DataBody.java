package main.data;

import java.util.ArrayList;

public class DataBody {

    private ArrayList<Byte> body;
    private ArrayList<ArrayList<Byte>> parsedBody;


    public DataBody (byte[] bytes) {
        parsedBody = new ArrayList<>();
        body = new ArrayList<>();
        for (int i = 16; i < bytes.length; i++) {
            body.add(bytes[i]);
        }

    }


//    public ArrayList<Byte> getBody() {
//        return body;
//    }
//
    public ArrayList<Byte> list() {
        return body;
    }

    public ArrayList<ArrayList<Byte>> getParsedBody() {
        return parsedBody;
    }

    public void setParsedBody(ArrayList<ArrayList<Byte>> parsedBody) {
        this.parsedBody.addAll(parsedBody);
    }
}
