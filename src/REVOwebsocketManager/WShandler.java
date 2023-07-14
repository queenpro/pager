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
package REVOwebsocketManager;

import REVOdbManager.EVOpagerDirectivesManager;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.EVOpagerDBconnection;
import REVOpager.EVOuser;
import REVOsetup.EVOsetup;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.Thread.sleep;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.websocket.Session;
import models.gate;
import models.semaphore;
import smartCore.smartAction;
import org.json.simple.JSONArray;
import showIt.ShowItForm;
import static showIt.eventManager.encodeURIComponent;
import smartCore.smartForm;
import smartCore.smartQuery;

/**
 *
 * @author FFS INFORMATICA [info at ffs.it]
 */
public class WShandler {

    public Settings mySettings;
    public String handlerID;
    public static ArrayList<WSclient> clientsConnected = new ArrayList<>();
    public static ArrayList<WSbroadcastMessage> bcMessages = new ArrayList<>();
    public static ArrayList<WScontext> contesti = new ArrayList();
    public static String lastContextConnected;
    public static Session GlobalSession;
    public static int attempt;
    public static String LastBackupStartTime;
    public static int intervalSecs = 10;
    public static boolean backupping = false;
    public static boolean beat5s = false;
    public static boolean beat1m = false;
    public static boolean beat2m = false;
    public static boolean beat5m = false;
    public static boolean beat10m = false;
    public static boolean beat1h = false;
    public static boolean beat6h = false;

    public static int counter;
    public static Set<Session> peers = Collections.synchronizedSet(new HashSet<>());
    //public static Set<Session> allSessions = Collections.synchronizedSet(new HashSet<Session>());
//    final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
//    public static Set<Session> allSessions;
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    public int currentWinner;

    public WShandler(Settings mySettings) {
        this.handlerID = "ND";
        this.mySettings = mySettings;
//          timer.scheduleAtFixedRate(() -> heartBeat(GlobalSession), 0, 1, TimeUnit.SECONDS);
    }

    public WShandler(Settings mySettings, EVOpagerParams myParams) {
        this.handlerID = mySettings.getInstallationName(myParams);
        this.mySettings = mySettings;
    }

    public WShandler(Settings mySettings, String hndlrID) {
        this.handlerID = hndlrID;
        this.mySettings = mySettings;
//          timer.scheduleAtFixedRate(() -> heartBeat(GlobalSession), 0, 1, TimeUnit.SECONDS);
    }

    public void heartBeat(Session session,    int frequenzaInSecondi) {
      
        int intervallo = 0;

//        if (true) {
//            return;
//        }
        counter++;
        intervallo = (60 * 60 * 24) / frequenzaInSecondi;
        if (counter >= intervallo) {
            counter = 0;// dopo un giorno azzero il counter
        }
        //        System.out.println("counter: " + counter);

        //*****OGNI 5 SECONDI*******   
        intervallo = (intervalSecs) / frequenzaInSecondi;
        if (counter % intervallo == 0) {//10 secondi 
            beat5s = true;
        }

        //*****OGNI 25 SECONDI*******   
        intervallo = (25) / frequenzaInSecondi;
        if (counter % intervallo == 0) {//25 secondi
//            System.out.println("counter 25 secondi--> " + counter);
            attempt++;
        }
////////        //*****OGNI MINUTO*******  
        intervallo = (60) / frequenzaInSecondi;
        if (counter % intervallo == 0) {
            beat1m = true;
        } else {
            beat1m = false;
        }
        ////////        //*****OGNI 2m*******  
        intervallo = (2 * 60) / frequenzaInSecondi;
        if (counter % intervallo == 0) {
            beat2m = true;
        } else {
            beat2m = false;
        }
        ////////        //*****OGNI 5 MINUTI*******      
        intervallo = (5 * 60) / frequenzaInSecondi;
        if (counter % intervallo == 0) {
            beat5m = true;
        } else {
            beat5m = false;
        }
        ////////        //*****OGNI ORA*******      
        intervallo = (60 * 60) / frequenzaInSecondi;
        if (counter % intervallo == 0) {
            beat1h = true;
        } else {
            beat1h = false;
        }
////////        //*****OGNI 6h*******      
        intervallo = (60 * 60 * 6) / frequenzaInSecondi;
        if (counter % intervallo == 0) {
            beat6h = true;
        } else {
            beat6h = false;
        }
////////        //*****OGNI ORA******* 
        intervallo = (60 * 60) / frequenzaInSecondi;
        if (counter % intervallo == 0) {
//             System.out.println("\nmanageSoftwareUpdate ");
            try {
                manageSoftwareUpdate(session);
            } catch (IOException ex) {
                System.out.println("ERROR. " + ex.toString());
            }
        }

        //*****OGNI 2 ORE*******   
        intervallo = (60 * 60 * 2) / frequenzaInSecondi;
        if (counter % intervallo == 0) {
            manageBackup(session);
        }
    }

    private String getJSONarg(JSONObject jsonObject, String argName) {
        //non consente valori null o "null"

        String argValue = "";
        try {
            argValue = jsonObject.get(argName).toString();
        } catch (Exception e) {
            argValue = "";
        }
        if (argValue == null || argValue.equalsIgnoreCase("NULL")) {
            argValue = "";
        }
        return argValue;
    }

