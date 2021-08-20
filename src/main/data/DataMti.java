package main.data;

import java.util.ArrayList;

public class DataMti {

    private ArrayList<Byte> mti;



    public DataMti(byte[] bytes) {

        mti = new ArrayList<>();
        for (int i = 4; i < 8; i++) {
            mti.add(bytes[i]);
        }

    }

    public ArrayList<Byte> getMti() {
        return mti;
    }


}
