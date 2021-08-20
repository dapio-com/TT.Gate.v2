package main.data;

import java.util.ArrayList;

public class DataFields {


    private ArrayList<Byte> fields;


    public DataFields(DataMap map){


        byte[] map_b = map.mapToString().getBytes();

        fields = new ArrayList<>();
        for (int i = 0; i < map_b.length; i++) {
            if (map_b[i] == 49) { //// 49 is byte representation of "1" String value
                fields.add((byte) (i + 1));
            }
        }


    }


    public ArrayList<Byte> getFields() {
        return fields;
    }


}
