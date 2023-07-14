/*
 * Copyright (C) 2022 Franco Venezia @ www.ffs.it
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lanCom;

import REVOwebsocketManager.WShandler;
import REVOwebsocketManager.WShandlerInstance;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Franco Venezia @ www.ffs.it
 */
public class smartHTTPservice { //used for Agateway to call external services (API) on Auroral swagger

    public responseHTTP get(String rProtocol, String rMethod, String rIP, int rPort, String rPage, WShandlerInstance myHandler) {
        responseHTTP myResponse = new responseHTTP();
        if (rProtocol == null) {
            rProtocol = "http://";
        } else {
            rProtocol = rProtocol.toLowerCase();
            if (!rProtocol.contains("://")) {
                rProtocol += "://";
            }
        }

        if (rPage != null && rPage.length() > 0) {
            if (!rPage.startsWith("/")) {
                rPage = "/" + rPage;
            }
        }
        String port = ":80";
        if (rPort > 0) {
            port = ":" + rPort;
        }
        if (port.equalsIgnoreCase(":80")) port="";
        if (rIP != null && rIP.length() > 7) {
            String urlString = rProtocol + rIP + port + rPage;

            System.out.println("urlString:" + urlString);
            myResponse = get(urlString);

        } else {
            myHandler.getMyHandler().sendToBrowser("status", null, myHandler.getMyParams().getCKtokenID(), "Indirizzo IP della macchina non indicato.");
        }
        return myResponse;
    }

    public responseHTTP get(String urlString) {
        responseHTTP myResponse = new responseHTTP();
        URL obj;
        try {
            obj = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            try {
                con.setRequestMethod("GET");//		con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                    System.out.println("GET Response Code :: " + responseCode);
                myResponse.ResponseCode = responseCode;
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer responseHTTP = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        responseHTTP.append(inputLine);
                    }
                    in.close();
                        System.out.println(responseHTTP.toString());
                    myResponse.responseString = responseHTTP.toString();
                } else {
                    System.out.println("GET request not worked");
                }

            } catch (ProtocolException ex) {
                Logger.getLogger(smartHTTPservice.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(smartHTTPservice.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(smartHTTPservice.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(smartHTTPservice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return myResponse;
    }
}
