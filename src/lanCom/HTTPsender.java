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
package lanCom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class HTTPsender {

    public String send(String Address, String jmessage) throws MalformedURLException {
        //String jmessage="{\"device\":\"192.168.1.101\",\"command\":\"SETVALUE\",\"periph\":\"2\",\"value\":\"0\"}";
        //String jmessage="{\"command\":\"GETMAP\"}";

        Map<String, String> arguments = new HashMap<>();
        arguments.put("message", jmessage);
        //  arguments.put("password", "sjh76HSn!"); // This is a fake password obviously
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            try {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(HTTPsender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        /*  String message = "AQEXE$$SETVALUE$$1$$1$$";
        String jmessage="{\"device\":\"192.168.1.101\",\"command\":\"SETVALUE\",\"periph\":\"2\",\"value\":\"1\"}";
         */

//        System.out.println("message = " + sj.toString());
        // byte[] out = jmessage.getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        URL url;
        String response = "";
        //url = new URL("http://192.168.1.101:8080");
        url = new URL("http://" + Address);
//        System.out.println("url = " + url.toString());
        URLConnection con = null;
        try {
            con = url.openConnection();
            con.setConnectTimeout(500);
            HttpURLConnection http = (HttpURLConnection) con;
            try {
                http.setRequestMethod("POST");
            } catch (ProtocolException ex) {
                Logger.getLogger(HTTPsender.class.getName()).log(Level.SEVERE, null, ex);
            }
            http.setDoOutput(true);
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            try {
                http.connect();
            } catch (Exception ex) {
                Logger.getLogger(HTTPsender.class.getName()).log(Level.WARNING, "QP DEVICE NOT FOUND.", false);
                System.out.println("HTTP SENDER NO CONNECTION");
                return "NO CONNECTION";
            }
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
                BufferedReader br = new BufferedReader(new InputStreamReader((http.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                response = sb.toString();

            } catch (Exception e) {
                Logger.getLogger(HTTPsender.class.getName()).log(Level.SEVERE, "ERRORE 69" + e, false);
            }

//            System.out.println("Ricevuta risposta:" + response);
        } catch (IOException ex) {
            System.out.println("ERROR SENDING TO " + url.toString() + ":" + ex.toString());
            Logger.getLogger(HTTPsender.class.getName()).log(Level.SEVERE, null, ex);
        }

// Do something with http.getInputStream()// Do something with http.getInputStream()
        return response;

    }

    public String sendHTTP(String Address, String Port, String Message) {

//        System.out.println("\n\nINVIO HTTP SU " + Address + ":" + Port);
        HTTPsender httpSender = new HTTPsender();
        String resp = "";
        try {
            resp = httpSender.send(Address + ":" + Port, Message);
        } catch (MalformedURLException ex) {
            System.out.println("\n\nERRORE INVIO HTTP SU" + Address + " : " + Message);
        }
        return resp;
    }

    public String sendHTTP(String Address, String Port, String page, String Message) {

//        System.out.println("\n\nINVIO HTTP SU " + Address + ":" + Port);
        HTTPsender httpSender = new HTTPsender();
        String resp = "";
        try {
            resp = httpSender.send(Address + ":" + Port + "/" + page, Message);
        } catch (MalformedURLException ex) {
            System.out.println("\n\nERRORE INVIO HTTP SU" + Address + " : " + Message);
        }
        return resp;
    }
}
