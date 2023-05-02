/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package REVOpack;

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.EVOpagerDBconnection;
import REVOpager.QPfield;
import REVOpager.QPtable;
import REVOsetup.EVOsetup;
import REVOsetup.ErrorLogger;
import REVOwebsocketManager.WShandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.eventManager;

/**
 *
 * @author Franco
 */
public class ClassQPmanageUpdate {

    int flagTimestampUsed = 0;

    ErrorLogger el;

    public String mode = "";
    String feed = "";
    String feedback = "";
    EVOpagerParams myParams;
    Settings mySettings;
    String PROJECT_ID;
    String LOCAL_SERVER_URL;
    String LOCAL_SERVER_ALTURL;
    int localRelease;
    int qpRelease;

    public ClassQPmanageUpdate(EVOpagerParams xParams, Settings xSettings) {
        this.myParams = new EVOpagerParams();
        this.mySettings = xSettings;
        this.myParams = xParams;
        this.mode = "TOTAL";
        PROJECT_ID = " " + myParams.getCKprojectName();
        LOCAL_SERVER_URL = mySettings.getData_defaultSQLserver();
        LOCAL_SERVER_ALTURL = mySettings.getData_alternativeSQLserver();

    }

    public String autoUpdate() {
        feedback = "Start Update.";
//1. verifico la mia release 
        aggiornamenti myUpdate = new aggiornamenti();
        System.out.println("\n*******\n1. CHIEDO AUTORIZZAZIONI al server QUEENPRO --->" + mySettings.getQP_centralManagerURL());
        try {
            myUpdate.getAuthFromQueenpro();
        } catch (Exception e) {
            System.out.println("ERRORE IN RICHIESTA AUTORIZZAZIONI");
        }
        System.out.println("swUpdatePresent:\t" + myUpdate.swUpdatePresent);
        System.out.println("swUpdateAuthorized:\t" + myUpdate.swUpdateAuthorized);
        System.out.println("templateUpdatePresent:\t" + myUpdate.templateUpdatePresent);
        System.out.println("templateUpdateAuthorized:\t" + myUpdate.templateUpdateAuthorized);
        System.out.println("dbUpdatePresent:\t" + myUpdate.dbUpdatePresent);
        System.out.println("dbUpdateAuthorized:\t" + myUpdate.dbUpdateAuthorized);
        System.out.println("swVersionAppropriate:\t" + myUpdate.swVersionAppropriate);

        //  System.out.println("\n*******\n2. NORMALIZZO FRONTEND");
        //  myUpdate.normalizeFEtbContent();// invio i miei params come APP che saranno usati da qpmanager per ricavare i valori relativi al progetto giusto
        //  myParams.printParams(" ClassQPmanageUpdate-autoUpdate");
        if (myUpdate.swVersionAppropriate == true) {
            if (myUpdate.dbUpdatePresent == true) {
                System.out.println("PROCEDO CON AGGIORNAMENTO DATABASE");
                myUpdate.makeModel();
                feedback += "\n\t database model Updated .";
                System.out.println("PROCEDO CON AGGIORNAMENTO TEMPLATE");
                myUpdate.normalizeFEtbContent();
                feedback += "\n\t template Updated .";
            } else if (myUpdate.templateUpdatePresent == true) {
                System.out.println("PROCEDO CON AGGIORNAMENTO TEMPLATE");
                myUpdate.normalizeFEtbContent();
                feedback += "\n\t template Updated .";
            }
            try {
                System.out.println("PROCEDO CON AGGIORNAMENTO DIRECTIVES");
                myUpdate.fillEVOdirectives(myParams, mySettings);
                feedback += "\n\t EVO directives updated .";
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ClassQPmanageUpdate.class.getName()).log(Level.SEVERE, null, ex);

                feedback += "\n\t ERROR UPDATING EVO DIRECTIVES. Please retry or contact service.";
            }
            System.out.println("PROCEDURA DI UPDATE COMPLETATA.\n=====================================================\n");
        }

