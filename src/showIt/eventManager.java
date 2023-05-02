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
package showIt;

import REVOdbManager.EVOpagerDirectivesManager;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOmail.EmailSessionBean;
import REVOpack.ClassProjectUpdate;
import REVOpack.ClassQPmanageUpdate;
import REVOpager.EVOpagerDBconnection;
import REVOpager.EVOuser;
import REVOsetup.EVOsetup;
import REVOsetup.ErrorLogger;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import models.CRUDorder;
import models.IncomingRequest;
import models.Linker;
import models.SelectListLine;
import models.TomcatGaiaHost;
import models.logEvent;
import models.objectLayout;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import showIt.ShowItForm;
import showIt.jsFunctionServer;
import smartCore.smartForm;
import static smartCore.smartRow.makeRoundedCorner;

/**
 *
 * @author Franco
 */
public class eventManager {

    EVOpagerParams myParams;
    Settings mySettings;
    String partsString;
    String dummyMessage;
    ErrorLogger el;

    public eventManager(EVOpagerParams xParams, Settings xSettings) {
        this.myParams = new EVOpagerParams();
        this.myParams = xParams;
        this.mySettings = xSettings;
        el = new ErrorLogger(myParams, mySettings);
        el.setPrintOnScreen(false);
        el.setPrintOnLog(true);
    }

    public IncomingRequest DataBrowserFormShow(IncomingRequest request) {
        String HtmlCode = "";
        System.out.println("#####################Creo smartForm.");
        String CNTX = this.findExtension();
        myParams.setCKcontextID(CNTX);
        myParams.setCKprojectName(mySettings.getProjectName());
        String WindowTitle = "<title>" + request.getMySettings().getProjectName().toUpperCase() + " " + CNTX + "</title>";
        String formHtmlCode = "";
        String formID = "";
        HtmlCode += "<!DOCTYPE html>";
        HtmlCode += "<html>";
        HtmlCode += "<head>";
        HtmlCode += WindowTitle;
        HtmlCode += " <meta charset=\"utf-8\">\n";
        HtmlCode += " <meta http-equiv=\"Content-Security-Policy\"  content=\"connect-src * 'unsafe-inline';name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n";
// cerco una direttiva chiamata gaia.css

        HtmlCode += "<link rel=\"stylesheet\" href=\"stylesheet.css\" type=\"text/css\" charset=\"utf-8\" />\n";
        HtmlCode += "<link rel=\"stylesheet\" href=\"gaia.css\" type=\"text/css\" charset=\"utf-8\" />\n";
        HtmlCode += "<script type=\"text/javascript\" src=\"external/nicEdit/nicEdit.js\"></script>\n";
//        HtmlCode += codeJavaScripts();
        HtmlCode += codeVariables(formID, request.getMyParams());

        HtmlCode += ("</head>");
        HtmlCode += ("<body onload=\"startup()\" style=\"  width: 100%;  \">PUBLIC BODY");
        HtmlCode += codeHiddenInputs(myParams.makeJSONparams());
//        HtmlCode += codeWindowTopBar();
//        HtmlCode += codeWindowBody(formHtmlCode);
//        HtmlCode += codeWindowFooter();
//        HtmlCode += codeEndingScripts();
        HtmlCode += "</body>\n";
        HtmlCode += "</html>\n";

        request.setResponse(HtmlCode);
        return request;
    }

    public IncomingRequest MasterFormShow(IncomingRequest request) {

        if (request.getMyGate().controlNeeded) {
            if (request.getMyGate().getUserEnabled() < 1) {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\nACCESSO NON CONSENTITO A MasterFormShowRenderPic");
            }
        }

//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\n\n\n********\nMasterFormShow by Event Manager");
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "IP:      \t" + request.getRemoteIP());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "recorded:\t" + request.getRecorded());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "projectName:\t" + request.getMySettings().getProjectName());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "contextID:\t" + request.getMyParams().getCKcontextID());
        String HtmlCode = "";
        String dbCode = "";
        String WindowTitle = "";
        String CNTX = "";
        try {
            CNTX = "" + myParams.getCKcontextID().toUpperCase();
        } catch (Exception e) {
        }
        if (CNTX.length() < 1) {
            CNTX = " ";
        }

        ShowItForm myForm = new ShowItForm("", myParams, mySettings); //in realtà qui ho solo il NAME del form, ma ricaverò l'ID dal DB
//        System.out.println("NOME FORM DA CARICARE IN MAINFORMSHOW: " + request.getMyGate().getFormName());

//*****************************************************************
//**QUI SI DECIDE IL PRIMO FORM DA CARICARE: **********************
//*****DI NORMA SARA' MAINFORM MA POTREBBE ESSERE ACCOUNTMANAGER **
//*****OPPURE LASTNEWS MANAGER OPPURE UNO SPLASH DA FIRMARE********
//*****************************************************************
        myForm.setName(request.getMyGate().getFormName());

        EVOuser myUser = new EVOuser(myParams, mySettings);
        myUser.loadDBinfos();

        String nameShowed = myUser.getNameShowed();
        if (nameShowed == "") {
//            System.out.println("CARICO ACCOUNT MANAGER");
            myForm.setAbstractTextCode("Prima di procedere nel portale occorre compilare <B>tutti i campi</B> del form !");
            myForm.setName("accountManager");
        }

//        myForm.setName("accountManager");
        myForm.setID("");
        myForm.setFather("");
        myForm.setFatherFilters("");
        myForm.setLoadType(request.getMyGate().getLoadType());
        myForm.setMyParams(myParams);
        myForm.setMySettings(mySettings);

        myForm.getFormInformationsFromDB(); // per mainFOrm questo non serviva e non so il perchè
//        System.out.println("firstForm ID ricavato da getFormInformationsFromDB: " + myForm.getID()); 

        System.out.println("\n\nfirstForm  : " + myForm.getType());
        String formHtmlCode = "";
        String formID = "";
        String firstForm = "";
        if (myForm.getType() != null && myForm.getType().equalsIgnoreCase("SMARTPANEL")) {
            smartForm mySmartForm = new smartForm("", myParams, mySettings);
            mySmartForm.setAbstractTextCode(myForm.getAbstractTextCode());
            mySmartForm.setName(myForm.getName());
            mySmartForm.setID("");
            mySmartForm.setFather("");
            mySmartForm.setFatherFilters("");
            mySmartForm.setLoadType("{\"type\":\"SMARTPANEL\","
                    + "\"visualType\":\"FULLFORM\","
                    + "\"firstRow\":\"1\","
                    + "\"NofRows\":\"50\","
                    + "\"currentPage\":\"1\","
                    + "\"visualFilter\":\"\"}");
            mySmartForm.setMyParams(myParams);
            mySmartForm.setMySettings(mySettings);
            mySmartForm.buildSchema();
            smartForm.smartFormResponse myFormResponse = mySmartForm.paintForm();
            firstForm = myFormResponse.getHtmlCode();
        } else {
            firstForm = myForm.paintForm().getHtmlCode();
        }
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(firstForm);
            try {
                formID = jsonObject.get("formID").toString();
                formID = java.net.URLDecoder.decode(formID, "UTF-8");
            } catch (Exception ex) {
            }
            try {
                formHtmlCode = ((String) jsonObject.get("htmlCode"));
                formHtmlCode = java.net.URLDecoder.decode(formHtmlCode, "UTF-8");
            } catch (Exception ex) {
            }

        } catch (org.json.simple.parser.ParseException pe) {
        }
        WindowTitle = "<title>" + request.getMySettings().getProjectName().toUpperCase() + " " + CNTX + "</title>";

        HtmlCode += "<!DOCTYPE html>";
        HtmlCode += "<html>";
        HtmlCode += "<head>";
        HtmlCode += WindowTitle;
        HtmlCode += " <meta charset=\"utf-8\">\n";
        HtmlCode += " <meta http-equiv=\"Content-Security-Policy\"  content=\"connect-src * 'unsafe-inline';name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n";

// cerco una direttiva chiamata gaia.css
        EVOpagerDirectivesManager myDirective = new EVOpagerDirectivesManager(request.getMyParams(), request.getMySettings());
        dbCode = myDirective.getDirective("gaia.inclusions");
        if (dbCode != null && !dbCode.trim().equals("")) {
            HtmlCode += (dbCode);
        }
        HtmlCode += "<link rel=\"stylesheet\" href=\"stylesheet.css\" type=\"text/css\" charset=\"utf-8\" />\n";
        HtmlCode += "<link rel=\"stylesheet\" href=\"gaia.css\" type=\"text/css\" charset=\"utf-8\" />\n";
        HtmlCode += "<script type=\"text/javascript\" src=\"external/nicEdit/nicEdit.js\"></script>\n";
        HtmlCode += codeJavaScripts();
        HtmlCode += codeVariables(formID, request.getMyParams());

        HtmlCode += ("</head>");
        HtmlCode += ("<body onload=\"startup()\" style=\"  width: 100%;  \">");
        HtmlCode += codeHiddenInputs(myParams.makeJSONparams());
        HtmlCode += codeWindowTopBar();
        HtmlCode += codeWindowBody(formHtmlCode);
        HtmlCode += codeWindowFooter();
        HtmlCode += codeEndingScripts();
        HtmlCode += "</body>\n";
        HtmlCode += "</html>\n";

        request.setResponse(HtmlCode);
        return request;
    }

    private String codeHiddenInputs(String jsonParams) {
        String HtmlCode = "";
        HtmlCode += ("<INPUT type='hidden' id='txtParams'  />");
        HtmlCode += ("<INPUT type='hidden' id='portalParams'  />");
        HtmlCode += ("<INPUT type='hidden' id='WSstatus' value=\"DISCONNECTED\"  />");
        HtmlCode += ("<INPUT type='hidden' id='WStoken'  />");
        HtmlCode += ("<INPUT type='hidden' id='WSsessionID'  />");
        HtmlCode += ("<INPUT type='hidden' id='WSclientId'  />");
        HtmlCode += ("<INPUT type=\"HIDDEN\" id=\"splash-type\" value=\"\">\n");
        HtmlCode += ("<INPUT type=\"HIDDEN\" id=\"splash-rifForm\" value=\"\">\n");
        HtmlCode += ("<INPUT type=\"HIDDEN\" id=\"splash-copyTag\" value=\"\">\n");
        HtmlCode += ("<INPUT type=\"HIDDEN\" id=\"splash-keyValue\" value=\"\">\n");
        HtmlCode += ("<INPUT type=\"HIDDEN\" id=\"splash-args\" value=\"\">\n");
        HtmlCode += ("<INPUT type=\"HIDDEN\" id=\"master-params\" value=\"" + encodeURIComponent(jsonParams) + "\">\n");
        return HtmlCode;
    }

    private String codeEndingScripts() {
        String HtmlCode = "";
        HtmlCode += "<script>"
                + "$(document).ready(function() {   });\n"
                //---------------
                + "$('body').on('focus',\".datetimepickerclass\", function() {\n"
                + "    $(this).datetimepicker({\n"
                + "   step:60,\n"
                + "monthChangeSpinner:true,\n"
                + "closeOnDateSelect:false,\n"
                + "closeOnWithoutClick:true,\n"
                + "closeOnInputClick: true,\n"
                + "timepicker:true,\n"
                + "datepicker:true,\n"
                + "defaultTime:false,\n"// use formatTime format (ex. '10:00' for formatTime:	'H:i')
                + "defaultDate:false,\n" // use formatDate format (ex new Date() or '1986/12/08' or '-1970/01/05' or '-1970/01/05')
                + "formatDate:'yyyy-MM-dd',\n"
                + "minDate:false,\n"
                + "maxDate:false,\n"
                + "minTime:false,\n"
                + "maxTime:false,\n"
                + "allowTimes:[],\n"
                + "opened:false,\n"
                + "initTime:true,\n"
                + "inline:false,\n"
                + "onSelectDate:function() {},\n"
                + "onSelectTime:function() {},\n"
                + "onChangeMonth:function() {},\n"
                + "onChangeDateTime:function() {},\n"
                + "onShow:function() {},\n"
                + "onClose:function() {},\n"
                + "onGenerate:function() {},\n"
                + "withoutCopyright:true,\n"
                + "inverseButton:false,\n"
                + "hours12:false,\n"
                + "next:	'xdsoft_next',\n"
                + "prev : 'xdsoft_prev',\n"
                + "dayOfWeekStart:0,\n"
                + "timeHeightInTimePicker:25,\n"
                + "todayButton:true,\n" // 2.1.0
                + "defaultSelect:true,\n" // 2.1.0
                + "scrollMonth:true,\n"
                + "scrollTime:true,\n"
                + "scrollInput:true,\n"
                + "lazyInit:false,\n"
                + "mask:false,\n"
                + "validateOnBlur:true,\n"
                + "allowBlank:true,\n"
                + "yearStart:2000,\n"
                + "yearEnd:2100,\n"
                + "style:'',\n"
                + "id:'',\n"
                + "fixed: false,\n"
                + "roundTime:'round',\n" // ceil, floor
                + "className:'',\n"
                + "weekends	: 	[],\n"
                + "yearOffset:0,\n"
                + "beforeShowDay: null"
                + "    });\n"
                + "});"
                //--------------------
                + "$('body').on('focus',\".timepickerclass\", function() {\n"
                + "    $(this).timepicker({\n"
                + "'scrollDefault': 'now',"
                + "'timeFormat': 'H:i:s'"
                + "});"
                + "});"
                + "$('body').on('focus',\".datepickerclass\", function(){\n"
                //QUESTO E' QUELLO CHE ASSEGNA VERAMENTE IL FORMATO DAL PICKER
                + "    $(this).datepicker({ dateFormat: 'dd/mm/yy' });\n"
                + "});"
                + "$('body').on('focus',\".datepickerfilter\", function(){\n"
                + "    $(this).datepicker({ dateFormat: 'dd/mm/yy' });\n"
                + "});"
                //-------
                ////////                + "$('body').on('focus',\".richTextClass\", function(){\n" 
                ////////                + "var objX = $(this).attr(\"id\");"
                ////////                + "console.log(\"rich text area focused:\"+objX); "
                ////////                //                + "rtfArea.removeInstance('myArea1');\n"
                ////////                //                + "rtfArea = null;\n"
                ////////                + "rtfArea = new nicEditor({fullPanel : true}).panelInstance(objX,{hasPanel : true});\n"
                ////////                //                + "    nicEditors.allTextAreas();"
                ////////                + "});\n"
                ////////                                //-------
                + "$('body').on('focusin',\".richTextClass\", function(){\n"
                + "var objX = $(this).attr(\"id\");"
                + "console.log(\"rich text area focusedIN:\"+objX); "
                + "rtfArea = new nicEditor({fullPanel : true}).panelInstance(objX,{hasPanel : true});\n"
                + "});\n"
                //-------
                + "$('body').on('focusout',\".richTextClass\", function(){\n"
                + "var objX = $(this).attr(\"id\");"
                + "console.log(\"rich text area focusedOUT:\"+objX); "
                + "rtfArea.removeInstance(objX);\n"
                + "});\n"
                + "</script>\n";
        return HtmlCode;
    }

    private String codeWindowFooter() {
        String HtmlCode = "";
        HtmlCode += "<div id=\"snackbar\"></div>";

        HtmlCode += " <div id=\"splashPanel\" class=\"modalDialog\" >\n"
                + "     <div><a href=\"#close\" title=\"Close\" class=\"close\">X</a>\n"
                + "         <div id=\"selectors\"><p></p></div>"
                + "     </div>\n"
                + "     </div>\n";

        HtmlCode += ""
                + "<div class=\"lightbox\" id=\"dropPanel\">\n"
                + "  <figure>\n"
                + "    <a href=\"#\" class=\"close\"></a>\n"
                + "    <figcaption id=\"dropPanelFigcaption\">"
                + "         <div style=\"width:100%;height:100%;overflow: auto;\" id=\"dropSpace\"  ></div>"
                + "  </figure>\n"
                + "</div>"
                + ""
                + "";
        HtmlCode += " <div id=\"suggesterPanel\" class=\"suggester\" >\n"
                + "<a href=\"#\" class=\"dropPanelClose\"> "
                + "	<div>\n"
                + " <a href=\"#suggesterclose\" title=\"Close\" class=\"close\">X</a>\n"
                + " <div id=\"selectors1\"  ><p></p></div>"
                + "</div>\n"
                + "</div>";

        HtmlCode += " <div id = \"contextmenu\" "
                + "class=\"contxtmenu\"  "
                + "onmouseout=\"javascript:setTreeContextMenuPosition('hide','','');\"></div>";
        HtmlCode += "<div id=\"info-box\" class=\"infobox\" style=\"display:none\"></div>\n";
        return HtmlCode;
    }

    private String codeWindowBody(String formHtmlCode) {
        String HtmlCode = "";
        HtmlCode += ("<DIV  "
                + "style=\"height: 100px; width:100%; "
                //                + "padding: 16px;\n"
                + "  margin-top: 80px;"
                + "display:block;\""
                + ">");
        HtmlCode += (" <TABLE style=\"height: 100px; width:100%;"
                + "  border-collapse: collapse;\n"
                + "    margin: 0px 0px 0px 0px;\n"
                + "    padding: 0px 0px 0px 0px;"
                + "background-color: white;\"><TR>");
        HtmlCode += ("<TD style=\"height: 100%; width:100%;\">");
        //***********************************************    
        HtmlCode += (" <div id=\"mainFormSpace\">");
        HtmlCode += (formHtmlCode);
        HtmlCode += (" </div>");
        //***********************************************   

        HtmlCode += ("</TD><TD>");

        HtmlCode += (" <div id=\"mainRightSpace\">");//mainRightSpace
        HtmlCode += ("<table><tr>");
        HtmlCode += ("<tr><td><div id=\"contactsSpace\"> </div><td><tr>");
        HtmlCode += ("</tr><tr>");
        HtmlCode += ("<tr><td><div id=\"formsMapSpace\"> </div><td><tr>");
        HtmlCode += ("</tr></table>");
        HtmlCode += ("</div>");//mainRightSpace

        HtmlCode += ("</TD></TR>");
        HtmlCode += ("<TR><TD colspan=\"2\">");
        HtmlCode += (" <div id=\"mainBottomSpace\" ></div>\n");
        HtmlCode += ("</TD></TR> ");
        HtmlCode += ("</TABLE>");
        HtmlCode += ("</DIV>");
        return HtmlCode;
    }

    private String codeWindowTopBar() {
        String HtmlCode = "";
        HtmlCode += ("<DIV  "
                + "style=\"height: 75px; width: 100%;display:block; padding: 2px;z-index: 99999;position: fixed; top: 0; overflow: hidden;"
                + " background-color: white; vertical-align: text-top;\n"
                + " \">");
        HtmlCode += ("<TABLE "
                + "style=\"height: 75px; width:100%;"
                //                + "padding: 0px;\n"
                //                + " position: fixed; top: 0; \" "
                + "background-color: white;vertical-align: text-top;\"><TR>");

        //---SESSION SPACE con ACCOUNT MANAGER-----------
        HtmlCode += ("<TD style=\"width:20%;align:right;overflow:hidden;white-space:nowrap;\">");
        HtmlCode += ("<DIV id=\"SessionSpace\" style=\"height:50px;width:100%; display:block;\"> </DIV>");
        HtmlCode += ("</TD>");
        //---TITLE BAR SPACE -----------
        HtmlCode += ("<TD style=\"width:40%;align:right;overflow:hidden;white-space:nowrap;vertical-align: top;\">");
        HtmlCode += ("<DIV id=\"titleBarSpace\" style=\"height:50px;width:100%; vertical-align: text-top; display:block;\"> </DIV>");
        HtmlCode += ("</TD>");

        //---LIGHTHOUSE CONNECTION SPACE -----------
        HtmlCode += ("<TD style=\"width:20%;align:left;overflow:hidden;white-space:nowrap;\">");
        HtmlCode += ("<DIV id=\"LHWSspace\" style=\"height:50px;width:100%; display:block;\"> </DIV>");
        HtmlCode += ("</TD>");
        //---LOCAL SERVER CONNECTION SPACE -----------
        HtmlCode += ("<TD style=\"width:20%;align:left;overflow:hidden;white-space:nowrap;\">");
        HtmlCode += ("<DIV id=\"WSspace\" style=\"height:50px;width:100%; display:block;\"> </DIV>");
        HtmlCode += ("</TD>");
        HtmlCode += ("</TR>"
                + "<TR  style=\"border-bottom: 1px solid red\"><TD></TD></TR>"
                + "</TABLE>");

        HtmlCode += ("</DIV>");
        return HtmlCode;
    }

    private String codeVariables(String formID, EVOpagerParams myParams) {
        String HtmlCode = "";
        HtmlCode += ("<script>");
        HtmlCode += ("var map;  ");
        HtmlCode += ("var activeForms = [{ level:0, father:'' , id:'" + formID + "', name:'mainForm', space:'', key:''}];");
        HtmlCode += ("var myObj = " + myParams.makeJSONparams() + ";");
        HtmlCode += ("var overallProjectName=\"" + myParams.getCKprojectName() + "\";\n");
        HtmlCode += ("var contextID=\"" + myParams.getCKcontextID() + "\";\n");
        HtmlCode += ("var CKtokenID=\"" + myParams.getCKtokenID() + "\";\n");
        HtmlCode += ("var PORTALparams = " + myParams.makePORTALparams() + ";\n");
        HtmlCode += ("var overallProjectContext = \"" + myParams.getCKcontextID() + "\";");
        HtmlCode += ("var WSactive =1  ;");
        HtmlCode += ("var rtfArea  ;");
        HtmlCode += ("var pinMarkers = {};");
        HtmlCode += ("var lastDraggedObj;");
        HtmlCode += ("var audioToken;");

        HtmlCode += ("</script>");
        return HtmlCode;
    }

    private String codeJavaScripts() {
        String HtmlCode = "";
        jsFunctionServer JSfs = new jsFunctionServer(myParams, mySettings);
        // SOLO SE USO AUDIO...
        if (mySettings.isUsesAudioRec()) {
            HtmlCode += "<script type=\"text/javascript\" src=\"audioRecord/js/recorder.js\"></script>\n";
            HtmlCode += "<script type=\"text/javascript\" src=\"audioRecord/js/app.js\"></script>\n";
        }
        if (mySettings.isUsesGeoMap()) {
            HtmlCode += (JSfs.getSmartGeomapHandler());
        }

        HtmlCode += "<script>";
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Carico gli script JS dal DB.");
        //dbCode = myDirective.getDirective("gaia.variables"); 
        HtmlCode += (JSfs.getGaiaVariables());
        //dbCode = myDirective.getDirective("gaia.function.openWS");
        HtmlCode += (JSfs.getWebsocketJS());
        //dbCode = myDirective.getDirective("gaia.function.manageSplash");
        HtmlCode += (JSfs.getManageSplash());
        //dbCode = myDirective.getDirective("gaia.function.manageUpDownload");
        HtmlCode += (JSfs.getManageUpDownload());
        //dbCode = myDirective.getDirective("gaia.function.manageTBS");
        HtmlCode += (JSfs.getManageTBS());
        //dbCode = myDirective.getDirective("gaia.function.manageUserActions");           
        HtmlCode += (JSfs.getPagerUserActions());
        //dbCode = myDirective.getDirective("gaia.function.manageAutoActions");           
        HtmlCode += (JSfs.getPagerAutoActions());
        //dbCode = myDirective.getDirective("gaia.function.serviceFunctions");            
        HtmlCode += (JSfs.getServiceFunctions());
        HtmlCode += (JSfs.getManageContextMenu());
        HtmlCode += (JSfs.getManageDoubleClickable());
        HtmlCode += (JSfs.getMapsJS());
        HtmlCode += (JSfs.getManageAdvancedFilter());
        HtmlCode += (JSfs.getSmartWebsocketRoutines());
        HtmlCode += ("var jqueryFunction;\n"
                + "\n"
                + "$().ready(function(){\n"
                + "//jQuery function\n"
                + "    jqueryFunction = function( _msg )\n"
                + "    {\n"
                + "                $('#date-pick').datepicker({\n"
                + "                    showOn: \"button\",\n"
                + "                    dateFormat: \"dd/mm/yy\",\n"
                + "                    onSelect: function() { \n"
                + "                    }\n"
                + "                });\n"
                //---
                + "                $('#date-pick').datepicker({\n"
                + "                    showOn: \"button\",\n"
                + "                    dateFormat: \"dd/mm/yy\",\n"
                + "                    onSelect: function() { \n"
                + "                    }\n"
                + "                });\n"
                //--
                + "    }\n"
                + "})\n"
                + "\n"
                + "//javascript function\n"
                + "function jsFunction()\n"
                + "{\n"
                + "    //Invoke jQuery Function\n"
                + "    jqueryFunction(\"Call from js to jQuery\");\n"
                + "}");
        HtmlCode += "</script>";
        return HtmlCode;
    }

    public IncomingRequest logout(IncomingRequest request) {
        logEvent myEvent = new logEvent();
        myEvent.setType("Logout");
        myEvent.setUser(myParams.getCKuserID());
        myEvent.setToken(myParams.getCKtokenID());

        System.out.println("\n*\neventManager-->SONO IN logout." + myParams.getCKuserID());
        String HtmlCode = "";
        myParams = request.getMyParams();
        mySettings = request.getMySettings();
        String token = myParams.getCKtokenID();
        String TABLEtokens = mySettings.getAccount_TABLEtokens();

        if (token != null && token != "null") {
            Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
            try {
                String SQLphrase;
                PreparedStatement ps;
                SQLphrase = "UPDATE `" + TABLEtokens + "` SET `loggedStatus`= 0  WHERE rifUser='" + myParams.getCKuserID() + "' AND token='" + myParams.getCKtokenID() + "'";
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "EVOLLogout ->SQLphrase:" + SQLphrase);
                ps = accountConny.prepareStatement(SQLphrase);
                try {
                    int i = ps.executeUpdate();
                } catch (Exception e) {
                    SQLphrase = "UPDATE `" + TABLEtokens.toLowerCase() + "` SET `loggedStatus`= 0  WHERE rifUser='" + myParams.getCKuserID() + "' AND token='" + myParams.getCKtokenID() + "'";
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "EVOLLogout ->SQLphrase:" + SQLphrase);
                    ps = accountConny.prepareStatement(SQLphrase);
                    try {
                        int i = ps.executeUpdate();
                    } catch (Exception ee) {
                    }

                }

                SQLphrase = "UPDATE `" + TABLEtokens + "` SET `token`= 'OVER_" + myParams.getCKtokenID() + "'  WHERE rifUser='" + myParams.getCKuserID() + "' AND token='" + myParams.getCKtokenID() + "'";
                System.out.println("\n*\neventManager-->SONO IN logout." + SQLphrase);
                ps = accountConny.prepareStatement(SQLphrase);
                try {
                    int i = ps.executeUpdate();
                } catch (Exception e) {
                    SQLphrase = "UPDATE `" + TABLEtokens.toLowerCase() + "` SET `token`= 'OVER_" + myParams.getCKtokenID() + "'  WHERE rifUser='" + myParams.getCKuserID() + "' AND token='" + myParams.getCKtokenID() + "'";
                    System.out.println("\n*\neventManager-->SONO IN logout." + SQLphrase);
                    ps = accountConny.prepareStatement(SQLphrase);
                    try {
                        int i = ps.executeUpdate();
                    } catch (Exception ee) {
                    }
                }
                accountConny.close();
            } catch (SQLException ex) {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Error:" + ex);
            }
            myEvent.setEventCode("LogoutOK");
        } else {
            myEvent.setEventCode("tokenNotFound");
        }

        try {
            myEvent.save(myParams, mySettings);
        } catch (Exception e) {
        }
        return request;
    }

    public IncomingRequest createMailAccount(IncomingRequest request) {
        el.setPrintOnScreen(true);
        String tabOperatori = mySettings.getAccount_TABLEoperatori();
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\nSONO IN-> createMailAccount");
        myParams = request.getMyParams();
        mySettings = request.getMySettings();

        String username = request.getMyGate().getUsername();
        String email = request.getMyGate().getEmail();
        String remoteIP = request.getRemoteIP();
        String pass = request.getMyGate().getPassword();
        String name = request.getMyGate().getName();
        String surname = request.getMyGate().getSurname();

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\nSONO IN-> createAccount");
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "createAccount; username:" + username + " - email:" + email + " - pass:" + pass);

        String HtmlCode = "";
        UUID idOne = null;
        String sender = null;
        String operation = "createAccount";
        String code = "";
        String mess = "";
        String newID = "";
        String action = "createAccount";
        //1. verifico che la mail non sia già esistente

        String SQLphrase;
        PreparedStatement ps;
        ResultSet rs;

        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
        SQLphrase = "SELECT * FROM " + tabOperatori + " WHERE email = '" + email + "'";

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "createAccount SQLphrase: " + SQLphrase);
        int lines = 0;

        try {
            ps = accountConny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                lines++;
                String user = rs.getString("ID");
            }
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\nESISTENZA email:->" + lines);
            if (lines > 0) {
                code = "ERR";
                mess = "Esiste già un utente con questo indirizzo mail.";
                String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
                String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                        + "\"event\":\"newAccount\","
                        + "\"type\":\"accountExists\" }]";
                String utils = "\"responseType\":\"text\"";
                String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
                String newPage = "portal?target=requestsManager&gp=" + encodeURIComponent(gp);
                request.setOutputStreamType("standard");
                request.setResponse(newPage);
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Esco per email già presente:" + newPage);
                return request;
            } else {

                String radiceMail = "";
                try {
                    radiceMail = email.substring(0, email.indexOf("@"));
                    radiceMail += "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
                    radiceMail = radiceMail.substring(0, 6);
                } catch (Exception e) {
                }

                System.out.println("IradiceMail: " + radiceMail);
                lines = 1;
                String tempID = "";
                String xID = "";
                while (lines == 1) {
                    lines = 0;
                    idOne = UUID.randomUUID();

                    tempID = "" + idOne + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
                    tempID = tempID.replace("&", "");
                    tempID = tempID.replace("-", "");
                    xID = radiceMail + tempID.substring(0, 32);
                    xID = xID.substring(0, 32);
                    System.out.println("xID: " + xID);
                    SQLphrase = "SELECT * FROM " + tabOperatori + " WHERE ID = '" + xID + "'";
                    ps = accountConny.prepareStatement(SQLphrase);
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        lines++;
                    }
                }

                String pwKEY = mySettings.getPasswordKey("");
                System.out.println("Inserisco utente con password:" + pass + " e key: " + pwKEY);
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n xID:->" + xID);
                SQLphrase = "INSERT INTO " + tabOperatori + " ("
                        + "ID, "
                        + "alive, "
                        + "email, "
                        + "username, "
                        + "password,"
                        + "name,"
                        + "surname"
                        + ") VALUES ("
                        + "'" + xID + "',"
                        + "-1,"
                        + "'" + email + "',"
                        + "'" + username + "',"
                        + "AES_ENCRYPT('" + pass + "', '" + pwKEY + "'), "
                        + "'" + name + "',"
                        + "'" + surname + "'"
                        + ")";
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "INSERIMENTO UTENTE-> " + SQLphrase + " ");
                ps = accountConny.prepareStatement(SQLphrase);
                int i = ps.executeUpdate();

