package main.util;

import main.data.DataParser;

import java.util.ArrayList;


public class ForLogging {


    public static String getFieldsForLog(final ArrayList<ArrayList<Byte>> parsedBody) {

        String[] fieldsNames = new String[65];
        fieldsNames[2] = " [PAN]              ";
        fieldsNames[3] = " [PROC_CODE]        ";
        fieldsNames[4] = " [AMOUNT]           ";
        fieldsNames[5] = " [AMOUNT_SETTL]     ";
        fieldsNames[7] = " [DATE_TIME]        ";
        fieldsNames[11] = "[STAN]             ";
        fieldsNames[12] = "[DATE_TIME_LOCAL]  ";
        fieldsNames[14] = "[DATE_EXPIRATION]  ";
        fieldsNames[15] = "[DATE_SETTL]       ";
        fieldsNames[22] = "[POS_ENTRY_MOE]    ";
        fieldsNames[24] = "[FUNC_CODE]        ";
        fieldsNames[25] = "[POS_COND_CODE]    ";
        fieldsNames[30] = "[AMOUNT_ORIG_SETTL]";
        fieldsNames[35] = "[TRACK_2]          ";
        fieldsNames[37] = "[RET_REF_NUM]      ";
        fieldsNames[38] = "[APPROVAL_NUM]     ";
        fieldsNames[39] = "[RESP_CODE]        ";
        fieldsNames[41] = "[TID]              ";
        fieldsNames[42] = "[MID]              ";
        fieldsNames[44] = "[ADD_RESP_DATA]    ";
        fieldsNames[45] = "[TRACK_1]          ";
        fieldsNames[46] = "[FEES]             ";
        fieldsNames[48] = "[ADDITIONAL_DATA]  ";
        fieldsNames[49] = "[CURRENCY_CODE]    ";
        fieldsNames[52] = "[PIN]              ";
        fieldsNames[53] = "[SECURITY INFO]    ";
        fieldsNames[55] = "[EMV_DATA]         ";

        StringBuilder sb = new StringBuilder("\n");

        sb.append("         [LENGTH]            = ").append(Converter.bytesToString(parsedBody.get(0))).append("\n");
        sb.append("         [MTI]               = ").append(Converter.bytesToString(parsedBody.get(1))).append("\n");
        //sb.append("         [FIELDS]            = ").append(getFIELDS_b(getMAP_b(bytes))).append("\n");
        for (int i = 2; i < parsedBody.size(); i++) {
            if (parsedBody.get(i) != null) {

                if (i == 48) {

                    ArrayList<String> data_48 = DataParser.parseField48(Converter.bytesToString(parsedBody.get(i)));
                    StringBuilder sb48 = new StringBuilder();
                    for (String data : data_48) {
                        if (data != null) {
                            sb48.append(data).append(" || ");
                        }

                    }

                    sb.append("field ").append(i).append(" ").append(fieldsNames[i]).append(" = ").append(sb48).append("\n");
                } else if (i == 52 && parsedBody.get(52) != null) {
                    byte [] fieldBytes = Converter.ArrayListToByteArray(parsedBody.get(52));
                    sb.append("field ").append(i).append(" ").append(fieldsNames[i]).append(" = ").append(Converter.bytesToHex(fieldBytes, "emv")).append("\n");
                } else if (i == 53 && parsedBody.get(53) != null) {
                    byte[] fieldBytes = Converter.ArrayListToByteArray(parsedBody.get(53));
                    sb.append("field ").append(i).append(" ").append(fieldsNames[i]).append(" = ").append(Converter.bytesToHex(fieldBytes, "emv")).append("\n");
                } else if (i == 55 && parsedBody.get(55) != null) {
                    byte[] fieldBytes = Converter.ArrayListToByteArray(parsedBody.get(55));
                    sb.append("field ").append(i).append(" ").append(fieldsNames[i]).append(" = ").append(Converter.bytesToHex(fieldBytes, "emv")).append("\n");
                } else {
                    sb.append("field ").append(i).append(" ").append(fieldsNames[i]).append(" = ").append(Converter.bytesToString(parsedBody.get(i))).append("\n");
                }
            }
        }

        return logFormatForFields(String.valueOf(sb));

    }

    public static String logFormatForPackages(final String string) {

        return string.replaceAll("\n", "\n\t\t\t\t\t\t\t\t\t\t\t\t");

    }


    private static String logFormatForFields(final String string) {

        return string.replaceAll("\n", "\n\t\t\t\t\t\t\t\t");

    }




}
