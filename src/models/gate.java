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
import REVOpager.EVOpagerDBconnection;
import java.io.UnsupportedEncodingException;
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
import smartCore.smartAction;

/**
 *
 * @author Franco
 */
public class gate {

    public String panJson = "";// contiene tutto il json da cui sono estratti gli argomenti
    public String door = "";
    public String event = "";
    public String type = "";
    public String params = "";
    public String form = "";
    public String formToLoad = "";
    public String formName = "";
    public String destination = "";
    public String formID = "";

    public String paramsToSend = "";

    public String formType = "";
    public String fatherArgs = "";
    public String loadType = "";
    public String fatherForm = "";
    public String fatherKEYvalue = "";
    public String fatherKEYtype = "";
    public String fatherCopyTag = "";
    public String curKEYvalue = "";
    public String curKEYtype = "";
    public String keyValue = "";
    public String keyType = "";
    public String ges_routineOnLoad = "";
    public String sendToCRUD = "";
    public String TBS = "";
    public String rifForm = "";
    public String copyTag = "";
    public String rifObj = "";
    public String routine = "";
    public String htmlCode = "";
    public String secondaryArgs = "";

    public String operation = "";
    public String primaryFieldName = "";
    public String primaryFieldType = "";
    public String primaryFieldValue = "";
    public String filterSequence = "";
    public String filterField = "";
    public String filterValue = "";
    public String cellType = "";
    public String newValue = "";
    public String cellName = "";
    public String fieldFiltered = "";
    public String fieldID = "";
    public String table = "";
    public String query = "";

    public String addingNewID = "";

    public String KeyField = "";
    public String currentKEY = "";
    public String picField = "";
    public String actionParams = "";

    public String triggerEvent = "";

    public String username;
    public String password;
    public String name;
    public String surname;
    public String email;
    public String IPaddress;
    public String token;

    public String routineOnChange;

    public EVOpagerParams myParams;

    public boolean controlNeeded = true;
    public String pageArgs;

    public int UserEnabled = 0;
    public String AfterOperationRoutineOnChange;
    public String AfterOperationRoutineOnNew;
    public String AfterOperationRoutineOnNew_NewValue;
    public String AfterOperationRoutineOnDelete;

    public String selectedRowID;
    public String cellID;
    public String thisFormFather;
    public String objName;

    public String curPage;

    public JSONObject routineResponse;
    public JSONArray actions;

    public String child_formID;
    public String child_copyTag;
    public String child_keyField;
    public String child_keyValue;

    public String bubble_formID;
    public String bubble_copyTag;
    public String bubble_keyValue;

    public gate() {
        routineResponse = new JSONObject();
        actions = new JSONArray();
    }

    public JSONObject insertAction_packResponse(EVOpagerParams myParams, Settings mySettings) {
        JSONObject outPayload = new JSONObject();
        outPayload.put("ACTION", "ROUTINE_RESPONSE");
        outPayload.put("DESTDIV", "");
        outPayload.put("CODE", "");
        outPayload.put("NEEDS", this.actions);

        JSONObject itemjObj = new JSONObject();
        itemjObj.put("ip", "0000");
        itemjObj.put("TYPE", "wsResponse");
        itemjObj.put("payload", outPayload);
        return itemjObj;
    }

    public JSONObject insertAction_packResponse(EVOpagerParams myParams, Settings mySettings, String Type, String Status) {
        // questo tipo di risposta serve ad essere parsato dal codice prima dell'invio al browser; es in caso di operation before CRUD
        JSONObject outPayload = new JSONObject();
        outPayload.put("ACTION", "ROUTINE_RESPONSE");
        outPayload.put("DESTDIV", "");
        outPayload.put("CODE", "");
        outPayload.put("TYPE", Type);
        outPayload.put("STATUS", Status);
        outPayload.put("NEEDS", this.actions);

        JSONObject itemjObj = new JSONObject();
        itemjObj.put("ip", "0000");
        itemjObj.put("TYPE", "wsResponse");
        itemjObj.put("payload", outPayload);
        return itemjObj;
    }

    public void insertAction_toast(EVOpagerParams myParams, Settings mySettings, String phrase) {
        JSONObject action = new JSONObject();
        action.put("action", "toast");
        action.put("phrase", phrase);

        this.actions.add(action);

    }

    public void insertAction_toast(EVOpagerParams myParams, Settings mySettings, String phrase, Boolean speak) {
        JSONObject action = new JSONObject();
        action.put("action", "toast");
        action.put("phrase", phrase);
        action.put("speak", speak);

        this.actions.add(action);

    }

    public void insertAction_splashReport(EVOpagerParams myParams, Settings mySettings, String phrase, Boolean speak) {
        JSONObject action = new JSONObject();
        action.put("action", "splash");
        action.put("phrase", phrase);
        action.put("speak", speak);

        this.actions.add(action);

    }

    public void insertAction_fileDownload(EVOpagerParams myParams, Settings mySettings, String content, String filename) {
        JSONObject action = new JSONObject();
        action.put("action", "Fdownload");
        action.put("content", content);
        action.put("filename", filename);

        this.actions.add(action);

    }

    public void insertAction_clickObject(EVOpagerParams myParams, Settings mySettings, String destTarget) {
//AMMIN18de3-X-selYear-FILTER  

        JSONObject action = new JSONObject();
        action.put("action", "clickObject");
        action.put("target", destTarget);// oggetto da cliccare
        action.put("htmlCode", "");// codice html
        this.actions.add(action);

    }

    public void insertAction_focusOnRow(EVOpagerParams myParams, Settings mySettings, String XformID, String XCopyTag, String XkeyValue) {

        String destTarget = XformID + "-" + XCopyTag + "-" + XkeyValue + "-SEL";
        System.out.println("FOCUS destTarget: " + destTarget);

        JSONObject action = new JSONObject();
        action.put("action", "focusOnRow");
        action.put("target", destTarget);// spazio destDiv da refreshare
        action.put("htmlCode", "");// codice html
        this.actions.add(action);

    }

    public void insertAction_synoptic(EVOpagerParams myParams, Settings mySettings, String XformID, String XCopyTag, String XrifObj, String XkeyValue, String htmlCode) {

        String destTarget = XformID + "-" + XCopyTag + "-" + XrifObj + "-" + XkeyValue + "-SYNOPTIC";
        System.out.println("SYNOPTIC destTarget: " + destTarget);
        JSONObject action = new JSONObject();
        action.put("action", "synoptic");
        action.put("target", destTarget);// spazio destDiv da refreshare
        action.put("htmlCode", htmlCode);// codice html
        this.actions.add(action);

    }

    public void insertAction_StopSynoptic(EVOpagerParams myParams, Settings mySettings, String XformID, String XCopyTag, String XrifObj, String XkeyValue) {

        String destTarget = XformID + "-" + XCopyTag + "-" + XrifObj + "-" + XkeyValue;
        System.out.println("STOPsynoptic destTarget: " + destTarget);
        JSONObject action = new JSONObject();
        action.put("action", "STOPsynoptic");
        action.put("target", destTarget);// spazio destDiv da refreshare
        action.put("htmlCode", "");// codice html
        this.actions.add(action);

    }

    public void insertAction_synopticLabelsUpdate(EVOpagerParams myParams, Settings mySettings, JSONArray jObj) {
        JSONObject action = new JSONObject();
        action.put("action", "synopticUpdate");
        action.put("updates", jObj);
        this.actions.add(action);

    }