// adesso invio la mail che deve rimandarmi il token ID corretto
                if (i > 0) {
                    code = "OK";
                    mess = "Registrato.";

                    String mitt = "info@gaiaweb.cloud";
                    String dest = email;
                    // la prossima riga è da commentare
                    //dest = "francovenezia@ffs.it";

                    EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
                    String ApplicationWebURL = myManager.getDirective("ApplicationWebURL");
//                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ApplicationWebURL:" + ApplicationWebURL);

                    String confirmAddress = ApplicationWebURL;

                    String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
                    String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                            + "\"event\":\"confirmNewAccount\","
                            + "\"token\":\"" + xID + "\","
                            + "\"email\":\"" + email + "\","
                            + "\"username\":\"" + username + "\","
                            + "\"projectName\":\"" + mySettings.getProjectName() + "\","
                            + "\"projectGroup\":\"" + myParams.getCKprojectGroup() + "\""
                            + "  }]";
                    String utils = "\"responseType\":\"text\"";
                    String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
                    String newPage = "portal?target=requestsManager&gp=" + encodeURIComponent(gp);
                    confirmAddress += newPage;

                    String oggetto = "Registrazione in QUEENPRO " + mySettings.getSoftwareTitle();
                    String body = "<html><h1>Benvenuto,</h1><p>Grazie per esserti registrato !</p></html>"
                            + "</br></br>Per poter completare l'attivazione del tuo account occrre cliccare su questo link:</br>"
                            + "<a href=\"" + confirmAddress + "\">Conferma !</a>"
                            + "</br></br>in alternativa puoi copiare questo link sulla barra degli indirizzi del tuo browser:</br>"
                            + "" + confirmAddress
                            + ""
                            + ""
                            + "";
                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Tento invio mail " + dest);
                    EmailSessionBean mailer = new EmailSessionBean();
                    mailer.SendEmail(dest, oggetto, body);

                } else {
                    code = "ERR";
                    mess = "Errore in registrazione nuovo uente.";
                }

            }

            accountConny.close();

        } catch (SQLException ex) {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERRORE IN createAccount " + ex.toString());
        }

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "CONCLUSA CREAZIONE ACCOUNT-> " + HtmlCode + " ");

        String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
        String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                + "\"event\":\"LoginForm\","
                + "\"type\":\"accountCreated\" }]";
        String utils = "\"responseType\":\"text\"";
        String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
        String newPage = "portal?target=requestsManager&gp=" + encodeURIComponent(gp);
        request.setOutputStreamType("standard");
        request.setResponse(newPage);

        return request;

    }

    public IncomingRequest createMailPW(IncomingRequest request) {

        String tabOperatori = mySettings.getAccount_TABLEoperatori();
        el.setPrintOnScreen(true);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\nSONO IN-> createMailPW");
        myParams = request.getMyParams();
        mySettings = request.getMySettings();
        UUID idOne = null;

        String username = request.getMyGate().getUsername();
        String email = request.getMyGate().getEmail();

        //1. verifico che la mail non sia già esistente
        String SQLphrase;
        PreparedStatement ps;
        ResultSet rs;

        Connection accountConny = new EVOpagerDBconnection(request.getMyParams(), request.getMySettings()).ConnAccountDB();
        SQLphrase = "SELECT * FROM " + tabOperatori + " WHERE email = '" + email + "'";

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "createMailPW SQLphrase: " + SQLphrase);
        int lines = 0;
        String user = "";
        try {
            ps = accountConny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                lines++;
                user = rs.getString("ID");
            }

            // email
            //user
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\nESISTENZA email:->" + lines);
            if (lines > 0) {
                // l'account esiste... posso procedere a creare il token e inviare la mail
                //1.CREO IL TOKEN
                lines = 0;
                idOne = UUID.randomUUID();
                String tempID = "" + idOne;
                tempID = tempID.replace("&", "");

                SQLphrase = "INSERT INTO `archivio_timedTokens`(`token`, `lifeInSeconds`, `info1`, `info2`) "
                        + "VALUES (?,?,?,?)";
                //2.SALVO IL TOKEN NEL DB
                ps = accountConny.prepareStatement(SQLphrase);
                ps.setString(1, tempID);
                ps.setInt(2, 300);
                ps.setString(3, email);
                ps.setString(4, user);

                int i = ps.executeUpdate();

// adesso invio la mail che deve rimandarmi il token ID corretto
                if (i > 0) {
                    //code = "OK";
                    //mess = "Registrato.";

                    String mitt = "info@queenpro.it";
                    String dest = email;
                    // la prossima riga è da commentare
                    //dest = "francovenezia@ffs.it";

                    EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
                    String ApplicationWebURL = myManager.getDirective("ApplicationWebURL");
                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nApplicationWebURL:" + ApplicationWebURL + "\n\n");

                    String confirmAddress = ApplicationWebURL;
                    String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
                    String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                            + "\"event\":\"newPasswordForm\","
                            + "\"token\":\"" + tempID + "\","
                            + "\"email\":\"" + email + "\","
                            + "\"username\":\"" + username + "\","
                            + "\"projectName\":\"" + mySettings.getProjectName() + "\","
                            + "\"projectGroup\":\"" + myParams.getCKprojectGroup() + "\""
                            + "  }]";
                    String utils = "\"responseType\":\"text\"";
                    String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
                    String newPage = "portal?target=requestsManager&gp=" + encodeURIComponent(gp);
                    confirmAddress += newPage;

                    String oggetto = "Modifica password.";
                    String body = "<html><h1>Ciao</h1><p> Grazie per esserti registrato !</p></html>"
                            + "</br></br>Per poter completare la modifica della password occrre cliccare su questo link entro 10 minuti:</br>"
                            + "<a href=\"" + confirmAddress + "\">Conferma !</a>"
                            + "</br>in alternativa puoi copiare questo link sulla barra degli indirizzi del tuo browser:</br>"
                            + "" + confirmAddress
                            + ""
                            + ""
                            + "";
                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Tento invio mail " + dest);
                    EmailSessionBean mailer = new EmailSessionBean();
                    try {
                        mailer.SendEmail(dest, oggetto, body);
                    } catch (Exception e) {
                        System.out.println("Errore in invio mail-->  " + e.toString());
                    }
                    System.out.println("INVIO CONCLUSO.  ");
                } else {
//                    code = "ERR";
//                    mess = "Errore in invio mail.";
                }

                accountConny.close();

            } else {
                // non esiste la mail
                // però non devo renderlo noto, per cui il messaggio sarà
                // in ogni caso: inviata mail di recupero password

            }
            accountConny.close();
        } catch (SQLException ex) {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERRORE IN createMailPW " + ex.toString());
        }

        String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
        String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                + "\"event\":\"LoginForm\","
                + "\"type\":\"emailSent\" }]";
        String utils = "\"responseType\":\"text\"";
        String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
        String newPage = "portal?target=requestsManager&gp=" + encodeURIComponent(gp);
        request.setOutputStreamType("standard");
        request.setResponse(newPage);

        return request;
    }

    public IncomingRequest confirmNewAccount(IncomingRequest request) {
        String tabOperatori = mySettings.getAccount_TABLEoperatori();
        System.out.println("SONO IN confirmNewAccount");
        String email = request.getMyGate().getEmail();
        String password = request.getMyGate().getPassword();
        String token = request.getMyGate().getToken();

        myParams = request.getMyParams();
        mySettings = request.getMySettings();
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\n---\nconfirmNewPassword-->email = " + email);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "confirmNewPassword-->token = " + token);

        System.out.println("\n\n---\nconfirmNewAccount-->email = " + email);
        String HtmlCode = "";
        String code = "";
        String mess = "";
        String extension = findExtension();
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "confirmNewAccount-->  trova in queenpro la voce CKextension :" + extension);

        // devo impostare il valore di ALIVE su 1 se NON si richiede l'autorizzazione dell'admin
        // su 0 se si richiede l'autorizzazione dell'admin
        int newStatusValue = 0;
        if (mySettings.getAdminConfirmationRequested() != null
                && mySettings.getAdminConfirmationRequested().equalsIgnoreCase("true")) {

        } else {
            newStatusValue = 1;
        }
        System.out.println("\n\n---\nconfirmNewAccount-->email = " + email);

        String SQLphrase;
        PreparedStatement ps;
        ResultSet rs;

        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
        SQLphrase = "SELECT * FROM  " + tabOperatori + "  WHERE ID ='" + token + "'";

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "confirmNewAccount SQLphrase: " + SQLphrase);
        int lines = 0;
        String DBusername = "";
        String DBemail = "";
        int DBalive = -1;
        String DBrecorded = "";
        try {
            ps = accountConny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();

            //1 verifico utente esistente e in sospeso
            while (rs.next()) {
                lines++;
                DBusername = rs.getString("username");
                DBemail = rs.getString("email");
                DBalive = rs.getInt("alive");
                DBrecorded = rs.getString("recorded");
            }
            if (lines < 1) {// non trovo questa registrazione.. return con messaggio errore
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nconfirmNewAccount ERRORE: non trovo questa registrazione..");
                code = "confirmNotFound";
                mess = "Conferma di registrazione non eseguita correttamente.";
            } else {
                // verifico il lasso di tempo tra la registrazione e la conferma
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
                Date recordedDate = null;
                try {
                    recordedDate = format.parse(DBrecorded);
                } catch (ParseException ex) {
                }
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String curDate = format0.format(cal.getTime());

                Calendar c1 = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                c1.setTime(recordedDate);
                c2.setTime(cal.getTime());

                long giorni = (c2.getTime().getTime() - c1.getTime().getTime()) / (24 * 3600 * 1000);
                long ore = (c2.getTime().getTime() - c1.getTime().getTime()) / (3600 * 1000);
                long minuti = (c2.getTime().getTime() - c1.getTime().getTime()) / (60 * 1000);

                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nSono passati " + minuti + " minuti dalla registrazione");
                if (minuti < 30) {
                    SQLphrase = "UPDATE " + tabOperatori + " SET alive = " + newStatusValue + " WHERE ID ='" + token + "'";
                    ps = accountConny.prepareStatement(SQLphrase);
                    int i = ps.executeUpdate();
                    code = "confirmOK";
                    mess = "Conferma di registrazione eseguita correttamente.";

                } else {
                    // in caso di utente bloccato vado a -1. Per essere eliminato devo essere a meno di -1
                    SQLphrase = "DELETE FROM " + tabOperatori + "   WHERE ID ='" + token + "' AND alive < -1 ";
                    ps = accountConny.prepareStatement(SQLphrase);
                    int i = ps.executeUpdate();
                    code = "confirmTimeout";
                    mess = "Timeout di conferma: eseguire nuovamente la procedura di registrazione o attendere l'abilitazione da parte di un amministratore.";

                }
            }

            accountConny.close();

        } catch (SQLException ex) {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERRORE IN createAccount " + ex.toString());
            code = "confirmNotConfirmed";
            mess = "Errore in conferma nuovo uente.";
        }
        String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
        String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                + "\"event\":\"LoginForm\","
                + "\"type\":\"" + code + "\" }]";
        String utils = "\"responseType\":\"text\"";
        String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
        String newPage = "portal?target=requestsManager&gp=" + encodeURIComponent(gp);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "confirm New User>>>> " + connectors);
        request.setOutputStreamType("forward");
        request.setResponse(newPage);

        return request;

    }

    public IncomingRequest confirmNewPassword(IncomingRequest request) {
        System.out.println("\nSONO IN confirmNewPassword");

        String tabOperatori = mySettings.getAccount_TABLEoperatori();
        String IDfield = mySettings.getAccount_FIELDoperatoriID();
        String email = request.getMyGate().getEmail();
        String password = request.getMyGate().getPassword();
        String token = request.getMyGate().getToken();

        myParams = request.getMyParams();
        mySettings = request.getMySettings();
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\n---\nconfirmNewPassword-->email = " + email);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "confirmNewPassword-->token = " + token);

        String sender = null;
        String operation = "PWconfirmationResponse";
        String code = "";
        String mess = "";
        String newID = "";
        String action = "login";
//        ErrorLogger el;
//        el = new ErrorLogger(myParams, mySettings);
        String CKprojectName = mySettings.getProjectName();
        String CKprojectGroup = this.myParams.getCKprojectGroup();
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "confirmNewPassword-->CKprojectName = " + CKprojectName);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "confirmNewPassword-->CKprojectGroup = " + CKprojectGroup);

        String HtmlCode = "";
        String extension = "";
        Connection QPconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalQueenpro();
        String SQLphrase = "SELECT * FROM definitions WHERE ID='" + CKprojectName + "'";
        PreparedStatement ps;
        try {
            ps = QPconny.prepareStatement(SQLphrase);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                extension = rs.getString("definition");
            }
            QPconny.close();

        } catch (SQLException ex) {
            Logger.getLogger(eventManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        ResultSet rs;

        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "confirmNewPassword SQLphrase: " + SQLphrase);
        int lines = 0;

        String DBrecorded = "";
        int lifeInSeconds = 0;
        String tempMail = "";
        String tempUserID = "";

        try {
            SQLphrase = "SELECT * FROM `archivio_timedTokens` WHERE token ='" + token + "'";
            ps = accountConny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                lines++;
                DBrecorded = rs.getString("recorded");
                lifeInSeconds = rs.getInt("lifeInSeconds");
                tempMail = rs.getString("info1");
                tempUserID = rs.getString("info2");
            }
//            System.out.println("info1=" + tempMail);
//            System.out.println("info2=" + tempUserID);
            // ora verifico corrispondenza tra utente e mail nell'archivio degli operatori

            if (tempMail != null && tempMail.length() > 0) {
                SQLphrase = "SELECT * FROM " + tabOperatori + " WHERE " + IDfield + "='" + tempUserID + "' ";
                ps = accountConny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();
                String realMail = "";
                while (rs.next()) {
                    realMail = rs.getString("email");
                    if (!realMail.equalsIgnoreCase(tempMail)) {
                        lines = 0;
                    }
                }
            } else {
                lines = 0;
            }
            if (lines == 0) {// non trovo questa registrazione.. return con messaggio errore
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nconfirmNewPassword ERRORE: non trovo questa registrazione..");
                code = "confirmNotFound";
                mess = "Conferma di cambio password non eseguita correttamente.";
            } else {
                // verifico il lasso di tempo tra la registrazione e la conferma
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
                Date recordedDate = null;
                try {
                    recordedDate = format.parse(DBrecorded);
                } catch (ParseException ex) {
                }
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String curDate = format0.format(cal.getTime());

                Calendar c1 = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                c1.setTime(recordedDate);
                c2.setTime(cal.getTime());

                long giorni = (c2.getTime().getTime() - c1.getTime().getTime()) / (24 * 3600 * 1000);
                long ore = (c2.getTime().getTime() - c1.getTime().getTime()) / (3600 * 1000);
                long minuti = (c2.getTime().getTime() - c1.getTime().getTime()) / (60 * 1000);

                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nSono passati " + minuti + " minuti dalla registrazione");
                if (minuti * 60 < lifeInSeconds) {

                    String pwKEY = mySettings.getPasswordKey("");
                    SQLphrase = "UPDATE " + tabOperatori + " SET password = "
                            + "AES_ENCRYPT('" + password + "', '" + pwKEY + "') "
                            + "WHERE " + IDfield + "='" + tempUserID + "' ";
//                    System.out.println("SQLphrase=" + SQLphrase);

                    ps = accountConny.prepareStatement(SQLphrase);
                    int i = ps.executeUpdate();
//                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nLA PASSWORD è STATA MODIFICATA IN  " + password);

                    code = "PWconfirmOK";
                    mess = "Conferma di registrazione eseguita correttamente.";

                } else {

                    code = "PWconfirmTimeout";
                    mess = "Timeout di conferma: eseguire nuovamente la procedura di modifica password.";

                }
            }

            accountConny.close();

        } catch (SQLException ex) {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERRORE IN confirm New Password " + ex.toString());
            code = "PWconfirmNotConfirmed";
            mess = "Errore in conferma nuova password.";
        }

        String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
        String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                + "\"event\":\"LoginForm\","
                + "\"type\":\"" + code + "\" }]";
        String utils = "\"responseType\":\"text\"";
        String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
        String newPage = "portal?target=requestsManager&gp=" + encodeURIComponent(gp);
        request.setOutputStreamType("standard");
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "confirm New Password>>>> " + connectors);

        request.setResponse(newPage);

        return request;
    }

    public String getChilds(String formID) {

        Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        ResultSet rs;
        String SQLphrase = "SELECT FE_forms_childhood.*, FE_forms.query FROM `FE_forms_childhood` "
                + " LEFT JOIN FE_forms ON FE_forms.ID=FE_forms_childhood.rifChild "
                + ""
                + " WHERE `rifFather`='" + formID + "'";
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "getChilds SQLphrase:" + SQLphrase);
        int lines = 0;
        String Message = "";
        try {
            Statement s = FEconny.createStatement();
            rs = s.executeQuery(SQLphrase);
            ArrayList<childLink> myChilds = new ArrayList<childLink>();

            while (rs.next()) {
                childLink myChild = new childLink();
                lines++;
                myChild.position = rs.getString("position");
                myChild.rifChild = rs.getString("rifChild");
                try {
                    myChild.rifChild = myChild.rifChild.replaceAll("[\n\r]", "");
                } catch (Exception e) {
                }
                if (myChild.position == null || myChild.position == "null" || myChild.position == "") {
                    myChild.position = "B";
                }
                myChilds.add(myChild);

            }
            //per sicurezza la query la prendo io dalle matrici e non uso parametri inviati dal browser
            for (int child = 0; child < myChilds.size(); child++) {
                SQLphrase = "SELECT * FROM `FE_forms` WHERE `id`='" + myChilds.get(child).getRifChild() + "' ";
                rs = s.executeQuery(SQLphrase);
                while (rs.next()) {
                    myChilds.get(child).setQuery(rs.getString("FE_forms.query"));

                }

            }

            Message += "{\"childs\":[";
            for (int child = 0; child < myChilds.size(); child++) {
                if (child > 0) {
                    Message += ",";
                }
                Message += "{";
                Message += "\"position\":\"" + myChilds.get(child).getPosition() + "\",";
                Message += "\"rifChild\":\"" + myChilds.get(child).getRifChild() + "\",";
                Message += "\"query\":\"" + myChilds.get(child).getQuery() + "\"";
                Message += "}";

            }
            Message += "]}";
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "getChilds :" + Message);
            /*   
             for (int child = 0; child < myChilds.size(); child++) {
             if (Message.length() > 0) {
             Message += "|";
             }
             Message += myChilds.get(child).getPosition() + "$$" + myChilds.get(child).getRifChild() + "$$" + myChilds.get(child).getQuery();
            el.log(myParams.getCKprojectName()+myParams.getCKcontextID()+"eventManager","getChilds :" + Message);
             }

             */

            FEconny.close();
        } catch (SQLException ex) {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", ex.toString());
        }
        if (Message != null) {
            //  Message = Message.replace("\n", "").replace("\r", "");
        }
        return Message;
    }

    public String pageBlockTitle(IncomingRequest request) {
        String HtmlCode = " TITLE ";
        EVOpagerDirectivesManager myDirective = new EVOpagerDirectivesManager(request.getMyParams(), request.getMySettings());
        //request.getMyParams().printParams("pageBlockTitle");
        //request.getMySettings().printSettings("pageBlockTitle");
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\nDa pageBlockTitle vado in myDirective.getDirective(\"softwareTitle\") :");
        HtmlCode = myDirective.getDirective("softwareTitle");
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", " softwareTitle  :" + HtmlCode);
        return HtmlCode;
    }

    public IncomingRequest siteOfflineForm(IncomingRequest request) {
        String HtmlCode = "";
        HtmlCode += ("<!DOCTYPE html>");
        HtmlCode += ("<html>");
        HtmlCode += ("<head>");
        HtmlCode += ("<title>" + mySettings.getSoftwareTitle() + " - Badge</title>"
                + "<meta http-equiv=\"cache-control\" content=\"no-cache\">\n"
                + "<meta http-equiv=\"expires\" content=\"0\">\n"
                + "<meta http-equiv=\"pragma\" content=\"no-cache\">");
        HtmlCode += ("<body onload=\"startup()\" style=\"  width: 100%;  \">"
                + "<link rel=\"stylesheet\" href=\"stylesheet.css\" type=\"text/css\" charset=\"utf-8\" />");
        HtmlCode += ("IL SERVIZIO E' IN FASE DI MANUTENZIONE. RIPROVARE PIU' TARDI.");
        HtmlCode += ("</body>");

        HtmlCode += ("</html>");
        request.setResponse((Object) HtmlCode);
        return request;
    }

    public IncomingRequest loginForm(IncomingRequest request) {
        System.out.println("Sono in login Form");
//        el = new ErrorLogger(myParams, mySettings);    
        myParams = request.getMyParams();
        mySettings = request.getMySettings();
//        el.setPrintOnScreen(false);
//        el.setPrintOnLog(false);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n----\n");
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "COSTRUZIONE LOGIN FORM " + request.getMyGate().getType());
//
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "GROUP FROM COMMAND LINE: " + request.getMyParams().getCKprojectGroup());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "PROJECT FROM COMMAND LINE: " + mySettings.getProjectName());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "CONTEXT FROM COMMAND LINE: " + myParams.getCKcontextID());
//
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "----\n");
//-------------------------------
// SOLO NEL LOGIN FORM, in caso di context nullo, posso cercare il context nel
// database "queenpro.definiitons" e compilarlo automaticamente di conseguenza:
//-------------------------------
//        TomcatGaiaHost gaiaHost = new TomcatGaiaHost(myParams.getCKprojectName());

