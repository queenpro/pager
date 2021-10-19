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
package REVOdbManager;

import REVOpager.EVOpagerDBconnection;
import REVOsetup.ErrorLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import showIt.eventManager;
import models.requestsManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Franco
 */
public class EVOpagerParams {

    String CKuserID;
    String CKcontextID;
    String CKtokenID;
    String CKmachineID;
    String CKrepID;
    String CKprojectName;
    String CKprojectGroup;
    String CKargs;
    String formsMap;

    String routineResult;
    int loginResult;
    int release;
    int version;

    // per integrare deviceParams
    String DevToken;
    String DevID;
    String DevType;
    String DevModel;
    String DevUserID;
    String DevUserPassword;

    public EVOpagerParams() {

    }

    public String getDevModel() {
        return DevModel;
    }

    public void setDevModel(String DevModel) {
        this.DevModel = DevModel;
    }

    public String getDevToken() {
        return DevToken;
    }

    public void setDevToken(String DevToken) {
        this.DevToken = DevToken;
    }

    public String getDevID() {
        return DevID;
    }

    public void setDevID(String DevID) {
        this.DevID = DevID;
    }

    public String getDevType() {
        return DevType;
    }

    public void setDevType(String DevType) {
        this.DevType = DevType;
    }

    public String getDevUserID() {
        return DevUserID;
    }

    public void setDevUserID(String DevUserID) {
        this.DevUserID = DevUserID;
    }

    public String getDevUserPassword() {
        return DevUserPassword;
    }

    public void setDevUserPassword(String DevUserPassword) {
        this.DevUserPassword = DevUserPassword;
    }

    public String getFormsMap() {
        return formsMap;
    }

    public void setFormsMap(String formsMap) {
        this.formsMap = formsMap;
    }

    public String getRoutineResult() {
        return routineResult;
    }

    public void setRoutineResult(String routineResult) {
        this.routineResult = routineResult;
    }

    public int getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(int loginResult) {
        this.loginResult = loginResult;
    }

    public String getCKargs() {
        return CKargs;
    }

    public void setCKargs(String CKargs) {
        this.CKargs = CKargs;
    }

    public int getRelease() {
        return release;
    }