    public void insertAction_repaintFormByName(EVOpagerParams myParams, Settings mySettings, String YformID, String XCopyTag, String XkeyValue) {
        System.out.println("\n ---SONO IN insertAction_repaintFormByName");
        // XkeyValue sarò la riga evidenziata
        String XhtmlCode = "";
        String XformID = "";
        XformID = formIDfromName(myParams, mySettings, YformID);
        JSONObject connector = loadConnector();
        connector.put("formID", XformID);
        connector.put("copyTag", XCopyTag);
        try {
            connector.put("keyValue", XkeyValue);
        } catch (Exception e) {
        }
//////        
////////        connector.put("formName", this.getFormName());
////////        connector.put("valueKEY", this.getPrimaryFieldName());
////////        connector.put("fatherForm", this.getFatherForm());
////////        connector.put("fatherCopyTag", this.getFatherCopyTag());
////////        connector.put("fatherKEYvalue", this.getFatherKEYvalue());
////////        connector.put("fatherKEYtype", this.getFatherKEYtype());
        connector.put("curPage", this.getCurPage());
//        connector.put("newValue", this.getFormID()); 
//        System.out.println("il connector che mando per insertAction_repaintFormDataOnly è: " + connector.toString());

        smartAction myAction = new smartAction(myParams, mySettings);
        String JhtmlCode = myAction.makeFormRepaintOrder(connector);
//        System.out.println("\n\n JhtmlCode da makeFormRefreshOrder :" + JhtmlCode);

        try {
            JhtmlCode = java.net.URLDecoder.decode(JhtmlCode, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
//        System.out.println(" decoded JhtmlCode da makeFormRefreshOrder :" + JhtmlCode);
        JSONParser jsonParser = new JSONParser();
        JSONObject myJC = new JSONObject();
        try {
            myJC = (JSONObject) jsonParser.parse(JhtmlCode);
            XhtmlCode = myJC.get("htmlCode").toString();
        } catch (ParseException ex) {

        }
        String destTarget = "FRAME-" + this.getFormName() + "-" + this.getCopyTag();
        if (!YformID.equalsIgnoreCase(this.getFormName())) {
            destTarget = "FRAME-" + YformID + "-" + this.getCopyTag();
        }

        System.out.println(" this.getFormName() :" + this.getFormName() + " YformID :" + YformID + " destTarget :" + destTarget);
        JSONObject action = new JSONObject();
        action.put("action", "repaintForm");
        action.put("target", destTarget);// spazio destDiv da refreshare
        action.put("htmlCode", XhtmlCode);// codice html

        actions.add(action);

    }

    public void insertAction_repaintFormRowByName(EVOpagerParams myParams, Settings mySettings, String XformID, String XCopyTag, String XkeyValue) {

        XformID = formIDfromName(myParams, mySettings, XformID);
        smartAction myAction = new smartAction(myParams, mySettings);
        JSONObject connector = loadConnector();
        connector.put("formID", XformID);
        connector.put("copyTag", XCopyTag);
        connector.put("keyValue", XkeyValue);
        connector.put("curPage", this.getCurPage());
        String htmlCode = "";
        System.out.println("\n\n\n OCCORRE REPAINTARE LA ROW " + XkeyValue + " NEL FORM :" + XformID);
        connector.replace("keyValue", XkeyValue);
        connector.replace("formID", XformID);

        System.out.println("il connector che mando per insertAction_repaintFormRow è: " + connector.toString());

        myAction = new smartAction(myParams, mySettings);
        String rowCode = myAction.makeRowRefreshOrder(connector);
        String destTarget = XformID + "-" + XCopyTag + "-" + XkeyValue + "-ROW";

        JSONObject action = new JSONObject();
        action.put("action", "repaintRow");
        action.put("target", destTarget);// spazio destDiv da refreshare
        action.put("htmlCode", rowCode);// codice html
        this.actions.add(action);

    }

    public void insertAction_repaintFormRow(EVOpagerParams myParams, Settings mySettings, String XformID, String XCopyTag, String XkeyValue) {
        smartAction myAction = new smartAction(myParams, mySettings);
        JSONObject connector = loadConnector();
        connector.put("formID", XformID);
        connector.put("copyTag", XCopyTag);
        connector.put("keyValue", XkeyValue);
        connector.put("curPage", this.getCurPage());
        String htmlCode = "";
        System.out.println("\n\n\n OCCORRE REPAINTARE LA ROW " + XkeyValue + " NEL FORM :" + XformID);
        connector.replace("keyValue", XkeyValue);
        connector.replace("formID", XformID);

        System.out.println("il connector che mando per insertAction_repaintFormRow è: " + connector.toString());

        myAction = new smartAction(myParams, mySettings);
        String rowCode = myAction.makeRowRefreshOrder(connector);
        String destTarget = XformID + "-" + XCopyTag + "-" + XkeyValue + "-ROW";

        JSONObject action = new JSONObject();
        action.put("action", "repaintRow");
        action.put("target", destTarget);// spazio destDiv da refreshare
        action.put("htmlCode", rowCode);// codice html
        this.actions.add(action);

    }

    public void insertAction_printReport(EVOpagerParams myParams, Settings mySettings, String newToken, String Xconnector) {
        // salvo sul database dei report il newToken accompagnato dal Xconnector
        System.out.println("\n\n\n  salvo sul database dei report il newToken accompagnato dal Xconnector :" + Xconnector);
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        PreparedStatement ps = null;
        ResultSet rs;
        String SQLphrase = "INSERT INTO archivio_timedTokens (info1, lifeInSeconds, info2, token "
                + ") VALUES ("
                + "'REPORT',200,?,'" + newToken + "' "
                + ")";
        System.out.println("SQLphrase:" + SQLphrase);
        try {
            ps = conny.prepareStatement(SQLphrase);
            ps.setString(1, Xconnector);
            int i = ps.executeUpdate();
            System.out.println("i:" + i);
        } catch (SQLException ex) {
            Logger.getLogger(gate.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(gate.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONObject action = new JSONObject();
        action.put("action", "printReport");
        action.put("token", newToken);
        this.actions.add(action);

    }

    public String formIDfromName(EVOpagerParams myParams, Settings mySettings, String formName) {
        String formID = formName;

        System.out.println("\n\n\n OCCORRE REPAINTARE IL FORM :" + formName);

        JSONObject connector = loadConnector();
        String formToSearch = formName;
        String tableFormsName = "gFE_forms_" + myParams.getCKprojectName();
// cerco l'ID del form da refreshare in base al nome
        String SQLphrase = "SELECT * FROM " + tableFormsName + " WHERE name='" + formToSearch + "'";
        System.out.println("SQLphrase  ---------->" + SQLphrase);
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                formID = rs.getString("ID");
            }
            conny.close();
        } catch (SQLException ex) {
            System.out.println("Errore :" + ex.toString());
        }
        System.out.println("L'ID del FORM :" + formID);
        return formID;
    }

    public void insertAction_repaintFormDataOnlyByName(EVOpagerParams myParams, Settings mySettings, String XformID, String XCopyTag, String XkeyValue) {
        String XhtmlCode = "";
        System.out.println("\n\n\n OCCORRE REPAINTARE IL FORM :" + XformID);

        JSONObject connector = loadConnector();

        XformID = formIDfromName(myParams, mySettings, XformID);

        System.out.println("\n\n\n OCCORRE REPAINTARE IL FORM :" + XformID);
        connector.put("formID", XformID);
        connector.put("copyTag", XCopyTag);
        connector.put("keyValue", XkeyValue);

//////        
////////        connector.put("formName", this.getFormName());
////////        connector.put("valueKEY", this.getPrimaryFieldName());
////////        connector.put("fatherForm", this.getFatherForm());
////////        connector.put("fatherCopyTag", this.getFatherCopyTag());
////////        connector.put("fatherKEYvalue", this.getFatherKEYvalue());
////////        connector.put("fatherKEYtype", this.getFatherKEYtype());
        connector.put("curPage", this.getCurPage());
//        connector.put("newValue", this.getFormID()); 
//        System.out.println("il connector che mando per insertAction_repaintFormDataOnlyByName è: " + connector.toString());

        smartAction myAction = new smartAction(myParams, mySettings);
        String JhtmlCode = myAction.makeFormRefreshOrder(connector);
//        System.out.println("\n\n JhtmlCode da makeFormRefreshOrder :" + JhtmlCode);

        try {
            JhtmlCode = java.net.URLDecoder.decode(JhtmlCode, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
//        System.out.println(" decoded JhtmlCode da makeFormRefreshOrder :" + JhtmlCode);
        JSONParser jsonParser = new JSONParser();
        JSONObject myJC = new JSONObject();
        try {
            myJC = (JSONObject) jsonParser.parse(JhtmlCode);
            XhtmlCode = myJC.get("htmlCode").toString();
        } catch (ParseException ex) {

        }
        String destTarget = XformID + "-" + this.getCopyTag() + "-ROWSDIV";
        JSONObject action = new JSONObject();
        action.put("action", "repaintForm");
        action.put("target", destTarget);// spazio destDiv da refreshare
        action.put("htmlCode", XhtmlCode);// codice html
        actions.add(action);

    }

    public void insertAction_repaintCalendarByName(EVOpagerParams myParams, Settings mySettings, String XformName, String XCopyTag, String XhtmlCode) {
        System.out.println("\n\n\n OCCORRE REPAINTARE IL FORM (name) :" + XformName);

        JSONObject connector = loadConnector();
        String XformID = formIDfromName(myParams, mySettings, XformName);
        System.out.println("\n\n\n OCCORRE REPAINTARE IL FORM :" + XformID);
        connector.put("formID", XformID);
        connector.put("copyTag", XCopyTag);
        connector.put("curPage", this.getCurPage());
        String destTarget = XformID + "-" + this.getCopyTag() + "-ROWSTABLE";
        JSONObject action = new JSONObject();
        action.put("action", "repaintForm");
        action.put("target", destTarget);// spazio destDiv da refreshare
        action.put("htmlCode", XhtmlCode);// codice html
        actions.add(action);

    }

    public void insertAction_repaintFormDataOnly(EVOpagerParams myParams, Settings mySettings, String XformID, String XCopyTag, String XkeyValue) {
        String XhtmlCode = "";
        System.out.println("\n\n\n OCCORRE REPAINTARE IL FORM :" + this.getFormID());

        JSONObject connector = loadConnector();
        connector.put("formID", XformID);
        connector.put("copyTag", XCopyTag);
        connector.put("keyValue", XkeyValue);

//////        
////////        connector.put("formName", this.getFormName());
////////        connector.put("valueKEY", this.getPrimaryFieldName());
////////        connector.put("fatherForm", this.getFatherForm());
////////        connector.put("fatherCopyTag", this.getFatherCopyTag());
////////        connector.put("fatherKEYvalue", this.getFatherKEYvalue());
////////        connector.put("fatherKEYtype", this.getFatherKEYtype());
        connector.put("curPage", this.getCurPage());
//        connector.put("newValue", this.getFormID()); 
//        System.out.println("il connector che mando per insertAction_repaintFormDataOnly è: " + connector.toString());

        smartAction myAction = new smartAction(myParams, mySettings);
        String JhtmlCode = myAction.makeFormRefreshOrder(connector);
//        System.out.println("\n\n JhtmlCode da makeFormRefreshOrder :" + JhtmlCode);

        try {
            JhtmlCode = java.net.URLDecoder.decode(JhtmlCode, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
//        System.out.println(" decoded JhtmlCode da makeFormRefreshOrder :" + JhtmlCode);
        JSONParser jsonParser = new JSONParser();
        JSONObject myJC = new JSONObject();
        try {
            myJC = (JSONObject) jsonParser.parse(JhtmlCode);
            XhtmlCode = myJC.get("htmlCode").toString();
        } catch (ParseException ex) {

        }
        String destTarget = this.getFormID() + "-" + this.getCopyTag() + "-ROWSDIV";
        JSONObject action = new JSONObject();
        action.put("action", "repaintForm");
        action.put("target", destTarget);// spazio destDiv da refreshare
        action.put("htmlCode", XhtmlCode);// codice html
        actions.add(action);

    }
//    public JSONArray STC;
//    public JSONArray tbs;

    public String getBubble_formID() {
        return bubble_formID;
    }

    public void setBubble_formID(String bubble_formID) {
        this.bubble_formID = bubble_formID;
    }

    public String getBubble_copyTag() {
        return bubble_copyTag;
    }

    public void setBubble_copyTag(String bubble_copyTag) {
        this.bubble_copyTag = bubble_copyTag;
    }

    public String getBubble_keyValue() {
        return bubble_keyValue;
    }

    public void setBubble_keyValue(String bubble_keyValue) {
        this.bubble_keyValue = bubble_keyValue;
    }

    public String getChild_formID() {
        return child_formID;
    }

    public void setChild_formID(String child_formID) {
        this.child_formID = child_formID;
    }

    public String getChild_copyTag() {
        return child_copyTag;
    }

    public void setChild_copyTag(String child_copyTag) {
        this.child_copyTag = child_copyTag;
    }

    public String getChild_keyField() {
        return child_keyField;
    }

    public void setChild_keyField(String child_keyField) {
        this.child_keyField = child_keyField;
    }

    public String getChild_keyValue() {
        return child_keyValue;
    }

    public void setChild_keyValue(String child_keyValue) {
        this.child_keyValue = child_keyValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurPage() {
        return curPage;
    }

    public void setCurPage(String curPage) {
        this.curPage = curPage;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSelectedRowID() {
        return selectedRowID;
    }

    public void setSelectedRowID(String selectedRowID) {
        this.selectedRowID = selectedRowID;
    }

    public String getCellID() {
        return cellID;
    }

    public void setCellID(String cellID) {
        this.cellID = cellID;
    }

    public String getThisFormFather() {
        return thisFormFather;
    }

    public void setThisFormFather(String thisFormFather) {
        this.thisFormFather = thisFormFather;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

//    public JSONArray getSTC() {
//        return STC;
//    }
//
//    public void setSTC(JSONArray STC) {
//        this.STC = STC;
//    }
//
//    public JSONArray getTbs() {
//        return tbs;
//    }
//
//    public void setTbs(JSONArray tbs) {
//        this.tbs = tbs;
//    }
    public String getAfterOperationRoutineOnNew_NewValue() {
        return AfterOperationRoutineOnNew_NewValue;
    }

    public void setAfterOperationRoutineOnNew_NewValue(String AfterOperationRoutineOnNew_NewValue) {
        this.AfterOperationRoutineOnNew_NewValue = AfterOperationRoutineOnNew_NewValue;
    }

    public String getAfterOperationRoutineOnChange() {
        return AfterOperationRoutineOnChange;
    }

    public void setAfterOperationRoutineOnChange(String AfterOperationRoutineOnChange) {
        this.AfterOperationRoutineOnChange = AfterOperationRoutineOnChange;
    }

    public String getAfterOperationRoutineOnNew() {
        return AfterOperationRoutineOnNew;
    }

    public void setAfterOperationRoutineOnNew(String AfterOperationRoutineOnNew) {
        this.AfterOperationRoutineOnNew = AfterOperationRoutineOnNew;
    }

    public String getAfterOperationRoutineOnDelete() {
        return AfterOperationRoutineOnDelete;
    }

    public void setAfterOperationRoutineOnDelete(String AfterOperationRoutineOnDelete) {
        this.AfterOperationRoutineOnDelete = AfterOperationRoutineOnDelete;
    }

    public String getActionParams() {
        return actionParams;
    }

    public void setActionParams(String actionParams) {
        this.actionParams = actionParams;
    }

    public String getParamsToSend() {
        return paramsToSend;
    }

    public void setParamsToSend(String paramsToSend) {
        this.paramsToSend = paramsToSend;
    }

    public int getUserEnabled() {
        return UserEnabled;
    }

    public void setUserEnabled(int UserEnabled) {
        this.UserEnabled = UserEnabled;
    }

    public String getRifForm() {
        return rifForm;
    }

    public void setRifForm(String rifForm) {
        this.rifForm = rifForm;
    }

    public String getGes_routineOnLoad() {
        return ges_routineOnLoad;
    }

    public void setGes_routineOnLoad(String ges_routineOnLoad) {
        this.ges_routineOnLoad = ges_routineOnLoad;
    }

    public String getPanJson() {
        return panJson;
    }

    public void setPanJson(String panJson) {
        this.panJson = panJson;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getPageArgs() {
        return pageArgs;
    }

    public void setPageArgs(String pageArgs) {
        this.pageArgs = pageArgs;
    }

    public String getSecondaryArgs() {
        return secondaryArgs;
    }

    public void setSecondaryArgs(String secondaryArgs) {
        this.secondaryArgs = secondaryArgs;
    }

    public String getHtmlCode() {
        return htmlCode;
    }

    public void setHtmlCode(String htmlCode) {
        this.htmlCode = htmlCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIPaddress() {
        return IPaddress;
    }

    public void setIPaddress(String IPaddress) {
        this.IPaddress = IPaddress;
    }

    public String getRoutine() {
        return routine;
    }

    public void setRoutine(String routine) {
        this.routine = routine;
    }

    public String getRoutineOnChange() {
        return routineOnChange;
    }

    public void setRoutineOnChange(String routineOnChange) {
        this.routineOnChange = routineOnChange;
    }

    public String getRifObj() {
        return rifObj;
    }

    public void setRifObj(String rifObj) {
        this.rifObj = rifObj;
    }

    public EVOpagerParams getMyParams() {
        return myParams;
    }

    public void setMyParams(EVOpagerParams myParams) {
        this.myParams = myParams;
    }

    public String getDoor() {
        return door;
    }

    public void setDoor(String door) {
        this.door = door;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getFormToLoad() {
        return formToLoad;
    }

    public void setFormToLoad(String formToLoad) {
        this.formToLoad = formToLoad;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public String getFatherArgs() {
        return fatherArgs;
    }

    public void setFatherArgs(String fatherArgs) {
        this.fatherArgs = fatherArgs;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public String getFatherForm() {
        return fatherForm;
    }

    public void setFatherForm(String fatherForm) {
        this.fatherForm = fatherForm;
    }

    public String getFatherKEYvalue() {
        return fatherKEYvalue;
    }

    public void setFatherKEYvalue(String fatherKEYvalue) {
        this.fatherKEYvalue = fatherKEYvalue;
    }

    public String getFatherKEYtype() {
        return fatherKEYtype;
    }

    public void setFatherKEYtype(String fatherKEYtype) {
        this.fatherKEYtype = fatherKEYtype;
    }

    public String getFatherCopyTag() {
        return fatherCopyTag;
    }

    public void setFatherCopyTag(String fatherCopyTag) {
        this.fatherCopyTag = fatherCopyTag;
    }

    public String getCurKEYvalue() {
        return curKEYvalue;
    }

    public void setCurKEYvalue(String curKEYvalue) {
        this.curKEYvalue = curKEYvalue;
    }

    public String getCurKEYtype() {
        return curKEYtype;
    }

    public void setCurKEYtype(String curKEYtype) {
        this.curKEYtype = curKEYtype;
    }

    public String getSendToCRUD() {
        return sendToCRUD;
    }

    public void setSendToCRUD(String sendToCRUD) {
        this.sendToCRUD = sendToCRUD;
    }

    public String getTBS() {
        return TBS;
    }

    public void setTBS(String TBS) {
        this.TBS = TBS;
    }

    public String getCopyTag() {
        return copyTag;
    }

    public void setCopyTag(String copyTag) {
        this.copyTag = copyTag;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPrimaryFieldName() {
        return primaryFieldName;
    }

    public void setPrimaryFieldName(String primaryFieldName) {
        this.primaryFieldName = primaryFieldName;
    }

    public String getPrimaryFieldType() {
        return primaryFieldType;
    }

    public void setPrimaryFieldType(String primaryFieldType) {
        this.primaryFieldType = primaryFieldType;
    }

    public String getPrimaryFieldValue() {
        return primaryFieldValue;
    }

    public void setPrimaryFieldValue(String primaryFieldValue) {
        this.primaryFieldValue = primaryFieldValue;
    }

    public String getFilterSequence() {
        return filterSequence;
    }

    public void setFilterSequence(String filterSequence) {
        this.filterSequence = filterSequence;
    }

    public String getFilterField() {
        return filterField;
    }

    public void setFilterField(String filterField) {
        this.filterField = filterField;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public String getFieldFiltered() {
        return fieldFiltered;
    }

    public void setFieldFiltered(String fieldFiltered) {
        this.fieldFiltered = fieldFiltered;
    }

    public String getFieldID() {
        return fieldID;
    }

    public void setFieldID(String fieldID) {
        this.fieldID = fieldID;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getKeyField() {
        return KeyField;
    }

    public void setKeyField(String KeyField) {
        this.KeyField = KeyField;
    }

    public String getCurrentKEY() {
        return currentKEY;
    }

    public void setCurrentKEY(String currentKEY) {
        this.currentKEY = currentKEY;
    }

    public String getPicField() {
        return picField;
    }

    public void setPicField(String picField) {
        this.picField = picField;
    }

    public boolean isControlNeeded() {
        return controlNeeded;
    }

    public void setControlNeeded(boolean controlNeeded) {
        this.controlNeeded = controlNeeded;
    }

    public String getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(String triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    public void connector2gate(JSONObject connector) {
        try {
            this.setDoor(connector.get("door").toString());
        } catch (Exception e) {
        }
        try {
            this.setFormID(connector.get("formID").toString());
        } catch (Exception e) {
        }
        try {
            this.setCopyTag(connector.get("copyTag").toString());
        } catch (Exception e) {
        }
        try {
            this.setEvent(connector.get("event").toString());
        } catch (Exception e) {
        }
        try {
            this.setRifObj(connector.get("rifObj").toString());
        } catch (Exception e) {
        }
        try {
            this.setKeyValue(connector.get("keyValue").toString());
        } catch (Exception e) {
        }
        try {
            this.setSelectedRowID(connector.get("selectedRowID").toString());
        } catch (Exception e) {
        }
        try {
            this.setNewValue(connector.get("newValue").toString());
        } catch (Exception e) {
        }
        try {
            this.setCurPage(connector.get("curPage").toString());
        } catch (Exception e) {
        }

        try {
            this.setPrimaryFieldValue(connector.get("primaryFieldValue").toString());
        } catch (Exception e) {
        }
        try {
            this.setFatherForm(connector.get("fatherForm").toString());
        } catch (Exception e) {
        }
        try {
            this.setCellName(connector.get("cellName").toString());
        } catch (Exception e) {
        }
        try {
            this.setRoutineOnChange(connector.get("routineOnChange").toString());
        } catch (Exception e) {
        }
        try {
            this.setFatherKEYtype(connector.get("fatherKEYtype").toString());
        } catch (Exception e) {
        }
        try {
            this.setFormName(connector.get("formName").toString());
        } catch (Exception e) {
        }
        try {
            this.setFatherArgs(connector.get("fatherArgs").toString());
        } catch (Exception e) {
        }
        try {
            this.setFatherKEYvalue(connector.get("fatherKEYvalue").toString());
        } catch (Exception e) {
        }
        try {
            this.setSendToCRUD(connector.get("STC").toString());
        } catch (Exception e) {
        }
        try {
            this.setTBS(connector.get("tbs").toString());
        } catch (Exception e) {
        }
        try {
            this.setNewValue(connector.get("newValue").toString());
        } catch (Exception e) {
        }
        try {
            this.setFatherCopyTag(connector.get("fatherCopyTag").toString());
        } catch (Exception e) {
        }
        try {
            this.setFilterField(connector.get("filterField").toString());
        } catch (Exception e) {
        }
        try {
            this.setPrimaryFieldType(connector.get("primaryFieldType").toString());
        } catch (Exception e) {
        }
        try {
            this.setCellType(connector.get("cellType").toString());
        } catch (Exception e) {
        }
        try {
            this.setFilterValue(connector.get("filterValue").toString());
        } catch (Exception e) {
        }
        try {
            this.setFilterSequence(connector.get("filterSequence").toString());
        } catch (Exception e) {
        }
        try {
            this.setOperation(connector.get("operation").toString());
        } catch (Exception e) {
        }
        try {
            this.setPrimaryFieldName(connector.get("primaryFieldName").toString());
        } catch (Exception e) {
        }
        try {
            this.setCellID(connector.get("cellID").toString());
        } catch (Exception e) {
        }
        try {
            this.setThisFormFather(connector.get("thisFormFather").toString());
        } catch (Exception e) {
        }
        try {
            this.setObjName(connector.get("objName").toString());
        } catch (Exception e) {
        }
        try {
            this.setFormToLoad(connector.get("formToLoad").toString());
        } catch (Exception e) {
        }
        try {
            this.setRoutine(connector.get("routine").toString());
        } catch (Exception e) {
        }
        try {
            this.setParamsToSend(connector.get("paramsToSend").toString());
        } catch (Exception e) {
        }

        try {
            this.setBubble_formID(connector.get("bubble_formID").toString());
        } catch (Exception e) {
        }
        try {
            this.setBubble_copyTag(connector.get("bubble_copyTag").toString());
        } catch (Exception e) {
        }
        try {
            this.setBubble_keyValue(connector.get("bubble_keyValue").toString());
        } catch (Exception e) {
        }

    }

//
//    public void connector2gate(JSONObject jsonObject) {
//        try {
//            this.setDoor(jsonObject.get("door").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setEvent(jsonObject.get("event").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setType(jsonObject.get("type").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setTable(jsonObject.get("table").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setKeyField(jsonObject.get("keyfield").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//
//        try {
//            this.setCurrentKEY(jsonObject.get("keyValue").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//
//        try {
//            this.setKeyValue(jsonObject.get("keyValue").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setKeyType(jsonObject.get("keyType").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setPicField(jsonObject.get("picfield").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setRifObj(jsonObject.get("rifObj").toString());
//
//        } catch (Exception e) {
//        }
//        try {
//            this.setFormToLoad(jsonObject.get("formToLoad").toString());
//            //System.out.println("formToLoad=" + myConnector.getFormToLoad());
//        } catch (Exception e) {
//            //System.out.println("errore formToLoad=" + e.toString());
//        } finally {
//        }
//        try {
//            this.setParamsToSend(jsonObject.get("paramsToSend").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setSecondaryArgs(jsonObject.get("secondaryArgs").toString());
//            secondaryArgs = java.net.URLDecoder.decode(secondaryArgs, "UTF-8");
//            System.out.println("secondaryArgs=" + secondaryArgs);
//        } catch (Exception e) {
//
//        }
//        if (this.getFormToLoad() == null
//                || this.getFormToLoad().toString() == ""
//                || this.getFormToLoad().toString().length() < 1
//                || this.getFormToLoad().toString().equalsIgnoreCase("null")
//                || this.getFormToLoad().toString().equalsIgnoreCase("undefined")) {
//            // System.out.println("Provo a caricare formToLaod...");
//            try {
//                this.setFormToLoad(jsonObject.get("formToLaod").toString());
//                // System.out.println("formToLaod="+ myConnector.getFormToLoad());
//            } catch (Exception e) {
//                //  System.out.println("errore2="+ e.toString());
//            } finally {
//            }
//        }
//        try {
//            this.setDestination(jsonObject.get("destination").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setFormID(jsonObject.get("formID").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            // System.out.println("CERCO formName negli args");
//            this.setFormName(jsonObject.get("formName").toString());
//            // System.out.println("formName:" + myConnector.getFormName());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setFatherArgs(jsonObject.get("fatherArgs").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setLoadType(jsonObject.get("loadType").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setFatherForm(jsonObject.get("fatherForm").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setFatherKEYvalue(jsonObject.get("fatherKEYvalue").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setFatherKEYtype(jsonObject.get("fatherKEYtype").toString());
//        } catch (Exception e) {
//        } finally {
//        }
//        try {
//            this.setFatherCopyTag(jsonObject.get("fatherCopyTag").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setCurKEYvalue(jsonObject.get("curKEYvalue").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setCurKEYtype(jsonObject.get("curKEYtype").toString());
//        } catch (Exception e) {
//        }
//
//        try {
//            this.setRoutine(jsonObject.get("routine").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setTBS(java.net.URLDecoder.decode(jsonObject.get("TBS").toString(), "UTF-8"));
//        } catch (Exception e) {
//        }
//        try {
//            this.setSendToCRUD(java.net.URLDecoder.decode(jsonObject.get("sendToCRUD").toString(), "UTF-8"));
//        } catch (Exception e) {
//        }
//        try {
//            this.setHtmlCode(java.net.URLDecoder.decode(jsonObject.get("htmlCode").toString(), "UTF-8"));
//        } catch (Exception e) {
//        }
//        try {
//            this.setRifForm(jsonObject.get("rifForm").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setCopyTag(jsonObject.get("copyTag").toString());
//            if (this.getCopyTag() == null || this.getCopyTag().length() < 1) {
//                this.setCopyTag("X");
//            }
//        } catch (Exception e) {
//        }
//
//        try {
//            this.setUsername(java.net.URLDecoder.decode(jsonObject.get("username").toString(), "UTF-8"));
//        } catch (Exception e) {
//        }
//        try {
//            this.setPassword(java.net.URLDecoder.decode(jsonObject.get("pass").toString(), "UTF-8"));
//        } catch (Exception e) {
//        }
//        try {
//            this.setEmail(java.net.URLDecoder.decode(jsonObject.get("email").toString(), "UTF-8"));
//        } catch (Exception e) {
//        }
//        try {
//            this.setToken(java.net.URLDecoder.decode(jsonObject.get("token").toString(), "UTF-8"));
//        } catch (Exception e) {
//        }
//
//        //----CRUD----
//        try {
//            this.setOperation(jsonObject.get("operation").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setPrimaryFieldName(jsonObject.get("primaryFieldName").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setPrimaryFieldType(jsonObject.get("primaryFieldType").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setPrimaryFieldName(jsonObject.get("primaryFieldName").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setPrimaryFieldValue(jsonObject.get("primaryFieldValue").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setFilterSequence(jsonObject.get("filterSequence").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setFilterField(jsonObject.get("filterField").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setFilterValue(jsonObject.get("filterValue").toString());
//        } catch (Exception e) {
//        }
//
//        try {
//            this.setCellType(jsonObject.get("cellType").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setNewValue(jsonObject.get("newValue").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setCellName(jsonObject.get("cellName").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setCellID(jsonObject.get("cellID").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setFieldFiltered(jsonObject.get("fieldFiltered").toString());
//            //       System.out.println("gaiaGate CRUD riceve fieldFiltered:" + myConnector.getFieldFiltered());
//        } catch (Exception e) {
//        }
//        try {
//            this.setSendToCRUD(jsonObject.get("sendToCRUD").toString());
//            //       System.out.println("\n\n\n^^^^^^^^^^^^^^^^^^^^^^^^\ngaiaGate CRUD riceve sendToCRUD:" + myConnector.getSendToCRUD());
//        } catch (Exception e) {
//        }
//        try {
//            this.setFieldID(jsonObject.get("fieldID").toString());
//        } catch (Exception e) {
//        }
//        try {
//            this.setRoutineOnChange(jsonObject.get("routineOnChange").toString());
//            //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
//        } catch (Exception e) {
//        }
//        try {
//            this.setGes_routineOnLoad(jsonObject.get("ges_routineOnLoad").toString());
//            //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
//        } catch (Exception e) {
//        }
//        try {
//            this.setActionParams(jsonObject.get("actionParams").toString());
//            //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
//        } catch (Exception e) {
//        }
//
//        try {
//            this.setTriggerEvent(jsonObject.get("triggerEvent").toString());
//            //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
//        } catch (Exception e) {
//        }
//
//    }
    public ArrayList<gate> chargeConnectors(String connectors) {
        ArrayList<gate> myConnectors = new ArrayList<gate>();
        JSONParser jsonParser = new JSONParser();
        try {
            if (connectors != null && connectors.length() > 0) {
                JSONParser parser = new JSONParser();
                Object obj;

                obj = parser.parse(connectors);
                JSONArray array = (JSONArray) obj;

                for (Object riga : array) {
                    gate myConnector = new gate();
                    myConnector.setPanJson(riga.toString());

                    JSONObject jsonObject = (JSONObject) jsonParser.parse(riga.toString());
                    //System.out.println("jsonObject=" + jsonObject);

                    try {
                        myConnector.setDoor(jsonObject.get("door").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setEvent(jsonObject.get("event").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setType(jsonObject.get("type").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setTable(jsonObject.get("table").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setKeyField(java.net.URLDecoder.decode(jsonObject.get("keyfield").toString(), "UTF-8"));
                    } catch (Exception e) {
                    } finally {
                    }

                    try {
                        myConnector.setCurrentKEY(jsonObject.get("keyValue").toString());
                    } catch (Exception e) {
                    } finally {
                    }

                    try {
                        myConnector.setKeyValue(jsonObject.get("keyValue").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setKeyType(jsonObject.get("keyType").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setPicField(jsonObject.get("picfield").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setRifObj(jsonObject.get("rifObj").toString());

                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setFormToLoad(jsonObject.get("formToLoad").toString());
                        //System.out.println("formToLoad=" + myConnector.getFormToLoad());
                    } catch (Exception e) {
                        //System.out.println("errore formToLoad=" + e.toString());
                    } finally {
                    }
                    try {
                        myConnector.setParamsToSend(jsonObject.get("paramsToSend").toString());
                        System.out.println("paramsToSend=" + myConnector.getParamsToSend());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setSecondaryArgs(jsonObject.get("secondaryArgs").toString());
                        secondaryArgs = java.net.URLDecoder.decode(secondaryArgs, "UTF-8");
//                    System.out.println("secondaryArgs=" + secondaryArgs);
                    } catch (Exception e) {

                    }
                    if (myConnector.getFormToLoad() == null
                            || myConnector.getFormToLoad().toString() == ""
                            || myConnector.getFormToLoad().toString().length() < 1
                            || myConnector.getFormToLoad().toString().equalsIgnoreCase("null")
                            || myConnector.getFormToLoad().toString().equalsIgnoreCase("undefined")) {
                        // System.out.println("Provo a caricare formToLaod...");
                        try {
                            myConnector.setFormToLoad(jsonObject.get("formToLaod").toString());
                            // System.out.println("formToLaod="+ myConnector.getFormToLoad());
                        } catch (Exception e) {
                            //  System.out.println("errore2="+ e.toString());
                        } finally {
                        }
                    }
                    try {
                        myConnector.setDestination(jsonObject.get("destination").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setFormID(jsonObject.get("formID").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        // System.out.println("CERCO formName negli args");
                        myConnector.setFormName(jsonObject.get("formName").toString());
                        // System.out.println("formName:" + myConnector.getFormName());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setFatherArgs(jsonObject.get("fatherArgs").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setLoadType(jsonObject.get("loadType").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setFatherForm(jsonObject.get("fatherForm").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setFatherKEYvalue(jsonObject.get("fatherKEYvalue").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setFatherKEYtype(jsonObject.get("fatherKEYtype").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        myConnector.setFatherCopyTag(jsonObject.get("fatherCopyTag").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setCurKEYvalue(jsonObject.get("curKEYvalue").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setCurKEYtype(jsonObject.get("curKEYtype").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setRoutine(jsonObject.get("routine").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setTBS(java.net.URLDecoder.decode(jsonObject.get("TBS").toString(), "UTF-8"));

                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setSendToCRUD(java.net.URLDecoder.decode(jsonObject.get("sendToCRUD").toString(), "UTF-8"));

                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setHtmlCode(java.net.URLDecoder.decode(jsonObject.get("htmlCode").toString(), "UTF-8"));

                    } catch (Exception e) {
                    }
//                    try {
//                        myConnector.setSecondaryArgs(java.net.URLDecoder.decode(jsonObject.get("secondaryArgs").toString(), "UTF-8"));
//
//                    } catch (Exception e) {
//                    }
                    try {
                        myConnector.setRifForm(jsonObject.get("rifForm").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setCopyTag(jsonObject.get("copyTag").toString());
                    } catch (Exception e) {
                    }
                    if (myConnector.getCopyTag() == null || myConnector.getCopyTag().length() < 1) {
                        myConnector.setCopyTag("X");
                    }
                    try {
                        myConnector.setUsername(java.net.URLDecoder.decode(jsonObject.get("username").toString(), "UTF-8"));
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setPassword(java.net.URLDecoder.decode(jsonObject.get("pass").toString(), "UTF-8"));
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setName(java.net.URLDecoder.decode(jsonObject.get("name").toString(), "UTF-8"));
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setSurname(java.net.URLDecoder.decode(jsonObject.get("surname").toString(), "UTF-8"));
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setEmail(java.net.URLDecoder.decode(jsonObject.get("email").toString(), "UTF-8"));
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setToken(java.net.URLDecoder.decode(jsonObject.get("token").toString(), "UTF-8"));
                    } catch (Exception e) {
                    }

                    //----CRUD----
                    try {
                        myConnector.setOperation(jsonObject.get("operation").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setPrimaryFieldName(jsonObject.get("primaryFieldName").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setPrimaryFieldType(jsonObject.get("primaryFieldType").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setPrimaryFieldName(jsonObject.get("primaryFieldName").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setPrimaryFieldValue(jsonObject.get("primaryFieldValue").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setFilterSequence(jsonObject.get("filterSequence").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setFilterField(jsonObject.get("filterField").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setFilterValue(jsonObject.get("filterValue").toString());
                    } catch (Exception e) {
                    }

                    try {
                        myConnector.setCellType(jsonObject.get("cellType").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setNewValue(jsonObject.get("newValue").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setCellName(jsonObject.get("cellName").toString());
                    } catch (Exception e) {
                    }

                    try {
                        myConnector.setFieldFiltered(jsonObject.get("fieldFiltered").toString());
                        //       System.out.println("gaiaGate CRUD riceve fieldFiltered:" + myConnector.getFieldFiltered());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setSendToCRUD(jsonObject.get("sendToCRUD").toString());
                        //       System.out.println("\n\n\n^^^^^^^^^^^^^^^^^^^^^^^^\ngaiaGate CRUD riceve sendToCRUD:" + myConnector.getSendToCRUD());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setFieldID(jsonObject.get("fieldID").toString());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setRoutineOnChange(jsonObject.get("routineOnChange").toString());
                        //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setGes_routineOnLoad(jsonObject.get("ges_routineOnLoad").toString());
                        //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
                    } catch (Exception e) {
                    }
                    try {
                        myConnector.setActionParams(jsonObject.get("actionParams").toString());
                        //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
                    } catch (Exception e) {
                        myConnector.setActionParams("");
                    }

                    try {
                        myConnector.setTriggerEvent(jsonObject.get("triggerEvent").toString());
                        //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
                    } catch (Exception e) {
                        myConnector.setTriggerEvent("");
                    }

                    myConnectors.add(myConnector);

                    //******************************************              
                    try {
                        this.setDoor(jsonObject.get("door").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setEvent(jsonObject.get("event").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setType(jsonObject.get("type").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setTable(jsonObject.get("table").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setKeyField(jsonObject.get("keyfield").toString());
                    } catch (Exception e) {
                    } finally {
                    }

                    try {
                        this.setCurrentKEY(jsonObject.get("keyValue").toString());
                    } catch (Exception e) {
                    } finally {
                    }

                    try {
                        this.setKeyValue(jsonObject.get("keyValue").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setKeyType(jsonObject.get("keyType").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setPicField(jsonObject.get("picfield").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setRifObj(jsonObject.get("rifObj").toString());

                    } catch (Exception e) {
                    }
                    try {
                        this.setFormToLoad(jsonObject.get("formToLoad").toString());
                        //System.out.println("formToLoad=" + myConnector.getFormToLoad());
                    } catch (Exception e) {
                        //System.out.println("errore formToLoad=" + e.toString());
                    } finally {
                    }
                    try {
                        this.setParamsToSend(jsonObject.get("paramsToSend").toString());
                        System.out.println("paramsToSend=" + myConnector.getParamsToSend());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setSecondaryArgs(jsonObject.get("secondaryArgs").toString());
                        secondaryArgs = java.net.URLDecoder.decode(secondaryArgs, "UTF-8");
//                    System.out.println("secondaryArgs=" + secondaryArgs);
                    } catch (Exception e) {

                    }
                    if (myConnector.getFormToLoad() == null
                            || myConnector.getFormToLoad().toString() == ""
                            || myConnector.getFormToLoad().toString().length() < 1
                            || myConnector.getFormToLoad().toString().equalsIgnoreCase("null")
                            || myConnector.getFormToLoad().toString().equalsIgnoreCase("undefined")) {
                        // System.out.println("Provo a caricare formToLaod...");
                        try {
                            this.setFormToLoad(jsonObject.get("formToLaod").toString());
                            // System.out.println("formToLaod="+ myConnector.getFormToLoad());
                        } catch (Exception e) {
                            //  System.out.println("errore2="+ e.toString());
                        } finally {
                        }
                    }
                    try {
                        this.setDestination(jsonObject.get("destination").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setFormID(jsonObject.get("formID").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        // System.out.println("CERCO formName negli args");
                        this.setFormName(jsonObject.get("formName").toString());
                        // System.out.println("formName:" + myConnector.getFormName());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setFatherArgs(jsonObject.get("fatherArgs").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setLoadType(jsonObject.get("loadType").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setFatherForm(jsonObject.get("fatherForm").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setFatherKEYvalue(jsonObject.get("fatherKEYvalue").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setFatherKEYtype(jsonObject.get("fatherKEYtype").toString());
                    } catch (Exception e) {
                    } finally {
                    }
                    try {
                        this.setFatherCopyTag(jsonObject.get("fatherCopyTag").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setCurKEYvalue(jsonObject.get("curKEYvalue").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setCurKEYtype(jsonObject.get("curKEYtype").toString());
                    } catch (Exception e) {
                    }

                    try {
                        this.setRoutine(jsonObject.get("routine").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setTBS(java.net.URLDecoder.decode(jsonObject.get("TBS").toString(), "UTF-8"));

                    } catch (Exception e) {
                    }
                    try {
                        this.setSendToCRUD(java.net.URLDecoder.decode(jsonObject.get("sendToCRUD").toString(), "UTF-8"));

                    } catch (Exception e) {
                    }
                    try {
                        this.setHtmlCode(java.net.URLDecoder.decode(jsonObject.get("htmlCode").toString(), "UTF-8"));

                    } catch (Exception e) {
                    }
                    try {
                        this.setSecondaryArgs(java.net.URLDecoder.decode(jsonObject.get("secondaryArgs").toString(), "UTF-8"));

                    } catch (Exception e) {
                    }
                    try {
                        this.setRifForm(jsonObject.get("rifForm").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setCopyTag(jsonObject.get("copyTag").toString());
                    } catch (Exception e) {
                    }
                    if (myConnector.getCopyTag() == null || myConnector.getCopyTag().length() < 1) {
                        this.setCopyTag("X");
                    }
                    try {
                        this.setUsername(java.net.URLDecoder.decode(jsonObject.get("username").toString(), "UTF-8"));
                    } catch (Exception e) {
                    }
                    try {
                        this.setPassword(java.net.URLDecoder.decode(jsonObject.get("pass").toString(), "UTF-8"));
                    } catch (Exception e) {
                    }
                    try {
                        this.setEmail(java.net.URLDecoder.decode(jsonObject.get("email").toString(), "UTF-8"));
                    } catch (Exception e) {
                    }
                    try {
                        this.setToken(java.net.URLDecoder.decode(jsonObject.get("token").toString(), "UTF-8"));
                    } catch (Exception e) {
                    }

                    //----CRUD----
                    try {
                        this.setOperation(jsonObject.get("operation").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setPrimaryFieldName(jsonObject.get("primaryFieldName").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setPrimaryFieldType(jsonObject.get("primaryFieldType").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setPrimaryFieldName(jsonObject.get("primaryFieldName").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setPrimaryFieldValue(jsonObject.get("primaryFieldValue").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setFilterSequence(jsonObject.get("filterSequence").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setFilterField(jsonObject.get("filterField").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setFilterValue(jsonObject.get("filterValue").toString());
                    } catch (Exception e) {
                    }

                    try {
                        this.setCellType(jsonObject.get("cellType").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setNewValue(jsonObject.get("newValue").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setCellName(jsonObject.get("cellName").toString());
                    } catch (Exception e) {
                    }

                    try {
                        this.setFieldFiltered(jsonObject.get("fieldFiltered").toString());
                        //       System.out.println("gaiaGate CRUD riceve fieldFiltered:" + myConnector.getFieldFiltered());
                    } catch (Exception e) {
                    }
                    try {
                        this.setSendToCRUD(jsonObject.get("sendToCRUD").toString());
                        //       System.out.println("\n\n\n^^^^^^^^^^^^^^^^^^^^^^^^\ngaiaGate CRUD riceve sendToCRUD:" + myConnector.getSendToCRUD());
                    } catch (Exception e) {
                    }
                    try {
                        this.setFieldID(jsonObject.get("fieldID").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setRoutineOnChange(jsonObject.get("routineOnChange").toString());
                        //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
                    } catch (Exception e) {
                    }
                    try {
                        this.setGes_routineOnLoad(jsonObject.get("ges_routineOnLoad").toString());
                        //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
                    } catch (Exception e) {
                    }
                    try {
                        this.setActionParams(jsonObject.get("actionParams").toString());
                        //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
                    } catch (Exception e) {
                    }

                    try {
                        this.setTriggerEvent(jsonObject.get("triggerEvent").toString());
                        //       System.out.println("gaiaGate CRUD riceve routineOnChange:" + myConnector.getRoutineOnChange());
                    } catch (Exception e) {
                    }

                }

            }
        } catch (ParseException ex) {
            System.out.println("error in requests manager");
        }
        return myConnectors;
    }

    public JSONObject makeJSON() {
        JSONObject myConnector = new JSONObject();

        if (door != null) {
            myConnector.put("door", door);
        }
        if (event != null) {
            myConnector.put("event", event);
        }
        if (type != null) {
            myConnector.put("type", type);
        }
        if (params != null) {
            myConnector.put("params", params);
        }
        if (form != null) {
            myConnector.put("form", form);
        }
        if (formToLoad != null) {
            myConnector.put("formToLoad", formToLoad);
        }
        if (formName != null) {
            myConnector.put("formName", formName);
        }
        if (destination != null) {
            myConnector.put("destination", destination);
        }
        if (paramsToSend != null) {
            myConnector.put("paramsToSend", paramsToSend);
        }
        if (formType != null) {
            myConnector.put("formType", formType);
        }
        if (fatherArgs != null) {
            myConnector.put("fatherArgs", fatherArgs);
        }
        if (loadType != null) {
            myConnector.put("loadType", loadType);
        }
        if (fatherForm != null) {
            myConnector.put("fatherForm", fatherForm);
        }
        if (fatherKEYvalue != null) {
            myConnector.put("fatherKEYvalue", fatherKEYvalue);
        }
        if (fatherKEYtype != null) {
            myConnector.put("fatherKEYtype", fatherKEYtype);
        }
        if (fatherCopyTag != null) {
            myConnector.put("fatherCopyTag", fatherCopyTag);
        }
        if (curKEYvalue != null) {
            myConnector.put("curKEYvalue", curKEYvalue);
        }
        if (curKEYtype != null) {
            myConnector.put("curKEYtype", curKEYtype);
        }
        if (keyValue != null) {
            myConnector.put("keyValue", keyValue);
        }
        if (keyType != null) {
            myConnector.put("keyType", keyType);
        }
        if (ges_routineOnLoad != null) {
            myConnector.put("ges_routineOnLoad", ges_routineOnLoad);
        }
        if (sendToCRUD != null) {
            myConnector.put("sendToCRUD", sendToCRUD);
        }
        if (TBS != null) {
            myConnector.put("TBS", TBS);
        }
        if (rifForm != null) {
            myConnector.put("rifForm", rifForm);
            //    System.out.println(">>>nel gate inserisco json per rifForm:" + rifForm + "");
        }
        if (copyTag != null) {
            myConnector.put("copyTag", copyTag);
        }
        if (rifObj != null) {
            myConnector.put("rifObj", rifObj);
        }
        if (routine != null) {
            myConnector.put("routine", routine);
        }
        if (htmlCode != null) {
            myConnector.put("htmlCode", htmlCode);
        }
        if (secondaryArgs != null) {
            myConnector.put("secondaryArgs", secondaryArgs);
        }

        if (operation != null) {
            myConnector.put("operation", operation);
        }
        if (primaryFieldName != null) {
            myConnector.put("primaryFieldName", primaryFieldName);
        }
        if (primaryFieldType != null) {
            myConnector.put("primaryFieldType", primaryFieldType);
        }
        if (primaryFieldValue != null) {
            myConnector.put("primaryFieldValue", primaryFieldValue);
        }
        if (filterSequence != null) {
            myConnector.put("filterSequence", filterSequence);
        }
        if (filterField != null) {
            myConnector.put("filterField", filterField);
        }
        if (filterValue != null) {
            myConnector.put("filterValue", filterValue);
        }
        if (cellType != null) {
            myConnector.put("cellType", cellType);
        }
        if (newValue != null) {
            myConnector.put("formType", newValue);
        }
        if (cellName != null) {
            myConnector.put("cellName", cellName);
        }
        if (fieldFiltered != null) {
            myConnector.put("fieldFiltered", fieldFiltered);
        }
        if (fieldID != null) {
            myConnector.put("fieldID", fieldID);
        }
        if (table != null) {
            myConnector.put("table", table);
        }

        if (query != null) {
            myConnector.put("query", query);
        }
        if (KeyField != null) {
            myConnector.put("KeyField", KeyField);
        }
        if (currentKEY != null) {
            myConnector.put("currentKEY", currentKEY);
        }
        if (picField != null) {
            myConnector.put("picField", picField);
        }
        if (username != null) {
            myConnector.put("username", username);
        }
        if (password != null) {
            myConnector.put("password", password);
        }
        if (email != null) {
            myConnector.put("email", email);
        }
        if (IPaddress != null) {
            myConnector.put("IPaddress", IPaddress);
        }
        if (token != null) {
            myConnector.put("token", token);
        }
        if (pageArgs != null) {
            myConnector.put("pageArgs", pageArgs);
        }
        if (routineOnChange != null) {
            myConnector.put("routineOnChange", routineOnChange);
        }
        if (triggerEvent != null) {
            myConnector.put("triggerEvent", triggerEvent);
        }
        if (curPage != null) {
            myConnector.put("curPage", curPage);
        }
        return myConnector;
    }

    public ArrayList<SelectListLine> parseJSONargsTBS(String SecondaryArgs) {

        ArrayList<SelectListLine> argList = new ArrayList<SelectListLine>();

        parseJSONargsTBS(argList, SecondaryArgs);
        return argList;
    }

    public ArrayList<SelectListLine> parseJSONargsTBS(ArrayList<SelectListLine> argList, String SecondaryArgs) {
//        System.out.println("parseJSONargsTBS--->SecondaryArgs:" + SecondaryArgs);
        try {
            SecondaryArgs = java.net.URLDecoder.decode(SecondaryArgs, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(gate.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            SecondaryArgs = java.net.URLDecoder.decode(SecondaryArgs, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(gate.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONParser jsonParser = new JSONParser();
//        ArrayList<SelectListLine> argList = new ArrayList<SelectListLine>();
        try {

            if (SecondaryArgs != null && SecondaryArgs.length() > 0) {
                SecondaryArgs = "{\"sendToCRUD\":" + SecondaryArgs.replace("'", "\"") + "}";
//                System.out.println("parseJSONargsTBS--->*SecondaryArgs:" + SecondaryArgs);
                JSONObject jsonObject;
                jsonObject = (JSONObject) jsonParser.parse(SecondaryArgs);
                String TBSarray = jsonObject.get("sendToCRUD").toString();
                if (TBSarray != null && TBSarray.length() > 0) {
                    JSONParser parser = new JSONParser();
                    Object obj;

                    obj = parser.parse(TBSarray);
                    JSONArray array = (JSONArray) obj;

                    for (Object riga : array) {
                        SelectListLine myLine = new SelectListLine();
                        jsonObject = (JSONObject) jsonParser.parse(riga.toString());
                        try {
                            myLine.setLabel(jsonObject.get("childMarker").toString());
                        } catch (Exception e) {
                        }
                        try {
                            myLine.setValue(jsonObject.get("value").toString());
                        } catch (Exception e) {
                        }
                        boolean found = false;
                        for (int y = 0; y < argList.size(); y++) {
                            if (argList.get(y).getLabel().equalsIgnoreCase(myLine.getLabel())) {
                                found = true;
                            }
                        }
                        if (found == false) {
                            argList.add(myLine);
                        }
                    }
                    //sendToCRUD
                    for (int y = 0; y < argList.size(); y++) {
                        System.out.println("<BR>KEY :" + argList.get(y).getLabel() + "->" + argList.get(y).getValue());
                    }
                }
            }
        } catch (ParseException ex) {
            System.out.println("parseJSONargsTBS--->ERROR PARSING TBS JSON:" + ex);
        }
        return argList;
    }

    private JSONObject loadConnector() {

////////        System.out.println("\n\nformID formID:" + myGate.formID);
////////        System.out.println("formID formName:" + myGate.formName);
////////        System.out.println("copyTag  copyTag:" + myGate.copyTag);
////////        System.out.println("keyValue  getPrimaryFieldValue:" + myGate.getPrimaryFieldValue());
////////        System.out.println("valueKEY  getPrimaryFieldName:" + myGate.getPrimaryFieldName());
////////        System.out.println("fatherForm  fatherForm:" + myGate.getFatherForm());
////////        System.out.println("fatherCopyTag  getFatherCopyTag:" + myGate.getFatherCopyTag());
////////        System.out.println("fatherKEYvalue  fatherKEYvalue:" + myGate.getFatherKEYvalue());
////////        System.out.println("fatherKEYtype  fatherKEYtype:" + myGate.getFatherKEYtype());
////////        System.out.println("getKeyValue  getKeyValue:" + myGate.getKeyValue());
////////        System.out.println("getKeyValue  getCurPage:" + myGate.getCurPage()); 
////////        System.out.println("newValue  newValue:" + myGate.newValue);
////////        System.out.println("tbs  TBS:" + myGate.TBS);
////////        System.out.println("STC  sendToCRUD:" + myGate.sendToCRUD);
        JSONObject connector = new JSONObject();

        if (this.getSendToCRUD() == null || this.getSendToCRUD().length() < 2 || !this.getSendToCRUD().startsWith("[")) {
            String stc = this.getSendToCRUD();
            stc = "[" + stc + "]";
            this.setSendToCRUD(stc);
        }
        JSONParser jsonParser = new JSONParser();
        JSONArray mySTC = new JSONArray();
        try {
            mySTC = (JSONArray) jsonParser.parse(this.getSendToCRUD());
        } catch (ParseException ex) {
        }
        connector.put("STC", mySTC);
        if (this.getTBS() == null || this.getTBS().length() < 2 || !this.getTBS().startsWith("[")) {
            String myTbs = this.getTBS();
            myTbs = "[" + myTbs + "]";
            this.setTBS(myTbs);
        }
        jsonParser = new JSONParser();
        JSONArray myTBS = new JSONArray();
        try {
            myTBS = (JSONArray) jsonParser.parse(this.getTBS());
        } catch (ParseException ex) {
        }
        connector.put("tbs", myTBS);

        return connector;
    }

}
