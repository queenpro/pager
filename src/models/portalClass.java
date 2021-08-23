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

import REVOdbManager.EVOpagerDirectivesManager;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOsetup.ErrorLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static showIt.ShowItForm.encodeURIComponent;

//import REVOreports.revoReport;
//import com.itextpdf.text.Document;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.net.URLDecoder;
//import java.sql.Blob;
//import java.sql.SQLException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import showIt.ShowItForm;
//import static showIt.ShowItForm.encodeURIComponent;
/**
 *
 * @author Franco
 */
public class portalClass {

    public EVOpagerParams myParams; //vengono inviati con la richiesta POST
    public Settings mySettings;
    public String RMtarget;
    public String RMargs;
    public String uploadType;
    public String responseType = "";
    public String connectors = "";
    public String params = "";
    public String rifForm = "";
    public String copyTag = "";
    public String htmlCode = "";
    public String routineCode = "";
    public String responsePhrase = "";
    public String rifProcedure = "";
    public String fatherID = "";
    public String rifFolder = "";
    public String UPLOAD_DIRECTORY = "";
    public String path = "";
    public String splashArgs = "";
    public String OSbasepath = "";
    public String WebserverBasepath = "";
    public String OSfolder = "";
    public String OSfilename = "";
    public String keyValue = "";
    public String formToLaod = "";
    public String activity = "";
    public String formID = "";
    public String formCopyTag = "";
    public String formObjName = "";
    public String formRowKey = "";
    public IncomingRequest myRequest;

    public portalClass() {
        myRequest = new IncomingRequest();
        myParams = new EVOpagerParams();
        mySettings = new Settings();
    }

    public void readURL() throws UnsupportedEncodingException {

        // <editor-fold defaultstate="collapsed" desc="RECUPERO PARAMETRI DA URL">
        //---------------
        JSONParser jsonParser = new JSONParser();

        if (RMargs != null) {
            String URLarguments = "";
            URLarguments = RMargs.replace(":undefined", ":\"\"");
//            System.out.println("portal - URLarguments =>" + RMargs);
            JSONObject jsonObject = null;
            boolean flagDecodificato = false;
            try {

                jsonObject = (JSONObject) jsonParser.parse(URLarguments);
                flagDecodificato = true;
            } catch (ParseException ex) {
//                System.out.println("PRIMO TENTATIVO FALLITO " + ex.toString());
//                System.out.println("Non parsable row: " + URLarguments);
                try {
                    URLarguments = java.net.URLDecoder.decode(URLarguments, "UTF-8");
//                    System.out.println("portal - URLarguments =>" + URLarguments);
                    jsonObject = (JSONObject) jsonParser.parse(URLarguments);
                    flagDecodificato = true;
                } catch (ParseException ex1) {
//                    System.out.println("SECONDO TENTATIVO FALLITO " + ex1.toString());
//                    System.out.println("Non parsable row: " + URLarguments);
                    try {
                        URLarguments = java.net.URLDecoder.decode(URLarguments, "UTF-8");
//                        System.out.println("portal - URLarguments =>" + URLarguments);
                        jsonObject = (JSONObject) jsonParser.parse(URLarguments);
                        flagDecodificato = true;
                    } catch (ParseException ex2) {
                        System.out.println("TERZO TENTATIVO FALLITO " + ex2.toString());
                        System.out.println("Non parsable row: " + URLarguments);

                    }
                }

            }
            if (flagDecodificato) {
                try {
                    responseType = (jsonObject.get("responseType").toString());
                } catch (Exception e) {
                }
                try {
                    // params = (jsonObject.get("params").toString());
                    params = java.net.URLDecoder.decode(jsonObject.get("params").toString(), "UTF-8");//params del contesto

                } catch (Exception e) {
                }
                try {
                    connectors = (jsonObject.get("connectors").toString());// richiesta per una door (Array di richieste)
                } catch (Exception e) {
                }
            }
        }
//        System.out.println("\n***---\nRICEVUTI su PORTAL");
//        System.out.println("params ->" + params);
//        System.out.println("responseType ->" + responseType);
//        System.out.println("connectors ->" + connectors);
        if (params != null && params.length() > 0) {
            try {
                myParams = myParams.chargeParams(params, mySettings);
            } catch (Exception e) {
            }
        }
// </editor-fold>  

    }

