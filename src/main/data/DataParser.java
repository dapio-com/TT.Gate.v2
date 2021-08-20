package main.data;

import main.util.Converter;
import main.util.ForLogging;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class DataParser {

    private static  final Logger log = Logger.getLogger(DataParser.class);



    public static ArrayList<ArrayList<Byte>> parseBody(String ip, String fromWho, String step, DataLen len, DataMti mti, DataFields fields, DataBody body) {


        ArrayList<ArrayList<Byte>> parsedBodyA = new ArrayList<>();
        for (int i = 0; i < 65; i++) {
            parsedBodyA.add(null);
        }

        parsedBodyA.set(0, len.getLen());
        parsedBodyA.set(1, mti.getMti());
        //System.out.println(getFIELDS_b(getMAP_b(bytes)));

        ArrayList<Integer> fieldsLen = new ArrayList<>();
        for (int i = 0; i < 65; i++) {
            fieldsLen.add(0);
        }

        for (int i = 0; i < 65; i++) {
            switch (i) {
                case 3:
                case 11:
                case 14:
                case 15:
                case 38: {
                    fieldsLen.set(i, 6);
                    break;
                }
                case 4:
                case 12:
                case 37: {
                    fieldsLen.set(i, 12);
                    break;
                }
                case 5:
                case 30: {
                    fieldsLen.set(i, 13);
                    break;
                }
                case 7: {
                    fieldsLen.set(i, 10);
                    break;
                }
                case 22:
                case 24:
                case 39:
                case 49: {
                    fieldsLen.set(i, 3);
                    break;
                }
                case 25: {
                    fieldsLen.set(i, 2);
                    break;
                }
                case 41:
                case 52:
                case 64: {
                    fieldsLen.set(i, 8);
                    break;
                }
                case 42: {
                    fieldsLen.set(i, 15);
                    break;
                }
                case 53: {
                    fieldsLen.set(i, 16);
                    break;
                }
                case 55: {
                    fieldsLen.set(i, 255);
                    break;
                }
                //default : {fieldsLen.set(i, 0); break;}
            }
        }


        ArrayList<Byte> messFields = fields.getFields();

        //ArrayList<Byte> messBody = body.getBody();

        for (int field : messFields) {

            switch (field) {
                case 2: {
                    parsedBodyA.set(2, getFieldBytes(body.list(), 2, false));
                    break;
                }
                case 3: {
                    parsedBodyA.set(3, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 4: {
                    parsedBodyA.set(4, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 5: {
                    parsedBodyA.set(5, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 7: {
                    parsedBodyA.set(7, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 11: {
                    parsedBodyA.set(11, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 12: {
                    parsedBodyA.set(12, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 14: {
                    parsedBodyA.set(14, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 15: {
                    parsedBodyA.set(15, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 22: {
                    parsedBodyA.set(22, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 24: {
                    parsedBodyA.set(24, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 25: {
                    parsedBodyA.set(25, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 30: {
                    parsedBodyA.set(30, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 35: {
                    parsedBodyA.set(35, getFieldBytes(body.list(), 2, false));
                    break;
                }
                case 37: {
                    parsedBodyA.set(37, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 38: {
                    parsedBodyA.set(38, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 39: {
                    parsedBodyA.set(39, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 41: {
                    parsedBodyA.set(41, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 42: {
                    parsedBodyA.set(42, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 44: {
                    int size = 3;
                    if (Converter.bytesToString(mti.getMti()).equals("0530")) {
                        size = 2;
                    }
                    parsedBodyA.set(44, getFieldBytes(body.list(), size, false));
                    break;
                }
                case 45: {
                    parsedBodyA.set(45, getFieldBytes(body.list(), 2, false));
                    break;
                }
                case 46: {
                    parsedBodyA.set(46, getFieldBytes(body.list(), 3, false));
                    break;
                }
                case 48: {
                    parsedBodyA.set(48, getFieldBytes(body.list(), 3, false));
                    break;
                }
                case 49: {
                    parsedBodyA.set(49, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 52: {
                    parsedBodyA.set(52, getFieldBytes(body.list(), fieldsLen.get(field), true));
                    break;
                }
                case 53: {
                    parsedBodyA.set(53, getFieldBytes(body.list(), 2, false));
                    break;
                }
                case 55: {
                    //ArrayList<Byte> fieldBytes = new ArrayList<>(body.list());
                    //parsedBodyA.set(55, fieldBytes);
                    parsedBodyA.set(55, body.list());
                    break;
                }
            }
        }

        log.info(ip + " [TID] " + Converter.bytesToString(parsedBodyA.get(41)) + " DATA FROM " + fromWho + step + " PARSED");
        //LOG.info(Parser.logFormatForFields(Parser.getFieldsForLog(PARSED_BODY_T)));
        log.info(ForLogging.getFieldsForLog(parsedBodyA));
        return parsedBodyA;

    }


    private static ArrayList<Byte> getFieldBytes(ArrayList<Byte> bodyList, int size, boolean isStatic){

        ArrayList<Byte> fieldBytes = new ArrayList<>();
        if (!isStatic){
            size = fLen(bodyList, size);
        }
        for (int i = 0; i < size; i++) {
            fieldBytes.add(bodyList.get(0));
            bodyList.remove(0);
        }
        return fieldBytes;
    }


    private static int fLen(ArrayList<Byte> body, int size){
        ArrayList<Byte> fLen = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            fLen.add(body.get(0));
            body.remove(0);

        }
        return Integer.parseInt(Converter.bytesToString(fLen));
    }


    public static ArrayList<String> parseField48(String field48) {
        ArrayList<String> parsedField48 = new ArrayList<>();


        boolean tag = true;
        int i = 0;
        //int m = 0;
        int t_len;

        while (i < field48.length()) {
            if (tag) {
                i += 3;
                tag = false;
            } else {
                t_len = Integer.parseInt(field48.substring(i, i + 3));
                i += 3;
                //parsedField48[m] = field48.substring(i, i + t_len);
                parsedField48.add(field48.substring(i, i + t_len));
                i += t_len;
                //m++;
                tag = true;
            }

        }


        return parsedField48;

    }


}
