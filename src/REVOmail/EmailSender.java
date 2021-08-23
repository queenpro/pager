/*
 * SOFTWARE BY FFS RELEASED UNDER AGPL LICENSE.
 * REFER TO WWW.FFS.IT AND INFO@FFS.IT FOR INFO.
 * Author: Franco Venezia
  
    Copyright (C) <2019>  <Franco Venezia @ ffs.it>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package REVOmail;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class EmailSender {

    private int port = 25;
    private String host = "mail.queenpro.it";
    private String from = "info@queenpro.it";
    private String username = "info@queenpro.it";
    private String password = "Qcosta32!quee17";
    private String replyTo = "info@queenpro.it";

    private boolean auth = true;
    private EmailProtocol protocol = EmailProtocol.SMTP;
    private boolean debug = true;

    String textPart;
    String htmlPart;

    public EmailSender() {

    }

    public void SendEmail(String to, String subject, String body) {
        SendEmail(to, subject, body, from);
    }

    public void SendEmail(String to, String subject, String body, String XreplyTo) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        switch (protocol) {
            case SMTPS:
                props.put("mail.smtp.ssl.enable", true);
                break;
            case TLS:
                props.put("mail.smtp.starttls.enable", true);
                break;
        }

        Authenticator authenticator = null;
        if (auth) {
            props.put("mail.smtp.auth", true);
            authenticator = new Authenticator() {
                private PasswordAuthentication pa = new PasswordAuthentication(username, password);

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }
        Session session = Session.getInstance(props, authenticator);
        session.setDebug(debug);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};
            message.setRecipients(Message.RecipientType.TO, address);
            InternetAddress[] addressReplyTo = {new InternetAddress(XreplyTo)};
            message.setReplyTo(addressReplyTo);
            message.setSubject(subject);
            Calendar cal = Calendar.getInstance();
            java.sql.Date stmt = new java.sql.Date(cal.getTimeInMillis());

            message.setSentDate(stmt);
            //message.setText(body);
            try {
                message.setHeader("Disposition-Notification-To:", replyTo);
            } catch (Exception e) {
            }
//        
            Multipart multipart = new MimeMultipart("alternative");
            MimeBodyPart textPart = new MimeBodyPart();
            String textContent = "Benvenuto!";
            textPart.setText(textContent);

            MimeBodyPart htmlPart = new MimeBodyPart();
//body = "<html><h1>Hi</h1><p>Nice to meet you!</p></html>";
            htmlPart.setContent(body, "text/html");

            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }

    public int SendEmailAttach(String ToList, String CcList, String BccList, String subject, String body, ByteArrayOutputStream outputStream) {

        InternetAddress[] myToList = null;

        InternetAddress[] myBccList = null;
        InternetAddress[] myCcList = null;
        try {
            myToList = InternetAddress.parse(ToList);
        } catch (AddressException ex) {
            Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            myBccList = InternetAddress.parse(BccList);
        } catch (AddressException ex) {
            Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            myCcList = InternetAddress.parse(CcList);
        } catch (AddressException ex) {
            Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, null, ex);
        }

        int esito = 0;
        String nomeAllegato = "attach.pdf";
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        switch (protocol) {
            case SMTPS:
                props.put("mail.smtp.ssl.enable", true);
                break;
            case TLS:
                props.put("mail.smtp.starttls.enable", true);
                break;
        }

        Authenticator authenticator = null;
        if (auth) {
            props.put("mail.smtp.auth", true);
            authenticator = new Authenticator() {
                private PasswordAuthentication pa = new PasswordAuthentication(username, password);

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }
        Session session = Session.getInstance(props, authenticator);
        session.setDebug(debug);
        MimeMessage message = new MimeMessage(session);
//1. pdfBodyPart  --->allegati
        byte[] bytes = outputStream.toByteArray();
        DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
        MimeBodyPart pdfBodyPart = new MimeBodyPart();
        try {
            pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            pdfBodyPart.setFileName(nomeAllegato);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            message.setFrom(new InternetAddress(from));
//            InternetAddress[] address = {new InternetAddress(to)};

            message.setRecipients(Message.RecipientType.TO, myToList);
            try {
                message.addRecipients(Message.RecipientType.BCC, myBccList);
            } catch (Exception ex) {
                ex.printStackTrace();

            }
            try {
                message.addRecipients(Message.RecipientType.CC, myCcList);
            } catch (Exception ex) {
                ex.printStackTrace();

            }
//            message.setRecipients(Message.RecipientType.TO, address);
            InternetAddress[] addressReplyTo = {new InternetAddress(replyTo)};
            message.setReplyTo(addressReplyTo);
            message.setSubject(subject);
            Calendar cal = Calendar.getInstance();
            java.sql.Date stmt = new java.sql.Date(cal.getTimeInMillis());

            message.setSentDate(stmt);
            //message.setText(body);
            try {
                message.setHeader("Disposition-Notification-To:", replyTo);
            } catch (Exception e) {
            }
//            Multipart multipart = new MimeMultipart("alternative");
            MimeMultipart multipart = new MimeMultipart();
//1. textPart           
            MimeBodyPart textPart = new MimeBodyPart();
            String textContent = "Messaggio di servizio.";
            textPart.setText(textContent);
//1. htmlPart    
            MimeBodyPart htmlPart = new MimeBodyPart();
//body = "<html><h1>Hi</h1><p>Nice to meet you!</p></html>";
            htmlPart.setContent(body, "text/html");

            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);
            multipart.addBodyPart(pdfBodyPart);

            message.setContent(multipart);

            Transport.send(message);
            esito = 1;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            esito = -1;

        }

        return esito;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public EmailProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(EmailProtocol protocol) {
        this.protocol = protocol;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
