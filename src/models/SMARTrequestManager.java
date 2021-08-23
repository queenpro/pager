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


package models;

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class SMARTrequestManager {

    EVOpagerParams myParams; //vengono inviati con la richiesta POST
    Settings mySettings; // vengono definiti dal FRAME GAIA e valgono per tutte le istanze
    IncomingRequest request;
    String RMargs;
    ArrayList<SMARTgate> mySmartGateArray = new ArrayList<SMARTgate>();

    public SMARTrequestManager(Settings mySettings) {
        this.mySettings = mySettings;
        this.myParams = new EVOpagerParams();
    }

    public void buildSmartGateArray(String urlTarget, String urlRMargs, String urlArgs) {
        String responseType = "";
        String connectors = "";
        String params = "";
        JSONParser jsonParser = new JSONParser();

        try {
            RMargs = java.net.URLDecoder.decode(urlRMargs, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SMARTrequestManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            RMargs = java.net.URLDecoder.decode(RMargs, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SMARTrequestManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        String URLresponseType = "";
        String URLparams = "";
        String URLconnectors = "";
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(RMargs);
            System.out.println("RMargs2=" + RMargs);
            try {
                URLresponseType = jsonObject.get("responseType").toString();
                responseType = java.net.URLDecoder.decode(URLresponseType, "UTF-8");
            } catch (Exception e) {
                System.out.println("Error in responseType: args=" + RMargs);
            }
            try {
                URLparams = jsonObject.get("params").toString();//params del contesto
                params = java.net.URLDecoder.decode(URLparams, "UTF-8");
                System.out.println("params=" + params);
            } catch (Exception e) {
                System.out.println("Error in params: args=" + RMargs);
            }
            try {
                URLconnectors = jsonObject.get("connectors").toString();// richiesta per una door (Array di richieste)
                connectors = java.net.URLDecoder.decode(URLconnectors, "UTF-8");
                System.out.println("connectors=" + connectors);
            } catch (Exception e) {
                System.out.println("Error in connectors: args=" + RMargs);
            }

        } catch (ParseException ex) {
            System.out.println("Error in requestManager: RMargs=" + RMargs);
            Logger.getLogger(requestsManager.class.getName()).log(Level.SEVERE, null, ex);

        }

        EVOpagerParams myp = new EVOpagerParams();
        myParams = myp.chargeParams(params, mySettings);
        mySmartGateArray = chargeSmartConnectors(connectors);

        System.out.println("HO CAROCATO I CONNETTORI SMARTgate:" + mySmartGateArray.size());

    }

    public ArrayList<SMARTgate> chargeSmartConnectors(String connectors) {

        ArrayList<SMARTgate> myConnectors = new ArrayList<SMARTgate>();
        JSONParser jsonParser = new JSONParser();

        if (connectors != null && connectors.length() > 0) {
            JSONParser parser = new JSONParser();
            Object obj;

            try {
                obj = parser.parse(connectors);

                JSONArray array = (JSONArray) obj;

                for (Object riga : array) {
                    SMARTgate myConnector = new SMARTgate();
                    myConnector.setPanJson(riga.toString());

                    JSONObject jsonObject = (JSONObject) jsonParser.parse(riga.toString());
                    //System.out.println("jsonObject=" + jsonObject);

                    try {
                        myConnector.setDOOR(jsonObject.get("DOOR").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setEVENT(jsonObject.get("EVENT").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setFORMID(jsonObject.get("FORMID").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setINSTANCEID(jsonObject.get("INSTANCEID").toString());
                    } catch (Exception e) {
                    } finally {
                    }

                    try {
                        myConnector.setLOADTYPE(jsonObject.get("LOADTYPE").toString());
                    } catch (Exception e) {
                    } finally {
                    }

                    try {
                        myConnector.setOBJNAME(jsonObject.get("OBJNAME").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setOBJKEY(jsonObject.get("OBJKEY").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setACTION(jsonObject.get("ACTION").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setLEVEL(jsonObject.get("LEVEL").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setSENDTOCRUD(java.net.URLDecoder.decode(jsonObject.get("SENDTOCRUD").toString()));

                    } catch (Exception e) {
                    }

                    myConnectors.add(myConnector);

                }
            } catch (ParseException ex) {
                Logger.getLogger(SMARTgate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return myConnectors;
    }

    public EVOpagerParams getMyParams() {
        return myParams;
    }

    public void setMyParams(EVOpagerParams myParams) {
        this.myParams = myParams;
    }

    public Settings getMySettings() {
        return mySettings;
    }

    public void setMySettings(Settings mySettings) {
        this.mySettings = mySettings;
    }

    public IncomingRequest getRequest() {
        return request;
    }

    public void setRequest(IncomingRequest request) {
        this.request = request;
    }

    public String getRMargs() {
        return RMargs;
    }

    public void setRMargs(String RMargs) {
        this.RMargs = RMargs;
    }

    public ArrayList<SMARTgate> getMySmartGateArray() {
        return mySmartGateArray;
    }

    public void setMySmartGateArray(ArrayList<SMARTgate> mySmartGateArray) {
        this.mySmartGateArray = mySmartGateArray;
    }

}
