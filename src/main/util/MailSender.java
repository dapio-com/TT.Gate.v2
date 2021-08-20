package main.util;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static main.TTGate.*;

public class MailSender {




    //public void Send(String mailHost, String mailPort, String mailUserName, String mailPassword, String mailInetAddressFrom, String mailInetAddressTo, String mailSubject, String mailText) {
    public void Send(String mailText) {

//        String username = "y.shepilova@turkmen-tranzit.com";
//        String password = "19idarat";



        Properties prop = new Properties();
        prop.put("mail.smtp.host", MAIL_HOST);
        prop.put("mail.smtp.port", MAIL_PORT);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", MAIL_PORT);
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(MAIL_USERNAME, MAIL_PASSWORD);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL_INET_ADDRESS_FROM));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(MAIL_INET_ADDRESS_TO)
            );
            message.setSubject(MAIL_SUBJECT);
            message.setText(mailText);

            Transport.send(message);

            //System.out.println("Done");

        } catch (Exception e) {
            System.out.println(new Date());
            e.printStackTrace();

        }
    }
}