//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager->loginForm", "GAIASETTINGS IN : " + gaiaHost.getPath());
        String HtmlCode = "";
        String extension = "";
        String extensionQuery = "";
        String extensionErrorMessage = "";
        if (myParams == null || myParams.getCKcontextID() == null || myParams.getCKcontextID().length() < 2) {
            System.out.println("Cerco extension");
//            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Cerco extension ");
            Connection QPconny = new EVOpagerDBconnection(request.getMySettings()).ConnLocalQueenpro();
            if (QPconny != null) {
                String SQLphrase = "SELECT * FROM definitions WHERE ID = '" + request.getMySettings().getProjectName() + "'";
                System.out.println("SQL per extension" + SQLphrase);
                extensionQuery = SQLphrase;
                PreparedStatement ps;
                try {
                    ps = QPconny.prepareStatement(SQLphrase);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        extension = rs.getString("definition");
                    }
                    QPconny.close();
                    request.getMyParams().setCKcontextID(extension);

                } catch (SQLException ex) {
                    Logger.getLogger(eventManager.class
                            .getName()).log(Level.SEVERE, null, ex);
                    extensionErrorMessage = "FILE DI CONFIGURAZIONE NON TROVATO !";
                    System.out.println("FILE DI CONFIGURAZIONE NON TROVATO !");
                }
            } else {
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Errore nel collegamento al database queenpro ");
                System.out.println("Errore nel collegamento al database queenpro ");
            }
        } else {
            extension = myParams.getCKcontextID();
            System.out.println("Viene fornita una extension da index.jsp : " + extension);
//            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Viene fornita una extension da index.jsp : " + extension);
        }
        if (extension.length() < 1) {
            extensionErrorMessage += " ESTENSIONE VUOTA !";
        }
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "tornato da ricerca extension : " + extension);
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(request.getMyParams(), mySettings);
        String ExpireDate = myManager.getEvoDirective("ExpireDate");
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "tornato da ricerca ExpireDate : " + ExpireDate);

        if (ExpireDate == null) {
            ExpireDate = "2014-01-01";
        }

        HtmlCode += ("<!DOCTYPE html>");
        HtmlCode += ("<html>");
        HtmlCode += ("<head>");
        HtmlCode += ("<title>" + mySettings.getSoftwareTitle() + " - Badge</title>"
                + "<meta http-equiv=\"cache-control\" content=\"no-cache\">\n"
                + "<meta http-equiv=\"expires\" content=\"0\">\n"
                + "<meta http-equiv=\"pragma\" content=\"no-cache\">");

        HtmlCode += ("<style>\n");

        HtmlCode += ("input{\n"
                + "-webkit-border-radius: 25px;\n"
                + "-moz-border-radius: 25px;\n"
                + "border-radius: 25px;\n"
                + "height: 20px;\n"
                + "padding: 10px;\n"
                + "}");
        HtmlCode += (".myButton {width:150px;  display:block;   background-color:lightGrey; "
                + " color:black;   padding:10px;   text-align:center;  border-radius: 25px; }");
        HtmlCode += (".myAreaButton {width:250px;  display:block; color:white;  background-color:Grey; "
                + " color:black;   padding:5px;   text-align:center;  border-radius: 5px; }");
        HtmlCode += (".mydiv  { font-family: 'Sansation', Arial, sans-serif; }\n");
        HtmlCode += ("</style>\n");

        HtmlCode += ("<SCRIPT>\n"
                + "");
        //----------------------------------------------------------   
        HtmlCode += ("function EVOcom()\n"
                + "{\n"
                + "var portalParams= document.getElementById(\"portalParams\").value;"
                + "    document.getElementById(\"EVObridge\").innerHTML=\"connecting QUEENPRO...\";\n"
                + "var xmlhttp;\n"
                + "if (window.XMLHttpRequest)\n"
                + "  {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "  xmlhttp=new XMLHttpRequest();\n"
                + "  }\n"
                + "else\n"
                + "  {// code for IE6, IE5\n"
                + "  xmlhttp=new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "  }\n"
                + "xmlhttp.onreadystatechange=function()\n"
                + "  {\n"
                + "  if (xmlhttp.readyState==4 && xmlhttp.status==200)\n"
                + "    {\n"
                + "    document.getElementById(\"EVObridge\").innerHTML=xmlhttp.responseText;\n"
                + "    }\n"
                + "  }\n"
                + "var params='\"params\":'+ document.getElementById(\"portalParams\").value;"
                + "var utils='\"responseType\":\"text\"';"
                + "var connectors='\"connectors\":[{\"door\":\"update\","
                + "     \"event\":\"projectUpdate\", "
                + "     \"type\":\"autoUpdate\"  "
                + " }]';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                + "gp=encodeURIComponent(gp);"
                + "    xmlhttp.open(\"POST\", \"portal\", true);\n"
                + "    xmlhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n"
                + "    xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + "}\n"
                + ""
                + "");
        //----------------------------------------------------------      

//----------------------------------------------------------   
        HtmlCode += coderPopulateHeader();
        //----------------------------------------------------------      
        HtmlCode += (" function newAccount()\n"
                + "{\n"
                + "var portalParams= document.getElementById(\"portalParams\").value;"
                + "var params='\"params\":'+ portalParams;"
                + "var connectors='\"connectors\":[{\"door\":\"AccountManager\","
                + "     \"event\":\"newAccount\" }]';"
                + "var utils='\"responseType\":\"text\"';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                + "gp=encodeURIComponent(gp);"
                + "var addr= \"portal?target=requestsManager&gp=\" + encodeURIComponent(gp);"
                + "    window.open(addr, '_parent');\n"
                + "}");

//----------------------------------------------------------   
        HtmlCode += ("function goLogin(goType)\n"
                + "{\n"
                + "var username=\"\";"
                + "var pass=\"\";"
                //+ "alert (\"LOGIN\");"
                + "if (goType==\"pincode\"){"
                + "      username=document.getElementById(\"pincodeusername\").value;"
                + "      pass=document.getElementById(\"pincode\").value;"
                + "}else{"
                + "      username=document.getElementById(\"username\").value;"
                + "      pass=document.getElementById(\"pass\").value;"
                + "}"
                //                + "console.log(\"goType: \" + goType);"
                //                + "console.log(\"username: \" + username);"
                //                + "console.log(\"pass: \" + pass);"
                + "var portalParams= document.getElementById(\"portalParams\").value;"
                + "var xmlhttp;\n"
                + "if (window.XMLHttpRequest)\n"
                + "  {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "  xmlhttp=new XMLHttpRequest();\n"
                + "  }\n"
                + "else\n"
                + "  {// code for IE6, IE5\n"
                + "  xmlhttp=new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "  }\n"
                + "xmlhttp.onreadystatechange=function()\n"
                + "  {\n"
                + "  if (xmlhttp.readyState==4 && xmlhttp.status==200)\n"
                + "    {\n"
                //+ "    document.getElementById(\"titlePlaceholder\").innerHTML=xmlhttp.responseText;\n"
                + " var newPage=xmlhttp.responseText;"
                + "window.location=newPage;"
                + "   }\n"
                + "  }\n"
                + "if( !username || username.length<2 ){"
                + " alert(\"Occorre compilare il nome utente.  -->\"+username);"
                + "}else{"
                + " var params='\"params\":'+ portalParams;"
                + " var connectors='\"connectors\":[{\"door\":\"AccountManager\","
                + "     \"event\":\"Login\","
                + "     \"type\":\"'+goType+'\","
                + "     \"pass\":\"'+pass+'\","
                + "     \"username\":\"'+username+'\"}]';"
                + " var utils='\"responseType\":\"text\"';"
                + " var gp='{'+utils+','+params+','+connectors+'}';"
                + " gp=encodeURIComponent(gp);"
                + " xmlhttp.open(\"POST\",\"portal\",true);\n"
                + " xmlhttp.setRequestHeader(\"Content-type\",\"application/x-www-form-urlencoded\");\n"
                + " xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + " }\n"
                + "}\n"
                + "");

//----------------------------------------------------------   
        HtmlCode += ("function goSendMailPW()\n"
                + "{\n"
                + "var email=document.getElementById(\"email\").value;"
                + "var portalParams= document.getElementById(\"portalParams\").value;"
                + "var xmlhttp;\n"
                + "if (window.XMLHttpRequest)\n"
                + "  {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "  xmlhttp=new XMLHttpRequest();\n"
                + "  }\n"
                + "else\n"
                + "  {// code for IE6, IE5\n"
                + "  xmlhttp=new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "  }\n"
                + "xmlhttp.onreadystatechange=function()\n"
                + "  {\n"
                + "  if (xmlhttp.readyState==4 && xmlhttp.status==200)\n"
                + "    {\n"
                + " var newPage=xmlhttp.responseText;"
                + "window.location=newPage;"
                + "   }\n"
                + "  }\n"
                + "var params='\"params\":'+ portalParams;"
                + "var connectors='\"connectors\":[{\"door\":\"AccountManager\","
                + "     \"event\":\"sendMailPW\","
                + "     \"email\":\"'+email+'\" }]';"
                + "var utils='\"responseType\":\"text\"';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                + "gp=encodeURIComponent(gp);"
                + " xmlhttp.open(\"POST\",\"portal\",true);\n"
                + " xmlhttp.setRequestHeader(\"Content-type\",\"application/x-www-form-urlencoded\");\n"
                + " xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + "}\n"
                + "");
        //----------------------------------------------------------  
        HtmlCode += (" function toggleLogin()\n"
                + "{\n"
                + "document.getElementById( \"loginSpace\").style.display=\"block\";"
                + "document.getElementById( \"forgotPasswordSpace\").style.display=\"none\";"
                + "document.getElementById( \"newAccountSpace\").style.display=\"none\";"
                + "document.getElementById( \"pincodeLoginSpace\").style.display=\"none\";"
                + "document.getElementById( \"LoginToggler\").style.width=\"500px\";"
                + "document.getElementById( \"ForgotPasswordToggler\").style.width=\"250px\";"
                + "document.getElementById( \"NewAccountToggler\").style.width=\"250px\";"
                + "document.getElementById( \"pincodeLoginToggler\").style.width=\"250px\";"
                + "}\n");
        //----------------------------------------------------------  
        HtmlCode += (" function togglePincodeLogin()\n"
                + "{\n"
                + "document.getElementById( \"loginSpace\").style.display=\"none\";"
                + "document.getElementById( \"forgotPasswordSpace\").style.display=\"none\";"
                + "document.getElementById( \"newAccountSpace\").style.display=\"none\";"
                + "document.getElementById( \"pincodeLoginSpace\").style.display=\"block\";"
                + "document.getElementById( \"LoginToggler\").style.width=\"250px\";"
                + "document.getElementById( \"ForgotPasswordToggler\").style.width=\"250px\";"
                + "document.getElementById( \"NewAccountToggler\").style.width=\"250px\";"
                + "document.getElementById( \"pincodeLoginToggler\").style.width=\"500px\";"
                + "}\n");
        //toggleForgotPassword
        HtmlCode += (" function toggleForgotPassword()\n"
                + "{\n"
                + "document.getElementById( \"loginSpace\").style.display=\"none\";"
                + "document.getElementById( \"forgotPasswordSpace\").style.display=\"block\";"
                + "document.getElementById( \"newAccountSpace\").style.display=\"none\";"
                + "document.getElementById( \"pincodeLoginSpace\").style.display=\"none\";"
                + "document.getElementById( \"LoginToggler\").style.width=\"250px\";"
                + "document.getElementById( \"ForgotPasswordToggler\").style.width=\"500px\";"
                + "document.getElementById( \"NewAccountToggler\").style.width=\"250px\";"
                + "document.getElementById( \"pincodeLoginToggler\").style.width=\"250px\";"
                + "}\n");
        //toggleNewAccount
        HtmlCode += (" function toggleNewAccount()\n"
                + "{\n"
                + "document.getElementById( \"loginSpace\").style.display=\"none\";"
                + "document.getElementById( \"forgotPasswordSpace\").style.display=\"none\";"
                + "document.getElementById( \"newAccountSpace\").style.display=\"block\";"
                + "document.getElementById( \"pincodeLoginSpace\").style.display=\"none\";"
                + "document.getElementById( \"LoginToggler\").style.width=\"250px\";"
                + "document.getElementById( \"ForgotPasswordToggler\").style.width=\"250px\";"
                + "document.getElementById( \"NewAccountToggler\").style.width=\"500px\";"
                + "document.getElementById( \"pincodeLoginToggler\").style.width=\"250px\";"
                + "}\n");

        //----------------------------------------------------------  
        HtmlCode += ("function startup()\n"
                + "{\n"
                + "document.getElementById(\"portalParams\").value= JSON.stringify(PORTALparams);"
                //                + "EVOcom();\n"
                + "populateHeader(\"title\");\n"
                + "populateHeader(\"version\");\n"
                + "}\n"
                + ""
                + "");
        HtmlCode += ""
                + "function showDetails(){"
                + "var Xvisible= document.getElementById(\"serverResponse\").style.display;\n"
                + "if (Xvisible == \"none\"){"
                + "document.getElementById(\"serverResponse\").style.display = \"block\"; \n"
                + "}else{"
                + "document.getElementById(\"serverResponse\").style.display = \"none\"; \n"
                + "}"
                + "}"
                + "</SCRIPT>";
        HtmlCode += "<SCRIPT>\n"
                + "var overallProjectName=\"" + request.getMySettings().getProjectName() + "\";\n"
                + "var PORTALparams = " + request.getMyParams().makePORTALparams() + ";\n"
                + "</SCRIPT>";
        HtmlCode += ("</head>");
        HtmlCode += ("<body onload=\"startup()\" style=\"  width: 100%;  \">"
                + "<link rel=\"stylesheet\" href=\"stylesheet.css\" type=\"text/css\" charset=\"utf-8\" />");
        HtmlCode += ("<INPUT type='hidden' id='portalParams'  />");
        HtmlCode += ("<TABLE  class=\"mydiv\" width= \"100%\"><TR><TD>");
        //extensionErrorMessage //extensionQuery
        HtmlCode += ("<div id=\"QPerr\"> ");
        HtmlCode += (extensionErrorMessage);
        HtmlCode += ("<INPUT type=\"hidden\" id=\"extensionQuery\" value=\"" + extensionQuery + "\"  />");
        HtmlCode += (" </DIV>");
        HtmlCode += ("<div id=\"EVObridge\" >");
        HtmlCode += ("</div>");
        HtmlCode += ("</TD></TR></TABLE>");
        HtmlCode += ("<div  class=\"mydiv\" id=\"titlePlaceholder\" ></div>");
        HtmlCode += ("<div class=\"mydiv\" id=\"versionPlaceholder\" ></div>");

        HtmlCode += ("<div class=\"mydiv\" id='badge'>");
        //  out.println("<font face=\"verdana\" size='1.5'> \n");
//        
//         HtmlCode += ("<div class=\"mydiv\" id='qa'>"
//                 + "<a  href=\"qa.jsp\" style=\"display:block;\">QuickAccess</a>"
//                 + "</div>");

        HtmlCode += ("<TABLE   height = '100px' cellpadding='0' border='0'"
                + " style='"
                // + "font-family: \"Verdana\", Verdana, serif; "
                + "font-size;14px;' "
                + " class=\"mydiv\" "
                + ">");
        //out.println("" + userName);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        Date recordedDate = null;
        try {
            recordedDate = format.parse(ExpireDate);
        } catch (ParseException ex) {

        }

        Calendar cal = Calendar.getInstance();
        //  SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd");
        //  String curDate = format0.format(cal.getTime());
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        long giorni = 0;
        if (ExpireDate != null && ExpireDate.length() > 0) {

            c1.setTime(recordedDate); // oggi
            c2.setTime(cal.getTime());

            giorni = (c2.getTime().getTime() - c1.getTime().getTime()) / (24 * 3600 * 1000);

            String giorniPhrase = "";
            String expiringPhrase = "<BR>Expiring Date:" + ExpireDate;
            if (giorni > 0) {
                giorniPhrase = "Licenza scaduta da " + giorni + " giorni.";
            } else {
                giorniPhrase = "" + giorni + " giorni alla scadenza.";

                if (giorni < -30) {
                    expiringPhrase = "";
                    giorniPhrase = "";
                }

            }

            HtmlCode += ("<TR>");
            HtmlCode += ("<TD class=\"mydiv\">");
            HtmlCode += (expiringPhrase);
            HtmlCode += ("</TD>");
            HtmlCode += ("</TR>");
            HtmlCode += ("<TR>");
            HtmlCode += ("<TD class=\"mydiv\">");
            HtmlCode += (" " + giorniPhrase + "<BR>");
            HtmlCode += ("</TD>");
            HtmlCode += ("</TR>");
        }
        int flagNewAccount = 1;
        if (giorni < 60) {

            if (giorni > 40) {
                long toend = 60 - giorni;
                HtmlCode += ("<TR><TD>Questo software diventerà inagibile fra " + toend + " giorni.</TD></TR>");
                HtmlCode += ("<TR><TD>Contattare info@ffs.it per riattivare la licenza.</TD></TR>");
            }

            HtmlCode += ("<TR>");

            HtmlCode += ("<TD  class=\"mydiv\" >");

            String type = request.getMyGate().getType();
            feedback myFB = new feedback();
            myFB.getPhraseAnswer(type);
            flagNewAccount = myFB.flagNewAccount;
            HtmlCode += myFB.answerPhrase;
            HtmlCode += ("<div id=\"userSelector\" ></div>");
            HtmlCode += "</TD>";
            boolean accessByPincode = false;
            boolean accessByAccount = false;

            if (mySettings.getAccessType().equalsIgnoreCase("UNPC+MLPW")) {
                accessByPincode = true;
                accessByAccount = true;
            } else if (mySettings.getAccessType().equalsIgnoreCase("UNPC")) {
                accessByPincode = true;
                accessByAccount = false;
            } else if (mySettings.getAccessType().equalsIgnoreCase("MLPW")) {
                accessByPincode = false;
                accessByAccount = true;
            }
////////System.out.println("mySettings.getAccessType():"+mySettings.getAccessType());
////////System.out.println("accessByPincode:"+accessByPincode);
////////System.out.println("accessByAccount:"+accessByAccount);
//           el.log(myParams.getCKprojectName()+myParams.getCKcontextID()+"eventManager","mySettings.getAccessType():" + mySettings.getAccessType());
//           el.log(myParams.getCKprojectName()+myParams.getCKcontextID()+"eventManager","accessByPincode:" + accessByPincode);
//           el.log(myParams.getCKprojectName()+myParams.getCKcontextID()+"eventManager","accessByAccount:" + accessByAccount);
//-------------------
            if (accessByPincode && getLocalClient(request) == true) {
                HtmlCode += "<TR> <TD><a  class= \"myAreaButton\" id= \"pincodeLoginToggler\" style=\"width:500px;\" "
                        + " onClick=\"javascript:togglePincodeLogin()\">LOGIN LOCALE CON PIN</a> </TD></TR>";
                HtmlCode += "<TR><TD><DIV id=\"pincodeLoginSpace\" style=\"display:block;  \" >";
                HtmlCode += "<form  method=\"post\" action=\"javascript:goLogin('pincode')\"><TABLE>";

//                HtmlCode += "<TABLE>";
                HtmlCode += "<tr>";
                HtmlCode += ("<td colspan = \"4\" class=\"mydiv\" >Accesso facilitato: inserire l'USERNAME prescelto e il relativo PINCODE.</TD>");
                HtmlCode += "</tr><tr>";
                HtmlCode += ("<td class=\"mydiv\" >Username:  <input type='text' name='pincodeusername'  id='pincodeusername'></TD>");
                HtmlCode += ("<td class=\"mydiv\">Pincode: <input type='password' name='pincode' id='pincode' "
                        //                        + "onchange='goLogin(this.id)'"
                        + "></TD>");
                HtmlCode += ("<td class=\"mydiv\"><input type='hidden' name='event' id='event' value='pincodelogin'></TD>");

                HtmlCode += ("<td class=\"mydiv\"><input type='submit' VALUE='Accesso con PIN' style='display:none;'></TD>");
                HtmlCode += ("<td><a class=\"myButton\" id= \"pincodeBtn\" "
                        + " onClick=\"javascript:goLogin('pincode')\">Accesso con PIN</a></TD>");
                HtmlCode += ("</tr>");
                HtmlCode += ("</TABLE></FORM>");
                HtmlCode += ("</DIV></TD> </TR>");
            }
            //---------------------------
            if (accessByAccount) {
                HtmlCode += "<TR> <TD><a  class= \"myAreaButton\" id= \"LoginToggler\"";

                if (accessByPincode && getLocalClient(request) == true) {
                    HtmlCode += " style=\"width:250px;\" ";
                } else {
                    HtmlCode += " style=\"width:500px;\" ";
                }
                HtmlCode += " onClick=\"javascript:toggleLogin()\">LOGIN CON MAIL E PASSWORD</a> </TD></TR>";
                HtmlCode += "<TR><TD><DIV id=\"loginSpace\" ";
                if (accessByPincode && getLocalClient(request) == true) {
                    HtmlCode += "style=\"display:none;  \" ";
                } else {
                    HtmlCode += "style=\"display:block;  \" ";
                }
                HtmlCode += ">";
                HtmlCode += "<form  method=\"post\" action=\"javascript:goLogin('mailpassword')\"><TABLE>";
                HtmlCode += "<tr>";
                HtmlCode += ("<td  colspan = \"4\"class=\"mydiv\" >Per accedere inserire l'INDIRIZZO MAIL usato per la registrazione e la relativa PASSWORD.</TD>");
                HtmlCode += "</tr><tr>";
                HtmlCode += ("<td class=\"mydiv\" >Email:  <input type='text' name='username'  id='username'></TD>");
                HtmlCode += ("<td class=\"mydiv\">Password: <input type='password' name='pass' id='pass'></TD>");
                HtmlCode += ("<td class=\"mydiv\"><input type='hidden' name='event' id='event' value='login'></TD>");
                HtmlCode += ("<td class=\"mydiv\"><input type='submit' VALUE='Accesso con Account' style='display:none;'></TD>");

                HtmlCode += ("<td><a class=\"myButton\"  id= \"passBtn\"  "
                        + " onClick=\"javascript:goLogin('mailpassword')\">Accesso con Account</a></TD>");
                HtmlCode += ("</tr>");
                HtmlCode += ("</TABLE></FORM>");
                HtmlCode += ("</DIV></TD> </TR>");
            }

        } else {
            HtmlCode += ("<TR>");

            HtmlCode += ("<TD>");

            HtmlCode += ("Contattare info@ffs.it per riattivare la licenza.");

            HtmlCode += ("</TD>");
            HtmlCode += ("</TR>");
        }
        //  if (event.equalsIgnoreCase("badCredentials") || event.equalsIgnoreCase("pincodeOnWeb")) {
        // inserisco link per eseguire recupero pw

        HtmlCode += "<TR> <TD>"
                + "<a  class= \"myAreaButton\" id= \"ForgotPasswordToggler\"   style=\"width:250px;\" "
                + "onClick=\"javascript:toggleForgotPassword()\">PASSWORD DIMENTICATA ?</a>"
                + " </TD></TR>";
        HtmlCode += "<TR><TD><DIV id=\"forgotPasswordSpace\" style=\"display:none;\">";
        HtmlCode += "<table> "
                + "<tr><td style=\"width:250px;\">Hai dimenticato la tua password ?</TD></tr>"
                + "<tr>"
                + "<td>Verrà inviata una mail di recupero al seguente indirizzo</td> "
                + "<td class=\"mydiv\" >email:  <input type='text' name='email'  id='email'></td>"
                + "<td><a  class= \"myButton\"   onClick=\"javascript:goSendMailPW()\">Invia</a></td>"
                + "</tr>"
                + "</table>";
        HtmlCode += ("</DIV></TD></TR>");
        // }
        if (request.getMySettings().getRegistrationAllowed() != null
                && request.getMySettings().getRegistrationAllowed().equalsIgnoreCase("TRUE")
                && flagNewAccount > 0) {
            // inserisco link per eseguire la registrazione
            HtmlCode += ("<TR>");
            HtmlCode += "<TD><a   id= \"NewAccountToggler\"  class= \"myAreaButton\" style=\"width:250px;\" "
                    + " onClick=\"javascript:toggleNewAccount()\">CREA UN ACCOUNT</a></TD>"
                    + "</TR>";
            HtmlCode += "<TR><TD><DIV id=\"newAccountSpace\" style=\"display:none; \" >";
            HtmlCode += "<TABLE><TR><TD style=\"width:250px;\">Sei un nuovo utente ?</TD><TD> "
                    + "<a  class= \"myButton\"  onClick=\"javascript:newAccount()\">Crea un account.</a></TD></TR></TABLE>";
            HtmlCode += ("</DIV></TD>");
            HtmlCode += ("</TR>");
        }

        HtmlCode += ("</TABLE>");
        if (mySettings.getUpdateMode() == null || !mySettings.getUpdateMode().equalsIgnoreCase("oldMysql")) {
            HtmlCode += "<INPUT type=\"hidden\" value=\"ACCOUNT@" + mySettings.getGaiaHost().getPwType() + " - " + mySettings.getGaiaHost().getDbUsername() + " > " + mySettings.getData_DATABASE_USER() + "\" >";
            HtmlCode += "<INPUT type=\"hidden\" value=\"QP@" + mySettings.getQP_centralManagerURL() + "\" >";
            HtmlCode += ("<td><a "
                    //                    + "class=\"myButton\" "
                    + "style = \" position: fixed; bottom: 0; width: 100%;\" id= \"CREDITS @QUEENPRO.COM\" "
                    + " onClick=\"javascript:EVOcom()\">Credits @queenpro.eu</a></TD>");
        }
        HtmlCode += ("</body>");
