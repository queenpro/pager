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

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.Database;
import REVOpager.EVOpagerDBconnection;
import REVOpager.EVOuser;
import REVOpager.Server;
import REVOpager.schema_column;
import static REVOwebsocketManager.WShandler.peers;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;
import models.CRUDorder;
import models.SelectList;
import models.ShowItObject;
import models.boundFields;
import models.gate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.ShowItForm;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class smartForm {
// <editor-fold defaultstate="collapsed" desc="DECLARATIONS">

    ResultSet DATArs;
    Server server;
    Database database;
    String ID;
    String copyTag;
    String name;
    String rifProject;
    String rifFrame;
    String serverURL;
    String databaseID;

    String formHeight;
    String formWidth;

    String position;
    String type;
    String leftCols;
    String rightCols;
    String bottomSpaces;
    String topSpaces;
    String father;
    String fatherFilters;
    String fatherKEYvalue;
    String fatherKEYtype;
    String fatherCopyTag;

    String curKEYvalue; // queste due servono in caso di repaint row
    String curKEYtype;

    String query;
    String queryMatrix;
    String queryUsed;
    String mainTable;
    String visualFilter;
    int firstRow;
    int rowsPerPage;
    int currentPage;
    int nofRows;
    String visualType;

    String userRights;
    smartObjRight formRightsRules;

    int layoutColumns;

    String filteredElements;
    String KEYfieldName;
    String KEYfieldType;

    String filterSequence;
    String loadType;
    int hasSearchFilter;
    String Label;
    String toBeSent;

    String infoReceived;

    int effectiveEnabled;
    String disableRules;
    String htmlPattern;
    String actualHtmlPattern;

    String rowBGcolor;
    String actualRowGBcolor;

    String complexSearchParams;
    String sendToCRUD;

    String ges_topBar;
    String ges_background;
    String ges_size;
    String ges_formPanel;
    String ges_routineOnLoad;
    String ges_triggers;

    ArrayList<boundFields> sentFieldList;
    ArrayList<boundFields> filterList;
    ArrayList<boundFields> boundFieldList;
    ArrayList<boundFields> rowValues;

    EVOpagerParams myParams;
    Settings mySettings;
    public ArrayList<smartObject> objects;
    public ArrayList<smartObject> formObjects;

    String directorParams;
    String childrenList;
    public smartFormResponse formResponse;

    String addRowPosition;
    String refreshOnAdd;
    String refreshOnUpdate;
    String updRowWhereClause;
    String showHeader;
    String showCounter;
    String showToggler;
    String bodyInitHidden;
    String collapseOnExpand;
    int maxRows;

    public String abstractTextCode;
    String advancedFiltered;
    ArrayList<schema_column> columns;

    String totalRowCode = "";
    String addingRowCode = "";
    String normalRowsCode = "";
    String pageSelectorCode = "";
    String headerCode = "";
    String rowsCode = "";
    String colsNamesCode = "";
    String dataOnlyCode = "";

    String divPrefix = "";
    String elementPrefix = "";
    String divTopBar = "";
    String divLeft = "";
    String divRight = "";
    String divBottom = "";
    String divBody = "";
    String attributes = "";
    String divExtR = "";
    String divExtB = "";
    String divIntRight = "";
    String outL = "";
    String outR = "";
    String divBodyUpDown = "";
    String mainQPtable = "";
    String fileTable = "";
    String mainBodyTable = "";

    String routineBeforeLoad = "";
    String routineAfterLoad = "";

    String routineBeforeNew = "";
    String routineAfterNew = "";

    String routineBeforeDelete = "";
    String routineAfterDelete = "";

    String routineBeforeUpdate = "";
    String routineAfterUpdate = "";

    boolean buildSchemaDone = false;
    int totalWidth;

    int righeScritte = 0;
    int rowsCounter = 0;
    int totalPages = 0;

    // </editor-fold>
    public smartForm(String id, EVOpagerParams xParams, Settings xSettings) {
        formResponse = new smartFormResponse();
        this.myParams = xParams;
        this.mySettings = xSettings;
        //-----SERVER & DB MAKER---------------------------------    
        this.server = new Server(mySettings);
        this.database = new Database(myParams, mySettings);
        //------------------------------------------------------- 
        this.ID = id;
//        System.out.println("smartForm->FORM DA COSTRUIRE ID: " + this.ID);
        this.objects = new ArrayList<smartObject>();
        this.formObjects = new ArrayList<smartObject>();
    }

    public smartForm(gate myGate, EVOpagerParams xParams, Settings xSettings) {
        formResponse = new smartFormResponse();
        this.myParams = xParams;
        this.mySettings = xSettings;
        //-----SERVER & DB MAKER---------------------------------    
        this.server = new Server(mySettings);
        this.database = new Database(myParams, mySettings);
        //------------------------------------------------------- 
        this.ID = myGate.getFormID();
        this.copyTag = myGate.copyTag;
//        System.out.println("smartForm->FORM DA COSTRUIRE da Gate: " + this.ID);
        this.objects = new ArrayList<smartObject>();
        this.formObjects = new ArrayList<smartObject>();
    }
// <editor-fold defaultstate="collapsed" desc="GETTERS & SETTERS">

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCopyTag() {
        return copyTag;
    }

    public void setCopyTag(String copyTag) {
        this.copyTag = copyTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRifProject() {
        return rifProject;
    }

    public void setRifProject(String rifProject) {
        this.rifProject = rifProject;
    }

    public String getRifFrame() {
        return rifFrame;
    }

    public void setRifFrame(String rifFrame) {
        this.rifFrame = rifFrame;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }

    public String getFormHeight() {
        return formHeight;
    }

    public void setFormHeight(String formHeight) {
        this.formHeight = formHeight;
    }

    public String getFormWidth() {
        return formWidth;
    }

    public void setFormWidth(String formWidth) {
        this.formWidth = formWidth;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLeftCols() {
        return leftCols;
    }

    public void setLeftCols(String leftCols) {
        this.leftCols = leftCols;
    }

    public String getRightCols() {
        return rightCols;
    }

    public void setRightCols(String rightCols) {
        this.rightCols = rightCols;
    }

    public String getBottomSpaces() {
        return bottomSpaces;
    }

    public void setBottomSpaces(String bottomSpaces) {
        this.bottomSpaces = bottomSpaces;
    }

    public String getTopSpaces() {
        return topSpaces;
    }

    public void setTopSpaces(String topSpaces) {
        this.topSpaces = topSpaces;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getFatherFilters() {
        return fatherFilters;
    }

    public void setFatherFilters(String fatherFilters) {
        this.fatherFilters = fatherFilters;
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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryMatrix() {
        return queryMatrix;
    }

    public void setQueryMatrix(String queryMatrix) {
        this.queryMatrix = queryMatrix;
    }

    public String getQueryUsed() {
        return queryUsed;
    }

    public void setQueryUsed(String queryUsed) {
        this.queryUsed = queryUsed;
    }

    public String getMainTable() {
        return mainTable;
    }

    public void setMainTable(String mainTable) {
        this.mainTable = mainTable;
    }

    public String getVisualFilter() {
        return visualFilter;
    }

    public void setVisualFilter(String visualFilter) {
        this.visualFilter = visualFilter;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNofRows() {
        return nofRows;
    }

    public void setNofRows(int nofRows) {
        this.nofRows = nofRows;
    }

    public String getVisualType() {
        return visualType;
    }

    public void setVisualType(String visualType) {
        this.visualType = visualType;
    }

    public String getUserRights() {
        return userRights;
    }

    public void setUserRights(String userRights) {
        this.userRights = userRights;
    }

    public smartObjRight getFormRightsRules() {
        return formRightsRules;
    }

    public int getLayoutColumns() {
        return layoutColumns;
    }

    public void setLayoutColumns(int layoutColumns) {
        this.layoutColumns = layoutColumns;
    }

    public String getFilteredElements() {
        return filteredElements;
    }

    public void setFilteredElements(String filteredElements) {
        this.filteredElements = filteredElements;
    }

    public String getKEYfieldName() {
        return KEYfieldName;
    }

    public void setKEYfieldName(String KEYfieldName) {
        this.KEYfieldName = KEYfieldName;
    }

    public String getKEYfieldType() {
        return KEYfieldType;
    }

    public void setKEYfieldType(String KEYfieldType) {
        this.KEYfieldType = KEYfieldType;
    }

    public String getFilterSequence() {
        return filterSequence;
    }

    public void setFilterSequence(String filterSequence) {
        this.filterSequence = filterSequence;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public int getHasSearchFilter() {
        return hasSearchFilter;
    }

    public void setHasSearchFilter(int hasSearchFilter) {
        this.hasSearchFilter = hasSearchFilter;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String Label) {
        this.Label = Label;
    }

    public String getToBeSent() {
        return toBeSent;
    }

    public void setToBeSent(String toBeSent) {
        this.toBeSent = toBeSent;
    }

    public String getInfoReceived() {
        return infoReceived;
    }

    public void setInfoReceived(String infoReceived) {
        this.infoReceived = infoReceived;
    }

    public int getEffectiveEnabled() {
        return effectiveEnabled;
    }

    public void setEffectiveEnabled(int effectiveEnabled) {
        this.effectiveEnabled = effectiveEnabled;
    }

    public String getDisableRules() {
        return disableRules;
    }

    public void setDisableRules(String disableRules) {
        this.disableRules = disableRules;
    }

    public String getHtmlPattern() {
        return htmlPattern;
    }

    public void setHtmlPattern(String htmlPattern) {
        this.htmlPattern = htmlPattern;
    }

    public String getActualHtmlPattern() {
        return actualHtmlPattern;
    }

    public void setActualHtmlPattern(String actualHtmlPattern) {
        this.actualHtmlPattern = actualHtmlPattern;
    }

    public String getRowBGcolor() {
        return rowBGcolor;
    }

    public void setRowBGcolor(String rowBGcolor) {
        this.rowBGcolor = rowBGcolor;
    }

    public String getActualRowGBcolor() {
        return actualRowGBcolor;
    }

    public void setActualRowGBcolor(String actualRowGBcolor) {
        this.actualRowGBcolor = actualRowGBcolor;
    }

    public String getComplexSearchParams() {
        return complexSearchParams;
    }

    public void setComplexSearchParams(String complexSearchParams) {
        this.complexSearchParams = complexSearchParams;
    }

    public String getSendToCRUD() {
        return sendToCRUD;
    }

    public void setSendToCRUD(String sendToCRUD) {
        this.sendToCRUD = sendToCRUD;
    }

    public String getGes_topBar() {
        return ges_topBar;
    }

    public void setGes_topBar(String ges_topBar) {
        this.ges_topBar = ges_topBar;
    }

    public String getGes_background() {
        return ges_background;
    }

    public void setGes_background(String ges_background) {
        this.ges_background = ges_background;
    }

    public String getGes_size() {
        return ges_size;
    }

    public void setGes_size(String ges_size) {
        this.ges_size = ges_size;
    }

    public String getGes_formPanel() {
        return ges_formPanel;
    }

    public void setGes_formPanel(String ges_formPanel) {
        this.ges_formPanel = ges_formPanel;
    }

    public String getGes_routineOnLoad() {
        return ges_routineOnLoad;
    }

    public void setGes_routineOnLoad(String ges_routineOnLoad) {
        this.ges_routineOnLoad = ges_routineOnLoad;
    }

    public String getGes_triggers() {
        return ges_triggers;
    }

    public void setGes_triggers(String ges_triggers) {
        this.ges_triggers = ges_triggers;
    }

    public ArrayList<boundFields> getSentFieldList() {
        return sentFieldList;
    }

    public void setSentFieldList(ArrayList<boundFields> sentFieldList) {
        this.sentFieldList = sentFieldList;
    }

    public ArrayList<boundFields> getFilterList() {
        return filterList;
    }

    public void setFilterList(ArrayList<boundFields> filterList) {
        this.filterList = filterList;
    }

    public ArrayList<boundFields> getBoundFieldList() {
        return boundFieldList;
    }

    public void setBoundFieldList(ArrayList<boundFields> boundFieldList) {
        this.boundFieldList = boundFieldList;
    }

    public ArrayList<boundFields> getRowValues() {
        return rowValues;
    }

    public void setRowValues(ArrayList<boundFields> rowValues) {
        this.rowValues = rowValues;
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

    public ArrayList<smartObject> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<smartObject> objects) {
        this.objects = objects;
    }

    public ArrayList<smartObject> getFormObjects() {
        return formObjects;
    }

    public void setFormObjects(ArrayList<smartObject> formObjects) {
        this.formObjects = formObjects;
    }

    public String getDirectorParams() {
        return directorParams;
    }

    public void setDirectorParams(String directorParams) {
        this.directorParams = directorParams;
    }

    public String getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(String childrenList) {
        this.childrenList = childrenList;
    }

    public smartFormResponse getFormResponse() {
        return formResponse;
    }

    public void setFormResponse(smartFormResponse formResponse) {
        this.formResponse = formResponse;
    }

    public String getAddRowPosition() {
        return addRowPosition;
    }

    public void setAddRowPosition(String addRowPosition) {
        this.addRowPosition = addRowPosition;
    }

    public String getRefreshOnAdd() {
        return refreshOnAdd;
    }

    public void setRefreshOnAdd(String refreshOnAdd) {
        this.refreshOnAdd = refreshOnAdd;
    }

    public String getRefreshOnUpdate() {
        return refreshOnUpdate;
    }

    public void setRefreshOnUpdate(String refreshOnUpdate) {
        this.refreshOnUpdate = refreshOnUpdate;
    }

    public String getUpdRowWhereClause() {
        return updRowWhereClause;
    }

    public void setUpdRowWhereClause(String updRowWhereClause) {
        this.updRowWhereClause = updRowWhereClause;
    }

    public String getShowHeader() {
        return showHeader;
    }

    public void setShowHeader(String showHeader) {
        this.showHeader = showHeader;
    }

    public String getShowCounter() {
        return showCounter;
    }

    public void setShowCounter(String showCounter) {
        this.showCounter = showCounter;
    }

    public String getAbstractTextCode() {
        return abstractTextCode;
    }

    public void setAbstractTextCode(String XabstractTextCode) {
        this.abstractTextCode = XabstractTextCode;
    }

    public String getAdvancedFiltered() {
        return advancedFiltered;
    }

    public void setAdvancedFiltered(String advancedFiltered) {
        this.advancedFiltered = advancedFiltered;
    }

    public String getShowToggler() {
        return showToggler;
    }

    public void setShowToggler(String showToggler) {
        this.showToggler = showToggler;
    }

    public String getBodyInitHidden() {
        return bodyInitHidden;
    }

    public void setBodyInitHidden(String bodyInitHidden) {
        this.bodyInitHidden = bodyInitHidden;
    }

    public String getRoutineBeforeLoad() {
        return routineBeforeLoad;
    }

    public void setRoutineBeforeLoad(String routineBeforeLoad) {
        this.routineBeforeLoad = routineBeforeLoad;
    }

    public String getRoutineAfterLoad() {
        return routineAfterLoad;
    }

    public void setRoutineAfterLoad(String routineAfterLoad) {
        this.routineAfterLoad = routineAfterLoad;
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

    // </editor-fold>
    public void buildSchema() {
//        System.out.println("-------->loadFormSettings() ");
//        System.out.println("-------->VisualType: "+this.getVisualType());
        loadFormSettings();
//        System.out.println("-------->getFormPanel() ");
//        System.out.println("-------->VisualType: "+this.getVisualType());
        getFormPanel(this.getGes_formPanel());
//        System.out.println("-------->loadBoundFieldList() ");
//        System.out.println("-------->VisualType: "+this.getVisualType());
        loadBoundFieldList();
//        System.out.println("-------->createColumnSchema() ");
//        System.out.println("-------->VisualType: "+this.getVisualType());
        createColumnSchema();
//        System.out.println("-------->buildObjectsArray() ");
//        System.out.println("-------->VisualType: "+this.getVisualType());
        buildObjectsArray();
//        System.out.println("-------->buildObjectsOriginList() ");
//        System.out.println("-------->VisualType: "+this.getVisualType());
        buildObjectsOriginList();
//        System.out.println("-------->prepareSQL() ");
//        System.out.println("-------->VisualType: "+this.getVisualType());
        prepareSQL(this.query);
        if (queryUsed == null) {
            queryUsed = "";
        }
        System.out.println("-------->FINE BUILD SCHEMA ");
//        System.out.println("-------->VisualType: "+this.getVisualType());
        this.buildSchemaDone = true;
    }

    public void loadFormSettings() {

//        System.out.println("loadFormSettings");
        //CARICO DATI DA DB   
        Connection FEconny = null;
        try {
            FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        } catch (Exception e) {
            System.out.println("errore in creazion econny: " + e.toString());
        }

        ResultSet rs;
        // cerca il FORM per nome e se non è compilato per ID
        try {

            Statement s = FEconny.createStatement();
            String SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_forms() + " WHERE `name`='" + this.name + "'";
            if (this.name == null || this.name == "") {
                SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_forms() + " WHERE `ID`='" + this.ID + "'";
            }

            System.out.println("buildSchema loadFormSettings SQLphrase:" + SQLphrase);
            rs = s.executeQuery(SQLphrase);
            int lines = 0;
            while (rs.next()) {
                lines++;
                try {
                    this.name = rs.getString("name");
                } catch (Exception ex) {
                    this.name = "";
                }
                try {
                    this.ID = rs.getString("ID");
                } catch (Exception ex) {
                    this.ID = "";
                }

                try {
                    this.rifProject = rs.getString("rifProject");
                } catch (Exception ex) {
                    this.rifProject = "";
                }

                try {
                    this.rifFrame = rs.getString("rifFrame");
                } catch (Exception ex) {
                    this.rifFrame = "";
                }
                try {
                    this.serverURL = rs.getString("serverURL");
                } catch (Exception ex) {
                    this.serverURL = "";
                }
                try {
                    this.databaseID = rs.getString("databaseID");
                } catch (Exception ex) {
                    this.databaseID = "";
                }
                try {
                    this.position = rs.getString("position");
                } catch (Exception ex) {
                    this.position = "";
                }
                try {
                    this.type = rs.getString("type");
                } catch (Exception ex) {
                    this.type = "";
                }
                //System.out.println("-TYPE:" + this.getType());
                try {
                    this.leftCols = rs.getString("leftCols");
                } catch (Exception ex) {
                    this.leftCols = "";
                }
                try {
                    this.rightCols = rs.getString("rightCols");
                } catch (Exception ex) {
                    this.rightCols = "";
                }
                try {
                    this.bottomSpaces = rs.getString("bottomSpaces");
                } catch (Exception ex) {
                    this.bottomSpaces = "";
                }
                try {
                    this.topSpaces = rs.getString("topSpaces");
                } catch (Exception ex) {
                    this.topSpaces = "";
                }

                try {
                    this.query = rs.getString("query");
                } catch (Exception ex) {
                    this.query = "";
                }
                this.queryMatrix = this.query;
                try {
                    this.mainTable = (rs.getString("mainTable"));
                } catch (Exception ex) {
                    this.mainTable = ("");
                }

                try {
                    this.userRights = (rs.getString("userRights"));
                } catch (Exception ex) {
                    this.userRights = ("");
                }

                try {
                    this.filteredElements = rs.getString("filteredElements");
                } catch (Exception ex) {
                    this.filteredElements = "";
                }
                try {
                    this.formHeight = rs.getString("height");
                } catch (Exception ex) {
                    this.formHeight = "";
                }
                try {
                    this.formWidth = rs.getString("width");
                } catch (Exception ex) {
                    this.formWidth = "";
                }

                try {
                    this.hasSearchFilter = rs.getInt("hasSearchFilter");
                } catch (Exception ex) {
                    this.hasSearchFilter = 0;
                }
                if (this.hasSearchFilter < 0) {
                    this.hasSearchFilter = 0;
                }
                try {
                    this.Label = rs.getString("Label");
                } catch (Exception ex) {
                    this.Label = "";
                }
                try {
                    this.toBeSent = rs.getString("toBeSent");
                } catch (Exception ex) {
                    this.toBeSent = "";
                }

                try {
                    this.disableRules = rs.getString("disableRules");
                } catch (Exception ex) {
                    this.disableRules = "";
                }
                try {
                    this.htmlPattern = rs.getString("htmlPattern");
                } catch (Exception ex) {
                    this.htmlPattern = "";
                }
                try {
                    this.rowBGcolor = rs.getString("rowBGcolor");
                } catch (Exception ex) {
                    this.rowBGcolor = "";
                }

                try {
                    this.ges_topBar = rs.getString("ges_topBar");
                } catch (Exception ex) {
                    this.ges_topBar = "";
                }
                try {
                    this.ges_formPanel = rs.getString("ges_formPanel");
                } catch (Exception ex) {
                    this.ges_formPanel = "";
                }
                // System.out.println(">>ges_topBarColor: " + ges_topBarColor);
                try {
                    this.ges_background = rs.getString("ges_background");
                } catch (Exception ex) {
                    this.ges_background = "";
                }
                try {
                    this.ges_size = rs.getString("ges_size");
                } catch (Exception ex) {
                    this.ges_size = "";
                }
                //  System.out.println("/////////////////////lettura da DB>>" + name + "\n>>ges_routineOnLoad: ");
                try {
                    this.ges_routineOnLoad = rs.getString("ges_routineOnLoad");
                    //System.out.println(">>ges_routineOnLoad: " + ges_routineOnLoad);
                    formResponse.setGes_routineOnLoad(ges_routineOnLoad);
                } catch (Exception ex) {
                    this.ges_routineOnLoad = "";
                }

                //System.out.println(">>htmlPattern: " + htmlPattern);
            }

            if (this.getCopyTag() == null || this.getCopyTag().equalsIgnoreCase("null") || this.getCopyTag().length() < 1) {
                this.setCopyTag("X");
            }
            formRightsRules = analyzeRightsRuleJson(userRights, null, null, 10);
            FEconny.close();

        } catch (SQLException ex) {

        }

    }

    public void createColumnSchema() {
        columns = new ArrayList<schema_column>();

        if (getMainTable() == null || getMainTable().equalsIgnoreCase("NULL")) {
            System.out.println("WARNING: MAIN TABLE NAME NOT COMPILED. TYPE:" + this.getType());
        } else {

            Connection schemaconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalSchema();

            Statement schemast;
            try {
                schemast = schemaconny.createStatement();

                // Statement localSt = localconny.createStatement();
                String SQLphrase = "SELECT * FROM COLUMNS\n"
                        + "           WHERE TABLE_NAME = '" + getMainTable() + "'\n"
                        + "             AND TABLE_SCHEMA = '" + this.database.getDbExtendedName() + "' ORDER BY ORDINAL_POSITION;";
                //       System.out.println("CREO SCHEMA DELLE COLONNE da information-Schema: SQLphrase=" + SQLphrase);

                ResultSet schemars = schemast.executeQuery(SQLphrase);
                while (schemars.next()) {
                    schema_column column = new schema_column();
                    try {
                        column.setCOLUMN_NAME(schemars.getString("COLUMN_NAME"));
                        column.setCOLUMN_DEFAULT(schemars.getString("COLUMN_DEFAULT"));
                        column.setCOLUMN_KEY(schemars.getString("COLUMN_KEY"));
                        column.setDATA_TYPE(schemars.getString("DATA_TYPE"));
                        column.setEXTRA(schemars.getString("EXTRA"));
                        column.setIS_NULLABLE(schemars.getString("IS_NULLABLE"));
                        column.setCOLUMN_TYPE(schemars.getString("COLUMN_TYPE"));
                        column.setORDINAL_POSITION(schemars.getInt("ORDINAL_POSITION"));
                        BigDecimal result = schemars.getBigDecimal("CHARACTER_MAXIMUM_LENGTH");
                        column.setCHARACTER_MAXIMUM_LENGTH(result == null ? null : result.toBigInteger());
                        column.setNUMERIC_PRECISION(schemars.getInt("NUMERIC_PRECISION"));
                        columns.add(column);
                    } catch (SQLException ex) {
                        Logger.getLogger(smartForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                schemaconny.close();
            } catch (SQLException ex) {
                Logger.getLogger(smartForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            String pKEY;
            String pKEYtype;

            //===CERCO LO SCHEMA PER INDIVIDUARE COLONNA PRIMARIA E ALTRE CARATTERISTICHE
            for (int jj = 0; jj < columns.size(); jj++) {
                //System.out.println("Colonna [" + jj + "] = '" + columns.get(jj).getCOLUMN_NAME() + "' -COLUMN_KEY:" + columns.get(jj).getCOLUMN_KEY() + " - EXTRA:" + columns.get(jj).getEXTRA());
                if (columns.get(jj).getCOLUMN_KEY() != null
                        && columns.get(jj).getCOLUMN_KEY().length() > 2
                        && columns.get(jj).getCOLUMN_KEY().substring(0, 3).equalsIgnoreCase("PRI")) {
                    //System.out.println("Colonna primaria");
                    pKEY = columns.get(jj).getCOLUMN_NAME();
                    this.setKEYfieldName(pKEY);

                    pKEYtype = "VARCHAR";
                    if (columns.get(jj).getDATA_TYPE().substring(0, 3).equalsIgnoreCase("INT")) {
                        pKEYtype = "INT";
                    }
                    if (columns.get(jj).getEXTRA().equalsIgnoreCase("auto_increment")) {
                        pKEYtype = "AUTOINCREMENT";
                    }
                    this.setKEYfieldType(pKEYtype);
//                    System.out.println("smartForm pKEY:" + pKEY + "   -   pKEYtype:" + pKEYtype);
                }
            }

            // System.out.println("schema colonne:");
////////            for (int jj = 0; jj < columns.size(); jj++) {
////////                 System.out.println("column:" + columns.get(jj).getCOLUMN_NAME() + " . KEY:" + columns.get(jj).getCOLUMN_KEY() + "  - extra:" + columns.get(jj).getEXTRA());
////////            }
        }

    }

    public void getFormPanel(String XformPanel) {
        /*
        Carica le settings inserite nel JSON nel campo formPanel
         */
        if (XformPanel != null && XformPanel.length() > 4) {
            String formPanel = "{\"formPanel\":" + XformPanel + "}";
            System.out.println("formPanel:" + this.getGes_formPanel());

//            System.out.println("formPanel:" + formPanel);
            JSONObject jsonObject = new JSONObject();
            JSONParser jsonParser = new JSONParser();
            ArrayList<smartForm.lockRule> lockRules = new ArrayList<smartForm.lockRule>();
            int tempRight = 0;

            boolean limitUp = false;
            boolean limitDown = false;
            String infotype = "";
            try {
                jsonObject = (JSONObject) jsonParser.parse(formPanel);
                String TRIGGERSarray = jsonObject.get("formPanel").toString();
                if (TRIGGERSarray != null && TRIGGERSarray.length() > 0) {
                    JSONParser parser = new JSONParser();
                    JSONArray array = (JSONArray) parser.parse(TRIGGERSarray);
                    this.addRowPosition = ("bottom");
                    this.refreshOnAdd = ("");
                    this.refreshOnUpdate = ("");
                    this.updRowWhereClause = ("");
                    this.showHeader = ("");
                    this.advancedFiltered = ("");
                    this.bodyInitHidden = ("");
                    this.showToggler = ("");
                    this.collapseOnExpand = ("");
                    for (Object riga : array) {
                        this.showCounter = ("");
                        lockRule myRule = new lockRule();
                        jsonObject = (JSONObject) jsonParser.parse(riga.toString());
                        try {
                            infotype = (jsonObject.get("infoType").toString());
                        } catch (Exception e) {
                            infotype = ("");
                        }
                        if (infotype.equalsIgnoreCase("panelSetting")) {
                            try {
                                this.addRowPosition = (jsonObject.get("addRowPosition").toString());
                            } catch (Exception e) {

                            }
                            try {
                                this.refreshOnAdd = (jsonObject.get("refreshOnAdd").toString());
                            } catch (Exception e) {

                            }
                            try {
                                this.refreshOnUpdate = (jsonObject.get("refreshOnUpdate").toString());
                                System.out.println("\nrefreshOnUpdate:" + this.refreshOnUpdate);

                            } catch (Exception e) {

                            }
                            try {
                                this.layoutColumns = Integer.parseInt(jsonObject.get("cols").toString());
//                                System.out.println("\nIMPOSTATO LAYOUT COLUMNS:" + this.layoutColumns);
                            } catch (Exception e) {
                            }

                            try {
                                this.updRowWhereClause = (jsonObject.get("updRowWhereClause").toString());
                            } catch (Exception e) {

                            }
                            try {
                                this.showHeader = (jsonObject.get("showHeader").toString());
                            } catch (Exception e) {

                            }
//                            System.out.println("\nRETREIVED showHeader:" + this.showHeader);
                            try {
                                this.showCounter = (jsonObject.get("showCounter").toString());
                            } catch (Exception e) {
                            }
                            System.out.println("\nRETREIVED showCounter:" + this.showCounter);
                            try {
                                this.advancedFiltered = (jsonObject.get("advancedFiltered").toString());
                            } catch (Exception e) {

                            }
                            try {
                                this.showToggler = (jsonObject.get("showToggler").toString());
                            } catch (Exception e) {
                            }
                            try {
                                this.bodyInitHidden = (jsonObject.get("bodyInitHidden").toString());
                            } catch (Exception e) {
                            }
                            try {
                                this.collapseOnExpand = (jsonObject.get("collapseOnExpand").toString());
                            } catch (Exception e) {
                            }
                            try {
                                this.maxRows = Integer.parseInt(jsonObject.get("maxRows").toString());
//                                System.out.println("\nIMPOSTATO maxRows:" + this.maxRows);
                            } catch (Exception e) {
                            }
                        } else if (infotype.equalsIgnoreCase("routinesOnEvent")) {
                            try {
                                this.routineBeforeLoad = (jsonObject.get("routineBeforeLoad").toString());
                            } catch (Exception e) {
                            }
                            try {
                                this.routineBeforeNew = (jsonObject.get("routineBeforeNew").toString());
                            } catch (Exception e) {
                            }
                            try {
                                this.routineBeforeDelete = (jsonObject.get("routineBeforeDelete").toString());
                            } catch (Exception e) {
                            }
                            try {
                                this.routineBeforeUpdate = (jsonObject.get("routineBeforeUpdate").toString());
                            } catch (Exception e) {
                            }
                            try {
                                this.routineAfterLoad = (jsonObject.get("routineAfterLoad").toString());
                            } catch (Exception e) {
                            }
                            try {
                                this.routineAfterNew = (jsonObject.get("routineAfterNew").toString());
                            } catch (Exception e) {
                            }
                            try {
                                this.routineAfterDelete = (jsonObject.get("routineAfterDelete").toString());
                            } catch (Exception e) {
                            }
                            try {
                                this.routineAfterUpdate = (jsonObject.get("routineAfterUpdate").toString());
                            } catch (Exception e) {
                            }
                        }else if (infotype.equalsIgnoreCase("autolinks")) {
                            
                        }
                    }//end FOR
                }
            } catch (ParseException ex) {
            }
        }
    }

    private void buildObjectsArray() {
        Connection FEconny = null;
        try {
            FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        } catch (Exception e) {
            System.out.println(" -buildSchema- ERROR connecting FEdb:" + myParams.getCKprojectName());
        }
        ResultSet rs;
        totalWidth = 0;
        // cerca il FORM per nome e se non è compilato per ID
        try {
            Statement s = FEconny.createStatement();

            // <editor-fold defaultstate="collapsed" desc="COSTRUISCO ARRAY OGGETTI">                   
//            System.out.println("smartForm_buildSchema_COSTRUISCO ARRAY OGGETTI");
            this.objects = new ArrayList<smartObject>();
            String SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_objects() + " WHERE `rifForm`='" + this.ID + "' ORDER BY position";
//            System.out.println("CARICO CARATTERISTICHE OGGETTO:" + SQLphrase);
            rs = s.executeQuery(SQLphrase);
            int lines = 0;
            int foundKey = 0;
            Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
            EVOuser myUser;
//            try {
            myUser = new EVOuser(myParams, mySettings);
            myUser.setTABLElinkUserGroups("archivio_correlazioni");
            myUser.setFIELDlinkUserGroupsRifOperatore("partAvalue");
            myUser.setFIELDlinkUserGroupsRifGruppo("partBvalue");

            myUser.setTABLEgruppi("archivio_operatoriGruppi");
            myUser.setFIELDGruppiIDgruppo("IDgruppo");
            myUser.setTABLEoperatori("archivio_operatori");
            myUser.setFIELDoperatoriID("ID");
            while (rs.next()) {
                try {
                    lines++;
//                    System.out.println("OGGETTO " + lines + ":" + rs.getString("name"));

                    smartObject myObject = new smartObject("new");
                    //==========================
                    myObject.loadFromDB(rs); // carico i valori da GES
                    //==========================
//                    System.out.println("OGGETTO:" + myObject.getName());
//-----------------------------------------------------------------------------------      

                    // <editor-fold defaultstate="collapsed" desc="VALUTO VISIBLE">        
//ATTENZIONE actuallyVisible si riferische al valore (C) dell'oggetto (VISIBLE)
// in realtà mi serve SOLO per decidere se creare la colonna in adding row e in totalrow
                    //System.out.println("VALUTO PERMESSI OGGETTO:" + myObject.getName() + " -> " + myObject.getVisible() + " .");
                    if (myObject.getVisible() != null && !myObject.getVisible().equalsIgnoreCase("DEFAULT:TRUE")) {
                        myObject.setActuallyVisible(0);
                        //System.out.println("EVOUSER dice:" + myUser.getActualRight(myObject.getVisible(), null) + " .");
                        myObject.setActuallyVisible(myUser.getActualRightAdvanced(myObject.getVisible(), null, accountConny));

                    } else {
                        myObject.setActuallyVisible(1);
                    }

                    if (myObject.getActuallyVisible() > 0) {
                        int objW = 0;
                        try {
                            objW = Integer.parseInt(myObject.C.getWidth().replace("px", ""));
                            if (objW < 0) {
                                objW = 0;
                                myObject.C.setWidth("0");
                            }
                        } catch (Exception e) {
                            objW = 50;
                            myObject.C.setWidth("20");
                        }
//                        System.out.println("OGGETTO " + myObject.name + " :" + myObject.CG.Type);
                        if (this.type != null && !this.type.equalsIgnoreCase("MLS") && !myObject.CG.Type.equalsIgnoreCase("FORMBUTTON")) {
                            totalWidth += objW;
                        }
                    }
                    // </editor-fold> 
// <editor-fold defaultstate="collapsed" desc="ASSEGNO RIGHS DI BASE DELL'OGGETTO">                    
                    myObject.objRights = analyzeRightsRuleJson(myObject.getVisible(), null, accountConny, 20);
                    // </editor-fold>                             

// <editor-fold defaultstate="collapsed" desc="VALUTO MODIFIABLE">           
                    //System.out.println(myObject.getName()+" - WIDTH:" + myObject.C.getWidth() + " .");
                    if (myObject.CG.getType().equalsIgnoreCase("FIELD")) {
//System.out.println("Analizzo il campo da aggiungere:" + myObject.getName() + " ."+myObject.C.getType());
//                        System.out.println(" CERCO  PRIMARIO AUTOCOMPILANTE .\n");
                        for (int jj = 0; jj < columns.size(); jj++) {

                            //System.out.println(myObject.getName()+" - " + columns.get(jj).getCOLUMN_NAME() + " .");
                            if (myObject.getName().equalsIgnoreCase(columns.get(jj).getCOLUMN_NAME())
                                    && (columns.get(jj).getCOLUMN_KEY().equalsIgnoreCase("PRI")
                                    || columns.get(jj).getCOLUMN_KEY().equalsIgnoreCase("UNI"))) {
                                foundKey++;
                                setKEYfieldName(columns.get(jj).getCOLUMN_NAME());
                                setKEYfieldType(columns.get(jj).getCOLUMN_TYPE());
                                myObject.setPrimary(columns.get(jj).getCOLUMN_KEY());
                                myObject.setAutoCompiled(columns.get(jj).getEXTRA());
                                //  System.out.println("1153 TROVATA KEYfieldName:" + KEYfieldName);
                                if (!myObject.getAutoCompiled().equalsIgnoreCase("AUTO_INCREMENT")
                                        && (myObject.getDefaultValue() == null || myObject.getDefaultValue() == "")) {
                                    myObject.setAddingRow_enabled(1);

                                } else {
                                    myObject.Content.setPrimaryFieldAutocompiled(true);
                                    myObject.setAddingRow_enabled(0);
//                                    System.out.println(myObject.getName() + " - PRIMARIO AUTOCOMPILANTE .\n");
                                }

                                break;
                            }
                        }
                        int presenteNeiBound = 0;

                        if (boundFieldList != null && boundFieldList.size() > 0) {
                            for (int jj = 0; jj < boundFieldList.size(); jj++) {
                                if (lines == 1) {
                                    // System.out.println("boundFieldList " + jj + "]" + boundFieldList.get(jj).getMarker());
                                }
//                                System.out.println("AGGIUNGO OGGETTO:" + myObject.getName());
                                if (boundFieldList.get(jj).getMarker().equalsIgnoreCase(myObject.getName())) {
                                    boundFieldList.get(jj).setPresent(true);
//                                    System.out.println("IMPOSTATO COME PRESENTE:" + myObject.getName());
                                    presenteNeiBound++;
                                }
                            }
                        }
                        if (presenteNeiBound == 0) {
//                            System.out.println("OGGETTO:" + myObject.getName() + " NON FA PARTE DEI BOUNDED");
                        }
                    }

//===================================================================     
                    //System.out.println("AGGIUNGO OGGETTO:" + myObject.getName());
                    if (myObject.CG.getType() != null
                            && myObject.CG.getType().equalsIgnoreCase("FORMBUTTON")) {
                        myObject.Content.setThisRowModifiable(1);// altrimenti il pulsante non si vede se i permessi sono inferiori
                        String header = "";
                        header = myObject.labelHeader;
                        myObject.ValueToWrite = header;
                        this.formObjects.add(myObject);
                        // System.out.println("AGGIUNGO OGGETTO FORMBUTTON:" + myObject.getName());
                    } else {
                        this.objects.add(myObject);
//                        System.out.println("AGGIUNGO OGGETTO " + myObject.CG.getType() + ":" + myObject.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Errore utente..." + e.toString());
                }
            }
            accountConny.close();

//===================================================================  
//===================================================================        
// <editor-fold defaultstate="collapsed" desc="COMPLETO INFORMAZIONI OGGETTI"> 
            //System.out.println("COMPLETO INFORMAZIONI OGGETTI" );
            if (this.type != null
                    && !this.type.equalsIgnoreCase("SINGLEROWFORM")
                    && !this.type.equalsIgnoreCase("INSERTROWFORM")
                    && totalWidth > 0) {

                if (showCounter == null || !showCounter.equalsIgnoreCase("FALSE")) {
                    totalWidth += 40;
                }
                if (formRightsRules.canDelete > 0) {
                    totalWidth += 20;
                }

                int formW = totalWidth;
                // System.out.println("SET  by DB WIDTH:" + this.getFormWidth() + " . ");
                try {
                    formW = Integer.parseInt(this.getFormWidth().replace("px", ""));
                    if (formW < totalWidth) {
                        formW = (int) ((totalWidth / 100) * 110);
                    }
                } catch (Exception e) {
                    formW = totalWidth;
                }
                //  System.out.println("TOTAL WIDTH:" + formW + "px .\n\n");
                this.setFormWidth("" + formW + "px");
            } else {
                // System.out.println("PANNELLO SINGLEROWFORM... WIDTH:" + this.getFormWidth() + " .\n\n");
            }

//=================================================================================================================
            // se rimangono campi filtrati ma non presenti 
            // li aggiungo come invisibili e li precompilo con il default value
            filterSequence = "";// in ADD il campo 'rifProject' si compila con il valore del DOM 'projectID'
            for (int kk = 0; kk < boundFieldList.size(); kk++) {
//                System.out.println(">>boundFieldList.get(" + kk + ").getMarker():" + boundFieldList.get(kk).getMarker());
//                System.out.println(">>boundFieldList.get(" + kk + ").getValue():" + boundFieldList.get(kk).getValue());

                if (!boundFieldList.get(kk).isPresent()) {
//                    System.out.println("OGGETTO:" + boundFieldList.get(kk).getMarker() + " non è presente: lo creo.");
                    // creo un nuovo oggetto con il campo descritto qui, precompilato con il valore indicato ma invisibile
                    smartObject myObject = new smartObject("new");
                    myObject.setID(boundFieldList.get(kk).getMarker());
                    myObject.setName(boundFieldList.get(kk).getMarker());
                    myObject.CG.setType("FIELD");
                    myObject.CG.setValue(boundFieldList.get(kk).getValue());//
                    myObject.C.setType("LABEL");
                    myObject.Content.setType("TEXT");
                    myObject.setAddingRow_enabled(0);
                    myObject.C.setVisible(false);
                    for (int jj = 0; jj < columns.size(); jj++) {
                        // System.out.println(myObject.getName() + " - " + columns.get(jj).getCOLUMN_NAME() + " .");
                        if (myObject.getName().equalsIgnoreCase(columns.get(jj).getCOLUMN_NAME()) //&& columns.get(jj).getCOLUMN_KEY().equalsIgnoreCase("PRI")
                                ) {
                            if (columns.get(jj).getCOLUMN_KEY().equalsIgnoreCase("PRI")) {

                                foundKey++;
                                setKEYfieldName(columns.get(jj).getCOLUMN_NAME());
                                setKEYfieldType(columns.get(jj).getCOLUMN_TYPE());
                                myObject.setPrimary(columns.get(jj).getCOLUMN_KEY());
                                //System.out.println("TROVATA KEYfieldName:"+KEYfieldName);  
                            }
                            myObject.Content.setType(columns.get(jj).getCOLUMN_TYPE());
                            myObject.setAutoCompiled(columns.get(jj).getEXTRA());
                            break;
                        }
                    }
                    this.objects.add(myObject);
                }

                if (filterSequence != null && filterSequence.length() > 0) {
                    filterSequence += ";";
                }
                filterSequence += boundFieldList.get(kk).getMarker() + "=" + boundFieldList.get(kk).getValue();
//                System.out.println(">>filterSequence:" + filterSequence);
            }

            FEconny.close();

        } catch (SQLException ex) {
            System.out.println("error in line s1247");
        }

    }

    private void loadBoundFieldList() {
        // System.out.println("ShowItForm_buildSchema_CARICO BOUND FIELD LIST");
        String formAdderArgs = "";
        boundFieldList = new ArrayList<>();
        // System.out.println("ANALIZZO fatherFilters per vedere se devo eseguire sostituzioni nella query");
        try {
            if (this.fatherFilters != null && this.fatherFilters.length() > 4) {
                //System.out.println("Query before " + this.query);
                // System.out.println("Carico fatherFilters " + this.fatherFilters);
                // es:  Macchina=TRZ_08;Coda=78

                // non so perchè sottopongo gli stessi filters a sostituzione standard (es. NOW, KEY ecc)
                this.fatherFilters = browserArgsReplace(this.fatherFilters);
                // System.out.println("diventa: " + this.fatherFilters);

                //SPLITTO I FILTERS
                String[] items = this.fatherFilters.split(";");
                List<String> itemList = Arrays.asList(items);

                for (int jj = 0; jj < itemList.size(); jj++) {
                    // System.out.println(">> " + itemList.get(jj).toString());
                    String[] couple = itemList.get(jj).split("=");
                    List<String> couples = Arrays.asList(couple);
                    // System.out.println("____" + couples.get(0).toString() + " == " + couples.get(1).toString());
                    String replaced = "[" + couples.get(0).toString() + "]";
                    String replacer = couples.get(1).toString();
                    // System.out.println("replaced:" + replaced + " == replacer:" + replacer);
                    if (formAdderArgs.length() > 0) {
                        formAdderArgs += "|";
                    }
                    formAdderArgs += replaced + "=" + replacer;

                    if (replaced != null && replacer != null) {
                        this.query = this.query.replace(replaced, replacer);
                    }
                }
                // System.out.println("\n\nQuery after " + this.query);
            }
        } catch (Exception e) {
            System.out.println("Error in step 4: " + e.toString());
        }
    }

    public void loadPagingInstructions() {

//        System.out.println("\n[]loadType : " + loadType);
        if (loadType != null) { //questi parametri arrivano dal browser
            if (loadType.startsWith("[") || loadType.startsWith("{")) {
//                System.out.println("loadPagingInstructions loadType IS A JSON:this.loadType=" + this.loadType);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) jsonParser.parse(loadType);
                    try {
                        this.setVisualType(jsonObject.get("visualType").toString());
                    } catch (Exception e) {
                    }
                    try {
                        this.setFirstRow(Integer.parseInt(jsonObject.get("firstRow").toString()));
                    } catch (Exception e) {
                    }
                    try {
                        this.setNofRows(Integer.parseInt(jsonObject.get("NofRows").toString()));
                    } catch (Exception e) {
                    }
                    try {
                        this.setCurrentPage(Integer.parseInt(jsonObject.get("currentPage").toString()));
                    } catch (Exception e) {
                    }
                    try {
                        this.setVisualFilter(jsonObject.get("visualFilter").toString());
                    } catch (Exception e) {
                    }

                } catch (ParseException ex) {
                }

            } else {

                System.out.println("loadPagingInstructions  loadType IS NOT A JSON::this.loadType=" + this.loadType);
                String[] couple = this.loadType.split(";");
                List<String> couples = Arrays.asList(couple);

                // sintassi--> type; prima riga ; numero righe (0=tutte); filtri
                try {

                    if (couples.size() > 0) {
                        this.setVisualType(couples.get(0).toString());
                    }
                    if (couples.size() > 1 && couples.get(1) != null) {
                        this.setFirstRow(Integer.parseInt(couples.get(1).toString()));
                    }
                    if (couples.size() > 2 && couples.get(2) != null) {
                        this.setNofRows(Integer.parseInt(couples.get(2).toString()));
                    }
                    if (couples.size() > 3 && couples.get(3) != null) {
                        this.setCurrentPage(Integer.parseInt(couples.get(3).toString()));
                    }
                    if (couples.size() > 4 && couples.get(4) != null) {
                        this.setVisualFilter(couples.get(4).toString());
                    }
                    //  System.out.println("\n[]setCurrentPage : " + currentPage);
                } catch (Exception e) {
                    System.out.println("1517_error:" + e);
                }
            }
        }
    }

    public void buildObjectsOriginList() {

        // se ci sono SELECTLIST non specifiche per riga, le precarico
        for (int obj = 0; obj < this.objects.size(); obj++) {
            if ((this.objects.get(obj).C.getType().equalsIgnoreCase("SELECT")
                    || this.objects.get(obj).C.getType().equalsIgnoreCase("MARKER")
                    || this.objects.get(obj).C.getType().equalsIgnoreCase("RADIOBUTTON"))
                    && this.objects.get(obj).CG.getType().equalsIgnoreCase("FIELD")) {
                String oQuery = this.objects.get(obj).Origin.getQuery();
                System.out.println(" this.sendToCRUD:" + this.sendToCRUD);
                oQuery = browserArgsReplace(oQuery);
                System.out.println("DOPO:" + oQuery);
                String oLabelField = this.objects.get(obj).Origin.getLabelField();
                // se la oLabelField contiene diversi campi....
                //  System.out.println("select(or radio) FIELD: this.objects.get(obj).Origin.getLabelField()="+ this.objects.get(obj).Origin.getLabelField());               
                String oValueField = this.objects.get(obj).Origin.getValueField();
                //  System.out.println("select(or radio) FIELD: this.objects.get(obj).Origin.getValueField()="+ this.objects.get(obj).Origin.getValueField());
                String oValueFieldType = this.objects.get(obj).Origin.getValueFieldType();
                SelectList myList = new SelectList(myParams, mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                myList.getList();
                this.objects.get(obj).Origin.setSelectList(myList);
            } else if ((this.objects.get(obj).C.getType().equalsIgnoreCase("RADIOFILTER"))) {
                String oQuery = this.objects.get(obj).Origin.getQuery();
                oQuery = browserArgsReplace(oQuery);
                String oLabelField = this.objects.get(obj).Origin.getLabelField();
                String oValueField = this.objects.get(obj).Origin.getValueField();
                String oValueFieldType = this.objects.get(obj).Origin.getValueFieldType();
                SelectList myList = new SelectList(myParams, mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                myList.getList();
                this.objects.get(obj).Origin.setSelectList(myList);

            } else if (this.objects.get(obj).C.getType().equalsIgnoreCase("VOICECHECK")) {
                String oQuery = this.objects.get(obj).Origin.getQuery();
                oQuery = browserArgsReplace(oQuery);
                String oLabelField = this.objects.get(obj).Origin.getLabelField();
                String oValueField = this.objects.get(obj).Origin.getValueField();
                String oValueFieldType = this.objects.get(obj).Origin.getValueFieldType();
                SelectList myList = new SelectList(myParams, mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                this.objects.get(obj).Origin.setSelectList(myList);
            } else if (this.objects.get(obj).C.getType().equalsIgnoreCase("SPAREVALUE")) {
                String oQuery = this.objects.get(obj).Origin.getQuery();
                oQuery = browserArgsReplace(oQuery);
                String oLabelField = this.objects.get(obj).Origin.getLabelField();
                String oValueField = this.objects.get(obj).Origin.getValueField();
                String oValueFieldType = this.objects.get(obj).Origin.getValueFieldType();
                SelectList myList = new SelectList(myParams, mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                this.objects.get(obj).Origin.setSelectList(myList);
            }

        }
    }
//-----------------------------------------------

    public smartFormResponse paintForm() {

        /*
        _________________________________________
	| TOP                           |	|
	|				|	|
	--------------------------------|       |
	|	|B O D Y|	|	| ExtRT	|
	|	|B O D Y|	|	|	|
	|  L	|B O D Y|  IntR	|   RT  |------	|
	|	|B O D Y|	|-------|	|
	|	|B O D Y|	|   R   |  ExtR |
	|	|B O D Y|	|	|	|
	|	|---------------|-------|	|
	|	|       B	|   RB  |------	|
	|	|--------------	|	| ExtRB |
	|	| BL	|  BR	|	|	|
	-----------------------------------------
	|	 			 	|
	|	 	ExtB     	 	|
	|	 			 	|
	-----------------------------------------
         */
//        System.out.println("\nSONO IN PAINTFORM : ");
//        //myParams.printParams("PAINTFORM");
        System.out.println("\nsmartForm-->paintForm-->Prima di  buildSchema il StC =  :\n " + this.getSendToCRUD());
        if (this.buildSchemaDone == false) {
            buildSchema();
        }

        System.out.println("\n*Dopo buildSchema il StC =  : " + this.getSendToCRUD());

//        System.out.println("SONO IN PAINTFORM :this.id=" + this.ID);
        this.setVisualFilter("");
        this.setFirstRow(0);
        this.setNofRows(0);
        this.setVisualType("FULLFORM");
        loadPagingInstructions();// questo compila il visualType 
        //==============================================================================   
        if (this.type == null) {
            this.type = "TABLE";
        }
        System.out.println("SONO IN smartForm->PAINTFORM :this.type: " + this.type);
        if (this.type.equalsIgnoreCase("SMARTTABLE")) {
            formResponse = paintDataTable();
        } else if (this.type.equalsIgnoreCase("SMARTTREE")) {
            formResponse = paintDataTree();
        } else if (this.type.equalsIgnoreCase("SMARTPANEL")) {
            formResponse = fillSmartPanel();
        }
        return formResponse;
    }

    public smartFormResponse fillSmartPanel() {
        String htmlCode = "";
        if (this.getVisualType().equalsIgnoreCase("FULLFORM")) {
            divPrefix = "CH-" + this.getID() + "-" + this.getCopyTag();
            elementPrefix = this.getID() + "-" + this.getCopyTag();
            prepareFullForm();
            System.out.println("fillSmartPanel-->CASO VisualType() FULLFORM");
            String fullFormCode = "<div id=\"FRAME-" + this.getName() + "-" + this.getCopyTag() + "\" "
                    + "class= \"frame scrollableContainer\" >";
            fullFormCode += mainQPtable;
            fullFormCode += ("</div>");
            htmlCode = "{"
                    + "\"respOK\":\"true\","
                    + "\"formName\":\"" + this.getName() + "\","
                    + "\"formID\":\"" + this.getID() + "\","
                    + "\"formType\":\"" + this.getType() + "\","
                    + "\"formCopyTag\":\"" + this.getCopyTag() + "\","
                    + "\"htmlCode\":\"" + encodeURIComponent(fullFormCode) + "\""
                    + "}";
        } else //==============================================================================
        if (this.getVisualType().equalsIgnoreCase("DATAONLY")) {//richiesta refresh tramite websocket
            divPrefix = "CH-" + this.getID() + "-" + this.getCopyTag();
            elementPrefix = this.getID() + "-" + this.getCopyTag();
//            prepareDivs();
            prepareBody();
            System.out.println("CASO VisualType() DATAONLY");
            htmlCode = "{"
                    + "\"respOK\":\"true\","
                    + "\"formName\":\"" + this.getName() + "\","
                    + "\"formID\":\"" + this.getID() + "\","
                    + "\"formType\":\"" + this.getType() + "\","
                    + "\"formCopyTag\":\"" + this.getCopyTag() + "\","
                    + "\"htmlCode\":\"" + encodeURIComponent(rowsCode) + "\""
                    + "}";

        }
        formResponse.setHtmlCode(htmlCode);
//        System.out.println("PAINTFORM_htmlCode:" + htmlCode);
        return formResponse;
    }
    //----------------------------------------
 public JSONObject paintTreeNewLeaf() { 

            String pKEYvalue = this.getCurKEYvalue().replace("'", "");
            String pKEYtype = this.getCurKEYtype();
            int myVal = 0;
            try {
                myVal = Integer.parseInt(pKEYvalue);
            } catch (Exception e) {
                myVal = 0;
            }
            String myTxVal = "" + myVal;
            if (myTxVal.equalsIgnoreCase(pKEYvalue.trim())) {
                pKEYtype = "INT";
            }
            String whereClause = "";
            //adesso chiamo la routine showItFOrm affinchè mi restituisca la singola riga aggiunta
            if (this.getKEYfieldType() == "INT") {
                whereClause = " " + this.getMainTable() + "." + this.getKEYfieldName() + " = " + pKEYvalue + " ";
            } else {
                String result = null;
                try {
                    result = java.net.URLEncoder.encode(pKEYvalue, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    System.out.println("serror in line 1710");
                }

                if (updRowWhereClause == null
                        || updRowWhereClause.equalsIgnoreCase("null")
                        || updRowWhereClause.length() < 1) {
                    whereClause = " " + this.getMainTable() + "." + this.getKEYfieldName() + " = '" + result + "'";
                    System.out.println("QUERY SEMPLICE: USO COME INDICE " + whereClause);
                } else {
                    whereClause = " " + updRowWhereClause + " ";
                    System.out.println("QUERY COMPLESSA: USO COME INDICE " + whereClause);
                }

            }

//            System.out.println("PAINTFORM_case singleRow_whereClause:" + whereClause);
            setInfoReceived(this.getToBeSent());

            System.out.println("paintDataTree-->CASO SINGLE ROW:"+ pKEYvalue);
            formResponse.getDataJSON().put("mode", "leaf");
            formResponse.getDataJSON().put("node", this.getID() + "-" + this.getCopyTag());
            formResponse.getDataJSON().put("keyField", this.getID() + "-" + this.getCopyTag());
            Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
////////        ResultSet DATArs;
            // cerca il FORM per nome e se non è compilato per ID

//        System.out.println("fillRowData query: " + this.query);
//        System.out.println("fillRowData whereClause: " + whereClause);
            String myQuery = prepareSQL(this.query);
            this.queryUsed = regenerateQuery(myQuery, whereClause, false, null, false, false);
            System.out.println("====fillRowData queryUsed: " + this.queryUsed);

            if (this.queryUsed == null || this.queryUsed == "") {
                return null;
            }
            JSONObject myleaf= new JSONObject();
            Statement s;
            try {
                s = conny.createStatement();
                DATArs = s.executeQuery(this.queryUsed);
                while (DATArs.next()) {
                    try {
                        smartRow myRow = new smartRow(this, DATArs, 0);
                        smartObjRight rowRights = myRow.valutaRightsRiga(this.getDisableRules(), DATArs);/// analizzo il LOCKER del form per la riga
                        smartObjRight actualRowRights = joinRights(formRightsRules, rowRights);
                        myRow.setActualRowRights(actualRowRights);
                        myRow.setFormRightsRules(formRightsRules); 
                        myleaf =  myRow.encodeTreeRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                break;// solo una riga
                }
            } catch (SQLException ex) {
                Logger.getLogger(smartForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conny.close();
            } catch (SQLException ex) {
                Logger.getLogger(smartForm.class.getName()).log(Level.SEVERE, null, ex);
            }
           return myleaf;

        } 
    public smartFormResponse paintDataTree() {
        String htmlCode = "";
        if (this.getVisualType().equalsIgnoreCase("singleRow")) {

            String pKEYvalue = this.getCurKEYvalue().replace("'", "");
            String pKEYtype = this.getCurKEYtype();
            int myVal = 0;
            try {
                myVal = Integer.parseInt(pKEYvalue);
            } catch (Exception e) {
                myVal = 0;
            }
            String myTxVal = "" + myVal;
            if (myTxVal.equalsIgnoreCase(pKEYvalue.trim())) {
                pKEYtype = "INT";
            }
            String whereClause = "";
            //adesso chiamo la routine showItFOrm affinchè mi restituisca la singola riga aggiunta
            if (this.getKEYfieldType() == "INT") {
                whereClause = " " + this.getMainTable() + "." + this.getKEYfieldName() + " = " + pKEYvalue + " ";
            } else {
                String result = null;
                try {
                    result = java.net.URLEncoder.encode(pKEYvalue, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    System.out.println("serror in line 1710");
                }

                if (updRowWhereClause == null
                        || updRowWhereClause.equalsIgnoreCase("null")
                        || updRowWhereClause.length() < 1) {
                    whereClause = " " + this.getMainTable() + "." + this.getKEYfieldName() + " = '" + result + "'";
//                    System.out.println("QUERY SEMPLICE: USO COME INDICE " + whereClause);
                } else {
                    whereClause = " " + updRowWhereClause + " ";
//                    System.out.println("QUERY COMPLESSA: USO COME INDICE " + whereClause);
                }

            }

//            System.out.println("PAINTFORM_case singleRow_whereClause:" + whereClause);
            setInfoReceived(this.getToBeSent());

            System.out.println("paintDataTree-->CASO SINGLE ROW");
            formResponse.getDataJSON().put("mode", "leaf");
            formResponse.getDataJSON().put("node", this.getID() + "-" + this.getCopyTag());
            formResponse.getDataJSON().put("keyField", this.getID() + "-" + this.getCopyTag());
            Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
////////        ResultSet DATArs;
            // cerca il FORM per nome e se non è compilato per ID

//        System.out.println("fillRowData query: " + this.query);
//        System.out.println("fillRowData whereClause: " + whereClause);
            String myQuery = prepareSQL(this.query);
            this.queryUsed = regenerateQuery(myQuery, whereClause, false, null, false, false);
            System.out.println("====fillRowData queryUsed: " + this.queryUsed);

            if (this.queryUsed == null || this.queryUsed == "") {
                return null;
            }
            JSONObject myleaf= new JSONObject();
            Statement s;
            try {
                s = conny.createStatement();
                DATArs = s.executeQuery(this.queryUsed);
                while (DATArs.next()) {
                    try {
                        smartRow myRow = new smartRow(this, DATArs, 0);
                        smartObjRight rowRights = myRow.valutaRightsRiga(this.getDisableRules(), DATArs);/// analizzo il LOCKER del form per la riga
                        smartObjRight actualRowRights = joinRights(formRightsRules, rowRights);
                        myRow.setActualRowRights(actualRowRights);
                        myRow.setFormRightsRules(formRightsRules);
//                        htmlCode += myRow.SMRTpaintRow("normal");
                        myleaf =  myRow.encodeTreeRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                break;// solo una riga
                }
            } catch (SQLException ex) {
                Logger.getLogger(smartForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conny.close();
            } catch (SQLException ex) {
                Logger.getLogger(smartForm.class.getName()).log(Level.SEVERE, null, ex);
            }
             formResponse.getDataJSON().put("node", this.getID() + "-" + this.getCopyTag());
            formResponse.getDataJSON().put("keyField", this.getID() + "-" + this.getCopyTag());
         

        } else //==============================================================================
        if (this.getVisualType().equalsIgnoreCase("FULLFORM")) {
            divPrefix = "CH-" + this.getID() + "-" + this.getCopyTag();
            elementPrefix = this.getID() + "-" + this.getCopyTag();

            formResponse.getDataJSON().put("node", this.getID() + "-" + this.getCopyTag());
            formResponse.getDataJSON().put("keyField", this.getID() + "-" + this.getCopyTag());
            prepareFrameOnly();
//            System.out.println("paintDataTable-->CASO VisualType() FULLFORM");
            String fullFormCode = "<div id=\"FRAME-" + this.getName() + "-" + this.getCopyTag() + "\" "
                    + "class= \"frame scrollableContainer\" >";
            fullFormCode += mainQPtable;
            fullFormCode += ("</div>");
            htmlCode = "{"
                    + "\"respOK\":\"true\","
                    + "\"formName\":\"" + this.getName() + "\","
                    + "\"formID\":\"" + this.getID() + "\","
                    + "\"formType\":\"" + this.getType() + "\","
                    + "\"formCopyTag\":\"" + this.getCopyTag() + "\","
                    + "\"htmlCode\":\"" + encodeURIComponent(fullFormCode) + "\""
                    + "}";
        } else //==============================================================================
        if (this.getVisualType().equalsIgnoreCase("DATAONLY")) {//richiesta refresh tramite websocket
            System.out.println("paintDataTree-->CASO DATAONLY");

        }

        formResponse.setHtmlCode(htmlCode);

        System.out.println("PAINTFORM_htmlCode:" + htmlCode);
        return formResponse;
    }

    //----------------------------------------
    public smartFormResponse paintDataTable() {
//        System.out.println("\nSONO IN smartForm-->paintDataForm : ");
        String htmlCode = "";

//        System.out.println("this.getVisualType():" + this.getVisualType());

        /* CASI PSSIBILI:
        1. SINGLEROW
        2. FULLFORM
        3. DATAONLY
         */
        if (this.getVisualType().equalsIgnoreCase("singleRow")) {
            System.out.println("smartForm-->CASO SINGLE ROW");
            // System.out.println("creo il filtro qualificato per ricerca");
            //makeQualifiedQuery();
            //==============================================================================
            String pKEYvalue = this.getCurKEYvalue().replace("'", "");
            String pKEYtype = this.getCurKEYtype();
            int myVal = 0;
            try {
                myVal = Integer.parseInt(pKEYvalue);
            } catch (Exception e) {
                myVal = 0;
            }
            String myTxVal = "" + myVal;
            if (myTxVal.equalsIgnoreCase(pKEYvalue.trim())) {
                pKEYtype = "INT";
            }
            String whereClause = "";
            //adesso chiamo la routine showItFOrm affinchè mi restituisca la singola riga aggiunta
            if (this.getKEYfieldType() == "INT") {
                whereClause = " " + this.getMainTable() + "." + this.getKEYfieldName() + " = " + pKEYvalue + " ";
            } else {
                String result = null;
                try {
                    result = java.net.URLEncoder.encode(pKEYvalue, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    System.out.println("serror in line 1710");
                }

                if (updRowWhereClause == null
                        || updRowWhereClause.equalsIgnoreCase("null")
                        || updRowWhereClause.length() < 1) {
                    whereClause = " " + this.getMainTable() + "." + this.getKEYfieldName() + " = '" + result + "'";
//                    System.out.println("QUERY SEMPLICE: USO COME INDICE " + whereClause);
                } else {
                    whereClause = " " + updRowWhereClause + " ";
//                    System.out.println("QUERY COMPLESSA: USO COME INDICE " + whereClause);
                }

            }

//            System.out.println("PAINTFORM_case singleRow_whereClause:" + whereClause);
            setInfoReceived(this.getToBeSent());
//            System.out.println("TIPO DI FORM:" + this.type);
            if (this.type.equalsIgnoreCase("SINGLEROWFORM")) {
                //----SINGOLA RIGA IN MASK-----------------------------------------------------------------------
//                System.out.println("----SINGOLA RIGA IN MASK-----------------------------------------------------------------------");
                htmlCode = fillRowData("MaskRow", whereClause);
            } else {
                //----SINGOLA RIGA IN TABLE-----------------------------------------------------------------------
//                System.out.println("----SINGOLA RIGA IN TABLE-----------------------------------------------------------------------");

                htmlCode = fillRowData("fillRow", whereClause);
            }

        } else //==============================================================================
        if (this.getVisualType().equalsIgnoreCase("FULLFORM")) {
            divPrefix = "CH-" + this.getID() + "-" + this.getCopyTag();
            elementPrefix = this.getID() + "-" + this.getCopyTag();
//            System.out.println("richiamo prepareFullForm().");
            prepareFullForm();
//            System.out.println("paintDataTable-->CASO VisualType() FULLFORM");
            String fullFormCode = "<div id=\"FRAME-" + this.getName() + "-" + this.getCopyTag() + "\" "
                    + "class= \"frame scrollableContainer\" >";
            fullFormCode += mainQPtable;
            fullFormCode += ("</div>");
            htmlCode = "{"
                    + "\"respOK\":\"true\","
                    + "\"formName\":\"" + this.getName() + "\","
                    + "\"formID\":\"" + this.getID() + "\","
                    + "\"formType\":\"" + this.getType() + "\","
                    + "\"formCopyTag\":\"" + this.getCopyTag() + "\","
                    + "\"htmlCode\":\"" + encodeURIComponent(fullFormCode) + "\""
                    + "}";
        } else //==============================================================================
        if (this.getVisualType().equalsIgnoreCase("DATAONLY")) {//richiesta refresh tramite websocket
            divPrefix = "CH-" + this.getID() + "-" + this.getCopyTag();
            elementPrefix = this.getID() + "-" + this.getCopyTag();
//            prepareDivs();
            prepareBody();
//            System.out.println("CASO VisualType() DATAONLY");
            htmlCode = "{"
                    + "\"respOK\":\"true\","
                    + "\"formName\":\"" + this.getName() + "\","
                    + "\"formID\":\"" + this.getID() + "\","
                    + "\"formType\":\"" + this.getType() + "\","
                    + "\"formCopyTag\":\"" + this.getCopyTag() + "\","
                    + "\"htmlCode\":\"" + encodeURIComponent(rowsCode) + "\""
                    + "}";

        }

        formResponse.setHtmlCode(htmlCode);
//        System.out.println("PAINTFORM_htmlCode:" + htmlCode);
        return formResponse;

    }

    private void prepareConsole() {
        divTopBar += "<TABLE style=\"border-collapse: collapse;border:0; padding: 0; margin: 0;  \" id=\"" + elementPrefix + "-topBarTable\">";
//        System.out.println("richiamo paintFormAbstract();");
        divTopBar += paintFormAbstract();
//        System.out.println("richiamo paintTopBar();");
        divTopBar += paintTopBar();
//        System.out.println("richiamo paintFormConsole();");
        divTopBar += paintFormConsole();//oggetti pulsante per form
        divTopBar += "</TABLE>";
    }

    private void prepareDivs() {
        String style = "style=\"display: table-cell; vertical-align: top;\"";
        style = "style=\"vertical-align: top;padding: 0; margin: 0;\"";
        //==============================================================================

        //==============================================================================
        divLeft = "<div class=\"leftTab\" " + style + " id=\"" + divPrefix + "-L\" ></div>";
        //  divLeft="LEFT";
        //==============================================================================

        divRight += "<TABLE style=\"border-collapse: collapse; padding: 0; margin: 0; \"><TR>\n";
        divRight += "</TR><TR><TD style=\"padding: 0; margin: 0; \">\n";
        divRight += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-RT\" ></div>\n";
        divRight += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-RT\" ></div>\n";
        divRight += "</TD></TR><TR><TD style=\"padding: 0; margin: 0; \">\n";
        divRight += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-R\" ></div>\n";
        divRight += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-R\" ></div>\n";
        divRight += "</TD></TR><TR><TD style=\"padding: 0; margin: 0; \">\n";
        divRight += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-RB\" ></div>\n";
        divRight += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-RB\" ></div>\n";
        divRight += "</TD></TR></TABLE>\n";
        //==============================================================================

        divBottom += "<div id=\"CHILDREN-" + elementPrefix + "\" >";
        divBottom += "<TABLE style=\"border-collapse: collapse; padding: 0; margin: 0;  \"><TR>\n";
        divBottom += "<TD colspan=\"2\"><div class=\"bottomTab\" id=\"" + divPrefix + "-B\" ></div></TD>\n";
        divBottom += "</TR><TR>\n";
        divBottom += "<TD colspan=\"2\"><div class=\"bottomTab\" id=\"CH-" + this.getName() + "-B\"></div></TD>\n";
        divBottom += "</TR><TR>\n";
        divBottom += "<TD><div class=\"bottomTab\" id=\"" + divPrefix + "-BL\" ></div></TD>\n";
        divBottom += "<TD><div class=\"bottomTab\" id=\"" + divPrefix + "-BR\" ></div></TD>\n";
        divBottom += "</TR></TABLE>\n";
        divBottom += "</div>";
        //    divBottom="BOTTOM";

        //==============================================================================
        divExtR += "<TABLE style=\"border-collapse: collapse; padding: 0; margin: 0;  \"><TR>\n";
        divExtR += "</TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-ExtRT\" ></div>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-ExtRT\" ></div>\n";
        divExtR += "</TD></TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-ExtR\" ></div>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-ExtR\" ></div>\n";
        divExtR += "</TD></TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-ExtR1\" ></div>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-ExtR1\" ></div>\n";
        divExtR += "</TD></TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-ExtR2\" ></div>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-ExtR2\" ></div>\n";
        divExtR += "</TD></TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-ExtR3\" ></div>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-ExtR3\" ></div>\n";
        divExtR += "</TD></TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-ExtR4\" ></div>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-ExtR4\" ></div>\n";
        divExtR += "</TD></TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-ExtR5\" ></div>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-ExtR5\" ></div>\n";
        divExtR += "</TD></TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-ExtR6\" ></div>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-ExtR6\" ></div>\n";
        divExtR += "</TD></TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"" + divPrefix + "-ExtRB\" ></div>\n";
        divExtR += "<div class=\"rightTab\" " + style + " id=\"CH-" + this.getName() + "-ExtRB\" ></div>\n";
        divExtR += "</TD></TR></TABLE>\n";

        //==============================================================================
        divExtB = "<div class=\"bottomTab\" id=\"" + divPrefix + "-ExtB\" ></div>\n";
        divExtB += "<div class=\"bottomTab\" id=\"CH-" + this.getName() + "-ExtB\" ></div>\n";
        //==============================================================================
        divIntRight = "<div class=\"bottomTab\" id=\"" + divPrefix + "-IntR\" ></div>\n";
        divIntRight += "<div class=\"bottomTab\" id=\"CH-" + this.getName() + "-IntR\" ></div>\n";
        //==============================================================================
        outL = "<div class=\"sideText\" id=\"" + divPrefix + "-OutL\" ></div>\n";
        outL += "<div class=\"bottomTab\" id=\"CH-" + this.getName() + "-OutL\" ></div>\n";
        //==============================================================================
        outR = "<div class=\"bottomTab\" id=\"" + divPrefix + "-OutR\" ></div>\n";
        outR += "<div class=\"bottomTab\" id=\"CH-" + this.getName() + "-OutR\" ></div>\n";
        //==============================================================================
//        System.out.println("concluso prepareDivs();");
    }

    private void prepareTreeLeafOnly() {
        System.out.println("8888 prepareTreeLeafOnly " + this.type);
        divBody += fillFormTree("FRAMEONLY");
    }

    private void prepareFrameOnly() {
        System.out.println("8888 prepareFrameOnly " + this.type);
        prepareDivs();
        prepareEmptyBody();
        prepareConsole(); // lo faccio dopo body per avere DATArs compilato e poter conoscere dati sulla tabella 
        prepareFrame();
    }

    private void prepareFullForm() {

        prepareDivs();
        prepareBody();
        prepareConsole(); // lo faccio dopo body per avere DATArs compilato e poter conoscere dati sulla tabella 
        prepareFrame();
    }

    private void prepareFrame() {

        //--------------------------------------------------------------------------
        divBodyUpDown += "<TABLE style=\"border-collapse: collapse; padding: 0; margin: 0;  \">";
        divBodyUpDown += "<TR><TD style=\"display: table-cell; vertical-align: top;\">";

        divBodyUpDown += "<TABLE style=\"border-collapse: collapse; padding: 0; margin: 0;  \"><TR>"
                + "<TD style=\"display: table-cell; vertical-align: top;\">";

        divBodyUpDown += "<DIV "
                + "id=\"" + this.getName() + "-HIDABLE\"";
        if (this.getBodyInitHidden() != null && this.getBodyInitHidden().equalsIgnoreCase("TRUE")) {
            divBodyUpDown += "style=\"display:none;\" ";
        }
        divBodyUpDown += ">";
        divBodyUpDown += "<DIV id=\"" + this.getID() + "-" + this.getCopyTag() + "-TABDIV\">";
        divBodyUpDown += divBody;
        divBodyUpDown += "</DIV>";
        divBodyUpDown += "</DIV>";

        divBodyUpDown += "</TD><TD style=\"display: table-cell; vertical-align: top;\">";
        divBodyUpDown += divIntRight;
        divBodyUpDown += "</TD></TR></TABLE>";

        divBodyUpDown += "</TD></TR>";
        divBodyUpDown += "<TR><TD style=\"display: table-cell; vertical-align: top;\">";
        divBodyUpDown += divBottom;
        divBodyUpDown += "</TD></TR>";
        divBodyUpDown += "</TABLE>";

//-----------------------------------------------------------------------    
        mainBodyTable += "<TABLE id=\"" + elementPrefix + "-mainBodyTable\" "
                + "style=\"border-collapse: collapse;border:0; padding: 0; margin: 0;  \""
                + "  width = '" + this.getFormWidth() + "' "
                + ">";

        mainBodyTable += "<TR>";
        mainBodyTable += "<TD colSpan=\"3\">" + divTopBar + "</TD>";
        mainBodyTable += "</TR>";

        mainBodyTable += "<TR><TD style=\"vertical-align:top\">" + divLeft + "</TD>";
        mainBodyTable += "<TD style=\"vertical-align:top\">" + divBodyUpDown + "</TD>";
        mainBodyTable += "<TD style=\"vertical-align:top\">" + divRight + "</TD></TR>";
        mainBodyTable += "</TABLE>";
//-----------------------------------------------------------------------  

        fileTable += "<TABLE width = \"" + this.getFormWidth() + "\" "
                + "style=\"border-collapse: collapse;border:0; padding: 0; margin: 0;  \""
                + ">";
        fileTable += "<TR>" + "<TD style=\"vertical-align:top\">" + mainBodyTable + "</TD>";
        fileTable += "<TD style=\"vertical-align:top\">" + divExtR + "</TD>" + "</TR>";
        fileTable += "<TR>" + "<TD colspan=\"2\" >" + divExtB + "</TD>" + "</TR>";
        fileTable += "</TABLE>";

        mainQPtable += "<TABLE>";
        mainQPtable += "<TR><TD style=\"display: table-cell; vertical-align: top;\">";
        mainQPtable += outL;
        mainQPtable += "</TD><TD style=\"display: table-cell; vertical-align: top; \" >";
        mainQPtable += fileTable;
        mainQPtable += "</TD><TD style=\"display: table-cell; vertical-align: top;\">";
        mainQPtable += outR;
        mainQPtable += "</TD></TR>";
        mainQPtable += "</TABLE>";

    }

    private void prepareEmptyBody() {

//        System.out.println("prepareBody.");
        String className = "formTable";
        if (this.type == null) {
            this.type = "TABLE";
        }

//        System.out.println(" this.type:" + this.type);
        //==============================================================================
        divBody = "<div id=\"FORM-" + this.name + "-" + this.getCopyTag() + "\"  ";
        divBody += "class = \"" + className + " \""
                + " style=\" width:" + this.getFormWidth() + "; "
                + " height:" + this.getFormHeight() + ";  "
                + "resize: vertical; "
                + "display: table-cell; vertical-align: top; overflow: auto; \" >";
        //==============================================================================

//        System.out.println("richiamo paintFormAttributes().");
        attributes = paintFormAttributes();
//        System.out.println("ho scritto sul form i segg attributi:" + attributes);
        divBody += attributes;
        //==============================================================================
        if (this.type.equalsIgnoreCase("SMARTPANEL")) {
            divBody += "+++";
        } else if (this.type.equalsIgnoreCase("SMARTTREE")) {
            divBody += fillFormTree("FRAMEONLY");
        } else {
            divBody += "+++";
        }
        divBody += "</div>\n"; // chiude FORM-xxxxxx 
    }

    public String fillFormTree(String Mode) {
        String htmlCode = "";
        rowsCode = getCodeTree(Mode);
        htmlCode += rowsCode;

        return htmlCode;
    }

    public String getCodeTree(String mode) {
        JSONArray carBrands = new JSONArray();

        String rowsCode = "";
        rowsPerPage = this.maxRows;
        if (rowsPerPage < 1) {
            rowsPerPage = 20;
        }
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        ResultSet rs;
        if (this.queryUsed == null || this.queryUsed == "") {
            return "ERROR LOADING FORM TABLE.";
        }
        righeScritte = 0;
        rowsCounter = 0;
        totalPages = 0;

        try {
//=====CONTO LE RIGHE TOTALI=================================================
            Statement s = conny.prepareStatement(this.queryUsed,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY); //questo serve per poter scorrere i record e riocomeinciare da capo
            rs = s.executeQuery(this.queryUsed);
            try {
                rowsCounter = 0;
                try {
                    while (rs.next()) {
                        rowsCounter++;
                    }
                    rs.beforeFirst();
                } catch (Exception e) {
                    System.out.println("errore in conteggio righe totali:" + e.toString());
                }
                System.out.println("conteggio righe totali:" + rowsCounter);
                float rawTotaPages = (float) rowsCounter / rowsPerPage;
                totalPages = (int) Math.floor(rowsCounter / rowsPerPage);
                if (rawTotaPages > totalPages) {
                    totalPages++;
                }
                if (this.getVisualType().equalsIgnoreCase("FULLFORM")) {
                    if (this.addRowPosition != null && this.addRowPosition.equalsIgnoreCase("TOP")) {
                        currentPage = 1;
                    } else {
                        currentPage = totalPages;
                    }
                }
                if (currentPage < 1) {
                    currentPage = 1;
                }
                if (currentPage > totalPages) {
                    currentPage = totalPages;
                }
                if (rowsPerPage < 1 || rowsPerPage > 150) {
                    rowsPerPage = 30;
                }
                int lines = 0;
//----------INIZIO RIGHE DATI
                int splitterPagesEnabled = 1;
                righeScritte = 0;
                for (int jj = 0; jj < this.objects.size(); jj++) {
                    this.objects.get(jj).objRights = analyzeRightsRuleJson(this.objects.get(jj).Content.getModifiable(), null, null, 400); //curObj.objRights=analyzeRightsRuleJson(curObj.Content.getModifiable(), null);
                }

//CICLO RIGHE TABELLA=========================================
                while (rs.next()) {
                    righeScritte++;
                    for (int jj = 0; jj < this.objects.size(); jj++) {
                        if (this.objects.get(jj).Content.getType() != null
                                && this.objects.get(jj).Content.getType().equalsIgnoreCase("INT")
                                && this.objects.get(jj).Content.getHasSum() > 0) {
                            int partial = this.objects.get(jj).Content.getActualSum();
                            int thisValue = 0;
                            try {
                                thisValue = rs.getInt(this.objects.get(jj).getName());
                            } catch (Exception e) {
                                thisValue = 0;
                            }
                            this.objects.get(jj).Content.setActualSum(partial + thisValue);
                        }
                    }
                    if (righeScritte > (rowsPerPage * currentPage) && splitterPagesEnabled > 0) {
                    } else if (righeScritte <= (rowsPerPage * (currentPage - 1)) && splitterPagesEnabled > 0) {
                    } else {
                        lines++;
                        smartRow myRow = new smartRow(this, rs, righeScritte);
                        smartObjRight rowRights = myRow.valutaRightsRiga(this.getDisableRules(), rs);
                        smartObjRight actualRowRights = joinRights(formRightsRules, rowRights);
                        myRow.setActualRowRights(actualRowRights);
                        myRow.setFormRightsRules(formRightsRules);
                        if (mode.equalsIgnoreCase("FRAMEONLY")) {
                            carBrands.add(myRow.encodeTreeRow());
                        } else {
//                            normalRowsCode += myRow.SMRTpaintBranch("normal");
                        }
                    }
                }
                String treeObjAddedName = "";
                for (int obj = 0; obj < objects.size(); obj++) {
                    if (objects.get(obj).AddingRow_enabled > 0) {
                        treeObjAddedName = objects.get(obj).name;
                        break;
                    }
                }
                formResponse.getDataJSON().put("objName", treeObjAddedName);
                formResponse.getDataJSON().put("addEnabled", "true");// QUESTO E' DA PROGRAMMARE IN BASE AI DIRITTI UTENTE
                formResponse.getDataJSON().put("values", carBrands);
            } catch (Exception e) {
                System.out.println("err 2028" + e.toString());
            }
        } catch (SQLException ex) {

            System.out.println("query error:" + ex.toString());
        }

        if (mode.equalsIgnoreCase("FRAMEONLY")) {
            normalRowsCode = "";
        } else {
        }

        totalRowCode = "";
        for (int jj = 0; jj < this.objects.size(); jj++) {
            if (this.objects.get(jj).Content.getType() != null
                    && this.objects.get(jj).Content.getType().equalsIgnoreCase("INT")
                    && this.objects.get(jj).Content.getHasSum() > 0) {
            }
        }
        rowsCode = "<div "
                + " id=\"" + this.getID() + "-" + this.getCopyTag() + "-ROWSDIV\" "
                + "style=\" height:" + this.getFormHeight() + "; "
                + "resize: vertical;\n"
                + "    overflow: auto; \""
                + ">";
        rowsCode += "<UL ";
        rowsCode += "id=\"" + this.getID() + "-" + this.getCopyTag() + "-ROOT-ROWSTABLE\"   ";
        rowsCode += "style=\"width:" + this.getFormWidth()+"; height:" + this.getFormHeight()+";\"   ";
        rowsCode += ">  ";
        if (this.addRowPosition != null && this.addRowPosition.equalsIgnoreCase("TOP")) {
            //===CASO FORMMASK CON ADD IN ALTO
            if (formRightsRules.canCreate > 0) {
                rowsCode += addingRowCode;
            }
            rowsCode += totalRowCode;
            rowsCode += normalRowsCode;
        } else {
            rowsCode += normalRowsCode;
            rowsCode += totalRowCode;
            if (formRightsRules.canCreate > 0) {
                rowsCode += addingRowCode;
            }
        }
        rowsCode += "</UL>";
        rowsCode += "</div>";
        //System.out.println("Righe DATI:" + lines);

        try {
            conny.close();

        } catch (SQLException ex) {
        }
//----------FINE RIGHE DATI
        return rowsCode;
    }

    private void prepareBody() {

//        System.out.println("prepareBody.");
        String className = "formTable";
        if (this.type == null) {
            this.type = "TABLE";
        }

//        System.out.println(" this.type:" + this.type);
        //==============================================================================
        divBody = "<div id=\"FORM-" + this.name + "-" + this.getCopyTag() + "\"  ";
        divBody += "class = \"" + className + " \""
                + " style=\" width:" + this.getFormWidth() + "; "
                + " height:" + this.getFormHeight() + ";  "
                + "resize: vertical; "
                + "display: table-cell; vertical-align: top; overflow: auto; \" >";
        //==============================================================================

//        System.out.println("richiamo paintFormAttributes().");
        attributes = paintFormAttributes();
//        System.out.println("ho scritto sul form i segg attributi:" + attributes);
        divBody += attributes;
        //==============================================================================
        if (this.type.equalsIgnoreCase("SMARTPANEL")) {
            divBody += fillFormPanel();
        } else if (this.type.equalsIgnoreCase("SMARTTREE")) {
            divBody += fillFormTree("FRAMEONLY");
        } else {
            divBody += fillFormData();
        }
        divBody += "</div>\n"; // chiude FORM-xxxxxx 
    }

    public String fillFormData() {
        String Xtype = "";
//        System.out.println("-------->fillFormData() type = " + this.type);
        if (this.type.equalsIgnoreCase("PANEL") || this.type.equalsIgnoreCase("FILTER")) {//MENU PANEL o VISUAL PANEL
            Xtype = "FormPanel";
        } else {//PER DEFAULT E' UNA TABLE
            Xtype = "FormTable";
        }
//        System.out.println("-------->fillFormData() type = " + Xtype);
        String htmlCode = "";
        if (Xtype.equalsIgnoreCase("FormTable")) {
            //makeQualifiedQuery();
            htmlCode = fillFormData_formTable();
        }
        return htmlCode;
    }

    public String fillFormData_formTable() {

        // è una TABLE, adesso in base a visualType discerno cosa mandare in codice
        // FULLFORM
        String htmlCode = "";
        headerCode = getCodeHeader();
        rowsCode = getCodeRows();

        htmlCode += headerCode;

        htmlCode += rowsCode;

        return htmlCode;
    }
 
    public String fillRowData(String type, String whereClause) {
        String htmlCode = "";
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
////////        ResultSet DATArs;
        // cerca il FORM per nome e se non è compilato per ID

//        System.out.println("fillRowData query: " + this.query);
//        System.out.println("fillRowData whereClause: " + whereClause);
        String myQuery = prepareSQL(this.query);
        this.queryUsed = regenerateQuery(myQuery, whereClause, false, null, false, false);
        System.out.println("====fillRowData queryUsed: " + this.queryUsed);

        if (this.queryUsed == null || this.queryUsed == "") {
            return "ERROR LOADING FORM TABLE.";
        }
        Statement s;
        try {
            s = conny.createStatement();
            DATArs = s.executeQuery(this.queryUsed);
            while (DATArs.next()) {
                try {
                    smartRow myRow = new smartRow(this, DATArs, 0);
                    smartObjRight rowRights = myRow.valutaRightsRiga(this.getDisableRules(), DATArs);/// analizzo il LOCKER del form per la riga
                    smartObjRight actualRowRights = joinRights(formRightsRules, rowRights);
                    myRow.setActualRowRights(actualRowRights);
                    myRow.setFormRightsRules(formRightsRules);
                    htmlCode += myRow.SMRTpaintRow("normal");
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                break;// solo una riga
            }
        } catch (SQLException ex) {
            Logger.getLogger(smartForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(smartForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return htmlCode;
    }

    public String fillFormPanel() {
        if (this.layoutColumns < 1) {
            this.layoutColumns = 9999;
        }
        // per ogni oggetto presente vado in paintObject...
        String htmlCode = "";
        htmlCode += ("<TABLE class=\"formPanel\"><tr>\n");
        int curCol = 0;
        for (int obj = 0; obj < this.objects.size(); obj++) {
            //---TRIGGERED STYLE--(paintRow)-------------------                    
            String triggeredStyle = feedTriggeredStyle(this.objects.get(obj), null);
            if (triggeredStyle != null && triggeredStyle.length() > 2) {
                //  System.out.println("Imposto lo stile da trigger come default: " + triggeredStyle);
                this.objects.get(obj).setTriggeredStyle(triggeredStyle);
            } else {
                this.objects.get(obj).setTriggeredStyle("");
            }

            curCol++;
            this.objects.get(obj).Content.setThisRowModifiable(5);
            String ValoreDaScrivere = this.objects.get(obj).getLabelHeader();
            if (ValoreDaScrivere == null || ValoreDaScrivere == "null" || ValoreDaScrivere.length() < 1) {
                ValoreDaScrivere = this.objects.get(obj).getLabelHeader();
            }
            if (ValoreDaScrivere == null || ValoreDaScrivere == "null" || ValoreDaScrivere.length() < 1) {
                ValoreDaScrivere = this.objects.get(obj).getName();
            }
            if (this.objects.get(obj).C.getType().equalsIgnoreCase("TEXT")) {
                ValoreDaScrivere = ""; // un text su un pannello è una cosa da compilare... deve essere vuoto
            }

            //  System.out.println("obj:" + obj + "  -->" + ValoreDaScrivere);
            //RICAVO VALORE DA SCRIVERE.
            this.objects.get(obj).setValueToWrite(ValoreDaScrivere);
//            System.out.println("fillFormPanel:paintPanelElement(" + this.objects.get(obj).getName() + ")---->" + ValoreDaScrivere);
            if (curCol > this.layoutColumns) {
                curCol = 1;
                htmlCode += "</TR><TR>";
            }
            htmlCode += paintPanelElement(this.objects.get(obj));

        }
        htmlCode += ("</tr></TABLE>\n");
        return htmlCode;
    }

    public String paintPanelElement(smartObject curObj) {

        String htmlCode = "";
        smartRow myRow = new smartRow(this, null, 0);

//        System.out.println("Diritti in base a :" + curObj.Content.getModifiable());
        if (curObj.Content.getModifiable() != null) {
            if (curObj.Content.getModifiable().startsWith("DEFAULT:4")
                    || curObj.Content.getModifiable().startsWith("DEFAULT:5")) {
                curObj.Content.setModifiable("[{\"ruleType\":\"default\",\"right\":\"255\"}]");
//                System.out.println("in paintPanelElement diventa :" + curObj.Content.getModifiable());
            }
        }

        //---DIRITTI--- DETERMINO I DIRITTI PER OGGETTO IN GENERALE (LIVELLO FORM)
        smartObjRight objRights = analyzeRightsRuleJson(curObj.Content.getModifiable(), null, null, 400);
        // objRights.print();
        if (curObj.CG.Type != null && curObj.CG.Type.equalsIgnoreCase("HTML")) {
//System.out.println("PAINT PANEL ELEMENT HTML:" + curObj.getName()  );

            //    System.out.println("VADO in browserArgsReplace per:" + curObj.CG.getValue());
            String code = browserArgsReplace(curObj.CG.getValue());
            //System.out.println("HTML:" + code  );
            htmlCode += code;

        } else {

            htmlCode += "<td style=\"padding-right: 10px;padding-left: 10px;\">";
            //------------------------------------------------- 
            // nel panel il keyvalue viene sostituito dall'ID dell  form   ... tanto per scrivere qualcosa
            // System.out.println("VADO in paintObject per:" + curObj.getName());
//poichè è un pannello non ha una key di riga: quindi uso il nome del pannello (this.ID)
            String panelKeyValue = "PANELelement";

            htmlCode += myRow.paintObject(panelKeyValue, curObj, objRights);
            //------------------------------------------------- 
            htmlCode += "</td>\n";

        }

        return htmlCode;
    }

    public String feedTriggeredStyle(smartObject curObj, ResultSet rs) {
        // rs rappresenta la riga del database da visualizzare
        // le colonne di rs sono le colonne della tabella richiesta dall'utente
        String trigs = curObj.getGes_triggers();
        if (trigs == null || trigs.length() < 2) {
            return "";
            // trigs = "[]";
        }

////////        ResultSetMetaData rsmd;
////////        try {
////////            rsmd = rs.getMetaData();
////////            for (int jj = 0; jj < rsmd.getColumnCount(); jj++) {
////////                String name = rsmd.getColumnName(jj + 1);
////////                // System.out.println("COLONNA " + jj + " ->" + name); 
////////            }
////////        } catch (SQLException ex) {
////////            Logger.getLogger(ShowItForm.class
////////                    .getName()).log(Level.SEVERE, null, ex);
////////        }
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        String TRIGGERSarray = null;
        String triggeredStyle = "";
//        String totalTtriggeredStyle = "";

        String AnalyzedObjType = "";
        String rowField = "";
        String test = "";
        String valueType = "";
        String value = "";
        String style = "";

        String tbsJson = "{\"TRIGGERS\":" + trigs + "}";
        //   System.out.println(tbsJson);
//childType childMarker value
        try {
            jsonObject = (JSONObject) jsonParser.parse(tbsJson);
            TRIGGERSarray = jsonObject.get("TRIGGERS").toString();
            if (TRIGGERSarray != null && TRIGGERSarray.length() > 0) {
                JSONParser parser = new JSONParser();
                Object obj;

                obj = parser.parse(TRIGGERSarray);
                JSONArray array = (JSONArray) obj;
                /*
                         [{"MarkerType":"rowField",
                         "Marker":"rifTipoSomm",
                         "test":"==",
                         "valueType":"text",
                         "value":"ORL",
                         "style":"background-color:grey; " } ]  
                 */

                for (Object riga : array) {
                    bound_Fields myBound = new bound_Fields();
                    jsonObject = (JSONObject) jsonParser.parse(riga.toString());

                    try {
                        AnalyzedObjType = jsonObject.get("AnalyzedObjType").toString();
                    } catch (Exception e) {
                    }
                    try {
                        rowField = jsonObject.get("rowField").toString();
                    } catch (Exception e) {
                    }
                    try {
                        test = jsonObject.get("test").toString();
                    } catch (Exception e) {
                    }
                    try {
                        valueType = jsonObject.get("valueType").toString();
                    } catch (Exception e) {
                    }
                    try {
                        value = jsonObject.get("value").toString();
                    } catch (Exception e) {
                    }
                    try {
                        style = jsonObject.get("style").toString();
                        System.out.println("\n\n>>>>>>>\nstyle: " + style);
                    } catch (Exception e) {
                    }

                    //    System.out.println("rowField: " + rowField + "  --test: " + test + "   -  value: " + value);
//----------------------------------------------------------------------------------------
                    if (AnalyzedObjType.equalsIgnoreCase("rowField") && rs != null) {
                        int flagVerified = 0;
// cerco il valore del field indicato
                        //String marker = 

                        if (valueType.equalsIgnoreCase("INT")) {
                            int xValue = Integer.parseInt(value);
                            try {
                                int dbVal = rs.getInt(rowField);
                                //String nome = rs.getString("nome");
                                System.out.println("dbVal: " + dbVal + "  --test: " + test + "   -  value: " + value);
                                if (test.equalsIgnoreCase("==")) {
                                    if (dbVal == xValue) {
                                        flagVerified = 1;
                                    }
                                } else if (test.equalsIgnoreCase(">")) {
                                    if (dbVal > xValue) {
                                        flagVerified = 1;
                                    }
                                } else if (test.equalsIgnoreCase("<")) {
                                    if (dbVal < xValue) {
                                        flagVerified = 1;
                                    }
                                } else if (test.equalsIgnoreCase(">=")) {
                                    if (dbVal >= xValue) {
                                        flagVerified = 1;
                                    }
                                } else if (test.equalsIgnoreCase("<=")) {
                                    if (dbVal <= xValue) {
                                        flagVerified = 1;

                                    }
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(ShowItForm.class
                                        .getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {

                        }

                        if (flagVerified > 0) {
                            triggeredStyle += (style);
//                            System.out.println("IMPOSTO TRIGGERED STYLE : " + triggeredStyle);
                        } else {
                            //triggeredStyle = "";
                        }

                    } else if (AnalyzedObjType.equalsIgnoreCase("defaultStyle")) {
                        // prendo style...    [{"AnalyzedObjType":"defaultStyle","style":"background-color:grey; " } ]                    
                        curObj.C.setDefaultStyle(style);
                        triggeredStyle += (style);
//                        System.out.println(" TRIGGERED STYLE : " + triggeredStyle);
                    }
                }
            }
        } catch (ParseException ex) {
            System.out.println("error in line 2082;:" + ex.toString());

        }
        return triggeredStyle;
    }

    public String paintTopBar() {
        String htmlCode = "";

        htmlCode += "";

        //   System.out.println("JSON PARSING:" + this.getGes_topBar());
        String topColor = "grey";
        String topHeight = "30px";
        String topWidth = "100%";
        String togglerIcon = "";
        String printIcon = "";
        String formIcon = "";
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        if (this.getGes_topBar() != null && this.getGes_topBar().length() > 0) {
            try {
                jsonObject = (JSONObject) jsonParser.parse(this.getGes_topBar());

                try {
                    topColor = jsonObject.get("baseColor").toString();
                } catch (Exception e) {
                }
                try {
                    topHeight = jsonObject.get("height").toString();
                } catch (Exception e) {
                }
                try {
                    togglerIcon = jsonObject.get("togglerIcon").toString();
                } catch (Exception e) {
                }
                try {
                    printIcon = jsonObject.get("printIcon").toString();
                } catch (Exception e) {
                }
                try {
                    formIcon = jsonObject.get("formIcon").toString();

                } catch (Exception e) {
                }
            } catch (ParseException ex) {
            }
        }

        if (!topHeight.equalsIgnoreCase("none")) {

            // riga del TOP 
            htmlCode += "<tr><td>";
            int togglerHeight = 10;
            String xTopHeight = topHeight;
            try {
                xTopHeight = topHeight.replace("px", "");
            } catch (Exception e) {

            }
            togglerHeight = Integer.parseInt(xTopHeight) - 5;
//T2 OPEN
            htmlCode += "<TABLE ";
            htmlCode += " style=\"width:" + topWidth + ";"
                    //  + "background-image:url(http://ran.ge/content/uploads/2009/11/logo-trimmed.png);\n" 
                    + " display:block;border: 0;\" >";
            htmlCode += "<tr><td>"; // xxxxxxxxxxxxxxxxxxxxx  table che contiene tutti i child
            htmlCode += ("<div "
                    //   + " class=\"topTab\" "
                    + " id=\"CH-" + this.getID() + "-" + this.getCopyTag() + "-T\" ");
            htmlCode += " style=\"width:" + topWidth + ";"
                    + "height:" + topHeight + ";"
                    + "background:" + topColor + ";border-top-right-radius: 15px;border-top-left-radius: 15px;\" ";
            htmlCode += "> ";
//-------------------------------------
//T3 OPEN
            htmlCode += ("<TABLE "
                    + "style= \""
                    //+ "height:"+ this.getFormHeight()+"; "
                    + "width: " + this.getFormWidth() + ";  border: 0;\" >"
                    + "<TR> ") // tble che contiene la riga 1
                    ;
            if (this.getLabel() == null || this.getLabel() == "null") {
                this.setLabel(this.getName());
            }
            String key = this.getFatherKEYvalue();

            htmlCode += "<td style=\"font-size:20px; padding-right:3px; \"> ";

            if ((togglerIcon != null && togglerIcon.equalsIgnoreCase("true")) || (this.getShowToggler() != null && this.getShowToggler().equalsIgnoreCase("TRUE"))) {
                htmlCode += " <img style=\"margin:0px 5px 0px 0px;\" "
                        //                        + "id=\"CH-" + this.getID() + "-" + this.getCopyTag() + "-TOGGLERIMG\" "
                        + "id=\"" + this.getName() + "-TOGGLERIMG\" "
                        + " height=\"" + togglerHeight + "px\"  align=\"left\" src='./media/icons/gaiaTopHide.png' alt='S/H'"
                        + " onclick=\"javascript:smartToggleBody('" + this.getName() + "','" + this.getID() + '-' + this.getCopyTag() + "')\" >";
            }
            htmlCode += "</td>";

            if (this.getType().equalsIgnoreCase("TABLE")) {

                htmlCode += "<td style=\"font-size:20px; padding-right:3px; \"> ";

                if (printIcon.equalsIgnoreCase("true")) {
                    htmlCode += " <img style=\"margin:0px 5px 0px 0px;\" id=\"CH-" + this.getID() + "-" + this.getCopyTag() + "-PRINTFORM\" "
                            + " height=\"" + togglerHeight + "px\"  align=\"left\" src='./media/icons/gaiaTopPrint.png' alt='PRINT'"
                            + " onclick=\"javascript:printFormReport('" + this.getID() + "','" + this.getCopyTag() + "','" + this.getName() + "')\" >";

                }
                htmlCode += "</td>";
            }

            //----------------------------           
            if (formIcon.equalsIgnoreCase("true")) {
                htmlCode += "<td style=\"font-size:20px; padding-right:3px; \"> ";
                UUID idOne = null;
                idOne = UUID.randomUUID();
                htmlCode += "<DIV"
                        + " id=\"\""
                        + ">";
                String usedKeyField = "ID";
                String usedKeyValue = this.ID;
                String usedKeyType = "TEXT";

                String image = "<img  alt=\"...\" src='portal?rnd=" + idOne + "&target=requestsManager&gp=";
                String params = "\"params\":\"" + encodeURIComponent(myParams.makePORTALparams()) + "\"";
//            String connectors = "\"connectors\":[{\"door\":\"RenderPic\","
//                    + "\"event\":\"fromDB\","
//                    + "\"table\":\"" + curObj.Origin.getQuery() + "\","// es operatori
//                    + "\"keyfield\":\"" + usedKeyField + "\","//es operatori.ID
//                    + "\"keyValue\":\"" + usedKeyValue + "\","// es 'pippo'
//                    + "\"keyType\":\"" + usedKeyType + "\","
//                    + "\"picfield\":\"" + curObj.Origin.getValueField() + "\" "//es. media
//                    + " }]";
                String connectors = "\"connectors\":[{\"door\":\"RenderPic\","
                        + "\"event\":\"fromDB\","
                        + "\"table\":\"" + mySettings.getLocalFE_forms() + "\","// es operatori
                        + "\"keyfield\":\"ID\","//es operatori.ID
                        + "\"keyValue\":\"" + this.ID + "\","// es 'pippo'
                        + "\"keyType\":\"TEXT\","
                        + "\"picfield\":\"picture\" "//es. media
                        + " }]";
//                System.out.println("OGGETTO PICTURE->" + connectors);
                String utils = "\"responseType\":\"text\"";
                String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
                image += encodeURIComponent(gp);
                image += "'  width='30px' heigth='30px' >";
                htmlCode += image;
                htmlCode += "</DIV>";
                htmlCode += "</td>";
            }
//---------------------------
            htmlCode += "<td style=\"font-size:20px; padding-right:10px; \"> ";
            String label = this.getLabel();
            if (label == null) {
                label = " ";
            }
            try {
                label = browserArgsReplace(this.getLabel());
            } catch (Exception e) {

            }

            htmlCode += label;
            htmlCode += "</td>";
            htmlCode += "<td style=\"font-size:10px; padding-right:10px; \"> ";
            if (key != null && key.length() > 0) {
                htmlCode += " <font size='1'>[rif." + key + "]</font>";
            }

            htmlCode += "</td> ";

            htmlCode += (" </tr></TABLE>");
//T3 CLOSE

            htmlCode += (" </div>");
            htmlCode += ("</td></tr></TABLE>");
            htmlCode += ("</td></tr>");

        }

        // ======================= SNACKBAR  =============================================0  
        htmlCode += (" <tr><td><div  id=\"" + this.getID() + "-" + this.getCopyTag() + "-SNACKBAR\" ></div></td></tr>");

        // ======================= SearchBOX =============================================0
        if (!this.type.equalsIgnoreCase("PANEL")
                && !this.type.equalsIgnoreCase("FILTER")
                && !this.type.equalsIgnoreCase("FILTERPANEL")
                && !this.type.equalsIgnoreCase("MLS")
                && this.hasSearchFilter > 0) {
            htmlCode += "<tr><td> ";
            htmlCode += "<FORM> ";

            htmlCode += ("Cerca: <INPUT type=\"TEXT\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-FILTER\" "
                    + " onChange=\"javascript:filterChanges('" + this.getID() + "','" + this.getCopyTag() + "')\" "
                    + "  onkeydown = \"if (event.keyCode == 13) {"
                    + "document.getElementById('" + this.getID() + "-" + this.getCopyTag() + "-SEARCHBUTTON').click();"
                    + "event.returnValue=false;"
                    + "event.cancel=true;}\" "
                    + "value=\"\"  >\n");
            htmlCode += ("<INPUT type=\"BUTTON\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-SEARCHBUTTON\" value=\" Vai !\" "
                    + "onClick=\"javascript:filterChanges('" + this.getID() + "','" + this.getCopyTag() + "')\" >\n");
            htmlCode += "</FORM> ";
            htmlCode += "</td></tr> ";
        }
//        System.out.println("Concluso paintTopBar");
        return htmlCode;
    }

    public String paintFormConsole() {
        String htmlCode = "";
//        System.out.println("this.formObjects.size(): " + this.formObjects.size());
        if (this.formObjects.size() > 0) {
            htmlCode += ("<tr><td>");
            htmlCode += ("<TABLE "
                    + "style=\"border-collapse: collapse;border:0; padding: 0; margin: 0;  \""
                    + "><tr>");
            for (int jj = 0; jj < this.formObjects.size(); jj++) {
//                System.out.println("DISEGNO OGGETTO IN CONSOLE: " + this.formObjects.get(jj).getName());
                smartObjRight objRights = analyzeRightsRuleJson(this.formObjects.get(jj).Content.getModifiable(), DATArs, null, 400);
                htmlCode += ("<td>");

                smartRow myRow = new smartRow(this, null, -2);
                if (this.formObjects.get(jj).C.Type.equalsIgnoreCase("TEXT")) {
                    this.formObjects.get(jj).setValueToWrite(this.formObjects.get(jj).getDefaultValue());
                }
                htmlCode += myRow.paintObject(this.getID() + "-" + this.getCopyTag(), this.formObjects.get(jj), objRights);

                htmlCode += ("</td>");
            }
            htmlCode += ("</tr></TABLE>");
            htmlCode += ("</td></tr>");
        }

        return htmlCode;
    }

    public String paintFormAbstract() {
        //==============================================================================
        String htmlCode = "";
        String abst = this.getAbstractTextCode();
        if (abst == null || abst.equalsIgnoreCase("null")) {
            abst = "";
        }
        htmlCode += "<DIV style=\"background-color: grey;\" >";
        htmlCode += abst;
        htmlCode += "</DIV>";

        return htmlCode;
    }

    public String paintFormAttributes() {
        //==============================================================================
        String htmlCode = "";

        if (this.getSendToCRUD() == null) {
            this.setSendToCRUD("");
        }
//        System.out.println("paintFormAttributes=====> SQL:\n" + queryUsed);

        String rootID = this.getID() + "-" + this.getCopyTag();
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.name + "-" + this.getCopyTag() + "-ID\" value=\"" + this.getID() + "-" + this.getCopyTag() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-FATHER\" value=\"" + this.getFather() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-FATHERARGS\" value=\"" + this.getFatherFilters() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-FATHERKEYVALUE\" value=\"" + this.getFatherKEYvalue() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-FATHERKEYTYPE\" value=\"" + this.getFatherKEYtype() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-FATHERCOPYTAG\" value=\"" + this.getFatherCopyTag() + "\">\n");

        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-FIELDFILTERED\" value=\"" + this.getFilteredElements() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-TOBESENT\" value=\"" + encodeURIComponent("" + this.getToBeSent() + "") + "\">\n");

        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-EXTENDEDNAME\" value=\"" + this.getName() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-FILSEQ\" value=\"" + this.getFilterSequence() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-KEYfieldName\" value=\"" + this.getKEYfieldName() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-KEYfieldType\" value=\"" + this.getKEYfieldType() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-ROUTINEONLOAD\" value=\"" + this.getGes_routineOnLoad() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-KEYfieldValue\" value=\"\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-ADDPOS\" value=\"" + this.addRowPosition + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-COLLAPSE\" value=\"" + this.collapseOnExpand + "\">\n");

        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-QUERYUSED\" value=\"" + encodeURIComponent(queryUsed) + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-STC\" value=\"" + encodeURIComponent(this.getSendToCRUD()) + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-CURPAGE\" value=\"1\">\n");
        JSONArray fieldsArray = new JSONArray();
        if (this.filteredElements != null) {
            String[] fields;
            fields = this.filteredElements.split(";");
            List<String> filtri = Arrays.asList(fields);
//            System.out.println(" filtri.size:" + filtri.size());

            for (int i = 0; i < this.objects.size(); i++) {
                JSONObject field = new JSONObject();
                field.put("name", this.objects.get(i).getName());
                //--------------------------
                if (this.objects.get(i).actuallyVisible <= 0) {
                    field.put("visible", "false");
                } else {

                    field.put("visible", "true");
                }
                //--------------------------
                boolean filtered = false;
                for (int f = 0; f < filtri.size(); f++) {
                    if (filtri.get(f).equalsIgnoreCase(this.objects.get(i).getName())) {
                        filtered = true;
                        break;
                    }
                }
                if (filtered == true) {
                    field.put("filtered", "true");
                } else {
                    field.put("filtered", "false");
                }
                fieldsArray.add(field);
            }
        }

        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + rootID + "-FIELDMAP\" value=\"" + encodeURIComponent(fieldsArray.toString()) + "\">\n");

// htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-SENDTOCRUD\" value=\"" + encodeURIComponent("" + this.getInfoReceived() + "") + "\">\n");
        //   System.out.println("Cerco campi relativi al FORM da inserire su richiesta del StC:" + this.getSendToCRUD());
        // in base al SENDTOCRUD devo creare dei valori relativi al FORM
        // TBS cerco il valore di >panelf8b2c-X-UTENTE-FORM
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        String TBSarray = null;
        String xchildType = null;
        String xchildMarker = null;
        String xvalue = null;
        if (this.getSendToCRUD() != null && this.getSendToCRUD().length() > 0) {
            String tbsJson = "{\"TBS\":" + this.getSendToCRUD() + "}";
            // System.out.println(tbsJson);
//childType childMarker value
            try {
                jsonObject = (JSONObject) jsonParser.parse(tbsJson);
                TBSarray = jsonObject.get("TBS").toString();
                if (TBSarray != null && TBSarray.length() > 0) {
                    JSONParser parser = new JSONParser();
                    Object obj;

                    obj = parser.parse(TBSarray);
                    JSONArray array = (JSONArray) obj;

                    for (Object riga : array) {
                        bound_Fields myBound = new bound_Fields();
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
//panelFilter
                        if (xchildType != null
                                && (xchildType.equalsIgnoreCase("formField")
                                || xchildType.equalsIgnoreCase("panelFilter"))
                                && xvalue != null && xchildMarker != null) {
                            htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + xchildMarker + "-FORM\" value=\"" + xvalue + "\">\n");

                        }

                    }
                }
            } catch (ParseException ex) {
                System.out.println("error in line 2082");
            }
        }

//==============================================================================
        return htmlCode;
    }

    public String getCodePageSelector(int rowsCounter, int totalPages) {

        String pagesCode = "";
        //=====PAGES=================================================
        if (rowsCounter > rowsPerPage) {
//            pagesCode += "<TABLE><tr><td>";
            // devo creare i pulsanti di navigazione

            pagesCode += "<DIV "
                    + "style=\""
                    + " width: " + this.formWidth + "; background-color: grey;"
                    + "overflow-x: scroll;"
                    + "\">";
            pagesCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-CURPAGE\" "
                    + "value=\"" + this.getCurrentPage() + "\">";
            pagesCode += "<TABLE  "
                    + "style=\""
                    //                    + " width: 100%;"
                    + " font-family: verdana, arial, helvetica, sans-serif;\n"
                    + " font-size: 9px;\n"
                    + " cellspacing: 0; \n"
                    + "overflow-x: auto;"
                    + "\"><tr>";
            pagesCode += "<td>PAGES: </td>";
            for (int hh = 1; hh <= totalPages; hh++) {
                pagesCode += "<td><div  ";
                pagesCode += "style=\""
                        + " font-size:9px;"
                        + " text-align: center;\n"
                        + " display:block;"
                        + " width:20px;"
                        + " height:20px;";
                if (hh == currentPage) {
                    pagesCode += " background:lightYellow;"
                            + "    border-collapse: collapse; \n"
                            + "    border-right: 2px solid #9; \n"
                            + "    border-bottom: 2px solid #9; \n"
                            + "    border-top: 1px solid #999; \n"
                            + "    border-left: 1px solid #999; \n";
                } else {
                    pagesCode += " background:lightGrey;"
                            + "    border-collapse: collapse; \n"
                            + "    border-right: 1px solid #999; \n"
                            + "    border-bottom: 1px solid #999; \n"
                            + "    border-top: 2px solid #9; \n"
                            + "    border-left: 2px solid #9; \n";

                }
                pagesCode += "\"   "
                        + "onclick='javascript:smartChangeCurPage(\"" + this.getID() + "\",\"" + this.getCopyTag() + "\"," + hh + ")'";
                pagesCode += "  >";
                pagesCode += hh;

                pagesCode += "</div></td>";

            }
            pagesCode += "</tr></TABLE>";
            pagesCode += "</DIV>";
//            pagesCode += "</td></tr></TABLE>";

        }
//========================================= 
        return pagesCode;
    }

    public String getCodeRows() {
        String rowsCode = "";
        rowsPerPage = this.maxRows;

//          System.out.println("\n #### fillFormData_formTable >>this.maxRows:" + this.maxRows);
        if (rowsPerPage < 1) {
            rowsPerPage = 20;
        }
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        ResultSet rs;
        // cerca il FORM per nome e se non è compilato per ID

//        System.out.println("query: " + this.query);
//        System.out.println("fillFormData_formTable queryUsed: " + this.queryUsed);
        if (this.queryUsed == null || this.queryUsed == "") {
            return "ERROR LOADING FORM TABLE.";
        }
        righeScritte = 0;
        rowsCounter = 0;
        totalPages = 0;
        try {
            System.out.println("\n #### fillFormData_formTable >>SQLphrase DATI:" + this.queryUsed);

//=====CONTO LE RIGHE TOTALI=================================================
            Statement s = conny.prepareStatement(this.queryUsed,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY); //questo serve per poter scorrere i record e riocomeinciare da capo
            rs = s.executeQuery(this.queryUsed);

//            System.out.println("query eseguita");
            try {

                rowsCounter = 0;

                try {
                    while (rs.next()) {
                        rowsCounter++;
                    }
                    rs.beforeFirst();
                } catch (Exception e) {
                    System.out.println("errore in conteggio righe totali:" + e.toString());
                }
//                System.out.println("conteggio righe totali:" + rowsCounter);

                float rawTotaPages = (float) rowsCounter / rowsPerPage;
                totalPages = (int) Math.floor(rowsCounter / rowsPerPage);
                if (rawTotaPages > totalPages) {
                    totalPages++;
                }

                if (this.getVisualType().equalsIgnoreCase("FULLFORM")) {
                    if (this.addRowPosition != null && this.addRowPosition.equalsIgnoreCase("TOP")) {
                        currentPage = 1;
                    } else {
                        currentPage = totalPages;
                    }
                }

                if (currentPage < 1) {
                    currentPage = 1;
                }
                if (currentPage > totalPages) {
                    currentPage = totalPages;
                }
                if (rowsPerPage < 1 || rowsPerPage > 150) {
                    rowsPerPage = 30;
                }

                int lines = 0;

//----------INIZIO RIGHE DATI
                int splitterPagesEnabled = 1;
                righeScritte = 0;

                for (int jj = 0; jj < this.objects.size(); jj++) {
                    this.objects.get(jj).objRights = analyzeRightsRuleJson(this.objects.get(jj).Content.getModifiable(), null, null, 400); //curObj.objRights=analyzeRightsRuleJson(curObj.Content.getModifiable(), null);
                }

//CICLO RIGHE TABELLA=========================================
                while (rs.next()) {
                    //------------------------------------ 
//                System.out.println("righeScritte :" + righeScritte);
//                System.out.println("rowsPerPage :" + rowsPerPage);
//                System.out.println("currentPage :" + currentPage);
//                System.out.println("splitterPagesEnabled :" + splitterPagesEnabled);
                    righeScritte++;
//                    System.out.println("\n::::::::::::::::\nSCRIVO RIGA TABELLA. n." + righeScritte);
                    for (int jj = 0; jj < this.objects.size(); jj++) {
//-----------------------------------------------
//--ESEGIUO LE SOMME DELLE COLONNE CON TOTALI----             
//-----------------------------------------------               
//                     System.out.println("++oggetto "+this.objects.get(jj).getName()+" - HAS SUM ="+ this.objects.get(jj).Content.getHasSum() );
                        if (this.objects.get(jj).Content.getType() != null
                                && this.objects.get(jj).Content.getType().equalsIgnoreCase("INT")
                                && this.objects.get(jj).Content.getHasSum() > 0) {
                            int partial = this.objects.get(jj).Content.getActualSum();
                            int thisValue = 0;
                            try {
                                thisValue = rs.getInt(this.objects.get(jj).getName());
                            } catch (Exception e) {
                                thisValue = 0;
                            }
                            this.objects.get(jj).Content.setActualSum(partial + thisValue);
//                        System.out.println("Tab_partial " + this.objects.get(jj).Content.getActualSum());

                        }
                    }

                    if (righeScritte > (rowsPerPage * currentPage) && splitterPagesEnabled > 0) {
                        // eseguo comunque la somma per i totali
                    } else if (righeScritte <= (rowsPerPage * (currentPage - 1)) && splitterPagesEnabled > 0) {
                        // queste righe sono precedenti alla pagina che mi interessa... non le scrivo
                    } else {
                        lines++;
//                        System.out.println("  riga:" + lines);
//NORMAL ROW=========================================    
//                    try {
                        smartRow myRow = new smartRow(this, rs, righeScritte);

                        smartObjRight rowRights = myRow.valutaRightsRiga(this.getDisableRules(), rs);/// analizzo il LOCKER del form per la riga
                        smartObjRight actualRowRights = joinRights(formRightsRules, rowRights);

                        myRow.setActualRowRights(actualRowRights);
                        myRow.setFormRightsRules(formRightsRules);
//                        System.out.println("------------------------riga:" + lines);
                        normalRowsCode += myRow.SMRTpaintRow("normal");
//                        System.out.println("------------------------fine riga:" + lines);
//                    } catch (Exception e) {
//                        
//                        System.out.println("errore riga:" + lines+":"+e.toString());
//                        e.printStackTrace();
//                    }

                    }
                }

//                System.out.println("Righe scritte a video:" + lines);
            } catch (Exception e) {

                System.out.println("err 2028" + e.toString());
            }
//            System.out.println("Righe da query:" + righeScritte);
//            System.out.println("normalRowsCode:" + normalRowsCode);
////            

            smartRow myRow = new smartRow(this, null, 0);
            myRow.setFormRightsRules(formRightsRules);

            totalRowCode = myRow.SMRTpaintRow("total");
            addingRowCode = myRow.SMRTpaintRow("adding");

            if (this.getHtmlPattern() != null && this.getHtmlPattern().length() > 0) {
                totalRowCode = "<TR><TR>";
            }

//            colsNamesCode = "";
            //mostro i TOTALI DEGLI OGGETTI CON HASsUM
            for (int jj = 0; jj < this.objects.size(); jj++) {
                if (this.objects.get(jj).Content.getType() != null
                        && this.objects.get(jj).Content.getType().equalsIgnoreCase("INT")
                        && this.objects.get(jj).Content.getHasSum() > 0) {
                }
            }

            pageSelectorCode = getCodePageSelector(rowsCounter, totalPages);

            rowsCode = "<div "
                    + " class=\"tabBody\""
                    + " id=\"" + this.getID() + "-" + this.getCopyTag() + "-ROWSDIV\" "
                    + "style=\" height:" + this.getFormHeight() + "; "
                    //                    + " background-color: coral;  "
                    + "resize: vertical;\n"
                    + "    overflow: auto; \""
                    + ">";

            rowsCode += "<TABLE id=\"" + this.getID() + "-" + this.getCopyTag() + "-TABLE\"   >";
            rowsCode += "<TR><TD>";

            rowsCode += pageSelectorCode;

            rowsCode += "</TD></TR>";
            rowsCode += "</TABLE>";
            rowsCode += "<TABLE id=\"" + this.getID() + "-" + this.getCopyTag() + "-ROWSTABLE\"   > <tbody>";
            if (this.addRowPosition != null && this.addRowPosition.equalsIgnoreCase("TOP")) {
                //===CASO FORMMASK CON ADD IN ALTO
                if (formRightsRules.canCreate > 0) {
                    rowsCode += addingRowCode;
                }
                rowsCode += totalRowCode;
                rowsCode += normalRowsCode;
            } else {
                rowsCode += normalRowsCode;
                rowsCode += totalRowCode;
                if (formRightsRules.canCreate > 0) {
                    rowsCode += addingRowCode;
                }
            }
            rowsCode += "</tbody> </TABLE>";
            rowsCode += "</TD></TR>";
            rowsCode += "</div>";
            //System.out.println("Righe DATI:" + lines);
        } catch (SQLException ex) {

            System.out.println("query error:" + ex.toString());
        }
        try {
            conny.close();

        } catch (SQLException ex) {
        }
//----------FINE RIGHE DATI
        return rowsCode;
    }

    public String getCodeHeader() {
        String htmlCode = "";
        htmlCode += "<div "
                + " class=\"tabBody\""
                + " id=\"" + this.getID() + "-" + this.getCopyTag() + "-HDRDIV\" "
                + "style=\" background-color: grey;  "
                + "    overflow: auto; "
                + "\" >";

        //HEADER======================================= 
//        System.out.println("\nthis.showHeader:" + this.showHeader);
        if (this.advancedFiltered != null && this.advancedFiltered.equalsIgnoreCase("TRUE")) {
//            System.out.println(" this.advancedFiltered:" + this.advancedFiltered);
            htmlCode += paintAdvancedFilters();
        } else {
            if ((this.showHeader != null && this.showHeader.equalsIgnoreCase("false"))
                    || (this.getHtmlPattern() != null && this.getHtmlPattern().length() > 0)) {
                htmlCode += "";
            } else {
                htmlCode += paintHeader();
            }
        }
//=================================== 
        htmlCode += "</div>";
        return htmlCode;
    }

    public String paintAdvancedFilters() {

        String htmlCode = "";

        htmlCode += "<TABLE border=\"1\" style=\" table-layout: fixed; width: " + this.totalWidth + ";"
                + "border-spacing: 0;  border-collapse: collapse; border: 1px solid black; background-color: grey;\">";
//        htmlCode +=  "<tr><td>";
        htmlCode += getCodeColumnsNamesRow(0);
        htmlCode += getFiltersRow();
//        htmlCode += "</td></tr>";
        htmlCode += "</TABLE>";
        return htmlCode;
    }

    public String getFiltersRow() {
        String htmlCode = "";

        List<String> filtri = null;
        if (this.filteredElements != null) {
            String[] fields;
            fields = this.filteredElements.split(";");
            filtri = Arrays.asList(fields);
//            System.out.println(" filtri.size:" + filtri.size());

            for (int i = 0; i < filtri.size(); i++) {
                String field = filtri.get(i);
                if (field.contains("[")) {
                    int start = field.indexOf("[");
                    String newField = field.substring(0, start);
                    filtri.set(i, newField);

//                    System.out.println("field: "  field + " --> filtri.get(i): " + filtri.get(i));
                }
//                System.out.println(i + ") FILTRO:" + filtri.get(i));
            }
        }
        htmlCode += "<tr>";
        if (this.getShowCounter() != null && this.getShowCounter().equalsIgnoreCase("FALSE")) {

            htmlCode += "<td>";
            htmlCode += "</td>";
        } else {
            //==================================================================    
            // htmlCode += "<td style=\"overflow-x: hidden; width:50px\" >";
            htmlCode += "<td "
                    //                    + " class=\"headerSelector\" "
                    + "style=\"width:40px;\""
                    + ">";

            for (int obj = 0; obj < this.objects.size(); obj++) {
                htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + this.objects.get(obj).name + "-TYPE\" "
                        + "value=\"" + this.objects.get(obj).Content.getType() + "\">";
            }
            htmlCode += "</td>";
        }
        //==================================================================    
        if (formRightsRules.canDelete > 0) {
            htmlCode += "<td class=\"lineDeleter\" style=\"overflow-x: hidden; \" > </td>";
        }

        //==================================================================    
        for (int obj = 0; obj < this.objects.size(); obj++) {

            if (!this.objects.get(obj).CG.getType().equalsIgnoreCase("FORMBUTTON")) {
                //Ricavo valorer da scrivere in intestazione
                String fieldName = this.objects.get(obj).getLabelHeader();

                if (fieldName == null || fieldName == "") {
                    fieldName = this.objects.get(obj).getName();
                }
                try {
                    fieldName = fieldName.toUpperCase();
                } catch (Exception ex) {
                    fieldName = "";
                }
                boolean objVisibile = true;
                if (this.objects.get(obj).objRights.canView < 1) {
                    objVisibile = false;
                }

                //questo lo metto per compatibilità col vecchiop sistema
                if (this.objects.get(obj).visible.equalsIgnoreCase("DEFAULT:0")) {
                    objVisibile = false;
                }

                //-------------------------
                // scrivo
                boolean filtered = false;
                if (filtri != null && filtri.size() > 0) {
                    for (int i = 0; i < filtri.size(); i++) {
                        if (this.objects.get(obj).getName().equalsIgnoreCase(filtri.get(i))) {
                            filtered = true;
                            break;
                        }
                    }
                }

                htmlCode += "<td "
                        //                    + "class=\"tabHeader\" "
                        + " style=\"";
                String myWidth = "";
                if (objVisibile == false) {
                    htmlCode += "  display:none;  ";
                } else {

//                if (this.objects.get(obj).C.getWidth() != null && this.objects.get(obj).C.getWidth() != "null" && this.objects.get(obj).C.getWidth() != "") {
//                    myWidth = this.objects.get(obj).C.getWidth();
//                    myWidth = myWidth.replace("px", "");
//                    int newValue = Integer.parseInt(myWidth);
//                    // tolgo lo spazio per cornice più grossa
//                    if (newValue > 2) {
//                        newValue = newValue - 2;
//                        myWidth = newValue + "px";
//                    }
//
//                }
//                htmlCode += " width:" + myWidth + ";  ";
                }
                htmlCode += "  overflow-x: hidden;\">";
//-----------------------------------------------------------            

                if (objVisibile == true) {

                    htmlCode += "<TABLE style = \"border-spacing: 0;  border-collapse: collapse;\"><TR><TD>";
                    htmlCode += paintOrderArrows(this.objects.get(obj).getName());
                    htmlCode += "</TD><TD>";

                    if (filtered == true) {
                        myWidth = this.objects.get(obj).C.getWidth();
                        myWidth = myWidth.replace("px", "");
                        int newValue = Integer.parseInt(myWidth);
                        if (newValue > 15) {
                            newValue = newValue - 15;
                        }
                        myWidth = newValue + "px";
                        // qui cambio filtro secondo il tipo di field
                        htmlCode += ("<INPUT type=\"TEXT\" "
                                + "id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + this.objects.get(obj).getName() + "-FILTER\" "
                                //                    + " onChange=\"javascript:filterChanges('" + this.getID() + "','" + this.getCopyTag() + "')\" "
                                + "style=\"background-color:lightBlue;   "
                                + " width:" + myWidth + ";  \""
                                + "onkeydown = \""
                                + "if (event.keyCode == 13) {"
                                + "smartFilterChanges('" + this.getID() + "','" + this.getCopyTag() + "') ; "
                                + "event.returnValue=false;"
                                + "event.cancel=true;"
                                + "}"
                                + "\" "
                                + "value=\"\"  >\n");

                    }
                    htmlCode += "</TD>";
                    htmlCode += "</TR></TABLE>";
                }
                htmlCode += "</td>";

            }
        }
        htmlCode += "</tr>";
        return htmlCode;
    }

    public String paintOrderArrows(String objName) {
        String htmlCode = "";

        htmlCode += "<TABLE  style = \"border-spacing: 0;  border-collapse: collapse;\"><TR><TD>";
        htmlCode += "<a onclick=\"smartFilterChanges('" + this.getID() + "', '" + this.getCopyTag() + "', '" + objName + "')\"> ";

        htmlCode += "<img id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + objName + "-ARROW\"  "
                + "height=\"7\" width=\"7\" align=\"middle\" src='./media/icons/orderASC.png' alt='^' ></img>";
        htmlCode += "</a>";
        htmlCode += "</TD></TR></TABLE>";
        htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + objName + "-ORDPOS\" value=0>";
        htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + objName + "-ORDDIR\" value=\"A\">";

        return htmlCode;
    }

    public String paintHeader() {
        String htmlCode = "";

        htmlCode += "<TABLE border=\"1\" style=\" table-layout: fixed; width: " + this.totalWidth
                + "px; border-spacing: 0;  border-collapse: collapse; border: 2px solid black;\">";
//        htmlCode +=  "<tr><td>";
        htmlCode += getCodeColumnsNamesRow(1);
//        htmlCode += "</td></tr>";
        htmlCode += "</TABLE>";
        return htmlCode;
    }

    public String getCodeColumnsNamesRow(int offset) {
        int lineSelectorWidth = 40;
        String Code = "<tr>";
        if (this.getShowCounter() != null && this.getShowCounter().equalsIgnoreCase("FALSE")) {
            Code += "<td></td>";
        } else {
            //==================================================================    
            // colsNamesCode += "<td style=\"overflow-x: hidden; width:50px\" >";
            Code += "<td "
                    //                    + "class=\"headerSelector\" "
                    + "style=\"width:" + lineSelectorWidth + "px;\">";

            for (int obj = 0; obj < this.objects.size(); obj++) {
                Code += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + this.objects.get(obj).name + "-TYPE\" "
                        + "value=\"" + this.objects.get(obj).Content.getType() + "\">";
            }
            Code += "</td>";
        }
        //==================================================================    
        if (formRightsRules.canDelete > 0) {
            Code += "<td class=\"lineDeleter\" style=\"overflow-x: hidden; \" > </td>";
        }

        //==================================================================    
        for (int obj = 0; obj < this.objects.size(); obj++) {

            if (!this.objects.get(obj).CG.getType().equalsIgnoreCase("FORMBUTTON")) {
                //Ricavo valorer da scrivere in intestazione
                String fieldName = this.objects.get(obj).getLabelHeader();
                if (fieldName == null || fieldName == "") {
                    fieldName = this.objects.get(obj).getName();
                }
                try {
                    fieldName = fieldName.toUpperCase();
                } catch (Exception ex) {
                    fieldName = "";
                }

                boolean objVisibile = true;
                if (this.objects.get(obj).objRights.canView < 1) {
                    objVisibile = false;
                }

                //questo lo metto per compatibilità col vecchiop sistema
                if (this.objects.get(obj).visible.equalsIgnoreCase("DEFAULT:0")) {
                    objVisibile = false;
                }

                //-------------------------
                // scrivo
//              System.out.println("\nINTESTAZIONE:" + fieldName);
                String intestazioneColonna = fieldName;
                Code += "<td"
                        //                    + " class=\"tabHeader\""
                        + "  style=\"";
                if (objVisibile == false && !this.objects.get(obj).visible.equalsIgnoreCase("DEFAULT:1")) {
                    Code += " width:-1px; display:none; ";
                    intestazioneColonna = "";
                } else {
                    String myWidth = "";
                    if (this.objects.get(obj).C.getWidth() != null && this.objects.get(obj).C.getWidth() != "null"
                            && this.objects.get(obj).C.getWidth() != "") {
                        myWidth = this.objects.get(obj).C.getWidth();
                        myWidth = myWidth.replace("px", "");
                        int newValue = Integer.parseInt(myWidth);
                        if (newValue > offset) {
                            newValue = newValue + offset;
                        }
                        myWidth = newValue + "px";
                        Code += " width:" + myWidth + ";  ";

                    }

                }
                Code += "  overflow-x: hidden; text-align:center; \">";
//            if (objVisibile == true) {

                Code += "<B>" + intestazioneColonna + "</B>";
//            }
                Code += "</td>";

            }
        }
        Code += "</tr>";

//        System.out.println("Code:" + Code);
        return Code;
    }

    //------------------------------------------
    public String prepareSQL(String myquery) {
        String SQLphrase = "";
        this.setToBeSent(browserArgsReplace(this.getToBeSent()));

//        System.out.println("prepareSQL-SQL FORM era:" + myquery);
        SQLphrase = browserArgsReplace(myquery);
//        System.out.println("prepareSQL-SQL FORM diventa:" + SQLphrase);
        queryUsed = SQLphrase;
        return SQLphrase;
    }

    public void makeQualifiedQuery() {
        String selezione;
        String condizioni = "";
        String coda = "";
        String qry = this.getQuery();
//        System.out.println("\nmakeQualifiedQuery()=" + this.getQuery());

        if (this.query != null && this.query.length() > 1) {
            /*
             String text = "0123hello9012hello8901hello7890";
             String word = "hello";

             System.out.println(text.indexOf(word)); // prints "4"
             System.out.println(text.lastIndexOf(word)); // prints "22"

             // find all occurrences forward
             for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
             System.out.println(i);
             } // prints "4", "13", "22"

             // find all occurrences backward
             for (int i = text.length(); (i = text.lastIndexOf(word, i - 1)) != -1; i++) {
             System.out.println(i);
             } // prints "22", "13", "4"
             */
            String smartTail = "";
            String staticWhere = "";
            String afterWHERE = "";
            String smartPartToKeep = "";
            System.out.println("----SMART--- query iniziale:" + this.query);
            //1. cerco la posizione dell'ultimo WHERE           
            int lastWHEREposition = 0;
            String text = this.query;
            String word = "WHERE";
            for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                lastWHEREposition = i;
            }

            smartPartToKeep = this.query;
            if (lastWHEREposition <= 0) {
                // non ci sono WHERE
                afterWHERE = this.query;
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
                        smartPartToKeep = this.query.substring(0, lastORDERBYposition);
                        smartTail = " ORDER BY " + text.substring(lastORDERBYposition + 8, text.length());
                    }
                } else {
                    //c'è un groupby da considerare dopo il where e se c'è conterrà anche l'order by
                    smartPartToKeep = this.query.substring(0, lastGROPUPBYposition);
                    smartTail = " GROUP BY " + text.substring(lastGROPUPBYposition + 8, text.length());
                }

            } else {
                System.out.println("----SMART--- posizione ultimo WHERE:" + lastWHEREposition);
                //2. tutta la parte di testo prima del WHERE è da tenere          
                smartPartToKeep = this.query.substring(0, lastWHEREposition);
                System.out.println("----SMART--- da tenere:" + smartPartToKeep);

                afterWHERE = this.query.substring(lastWHEREposition + 5, this.query.length());
//                System.out.println("----SMART--- afterWHERE:" + afterWHERE);

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
                        staticWhere = text;
                    } else {
                        //c'è un orderby da considerare dopo il where
                        smartTail = " ORDER BY " + text.substring(lastORDERBYposition + 8, text.length());
                        staticWhere = text.substring(0, lastORDERBYposition);
                    }
                } else {
                    //c'è un groupby da considerare dopo il where e se c'è conterrà anche l'order by
                    smartTail = " GROUP BY " + text.substring(lastGROPUPBYposition + 8, text.length());
                    staticWhere = text.substring(0, lastGROPUPBYposition);
                }

            }
            if (staticWhere.length() > 3) {
                staticWhere = " WHERE " + staticWhere.trim() + " ";
            } else {
                staticWhere = "";
            }
            condizioni = staticWhere;
            System.out.println("----SMART--- smartTail:" + smartTail);
            System.out.println("----SMART--- staticWhere:" + staticWhere);
            String newFilter = "";
            if (this.filteredElements != null && this.filteredElements.length() > 1
                    && this.visualFilter != null && this.visualFilter.length() > 2) {

                String[] filtrs = this.filteredElements.split(";");
                List<String> filters = Arrays.asList(filtrs);
                for (int jj = 0; jj < filters.size(); jj++) {

                    String thisFilter = filters.get(jj).toString() + " LIKE '%" + this.getVisualFilter() + "%' ";
                    if (newFilter.length() > 0) {
                        newFilter += " OR ";
                    }
                    newFilter += thisFilter;
                    System.out.println("newFilter=" + newFilter);

                }

                if (condizioni.length() < 3) {
                    condizioni = " WHERE " + newFilter;
                } else {
                    if (newFilter.length() > 0) {
                        condizioni += " AND (" + newFilter + ")";
                    }
                }
                qry = smartPartToKeep + condizioni + " " + smartTail;

//                System.out.println("Creo filtro qualificato:()=" + qry);
                this.setQuery(qry);
            }

        }

    }

    public String regenerateQuery(String originalQuery, String myWhereClause, boolean keepOriginalWhere, String joinWhereOrAnd, boolean keepOriginalOrderby, boolean keepOriginalGroupby) {
        String formVisualFilteredElements = this.filteredElements;
        String formVisualFilter = this.visualFilter;
        String condizioni = "";
        String newQuery = originalQuery;
//        System.out.println("\n()->originalQuery = " + originalQuery);
        System.out.println("myWhereClause = " + originalQuery);

//        System.out.println("\n()->originalQuery = " + originalQuery);
        if (originalQuery != null && originalQuery.length() > 1) {
            /*
             String text = "0123hello9012hello8901hello7890";
             String word = "hello";

             System.out.println(text.indexOf(word)); // prints "4"
             System.out.println(text.lastIndexOf(word)); // prints "22"

             // find all occurrences forward
             for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
             System.out.println(i);
             } // prints "4", "13", "22"

             // find all occurrences backward
             for (int i = text.length(); (i = text.lastIndexOf(word, i - 1)) != -1; i++) {
             System.out.println(i);
             } // prints "22", "13", "4"
             */
            String smartTail = "";
            String staticWhere = "";
            String afterWHERE = "";
            String smartPartToKeep = "";
//            System.out.println("----SMART--- query iniziale:" + originalQuery);
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
                        smartTail = " ORDER BY " + text.substring(lastORDERBYposition + 8, text.length());
                    }
                } else {
                    //c'è un groupby da considerare dopo il where e se c'è conterrà anche l'order by
                    smartPartToKeep = originalQuery.substring(0, lastGROPUPBYposition);
                    smartTail = " GROUP BY " + text.substring(lastGROPUPBYposition + 8, text.length());
                }

            } else {
//                System.out.println("----SMART--- posizione ultimo WHERE:" + lastWHEREposition);
                //2. tutta la parte di testo prima del WHERE è da tenere          
                smartPartToKeep = originalQuery.substring(0, lastWHEREposition);
//                System.out.println("----SMART--- da tenere:" + smartPartToKeep);

                afterWHERE = originalQuery.substring(lastWHEREposition + 5, originalQuery.length());
//                System.out.println("----SMART--- afterWHERE:" + afterWHERE);

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
                        staticWhere = text;
                    } else {
                        //c'è un orderby da considerare dopo il where
                        smartTail = " ORDER BY " + text.substring(lastORDERBYposition + 8, text.length());
                        staticWhere = text.substring(0, lastORDERBYposition);
                    }
                } else {
                    //c'è un groupby da considerare dopo il where e se c'è conterrà anche l'order by
                    smartTail = " GROUP BY " + text.substring(lastGROPUPBYposition + 8, text.length());
                    staticWhere = text.substring(0, lastGROPUPBYposition);
                }

            }
            if (staticWhere.length() > 3) {
                staticWhere = " " + staticWhere.trim() + " ";
            } else {
                staticWhere = "";
            }
            condizioni = staticWhere;
//            System.out.println("----SMART--- smartTail:" + smartTail);
//            System.out.println("----SMART--- staticWhere:" + staticWhere);

//AGGIUNTA FILTRI VISUALI
            String newFilter = "";
            if (formVisualFilteredElements != null && formVisualFilteredElements.length() > 1
                    && formVisualFilter != null && formVisualFilter.length() > 2) {

                String[] filtrs = formVisualFilteredElements.split(";");
                List<String> filters = Arrays.asList(filtrs);
                for (int jj = 0; jj < filters.size(); jj++) {

                    String thisFilter = filters.get(jj).toString() + " LIKE '%" + formVisualFilter + "%' ";
                    if (newFilter.length() > 0) {
                        newFilter += " OR ";
                    }
                    newFilter += thisFilter;
                    System.out.println("newFilter=" + newFilter);
                }

                if (condizioni.length() < 3) {
                    condizioni = " WHERE " + newFilter;
                } else {
                    if (newFilter.length() > 0) {
                        condizioni += " AND (" + newFilter + ")";
                    }
                }

            }
            if (myWhereClause != null && myWhereClause.length() > 1) {
                if (condizioni.length() < 3 || keepOriginalWhere == false) {
                    condizioni = " WHERE " + myWhereClause;
                } else {
                    if (newFilter.length() > 0) {
                        condizioni += " AND (" + myWhereClause + ")";
                    }
                }

            }
            newQuery = smartPartToKeep + condizioni + " " + smartTail;

        }
//        System.out.println(">>()=" + newQuery);

        return newQuery;
    }

    public smartObjRight analyzeRightsRuleJson(String rights, ResultSet rs, Connection Conny, int levelBase) {

        smartEntity myEntity = new smartEntity(myParams, mySettings);
        myEntity.setFatherKEYvalue(fatherKEYvalue);
        myEntity.setSendToCRUD(sendToCRUD);
//        System.out.println("rowsCounter=" + rowsCounter);

        myEntity.setRowsCounter(rowsCounter);
        smartObjRight rowRights = new smartObjRight(-1);
        rowRights = myEntity.analyzeRightsRuleJson(rights, rs, Conny, levelBase);

        return rowRights;
    }

    public smartObjRight joinRights(smartObjRight Arights, smartObjRight Brights) {
        if (Arights == null) {
            Arights = new smartObjRight(1);
        }
        Arights.evaluateRights();
        if (Brights == null) {
            Brights = new smartObjRight(1);
        }
        Brights.evaluateRights();

        int newPerm = 0;
        int newLevel = 0;
        if (Arights.level <= Brights.level) {
            newLevel = Brights.level;
            //prervale permesso oggetto
            /*
              int canView;//1
        int canModify;//2
        int canDelete;//4
        int canCreate;//8
        int canPushButton;//16
        int canEverything;//128
             */
            if (Brights.canView >= 0) {
                newPerm += 1 * Brights.canView;//uso il permesso dell'oggetto
            } else {
                newPerm += 1 * Arights.canView;//prendo il permesso di riga
            }
            if (Brights.canModify >= 0) {
                newPerm += 2 * Brights.canModify;//uso il permesso dell'oggetto
            } else {
                newPerm += 2 * Arights.canModify;//prendo il permesso di riga
            }
            if (Brights.canDelete >= 0) {
                newPerm += 4 * Brights.canDelete;//uso il permesso dell'oggetto
            } else {
                newPerm += 4 * Arights.canDelete;//prendo il permesso di riga
            }
            if (Brights.canCreate >= 0) {
                newPerm += 8 * Brights.canCreate;//uso il permesso dell'oggetto
            } else {
                newPerm += 8 * Arights.canCreate;//prendo il permesso di riga
            }
            if (Brights.canPushButton >= 0) {
                newPerm += 16 * Brights.canPushButton;//uso il permesso dell'oggetto
            } else {
                newPerm += 16 * Arights.canPushButton;//prendo il permesso di riga
            }
            if (Brights.canEverything >= 0) {
                newPerm += 128 * Brights.canEverything;//uso il permesso dell'oggetto
            } else {
                newPerm += 128 * Arights.canEverything;//prendo il permesso di riga
            }

        } else if (Arights.level == Brights.level) {
            // se i livelli sono uguali prevale il più permissivo
            if (Brights.canView >= 0 || Arights.canView >= 0) {
                newPerm += 1 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 1 * 0;//prendo il permesso di riga
            }
            if (Brights.canModify >= 0 || Arights.canModify >= 0) {
                newPerm += 2 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 2 * 0;//prendo il permesso di riga
            }
            if (Brights.canDelete >= 0 || Arights.canDelete >= 0) {
                newPerm += 4 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 4 * 2;//prendo il permesso di riga
            }
            if (Brights.canCreate >= 0 || Arights.canCreate >= 0) {
                newPerm += 8 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 8 * 0;//prendo il permesso di riga
            }
            if (Brights.canPushButton >= 0 || Arights.canPushButton >= 0) {
                newPerm += 16 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 16 * 0;//prendo il permesso di riga
            }
            if (Brights.canEverything >= 0 || Arights.canEverything >= 0) {
                newPerm += 128 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 128 * 0;//prendo il permesso di riga
            }
        } else {
            newLevel = Arights.level;
            if (Arights.canView >= 0) {
                newPerm += 1 * Arights.canView;//uso il permesso dell'oggetto
            } else {
                newPerm += 1 * Brights.canView;//prendo il permesso di riga
            }
            if (Arights.canModify >= 0) {
                newPerm += 2 * Arights.canModify;//uso il permesso dell'oggetto
            } else {
                newPerm += 2 * Brights.canModify;//prendo il permesso di riga
            }
            if (Arights.canDelete >= 0) {
                newPerm += 4 * Arights.canDelete;//uso il permesso dell'oggetto
            } else {
                newPerm += 4 * Brights.canDelete;//prendo il permesso di riga
            }
            if (Arights.canCreate >= 0) {
                newPerm += 8 * Arights.canCreate;//uso il permesso dell'oggetto
            } else {
                newPerm += 8 * Brights.canCreate;//prendo il permesso di riga
            }
            if (Arights.canPushButton >= 0) {
                newPerm += 16 * Arights.canPushButton;//uso il permesso dell'oggetto
            } else {
                newPerm += 16 * Brights.canPushButton;//prendo il permesso di riga
            }
            if (Arights.canEverything >= 0) {
                newPerm += 128 * Arights.canEverything;//uso il permesso dell'oggetto
            } else {
                newPerm += 128 * Brights.canEverything;//prendo il permesso di riga
            }
        }

        if (newPerm < -1) {
            newPerm = -1;
        }

        smartObjRight realRights = new smartObjRight(newPerm);
        realRights.level = newLevel;
        realRights.evaluateRights();
//        System.out.println("JOIN A:" + Arights.totalRight + " LEVEL:" + Arights.level);
//        System.out.println("JOIN B:" + Brights.totalRight + " LEVEL:" + Brights.level);
//        System.out.println("RISULTATO::" + realRights.totalRight + " LEVEL:" + realRights.level);

        return realRights;

    }

    public class bound_Fields {

        String marker;
        String value;
        boolean present;

        public boolean isPresent() {
            return present;
        }

        public void setPresent(boolean present) {
            this.present = present;
        }

        public String getMarker() {
            return marker;
        }

        public void setMarker(String marker) {
            this.marker = marker;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public class lockRule {

        String ruleType;
        String typeA;
        String fieldA;
        String fieldTypeA;
        String valueA;
        String typeB;
        String fieldB;
        String fieldTypeB;
        String valueB;
        String test = "";
        int level;
        int right;
        String limitUp;
        String limitDown;

        String tabCorrelazioni;
        String tabGruppi;
        String fieldIDinTabGruppi;

        public String getTabCorrelazioni() {
            return tabCorrelazioni;
        }

        public void setTabCorrelazioni(String tabCorrelazioni) {
            this.tabCorrelazioni = tabCorrelazioni;
        }

        public String getTabGruppi() {
            return tabGruppi;
        }

        public void setTabGruppi(String tabGruppi) {
            this.tabGruppi = tabGruppi;
        }

        public String getFieldIDinTabGruppi() {
            return fieldIDinTabGruppi;
        }

        public void setFieldIDinTabGruppi(String fieldIDinTabGruppi) {
            this.fieldIDinTabGruppi = fieldIDinTabGruppi;
        }

        public String getRuleType() {
            return ruleType;
        }

        public void setRuleType(String ruleType) {
            this.ruleType = ruleType;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getFieldTypeA() {
            return fieldTypeA;
        }

        public void setFieldTypeA(String fieldTypeA) {
            this.fieldTypeA = fieldTypeA;
        }

        public String getFieldTypeB() {
            return fieldTypeB;
        }

        public void setFieldTypeB(String fieldTypeB) {
            this.fieldTypeB = fieldTypeB;
        }

        public String getTypeA() {
            return typeA;
        }

        public void setTypeA(String typeA) {
            this.typeA = typeA;
        }

        public String getFieldA() {
            return fieldA;
        }

        public void setFieldA(String fieldA) {
            this.fieldA = fieldA;
        }

        public String getValueA() {
            return valueA;
        }

        public void setValueA(String valueA) {
            this.valueA = valueA;
        }

        public String getTypeB() {
            return typeB;
        }

        public void setTypeB(String typeB) {
            this.typeB = typeB;
        }

        public String getFieldB() {
            return fieldB;
        }

        public void setFieldB(String fieldB) {
            this.fieldB = fieldB;
        }

        public String getValueB() {
            return valueB;
        }

        public void setValueB(String valueB) {
            this.valueB = valueB;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }

        public String getLimitUp() {
            return limitUp;
        }

        public void setLimitUp(String limitUp) {
            this.limitUp = limitUp;
        }

        public String getLimitDown() {
            return limitDown;
        }

        public void setLimitDown(String limitDown) {
            this.limitDown = limitDown;
        }

    }

    public class smartFormResponse {

        String HtmlCode = "";
        String ges_routineOnLoad = "";
        JSONObject dataJSON = new JSONObject();

        public JSONObject getDataJSON() {
            return dataJSON;
        }

        public void setDataJSON(JSONObject dataJSON) {
            this.dataJSON = dataJSON;
        }

        public String getHtmlCode() {
            return HtmlCode;
        }

        public void setHtmlCode(String HtmlCode) {
            this.HtmlCode = HtmlCode;
        }

        public String getGes_routineOnLoad() {
            return ges_routineOnLoad;
        }

        public void setGes_routineOnLoad(String ges_routineOnLoad) {
            this.ges_routineOnLoad = ges_routineOnLoad;
        }

    }

    public String browserArgsReplace(String query) {

//        System.out.println("\n>browserArgsReplace di SMART FORM ");
//        System.out.println("this.sendToCRUD: " + this.sendToCRUD);
//        System.out.println("this.toBeSent: " + this.toBeSent);
        if (query == null) {
            return null;
        }
        CRUDorder myCRUD = new CRUDorder(myParams, mySettings);
        myCRUD.setFatherKEYvalue(this.fatherKEYvalue);

        myCRUD.setSendToCRUD(this.sendToCRUD);
        query = myCRUD.standardReplace(query, null);

        if (rowValues != null) {
            for (int jj = 0; jj < rowValues.size(); jj++) {
                String toBeReplaced = "!##" + rowValues.get(jj).getMarker() + "##!";
                if (query.contains(toBeReplaced)) {
                    query = query.replace(toBeReplaced, rowValues.get(jj).getValue());
                }
            }
        }
//        System.out.println("\n>browserArgsReplace>>> " + query);
        return query;

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

    public void printVals() {
        System.out.println("ID :" + ID);
        System.out.println("copyTag :" + copyTag);
        System.out.println("name :" + name);
        System.out.println("rifProject :" + rifProject);
        System.out.println("rifFrame :" + rifFrame);
        System.out.println("serverURL :" + serverURL);
        System.out.println("databaseID :" + databaseID);

        System.out.println("formHeight :" + formHeight);
        System.out.println("formWidth :" + formWidth);

        System.out.println("curKEYvalue :" + curKEYvalue);
        System.out.println("curKEYtype :" + curKEYtype);

        System.out.println("query :" + query);
        System.out.println("queryMatrix :" + queryMatrix);
        System.out.println("mainTable :" + mainTable);
        System.out.println("visualFilter :" + visualFilter);

    }
}
