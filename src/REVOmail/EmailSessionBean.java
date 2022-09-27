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

import REVOdbManager.EVOpagerDirectivesManager;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import java.sql.Date;
import java.util.Calendar;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSessionBean {

    /* private int port = 465;
private String host = "smtp.example.com";
private String from = "matt@example.com";
private boolean auth = true;
private String username = "matt@example.com";
private String password = "secretpw";
private Protocol protocol = Protocol.SMTPS;
private boolean debug = true;
     */
 /*
private int port = 465;
private String host = "smtp.gmail.com";
private String from = "sitiffs@gmail.com";
private boolean auth = true;
private String username = "sitiffs@gmail.com";
private String password = "marocchi";
private EmailProtocol protocol = EmailProtocol.SMTPS;
private boolean debug = true;
     */
    private int port = 25;
    private String host = "mail.queenpro.it";
    private String from = "info@queenpro.it";

    private String username = "info@queenpro.it";
    private String password = "Qcosta32!quee17";

    private boolean auth = true;
    private EmailProtocol protocol = EmailProtocol.SMTP;
    private boolean debug = true;

    public void SendMail(EVOpagerParams myParams, Settings mySettings) {
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
        String lastBackupDate = myManager.getEvoDirective("lastBackupDate");
    }

    public void SendEmail(String to, String subject, String body) {

        System.out.println("SONO IN EmailSessionBean--> SENDMAIL protocol:" + protocol + " HOST:" + host + " PORT:" + port);
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        switch (protocol) {
            case SMTPS:
                props.put("mail.smtp.ssl.enable", true);
                break;
            case TLS:
                props.put("mail.smtp.starttls.enable", true);
                break;
            default:
                //
        }

        Authenticator authenticator = null;
        if (auth) {
            System.out.println("SONO IN EmailSessionBean--> Authenticator  username:" + username + " password:" + password);
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
        System.out.println("SONO IN EmailSessionBean--> Imposto debug.");
        session.setDebug(debug);
        System.out.println("SONO IN EmailSessionBean--> Creo messaggio MIME.");
        MimeMessage message = null;
        System.out.println("SONO IN EmailSessionBean-->Messaggio MIME dichiarato.");
        try {
            message = new MimeMessage(session);
            System.out.println("SONO IN EmailSessionBean-->Messaggio MIME creato.");
        } catch (Exception ex) {
            System.out.println("ERRORE." + ex.toString());
        }

        try {

            System.out.println("SONO IN EmailSessionBean--> Procedo in richiesta invio.");

            message.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};
            message.setRecipients(Message.RecipientType.TO, address);
            message.setSubject(subject);
            Calendar cal = Calendar.getInstance();
            java.sql.Date stmt = new java.sql.Date(cal.getTimeInMillis());

            message.setSentDate(stmt);
            //message.setText(body);

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
            System.out.println("ERRORE.");
            ex.printStackTrace();
        }
    }

}