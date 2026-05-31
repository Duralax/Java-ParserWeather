package main;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Message;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class EmailNotify {
    public static void send(String text) {
        send(text, null);
    }

    public static void send(String text, File file) {
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

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("uityerect@gmail.com, mor_top@mail.ru"));
            message.setSubject("Погода");
            if (file != null){
                MimeMultipart multipart = new MimeMultipart();

                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText("\n" + text);
                multipart.addBodyPart(textPart);

                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(file);
                multipart.addBodyPart(attachmentPart);

                message.setContent(multipart);
            } else {
                message.setText("\n" + text);
            }

            Transport.send(message);
            System.out.println("Сообщение отправлено!");
        } catch (MessagingException e) {
            System.out.println("Ошибка при отправке: " + e.getMessage());
        } catch (IOException e){
            System.out.println("Ошибка файла: " + e.getMessage());
        }


    }
}