//        HtmlCode+="<SCRIPT>"
//                 + "var XpincodeLogin = document.getElementById(\"pincode\");\n" 
//                + "XpincodeLogin.addEventListener(\"keyup\", function(event) {\n" 
//                + "  if (event.keyCode === 13) {\n" 
//                + "    event.preventDefault();\n" 
//                + "    document.getElementById(\"pincodeBtn\").click();\n"
//                + "  }\n"
//                + "});"
//                + "var XpassLogin = document.getElementById(\"pass\");\n" 
//                + "XpincodeLogin.addEventListener(\"keyup\", function(event) {\n" 
//                + "  if (event.keyCode === 13) {\n" 
//                + "    event.preventDefault();\n" 
//                + "    document.getElementById(\"passBtn\").click();\n"
//                + "  }\n"
//                + "});"
//                + "</SCRIPT>";
        HtmlCode += ("</html>");
        request.setResponse((Object) HtmlCode);
        return request;
    }

    public IncomingRequest quickLoginForm(IncomingRequest request) {
        el.setPrintOnScreen(true);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n----\n");
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "COSTRUZIONE LOGIN FORM " + request.getMyGate().getType());
        myParams = request.getMyParams();
        mySettings = request.getMySettings();
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "GROUP FROM COMMAND LINE: " + request.getMyParams().getCKprojectGroup());
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "PROJECT FROM COMMAND LINE: " + mySettings.getProjectName());
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "CONTEXT FROM COMMAND LINE: " + myParams.getCKcontextID());

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "----\n");
//-------------------------------
// SOLO NEL LOGIN FORM, in caso di context nullo, posso cercare il context nel
// database "queenpro.definiitons" e compilarlo automaticamente di conseguenza:
//-------------------------------

        String HtmlCode = "";
        String extension = "";

        if (request.getMyParams().getCKcontextID() == null || request.getMyParams().getCKcontextID().length() < 1) {
            el = new ErrorLogger(request.getMyParams(), request.getMySettings());

            Connection QPconny = new EVOpagerDBconnection(request.getMyParams(), request.getMySettings()).ConnLocalQueenpro();
            String SQLphrase = "SELECT * FROM definitions WHERE ID='" + request.getMySettings().getProjectName() + "'";
            PreparedStatement ps;
            try {
                ps = QPconny.prepareStatement(SQLphrase);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    extension = rs.getString("definition");
                }
                QPconny.close();
                request.getMyParams().setCKcontextID(extension);

            } catch (SQLException ex) {
                Logger.getLogger(eventManager.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(request.getMyParams(), mySettings);
        String ExpireDate = myManager.getEvoDirective("ExpireDate");
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "tornato da ricerca definition: " + ExpireDate);

        if (ExpireDate == null) {
            ExpireDate = "2010-01-01";
        }

        HtmlCode += ("<!DOCTYPE html>");
        HtmlCode += ("<html>");
        HtmlCode += ("<head>");
        HtmlCode += ("<title>QUICK LOGIN</title>"
                + "<meta http-equiv=\"cache-control\" content=\"no-cache\">\n"
                + "<meta http-equiv=\"expires\" content=\"0\">\n"
                + "<meta http-equiv=\"pragma\" content=\"no-cache\">");

        HtmlCode += ("<style>\n");

        HtmlCode += ("input{\n"
                + "-webkit-border-radius: 25px;\n"
                + "-moz-border-radius: 25px;\n"
                + "border-radius: 25px;\n"
                + "height: 20px;\n"
                + "padding: 10px;\n"
                + "}");
        HtmlCode += (".myButton {width:150px;  display:block;   background-color:lightGrey; "
                + " color:black;   padding:10px;   text-align:center;  border-radius: 25px; }");
        HtmlCode += (".myAreaButton {width:250px;  display:block; color:white;  background-color:Grey; "
                + " color:black;   padding:5px;   text-align:center;  border-radius: 5px; }");
        HtmlCode += (".mydiv  { font-family: 'Sansation', Arial, sans-serif; }\n");
        HtmlCode += ("</style>\n");

        HtmlCode += ("<SCRIPT>\n"
                + "");

//----------------------------------------------------------   
        HtmlCode += coderPopulateHeader();

        HtmlCode += ("function goQuickLogin(goType)\n"
                + "{\n"
                //+ "alert (\"LOGIN\");"
                + "     var barcode=document.getElementById(\"barcode\").value;"
                + "var portalParams= document.getElementById(\"portalParams\").value;"
                + "var xmlhttp;\n"
                + "if (window.XMLHttpRequest)\n"
                + "  {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "  xmlhttp=new XMLHttpRequest();\n"
                + "  }\n"
                + "else\n"
                + "  {// code for IE6, IE5\n"
                + "  xmlhttp=new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "  }\n"
                + "xmlhttp.onreadystatechange=function()\n"
                + "  {\n"
                + "  if (xmlhttp.readyState==4 && xmlhttp.status==200)\n"
                + "    {\n"
                //+ "    document.getElementById(\"titlePlaceholder\").innerHTML=xmlhttp.responseText;\n"
                + " var newPage=xmlhttp.responseText;"
                + "window.location=newPage;"
                + "   }\n"
                + "  }\n"
                + "var params='\"params\":'+ portalParams;"
                + "var connectors='\"connectors\":[{\"door\":\"AccountManager\","
                + "     \"event\":\"QuickLogin\","
                + "     \"type\":\"'+goType+'\","
                + "     \"pass\":\"'+barcode+'\","
                + "     \"username\":\"'+barcode+'\"}]';"
                + "var utils='\"responseType\":\"text\"';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                + "gp=encodeURIComponent(gp);"
                + " xmlhttp.open(\"POST\",\"portal\",true);\n"
                + " xmlhttp.setRequestHeader(\"Content-type\",\"application/x-www-form-urlencoded\");\n"
                + " xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + "}\n"
                + ""
                + "");

        //----------------------------------------------------------  
        HtmlCode += ("function startup()\n"
                + "{\n"
                + "document.getElementById(\"portalParams\").value= JSON.stringify(PORTALparams);"
                //                + "EVOcom();\n"
                + "populateHeader(\"title\");\n"
                + "populateHeader(\"version\");\n"
                + "document.getElementById(\"barcode\").focus();\n"
                + "}\n"
                + "</SCRIPT>");

        HtmlCode += "<SCRIPT>\n"
                + "var overallProjectName=\"" + request.getMySettings().getProjectName() + "\";\n"
                + "var PORTALparams = " + request.getMyParams().makePORTALparams() + ";\n"
                + "</SCRIPT>";
        HtmlCode += ("</head>");
        HtmlCode += ("<body onload=\"startup()\" style=\"  width: 100%;  \">"
                + "<link rel=\"stylesheet\" href=\"stylesheet.css\" type=\"text/css\" charset=\"utf-8\" />");
        HtmlCode += ("<INPUT type='hidden' id='portalParams'  />");
        HtmlCode += ("<TABLE  class=\"mydiv\" width= \"100%\"><TR><TD>");
        HtmlCode += ("<div id=\"EVObridge\" >");
//        HtmlCode += ("</div>");
        HtmlCode += ("</TD></TR></TABLE>");
        HtmlCode += ("<div  class=\"mydiv\" id=\"titlePlaceholder\" ></div>");
        HtmlCode += ("<div class=\"mydiv\" id=\"versionPlaceholder\" ></div>");

        HtmlCode += ("<div class=\"mydiv\" id='badge'>");
        //  out.println("<font face=\"verdana\" size='1.5'> \n");
        HtmlCode += ("<TABLE   height = '100px' cellpadding='0' border='0'"
                + " style='"
                // + "font-family: \"Verdana\", Verdana, serif; "
                + "font-size;14px;' "
                + " class=\"mydiv\" "
                + ">");
        //out.println("" + userName);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        Date recordedDate = null;
        try {
            recordedDate = format.parse(ExpireDate);
        } catch (ParseException ex) {

        }

        Calendar cal = Calendar.getInstance();
        //  SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd");
        //  String curDate = format0.format(cal.getTime());
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(recordedDate); // oggi
        c2.setTime(cal.getTime());

        long giorni = (c2.getTime().getTime() - c1.getTime().getTime()) / (24 * 3600 * 1000);
        String giorniPhrase = "";
        String expiringPhrase = "<BR>Expiring Date:" + ExpireDate;
        if (giorni > 0) {
            giorniPhrase = "Licenza scaduta da " + giorni + " giorni.";
        } else {
            giorniPhrase = "" + giorni + " giorni alla scadenza.";

            if (giorni < -30) {
                expiringPhrase = "";
                giorniPhrase = "";
            }

        }
        if (ExpireDate != null && ExpireDate.length() > 0) {
            HtmlCode += ("<TR>");
            HtmlCode += ("<TD class=\"mydiv\">");
            HtmlCode += (expiringPhrase);
            HtmlCode += ("</TD>");
            HtmlCode += ("</TR>");
            HtmlCode += ("<TR>");
            HtmlCode += ("<TD class=\"mydiv\">");
            HtmlCode += (" " + giorniPhrase + "<BR>");
            HtmlCode += ("</TD>");
            HtmlCode += ("</TR>");
        }
        int flagNewAccount = 1;
        if (giorni < 60) {

            if (giorni > 40) {
                long toend = 60 - giorni;
                HtmlCode += ("<TR><TD>Questo software diventerà inagibile fra " + toend + " giorni.</TD></TR>");
                HtmlCode += ("<TR><TD>Contattare info@ffs.it per riattivare la licenza.</TD></TR>");
            }

            HtmlCode += ("<TR>");

            HtmlCode += ("<TD  class=\"mydiv\" >");
            String type = request.getMyGate().getType();
            feedback myFB = new feedback();
            myFB.getPhraseAnswer(type);
            flagNewAccount = myFB.flagNewAccount;
            HtmlCode += myFB.answerPhrase;

////////            
////////            String risposta = "";
////////            String type = request.getMyGate().getType();
////////            if (type.equalsIgnoreCase("badCredentials")) {
////////                risposta += ("CREDENZIALI ERRATE: reinserire le credenziali per il login:");
////////            } else if (type.equalsIgnoreCase("accessDenied")) {
////////                risposta += ("QUESTO UTENTE NON E' ABILITATO: tentare il Login come altro utente.");
////////            } else if (type.equalsIgnoreCase("emailSent")) {
////////                risposta += ("E' stata inviata una mail sulla casella dell'utente per il ripristino della password.");
////////            } else if (type.equalsIgnoreCase("pincodeOnWeb")) {
////////                risposta += ("Il Login con Username + Pin Code è consentito solo da rete locale. Usare email + password.");
////////            } else if (type.equalsIgnoreCase("accountCreated")) {
////////                risposta += ("Riceverai una mail all'indirizzo indicato per confermare l'account prima di poter fare il primo accesso.");
////////                flagNewAccount = 0;
////////            } else if (type.equalsIgnoreCase("confirmTimeout")) {
////////                risposta += ("Timeout di conferma: eseguire nuovamente la procedura di registrazione.");
////////                flagNewAccount = 0;
////////            } else if (type.equalsIgnoreCase("confirmOK")) {
////////                risposta += ("Conferma di registrazione eseguita correttamente.");
////////                flagNewAccount = 0;
////////            } else if (type.equalsIgnoreCase("confirmNotFound")) {
////////                risposta += ("Riceverai una mail all'indirizzo indicato per confermare l'account prima di poter fare il primo accesso.");
////////                flagNewAccount = 0;
////////            } else if (type.equalsIgnoreCase("confirmNotConfirmed")) {
////////                risposta += ("Errore in conferma nuovo uente.");
////////                flagNewAccount = 0;
////////            } else if (type.equalsIgnoreCase("PWconfirmTimeout")) {
////////                risposta += ("Timeout di conferma: eseguire nuovamente la procedura di Modifica password e confermare entro 10 minuti.");
////////                flagNewAccount = 0;
////////            } else if (type.equalsIgnoreCase("PWconfirmOK")) {
////////                risposta += ("Modifica password eseguita correttamente. Ora è possibile eseguire il Login con le nuove credenziali.");
////////                flagNewAccount = 0;
////////            } else if (type.equalsIgnoreCase("PWconfirmNotConfirmed")) {
////////                risposta += ("Errore in Modifica password .");
////////                flagNewAccount = 0;
////////            } else {
////////
////////                risposta += ("Inserire le credenziali per il login: ");
////////                String localIP = "";
////////                String remoteIP = request.getRemoteIP();
////////                try {
////////                    InetAddress ip = InetAddress.getLocalHost();
////////                    localIP = ip.getHostAddress();
////////                } catch (UnknownHostException ex) {
////////                    Logger.getLogger(eventManager.class.getName()).log(Level.SEVERE, null, ex);
////////                }
//////////                HtmlCode += ("<BR>Local IP: " + localIP);
//////////                HtmlCode += ("<BR>remote IP: " + remoteIP);
//////////                HtmlCode += ("<BR>IP is local: " + getLocalClient(request));
//////////                HtmlCode += ("<BR>" + dummyMessage);
////////            }
////////
////////            HtmlCode += risposta;
            HtmlCode += ("<div id=\"userSelector\" ></div>");
            HtmlCode += "</TD>";

            HtmlCode += "<TR> <TD><a  class= \"myAreaButton\" id= \"pincodeLoginToggler\" style=\"width:500px;\" "
                    + " onClick=\"javascript:clearBarcode()\">BARCODE LOCAL LOGIN</a> </TD></TR>";
            HtmlCode += "<TR><TD><DIV id=\"pincodeLoginSpace\" style=\"display:block;  \" >";
            HtmlCode += "<TABLE>";
            HtmlCode += "<tr>";
            HtmlCode += ("<td colspan = \"4\" class=\"mydiv\" >SCANSIONARE IL BADGE.</TD>");
            HtmlCode += "</tr><tr>";
            HtmlCode += ("<td class=\"mydiv\" >BARCODE:  "
                    + "<input type='password' name='barcode'  id='barcode'"
                    + " onChange=\"javascript:goQuickLogin('barcode')\""
                    + " onDblClick=\"javascript:goQuickLogin('barcode')\""
                    + "></TD>");
            HtmlCode += ("</tr>");
            HtmlCode += ("</TABLE>");
            HtmlCode += ("</DIV></TD> </TR>");
        } else {
            HtmlCode += ("<TR>");
            HtmlCode += ("<TD>");
            HtmlCode += ("Contattare info@ffs.it per riattivare la licenza.");
            HtmlCode += ("</TD>");
            HtmlCode += ("</TR>");
        }

        HtmlCode += ("</TABLE>");
        HtmlCode += ("</body>");
        HtmlCode += ("</html>");
        request.setResponse((Object) HtmlCode);
        return request;
    }

    public IncomingRequest newAccountForm(IncomingRequest request) {

        String extension = findExtension();
        request.getMyParams().setCKcontextID(extension);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "EVENT MANAGER-->loginForm  trova in queenpro la voce CKextension :" + extension);
        String HtmlCode = "";
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(request.getMyParams(), request.getMySettings());
        String ExpireDate = myManager.getEvoDirective("ExpireDate");
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "tornato da ricerca Expire date: " + ExpireDate);

        if (ExpireDate == null) {
            ExpireDate = "2000-01-01";
        }

        HtmlCode += ("<!DOCTYPE html>");
        HtmlCode += ("<html>");
        HtmlCode += ("<head>");
        HtmlCode += ("<title>NEW ACCOUNT</title>"
                + "<meta http-equiv=\"cache-control\" content=\"no-cache\">\n"
                + "<meta http-equiv=\"expires\" content=\"0\">\n"
                + "<meta http-equiv=\"pragma\" content=\"no-cache\">");
        HtmlCode += ("<SCRIPT>\n");
        //----------------------------------------------------------   

//----------------------------------------------------------   
        HtmlCode += coderPopulateHeader();
        //----------------------------------------------------------      