    public WShandlerResponse parseMessageReceived(String message, Session session, String clientId) {
        WShandlerResponse myResponse = new WShandlerResponse();
        JSONObject jObj = new JSONObject();
        JSONParser jsonParser = new JSONParser();
//        System.out.println("#parseMessageReceived I TOKEN VALIDI IN RAM SONO:" + clientsConnected.size());
        boolean callerFound = false;
        String RAMclientID = "";
        String RAMsessionID = "";
        String ClientStatus = "";
        String ClientType = "";
        String SENTclientID = clientId;
        String SENTsessionID = session.getId();
        String clientTokenInSessionArchive = "";
        //System.out.println("@@@WS:Messaggio ricevuto da client: " + SENTclientID + "  SESSION ID: " + SENTsessionID  );
        boolean foundInRam = false;
        for (int jj = 0; jj < clientsConnected.size(); jj++) {

            // System.out.println("Confronto con  " + jj + ")" + clientId + " -->  clientID: " + clientsConnected.get(jj).getClientID());
//            if (clientsConnected.get(jj).getClientID().equalsIgnoreCase(clientId)) {
            if (clientsConnected.get(jj).getClientID().endsWith(clientId)) {
                RAMsessionID = clientsConnected.get(jj).getSessionID();
                RAMclientID = clientsConnected.get(jj).getClientID();
                ClientStatus = clientsConnected.get(jj).getStatus();
                clientTokenInSessionArchive = clientsConnected.get(jj).tokenAssigned;
                ClientType = clientsConnected.get(jj).clientType;
                clientsConnected.get(jj).setLastMessageRecorded(System.currentTimeMillis());
                foundInRam = true;
                break;
            }
        }
        if (foundInRam == true) {
            System.out.println("Accetto una richiesta da un client registrato! ");
            callerFound = true;
//            System.out.println("[" + SENTsessionID + "]## FOUND! CALLER:" + clientId + " = " + RAMclientID
//                    + "(RAM:" + RAMsessionID + ") " + "  TYPE:" + ClientType
//                    + "  STATUS:" + ClientStatus
//                    + "  TOKENASSIGNED:" + clientTokenInSessionArchive);
            if (ClientType != null && !ClientType.equalsIgnoreCase("BROWSER") && ClientStatus.startsWith("HANDSHAKED")) {
                setDBenrolledDeviceStatus(RAMclientID, 1, "lastTouch");
            }
//            else {
//                if ( !ClientType.equalsIgnoreCase("BROWSER")) {
//                    setDBenrolledDeviceStatus(RAMclientID, 0, "lastTouch");
//                }
//            }
        } else {
            callerFound = false;
            System.out.println("[" + SENTsessionID + "]## CALLER NOT FOUND IN RAM : " + SENTclientID);
            aggiornaSessioniValide();
        }

        String decodedMessage = message;
        try {
            decodedMessage = java.net.URLDecoder.decode(message, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("ERROR. " + ex.toString());
        }

        try {
            decodedMessage = java.net.URLDecoder.decode(decodedMessage, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("ERROR. " + ex.toString());
        }

//        System.out.println("\n====APP-WS: ===============");
//        System.out.println("RAMsessionID :" + RAMsessionID + "   RAMclientID :" + RAMclientID);
//        System.out.println("SENTsessionID :" + RAMsessionID + "   SENTclientID :" + RAMclientID);
//
        System.out.println("[" + SENTsessionID + "]parseMessageReceived-->MESSAGGIO: " + decodedMessage);
        String tokenReceived = "";
        String MessageTypeReceived = "";
        String paramsReceived = "";
        String uidReceived = "";
        JSONObject MAINjsonObject;
        try {
            jsonParser = new JSONParser();
            MAINjsonObject = (JSONObject) jsonParser.parse(decodedMessage);
            tokenReceived = getJSONarg(MAINjsonObject, "token");
//            System.out.println("tokenReceived: " + tokenReceived);
            MessageTypeReceived = getJSONarg(MAINjsonObject, "type");
//            System.out.println("MessageTypeReceived: " + MessageTypeReceived);
            try {
                uidReceived = getJSONarg(MAINjsonObject, "uid");
            } catch (Exception ex) {
            }
            System.out.println("uidReceived: " + uidReceived);// riferimento alla pagina del browser da cui arriva la richiesta. Cambia se faccio DUPLICA in chrome
            try {
                paramsReceived = getJSONarg(MAINjsonObject, "params");
                try {
                    paramsReceived = java.net.URLDecoder.decode(paramsReceived, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
                }
//                System.out.println("paramsReceived: " + paramsReceived);
            } catch (Exception e) {
            }
        } catch (ParseException ex) {
            System.out.println("WShandler: ERROR6: " + ex.toString());
        }
        if (tokenReceived != null
                && tokenReceived.length() > 0
                && MessageTypeReceived != null
                && MessageTypeReceived.length() > 0) {
            WSclient senderClient = new WSclient(SENTclientID, SENTsessionID, tokenReceived);//compila tokenAssigned
//            System.out.println("[" + SENTsessionID + "]============================");
            System.out.println("[" + SENTsessionID + "]WShandler: MessageTypeReceived: " + MessageTypeReceived);
//            System.out.println("[" + SENTsessionID + "]WShandler: PARAMS: " + paramsReceived);
//            System.out.println("[" + SENTsessionID + "]WShandler: tokenAssigned: " + senderClient.tokenAssigned);
//            System.out.println("[" + SENTsessionID + "]WShandler: tokenReceived: " + tokenReceived);
//            System.out.println("[" + SENTsessionID + "]============================");
            ////////        if (typeReceived== null || typeReceived.equalsIgnoreCase("")){
            ////////            this.parseClose(sessionID);
            ////////        }

//            System.out.println("Creo senderClient! ");
            senderClient = loadWSclientParams(senderClient, paramsReceived);
            senderClient.setURI(session.getRequestURI().getHost());
            myResponse.setSenderClient(senderClient);
            if (MessageTypeReceived.equalsIgnoreCase("handshake") || MessageTypeReceived.equalsIgnoreCase("deviceHandshake")
                    || MessageTypeReceived.equalsIgnoreCase("QPBappHandshake") || MessageTypeReceived.equalsIgnoreCase("serverHandshake")) {

// <editor-fold defaultstate="collapsed" desc="handshake">
                if (MessageTypeReceived.equalsIgnoreCase("handshake")) {/// DA BROWSER!!!!
//                System.out.println("\n###############\n<" + this.handlerID + ">==CASO HANDSHAKE (da browser)===========");

                    lastContextConnected = senderClient.getClientParams().getCKcontextID();
                    Connection accountConny = new EVOpagerDBconnection(senderClient.getClientParams(), mySettings).ConnAccountDB();

                    //prima di inserire una sessione cancello tutte le sessioni esistenti con lo stesso CKtoken (clientID)
                    PreparedStatement ps;
                    String SQLphrase;
                    ResultSet rs = null;
                    System.out.println("[" + SENTsessionID + "]#received Websocket handshake message:" + message);
                    String CLIENT_ID = "SESS-" + session.getId() + "-" + session.getRequestURI();
                    String CLIENT_TYPE = "BROWSER";
                    String CLIENT_MODEL = "defBrowser";
                    String USER_ID = senderClient.getClientParams().getCKuserID();
                    String USER_PW = "XXX";

                    System.out.println("[" + SENTsessionID + "]###############\n\"[\"+SENTsessionID+\"]<" + this.handlerID + ">==CASO HANDSHAKE (da browser)===========");


                    /*
             1. verifico se il token è quello da me registrato
             2. salvo su DB la sessione con i parametri inviati per confrontarli in futuro
                     */
                    for (int jj = 0; jj < clientsConnected.size(); jj++) {
//                System.out.println("\n\n\n\n\n\n->CERCO tra i peers quello che mi ha mandato \nl'handshake:" + tokenReceived);
//                System.out.println(">: " + clientsConnected.get(jj).tokenAssigned);
                        if (clientsConnected.get(jj).getTokenAssigned().equals(tokenReceived)) {
//                        System.out.println(">[" + senderClient.sessionID + "]TROVATO CLIENT IN DB ! " + clientsConnected.get(jj).getTokenAssigned());
                            //senderClient.sessionID = clientsConnected.get(jj).getSessionID();
                            //Qui devo verificare che la SESSIONID del sender corrisponda alla sessionID in RAM

//                        System.out.println("sessionID>[SENDER:" + senderClient.sessionID + "] [RAM:" + clientsConnected.get(jj).getSessionID() + "] ");
                            clientsConnected.get(jj).setClientParams(new EVOpagerParams());
                            clientsConnected.get(jj).setClientParams(senderClient.getClientParams());
                            clientsConnected.get(jj).getClientParams().setCKargs(senderClient.getClientParams().getCKargs());
                            clientsConnected.get(jj).getClientParams().setCKcontextID(senderClient.getClientParams().getCKcontextID());
                            clientsConnected.get(jj).getClientParams().setCKprojectGroup(senderClient.getClientParams().getCKprojectGroup());
                            clientsConnected.get(jj).getClientParams().setCKprojectName(senderClient.getClientParams().getCKprojectName());
                            clientsConnected.get(jj).getClientParams().setCKuserID(senderClient.getClientParams().getCKuserID());
                            clientsConnected.get(jj).getClientParams().setCKtokenID(senderClient.getClientParams().getCKtokenID());

                            clientsConnected.get(jj).setHandshaked(true);
                            clientsConnected.get(jj).setWSUUID(senderClient.getUid());
                            clientsConnected.get(jj).setClientType(CLIENT_TYPE);
                            clientsConnected.get(jj).setClientModel(CLIENT_MODEL);
                            clientsConnected.get(jj).setClientID(CLIENT_ID);
                            clientsConnected.get(jj).setUserID(USER_ID);
                            clientsConnected.get(jj).setUserPassword(USER_PW);//
                            clientsConnected.get(jj).setHandshaked(true);
                            clientsConnected.get(jj).setStatus("HANDSHAKED");
//                        System.out.println("sessionID>[SENDER:" + senderClient.sessionID + "] [RAM:" + clientsConnected.get(jj).getSessionID() + "] - " + "assegnato context (" + clientsConnected.get(jj).getClientParams().getCKcontextID() + ") e utente (" + clientsConnected.get(jj).getClientParams().getCKuserID() + ")");

                            WSclient myClient = clientsConnected.get(jj);
                            updateClientOnDB(myClient, accountConny, tokenReceived);// scrive in archivio_sessions 
                            this.aggiornaSessioniValide();
                            onNewClient(session);
                            break;
                        }
                    }
                    try {
                        accountConny.close();
                    } catch (SQLException ex) {
                        System.out.println("ERROR. " + ex.toString());
                    }
                } else //--------------------------------------------------------------------------
                // </editor-fold>  
                // <editor-fold defaultstate="collapsed" desc="deviceHandshake"> 
                if (MessageTypeReceived.equalsIgnoreCase("deviceHandshake")) {//DA DEVICE ---> CON CONTROLLO USERNAME E PASSWORD
                    // a differenza dell'handshake da browser (che lascia passare tutti  gli utenti perchè sono già autentiucati) qui si  deve fare il 
                    // controllo con una lista di Enrolled Devices presente nella omonima tabella
                    System.out.println("[" + SENTsessionID + "]@@@@@@@@@@@@@@@@@@@\n\"[\"+SENTsessionID+\"]<" + this.handlerID + "> ==CASO DEVICE HANDSHAKE (da DEVICE)===========");
                    System.out.println("[" + SENTsessionID + "]token " + tokenReceived);
                    System.out.println("[" + SENTsessionID + "]session: " + session + "    -   clientId: " + clientId);
                    System.out.println("[" + SENTsessionID + "]decodedMessage: " + decodedMessage);

                    lastContextConnected = senderClient.getClientParams().getCKcontextID();
                    String deviceHandshakeInfos = "";
                    String clientUUID;
                    String clientIP;
                    String clientIPTYPE;
                    try {
                        jsonParser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(decodedMessage);
                        deviceHandshakeInfos = jsonObject.get("payload").toString();
                        try {
                            deviceHandshakeInfos = java.net.URLDecoder.decode(deviceHandshakeInfos, "UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (ParseException ex) {
                        System.out.println("ERROR: " + ex.toString());
                    }
                    System.out.println("[" + SENTsessionID + "]deviceHandshakeInfos: " + deviceHandshakeInfos);
                    JSONParser HSjsonParser = new JSONParser();
                    try {
                        JSONObject jsonObject = (JSONObject) HSjsonParser.parse(deviceHandshakeInfos);

                        senderClient.setWSUUID(getJSONarg(jsonObject, "UUID"));
                        senderClient.setClientIP(getJSONarg(jsonObject, "IP"));
                        senderClient.setClientIPtype(getJSONarg(jsonObject, "IPTYPE"));
                    } catch (ParseException ex) {
                        Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Connection accountConny = new EVOpagerDBconnection(senderClient.getClientParams(), mySettings).ConnAccountDB();

                    //prima di inserire una sessione cancello tutte le sessioni esistenti con lo stesso CKtoken (clientID)
//                PreparedStatement ps;
//                String SQLphrase;
//                ResultSet rs = null;

                    /*
             1. verifico se il token è quello da me registrato
             2. salvo su DB la sessione con i parametri inviati per confrontarli in futuro
                     */
                    String session_removed = "";
                    for (int jj = 0; jj < clientsConnected.size(); jj++) {
//                System.out.println("\n\n\n\n\n\n->CERCO tra i peers quello che mi ha mandato \nl'handshake:" + tokenReceived);
//                System.out.println(">: " + clientsConnected.get(jj).tokenAssigned);
                        if (clientsConnected.get(jj).getTokenAssigned().equals(tokenReceived)) {
                            System.out.println("[" + SENTsessionID + "]deviceHandshake>[" + senderClient.sessionID + "]TROVATO CLIENT IN DB ! " + clientsConnected.get(jj).getTokenAssigned());
                            System.out.println("[" + SENTsessionID + "]sessionID>[SENDER:" + senderClient.sessionID + "] [RAM:" + clientsConnected.get(jj).getSessionID() + "] ");

                            clientsConnected.get(jj).setClientParams(new EVOpagerParams());
                            clientsConnected.get(jj).setClientParams(senderClient.getClientParams());
                            clientsConnected.get(jj).getClientParams().setCKargs(senderClient.getClientParams().getCKargs());
                            clientsConnected.get(jj).getClientParams().setCKcontextID(senderClient.getClientParams().getCKcontextID());
                            clientsConnected.get(jj).getClientParams().setCKprojectGroup(senderClient.getClientParams().getCKprojectGroup());
                            clientsConnected.get(jj).getClientParams().setCKprojectName(senderClient.getClientParams().getCKprojectName());
                            clientsConnected.get(jj).getClientParams().setCKuserID(senderClient.getClientParams().getCKuserID());
                            clientsConnected.get(jj).getClientParams().setCKtokenID(senderClient.getClientParams().getCKtokenID());

                            clientsConnected.get(jj).setClientType(senderClient.clientType);
                            clientsConnected.get(jj).setClientModel(senderClient.clientModel);
                            clientsConnected.get(jj).setClientID(senderClient.clientID);
                            clientsConnected.get(jj).setUserID(senderClient.userID);
                            clientsConnected.get(jj).setUserPassword(senderClient.userPassword);
                            clientsConnected.get(jj).setClientIP(senderClient.clientIP);
                            clientsConnected.get(jj).setClientIPtype(senderClient.clientIPtype);
                            clientsConnected.get(jj).setWSUUID(senderClient.WSUUID);

                            clientsConnected.get(jj).setHandshaked(true);
                            System.out.println("[" + SENTsessionID + "]assegnato context (" + clientsConnected.get(jj).getClientParams().getCKcontextID() + ") "
                                    + "e utente (" + clientsConnected.get(jj).getClientParams().getCKuserID() + ") e UUID (" + clientsConnected.get(jj).getWSUUID() + ")"
                            );

                            WSclient myClient = clientsConnected.get(jj);
                            //1. controllo se era una delle enrolled devices
                            // se è enrolled la inserisco nel database delle sessions
                            // se non è enrolled la segnalo fra i golem e poi la sgancio come websocket
                            boolean enrolled = false;
                            enrolled = verifyEnrolledClientOnDB(myClient, accountConny);

                            if (enrolled == true) {
                                clientsConnected.get(jj).setStatus("HANDSHAKED-DEVICE");
                                System.out.println("IL DEVICE " + myClient.clientID + " si trova tra le enrolled devices. Viene aggiunto nella connessione locale");
                                updateClientOnDB(myClient, accountConny, tokenReceived);// compila la tabella archivio_sessions
                                this.aggiornaSessioniValide();
                                // imposto la enrolled device corrispondente come online
                                setDBenrolledDeviceStatus(clientsConnected.get(jj).getClientID(), 1, "login");
                                onNewClient(session);//broadcast peers
                            } else {
                                clientsConnected.get(jj).setStatus("REFUSED");
                                System.out.println("IL DEVICE " + myClient.clientID + " NON si trova tra le enrolled devices. Viene aggiunto tra i GOLEM DEVICES");
                                createGolemOnDB(myClient, accountConny);
                                jObj = new JSONObject();
                                jObj.put("ip", "0000");
                                jObj.put("TYPE", "REMOVESESSION");
                                jObj.put("VALUE", "CLIENT_REFUSED");
                                setDBenrolledDeviceStatus(clientsConnected.get(jj).getClientID(), 0, "lastTuoch");
                            }
                            break;
                        }
                    }

                    try {
                        accountConny.close();
                    } catch (SQLException ex) {
                        System.out.println("ERROR. " + ex.toString());
                    }

                } else //--------------------------------------------------------------------------
                // </editor-fold>                 
                // <editor-fold defaultstate="collapsed" desc="QPBappHandshake"> 
                if (MessageTypeReceived.equalsIgnoreCase("QPBappHandshake")) {

                    System.out.println("\n###############\n<" + this.handlerID + "> ==CASO QPBapp HANDSHAKE (da QPBapp)===========");
                    System.out.println("token " + tokenReceived);
                    System.out.println("session: " + session + "    -   clientId: " + clientId);
                    System.out.println("decodedMessage: " + decodedMessage);

                    lastContextConnected = senderClient.getClientParams().getCKcontextID();

                    Connection accountConny = new EVOpagerDBconnection(senderClient.getClientParams(), mySettings).ConnAccountDB();

                    //prima di inserire una sessione cancello tutte le sessioni esistenti con lo stesso CKtoken (clientID)
                    PreparedStatement ps;
                    String SQLphrase;
                    ResultSet rs = null;

                    /*
             1. verifico se il token è quello da me registrato
             2. salvo su DB la sessione con i parametri inviati per confrontarli in futuro
                     */
                    for (int jj = 0; jj < clientsConnected.size(); jj++) {
//                System.out.println("\n\n\n\n\n\n->CERCO tra i peers quello che mi ha mandato \nl'handshake:" + tokenReceived);
//                System.out.println(">: " + clientsConnected.get(jj).tokenAssigned);
                        if (clientsConnected.get(jj).getTokenAssigned().equals(tokenReceived)) {
                            System.out.println("QPBappHandshake>[" + senderClient.sessionID + "]TROVATO CLIENT IN DB ! " + clientsConnected.get(jj).getTokenAssigned());
                            System.out.println("sessionID>[SENDER:" + senderClient.sessionID + "] [RAM:" + clientsConnected.get(jj).getSessionID() + "] ");

                            clientsConnected.get(jj).setClientParams(new EVOpagerParams());
                            clientsConnected.get(jj).setClientParams(senderClient.getClientParams());
                            clientsConnected.get(jj).getClientParams().setCKargs(senderClient.getClientParams().getCKargs());
                            clientsConnected.get(jj).getClientParams().setCKcontextID(senderClient.getClientParams().getCKcontextID());
                            clientsConnected.get(jj).getClientParams().setCKprojectGroup(senderClient.getClientParams().getCKprojectGroup());
                            clientsConnected.get(jj).getClientParams().setCKprojectName(senderClient.getClientParams().getCKprojectName());
                            clientsConnected.get(jj).getClientParams().setCKuserID(senderClient.getClientParams().getCKuserID());
                            clientsConnected.get(jj).getClientParams().setCKtokenID(senderClient.getClientParams().getCKtokenID());

                            clientsConnected.get(jj).setClientType(senderClient.clientType);
                            clientsConnected.get(jj).setClientModel(senderClient.clientModel);
                            clientsConnected.get(jj).setClientID(senderClient.clientID);
                            clientsConnected.get(jj).setUserID(senderClient.userID);
                            clientsConnected.get(jj).setUserPassword(senderClient.userPassword);

                            clientsConnected.get(jj).setHandshaked(true);
                            clientsConnected.get(jj).setStatus("HANDSHAKED-DEVICE");
                            System.out.println("assegnato context (" + clientsConnected.get(jj).getClientParams().getCKcontextID() + ") e utente (" + clientsConnected.get(jj).getClientParams().getCKuserID() + ")");
                            WSclient myClient = clientsConnected.get(jj);
                            updateClientOnDB(myClient, accountConny, tokenReceived);
                            this.aggiornaSessioniValide();
                            onNewClient(session);
                            break;
                        }
                    }
                    try {
                        accountConny.close();
                    } catch (SQLException ex) {
                        System.out.println("ERROR. " + ex.toString());
                    }

                } else // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="serverHandshake"> 
                // dopo aver ricevuto il token da queswto server, il client mi manda un handshake
                // con le info che lo riguardano da salvare sul DB
                if (MessageTypeReceived.equalsIgnoreCase("serverHandshake")) {

//            System.out.println("\n#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*\n <" + this.handlerID + ">==CASO SERVER HANDSHAKE (da INSTALLAZIONE SOFTWARE)===========");
//            System.out.println("token " + tokenReceived);
//            System.out.println("session: " + session + "    -   clientId: " + clientId);
//            System.out.println("decodedMessage: " + decodedMessage);
                    try {
                        Connection accountConny = new EVOpagerDBconnection(senderClient.getClientParams(), mySettings).ConnAccountDB();

                        //prima di inserire una sessione cancello tutte le sessioni esistenti con lo stesso CKtoken (clientID)
                        PreparedStatement ps;
                        String SQLphrase;
                        ResultSet rs = null;
                        boolean tokenfound = false;
                        /*
             1. verifico se il token è quello da me registrato
             2. salvo su DB la sessione con i parametri inviati per confrontarli in futuro
                         */
                        for (int jj = 0; jj < clientsConnected.size(); jj++) {
//                System.out.println("\n\n\n\n\n\n->CERCO tra i peers quello che mi ha mandato \nl'handshake:" + tokenReceived);
//                System.out.println(">: " + clientsConnected.get(jj).tokenAssigned);
                            if (clientsConnected.get(jj).getTokenAssigned().equals(tokenReceived)) {
                                tokenfound = true;
                                System.out.println("serverHandshake>[" + senderClient.sessionID + "]TROVATO CLIENT IN DB ! " + clientsConnected.get(jj).getTokenAssigned());
                                System.out.println("sessionID>[SENDER:" + senderClient.sessionID + "] [RAM:" + clientsConnected.get(jj).getSessionID() + "] ");

                                clientsConnected.get(jj).setClientParams(new EVOpagerParams());
                                clientsConnected.get(jj).setClientParams(senderClient.getClientParams());
                                clientsConnected.get(jj).getClientParams().setCKargs(senderClient.getClientParams().getCKargs());
                                clientsConnected.get(jj).getClientParams().setCKcontextID(senderClient.getClientParams().getCKcontextID());
                                clientsConnected.get(jj).getClientParams().setCKprojectGroup(senderClient.getClientParams().getCKprojectGroup());
                                clientsConnected.get(jj).getClientParams().setCKprojectName(senderClient.getClientParams().getCKprojectName());
                                clientsConnected.get(jj).getClientParams().setCKuserID(senderClient.getClientParams().getCKuserID());
                                clientsConnected.get(jj).getClientParams().setCKtokenID(senderClient.getClientParams().getCKtokenID());

                                clientsConnected.get(jj).setClientType(senderClient.clientType);
                                clientsConnected.get(jj).setClientModel(senderClient.clientModel);
                                clientsConnected.get(jj).setClientID(senderClient.clientID);
                                clientsConnected.get(jj).setUserID(senderClient.userID);
                                clientsConnected.get(jj).setUserPassword(senderClient.userPassword);
                                clientsConnected.get(jj).setURI(senderClient.URI);

                                clientsConnected.get(jj).setHandshaked(true);
                                clientsConnected.get(jj).setStatus("HANDSHAKED-SERVER");
                                System.out.println("assegnato context (" + clientsConnected.get(jj).getClientParams().getCKcontextID() + ") e utente (" + clientsConnected.get(jj).getClientParams().getCKuserID() + ")");
                                WSclient myClient = clientsConnected.get(jj);
                                updateClientOnDB(myClient, accountConny, tokenReceived);
                                this.aggiornaSessioniValide();
                                onNewClient(session);
                                break;
                            }
                        }

                        if (tokenfound == false) {
                            System.out.println("<" + this.handlerID + ">Non ho trovato il token fra i non registrati ");
                        }

                        accountConny.close();
                    } catch (SQLException ex) {
                        System.out.println("ERROR. " + ex.toString());
                    }

                } else // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="deviceMap">   
                if (MessageTypeReceived.equalsIgnoreCase("deviceMap")) {
                    System.out.println("\n#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*\n <" + this.handlerID
                            + ">==CASO DEVICE MAP===========");
                    System.out.println("token " + tokenReceived);
                    System.out.println("session: " + session + "    -   clientId: " + clientId);
                    System.out.println("decodedMessage: " + decodedMessage);

                }  //--------------------------------------------------------------------------
                // </editor-fold>
            } else {
                if (callerFound == true) {
                    // <editor-fold defaultstate="collapsed" desc="formsMap">   
                    if (MessageTypeReceived.equalsIgnoreCase("formsMap")) {
//            System.out.println("\n#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*\n <" + this.handlerID
//                    + ">==CASO FORMS MAP===========");
//            System.out.println("token " + tokenReceived);
//            System.out.println("session: " + session + "    -   clientId: " + clientId);
//            System.out.println("decodedMessage: " + decodedMessage);

                        String formsMap = "";
                        try {
                            jsonParser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(decodedMessage);
                            formsMap = jsonObject.get("payload").toString();
                            try {
                                formsMap = java.net.URLDecoder.decode(formsMap, "UTF-8");
                            } catch (UnsupportedEncodingException ex) {
                                Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (ParseException ex) {
                            System.out.println("ERROR: " + ex.toString());
                        }

                        for (int jj = 0; jj < clientsConnected.size(); jj++) {
                            if (clientsConnected.get(jj).getTokenAssigned().equalsIgnoreCase(tokenReceived)) {
                                senderClient.sessionID = clientsConnected.get(jj).getSessionID();
                                clientsConnected.get(jj).getClientParams().setFormsMap(formsMap);
//                    System.out.println("Settata fromsMap per client: "
//                            + clientsConnected.get(jj).clientParams.getCKuserID()
//                            + " MAP:" + formsMap);
                                break;
                            }
                        }
                    } else //--------------------------------------------------------------------------
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="quickDutyOrder">   
                    if (MessageTypeReceived.equalsIgnoreCase("quickDutyOrder")) {
                        System.out.println("[" + SENTsessionID + "]\n###############\n ==CASO quickDutyOrder  ===========");

                        try {
                            jsonParser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(decodedMessage);
                            JSONObject payload = (JSONObject) jsonObject.get("payload");
                            System.out.println("payload: " + payload);

                            //==CASO quickDutyOrder  ===========
                            //payload: {"password":"pass","sender":"thermLCDdev","payload":"SETPIN,2,FLIP","targetDevice":"AriaDEMO","user":"pippo"}
                            String sender = getJSONarg(payload, "sender");
                            String targetDevice = getJSONarg(payload, "targetDevice");
                            String user = getJSONarg(payload, "user");
                            String password = getJSONarg(payload, "password");
                            String order = getJSONarg(payload, "payload");

                            String targetSessionID = "";
                            for (int jj = 0; jj < clientsConnected.size(); jj++) {
                                if (clientsConnected.get(jj).getClientID().equalsIgnoreCase(targetDevice)) {
                                    targetSessionID = clientsConnected.get(jj).getSessionID();
                                    jObj = new JSONObject();
                                    jObj.put("order", order);
                                    jObj.put("user", user);
                                    jObj.put("password", password);
                                    //this.sendToPeer("heartbeat", answerJSON, session.getId(), jObj.toString());
                                    this.sendToPeer("dutyOrder", jObj, targetSessionID, "dutyOrder");
                                    break;
                                }
                            }

                        } catch (ParseException ex) {
                            System.out.println("ERROR: " + ex.toString());
                        }

                    } else //--------------------------------------------------------------------------
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="P2P">   
                    if (MessageTypeReceived.equalsIgnoreCase("P2P")) {
                        String CommArray = "";
                        System.out.println("[" + SENTsessionID + "]\n###############\n ==CASO P2P  ===========");
                        String idMittente = "";
                        String tokenMittente = tokenReceived;
                        String idDestinatario = "";
                        String tokenDestinatario = "";
                        String payload = "";

                        try {
                            jsonParser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(decodedMessage);
                            payload = getJSONarg(jsonObject, "payload");
                        } catch (ParseException ex) {
                            System.out.println("ERROR: " + ex.toString());
                        }

                        for (int jj = 0; jj < clientsConnected.size(); jj++) {
                            if (clientsConnected.get(jj).getTokenAssigned().equalsIgnoreCase(tokenReceived)) {
                                senderClient.sessionID = clientsConnected.get(jj).getSessionID();
                                break;
                            }
                        }

                        System.out.println("idMittente: " + idMittente);
                        System.out.println("tokenMittente: " + tokenMittente);
                        System.out.println("idDestinatario: " + idDestinatario);
                        System.out.println("tokenDestinatario: " + tokenDestinatario);
                        System.out.println("payload: " + payload);
                        //adesso sviscero il payload per ricavare una array di comandi per i destinatari
                        EVOpagerParams clientParams = new EVOpagerParams();

                        for (int jj = 0; jj < clientsConnected.size(); jj++) {
                            if (clientsConnected.get(jj).getTokenAssigned().equals(tokenMittente)) {
                                clientParams = clientsConnected.get(jj).getClientParams();
                            }
                        }

                        JSONObject jsonObject;
                        try {
                            jsonObject = (JSONObject) jsonParser.parse(payload);

                            String rifForm = "";
                            String keyValue = "";
                            String rifObj = "";
                            rifForm = getJSONarg(jsonObject, "rifForm");
                            keyValue = getJSONarg(jsonObject, "keyValue");
                            rifObj = getJSONarg(jsonObject, "rifObj");
                            ShowItForm myForm = new ShowItForm(rifForm, clientParams, mySettings);
                            myForm.buildSchema();

                            for (int jj = 0; jj < myForm.objects.size(); jj++) {
                                if (myForm.objects.get(jj).getName().equals(rifObj)) {
                                    System.out.println("Trovato oggetto  " + rifObj);
                                    System.out.println("getParams:  " + myForm.objects.get(jj).CG.getParams());
                                    System.out.println("getType:  " + myForm.objects.get(jj).CG.getType());
                                    System.out.println("getValue:  " + myForm.objects.get(jj).CG.getValue());
                                    CommArray = myForm.objects.get(jj).CG.getParams();
                                    break;
                                }
                            }
                            /*
                         getParams:  {"orders":[{"destBISHOP":"BISHOP01","destBISHOPpw":"mypassword",
                "action":"SETPERIPHVALUE","deviceLocalIP":"192.168.1.101","deviceLocalPeriph":"1",
                "message":{"command":"SETVALUE","periph":"1","value":"1"}}]}
                             */

                        } catch (ParseException ex) {
                            Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        System.out.println("CommArray:  " + CommArray);
                        ArrayList<WSorderP2P> Orders = new ArrayList();

                        if (CommArray != null && CommArray.length() > 0) {
                            JSONParser parser = new JSONParser();
                            Object obj;

                            try {
                                obj = parser.parse(CommArray);
                                JSONArray array = (JSONArray) obj;

                                for (Object riga : array) {
                                    JSONObject commandRow = (JSONObject) jsonParser.parse(riga.toString());
                                    WSorderP2P myOrder = new WSorderP2P();
                                    try {
                                        myOrder.DestinatarioID = commandRow.get("destBISHOP").toString();
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myOrder.DestinatarioPIN = commandRow.get("destBISHOPpw").toString();
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myOrder.DeviceLocalIP = commandRow.get("deviceLocalIP").toString();
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myOrder.DeviceLocalPeriph = commandRow.get("deviceLocalPeriph").toString();
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myOrder.TBSmessage = commandRow.get("message").toString();
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myOrder.payload = (JSONObject) commandRow;
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myOrder.action = commandRow.get("action").toString();
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myOrder.DestinatarioToken = tokenDestinatario;
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myOrder.value = "";
                                    } catch (Exception e) {
                                    }
                                    Orders.add(myOrder);
                                }
                            } catch (ParseException ex) {
                                Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        int responseValue = 0;

                        if (Orders.size() > 0) {

                            // ci sono ordini da eseguire
                            for (int jj = 0; jj < Orders.size(); jj++) {
                                if (Orders.get(jj).action.equalsIgnoreCase("SETPERIPHVALUE")) {

                                    System.out.println(" ==CASO SETPERIPHVALUE  =========== CERCO->" + Orders.get(jj).DestinatarioID);
                                    // cerco il destinatario e gli invio il pacchetto do comando
                                    for (int kk = 0; kk < clientsConnected.size(); kk++) {
                                        System.out.println("Confronto client con DEV ID = " + clientsConnected.get(kk).clientID);
                                        // in questo caso faccio riferimento al devID che per il browser sarebbe nullo, mentre per un device è compilato
                                        if (clientsConnected.get(kk).clientID != null && clientsConnected.get(kk).clientID.equals(Orders.get(jj).DestinatarioID)) {
                                            // qui devo controllare anche che il device sia nella rosa disponibile dell'utente e che il PIN sia giusto
                                            // magari impongo anche l'ID dell'owner
                                            System.out.println("E' proprio il destinatario dell'ordine -->" + Orders.get(jj).DestinatarioID);

                                            Orders.get(jj).sessionID = clientsConnected.get(kk).getSessionID();
                                            System.out.println("sendToPeer:  " + Orders.get(jj).payload);
                                            try {
                                                sendToPeer("executeCommand", Orders.get(jj).payload, Orders.get(jj).sessionID, Orders.get(jj).value);
                                                responseValue = 1;
                                            } catch (Exception e) {
                                                responseValue = -1;
                                                clientsConnected.get(kk).setNofErrors(clientsConnected.get(kk).nofErrors + 1);

                                                System.out.println("Errore in invio:  " + Orders.get(jj).sessionID);
                                            }
                                            break;
                                        } else {
                                            System.out.println("Non è il destinatario dell'ordine -->" + Orders.get(jj).DestinatarioID);
                                        }
                                    }
                                    // ora posso fare il send

                                } else if (Orders.get(jj).action.equalsIgnoreCase("EXECUTEROUTINE")) {
                                    System.out.println(" ==CASO EXECUTEROUTINE  ===========");
                                    // cerco il destinatario e gli invio il pacchetto do comando
                                    for (int kk = 0; kk < clientsConnected.size(); kk++) {
                                        if (clientsConnected.get(kk).clientID.equals(Orders.get(jj).DestinatarioID)) {
                                            // qui devo controllare anche che il device sia nella rosa disponibile dell'utente e che il PIN sia giusto
                                            // magari impongo anche l'ID dell'owner
                                            Orders.get(jj).sessionID = clientsConnected.get(kk).getSessionID();
                                            try {
                                                sendToPeer("executeRoutine", Orders.get(jj).payload, Orders.get(jj).sessionID, Orders.get(jj).value);
                                            } catch (Exception e) {
                                                responseValue = -1;
                                                clientsConnected.get(kk).setNofErrors(clientsConnected.get(kk).nofErrors + 1);
                                                System.out.println("Errore in invio:  " + Orders.get(jj).sessionID);
                                            }
                                            break;
                                        }
                                    }
                                    // ora posso fare il send

                                }
                            }

                        }
                        String responseMessage = "receivedOK";
                        if (responseValue == 0) {
                            responseMessage = "Target not found";
                        }
                        if (responseValue == -1) {
                            responseMessage = "Comunication error";
                        }
                        if (responseValue == 1) {
                            responseMessage = "Comunication delivered";
                        }
                        jObj = new JSONObject();
                        jObj.put("ip", "0000");
                        jObj.put("TYPE", "" + responseMessage);
                        jObj.put("VALUE", "" + responseValue);

                    } else //--------------------------------------------------------------------------
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="deviceCall"> 
                    if (MessageTypeReceived.equalsIgnoreCase("deviceCall")) {

                        // Qui dovrfei comparare le info in senderClient con quelle nel database tab. archivio_sessions
                        // se ho una risposta di TYPE=CUSTOM allora devo elaborare l'Obj da inviare qui e adesso
                        // direttamente su AppWS, poi aggiorno il PAYLOAD della risposta e la invio
                        // Questo serve in casi specifici di job per i quali non voglio oberare PAGER ma essendo app-specifico
                        // cerco di spostare il focus sull'applicazione
                        JSONObject obj = new JSONObject();
                        obj.put("TYPE", "CUSTOM");
                        obj.put("receivedPayload", message);// contiene clientPoint:oggetto JSON con clientID, sessionID e token appena assegnato
                        obj.put("receivedType", "deviceCall");
                        obj.put("priority", "QUICK");
                        jObj = obj;

                    } else //--------------------------------------------------------------------------
                    // </editor-fold>        
                    // <editor-fold defaultstate="collapsed" desc="heartbeat">  
                    if (MessageTypeReceived.equalsIgnoreCase("heartbeat")) {
                        JSONObject answerJSON = new JSONObject();
                        answerJSON.put("clientId", clientId);
                        answerJSON.put("sessionID", session.getId());
                        if (ClientStatus.equalsIgnoreCase("REFUSED")) {
                            jObj = new JSONObject();
                            jObj.put("ip", "0000");
                            jObj.put("TYPE", "REMOVESESSION");
                            jObj.put("VALUE", "CLIENT_REFUSED");
                            //this.sendToPeer("heartbeat", answerJSON, session.getId(), jObj.toString());
                            this.sendToPeer("heartbeatNAK", jObj, session.getId(), "CLIENT_REFUSED");
                        } else {
//============ASSEGNO IL TOKEN AL DESTINATARIO=========================
                            this.sendToPeer("heartbeatOK", answerJSON, session.getId(), "");
//============ASSEGNO IL TOKEN AL DESTINATARIO=========================        
                        }
                    } else // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="wsRequest">  
                    if (MessageTypeReceived.equalsIgnoreCase("wsRequest")) {
                        // per principio, tutti gli scambi non generalisti, ma app-specifici vanno gestiti sul AppWS secifico, per cui devo dirottare wui 
                        // tutte le comunicazioni di questo tipo così che tornino al callback con le info necessarie ad essere eseguite fuori da Pager
                        //--------------------------------------------------------------------------------------------------------------------------------
////////                String msg = "";
////////                msg += ("\n<" + this.handlerID + ">SERVERHANDSHAKE [" + senderClient.sessionID + "]:");
////////                msg += ("\n [" + senderClient.sessionID + "]CKprojectGroup: " + senderClient.getClientParams().getCKprojectGroup());
////////                msg += ("\n [" + senderClient.sessionID + "]CKprojectName: " + senderClient.getClientParams().getCKprojectName());
////////                msg += ("\n [" + senderClient.sessionID + "]CKcontextID: " + senderClient.getClientParams().getCKcontextID());
////////                msg += ("\n [" + senderClient.sessionID + "]CKuserID: " + senderClient.getClientParams().getCKuserID());
////////                msg += ("\n [" + senderClient.sessionID + "]CKargs: " + senderClient.getClientParams().getCKargs());
////////                msg += ("\n [" + senderClient.sessionID + "]CKtokenID: " + senderClient.getClientParams().getCKtokenID());
////////                msg += ("\n [" + senderClient.sessionID + "]token: " + senderClient.token);
////////                msg += ("\n [" + senderClient.sessionID + "]clientID: " + senderClient.clientID);
////////                msg += ("\n [" + senderClient.sessionID + "]type: " + senderClient.clientType);
////////                msg += ("\n [" + senderClient.sessionID + "]model: " + senderClient.clientModel);
////////                msg += ("\n [" + senderClient.sessionID + "]userID: " + senderClient.userID);
////////                msg += ("\n [" + senderClient.sessionID + "]userPassword: " + senderClient.userPassword);
////////                System.out.println(msg);
                        smartAction myAction = new smartAction(this, senderClient, senderClient.getClientParams(), mySettings);
                        myResponse.setActionType("wsRequestCallback");
                        myResponse.setDecodedMessage(decodedMessage);
                        myResponse.setMyAction(myAction);

                        System.out.println("[" + SENTsessionID + "]**WSHANDLER**DOOR:  " + myAction.getDoor());
                        System.out.println("[" + SENTsessionID + "]**WSHANDLER**EVENT: " + myAction.getEvent());
                        System.out.println("[" + SENTsessionID + "]**WSHANDLER**Childs().size: " + myAction.getMyChilds().size());
                    } else // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="deviceWSrequest">  
                    if (MessageTypeReceived.equalsIgnoreCase("deviceWSrequest")) {

                        smartAction myAction = new smartAction(this, senderClient, senderClient.getClientParams(), mySettings);
                        myResponse.setActionType("deviceWSrequestCallback");
                        myResponse.setDecodedMessage(decodedMessage);
                        myResponse.setMyAction(myAction);
                        myResponse.setSenderClient(senderClient);

                    }
// </editor-fold>
                } else {
                    System.out.println("Non posso accettare una richiesta da un client non registrato! ");
                }
            }
        } else {
            System.out.println("WRONG TOKEN " + tokenReceived);

        }
        myResponse.setjObj(jObj);

        return myResponse;
    }

    private boolean verifyEnrolledClientOnDB(WSclient myClient, Connection accountConny) {
        boolean enrolled = false;
        // devo trovare nelle enrolled devices una con lo stesso nome, nome utente e password
        String guestName = myClient.clientID;
        String guestUser = myClient.userID;
        String guestPassword = myClient.userPassword;
        System.out.println("guestName: " + guestName + "     guestUser: " + guestUser + "    guestPassword: " + guestPassword);
        //... vediamo se esiste nelle enrolled devices
        PreparedStatement ps;
        String SQLphrase = null;
        ResultSet rs;
        try {
            SQLphrase = "SELECT * FROM enrolledDevices WHERE name = '" + guestName + "'  AND username = '" + guestUser + "'  AND  password = '" + guestPassword + "'  ";
            ps = accountConny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                enrolled = true;
            }
        } catch (SQLException ex) {
            System.out.println("ERROR. " + SQLphrase + "\n" + ex.toString());
        }

        return enrolled;
    }

    private void createGolemOnDB(WSclient myClient, Connection accountConny) {
        PreparedStatement ps;
        String SQLphrase = null;
        ResultSet rs;
        boolean found = false;
        try {
            SQLphrase = "SELECT * FROM  golemDevices  WHERE  name  = '" + myClient.clientID + "'";
            ps = accountConny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();

            while (rs.next()) {
                found = true;
            }
        } catch (SQLException ex) {
            System.out.println("ERROR. " + SQLphrase + "\n" + ex.toString());
        }
        if (found == false) {
            try {
                SQLphrase = "INSERT INTO golemDevices(username, name, ID, IP, IPtype, clientType, clientModel) VALUES "
                        + "('" + myClient.localUserID + "','" + myClient.clientID + "','" + myClient.clientID + "','" + myClient.clientIP + "','" + myClient.clientIPtype + "','" + myClient.clientType + "','" + myClient.clientModel + "')";
                ps = accountConny.prepareStatement(SQLphrase);
                int i = ps.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("ERROR. " + SQLphrase + "\n" + ex.toString());
            }
        } else {
            try {
                SQLphrase = "UPDATE golemDevices SET "
                        + "username='" + myClient.localUserID + "', "
                        + "IP='" + myClient.clientIP + "', "
                        + "IPtype='" + myClient.clientIPtype + "', "
                        + "clientType='" + myClient.clientType + "', "
                        + "clientModel='" + myClient.clientModel + "' "
                        + " WHERE name ='" + myClient.clientID + "' ";

                ps = accountConny.prepareStatement(SQLphrase);
                int i = ps.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("ERROR. " + SQLphrase + "\n" + ex.toString());
            }
        }
    }

    private void updateClientOnDB(WSclient myClient, Connection accountConny, String token) {
        PreparedStatement ps;
        String SQLphrase;
        ResultSet rs;
        String user = myClient.getClientParams().getCKuserID();
        if (user == null || user.length() < 1) {
            user = "(U)" + myClient.getUserID() + "(P)" + myClient.getUserPassword();
        }

        // prima guardo se esiste
        SQLphrase = "SELECT * FROM archivio_sessions   "
                + "WHERE token = '" + token + "'";
        System.out.println("\n\n**************\n<" + this.handlerID + ">SQLphrase:" + SQLphrase);
        boolean exists = false;
        try {
            ps = accountConny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                exists = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (exists == true) {
            try {

                SQLphrase = "UPDATE archivio_sessions SET "
                        + "contextID = '" + myClient.getClientParams().getCKcontextID() + "', "
                        + "user = '" + user + "', "
                        + "clientID = '" + myClient.clientID + "', "
                        + "clientType = '" + myClient.clientType + "', "
                        + "clientModel = '" + myClient.clientModel + "', "
                        + "clientIP = '" + myClient.clientIP + "', "
                        + "status = '" + myClient.status + "', "
                        + "clientUUID = '" + myClient.WSUUID + "', "
                        + "lastTouch=NOW() "
                        + "WHERE token = '" + token + "'";
                System.out.println("\n\n**************\n<" + this.handlerID + ">SQLphrase:" + SQLphrase);
                ps = accountConny.prepareStatement(SQLphrase);
                int i = ps.executeUpdate();
//            System.out.println("<" + this.handlerID + "> Result SQL:" + i);

            } catch (SQLException ex) {
                System.out.println("ERROR. " + ex.toString());
            }
        } else {
            // non esiste sa sessione nel database... la creo
            // di norma questo non succede, ma se ero collegato e si riavvia il server potrebbe succedere che nella lista delle sessions non ci sia il browser che era rimasto aperto
            // lo inserisco come open e aspetto un nuovo handshake.. gli devo fare un tokenassign
            SQLphrase = "INSERT INTO  archivio_sessions ("
                    + " token , "
                    + " sessionID , "
                    + " lastTouch , "
                    + " clientType , "
                    + " clientIP , "
                    + " clientID , "
                    + " contextID , "
                    + " status , "
                    + " user , "
                    + " clientUUID , "
                    + " clientModel "
                    + ") VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        }
    }

    private WSclient loadWSclientInfos(WSclient myClient, String payload) {

        return myClient;
    }

    private WSclient loadWSclientParams(WSclient myClient, String decodedMessage) {

        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(decodedMessage);

            myClient.getClientParams().setCKprojectGroup(getJSONarg(jsonObject, "CKprojectGroup"));
            myClient.getClientParams().setCKprojectName(getJSONarg(jsonObject, "CKprojectName"));
            myClient.getClientParams().setCKuserID(getJSONarg(jsonObject, "CKuserID"));
            myClient.getClientParams().setCKtokenID(getJSONarg(jsonObject, "CKtokenID"));
            myClient.getClientParams().setCKcontextID(getJSONarg(jsonObject, "CKcontextID"));

//            System.out.println(mySettings.getSoftwareName() + " - APP-WS: ");
//            System.out.println("============================");
            myClient.setToken(getJSONarg(jsonObject, "token"));
            myClient.setClientID(getJSONarg(jsonObject, "clientID"));
            myClient.setClientType(getJSONarg(jsonObject, "clientType"));
            myClient.setClientModel(getJSONarg(jsonObject, "clientModel"));
            myClient.setUserID(getJSONarg(jsonObject, "userID"));
            myClient.setUserPassword(getJSONarg(jsonObject, "userPassword"));
            myClient.setClientIP(getJSONarg(jsonObject, "IP"));
            myClient.setClientIPtype(getJSONarg(jsonObject, "IPTYPE"));
            myClient.setWSUUID(getJSONarg(jsonObject, "UUID"));
            String msg = "";
////////            msg += ("\n<" + this.handlerID + ">SERVERHANDSHAKE [" + myClient.sessionID + "]:");
////////            msg += ("\n [" + myClient.sessionID + "]CKprojectGroup: " + myClient.getClientParams().getCKprojectGroup());
////////            msg += ("\n [" + myClient.sessionID + "]CKprojectName: " + myClient.getClientParams().getCKprojectName());
////////            msg += ("\n [" + myClient.sessionID + "]CKcontextID: " + myClient.getClientParams().getCKcontextID());
////////            msg += ("\n [" + myClient.sessionID + "]CKuserID: " + myClient.getClientParams().getCKuserID());
////////            msg += ("\n [" + myClient.sessionID + "]CKargs: " + myClient.getClientParams().getCKargs());
////////            msg += ("\n [" + myClient.sessionID + "]CKtokenID: " + myClient.getClientParams().getCKtokenID());
////////
////////            msg += ("\n [" + myClient.sessionID + "]token: " + myClient.token);
            msg += ("\n [" + myClient.sessionID + "]clientID: " + myClient.clientID);
            msg += ("\n [" + myClient.sessionID + "]type: " + myClient.clientType);
            msg += ("\n [" + myClient.sessionID + "]model: " + myClient.clientModel);
            msg += ("\n [" + myClient.sessionID + "]userID: " + myClient.userID);
            msg += ("\n [" + myClient.sessionID + "]userPassword: " + myClient.userPassword);
            System.out.println(msg);
        } catch (ParseException ex) {
            System.out.println("ERROR IN server message BAD SYNTAX: " + ex.toString());
        }

        return myClient;
    }

//onOpen
    public void parseOpen(Session session, String clientId) {
        this.GlobalSession = session;
        System.out.println("\n###############\nopenRoutine]session.getOpenSessions().size = " + session.getOpenSessions().size());
        /*
        Controllo se non ho già in memoria un client con lo stesso clientID e SessionID
         */
        int flag = 0;
        for (int jj = 0; jj < this.getClientsConnected().size(); jj++) {
            if (this.getClientsConnected().get(jj).clientID.equals(clientId)
                    && this.getClientsConnected().get(jj).getSessionID().equals(session.getId())) {
                flag = 1;
                break;
            }
        }

        if (flag > 0) {
            System.out.println("<" + this.handlerID + ">CHIAMATA DA UN CLIENT GAI' REGISTRATO " + session.getId() + " ->" + clientId);
        } else {
            System.out.println("<" + this.handlerID + ">CHIAMATA DA UN CLIENT NON REGISTRATO " + session.getId() + " ->" + clientId);
            // Nel caso di un browser il field clientID nella tabella archivio_sessions sarà qialcosa del tipo :
            //SESS-3c8-ws://localhost:8080/lucifer/WS/f111a13f-fb54-4734-9fd4-069ce2574ee4
            // dove l'ulima parte corrisponde al clientId che ho qui nella routine
            // quindi se nel DB il clientID LIKE "SESS%[clientId]" allora devo leggere il campo token e successivasmente
            //alla creazione della nuova session sostituire il token di rifermiento in tutte le subscriptions che usavano il token precedente
            String SQLphrase;
            PreparedStatement ps;
            ResultSet rs;
            Connection conny = new EVOpagerDBconnection(mySettings).ConnLocalDMZ();

// un client non ancora registrato sta provando a comunicare con il server.
// prima di dargli l'ok e poi ricevere l'handshake potrei effettuare un primo controllo. Come dato avrei solamente il nome del device (clientId)
            UUID idOne = null;
            idOne = UUID.randomUUID();
            String newToken = "" + idOne;
            WSclient myClient = new WSclient(clientId, session.getId(), newToken);
            myClient.setStatus("REQUEST");
            long mo = System.currentTimeMillis();
            myClient.setLastMessageRecorded(mo);

//********************************************************************************************************
//****recupero subscription precedenti********************************************************************
            String prevToken = "";
            SQLphrase = "SELECT * FROM archivio_sessions WHERE clientID LIKE 'SESS%" + clientId + "'";
            try {
                ps = conny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();
                while (rs.next()) {
                    prevToken = rs.getString("token");
                    break;
                }
            } catch (SQLException ex) {
                Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            SQLphrase = "UPDATE archivio_sessionsSubscriptions SET  rifSessionToken ='" + newToken + "' WHERE rifSessionToken = '" + prevToken + "' ";
            try {
                ps = conny.prepareStatement(SQLphrase);
                int i = ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
            }
//********************************************************************************************************               

//------AGGIUNGO IL PEER----------------------------------------------------------------------------
            this.peers.add(session);
//            System.out.println("@OnOpen] peers.size = " + this.peers.size());

//            System.out.println("<" + this.handlerID + ">@OnOpen]" + session.getId() + " >>>>Prima dell'inserimento i clientsConnecte sono " + this.getClientsConnected().size());
            this.getClientsConnected().add(myClient);
//            System.out.println("<" + this.handlerID + ">@OnOpen]" + session.getId() + " >>>>Dopo l'inserimento i clientsConnected sono " + this.getClientsConnected().size());
            this.aggiornaSessioniValide();
//            System.out.println("<" + this.handlerID + ">@OnOpen]" + session.getId() + " >>>>DOPO LA VALIDAZIONE i clientsConnected sono " + this.getClientsConnected().size());

//System.out.println("============================");
            String message = "<" + this.handlerID + ">APP-WS: INSERITO CLIENT : SESSION:" + session.getId()
                    + ", CLIENT:" + clientId + ",TOKEN:" + newToken; //(es. 15) 
//            System.out.println(message);
            /*       System.out.println("allSessions.size()=" + allSessions.size());
         System.out.println("peers.size()=" + peers.size());
         System.out.println("clientsConnected.size()=" + clientsConnected.size());
         System.out.println("============================");
             */
            // System.out.println(message);
            // el.log(PROJECT_ID, message);
            //sendMessageToAll(session, message);
            JSONObject clientPoint = new JSONObject();
            clientPoint.put("clientId", clientId);
            clientPoint.put("sessionID", session.getId());
            clientPoint.put("newToken", newToken);
//            System.out.println("<" + this.handlerID + ">@OnOpen]" + session.getId() + " >>>>tokenAssign-->" + newToken);

//============ASSEGNO IL TOKEN AL DESTINATARIO=========================
            this.sendToPeer("tokenAssign", clientPoint, session.getId(), message);
//============ASSEGNO IL TOKEN AL DESTINATARIO=========================        

            //broadcast(session, "message", "000", message);
//        this.printPeers();
//        this.printClientsConnected();
        }
        this.attempt = 0;
    }

    public void parseClose(Session session) {
        for (Session sess : peers) {
            if (session.getId() == sess.getId()) {
                parseClose(session.getId());// rimuove il peer non raggiungibile 
                break;
            }
        }

    }

    //onClose
    public void parseClose(String sessionIDtoRemove) {
        // String sessionIDtoRemove = session.getId();
        int clientConnessiPrima = clientsConnected.size();
        this.attempt = 0;
        System.out.println("\n\n----------------\n-RIMUOVO : " + sessionIDtoRemove);
//        myWShandler.allSessions.remove(session);
        Session session = null;
        for (Session peerSsession : peers) {
            if (peerSsession.getId().equals(sessionIDtoRemove)) {
                this.peers.remove(peerSsession);
                session = peerSsession;
                break;
            }

        }
        for (int jj = 0; jj < clientsConnected.size(); jj++) {
            if (clientsConnected.get(jj).getSessionID().equals(sessionIDtoRemove)) {
                System.out.println("@onClose]" + sessionIDtoRemove + " Rimuovendo >>>>" + clientsConnected.get(jj).getSessionID());
                System.out.println(" Rimuovendo getTokenAssigned>>>>" + clientsConnected.get(jj).getTokenAssigned());
                System.out.println(" Rimuovendo getToken>>>>" + clientsConnected.get(jj).getToken());
                System.out.println(" Rimuovendo getClientIP>>>>" + clientsConnected.get(jj).getClientIP());
                System.out.println(" Rimuovendo getClientID>>>>" + clientsConnected.get(jj).getClientID());
                if (clientsConnected.get(jj) != null && !clientsConnected.get(jj).getClientType().equalsIgnoreCase("BROWSER")) {
                    // imposto la enrolled device corrispondente come offline
                    setDBenrolledDeviceStatus(clientsConnected.get(jj).getClientID(), 0, "lastTouch");
                }
//                removeDBtokenSession(clientsConnected.get(jj).getTokenAssigned());
                clientsConnected.remove(jj);
                this.onLostClient(session);// invia ai peers la nuova mappa
                break;
            }
        }
        int clientConnessiDopo = clientsConnected.size();
        System.out.println("clientConnessiPrima: " + clientConnessiPrima + " DOPO:" + clientConnessiDopo);
        for (int jj = 0; jj < clientsConnected.size(); jj++) {
            System.out.println(jj + ":> " + clientsConnected.get(jj).getSessionID());
        }
        this.aggiornaSessioniValide();
    }

    public void setDBenrolledDeviceStatus(String clientID, int value, String type) {
        String logType = "lastTouch";
        if (type.equalsIgnoreCase("login")) {
            logType = "login";
        }
        String SQLphrase;
        PreparedStatement ps;
        ResultSet rs;
        Connection conny = new EVOpagerDBconnection(mySettings).ConnLocalDMZ();
        int prevValue = -1;
        SQLphrase = "SELECT * FROM  enrolledDevices WHERE name = '" + clientID + "'";
        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                prevValue = rs.getInt("connected");
            }
        } catch (SQLException ex) {
            Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
        }
//        System.out.println("## >prevValue: " + prevValue + "    >value: " + value);
        if (logType.equalsIgnoreCase("login")) {
            SQLphrase = "UPDATE  enrolledDevices  SET  connected = " + value + ", connDate = NOW(), lastTouch = NOW() WHERE name = '" + clientID + "'";
        } else {
            SQLphrase = "UPDATE  enrolledDevices  SET  connected = " + value + ", lastTouch = NOW() WHERE name = '" + clientID + "'";
        }
//        System.out.println("## >SQLphrase: " + SQLphrase);
        try {
            ps = conny.prepareStatement(SQLphrase);
            int i = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        String tabRowID = "";
        if (prevValue != value) {
            try {
                SQLphrase = "SELECT ID FROM enrolledDevices WHERE name = '" + clientID + "'";
                ps = conny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabRowID = rs.getString("ID");
                }
            } catch (SQLException ex) {
                Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            //il broadcast riguarda solo i target di tipo browser perchè coinvolge la creazione di una intera riga di tabella
            System.out.println("\n\n\n ATTENZIONE!!! PARTE IL BROADCAST (SU BROWSER) DI UNA INFORMAZIONE SOTTOSCRIVIBILE!!!  ->" + clientID);
            System.out.println("\n\n\n================================================================================================= ");
            System.out.println("\n\n\n================================================================================================= ");
            System.out.println("\n\n\n================================================================================================= ");
            broadcastSubscriptions_typeDB(conny, "enrolledDevices", "connected", "name", true, clientID, tabRowID);
            System.out.println("\n\n\n ");

        }
        try {
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
////////    public void removeDBtokenSession(String tokenToRemove) {
////////        String SQLphrase;
////////        PreparedStatement ps;
////////        ResultSet rs;
////////        EVOpagerParams myParams = new EVOpagerParams();
////////        for (int jj = 0; jj < clientsConnected.size(); jj++) {
////////            myParams = clientsConnected.get(jj).getClientParams();
////////            break;
////////        }
////////        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
////////        SQLphrase = "DELETE FROM " + mySettings.getAccount_TABLEtokens() + " WHERE  token ='" + tokenToRemove + "'";
////////        System.out.println(":>SQLphrase: " + SQLphrase);
////////        try {
////////            ps = conny.prepareStatement(SQLphrase);
////////            int i = ps.executeUpdate();
////////        } catch (SQLException ex) {
////////            Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
////////        }
////////
////////    }

    public void removeBadGuests() {
        // NE APPROFITTO PER VERIFICARE SE CI SONO UTENTI NON CONFERMATI DA ELIMINARE
        String SQLphrase;
        PreparedStatement ps;
        ResultSet rs;
        EVOpagerParams myParams = new EVOpagerParams();
//        for (int jj = 0; jj < clientsConnected.size(); jj++) {
//            myParams = clientsConnected.get(jj).getClientParams();
//            break;
//        }
//        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
        SQLphrase = "SELECT * FROM  archivio_operatori  WHERE alive < -10";

//        System.out.println("\n\nremoveBadGuests-->" + this.handlerID + ">SQLphrase:" + SQLphrase);
        String DBrecorded = "";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
        Date recordedDate = null;
        Calendar cal = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.setTime(cal.getTime());
        try {
            ps = accountConny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                EVOuser myUser = new EVOuser(myParams, mySettings);
                myUser.setEmail(rs.getString("email"));
                myUser.setUserID(rs.getString(mySettings.getAccount_FIELDoperatoriID()));
                DBrecorded = rs.getString("recorded");
                try {
                    recordedDate = format.parse(DBrecorded);
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(recordedDate);
                    long minuti = (c2.getTime().getTime() - c1.getTime().getTime()) / (60 * 1000);
                    if (minuti > 30) {
                        myUser.removeRegistrationByIDandMail(accountConny);
                        System.out.println("ELIMINO UTENTE PER MANCATA CONFERMA: " + myUser.getEmail() + " ID:" + myUser.getUserID());

                    } else {
                        System.out.println(" UTENTE: " + myUser.getEmail() + " ID:" + myUser.getUserID() + " IN ATTESA DI CONFERMA.");

                    }
                } catch (java.text.ParseException ex) {
                }

            }
            accountConny.close();
        } catch (SQLException ex) {
        }

    }

    public String executeCommand(JSONObject params) {
        String result = "NOP";
        result = "Client connessi: " + clientsConnected.size();
        System.out.println("params--->" + params.toString());
        String targetToken = getJSONarg(params, "deviceToken");
        String targetID = getJSONarg(params, "deviceID");
        String targetPhrase = getJSONarg(params, "devicePhrase");
        String targetUser = getJSONarg(params, "deviceUsername");
        String targetPassword = getJSONarg(params, "devicePassword");

        System.out.println("sono in WShandler->executeCommand--->" + result);
        for (int jj = 0; jj < clientsConnected.size(); jj++) {
            System.out.println("\nclientID--->" + clientsConnected.get(jj).clientID + " clientType--->" + clientsConnected.get(jj).clientType
                    + "  tokenAssigned--->" + clientsConnected.get(jj).tokenAssigned + " token--->" + clientsConnected.get(jj).token);
            System.out.println("clientModel--->" + clientsConnected.get(jj).clientModel + " localUserID--->" + clientsConnected.get(jj).userID);
            System.out.println("status--->" + clientsConnected.get(jj).status + " userPassword--->" + clientsConnected.get(jj).userPassword);

            if (clientsConnected.get(jj).tokenAssigned.equalsIgnoreCase(targetToken)
                    && clientsConnected.get(jj).clientID.equalsIgnoreCase(targetID)
                    && clientsConnected.get(jj).userID.equalsIgnoreCase(targetUser)
                    && clientsConnected.get(jj).userPassword.equalsIgnoreCase(targetPassword)) {
                System.out.println("TROVATO CLIENT WEBSOCKET A CUI INVIARFE IL COMANDO !");
                int responseValue = 0;
                try {
                    sendToPeer("executeCommand", params, clientsConnected.get(jj).sessionID, targetPhrase);
                    responseValue = 1;
                } catch (Exception e) {
                    responseValue = -1;

                    System.out.println("Errore in invio:  " + clientsConnected.get(jj).sessionID);
                }

            }

        }
        return result;
    }

    public void manageSoftwareUpdate(Session session) throws MalformedURLException, IOException {
//        if (true) {
//            return;
//        }
        this.aggiornaSessioniValide();
        broadcast(session, "updateTest", "000", "orange", "Verifica update");
        semaphore mySem = new semaphore();
        String backMessage = "update done.";
        String backCode = "green";

//1. verifico la last version nelle mie direttive
        EVOpagerParams myParams = new EVOpagerParams();
        for (int jj = 0; jj < clientsConnected.size(); jj++) {
            myParams = clientsConnected.get(jj).getClientParams();
            break;
        }
        EVOsetup mySetup = new EVOsetup(myParams, mySettings);
        mySem = mySetup.doUpdate();
        broadcast(session, "updateTest", "000", mySem.getColor(), mySem.getTooltipText());

    }

    public void manageBackup(Session session) {
//        if (true) {
//            return;
//        }

        this.aggiornaSessioniValide();
        broadcast(session, "backupTest", "000", "orange", "Verifica backup");

//            ProjectSettings pSettings = new ProjectSettings();
//            Settings mySettings = pSettings.getSettings();
        String backMessage = "";
        String backCode = "";
        for (int jj = 0; jj < this.contesti.size(); jj++) {
            System.out.println("CONTESTO " + jj + ") " + this.contesti.get(jj).getContextID());
            EVOpagerParams myParams = new EVOpagerParams();
            myParams.setCKcontextID(this.contesti.get(jj).getContextID());
            semaphore mySem = new semaphore();

            try {
//                    backMessage = verifyBackup(myParams);
                mySem = verifyBackup(myParams);

                if (mySem.getValue() == 1) {

                    System.out.println("Esigenza di backup: POSITIVA !");
                    backupping = true;
                    try {
                        EVOsetup mySetup = new EVOsetup(myParams, mySettings);
                        System.out.println("Creato mySetup");
                        mySem = mySetup.doBackup();
                        if (mySem.getValue() > 0) {
                            backMessage = "backup ok.";
                            backCode = "green";
                        } else {
                            backMessage = "backup failed.";
                            mySem.setTooltipText("backup failed.");
                            backCode = "red";
                        }
                    } catch (Exception e) {
//                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "EVO catcher : errore nella fase di backup.\n");
                        backMessage = "backup failed.";
                        mySem.setTooltipText("backup failed.");
                        backCode = "red";
                    }
                } else if (mySem.getValue() == -1) {
                    backMessage = "backup disabled.";
                    mySem.setTooltipText("backup disabled.");
                    backCode = "disabled";
                } else {
                    System.out.println("Esigenza di backup: NEGATIVA !");
                    backMessage = "it's not time for backup.";
                    backCode = "green";
                }

            } catch (Exception e) {
                backMessage = "backup failed.";
                mySem.setTooltipText("backup failed.");
                backCode = "red";
            }
            System.out.println("backupTest message:" + backMessage);
            broadcast(session, "backupTest", "000", backCode, mySem.getTooltipText());
            backupping = false;
        }

    }

    public semaphore verifyBackup(EVOpagerParams myParams) {
        semaphore mySem = new semaphore();
        int backCode = 0;
        String backMessage = "";
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
        String lastBackupDate = myManager.getEvoDirective("lastBackupDate");
        mySem.setTooltipText("Ultimo backup:" + lastBackupDate);
        String backupInterval = myManager.getDirective("backupInterval");
        if (lastBackupDate == null || lastBackupDate.length() < 10) {
            lastBackupDate = "1900-01-01 00:00:00";
        }
        Calendar cal = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
        Date recordedDate = null;
        try {
            recordedDate = format.parse(lastBackupDate);
        } catch (java.text.ParseException ex) {
            try {
                recordedDate = format.parse("1900-01-01 00:00:00");

            } catch (java.text.ParseException ex1) {
                Logger.getLogger(EVOsetup.class
                        .getName()).log(Level.SEVERE, null, ex1);
            }
        }
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(recordedDate);
        } catch (Exception e) {

        }

        try {
            c2.setTime(cal.getTime());
        } catch (Exception e) {

        }
        long minuti = (c2.getTime().getTime() - c1.getTime().getTime()) / (60 * 1000);

        int interval = 1440; // 1 giorno

        try {
            interval = Integer.parseInt(backupInterval);
        } catch (Exception e) {
            interval = 1440;
        }
        String BackupRequested = myManager.getDirective("backupRequested");

        if (minuti > interval) {
            if (BackupRequested != null && BackupRequested.equalsIgnoreCase("true")) {
                backCode = 1;
            } else {
                backCode = -1;
            }

        } else {
            if (BackupRequested != null && BackupRequested.equalsIgnoreCase("true")) {
                backCode = 0;
            } else {
                backCode = -1;
            }
        }
        mySem.setValue(backCode);
        return mySem;
    }

    public void sendToBrowser(String type, JSONObject payload, String token, String message) {
        if (token != null && token.length() > 0) {
            String destinationID = getClientIDbyToken(token);
            String senderName = "***QUEENPRO SERVER***";
//            System.out.println("sendToBrowser->token: " + token + " sessionID:" + destinationID);
            try {
//            el.log(PROJECT_ID, "APP-WS: sendToPeer from " + senderName + " to " + sess.getId() + ":" + message);
                JSONObject obj = new JSONObject();
                obj.put("ip", "000");
                obj.put("SENDER", senderName);
                obj.put("TYPE", type);
                obj.put("VALUE", message);//descrizione...i9nserito client ecc...(per speech)
                obj.put("payload", payload);// contiene clientPoint:oggetto JSON con clientID, sessionID e token appena assegnato
                obj.put("priority", "0");
                obj.put("duration", "3");
                for (Session sess : peers) {
                    if (destinationID.equals(sess.getId())) {
//                        System.out.println("INVIO AL PEER " + destinationID + " messaggio TYPE:" + type + " con payload=" + payload);
                        try {
                            sess.getBasicRemote().sendText(obj.toJSONString());
                        } catch (IOException ioe) {
                            try {
                                sleep(10);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                sess.getBasicRemote().sendText(obj.toJSONString());
                            } catch (IOException ie) {
//                        System.out.println(ie.getMessage());
                                parseClose(destinationID);// rimuove il peer non raggiungibile
                            }
                            parseClose(destinationID);// rimuove il peer non raggiungibile
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int sendToPeer(String type, JSONObject payload, String targetSessionID, String message) {
//        System.out.println("sendToPeer... " + targetSessionID);
        int esito = -1;
        String senderName = mySettings.getProjectName();
//            el.log(PROJECT_ID, "APP-WS: sendToPeer from " + senderName + " to " + sess.getId() + ":" + message);
        JSONObject obj = new JSONObject();
        obj.put("ip", "000");
        obj.put("SENDER", senderName);
        obj.put("TYPE", type);
        obj.put("VALUE", message);//descrizione...i9nserito client ecc...(per speech)
        obj.put("payload", payload);// contiene clientPoint:oggetto JSON con clientID, sessionID e token appena assegnato
        obj.put("priority", "0");
        obj.put("duration", "3");

        for (Session sess : peers) {

            if (sess.getId() != null && sess.getId().equals(targetSessionID)) {
//                System.out.println("found sess.getId: " + sess.getId() + " while searching:" + targetSessionID);
//                System.out.println("INVIO AL PEER " + destinationID + " messaggio TYPE:" + type + " con payload=" + payload);
                try {
                    sess.getBasicRemote().sendText(obj.toJSONString());
                    esito = 1;
                } catch (IOException ioe) {
                    try {
                        sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        sess.getBasicRemote().sendText(obj.toJSONString());
                    } catch (IOException ie) {
//                        System.out.println(ie.getMessage());
                        parseClose(targetSessionID);// rimuove il peer non raggiungibile
                    }
                    parseClose(targetSessionID);// rimuove il peer non raggiungibile
                }
                break;
            }
        }
        return esito;
    }
//noMessage

    public void sendToPeerByName(String destination, String type, JSONObject payload) {
        String senderName = "***GAIAWEB SERVER***";
//            el.log(PROJECT_ID, "APP-WS: sendToPeer from " + senderName + " to " + sess.getId() + ":" + message);
        JSONObject obj = new JSONObject();
        obj.put("ip", "000");
        obj.put("SENDER", senderName);
        obj.put("TYPE", type);
        obj.put("payload", payload);// contiene clientPoint:oggetto JSON con clientID, sessionID e token appena assegnato
        obj.put("priority", "0");
        obj.put("duration", "3");
        String destinationID = "";
        String tokenToUse = "";
        String userToUse = "";
        String passwordToUse = "";
        for (WSclient client : clientsConnected) {
            if (client.clientID.equalsIgnoreCase(destination)) {
                destinationID = client.getSessionID();
                tokenToUse = client.tokenAssigned;
                userToUse = client.getUserID();
                passwordToUse = client.getUserPassword();
            }
        }
        for (Session sess : peers) {
            if (destinationID == null ? sess.getId() == null : destinationID.equals(sess.getId())) {
//                System.out.println("INVIO AL PEER " + destinationID + " messaggio TYPE:" + type + " con payload=" + payload);
                try {
                    sess.getBasicRemote().sendText(obj.toJSONString());
                } catch (IOException ioe) {
                    try {
                        sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        sess.getBasicRemote().sendText(obj.toJSONString());
                    } catch (IOException ie) {
//                        System.out.println(ie.getMessage());
                        parseClose(destinationID);// rimuove il peer non raggiungibile
                    }
                    parseClose(destinationID);// rimuove il peer non raggiungibile
                }
                break;
            }
        }
    }
//noMessage

    public void broadcastSubscriptions_typeDB(Connection conny, String BCformName, String BCfieldName, String keyField, boolean isString, String keyValue, String tabRowID) {
// devo creare l'oggetto finito da mandare al mio client per aggiornare il contenuto
// oggetto tipo LABEL
        System.out.println("## >BROADCAST su FORM:" + BCformName + " per FIELD: " + keyField + " CON VALUE: " + tabRowID);
        JSONParser jsonParser = new JSONParser();
        JSONParser jParser = new JSONParser();
        String formID = "";
        gate myGate = new gate();
        formID = myGate.formIDfromName(mySettings, BCformName);
//        System.out.println("\n\n\nbroadcastSubscriptions_typeDB-->DEVO CONTROLLARE le subscriptions del form '" + BCformName + "' che corrisponde all'ID: " + formID);
//        System.out.println("CERCO UN FIELD CON NOME '" + BCfieldName + "' nella riga con campo '" + keyField + "'='" + keyValue + "' ");
        //se ho un subscriber che interroga il form indicato e il field indicato allora parte la creazione dell'object e poi l'invio al browser
        String SQLphrase;
        PreparedStatement ps;
        ResultSet rs;
        SQLphrase = "SELECT archivio_sessionsSubscriptions.*,archivio_sessions.sessionID,archivio_sessions.clientID  FROM archivio_sessionsSubscriptions "
                + "LEFT JOIN archivio_sessions ON archivio_sessions.token = archivio_sessionsSubscriptions.rifSessionToken "
                + "WHERE infoSourceType = 'FORMOBJECT' AND info1='" + formID + "' AND clientID IS NOT NULL";

        System.out.println("## >SQLphrase: " + SQLphrase);
        String txtObjects = "";
        String txtSubscriber = "";
        String rifSessionToken = "";
        String txtPortalParams = "";
        String sessionID = "";
        String clientID = "";
        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            String objCode = "";
            while (rs.next()) {// per tutte le sessions valide (i browser connessi)
                txtObjects = rs.getString("objects");
                txtSubscriber = rs.getString("subscriber");
                rifSessionToken = rs.getString("rifSessionToken");
                txtPortalParams = rs.getString("myParams");
                sessionID = rs.getString("sessionID");
                clientID = rs.getString("clientID");
//                System.out.println("## >txtObjects: " + txtObjects);
//                System.out.println("## >txtSubscriber: " + txtSubscriber);
//                System.out.println("## >rifSessionToken: " + rifSessionToken);
//                System.out.println("## >txtPortalParams: " + txtPortalParams);
//                System.out.println("## >sessionID: " + sessionID);
//                System.out.println("## >clientID: " + clientID);
                try {
                    if (clientID.contains("/")) {
                        int p = 1 + clientID.lastIndexOf("/");
                        clientID = clientID.substring(p);
                    }
                } catch (Exception e) {
                }
//                System.out.println("## >clientID (token): " + clientID);
                if (txtObjects != null && txtSubscriber != null) {
                    if (txtObjects.length() > 2) {
                        JSONArray array;
                        try {
                            array = (JSONArray) jsonParser.parse(txtObjects);
                            for (Object riga : array) {
                                JSONObject row = (JSONObject) riga;
//                                System.out.println("riga in Objects: " + row.toString());
                                String field = getJSONarg(row, "objectName");
                                if (field != null && field.equalsIgnoreCase(BCfieldName)) {
                                    if (objCode.length() < 1) {
                                        System.out.println("TROVATO UN CLIENT BROWSER IDONEO:" + clientID);
                                        // devo trasmettere a questo client l'oggetto 
                                        // userò le informazioni in "subscriber" per ricostruire il form e creare l'oggetto
                                        // poi con le informzaioni del token mando l'oggetto al client giusto.

                                        objCode = buildSynopticObject(txtPortalParams, txtSubscriber, BCfieldName, keyField, isString, keyValue, tabRowID);
                                    }
                                    // procedo all'invio
//                                    System.out.println("## >objCode: " + objCode);// puro codice html da impacchettare per l'invio
                                    ////-----------
//                                    gate myGate = new gate();
//                myGate.connector2gate(JsonConnector);
//txtPortalParams = {"GRP":"","USR":"admin","TKN":"95e7b092-dd2f-48f3-a28e-30ad66542c33","CNT":"ms","PRJ":"lucifer"}
                                    // creo myParams da sender
                                    JSONObject jparams = (JSONObject) jParser.parse(txtPortalParams);
                                    EVOpagerParams myParams = new EVOpagerParams();
                                    myParams.setCKcontextID(getJSONarg(jparams, "CNT"));
                                    myParams.setCKprojectGroup(getJSONarg(jparams, "GRP"));
                                    myParams.setCKuserID(getJSONarg(jparams, "USR"));
                                    myParams.setCKtokenID(getJSONarg(jparams, "TKN"));
                                    myParams.setCKprojectName(getJSONarg(jparams, "PRJ"));
                                    JSONObject action = new JSONObject();
                                    action.put("action", "synopticObj");
                                    action.put("target", formID + "-" + "X" + "-" + BCfieldName + "-" + tabRowID + "-PLACE");// spazio destDiv da refreshare
                                    action.put("htmlCode", objCode);// codice html
                                    //myGate.insertAction_synopticObject(myParams, mySettings, formID, "X", BCfieldName, keyValue, objCode);
                                    //JSONObject jObj = myGate.insertAction_packResponse(myParams, mySettings,"wsResponse","");
                                    // sendToBrowser(String type, JSONObject payload, String token, String message)
//                                    System.out.println("Procedo con l'invio di un messaggio synopticObject verso clientID:" + clientID);
                                    String destinationID = getClientIDbyToken(clientID);
//                                    sendToBrowser("wsResponse", jObj, clientID, "");
                                    sendToPeer("SYNOPTICOBJ", action, destinationID, "");
                                    ////-----------
                                    break;// esco dal for
                                }
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(WShandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String buildSynopticObject(String portalParams, String txtSubscriber, String fieldName, String keyField, boolean isString, String keyValue, String tabRowID) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        String HtmlCode = "";
        String params = "";
        JSONObject subscriber = (JSONObject) jsonParser.parse(txtSubscriber);
        String formID = getJSONarg(subscriber, "formID");
        String copyTag = getJSONarg(subscriber, "copyTag");
        String rifObj = getJSONarg(subscriber, "rifObj");
        //String keyValue = getJSONarg(subscriber, "keyValue");
        //String cellName = getJSONarg(subscriber, "cellName");
        String cellName = fieldName;
        String newValue = getJSONarg(subscriber, "newValue");
        String fatherKEYvalue = getJSONarg(subscriber, "fatherKEYvalue");
        String fatherKEYtype = getJSONarg(subscriber, "fatherKEYtype");
        String fatherForm = getJSONarg(subscriber, "fatherForm");
        String fatherCopyTag = getJSONarg(subscriber, "fatherCopyTag");
        String tbs = getJSONarg(subscriber, "tbs");
        String STC = getJSONarg(subscriber, "STC");
        String rowToRepaint = formID + "-" + copyTag + "-" + tabRowID + "-ROW";

        JSONObject pParams = (JSONObject) jsonParser.parse(portalParams);
        EVOpagerParams myParams = new EVOpagerParams();
        myParams.setCKprojectName(getJSONarg(pParams, "PRJ"));
        myParams.setCKcontextID(getJSONarg(pParams, "CNT"));
        myParams.setCKtokenID(getJSONarg(pParams, "TKN"));
        myParams.setCKprojectGroup(getJSONarg(pParams, "GRP"));
        myParams.setCKuserID(getJSONarg(pParams, "USR"));

        smartForm mySmartForm = new smartForm(formID, myParams, mySettings);
        mySmartForm.setID(formID);
        mySmartForm.setType("SMARTTABLE");
        mySmartForm.setFatherKEYvalue(fatherKEYvalue);
        mySmartForm.setFatherKEYtype(fatherKEYtype);
        mySmartForm.setFather(fatherForm);
        mySmartForm.setFatherCopyTag(fatherCopyTag);
        mySmartForm.setInfoReceived(tbs);
        mySmartForm.setCopyTag(copyTag);
        mySmartForm.setSendToCRUD(STC);
        mySmartForm.setLoadType("{\"type\":\"SMARTTABLE\",\"visualType\":\"SINGLEROW\"}");
//        System.out.println("Costruito smartForm... vado in buildSchema");
        mySmartForm.loadPagingInstructions();
        mySmartForm.buildSchema();
        String keyFieldName = mySmartForm.getKEYfieldName();
        String keyFieldType = mySmartForm.getKEYfieldType();
        String myQuery = mySmartForm.getQueryUsed();
        System.out.println("mySmartForm.keyFieldType:" + keyFieldType);
        System.out.println("mySmartForm.keyFieldName:" + keyFieldName);
        System.out.println("mySmartForm.myQuery:" + myQuery);
////        smartQuery mySquery = new smartQuery(myQuery, "", "");
////        String sQuery = mySquery.regenerateQuery(keyField + " = '" + keyValue + "'", "", "", false, " AND", false, false);
////        if (isString == false) {
////            sQuery = mySquery.regenerateQuery(keyField + " = " + keyValue + " ", "", "", false, " AND", false, false);
////        }
////        System.out.println("mySmartForm.sQuery:" + sQuery);
//        System.out.println("mySmartForm.getName:" + mySmartForm.getName());
//        System.out.println("Concluso buildSchema. verifico presenza routine onn change per oggetto groupchecker");
        String AfterProcessByObjectRoutine = "";
        for (int kk = 0; kk < mySmartForm.getObjects().size(); kk++) {
            if (mySmartForm.getObjects().get(kk).getName().equalsIgnoreCase(fieldName)) {
                params = mySmartForm.getObjects().get(kk).CG.getParams();
                AfterProcessByObjectRoutine = mySmartForm.getObjects().get(kk).routineOnChange;
            }
        }
        // adesso devo ridisegnare la row a cui appartiene il targertElement
        System.out.println("rowToRepaint ---------->" + rowToRepaint);

        mySmartForm.setCurKEYvalue(tabRowID);
        System.out.println("Query used default: " + mySmartForm.getQueryUsed());
        smartForm.smartFormResponse myFormResponse = mySmartForm.paintSingleObject(fieldName);
        HtmlCode = myFormResponse.getHtmlCode();
        HtmlCode = encodeURIComponent(HtmlCode);

        return HtmlCode;
    }

    public void popolaContextList() {
//        System.out.println("#popolaContextList I TOKEN VALIDI IN RAM SONO:" + clientsConnected.size());

        // popolo la lista dei context validi per  eseguire su ognuno le operazioni di rito (es. backup)
//        System.out.println("#popolaContextList (clientsConnected=" + clientsConnected.size());
        contesti = new ArrayList();
        int num = 0;
        for (int jj = 0; jj < clientsConnected.size(); jj++) {
            if (clientsConnected.get(jj).isHandshaked()) {
                String cont = clientsConnected.get(jj).getClientParams().getCKcontextID();
                if (cont == null) {
                    cont = "";
                    clientsConnected.get(jj).getClientParams().setCKcontextID("");
                }
                int flag = 0;
                for (int kk = 0; kk < contesti.size(); kk++) {
                    if (contesti.get(kk).getContextID().equalsIgnoreCase(cont)) {
                        flag++;
                        break;
                    }
                }
                if (flag == 0) {
                    num++;
                    WScontext myContext = new WScontext();
                    myContext.setContextID(cont);
                    contesti.add(myContext);
//                    System.out.println("AGGIUNGO CONTEXT:" + num + ") -> _" + cont + "_");
                }
            }
        }
    }

    public void aggiornaSessioniValide() {
//        System.out.println("#aggiornaSessioniValide I TOKEN VALIDI IN RAM SONO:" + clientsConnected.size());

        // verificare se si caRICA L'ARRAY CORRETTAMENTE
        popolaContextList();
//        System.out.println("\n@@@@@@@@@@@@@@@@@\n<" + this.handlerID + ">AggiornaSessioniValide. CONTESTI:" + contesti.size());

        Connection accountConny;
//        printPeers();
//        System.out.println("\n");
//        printClientsConnected();
        for (int jj = 0; jj < contesti.size(); jj++) {
            System.out.println("\n<" + this.handlerID + ">CONTESTO:" + contesti.get(jj).getContextID());

            if (contesti.get(jj).getContextID() != null) {

                EVOpagerParams tempParams = new EVOpagerParams();
                tempParams.setCKcontextID(contesti.get(jj).getContextID());
                tempParams.setCKprojectGroup("");
                tempParams.setCKprojectName(mySettings.getProjectName());
//                System.out.println("tempParams getCKprojectName:" + tempParams.getCKprojectName());
//                System.out.println("tempParams getCKcontextID:" + tempParams.getCKcontextID());
                accountConny = new EVOpagerDBconnection(tempParams, mySettings).ConnAccountDB();
                //prima di inserire una sessione cancello tutte le sessioni esistenti con lo stesso CKtoken (clientID)
                PreparedStatement ps;
                String SQLphrase;
                ResultSet rs = null;

                ArrayList<String> toErase = new ArrayList();
                ArrayList<String> toUpdate = new ArrayList();
//                System.out.println("<" + this.handlerID + ">I TOKEN VALIDI IN RAM SONO:" + clientsConnected.size());
                for (int kk = 0; kk < clientsConnected.size(); kk++) {
                    clientsConnected.get(kk).setTempFound(0);
//                    System.out.println(kk + ". TOKEN: " + clientsConnected.get(kk).tokenAssigned
//                            + " - TYPE: " + clientsConnected.get(kk).clientType
//                            + " - USER: " + clientsConnected.get(kk).user
//                            + " - SESSION: " + clientsConnected.get(kk).sessionID
//                            + " - CLIENT: " + clientsConnected.get(kk).clientID
//                    );
                }

//                System.out.println("<" + this.handlerID + ">SESSIONI SALVATE SU DATABASE:");
                SQLphrase = "SELECT * FROM archivio_sessions WHERE 1 ";
                try {
//                    System.out.println("-SQLphrase" + SQLphrase);
                    ps = accountConny.prepareStatement(SQLphrase);
                    rs = ps.executeQuery();
                    int line = 0;
                    while (rs.next()) {
                        line++;
//                        String Session = rs.getString("sessionID");
//                        String Client = rs.getString("clientID");
                        String dbToken = rs.getString("token");
//                        String User = rs.getString("user");
//                        System.out.println("" + line + " . User: " + User + " . Client: " + dbToken + ". Token: " + dbToken);
                        int found = 0;
                        for (int kk = 0; kk < clientsConnected.size(); kk++) {
                            if (dbToken.equals(clientsConnected.get(kk).tokenAssigned)) {
                                found++;
                                clientsConnected.get(kk).setTempFound(1);
                                break;
                            }
                        }
                        if (found == 0) {
                            toErase.add(dbToken);// cìè in DB ma non in RAM
                            System.out.println("toErase: " + dbToken);
                        } else {
                            toUpdate.add(dbToken);// c'è in RAM e IN DB
//                            System.out.println("toUpdate: " + dbToken);
                        }

                    }

                } catch (SQLException ex) {
                    System.out.println("ERROR1199. " + ex.toString());
                }
//-----------------------------------------------------------------

//-------------------------------------------------
                try {
                    for (int xx = 0; xx < toErase.size(); xx++) {
//                        System.out.println("ELIMINO DAL DATABASE " + toErase.get(xx));
                        SQLphrase = "DELETE FROM archivio_sessions WHERE token = '" + toErase.get(xx) + "' ";
                        ps = accountConny.prepareStatement(SQLphrase);
                        int i = ps.executeUpdate();

                        try {
                            SQLphrase = "DELETE FROM archivio_sessionsSubscriptions WHERE rifSessionToken ='" + toErase.get(xx) + "'";
                            ps = accountConny.prepareStatement(SQLphrase);
                            i = ps.executeUpdate();

                        } catch (SQLException ex) {
                        }

                    }
                } catch (SQLException ex) {
                    System.out.println("ERROR. " + ex.toString());
                }
//-----------------------------------------------------------------
                for (int xx = 0; xx < toUpdate.size(); xx++) {
                    for (int kk = 0; kk < clientsConnected.size(); kk++) {
                        if (toUpdate.get(xx).equals(clientsConnected.get(kk))) {
//                            System.out.println("AGGIORNO LASTTOUCH SU DATABASE " + toUpdate.get(xx));
                            WSclient myClient = clientsConnected.get(kk);
                            updateClientOnDB(myClient, accountConny, toUpdate.get(xx));
                            break;
                        }
                    }
                }//--AGGIUNGO I NUOVI---------------------------------------------------------------

                for (int kk = 0; kk < clientsConnected.size(); kk++) {
                    if (clientsConnected.get(kk).getTempFound() == 0) { // c'è in RAM ma non in DB
                        //Verifico se esiste la sessione

//                        myWShandler.allSessions.remove(session);
                        Session session = null;
                        boolean foundInPeers = false;
                        for (Session peerSsession : peers) {
                            if (peerSsession.getId().equals(clientsConnected.get(kk).getSessionID())) {
                                // cerco sessiomi presenti in clientconnected ma non realmente collegate
                                foundInPeers = true;
                                //this.peers.remove(peerSsession);
//                                session = peerSsession;
                                break;
                            }
                        }
                        if (foundInPeers == false) {
                            System.out.println("LA SESSIONE CON ID_. " + clientsConnected.get(kk).getSessionID() + " NON ESISTE... va eliminata");
                            try {
                                clientsConnected.remove(kk);
                                System.out.println("FATTO. ");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                            String user = clientsConnected.get(kk).getClientParams().getCKuserID();
                            if (user == null || user.length() < 1) {
                                user = "(U)" + clientsConnected.get(kk).getUserID() + "(P)" + clientsConnected.get(kk).getUserPassword();
                            }
//                            System.out.println("LA SESSIONE CON ID_. " + clientsConnected.get(kk).getSessionID() + " ESISTE... ma non nel database. Va creata e aggiiornata");
                            try {
                                SQLphrase = "INSERT INTO  archivio_sessions ("
                                        + " token , "
                                        + " clientID , "
                                        + " sessionID , "
                                        + " contextID , "
                                        + " clientType , "
                                        + " clientModel , "
                                        + " clientIP , "
                                        + " status , "
                                        + " lastTouch , "
                                        + " user  "
                                        + ") VALUES ("
                                        + "'" + clientsConnected.get(kk).getTokenAssigned() + "',"
                                        + "'" + clientsConnected.get(kk).clientID + "',"
                                        + "'" + clientsConnected.get(kk).getSessionID() + "',  "
                                        + "'" + clientsConnected.get(kk).getClientParams().getCKcontextID() + "',  "
                                        + "'" + clientsConnected.get(kk).getClientType() + "',  "
                                        + "'" + clientsConnected.get(kk).getClientModel() + "',  "
                                        + "'" + clientsConnected.get(kk).getClientIP() + "',  "
                                        + "'" + clientsConnected.get(kk).getStatus() + "',  "
                                        + "NOW(),  "
                                        + "'" + user + "') ";

//                                System.out.println("<" + this.handlerID + ">INSERISCO TOKEN ->SQLphrase:" + SQLphrase);
                                ps = accountConny.prepareStatement(SQLphrase);
                                int i = ps.executeUpdate();
                            } catch (SQLException ex) {
                                System.out.println("Error 1276:" + ex);
                            }
                        }
                    }

                }

                try {
                    accountConny.close();

                } catch (SQLException ex) {
                    System.out.println("ERROR. " + ex.toString());
                }
            } else {
                // context null nella sessione
                //contesti.remove(jj);
                // potrebbe essere stato appena inserito e non avere ancora il contexyt

            }
        }

    }

    public void broadcast(Session session, String type, String sender, String code, String message) {
//        System.out.println("SONO IN BROADCAST. clientsConnected:" + clientsConnected.size()
//                + "  - PEERS:" + this.peers.size());

//        el.log(PROJECT_ID, "APP-WS: Broadcast from " + sender + ":" + message);
//        allSessions = session.getOpenSessions();
        if (session.isOpen()) {

            String senderName = "";
            if (sender == "000") {
                senderName = "WS-SERVER";
            } else {
                for (int jj = 0; jj < clientsConnected.size(); jj++) {
//                    System.out.println(jj + ">: " + sender + " = " + clientsConnected.get(jj).getUser());
                    if (clientsConnected.get(jj).getSessionID().equals(sender)) {
                        senderName = clientsConnected.get(jj).getUser();
                        break;
                    }
                }
            }
            JSONObject obj = new JSONObject();
            obj.put("ip", "000");
            obj.put("SENDER", senderName);
            obj.put("TYPE", type);
            obj.put("CODE", code);
            obj.put("VALUE", message);
            obj.put("map", prepareMap("peers"));

            for (Session peerSsession : peers) {
//            el.log(PROJECT_ID, "APP-WS: single Broadcast from " + senderName + " to " + sess.getId() + ":" + message);

                if (!sender.equals(peerSsession.getId())) { //invio solo se non è il sender
                    try {
                        peerSsession.getBasicRemote().sendText(obj.toJSONString());
                    } catch (IOException ioe) {
                        System.out.println(ioe.getMessage());
//                        el.log(PROJECT_ID, "ERRORE 092: " + ioe.getMessage());
                        // peers.remove(sess);
                        parseClose(peerSsession.getId());
                    }
                } else {
//                System.out.println("APP-WS: Al destinatario " + senderName + "(" + sender + ") non invio perchè è il mittente.");
                }
            }
        }
    }

    private void sendTimeToAll(Session session) {
//        allSessions = session.getOpenSessions();
//        System.out.println("\n\n Invio orario " + allSessions.size());
        /*
         JSONObject obj = new JSONObject();       
         obj.put("ip", "000");
         obj.put("TYPE","time");
         obj.put("VALUE", LocalTime.now().format(timeFormatter));
         lastMessage=obj.toJSONString();
         */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Rome");
        sdf.setTimeZone(timeZone);
        String dataOra = sdf.format(calendar.getTime());
        String elencoSessioni = "";
        for (Session sess : peers) {
            if (elencoSessioni.length() > 1) {
                elencoSessioni += " - ";
            }
            elencoSessioni += sess.getId();
        }

        for (Session sess : peers) {
            try {

                String id = sess.getId();
                String obj = "{\"ip\":\"000\" ,\"TYPE\":\"time\",\"VALUE\":\"" + id + " - " + dataOra + "\"}";
                sess.getBasicRemote().sendText(obj);
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());

            }
        }
    }

    public void printPeers() {
        System.out.println("\n--------\nPEERS ");
        for (Session sess : peers) {
            // LISTA DEI CLIENT
            System.out.println("PEER> " + sess.getId());

        }
////         System.out.println("\n--------\nALL SESSIONS " );
////         for (Session sess : allSessions) {
////            // LISTA DEI CLIENT
////            System.out.println("SESSION> " + sess.getId());
////
////        }
    }

    public void printClientsConnected() {
        System.out.println("\n--------\nCLIENT CONNECTED ");
        for (WSclient client : clientsConnected) {
            System.out.println("CLIENT CONNECTED> "
                    + client.getSessionID()
                    + " Cid:" + client.clientID
                    + " U:" + client.getUser()
                    + " T:" + client.tokenAssigned);// questo token non è quello del CK, ma viene assegnato nell'handshake al client

        }
    }

    public String getClientIDbyToken(String token) {
        String searched = "WS/" + token;
        String clientID = "";
        if (token != null && token.length() > 10) {
            for (WSclient client : clientsConnected) {
                if (client.clientID.contains(searched)) {
//                    System.out.println("client.clientID> " + client.clientID + "  CONTIENE " + searched + "  ---> SESSION ID:" + client.getSessionID());
                    clientID = client.getSessionID();
                    break;
                }

            }
        }
        return clientID;
    }

    private JSONObject prepareMap(String type) {

        JSONObject map = new JSONObject();

        if (type.equalsIgnoreCase("peers")) {
            try {
                JSONArray clientsArray = new JSONArray();
                for (Session sess : peers) {

                    JSONObject clientPoint = new JSONObject();
                    clientPoint.put("cl", sess.getId());
                    clientsArray.add(clientPoint);

                }

                map.put("clientsPeers", clientsArray);

                JSONArray allSessionsArray = new JSONArray();
                for (Session sess : peers) {
                    JSONObject clientPoint = new JSONObject();
                    clientPoint.put("id", sess.getId());
                    allSessionsArray.add(clientPoint);
                }
                map.put("clientsSession", clientsArray);
            } catch (Exception e) {
            }

        }
        String maptxt = map.toJSONString();
        // System.out.println("maptxt: " + maptxt);

        return map;

    }

    private void onNewClient(Session session) {
        broadcast(session, "refreshContacts", "000", "000", "");
    }

    public void onLostClient(Session session) {
        broadcast(session, "refreshContacts", "000", "000", "");
    }

    public Settings getMySettings() {
        return mySettings;
    }

    public void setMySettings(Settings mySettings) {
        this.mySettings = mySettings;
    }

    public ArrayList<WSclient> getClientsConnected() {
        return clientsConnected;
    }

    public ArrayList<WSbroadcastMessage> getBcMessages() {
        return bcMessages;
    }

    public void setBcMessages(ArrayList<WSbroadcastMessage> bcMessages) {
        this.bcMessages = bcMessages;
    }

    public ArrayList<WScontext> getContesti() {
        return contesti;
    }

    public void setContesti(ArrayList<WScontext> contesti) {
        this.contesti = contesti;
    }

    public String getLastContextConnected() {
        return lastContextConnected;
    }

    public void setLastContextConnected(String lastContextConnected) {
        this.lastContextConnected = lastContextConnected;
    }

    public Session getGlobalSession() {
        return GlobalSession;
    }

    public void setGlobalSession(Session GlobalSession) {
        this.GlobalSession = GlobalSession;
    }

    public static Set<Session> getPeers() {
        return peers;
    }

    public static void setPeers(Set<Session> peers) {
        WShandler.peers = peers;
    }

//    public static Set<Session> getAllSessions() {
//        return allSessions;
//    }
//
//    public static void setAllSessions(Set<Session> allSessions) {
//        WShandler.allSessions = allSessions;
//    }
    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        WShandler.counter = counter;
    }

    public static int getAttempt() {
        return attempt;
    }

    public static void setAttempt(int attempt) {
        WShandler.attempt = attempt;
    }

    public static boolean isBeat5s() {
        return beat5s;
    }

    public boolean isBeat1m() {
        return beat1m;
    }

    public boolean isBeat2m() {
        return beat2m;
    }

    public boolean isBeat5m() {
        return beat5m;
    }

    public boolean isBeat10m() {
        return beat10m;
    }

    public boolean isBeat1h() {
        return beat1h;
    }

    public boolean isBeat6h() {
        return beat6h;
    }

    public static int getIntervalSecs() {
        return intervalSecs;
    }

    public static void setIntervalSecs(int intervalSecs) {
        WShandler.intervalSecs = intervalSecs;
    }

    public class WSorderJOB {

        String action = "";
        String senderID = "";
        String senderIP = "";
        String targetID = "";
        String targetIP = "";
        JSONObject payload;
    }

    public class WSorderP2P {

        String MittenteID = "";
        String DestinatarioID = "";
        String action = "";
        String DeviceLocalPeriph = "";
        String value = "";

        String MittenteToken = "";
        String DestinatarioToken = "";
        String DestinatarioPIN = "";
        String DeviceLocalIP = "";
        String TBSmessage = "";
        String sessionID;
        JSONObject payload;
    }

}
