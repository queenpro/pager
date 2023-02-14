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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.ShowItForm;

public class SMARTtreeView {

    EVOpagerParams myParams;
    Settings mySettings;

    public SMARTtreeView(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
    }

//    {"type":"multilevel","lastLevel":"3","levels":[
//{"level":"0","type":"queryBased","query":"SELECT * FROM animali_famiglie WHERE 1"},
//{"level":"1","type":"queryBased","query":"SELECT * FROM animali_specie WHERE rifFamiglia = '###FAMIGLIA###'"},
//{"level":"2","type":"queryBased","query":"SELECT * FROM animali_razza WHERE rifSpecie = '###SPECIE###'"}]}
    public String buildBaseCode(int Level, ShowItForm myForm, ShowItObject curObj) {
        String HTMLcode = "";

        //-----SERVER & DB MAKER---------------------------------
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        ResultSet rs;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //1. rilevo le caratteristiche dell'oggetto     
        System.out.println("-TREEVIEW buildBaseCode CG.Value:" + curObj.CG.Value);
        System.out.println("-TREEVIEW buildBaseCode getSendToCRUD:" + myForm.getSendToCRUD());
        ArrayList<TreeNode> nodes = new ArrayList();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        String LVLSarray = null;
        TreeNode actualNode = new TreeNode();
        int numLvs = 1;
        if (curObj.CG.Value != null && curObj.CG.Value.length() > 0) {
            try {
                jsonObject = (JSONObject) jsonParser.parse(curObj.CG.Value);

                try {
                    numLvs = Integer.parseInt(jsonObject.get("lastLevel").toString());
                } catch (Exception ex) {
                    System.out.println("ERROR:" + ex.toString());
                }

                //-----LEVELS-------------------------------------------------------
                LVLSarray = jsonObject.get("levels").toString();
                System.out.println("LVLSarray:" + LVLSarray);

                if (LVLSarray != null && LVLSarray.length() > 0) {
                    JSONParser parser = new JSONParser();
                    Object obj;

                    obj = parser.parse(LVLSarray);
                    JSONArray array = (JSONArray) obj;

                    for (Object riga : array) {
                        TreeNode myNode = new TreeNode();
                        jsonObject = (JSONObject) jsonParser.parse(riga.toString());

                        try {
                            String lvl = jsonObject.get("level").toString();
                            int i = Integer.parseInt(lvl);
                            myNode.setLevel(i);
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());

                        }
                        try {
                            myNode.setQuery(jsonObject.get("query").toString());
                            System.out.println("QUERY LEVEL" + myNode.getLevel() + " = " + myNode.getQuery());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());

                        }
                        try {
                            myNode.setNodeType(jsonObject.get("type").toString());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());

                        }
                        try {
                            myNode.setTextField(jsonObject.get("textField").toString());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());

                        }
                        try {
                            myNode.setValueField(jsonObject.get("valueField").toString());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());

                        }
                        try {
                            myNode.setNodeName(jsonObject.get("name").toString());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());

                        }
                        try {
                            myNode.setChilds(jsonObject.get("childs").toString());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());

                        }
                        nodes.add(myNode);
                        if (myNode.level == Level) {
                            actualNode = myNode;
                        }
                    }
                }

            } catch (ParseException ex) {
                System.out.println("error in line 88" + ex.toString());
            }
        }

        System.out.println("actualNode:" + actualNode.getQuery());

        String CampoID = actualNode.getValueField();
        String CampoDescrizione = actualNode.getTextField();

        String SQLphrase = "";

        SQLphrase = browserArgsReplace(actualNode.getQuery(), curObj.CG.Value, myForm.getSendToCRUD());
        System.out.println("-TREEVIEW:" + SQLphrase);
        String rsValue = "";// sono in un panel per cui il valore è nullo
        String elenco = "";
        for (int jj = 0; jj < numLvs; jj++) {
            elenco += "<INPUT type=\"HIDDEN\" id=\"" + myForm.getID() + "-"
                    + myForm.getCopyTag() + "-"
                    + curObj.name
                    + "-L" + jj
                    + "-" + rsValue + "\" "
                    + "value=\"" + "" + "\">\n";
        }
        elenco += " <ul  data-role=\"treeview\" style=\"width: 300px; height: 200px; overflow: auto;\">\n";
        String idList = myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name;
        elenco += "<li "
                + "id = \"\" "
                + "> "
                + "<INPUT type=\"TEXT\" "
                + "id = \"" + idList + "\" "
                + "onclick=\"javascript:populateNestedList('" + myForm.getID() + "',"
                + "'" + myForm.getCopyTag() + "',"
                + "'" + curObj.name + "',"
                + "'ul-root',"
                + "'addNode',"
                + "'" + Level + "',"
                + ")\" "
                + "><a>ADD</a>"
                + "</li>";

        try {
            //======================================================
            Statement s = conny.createStatement();
            rs = s.executeQuery(SQLphrase);
            int lines = 0;
            while (rs.next()) {
                lines++;
                String idx = idList + "-"
                        + rs.getString(CampoID);
                elenco += "<li "
                        + "id = \"" + idx + "\" "
                        + "onclick=\"javascript:populateNestedList('" + myForm.getID() + "',"
                        + "'" + myForm.getCopyTag() + "',"
                        + "'" + curObj.name + "',"
                        + "'" + rs.getString(CampoID) + "',"
                        + "'expandNode',"
                        + "'" + Level + "'"
                        + ")\" ";
                int contextMenuAttivato = 0;
                if (contextMenuAttivato > 0) {
                    elenco += "oncontextmenu=\"javascript:rightClicked('" + myForm.getID() + "',"
                            + "'" + myForm.getCopyTag() + "',"
                            + "'" + curObj.name + "',"
                            + "'" + rs.getString(CampoID) + "',"
                            + "'expandNode',"
                            + "'" + Level + "',this, event"
                            + ")\" "
                            + "";
                }
                elenco += ""
                        + "type=\"" + actualNode.getNodeType() + "\" "
                        + "> "
                        + rs.getString(CampoDescrizione)
                        + "</li>";
            }
            elenco += " </ul>";

        } catch (SQLException ex) {
            System.out.println("error in line 154:" + ex.toString());

        }

        try {
            conny.close();
        } catch (SQLException ex) {
            System.out.println("error in onny.close( line 161:" + ex.toString());
        }
        HTMLcode += elenco;
        System.out.println("buildBaseCode HTMLcode:" + HTMLcode);

        return HTMLcode;
    }

    public String buildChildNodes(int Level, ShowItForm myForm, ShowItObject curObj, String destSpan, String event) {
        String HTMLcode = "";

        //-----SERVER & DB MAKER---------------------------------
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        ResultSet rs;

        System.out.println("OGGETTI IN FORM " + myForm.getID() + ":" + myForm.objects.size());
        for (int jj = 0; jj < myForm.objects.size(); jj++) {
            System.out.println("OGGETTO IN FORM:" + myForm.objects.get(jj).name);
            if (curObj.name.equalsIgnoreCase(myForm.objects.get(jj).name)) {
                curObj.CG.Value = myForm.objects.get(jj).CG.Value;
                break;
            }

        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //1. rilevo le caratteristiche dell'oggetto     
        System.out.println("-TREEVIEW CG.Value:" + curObj.CG.Value);
        System.out.println("-TREEVIEW getSendToCRUD:" + myForm.getSendToCRUD());
        System.out.println("-TREEVIEW Level:" + Level);
        ArrayList<TreeNode> nodes = new ArrayList();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        String LVLSarray = null;
        TreeNode actualNode = new TreeNode();

        if (curObj.CG.Value != null && curObj.CG.Value.length() > 0) {
            try {

                jsonObject = (JSONObject) jsonParser.parse(curObj.CG.Value);

                LVLSarray = jsonObject.get("levels").toString();
                System.out.println("LVLSarray:" + LVLSarray);

                if (LVLSarray != null && LVLSarray.length() > 0) {
                    JSONParser parser = new JSONParser();
                    Object obj;

                    obj = parser.parse(LVLSarray);
                    JSONArray array = (JSONArray) obj;

                    for (Object riga : array) {
                        TreeNode myNode = new TreeNode();
                        jsonObject = (JSONObject) jsonParser.parse(riga.toString());

                        try {
                            String lvl = jsonObject.get("level").toString();
                            int i = Integer.parseInt(lvl);
                            System.out.println("-TREEVIEW Level num :" + i);

                            myNode.setLevel(i);
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());
                        }
                        try {
                            myNode.setQuery(jsonObject.get("query").toString());
                            System.out.println("QUERY LEVEL" + myNode.getLevel() + " = " + myNode.getQuery());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());
                        }
                        try {
                            myNode.setNodeType(jsonObject.get("type").toString());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());
                        }
                        try {
                            myNode.setTextField(jsonObject.get("textField").toString());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());
                        }
                        try {
                            myNode.setValueField(jsonObject.get("valueField").toString());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());
                        }
                        try {
                            myNode.setNodeName(jsonObject.get("name").toString());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());
                        }
                        try {
                            myNode.setChilds(jsonObject.get("childs").toString());
                            System.out.println("CHILDS:" + myNode.getChilds());
                        } catch (Exception e) {
                            System.out.println("ERROR:" + e.toString());

                        }

                        nodes.add(myNode);
                        if (myNode.level == Level) {
                            actualNode = myNode;
                        }
                    }
                }
            } catch (ParseException ex) {
                System.out.println("error in line 88" + ex.toString());
            }
        }

        System.out.println("actualNode:" + actualNode.getQuery());

        String CampoID = actualNode.getValueField();
        String CampoDescrizione = actualNode.getTextField();

        String SQLphrase = "";
        SQLphrase = browserArgsReplace(actualNode.getQuery(), curObj.Content.Value, myForm.getSendToCRUD());

        System.out.println("-TREEVIEW:" + SQLphrase);

        String elenco = "";

        elenco += "<INPUT type=\"HIDDEN\" id=\"" + myForm.getID() + "-"
                + myForm.getCopyTag() + "-"
                + curObj.name + "-" + actualNode.nodeName + "\" value=\"" + "" + "\">\n";
        if (actualNode.childs == null || actualNode.childs.length() < 3) {
            actualNode.childs = "[]";
        }
        String rsp = "";
        rsp += "{\"tree\":\"" + event + "\","
                + "\"destSpan\":\"" + destSpan + "\","
                + "\"FORMID\":\"" + myForm.getID() + "\","
                + "\"INSTANCEID\":\"" + myForm.getCopyTag() + "\","
                + "\"OBJNAME\":\"" + curObj.getName() + "\","
                + "\"LEVEL\":\"" + Level + "\","
                + "\"type\":\"" + actualNode.nodeType + "\","
                + "\"childs\":" + actualNode.childs + ","
                + "\"leafs\":["
                + "";

        try {
            //======================================================
            Statement s = conny.createStatement();
            rs = s.executeQuery(SQLphrase);
            int lines = 0;
            while (rs.next()) {
                lines++;
                if (lines > 1) {
                    rsp += ", ";
                }
                rsp += "{\"descrizione\":\"" + rs.getString(CampoDescrizione) + "\","
                        + "\"ID\":\"" + rs.getString(CampoID) + "\"} ";

            }

            conny.close();
        } catch (SQLException ex) {
            System.out.println("error in line 5094" + ex.toString());
        }
        rsp += "]"
                + "}";

        HTMLcode += rsp;
        return HTMLcode;
    }

    public class TreeNode {

        String ID;
        int level;
        String nodeType;
        String nodeName;
        String descrizione;
        String query;
        String textField;
        String valueField;
        String childs;

        public String getChilds() {
            return childs;
        }

        public void setChilds(String childs) {
            this.childs = childs;
        }

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public String getTextField() {
            return textField;
        }

        public void setTextField(String textField) {
            this.textField = textField;
        }

        public String getValueField() {
            return valueField;
        }

        public void setValueField(String valueField) {
            this.valueField = valueField;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getNodeType() {
            return nodeType;
        }

        public void setNodeType(String nodeType) {
            this.nodeType = nodeType;
        }

        public String getDescrizione() {
            return descrizione;
        }

        public void setDescrizione(String descrizione) {
            this.descrizione = descrizione;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

    }

    public String browserArgsReplace(String query, String fatherKEYvalue, String sendToCRUD) {
        System.out.println("\n>browserArgsReplace fatherKEYvalue===>>> " + fatherKEYvalue);
        if (query == null) {
            return null;
        }
        CRUDorder myCRUD = new CRUDorder(myParams, mySettings);
        myCRUD.setFatherKEYvalue(fatherKEYvalue);

        myCRUD.setSendToCRUD(sendToCRUD);
        query = myCRUD.standardReplace(query, null);

        System.out.println("\n>browserArgsReplace->->-> " + query);
        return query;

    }

}