//----------------------------------------------------------   
        HtmlCode += ("function goNewAccount()\n"
                + "{\n"
                //+ "alert (\"NEW\");"
                + "var username=document.getElementById(\"username\").value;\n"
                + "var pass=document.getElementById(\"pass\").value;\n"
                + "var surname=document.getElementById(\"surname\").value;\n"
                + "var name=document.getElementById(\"name\").value;\n"
                + "var email=document.getElementById(\"email\").value;\n"
                + "var portalParams= document.getElementById(\"portalParams\").value;"
                + "console.log(\"email:\" + email);"
                + "var xmlhttp;\n"
                + "if (window.XMLHttpRequest)\n"
                + "  {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "  xmlhttp=new XMLHttpRequest();\n"
                + "  }\n"
                + "else\n"
                + "  {// code for IE6, IE5\n"
                + "  xmlhttp=new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "  }\n"
                + "xmlhttp.onreadystatechange=function()\n"
                + "  {\n"
                + "  if (xmlhttp.readyState==4 && xmlhttp.status==200)\n"
                + "    {\n"
                + " var newPage=xmlhttp.responseText;"
                + "window.location=newPage;"
                + "   }\n"
                + "  }\n"
                + "var params='\"params\":'+ portalParams;"
                + "var connectors='\"connectors\":[{"
                + "     \"door\":\"AccountManager\","
                + "     \"event\":\"createMailAccount\","
                + "     \"email\":\"'+email+'\","
                + "     \"pass\":\"'+pass+'\","
                + "     \"surname\":\"'+surname+'\","
                + "     \"name\":\"'+name+'\","
                + "     \"username\":\"'+username+'\"}]';"
                + "var utils='\"responseType\":\"text\"';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                + "gp=encodeURIComponent(gp);"
                + " xmlhttp.open(\"POST\",\"portal\",true);\n"
                + " xmlhttp.setRequestHeader(\"Content-type\",\"application/x-www-form-urlencoded\");\n"
                + " xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + "}\n"
                + ""
                + "");
        //----------------------------------------------------------  
        HtmlCode += (" function is_email(email){      \n"
                + "var emailReg = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{1,4}$/;\n"
                + "return emailReg.test(email); \n"
                + "} \n");

        //----------------------------------------------------------  
        //----------------------------------------------------------  
        HtmlCode += (" function validateMail(){      \n"
                + "var addr= document.getElementById(\"email\").value;\n"
                + "if (is_email(addr)){\n"
                + "document.getElementById(\"submitButton\").style.display='block';\n"
                + "}else{\n"
                + "document.getElementById(\"submitButton\").style.display='none';\n"
                + "}\n"
                + "} ");

        //----------------------------------------------------------  
        //----------------------------------------------------------  
        HtmlCode += (" function validatePassword(){      \n"
                + "var pass= document.getElementById(\"password\").value;\n"
                + "if (pass && pass.length >6){"
                + "document.getElementById(\"submitButton\").style.display='block';\n"
                + "}else{"
                + "document.getElementById(\"submitButton\").style.display='none';\n"
                + "}"
                + "} ");
        //----------------------------------------------------------  
        HtmlCode += (" function validateAccount(){      \n"
                + "testResult=1;"
                + "passValida=1;"
                // verifico username
                + "var INusername= document.getElementById(\"username\").value;\n"
                + "if (!INusername || INusername.length<4){\n" //alertUsername
                + "document.getElementById(\"alertUsername\").innerHTML = \"username deve contenere almeno 4 caratteri senza spazi.\";\n"
                + "testResult=testResult*0;"
                + "}else{\n"
                + "document.getElementById(\"alertUsername\").innerHTML = \"OK\";\n"
                + "}\n"
                // verifico email
                + "var addr= document.getElementById(\"email\").value;\n"
                + "if (addr && addr.length >3){"
                + "if (is_email(addr)){\n"
                + "document.getElementById(\"alertEmail\").innerHTML = \"OK\";\n"
                + "emailValida=1;"
                + "}else{\n"
                + "document.getElementById(\"alertEmail\").innerHTML = \"indirizzo non valido\";\n"
                + "testResult=testResult*0;"
                + "}\n"
                + "}else{"
                + "document.getElementById(\"alertEmail\").innerHTML = \"indirizzo non valido\";\n"
                + "testResult=testResult*0;"
                + "}"
                // verifico pass
                + "var pass= document.getElementById(\"pass\").value;\n"
                + "console.log(\" pass.length \" +  pass.length );"
                + "if (!pass || pass.length <6){"
                + "passValida=0;"
                + "document.getElementById(\"alertPassword\").innerHTML = \"password deve contenere almeno 6 caratteri senza spazi.\";\n"
                + "testResult=testResult*0;"
                + "}else{"
                + "document.getElementById(\"alertPassword\").innerHTML = \"OK\";\n"
                + "}"
                // verifico pass2
                + "if (passValida>0){"
                + "document.getElementById(\"pass2space\").style.display='block';\n"
                + "var pass2= document.getElementById(\"pass2\").value;\n"
                + "if (pass2==pass){"
                + "document.getElementById(\"alertConfirm\").innerHTML = \"OK\";\n"
                + "}else{"
                + "document.getElementById(\"alertConfirm\").innerHTML = \"non corrispondente\";\n"
                + "testResult=testResult*0;"
                + "}"
                + "}else{"// se la prima non è valida cancello e nascondo la seconda
                + "document.getElementById(\"pass2space\").style.display='none';\n"
                + "document.getElementById(\"pass2\").value=\"\";\n"
                + "document.getElementById(\"alertConfirm\").innerHTML = \"\";\n"
                + "testResult=testResult*0;"
                + "}"
                + "console.log(\"TEST:\" + testResult);"
                + "if (testResult>0){\n"
                + "document.getElementById(\"submitButton\").style.display='block';\n"
                + "}else{\n"
                + "document.getElementById(\"submitButton\").style.display='none';\n"
                + "}"
                + "} ");

        //----------------------------------------------------------  
        HtmlCode += ("function startup()\n"
                + "{\n"
                + "document.getElementById(\"portalParams\").value= JSON.stringify(PORTALparams);"
                // + "EVOcom();\n"
                + "populateHeader(\"title\");\n"
                + "validateAccount();\n"
                // + "populateVersion();\n"
                + "}\n"
                + "</SCRIPT>");
        HtmlCode += "<SCRIPT>"
                + "function showDetails(){"
                + "var Xvisible= document.getElementById(\"serverResponse\").style.display;\n"
                + "if (Xvisible == \"none\"){"
                + "document.getElementById(\"serverResponse\").style.display = \"block\"; \n"
                + "}else{"
                + "document.getElementById(\"serverResponse\").style.display = \"none\"; \n"
                + "}"
                + "}"
                + "</SCRIPT>";
        HtmlCode += "<SCRIPT>"
                + "var overallProjectName=\"" + mySettings.getProjectName() + "\";"
                + "var PORTALparams = " + myParams.makePORTALparams() + ";"
                + "</SCRIPT>";
        HtmlCode += ("</head>");
        HtmlCode += ("<body onload=\"startup()\" style=\"  width: 100%;  \">");

        HtmlCode += ("<INPUT type='hidden' id='portalParams'  />");
        HtmlCode += ("<TABLE width= \"100%\"><TR><TD>");
        HtmlCode += ("<div id=\"EVObridge\" >");

        HtmlCode += ("</div>");
        HtmlCode += ("</TD></TR></TABLE>");
        HtmlCode += ("<div  class=\"mydiv\" id=\"titlePlaceholder\" ></div>");
        HtmlCode += ("<div class=\"mydiv\" id=\"versionPlaceholder\" ></div>");

        HtmlCode += ("<div id='badge'>");
        //  out.println("<font face=\"verdana\" size='1.5'> \n");
        HtmlCode += ("<TABLE height = '100px' cellpadding='0' border='0' style='font-family: \"Verdana\", Verdana, serif; font-size;14px;' >");
        //out.println("" + userName);
        if (mySettings.getRegistrationAllowed() != null && mySettings.getRegistrationAllowed().equalsIgnoreCase("TRUE")) {

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
            Date recordedDate = null;
            try {
                recordedDate = format.parse(ExpireDate);
            } catch (ParseException ex) {

            }

            Calendar cal = Calendar.getInstance();
            //  SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd");
            //  String curDate = format0.format(cal.getTime());
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(recordedDate); // oggi
            c2.setTime(cal.getTime());

            long giorni = (c2.getTime().getTime() - c1.getTime().getTime()) / (24 * 3600 * 1000);
            String giorniPhrase = "";
            String expiringPhrase = "<BR>Expiring Date:" + ExpireDate;
            if (giorni > 0) {
                giorniPhrase = "Licenza scaduta da " + giorni + " giorni.";
            } else {
                giorniPhrase = "" + giorni + " giorni alla scadenza.";

                if (giorni < -30) {
                    expiringPhrase = "";
                    giorniPhrase = "";
                }

            }
            HtmlCode += ("CREAZIONE NUOVO ACCOUNT:");
            if (request.getMyGate().getType().equalsIgnoreCase("accountExists")) {
                HtmlCode += ("ATTENZIONE l'account esiste già: provare con un altro indirizzo mail.");
            }

            HtmlCode += ("<TABLE>");
            HtmlCode += ("<TR><TD> Cognome: </TD><TD> <input type='text' name='surname'  id='surname' "
                    + "onKeyUp='javascript:validateAccount()' onChange='javascript:validateAccount()'>"
                    + "</TD><TD><DIV id=\"alertSurname\"></DIV> </TD></TR>");
            HtmlCode += ("<TR><TD> Nome: </TD><TD> <input type='text' name='name'  id='name' "
                    + "onKeyUp='javascript:validateAccount()' onChange='javascript:validateAccount()'>"
                    + "</TD><TD><DIV id=\"alertName\"></DIV> </TD></TR>");

            HtmlCode += ("<TR><TD> Username: </TD><TD> <input type='text' name='username'  id='username' "
                    + "onKeyUp='javascript:validateAccount()' onChange='javascript:validateAccount()'>"
                    + "</TD><TD><DIV id=\"alertUsername\"></DIV> </TD></TR>");
            HtmlCode += ("<TR><TD> Email:  </TD><TD> <input type='text' name='email'  id='email' "
                    + "onKeyUp='javascript:validateAccount()' onChange='javascript:validateAccount()'>"
                    + "</TD><TD><DIV id=\"alertEmail\"></DIV> </TD></TR>");
            HtmlCode += ("<TR><TD> Inserisci la Password: </TD><TD> <input type='password' name='pass' id='pass'  "
                    + "onKeyUp='javascript:validateAccount()' onChange='javascript:validateAccount()'>"
                    + "</TD><TD><DIV id=\"alertPassword\"></DIV> </TD></TR>");
            HtmlCode += ("<TR><DIV id=\"pass2space\"><TD> Conferma la Password: </TD><TD> <input type='password' name='pass2' id='pass2'  "
                    + "onKeyUp='javascript:validateAccount()' onChange='javascript:validateAccount()'>"
                    + "</TD><TD><DIV id=\"alertConfirm\"></DIV>  </TD></DIV></TR>");

            HtmlCode += ("<TR><TD> </TD><TD> <input type='hidden' name='event' id='event' value='newAccount'></TD><TD>  </TD> </TR> ");
            HtmlCode += ("<TR><TD> </TD><TD> <input id='submitButton' type='submit' value='Crea' onClick='javascript:goNewAccount()'></TD><TD>  </TD></TR>");

            HtmlCode += ("<br>");
            HtmlCode += ("</TD>");
            HtmlCode += ("</TR>");

        } else {
            HtmlCode += ("<TR>");
            HtmlCode += ("<TD>");
            HtmlCode += ("Creazione di un  nuovo account non consentita. Contattare l'amministratore.");

            HtmlCode += ("</TD>");
            HtmlCode += ("</TR>");
        }

        HtmlCode += ("</TABLE>");
        HtmlCode += ("</body>");
        HtmlCode += ("</html>");
        request.setResponse((Object) HtmlCode);
        return request;
    }

    public IncomingRequest newPasswordForm(IncomingRequest request) {
        myParams = request.getMyParams();
        //myParams.printParams("newPasswordForm");
        String email = request.getMyGate().getEmail();
        String token = request.getMyGate().getToken();
        String remoteIP = request.getRemoteIP();
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "newPasswordForm>>> ricevo email: " + email);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "newPasswordForm>>> ricevo token: " + token);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "newPasswordForm>>> ricevo remoteIP: " + remoteIP);

        String HtmlCode = "";

        HtmlCode += ("<!DOCTYPE html>");
        HtmlCode += ("<html>");
        HtmlCode += ("<head>");
        HtmlCode += ("<title>NEW PASSWORD</title>"
                + "<meta http-equiv=\"cache-control\" content=\"no-cache\">\n"
                + "<meta http-equiv=\"expires\" content=\"0\">\n"
                + "<meta http-equiv=\"pragma\" content=\"no-cache\">");
        HtmlCode += ("<SCRIPT>\n");
        //----------------------------------------------------------   

//----------------------------------------------------------   
        HtmlCode += coderPopulateHeader();

//----------------------------------------------------------   
        HtmlCode += ("function goNewPassword()\n"
                + "{\n"
                + "var pass=document.getElementById(\"pass\").value;\n"
                + "var portalParams= document.getElementById(\"portalParams\").value;"
                + "var xmlhttp;\n"
                + "if (window.XMLHttpRequest)\n"
                + "  {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "  xmlhttp=new XMLHttpRequest();\n"
                + "  }\n"
                + "else\n"
                + "  {// code for IE6, IE5\n"
                + "  xmlhttp=new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "  }\n"
                + "xmlhttp.onreadystatechange=function()\n"
                + "  {\n"
                + "  if (xmlhttp.readyState==4 && xmlhttp.status==200)\n"
                + "    {\n"
                + " var newPage=xmlhttp.responseText;"
                // + "     console.log(\"goNewPassword riceve ordine per new Page:\"+newPage);"
                + "window.location=newPage;"
                + "   }\n"
                + "  }\n"
                + "var params='\"params\":'+ portalParams;"
                + "var connectors='\"connectors\":"
                + "     [{"
                + "     \"door\":\"AccountManager\","
                + "     \"event\":\"confirmNewPassword\","
                + "     \"email\":\"" + email + "\","
                + "     \"pass\":\"'+pass+'\","
                + "     \"token\":\"" + token + "\""
                + "     }]';"
                + "var utils='\"responseType\":\"text\"';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                + "console.log (gp);"
                + " xmlhttp.open(\"POST\",\"portal\",true);\n"
                + " xmlhttp.setRequestHeader(\"Content-type\",\"application/x-www-form-urlencoded\");\n"
                + " xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + "}\n");

        //----------------------------------------------------------  
        //----------------------------------------------------------  
        HtmlCode += (" function validatePassword(){      \n"
                + "var pass= document.getElementById(\"password\").value;\n"
                + "if (pass && pass.length >6){"
                + "document.getElementById(\"submitButton\").style.display='block';\n"
                + "}else{"
                + "document.getElementById(\"submitButton\").style.display='none';\n"
                + "}"
                + "} ");
        //----------------------------------------------------------  
        HtmlCode += (" function validateAccount(){      \n"
                + "testResult=1;"
                + "passValida=1;"
                // verifico pass
                + "var pass= document.getElementById(\"pass\").value;\n"
                + "console.log(\" pass.length \" +  pass.length );"
                + "if (!pass || pass.length <8){"
                + "passValida=0;"
                + "document.getElementById(\"alertPassword\").innerHTML = \"password deve contenere almeno 8 caratteri senza spazi.\";\n"
                + "testResult=testResult*0;"
                + "}else{"
                + "document.getElementById(\"alertPassword\").innerHTML = \"OK\";\n"
                + "}"
                // verifico pass2
                + "if (passValida>0){"
                + "document.getElementById(\"pass2space\").style.display='block';\n"
                + "var pass2= document.getElementById(\"pass2\").value;\n"
                + "if (pass2==pass){"
                + "document.getElementById(\"alertConfirm\").innerHTML = \"OK\";\n"
                + "}else{"
                + "document.getElementById(\"alertConfirm\").innerHTML = \"non corrispondente\";\n"
                + "testResult=testResult*0;"
                + "}"
                + "}else{"// se la prima non è valida cancello e nascondo la seconda
                + "document.getElementById(\"pass2space\").style.display='none';\n"
                + "document.getElementById(\"pass2\").value=\"\";\n"
                + "document.getElementById(\"alertConfirm\").innerHTML = \"\";\n"
                + "testResult=testResult*0;"
                + "}"
                + "console.log(\"TEST:\" + testResult);"
                + "if (testResult>0){\n"
                + "document.getElementById(\"submitButton\").style.display='block';\n"
                + "}else{\n"
                + "document.getElementById(\"submitButton\").style.display='none';\n"
                + "}"
                + "} ");

        //----------------------------------------------------------  
        HtmlCode += ("function startup()\n"
                + "{\n"
                + "document.getElementById(\"portalParams\").value= JSON.stringify(PORTALparams);"
                + "populateHeader(\"title\");\n"
                + "populateHeader(\"version\");\n"
                + "validateAccount();\n"
                + "}\n"
                + "</SCRIPT>");
        HtmlCode += "<SCRIPT>"
                + "function showDetails(){"
                + "var Xvisible= document.getElementById(\"serverResponse\").style.display;\n"
                + "if (Xvisible == \"none\"){"
                + "document.getElementById(\"serverResponse\").style.display = \"block\"; \n"
                + "}else{"
                + "document.getElementById(\"serverResponse\").style.display = \"none\"; \n"
                + "}"
                + "}"
                + "</SCRIPT>";
        HtmlCode += "<SCRIPT>"
                + "var overallProjectName=\"" + mySettings.getProjectName() + "\";"
                + "var myObj = " + myParams.makeJSONparams() + ";"
                + "var PORTALparams = " + myParams.makePORTALparams() + ";"
                + "</SCRIPT>";
        HtmlCode += ("</head>");
        HtmlCode += ("<body onload=\"startup()\" style=\"  width: 100%;  \">");

        HtmlCode += ("<INPUT type='hidden' id='portalParams'  />");
        HtmlCode += ("<TABLE width= \"100%\"><TR><TD>");
        HtmlCode += ("<div id=\"EVObridge\" >");

        HtmlCode += ("</div>");
        HtmlCode += ("</TD></TR></TABLE>");
        HtmlCode += ("<div  class=\"mydiv\" id=\"titlePlaceholder\" ></div>");
        HtmlCode += ("<div class=\"mydiv\" id=\"versionPlaceholder\" ></div>");
        HtmlCode += ("<div id='badge'>");
        //  out.println("<font face=\"verdana\" size='1.5'> \n");
        HtmlCode += ("<TABLE height = '100px' cellpadding='0' border='0' style='font-family: \"Verdana\", Verdana, serif; font-size;14px;' >");
        HtmlCode += ("CREAZIONE NUOVA PASSWORD PER UTENTE:");
        HtmlCode += ("<TABLE>");
        HtmlCode += ("<TR><TD> Email:  </TD><TD> <input type='text' name='email'  id='email' "
                + "onKeyUp='javascript:validateAccount()' onChange='javascript:validateAccount()'>"
                + "</TD><TD><DIV id=\"alertEmail\"></DIV> </TD></TR>");
        HtmlCode += ("<TR><TD> Inserisci la Password: </TD><TD> <input type='password' name='pass' id='pass'  "
                + "onKeyUp='javascript:validateAccount()' onChange='javascript:validateAccount()'>"
                + "</TD><TD><DIV id=\"alertPassword\"></DIV> </TD></TR>");
        HtmlCode += ("<TR><DIV id=\"pass2space\"><TD> Conferma la Password: </TD><TD> <input type='password' name='pass2' id='pass2'  "
                + "onKeyUp='javascript:validateAccount()' onChange='javascript:validateAccount()'>"
                + "</TD><TD><DIV id=\"alertConfirm\"></DIV>  </TD></DIV></TR>");

        HtmlCode += ("<TR><TD> </TD><TD> <input type='hidden' name='event' id='event' value='newAccount'></TD><TD>  </TD> </TR> ");
        HtmlCode += ("<TR><TD> </TD><TD> <input id='submitButton' type='submit' value='Crea' onClick='javascript:goNewPassword()'></TD><TD>  </TD></TR>");

        HtmlCode += ("<br>");
        HtmlCode += ("</TD>");
        HtmlCode += ("</TR>");

        HtmlCode += ("</TABLE>");
        HtmlCode += ("</body>");
        HtmlCode += ("</html>");
        request.setResponse((Object) HtmlCode);
        return request;
    }

    public String projectUpdate() {
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n***************\nSONO IN PROJECT UPDATE :\n******************");
        String CKprojectName = mySettings.getProjectName();
        String CKprojectGroup = this.myParams.getCKprojectGroup();
        String CKcontextID = this.myParams.getCKcontextID();

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-->CKprojectName :" + CKprojectName);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-->CKprojectGroup :" + CKprojectGroup);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-->CKcontextID :" + CKcontextID);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-->Settings projectName :" + mySettings.getProjectName());

        String HtmlCode = "";
        String extension = "";
        /*
         A questo punto i casi sono 2:
         1. sto aprendo un SW proprietario sul server del cliente: avro un'estensione da leggere nel DB locale queenpro
        
         2. sto aprendo un software GAIApp per cui mi viene fornito nei myParams una extension valida in CKcontextID
        
         */

        if (CKcontextID != null && !CKcontextID.equalsIgnoreCase("null")) {
            extension = CKcontextID;
        } else {
            extension = findExtension();
            //       el.log(myParams.getCKprojectName()+myParams.getCKcontextID()+"eventManager","\n-->projectUpdate  trova in queenpro la voce CKextension :" + extension);
        }
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n-->projectUpdate  trova in queenpro la voce CKextension :" + extension);

        EVOpagerParams newParams = new EVOpagerParams();

        CKprojectName = mySettings.getProjectName();
        CKprojectGroup = this.myParams.getCKprojectGroup();
        CKcontextID = this.myParams.getCKcontextID();

        newParams.setCKuserID("");
        newParams.setCKprojectGroup(CKprojectGroup);
        newParams.setCKcontextID(extension);
        newParams.setCKprojectName(mySettings.getProjectName());
        myParams = newParams;

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "CREO OGGETTO DBMAKER");
        myParams.printParams(" parametri per creazione dbMaker");

        ClassQPmanageUpdate dbMaker = new ClassQPmanageUpdate(myParams, mySettings);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "CREATO OGGETTO DBMAKER");

        String feedback = "update response null.";
        try {
            feedback = dbMaker.autoUpdate();
        } catch (Exception e) {
            feedback += e.toString();
        }
        HtmlCode = "<div id='updateResponse' style='display:none'>";
        HtmlCode += feedback;
        HtmlCode += "<div>";

        return HtmlCode;
    }

    public String QPfrontendUpdate() {
        System.out.println("\n***************\nSONO IN frontendUpdate  :\n******************");
        String CKprojectName = mySettings.getProjectName();
        String CKprojectGroup = this.myParams.getCKprojectGroup();
        String CKcontextID = this.myParams.getCKcontextID();
        String CKtokenID = myParams.getCKtokenID();
        System.out.println("-->CKprojectName :" + CKprojectName);
        System.out.println("-->CKprojectGroup :" + CKprojectGroup);
        System.out.println("-->CKcontextID :" + CKcontextID);
        String HtmlCode = "";
        String extension = "";
        if (CKcontextID != null && !CKcontextID.equalsIgnoreCase("null")) {
            extension = CKcontextID;
        } else {
            extension = findExtension();
            System.out.println("\n-->projectUpdate  trova in queenpro la voce CKextension :" + extension);
        }
        System.out.println("\n-->projectUpdate  TOKEN DA BROWSER:" + myParams.getCKtokenID());

        EVOpagerParams myParams = new EVOpagerParams();

        myParams.setCKuserID("");
        myParams.setCKprojectGroup(CKprojectGroup);
        myParams.setCKcontextID(extension);
        myParams.setCKprojectName(mySettings.getProjectName());
        if (CKtokenID != null && CKtokenID.length() > 0) {
            myParams.setCKtokenID(CKtokenID);
        }
        CKprojectName = mySettings.getProjectName();
        CKprojectGroup = this.myParams.getCKprojectGroup();
        CKcontextID = this.myParams.getCKcontextID();
        String feedback = null;

        if (mySettings.getUpdateMode() != null && mySettings.getUpdateMode().endsWith("oldMysql")) {
            ClassProjectUpdate dbMaker = new ClassProjectUpdate(myParams, mySettings);
            try {
                feedback = dbMaker.autoUpdate();

            } catch (IOException ex) {
                Logger.getLogger(eventManager.class
                        .getName()).log(Level.SEVERE, null, ex);
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", ex.toString());

            }
        } else {
            ClassQPmanageUpdate dbMaker = new ClassQPmanageUpdate(myParams, mySettings);
            dbMaker.setMode("FEONLY");
            feedback = "update response null.";
            try {
                feedback = dbMaker.autoUpdate();
            } catch (Exception e) {
                feedback += e.toString();
            }
        }

        HtmlCode = "<div id='updateResponse' style='display:none'>";
        HtmlCode += feedback;
        HtmlCode += "<div>";

        return HtmlCode;
    }

    public String frontendUpdate() {
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n***************\nSONO IN frontendUpdate  :\n******************");
        String CKprojectName = mySettings.getProjectName();
        String CKprojectGroup = this.myParams.getCKprojectGroup();
        String CKcontextID = this.myParams.getCKcontextID();

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-->CKprojectName :" + CKprojectName);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-->CKprojectGroup :" + CKprojectGroup);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-->CKcontextID :" + CKcontextID);
        String HtmlCode = "";
        String extension = "";
        if (CKcontextID != null && !CKcontextID.equalsIgnoreCase("null")) {
            extension = CKcontextID;
        } else {
            extension = findExtension();
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n-->projectUpdate  trova in queenpro la voce CKextension :" + extension);
        }
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n-->projectUpdate  trova in queenpro la voce CKextension :" + extension);

        EVOpagerParams myParams = new EVOpagerParams();

        myParams.setCKuserID("");
        myParams.setCKprojectGroup(CKprojectGroup);
        myParams.setCKcontextID(extension);
        myParams.setCKprojectName(mySettings.getProjectName());

        CKprojectName = mySettings.getProjectName();
        CKprojectGroup = this.myParams.getCKprojectGroup();
        CKcontextID = this.myParams.getCKcontextID();

        ClassProjectUpdate dbMaker = new ClassProjectUpdate(myParams, mySettings);
        dbMaker.setMode("FEONLY");
        String feedback = null;

        try {
            feedback = dbMaker.autoUpdate();

        } catch (IOException ex) {
            Logger.getLogger(eventManager.class
                    .getName()).log(Level.SEVERE, null, ex);
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n-->feedback:" + feedback);

        }
        HtmlCode = "<div id='updateResponse' style='display:none'>";
        HtmlCode += feedback;
        HtmlCode += "<div>";

        return HtmlCode;
    }

    public IncomingRequest askFilenameForm(IncomingRequest request) {
        String HtmlCode = "";
        String botName = request.getMyGate().getRifForm() + "-" + request.getMyGate().getCopyTag()
                + "-" + request.getMyGate().getRifObj() + "-" + request.getMyGate().getKeyValue();

        HtmlCode += ("<div id=\"uploadPanel\" class=\"uploadPanelClass\"> \n");
        HtmlCode += ("<form id='formUploadDocument' name='formUploadDocument' "
                + " action='uploadServlet' method='post' enctype='multipart/form-data'>\n"
                + "<INPUT type='hidden' name='target'  id='target' value= 'uploadManager'  />\n"
                + "<INPUT type='hidden' name='uploadType' id='uploadType' value= 'fileToFS'  />\n"
                + "<INPUT type='hidden' name='activity' id='activity' value= 'uploadFile'  />\n"
                + "<INPUT type='hidden' name='oFormTBS' id='oFormTBS' value= ''  />\n"
                + "<INPUT type='hidden' name='rifFolder' id='rifFolder' VALUE='pathBaseUploadsLocal'  />\n"
                + "<INPUT type='hidden' name='father' id='father' VALUE='" + request.getMyGate().getFatherForm() + "'>\n"
                + "<INPUT type='hidden' name='rifUser' id='rifUser' VALUE='" + request.getMyParams().getCKuserID() + "'>\n"
                + "<INPUT type='hidden' name='rifRules' id='rifRules' VALUE='.*'  />\n"
                + "<INPUT type='hidden' name='rifName' id='rifName' VALUE='" + "" + "'  />\n"
                + "<INPUT type='hidden' name='rifProcedure'  id='rifProcedure' VALUE='OBJname'  />\n"
                + "<INPUT type='hidden' name='rifExitPage' id='rifExitPage' VALUE='uploadChild'  />\n"
                + "<INPUT type='hidden' name='formID' id='formID' value= '" + request.getMyGate().getRifForm() + "'  />\n"
                + "<INPUT type='hidden' name='formCopyTag' id='formCopyTag' value= '" + request.getMyGate().getCopyTag() + "' />\n"
                + "<INPUT type='hidden' name='formObjName' id='formObjName' value= '" + request.getMyGate().getRifObj() + "' />\n"
                + "<INPUT type='hidden' name='formRowKey' id='formRowKey' value='" + request.getMyGate().getKeyValue() + "' />\n"
                + "<INPUT type='hidden' name='primaryFieldValue' id='primaryFieldValue' value= '" + request.getMyGate().getKeyValue() + "' />\n"
                + "<INPUT type='hidden' name='CKtokenID' id='CKtokenID' VALUE='" + request.getMyParams().getCKtokenID() + "'/>\n"
                + "<INPUT type='hidden' name='CKuserID'  id='CKuserID' VALUE='" + request.getMyParams().getCKuserID() + "'/>\n"
                + "<INPUT type='hidden' name='CKcontextID' id='CKcontextID' VALUE='" + request.getMyParams().getCKcontextID() + "'/>\n"
                + "<INPUT type='hidden' name='CKprojectName' id='CKprojectName' VALUE='" + request.getMySettings().getProjectName() + "'/>\n"
                + "<INPUT type='hidden' name='CKprojectGroup' id='CKprojectGroup' VALUE='" + request.getMyParams().getCKprojectGroup() + "'/>\n");

        HtmlCode += "<input type=\"file\" name=\"thefile\" id=\"thefile\" "
                + " onchange=\"alertFilename('" + request.getMyGate().getFatherForm() + "','UploadFile','" + request.getMyGate().getRifForm() + "','" + request.getMyGate().getCopyTag() + "','" + request.getMyGate().getRifObj() + "','" + request.getMyGate().getKeyValue() + "')\" />\n"
                + "<input type=\"hidden\" name=\"newfilename\" id=\"newfilename\" value=\"\"   />\n"
                + "<br />\n";
        HtmlCode += ("</form>");
        HtmlCode += ("</div>");

        request.setResponse((Object) HtmlCode);
        return request;
    }

    public IncomingRequest ServeFileFromFS(IncomingRequest request) {
//        el.setPrintOnScreen(true);
        OutputStream os;
        String name = "";

        String rifForm = "";
        String objID = "";
        String fileID = "";

        rifForm = request.getMyGate().rifForm;
        objID = request.getMyGate().rifObj;
        fileID = request.getMyGate().keyValue;
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-----INVIATI A ServeFileFromFS");
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "formID ---------->" + rifForm);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "objID ---------->" + objID);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "fileID ---------->" + fileID);

        //TODO!!!!!!!!!!!! devo ricavare il nome della tabella dal nome del form
        ShowItForm myForm = new ShowItForm(rifForm, myParams, mySettings);
        myForm.setMyParams(myParams);
        myForm.setMySettings(mySettings);
        myForm.buildSchema();
        String myTable = myForm.getMainTable();
        String myKyefield = myForm.getKEYfieldName();
        String myKyefieldType = myForm.getKEYfieldType();

