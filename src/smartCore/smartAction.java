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
package smartCore;

import smartCore.smartForm;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.EVOpagerDBconnection;
import REVOwebsocketManager.WSclient;
import REVOwebsocketManager.WShandler;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.CRUDorder;
import models.Linker;
import models.SelectListLine;
import models.correlazione;
import showIt.eventManager;
import static showIt.eventManager.encodeURIComponent;
import models.gate;
import org.apache.commons.io.IOUtils;
import smartCore.smartForm.smartFormResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.ShowItForm;

/**
 *
 * @author FFS INFORMATICA [info at ffs.it]
 */
public class smartAction {

    public JSONObject connector;
    public JSONObject jObj = new JSONObject();
    public EVOpagerParams myParams;
    public Settings mySettings;
    WSclient senderClient;
    WShandler supreme;
    public ArrayList<childLink> myChilds;
    public String door;
    public String event;
    CRUDorder myCrud;

    public smartAction(WShandler Xsupreme, WSclient XsenderClient, EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        this.senderClient = XsenderClient;
        this.supreme = Xsupreme;
        myChilds = new ArrayList<childLink>();
        door = "";
        event = "";
    }

    public smartAction(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        this.senderClient = null;
        this.supreme = null;
        myChilds = new ArrayList<childLink>();
        door = "";
        event = "";
    }

    public CRUDorder getMyCrud() {
        return myCrud;
    }

    public void setMyCrud(CRUDorder myCrud) {
        this.myCrud = myCrud;
    }

    public JSONObject getjObj() {
        return jObj;
    }

    public ArrayList<childLink> getMyChilds() {
        return myChilds;
    }

    public JSONObject getConnector() {
        return connector;
    }

    public String getDoor() {
        return door;
    }

    public String getEvent() {
        return event;
    }

