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
import showIt.eventManager;
import smartCore.smartForm;
import REVOpager.EVOpagerDBconnection;
import REVOpager.EVOuser;
import REVOsetup.EVOsetup;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.ShowItForm;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

public class requestsManager {

    IncomingRequest Irequest;
    // String RMargs;
    ArrayList<gate> myConnectors;

    public requestsManager(IncomingRequest Xrequest
    //, String RMargs
    ) {
        this.Irequest = new IncomingRequest();
        this.Irequest = Xrequest;
        this.Irequest.setOutputStreamType("PrintWriter");
        //  this.RMargs = RMargs;

//        System.out.println("\n*****\nCostruito requestsManager -Settings.pr = \n" + request.getMySettings().getProjectName());
//        System.out.println("Costruito requestsManager -Params.pr = \n" + request.getMySettings().getProjectName());
    }

    public IncomingRequest processRequest() {

//        System.out.println("SONO IN PROCESS REQUEST:");
        String responseType = this.Irequest.getResponseType();
        String connectors = this.Irequest.getConnectors();
        String paramsJSON = this.Irequest.getParams();
//        System.out.println("IncomingRequest processRequest()...Assegno i params " + paramsJSON);
        try {
            EVOpagerParams myParams = Irequest.getMyParams().chargeParams(paramsJSON, Irequest.getMySettings());
            if (myParams != null) {
                Irequest.setMyParams(myParams);
            }
        } catch (Exception e) {
            System.out.println("requestsManager-->ERRORE IN CARICAMENTO PARAMETRI:" + e.toString());
        }
//          System.out.println("\t\tFATTO! ");
/*
        Attenzione : a questo punto se sono in LOGIN e non ho un CNT (context) devo
        cercare nel DB queenpro il CNT di default.
        Se invece ce l'ho devo usare quello che ho a disposizione
         */

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        //System.out.println("Assegno i connectors :" + connectors);
        gate myGate1 = new gate();
        //a partire dal Gate riempio una matrice di connettori
        //per il momento userò solo un connettore (o comunque l'ultimo della lista che sovrascrive i valori dei precedenti)
        myConnectors = myGate1.chargeConnectors(connectors);
        //System.out.println("\t\tFATTO! " + myConnectors.size() + " connettori");

        //*****************
        // facciamo verifica utente ??  EVOuser myUser = new EVOuser(myParams, mySettings);
        //************************
        eventManager myEvent = new eventManager(Irequest.getMyParams(), Irequest.mySettings);

        for (int jj = 0; jj < myConnectors.size(); jj++) {
//            System.out.println("\n---------\n"
//                    + "CONNECTOR : door=" + myConnectors.get(jj).getDoor()
//                    + "\t event=" + myConnectors.get(jj).getEvent()
//                    + "\tkeyValue=" + myConnectors.get(jj).getKeyValue());

            Irequest.setMyGate(myConnectors.get(jj));
//            System.out.println("Irequest keyValue:" + Irequest.getMyGate().getKeyValue());

            /*
            CONTROLLO SESSIONE
             */
            int flagSession = 1;
            if (Irequest.getMyParams().getCKprojectName() == null || Irequest.myParams.getCKuserID() == null) {
                flagSession = 0;
            }
            EVOuser myUser = new EVOuser(Irequest.getMyParams(), Irequest.mySettings);
            int sessionValid = 1;
            int enabled = -1;

            try {
                enabled = myUser.authorizeUser();
            } catch (Exception e) {
                enabled = -1;
            }
            if (enabled < 1 || flagSession < 1) {
                String errorMessage = "requestsManager-->MESSAGE:666 - USER UNKNOWN. PLEASE LOGOUT & LOGIN AGAIN.";
                System.out.println(errorMessage);
                Irequest.getMyGate().setUserEnabled(0);
                sessionValid = 0;
            } else {
                Irequest.getMyGate().setUserEnabled(1);
                sessionValid = 1;
            }

            Irequest.setSessionValid(sessionValid);

            if (Irequest.getSessionValid() < 1) {if ( Irequest.mySettings.isDatabrowser()){
                    System.out.println("SESSIONE DATA BROWSER:" + Irequest.getMyGate().getDoor());
                   Irequest = myEvent.DataBrowserFormShow(Irequest); 
                    return Irequest;
                }else if ((Irequest.getMyGate().getDoor().equalsIgnoreCase("AccountManager") && !Irequest.getMyGate().getEvent().equalsIgnoreCase("ownerAccountManager"))
                        || (Irequest.getMyGate().getDoor().equalsIgnoreCase("renderPic") && Irequest.getMyGate().getKeyValue().equalsIgnoreCase("softwareLogo"))
                        || (Irequest.getMyGate().getDoor().equalsIgnoreCase("populateHeader"))
                        || (Irequest.getMyGate().getDoor().equalsIgnoreCase("update"))) {

                    System.out.println("SESSIONE NON VALIDA MA OPERAZIONE AUTORIZZATA PER LOGIN FORM");
                } else {
                    System.out.println("SESSIONE NON VALIDA:" + Irequest.getMyGate().getDoor());
                    System.out.println(" >" + Irequest.getMyGate().getEvent());
                    Irequest.getMyGate().setForm("");
                    EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(Irequest.myParams, Irequest.mySettings);
                    String ApplicationWebURL = myManager.getDirective("ApplicationWebURL");
                    if (ApplicationWebURL == null || ApplicationWebURL.equalsIgnoreCase("null")) {
                        ApplicationWebURL = Irequest.mySettings.getProjectName();
                    }

////////                    String overPage = "<!DOCTYPE html>\n"
////////                            + "<html>\n"
////////                            + "   <head>\n"
////////                            + "      <title>SESSIONE SCADUTA</title>\n"
////////                            + "      <meta http-equiv = \"refresh\" content = \"2; url = "+localWebserverBaseURL+"\" />\n"
////////                            + "   </head>\n"
////////                            + "   <body>\n"
////////                            + "      <p>SESSIONE SCADUTA, RIESEGUIRE IL LOGIN.</p>\n"
////////                            + "   </body>\n"
////////                            + "</html>"; 
                    String link = "<a href='" + ApplicationWebURL + "'>LOGIN</a>";

                    String risposta = "{\"ACTION\":\"LOGOUT\",\"respOK\":\"logout\",\"MESSAGE\":\"INVALID SESSION\",\"DESCRIPTION\":\"SESSIONE SCADUTA, RIESEGUIRE IL " + link + ".\"}";
                    Irequest.setResponse(risposta);
                    return Irequest;
                }
            }

            Irequest.getMyGate().setMyParams(Irequest.getMyParams());

            //*****D O O R S*****//            
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="FormShow">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("FormShow")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("firstForm")) {
                    Irequest.getMyGate().setControlNeeded(true);

                    if (Irequest.getMyGate().getFormToLoad().equalsIgnoreCase("mainForm")) {
                        Irequest = myEvent.MasterFormShow(Irequest);
                    } else {//caso chiamata form da quickAccessForm
                        System.out.println("formToLoad:" + Irequest.getMyGate().getFormToLoad());
                        System.out.println("formFormName:" + Irequest.getMyGate().getFormName());
                        Irequest.getMyGate().setFormToLoad(Irequest.getMyGate().getFormName());
                        System.out.println("keyValue:" + Irequest.getMyGate().getKeyValue());
                        System.out.println("keyType:" + Irequest.getMyGate().getKeyType());
                        Irequest = myEvent.MasterFormShow(Irequest);
//                      ShowItForm myForm = loadFORMfromGATE();
//                      
//                    System.out.println("SONO IN LOADCHILD");
//                    ShowItFormResponse formResponse = new ShowItFormResponse();
//                    formResponse = myForm.paintForm();
//                    Irequest.getMyGate().setGes_routineOnLoad(formResponse.ges_routineOnLoad);
//                    Irequest.setResponse(formResponse.HtmlCode);
//                    System.out.println("CODICE HTML CHILD: " + formResponse.HtmlCode);
//                    //request.setResponse(myForm.paintForm().HtmlCode);
//                    Irequest.getMyGate().setForm((String) Irequest.getResponse());
//                    Irequest.getMyGate().setFormType(myForm.getType());
                    }

                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("OPENSPLASHFRAME")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    if (Irequest.getMyGate().getType().equalsIgnoreCase("formDetails")) {
                        String splashArgs = Irequest.getMyGate().getPanJson();

                        System.out.println("\nsplashArgs:" + splashArgs);
                        if (splashArgs != null && splashArgs.length() > 0) {
                            String decodedString1;
                            try {
                                decodedString1 = URLDecoder.decode(splashArgs, "UTF-8");
                                splashArgs = decodedString1.toString();
                            } catch (UnsupportedEncodingException ex) {
                                Logger.getLogger(requestsManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        System.out.println("formToLoad:" + Irequest.getMyGate().getFormToLoad());
                        System.out.println("keyValue:" + Irequest.getMyGate().getKeyValue());
                        System.out.println("keyType:" + Irequest.getMyGate().getKeyType());
                        System.out.println("\n\n\nformDetails chiama:" + Irequest.getMyGate().getFormToLoad());
                        String mySplashArgs = null;
                        try {
                            mySplashArgs = URLEncoder.encode(splashArgs, "UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(requestsManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // rimando a portal gli argomenti che saranno passati alla pagina di redirect sotto forma di stringa (JSON)

                        Irequest.setResponse(mySplashArgs);
                        Irequest.setOutputStreamType("splashFrame");

                    }

                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("OPENCUSTOMINFRAME")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    if (Irequest.getMyGate().getType().equalsIgnoreCase("formDetails")) {
                        String splashArgs = Irequest.getMyGate().getPanJson();

                        System.out.println("\nsplashArgs:" + splashArgs);
                        if (splashArgs != null && splashArgs.length() > 0) {
                            String decodedString1;
                            try {
                                decodedString1 = URLDecoder.decode(splashArgs, "UTF-8");
                                splashArgs = decodedString1.toString();
                            } catch (UnsupportedEncodingException ex) {
                                Logger.getLogger(requestsManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        System.out.println("formToLoad:" + Irequest.getMyGate().getFormToLoad());
                        System.out.println("keyValue:" + Irequest.getMyGate().getKeyValue());
                        System.out.println("keyType:" + Irequest.getMyGate().getKeyType());
                        System.out.println("\n\n\nformDetails chiama:" + Irequest.getMyGate().getFormToLoad());
                        String mySplashArgs = null;
                        try {
                            mySplashArgs = URLEncoder.encode(splashArgs, "UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(requestsManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // rimando a portal gli argomenti che saranno passati alla pagina di redirect sotto forma di stringa (JSON)

                        Irequest.setResponse(mySplashArgs);
                        Irequest.setOutputStreamType("customFrame");

                    }

                }
            } else // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="uploadFile">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("uploadFile")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("uploadOnDB")) {
                    Irequest.getMyGate().setControlNeeded(false);

                    return Irequest;
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("oploadOnDisk")) {
                    Irequest.getMyGate().setControlNeeded(false);

                }

            } else // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="AccountManager">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("AccountManager")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("LoginForm")) {
                    EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(Irequest.myParams, Irequest.mySettings);
                    String siteSuspended = myManager.getDirective("siteSuspended");
                    if (siteSuspended != null && siteSuspended.equalsIgnoreCase("TRUE")) {
                        System.out.println("vado in siteOfflineForm ");
                        Irequest.getMyGate().setControlNeeded(false);
                        Irequest = myEvent.siteOfflineForm(Irequest);
                    } else {
                        System.out.println("vado in LoginForm ");
                        Irequest.getMyGate().setControlNeeded(false);
                        Irequest = myEvent.loginForm(Irequest);
                    }
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("siteOfflineForm")) {
                    System.out.println("vado in LoginForm ");
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest = myEvent.loginForm(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("quickLoginForm")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest.myParams.setLoginResult(0);
                    Irequest = myEvent.quickLoginForm(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("Login")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest.myParams.setLoginResult(0);
                    Irequest = myEvent.login(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("Logout")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest.myParams.setLoginResult(0);
                    Irequest = myEvent.logout(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("quickLogin")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest.myParams.setLoginResult(0);
                    Irequest = myEvent.quickLogin(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("sendMailPW")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest = myEvent.createMailPW(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("newPasswordForm")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest = myEvent.newPasswordForm(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("confirmNewPassword")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest = myEvent.confirmNewPassword(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("newAccount")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest = myEvent.newAccountForm(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("createMailAccount")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest = myEvent.createMailAccount(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("confirmNewAccount")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest = myEvent.confirmNewAccount(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("ownerAccountManager")) {
                    /* request.getMyGate().setControlNeeded(false);
                   request = myEvent.ownerAccountManager(request);*/
                    ShowItForm myForm = loadFORMfromGATE();
                    Irequest.setResponse(myForm.paintForm().HtmlCode);
                    Irequest.getMyGate().setForm((String) Irequest.getResponse());
                    Irequest.getMyGate().setFormType(myForm.getType());

                }
                //-----------------------------------------------------------------------------------
            } else // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="update">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("update")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("frontendUpdate")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest.response = myEvent.QPfrontendUpdate();
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("projectUpdate")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest.response = myEvent.projectUpdate();
                }
                //-----------------------------------------------------------------------------------
            } else // </editor-fold>      
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="dbManager">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("dbManager")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("backupDB")) {

                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("backupAllDb")) {

                    Irequest.setResponse("Backup requested.");
                    EVOsetup mySetup = new EVOsetup(Irequest.myParams, Irequest.mySettings, "ALLDB");

                    Irequest.setResponse("Backup done.");
                }
                //-----------------------------------------------------------------------------------
            } else // </editor-fold>     
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="manageStoredFiles">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("manageStoredFiles")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("askFilenameForm")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    System.out.println("richiesto form per nome file upload: :" + Irequest.getMyGate().getFatherForm());
                    Irequest = myEvent.askFilenameForm(Irequest);
                    System.out.println("form:" + Irequest.response.toString());

                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("ServeFile")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest.setOutputStreamType("renderApplicationX");
                    Irequest = myEvent.ServeFileFromFS(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("DeleteFile")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest.response = myEvent.DeleteFileFromFS(Irequest);
                    System.out.println("\nESEGUITO DELETE!");
                }
            } else // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="populateHeader">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("populateHeader")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("title")) {
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest = myEvent.populateTitle(Irequest);
//                    System.out.println("TITOLO:" + Irequest.response);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("version")) {
                    System.out.println("\n\nRICHIESTA VERSIONE:");
                    Irequest.getMyGate().setControlNeeded(false);
                    Irequest = myEvent.populateVersion(Irequest);
                    System.out.println("VERSIONE:" + Irequest.response);
                }
                //-----------------------------------------------------------------------------------
            } else // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="clickedObject">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("clickedObject")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("OpenSecForm")) {

                    ShowItForm myForm = loadFORMfromGATE();
                    myForm.loadFormSettings();
                    //faccio apertura form secondario dopo click su oggetto.
                    // quando selezionavo riga, il keyvalue corretto era su fatherkjeyValue
                    // provo a usarlo anche qui
                    //AGGIUNTO  17-7-2019----------------------------------------------------
                    myForm.setCurKEYvalue(Irequest.getMyGate().getKeyValue());
                    System.out.println("myForm KeyValue= " + myForm.getCurKEYvalue());
                    myForm.setCurKEYtype(Irequest.getMyGate().getKeyType());
                    System.out.println("myForm getCurKEYtype = " + myForm.getCurKEYtype());

                    myForm.setFatherKEYvalue(Irequest.getMyGate().getKeyValue());
                    System.out.println("myForm setFatherKEYvalue= " + myForm.getFatherKEYvalue());
                    myForm.setFatherKEYtype(Irequest.getMyGate().getKeyType());
                    System.out.println("myForm setFatherKEYtype = " + myForm.getFatherKEYtype());
                    // questo perchè nelle sostituzioni del browserArgsReplace
                    // il $$$KEY$$$  viene sostituito con il fatherKeyValue
                    ShowItFormResponse formResponse = new ShowItFormResponse();
                    System.out.println("\n\n\n------------\nATTENZIONE! apro un form del tipo  " + myForm.getType());
                    //---fine aggiunta----------------------------------------------------
                    if (myForm.getType() == null) {
                        myForm.setType("TABLE");
                    }
                    if (myForm.getType().equalsIgnoreCase("SMARTTABLE")) {
                        smartForm mySmartForm = loadSmartFORMfromGATE();
                        mySmartForm.setLoadType("{\"type\":\"SMARTTABLE\","
                                + "\"visualType\":\"FULLFORM\","
                                + "\"firstRow\":\"1\","
                                + "\"NofRows\":\"50\","
                                + "\"currentPage\":\"1\","
                                + "\"visualFilter\":\"\"}");
                        System.out.println("VADO IN clickedObject-->SMARTTABLE, getVisualType:" + mySmartForm.getVisualType());
                        mySmartForm.paintForm();//questo mette il codice html in mySmartForm.formResponse
                        System.out.println("PaintForm eseguito");

                        formResponse.setGes_routineOnLoad(mySmartForm.formResponse.getGes_routineOnLoad());
                        formResponse.setHtmlCode(mySmartForm.formResponse.getHtmlCode());
                        Irequest.getMyGate().setGes_routineOnLoad(formResponse.ges_routineOnLoad);
                        Irequest.setResponse(formResponse.HtmlCode);
                        System.out.println("Esco con Door=" + Irequest.getMyGate().getDoor());

                    } else if (myForm.getType().equalsIgnoreCase("SMARTTREE")) {
                        smartForm mySmartForm = loadSmartFORMfromGATE();
                        mySmartForm.setLoadType("{\"type\":\"SMARTTREE\","
                                + "\"visualType\":\"FULLFORM\","
                                + "\"firstRow\":\"1\","
                                + "\"NofRows\":\"50\","
                                + "\"currentPage\":\"1\","
                                + "\"visualFilter\":\"\"}");
                        System.out.println("VADO IN clickedObject-->SMARTTREE, getVisualType:" + mySmartForm.getVisualType());
                        mySmartForm.paintForm();//questo mette il codice html in mySmartForm.formResponse
                        System.out.println("PaintForm eseguito");

                        formResponse.setGes_routineOnLoad(mySmartForm.formResponse.getGes_routineOnLoad());
                        formResponse.setHtmlCode(mySmartForm.formResponse.getHtmlCode());
                        Irequest.getMyGate().setGes_routineOnLoad(formResponse.ges_routineOnLoad);
                        Irequest.setResponse(formResponse.HtmlCode);
                        System.out.println("Esco con Door=" + Irequest.getMyGate().getDoor());
                        System.out.println("formResponse.HtmlCode=" + formResponse.HtmlCode);
                    } else if (myForm.getType().equalsIgnoreCase("SMARTCALENDAR")) {
                        smartForm mySmartForm = loadSmartFORMfromGATE();
                        mySmartForm.setLoadType("{\"type\":\"SMARTCALENDAR\","
                                + "\"visualType\":\"FULLFORM\","
                                + "\"firstRow\":\"1\","
                                + "\"NofRows\":\"50\","
                                + "\"currentPage\":\"1\","
                                + "\"visualFilter\":\"\"}");
                        System.out.println("VADO IN clickedObject-->SMARTCALENDAR, getVisualType:" + mySmartForm.getVisualType());
                        mySmartForm.paintForm();//questo mette il codice html in mySmartForm.formResponse
                        System.out.println("PaintForm eseguito");

                        formResponse.setGes_routineOnLoad(mySmartForm.formResponse.getGes_routineOnLoad());
                        formResponse.setHtmlCode(mySmartForm.formResponse.getHtmlCode());
                        Irequest.getMyGate().setGes_routineOnLoad(formResponse.ges_routineOnLoad);
                        Irequest.setResponse(formResponse.HtmlCode);
                        System.out.println("Esco con Door=" + Irequest.getMyGate().getDoor());
                        System.out.println("formResponse.HtmlCode=" + formResponse.HtmlCode);

                    } else if (myForm.getType().equalsIgnoreCase("SMARTPANEL")) {
                        smartForm mySmartForm = loadSmartFORMfromGATE();
                        mySmartForm.setLoadType("{\"type\":\"SMARTPANEL\","
                                + "\"visualType\":\"FULLFORM\","
                                + "\"firstRow\":\"1\","
                                + "\"NofRows\":\"50\","
                                + "\"currentPage\":\"1\","
                                + "\"visualFilter\":\"\"}");
                        System.out.println("VADO IN clickedObject-->SMARTPANEL, getVisualType:" + mySmartForm.getVisualType());
                        mySmartForm.paintForm();//questo mette il codice html in mySmartForm.formResponse
                        System.out.println("PaintForm eseguito");

                        formResponse.setGes_routineOnLoad(mySmartForm.formResponse.getGes_routineOnLoad());
                        formResponse.setHtmlCode(mySmartForm.formResponse.getHtmlCode());
                        Irequest.getMyGate().setGes_routineOnLoad(formResponse.ges_routineOnLoad);

                        Irequest.setResponse(formResponse.HtmlCode);
                      } else if (myForm.getType().equalsIgnoreCase("SMARTCAROUSEL")) {
                       smartForm mySmartForm = loadSmartFORMfromGATE();
                        mySmartForm.setLoadType("{\"type\":\"SMARTCAROUSEL\","
                                + "\"visualType\":\"FULLFORM\","
                                + "\"firstRow\":\"1\","
                                + "\"NofRows\":\"50\","
                                + "\"currentPage\":\"1\","
                                + "\"visualFilter\":\"\"}");
                        System.out.println("VADO IN clickedObject-->SMARTCAROUSEL, getVisualType:" + mySmartForm.getVisualType());
                        mySmartForm.paintForm();//questo mette il codice html in mySmartForm.formResponse
                        System.out.println("PaintForm eseguito");

                        formResponse.setGes_routineOnLoad(mySmartForm.formResponse.getGes_routineOnLoad());
                        formResponse.setHtmlCode(mySmartForm.formResponse.getHtmlCode());
                        Irequest.getMyGate().setGes_routineOnLoad(formResponse.ges_routineOnLoad);
                        Irequest.setResponse(formResponse.HtmlCode);
                        System.out.println("Esco con Door=" + Irequest.getMyGate().getDoor());

                    } else {
                        formResponse = myForm.paintForm();
                        Irequest.getMyGate().setGes_routineOnLoad(formResponse.ges_routineOnLoad);
                        Irequest.setResponse(formResponse.HtmlCode);
                    }
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("OpenFilteredForm")) {
                    ShowItForm myForm = loadFORMfromGATE();
                    ShowItFormResponse formResponse = new ShowItFormResponse();
                    formResponse = myForm.paintForm();
                    Irequest.getMyGate().setGes_routineOnLoad(formResponse.ges_routineOnLoad);
                    Irequest.setResponse(formResponse.HtmlCode);
                }
            } else // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="formPager">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("formPager")) {
                System.out.println("\n\n\nRICHIESTA DI TIPO FORMPAGER. EVENT:" + Irequest.getMyGate().getEvent());

                ShowItForm myForm = loadFORMfromGATE();
                myForm.loadFormSettings();
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("REPAINTROW")) {
                    System.out.println("SONO IN REPAINTROW");

                    if (myForm.getType().equalsIgnoreCase("SMARTTABLE")) {
                        //uso smartForm con websocket
                        smartForm mySmartForm = loadSmartFORMfromGATE();
                        mySmartForm.setLoadType("{\"type\":\"SMARTTABLE\","
                                + "\"visualType\":\"singleRow\"}");

                        System.out.println("VADO IN SMARTTABLE, getVisualType:" + mySmartForm.getVisualType());
                        Irequest.setResponse(mySmartForm.paintForm().getHtmlCode());
                    } else {
                        // uso showItForm con ajax
                        Irequest.setResponse(myForm.paintForm().HtmlCode);
                    }
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("FORMREFRESH")) {
                    Irequest.setResponse(myForm.paintForm().HtmlCode);
                    Irequest.getMyGate().setForm((String) Irequest.getResponse());
                    Irequest.getMyGate().setFormType(myForm.getType());

                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("LOADCHILD")) {
//                    System.out.println("SONO IN LOADCHILD");
                    ShowItFormResponse formResponse = new ShowItFormResponse();

                    if (myForm.getType().equalsIgnoreCase("SMARTTABLE")) {
                        smartForm mySmartForm = loadSmartFORMfromGATE();
                        mySmartForm.setLoadType("SMARTTABLE");
                        mySmartForm.setVisualType("FULLFORM");
                        System.out.println("VADO IN SMARTTABLE, getVisualType:" + mySmartForm.getVisualType());
                        mySmartForm.paintForm();//questo mette il codice html in mySmartForm.formResponse
                        formResponse.setGes_routineOnLoad(mySmartForm.formResponse.getGes_routineOnLoad());
                        formResponse.setHtmlCode(mySmartForm.formResponse.getHtmlCode());
                        Irequest.getMyGate().setGes_routineOnLoad(formResponse.ges_routineOnLoad);
                        Irequest.setResponse(formResponse.HtmlCode);
                    } else if (myForm.getType().equalsIgnoreCase("SMARTPANEL")) {
                        smartForm mySmartForm = loadSmartFORMfromGATE();
                        mySmartForm.setLoadType("{\"type\":\"SMARTPANEL\","
                                + "\"visualType\":\"FULLFORM\","
                                + "\"firstRow\":\"1\","
                                + "\"NofRows\":\"50\","
                                + "\"currentPage\":\"1\","
                                + "\"visualFilter\":\"\"}");
                        System.out.println("VADO IN clickedObject-->SMARTPANEL, getVisualType:" + mySmartForm.getVisualType());
                        mySmartForm.paintForm();//questo mette il codice html in mySmartForm.formResponse
                        System.out.println("PaintForm eseguito");

                        formResponse.setGes_routineOnLoad(mySmartForm.formResponse.getGes_routineOnLoad());
                        formResponse.setHtmlCode(mySmartForm.formResponse.getHtmlCode());
                        Irequest.getMyGate().setGes_routineOnLoad(formResponse.ges_routineOnLoad);

                        Irequest.setResponse(formResponse.HtmlCode);
                    } else {
                        System.out.println("VADO IN PAINTFORM");
                        formResponse = myForm.paintForm();
                        Irequest.getMyGate().setGes_routineOnLoad(formResponse.ges_routineOnLoad);
                        Irequest.setResponse(formResponse.HtmlCode);
                        //System.out.println("CODICE HTML CHILD: " + formResponse.HtmlCode);
                        //request.setResponse(myForm.paintForm().HtmlCode);
                        Irequest.getMyGate().setForm((String) Irequest.getResponse());
                        Irequest.getMyGate().setFormType(myForm.getType());
                    }
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("LOADCUSTOMCHILD")) {
                    System.out.println("SONO IN LOADCUSTOMCHILD");
                    Irequest.setOutputStreamType("customFrame");
                    Irequest.setResponse(Irequest.getMyGate().getFormToLoad());
                    System.out.println("getFormToLoad: " + Irequest.getMyGate().getFormToLoad());
                    System.out.println("CODICE HTML CUSTOMCHILD: " + Irequest.getMyGate().getFormToLoad());
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("GETCHILDRENLIST")) {

//                    myForm = new ShowItForm(Irequest.getMyGate().getFormID(), Irequest.myParams, Irequest.mySettings);
//                    myForm.loadFormSettings();
                    Irequest.setResponse(myForm.getChildrenList());
                }
            } else // </editor-fold>             
            //----------------------------------------------------------  
            // <editor-fold defaultstate="collapsed" desc="groupChecker">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("groupChecker")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("getGroups")) {

                    System.out.println("\n\nSONO IN getGroups");
                    Irequest = myEvent.getGroups(Irequest);
                } else //setRelations
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("setRelations")) {

                    System.out.println("\n\nSONO IN setRelations");
                    Irequest = myEvent.setRelations(Irequest);
                }
            } else // </editor-fold>             
            //---------------------------------------------------------- 
            // <editor-fold defaultstate="collapsed" desc="suggestField">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("suggestField")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("getSuggestedList")) {

                    System.out.println("\n\nSONO IN getSuggestedList");
                    Irequest = myEvent.getSuggestedList(Irequest);
                } else //setRelations
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("setSuggestedValue")) {

                    System.out.println("\n\nSONO IN setSuggestedValue");
                    Irequest = myEvent.setSuggestedValue(Irequest);
                }
            } else // </editor-fold>             
            //----------------------------------------------------------
            // <editor-fold defaultstate="collapsed" desc="CRUD">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("CRUD")) {
                if (Irequest.getSessionValid() < 1) {
                }
                CRUDorder myCrud = new CRUDorder(Irequest.myParams, Irequest.mySettings);