//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "myTable ---------->" + myTable);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "myKyefield ---------->" + myKyefield);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "myKyefieldType ---------->" + myKyefieldType);
        String SQLphrase = "";

        // apro la tabella e cerco il nome del file con estensione
        if (myKyefieldType != null && (myKyefieldType.contains("int") || myKyefieldType.contains("INT"))) {
            SQLphrase = "SELECT " + objID + " FROM " + myTable + "  WHERE " + myKyefield + "= " + fileID + "";
        } else {
            SQLphrase = "SELECT " + objID + " FROM " + myTable + "  WHERE " + myKyefield + "= '" + fileID + "'";
        }
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase ---------->" + SQLphrase);
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                name = rs.getString(objID);
            }
            conny.close();
        } catch (SQLException ex) {

        }
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "nomeDaDB ---------->" + name);
        int posizionePunto = name.lastIndexOf(".");
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "posizionePunto ---------->" + posizionePunto);
        String estensione = name.substring(posizionePunto);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "_estensione ---------->" + estensione);
        //        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "HtmlCode rrestituito a portal---------->" + HtmlCode);
        request.setResponse((Object) name);
        return request;
    }

    public IncomingRequest DeleteFileFromFS(IncomingRequest request) {
        String HtmlCode = "";

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\n********\n-------\nENTRO IN DeleteFileFromFS");
        String name = "";

        String rifForm = "";
        String objID = "";
        String fileID = "";

        rifForm = request.getMyGate().rifForm;
        objID = request.getMyGate().rifObj;
        fileID = request.getMyGate().keyValue;
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-----INVIATI A DeleteFileFromFS");
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "formID ---------->" + rifForm);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "objID ---------->" + objID);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "fileID ---------->" + fileID);

        //TODO!!!!!!!!!!!! devo ricavare il nome della tabella dal nome del form
        ShowItForm myForm = new ShowItForm(rifForm, myParams, mySettings);
        myForm.setMyParams(myParams);
        myForm.setMySettings(mySettings);
        myForm.buildSchema();
        String myTable = myForm.getMainTable();
        String myKyefield = myForm.getKEYfieldName();
        String myKyefieldType = myForm.getKEYfieldType();

//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "myTable ---------->" + myTable);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "myKyefield ---------->" + myKyefield);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "myKyefieldType ---------->" + myKyefieldType);
        String SQLphrase = "";
// apro la tabella e cerco il nome del file con estensione
        if (myKyefieldType != null && (myKyefieldType.contains("int") || myKyefieldType.contains("INT"))) {
            SQLphrase = "SELECT " + objID + " FROM " + myTable + "  WHERE " + myKyefield + "= " + fileID + "";
        } else {
            SQLphrase = "SELECT " + objID + " FROM " + myTable + "  WHERE " + myKyefield + "= '" + fileID + "'";
        }
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase -2525--------->" + SQLphrase);
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                name = rs.getString(objID);
            }

            // apro la tabella e cerco il nome del file con estensione
            if (myKyefieldType != null && (myKyefieldType.contains("int") || myKyefieldType.contains("INT"))) {
                SQLphrase = "UPDATE  " + myTable + " SET " + objID + "=NULL WHERE " + myKyefield + "= " + fileID + "";
            } else {
                SQLphrase = "UPDATE  " + myTable + " SET " + objID + "=NULL WHERE " + myKyefield + "= '" + fileID + "'";
            }
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase --2545-------->" + SQLphrase);

            int delSuccess = 0;

            ps = conny.prepareStatement(SQLphrase);
            delSuccess = ps.executeUpdate();
            conny.close();
        } catch (SQLException ex) {

        }

        HtmlCode = name;
        File file = new File(name);
        if (!file.exists()) {
            HtmlCode = "IL FILE " + name + " NON ESISTE !!!";
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", HtmlCode);
        } else {

            Path Xpath = FileSystems.getDefault().getPath(name);
            try {
                Files.delete((java.nio.file.Path) Xpath);
                HtmlCode = "IL FILE " + name + " E' STATO ELIMINATO !!!";
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", HtmlCode);
            } catch (IOException | SecurityException e) {
                System.err.println(e);
            }
        }
        request.setResponse((Object) HtmlCode);

        return request;
    }

    public IncomingRequest DeleteFileFromDB(IncomingRequest request) {
        String HtmlCode = "";

        request.setResponse((Object) HtmlCode);
        return request;
    }

    public IncomingRequest populateVersion(IncomingRequest request) {
        String HtmlCode = "";
        HtmlCode = "VERSIONE SOFTWARE: " + request.getMySettings().getSoftwareVersion();
        request.setResponse((Object) HtmlCode);
        return request;
    }

    public ShowItForm loadFORMfromGATE(IncomingRequest request) {

//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nloadFORMfromGATE==ID:" + request.getMyGate().getFormID() + "========NAME:" + request.getMyGate().getFormName() + "==============");
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "formToLoad = " + request.getMyGate().getFormToLoad() + "   *   destination = " + request.getMyGate().getDestination());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "formID = " + request.getMyGate().getFormID() + "   *   args = ?");
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "copyTag = " + request.getMyGate().getCopyTag());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "loadType = " + request.getMyGate().getLoadType() + "   *   fatherForm = " + request.getMyGate().getFatherForm());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "fatherKEYvalue = " + request.getMyGate().getFatherKEYvalue() + "   *   fatherKEYtype = " + request.getMyGate().getFatherKEYtype() + "   *   fatherCopyTag = " + request.getMyGate().getFatherCopyTag());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "TBS received= " + request.getMyGate().getTBS());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "curKEYvalue= " + request.getMyGate().getCurKEYvalue());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "sendToCRUD= " + request.getMyGate().getSendToCRUD());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "routine= " + request.getMyGate().getRoutine());
        ShowItForm myForm = new ShowItForm(request.getMyGate().getFormID(), request.getMyParams(), request.getMySettings());
        myForm.setName(request.getMyGate().getFormToLoad());
        myForm.setID(request.getMyGate().getFormID());
        myForm.setFatherKEYvalue(request.getMyGate().getFatherKEYvalue());
        myForm.setFatherKEYtype(request.getMyGate().getFatherKEYtype());
        myForm.setFather(request.getMyGate().getFatherForm());
        myForm.setFatherCopyTag(request.getMyGate().getFatherCopyTag());
        myForm.setFatherFilters(request.getMyGate().getFatherArgs());
        myForm.setLoadType(request.getMyGate().getLoadType());
        myForm.setInfoReceived(request.getMyGate().getTBS());
        myForm.setCopyTag(request.getMyGate().getCopyTag());
        myForm.setCurKEYvalue(request.getMyGate().getCurKEYvalue());
        myForm.setCurKEYtype(request.getMyGate().getCurKEYtype());
        myForm.setSendToCRUD(request.getMyGate().getSendToCRUD());

        return myForm;
    }

    public IncomingRequest getGroups(IncomingRequest request) {
        //devo creare elenco dei gruppi in base a formID e object name, compilando 
        //le righe in base a KEYvalue

        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        PreparedStatement ps = null;
        ResultSet rs;

        el.setPrintOnScreen(true);
        System.out.println("SONO IN getGroups.");
        String formID = "";
        String objID = "";
        String copyTag = "";
        String keyValue = "";
        String params = "";
        el.setPrintOnScreen(true);
        formID = request.getMyGate().formID;
        copyTag = request.getMyGate().copyTag;
        objID = request.getMyGate().rifObj;
        keyValue = request.getMyGate().keyValue;
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "getGroups:devo creare elenco dei gruppi in base a:");
        ShowItForm myForm = loadFORMfromGATE(request);
        myForm.buildSchema();
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "rifForm:" + formID);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "objID:" + objID);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "keyValue:" + keyValue);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", " myForm.getName():" + myForm.getName());

        for (int kk = 0; kk < myForm.getObjects().size(); kk++) {
            if (myForm.getObjects().get(kk).getName().equalsIgnoreCase(objID)) {
                params = myForm.getObjects().get(kk).CG.getParams();
            }
        }
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "params:" + params);
        /*.
        {"partAtab":"contacts",
        "partAfield":"ID" 
        "partBquery":"SELECT * FROM gruppiInteresse",
        "partBvalueField":"ID",
        "partBlabelField":"descrizione",
        "partBiconField":"" }
         */
        Linker myLinker = new Linker();
        myLinker.readParamsJson(params);
        ArrayList<SelectListLine> myLines = new ArrayList<SelectListLine>();

        CRUDorder myCRUD = new CRUDorder(myParams, mySettings);
        myCRUD.setFatherKEYvalue(request.getMyGate().getFatherKEYvalue());
        myCRUD.setSendToCRUD(request.getMyGate().sendToCRUD);
        String SQLphrase = myCRUD.standardReplace(myLinker.getPartBquery(), null);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase ---2892------->" + SQLphrase);

//////        
//////        String infos = request.getMyGate().sendToCRUD;
//////         String infos1 = request.getMyGate().TBS;
//////         String infos2 = request.getMyGate().params;
//////         String infos3 = request.getMyGate().paramsToSend;
//////        System.out.println("infos: "+infos);
//////        System.out.println("infos1: "+infos1);
//////        System.out.println("infos2: "+infos2);
//////        System.out.println("infos3: "+infos3); 
        //Prima di tutto voglio la row della tabella che sta visualizzando l'utente per avere i valori dei field locali
