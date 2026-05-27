package main;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Message;
import java.util.Properties;

public class EmailNotify {
    public static void send(String text){
        String host = "smtp.gmail.com";
        String port = "587";
        String username = "notdepjohny@gmail.com";
        String password = "hvbw mcpz tejx tdqr";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");


        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("mor_top@mail.ru"));
            message.setSubject("Круто?");
            message.setText("Как тебе парсинг? \n\n"+ text);
            Transport.send(message);
            System.out.println("Сообщение отправлено!");
        } catch (MessagingException e) {
            System.out.println("Ошибка при отправке: " + e.getMessage());
        }


    }
}