                myCrud.setOperation(Irequest.getMyGate().getOperation());    // ADD;UPD;DEL
                myCrud.setFormName(Irequest.getMyGate().getFormName());
                myCrud.setCellType(Irequest.getMyGate().getCellType());
                myCrud.setRoutineOnChange(Irequest.getMyGate().getRoutineOnChange());
                // myCrud.setValueType(valueType);
                myCrud.setFilterSequence(Irequest.getMyGate().getFilterSequence()); // in caso di ADD  
                System.out.println("_request manager setta FilterSequence:" + Irequest.getMyGate().getFilterSequence());

                myCrud.setPrimaryFieldName(Irequest.getMyGate().getPrimaryFieldName());// in caso di DEL indica il valore del campo primary (es. ID)
                myCrud.setPrimaryFieldValue(Irequest.getMyGate().getPrimaryFieldValue());// in caso di DEL indica il valore del campo primary
                myCrud.setPrimaryFieldType(Irequest.getMyGate().getPrimaryFieldType());
                myCrud.setFilterField(Irequest.getMyGate().getFilterField());
                myCrud.setFilterValue(Irequest.getMyGate().getFilterValue());
                myCrud.setCellName(Irequest.getMyGate().getCellName());
                // myCrud.setCellID(cellID);
                myCrud.setNewValue(Irequest.getMyGate().getNewValue());
                myCrud.setFatherKEYvalue(Irequest.getMyGate().getFatherKEYvalue());
                myCrud.setFatherKEYtype(Irequest.getMyGate().getFatherKEYtype());
                myCrud.setFieldFiltered(Irequest.getMyGate().getFieldFiltered());
                myCrud.setToBeSent(Irequest.getMyGate().getTBS());
                myCrud.setSendToCRUD(Irequest.getMyGate().getSendToCRUD());
                myCrud.setFormID(Irequest.getMyGate().getFormID());
                myCrud.setFormCopyTag(Irequest.getMyGate().getCopyTag());

//System.out.println("_request manager Irequest.getMyGate().getPrimaryFieldName():" + Irequest.getMyGate().getPrimaryFieldName());
//System.out.println("_request manager Irequest.getMyGate().getPrimaryFieldValue():" + Irequest.getMyGate().getPrimaryFieldValue());
//System.out.println("_request manager Irequest.getMyGate().getNewValue():" + Irequest.getMyGate().getNewValue());
//System.out.println("_request manager Irequest.getMyGate().getSendToCRUD():" + Irequest.getMyGate().getSendToCRUD());
//System.out.println("_request manager Irequest.getMyGate().getTBS():" + Irequest.getMyGate().getTBS());
//System.out.println("_request manager Irequest.getMyGate().getFatherKEYvalue():" + Irequest.getMyGate().getFatherKEYvalue());
//System.out.println("_request manager Irequest.getMyGate().getCellName():" + Irequest.getMyGate().getCellName());
//                System.out.println("\\n\\n>>routineOnChange: " + myCrud.getRoutineOnChange());
                //===ESEGUO OPERAZIONE CRUD=====================================
                Irequest.getMyGate().setForm(myCrud.executeCRUD());
                //==============================================================