// e se serve usarli per ler sostituzioni nella query della tendina
        try {
            myCRUD.setSendToCRUD(request.getMyGate().sendToCRUD);
            String lookupQuery = myCRUD.standardReplace(myForm.getQuery(), null);
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase ---2939------->" + lookupQuery);
            lookupQuery = remakeQuery(lookupQuery, " WHERE " + myForm.getKEYfieldName() + " = ");
            String afterEffect = "'";
            if (myForm.getKEYfieldType() != null && myForm.getKEYfieldType().startsWith("int")) {
                afterEffect = "";
            }
            lookupQuery += afterEffect + request.getMyGate().getKeyValue() + afterEffect + " ";

            System.out.println("lookupQuery: " + lookupQuery);

            System.out.println("keyField: " + myForm.getKEYfieldName());
            System.out.println("keyFieldType: " + myForm.getKEYfieldType());
            System.out.println("KeyValue: " + request.getMyGate().getKeyValue());

            ps = conny.prepareStatement(lookupQuery);
            rs = ps.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < myForm.objects.size(); i++) {
                    System.out.println(" Field: " + myForm.objects.get(i).name + " ID: " + myForm.objects.get(i).ID
                            + " type: " + myForm.objects.get(i).CG.Type + " Field type: " + myForm.objects.get(i).Content.Type);
                    if (myForm.objects.get(i).Content != null
                            && myForm.objects.get(i).CG.Type != null
                            && myForm.objects.get(i).CG.Type.equalsIgnoreCase("FIELD")) {
                        String marker = myForm.objects.get(i).name;
                        String value = rs.getString(myForm.objects.get(i).name);
                        if (value == null) {
                            value = "";
                        }
                        System.out.println("Rimpiazzo @@@" + marker + "@@@ CON " + value);
                        SQLphrase = SQLphrase.replace("@@@" + marker + "@@@", value);
                    }
                }

            }

        } catch (SQLException ex) {

        }

        try {

            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                SelectListLine myLine = new SelectListLine();
                myLine.setLabel(rs.getString(myLinker.getPartBlabelField()));
                myLine.setValue(rs.getString(myLinker.getPartBvalueField()));
                myLines.add(myLine);
            }
            for (int jj = 0; jj < myLines.size(); jj++) {
                SQLphrase = "SELECT * FROM " + myLinker.getLinkTableName() + " WHERE "
                        + "partAtab = '" + myLinker.getPartAtab() + "' "
                        + "AND partAvalueField = '" + myLinker.getPartAfield() + "' "
                        + "AND partAvalue = '" + keyValue + "' "
                        + "AND partBtab = '" + myLinker.getPartBtab() + "' "
                        + "AND partBvalueField = '" + myLinker.getPartBvalueField() + "' "
                        + "AND partBvalue = '" + myLines.get(jj).getValue() + "' ";
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "CERCO CHECK ---------->" + SQLphrase);
                myLines.get(jj).setChecked(0);
                ps = conny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();
                while (rs.next()) {
                    myLines.get(jj).setChecked(1);
                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "TROVATO !");
                    break;
                }
            }

        } catch (SQLException ex) {

        }
        try {
            conny.close();

        } catch (SQLException ex) {
            Logger.getLogger(eventManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        String HtmlCode = "";
        HtmlCode += "<TABLE>";
        for (int jj = 0; jj < myLines.size(); jj++) {

            String cellName = "Xitem" + jj;

            String jsonArgs = "{";
            jsonArgs += "\"formID\":\"" + formID + "\",";
            jsonArgs += "\"copyTag\":\"" + copyTag + "\",";
            jsonArgs += "\"rifObj\":\"" + objID + "\",";
            jsonArgs += "\"keyValue\":\"" + keyValue + "\",";
            jsonArgs += "\"newValue\":\"" + myLines.get(jj).getValue() + "\",";
            jsonArgs += "\"cellName\":\"" + cellName + "\",";
            jsonArgs += "\"operation\":\"setRelations\" }";

            HtmlCode += "<TR><TD>";
            HtmlCode += "<input type=\"checkbox\" "
                    + "name=\"" + cellName + "\" "
                    + "id=\"" + cellName + "\" "
                    + "value=\"" + myLines.get(jj).getValue() + "\" ";
            if (myLines.get(jj).getChecked() > 0) {

                HtmlCode += " checked ";
            } else {

            }
            HtmlCode += " onClick='javascript:groupChecker(" + jsonArgs + ")'  ";
            HtmlCode += "></TD><TD>";
            HtmlCode += myLines.get(jj).getLabel();
            HtmlCode += " </TD></TR>";
        }
        HtmlCode += "</TABLE>";
        request.setResponse((Object) HtmlCode);
        return request;
    }

    public IncomingRequest setRelations(IncomingRequest request) {
        //devo creare elenco dei gruppi in base a formID e object name, compilando 
        //le righe in base a KEYvalue
        el.setPrintOnScreen(true);
        System.out.println("SONO IN setRelations.");
        String formID = "";
        String objID = "";
        String copyTag = "";
        String keyValue = "";
        String params = "";
        String valueKEY = "";
        String newValue = "";

        String HtmlCode = "";

        formID = request.getMyGate().formID;
        copyTag = request.getMyGate().copyTag;
        objID = request.getMyGate().rifObj;
        keyValue = request.getMyGate().keyValue;
        valueKEY = request.getMyGate().cellName;
        newValue = request.getMyGate().newValue;
//el.setPrintOnScreen(true); 
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n>>>>>>>>>>setRelations:");
        ShowItForm myForm = loadFORMfromGATE(request);
        myForm.buildSchema();
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "rifForm:" + formID);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "objID:" + objID);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "keyValue:" + keyValue);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", " myForm.getName():" + myForm.getName());
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "valueKEY:" + valueKEY);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "newValue:" + newValue);
        String AfterProcessByObjectRoutine = "";
        for (int kk = 0; kk < myForm.getObjects().size(); kk++) {
            if (myForm.getObjects().get(kk).getName().equalsIgnoreCase(objID)) {
                params = myForm.getObjects().get(kk).CG.getParams();
                AfterProcessByObjectRoutine = myForm.getObjects().get(kk).routineOnChange;
            }
        }
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "params:" + params);
        System.out.println("\n----\tROUTINE SU SELEZIONECHECK:" + AfterProcessByObjectRoutine);
        request.setAfterProcessByObjectRoutines(AfterProcessByObjectRoutine);
        Linker myLinker = new Linker();
        myLinker.readParamsJson(params);

        // l'elenco da mostrare sarà dato dagli elementi in partBquery
        //Poi per ogni elemento, se esiste già una correlazione con il mio valore key
        // allora la spunta risulterà checkata
        ArrayList<SelectListLine> myLines = new ArrayList<SelectListLine>();
        String SQLphrase = myLinker.getPartBquery();
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase partBquery:" + myLinker.getPartBquery());

        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        PreparedStatement ps = null;
        ResultSet rs;
        try {

            try {
//prima elimino la correlazione (vale sia per cancellare che per riscrivere 
                SQLphrase = "DELETE FROM " + myLinker.getLinkTableName() + " WHERE "
                        + "partAtab = '" + myLinker.getPartAtab() + "' "
                        + "AND partAvalueField = '" + myLinker.getPartAfield() + "' "
                        + "AND partAvalue = '" + keyValue + "' "
                        + "AND partBtab = '" + myLinker.getPartBtab() + "' "
                        + "AND partBvalueField = '" + myLinker.getPartBvalueField() + "' "
                        + "AND partBvalue = '" + valueKEY + "' ";
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase eliminazione:" + SQLphrase);

                ps = conny.prepareStatement(SQLphrase);
                ps.executeUpdate();
            } catch (SQLException ex) {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERROR c ---------->" + ex.toString());
            }
            try {
                if (Integer.parseInt(newValue) > 0) {
                    SQLphrase = "INSERT INTO " + myLinker.getLinkTableName() + "  ( partAtab,partAvalueField, partAvalue,"
                            + "partBtab,partBvalueField,partBvalue"
                            + ")VALUES('" + myLinker.getPartAtab() + "','" + myLinker.getPartAfield() + "','" + keyValue + "',"
                            + "'" + myLinker.getPartBtab() + "','" + myLinker.getPartBvalueField() + "','" + valueKEY + "')";
                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase inserimento ---------->" + SQLphrase);
                    ps = conny.prepareStatement(SQLphrase);
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERROR d ---------->" + ex.toString());
            }
            conny.close();
        } catch (SQLException ex) {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERROR ---------->" + ex.toString());
        }
        request.setResponse((Object) HtmlCode);
        return request;
    }

    public IncomingRequest getSuggestedList(IncomingRequest request) {
        //devo creare elenco dei gruppi in base a formID e object name, compilando 
        //le righe in base a KEYvalue
        String formID = "";
        String objID = "";
        String copyTag = "";
        String keyValue = "";
        String params = "";
        String newValue = "";
        ArrayList<SelectListLine> myLines = new ArrayList<SelectListLine>();
        el.setPrintOnScreen(true);
        formID = request.getMyGate().formID;
        copyTag = request.getMyGate().copyTag;
        objID = request.getMyGate().rifObj;
        keyValue = request.getMyGate().keyValue;
        newValue = request.getMyGate().newValue;

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "devo creare elenco dei suggerimenti in base a:");
        ShowItForm myForm = loadFORMfromGATE(request);
        myForm.buildSchema();

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "rifForm:" + formID);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "objID:" + objID);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "keyValue:" + keyValue);
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", " myForm.getName():" + myForm.getName());
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", " newValue:" + newValue);
        String listQuery = "";
        for (int kk = 0; kk < myForm.getObjects().size(); kk++) {
            if (myForm.getObjects().get(kk).getName().equalsIgnoreCase(objID)) {
                params = myForm.getObjects().get(kk).CG.getParams();
                System.out.println("params oggetto " + myForm.getObjects().get(kk).getName() + " = " + params);

                listQuery = myForm.getObjects().get(kk).Origin.query;
            }
        }

        String SQLphrase = "";

        SQLphrase = listQuery;
        SQLphrase = SQLphrase.replace("$$$SUGGESTED$$$", newValue);

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase --3173-------->" + SQLphrase);
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                SelectListLine myLine = new SelectListLine();
                myLine.setValue(rs.getString("IDprogetto"));
                myLine.setLabel(rs.getString("nomeProgetto"));
                myLines.add(myLine);
            }

            conny.close();
        } catch (SQLException ex) {

        }

        String HtmlCode = "";
        HtmlCode += "<TABLE>";
        for (int jj = 0; jj < myLines.size(); jj++) {

            String cellName = "Xitem" + jj;

            String jsonArgs = "{";
            jsonArgs += "\"formID\":\"" + formID + "\",";
            jsonArgs += "\"copyTag\":\"" + copyTag + "\",";
            jsonArgs += "\"rifObj\":\"" + objID + "\",";
            jsonArgs += "\"keyValue\":\"" + keyValue + "\",";
            jsonArgs += "\"newValue\":\"" + myLines.get(jj).getValue() + "\",";
            jsonArgs += "\"newLabel\":\"" + myLines.get(jj).getLabel() + "\",";
            jsonArgs += "\"cellName\":\"" + cellName + "\",";
            jsonArgs += "\"operation\":\"setSuggestedList\" }";

            HtmlCode += "<TR style='display:block;' onClick='javascript:suggestList(-1," + jsonArgs + ")' ><TD>";
            HtmlCode += "<p style='display:block;' "
                    + "id='" + cellName + "' "
                    + "value='" + myLines.get(jj).getValue() + "' ";

            HtmlCode += " onClick='javascript:suggestList(-1," + jsonArgs + ")'  >";
            HtmlCode += myLines.get(jj).getLabel();
            HtmlCode += "</p></TD></TR>";
        }
        HtmlCode += "</TABLE>";
        request.setResponse((Object) HtmlCode);
        return request;
    }

    public IncomingRequest setSuggestedValue(IncomingRequest request) {
//        //devo creare elenco dei gruppi in base a formID e object name, compilando 
//        //le righe in base a KEYvalue
//        String formID = "";
//        String objID = "";
//        String copyTag = "";
//        String keyValue = "";
//        String params = "";
//        String valueKEY = "";
//        String newValue = "";
//
//        String HtmlCode = "";
//
//        formID = request.getMyGate().formID;
//        copyTag = request.getMyGate().copyTag;
//        objID = request.getMyGate().rifObj;
//        keyValue = request.getMyGate().keyValue;
//        valueKEY = request.getMyGate().cellName;
//        newValue = request.getMyGate().newValue;
//        el.setPrintOnScreen(true);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n>>>>>>>>>>setRelations:");
//        ShowItForm myForm = loadFORMfromGATE(request);
//        myForm.buildSchema();
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "rifForm:" + formID);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "objID:" + objID);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "keyValue:" + keyValue);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", " myForm.getName():" + myForm.getName());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "valueKEY:" + valueKEY);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "newValue:" + newValue);
//
//        for (int kk = 0; kk < myForm.getObjects().size(); kk++) {
//            if (myForm.getObjects().get(kk).getName().equalsIgnoreCase(objID)) {
//                params = myForm.getObjects().get(kk).CG.getParams();
//            }
//        }
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "params:" + params);
//        int jjjj = 0;
//        if (jjjj == 0) {
//            return request;
//        }
//        String partAtab = "";
//        String partAfield = "";
//        String partBtab = "";
//        String partBquery = "";
//        String partBvalueField = "";
//        String partBlabelField = "";
//        String partBiconField = "";
//
//        if (params != null && params.length() > 4) {
//            try {
//                JSONParser jsonParser = new JSONParser();
//                JSONObject jsonObject = (JSONObject) jsonParser.parse(params);
//
//                try {
//                    partAtab = jsonObject.get("partAtab").toString();
//                } catch (Exception ex) {
//                }
//                try {
//                    partAfield = jsonObject.get("partAvalueField").toString();
//                } catch (Exception ex) {
//                }
//                try {
//                    partBtab = jsonObject.get("partBtab").toString();
//                } catch (Exception ex) {
//                }
//                try {
//                    partBquery = jsonObject.get("partBquery").toString();
//                } catch (Exception ex) {
//                }
//                try {
//                    partBvalueField = jsonObject.get("partBvalueField").toString();
//                } catch (Exception ex) {
//                }
//                try {
//                    partBlabelField = jsonObject.get("partBlabelField").toString();
//                } catch (Exception ex) {
//                }
//                try {
//                    partBiconField = jsonObject.get("partBiconField").toString();
//                } catch (Exception ex) {
//                }
//            } catch (org.json.simple.parser.ParseException pe) {
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERROR PARSING params (partA, partB ecc...:" + params.toString());
//            }
//
//            // l'elenco da mostrare sarà dato dagli elementi in partBquery
//            //Poi per ogni elemento, se esiste già una correlazione con il mio valore key
//            // allora la spunta risulterà checkata
//        }
//        ArrayList<SelectListLine> myLines = new ArrayList<SelectListLine>();
//        String SQLphrase = partBquery;
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase partBquery:" + partBquery);
//
//        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
//        PreparedStatement ps = null;
//        ResultSet rs;
//        try {
//            try {
//                ps = conny.prepareStatement(SQLphrase);
//                rs = ps.executeQuery();
//                while (rs.next()) {
//                    SelectListLine myLine = new SelectListLine();
//                    myLine.setLabel(rs.getString(partBlabelField));
//                    myLine.setValue(rs.getString(partBvalueField));
//                    myLines.add(myLine);
//                }
//            } catch (SQLException ex) {
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERROR a ---------->" + ex.toString());
//            }
//            try {
//                for (int jj = 0; jj < myLines.size(); jj++) {
//                    SQLphrase = "SELECT * FROM archivio_correlazioni WHERE "
//                            + "partAtab = '" + partAtab + "' "
//                            + "AND partAvalueField = '" + partAfield + "' "
//                            + "AND partAvalue = '" + keyValue + "' "
//                            + "AND partBtab = '" + partBtab + "' "
//                            + "AND partBvalueField = '" + partBvalueField + "' "
//                            + "AND partBvalue = '" + myLines.get(jj).getValue() + "' ";
//                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase ricerca:" + SQLphrase);
//                    myLines.get(jj).setChecked(0);
//                    ps = conny.prepareStatement(SQLphrase);
//                    rs = ps.executeQuery();
//                    while (rs.next()) {
//                        myLines.get(jj).setChecked(1);
//                        break;
//                    }
//                }
//            } catch (SQLException ex) {
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERROR b ---------->" + ex.toString());
//            }
//
//            try {
////prima elimino la correlazione (vale sia per cancellare che per riscrivere 
//                SQLphrase = "DELETE FROM archivio_correlazioni WHERE "
//                        + "partAtab = '" + partAtab + "' "
//                        + "AND partAvalueField = '" + partAfield + "' "
//                        + "AND partAvalue = '" + keyValue + "' "
//                        + "AND partBtab = '" + partBtab + "' "
//                        + "AND partBvalueField = '" + partBvalueField + "' "
//                        + "AND partBvalue = '" + valueKEY + "' ";
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase eliminazione:" + SQLphrase);
//
//                ps = conny.prepareStatement(SQLphrase);
//                ps.executeUpdate();
//            } catch (SQLException ex) {
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERROR c ---------->" + ex.toString());
//            }
//            try {
//                if (Integer.parseInt(newValue) > 0) {
//                    SQLphrase = "INSERT INTO archivio_correlazioni  ( partAtab,partAvalueField, partAvalue,"
//                            + "partBtab,partBvalueField,partBvalue"
//                            + ")VALUES('" + partAtab + "','" + partAfield + "','" + keyValue + "',"
//                            + "'" + partBtab + "','" + partBvalueField + "','" + valueKEY + "')";
//                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "SQLphrase inserimento ---------->" + SQLphrase);
//                    ps = conny.prepareStatement(SQLphrase);
//                    ps.executeUpdate();
//                }
//            } catch (SQLException ex) {
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERROR d ---------->" + ex.toString());
//            }
//            conny.close();
//        } catch (SQLException ex) {
//            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "ERROR ---------->" + ex.toString());
//        }
//        request.setResponse((Object) HtmlCode);
        return request;
    }

    public IncomingRequest populateTitle(IncomingRequest request) {
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "populateTitle:" + request.getRemoteIP());
        String HtmlCode = "";

        HtmlCode += "<TABLE><TR> ";
        HtmlCode += " <td>";

        int xW = 100;
        int xH = 100;

        String QAcode1 = "<div class=\"mydiv\" id='qa'>"
                + "<a  href=\"qa.jsp\" style=\"display:block;\"> ";
        String QAcode2 = "</a></div>";

        String image = "<img  alt=\"...\" src='portal?target=requestsManager&gp=";

        String params = "\"params\":\"" + encodeURIComponent(myParams.makePORTALparams()) + "\"";
        String connectors = "\"connectors\":[{\"door\":\"RenderPic\","
                + "\"event\":\"title\","
                + "\"table\":\"" + request.getMySettings().getLocalFE_directives() + "\","
                + "\"keyfield\":\"infoName\","
                + "\"keyValue\":\"softwareLogo\","
                + "\"picfield\":\"media\" "
                + " }]";
        String utils = "\"responseType\":\"text\"";
        String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";

        image += encodeURIComponent(gp);
        image += "&rnd=$$$newRandom$$$'  width='" + xW + "' heigth='" + xH + "' >";

        image = QAcode1 + image + QAcode2;

        HtmlCode += image;

        HtmlCode += "</td>";

        HtmlCode += " <td>";
        String form = pageBlockTitle(request);
        HtmlCode += form;
        HtmlCode += "</td></tr></TABLE>";
        // el.log(myParams.getCKprojectName()+myParams.getCKcontextID()+"eventManager","HtmlCode:" + HtmlCode);
        request.setResponse((Object) HtmlCode);
        return request;
    }

    public String remakeQuery(String originalQuery, String newWhereCondition) {
        String newQuery = "";
        if (originalQuery != null && originalQuery.length() > 1) {

            String smartTile = "";
            String afterWHERE = "";
            String smartPartToKeep = "";
            // System.out.println("----SMART--- query iniziale:" + this.query);
            //1. cerco la posizione dell'ultimo WHERE           
            int lastWHEREposition = 0;
            String text = originalQuery;
            String word = "WHERE";
            for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                lastWHEREposition = i;
            }

            smartPartToKeep = originalQuery;
            if (lastWHEREposition <= 0) {
                // non ci sono WHERE
                afterWHERE = originalQuery;
                text = afterWHERE;
                int lastGROPUPBYposition = 0;
                word = "GROUP BY";
                for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                    lastGROPUPBYposition = i;
                }
                if (lastGROPUPBYposition <= 0) {
                    // non ci sono GROPUPBY.. CERCO almeno un ORDER BY
                    int lastORDERBYposition = 0;
                    word = "ORDER BY";
                    for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                        lastORDERBYposition = i;
                    }
                    if (lastORDERBYposition <= 0) {
                        // non ci sono orderby

                    } else {
                        //c'è un orderby da considerare dopo il where
                        smartPartToKeep = originalQuery.substring(0, lastORDERBYposition);
                        smartTile = " ORDER BY " + text.substring(lastORDERBYposition + 8, text.length());
                    }
                } else {
                    //c'è un groupby da considerare dopo il where e se c'è conterrà anche l'order by
                    smartPartToKeep = originalQuery.substring(0, lastGROPUPBYposition);
                    smartTile = " GROUP BY " + text.substring(lastGROPUPBYposition + 8, text.length());
                }

            } else {
                // System.out.println("----SMART--- posizione ultimo WHERE:" + lastWHEREposition);
                //2. tutta la parte di testo prima del WHERE è da tenere          
                smartPartToKeep = originalQuery.substring(0, lastWHEREposition);
                // System.out.println("----SMART--- da tenere:" + smartPartToKeep);

                afterWHERE = originalQuery.substring(lastWHEREposition + 5, originalQuery.length());
                text = afterWHERE;
                int lastGROPUPBYposition = 0;
                word = "GROUP BY";
                for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                    lastGROPUPBYposition = i;
                }
                if (lastGROPUPBYposition <= 0) {
                    // non ci sono GROPUPBY.. CERCO almeno un ORDER BY
                    int lastORDERBYposition = 0;
                    word = "ORDER BY";
                    for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                        lastORDERBYposition = i;
                    }
                    if (lastORDERBYposition <= 0) {
                        // non ci sono orderby

                    } else {
                        //c'è un orderby da considerare dopo il where
                        smartTile = " ORDER BY " + text.substring(lastORDERBYposition + 8, text.length());
                    }
                } else {
                    //c'è un groupby da considerare dopo il where e se c'è conterrà anche l'order by
                    smartTile = " GROUP BY " + text.substring(lastGROPUPBYposition + 8, text.length());

                }

            }

            originalQuery = smartPartToKeep + newWhereCondition + smartTile;
        }
        newQuery = originalQuery;
        return newQuery;
    }

    public IncomingRequest populateWS(IncomingRequest request) {
        // STATUSBAR DEL WEBSOCKET

        String HtmlCode = "";

        HtmlCode += (""
                + "<TABLE  >"
                + "<tr><td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"timeGoesHere\"></span>\n"
                + "</td>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"message\"></span>\n"
                + " </td>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"serverConnected\"></span>\n"
                + " </td>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"testLine\"></span>\n"
                + " </td>"
                + "<td>"
                //-----
                + "<TABLE  style = \"border: 0px solid black;border-collapse: collapse;\">"
                + "<tr>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"busyLine\"></span>\n"
                + "</td>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"ledLine\"></span>\n"
                + "</td>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"statusLine\"></span>\n"
                + " </td>"
                + "</tr></table>"
                //-----                
                + "</td>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"backupLine\"></span>\n"
                + "</td>"
                /* + "<td>"
                 + "<input type=\"TEXT\" id=\"nameField\" value=\"\" />"
                 + "<input type=\"submit\" value=\"Send Name\" onclick=\"sendName()\" />"
                 + " </td>"
                 + "<td>"
                 + "RECV: <span style=\"width:100px; font-family: \"Verdana\", Georgia, sans-Serif;font-size:xx-small;\" id=\"recv\"></span>\n"
                 + " </td>"
                 + "<td>"
                 + "RAW: <span style=\"width:100px; font-family: \"Verdana\", Georgia, sans-Serif;font-size:xx-small;\" id=\"raw\"></span>\n"
                 + " </td>"
                 + "<td>"
                 + "MAP: <span style=\"width:100px; font-family: \"Verdana\", Georgia, sans-Serif;font-size:xx-small;\" id=\"map\"></span>\n"
                 +" </td>"
                 + "<td></td>"*/
                + " </tr>"
                + "</TABLE> "
                + " </td></tr>"
                + "</TABLE> ");
        request.setResponse(HtmlCode);
        return request;
    }

    public IncomingRequest populateQPWS(IncomingRequest request) {
        String HtmlCode = "";

        HtmlCode += (""
                + "<TABLE>"
                + "<tr><td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"LHtimeGoesHere\"></span>\n"
                + "</td>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"LHmessage\"></span>\n"
                + " </td>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"LHserverConnected\"></span>\n"
                + " </td>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"LHtestLine\"></span>\n"
                + " </td>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"LHstatusLine\"></span>\n"
                + "</td>"
                + "<td>"
                //-----
                + "<TABLE  style = \"border: 0px solid black;border-collapse: collapse;\">"
                + "<tr><td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"lighthouseStatusLine\"></span>\n"
                + " </td>"
                + "<td>"
                + "<span style=\"width:100px; font-family: 'Verdana', Georgia, sans-Serif;font-size:xx-small;\" id=\"LHledLine\"></span>\n"
                + "</td>"
                + "</tr></table>"
                //-----
                + " </td></tr>"
                + "</TABLE> "
                + " </td></tr>"
                + "</TABLE> ");
        request.setResponse(HtmlCode);
        return request;
    }

    public String getRowImageHtmlCode(BufferedImage image) {
        int imgw = 40;
        int imgh = 40;
        try {
            imgw = image.getWidth();
            imgh = image.getHeight();
        } catch (Exception e) {

        }
        String picCode = getRowImageHtmlCode(image, imgw, imgh);

        return picCode;
    }

    public String getRowImageHtmlCode(BufferedImage image, int W, int H) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String picCode = "";
        String imageString = null;
        int radio = 10;
        if (image != null) {
            try {
                int HH = (int) image.getWidth();
                if (HH > 20) {
                    radio = HH / 2;
                }
            } catch (Error e) {
            }
            BufferedImage Rimage = makeRoundedCorner(image, radio);
            try {

                ImageIO.write(Rimage, "gif", bos);
                byte[] imageBytes = bos.toByteArray();
                imageString = Base64.getEncoder().encodeToString(imageBytes);
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            imageString = "";
        }
        int imgw = W;
        int imgh = H;

        picCode += "<img src=\"data:image/gif;base64," + imageString + "\" alt=\"" + "" + "\"";
        picCode += "   width=\"" + imgw + "px\" heigth=\"" + imgh + "px\" ";
        picCode += " />";
//        System.out.println("picCode:\n" + picCode);
        return picCode;
    }

    public IncomingRequest populateTitleBar(IncomingRequest request) {
        String HtmlCode = "";  //        Settings mySettings = new Settings();
        String title = mySettings.getSoftwareTitle();
        EVOpagerDirectivesManager myDirective = new EVOpagerDirectivesManager(myParams, mySettings);
        BufferedImage myLogo = null;
        myLogo = myDirective.getDirectiveMediaBI("softwareLogo");
        Rectangle bs;
        bs = new Rectangle(70, 70);
        String imageCode = getRowImageHtmlCode(myLogo, 60, 60);
        HtmlCode += (""
                + "<TABLE border = \"0\">"
                + "<tr><td>"
                + "<table><tr>"
                + "<td style=\"vertical-align: top; font-size: 400%;font-style: oblique;font-weight: bold;\">"
                + imageCode
                + " </td>"
                + "<td style=\"vertical-align: top; font-size: 400%;font-style: oblique;font-weight: bold;\">"
                + title
                + " </td>"
                + "</tr>"
                + "</TABLE> ");

        System.out.println("title:" + title);
        request.setResponse(HtmlCode);
        return request;
    }

    public IncomingRequest populateSession(IncomingRequest request) {
        String HtmlCode = "";
        String user = request.getMyParams().getCKuserID();
        EVOuser myUser = new EVOuser(request.getMyParams(), request.getMySettings());
        myUser.setUserID(user);
        String picCode = myUser.getUserPicture();
        String accountHolderCode = myUser.getUserAccountHolderButton();
        String logoutButtonCode = myUser.getUserLogoutButton();
        String updateFrontendButtonCode = myUser.getFrontendUpdateButton();
        HtmlCode += (""
                + "<TABLE border = \"0\">"
                + "<tr><td>"
                + "<table><tr>"
                + "<td>"
                + "<a href=\"javascript:backHome()\"> "
                + "<img border=\"0\" alt=\"Home\" src=\"./media/icons/gaiaTopHome.png\" "
                + "width=\"30\" height=\"30\">\n"
                + "</a>"
                + "</td>"
                + "</tr></table>"
                + " </td>"
                + "<td>"
                + logoutButtonCode
                + " </td>" + "<td>"
                + updateFrontendButtonCode
                + " </td>"
                + "<td>"
                + accountHolderCode
                + " </td>"
                + "</tr>"
                + "</TABLE> ");
        request.setResponse(HtmlCode);
        return request;
    }

    public IncomingRequest populatePeople(IncomingRequest request) {

        String tabOperatori = mySettings.getAccount_TABLEoperatori();
        String HtmlCode = "";
        String user = request.getMyParams().getCKuserID();
        ArrayList<String> contatti = new ArrayList<String>();

        try {

            Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

            String SQLphrase = "SELECT archivio_sessions.*," + tabOperatori + ".username "
                    + "FROM archivio_sessions "
                    + "LEFT JOIN " + tabOperatori + " ON " + tabOperatori + ".ID =archivio_sessions.user "
                    + "WHERE `user`<>'" + myParams.getCKuserID() + "' "
                    + "GROUP BY user ORDER BY recorded DESC";
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-> " + SQLphrase + " ");
            PreparedStatement ps = null;
            ResultSet rs = null;

            ps = accountConny.prepareStatement(SQLphrase);
            rs = null;
            rs = ps.executeQuery();
            while (rs.next()) {

                try {
                    String contatto = rs.getString(tabOperatori + ".username");
                    if (contatto != null && contatto.length() > 0) {
                        contatti.add(contatto);
                    }
                } catch (Exception ex) {

                }
            }

            accountConny.close();
        } catch (SQLException ex) {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Errore in accesso al DB degli utenti");
        }
        HtmlCode += "CONTATTI CONNESSI:";
        HtmlCode += "<TABLE border = \"0\">";
        if (contatti.size() > 0) {
            for (int kk = 0; kk < contatti.size(); kk++) {
                HtmlCode += "<tr><td>" + contatti.get(kk) + "<td><tr>";
            }

        } else {
            HtmlCode += "<tr><td>Non ci sono contatti in linea<td><tr>";
        }

        HtmlCode += "</TABLE> ";
        request.setResponse(HtmlCode);
        return request;
    }

    public IncomingRequest login(IncomingRequest request) {
        logEvent myEvent = new logEvent();
        myEvent.setType("Login");
        String tabOperatori = mySettings.getAccount_TABLEoperatori();
//        el.setPrintOnScreen(true);
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nEvent Manager LOGIN; username:" + request.getMyGate().getUsername() + " - pass:" + request.getMyGate().getPassword());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "getAccessType:" + mySettings.getAccessType());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "remoteIP:" + request.getRemoteIP());
//        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "getAdminConfirmationRequested:" + mySettings.getAdminConfirmationRequested() + " ");

        String pass = request.getMyGate().getPassword();
        String username = request.getMyGate().getUsername();
        String remoteIP = request.getRemoteIP();
        String XRealIP = request.getXRealIP();
        String XForwardedFor = request.getXForwardedFor();
        System.out.println("XRealIP:" + XRealIP);
        System.out.println("XForwardedFor:" + XForwardedFor);
        System.out.println("remoteIP:" + remoteIP);
        if (XRealIP != null) {
            myEvent.setRemoteIP(XRealIP);
        } else if (XForwardedFor != null) {
            myEvent.setRemoteIP(XForwardedFor);
        } else {
            myEvent.setRemoteIP(remoteIP);
        }
        System.out.println("USED CLIENT IP :" + myEvent.getRemoteIP());

        /*
         posso accettare
         
         Type UNPC   username + pincode
         Type UNPW   username + password
         Type MLPW   mail + password
         
         */
        String descrizioneErrore = "";
        String name = "";
        String surname = "";
        String pincode = "";
        String password = "";
        String ID = "";
        int utenteAttivo = 0;
        int esito = 0;
        boolean st = false;
        String eventType = "PERIODIC";
        if (eventType != null
                && eventType != "FORCED"
                && eventType != "ALLDB") {
            eventType = "PERIODIC";
        }
        el.setPrintOnScreen(true);
        if (mySettings.getAccessType().equalsIgnoreCase("UNPC")
                || mySettings.getAccessType().equalsIgnoreCase("UNPC+MLPW")) {//username e pincode
            try {

                Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

                String SQLphrase = "SELECT * FROM  " + tabOperatori + " "
                        + "WHERE alive >=0 "
                        + "AND username ='" + username + "' ";
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-> " + SQLphrase + " ");
                PreparedStatement ps = null;
                ResultSet rs = null;

                ps = accountConny.prepareStatement(SQLphrase);
                rs = null;
                rs = ps.executeQuery();

                st = false;
                while (rs.next()) {

                    try {
                        ID = rs.getString("ID");
                    } catch (Exception ex) {
                        ID = "";
                    }
                    try {
                        utenteAttivo = rs.getInt("alive");
                    } catch (Exception ex) {
                        utenteAttivo = 0;
                    }
                    try {
                        pincode = rs.getString("pincode");
                    } catch (Exception ex) {
                        pincode = "";
                    }

                    try {
                        name = rs.getString("name");
                    } catch (Exception ex) {
                        name = "N.D.";
                    }
                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n---> " + name + " ");
                    try {
                        surname = rs.getString("surname");
                    } catch (Exception ex) {
                        surname = "N.D.";
                    }
// parsando tutti gli operatori con username indicato (possino essere diversi)
//                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Confronto password inserita dall'utente (" + pass + ") con password da database (" + pincode + ")");
                    System.out.println("PINCODE RICHIESTO: " + pincode);
                    System.out.println("PINCODE INSERITO: " + pass);
                    System.out.println("AccessType: " + mySettings.getAccessType());
                    if (pincode != null && pincode.length() > 1
                            && pincode.equalsIgnoreCase(pass)
                            && (mySettings.getAccessType().equalsIgnoreCase("UNPC"))) {
                        st = true;
                        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-> PINCODE MATCHES ! ");
                        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "mySettings.getAccessType()->  " + mySettings.getAccessType());
                        break;
                    } else {
                        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-> PINCODE NON CORRISPONDENTE ");
                    }

                }
                accountConny.close();
            } catch (SQLException ex) {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Errore in accesso al DB degli utenti");
            }
        }

        if (st == false
                && (mySettings.getAccessType().equalsIgnoreCase("MLPW")
                || mySettings.getAccessType().equalsIgnoreCase("UNPC+MLPW"))) {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-> SECONDO TENTATIVO CON MAIL + PASSSWORD" + "UNPC+MLPW");

            int lines = 0;
            try {
                // myParams.printParams(" EventManager.login ");
                // mySettings.printSettings(" EventManager.login ");
                Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
                String pwKEY = mySettings.getPasswordKey("");
                String DATABASE_FIELD_PW = "AES_DECRYPT(`password`, '" + pwKEY + "')";
                String SQLphrase = "SELECT " + tabOperatori + ".*," + DATABASE_FIELD_PW + "AS clearPW "
                        + "FROM  " + tabOperatori + " WHERE `alive`>=-9 "
                        + "AND email ='" + username + "' "
                        + " ";
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nCerco accesso-> " + SQLphrase + " ");
                PreparedStatement ps = null;
                ResultSet rs = null;

                ps = accountConny.prepareStatement(SQLphrase);
                rs = null;
                rs = ps.executeQuery();

                st = false;
                while (rs.next()) {
                    lines++;

                    try {
                        ID = rs.getString("ID");
                    } catch (Exception ex) {
                        ID = "";
                    }
                    try {
                        utenteAttivo = rs.getInt("alive");
                    } catch (Exception ex) {
                        utenteAttivo = 0;
                    }
                    try {
                        pincode = rs.getString("pincode");
                        //System.out.println("pincode:" + pincode );
                    } catch (Exception ex) {
                        pincode = "";
                    }
                    try {
                        password = rs.getString("clearPW");
                    } catch (Exception ex) {
                        password = "";
                    }
//                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\n\nDBpassword criptata = " + password);
//                    System.out.println("\n\n\nDBpassword criptata = " + password);

                    try {
                        name = rs.getString("name");
                    } catch (Exception ex) {
                        name = "N.D.";
                    }
                    try {
                        surname = rs.getString("surname");
                    } catch (Exception ex) {
                        surname = "N.D.";
                    }

                    break;
                }
// System.out.println("pwKEY:" + pwKEY+  "   pass:" + pass + " - password:" + password);

                accountConny.close();
            } catch (SQLException ex) {

            }
            //            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "pass:" + pass + " - password:" + password);
            if (lines > 0 && password != null && password.equals(pass)) {

                st = true;
                // } else if (password == null) {
                //    st = true;
            } else if (lines > 0
                    && password == null
                    && pass.equals("")
                    && ID.equalsIgnoreCase("ADMIN")) {
                st = true;
                // } else if (password == null) {
                //    st = true;
            } else {
                st = false;
            }

        }

        myEvent.setUser(ID);
        //  userExtendedName = "" + name + " " + surname;
        boolean success = st;
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\n-----------\nPRE ESITO DEL LOGIN:\n-------");
        if (success == true && utenteAttivo < 1 && !ID.equalsIgnoreCase("ADMIN")) {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Login corretto. Utente non abilitato/attivo.");
            esito = -1;
        } else if (success == true) {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Accesso consentito!");
            esito = 1;
            UUID idOne = null;
            idOne = UUID.randomUUID();
            String newToken = "" + idOne;
            myParams.setCKtokenID(newToken);
            myParams.setCKuserID(ID);
            myEvent.setToken(newToken);
            // compilo database dei login
            Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
            String SQLphrase = "INSERT INTO `" + mySettings.getAccount_TABLEtokens() + "`("
                    + "`token`, `rifUser`,  `updated`, `jsessionID`, "
                    + "`remoteIP`, `loggedStatus`, `projectGroup`, `projectName`, `contextID`"
                    + ") VALUES ("
                    + "?,?,now(),?,?,?,?,?,?)";

            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                ps = accountConny.prepareStatement(SQLphrase);

                ps.setString(1, myParams.getCKtokenID());
                ps.setString(2, myParams.getCKuserID());
                ps.setString(3, "newSession");
                ps.setString(4, remoteIP);
                ps.setInt(5, 1);
                ps.setString(6, myParams.getCKprojectGroup());
                ps.setString(7, mySettings.getProjectName());
                ps.setString(8, myParams.getCKcontextID());
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nINSERIMENTO TOKEN.\n" + ps.toString());
                int i = ps.executeUpdate();
                esito = i;

            } catch (SQLException ex) {
                System.out.println("\n\n\nTENTIAMO CON LOWERCASE ");
                SQLphrase = "INSERT INTO `" + mySettings.getAccount_TABLEtokens().toLowerCase() + "`("
                        + "`token`, `rifUser`,  `updated`, `jsessionID`, "
                        + "`remoteIP`, `loggedStatus`, `projectGroup`, `projectName`, `contextID`"
                        + ") VALUES ("
                        + "?,?,now(),?,?,?,?,?,?)";

                try {
                    ps = accountConny.prepareStatement(SQLphrase);

                    ps.setString(1, myParams.getCKtokenID());
                    ps.setString(2, myParams.getCKuserID());
                    ps.setString(3, "newSession");
                    ps.setString(4, remoteIP);
                    ps.setInt(5, 1);
                    ps.setString(6, myParams.getCKprojectGroup());
                    ps.setString(7, mySettings.getProjectName());
                    ps.setString(8, myParams.getCKcontextID());
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nINSERIMENTO TOKEN.\n" + ps.toString());
                    int i = ps.executeUpdate();
                    esito = i;

                } catch (SQLException e) {
//                    Logger.getLogger(eventManager.class
//                            .getName()).log(Level.SEVERE, null, e);
                }
            }