    public JSONObject resolveConnector(String decodedMessage) {
//        System.out.println("resolveAction decodedMessage: " + decodedMessage);
        JSONParser jsonParser = new JSONParser();
        String payload = "";
        String params = "";
        try {
            jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(decodedMessage);
            payload = getJSONarg(jsonObject, "payload");
            params = getJSONarg(jsonObject, "params");
        } catch (ParseException ex) {
            System.out.println("ERROR: " + ex.toString());
        }

        JSONArray filter = null;
        JSONArray connectors = null;
        try {
            try {
                payload = java.net.URLDecoder.decode(payload, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
            }
            System.out.println("resolveConnector RICEVUTA RICHIESTA wsRequest. payload: " + payload);
            jsonParser = new JSONParser();
            JSONObject pl = (JSONObject) jsonParser.parse(payload);
            try {
                filter = (JSONArray) pl.get("filter");
            } catch (Exception e1) {
                System.out.println("resolveAction filter ERROR: " + e1.toString());
            }
            try {
                connectors = (JSONArray) pl.get("connectors");
                System.out.println("connectors.size: " + connectors.size());
            } catch (Exception e1) {
                System.out.println("connectors connectors ERROR: " + e1.toString());
            }
        } catch (ParseException ex) {
//            System.out.println("resolveAction ERROR: " + ex.toString());
            try {
                try {
                    payload = java.net.URLDecoder.decode(payload, "UTF-8");
                } catch (UnsupportedEncodingException ezx) {
                }
                System.out.println("rrielaborata RICHIESTA wsRequest. payload: " + payload);
                jsonParser = new JSONParser();
                JSONObject pl = (JSONObject) jsonParser.parse(payload);
                try {
                    filter = (JSONArray) pl.get("filter");
                } catch (Exception e1) {
                    System.out.println("resolveAction filter2 ERROR: " + e1.toString());
                }
                try {
                    connectors = (JSONArray) pl.get("connectors");
                    System.out.println("connectors.size: " + connectors.size());
                } catch (Exception e1) {
                    System.out.println("connectors connectors ERROR: " + e1.toString());
                }
            } catch (ParseException exx) {
                System.out.println("resolveAction2 ERROR: " + exx.toString());

            }

        }
        jObj = (JSONObject) connectors.get(0);
        System.out.println("resolveConnector---> connector: " + jObj.toString());

        return jObj;
    }

    public void makeCRUDobject(JSONObject Xconnector) {

        connector = Xconnector;
        System.out.println("makeCRUDobject ---> : " + connector.toString());
        myCrud = new CRUDorder(myParams, mySettings);

        try {
            myCrud.setOperation(connector.get("operation").toString());
        } catch (Exception e) {
        }   // ADD;UPD;DEL
//                    try{ myCrud.setFormName(connector.get("operation").toString());} catch(Exception e){}
        try {
            myCrud.setCellType(connector.get("cellType").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setRoutineOnChange(connector.get("routineOnChange").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setFilterSequence(connector.get("filterSequence").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setPrimaryFieldName(connector.get("primaryFieldName").toString());
        } catch (Exception e) {
        } // in caso di DEL indica il valore del campo primary (es. ID)
        try {
            myCrud.setPrimaryFieldValue(connector.get("primaryFieldValue").toString());
        } catch (Exception e) {
        } // in caso di DEL indica il valore del campo primary
        try {
            myCrud.setPrimaryFieldType(connector.get("primaryFieldType").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setFilterField(connector.get("filterField").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setFilterValue(connector.get("filterValue").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setCellName(connector.get("cellName").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setNewValue(connector.get("newValue").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setFatherKEYvalue(connector.get("fatherKEYvalue").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setFatherKEYtype(connector.get("fatherKEYtype").toString());
        } catch (Exception e) {
        }
//                   try{  myCrud.setFieldFiltered(connector.get("operation").toString());} catch(Exception e){}
        try {
            myCrud.setToBeSent(connector.get("tbs").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setSendToCRUD(connector.get("sendToCRUD").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setSendToCRUD(connector.get("STC").toString());
        } catch (Exception e) {
        }

//                    System.out.println("smartAction --->connector.get(\"sendToCRUD\"): " + connector.get("sendToCRUD").toString());
//                    System.out.println("smartAction --->connector.get(\"STC\"): " + connector.get("STC").toString());
        try {
            myCrud.setFormID(connector.get("formID").toString());
        } catch (Exception e) {
        }

//        System.out.println("smartAction --->connector.get(\"formID\"): " + connector.get("formID").toString());
        try {
            myCrud.setFormCopyTag(connector.get("copyTag").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setCellID(connector.get("cellID").toString());
        } catch (Exception e) {
        }

        try {
            myCrud.setNodeName(connector.get("nodeName").toString());
        } catch (Exception e) {
        }
        try {
            myCrud.setFatherNodeName(connector.get("fatherNodeName").toString());
        } catch (Exception e) {
        }

    }

    public JSONObject deployAction(JSONObject Xconnector) {

        connector = Xconnector;
        System.out.println("\ndeployAction----->connector: " + connector );
        JSONObject actionResponse = new JSONObject();
        try {
//            connector = (JSONObject) connectors.get(0);
            door = connector.get("door").toString();
            event = connector.get("event").toString();
            System.out.println("\ndeployAction----->door: " + door + ",  event: " + event);
// <editor-fold defaultstate="collapsed" desc="DOOR formPager">            
            if (door.equalsIgnoreCase("formPager")) {
                //------------------------------------------------------------------------------ 
                // <editor-fold defaultstate="collapsed" desc="REFILTER">
                if (event.equalsIgnoreCase("REFILTER")) {
                    try {
                        String filtroLike = "";
                        System.out.println("resolveAction--->EVENTO REFILTER ");
                        String jfilter = "";
                        String JcurPage = "";
                        String newOrder = "";
                        try {
                            jfilter = connector.get("filter").toString();

                            System.out.println("*jfilter: " + jfilter);
                            JSONArray array = (JSONArray) connector.get("filter");
                            System.out.println("array.size: " + array.size());

                            try {

                                ArrayList<JSONObject> list = new ArrayList<>();

                                for (int i = 0; i < array.size(); i++) {
                                    list.add((JSONObject) array.get(i));
                                    System.out.println("list.add: " + ((JSONObject) array.get(i)).get("field").toString());
                                }
                                if (list != null && list.size() > 0) {
                                    Collections.sort(list, new MyJSONComparator());
                                }

                                for (JSONObject obj : list) {
                                    String field = obj.get("field").toString();
                                    String direction = obj.get("direction").toString();
                                    String value = obj.get("value").toString();//testo inserito nel filtro
                                    if (field.contains("[")) {
                                        try {
                                            int start = field.indexOf("[");
                                            int end = field.indexOf("]");
                                            String newField = field.substring(start + 1, end);
//                                            System.out.println("field: " + field + " --> newField: " + newField);
                                            field = newField;
                                        } catch (Exception e) {
                                            System.out.println("Error in FILTER manage ");
                                        }
                                    }
                                    if (value != null && value.length() > 2) {
                                        if (filtroLike.length() > 0) {
                                            filtroLike += " AND ";
                                        }
                                        filtroLike += " " + field + " LIKE '%" + value + "%' ";
                                    }

                                    int pos = 0;
                                    try {
                                        pos = Integer.parseInt(obj.get("position").toString());
                                    } catch (Exception e) {
                                    }
                                    System.out.println("POS:" + pos + ")Field:" + field + " ");
                                    if (newOrder.length() > 0) {
                                        newOrder += ", ";
                                    }
                                    newOrder += field;
                                    if (!direction.equalsIgnoreCase("A")) {
                                        newOrder += " DESC ";
                                    } else {
                                        newOrder += " ASC ";
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                        }
                        try {
                            JcurPage = connector.get("curPage").toString();
                        } catch (Exception e) {
                        }

                        int curPage = 1;
                        try {
                            curPage = Integer.parseInt(JcurPage);
                        } catch (Exception e) {

                        }
//                        System.out.println("curPage:" + curPage);
                        smartForm myForm = loadFORMparams(connector);
                        JSONObject loadTypeObj = new JSONObject();
                        loadTypeObj.put("visualType", "DATAONLY");
                        loadTypeObj.put("firstRow", 1);
                        loadTypeObj.put("NofRows", 50);
                        loadTypeObj.put("currentPage", curPage);
                        loadTypeObj.put("visualFilter", "");
                        myForm.setLoadType(loadTypeObj.toString());
                        myForm.buildSchema();
                        myForm.loadPagingInstructions();
                        String newFilter = filtroLike;
                        System.out.println("newFilter: " + newFilter);
                        System.out.println("newOrder:" + newOrder);
                        String newGroup = "";

//                        System.out.println("Come base per il filter uso la query già sostituita: " + myForm.queryUsed);
//                        if (newFilter != null && newFilter.length() > 0) {
                        smartQuery mySquery = new smartQuery(myForm.queryUsed, myForm.filteredElements, myForm.visualFilter);
                        myForm.queryUsed = mySquery.regenerateQuery(newFilter, newOrder, newGroup, true, "AND", true, true);
                        System.out.println("regenerateQuery-->Query used: " + myForm.queryUsed);
//                        }

//                    myForm.queryUsed += newOrder;
                        System.out.println("Query used: " + myForm.queryUsed);
                        smartFormResponse myFormResponse = myForm.paintDataTable();
                        //                    smartFormResponse myFormResponse = myForm.paintForm();
                        //                    System.out.println("Eseguito paintform: " + myFormResponse.HtmlCode);
                        String htmlCode = myFormResponse.getHtmlCode();
                        htmlCode = encodeURIComponent(htmlCode);
                        String destDiv = "";
                        destDiv += myForm.getID() + "-" + myForm.getCopyTag() + "-ROWSDIV";
//                        System.out.println("destDiv: " + destDiv);

                        JSONObject outPayload = new JSONObject();
                        outPayload.put("ACTION", "REFRESHFORM");
                        outPayload.put("DESTDIV", destDiv);
                        outPayload.put("CODE", htmlCode);
                        actionResponse = new JSONObject();

                        actionResponse.put("ip", "0000");
                        actionResponse.put("TYPE", "wsResponse");
                        actionResponse.put("payload", outPayload);
                    } catch (Exception e) {
                        System.out.println("Error in refilter: " + e.toString());
                        e.printStackTrace();
                    }
                } else //------------------------------------------------------------------------------
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="LOADCHILDREN">
                if (event.equalsIgnoreCase("LOADCHILDREN")) {
                    System.out.println("resolveAction--->EVENTO LOADCHILDREN ");
                    actionResponse = new JSONObject();
                    actionResponse = WSRloadChildren(connector);

                    System.out.println("deployAction--->done EVENTO LOADCHILDREN: " + actionResponse.toString());
                } else // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="NOTIFYSELECTION">
                if (event.equalsIgnoreCase("NOTIFYSELECTION")) {
                    System.out.println("resolveAction--->EVENTO NOTIFYSELECTION ");
                    actionResponse = new JSONObject();

                    gate myGate = new gate();
                    myGate.connector2gate(connector);
                    smartForm mySmartForm = new smartForm(myGate, myParams, mySettings);
                    mySmartForm.loadSettingsAndPanel();
                    String routineOnTouch = mySmartForm.routineOnRowSelected;

                    JSONObject myJObj = new JSONObject();

                    myJObj.put("ip", "0000");
                    myJObj.put("TYPE", "wsRoutineOnTouch");
                    myJObj.put("ROUTINE", routineOnTouch);
                    actionResponse = myJObj;
                    System.out.println("deployAction--->done EVENTO NOTIFYSELECTION: " + actionResponse.toString());
                } else // </editor-fold>                    
                // <editor-fold defaultstate="collapsed" desc="LOADTREEBRANCH">
                if (event.equalsIgnoreCase("LOADTREEBRANCH")) {
                    System.out.println("resolveAction--->EVENTO LOADTREEBRANCH " + connector.toString());

                    actionResponse = new JSONObject();
                    actionResponse = WSRloadTreeBranch(connector);

                    //         
//        gate myGate = new gate();
//        JSONObject Jaction = new JSONObject();
//        Jaction.put("action", "PAINTBRANCH");
//        Jaction.put("CONNECTOR", (connector));
//        Jaction.put("DATA", encodeURIComponent(data.toString()));
//        myGate.actions.add(Jaction);
//        
//       JSONObject    actionResponse = myGate.insertAction_packResponse(myParams, mySettings);
                    System.out.println("resolveAction--->done EVENTO LOADTREEBRANCH: " + actionResponse.toString());
                } else // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="GETSUGGESTEDLIST">
                if (event.equalsIgnoreCase("GETSUGGESTEDLIST")) {
//                    System.out.println("resolveAction--->EVENTO GETSUGGESTEDLIST ");

                    String htmlCode = "";
                    htmlCode = encodeURIComponent(htmlCode);
                    String destDiv = "";
                    destDiv += "";
//                    System.out.println("destDiv: " + destDiv);

                    actionResponse = new JSONObject();

                    JSONObject outPayload = new JSONObject();
                    outPayload.put("ACTION", "REFRESHFORM");
                    outPayload.put("DESTDIV", destDiv);
                    outPayload.put("CODE", htmlCode);

                    actionResponse.put("ip", "0000");
                    actionResponse.put("TYPE", "wsResponse");
                    actionResponse.put("payload", outPayload);

////////                    
////////                    
////////                    
////////                    actionResponse = new JSONObject();
////////                    actionResponse.put("ip", "0000");
////////                    actionResponse.put("TYPE", "wsResponse");
////////                    actionResponse.put("ACTION", "REFRESHFORM");
////////                    actionResponse.put("DESTDIV", destDiv);
////////                    actionResponse.put("CODE", htmlCode);
                }
// </editor-fold>    
                // <editor-fold defaultstate="collapsed" desc="CELLCHANGED">
                if (event.equalsIgnoreCase("CELLCHANGED")) { //---> COMPORTA UN CRUD
                    System.out.println("deployAction--->EVENTO CELLCHANGED ");
                    makeCRUDobject(connector);
                    System.out.println("\n\n*****\nsono in smartAction e sto per esegiore il CRUD " + myCrud.getOperation());
//                    UUID idOne = null;
//                    idOne = UUID.randomUUID();
//                    connector.put("opToken", idOne);
                    String CRUDrepsonse = null;
                    try {
//                        CRUDrepsonse = myCrud.executeCRUD();
                        CRUDrepsonse = myCrud.executeSmartCRUD();
                    } catch (Exception e) {
                        System.out.println("er4:D" + e.toString());
                    }
                    System.out.println("deployAction--->concluso  executeCRUD");
                    System.out.println("deployAction--->CRUDrepsonse" + CRUDrepsonse.toString());
                    JSONObject Rjson = null;
                    String newID = "";
                    try {
                        JSONParser parser = new JSONParser();
                        Rjson = (JSONObject) parser.parse(CRUDrepsonse);

                        newID = Rjson.get("newID").toString();
                        // adesso ricavo la riga da mettere in lista 
                        System.out.println("smartAction --->QUERY: " + myCrud.getMyForm().getQuery());
                        System.out.println("smartAction --->newID: " + newID);
                        System.out.println("smartAction --->myCrud.getOperation(): " + myCrud.getOperation());
                        try {
                            connector.remove("keyValue");
                        } catch (Exception e) {
                        }
                        connector.put("keyValue", Rjson.get("newID").toString());
                    } catch (Exception e) {
                        System.out.println("smartAction --->NON NEW ID: " + e.toString());
                    }

                    System.out.println("smartAction --->connector: " + connector.toString());
//                    System.out.println("smartAction --->FORM TYPE: " + myCrud.getFormType());
//                    System.out.println("smartAction --->CRUDrepsonse: " + CRUDrepsonse);
//                    System.out.println("smartAction --->getGes_formPanel: " + myCrud.getMyForm().getGes_formPanel());
                    if (myCrud.getOperation().equalsIgnoreCase("ADD")) {
                        String action = "SHOWADDEDROW";
                        // <editor-fold defaultstate="collapsed" desc="caso SMARTTREE">
                        if (myCrud.getFormType() != null && myCrud.getFormType().equalsIgnoreCase("SMARTTREE")) {
                            String nodeName = (String) connector.get("nodeName");
                            System.out.println("smartAction --->ADD nodeName: " + nodeName);

                            action = "SHOWADDEDLEAF";
                            String fatherNodeObject = "";
                            String arrayToRead = "CHILDautolinks";
                            if (nodeName.endsWith("-ROOT")) {
                                arrayToRead = "ROOTautolinks";
                                fatherNodeObject = "";
                            } else {
                                String[] xParts = nodeName.split("-");
                                List<String> nodeNameElements = Arrays.asList(xParts);
                                if (nodeNameElements.size() > 2) {
                                    fatherNodeObject = nodeNameElements.get(2).toString();
                                    System.out.println("smartAction --->fatherNodeObject: " + fatherNodeObject);
                                }
                            }
////////                            // ho appena eseguito crud su un leaf di SMARTTREE
////////                            // devo analizzare il FORMPANEL INFO compilato in GaiaEngineSetter per estrapolare 
////////                            //IN CASO DI ROOT {"autolinks":[{"linkTab":"TS_link_obj_tipi","partAvalue":"ID","partBvalue":"automobili","partAtab ":"TS_objects","partAvalueField":"ID","partBtab":"TS_tipi","partBvalueField":"ID","partAstatus":"1","partBstatus":"1","superStatus":"1"}]} 
////////                            String formPanelArrayTXT = myCrud.getMyForm().getGes_formPanel();
////////                            JSONParser parser1 = new JSONParser();
////////                            JSONArray PNLjson = (JSONArray) parser1.parse(formPanelArrayTXT);
////////                            for (Object righe : PNLjson) {
////////                                JSONObject riga = (JSONObject) righe;
////////                                try {
////////                                    JSONArray ALjson = (JSONArray) riga.get(arrayToRead);
////////                                    if (ALjson != null && ALjson.size() > 0) {
////////                                        for (Object Xautolink : ALjson) {
////////                                            JSONObject Tlink = (JSONObject) Xautolink;
////////
////////                                            System.out.println("smartAction --->Tlink: " + Tlink.toString());
////////                                            if (Tlink != null) {
////////                                                int Astatus = 1;
////////                                                int Bstatus = 1;
////////                                                int superStatus = 1;
////////                                                correlazione myLink = new correlazione();
////////
////////                                                try {
////////                                                    myLink.setLinkTab(Tlink.get("linkTab").toString());
////////                                                } catch (Exception e) {
////////                                                }
////////                                                //"partAvalue":"@@@FATHERNODE@@@","partBvalue":"@@@ID@@@"
////////                                                try {
////////                                                    String partAvalue = Tlink.get("partAvalue").toString();
////////                                                    partAvalue = partAvalue.replace("@@@ID@@@", newID);
////////                                                    partAvalue = partAvalue.replace("@@@FATHERNODE@@@", fatherNodeObject);
////////                                                    myLink.setPartAvalue(partAvalue);
////////
////////                                                } catch (Exception e) {
////////                                                }
////////                                                try {
////////                                                    String partBvalue = Tlink.get("partBvalue").toString();
////////                                                    partBvalue = partBvalue.replace("@@@ID@@@", newID);
////////                                                    partBvalue = partBvalue.replace("@@@FATHERNODE@@@", fatherNodeObject);
////////                                                    myLink.setPartBvalue(partBvalue);
////////
////////                                                } catch (Exception e) {
////////                                                }
////////                                                try {
////////                                                    myLink.setPartAtab(Tlink.get("partAtab").toString());
////////                                                } catch (Exception e) {
////////                                                }
////////                                                try {
////////                                                    myLink.setPartAvalueField(Tlink.get("partAvalueField").toString());
////////                                                } catch (Exception e) {
////////                                                }
////////                                                try {
////////                                                    myLink.setPartBtab(Tlink.get("partBtab").toString());
////////                                                } catch (Exception e) {
////////                                                }
////////                                                try {
////////                                                    myLink.setPartBvalueField(Tlink.get("partBvalueField").toString());
////////                                                } catch (Exception e) {
////////                                                }
////////                                                try {
////////                                                    myLink.setPartAstatus(Tlink.get("partAstatus").toString());
////////                                                    Astatus = Integer.parseInt(myLink.getPartAstatus());
////////                                                } catch (Exception e) {
////////                                                }
////////                                                try {
////////                                                    myLink.setPartBstatus(Tlink.get("partBstatus").toString());
////////                                                    Bstatus = Integer.parseInt(myLink.getPartBstatus());
////////                                                } catch (Exception e) {
////////                                                }
////////                                                try {
////////                                                    myLink.setSuperStatus(Tlink.get("superStatus").toString());
////////                                                    superStatus = Integer.parseInt(myLink.getSuperStatus());
////////
////////                                                } catch (Exception e) {
////////                                                }
////////                                                System.out.println("\n****\nsmartAction --->DEVO LINKARE SULLA TABELLA : " + myLink.getLinkTab());
////////
////////                                                Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
////////                                                PreparedStatement ps = null;
////////                                                ResultSet rs;
////////                                                String linkerQuery = "INSERT INTO " + myLink.getLinkTab() + "( `superStatus`, `partAtab`, `partAvalueField`, `partAvalue`, `partAstatus`, `partBtab`,"
////////                                                        + " `partBvalueField`, `partBvalue`, `partBstatus` "
////////                                                        + ") VALUES ("
////////                                                        + " ?,?,?,?,?,?,?,?,?) ";
////////
////////                                                ps = conny.prepareStatement(linkerQuery);
////////                                                ps.setInt(1, superStatus);
////////                                                ps.setString(2, myLink.getPartAtab());
////////                                                ps.setString(3, myLink.getPartAvalueField());
////////                                                ps.setString(4, myLink.getPartAvalue());
////////                                                ps.setInt(5, Astatus);
////////                                                ps.setString(6, myLink.getPartBtab());
////////                                                ps.setString(7, myLink.getPartBvalueField());
////////                                                ps.setString(8, myLink.getPartBvalue());
////////                                                ps.setInt(9, Bstatus);
////////
////////                                                System.out.println(" linkerQuery : " + ps.toString());
////////                                                try {
////////                                                    int i = ps.executeUpdate();
////////                                                    System.out.println(" esito inserimento: " + i);
////////                                                } catch (Exception e) {
////////                                                    System.out.println(" errore626 : " + e.toString());
////////                                                }
////////                                            }
////////                                        }
////////                                    }
////////                                } catch (Exception ee) {
////////
////////                                }
////////                            }

                            insertAutolinks(arrayToRead, newID, fatherNodeObject);
                            System.out.println("smartAction --->vado a costruire la nuova riga leaf ");
                            JSONObject newLeaf = makeLeafAddedJson(connector);
                            Rjson.put("code", newLeaf);
                            CRUDrepsonse = Rjson.toString();
                            System.out.println("smartAction --->leaf: " + newLeaf);
                        } else {
                            // eseguito NEW su form SMARTTABLE
                            String arrayToRead = "autolinks";
                            insertAutolinks(arrayToRead, newID, "");
                            System.out.println("smartAction --->autolinks inseriti. ");
                        }
// </editor-fold>
//                        String htmlCode = "";
//                        htmlCode = encodeURIComponent(htmlCode);
                        actionResponse = new JSONObject();
                        JSONObject outPayload = new JSONObject();
                        outPayload.put("ACTION", action);
                        outPayload.put("CONNECTOR", (connector));
                        outPayload.put("CRUDrepsonse", encodeURIComponent(CRUDrepsonse));
                        JSONObject clientParams = myParams.makeJSONobjParams();
                        actionResponse.put("ip", "0000");
                        actionResponse.put("TYPE", "wsResponse");
                        actionResponse.put("payload", outPayload);
                        actionResponse.put("clientParams", clientParams);

                        System.out.println("[ADD] actionResponse ---> " + actionResponse.toString());
                    } else if (myCrud.getOperation().equalsIgnoreCase("DEL")) {

                    } else if (myCrud.getOperation().equalsIgnoreCase("UPD")) {

                        System.out.println("deployAction--->actionResponse" + actionResponse.toString());
                        connector.put("routineOnUpdateObj", Rjson.get("routineOnUpdateObj").toString());
                        System.out.println("connector ---> " + connector.toString());
                        String action = "CRUD-UPD-RESPONSE";
                        if (myCrud.getFormType() != null && myCrud.getFormType().equalsIgnoreCase("SMARTTREE")) {
                            action = "CRUD-UPD-RESPONSE-LEAF";
                        }
//                        System.out.println("smartAction --->myCrud.getRoutineOnChange(): " + myCrud.getRoutineOnChange());
                        actionResponse = new JSONObject();
                        JSONObject outPayload = new JSONObject();
                        outPayload.put("ACTION", action);
                        outPayload.put("CONNECTOR", (connector));
                        outPayload.put("CRUDrepsonse", encodeURIComponent(CRUDrepsonse));
                        JSONObject clientParams = myParams.makeJSONobjParams();
                        actionResponse.put("ip", "0000");
                        actionResponse.put("TYPE", "wsResponse");
                        actionResponse.put("payload", outPayload);
                        actionResponse.put("clientParams", clientParams);
                        System.out.println("actionResponse ---> " + actionResponse.toString());
                    }

                } else {

                }
                // </editor-fold>    

            } else // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="DOOR clickedObject">            
            if (door.equalsIgnoreCase("clickedObject")) {
//------------------------------------------------------------------------------ 
                // <editor-fold defaultstate="collapsed" desc="ExecuteRoutine">
                if (event.equalsIgnoreCase("ExecuteRoutine")) {
                    System.out.println("\n\n###########\nSONO IN CERCA DELLA ROUTINE DA ESEGUIRE ");
                    //2021-03-16: la routine voglio andarla a prendere dallo schema del form
                    // senza farmela passare dalle procedure via browser
                    gate myGate = new gate();
                    myGate.connector2gate(connector);
                    smartForm mySmartForm = new smartForm(myGate, myParams, mySettings);
                    mySmartForm.buildSchema();
                    boolean found = false;

                    System.out.println("Cerco routine in oggetto: " + myGate.getRifObj());
                    for (smartObject object : mySmartForm.objects) {
                        if (object.name.equalsIgnoreCase(myGate.getRifObj())) {
                            String objActionParams = object.actionParams;
                            System.out.println("--AppWS: PULSANTE " + myGate.getRifObj() + ", objActionParams=" + objActionParams);
                            JSONParser jsonParser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(objActionParams);
                            String routine = getJSONarg(jsonObject, "routine");
                            String paramsToSend = getJSONarg(jsonObject, "paramsToSend");
                            connector.put("routine", routine);
                            System.out.println("ricavata routine: " + routine);
                            connector.put("paramsToSend", paramsToSend);
                            found = true;
                            break;
                        }
                    }
                    if (found == false) {
                        System.out.println("Non trovato oggetto, cerco nei Form objects " + mySmartForm.formObjects.size());
                        for (smartObject object : mySmartForm.formObjects) {
                            if (object.name.equalsIgnoreCase(myGate.getRifObj())) {
                                String objActionParams = object.actionParams;
                                System.out.println("--AppWS: PULSANTE " + myGate.getRifObj() + ", objActionParams=" + objActionParams);
                                JSONParser jsonParser = new JSONParser();
                                JSONObject jsonObject = (JSONObject) jsonParser.parse(objActionParams);
                                String routine = getJSONarg(jsonObject, "routine");
                                String paramsToSend = getJSONarg(jsonObject, "paramsToSend");
                                connector.put("routine", routine);
                                System.out.println("ricavata routine: " + routine);
                                connector.put("paramsToSend", paramsToSend);
                                found = true;
                                break;
                            }
                        }
                    }
                    // se ancora non trovo una routine da eseguire o non trovo l'oggetto nel form 
                    // ad esempio nei form calendar i pulsanti per cambiare anno e mese
                    // allora eseguo una routine con lo stesso nome del pulsante
                    if (found == false) {
                        System.out.println("Non trovato oggetto, allora eseguo una routine con lo stesso nome del pulsante. " + myGate.getRifObj());
                        connector.put("routine", myGate.getRifObj());
                        found = true;
                    }

                    // dovrò rimandare a AppWS il nome della routine da eseguire
                    // mettendolo in connector.routine
                    String destDiv = "";
//                    System.out.println("destDiv: " + destDiv);

                    JSONObject outPayload = new JSONObject();
                    outPayload.put("CONNECTOR", connector);
                    outPayload.put("DESTDIV", "");
                    outPayload.put("CODE", "");
                    actionResponse = new JSONObject();

                    JSONObject clientParams = myParams.makeJSONobjParams();
                    actionResponse.put("ip", "0000");
                    actionResponse.put("TYPE", "wsResponse");
                    actionResponse.put("payload", outPayload);
                    actionResponse.put("clientParams", clientParams);// NECESSARIO perchè AppWS possa eseguire a sua volta operazione di DB

                } else //------------------------------------------------------------------------------
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="WSktComm">
                if (event.equalsIgnoreCase("WSktComm")) {
                    // quando premo il pulsante mi comporto come se caricassi ichildren di un form
                    // semplicemente prendo i dati sui childs da actyionParams {"formToLoad":"registrazionePallet","destDiv":"R"}
                    System.out.println("resolveAction---> EVENTO WSktComm: " + connector.toString());
                    actionResponse = new JSONObject();
                    actionResponse = WSRexecuteWSroutine(connector);
                    System.out.println("resolveAction--->done EVENTO WSktComm: " + actionResponse.toString());
                } else //------------------------------------------------------------------------------
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="OpenSecForm">
                if (event.equalsIgnoreCase("OpenSecForm")) {
                    // quando premo il pulsante mi comporto come se caricassi ichildren di un form
                    // semplicemente prendo i dati sui childs da actyionParams {"formToLoad":"registrazionePallet","destDiv":"R"}
                    System.out.println("resolveAction---> EVENTO OpenSecForm: " + connector.toString());
                    actionResponse = new JSONObject();
                    actionResponse = WSRloadSecondaryForms(connector);
                    System.out.println("resolveAction--->done EVENTO SECFORM: " + actionResponse.toString());
                }
                //else //------------------------------------------------------------------------------
                // </editor-fold>                

            } else // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="DOOR GROUPCHECKER">
            if (door.equalsIgnoreCase("GROUPCHECKER")) {
                System.out.println("resolveAction--->DOOR GROUPCHECKER ");
                if (event.equalsIgnoreCase("GETGROUPS")) {
                    System.out.println("resolveAction--->EVENTO GETGROUPS");
                    actionResponse = WSRgetGroups(connector);
                } else if (event.equalsIgnoreCase("SETGROUP")) {
                    System.out.println("resolveAction--->EVENTO SETGROUP ");
                    actionResponse = WSRsetGroup(connector);// nel payload ho il nome della routine da eseguire come AfterProcessByObjectRoutine
                }
            } else // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="DOOR SMARTDROP">
            if (door.equalsIgnoreCase("SMARTDROP")) {
                System.out.println("resolveAction--->DOOR SMARTDROP ");
                if (event.equalsIgnoreCase("SOFTDROP")) {
                    System.out.println("resolveAction--->EVENTO SOFTDROP");
                    gate myGate = new gate();
                    myGate.connector2gate(connector);
                    String routine = "defaultSOFTDROP";
                    String paramsToSend = myGate.getParamsToSend();
                    connector.put("routine", routine);
                    connector.put("paramsToSend", paramsToSend);
                    JSONObject outPayload = new JSONObject();
                    outPayload.put("CONNECTOR", connector);
                    outPayload.put("DESTDIV", "");
                    outPayload.put("CODE", "");
                    actionResponse = new JSONObject();

                    JSONObject clientParams = myParams.makeJSONobjParams();
                    actionResponse.put("ip", "0000");
                    actionResponse.put("TYPE", "wsResponse");
                    actionResponse.put("payload", outPayload);
                    actionResponse.put("clientParams", clientParams);// NECESSARIO perchè AppWS possa eseguire a sua volta operazione di DB
                } else if (event.equalsIgnoreCase("HARDDROP")) {
                    System.out.println("resolveAction--->EVENTO HARDDROP ");

                }
            } else // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="DOOR GEOMAP">
            if (door.equalsIgnoreCase("GEOMAP")) {
                System.out.println("resolveAction--->DOOR GEOMAP ");
                if (event.equalsIgnoreCase("SETCOORDS")) {
                    System.out.println("resolveAction--->EVENTO SETCOORDS");
                    gate myGate = new gate();
                    myGate.connector2gate(connector);
                    String routine = "genericSETCOORDS";
                    String paramsToSend = myGate.getParamsToSend();
                    connector.put("routine", routine);
                    connector.put("paramsToSend", paramsToSend);
                    JSONObject outPayload = new JSONObject();
                    outPayload.put("CONNECTOR", connector);
                    outPayload.put("DESTDIV", "");
                    outPayload.put("CODE", "");
                    actionResponse = new JSONObject();

                    JSONObject clientParams = myParams.makeJSONobjParams();
                    actionResponse.put("ip", "0000");
                    actionResponse.put("TYPE", "wsResponse");
                    actionResponse.put("payload", outPayload);
                    actionResponse.put("clientParams", clientParams);// NECESSARIO perchè AppWS possa eseguire a sua volta operazione di DB
                } else if (event.equalsIgnoreCase("GETCOORDS")) {
                    System.out.println("resolveAction--->EVENTO GETCOORDS ");

                }
            } else // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="DOOR smartBlobManage">
            if (door.equalsIgnoreCase("smartBlobManage")) {//upload audio file
                System.out.println("resolveAction--->DOOR smartBlobManage ");
                if (event.equalsIgnoreCase("smartBlobUpload")) {
                    System.out.println("resolveAction--->EVENTO smartBlobUpload");
                    gate myGate = new gate();
                    myGate.connector2gate(connector);
                    String audioFile = getJSONarg(connector, "plblob");

                    int i = audioFile.indexOf(",");
                    audioFile = audioFile.substring(i + 1, audioFile.length());
                    System.out.println("Blob--->" + audioFile);
// decode a String
                    byte[] barr = Base64.getDecoder().decode(audioFile);
                    InputStream targetStream = new ByteArrayInputStream(barr);
                    salvaBlob(targetStream, "share\\CACHEDEV1_DATA\\Public\\queenproFileFolder\\audio.wav", "\\share\\CACHEDEV1_DATA\\Public\\queenproFileFolder\\");
                    System.out.println("deployAction--->actionResponse" + actionResponse.toString());
                    connector.put("routineOnUpdateObj", "");
                    System.out.println("connector ---> " + connector.toString());
                    String action = "CRUD-UPD-RESPONSE";
                    actionResponse = new JSONObject();
                    JSONObject outPayload = new JSONObject();
                    outPayload.put("ACTION", action);
                    outPayload.put("CONNECTOR", (connector));
                    outPayload.put("CRUDrepsonse", encodeURIComponent("X0X"));
                    JSONObject clientParams = myParams.makeJSONobjParams();
                    actionResponse.put("ip", "0000");
                    actionResponse.put("TYPE", "wsResponse");
                    actionResponse.put("payload", outPayload);
                    actionResponse.put("clientParams", clientParams);
                    System.out.println("actionResponse ---> " + actionResponse.toString());

                }
            }

// </editor-fold>
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        System.out.println("smartAction [end] --->actionResponse: " + actionResponse);
        return actionResponse;
    }

    private void salvaBlob(InputStream inputStream, String usatoPerFile, String folderPath) {
        OutputStream outputStream = null;
        try {
            System.out.println(">>>> ESEGUO IL SALVATAGGIO." + usatoPerFile);
            File file = new File(usatoPerFile);
            outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
            System.out.println(">>>>ok SALVATAGGIO ESEGUITO.");
        } catch (Exception e) {
            System.out.println("error outputStream>" + e.toString());
//                    myWShandler.sendToBrowser("status", null, myParams.getCKtokenID(), "error outputStream>" + e.toString());
            new File(folderPath).mkdirs();
            try {
                File file = new File(usatoPerFile);
                outputStream = new FileOutputStream(file);
                IOUtils.copy(inputStream, outputStream);
                System.out.println(">>>> SALVATAGGIO ESEGUITO.");
            } catch (Exception ex) {
                System.out.println("error2 outputStream>" + ex.toString());
//                        myWShandler.sendToBrowser("status", null, myParams.getCKtokenID(), "error2 outputStream>" + ex.toString());
                System.out.println(">>>> SALVATAGGIO NON ESEGUITO.");
            }
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(smartAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void insertAutolinks(String arrayToRead, String newID, String fatherNodeObject) throws ParseException {
        System.out.println("insertAutolinks --->arrayToRead: " + arrayToRead);
        System.out.println("insertAutolinks --->newID: " + newID);
        System.out.println("insertAutolinks --->fatherNodeObject: " + fatherNodeObject);

        // ho appena eseguito crud su un leaf di SMARTTREE
        // devo analizzare il FORMPANEL INFO compilato in GaiaEngineSetter per estrapolare 
        //IN CASO DI ROOT {"autolinks":[{"linkTab":"TS_link_obj_tipi","partAvalue":"ID","partBvalue":"automobili","partAtab ":"TS_objects","partAvalueField":"ID","partBtab":"TS_tipi","partBvalueField":"ID","partAstatus":"1","partBstatus":"1","superStatus":"1"}]} 
        String formPanelArrayTXT = myCrud.getMyForm().getGes_formPanel();

        System.out.println("insertAutolinks --->formPanelArrayTXT: " + formPanelArrayTXT);
        System.out.println("insertAutolinks ---> myCrud.getSendToCRUD(): " + myCrud.getSendToCRUD());
        formPanelArrayTXT = myCrud.replaceMarkers(formPanelArrayTXT, myCrud.getSendToCRUD());
        if (formPanelArrayTXT != null && formPanelArrayTXT.length() > 0) {
            try {
                JSONParser parser1 = new JSONParser();
                JSONArray PNLjson = (JSONArray) parser1.parse(formPanelArrayTXT);
                for (Object righe : PNLjson) {
                    JSONObject riga = (JSONObject) righe;
                    try {
                        JSONArray ALjson = (JSONArray) riga.get(arrayToRead);
                        System.out.println("smartAction --->ALjson: " + ALjson.toString());

                        if (ALjson != null && ALjson.size() > 0) {
                            for (Object Xautolink : ALjson) {
                                JSONObject Tlink = (JSONObject) Xautolink;

                                System.out.println("smartAction --->Tlink: " + Tlink.toString());
                                if (Tlink != null) {
                                    int Astatus = 1;
                                    int Bstatus = 1;
                                    int superStatus = 1;
                                    correlazione myLink = new correlazione();

                                    try {
                                        myLink.setLinkTab(Tlink.get("linkTab").toString());
                                    } catch (Exception e) {
                                    }
                                    //"partAvalue":"@@@FATHERNODE@@@","partBvalue":"@@@ID@@@"
                                    try {
                                        String partAvalue = Tlink.get("partAvalue").toString();
                                        partAvalue = partAvalue.replace("@@@ID@@@", newID);
                                        partAvalue = partAvalue.replace("@@@FATHERNODE@@@", fatherNodeObject);
                                        myLink.setPartAvalue(partAvalue);

                                    } catch (Exception e) {
                                    }
                                    try {
                                        String partBvalue = Tlink.get("partBvalue").toString();
                                        partBvalue = partBvalue.replace("@@@ID@@@", newID);
                                        partBvalue = partBvalue.replace("@@@FATHERNODE@@@", fatherNodeObject);
                                        myLink.setPartBvalue(partBvalue);
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myLink.setPartAtab(Tlink.get("partAtab").toString());
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myLink.setPartAvalueField(Tlink.get("partAvalueField").toString());
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myLink.setPartBtab(Tlink.get("partBtab").toString());
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myLink.setPartBvalueField(Tlink.get("partBvalueField").toString());
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myLink.setPartAstatus(Tlink.get("partAstatus").toString());
                                        Astatus = Integer.parseInt(myLink.getPartAstatus());
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myLink.setPartBstatus(Tlink.get("partBstatus").toString());
                                        Bstatus = Integer.parseInt(myLink.getPartBstatus());
                                    } catch (Exception e) {
                                    }
                                    try {
                                        myLink.setSuperStatus(Tlink.get("superStatus").toString());
                                        superStatus = Integer.parseInt(myLink.getSuperStatus());

                                    } catch (Exception e) {
                                    }
                                    System.out.println("\n****\nsmartAction --->DEVO LINKARE SULLA TABELLA : " + myLink.getLinkTab());

                                    Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
                                    PreparedStatement ps = null;
                                    ResultSet rs;
                                    String linkerQuery = "INSERT INTO " + myLink.getLinkTab() + "( `superStatus`, `partAtab`, `partAvalueField`, `partAvalue`, `partAstatus`, `partBtab`,"
                                            + " `partBvalueField`, `partBvalue`, `partBstatus` "
                                            + ") VALUES ("
                                            + " ?,?,?,?,?,?,?,?,?) ";

                                    ps = conny.prepareStatement(linkerQuery);
                                    ps.setInt(1, superStatus);
                                    ps.setString(2, myLink.getPartAtab());
                                    ps.setString(3, myLink.getPartAvalueField());
                                    ps.setString(4, myLink.getPartAvalue());
                                    ps.setInt(5, Astatus);
                                    ps.setString(6, myLink.getPartBtab());
                                    ps.setString(7, myLink.getPartBvalueField());
                                    ps.setString(8, myLink.getPartBvalue());
                                    ps.setInt(9, Bstatus);

                                    System.out.println(" linkerQuery : " + ps.toString());
                                    try {
                                        int i = ps.executeUpdate();
                                        System.out.println(" esito inserimento: " + i);
                                    } catch (Exception e) {
                                        System.out.println(" errore626 : " + e.toString());
                                    }
                                }
                            }
                        }
                    } catch (Exception ee) {

                    }
                }
            } catch (Exception d) {
                d.printStackTrace();
            }
        }

    }

    private JSONObject WSRgetGroups(JSONObject connector) {
        /*
                    + "connector.formID=formID; \n"
                + "connector.formName=formName; \n"
                + "connector.copyTag=copyTag; \n"
                + "connector.fatherForm=fatherForm; \n"
                + "connector.fatherCopyTag=fatherCopyTag; \n"
                + "connector.fatherKEYvalue=fatherKEYvalue; \n"
                + "connector.fatherKEYtype=fatherKEYtype; \n"
                + "connector.fatherArgs=fatherArgs; \n"
                + "connector.selectedRowID=selectedRowID; \n"
                + "connector.STC=StC; \n"
                + "connector.tbs = getSmartTBS(formID, copyTag, selectedRowID);\n"
                    + "connector.formID= myArg.formID;\n"
                + "connector.copyTag= myArg.copyTag;\n"
                + "connector.rifObj= myArg.rifObj;\n"
                + "connector.keyValue= myArg.keyValue;\n"
                + "connector.cellType= myArg.cellType;\n"
                + "connector.filterField= myArg.filterField;\n"
                + "connector.routineOnChange= myArg.routineOnChange;\n"
                + "connector.exitRoutine= myArg.exitRoutine;\n"
                + "connector.cellName= myArg.cellName;\n"
                + "connector.newValue= myArg.newValue;\n"
         */
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        PreparedStatement ps = null;
        ResultSet rs;
        System.out.println("SONO IN getGroups.");
        String formID = "";
        String objID = "";
        String copyTag = "";
        String keyValue = "";
        String params = "";
        formID = connector.get("formID").toString();
        copyTag = connector.get("copyTag").toString();
        objID = connector.get("rifObj").toString();
        keyValue = connector.get("keyValue").toString();
        String targetElement = formID + "-" + copyTag + "-" + objID + "-" + keyValue;

        smartForm mySmartForm = new smartForm(connector.get("formID").toString(), myParams, mySettings);
        mySmartForm.buildSchema();

        for (int kk = 0; kk < mySmartForm.getObjects().size(); kk++) {
            if (mySmartForm.getObjects().get(kk).getName().equalsIgnoreCase(objID)) {
                params = mySmartForm.getObjects().get(kk).CG.getParams();
            }
        }
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
        myCRUD.setFatherKEYvalue(connector.get("fatherKEYvalue").toString());
        myCRUD.setSendToCRUD(connector.get("tbs").toString());
        String SQLphrase = myCRUD.standardReplace(myLinker.getPartBquery(), null);

//////        
        String infos = connector.get("tbs").toString();
        String infos1 = connector.get("STC").toString();
//////         String infos2 = request.getMyGate().params;
//////         String infos3 = request.getMyGate().paramsToSend;
        System.out.println("infos (*used) tbs: " + infos);
        System.out.println("infos1 STC: " + infos1);
//////        System.out.println("infos2: "+infos2);
//////        System.out.println("infos3: "+infos3); 
        //Prima di tutto voglio la row della tabella che sta visualizzando l'utente per avere i valori dei field locali
// e se serve usarli per ler sostituzioni nella query della tendina
        try {
            myCRUD.setSendToCRUD(connector.get("STC").toString());
            String lookupQuery = myCRUD.standardReplace(mySmartForm.getQuery(), null);
            lookupQuery = remakeQuery(lookupQuery, " WHERE " + mySmartForm.getKEYfieldName() + " = ");
            String afterEffect = "'";
            if (mySmartForm.getKEYfieldType() != null && mySmartForm.getKEYfieldType().startsWith("int")) {
                afterEffect = "";
            }
            lookupQuery += afterEffect + keyValue + afterEffect + " ";

            System.out.println("lookupQuery: " + lookupQuery);

            System.out.println("keyField: " + mySmartForm.getKEYfieldName());
            System.out.println("keyFieldType: " + mySmartForm.getKEYfieldType());
            System.out.println("KeyValue: " + keyValue);

            ps = conny.prepareStatement(lookupQuery);
            rs = ps.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < mySmartForm.objects.size(); i++) {
//                    System.out.println(" Field: " + mySmartForm.objects.get(i).name + " ID: " + mySmartForm.objects.get(i).ID
//                            + " type: " + mySmartForm.objects.get(i).CG.Type + " Field type: " + mySmartForm.objects.get(i).Content.Type);
                    if (mySmartForm.objects.get(i).Content != null
                            && mySmartForm.objects.get(i).CG.Type != null
                            && mySmartForm.objects.get(i).CG.Type.equalsIgnoreCase("FIELD")) {
                        String marker = mySmartForm.objects.get(i).name;
                        String value = rs.getString(mySmartForm.objects.get(i).name);
                        if (value == null) {
                            value = "";
                        }
//                        System.out.println("Rimpiazzo @@@" + marker + "@@@ CON " + value);
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
                myLines.get(jj).setChecked(0);
                ps = conny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();
                while (rs.next()) {
                    myLines.get(jj).setChecked(1);
                    break;
                }
            }

        } catch (SQLException ex) {

        }
        try {
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(eventManager.class.getName()).log(Level.SEVERE, null, ex);
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
            HtmlCode += " onClick='javascript:smartGroupChecker(" + jsonArgs + ")'  ";
            HtmlCode += "></TD><TD>";
            HtmlCode += myLines.get(jj).getLabel();
            HtmlCode += " </TD></TR>";
        }
        HtmlCode += "</TABLE>";

        System.out.println("SHOWGROUPSPANEL: \n" + HtmlCode);

        HtmlCode = encodeURIComponent(HtmlCode);
        String destDiv = "showPanel";

        JSONObject CODE = new JSONObject();
        CODE.put("htmlCode", HtmlCode);
        String htmlCode = CODE.toString();
        htmlCode = encodeURIComponent(htmlCode);

        JSONObject payload = new JSONObject();
        payload.put("ACTION", "SHOWGROUPSPANEL");
        payload.put("DESTDIV", destDiv);
        payload.put("CODE", htmlCode);
        payload.put("TARGETELEMENT", targetElement);

        JSONObject myJObj = new JSONObject();
        myJObj.put("ip", "0000");
        myJObj.put("TYPE", "wsResponse");
        myJObj.put("payload", payload);
        return myJObj;
    }

    private JSONObject WSRsetGroup(JSONObject connector) {
        System.out.println("SONO IN setRelations.");
        /*
{"formID":"mieRif8c95",
"formName":"mieRicetteDetails",
"copyTag":"X",

"fatherForm":"mieRibef25",
"fatherCopyTag":"X",
"fatherKEYvalue":"Cremapas66e01cfa",
"fatherKEYtype":"VARCHAR",
"fatherArgs":"null",
"selectedRowID":"",
"STC":[{
"childType":"formField","value":"Cremapas66e01cfa","childMarker":"RICETTA"},
{"childType":"formField","value":"Crema pasticcera","childMarker":"NOMERICETTA"}],

"tbs":[
{"childType":"formField","childMarker":"RICETTA","value":"null"},
{"childType":"formField","childMarker":"NOMERICETTA","value":"null"}
],
"event":"SETGROUP",
"door":"GROUPCHECKER",
"rifObj":"categorie",
"keyValue":"Cremapas66e01cfa",
"cellName":"Piattiund38a592b",
"newValue":0}
         */

        String formID = "";
        String objID = "";
        String copyTag = "";
        String keyValue = "";
        String params = "";
        String valueKEY = "";
        String newValue = "";
        String fatherKEYvalue = "";
        String fatherKEYtype = "";
        String fatherForm = "";
        String fatherCopyTag = "";
        String tbs = "";
        String STC = "";
        formID = connector.get("formID").toString();
        copyTag = connector.get("copyTag").toString();
        objID = connector.get("rifObj").toString();
        keyValue = connector.get("keyValue").toString();
        valueKEY = connector.get("cellName").toString();
        newValue = connector.get("newValue").toString();
        System.out.println("newValue:" + newValue);
        fatherKEYvalue = connector.get("fatherKEYvalue").toString();
        fatherKEYtype = connector.get("fatherKEYtype").toString();
        fatherForm = connector.get("fatherForm").toString();
        fatherCopyTag = connector.get("fatherCopyTag").toString();
        tbs = connector.get("tbs").toString();
        STC = connector.get("STC").toString();

////////        System.out.println("formID:" + formID);
////////        System.out.println("copyTag:" + copyTag);
////////        System.out.println("objID:" + objID);
////////        System.out.println("keyValue:" + keyValue);
////////        System.out.println("valueKEY:" + valueKEY);
////////        System.out.println("newValue:" + newValue);
////////        System.out.println("fatherKEYvalue:" + fatherKEYvalue);
////////        System.out.println("fatherKEYtype:" + fatherKEYtype);
////////        System.out.println("fatherForm:" + fatherForm);
////////        System.out.println("fatherCopyTag:" + fatherCopyTag);
////////        System.out.println("tbs:" + tbs);
////////        System.out.println("STC:" + STC);
        String targetElement = formID + "-" + copyTag + "-" + objID + "-" + keyValue;
        String rowToRepaint = formID + "-" + copyTag + "-" + keyValue + "-ROW";

        String HtmlCode = "";
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
        mySmartForm.setLoadType("{\"type\":\"SMARTTABLE\","
                + "\"visualType\":\"SINGLEROW\"}");
//        System.out.println("Costruito smartForm... vado in buildSchema");
        mySmartForm.loadPagingInstructions();
        mySmartForm.buildSchema();
//        System.out.println("mySmartForm.getName:" + mySmartForm.getName());
//        System.out.println("Concluso buildSchema. verifico presenza routine onn change per oggetto groupchecker");
        String AfterProcessByObjectRoutine = "";
        for (int kk = 0; kk < mySmartForm.getObjects().size(); kk++) {
            if (mySmartForm.getObjects().get(kk).getName().equalsIgnoreCase(objID)) {
                params = mySmartForm.getObjects().get(kk).CG.getParams();
                AfterProcessByObjectRoutine = mySmartForm.getObjects().get(kk).routineOnChange;
            }
        }
//        System.out.println(" routine on change per oggettoo groupchecker:" + AfterProcessByObjectRoutine);

//////         request.setAfterProcessByObjectRoutines(AfterProcessByObjectRoutine);
        Linker myLinker = new Linker();
        myLinker.readParamsJson(params);

        // l'elenco da mostrare sarà dato dagli elementi in partBquery
        //Poi per ogni elemento, se esiste già una correlazione con il mio valore key
        // allora la spunta risulterà checkata
        ArrayList<SelectListLine> myLines = new ArrayList<SelectListLine>();
        String SQLphrase = myLinker.getPartBquery();

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
                System.out.println("SQLphrase:" + SQLphrase);
                ps = conny.prepareStatement(SQLphrase);
                ps.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("ERROR c ---------->" + ex.toString());
            }
//            try {
            if (Integer.parseInt(newValue) > 0) {
                SQLphrase = "INSERT INTO " + myLinker.getLinkTableName() + "  ( partAtab,partAvalueField, partAvalue,"
                        + "partBtab,partBvalueField,partBvalue"
                        + ")VALUES('" + myLinker.getPartAtab() + "','" + myLinker.getPartAfield() + "','" + keyValue + "',"
                        + "'" + myLinker.getPartBtab() + "','" + myLinker.getPartBvalueField() + "','" + valueKEY + "')";
                System.out.println("SQLphrase inserimento ---------->" + SQLphrase);
                ps = conny.prepareStatement(SQLphrase);
                ps.executeUpdate();
            }
//            } catch (SQLException ex) {
//                System.out.println("ERROR d ---------->" + ex.toString());
//            }
            conny.close();
        } catch (SQLException ex) {
            System.out.println("ERROR e ---------->" + ex.toString());
        }
// adesso devo ridisegnare la row a cui appartiene il targertElement
//        System.out.println("rowToRepaint ---------->" + rowToRepaint);

        mySmartForm.setCurKEYvalue(keyValue);
//        System.out.println("Query used: " + mySmartForm.queryUsed);
        smartFormResponse myFormResponse = mySmartForm.paintDataTable();

        HtmlCode = myFormResponse.getHtmlCode();
        HtmlCode = encodeURIComponent(HtmlCode);
        String destDiv = rowToRepaint;

        JSONObject CODE = new JSONObject();
        CODE.put("htmlCode", HtmlCode);
        String htmlCode = CODE.toString();
        htmlCode = encodeURIComponent(htmlCode);

        JSONObject payload = new JSONObject();
        payload.put("ACTION", "REFRESHROW");
        payload.put("DESTDIV", destDiv);
        payload.put("CODE", htmlCode);
        payload.put("AfterProcessByObjectRoutine", AfterProcessByObjectRoutine);

        JSONObject myJObj = new JSONObject();
        myJObj.put("ip", "0000");
        myJObj.put("TYPE", "wsResponse");
        myJObj.put("payload", payload);
        return myJObj;
    }

    public JSONObject makeLeafAddedJson(JSONObject connector) {
        System.out.println("<---------->\nmakeLeafAddedJson   " + connector.toString());
        JSONObject leafJson = new JSONObject();
        String formID = "";
        String objID = "";
        String copyTag = "";
        String keyValue = "";
        String params = "";
        String valueKEY = "";
        String newValue = "";
        String fatherKEYvalue = "";
        String fatherKEYtype = "";
        String fatherForm = "";
        String fatherCopyTag = "";
        String tbs = "";
        String STC = "";
        String nodeName = "";
        try {
            formID = connector.get("formID").toString();
        } catch (Exception e) {
        }
        try {
            copyTag = connector.get("copyTag").toString();
        } catch (Exception e) {
        }
        try {
            objID = connector.get("rifObj").toString();
        } catch (Exception e) {
        }
        try {
            keyValue = connector.get("keyValue").toString();
        } catch (Exception e) {
        }
        try {
            valueKEY = connector.get("cellName").toString();
        } catch (Exception e) {
        }
        try {
            newValue = connector.get("newValue").toString();
        } catch (Exception e) {
        }
        try {
            fatherKEYvalue = connector.get("fatherKEYvalue").toString();
        } catch (Exception e) {
        }
        try {
            fatherKEYtype = connector.get("fatherKEYtype").toString();
        } catch (Exception e) {
        }
        try {
            fatherForm = connector.get("fatherForm").toString();
        } catch (Exception e) {
        }
        try {
            fatherCopyTag = connector.get("fatherCopyTag").toString();
        } catch (Exception e) {
        }
        try {
            tbs = connector.get("tbs").toString();
        } catch (Exception e) {
        }
        try {
            STC = connector.get("STC").toString();
        } catch (Exception e) {
        }
        try {
            nodeName = connector.get("nodeName").toString();
        } catch (Exception e) {
        }

        String rowToRepaint = formID + "-" + copyTag + "-" + keyValue + "-ROW";

        System.out.println("makeLeafAddedJson ---------->rowToRepaint: " + rowToRepaint);
        String HtmlCode = "";
        smartForm mySmartForm = new smartForm(formID, myParams, mySettings);
        mySmartForm.setID(formID);
        mySmartForm.setType("SMARTTREE");
        mySmartForm.setFatherKEYvalue(fatherKEYvalue);
        mySmartForm.setFatherKEYtype(fatherKEYtype);
        mySmartForm.setFather(fatherForm);
        mySmartForm.setFatherCopyTag(fatherCopyTag);
        mySmartForm.setInfoReceived(tbs);
        mySmartForm.setCopyTag(copyTag);
        mySmartForm.setSendToCRUD(STC);
        mySmartForm.setLoadType("{\"type\":\"SMARTTREE\",\"visualType\":\"SINGLEROW\"}");
//        System.out.println("Costruito SMARTTREE... vado in buildSchema");
        mySmartForm.loadPagingInstructions();
        mySmartForm.buildSchema();
        System.out.println("mySmartForm.getName:" + mySmartForm.getName());
        System.out.println("Concluso buildSchema. verifico presenza routine onn change per oggetto groupchecker");
        String AfterProcessByObjectRoutine = "";
        for (int kk = 0; kk < mySmartForm.getObjects().size(); kk++) {
            if (mySmartForm.getObjects().get(kk).getName().equalsIgnoreCase(objID)) {
                params = mySmartForm.getObjects().get(kk).CG.getParams();
                AfterProcessByObjectRoutine = mySmartForm.getObjects().get(kk).routineOnChange;
            }
        }
        // adesso devo ridisegnare la row a cui appartiene il targertElement
        System.out.println("makeLeafAddedJson ---------->rowToRepaint: " + rowToRepaint);
        System.out.println("makeLeafAddedJson ---------->nodeName: " + nodeName);

        mySmartForm.setCurKEYvalue(keyValue);
        System.out.println("makeLeafAddedJson Query used : " + mySmartForm.queryUsed);
        if (nodeName.endsWith("-ROOT")) {
        } else {
            mySmartForm.setQuery("SELECT * FROM " + mySmartForm.mainTable + " ");
        }

        leafJson = mySmartForm.paintTreeNewLeaf();
        System.out.println("makeLeafAddedJson leafJson: " + leafJson);
        return leafJson;
    }

    public String makeRowRefreshOrder(JSONObject connector) {

        String formID = "";
        String objID = "";
        String copyTag = "";
        String keyValue = "";
        String params = "";
        String valueKEY = "";
        String newValue = "";
        String fatherKEYvalue = "";
        String fatherKEYtype = "";
        String fatherForm = "";
        String fatherCopyTag = "";
        String tbs = "";
        String STC = "";
        try {
            formID = connector.get("formID").toString();
        } catch (Exception e) {
        }
        try {
            copyTag = connector.get("copyTag").toString();
        } catch (Exception e) {
        }
        try {
            objID = connector.get("rifObj").toString();
        } catch (Exception e) {
        }
        try {
            keyValue = connector.get("keyValue").toString();
        } catch (Exception e) {
        }
        try {
            valueKEY = connector.get("cellName").toString();
        } catch (Exception e) {
        }
        try {
            newValue = connector.get("newValue").toString();
        } catch (Exception e) {
        }
        try {
            fatherKEYvalue = connector.get("fatherKEYvalue").toString();
        } catch (Exception e) {
        }
        try {
            fatherKEYtype = connector.get("fatherKEYtype").toString();
        } catch (Exception e) {
        }
        try {
            fatherForm = connector.get("fatherForm").toString();
        } catch (Exception e) {
        }
        try {
            fatherCopyTag = connector.get("fatherCopyTag").toString();
        } catch (Exception e) {
        }
        try {
            tbs = connector.get("tbs").toString();
        } catch (Exception e) {
        }
        try {
            STC = connector.get("STC").toString();
        } catch (Exception e) {
        }
////////
////////        System.out.println("\n******\nmakeRowRefreshOrder\nformID:" + formID);
////////        System.out.println("copyTag:" + copyTag);
////////        System.out.println("objID:" + objID);//rifObj
////////        System.out.println("keyValue:" + keyValue);
////////        System.out.println("valueKEY:" + valueKEY);//cellName
////////        System.out.println("newValue:" + newValue);
////////        System.out.println("fatherKEYvalue:" + fatherKEYvalue);
////////        System.out.println("fatherKEYtype:" + fatherKEYtype);
////////        System.out.println("fatherForm:" + fatherForm);
////////        System.out.println("fatherCopyTag:" + fatherCopyTag);
////////        System.out.println("tbs:" + tbs);
////////        System.out.println("STC:" + STC);

        String rowToRepaint = formID + "-" + copyTag + "-" + keyValue + "-ROW";

        String HtmlCode = "";
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
//        System.out.println("mySmartForm.getName:" + mySmartForm.getName());
//        System.out.println("Concluso buildSchema. verifico presenza routine onn change per oggetto groupchecker");
        String AfterProcessByObjectRoutine = "";
        for (int kk = 0; kk < mySmartForm.getObjects().size(); kk++) {
            if (mySmartForm.getObjects().get(kk).getName().equalsIgnoreCase(objID)) {
                params = mySmartForm.getObjects().get(kk).CG.getParams();
                AfterProcessByObjectRoutine = mySmartForm.getObjects().get(kk).routineOnChange;
            }
        }
        // adesso devo ridisegnare la row a cui appartiene il targertElement
        System.out.println("rowToRepaint ---------->" + rowToRepaint);

        mySmartForm.setCurKEYvalue(keyValue);
        System.out.println("Query used: " + mySmartForm.queryUsed);
        smartFormResponse myFormResponse = mySmartForm.paintDataTable();

        HtmlCode = myFormResponse.getHtmlCode();
        HtmlCode = encodeURIComponent(HtmlCode);
//        String destDiv = rowToRepaint;
//
//        JSONObject CODE = new JSONObject();
//        CODE.put("htmlCode", HtmlCode);
//        String htmlCode = CODE.toString();
//        htmlCode = encodeURIComponent(htmlCode);
//
//        JSONObject payload = new JSONObject();
//        payload.put("ACTION", "REFRESHROW");
//        payload.put("DESTDIV", destDiv);
//        payload.put("CODE", htmlCode);
//        JSONObject myJObj = new JSONObject();
//        myJObj.put("ip", "0000");
//        myJObj.put("TYPE", "wsResponse");
//        myJObj.put("payload", payload);
//        

        return HtmlCode;
    }

    private JSONObject WSRloadTreeBranch(JSONObject connector) {
        System.out.println("WSRloadTreeBranch connector:" + connector.toString());
        String formName = connector.get("formName").toString();
        //dal form devo ricavare keyField e nome del campo in adding row

        String objName = connector.get("objName").toString();

        int objLevel = 0;
        try {
            String xobjLevel = connector.get("objLevel").toString();

            System.out.println("WSRloadTreeBranch xobjLevel:" + xobjLevel);
            objLevel = Integer.parseInt(xobjLevel);
        } catch (Exception e) {
        }

        JSONObject data = new JSONObject();

        System.out.println("WSRloadTreeBranch formName:" + formName);
        System.out.println("WSRloadTreeBranch objName:" + objName);
        System.out.println("WSRloadTreeBranch objLevel:" + objLevel);
        //autom30e1a-X-ALFAXXXX4ecea29a-ROW

        String senderFromID = "";
        String senderCopyTag = "";
        String senderObjID = "";
        String senderFormMainTable = "";
        String[] items = objName.split("-");
        List<String> itemList = Arrays.asList(items);
        if (itemList.size() > 2) {
            senderFromID = itemList.get(0);
            senderCopyTag = itemList.get(1);
            senderObjID = itemList.get(2);
        }

        smartForm myForm = new smartForm(senderFromID, myParams, mySettings);
        myForm.setCopyTag(senderCopyTag);
//        myForm.setSendToCRUD("");
        myForm.buildSchema();
        myForm.loadFormSettings();
        senderFormMainTable = myForm.getMainTable();
//        myForm.getFormPanel(myForm.getGes_formPanel());
        System.out.println("WSRloadTreeBranch objName:" + objName);
        String arrayToRead = "CHILDautolinks";
        String formPanelArrayTXT = myForm.getGes_formPanel();
        System.out.println("WSRloadTreeBranch formPanelArrayTXT:" + formPanelArrayTXT);
        String tabellaLink = "";
        JSONParser parser1 = new JSONParser();
        JSONArray PNLjson;
        String fieldID = "ID";
        String fieldValue = "value";
        JSONArray childs = new JSONArray();
        try {
            PNLjson = (JSONArray) parser1.parse(formPanelArrayTXT);
            for (Object righe : PNLjson) {
                JSONObject riga = (JSONObject) righe;
                try {
                    JSONArray ALjson = (JSONArray) riga.get(arrayToRead);
                    if (ALjson != null && ALjson.size() > 0) {
                        for (Object Xautolink : ALjson) {
                            JSONObject Tlink = (JSONObject) Xautolink;
                            if (Tlink != null) {
                                try {
                                    tabellaLink = Tlink.get("linkTab").toString();
                                } catch (Exception e) {
                                }
                                try {
                                    fieldID = Tlink.get("fieldID").toString();
                                } catch (Exception e) {
                                }
                                try {
                                    fieldValue = Tlink.get("fieldValue").toString();
                                } catch (Exception e) {
                                }
                            }

                            //----------------------
                            Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
                            String SQLphrase = "";

                            JSONObject childrow = new JSONObject();
                            try {
                                Statement s = conny.createStatement();

                                SQLphrase = "SELECT " + senderFormMainTable + ".* FROM " + senderFormMainTable + " "
                                        + "JOIN " + tabellaLink + " ON " + tabellaLink + ".partAvalue = '" + senderObjID + "' AND " + tabellaLink + ".partBvalue = " + senderFormMainTable + ".ID\n"
                                        + "WHERE 1";

                                System.out.println("getChildrenLeafs: " + SQLphrase);
                                ResultSet rs = s.executeQuery(SQLphrase);
                                while (rs.next()) {
                                    childrow = new JSONObject();
                                    String objID = rs.getString(fieldID);
                                    senderFromID = itemList.get(0);
                                    senderCopyTag = itemList.get(1);
                                    childrow.put("ID", senderFromID + "-" + senderCopyTag + "-" + objID + "-ROW");
                                    childrow.put("value", rs.getString(fieldValue));
                                    childs.add(childrow);
                                    System.out.println("childrow: " + childrow.toString());
                                }
                            } catch (SQLException ex) {

                                System.out.println("error in line 1596:" + ex.toString());
                            }
                            try {
                                conny.close();
                            } catch (SQLException ex1) {
                                Logger.getLogger(smartAction.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                        }
                    }
                } catch (Exception ee) {
                    System.out.println("error : " + ee.toString());
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(smartAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("\n****\nsmartAction --->DEVO LINKARE SULLA TABELLA : " + tabellaLink);
        String addEnabled = "true";
        if (objLevel > 2) {
            addEnabled = "false";
        }

        data.put("values", childs);
        data.put("node", objName);
        data.put("keyField", objName);
        data.put("objName", "");
        data.put("addEnabled", addEnabled);

        String action = "PAINTBRANCH";
        JSONObject actionResponse = new JSONObject();
        JSONObject outPayload = new JSONObject();
        outPayload.put("ACTION", action);
        outPayload.put("CONNECTOR", (connector));
        outPayload.put("DATA", encodeURIComponent(data.toString()));
        JSONObject clientParams = myParams.makeJSONobjParams();

        //-----------------------------------
        actionResponse.put("ip", "0000");
        actionResponse.put("TYPE", "wsResponse");
        actionResponse.put("payload", outPayload);
        actionResponse.put("clientParams", clientParams);

        System.out.println("WSRloadTreeBranch:" + actionResponse.toString());
        return actionResponse;
    }

    private JSONObject WSRloadChildren(JSONObject connector) {
        System.out.println("\n++++\nWSRloadChildren : " + connector.toString());
        String formName = connector.get("formName").toString();
        Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        String SQLphrase = "";

        try {
            Statement s = FEconny.createStatement();
            String tabForms = mySettings.getLocalFE_forms();
            String tabForms_childhood = mySettings.getLocalFE_forms_childhood();

            SQLphrase = "SELECT " + tabForms_childhood + ".*, " + tabForms + ".query FROM " + tabForms_childhood + " "
                    + " LEFT JOIN " + tabForms + " ON " + tabForms + ".name = " + tabForms_childhood + ".rifChild "
                    + " WHERE `rifFather`='" + formName + "'";

//            System.out.println("getChildrenList: " + SQLphrase);
            ResultSet rs = s.executeQuery(SQLphrase);

            int lines = 0;
            while (rs.next()) {
                childLink myChild = new childLink();
                lines++;
                myChild.position = rs.getString("destination");
                myChild.rifChild = rs.getString("rifChild");
                try {
                    myChild.conditions = rs.getString("conditions");
                } catch (Exception e) {
                }
                try {
                    myChild.rifChild = myChild.rifChild.replaceAll("[\n\r]", "");
                } catch (Exception e) {
                }
                if (myChild.position == null || myChild.position == "null" || myChild.position == "") {
                    myChild.position = "B";
                }

                if (myChild.conditions != null && myChild.conditions.length() > 2) {
                    System.out.println("CONDIZIONE PER CHILD PRESENTE !!! ");
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject;

                    jsonObject = (JSONObject) jsonParser.parse(myChild.conditions);
                    //{"type":"TBSequals","field":"TIPOFILE","value":"ASCII"}
                    String type = getJSONarg(jsonObject, "type");
                    System.out.println("type: " + type);
                    if (type.equalsIgnoreCase("TBSequals")) {
                        String field = getJSONarg(jsonObject, "field");
                        String value = getJSONarg(jsonObject, "value");

                        String TBSarray = null;
                        String xchildType = null;
                        String xchildMarker = null;
                        String xvalue = null;
                        TBSarray = getJSONarg(connector, "tbs");

                        System.out.println("TBSarray: " + TBSarray);
                        if (TBSarray != null && TBSarray.length() > 0) {
                            JSONParser parser = new JSONParser();
                            Object obj;

                            try {
                                obj = parser.parse(TBSarray);
                                JSONArray array = (JSONArray) obj;
                                String ValoreInviato = "";
                                for (Object riga : array) {
                                    jsonObject = (JSONObject) jsonParser.parse(riga.toString());

                                    try {
                                        xchildType = jsonObject.get("childType").toString();
                                    } catch (Exception e) {
                                    }
                                    try {
                                        xchildMarker = jsonObject.get("childMarker").toString();
                                    } catch (Exception e) {
                                    }
                                    try {
                                        xvalue = jsonObject.get("value").toString();
                                    } catch (Exception e) {
                                    }
                                    if (xchildMarker.equalsIgnoreCase(field)) {
                                        ValoreInviato = xvalue;

                                        if (value.equalsIgnoreCase(ValoreInviato)) {
                                            myChilds.add(myChild);
                                        }
                                        break;
                                    }

                                }

                            } catch (ParseException ex) {
                                Logger.getLogger(smartAction.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    } else {
                        myChilds.add(myChild);
                    }

                } else {
                    myChilds.add(myChild);
                }
            }

        } catch (SQLException ex) {
            System.out.println("error in line 5317");
        } catch (ParseException ex) {
            Logger.getLogger(smartAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONArray myChildsArray = new JSONArray();
        for (int child = 0; child < myChilds.size(); child++) {
            //CICLO PER OGNI CHILD
            childLink myChild = myChilds.get(child);
            SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_forms() + " WHERE `name`='" + myChild.getRifChild() + "' ";
            try {
                Statement s;

                s = FEconny.createStatement();

                ResultSet rs = s.executeQuery(SQLphrase);
                while (rs.next()) {
                    myChild.setQuery(rs.getString("query"));
                    myChild.setRifChildID(rs.getString("ID"));
                    myChild.setRoutineBeforeLoad(rs.getString("ges_routineOnLoad"));
                    myChild.setType(rs.getString("type"));
                    try {
                        myChild.setRoutineAfterLoad(rs.getString("ges_routineAfterLoad"));
                    } catch (Exception e) {
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(smartAction.class.getName()).log(Level.SEVERE, null, ex);
            }
            myChildsArray.add(myChild.makeJSON());

        }// FINE CICLO PER OGNI CHILD

        try {
            FEconny.close();
        } catch (SQLException ex) {
            Logger.getLogger(smartAction.class.getName()).log(Level.SEVERE, null, ex);
        }

        JSONObject myJObj = new JSONObject();

        myJObj.put("ip", "0000");
        myJObj.put("TYPE", "wsChildsPrerequisites");
        myJObj.put("CHILDREN", myChildsArray);

        return myJObj;
    }

    public JSONObject WSRloadChild(childLink myChild, JSONObject connector) throws SQLException {
        System.out.println("\n\n\nCREO IL FORM DA VISUALIZZARE!!!!!!!!!!!! \n");
        System.out.println("WSRloadChild --> connector : " + connector.toString());
        //WebsocketResponse
        JSONObject itemjObj = new JSONObject();

        String Name = myChild.getRifChild();
        String ID = myChild.getRifChildID();
        String Type = "SMARTTABLE";
        String FatherKEYvalue = getJSONarg(connector, "selectedRowID");//connector.get("selectedRowID").toString();
        String FatherKEYtype = "VARCHAR";
        String Father = getJSONarg(connector, "formID");// connector.get("formID").toString();
        String FatherCopyTag = getJSONarg(connector, "copyTag");//connector.get("copyTag").toString();
        String InfoReceived = getJSONarg(connector, "tbs");//connector.get("tbs").toString();
        if (InfoReceived.length() < 4) {
            InfoReceived = getJSONarg(connector, "STC");
//            System.out.println("Sostituisco i tbs (vuoti) con i STC:");
        }
        String CopyTag = getJSONarg(connector, "copyTag");//connector.get("copyTag").toString();

//        System.out.println("Name:" + Name);
//        System.out.println("ID:" + ID);
//        System.out.println("Type:" + Type);
//        System.out.println("FatherKEYvalue:" + FatherKEYvalue);
//        System.out.println("FatherKEYtype:" + FatherKEYtype);
//        System.out.println("Father:" + Father);
//        System.out.println("FatherCopyTag:" + FatherCopyTag);
//        System.out.println("InfoReceived:" + InfoReceived);
//        System.out.println("CopyTag:" + CopyTag);
        smartForm mySmartForm = new smartForm(myChild.getRifChildID(), myParams, mySettings);
        mySmartForm.setName(Name);
        mySmartForm.setID(ID);
        mySmartForm.setType(Type);
        mySmartForm.setFatherKEYvalue(FatherKEYvalue);
        mySmartForm.setFatherKEYtype(FatherKEYtype);
        mySmartForm.setFather(Father);
        mySmartForm.setFatherCopyTag(FatherCopyTag);
        mySmartForm.setInfoReceived(InfoReceived);
        mySmartForm.setCopyTag(CopyTag);
        try {
            mySmartForm.setSubscriptionsToken(this.senderClient.getTokenAssigned());
        } catch (Exception e) {
            
        }

        // il keyValue è dato dalla riga EVIDENZIATA
        // però se sto cliccando un pulsante deve valere la riga del pulsante !!!
        // come ho implementato ?
//                                mySmartForm.setCurKEYvalue(selectedRowID);
//                                mySmartForm.setCurKEYtype(""); 
        mySmartForm.setSendToCRUD(InfoReceived);//connector.get("tbs").toString()); 
        mySmartForm.setLoadType("{\"type\":\"SMARTTABLE\",\"visualType\":\"FULLFORM\"}");
//        System.out.println("WSRloadChild--->>VADO IN SMARTTABLE, getVisualType:" + mySmartForm.getVisualType());

        //STO PER GENERARE IL FORM... controllo se ho un aroutine on load da eseguire prima
        mySmartForm.buildSchema();
//        System.out.println("WSRloadChild--->>Prima devo eseguire:" + mySmartForm.getGes_routineOnLoad());

        //*****************************************************
//        System.out.println("WSRloadChild--->>mySmartForm.getType():" + mySmartForm.getType());
        String action = "REFRESHFORM";
        String collapseFather = "false";

        if (mySmartForm.collapseFather != null && mySmartForm.collapseFather.equalsIgnoreCase("true")) {
            collapseFather = "true";
        }
//        System.out.println("WSRloadChild--->>mySmartForm.collapseFather:" + collapseFather);
        System.out.println(" action:" + action);
        String DATA = "";

        smartFormResponse myFormResponse = mySmartForm.paintForm();
        String fW = mySmartForm.formWidth;
        String fH = mySmartForm.formHeight;

        if (fW != null && fW.length() > 0) {
            fW = fW.replaceAll("px", "");
            fW = fW.trim();
        }
        if (fH != null && fH.length() > 0) {
            fH = fH.replaceAll("px", "");
            fH = fH.trim();
        }
//         System.out.println("TREE FORM SIZE:::::" + fW+" x "+fH);

        String htmlCode = myFormResponse.getHtmlCode();
        htmlCode = encodeURIComponent(htmlCode);

        if (mySmartForm.getType().equalsIgnoreCase("SMARTTREE")) {
            action = "POPULATETREE";
            DATA = mySmartForm.getFormResponse().getDataJSON().toString();
            DATA = encodeURIComponent(DATA);
        }
        String destDiv = "";

        String formID = getJSONarg(connector, "formID");
        String copyTag = getJSONarg(connector, "copyTag");

        if (myChild.getPosition() != null && myChild.getPosition().startsWith("@")) {
            String absolutePosition = myChild.getPosition().substring(1, myChild.getPosition().length());
            destDiv += absolutePosition;

        } else {
            destDiv += "CH-" + formID + "-" + copyTag + "-" + myChild.getPosition();
        }
        //System.out.println("destDiv: " + destDiv);

        JSONObject outPayload = new JSONObject();
        outPayload.put("ACTION", action);
        outPayload.put("DESTDIV", destDiv);
        outPayload.put("CODE", htmlCode);
        outPayload.put("DATA", DATA);
        outPayload.put("W", fW);
        outPayload.put("H", fH);
        outPayload.put("COLLAPSEFATHER", collapseFather);

        itemjObj.put("ip", "0000");
        itemjObj.put("TYPE", "wsResponse");
        itemjObj.put("payload", outPayload);

        return itemjObj;
    }

    private JSONObject WSRexecuteWSroutine(JSONObject connector) {

        String rifObj = connector.get("rifObj").toString();//"rifObj":"daRegistrare",
        System.out.println("WSRexecuteWSroutine rifObj:" + rifObj);
        System.out.println("connector:" + connector.toString());
//
        String formID = getJSONarg(connector, "formID");//"formID":"gestifa003",
        String copyTag = getJSONarg(connector, "copyTag");
        String keyValue = getJSONarg(connector, "keyValue");//"keyValue":"panelElement",
        String connectorSTC = getJSONarg(connector, "STC");//"keyValue":"panelElement",

        System.out.println(" formID: " + formID);
        System.out.println(" copyTag: " + copyTag);
        System.out.println(" keyValue: " + keyValue);
        System.out.println(" connectorSTC: " + connectorSTC);
        String actionParams = "";
        Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();

//        String SQLphrase = "";
//        System.out.println("VAdo in loadFORMparams");
//        smartForm myForm = loadFORMparams(connector);
//        System.out.println("done");
//        if (keyValue.equalsIgnoreCase("PANELelement")) {
//            formID = getJSONarg(connector, "rifForm");
//            myForm.setID(formID);
//            System.out.println(" formID: " + formID);
//        }
//        System.out.println("VAdo in buildSchema");
//        myForm.buildSchema();
//
//        System.out.println(" Schema oggetti: " + myForm.objects.size());
//        for (smartObject object : myForm.objects) {
//            System.out.println("FORM OBJECT:" + object.name);
//            if (object.name.equalsIgnoreCase(rifObj)) {
//                actionParams = object.getActionParams();
//                break;
//            }
//        }
//        //CERCO TRA I FORM OBJECTS
//        if (actionParams == null || actionParams == "") {
//            for (smartObject object : myForm.formObjects) {
//                System.out.println("FORM OBJECT:" + object.name);
//                if (object.name.equalsIgnoreCase(rifObj)) {
//                    actionParams = object.getActionParams();
//                    break;
//                }
//            }
//        }
        System.out.println("resolveAction--->actionParams per richiedente websocket routine (es da un button IN RIGA (non in panel!!!!)): " + actionParams.toString());
        if (actionParams != null && actionParams.length() > 2) {

        }

        try {
            FEconny.close();
        } catch (SQLException ex) {
            Logger.getLogger(smartAction.class.getName()).log(Level.SEVERE, null, ex);
        }

        JSONObject myJObj = new JSONObject();

        myJObj.put("ip", "0000");
        myJObj.put("TYPE", "WSktComm");
        System.out.println("Esco da WSRexecuteWSroutine: " + myJObj.toString());
        return myJObj;
    }

    private JSONObject WSRloadSecondaryForms(JSONObject connector) {

        String rifObj = connector.get("rifObj").toString();//"rifObj":"daRegistrare",
        System.out.println("WSRloadSecondaryForms rifObj:" + rifObj);
        System.out.println("connector:" + connector.toString());
//
        String formID = getJSONarg(connector, "formID");//"formID":"gestifa003",
        String copyTag = getJSONarg(connector, "copyTag");
        String keyValue = getJSONarg(connector, "keyValue");//"keyValue":"panelElement",
        String connectorSTC = getJSONarg(connector, "STC");//"keyValue":"panelElement",

        System.out.println(" formID: " + formID);
        System.out.println(" copyTag: " + copyTag);
        System.out.println(" keyValue: " + keyValue);
        System.out.println(" connectorSTC: " + connectorSTC);
        String actionParams = "";
        Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        String SQLphrase = "";
        System.out.println("VAdo in loadFORMparams");
        smartForm myForm = loadFORMparams(connector);
        System.out.println("done");
        if (keyValue.equalsIgnoreCase("PANELelement")) {
            formID = getJSONarg(connector, "rifForm");
            myForm.setID(formID);
            System.out.println(" formID: " + formID);
        }
        System.out.println("VAdo in buildSchema");
        myForm.buildSchema();

        System.out.println(" Schema oggetti: " + myForm.objects.size());
        for (smartObject object : myForm.objects) {
//            System.out.println("FORM OBJECT:" + object.name);
            if (object.name.equalsIgnoreCase(rifObj)) {
                actionParams = object.getActionParams();
                break;
            }
        }
        //CERCO TRA I FORM OBJECTS
        if (actionParams == null || actionParams == "") {
            for (smartObject object : myForm.formObjects) {
//                System.out.println("FORM OBJECT:" + object.name);
                if (object.name.equalsIgnoreCase(rifObj)) {
                    actionParams = object.getActionParams();
                    break;
                }
            }
        }

        System.out.println("resolveAction--->actionParams per richiedente secondary form (es da un button IN RIGA (non in panel!!!!)): " + actionParams.toString());
        if (actionParams != null && actionParams.length() > 2) {
            if (actionParams.startsWith("{")) {//singolo form da aprire
                JSONParser jsonParser = new JSONParser();
                String formToLoad = "";
                String destDiv = "";
                String paramsToSend = "";
                String sendToCrud = "";
                try {
                    jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(actionParams);
                    formToLoad = getJSONarg(jsonObject, "formToLoad");
                    destDiv = getJSONarg(jsonObject, "destDiv");
                    paramsToSend = getJSONarg(jsonObject, "paramsToSend");
                    sendToCrud = getJSONarg(jsonObject, "sendToCrud");

                    System.out.println("WSRloadSecondaryForms--->  paramsToSend: " + paramsToSend);
                    System.out.println("WSRloadSecondaryForms--->  sendToCrud: " + sendToCrud);
                    childLink myChild = new childLink();
                    myChild.position = destDiv;
                    myChild.rifChild = formToLoad;

                    try {
                        myChild.rifChild = myChild.rifChild.replaceAll("[\n\r]", "");
                    } catch (Exception e) {
                    }
                    if (myChild.position == null || myChild.position == "null" || myChild.position == "") {
                        myChild.position = "B";
                    }
                    System.out.println("DA CARICARE: " + myChild.getRifChild());
                    myChilds.add(myChild);
                } catch (ParseException ex) {
                    System.out.println("ERROR: " + ex.toString());
                }

            } else if (actionParams.startsWith("[")) {// matrice di form secondari
//                JSONObject childs = new JSONObject();

//                childs.put("secForms", actionParams);
                System.out.println("DA CARICARE[]: " + actionParams.toString());
                JSONParser parser = new JSONParser();
                JSONArray array;

//                JSONArray array = new JSONArray();
                try {
//                    array = (JSONArray) childs.get("secForms");
                    array = (JSONArray) parser.parse(actionParams);
                    ArrayList<JSONObject> list = new ArrayList<>();
                    for (int i = 0; i < array.size(); i++) {
                        list.add((JSONObject) array.get(i));
                        System.out.println("Aggiungo alla lista: " + array.get(i));
                    }
                    System.out.println("Totale lista: " + list.size());
                    for (JSONObject jsonObject : list) {
                        childLink myChild = new childLink();
                        myChild.position = getJSONarg(jsonObject, "destDiv");
                        myChild.rifChild = getJSONarg(jsonObject, "formToLoad");
                        try {
                            myChild.rifChild = myChild.rifChild.replaceAll("[\n\r]", "");
                        } catch (Exception e) {
                        }
                        if (myChild.position == null || myChild.position == "null" || myChild.position == "") {
                            myChild.position = "B";
                        }
                        myChilds.add(myChild);
                    }

                } catch (Exception e) {
                    System.out.println("Errore lista: " + e.toString());
                }
            }

        }
        System.out.println("DA CARICARE: " + myChilds.size() + " FORM.");
        JSONArray myChildsArray = new JSONArray();
        for (int child = 0; child < myChilds.size(); child++) {
            //CICLO PER OGNI CHILD
            childLink myChild = myChilds.get(child);
            SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_forms() + " WHERE `name`='" + myChild.getRifChild() + "' ";
            System.out.println("DA CARICARE SQLphrase: " + SQLphrase);
            try {
                Statement s;
                s = FEconny.createStatement();
                ResultSet rs = s.executeQuery(SQLphrase);
                while (rs.next()) {
                    myChild.setQuery(rs.getString("query"));
                    myChild.setRifChildID(rs.getString("ID"));
                    myChild.setRoutineBeforeLoad(rs.getString("ges_routineOnLoad"));
                    myChild.setType(rs.getString("type"));
                    try {
                        myChild.setRoutineAfterLoad(rs.getString("ges_routineAfterLoad"));
                    } catch (Exception e) {
                    }

                    myChilds.get(child).setQuery(myChild.getQuery());
                    myChilds.get(child).setRifChildID(myChild.getRifChildID());
                    myChilds.get(child).setRoutineBeforeLoad(myChild.getRoutineBeforeLoad());
                    myChilds.get(child).setType(myChild.getType());
                    myChilds.get(child).setRoutineAfterLoad(myChild.getRoutineAfterLoad());

                }
            } catch (SQLException ex) {
                Logger.getLogger(smartAction.class.getName()).log(Level.SEVERE, null, ex);
            }
            myChildsArray.add(myChild.makeJSON());

        }// FINE CICLO PER OGNI CHILD

        try {
            FEconny.close();
        } catch (SQLException ex) {
            Logger.getLogger(smartAction.class.getName()).log(Level.SEVERE, null, ex);
        }

        JSONObject myJObj = new JSONObject();

        myJObj.put("ip", "0000");
        myJObj.put("TYPE", "wsChildsPrerequisites");
//        myJObj.put("CHILDREN", myChildsArray);
        System.out.println("Esco da WSRloadSecondaryForms: " + myJObj.toString());
        return myJObj;
    }

    public String makeFormRefreshOrder(JSONObject connector) {
        System.out.println("\nSONO IN makeFormRefreshOrder.");
        String formID = "";
        String objID = "";
        String copyTag = "";
        String keyValue = "";
        String params = "";
        String valueKEY = "";
        String newValue = "";
        String fatherKEYvalue = "";
        String fatherKEYtype = "";
        String fatherForm = "";
        String fatherCopyTag = "";
        String tbs = "";
        String STC = "";
        try {
            formID = connector.get("formID").toString();
        } catch (Exception e) {
        }
        try {
            copyTag = connector.get("copyTag").toString();
        } catch (Exception e) {
        }
        try {
            objID = connector.get("rifObj").toString();
        } catch (Exception e) {
        }
        try {
            keyValue = connector.get("keyValue").toString();
        } catch (Exception e) {
        }
        try {
            valueKEY = connector.get("cellName").toString();
        } catch (Exception e) {
        }
        try {
            newValue = connector.get("newValue").toString();
        } catch (Exception e) {
        }
        try {
            fatherKEYvalue = connector.get("fatherKEYvalue").toString();
        } catch (Exception e) {
        }
        try {
            fatherKEYtype = connector.get("fatherKEYtype").toString();
        } catch (Exception e) {
        }
        try {
            fatherForm = connector.get("fatherForm").toString();
        } catch (Exception e) {
        }
        try {
            fatherCopyTag = connector.get("fatherCopyTag").toString();
        } catch (Exception e) {
        }
        try {
            tbs = connector.get("tbs").toString();
        } catch (Exception e) {
        }
        try {
            STC = connector.get("STC").toString();
        } catch (Exception e) {
        }

        String filtroLike = "";
        System.out.println("resolveAction--->EVENTO REFILTER ");
        String jfilter = "";
        String JcurPage = "";

        try {
            jfilter = connector.get("filter").toString();
        } catch (Exception e) {
        }
        try {
            JcurPage = connector.get("curPage").toString();
        } catch (Exception e) {
        }

        int curPage = 1;
        try {
            curPage = Integer.parseInt(JcurPage);
        } catch (Exception e) {

        }
        System.out.println("jfilter: " + jfilter);
        JSONArray array = new JSONArray();
        System.out.println("array.size: " + array.size());
        String newOrder = "";
        if (jfilter != null && jfilter.length() > 2) {
            try {
                array = (JSONArray) connector.get("filter");
                ArrayList<JSONObject> list = new ArrayList<>();

                for (int i = 0; i < array.size(); i++) {
                    list.add((JSONObject) array.get(i));
                    System.out.println("list.add: " + ((JSONObject) array.get(i)).get("field").toString());
                }
                if (list != null && list.size() > 0) {
                    Collections.sort(list, new MyJSONComparator());
                }

                for (JSONObject obj : list) {
                    String field = obj.get("field").toString();
                    String direction = obj.get("direction").toString();
                    String value = obj.get("value").toString();//testo inserito nel filtro

                    if (value != null && value.length() > 2) {
                        if (filtroLike.length() > 0) {
                            filtroLike += " AND ";
                        }
                        filtroLike += " " + field + " LIKE '%" + value + "%' ";
                    }

                    int pos = 0;
                    try {
                        pos = Integer.parseInt(obj.get("position").toString());
                    } catch (Exception e) {
                    }
                    System.out.println("POS:" + pos + ")Field:" + field + " ");
                    if (newOrder.length() > 0) {
                        newOrder += ", ";
                    }
                    newOrder += field;
                    if (!direction.equalsIgnoreCase("A")) {
                        newOrder += " DESC ";
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        smartForm myForm = loadFORMparams(connector);
        JSONObject loadTypeObj = new JSONObject();
        loadTypeObj.put("visualType", "DATAONLY");
        loadTypeObj.put("firstRow", 1);
        loadTypeObj.put("NofRows", 50);
        loadTypeObj.put("currentPage", curPage);
        loadTypeObj.put("visualFilter", "");
        myForm.setLoadType(loadTypeObj.toString());
        myForm.buildSchema();
        myForm.loadPagingInstructions();
        String newFilter = filtroLike;
        System.out.println("newFilter: " + newFilter);
        String newGroup = "";

        System.out.println("Come base per il filter uso la query già sostituita: " + myForm.queryUsed);

        smartQuery mySquery = new smartQuery(myForm.queryUsed, myForm.filteredElements, myForm.visualFilter);
        System.out.println("myForm.query: " + myForm.queryUsed);

        String preparedQuery = mySquery.regenerateQuery(newFilter, newOrder, newGroup, true, "AND", true, true);
        System.out.println("preparedQuery: " + preparedQuery);

        preparedQuery = browserArgsReplace(preparedQuery, myForm.fatherKEYvalue, myForm.sendToCRUD, myForm.toBeSent);
        System.out.println("preparedQuery replaced: " + preparedQuery);

        myForm.queryUsed = preparedQuery;

        System.out.println("makeFormRefreshOrder -> regenerateQuery-->Query used: " + myForm.queryUsed);

        smartFormResponse myFormResponse = myForm.paintDataTable();
        //                    smartFormResponse myFormResponse = myForm.paintForm();
        //                    System.out.println("Eseguito paintform: " + myFormResponse.HtmlCode);
        String htmlCode = myFormResponse.getHtmlCode();
        htmlCode = encodeURIComponent(htmlCode);

        return htmlCode;
    }

    public String makeFormRepaintOrder(JSONObject connector) {
        System.out.println("\nSONO IN makeFormRefreshOrder.");
        String formID = "";
        String objID = "";
        String copyTag = "";
        String keyValue = "";
        String params = "";
        String valueKEY = "";
        String newValue = "";
        String fatherKEYvalue = "";
        String fatherKEYtype = "";
        String fatherForm = "";
        String fatherCopyTag = "";
        String tbs = "";
        String STC = "";
        try {
            formID = connector.get("formID").toString();
        } catch (Exception e) {
        }
        try {
            copyTag = connector.get("copyTag").toString();
        } catch (Exception e) {
        }
        try {
            objID = connector.get("rifObj").toString();
        } catch (Exception e) {
        }
        try {
            keyValue = connector.get("keyValue").toString();
        } catch (Exception e) {
        }
        try {
            valueKEY = connector.get("cellName").toString();
        } catch (Exception e) {
        }
        try {
            newValue = connector.get("newValue").toString();
        } catch (Exception e) {
        }
        try {
            fatherKEYvalue = connector.get("fatherKEYvalue").toString();
        } catch (Exception e) {
        }
        try {
            fatherKEYtype = connector.get("fatherKEYtype").toString();
        } catch (Exception e) {
        }
        try {
            fatherForm = connector.get("fatherForm").toString();
        } catch (Exception e) {
        }
        try {
            fatherCopyTag = connector.get("fatherCopyTag").toString();
        } catch (Exception e) {
        }
        try {
            tbs = connector.get("tbs").toString();
        } catch (Exception e) {
        }
        try {
            STC = connector.get("STC").toString();
        } catch (Exception e) {
        }

        String filtroLike = "";
        System.out.println("resolveAction--->EVENTO REFILTER ");
        String jfilter = "";
        String JcurPage = "";

        try {
            jfilter = connector.get("filter").toString();
        } catch (Exception e) {
        }
        try {
            JcurPage = connector.get("curPage").toString();
        } catch (Exception e) {
        }

        int curPage = 1;
        try {
            curPage = Integer.parseInt(JcurPage);
        } catch (Exception e) {

        }
        System.out.println("jfilter: " + jfilter);
        JSONArray array = new JSONArray();
        System.out.println("array.size: " + array.size());
        String newOrder = "";
        if (jfilter != null && jfilter.length() > 2) {
            try {
                array = (JSONArray) connector.get("filter");
                ArrayList<JSONObject> list = new ArrayList<>();

                for (int i = 0; i < array.size(); i++) {
                    list.add((JSONObject) array.get(i));
                    System.out.println("list.add: " + ((JSONObject) array.get(i)).get("field").toString());
                }
                if (list != null && list.size() > 0) {
                    Collections.sort(list, new MyJSONComparator());
                }

                for (JSONObject obj : list) {
                    String field = obj.get("field").toString();
                    String direction = obj.get("direction").toString();
                    String value = obj.get("value").toString();//testo inserito nel filtro

                    if (value != null && value.length() > 2) {
                        if (filtroLike.length() > 0) {
                            filtroLike += " AND ";
                        }
                        filtroLike += " " + field + " LIKE '%" + value + "%' ";
                    }

                    int pos = 0;
                    try {
                        pos = Integer.parseInt(obj.get("position").toString());
                    } catch (Exception e) {
                    }
                    System.out.println("POS:" + pos + ")Field:" + field + " ");
                    if (newOrder.length() > 0) {
                        newOrder += ", ";
                    }
                    newOrder += field;
                    if (!direction.equalsIgnoreCase("A")) {
                        newOrder += " DESC ";
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        smartForm myForm = loadFORMparams(connector);
        JSONObject loadTypeObj = new JSONObject();
        loadTypeObj.put("visualType", "FULLFORM");
        loadTypeObj.put("firstRow", 1);
        loadTypeObj.put("NofRows", 50);
        loadTypeObj.put("currentPage", curPage);
        loadTypeObj.put("visualFilter", "");
        myForm.setLoadType(loadTypeObj.toString());
        myForm.buildSchema();
        myForm.loadPagingInstructions();
        String newFilter = filtroLike;
        System.out.println("newFilter: " + newFilter);
        String newGroup = "";

        System.out.println("Come base per il filter uso la query già sostituita: " + myForm.queryUsed);

        smartQuery mySquery = new smartQuery(myForm.queryUsed, myForm.filteredElements, myForm.visualFilter);
        System.out.println("myForm.query: " + myForm.queryUsed);

        String preparedQuery = mySquery.regenerateQuery(newFilter, newOrder, newGroup, true, "AND", true, true);
        System.out.println("preparedQuery: " + preparedQuery);

        preparedQuery = browserArgsReplace(preparedQuery, myForm.fatherKEYvalue, myForm.sendToCRUD, myForm.toBeSent);
        System.out.println("preparedQuery replaced: " + preparedQuery);

        myForm.queryUsed = preparedQuery;

        System.out.println("makeFormRefreshOrder -> regenerateQuery-->Query used: " + myForm.queryUsed);

        smartFormResponse myFormResponse = myForm.paintDataTable();
        //                    smartFormResponse myFormResponse = myForm.paintForm();
        //                    System.out.println("Eseguito paintform: " + myFormResponse.HtmlCode);
        String htmlCode = myFormResponse.getHtmlCode();
        htmlCode = encodeURIComponent(htmlCode);

        return htmlCode;
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

    public String browserArgsReplace(String query, String fatherKEYvalue, String sendToCRUD, String toBeSent) {
        System.out.println("\n>browserArgsReplace fatherKEYvalue===>>> " + fatherKEYvalue);
        if (query == null) {
            return null;
        }
        CRUDorder myCRUD = new CRUDorder(myParams, mySettings);
        myCRUD.setFatherKEYvalue(fatherKEYvalue);

        myCRUD.setSendToCRUD(sendToCRUD);
        myCRUD.setToBeSent(toBeSent);
        query = myCRUD.standardReplace(query, null);

//        System.out.println("\n>browserArgsReplace>>>> " + query);
        return query;

    }

    class MyJSONComparator implements Comparator<JSONObject> {

        @Override
        public int compare(JSONObject o1, JSONObject o2) {
            int v1 = Integer.parseInt(o1.get("position").toString());
            int v3 = Integer.parseInt(o2.get("position").toString());
            System.out.println("v1:" + v1 + "-->v3:" + v3);
            if (v1 < v3) {
                return 1;
            }
            if (v1 == v3) {
                return 0;
            } else {
                return -1;
            }

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

    public smartForm loadFORMparams(JSONObject connector) {
        // finora queste funzioni sono usate solo a partire da pulsanti in row dentro a uno SMARTTABLE. 
        //Non l'ho implementato da un panel
// System.out.println("### costriuisco form : " + connector.toString());
        smartForm myForm = new smartForm(connector.get("formID").toString(),
                myParams, mySettings);

        try {
//            System.out.println("### costriuto form : " + connector.get("formID").toString());
            try {
                myForm.setName(connector.get("formName").toString());
            } catch (Exception e) {
            }
            myForm.setID(connector.get("formID").toString());
            try {
                myForm.setCopyTag(connector.get("copyTag").toString());
            } catch (Exception e) {
            }
            try {
                myForm.setFatherKEYvalue(connector.get("fatherKEYvalue").toString());
            } catch (Exception e) {
            }
            try {
                myForm.setFatherKEYtype(connector.get("fatherKEYtype").toString());
            } catch (Exception e) {
            }

            try {
                myForm.setFather(connector.get("fatherForm").toString());
            } catch (Exception e) {
            }
            try {
                myForm.setFatherCopyTag(connector.get("fatherCopyTag").toString());
            } catch (Exception e) {
            }
            try {
                myForm.setFatherFilters("");
            } catch (Exception e) {
            }
            myForm.setLoadType("");
            try {
                myForm.setInfoReceived(connector.get("tbs").toString());
            } catch (Exception e) {
            }

            // il keyValue è dato dalla riga EVIDENZIATA
            // però se sto cliccando un pulsante deve valere la riga del pulsante !!!
            // come ho implementato ?
            try {
                myForm.setCurKEYvalue(connector.get("selectedRowId").toString());
            } catch (Exception e) {
            }

            try {
                myForm.setCurKEYtype("");
            } catch (Exception e) {
            }
            try {
                myForm.setSendToCRUD(connector.get("STC").toString());
            } catch (Exception e) {
            }
//            myForm.printVals();
        } catch (Exception e) {
            System.out.println("### ERRORR : " + e.toString());
        }
        return myForm;

    }

    public class childLink {

        String routineBeforeLoad;
        String routineAfterLoad;

        String routineBeforeNew;
        String routineAfterNew;

        String routineBeforeDelete;
        String routineAfterDelete;

        String position;
        String query;
        String rifChild;
        String rifChildID;
        String type;
        String conditions;

        public JSONObject makeJSON() {
            JSONObject myObj = new JSONObject();
            myObj.put("routineOnLoad", routineBeforeLoad);
            myObj.put("routineAfterLoad", routineAfterLoad);
            myObj.put("position", position);
            myObj.put("query", query);
            myObj.put("rifChild", rifChild);
            myObj.put("rifChildID", rifChildID);
            myObj.put("type", type);

            return myObj;
        }

        public String getRoutineBeforeLoad() {
            return routineBeforeLoad;
        }

        public void setRoutineBeforeLoad(String routineBeforeLoad) {
            this.routineBeforeLoad = routineBeforeLoad;
        }

        public String getRoutineBeforeNew() {
            return routineBeforeNew;
        }

        public void setRoutineBeforeNew(String routineBeforeNew) {
            this.routineBeforeNew = routineBeforeNew;
        }

        public String getRoutineAfterNew() {
            return routineAfterNew;
        }

        public void setRoutineAfterNew(String routineAfterNew) {
            this.routineAfterNew = routineAfterNew;
        }

        public String getRoutineBeforeDelete() {
            return routineBeforeDelete;
        }

        public void setRoutineBeforeDelete(String routineBeforeDelete) {
            this.routineBeforeDelete = routineBeforeDelete;
        }

        public String getRoutineAfterDelete() {
            return routineAfterDelete;
        }

        public void setRoutineAfterDelete(String routineAfterDelete) {
            this.routineAfterDelete = routineAfterDelete;
        }

        public String getRoutineAfterLoad() {
            return routineAfterLoad;
        }

        public void setRoutineAfterLoad(String routineAfterLoad) {
            this.routineAfterLoad = routineAfterLoad;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRifChildID() {
            return rifChildID;
        }

        public void setRifChildID(String rifChildID) {
            this.rifChildID = rifChildID;
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

}
