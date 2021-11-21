package main.connections;


import main.builders.SendBackBuilder;
import main.repo.SaveError;
import main.util.Converter;
import main.util.Sender;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;


public class ToHostConnection {

    private final Logger log = Logger.getLogger(ToHostConnection.class);


    private Socket connection;
    private ArrayList<ArrayList<Byte>> parsedBody;
    private String ip;
    private ConnectionTarget connectionTarget;
    //private String tid;
    private String hostIp;
    private int hostPort;
    private int hostConnectionTimeout;
    private int hostReadTimeOut;
    private OutputStream toTerminal;
    private long startTime;


    public ToHostConnection(ArrayList<ArrayList<Byte>> parsedBody, String ip, ConnectionTarget connectionTarget, String hostIp, int hostPort, int hostConnectionTimeout, int hostReadTimeOut, OutputStream toTerminal, long startTime){
        this.connection = new Socket();
        this.parsedBody = parsedBody;
        this.ip = ip;
        this.connectionTarget = connectionTarget;
        //this.tid = tid;
        this.hostIp = hostIp;
        this.hostPort = hostPort;
        this.hostConnectionTimeout = hostConnectionTimeout;
        this.hostReadTimeOut = hostReadTimeOut;
        this.toTerminal = toTerminal;
        this.startTime = startTime;

    }

    public void closeConnection(){
        try {
            this.connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Socket connect() {
        String errCode = "";
        switch (connectionTarget) {
            case HOST -> {
                errCode = "700";
            }
            case AA_SERVICE -> {
                errCode = "710";
            }
            case DAYHAN_SERVICE -> {
                errCode = "720";
            }
            case FUEL_SERVICE -> {
                errCode = "730";
            }
        }
        try {
            connection.connect(new InetSocketAddress(hostIp, hostPort), hostConnectionTimeout);
            connection.setSoTimeout(hostReadTimeOut);
            log.info(ip + " [TID] " + Converter.bytesToString(parsedBody.get(41))+ " TO " + connectionTarget + " CONNECTED");
            return connection;
        } catch (Exception e) {
            String tid = Converter.bytesToString(parsedBody.get(41));
            //new MailSender().Send(ip + " [TID] " + tid + " ERROR: CAN`T CONNECT TO " + connectionTarget + "\n" + e.getMessage().toUpperCase() + "\n" + Arrays.toString(e.getStackTrace()).replaceAll(",", "\n"));
            //new MailSender().Send(MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD, MAIL_INET_ADDRESS_FROM, MAIL_INET_ADDRESS_TO, MAIL_SUBJECT, ip + " [TID] " + tid + " ERROR: CAN`T CONNECT TO " + connectionTarget + "\n" + e.getMessage().toUpperCase() + "\n" + Arrays.toString(e.getStackTrace()).replaceAll(",", "\n"));
            log.error(ip + " [TID] " + tid + " ERROR: CAN`T CONNECT TO " + connectionTarget + "\n", e);
            SaveError.errorToDb(ip, tid, "CAN`T CONNECT TO " + connectionTarget);
            if(Converter.bytesToString(parsedBody.get(1)).equals("0200")){
                SendBackBuilder sendBackBuilder = new SendBackBuilder();
                Sender.sendTo(toTerminal, sendBackBuilder.sendBack0210Build(parsedBody, ip, errCode), ip, "0210", tid, "TERMINAL", startTime);
            }
            return null;
        }

    }



    public InputStream getIs(){
        try {
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public OutputStream getOs(){
        try {
            return connection.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public ArrayList<ArrayList<Byte>> getParsedBody() {
        return parsedBody;
    }

    public String getIp() {
        return ip;
    }

//    public ConnectionTarget getConnectionTarget(){
//        return connectionTarget;
//    }






}