                Irequest.getMyGate().setAfterOperationRoutineOnNew("");
                Irequest.getMyGate().setAfterOperationRoutineOnChange("");
                Irequest.getMyGate().setAfterOperationRoutineOnDelete("");

                if (myCrud.getOperation() != null && myCrud.getOperation().equalsIgnoreCase("ADD")) {
                    Irequest.getMyGate().setAfterOperationRoutineOnNew(myCrud.getAfterOperationRoutineOnNew());
                } else if (myCrud.getOperation() != null && myCrud.getOperation().equalsIgnoreCase("UPD")) {
                    Irequest.getMyGate().setAfterOperationRoutineOnChange(myCrud.getAfterOperationRoutineOnChange());
                } else if (myCrud.getOperation() != null && myCrud.getOperation().equalsIgnoreCase("DEL")) {
                    Irequest.getMyGate().setAfterOperationRoutineOnDelete(myCrud.getAfterOperationRoutineOnDelete());
                }

                System.out.println("\n[requestsManager]routineOnNew:" + Irequest.getMyGate().getAfterOperationRoutineOnNew());
                System.out.println("_CRUD ESEGUITO:" + Irequest.getMyGate().getForm());
                System.out.println("_ora verifico routineOnChange: " + myCrud.getRoutineOnChange());
                if (myCrud.getRoutineOnChange() != null && myCrud.getRoutineOnChange().length() >= 0) {
                    Irequest.getMyGate().setRoutineOnChange(myCrud.getRoutineOnChange());
                }
                System.out.println("_ora verifico ActionParams(): " + myCrud.getActionParams());
                String refreshOnChange = "";
                //{"changeRefresh":[{"type":"form","name":"infoPallet"}]}
                if (myCrud.getActionParams() != null && myCrud.getActionParams().length() > 2) {
                    try {
                        jsonObject = (JSONObject) jsonParser.parse(myCrud.getActionParams());
                        if (jsonObject.get("changeRefresh") != null) {
                            String TBSarray = jsonObject.get("changeRefresh").toString();
                            if (TBSarray != null && TBSarray.length() > 0) {
                                JSONParser parser = new JSONParser();
                                Object obj = parser.parse(TBSarray);
                                JSONArray array = (JSONArray) obj;

                                for (Object riga : array) {
                                    jsonObject = (JSONObject) jsonParser.parse(riga.toString());
                                    String xType = "";
                                    String xName = "";
                                    try {
                                        xType = jsonObject.get("type").toString();
                                    } catch (Exception e) {
                                    }
                                    try {
                                        xName = jsonObject.get("name").toString();
                                    } catch (Exception e) {
                                    }
                                    if (xType.length() > 0 && xName.length() > 0) {
                                        refreshOnChange += xType + ":" + xName + ";";
                                    }

                                }
                            }
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                // ho aggoirnato un campo di una tabella con una certa KEY;
                // esegui la routine routineOnChange passando il        
                // pKEYtype = "INT";
                //this.getPrimaryFieldName()
                //this.getPrimaryFieldValue() 

// adesso devo sostituire il comando post esecuzione da inviare a MasterFormShow
                // la matrice arriva da CRUDorder come JSON dentro  myGate.getForm();
                String tbsJson = Irequest.getMyGate().getForm();
                String sender = null;
                String operation = null;
                String code = null;
                String mess = null;
                String newID = null;
                String action = "";
                String routineResponse = "";
                //System.out.println(">>>tbsJson: " + tbsJson);
                try {
                    jsonObject = (JSONObject) jsonParser.parse(tbsJson);

                    try {
                        sender = jsonObject.get("sender").toString();
                    } catch (Exception e) {
                    }
                    try {
                        operation = jsonObject.get("operation").toString();
                    } catch (Exception e) {
                    }
                    try {
                        code = jsonObject.get("code").toString();
                    } catch (Exception e) {
                    }

                    try {
                        mess = jsonObject.get("mess").toString();
                    } catch (Exception e) {
                    }
                    try {
                        newID = jsonObject.get("newID").toString();
                    } catch (Exception e) {
                    }
                    try {
                        action = jsonObject.get("actionPassed").toString();
                    } catch (Exception e) {
                        action = "repaintRow";
                    }

                    if (myCrud.getOperation() != null && myCrud.getOperation().equalsIgnoreCase("DEL")) {
                        // in caso di cancellasione non faccio il refresh a meno che non sia indicato in jsonTOPbar
                        try {
                            routineResponse = jsonObject.get("routineResponse").toString();
                            action = "";
                        } catch (Exception e) {
                        }
                    } else {
                        try {
                            routineResponse = jsonObject.get("routineResponse").toString();

                        } catch (Exception e) {
                            routineResponse = "";
                        }
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (routineResponse == null || routineResponse.length() < 1) {
                    routineResponse = "[]";
                }
                String newjson = "{\"sender\":\"" + sender + "\",";
                newjson += "\"operation\":\"" + operation + "\",";
                newjson += "\"code\":\"" + code + "\",";
                newjson += "\"mess\":\"" + mess + "\",";
                newjson += "\"newID\":\"" + newID + "\",";
                newjson += "\"action\":\"" + action + "\",";
                newjson += "\"routineResponse\":" + routineResponse + ",";
                newjson += "\"routineOnChange\":\"" + myCrud.getRoutineOnChange() + "\",";
                newjson += "\"refreshOnChange\":\"" + refreshOnChange + "\"";
                newjson += "}";
//
                System.out.println(">>>newjson: " + newjson);
                if (myCrud.getRoutineOnChange() != null) {
                    // se era un add il nuovo ID da mettere in myCrud.getPrimaryFieldValue()
                    // è nel json inviato dal crud come parametro newID
                    if (operation != null && operation.equalsIgnoreCase("ADD")) {
                        myCrud.setPrimaryFieldValue(newID);
                        Irequest.getMyGate().setAfterOperationRoutineOnNew_NewValue(newID);

                    }

                    ShowItForm myForm = new ShowItForm(myCrud.getFormID(), Irequest.myParams, Irequest.mySettings);
                    if (myCrud.getFormName() != null) {
                        myForm.setName(myCrud.getFormName());
                    }
                    myForm.setCopyTag(myCrud.getFormCopyTag());
                    myForm.loadFormSettings();
                    myForm.getFormInformationsFromDB();
                    String dbTable = myForm.getMainTable();
                    myCrud.setMainTable(dbTable);
                    System.out.println("requestManager_Tabella individuata:" + myCrud.getMainTable() + " ; KEY:" + myCrud.getPrimaryFieldValue());
                    Irequest.getMyGate().setForm(newjson);
                    Irequest.setResponse(newjson);

                    // su Irequest posso passare anche i parametri usati per il CCRUD che andranno al portal e serviranno per l'esecuzione della routineOnChange
                }

            } else // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="CMLNK">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("CMLNK")) {
                CRUDorder myCrud = new CRUDorder(Irequest.myParams, Irequest.mySettings);
                myCrud.setOperation(Irequest.getMyGate().getOperation());
                myCrud.setFilterValue(Irequest.getMyGate().getFilterValue());
                myCrud.setNewValue(Irequest.getMyGate().getNewValue());
                myCrud.setPrimaryFieldValue(Irequest.getMyGate().getPrimaryFieldValue());
                myCrud.setCellName(Irequest.getMyGate().getCellName());
                //request.getMyGate().setForm(myCrud.executeCMLNK());                
                Irequest.setResponse(myCrud.executeCMLNK());
            } else // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="renderPic">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("renderPic")) {

                Irequest.getMyGate().setControlNeeded(true);
                String SQLphrase = null;
                PreparedStatement ps;
                ResultSet rs;
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("fromDB")) {

                    Connection conny = new EVOpagerDBconnection(Irequest.myParams, Irequest.mySettings).ConnLocalDataDB();
                    // nel primo caso con query su una unica tabella contenente l'9immagine
                    // il campo Irequest.getMyGate().getKeyField() contiene la colonna chiave es. "ID"
                    // se l'immagine è in unA TABELLA IN JOIN, 
                    //DEVO INDICARE ANCHE IL FIELD DA CERCARE NELLA TABELLA IN JOIN E CHE SIA UGUALE AL VALORE DELLA CHIAVE INDICATA
                    // DA USARE AL POSTO DI Irequest.getMyGate().getCurrentKEY() 
                    // DEVO DIRE QUINDI DI CERCARE ES:     cerca "PicField" (nella tabella con le immagini) dove
                    // il campo ID (della tabella indicata) sia uguale al campo "TIPOGRUPPO" della tabella main
                    //

                    //    .. vediamo se trovo il nome del form e riesco a risalire alla query corretta per la picture
//////                    System.out.println("\n\n\nRICHIESTA renderPic (riga 700 di requestManager)");
//////                    System.out.println("fatherForm:" + Irequest.getMyGate().fatherForm);
//////                    System.out.println("formName:" + Irequest.getMyGate().formName);
//////                    System.out.println("formToLoad:" + Irequest.getMyGate().formToLoad);
//////                    System.out.println("formID:" + Irequest.getMyGate().formID);
                    String union = " WHERE ";
                    try {
                        if (Irequest.getMyGate().getTable() != null
                                && Irequest.getMyGate().getTable().contains("WHERE")) {
                            union = " AND ";
                        }
                    } catch (Exception e) {
                    }

                    SQLphrase = "SELECT " + Irequest.getMyGate().getPicField()
                            + " FROM " + Irequest.getMyGate().getTable()
                            + union + Irequest.getMyGate().getKeyField()
                            + "='" + Irequest.getMyGate().getCurrentKEY() + "'";
//                    System.out.println("PIC SQLphrase fromDB: " + SQLphrase);

                    Blob blob = null;
                    try {
                        ps = conny.prepareStatement(SQLphrase);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            blob = rs.getBlob(Irequest.getMyGate().getPicField());
                        }
                        conny.close();
                    } catch (SQLException ex) {
                        System.out.println("ERRORE IN PIC fromDB" + ex.toString());
                    }
                    if (blob != null) {
                        Irequest.setOutputStreamType("renderPic");
                        Irequest.setResponse(blob);
                    }

                } else {

                    Connection conny = new EVOpagerDBconnection(Irequest.myParams, Irequest.mySettings).ConnLocalDataDB();
                    SQLphrase = "SELECT " + Irequest.getMyGate().getPicField() + " FROM " + Irequest.getMyGate().getTable() + " WHERE " + Irequest.getMyGate().getKeyField() + "='" + Irequest.getMyGate().getCurrentKEY() + "'";
//                    System.out.println("PIC SQLphrase: " + SQLphrase);
                    Blob blob = null;
                    if (conny != null) {
                        try {
                            ps = conny.prepareStatement(SQLphrase);
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                blob = rs.getBlob(Irequest.getMyGate().getPicField());
                            }
                            conny.close();
                        } catch (SQLException ex) {
                            System.out.println("ERRORE IN PIC " + ex.toString());
                        }
                    }

                    if (blob != null) {
                        Irequest.setOutputStreamType("renderPic");
                        Irequest.setResponse(blob);
                    }
                }
            } else if (Irequest.getMyGate().getDoor().equalsIgnoreCase("session")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("PopulateWS")) {
                    Irequest.getMyGate().setControlNeeded(true);
                    Irequest = myEvent.populateWS(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("PopulateQPWS")) {
                    Irequest.getMyGate().setControlNeeded(true);
                    Irequest = myEvent.populateQPWS(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("PopulateTitleBar")) {
                    Irequest.getMyGate().setControlNeeded(true);
                    Irequest = myEvent.populateTitleBar(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("PopulateSession")) {
                    Irequest.getMyGate().setControlNeeded(true);
                    Irequest = myEvent.populateSession(Irequest);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("PopulatePeople")) {
                    Irequest.getMyGate().setControlNeeded(true);
                    Irequest = myEvent.populatePeople(Irequest);
                }

            } else// </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="printPDF">               
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("printPDF")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("printFormReport")) {
                    ShowItForm myForm = loadFORMfromGATE();
                    myForm.loadFormSettings();
                    myForm.getFormInformationsFromDB();
                    Irequest.setOutputStreamType("renderPDF");
                    Irequest.setResponse(myForm);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("printReport")) {
                    ShowItForm myForm = loadFORMfromGATE();
                    myForm.loadFormSettings();
                    myForm.getFormInformationsFromDB();
                    Irequest.setOutputStreamType("renderPDF");
                    Irequest.setResponse(myForm);
                }

            } else // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="printRTF">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("printRTF")) {
                ShowItForm myForm = loadFORMfromGATE();
                myForm.loadFormSettings();
                myForm.getFormInformationsFromDB();
                Irequest.setOutputStreamType("renderRTF");
                Irequest.setResponse(myForm);
            } else // </editor-fold>             
            //----------------------------------------------------------   
            // <editor-fold defaultstate="collapsed" desc="iframe">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("iframe")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("MAP")) {
                    //usato per mappa cimitero
                    Irequest.getMyGate().setFormToLoad(Irequest.getMyGate().getFormName());
                    gaiaMap myMap = new gaiaMap(Irequest.getMyParams(), Irequest.mySettings);
                    String htmlCode = myMap.prepareCode(Irequest.getMyGate());
                    Irequest.setResponse(htmlCode);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("SYNOPTICMAPBUILDER")) {
                    //usato per mappa cimitero
                    Irequest.getMyGate().setFormToLoad(Irequest.getMyGate().getFormName());
                    synopticMapBuilder myMap = new synopticMapBuilder(Irequest.getMyParams(), Irequest.mySettings);
                    String htmlCode = myMap.prepareCode(Irequest.getMyGate());
                    Irequest.setResponse(htmlCode);
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("SYNOPTICMAP")) {
                    Irequest.getMyGate().setFormToLoad(Irequest.getMyGate().getFormName());
                    synopticIframeMap myMap = new synopticIframeMap(Irequest.getMyParams(), Irequest.mySettings);
                    String htmlCode = myMap.prepareCode(Irequest.getMyGate());
                    Irequest.setResponse(htmlCode);
                }

            } else // </editor-fold>             
            //---------------------------------------------------------- 
            // <editor-fold defaultstate="collapsed" desc="executeRoutine">
            if (Irequest.getMyGate().getDoor().equalsIgnoreCase("executeRoutine")) {
                if (Irequest.getMyGate().getEvent().equalsIgnoreCase("ExecuteRoutine")) {
                    Irequest.setOutputStreamType("routineExecuter");
                    System.out.println("EXECUTEROUTINEONFORM: formName = " + Irequest.getMyGate().getFormName());
                    System.out.println("EXECUTEROUTINEONFORM: formID = " + Irequest.getMyGate().getFormID());

                    // IN PRATICA QUI TORNO A PPORTAL CON UNA ROUTINE DA ESEGIORE IN LOCALE
                    // compilando myGate con i dati sulla roiutine da eseguire
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("APIserverRoutine")) { 
                    Irequest.setOutputStreamType("APIserverRoutine");
                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("ROUTINEONLOAD")) {
                    // IN PRAICA QUI TORNO A PPORTAL CON UNA ROUTINE DA ESEGIORE IN LOCALE
                    // compilando myGate con i dati sulla roiutine da eseguire
                    Irequest.setOutputStreamType("routineExecuter");

                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("ROUTINEAFTERLOAD")) {
                    // IN PRAICA QUI TORNO A PPORTAL CON UNA ROUTINE DA ESEGIORE IN LOCALE
                    // compilando myGate con i dati sulla roiutine da eseguire
                    Irequest.setOutputStreamType("routineExecuter");

                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("ROUTINEONCHANGE")) {
                    // IN PRAICA QUI TORNO A PPORTAL CON UNA ROUTINE DA ESEGIORE IN LOCALE
                    // compilando myGate con i dati sulla roiutine da eseguire
                    System.out.println("IN RequestManager impongo a portal l'esecuzione della routineOnCHange:" + Irequest.getMyGate().getRoutineOnChange());

                    Irequest.setOutputStreamType("routineExecuter");
                    Irequest.getMyGate().setRoutine(Irequest.getMyGate().getRoutineOnChange());
                    System.out.println("request manager->ROUTINEONCHANGE-> torno a portal con Stream routineExecuter ");

                } else if (Irequest.getMyGate().getEvent().equalsIgnoreCase("EXECUTEROUTINEONFORM")) {

                    //eseguo la routineOnChange per ogni oggetto che nel form ce l'ha
                    System.out.println("EXECUTEROUTINEONFORM: formName = " + Irequest.getMyGate().getFormName());
                    System.out.println("EXECUTEROUTINEONFORM: formID = " + Irequest.getMyGate().getFormID());
                    // in base al formID devo ricaVARE IL formName
                    ShowItForm myForm = loadFORMfromGATE();
                    myForm.buildSchema();
                    System.out.println("IL FORM CONTIENE " + myForm.objects.size() + " OGGETTI.");
                    // opra per ogni oggetto del form cerco la onChangeRoutine e la eseguo
                    try {
                        for (int kk = 0; kk < myForm.objects.size(); kk++) {

                            String routineName = myForm.objects.get(kk).getRoutineOnChange();
                            System.out.println("OGGETTO = " + myForm.objects.get(kk).getName() + " ESEGUO ROUTINE:--->" + routineName);

                            if (routineName != null && routineName.length() > 0) {
                                Irequest.getMyGate().setRoutine(routineName);

                            }
                        }
                    } catch (Exception e) {
                    }

                }

            }
            // </editor-fold>             
            //----------------------------------------------------------    

        }
        return Irequest;
    }

    public ShowItForm loadFORMfromGATE() {
//        System.out.println("\n\nloadFORMfromGATE==ID:" + Irequest.getMyGate().getFormID() + "========NAME:" + request.getMyGate().getFormName() + "==============");
//        System.out.println("formToLoad = " + Irequest.getMyGate().getFormToLoad() + "   *   destination = " + request.getMyGate().getDestination());
//        System.out.println("formID = " + Irequest.getMyGate().getFormID() + "   *   args = ?");
//        System.out.println("copyTag = " + Irequest.getMyGate().getCopyTag());
//        System.out.println("loadType = " + Irequest.getMyGate().getLoadType() + "   *   fatherForm = " + request.getMyGate().getFatherForm());
//        System.out.println("fatherKEYvalue = " + Irequest.getMyGate().getFatherKEYvalue() + "   *   fatherKEYtype = " + request.getMyGate().getFatherKEYtype() + "   *   fatherCopyTag = " + request.getMyGate().getFatherCopyTag());
//        System.out.println("TBS received= " + Irequest.getMyGate().getTBS());
////////        System.out.println("\n************-->\ncurKEYvalue= " + Irequest.getMyGate().getCurKEYvalue());
////////        System.out.println("sendToCRUD= " + Irequest.getMyGate().getSendToCRUD());
//        System.out.println("routine= " + Irequest.getMyGate().getRoutine()); 
        ShowItForm myForm = new ShowItForm(Irequest.getMyGate().getFormID(), Irequest.getMyParams(), Irequest.getMySettings());
        myForm.setName(Irequest.getMyGate().getFormToLoad());
        myForm.setID(Irequest.getMyGate().getFormID());
        myForm.setType(Irequest.getMyGate().getFormType());
        myForm.setFatherKEYvalue(Irequest.getMyGate().getFatherKEYvalue());
        myForm.setFatherKEYtype(Irequest.getMyGate().getFatherKEYtype());

        myForm.setFather(Irequest.getMyGate().getFatherForm());
        myForm.setFatherCopyTag(Irequest.getMyGate().getFatherCopyTag());
        myForm.setFatherFilters(Irequest.getMyGate().getFatherArgs());
        myForm.setLoadType(Irequest.getMyGate().getLoadType());

//        System.out.println(" LoadType= " + Irequest.getMyGate().getLoadType());
        myForm.setInfoReceived(Irequest.getMyGate().getTBS());
        myForm.setCopyTag(Irequest.getMyGate().getCopyTag());
        // il keyValue è dato dalla riga EVIDENZIATA
        // però se sto cliccando un pulsante deve valere la riga del pulsante !!!
        // come ho implementato ?
        myForm.setCurKEYvalue(Irequest.getMyGate().getCurKEYvalue());

        myForm.setCurKEYtype(Irequest.getMyGate().getCurKEYtype());
        myForm.setSendToCRUD(Irequest.getMyGate().getSendToCRUD());

        return myForm;
    }

    public smartForm loadSmartFORMfromGATE() {
//        System.out.println("\n\nloadFORMfromGATE==ID:" + Irequest.getMyGate().getFormID() + "========NAME:" + request.getMyGate().getFormName() + "==============");
//        System.out.println("formToLoad = " + Irequest.getMyGate().getFormToLoad() + "   *   destination = " + request.getMyGate().getDestination());
//        System.out.println("formID = " + Irequest.getMyGate().getFormID() + "   *   args = ?");
//        System.out.println("copyTag = " + Irequest.getMyGate().getCopyTag());
//        System.out.println("loadType = " + Irequest.getMyGate().getLoadType() + "   *   fatherForm = " + request.getMyGate().getFatherForm());
//        System.out.println("fatherKEYvalue = " + Irequest.getMyGate().getFatherKEYvalue() + "   *   fatherKEYtype = " + request.getMyGate().getFatherKEYtype() + "   *   fatherCopyTag = " + request.getMyGate().getFatherCopyTag());
//        System.out.println("TBS received= " + Irequest.getMyGate().getTBS());
////////        System.out.println("\n*************\ncurKEYvalue= " + Irequest.getMyGate().getCurKEYvalue());
////////        System.out.println("sendToCRUD= " + Irequest.getMyGate().getSendToCRUD());
//        System.out.println("routine= " + Irequest.getMyGate().getRoutine()); 
        smartForm myForm = new smartForm(Irequest.getMyGate().getFormID(), Irequest.getMyParams(), Irequest.getMySettings());
        myForm.setName(Irequest.getMyGate().getFormToLoad());
        myForm.setID(Irequest.getMyGate().getFormID());
        myForm.setType(Irequest.getMyGate().getFormType());
        myForm.setFatherKEYvalue(Irequest.getMyGate().getFatherKEYvalue());
        myForm.setFatherKEYtype(Irequest.getMyGate().getFatherKEYtype());

        myForm.setFather(Irequest.getMyGate().getFatherForm());
        myForm.setFatherCopyTag(Irequest.getMyGate().getFatherCopyTag());
        myForm.setFatherFilters(Irequest.getMyGate().getFatherArgs());
        myForm.setLoadType(Irequest.getMyGate().getLoadType());
////////        System.out.println(" LoadType= " + Irequest.getMyGate().getLoadType());

        myForm.setInfoReceived(Irequest.getMyGate().getTBS());
        myForm.setCopyTag(Irequest.getMyGate().getCopyTag());
        // il keyValue è dato dalla riga EVIDENZIATA
        // però se sto cliccando un pulsante deve valere la riga del pulsante !!!
        // come ho implementato ?
        myForm.setCurKEYvalue(Irequest.getMyGate().getCurKEYvalue());
        myForm.setKEYfieldName(Irequest.getMyGate().getKeyField());
////////        System.out.println(" KEYfieldName= " + Irequest.getMyGate().getKeyField());
        myForm.setCurKEYtype(Irequest.getMyGate().getCurKEYtype());
        myForm.setSendToCRUD(Irequest.getMyGate().getSendToCRUD());
        return myForm;
    }

    public String standardReplace(String defVal, String radix) {

        if (radix != null && radix.length() > 0) {
            radix = radix.replaceAll("[^\\w\\d]", "");//toglie tutti i caratteri speciali
        } else {
            radix = "";
        }

        // System.out.println("Step 1 :" + defVal);
        int step = 0;
        try {
            step = 1;
            defVal = defVal.replace("$$$KEY$$$", Irequest.getMyGate().getCurrentKEY());
            step = 2;
            defVal = defVal.replace("$$$NOW$$$", " NOW() ");
            step = 3;
            defVal = defVal.replace("$$$TODAY$$$", " date(NOW()) ");
            step = 4;
            defVal = defVal.replace("$$$TIMENOW$$$", " CURTIME() ");

            step = 5;
            defVal = defVal.replace("$$$CKuserID$$$", Irequest.getMyGate().getMyParams().getCKuserID());
            step = 6;
            defVal = defVal.replace("$$$USER$$$", Irequest.getMyGate().getMyParams().getCKuserID());
            step = 7;
            defVal = defVal.replace("$$$userID$$$", Irequest.getMyGate().getMyParams().getCKuserID());
            step = 8;

            if (defVal.contains("$$$CURUSERNAME$$$")) {
                EVOuser myUser = new EVOuser(Irequest.getMyParams(), Irequest.getMySettings());
                myUser.loadDBinfos();
                String nomeUtente = myUser.getExtendedName();
                step = 9;
                defVal = defVal.replace("$$$CURUSERNAME$$$", nomeUtente);

            }
            step = 10;

            String newID = radix.trim() + "XXXXXXXXXXXXXXXXXX";
            UUID idOne = null;
            idOne = UUID.randomUUID();
            String newToken = "" + idOne;
            newToken = newToken.replace("-", "");
            step = 11;

            String RT10newID = newID.substring(0, 8) + newToken.substring(0, 8);
            RT10newID = RT10newID.replace("-", "");
            defVal = defVal.replace("$$$RANDOMTEXT10$$$", RT10newID);
            step = 12;

            String RT16newID = newID.substring(0, 16) + newToken.substring(0, 16);
            RT16newID = RT16newID.replace("-", "");
            defVal = defVal.replace("$$$RANDOMTEXT16$$$", RT16newID);
            step = 13;

            newID = newID.substring(0, 5) + "_" + newToken;
            newID = newID.replace("-", "");
            defVal = defVal.replace("$$$RANDOMTEXT$$$", newID);
            step = 14;

            defVal = defVal.replace("$$$newRandom$$$", newToken);

        } catch (Exception e) {
            System.out.println("CRUD ORDER:STEP" + step + ", ERROR standardReplace :" + e.toString());
            //System.out.println("step :" + step);

        }
        //   System.out.println("Step 1 RRESULT:" + defVal);

        //   System.out.println("Step 2 :");
        if (Irequest.getMyParams().getCKuserID() != null) {
            // if (query.contains("$$$RANGO$$$")){
            EVOuser myUser = new EVOuser(Irequest.getMyParams(), Irequest.getMySettings());
            int xRango;
            xRango = myUser.getActualRango(Irequest.getMyGate().getMyParams().getCKuserID());
            defVal = defVal.replace("$$$RANGO$$$", "" + xRango);
        }
        //    System.out.println("Step 2 RRESULT:" + defVal);

        // ora inizio il parsing delle info da StC
        String params = Irequest.getMyGate().getSendToCRUD();
        //System.out.println("Step 3 :" + params);

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        String TBSarray = null;
        String xValue = null;
        String xMarker = null;
        String xType = null;
        if (params != null && params.length() > 0) {
            String tbsJson = "{\"TBS\":" + params + "}";
            //      System.out.println(" tbsJson:" + tbsJson);
            try {
                jsonObject = (JSONObject) jsonParser.parse(tbsJson);
                TBSarray = jsonObject.get("TBS").toString();
                if (TBSarray != null && TBSarray.length() > 0) {
                    JSONParser parser = new JSONParser();
                    Object obj;

                    obj = parser.parse(TBSarray);
                    JSONArray array = (JSONArray) obj;

                    for (Object riga : array) {
                        jsonObject = (JSONObject) jsonParser.parse(riga.toString());

                        try {
                            xType = jsonObject.get("childType").toString();
                        } catch (Exception e) {
                        }

                        try {
                            xMarker = jsonObject.get("childMarker").toString();
                        } catch (Exception e) {
                        }
                        //       System.out.println(" xMarker:" + xMarker);
                        try {
                            xValue = jsonObject.get("value").toString();
                        } catch (Exception e) {
                        }

                        if (xValue != null && xMarker != null) {

                            if (xType.equalsIgnoreCase("formField")) {
                                String toBeReplaced = "###" + xMarker + "###";
                                if (defVal.contains(toBeReplaced)) {
                                    defVal = defVal.replace(toBeReplaced, xValue);
                                }
                            }
                            if (xType.equalsIgnoreCase("panelFilter")) {
                                String toBeReplaced = "###" + xMarker + "###";
                                if (defVal.contains(toBeReplaced)) {
                                    defVal = defVal.replace(toBeReplaced, xValue);
                                }
                            } else if (xType.equalsIgnoreCase("rowField")) {
                                String toBeReplaced = "@@@" + xMarker + "@@@";
                                if (defVal.contains(toBeReplaced)) {
                                    defVal = defVal.replace(toBeReplaced, xValue);
                                }
                            } else if (xType.equalsIgnoreCase("overall")) {

                            }
                        }
                        //  System.out.println(" xMarker:" + xMarker + " xValue:" + xValue);
                    }
                }
            } catch (ParseException ex) {
                System.out.println(" err3:" + ex);
            }
            //   System.out.println("Step 3 RRESULT:" + defVal);

        }
        //System.out.println(" REPLACE RESULT:" + defVal);

        return defVal;
    }
}