    public String manageCaseSplashFrame() {
        if (this.myRequest.getMyGate().paramsToSend != null) {
            System.out.println("\n\nDI RITORNO A PORTAL CON LO SPLASH:" + this.myRequest.getMyGate().paramsToSend);
        }
        this.params = "\"params\":" + encodeURIComponent(this.myRequest.getMyParams().makePORTALparams());
        this.connectors = "\"connectors\":[{"
                + "\"door\":\"splashOperation\"," // questo non verrà letto da portal, ma dalla servlet indicata
                + "\"event\":\"demoEvent\","
                + "\"paramsToSend\":\"" + this.myRequest.getMyGate().paramsToSend + "\","
                + "\"keyValue\":\"" + this.myRequest.getMyGate().getKeyValue() + "\", "
                + "\"secondaryArgs\":" + this.myRequest.getResponse() + " "
                + "  }]";
        String utils = "\"responseType\":\"text\"";
        String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(this.params) + "," + encodeURIComponent(this.connectors) + "}";
        String newPage = this.myRequest.getMyGate().getFormToLoad() + "?target=requestsManager&gp=" + encodeURIComponent(gp);
        return newPage;
    }

    public storedFile managerenderApplicationX() throws FileNotFoundException, IOException {

        String jsonString = (String) this.myRequest.getResponse();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        String filepath = "";
        String FileSysName = "";
        String originalName = "";
        String ext = "";
        {
            try {
                jsonObject = (JSONObject) jsonParser.parse(jsonString);
                try {
                    FileSysName = jsonObject.get("FileSysName").toString();
                } catch (Exception e) {
                }
                try {
                    originalName = jsonObject.get("originalName").toString();
                } catch (Exception e) {
                }
                try {
                    ext = jsonObject.get("ext").toString();
                } catch (Exception e) {
                }
            } catch (ParseException ex) {
//                            Logger.getLogger(this.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        filepath = FileSysName;

        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(this.myRequest.getMyParams(), this.mySettings);
        String OSbasepath = myManager.getDirective("uploadBasePath");
        String slash = OSbasepath.substring(0, 1);
        String nomeContesto = "";

        if (this.myRequest.getMyParams().getCKcontextID() != null && this.myRequest.getMyParams().getCKcontextID().length() > 0) {
            nomeContesto = this.myRequest.getMyParams().getCKcontextID() + slash;
        }

        String completePathFilename = OSbasepath + nomeContesto + filepath;
        System.out.println(">>>>ServeFile:" + completePathFilename);
//                    el = new ErrorLogger(myPortal.myRequest.getMyParams(), myPortal.mySettings);
//                    el.setPrintOnScreen(false);
//                    el.setPrintOnLog(true);
//                    el.log(myPortal.myRequest.getMyParams().getCKprojectName() + myPortal.myRequest.getMyParams().getCKcontextID() + "eventManager", ">>>>ServeFile: " + completePathFilename);
        storedFile myFile = new storedFile(this.myRequest.getMyParams(), this.mySettings, completePathFilename);

        File file = new File(completePathFilename);
        if (!file.exists()) {
            System.out.println("File doesn't exists on server:" + completePathFilename);
        }
        String contentType = myFile.getContentType();
//        System.out.println(contentType);

        String servedFilename = "";
        if (originalName != null && originalName.length() > 0) {
            servedFilename = originalName;
        }

        if (servedFilename.length() == 0 && this.myRequest.getMyParams().getCKprojectName() != null && this.myRequest.getMyParams().getCKprojectName().length() > 0) {
            servedFilename = this.myRequest.getMyParams().getCKprojectName();
        }

        if (servedFilename.length() == 0) {
            servedFilename = "file";
        }
        if (ext != null && ext.length() > 0) {
            servedFilename += ext;
        }

        int length = (int) file.length();

        if (length > Integer.MAX_VALUE) {
            System.out.println("File too big!");
        } else {
//            System.out.println("File length:" + length);
        }

        byte[] bytes = new byte[length];

        FileInputStream fin;
        try {
            fin = new FileInputStream(file);
            fin.read(bytes);
        } catch (Exception e) {
            System.out.println("error reading file:" + e.toString());
        }
//        System.out.println("managerenderApplicationX....contentType():" + contentType);
//        System.out.println("managerenderApplicationX.....servedFilename():" + servedFilename);
//        storedFile myStoredFile = new storedFile();
        myFile.setFileContentType(contentType);
//        System.out.println("managerenderApplicationX....getContentType():" + myFile.getContentType());

        myFile.setServedFilename(servedFilename);
//        System.out.println("managerenderApplicationX.....getServedFilename():" + myFile.getServedFilename());
        myFile.setBites(bytes);

//        System.out.println("Caricati valori su myStoredFile, torno al portal");
        return myFile;
    }

    public String getWebserverBasepath() {
        return WebserverBasepath;
    }

    public void setWebserverBasepath(String WebserverBasepath) {
        this.WebserverBasepath = WebserverBasepath;
    }

    public IncomingRequest getMyRequest() {
        return myRequest;
    }

    public void setMyRequest(IncomingRequest myRequest) {
        this.myRequest = myRequest;
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

    public String getRMtarget() {
        return RMtarget;
    }

    public void setRMtarget(String RMtarget) {
        this.RMtarget = RMtarget;
    }

    public String getRMargs() {
        return RMargs;
    }

    public void setRMargs(String RMargs) {
        this.RMargs = RMargs;
    }

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getConnectors() {
        return connectors;
    }

    public void setConnectors(String connectors) {
        this.connectors = connectors;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getRifForm() {
        return rifForm;
    }

    public void setRifForm(String rifForm) {
        this.rifForm = rifForm;
    }

    public String getCopyTag() {
        return copyTag;
    }

    public void setCopyTag(String copyTag) {
        this.copyTag = copyTag;
    }

    public String getHtmlCode() {
        return htmlCode;
    }

    public void setHtmlCode(String htmlCode) {
        this.htmlCode = htmlCode;
    }

    public String getRoutineCode() {
        return routineCode;
    }

    public void setRoutineCode(String routineCode) {
        this.routineCode = routineCode;
    }

    public String getResponsePhrase() {
        return responsePhrase;
    }

    public void setResponsePhrase(String responsePhrase) {
        this.responsePhrase = responsePhrase;
    }

    public String getRifProcedure() {
        return rifProcedure;
    }

    public void setRifProcedure(String rifProcedure) {
        this.rifProcedure = rifProcedure;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getRifFolder() {
        return rifFolder;
    }

    public void setRifFolder(String rifFolder) {
        this.rifFolder = rifFolder;
    }

    public String getUPLOAD_DIRECTORY() {
        return UPLOAD_DIRECTORY;
    }

    public void setUPLOAD_DIRECTORY(String UPLOAD_DIRECTORY) {
        this.UPLOAD_DIRECTORY = UPLOAD_DIRECTORY;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSplashArgs() {
        return splashArgs;
    }

    public void setSplashArgs(String splashArgs) {
        this.splashArgs = splashArgs;
    }

    public String getOSbasepath() {
        return OSbasepath;
    }

    public void setOSbasepath(String OSbasepath) {
        this.OSbasepath = OSbasepath;
    }

    public String getOSfolder() {
        return OSfolder;
    }

    public void setOSfolder(String OSfolder) {
        this.OSfolder = OSfolder;
    }

    public String getOSfilename() {
        return OSfilename;
    }

    public void setOSfilename(String OSfilename) {
        this.OSfilename = OSfilename;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getFormToLaod() {
        return formToLaod;
    }

    public void setFormToLaod(String formToLaod) {
        this.formToLaod = formToLaod;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public String getFormCopyTag() {
        return formCopyTag;
    }

    public void setFormCopyTag(String formCopyTag) {
        this.formCopyTag = formCopyTag;
    }

    public String getFormObjName() {
        return formObjName;
    }

    public void setFormObjName(String formObjName) {
        this.formObjName = formObjName;
    }

    public String getFormRowKey() {
        return formRowKey;
    }

    public void setFormRowKey(String formRowKey) {
        this.formRowKey = formRowKey;
    }

}
