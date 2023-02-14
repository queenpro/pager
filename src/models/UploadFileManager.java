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
import REVOpager.EVOpagerDBconnection;
import REVOpager.EVOuser;
import REVOsetup.ErrorLogger;
import REVOsetup.OSenv;
import REVOwebsocketManager.WShandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.ShowItForm;
import static showIt.ShowItForm.encodeURIComponent;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class UploadFileManager {

    EVOpagerParams myParams;
    Settings mySettings;
    public UpToDBpicture pic;

    public UploadFileManager(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        pic = new UpToDBpicture();
    }

    public void uploadFile2FileSystem(portalClass myPortal, Part filePart, String oFormTBS) throws IOException { 
            WShandler myWShandler = new WShandler(mySettings, mySettings.getInstallationName(myParams)); 
            myWShandler.sendToBrowser("status", null, myParams.getCKtokenID(), "uploadFile .");


        Connection conny = new EVOpagerDBconnection(myPortal.myParams, myPortal.mySettings).ConnLocalDataDB();
//===================================================================================================           
        //--- chiedo al DB quale percorso dovrei trovare come  absoluteFilePath 
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myPortal.myParams, myPortal.mySettings);
        myPortal.OSbasepath = myManager.getDirective("uploadBasePath");
        System.out.println("\n************\nSbasepath:" + myPortal.OSbasepath + "\n************\n");
        myPortal.WebserverBasepath = myManager.getDirective("localWebserverBasePath");
        System.out.println("\n************\nWebserverBasepath:" + myPortal.WebserverBasepath + "\n************\n");

//==================================================================  
//stabiliamo quale sarà il percorso del filename...
// LOCAL_WEB_SERVER_xxxyyy indica un percorso sul webserver locale: si userà come base  myPortal.WebserverBasepath
// diversamente si userà come base  myPortal.OSbasepath
        String percorsoBase = myPortal.OSbasepath;

//==================================================================
        InputStream inputStream = null;

        String name = filePart.getSubmittedFileName();
        String nameToStore = "";

//                        FileItem item = (FileItem) filePart;
        if (filePart != null) {

//                            System.out.println("@@@getSubmittedFileName: " + filePart.getSubmittedFileName());
            System.out.println(filePart.getSize());
            System.out.println(filePart.getContentType());
            inputStream = filePart.getInputStream();

            //-----CREAZIONE NOME------
            System.out.println("item.getName()X---------->" + filePart.getName());
            int posizionePunto = name.lastIndexOf(".");
            //System.out.println("posizionePunto ---------->" + posizionePunto);

            //----------------------------------------------------------    
// <editor-fold defaultstate="collapsed" desc="PROCEDURA nome casuale">
            if (myPortal.rifProcedure.equalsIgnoreCase("rename")) {
                UUID idOne = null;
                idOne = UUID.randomUUID();
                String newobjectID = "" + idOne;
                // ORA DEVO SALVARE NEL DB l'ID che userò per salvare il file
                System.out.println("  newobjectID>" + newobjectID);

                myPortal.OSfolder = "FOLDER/";
                String folderPath = percorsoBase + myPortal.OSfolder;
                System.out.println("_folderPath>" + folderPath);
                new File(folderPath).mkdirs();

                String estensione = name.substring(posizionePunto);
                System.out.println("_estensione ---------->" + estensione);
                name = newobjectID + estensione;
                System.out.println("_name ---------->" + name);
//----------

                OutputStream outputStream = null;
                try {
                    File file = new File(percorsoBase + myPortal.OSfolder + File.separator + name);
                    outputStream = new FileOutputStream(file);

                    int read = 0;
                    byte[] bytes = new byte[1024];
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                } finally {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }

//----------
////////                                try {
////////                                    item.write(new File(myPortal.OSbasepath + myPortal.OSfolder + File.separator + name));
////////                                } catch (Exception ex) {
////////                                    Logger.getLogger(portal.class.getName()).log(Level.SEVERE, null, ex);
////////                                }
                nameToStore = myPortal.OSfolder + File.separator + name;
            } else // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="PROCEDURA nome Oggetto">            
            if (myPortal.rifProcedure.equalsIgnoreCase("OBJname")) {
                String estensione = name.substring(posizionePunto);
                String nomeOriginale = name.substring(0, posizionePunto);
                // adesso salvo nel DB il nome del file da conservare per il download.
                // salvo solo pa parte del nome a valle della pathBase (nameToStore)
                // ricavo nome cartella e nome file fa fatherID
                //String formID = "";
                String objID = "";
                String fileID = "";
                String[] items = myPortal.fatherID.split("-");
                List<String> roules = Arrays.asList(items);
                if (roules.size() == 4) {
                    myPortal.formID = roules.get(0);
                    objID = roules.get(2);
                    fileID = roules.get(3);
                }
                System.out.println("_estensione ---------->" + estensione);
                System.out.println("_nome originale ---------->" + nomeOriginale);
                System.out.println("formID ---------->" + myPortal.formID);
                System.out.println("objID ---------->" + objID);
                System.out.println("fileID ---------->" + fileID);
                //TODO!!!!!!!!!!!! devo ricavare il nome della tabella dal nome del form
                ShowItForm myForm = new ShowItForm(myPortal.formID, myPortal.myParams, myPortal.mySettings);
                myForm.setMyParams(myPortal.myParams);
                myForm.setMySettings(myPortal.mySettings);
                myForm.buildSchema();
                String myTable = myForm.getMainTable();
                String myKyefield = myForm.getKEYfieldName();
                String myKyefieldType = myForm.getKEYfieldType();

                System.out.println("objectName ---------->" + objID);
                System.out.println("myTable ---------->" + myTable);
                System.out.println("myKyefield ---------->" + myKyefield);
                System.out.println("myKyefieldType ---------->" + myKyefieldType);
                String SQLphrase = "";

//                                String originalFilename = fileID + estensione;
                // ricavo da GES le regole per formare il nome
                String nameRules = "";
                String actionParams = "";
                for (int i = 0; i < myForm.objects.size(); i++) {
                    if (myForm.objects.get(i).getName().equalsIgnoreCase(objID)) {
                        nameRules = myForm.objects.get(i).CG.getParams();
                        actionParams = myForm.objects.get(i).getActionParams();
                        break;
                    }

                }
                System.out.println("nameRules ---------->" + nameRules);
                System.out.println("actionParams ---------->" + actionParams);
                String fieldForFilename = "";
                if (actionParams != null) {
                    try {
                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject;
                        jsonObject = (JSONObject) jsonParser.parse(actionParams);
                        fieldForFilename = jsonObject.get("copyFilename").toString();
                    } catch (Exception e) {

                    }
                }

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject;
                String RULESarray = null;
                String newNameRules = nameRules;
                String tbsJson = "{\"RULES\":" + oFormTBS + "}";
                try {
                    jsonObject = (JSONObject) jsonParser.parse(tbsJson);
                    RULESarray = jsonObject.get("RULES").toString();
                    if (RULESarray != null && RULESarray.length() > 0) {
                        JSONParser parser = new JSONParser();
                        Object obj;
                        obj = parser.parse(RULESarray);
                        JSONArray array = (JSONArray) obj;
                        String childType = "";
                        String Marker = "";
                        String value = "";
                        for (Object riga : array) {
                            jsonObject = (JSONObject) jsonParser.parse(riga.toString());
                            try {
                                Marker = jsonObject.get("childMarker").toString();
                            } catch (Exception e) {
                            }
                            try {
                                value = jsonObject.get("value").toString();
                            } catch (Exception e) {
                            }
                            String toReplace = "###" + Marker + "###";
                            newNameRules = newNameRules.replace(toReplace, value);
                        }
                    }
                } catch (ParseException ex) {
                    System.out.println("error in line 2082");
                    Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                String completeFileName = "";
//*******PERCORSO DI SALVATAGGIO***********
////////                String systemBasepath = System.getProperty("user.dir");
////////                System.out.println("Working Directory = " + systemBasepath);
////////                String slash = percorsoBase.substring(0, 1);
////////                if (systemBasepath.contains("\\")) {
////////                    slash = "\\";
////////                    percorsoBase = percorsoBase.replace("/", "\\");
////////                } else {
////////                    slash = "/";
////////                    percorsoBase = percorsoBase.replace("\\", "/");
////////                }
////////                System.out.println("slash = " + slash);
                OSenv myEnv = new OSenv();

                String folderPath = "";
                if (nameRules != null && newNameRules.length() > 0) {
                    String toReplace = "$$$slash$$$";
                    newNameRules = newNameRules.replace(toReplace, myEnv.getOSslash());
                    toReplace = "$$$extension$$$";
                    newNameRules = newNameRules.replace(toReplace, estensione);
                    toReplace = "$$$originalFilename$$$";
                    newNameRules = newNameRules.replace(toReplace, nomeOriginale);

                    String newID = "XXXXXXXXXXXXXXXXXX";
                    UUID idOne = null;
                    idOne = UUID.randomUUID();
                    String newToken = "" + idOne;

                    String RT10newID = newID.substring(0, 8) + newToken.substring(0, 8);
                    RT10newID = RT10newID.replace("-", "");
                    newNameRules = newNameRules.replace("$$$RANDOMTEXT10$$$", RT10newID);

                    String RT16newID = newID.substring(0, 16) + newToken.substring(0, 16);
                    RT16newID = RT16newID.replace("-", "");
                    newNameRules = newNameRules.replace("$$$RANDOMTEXT16$$$", RT16newID);

                    newID = newID.substring(0, 5) + "_" + newToken;
                    newID = newID.replace("-", "");
                    newNameRules = newNameRules.replace("$$$RANDOMTEXT$$$", newID);

                    newNameRules = newNameRules.replace("$$$newRandom$$$", newToken);
                    newNameRules = newNameRules.replace("$$$CKUSER$$$", myParams.getCKuserID());
                    newNameRules = newNameRules.replace("$$$USER$$$", myParams.getCKuserID());
                    newNameRules = myEnv.normalizePath(newNameRules);

                    int posizioneSlash = newNameRules.lastIndexOf(myEnv.getOSslash());
                    System.out.println("posizioneSlash ---------->" + posizioneSlash);
                    String percorso = newNameRules.substring(0, posizioneSlash);
                    percorso = percorso.trim();
                    percorso = percorso.replace("'", "");
                    System.out.println("percorso ---------->" + percorso);
                    completeFileName = newNameRules;
                    if (percorso.startsWith("LOCAL_WEB_SERVER_")) {
                        percorso = percorso.substring(17, percorso.length());
                        System.out.println("percorso MODIFICATO PER WEBSERVER: ---------->" + percorso);
                        completeFileName = completeFileName.substring(17, completeFileName.length());
                        percorsoBase = myPortal.WebserverBasepath;
                    }

                    completeFileName = completeFileName.trim();
                    completeFileName = completeFileName.replace("'", "");
                    System.out.println("completeFileName ---------->" + completeFileName);

                    System.out.println("percorso ---------->" + percorso);
                    folderPath = percorsoBase
                            + myPortal.getMyParams().getCKcontextID()
                            + myEnv.getOSslash()
                            + percorso;
                    System.out.println("folderPath ---------->" + folderPath);
                    // new File(percorsoBase + percorso).mkdirs();
                } else {
                    //NON CI SONO REGOLE
                    // aggiungo il CKcontextID come cartella nel percorso base
                    folderPath = percorsoBase;
                    System.out.println("name>" + name);
                    completeFileName = name;
                }
                //creo le cartelle in base al nome costruiito (che può contenere sottocartelle)
                System.out.println("folderPath>" + folderPath);
                folderPath = myEnv.normalizePath(folderPath);
                new File(folderPath).mkdirs();
                //---------------------------------------------
                String usatoPerFile = myEnv.getOSbasePath() + percorsoBase
                        + myPortal.getMyParams().getCKcontextID()
                        + myEnv.getOSslash()
                        + completeFileName;
                if (percorsoBase.startsWith("[]")){
                    usatoPerFile = percorsoBase
                        + myPortal.getMyParams().getCKcontextID()
                        + myEnv.getOSslash()
                        + completeFileName;
                    usatoPerFile = usatoPerFile.replace("[]", "");
                    
                }
                
                
                usatoPerFile = myEnv.normalizePath(usatoPerFile);
                String safeCFN = completeFileName;
                safeCFN = safeCFN.replace("\\", "/");//nel json non ci possono essere backslash... sostituisco con slash 
                String usatoPerDB = "{\"FileSysName\":\"" + safeCFN + "\", \"originalName\":\"" + nomeOriginale + "\", \"ext\":\"" + estensione + "\"}";
                System.out.println("*usato per file ---------->" + usatoPerFile);
                System.out.println("usato per DB ---------->" + usatoPerDB);
                myWShandler.sendToBrowser("status", null, myParams.getCKtokenID(), "usato per file ---------->" + usatoPerFile);
//                System.out.println(">>>>uploadManager: " + usatoPerFile);

                // Nel database salvo il nome completo escluso il nome della cartella dato dal context
                // in ogni caso servendo il file sarà aggiunto in automatico il context per evitare hacking
                if (myKyefieldType != null && (myKyefieldType.contains("int") || myKyefieldType.contains("INT"))) {
                    SQLphrase = "UPDATE " + myTable + " SET  `" + objID + "`='" + usatoPerDB + "' WHERE " + myKyefield + "= " + fileID + "";
                } else {
                    SQLphrase = "UPDATE " + myTable + " SET  `" + objID + "`='" + usatoPerDB + "' WHERE " + myKyefield + "= '" + fileID + "'";
                }
                System.out.println(">>>>SQLphrase: " + SQLphrase);
                PreparedStatement ps = null;
                try {
                    ps = conny.prepareStatement(SQLphrase);
                    int i = ps.executeUpdate();
                } catch (SQLException ex) {

                }

                if (fieldForFilename != null && fieldForFilename.length() > 0) {
                    if (myKyefieldType != null && (myKyefieldType.contains("int") || myKyefieldType.contains("INT"))) {
                        SQLphrase = "UPDATE " + myTable + " SET  `" + fieldForFilename + "`='" + name + "' WHERE " + myKyefield + "= " + fileID + "";
                    } else {
                        SQLphrase = "UPDATE " + myTable + " SET  `" + fieldForFilename + "`='" + name + "' WHERE " + myKyefield + "= '" + fileID + "'";
                    }
                    System.out.println(">>>>SQLphrase: " + SQLphrase);
                    try {
                        ps = conny.prepareStatement(SQLphrase);
                        int i = ps.executeUpdate();
                    } catch (SQLException ex) {

                    }
                }

                //   myPortal.OSfolder = myPortal.formID + slash + objID;
                OutputStream outputStream = null;
                try {
                    System.out.println(">>>> ESEGUO IL SALVATAGGIO.");
                    File file = new File(usatoPerFile);
                    outputStream = new FileOutputStream(file);
                    IOUtils.copy(inputStream, outputStream);
                    System.out.println(">>>>ok SALVATAGGIO ESEGUITO.");
                    

                } catch (Exception e) {
                    System.out.println("error outputStream>" + e.toString());
                    myWShandler.sendToBrowser("status", null, myParams.getCKtokenID(), "error outputStream>" + e.toString());
                    new File(folderPath).mkdirs();
                    try {
                        File file = new File(usatoPerFile);
                        outputStream = new FileOutputStream(file);
                        IOUtils.copy(inputStream, outputStream);
                        System.out.println(">>>> SALVATAGGIO ESEGUITO.");
                    } catch (Exception ex) {
                        System.out.println("error2 outputStream>" + ex.toString());
                        myWShandler.sendToBrowser("status", null, myParams.getCKtokenID(), "error2 outputStream>" + ex.toString());
                        System.out.println(">>>> SALVATAGGIO NON ESEGUITO.");
                    }
                } finally {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }

                nameToStore = name;

            } else // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="PROCEDURA per altri tipi di upload">
            {
                // altri tipi di upload
                System.out.println("rifProcedure NON DEFINITO>" + myPortal.rifProcedure);
            }
// </editor-fold>
        }

        try {
            conny.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }

    }

    public String uploadFile2DB(portalClass myPortal, Part filePart) throws IOException {
        //**************************************************************************
        //In realtà è previsto l'upload di SOLE IMMAGINI (pictures) sul DB,
        // per altri documenti si usa uploadFile2FileSystem
        //**************************************************************************

//              String WDTH = (request.getParameter("width"));
//                String HGHT = (request.getParameter("height"));
//                String tabella = (request.getParameter("formName"));
//                String fieldMedia = (request.getParameter("cellName"));
//                String primaryFieldName = (request.getParameter("primaryFieldName"));
//                String primaryFieldValue = (request.getParameter("primaryFieldValue"));
//                String primaryFieldType = (request.getParameter("primaryFieldType"));
//                myPortal.formID = (request.getParameter("formID"));
//                myPortal.formCopyTag = (request.getParameter("formCopyTag"));
//                myPortal.formObjName = (request.getParameter("formObjName"));
//                myPortal.formRowKey = (request.getParameter("formRowKey"));
//                System.out.println("FormName:" + tabella);
//                System.out.println("CellName:" + fieldMedia);
//                System.out.println("primaryFieldName:" + primaryFieldName);
//                System.out.println("primaryFieldValue:" + primaryFieldValue);
//                System.out.println("WDTH:" + WDTH);
//                System.out.println("HGHT:" + HGHT);
        Connection conny = new EVOpagerDBconnection(myPortal.myParams, myPortal.mySettings).ConnLocalDataDB();

        InputStream inputStream = null;

        if (filePart != null) {
            System.out.println(filePart.getName());
            System.out.println(filePart.getSize());
            System.out.println(filePart.getContentType());
            inputStream = filePart.getInputStream();
        }
        String message = null;  // message will be sent back to client

        try {
            String sql = "UPDATE " + pic.tabella + " SET "
                    + pic.fieldMedia + " = ? WHERE " + pic.primaryFieldName
                    + " ='" + pic.primaryFieldValue + "'";

            System.out.println(sql);
            PreparedStatement statement = conny.prepareStatement(sql);

            if (inputStream != null) {
                // fetches input stream of the upload file for the blob column
                statement.setBlob(1, inputStream);
//                      statement.setBinaryStream(1, inputStream, 100000000);
 

            }
            int row = statement.executeUpdate();
            if (row > 0) {
                message = "File uploaded and saved into database";
            }
        } catch (SQLException ex) {
            message = "ERROR: " + ex.getMessage();
            ex.printStackTrace();
        } finally {
            if (conny != null) {
                // closes the database connection
                try {
                    conny.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

        }
//*********************
//**RETURN IMAGE CODE*****
//**********************
        String xW = pic.WDTH;
        String xH = pic.HGHT;

        myPortal.htmlCode = "<DIV"
                + " id=\"" + myPortal.formID + "-" + myPortal.formCopyTag + "-" + myPortal.formObjName + "-" + myPortal.formRowKey + "-PIC\""
                + ">";
        UUID idOne = null;
        idOne = UUID.randomUUID();
        String image = "<img  alt=\"...\" src='portal?rnd=" + idOne + "&target=requestsManager&gp=";
        myPortal.params = "\"params\":\"" + encodeURIComponent(myPortal.myParams.makePORTALparams()) + "\"";
        myPortal.connectors = "\"connectors\":[{\"door\":\"RenderPic\","
                + "\"event\":\"fromDB\","
                + "\"table\":\"" + pic.tabella + "\","
                + "\"keyfield\":\"" + pic.primaryFieldName + "\","
                + "\"keyValue\":\"" + pic.primaryFieldValue + "\","
                + "\"keyType\":\"" + pic.primaryFieldType + "\","
                + "\"picfield\":\"" + pic.fieldMedia + "\","
                + " }]";
        String utils = "\"responseType\":\"text\"";
        String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(myPortal.params) + "," + encodeURIComponent(myPortal.connectors) + "}";

        image += encodeURIComponent(gp);
        image += "'  width='" + xW + "px' heigth='" + xH + "px' >";

        return image;
    }

    public class UpToDBpicture {

        String WDTH = "";
        String HGHT = "";
        String tabella = "";
        String fieldMedia = "";
        String primaryFieldName = "";
        String primaryFieldValue = "";
        String primaryFieldType = "";

        public String getWDTH() {
            return WDTH;
        }

        public void setWDTH(String WDTH) {
            this.WDTH = WDTH;
        }

        public String getHGHT() {
            return HGHT;
        }

        public void setHGHT(String HGHT) {
            this.HGHT = HGHT;
        }

        public String getTabella() {
            return tabella;
        }

        public void setTabella(String tabella) {
            this.tabella = tabella;
        }

        public String getFieldMedia() {
            return fieldMedia;
        }

        public void setFieldMedia(String fieldMedia) {
            this.fieldMedia = fieldMedia;
        }

        public String getPrimaryFieldName() {
            return primaryFieldName;
        }

        public void setPrimaryFieldName(String primaryFieldName) {
            this.primaryFieldName = primaryFieldName;
        }

        public String getPrimaryFieldValue() {
            return primaryFieldValue;
        }

        public void setPrimaryFieldValue(String primaryFieldValue) {
            this.primaryFieldValue = primaryFieldValue;
        }

        public String getPrimaryFieldType() {
            return primaryFieldType;
        }

        public void setPrimaryFieldType(String primaryFieldType) {
            this.primaryFieldType = primaryFieldType;
        }

    }
}