//            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\n\n\n\tLOGIN EFFETUATO.\n\n\n\n\n");
//*****************************************************            
            // EFFETTUO IL BACKUP
            EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
            String BackupRequested = myManager.getDirective("BackupRequested");
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "BackupRequested:" + BackupRequested);
            if (BackupRequested != null && BackupRequested.equalsIgnoreCase("true")) {
                try {
                    EVOsetup mySetup = new EVOsetup(myParams, mySettings, eventType);
                } catch (Exception e) {
                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "EVO catcher : errore nella fase di backup.\n");
                }
            } else {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Backup disabilitato.\n");

            }
//*****************************************************
        } else {
            esito = 0;
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Login:Username or Password incorrect");
            /*   try {
             EVOsetup mySetup = new EVOsetup(myParams, mySettings, "LIGHT");
             } catch (Exception e) {
            el.log(myParams.getCKprojectName()+myParams.getCKcontextID()+"eventManager","EVO catcher : errore nella fase di backup.\n");
             }*/
            String tableTokens = mySettings.getAccount_TABLEtokens();
            Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

            PreparedStatement ps = null;
            ResultSet rs = null;
            String SQLphrase = "INSERT INTO `" + tableTokens + "`("
                    + "`token`, `rifUser`,  `updated`, `jsessionID`, "
                    + "`remoteIP`, `loggedStatus`, `projectGroup`, `projectName`, `contextID`"
                    + ") VALUES ("
                    + "?,?,now(),?,?,?,?,?,?)";
            try {
                ps = accountConny.prepareStatement(SQLphrase);
                try {

                    ps.setString(1, "OVER_" + myParams.getCKtokenID());
                    ps.setString(2, username);
                    ps.setString(3, "BadCredentials");
                    ps.setString(4, remoteIP);
                    ps.setInt(5, -2);
                    ps.setString(6, myParams.getCKprojectGroup());
                    ps.setString(7, mySettings.getProjectName());
                    ps.setString(8, myParams.getCKcontextID());
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nINSERIMENTO TOKEN.\n" + ps.toString());
                    int i = ps.executeUpdate();

                } catch (SQLException ex) {
                    Logger.getLogger(eventManager.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                SQLphrase = "INSERT INTO `" + tableTokens.toLowerCase() + "`("
                        + "`token`, `rifUser`,  `updated`, `jsessionID`, "
                        + "`remoteIP`, `loggedStatus`, `projectGroup`, `projectName`, `contextID`"
                        + ") VALUES ("
                        + "?,?,now(),?,?,?,?,?,?)";
                try {
                    ps = accountConny.prepareStatement(SQLphrase);
                    try {

                        ps.setString(1, "OVER_" + myParams.getCKtokenID());
                        ps.setString(2, username);
                        ps.setString(3, "BadCredentials");
                        ps.setString(4, remoteIP);
                        ps.setInt(5, -2);
                        ps.setString(6, myParams.getCKprojectGroup());
                        ps.setString(7, mySettings.getProjectName());
                        ps.setString(8, myParams.getCKcontextID());
//                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nINSERIMENTO TOKEN.\n" + ps.toString());
                        int i = ps.executeUpdate();

                    } catch (SQLException ep) {
                        Logger.getLogger(eventManager.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (SQLException ee) {
                }
            }

        }

        myParams.setLoginResult(esito);
        int i = esito;
        String newPage = "";
        System.out.println("ESITO LOGIN: " + esito);
        if (i > 0) {// login ok
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n*********\nInserito il TOKEN:" + myParams.getCKtokenID());

            String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());

            String connectors = "\"connectors\":[{\"door\":\"FormShow\","
                    + "\"event\":\"firstForm\","
                    + "\"formName\":\"mainForm\" }]";
            String utils = "\"responseType\":\"text\"";
            String gp = "{" + utils + "," + params + "," + connectors + "}";
            newPage = "portal?type=1&target=requestsManager&gp=" + encodeURIComponent(gp);
            request.setOutputStreamType("standard");
            request.setSessionValid(1);
            myEvent.setEventCode("loginOK");
        } else if (i == 0) {//password sbagliata
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Sono in Login : esco per credenziali errate.\n");
            String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
            String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                    + "\"event\":\"LoginForm\","
                    + "\"type\":\"badCredentials\" }]";
            String utils = "\"responseType\":\"text\"";
            String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
            newPage = "portal?type=2&target=requestsManager&gp=" + encodeURIComponent(gp);
            request.setOutputStreamType("standard");
            myEvent.setEventCode("badCredentials");
        } else if (descrizioneErrore != "") {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Sono in Login : esco per pincodeOnWeb.\n");
            request.getMyGate().setType("pincodeOnWeb");
            String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
            String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                    + "\"event\":\"LoginForm\","
                    + "\"type\":\"pincodeOnWeb\" }]";
            String utils = "\"responseType\":\"text\"";
            String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
            newPage = "portal?type=3&target=requestsManager&gp=" + encodeURIComponent(gp);
            request.setOutputStreamType("standard");

            myEvent.setEventCode("pincodeOnWeb");

        } else {
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Sono in Login : esco perchè l'utente non è attivo.\n");
            String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
            String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                    + "\"event\":\"LoginForm\","
                    + "\"type\":\"accessDenied\" }]";
            String utils = "\"responseType\":\"text\"";
            String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
            newPage = "portal?type=4&target=requestsManager&gp=" + encodeURIComponent(gp);
            request.setOutputStreamType("standard");
            myEvent.setEventCode("accessDenied");

        }
        try {
            myEvent.save(myParams, mySettings);
        } catch (Exception e) {
        }

        request.setResponse((Object) newPage);
        return request;
    }

    public IncomingRequest quickLogin(IncomingRequest request) {

        String newPage = "";

        boolean accessByPincode = false;
        boolean accessByAccount = false;

        if (mySettings.getAccessType().equalsIgnoreCase("UNPC+MLPW")) {
            accessByPincode = true;
            accessByAccount = true;
        } else if (mySettings.getAccessType().equalsIgnoreCase("UNPC")) {
            accessByPincode = true;
            accessByAccount = false;
        } else if (mySettings.getAccessType().equalsIgnoreCase("MLPW")) {
            accessByPincode = false;
            accessByAccount = true;
        }

        String remoteIP = request.getRemoteIP();
        String tabOperatori = mySettings.getAccount_TABLEoperatori();

        String pass = request.getMyGate().getPassword();
        String username = request.getMyGate().getUsername();
        String tipoAccesso = "";
        String QAC = username;

        String[] couples = username.split(":");
        List<String> param = Arrays.asList(couples);
        if (param.size() > 1) {
            SelectListLine parametro = new SelectListLine();
            tipoAccesso = (param.get(0));
            QAC = (param.get(1));
        }
        logEvent myEvent = new logEvent();
        myEvent.setType("QuickLogin");
        myEvent.setRemoteIP(remoteIP);
        myEvent.setUser(myParams.getCKuserID());
        myEvent.setToken(QAC);

        if (accessByPincode && getLocalClient(request) == true) {

            System.out.println("tipoAccesso:" + tipoAccesso);
            System.out.println("QAC:" + QAC);
            el.setPrintOnScreen(true);

            String descrizioneErrore = "";
            String name = "";
            String surname = "";
            String pincode = "";
            String password = "";
            String ID = "";
            int utenteAttivo = 0;
            int esito = 0;
            boolean st = false;
            Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

            String SQLphrase = "SELECT * FROM  " + tabOperatori + " WHERE"
                    + " `alive`=1 AND `quickAccessToken`='" + username + "' ";
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "-> " + SQLphrase + " ");
            PreparedStatement ps = null;
            ResultSet rs = null;
            int lines = 0;
            try {
                ps = accountConny.prepareStatement(SQLphrase);
                rs = null;
                rs = ps.executeQuery();

                st = false;
                while (rs.next()) {
                    lines++;
                    try {
                        ID = rs.getString("ID");
                    } catch (Exception ex) {
                        ID = "";
                    }
                    try {
                        utenteAttivo = rs.getInt("alive");
                    } catch (Exception ex) {
                        utenteAttivo = 0;
                    }
                    try {
                        name = rs.getString("name");
                    } catch (Exception ex) {
                        name = "N.D.";
                    }
                    try {
                        surname = rs.getString("surname");
                    } catch (Exception ex) {
                        surname = "N.D.";

                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(eventManager.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

            if (lines == 1) {
                st = true;
            } else {
                st = false; // nega accesso se non esiste ma anche se ci sono più righe con lo stesso token
            }

            boolean success = st;
            el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\n-----------\nPRE ESITO DEL LOGIN:\n-------");
            if (success && (utenteAttivo < 1 && !ID.equalsIgnoreCase("ADMIN"))) {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Login corretto. Utente non abilitato/attivo.");
                esito = -1;
            } else if (success) {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Accesso consentito!");
                esito = 1;
                UUID idOne = null;
                idOne = UUID.randomUUID();
                String newToken = "" + idOne;
                myParams.setCKtokenID(newToken);
                myParams.setCKuserID(ID);
                String tableTokens = mySettings.getAccount_TABLEtokens();
                // compilo database dei login
                //  Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

                ps = null;
                rs = null;
                SQLphrase = "INSERT INTO `" + tableTokens + "`("
                        + "`token`, `rifUser`,  `updated`, `jsessionID`, "
                        + "`remoteIP`, `loggedStatus`, `projectGroup`, `projectName`, `contextID`, `quickAccessToken`"
                        + ") VALUES ("
                        + "?,?,now(),?,?,?,?,?,?,?)";
                System.out.println("SQLphrase:" + SQLphrase);
                try {
                    ps = accountConny.prepareStatement(SQLphrase);
                } catch (SQLException ex) {
                    SQLphrase = "INSERT INTO `" + tableTokens.toLowerCase() + "`("
                            + "`token`, `rifUser`,  `updated`, `jsessionID`, "
                            + "`remoteIP`, `loggedStatus`, `projectGroup`, `projectName`, `contextID`, `quickAccessToken`"
                            + ") VALUES ("
                            + "?,?,now(),?,?,?,?,?,?,?)";
                    System.out.println("SQLphrase:" + SQLphrase);
                    try {
                        ps = accountConny.prepareStatement(SQLphrase);
                    } catch (SQLException ee) {
                        Logger.getLogger(eventManager.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    ps.setString(1, myParams.getCKtokenID());
                    ps.setString(2, myParams.getCKuserID());
                    ps.setString(3, "newSession");
                    ps.setString(4, remoteIP);
                    ps.setInt(5, 1);
                    ps.setString(6, myParams.getCKprojectGroup());
                    ps.setString(7, mySettings.getProjectName());
                    ps.setString(8, myParams.getCKcontextID());
                    ps.setString(9, QAC);
                    el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nINSERIMENTO TOKEN.\n" + ps.toString());
                    int i = ps.executeUpdate();
                    esito = i;

                } catch (SQLException ex) {
                    Logger.getLogger(eventManager.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\n\n\n\tLOGIN EFFETUATO.\n\n\n\n\n");

            } else {
                esito = 0;
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Login:Username or Password incorrect");
                /*   try {
             EVOsetup mySetup = new EVOsetup(myParams, mySettings, "LIGHT");
             } catch (Exception e) {
            el.log(myParams.getCKprojectName()+myParams.getCKcontextID()+"eventManager","EVO catcher : errore nella fase di backup.\n");
             }*/

            }

            myParams.setLoginResult(esito);
            int i = esito;

            if (i > 0) {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n*********\nInserito il TOKEN:" + myParams.getCKtokenID());

                String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());

                String connectors = "\"connectors\":[{\"door\":\"FormShow\","
                        + "\"event\":\"firstForm\","
                        + "\"formName\":\"" + tipoAccesso + "mainForm\" }]";
                String utils = "\"responseType\":\"text\"";
                String gp = "{" + utils + "," + params + "," + connectors + "}";
                newPage = "portal?type=1&target=requestsManager&gp=" + encodeURIComponent(gp);
                request.setOutputStreamType("standard");
                request.setSessionValid(1);
                myEvent.setEventCode("loginOK");
            } else if (i == 0) {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Sono in Login : esco per credenziali errate.\n");
                String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
                String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                        + "\"event\":\"LoginForm\","
                        + "\"type\":\"badCredentials\" }]";
                String utils = "\"responseType\":\"text\"";
                String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
                newPage = "portal?type=2&target=requestsManager&gp=" + encodeURIComponent(gp);
                request.setOutputStreamType("standard");
                myEvent.setEventCode("badCredentials");
            } else if (descrizioneErrore != "") {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Sono in Login : esco per pincodeOnWeb.\n");
                request.getMyGate().setType("pincodeOnWeb");
                String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
                String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                        + "\"event\":\"LoginForm\","
                        + "\"type\":\"pincodeOnWeb\" }]";
                String utils = "\"responseType\":\"text\"";
                String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
                newPage = "portal?type=3&target=requestsManager&gp=" + encodeURIComponent(gp);
                request.setOutputStreamType("standard");

                myEvent.setEventCode("pincodeOnWeb");
            } else {
                el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "Sono in Login : esco perchè l'utente non è attivo.\n");
                String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
                String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                        + "\"event\":\"LoginForm\","
                        + "\"type\":\"accessDenied\" }]";
                String utils = "\"responseType\":\"text\"";
                String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
                newPage = "portal?type=4&target=requestsManager&gp=" + encodeURIComponent(gp);
                request.setOutputStreamType("standard");

                myEvent.setEventCode("accessDenied");
            }
            try {
                accountConny.close();

            } catch (SQLException ex) {
                Logger.getLogger(eventManager.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {// non attivo il quicklogin
            request.getMyGate().setType("pincodeOnWeb");
            String params = "\"params\":" + encodeURIComponent(myParams.makePORTALparams());
            String connectors = "\"connectors\":[{\"door\":\"AccountManager\","
                    + "\"event\":\"LoginForm\","
                    + "\"type\":\"pincodeOnWeb\" }]";
            String utils = "\"responseType\":\"text\"";
            String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
            newPage = "portal?type=3&target=requestsManager&gp=" + encodeURIComponent(gp);
            request.setOutputStreamType("standard");
            myEvent.setEventCode("nonLocalIP");

        }
        try {
            myEvent.save(myParams, mySettings);
        } catch (Exception e) {
        }
        request.setResponse((Object) newPage);
        return request;
    }

    private boolean getLocalClient(IncomingRequest request) {
        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\n\n\nSONO IN getLocalClient " + request.getRemoteIP());

        Boolean localClient = false;
        String localIP = "";
        String remoteIP = request.getRemoteIP();
        String XRealIP = request.getXRealIP();
        String XForwardedFor = request.getXForwardedFor();
        dummyMessage = "\nremoteIP:" + remoteIP;

        try {
            InetAddress ip = InetAddress.getLocalHost();
            localIP = ip.getHostAddress();

        } catch (UnknownHostException ex) {
            Logger.getLogger(eventManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("SERVER LOCAL IP:" + localIP);
        System.out.println("remoteIP IP:" + remoteIP);
        System.out.println("XRealIP IP:" + XRealIP);
        System.out.println("XForwardedFor IP:" + XForwardedFor);

        if (XRealIP != null && XRealIP != "null") {
            remoteIP = XRealIP;
        }

        int flag = 0;
        if (!remoteIP.equalsIgnoreCase("0:0:0:0:0:0:0:1") && !remoteIP.equalsIgnoreCase("127.0.0.1")) {
            String[] remoteIPparts = remoteIP.split("\\.", -1);
            String[] localIPparts = localIP.split("\\.", -1);
            List<String> remoteparts = Arrays.asList(remoteIPparts);
            List<String> localparts = Arrays.asList(localIPparts);
            if (localparts.size() > 2 && remoteparts.size() > 2) {

                if (localparts.get(0) != null && remoteparts.get(0) != null
                        && localparts.get(0).equalsIgnoreCase(remoteparts.get(0))) {
                    flag++;
                }
                if (localparts.get(1) != null && remoteparts.get(1) != null
                        && localparts.get(1).equalsIgnoreCase(remoteparts.get(1))) {
                    flag++;
                }
                if (localparts.get(2) != null && remoteparts.get(1) != null
                        && localparts.get(2).equalsIgnoreCase(remoteparts.get(2))) {
                    flag++;

                }
            }

        }

        if (flag == 3 || remoteIP.equalsIgnoreCase("0:0:0:0:0:0:0:1") || remoteIP.equalsIgnoreCase("127.0.0.1")) {
            localClient = true;
        }

        return localClient;

    }

    private class childLink {

        String position;
        String query;
        String rifChild;
        String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getRifChild() {
            return rifChild;
        }

        public void setRifChild(String rifChild) {
            this.rifChild = rifChild;
        }

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

    public String findExtension() {
        String extension = "";
        Connection QPconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalQueenpro();
        String SQLphrase = "SELECT * FROM definitions WHERE ID='" + mySettings.getProjectName() + "'";

        el.log(myParams.getCKprojectName() + myParams.getCKcontextID() + "eventManager", "\n\nfindExtension()---->" + SQLphrase);
        PreparedStatement ps;
        try {
            ps = QPconny.prepareStatement(SQLphrase);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                extension = rs.getString("definition");
            }
            QPconny.close();

        } catch (SQLException ex) {
            Logger.getLogger(eventManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return extension;
    }

    public String coderPopulateHeader() {
        String HtmlCode = ("function populateHeader(event)\n"
                + "{\n"
                + "var portalParams= document.getElementById(\"portalParams\").value;"
                + "var xmlhttp;\n"
                + "if (window.XMLHttpRequest)\n"
                + "  {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "  xmlhttp=new XMLHttpRequest();\n"
                + "  }\n"
                + "else\n"
                + "  {// code for IE6, IE5\n"
                + "  xmlhttp=new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "  }\n"
                + "xmlhttp.onreadystatechange=function()\n"
                + "  {\n"
                + "  if (xmlhttp.readyState==4 && xmlhttp.status==200)\n"
                + "    {\n"
                + "    document.getElementById(event+\"Placeholder\").innerHTML=xmlhttp.responseText;\n"
                + "    }\n"
                + "  }\n"
                + "var params='\"params\":'+ portalParams;"
                + "var connectors='\"connectors\":[{\"door\":\"populateHeader\","
                + "     \"event\":\"'+event+'\"}]';"
                + "var utils='\"responseType\":\"text\"';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                //+ "alert(\"POPULATE HEADER :\"+gp);"
                + " xmlhttp.open(\"POST\",\"portal\",true);\n"
                + " xmlhttp.setRequestHeader(\"Content-type\",\"application/x-www-form-urlencoded\");\n"
                + " xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + "}\n"
                + ""
                + "");
        return HtmlCode;

    }
}

class feedback {

    String answerPhrase;
    int flagNewAccount;

    public void getPhraseAnswer(String type) {
        this.flagNewAccount = 1;
        String color = "red";
        String risposta = "<B><p style=\"color:red\"> ";
        if (type.equalsIgnoreCase("badCredentials")) {
            color = "red";
            risposta += ("CREDENZIALI ERRATE: reinserire le credenziali per il login:");
        } else if (type.equalsIgnoreCase("accessDenied")) {
            color = "red";
            risposta += ("QUESTO UTENTE NON E' ABILITATO: attendere l'abilitazione da parte di un Manager o tentare il Login come altro utente.");
        } else if (type.equalsIgnoreCase("emailSent")) {
            color = "green";
            risposta += ("E' stata inviata una mail sulla casella dell'utente per il ripristino della password.");
        } else if (type.equalsIgnoreCase("pincodeOnWeb")) {
            color = "orange";
            risposta += ("Il Login con Username + Pin Code è consentito solo da rete locale. Usare email + password.");
        } else if (type.equalsIgnoreCase("accountCreated")) {
            color = "green";
            risposta += ("Riceverai una mail all'indirizzo indicato per confermare l'account prima di poter fare il primo accesso.");
            this.flagNewAccount = 0;
        } else if (type.equalsIgnoreCase("confirmTimeout")) {
            color = "red";
            risposta += ("Timeout di conferma: eseguire nuovamente la procedura di registrazione.");
            this.flagNewAccount = 0;
        } else if (type.equalsIgnoreCase("confirmOK")) {
            color = "green";
            risposta += ("Conferma di registrazione eseguita correttamente.");
            this.flagNewAccount = 0;
        } else if (type.equalsIgnoreCase("confirmNotFound")) {
            color = "red";
            risposta += ("Riceverai una mail all'indirizzo indicato per confermare l'account prima di poter fare il primo accesso.");
            this.flagNewAccount = 0;
        } else if (type.equalsIgnoreCase("confirmNotConfirmed")) {
            color = "red";
            risposta += ("Errore in conferma nuovo uente.");
            this.flagNewAccount = 0;
        } else if (type.equalsIgnoreCase("PWconfirmTimeout")) {
            color = "red";
            risposta += ("Timeout di conferma: eseguire nuovamente la procedura di Modifica password e confermare entro 10 minuti.");
            this.flagNewAccount = 0;
        } else if (type.equalsIgnoreCase("PWconfirmOK")) {
            color = "green";
            risposta += ("Modifica password eseguita correttamente. Ora è possibile eseguire il Login con le nuove credenziali.");
            this.flagNewAccount = 0;
        } else if (type.equalsIgnoreCase("PWconfirmNotConfirmed")) {
            color = "red";
            risposta += ("Errore in Modifica password .");
            this.flagNewAccount = 0;
        } else {
            color = "black";
            risposta += ("Inserire le credenziali per il login: ");
//                String localIP = "";
//                String remoteIP = request.getRemoteIP();
//                try {
//                    InetAddress ip = InetAddress.getLocalHost();
//                    localIP = ip.getHostAddress();
//                } catch (UnknownHostException ex) {
//                    Logger.getLogger(eventManager.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                HtmlCode += ("<BR>Local IP: " + localIP);
//                HtmlCode += ("<BR>remote IP: " + remoteIP);
//                HtmlCode += ("<BR>IP is local: " + getLocalClient(request));
//                HtmlCode += ("<BR>" + dummyMessage);
        }

        risposta = "<B><p style=\"color:" + color + "\"> " + risposta;

        risposta += "</B></p><BR>";
        this.answerPhrase = risposta;

    }

}