    public void setRelease(int release) {
        this.release = release;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCKprojectGroup() {
        return CKprojectGroup;
    }

    public void setCKprojectGroup(String CKprojectGroup) {
        this.CKprojectGroup = CKprojectGroup;
    }

    public String getCKprojectName() {
        return CKprojectName;
    }

    public void setCKprojectName(String CKprojectName) {
        this.CKprojectName = CKprojectName;
    }

    public String getCKmachineID() {
        return CKmachineID;
    }

    public void setCKmachineID(String CKmachineID) {
        this.CKmachineID = CKmachineID;
    }

    public String getCKrepID() {
        return CKrepID;
    }

    public void setCKrepID(String CKrepID) {
        this.CKrepID = CKrepID;
    }

    public String getCKuserID() {
        return CKuserID;
    }

    public void setCKuserID(String CKuserID) {
        this.CKuserID = CKuserID;
    }

    public String getCKcontextID() {
        return CKcontextID;
    }

    public void setCKcontextID(String CKcontextID) {
        this.CKcontextID = CKcontextID;
    }

    public String getCKtokenID() {
        return CKtokenID;
    }

    public void setCKtokenID(String CKtokenID) {
        this.CKtokenID = CKtokenID;
    }

    public String makeParamsPhrase() {
        String args = getCKargs();
        if (args != null && args.length() > 0) {
            args = getCKargs().replace("\"", "'");
        }

        String varx = "?";
        varx += "CKuserID=" + getCKuserID();
        varx += "&";
        varx += "CKcontextID=" + getCKcontextID();
        varx += "&";
        varx += "CKtokenID=" + getCKtokenID();
        varx += "&";
        varx += "CKrepID=" + getCKrepID();
        varx += "&";
        varx += "CKmachineID=" + getCKmachineID();
        varx += "&";
        varx += "CKprojectName=" + getCKprojectName();
        varx += "&";
        varx += "CKprojectGroup=" + getCKprojectGroup();
        varx += "&";
        varx += "CKargs=" + args;
        return varx;
    }

    public JSONObject makeJSONobjParams() {
        String args = getCKargs();
        if (args != null && args.length() > 0) {
            args = getCKargs().replace("\"", "'");
        }
        if (args == null) {
            args = "";
        }
        if (this.CKtokenID == null) {
            this.CKtokenID = "";
        }
        JSONObject obj = new JSONObject();
        obj.put("CKuserID", getCKuserID());
        obj.put("CKcontextID", getCKcontextID());
        obj.put("CKtokenID", getCKtokenID());
        obj.put("CKprojectName", getCKprojectName());
        obj.put("CKprojectGroup", getCKprojectGroup());
        obj.put("CKargs", args);
        return obj ;
    }

    public String makeJSONparams() {
        String args = getCKargs();
        if (args != null && args.length() > 0) {
            args = getCKargs().replace("\"", "'");
        }
        if (args == null) {
            args = "";
        }
        if (this.CKtokenID == null) {
            this.CKtokenID = "";
        }
        JSONObject obj = new JSONObject();
        obj.put("CKuserID", getCKuserID());
        obj.put("CKcontextID", getCKcontextID());
        obj.put("CKtokenID", getCKtokenID());
        obj.put("CKprojectName", getCKprojectName());
        obj.put("CKprojectGroup", getCKprojectGroup());
        obj.put("CKargs", args);
        return obj.toString();
    }

    public String makePORTALparams() {
        String args = getCKargs();
        if (args != null && args.length() > 0) {
            args = getCKargs().replace("\"", "'");
        }
        if (args == null) {
            args = "";
        }
        if (this.CKtokenID == null) {
            this.CKtokenID = "";
        }
        JSONObject obj = new JSONObject();
        obj.put("USR", getCKuserID());
        obj.put("CNT", getCKcontextID());
        obj.put("TKN", getCKtokenID());
        obj.put("PRJ", getCKprojectName());
        obj.put("GRP", getCKprojectGroup());
        return obj.toString();
    }

    public JSONObject makeJsonPORTALparams() {
        String args = getCKargs();
        if (args != null && args.length() > 0) {
            args = getCKargs().replace("\"", "'");
        }
        if (args == null) {
            args = "";
        }
        if (this.CKtokenID == null) {
            this.CKtokenID = "";
        }
        
        JSONObject params = new JSONObject();
        JSONObject obj = new JSONObject();
        obj.put("USR", getCKuserID());
        obj.put("CNT", getCKcontextID());
        obj.put("TKN", getCKtokenID());
        obj.put("PRJ", getCKprojectName());
        obj.put("GRP", getCKprojectGroup());
        params.put("params", obj);
        
        return obj;
    }

    public String makeAsyncParamsPhrase() {
        String args = getCKargs();
        if (args != null && args.length() > 0) {
            args = getCKargs().replace("\"", "'");
        }

        String varx = "";
        varx += "CKuserID=" + getCKuserID();
        varx += "&";
        varx += "CKcontextID=" + getCKcontextID();
        varx += "&";
        varx += "CKtokenID=" + getCKtokenID();
        varx += "&";
        varx += "CKrepID=" + getCKrepID();
        varx += "&";
        varx += "CKmachineID=" + getCKmachineID();
        varx += "&";
        varx += "CKprojectName=" + getCKprojectName();
        varx += "&";
        varx += "CKprojectGroup=" + getCKprojectGroup();
        varx += "&";
        varx += "CKargs=" + args;
        return varx;
    }

    /*  public void makeCookies(HttpServletResponse response) {
     Cookie myCookie = new Cookie("CKuserID", CKuserID);
     System.out.println("CKuserID:" + CKuserID);
     myCookie.setMaxAge(90 * 60);
     response.addCookie(myCookie);
     myCookie = new Cookie("CKcontextID", CKcontextID);
     System.out.println("CKcontextID:" + CKcontextID);
     myCookie.setMaxAge(90 * 60);
     response.addCookie(myCookie);
     myCookie = new Cookie("CKtokenID", CKtokenID);
     System.out.println("CKtokenID:" + CKtokenID);
     myCookie.setMaxAge(90 * 60);
     response.addCookie(myCookie);
     myCookie = new Cookie("CKrepID", CKrepID);
     System.out.println("CKrepID:" + CKrepID);
     myCookie.setMaxAge(90 * 60);
     response.addCookie(myCookie);
     myCookie = new Cookie("CKmachineID", CKmachineID);
     System.out.println("CKmachineID:" + CKmachineID);
     myCookie.setMaxAge(90 * 60);
     response.addCookie(myCookie);
     myCookie = new Cookie("CKprojectName", CKprojectName);
     System.out.println("CKprojectName:" + CKprojectName);
     myCookie.setMaxAge(90 * 60);
     response.addCookie(myCookie);
     myCookie = new Cookie("CKprojectGroup", CKprojectGroup);
     System.out.println("CKprojectGroup:" + CKprojectGroup);
     myCookie.setMaxAge(90 * 60);
     response.addCookie(myCookie);

     }*/
    public void printParams(String sender) {

        System.out.println("\n=============================================\nPARAMS RECEIVED BY" + sender + "\n============================================ \n");
        System.out.println("CKuserID = " + this.getCKuserID() + "    -    CKtokenID = " + this.getCKtokenID()); // token di Gaia ( da replicare nella app )
        System.out.println("CKprojectGroup = " + this.getCKprojectGroup());// es. NTP
        System.out.println("CKprojectName = " + this.getCKprojectName());// nome del progetto APP per generare il nome del DB
        System.out.println("CKcontextID = " + this.getCKcontextID()); // serve per ricavare il nome del DB NTP in base al nome OBJ di Gaia
        System.out.println("CKargs = " + this.getCKargs()); // serve per ricavare il nome del DB NTP in base al nome OBJ di Gaia
        System.out.println("\n=============================================\n");
    }

    public EVOpagerParams chargeParams(String params, Settings mySettings) {
//        System.out.println("\nchargeParams: "+params);
        EVOpagerParams myParams = new EVOpagerParams();
        if (params != null && params.length() > 0) {
            JSONParser paramsParser = new JSONParser();
            JSONObject paramsObject;
            try {
                paramsObject = (JSONObject) paramsParser.parse(params);

                try {
                    myParams.setCKprojectName(paramsObject.get("PRJ").toString());
//                     System.out.println("CKprojectName:" + myParams.getCKprojectName());
                } catch (Exception e) {
                    myParams.setCKprojectName("");
                }
                try {
                    myParams.setCKprojectGroup(paramsObject.get("GRP").toString());
                } catch (Exception e) {
                    myParams.setCKprojectGroup("");
                }
                try {
                    myParams.setCKcontextID(paramsObject.get("CNT").toString());
                    System.out.println("CKcontextID:" + myParams.getCKcontextID());
                } catch (Exception e) {
                    myParams.setCKcontextID("");
                }

                try {
                    myParams.setCKuserID(paramsObject.get("USR").toString());
                } catch (Exception e) {
                    myParams.setCKuserID("");
                }
                try {
                    myParams.setCKtokenID(paramsObject.get("TKN").toString());
                    //  System.out.println("CKtokenID:" + myParams.getCKtokenID());
                } catch (Exception e) {
                    myParams.setCKtokenID("");
                }
            } catch (ParseException ex) {
                Logger.getLogger(requestsManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (myParams.getCKargs() == null || myParams.getCKargs() == "null") {
                myParams.setCKargs("");
            }
            if (myParams.getCKcontextID() == null || myParams.getCKcontextID() == "null") {
                myParams.setCKcontextID("");
            }
            if (myParams.getCKprojectGroup() == null || myParams.getCKprojectGroup() == "null") {
                myParams.setCKprojectGroup("");
            }
            if (myParams.getCKprojectName() == null || myParams.getCKprojectName() == "null") {
                myParams.setCKprojectName("");
            }

            if (myParams.getCKtokenID() == null || myParams.getCKtokenID() == "null") {
                myParams.setCKtokenID("");
            }
            if (myParams.getCKuserID() == null || myParams.getCKuserID() == "null") {
                myParams.setCKuserID("");
            }

            //  myParams.printParams(" EVOpagerParams chargeParams ");
            //ATTENZIONE: SE IL CONTEXT NON E' COMPILATO DEVO CERCARE IL VALORE
            // DI DEFAULT DENTRO LA TABELLA NEL DB queenpro
            String extension = "";

            myParams.setCKprojectName(mySettings.getProjectName());

            if (myParams.getCKprojectName() == null
                    || (myParams.getCKcontextID() == null
                    || myParams.getCKcontextID().length() < 1)) {

                Connection QPconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalQueenpro();
                String SQLphrase = "SELECT * FROM definitions WHERE ID='" + mySettings.getProjectName() + "'";
//                System.out.println("SQLphrase:" + SQLphrase);
                PreparedStatement ps;
                try {
                    ps = QPconny.prepareStatement(SQLphrase);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        extension = rs.getString("definition");
                    }
//                      System.out.println("extension:" + extension);

                    QPconny.close();
                    myParams.setCKprojectName(mySettings.getProjectName());
                    myParams.setCKcontextID(extension);

                } catch (SQLException ex) {
                    Logger.getLogger(eventManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        return myParams;
    }
}