        return feedback;
    }

    public class aggiornamenti {

        boolean swUpdatePresent;
        boolean swUpdateAuthorized;
        boolean dbUpdatePresent;
        boolean dbUpdateAuthorized;
        boolean templateUpdatePresent;
        boolean templateUpdateAuthorized;
        boolean swVersionAppropriate;
        String EVOprjVersion;
        String EVOprjRelease;

        ArrayList<QPtable> tabs = new ArrayList<QPtable>();
        String dbID = null;
        String prjID;

        public aggiornamenti() {
            swUpdatePresent = false;
            swUpdateAuthorized = false;
            dbUpdatePresent = false;
            dbUpdateAuthorized = false;
            templateUpdatePresent = false;
            templateUpdateAuthorized = false;
            swVersionAppropriate = false;
            tabs = new ArrayList<QPtable>();
        }

        public void getAuthFromQueenpro() {
            dbUpdatePresent = true;
            dbUpdateAuthorized = true;
            templateUpdatePresent = true;
            templateUpdateAuthorized = true;
            swVersionAppropriate = true;

            Connection localconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
            String LOCALprjVersion = null;
            String LOCALprjRelease = null;
            String LOCALSwVersion = null;
            if (localconny != null) {
                try {
                    Statement locals = localconny.createStatement();
                    String SQLphrase = "SELECT * FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='FE_version';";
//                System.out.println("->autoUpdate() ->SQLphrase: " + SQLphrase);

                    ResultSet localrs = locals.executeQuery(SQLphrase);
                    while (localrs.next()) {
                        LOCALprjVersion = localrs.getString("infoValue");
                        break;
                    }
                    SQLphrase = "SELECT * FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='FE_release';";
//                System.out.println("->autoUpdate() ->SQLphrase: " + SQLphrase);

                    localrs = locals.executeQuery(SQLphrase);
                    while (localrs.next()) {
                        LOCALprjRelease = localrs.getString("infoValue");
                        break;
                    }

                    LOCALSwVersion = mySettings.getSoftwareVersion();

                } catch (SQLException ex) {
                    Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
                    // non trovo info riguardo a version e release;
                    System.out.println("\nnon trovo info riguardo a version e release\n ");
                    feed = ex.toString();
                    this.templateUpdatePresent = true;
                    this.templateUpdateAuthorized = true;

                    LOCALprjRelease = null;
                    LOCALprjVersion = null;
                }

//                System.out.println("->autoUpdate() ->LOCALprjRelease: " + LOCALprjRelease);
//                System.out.println("->autoUpdate() ->LOCALprjVersion: " + LOCALprjVersion);
//                System.out.println("->autoUpdate() ->LOCALSwVersion: " + LOCALSwVersion);
//2. verifico  release su queenpro
                String connectors = "[{\"door\":\"manageUpdate\","
                        + "     \"event\":\"getQPversion\""
                        + " }]";
                String response = askQueenpro(connectors);
                response = decodificaJSONdaURL(response);
                System.out.println("getAuthFromQueenpro-->response: " + response);
                //{"EVOminSWrelease":"1","EVOprjVersion":"1","EVOprjRelease":"125"}
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject;
                String EVOminSWrelease = null;
                try {
                    jsonObject = (JSONObject) jsonParser.parse(response);

                    try {
                        EVOminSWrelease = jsonObject.get("EVOminSWrelease").toString();
                    } catch (Exception e) {
                    }
                    try {
                        EVOprjVersion = jsonObject.get("EVOprjVersion").toString();
                    } catch (Exception e) {
                    }
                    try {
                        EVOprjRelease = jsonObject.get("EVOprjRelease").toString();
                    } catch (Exception e) {
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(ClassQPmanageUpdate.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (LOCALSwVersion == null || LOCALSwVersion.length() < 1 || LOCALprjVersion == null || LOCALprjVersion == "" || LOCALprjRelease == null || LOCALprjRelease == "") {
                    this.templateUpdatePresent = true;
                    this.templateUpdateAuthorized = true;
                } else {
                    int locVer = 0;
                    int evoVer = 0;
                    int locRel = 0;
                    int evoRel = 0;
                    int locSW = 0;
                    int evoMinSW = 0;

                    try {
                        locVer = Integer.parseInt(LOCALprjVersion);
                    } catch (Exception e) {
                    }
                    try {
                        evoVer = Integer.parseInt(EVOprjVersion);
                    } catch (Exception e) {
                    }
                    try {
                        locRel = Integer.parseInt(LOCALprjRelease);
                    } catch (Exception e) {
                    }
                    try {
                        evoRel = Integer.parseInt(EVOprjRelease);
                    } catch (Exception e) {
                    }
                    try {
                        locSW = Integer.parseInt(LOCALSwVersion);
                    } catch (Exception e) {
                    }
                    try {
                        evoMinSW = Integer.parseInt(EVOminSWrelease);
                    } catch (Exception e) {
                    }

//                    System.out.println("->autoUpdate() ->EVOminSWrelease: " + evoMinSW);
//                    System.out.println("->autoUpdate() ->EVOversion: " + evoVer);
//                    System.out.println("->autoUpdate() ->EVOrelease: " + evoRel);
                    //ATTENZIONE! qui non riesco a valutare se è diponibile una versione nuova del sw
                    if (locSW >= evoMinSW) {
                        this.swVersionAppropriate = true;//la versione sw è maggiore della minimumRelease per consentire aggiornamenti
                    } else {
                        this.swVersionAppropriate = false;//la versione del sw non è autorizzata a fare aggiornamenti
                        this.dbUpdateAuthorized = false;
                        this.templateUpdateAuthorized = false;
                    }

                    if (evoVer > locVer) {
                        this.dbUpdatePresent = true; //database da aggiornare
                    } else {
                        this.dbUpdatePresent = false;
                    }

                    if (evoRel > locRel) {
                        this.templateUpdatePresent = true; //template da aggiornare
                    } else {
                        this.templateUpdatePresent = false;
                    }
                    System.out.println("->autoUpdate() ->LOCALprjRelease: " + LOCALprjRelease + "  ->EVOrelease: " + evoRel + "  ->templateUpdatePresent: " + templateUpdatePresent);
                    System.out.println("->autoUpdate() ->LOCALprjVersion: " + LOCALprjVersion + "  ->EVOversion: " + evoVer + "  ->dbUpdatePresent: " + dbUpdatePresent);
                    System.out.println("->autoUpdate() ->LOCALSwVersion: " + LOCALSwVersion + "  ->EVOminSWrelease: " + evoMinSW + "  ->swVersionAppropriate: " + swVersionAppropriate);

                }

//                System.out.println("->autoUpdate() ->fropm QP: " + feed);
            } else {
                System.out.println("->autoUpdate() MANCATA CONNESSIONE!");
            }
            try {
                localconny.close();
            } catch (SQLException ex) {
                Logger.getLogger(ClassQPmanageUpdate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private String decodificaJSONdaURL(String JsonCodificato) {
            if (JsonCodificato == null || JsonCodificato.equalsIgnoreCase("null")) {
                return "{}";
            }
            JSONObject jsonObject = null;
            boolean flagDecodificato = false;
            JSONParser jsonParser = new JSONParser();
            JsonCodificato = JsonCodificato.replace(":undefined", ":\"\"");
            try {

                jsonObject = (JSONObject) jsonParser.parse(JsonCodificato);
                flagDecodificato = true;
            } catch (ParseException ex) {
                try {
                    JsonCodificato = java.net.URLDecoder.decode(JsonCodificato, "UTF-8");
                    jsonObject = (JSONObject) jsonParser.parse(JsonCodificato);
                    flagDecodificato = true;
                } catch (ParseException ex1) {
                    try {
                        JsonCodificato = java.net.URLDecoder.decode(JsonCodificato, "UTF-8");
                        jsonObject = (JSONObject) jsonParser.parse(JsonCodificato);
                        flagDecodificato = true;
                    } catch (ParseException ex2) {
                        System.out.println("TERZO TENTATIVO FALLITO " + ex2.toString());
                        System.out.println("Non parsable row: " + JsonCodificato);

                    } catch (UnsupportedEncodingException ex2) {
                        Logger.getLogger(ClassQPmanageUpdate.class.getName()).log(Level.SEVERE, null, ex2);
                    }
                } catch (UnsupportedEncodingException ex1) {
                    Logger.getLogger(ClassQPmanageUpdate.class.getName()).log(Level.SEVERE, null, ex1);
                }

            }
            return JsonCodificato;
        }

        private String askQueenpro(String connectors) {
            String response = null;
            String address = mySettings.getQP_centralManagerURL();
            System.out.println("\n------------------\n askQueenpro ->address: " + address);
            String args = "?target=requestsManager&gp=";
            JSONObject obj = new JSONObject();
            JSONObject params = myParams.makeJsonPORTALparams();

            obj.put("params", params);
            obj.put("responseType", "text");
            obj.put("connectors", encodeURIComponent(connectors));

            String gp = encodeURIComponent(obj.toString());

            args += encodeURIComponent(gp);
            address += args;
            System.out.println("askQueenpro ->URL: " + address);

            try {

                URL url = new URL(address);//your url i.e fetch data from .
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP Error code : "
                            + conn.getResponseCode());
                }
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(in);
                String output;
                while ((output = br.readLine()) != null) {
//                    System.out.println(output);
                    response = output;
                }
                conn.disconnect();
                //System.out.println("RESPONSE FROM QP :  " + output);
            } catch (Exception e) {
//                System.out.println("Exception in NetClientGet:- " + e);

                System.out.println("Retrieve emergency configuration :- ");
                address = "https://ffs.it/download/qpconf/qpconf.html";
                try {

                    URL url = new URL(address);//your url i.e fetch data from .
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP Error code : "
                                + conn.getResponseCode());
                    }
                    InputStreamReader in = new InputStreamReader(conn.getInputStream());
                    BufferedReader br = new BufferedReader(in);
                    String output = "";
                    String confRetreived = "";
                    while ((output = br.readLine()) != null) {
//                         System.out.println("->output: " + output);
                        confRetreived = output;
                    }
                    conn.disconnect();
                    address = "";

//                         System.out.println("1->confRetreived: " + confRetreived);
                    // leggo il parametro di emergenza
                    if (confRetreived != null && confRetreived.length() > 0) {
//                         System.out.println("2->confRetreived: " + confRetreived);
                        try {
                            JSONParser jsonParser = new JSONParser();
                            JSONObject jObject = (JSONObject) jsonParser.parse(confRetreived);
                            try {
                                address = jObject.get("altCentralManager").toString();
                            } catch (Exception ex) {
                            }
                            System.out.println("askQueenpro->address alternativo trovato su FFS.IT: " + address);
                            System.out.println("askQueenpro->REITERO LA RICHIESTA ");
                            if (address.length() > 0) {
                                response = null;
                                address += "qpmanager/centralManager";

//                                System.out.println("->address: " + address);
                                args = "?target=requestsManager&gp=";

                                obj = new JSONObject();
                                params = myParams.makeJsonPORTALparams();

                                obj.put("params", params);
                                obj.put("responseType", "text");
                                obj.put("connectors", encodeURIComponent(connectors));

                                gp = encodeURIComponent(obj.toString());

                                args += encodeURIComponent(gp);
                                address += args;
//                                System.out.println("->autoUpdate() ->address: " + address);

                                try {

                                    url = new URL(address);//your url i.e fetch data from .
                                    conn = (HttpURLConnection) url.openConnection();
                                    conn.setRequestMethod("GET");
                                    conn.setRequestProperty("Accept", "application/json");
                                    if (conn.getResponseCode() != 200) {
                                        throw new RuntimeException("Failed : HTTP Error code : "
                                                + conn.getResponseCode());
                                    }
                                    in = new InputStreamReader(conn.getInputStream());
                                    br = new BufferedReader(in);
                                    while ((output = br.readLine()) != null) {
//                                        System.out.println("askQueenpro-->RISPOSTA: " + output);
                                        response = output;
                                    }
                                    conn.disconnect();
                                } catch (Exception ee) {
                                    System.out.println("ERRORE: " + ee.toString());
                                }

                            } else {

                                System.out.println("-JSON ADDRESS NOT COMPILED: " + address);
                            }

                        } catch (org.json.simple.parser.ParseException pe) {
                            System.out.println("parser error:- " + pe.toString());
                        }

                    } else {
                        System.out.println("JSON DA ffs.it NULLO");
                    }

                } catch (Exception ex) {
                    System.out.println("Exception in retrieving emergency config:- " + ex);

                }
            }

            return response;
        }

        private void getQPdatabaseSchema(String type) {

            tabs = new ArrayList<QPtable>();
            dbID = null;
            prjID = null;
            //1. cerco ID del database
            String connectors = "[{\"door\":\"manageUpdate\","
                    + "     \"event\":\"getQP_FEtabs\""
                    + "     \"type\":\"" + type + "\""
                    + " }]";
            String response = askQueenpro(connectors);
            response = decodificaJSONdaURL(response);
//            System.out.println("getQP_FEtabs--->response: " + response);
            // ritorna gFEtabs come Json che contiene un array

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject;
            JSONArray Jtabs = new JSONArray();

            try {
                jsonObject = (JSONObject) jsonParser.parse(response);
                dbID = jsonObject.get("dbID").toString();
                prjID = jsonObject.get("prjID").toString();
                try {

                    Jtabs = (JSONArray) jsonObject.get("gFEtabs");
//                    System.out.println("Leggo  " + Jtabs.size() + " definizioni tabella.");
                    for (Object Jtab : Jtabs) {
//                        JSONObject JO = (JSONObject) jsonParser.parse(Jtab.toString());
                        JSONObject JO = (JSONObject) Jtab;
                        QPtable myTab = new QPtable();
                        myTab.ID = JO.get("ID").toString();
                        myTab.name = JO.get("name").toString();
//                        System.out.println("Tabella >:  " + myTab.name);
                        ArrayList<QPfield> fields = new ArrayList();
                        JSONArray Jfields = (JSONArray) JO.get("fields");
//                        System.out.println("\tcampi >:  " + Jfields.size());
                        for (Object Jfield : Jfields) {
                            try {
                                JSONObject JF = (JSONObject) Jfield;
//                            System.out.println("\t\t >:  " + JF.toString());
                                QPfield myField = new QPfield();

                                myField.setName(JF.get("name").toString());
//                            System.out.println("\t\t >:  " + myField.name);
                                myField.type = JF.get("type").toString();
//                            System.out.println("\t\t >:  " + myField.type);
                                try {
                                    myField.length = Integer.parseInt(JF.get("length").toString());
//                                System.out.println("\t\t >:  " + myField.length);
                                } catch (Exception e) {
                                }

                                if (type != null && type.equalsIgnoreCase("allTabs")) {
                                    try {
                                        myField.ID = JF.get("ID").toString();
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myField.defaultValue = JF.get("defaultValue").toString();
                                    } catch (Exception e) {
                                    }

                                    try {
                                        myField.autoIncrement = Integer.parseInt(JF.get("autoIncrement").toString());
                                        myField.BoolAutoIncrement = false;
                                        if (myField.autoIncrement > 0) {
                                            myField.BoolAutoIncrement = true;
                                        }
                                    } catch (Exception e) {
                                        myField.BoolAutoIncrement = false;
                                    }
                                    try {
                                        myField.primary = Integer.parseInt(JF.get("primary").toString());
                                        myField.BoolPrimary = false;
                                        if (myField.primary > 0) {
                                            myField.BoolPrimary = true;
                                        }
                                    } catch (Exception e) {
                                        myField.BoolPrimary = false;
                                    }
                                    try {
                                        myField.notNull = Integer.parseInt(JF.get("notNull").toString());
                                        myField.BoolNotNull = false;
                                        if (myField.notNull > 0) {
                                            myField.BoolNotNull = true;
                                        }
                                    } catch (Exception e) {
                                        myField.BoolNotNull = false;
                                    }
                                    try {
                                        myField.position = Integer.parseInt(JF.get("position").toString());
                                    } catch (Exception e) {
                                    }

//                                System.out.println("\t\t >:  " + myField.name + " -type:" + myField.type + " -PRIM:" + myField.primary + " -NNULL:" + myField.notNull + " -AI:" + myField.autoIncrement);
                                }

                                fields.add(myField);
//                            System.out.println("Tabella  " + myTab.name + " _ Campo:" + myField.name);
                            } catch (Exception e) {
                                System.out.println("\n***** ERROR IN LETTURA TABELLA:" + myTab.name + " \nFIELDS:" + Jfield.toString());
                            }
                        }
                        myTab.fields = fields;
                        tabs.add(myTab);
                    }

                } catch (Exception e) {
                }

            } catch (ParseException ex) {
                Logger.getLogger(ClassQPmanageUpdate.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        public int normalizeFEtbContent() {
            String tokenUsed = myParams.getCKtokenID();
            String installationName = mySettings.getInstallationName(myParams);
            System.out.println("\n-------\n installationName:" + installationName);
            System.out.println("myParams.getCKtokenID():" + tokenUsed);
            WShandler myWShandler = new WShandler(mySettings, installationName);
//            myWShandler.printClientsConnected();
//            myWShandler.printPeers();
            myWShandler.sendToBrowser("status", null, myParams.getCKtokenID(), "Acquisizione aggiornamenti.");

            System.out.println("SONO IN normalizeFEtbContent: " + myParams.getCKprojectName() + "_" + myParams.getCKcontextID());
            getQPdatabaseSchema("gFE_only");

            System.out.println("\n-------\nnormalizeFEtbContent>Trovate " + tabs.size() + " tabelle tecniche Front-end da ripopolare.");

            for (QPtable tab : tabs) {
                System.out.println("normalizeFEtbContent>Tabella  " + tab.getName() + " _ Campi:" + tab.fields.size());

            }
            Connection localconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();

            try {
                // la struttura delle tabelle è già giusta perchè garantita dalla funzione
                // makeModel(String CKprojectName, String CKcontextID) che viene eseguita per prima
                // quindi mi posso concentrare sul ripopolamento.

                // 1. prendo il nome della prima tabella e in locale la svuoto
                Statement locals = localconny.createStatement();
                //=============  CICLO PER CONENUTI RELATIVI AL PROGETTO SPECIFICO    
                String SQLphrase;
//                int flagBLOB = 0;
//PER OGNI TABELLA gFE                
                for (int tbl = 0; tbl < tabs.size(); tbl++) {
//                    System.out.println("\n----------------------------------------\nTABELLA:" + tabs.get(tbl).name);
                    for (int jj = 0; jj < tabs.get(tbl).fields.size(); jj++) {
//                        System.out.println("    Field " + jj + ":" + tabs.get(tbl).fields.get(jj).name + " (" + tabs.get(tbl).fields.get(jj).type + ")" + tabs.get(tbl).fields.get(jj).length);
//                        if (tabs.get(tbl).fields.get(jj).type.equalsIgnoreCase("BLOB")) {
//                            flagBLOB++;
//                        }

                    }
                    String localFEtableName = tabs.get(tbl).name + "_" + mySettings.getProjectName();
//                    myWShandler.broadcast(myWShandler.getGlobalSession(), "status", "PIPPO", "", "Acquisizione aggiornamenti. Tabella " + localFEtableName);
//                    System.out.println("\n=======================================\nsvuoto la tabella esistente (se esiste) :" + tabs.get(tbl).name);
                    SQLphrase = "TRUNCATE TABLE `" + localFEtableName + "`  ;";
//                    System.out.println("SQLphrase :" + SQLphrase);
                    int result = locals.executeUpdate(SQLphrase);

                    // chiedo a QP il contenuto della tabella sotto forma di preparedStatement
                    String connectors = "[{\"door\":\"manageUpdate\","
                            + "\"event\":\"getQP_getTabRowsNumber\","
                            + "\"formID\":\"" + dbID + "\","
                            + "\"table\":\"" + tabs.get(tbl).name + "\""
                            + " }]";

//                    System.out.println("Chiedo a queenpro... " + tabs.get(tbl).name);
                    String response = askQueenpro(connectors);

                    response = decodificaJSONdaURL(response);
//                    System.out.println("response: " + response);

                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject;
                    JSONArray Jtabs = new JSONArray();
                    int totLines = 0;
                    try {
                        jsonObject = (JSONObject) jsonParser.parse(response);
                        totLines = Integer.parseInt(jsonObject.get("lines").toString());
                    } catch (ParseException ex) {
                        Logger.getLogger(ClassQPmanageUpdate.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                    System.out.println("\n=======================================\nil numero di rows nella tabella è :" + totLines);

//                    System.out.println("\n\nRIPORTO IN LOCALE tabella:" + localFEtableName);
                    for (int lns = 0; lns < totLines; lns++) {
                        int numRiga = lns + 1;
                        connectors = "[{\"door\":\"manageUpdate\","
                                + "\"event\":\"getQP_getTabContentLine\","
                                + "\"formID\":\"" + dbID + "\","
                                + "\"table\":\"" + tabs.get(tbl).name + "\","
                                + "\"keyValue\":\"" + numRiga + "\""
                                + " }]";

//                        System.out.println("Chiedo a queenpro... " + tabs.get(tbl).name + "   row :" + numRiga);
                        response = askQueenpro(connectors);
                        response = decodificaJSONdaURL(response);
//                        System.out.println("Line " + numRiga + ") response: " + response);

                        jsonParser = new JSONParser();
                        JSONArray Jlines = new JSONArray();

                        int lineNumber;
                        try {
                            jsonObject = (JSONObject) jsonParser.parse(response);
//                            lineNumber = Integer.parseInt(jsonObject.get("lineNumber").toString());
                            try {

                                Jlines = (JSONArray) jsonObject.get("lines");
//                                System.out.println("RIGHE IN COPIA " + Jlines.size() + ") valori: " + Jlines.toString());

                                for (Object JL : Jlines) {// in realtà è una sola linea
                                    JSONObject JOx = (JSONObject) jsonParser.parse(JL.toString());
//                                    System.out.println("tabs.get(tbl).field.size(): " + tabs.get(tbl).field.size() + "completeJOx: " + JOx.toString());

                                    for (int dd = 0; dd < tabs.get(tbl).fields.size(); dd++) {
                                        try {
                                            String fieldName = tabs.get(tbl).fields.get(dd).name;

                                            String valore = JOx.get(fieldName).toString();
                                            String valueDecoded = valore;
                                            if (!tabs.get(tbl).fields.get(dd).getType().equalsIgnoreCase("BLOB")) {
                                                if (valore != null && !valore.equalsIgnoreCase("null")) {
                                                    try {
                                                        valueDecoded = java.net.URLDecoder.decode(valore, "UTF-8");
                                                    } catch (Exception e) {
                                                        valueDecoded = valore;
                                                    }
                                                } else {
                                                    valueDecoded = "";
                                                }
                                            }
//                                            System.out.println("clientside COMPILO CAMPO : " + fieldName + " -------> " + valueDecoded);
                                            tabs.get(tbl).fields.get(dd).value = valueDecoded;
//                                        System.out.println("*decoded JOx: " + fieldName + " : " + valueDecoded);
                                        } catch (Exception e) {
                                            System.out.println("err_:" + e.toString());
                                        }
                                    }
                                }

                            } catch (Exception e) {
                            }

                        } catch (ParseException ex) {
                            Logger.getLogger(ClassQPmanageUpdate.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        // PREPARO LA SCRITTURA DELLA RIGA SUL DATASBASE LOCALE
                        SQLphrase = "INSERT INTO `" + localFEtableName + "`(";
                        int firstinserted = 0;
                        for (int jj = 0; jj < tabs.get(tbl).fields.size(); jj++) {
//                            if (tabs.get(tbl).field.get(jj).type.equalsIgnoreCase("BLOB")) {
//
//                            } else {
                            if (jj > 0 && firstinserted > 0) {
                                SQLphrase += ", ";
                            }
                            firstinserted++;
                            SQLphrase += "`" + tabs.get(tbl).fields.get(jj).name + "`";
//                            }
                        }
                        SQLphrase += ")VALUES(";
                        firstinserted = 0;
                        for (int jj = 0; jj < tabs.get(tbl).fields.size(); jj++) {
//                            if (tabs.get(tbl).field.get(jj).type.equalsIgnoreCase("BLOB")) {
//
//                            } else {
                            if (jj > 0 && firstinserted > 0) {
                                SQLphrase += ", ";
                            }
                            firstinserted++;
                            SQLphrase += "?";
//                            }
                        }

                        SQLphrase += ");";

//                        System.out.println("SQLphrase:  " + SQLphrase);
                        PreparedStatement statement = localconny.prepareStatement(SQLphrase);
                        // compilo i  punti interrogativi dello statement...
                        firstinserted = 0;
                        for (int jj = 0; jj < tabs.get(tbl).fields.size(); jj++) {

                            firstinserted++;
                            String newValue = "";
                            String newType = "varchar";
                            newValue = tabs.get(tbl).fields.get(jj).value;
                            newType = tabs.get(tbl).fields.get(jj).type;
                            if (newType.equalsIgnoreCase("BLOB")) {
//                                System.out.println("\n\nIL CAMPO " + tabs.get(tbl).fields.get(jj).name + " è un BLOB: " + newValue);
                                if (newValue == null || newValue.equalsIgnoreCase("null")) {
                                    statement.setBinaryStream(firstinserted, null);
                                } else {
                                    byte[] imageBytes;
                                    imageBytes = Base64.getUrlDecoder().decode(newValue.getBytes());
                                    statement.setBytes(firstinserted, imageBytes);

                                }

                            } else if (newType.equalsIgnoreCase("INT")) {
                                int numberValue = 0;
                                try {
                                    numberValue = Integer.parseInt(newValue);
                                } catch (Exception e) {
                                    numberValue = 0;
                                }
                                statement.setInt(firstinserted, numberValue);
                            } else {
                                statement.setString(firstinserted, newValue);
                            }

                        }
//                        System.out.println("LI RICOPIO IN LOCALE:" + statement.toString());
//                        System.out.println("RIPORTO IN LOCALE tabella:" + localFEtableName + " RIGA " + numRiga + "/" + totLines);

                        myWShandler.sendToBrowser("status", null, tokenUsed, "Acquisizione aggiornamenti. Tabella " + localFEtableName + " RIGA " + numRiga + "/" + totLines);

//                        myWShandler.broadcast(myWShandler.getGlobalSession(), "status", "PIPPO", "", "Acquisizione aggiornamenti. Tabella " + localFEtableName + " RIGA " + numRiga + "/" + totLines);
                        try {
                            result = statement.executeUpdate();
                        } catch (Exception e) {
                            System.out.println("Errore Stavo copiando la voce :" + e.toString());
                        }
                        statement.close();
                    }

//******************************************************************** 
////                    myWShandler.printPeers();
////                    myWShandler.printClientsConnected();
////                    System.out.println("Il token in routine è  myParams.getCKtokenID(): " + myParams.getCKtokenID());
////                    //broadcast(Session session, String type, String sender, String code, String message)
//********************************************************************************                
                    {

                    }
                }// chiude ciclo tabella 

                myWShandler.sendToBrowser("status", null, tokenUsed, "Acquisizione aggiornamenti frontend terminata.");

                localconny.close();
            } catch (SQLException ex) {
                Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("OPERAZIONE UPDATE TEMPLATE CONCLUSA.  ");
            try {
                localconny.close();
            } catch (SQLException ex) {
                Logger.getLogger(ClassQPmanageUpdate.class.getName()).log(Level.SEVERE, null, ex);
            }
            return 1;
        }

        public int fillEVOdirectives(EVOpagerParams myParams, Settings mySettings) throws UnsupportedEncodingException {
            Connection localconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
            Statement locals;
            String SQLphrase = null;
            try {
                locals = localconny.createStatement();
                SQLphrase = "CREATE TABLE IF NOT EXISTS " + mySettings.getLocalEVO_directives() + " ("
                        + "  `ID` int(11) NOT NULL AUTO_INCREMENT,\n"
                        + "  `infoName` varchar(128) DEFAULT NULL,\n"
                        + "  `infoValue` text,\n"
                        + "  `instanceInfoValue` text,\n"
                        + "  `note` text,\n"
                        + "  `rifProjects` varchar(64) DEFAULT NULL,\n"
                        + "  `instance` varchar(256) DEFAULT 'ALL',\n"
                        + "  `superGroup` varchar(256) DEFAULT 'FrontEnd',\n"
                        + "  `group` varchar(256) DEFAULT 'Aspect',\n"
                        + "  `modifiable` varchar(256) DEFAULT 'DEFAULT:FALSE',\n"
                        + "  PRIMARY KEY (`ID`)\n"
                        + ") ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;";
                feed = "->  " + SQLphrase;
                int i = locals.executeUpdate(SQLphrase);

                SQLphrase = "DELETE FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='FE_version';";
                i = locals.executeUpdate(SQLphrase);
                SQLphrase = "DELETE FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='FE_release';";
                i = locals.executeUpdate(SQLphrase);
                SQLphrase = "INSERT INTO " + mySettings.getLocalEVO_directives() + "  (`infoName`,`infoValue`) VALUES ('FE_version','" + EVOprjVersion + "');";
                feed = "SQLphrase:" + SQLphrase;
                i = locals.executeUpdate(SQLphrase);
                SQLphrase = "INSERT INTO " + mySettings.getLocalEVO_directives() + "  (`infoName`,`infoValue`) VALUES ('FE_release','" + EVOprjRelease + "');";
                feed = "SQLphrase:" + SQLphrase;
                i = locals.executeUpdate(SQLphrase);

                String connectors = "[{\"door\":\"manageUpdate\","
                        + "     \"event\":\"getEvoDirectives\""
                        + " }]";
                String response = askQueenpro(connectors);
                // ottengo un jsonArray

                JSONObject jsonObject = null;
                boolean flagDecodificato = false;
                JSONParser jsonParser = new JSONParser();
                response = response.replace(":undefined", ":\"\"");
                try {

                    jsonObject = (JSONObject) jsonParser.parse(response);
                    flagDecodificato = true;
                } catch (ParseException ex) {
                    try {
                        response = java.net.URLDecoder.decode(response, "UTF-8");
                        jsonObject = (JSONObject) jsonParser.parse(response);
                        flagDecodificato = true;
                    } catch (ParseException ex1) {
                        try {
                            response = java.net.URLDecoder.decode(response, "UTF-8");
                            jsonObject = (JSONObject) jsonParser.parse(response);
                            flagDecodificato = true;
                        } catch (ParseException ex2) {
//                            System.out.println("TERZO TENTATIVO FALLITO " + ex2.toString());
                            System.out.println("Non parsable row: " + response);

                        }
                    }

                }

                if (flagDecodificato == true) {
                    String directives = jsonObject.get("EVOdirectives").toString();
                    if (directives != null && directives.length() > 0) {
                        JSONParser parser = new JSONParser();
                        Object obj;
                        try {
                            obj = parser.parse(directives);
                            JSONArray array = (JSONArray) obj;
                            for (Object riga : array) {
                                jsonObject = (JSONObject) jsonParser.parse(riga.toString());
                                SQLphrase = "DELETE FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='" + jsonObject.get("infoName").toString() + "';";
                                i = locals.executeUpdate(SQLphrase);
                                SQLphrase = "INSERT INTO " + mySettings.getLocalEVO_directives() + "  (`infoName`,`infoValue`) "
                                        + "VALUES "
                                        + "('" + jsonObject.get("infoName").toString() + "','" + jsonObject.get("infoValue").toString() + "');";
                                i = locals.executeUpdate(SQLphrase);
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(ClassQPmanageUpdate.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                localconny.close();
            } catch (SQLException ex) {
                Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
                feed = "ERRORE:" + ex.toString();
            }

            return 0;
        }

        public void makeModel() {
            Connection conny = null;
            String CKprojectName = myParams.getCKprojectName();
            String CKcontextID = myParams.getCKcontextID();

            boolean newMadeDatabase = false;
            System.out.println("+-----------------------------------------------------------+");
            System.out.println("+---PROCEDURA MAKE MODEL------------------------------------+");
            System.out.println("+-----------------------------------------------------------+");

            int connectionOK = 0;
            try {
                conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
                connectionOK = 1;
                conny.close();
            } catch (Exception e) {
                conny = null;
                connectionOK = 0;
                newMadeDatabase = true;
            }

            if (connectionOK < 1) {
                feed = "IL DB LOCALE NON ESISTE ";
//                System.out.println(feed);
                // qui valuto Token di permesso di costruire :-p
                newMadeDatabase = true;
            } else {
                feed = "IL DB LOCALE ESISTE GIà ";
//                System.out.println(feed);
            }
            try {
                getQPdatabaseSchema("allTabs");
                System.out.println("\n-------\nTrovate " + tabs.size() + " tabelle complessivamente.");
                System.out.println("dbID: " + dbID + " prjID: " + prjID);
                if (this.dbID == null || this.prjID == null || tabs.size() < 1) {
                    // su QP non esiste un progetto di tal fatta
                    return;
                } else {
                    remakeDB();
                }

            } catch (Exception e) {

            }

            System.out.println("newMadeDatabase=> " + newMadeDatabase);
            if (newMadeDatabase == true) {
                Statement s = null;

//CONNESSIONE SERVER//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //in questo caso il DB non esisteva e devo creare l'utente ADMIN e il gruppo superUsers
                String SQLphrase = "INSERT INTO `archivio_operatori` (`ID`, `username`, `name`, `surname`, `alive`, `pincode`, `picture`, `gaiaID`, `grado`, `owner`, `recorded`, `rango`) VALUES ('admin', 'admin', 'admin', 'admin', '1', 'admin', NULL, NULL, '999', NULL, '2017-09-29 08:56:35', '999')";
                try {
                    s = conny.createStatement();
                    int Result = s.executeUpdate(SQLphrase);

                    SQLphrase = "INSERT INTO " + mySettings.getAccount_TABLEgruppi() + " (`IDgruppo`, `descrizione`, `rango`) VALUES ('superAdmin', 'superAdmin', '1000')";
                    Result = s.executeUpdate(SQLphrase);
                    SQLphrase = "INSERT INTO " + mySettings.getAccount_TABLElinkUserGroups() + "  (`id`, `rifOperatore`, `rifGruppo`) VALUES (NULL, 'admin', 'superAdmin')";
                    Result = s.executeUpdate(SQLphrase);
                    SQLphrase = "INSERT INTO " + mySettings.getAccount_TABLElinkUserGroups() + " (`id`, `rifOperatore`, `rifGruppo`) VALUES (NULL, 'admin', 'admin')";
                    Result = s.executeUpdate(SQLphrase);
                    System.out.println("Creato utente Admin ");
                } catch (SQLException ex) {

                    System.out.println("Errore in creazione utente Admin ");
                }

                // adesso, affinchè funzioni, devo creare su EVO una licenza base (1 mese) per il context creato
                String connectors = "[{\"door\":\"manageUpdate\","
                        + "\"event\":\"getQP_setTrialEVOdirective\","
                        + "\"formID\":\"" + dbID + "\""
                        + " }]";
                String response = askQueenpro(connectors);

                System.out.println("Richiesta licenza trial su Queenpro. " + response);
                response = decodificaJSONdaURL(response);

                System.out.println("Creata licenza trial su Queenpro. ");
            }

            try {
                conny.close();
            } catch (SQLException ex) {
                Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
                feed = "ERRORE  ...  " + ex.toString();
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            }
        }

        public int remakeDB() {
            String history = "";
            String historyMessage = "";
            //int response = 0;
            //----------------------------------
            historyMessage = "SONO IN remakeDB";
            System.out.println(historyMessage);
            history += historyMessage + "\n";
            String tokenUsed = myParams.getCKtokenID();
            String installationName = mySettings.getInstallationName(myParams);
            System.out.println("\n-------\n installationName:" + installationName);
            System.out.println("myParams.getCKtokenID():" + tokenUsed);
            WShandler myWShandler = new WShandler(mySettings, installationName);
            myWShandler.printClientsConnected();
            myWShandler.printPeers();
            myWShandler.sendToBrowser("status", null, myParams.getCKtokenID(), "Acquisizione aggiornamenti struttura database.");

//            feed = "SONO IN EVO remakeDB. ";
//            System.out.println(feed);
//        int tabs = myDatabase.table.size();
            System.out.println("\n\n+-----------------------------------------------------------+");
            System.out.println("+---PROCEDURA REMAKE DATABASE-------------------------------+");
            System.out.println("+TABS:" + tabs.size());
            System.out.println("+-----------------------------------------------------------+");

            String SQLphrase = "";
            String routine = "";
            try {

                EVOpagerDBconnection Masterconn = new EVOpagerDBconnection(myParams, mySettings);
                Connection globalConny = Masterconn.ConnLocalNoDB();
//                feed = "Connessione al server LOCALE";
//                System.out.println(feed);
                Statement gs = globalConny.createStatement();
//                routine = "1.CREATE DATABASE IF NOT EXISTS. ";
                //String dbName=myDatabase.getGroup()+"_"+myDatabase.getName()+"_"+myDatabase.getID();
//                System.out.println("+--------------------------------------------------------------+");
                String DestinationDBname = Masterconn.getDbExtendedName(myParams.getCKprojectGroup(), mySettings.getFrontendDB(), myParams.getCKcontextID());
//                System.out.println("CREO IL DATABASE:" + DestinationDBname);
                int Result = gs.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DestinationDBname + "`");
//                System.out.println("Result:" + Result);

                //System.out.println("+--------------------------------------------------------------+");
                //System.out.println("Connessione allo schema mySQL");
                /* Connection schemaconny = myDBC.makeConnection(myServer, "information_schema");
             Statement schemast = schemaconny.createStatement();
            
                 */
                //MI CONNETTO ALLO SCHEMA LOCALE
                Connection schemaconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalSchema();
                Statement schemast = schemaconny.createStatement();
//CONNESSIONE SERVER//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
                Statement s = conny.createStatement();
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//=================================================
//==CICLO PER OGNI TABLE===========================
//=================================================                       
                for (int currentTable = 0; currentTable < tabs.size(); currentTable++) {

                    int exists = 0;
                    QPtable myTable = tabs.get(currentTable);
                    String tableName = myTable.getName();

                    if (tableName != null && tableName != "" && tableName.length() > 3
                            && (tableName.substring(0, 4).equalsIgnoreCase("gFE_")
                            || tableName.substring(0, 5).equalsIgnoreCase("gEVO_"))) {

                        tableName = tableName + "_" + mySettings.getProjectName();
                    }

                    int numFields = myTable.getFields().size();
                    myWShandler.sendToBrowser("status", null, tokenUsed, "Acquisizione aggiornamenti struttura database. Tabella " + (1 + currentTable) + "/" + tabs.size());

                    feed = "\n\n-----\nClassQPmanageUpdate>> " + (1 + currentTable) + "/" + tabs.size()
                            + " . AGGIORNAMENTO TABELLA  >>" + tableName;
                    System.out.println(feed);
                    try {
                        SQLphrase = "SELECT * FROM COLUMNS "
                                + " WHERE TABLE_NAME = '" + tableName + "' "
                                + " AND TABLE_SCHEMA = '" + DestinationDBname + "';";
                        feed = "RICERCA SCHEMA TABELLA: " + SQLphrase;

//ResultSet rs = schemast.executeQuery(SQLphrase); 
                        PreparedStatement ps = schemaconny.prepareStatement(SQLphrase);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            exists++;
                            break;
                        }

                    } catch (SQLException ex) {
                        feed = "RICERCA SCHEMA TABELLA: " + SQLphrase;
                        System.out.println(feed);
                        feed = "ERRORE IN RICERCA SCHEMA TABELLA: " + ex.toString();
                        System.out.println(feed);
                    }
//                    System.out.println("CONTROLLO ESISTENZA EFFETTUATO: " + exists + " TAB:" + tableName);
//==========SE LA TABELLA NON ESISTE LA CREO ED E' DI SICURO AGGIORNATA===============================                
                    if (exists < 1) {
                        feed = "LA TABELLA NON ESISE IN LOCALE. ";
                        System.out.println(feed);

                        SQLphrase = "CREATE TABLE IF NOT EXISTS `";
                        SQLphrase += tableName + "` ( ";
                        System.out.println("CREAZIONE TABELLA " + currentTable + "):" + tableName);

                        for (int jj = 0; jj < numFields; jj++) {
                            QPfield myField = tabs.get(currentTable).getFields().get(jj);
                            Boolean primary = tabs.get(currentTable).getFields().get(jj).getBoolPrimary();
                            if (jj > 0) {
                                SQLphrase += ", ";
                            }
                            SQLphrase += " `" + tabs.get(currentTable).getFields().get(jj).getName() + "` ";
                            SQLphrase += parseFieldQuery(myField, jj);

                            if (primary != null && primary == true) {
                                SQLphrase += " PRIMARY KEY \n";
                            }

//                            System.out.println("Field:" + tabs.get(currentTable).getFields().get(jj).getName() + " primary:" + tabs.get(currentTable).getFields().get(jj).getBoolPrimary());
                        }
                        SQLphrase += " ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

                        historyMessage = "SQLphrase :" + SQLphrase;
                        System.out.println(historyMessage);
                        history += historyMessage + "\n";
                        try {
                            Result = s.executeUpdate(SQLphrase); // creazione tabella
                            historyMessage = "OK";
                            System.out.println(historyMessage);
                            history += historyMessage + "\n";
                        } catch (SQLException ex) {
                            historyMessage = "ERROR:" + ex.toString();
                            System.out.println(historyMessage);
                            history += historyMessage + "\n";
                            Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ">>>" + ex);
                            Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ">>>" + SQLphrase);
                            feed = "943.ERRORE IN Connessione a " + DestinationDBname;
                            System.out.println(feed);
                            feed = "943.ERRORE  " + s.getWarnings();
                            System.out.println(feed);
                            feed = "943.SQLphrase: " + SQLphrase;
                            System.out.println(feed);
                            myWShandler.sendToBrowser("status", null, tokenUsed, "Acquisizione aggiornamenti struttura database. <B>ERRORE</B>.");

                        }
                        if (Result >= 0) {
//                            feed = ">> 2. CREATA TABELLA: " + SQLphrase;
//                            System.out.println(feed);
                        } else {
//                            feed = ">> 2. ERRORE ROUTINE DI CREAZIONE TABELLA: " + SQLphrase;
//                            System.out.println(feed);
                        }

                    } else {
//==========SE LA TABELLA ESISTE GIA' VADO A CONTROLLARE L'ESISTENZA DI TUTTI I FIELDSA NECESSARI===============================                    
//                        feed = "LA TABELLA ESISTE GIà IN LOCALE. ";
//                        System.out.println(feed);
                        flagTimestampUsed = 0;
                        routine = "3. SELECT * FROM COLUMNS per verificarne esistenza in SCHEMA.";
                        for (int jj = 0; jj < numFields; jj++) {

//                            QPfield myField = tabs.get(currentTable).getFields().get(jj);
                            String name = tabs.get(currentTable).getFields().get(jj).getName();

//                            if (tabs.get(currentTable).getFields().get(jj).autoIncrement > 0) {
//                                tabs.get(currentTable).getFields().get(jj).setBoolAutoIncrement(Boolean.TRUE);
//                            } else {
//                                tabs.get(currentTable).getFields().get(jj).setBoolAutoIncrement(Boolean.FALSE);
//                            }
//                            if (tabs.get(currentTable).getFields().get(jj).primary > 0) {
//                                tabs.get(currentTable).getFields().get(jj).setBoolPrimary(Boolean.TRUE);
//                            } else {
//                                tabs.get(currentTable).getFields().get(jj).setBoolPrimary(Boolean.FALSE);
//                            }
//                            if (tabs.get(currentTable).getFields().get(jj).notNull > 0) {
//                                tabs.get(currentTable).getFields().get(jj).setBoolNotNull(Boolean.TRUE);
//                            } else {
//                                tabs.get(currentTable).getFields().get(jj).setBoolNotNull(Boolean.FALSE);
//                            }
                            SQLphrase = "SELECT * FROM COLUMNS\n"
                                    + "           WHERE TABLE_NAME = '" + tableName + "'\n"
                                    + "             AND TABLE_SCHEMA = '" + DestinationDBname + "'\n"
                                    + "             AND COLUMN_NAME = '" + name + "' ;";
                            //System.out.println("SQLphrase="+SQLphrase);
//                            System.out.println(">>> CERCO COLONNA " + name);
                            int i = 0;
                            try {
                                ResultSet rs = schemast.executeQuery(SQLphrase);
                                while (rs.next()) {
                                    i++;
                                }
                            } catch (SQLException ex) {

                            }

                            routine = "4. ALTER TABLE PER ADDING FIELDS.";
//==========SE UN FIELD NON ESISTE LO CREO ADESSO===============================                            
                            if (i < 1) {

                                try {
//                                    System.out.println(">>> COLONNA " + name + " NON ESISTE.");
//                                    Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ">>> COLONNA " + name + " NON ESISTE.");
                                    SQLphrase = "ALTER TABLE `" + tableName + "` ADD ";
                                    SQLphrase += " " + name + " ";
                                    SQLphrase += parseFieldQuery(tabs.get(currentTable).getFields().get(jj), jj);
                                    if (tabs.get(currentTable).getFields().get(jj).primary > 0) {
                                        SQLphrase += " PRIMARY KEY ";
                                    }
                                    System.out.println("ROUTINE DI ADD FIELD: " + SQLphrase);
                                    historyMessage = "NECESSARIO ADDING FIELD:" + SQLphrase;
                                    System.out.println(historyMessage);
                                    history += historyMessage + "\n";
//                                    Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ">>>" + SQLphrase);
                                    Result = s.executeUpdate(SQLphrase); // creazione field 

                                } catch (SQLException ex) {

                                    historyMessage = "ERR:" + ex.toString();
                                    System.out.println(historyMessage);
                                    history += historyMessage + "\n";
//                                    feed = "ROUTINE AGGIUNTA FIELD: " + SQLphrase;
//                                    System.out.println(feed);
//                                    feed = "ERRORE IN AGGIUNTA FIELD: " + ex.toString();
//                                    System.out.println(feed);
//
//                                    myWShandler.sendToBrowser("status", null, tokenUsed, "Acquisizione aggiornamenti struttura database. <B>ERRORE IN AGGIUNTA FIELD</B>.");
//                                    myWShandler.sendToBrowser("errlog", null, tokenUsed, SQLphrase);
                                    String adTblNm = tableName.toLowerCase();
                                    try {
//                                    System.out.println(">>> COLONNA " + name + " NON ESISTE.");
//                                        Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ">>> COLONNA " + name + " NON ESISTE.");
                                        SQLphrase = "ALTER TABLE `" + adTblNm + "` ADD ";
                                        SQLphrase += " " + name + " ";
                                        SQLphrase += parseFieldQuery(tabs.get(currentTable).getFields().get(jj), jj);
                                        if (tabs.get(currentTable).getFields().get(jj).primary > 0) {
                                            SQLphrase += " PRIMARY KEY ";
                                        }
                                        System.out.println("ROUTINE DI ADD FIELD: " + SQLphrase);
                                        historyMessage = "RIPROVO ADDING FIELD:" + SQLphrase;
                                        System.out.println(historyMessage);
                                        history += historyMessage + "\n";
//                                        Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ">>>" + SQLphrase);
                                        Result = s.executeUpdate(SQLphrase); // creazione field 

                                    } catch (SQLException e) {
                                        historyMessage = "ERR2:" + e.toString();
                                        System.out.println(historyMessage);
                                        history += historyMessage + "\n";
                                        feed = "ROUTINE AGGIUNTA FIELD: " + SQLphrase;
                                        System.out.println(feed);
                                        feed = "ERRORE IN AGGIUNTA FIELD: " + e.toString();
                                        System.out.println(feed);

                                        myWShandler.sendToBrowser("status", null, tokenUsed, "Acquisizione aggiornamenti struttura database. <B>ERRORE IN AGGIUNTA FIELD</B>.");
                                        myWShandler.sendToBrowser("errlog", null, tokenUsed, SQLphrase);

                                    }

                                }

                            }

                        }

                    }

                    flagTimestampUsed = 0;
                    routine = "5. ALTER TABLE PER CHANGING FIELDS.";

//==CHANGE=========================================
//==CICLO PER OGNI FIELD===========================
//=================================================             
                    feed = "\n\n\n</BR></BR>-INIZIO FASE DI CHANGING---------------------</BR></BR>\n\n\n";

                    for (int jj = 0; jj < numFields; jj++) {
                        try {
                            QPfield myField = tabs.get(currentTable).getFields().get(jj);
                            String name = myField.getName();
//
//                            feed = "ROUTINE DI CHANGE TABELLA: " + tableName + " FIELD: " + name + "   (" + jj + "/" + numFields + ")";
//                            System.out.println(feed);

                            SQLphrase = "ALTER TABLE `" + tableName + "` ";
                            SQLphrase += "CHANGE `" + name + "` `" + name + "` ";
                            SQLphrase += parseFieldQuery(myField, jj);
                            System.out.println("SQL:" + SQLphrase);
                            Result = s.executeUpdate(SQLphrase); // creazione field
                        } catch (SQLException ex) {
                            feed = "ROUTINE DI CHANGE TABELLA: " + SQLphrase;
                            System.out.println(feed);
                            feed = "ERRORE IN CHANGE TABELLA: " + ex.toString();
                            System.out.println(feed);
                            Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
                            myWShandler.sendToBrowser("status", null, tokenUsed, "Acquisizione aggiornamenti struttura database. <B>ERRORE IN CHANGE TABELLA</B>.");

                        } finally {

                        }
                        SQLphrase = "";
                    }

                }// fine ciclo per ogni tabella
                globalConny.close();
                schemaconny.close();
                conny.close();
            } catch (Exception ex) {
                feed = "ROUTINE DI CHANGE TABELLA: " + SQLphrase;
                feed = "ERRORE IN REMAKE DB: " + ex.toString();
                Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
                myWShandler.sendToBrowser("status", null, tokenUsed, "Acquisizione aggiornamenti struttura database. <B>ERRORE IN REMAKE DB</B>.");

            }
            setEvoDirective(myParams, "lastHistory", history);
            return 0;
        }

        public String parseFieldQuery(QPfield myField, int jj) {
            String SQLphrase = "";
            String name = myField.getName();
            String type = myField.getType();
            int length = myField.getLength();
            String defaultValue = myField.getDefaultValue();

            Boolean autoIncrement = myField.BoolAutoIncrement;
            Boolean primary = myField.BoolPrimary;
            Boolean notnull = myField.BoolNotNull;

            Boolean virgolette = Boolean.TRUE;
            Boolean acceptDefault = Boolean.TRUE;
            if (flagTimestampUsed < 0) {
                flagTimestampUsed = 0;
            }
            int flag = flagTimestampUsed;

            if (type == null) {
//                System.out.println("CAMPO " + name + " ha type vuoto. Diventa VARCHAR");
                type = "VARCHAR";
            }

            if (type.equalsIgnoreCase("INT")) {
                SQLphrase += " INT(" + length + ") ";
                virgolette = Boolean.FALSE;
            } else if (type.equalsIgnoreCase("FLOAT")) {
                SQLphrase += " FLOAT ";
                virgolette = Boolean.FALSE;
            } else if (type.equalsIgnoreCase("DOUBLE")) {
                SQLphrase += " DOUBLE ";
                virgolette = Boolean.FALSE;
            } else if (type.equalsIgnoreCase("DATE")) {
                SQLphrase += " DATE ";
            } else if (type.equalsIgnoreCase("TIME")) {
                SQLphrase += " TIME ";
            } else if (type.equalsIgnoreCase("TIMESTAMP")) {
//                System.out.println("flagTimestampUsed: " + flag);
                if (flag < 1 && defaultValue != null && defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                    SQLphrase += "  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ";
                    flag++;
                } else {
                    SQLphrase += "  TIMESTAMP ";
                }
                acceptDefault = Boolean.FALSE;

            } else if (type.equalsIgnoreCase("DATETIME")) {
                SQLphrase += " DATETIME ";
                acceptDefault = Boolean.FALSE;
            } else if (type.equalsIgnoreCase("TEXT")) {
                SQLphrase += " TEXT ";
            } else if (type.equalsIgnoreCase("MEDIUMTEXT")) {
                SQLphrase += " MEDIUMTEXT ";
                acceptDefault = Boolean.FALSE;
            } else if (type.equalsIgnoreCase("BLOB")) {
                SQLphrase += " BLOB ";
                acceptDefault = Boolean.FALSE;
            } else if (type.equalsIgnoreCase("MEDIUMBLOB")) {
                SQLphrase += " MEDIUMBLOB ";
            } else if (type.equalsIgnoreCase("LONGBLOB")) {
                acceptDefault = Boolean.FALSE;
                SQLphrase += " LONGBLOB ";
            } else if (type.equalsIgnoreCase("BIT")) {
                SQLphrase += " BIT(1) ";
                virgolette = Boolean.FALSE;
            } else if (type.equalsIgnoreCase("VARCHAR")) {
                SQLphrase += " VARCHAR(" + length + ") ";
            }

            if (defaultValue == null || defaultValue.isEmpty() || defaultValue == "" || defaultValue.equalsIgnoreCase("NULL")) {
                if (notnull == Boolean.FALSE && !type.equalsIgnoreCase("TIMESTAMP") && autoIncrement != true && primary != true) {
                    defaultValue = "NULL";
                    virgolette = Boolean.FALSE;
                    acceptDefault = Boolean.TRUE;
                } else {
                    acceptDefault = Boolean.FALSE;
                }
            }

            if (acceptDefault != null && acceptDefault == Boolean.TRUE) {
                SQLphrase += " DEFAULT ";
                if (virgolette == Boolean.FALSE) {
                    SQLphrase += defaultValue + "\n";
                } else {
                    SQLphrase += "'" + defaultValue + "'  \n";
                }
            }

            if (autoIncrement != null && autoIncrement == true) {
                SQLphrase += " AUTO_INCREMENT \n";
            }
            flagTimestampUsed = flag;
            return SQLphrase;
        }

    }

    public int setEvoDirective(EVOpagerParams myParams, String infoName, String infoValue) {
        int result = 0;

        Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        try {
            String SQLphrase = "DELETE FROM " + mySettings.getLocalEVO_directives() + " WHERE infoName = '" + infoName + "' ";
            PreparedStatement statement = FEconny.prepareStatement(SQLphrase);
            int i = statement.executeUpdate();

            SQLphrase = "INSERT INTO " + mySettings.getLocalEVO_directives() + "  ( infoName, infoValue) VALUES ( ?,? ) ";
            statement = FEconny.prepareStatement(SQLphrase);
            statement.setString(1, infoName);
            statement.setString(2, infoValue);
            i = statement.executeUpdate();

            FEconny.close();
            result = 1;
            el.log("setEvoDirective", SQLphrase);
        } catch (SQLException ex) {
            result = -1;
            Logger.getLogger(EVOsetup.class.getName()).log(Level.SEVERE, null, ex);
            el.log("setEvoDirective", ex.toString());
        }

        return result;
    }

    public class evoDirective {

        String infoName;
        String infoValue;
        String group;
        String superGroup;
        String note;
        String modifiable;
        String instanceInfoValue;

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getInstanceInfoValue() {
            return instanceInfoValue;
        }

        public void setInstanceInfoValue(String instanceInfoValue) {
            this.instanceInfoValue = instanceInfoValue;
        }

        public String getInfoName() {
            return infoName;
        }

        public void setInfoName(String infoName) {
            this.infoName = infoName;
        }

        public String getInfoValue() {
            return infoValue;
        }

        public void setInfoValue(String infoValue) {
            this.infoValue = infoValue;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getSuperGroup() {
            return superGroup;
        }

        public void setSuperGroup(String superGroup) {
            this.superGroup = superGroup;
        }

        public String getNoe() {
            return note;
        }

        public void setNoe(String note) {
            this.note = note;
        }

        public String getModifiable() {
            return modifiable;
        }

        public void setModifiable(String modifiable) {
            this.modifiable = modifiable;
        }

    }

    public int getFlagTimestampUsed() {
        return flagTimestampUsed;
    }

    public void setFlagTimestampUsed(int flagTimestampUsed) {
        this.flagTimestampUsed = flagTimestampUsed;
    }

    public ErrorLogger getEl() {
        return el;
    }

    public void setEl(ErrorLogger el) {
        this.el = el;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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

    public String getPROJECT_ID() {
        return PROJECT_ID;
    }

    public void setPROJECT_ID(String PROJECT_ID) {
        this.PROJECT_ID = PROJECT_ID;
    }

    public String getLOCAL_SERVER_URL() {
        return LOCAL_SERVER_URL;
    }

    public void setLOCAL_SERVER_URL(String LOCAL_SERVER_URL) {
        this.LOCAL_SERVER_URL = LOCAL_SERVER_URL;
    }

    public String getLOCAL_SERVER_ALTURL() {
        return LOCAL_SERVER_ALTURL;
    }

    public void setLOCAL_SERVER_ALTURL(String LOCAL_SERVER_ALTURL) {
        this.LOCAL_SERVER_ALTURL = LOCAL_SERVER_ALTURL;
    }

    public static String encodeURIComponent(String s) {
        String result;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }

}
