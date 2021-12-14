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
import models.ShowItObject;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.DBimage;
import REVOpager.EVOpagerDBconnection;
import REVOpager.Database;
import REVOpager.EVOuser;
import REVOpager.Server;
import REVOpager.schema_column;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import models.CRUDorder;
import models.Linker;
import models.SMARTtreeView;
import models.SelectList;
import models.SelectListLine;
import models.ShowItFormResponse;
import models.ShowItObject.objRight;
import models.boundFields;
import models.gaiaCalendar;
import models.gaiaCalendar.Job;
import models.objectLayout;
import models.requestsManager;
import models.schedule;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Franco
 */
public class ShowItForm {

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
    objRight formRightsRules;

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
    String ges_autolinks;

    ArrayList<boundFields> sentFieldList;
    ArrayList<boundFields> filterList;
    ArrayList<boundFields> boundFieldList;
    ArrayList<boundFields> rowValues;

    EVOpagerParams myParams;
    Settings mySettings;
    public ArrayList<ShowItObject> objects;
    public ArrayList<ShowItObject> formObjects;

    String directorParams;
    String childrenList;
    ShowItFormResponse formResponse;

    String addRowPosition;
    String refreshOnAdd;
    String refreshOnUpdate;
    String updRowWhereClause;
    String showHeader;
    String showCounter;

    String abstractTextCode;
    String advancedFiltered;

    public ShowItForm(String id, EVOpagerParams xParams, Settings xSettings) {
        formResponse = new ShowItFormResponse();
        this.myParams = xParams;
        //this.myParams.printParams("SHOW IT FORM");
//        this.mySettings = new Settings();
        this.mySettings = xSettings;
        //this.mySettings.printSettings("SHOW IT FORM");
        //-----SERVER & DB MAKER---------------------------------    
        this.server = new Server(mySettings);
        this.database = new Database(myParams, mySettings);
        //------------------------------------------------------- 
        this.ID = id;
        System.out.println("ShowItForm " + this.ID);
        this.objects = new ArrayList<ShowItObject>();
        this.formObjects = new ArrayList<ShowItObject>();
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
// <editor-fold defaultstate="collapsed" desc="GETTERS & SETTERS">

    public String getGes_autolinks() {
        return ges_autolinks;
    }

    public void setGes_autolinks(String ges_autolinks) {
        this.ges_autolinks = ges_autolinks;
    }

    public String getAdvancedFiltered() {
        return advancedFiltered;
    }

    public void setAdvancedFiltered(String advancedFiltered) {
        this.advancedFiltered = advancedFiltered;
    }

    public String getQueryUsed() {
        return queryUsed;
    }

    public void setQueryUsed(String queryUsed) {
        this.queryUsed = queryUsed;
    }

    public objRight getFormRightsRules() {
        return formRightsRules;
    }

    public void setFormRightsRules(objRight formRightsRules) {
        this.formRightsRules = formRightsRules;
    }

    public int getLayoutColumns() {
        return layoutColumns;
    }

    public void setLayoutColumns(int layoutColumns) {
        this.layoutColumns = layoutColumns;
    }

    public ArrayList<boundFields> getRowValues() {
        return rowValues;
    }

    public void setRowValues(ArrayList<boundFields> rowValues) {
        this.rowValues = rowValues;
    }

    public ArrayList<ShowItObject> getFormObjects() {
        return formObjects;
    }

    public void setFormObjects(ArrayList<ShowItObject> formObjects) {
        this.formObjects = formObjects;
    }

    public ShowItFormResponse getFormResponse() {
        return formResponse;
    }

    public void setFormResponse(ShowItFormResponse formResponse) {
        this.formResponse = formResponse;
    }

    public String getAddRowPosition() {
        return addRowPosition;
    }

    public void setAddRowPosition(String addRowPosition) {
        this.addRowPosition = addRowPosition;
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

    public void setAbstractTextCode(String abstractTextCode) {
        this.abstractTextCode = abstractTextCode;
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

    public String getGes_triggers() {
        return ges_triggers;
    }

    public void setGes_triggers(String ges_triggers) {
        this.ges_triggers = ges_triggers;
    }

    public String getActualHtmlPattern() {
        return actualHtmlPattern;
    }

    public void setActualHtmlPattern(String actualHtmlPattern) {
        this.actualHtmlPattern = actualHtmlPattern;
    }

    public String getGes_routineOnLoad() {
        return ges_routineOnLoad;
    }

    public void setGes_routineOnLoad(String ges_routineOnLoad) {
        this.ges_routineOnLoad = ges_routineOnLoad;
    }

    public String getGes_formPanel() {
        return ges_formPanel;
    }

    public void setGes_formPanel(String ges_formPanel) {
        this.ges_formPanel = ges_formPanel;
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

    public String getDirectorParams() {
        return directorParams;
    }

    public void setDirectorParams(String directorParams) {
        this.directorParams = directorParams;
    }

    public ArrayList<boundFields> getFilterList() {
        return filterList;
    }

    public void setFilterList(ArrayList<boundFields> filterList) {
        this.filterList = filterList;
    }

    public String getSendToCRUD() {
        return sendToCRUD;
    }

    public void setSendToCRUD(String sendToCRUD) {
//        System.out.println("\n\n\nASSEGNATO sendToCRUD:" + sendToCRUD + "\n\n\n");
        this.sendToCRUD = sendToCRUD;
    }

    public String getActualRowGBcolor() {
        return actualRowGBcolor;
    }

    public void setActualRowGBcolor(String actualRowGBcolor) {
        this.actualRowGBcolor = actualRowGBcolor;
    }

    public String getRowBGcolor() {
        return rowBGcolor;
    }

    public void setRowBGcolor(String rowBGcolor) {
        this.rowBGcolor = rowBGcolor;
    }

    public String getFatherCopyTag() {
        return fatherCopyTag;
    }

    public void setFatherCopyTag(String fatherCopyTag) {
        this.fatherCopyTag = fatherCopyTag;
    }

    public String getCopyTag() {
        return copyTag;
    }

    public void setCopyTag(String copyTag) {
        this.copyTag = copyTag;
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

    public ArrayList<boundFields> getSentFieldList() {
        return sentFieldList;
    }

    public void setSentFieldList(ArrayList<boundFields> sentFieldList) {
        this.sentFieldList = sentFieldList;
    }

    public ArrayList<ShowItObject> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<ShowItObject> objects) {
        this.objects = objects;
    }

    public ArrayList<boundFields> getBoundFieldList() {
        return boundFieldList;
    }

    public void setBoundFieldList(ArrayList<boundFields> boundFieldList) {
        this.boundFieldList = boundFieldList;
    }

    public String getHtmlPattern() {
        return htmlPattern;
    }

    public void setHtmlPattern(String htmlPattern) {
        this.htmlPattern = htmlPattern;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public String getComplexSearchParams() {
        return complexSearchParams;
    }

    public void setComplexSearchParams(String complexSearchParams) {
        this.complexSearchParams = complexSearchParams;
    }

    public String getDisableRules() {
        return disableRules;
    }

    public void setDisableRules(String disableRules) {
        this.disableRules = disableRules;
    }

    public int getEffectiveEnabled() {
        return effectiveEnabled;
    }

    public void setEffectiveEnabled(int effectiveEnabled) {
        this.effectiveEnabled = effectiveEnabled;
    }

    public String getInfoReceived() {
        return infoReceived;
    }

    public void setInfoReceived(String infoReceived) {
        this.infoReceived = infoReceived;
    }

    public String getLabel() {
        return Label;
    }

    public String getToBeSent() {
        return toBeSent;
    }

    public void setToBeSent(String toBeSent) {
        this.toBeSent = toBeSent;
    }

    public void setLabel(String Label) {
        this.Label = Label;
    }

    public int getHasSearchFilter() {
        return hasSearchFilter;
    }

    public void setHasSearchFilter(int hasSearchFilter) {
        this.hasSearchFilter = hasSearchFilter;
    }

    public String getUserRights() {
        return userRights;
    }

    public void setUserRights(String userRights) {
        this.userRights = userRights;
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

    public String getFilterSequence() {
        return filterSequence;
    }

    public void setFilterSequence(String filterSequence) {
        this.filterSequence = filterSequence;
    }

    public String getVisualType() {
        return visualType;
    }

    public void setVisualType(String visualType) {
        this.visualType = visualType;
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

    public int getNofRows() {
        return nofRows;
    }

    public void setNofRows(int nofRows) {
        this.nofRows = nofRows;
    }

    public String getFatherKEYtype() {
        return fatherKEYtype;
    }

    public void setFatherKEYtype(String fatherKEYtype) {
        this.fatherKEYtype = fatherKEYtype;
    }

    public String getFatherKEYvalue() {
        return fatherKEYvalue;
    }

    public void setFatherKEYvalue(String fatherKEYvalue) {
        this.fatherKEYvalue = fatherKEYvalue;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public String getQueryMatrix() {
        return queryMatrix;
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

    public void setQueryMatrix(String queryMatrix) {
        this.queryMatrix = queryMatrix;
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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getMainTable() {
        return mainTable;
    }

    public void setMainTable(String mainTable) {
        this.mainTable = mainTable;
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

    // </editor-fold>
    public String loadFormSettings() {
        // <editor-fold defaultstate="collapsed" desc="CARICO DATI DA DB">   
        Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();

        ResultSet rs;
        // cerca il FORM per nome e se non è compilato per ID
        try {

            Statement s = FEconny.createStatement();
            String SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_forms() + " WHERE `name`='" + this.name + "'";
            if (this.name == null || this.name == "") {
                SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_forms() + " WHERE `ID`='" + this.ID + "'";
            }

//            System.out.println("buildSchema loadFormSettings SQLphrase:" + SQLphrase);
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
            FEconny.close();

        } catch (SQLException ex) {
            Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    // </editor-fold>       
    public String buildSchema() {
////////
////////        System.out.println("*************************************");
////////        System.out.println("**INIZIO BUILD SCHEMA****************");
////////        System.out.println("*************************************");
        // <editor-fold defaultstate="collapsed" desc="buildSchema">   
//        System.out.println(" -buildSchema- PRIMA DI LOAD FORM SETTINGS this.sendToCRUD:" + this.sendToCRUD);
        loadFormSettings();
//        System.out.println(" -buildSchema- LEGGO formPanel:" + this.ges_formPanel);
        getFormPanel();
//        System.out.println(" -buildSchema- DOPO LOAD FORM SETTINGS this.sendToCRUD:" + this.sendToCRUD);
        //myParams.printParams("ShowItForm_buildSchema");
        //mySettings.printSettings("ShowItForm_buildSchema");
//1. mi connetto al LOCAL DB per cercare la tabella richiesta da 'query'
        Connection FEconny = null;
        try {
            FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        } catch (Exception e) {
            System.out.println(" -buildSchema- ERROR connecting FEdb:" + myParams.getCKprojectName());
        }
        ResultSet rs;
        int totalWidth = 0;
        // cerca il FORM per nome e se non è compilato per ID
        try {
            Statement s = FEconny.createStatement();
// </editor-fold>
//===================================================================  
// <editor-fold defaultstate="collapsed" desc="VERIFICO PERMESSI FORM">   
//=============================================
            //===GESTIONE RIGHTS PER IL FORM===========================
            //  formati validi:
            //1) DEFAIULT:1; ADMIN:5;
            //2) [{"ruleType":"default","right":"23"},{"ruleType":"userInStandardGroup","test":"==", "valueB":"MEDICI","right":"17","level":"10"}]
            //========================================================
            // in questa fase il resultset è ancora nullo
            //quindi la decisiione sui diritti si basa SOLO su gruppi di appartenenza o default
            formRightsRules = analyzeRightsRuleJson(userRights, null, null, 10);
            System.out.println("RIGHTS DA FORM:" + userRights);
//            System.out.println("GENERA RIGHTS PER IL FORM:" + formRightsRules.totalRight + " LEVEL:" + formRightsRules.level);
// </editor-fold>
//===================================================================    
// <editor-fold defaultstate="collapsed" desc="CARICO INFORMAZIONI TBS dal father">                   
            // System.out.println("buildSchema STEP 3:CARICO INFORMAZIONI TBS dal father" );
            //1. carico le informazioni arrivate dalla finestra chiamante
            /*
            
             jsonTranslate JT = new jsonTranslate();
             sentFieldList = JT.makeList(this.getInfoReceived());
             //1. carico le informazioni di filtro
             JT = new jsonTranslate();
             filterList = JT.makeList(this.getSendToCRUD());
             System.out.println("ShowItForm_buildSchema_caricoTBSreceived():" + sentFieldList.size());
             System.out.println("ShowItForm_buildSchema_caricoFilterList():" + filterList.size());
             */
            // </editor-fold>       
//===================================================================        
// <editor-fold defaultstate="collapsed" desc="CARICO BOUND FIELD LIST da fatherFilters">           
            System.out.println("buildSchema STEP 4:CARICO BOUND FIELD LIST da fatherFilters");
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
            // System.out.println("\n  boundFieldList:" + boundFieldList.size() + " elementi.");
            ArrayList<schema_column> columns = new ArrayList<schema_column>();
            // </editor-fold>       
//===================================================================        
// <editor-fold defaultstate="collapsed" desc="CREO SCHEMA DELLE COLONNE da information-Schema">           
            System.out.println("CREO SCHEMA DELLE COLONNE da information-Schema");
            //  System.out.println("ShowItForm_buildSchema_CREO SCHEMA DELLE COLONNE");
            if (getMainTable() == null || getMainTable().equalsIgnoreCase("NULL")) {
                System.out.println("WARNING: MAIN TABLE NAME NOT COMPILED. TYPE:" + this.getType());
            } else {

                Connection schemaconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalSchema();

                Statement schemast = schemaconny.createStatement();
                // Statement localSt = localconny.createStatement();
                String SQLphrase = "SELECT * FROM COLUMNS\n"
                        + "           WHERE TABLE_NAME = '" + getMainTable() + "'\n"
                        + "             AND TABLE_SCHEMA = '" + this.database.getDbExtendedName() + "' ORDER BY ORDINAL_POSITION;";
                //       System.out.println("CREO SCHEMA DELLE COLONNE da information-Schema: SQLphrase=" + SQLphrase);

                ResultSet schemars = schemast.executeQuery(SQLphrase);
                int i = 0;
                /* 
                 String IS_NULLABLE[]=new String[100]; // ES. 'YES' oppure 'NO'
                 String COLUMN_KEY[]=new String[100]; // ES. 'PRI'=PRIMARY
                 String EXTRA[]=new String[100]; // ES. 'auto_increment'
                 */

                while (schemars.next()) {
                    i++;
                    schema_column column = new schema_column();
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
                    //System.out.println("COLUMN_KEY=" + schemars.getString("COLUMN_KEY")+"  EXTRA=" + schemars.getString("EXTRA"));

                    columns.add(column);
                }
                schemaconny.close();

                // System.out.println("schema colonne:");
                for (int jj = 0; jj < columns.size(); jj++) {
                    // System.out.println("column:" + columns.get(jj).getCOLUMN_NAME() + " . KEY:" + columns.get(jj).getCOLUMN_KEY() + "  - extra:" + columns.get(jj).getEXTRA());
                }

            }

            /*
             Orac ostruisco l'array list degli oggetti presenti nel form
             e faccio il cast nel bean in form
             
             */
            // </editor-fold>       
//===================================================================        
// <editor-fold defaultstate="collapsed" desc="COSTRUISCO ARRAY OGGETTI">                   
            System.out.println("ShowItForm_buildSchema_COSTRUISCO ARRAY OGGETTI");
            this.objects = new ArrayList<ShowItObject>();
            String SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_objects() + " WHERE `rifForm`='" + this.ID + "' ORDER BY position";
            //   System.out.println("CARICO CARATTERISTICHE OGGETTO:" + SQLphrase);
            rs = s.executeQuery(SQLphrase);
            int lines = 0;
            int foundKey = 0;
            Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
            EVOuser myUser;
            try {
                myUser = new EVOuser(myParams, mySettings);
                myUser.setTABLElinkUserGroups("archivio_correlazioni");
                myUser.setFIELDlinkUserGroupsRifOperatore("partAvalue");
                myUser.setFIELDlinkUserGroupsRifGruppo("partBvalue");

                myUser.setTABLEgruppi("archivio_operatoriGruppi");
                myUser.setFIELDGruppiIDgruppo("IDgruppo");
                myUser.setTABLEoperatori("archivio_operatori");
                myUser.setFIELDoperatoriID("ID");
                while (rs.next()) {
                    lines++;
//                     System.out.println("OGGETTO " + lines + ":" + rs.getString("name"));
                    ShowItObject myObject = new ShowItObject("new");
                    //==========================
                    myObject.loadFromDB(rs); // carico i valori da GES
                    //==========================
                    // System.out.println("OGGETTO:" + myObject.getName());
//-----------------------------------------------------------------------------------      

                    // <editor-fold defaultstate="collapsed" desc="VALUTO VISIBLE">        
//ATTENZIONE actuallyVisible si riferische al valore (C) dell'oggetto (VISIBLE)
// in realtà mi serve SOLO per decidere se creare la colonna in adding row e in totalrow
//                    System.out.println("#_VALUTO PERMESSI OGGETTO:" + myObject.getName() + " -> " + myObject.getVisible() + " .");
                    if (myObject.getVisible() != null && !myObject.getVisible().equalsIgnoreCase("DEFAULT:TRUE")) {
                        myObject.setActuallyVisible(0);
//                        System.out.println("#_EVOUSER dice (getActualRight) :" + myUser.getActualRight(myObject.getVisible(), null) + " .");
                        myObject.setActuallyVisible(myUser.getActualRightAdvanced(myObject.getVisible(), null, accountConny));
//                        System.out.println("#_ActuallyVisible :" + myObject.getActuallyVisible());

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
//                        System.out.println("OGGETTO -->" + myObject.name + " :" + myObject.CG.Type);
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
                        //   System.out.println(" CERCO  PRIMARIO AUTOCOMPILANTE .\n");
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
                                    //System.out.println(myObject.getName()+" - PRIMARIO AUTOCOMPILANTE .\n");
                                }

                                break;
                            }
                        }
                        if (foundKey > 0) {
                            // System.out.println("<<<Trovato KEY FIELD:" + getKEYfieldName() + " .");
                        } else {
                            //   System.out.println("<<<Non Trovato KEY FIELD. Il tipo era:" +myObject.CG.getType() + " .");
                        }
                        int presenteNeiBound = 0;
                        for (int jj = 0; jj < boundFieldList.size(); jj++) {
                            if (lines == 1) {
                                // System.out.println("boundFieldList " + jj + "]" + boundFieldList.get(jj).getMarker());
                            }
                            //System.out.println("AGGIUNGO OGGETTO:" + myObject.getName());
                            if (boundFieldList.get(jj).getMarker().equalsIgnoreCase(myObject.getName())) {
                                boundFieldList.get(jj).setPresent(true);
                                //System.out.println("IMPOSTATO COME PRESENTE:" + myObject.getName());
                                presenteNeiBound++;
                            }
                        }
                        if (presenteNeiBound == 0) {
                            //System.out.println("OGGETTO:" + myObject.getName() + " NON FA PARTE DEI BOUNDED");
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
                        // System.out.println("AGGIUNGO OGGETTO " + myObject.CG.getType() + ":" + myObject.getName());
                    }
                }
            } catch (Exception e) {
                System.out.println("Errore utente..." + e.toString());

            }
            accountConny.close();
            // </editor-fold>       
//===================================================================        
// <editor-fold defaultstate="collapsed" desc="COMPLETO INFORMAZIONI OGGETTI"> 
            //System.out.println("COMPLETO INFORMAZIONI OGGETTI" );
            if (this.type != null
                    && !this.type.equalsIgnoreCase("SINGLEROWFORM")
                    && !this.type.equalsIgnoreCase("INSERTROWFORM")
                    && totalWidth > 0) {
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
                System.out.println(">>boundFieldList.get(" + kk + ").getMarker():" + boundFieldList.get(kk).getMarker());
                System.out.println(">>boundFieldList.get(" + kk + ").getValue():" + boundFieldList.get(kk).getValue());

                if (!boundFieldList.get(kk).isPresent()) {
                    System.out.println("OGGETTO:" + boundFieldList.get(kk).getMarker() + " non è presente: lo creo.");
                    // creo un nuovo oggetto con il campo descritto qui, precompilato con il valore indicato ma invisibile
                    ShowItObject myObject = new ShowItObject("new");
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
                System.out.println(">>filterSequence:" + filterSequence);
            }

            // System.out.println("Trovati:" + this.objects.size() + " oggetti, " + foundKey + " campi PRIMARY KEY." + getKEYfieldName());
            // voglio anche sapere in questa main table quali sono i campi primary, e se sono autoincrement o meno
            //2. apro il database con la query trovata e cerco le righe dei recodr caricandole nel rs
            /*
             if (this.databaseID != null && this.databaseID != this.database.getName()) {
             // il DB che contiene i record è diverso da quello che ocntiene  
             // le indicazioni sul form (questo non succede praticamente mai)
             conny.close();
             server dataServer = new server("mySql", this.serverURL, this.serverURL);
             conny = myCon.makeConnection(dataServer, this.databaseID);
             s = conny.createStatement();
             System.out.println("ATTENZIONE ! SERVER ESTERNO NON ISTANZIATO.");

             }*/
            FEconny.close();

        } catch (SQLException ex) {
            Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in line 1247");
        }

        // adesso che la costruzione è terminata, valuto se le informazioni di formEnabled
        // determinano la disabilitaizone
        // System.out.println("\nInfoReceived:" + this.getInfoReceived() + "\n");
        //System.out.println("getDisableRules:"+ this.getDisableRules() );
        // </editor-fold>       
//===================================================================        
// <editor-fold defaultstate="collapsed" desc="COSTRUISCO LE LISTE ACCESSORIE DEGLI OGGETTI">           
//===============================================================
        //  System.out.println("COSTRUISCO LE LISTE ACCESSORIE DEGLI OGGETTI" ); 
//        System.out.println("ShowItForm_buildSchema_COSTRUISCO LE LISTE ACCESSORIE DEGLI OGGETTI" + this.Label);
        buildObjectsOriginList();
        // </editor-fold>       
//===================================================================        
//        System.out.println("*************************************");
        System.out.println("**FINE SCHEMA************************");
//        System.out.println("*************************************");
        return null;
    }

    public void buildObjectsOriginList() {

        // se ci sono SELECTLIST non specifiche per riga, le precarico
        for (int obj = 0; obj < this.objects.size(); obj++) {
            /* String ValoreDaScrivere = "";
             String fieldName = this.objects.get(obj).name;
             String Type = this.objects.get(obj).Content.getType();
             String CGtype = this.objects.get(obj).CG.getType();
             String containerType = this.objects.get(obj).C.getType();*/
            if ((this.objects.get(obj).C.getType().equalsIgnoreCase("SELECT")
                    || this.objects.get(obj).C.getType().equalsIgnoreCase("MARKER")
                    || this.objects.get(obj).C.getType().equalsIgnoreCase("RADIOBUTTON"))
                    && this.objects.get(obj).CG.getType().equalsIgnoreCase("FIELD")) {
                //System.out.println("this.objects.get(obj).CG.getType()=" + this.objects.get(obj).CG.getType());
                String oQuery = this.objects.get(obj).Origin.getQuery();
                //  System.out.println("buildObjectsOriginList->APPLICO SOSTITUZIONI:"+ oQuery );

//                System.out.println("buildObjectsOriginList\nPRIMA:" + oQuery);
                System.out.println(" this.sendToCRUD:" + this.sendToCRUD);
                oQuery = browserArgsReplace(oQuery);

                System.out.println("DOPO-->:" + oQuery);
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
                // System.out.println("this.objects.get(obj).CG.getType()="+this.objects.get(obj).CG.getType());
                String oQuery = this.objects.get(obj).Origin.getQuery();
                //  System.out.println("buildObjectsOriginList->APPLICO SOSTITUZIONI:"+ oQuery );

                //  System.out.println("buildObjectsOriginList\nPRIMA:" + oQuery);
                oQuery = browserArgsReplace(oQuery);

                // System.out.println("radiofilter DOPO:" + oQuery);
                String oLabelField = this.objects.get(obj).Origin.getLabelField();
                //  System.out.println(" this.objects.get(obj).Origin.getLabelField()="+ this.objects.get(obj).Origin.getLabelField());               
                String oValueField = this.objects.get(obj).Origin.getValueField();
                //  System.out.println(" this.objects.get(obj).Origin.getValueField()="+ this.objects.get(obj).Origin.getValueField());

                String oValueFieldType = this.objects.get(obj).Origin.getValueFieldType();
                SelectList myList = new SelectList(myParams, mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                myList.getList();
                this.objects.get(obj).Origin.setSelectList(myList);

            } else if (this.objects.get(obj).C.getType().equalsIgnoreCase("VOICECHECK")) {
                String oQuery = this.objects.get(obj).Origin.getQuery();
                //  System.out.println("APPLICO SOSTITUZIONI:"+ oQuery );
                oQuery = browserArgsReplace(oQuery);
                //  System.out.println("diventa:"+ oQuery );

                String oLabelField = this.objects.get(obj).Origin.getLabelField();
                String oValueField = this.objects.get(obj).Origin.getValueField();
                String oValueFieldType = this.objects.get(obj).Origin.getValueFieldType();
                SelectList myList = new SelectList(myParams, mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                this.objects.get(obj).Origin.setSelectList(myList);
            } else if (this.objects.get(obj).C.getType().equalsIgnoreCase("SPAREVALUE")) {
                String oQuery = this.objects.get(obj).Origin.getQuery();
                //  System.out.println("APPLICO SOSTITUZIONI:"+ oQuery );
                oQuery = browserArgsReplace(oQuery);
                //  System.out.println("diventa:"+ oQuery );

                String oLabelField = this.objects.get(obj).Origin.getLabelField();
                String oValueField = this.objects.get(obj).Origin.getValueField();
                String oValueFieldType = this.objects.get(obj).Origin.getValueFieldType();
                SelectList myList = new SelectList(myParams, mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                this.objects.get(obj).Origin.setSelectList(myList);
            } else if (this.objects.get(obj).C.getType().equalsIgnoreCase("MLSfield")) {
                String oQuery = this.objects.get(obj).Origin.getQuery();
                oQuery = browserArgsReplace(oQuery);
                String[] blocks = oQuery.split(";");
                List<String> block = Arrays.asList(blocks);

                SelectList myBlocco = new SelectList();
                String blocco = block.get(block.size() - 1);
                String[] part = blocco.split("\\|");
                List<String> parts = Arrays.asList(part);
                myBlocco.setoQuery(parts.get(2));
                myBlocco.setoValueField(parts.get(0).trim());
                myBlocco.setoLabelField(parts.get(1));
                myBlocco.setoTitle(parts.get(3));

                oQuery = myBlocco.getoQuery();

                // in questa query devo eliminare la parte dopo il WHERE
                String[] blocks1 = oQuery.split("WHERE");
                List<String> block1 = Arrays.asList(blocks1);
                oQuery = block1.get(0);

                // System.out.println("oQuery per MLS label:" + oQuery );
                oQuery = browserArgsReplace(oQuery);
                //  System.out.println("DOPO:" + oQuery);

                String oLabelField = myBlocco.getoLabelField();
                String oValueFieldType = "TEXT";
                String oValueField = myBlocco.getoValueField();
                //    System.out.println("oValueField:" + oValueField);
                // la label può avere diversi field 
                ArrayList<labels> MyLabels = new ArrayList();

                String[] partx = oLabelField.split(",");
                List<String> partsx;
                partsx = Arrays.asList(partx);
                for (int lbls = 0; lbls < partsx.size(); lbls++) {
                    String rawLab = partsx.get(lbls);
                    labels myLabel = new labels();

                    String[] pezzo = rawLab.split(":");
                    List<String> pezzi = Arrays.asList(pezzo);

                    if (pezzi.size() > 1) {
                        myLabel.setLabelField(pezzi.get(0));
                        myLabel.setLabelText(pezzi.get(1));
                    } else {
                        myLabel.setLabelField(pezzi.get(0));
                        myLabel.setLabelText("");
                    }

                    MyLabels.add(myLabel);
                }

                Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();

                String SQLphrase = "";
                // System.out.println(  "SQLphrase:" + SQLphrase);

                PreparedStatement ps = null;
                ResultSet rs = null;
                SQLphrase = oQuery;
                // System.out.println("\n\n" + this.Label + "_\n_COSTRUZIONE LISTA ACCESSORIA per " + this.objects.get(obj).getName() + "; SQLphrase:" + SQLphrase);
                ArrayList<SelectListLine> myList = new ArrayList();

                ps = null;
                rs = null;

                try {
                    ps = conny.prepareStatement(SQLphrase);

                    rs = null;
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        SelectListLine myLine = new SelectListLine();

                        try {
                            String oVal = "";
                            try {
                                oVal = rs.getString(oValueField);
                            } catch (Exception e) {
                                oVal = "";
                            }
                            myLine.setValue(oVal);

                            myLine.setLabel("");
                            String curLabel = "";
                            for (int lbls = 0; lbls < MyLabels.size(); lbls++) {
                                if (lbls > 0) {
                                    curLabel += " - ";
                                }

                                if (MyLabels.get(lbls).getLabelText() != null && MyLabels.get(lbls).getLabelText().length() > 0) {
                                    curLabel += MyLabels.get(lbls).getLabelText() + ": ";
                                }

                                try {
                                    curLabel += rs.getString(MyLabels.get(lbls).getLabelField());
                                } catch (Exception e) {
                                }
                            }

                            //   System.out.println( myLine.getValue()+ ") curLabel:" + curLabel);
                            myLine.setLabel(curLabel);
                            myList.add(myLine);
                        } catch (Exception ex) {
                            System.out.println("error in line 1520:" + ex.toString());
                        }
                    }

                } catch (SQLException ex) {

                    System.out.println("\n\n" + this.Label + "_\n_ERR IN COSTRUZIONE LISTA ACCESSORIA per " + this.objects.get(obj).getName() + "; SQLphrase:" + SQLphrase);

                    Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                }

                SelectList MLSlist = new SelectList();
                MLSlist.list = myList;
                this.objects.get(obj).Origin.setSelectList(MLSlist);
            }

        }
    }

    private objRight valutaRightsOggetto(ShowItObject myObject, ResultSet rs) {
        /*
        DIRITTI: ogni regola ha un suo level che ne determina la potenza
        DA FORM:    può essere una impostazione di default o cambiare per un gruppo sppecifico o per un valore TBS
                    A) USERRIGHTS
                    B) LOCKERS
        
        DA OGGETTO  può cambiare in base all'appartenenza dell'utente ad un gruppo o a un valore specifico nella riga
                    C) VISIBLE
                    D) MODIFIABLE   
         */
//myObject.objRights contiene i diritti a priori assegnati all'oggetto in base ai diritti del FORM (A e B) e ai diritti di visibilità C
//        System.out.println("DIRITTI BASE OGGETTO ->" + myObject.getName() + " : " + myObject.objRights.totalRight);
        objRight realRights = myObject.objRights;
        if (myObject.Content.getModifiable() != null && myObject.Content.getModifiable().startsWith("[{")) {
//            System.out.println("DIRITTI COMPLESSI OGGETTO ->" + myObject.getName() + " : " + myObject.Content.getModifiable());

            realRights = analyzeRightsRuleJson(myObject.Content.getModifiable(), rs, null, 400);
        } else {
            realRights = myObject.objRights;
        }

        realRights.evaluateRights();
//        System.out.println("  CHE DIVENTA: " + realRights.totalRight + " LEVEL: " + realRights.level);

        return realRights;
    }

    private objRight valutaRightsRiga(String LOCKERS, ResultSet rs) {
        /*
        DIRITTI: ogni regola ha un suo level che ne determina la potenza
        DA FORM:    può essere una impostazione di default o cambiare per un gruppo sppecifico o per un valore TBS
                    A) USERRIGHTS
                    B) LOCKERS
         */

        objRight ObjRights = this.formRightsRules;
        if (LOCKERS != null && LOCKERS.startsWith("[{")) {
            ObjRights = analyzeRightsRuleJson(LOCKERS, rs, null, 200);
        } else {
            ObjRights = this.formRightsRules;
        }

        return ObjRights;
    }

    public class condizione {

        String leftPart;
        String operand;
        String rightPart;
        int leftNumber;
        int rightNumber;
        int verificata;

        public int getVerificata() {
            return verificata;
        }

        public void setVerificata(int verificata) {
            this.verificata = verificata;
        }

        public String getLeftPart() {
            return leftPart;
        }

        public void setLeftPart(String leftPart) {
            this.leftPart = leftPart;
        }

        public String getOperand() {
            return operand;
        }

        public void setOperand(String operand) {
            this.operand = operand;
        }

        public String getRightPart() {
            return rightPart;
        }

        public void setRightPart(String rightPart) {
            this.rightPart = rightPart;
        }

        public int getLeftNumber() {
            return leftNumber;
        }

        public void setLeftNumber(int leftNumber) {
            this.leftNumber = leftNumber;
        }

        public int getRightNumber() {
            return rightNumber;
        }

        public void setRightNumber(int rightNumber) {
            this.rightNumber = rightNumber;
        }

    }

    public ShowItFormResponse paintForm() {

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
//        System.out.println("\nPrima di  buildSchema il StC =  : " + this.getSendToCRUD());
        buildSchema();
//        System.out.println("\nDopo buildSchema il StC =  : " + this.getSendToCRUD());
//        System.out.println("SONO IN PAINTFORM :this.id=" + this.ID);
        if (this.type == null) {
            this.type = "TABLE";
        }
        if (this.type.equalsIgnoreCase("timeline")) {
            formResponse = paintTimelineForm();
        } else if (this.type.equalsIgnoreCase("smart")) {
            formResponse = paintSmartForm();
        } else {
            formResponse = paintDataForm();
        }
        return formResponse;
    }

    public String fillMapBuilderForm(String event) {

        System.out.println("MISURE MAP FORM: " + this.formWidth + "-" + this.formHeight);
        String W = this.formWidth;
        String H = this.formHeight;
        String htmlCode = "";
        String SQLphrase = "";
        makeQualifiedQuery();
        SQLphrase = browserArgsReplace(this.query);
        System.out.println("SQLphrase = " + SQLphrase);
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        ResultSet rs;
        try {
            SQLphrase = browserArgsReplace(this.query);
            System.out.println("fillMapBuilderForm SQLphrase:" + SQLphrase);
            Statement s = conny.prepareStatement(SQLphrase,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY); //questo serve per poter scorrere i record e riocomeinciare da capo
            rs = s.executeQuery(SQLphrase);
            int lines = 0;

            while (rs.next()) {
                lines++;
                String IDmappa = rs.getString("ID");
                // costruisco una mappa per ogni riga

                String params = "\"params\":\"" + encodeURIComponent(myParams.makePORTALparams()) + "\"";
                String connectors = "\"connectors\":[{\"door\":\"iframe\","
                        + "\"event\":\"" + event + "\","
                        + "\"table\":\"" + this.mainTable + "\","// es operatori
                        + "\"formID\":\"" + this.ID + "\","// è l'ID del mio FORM di tipo MAP
                        + "\"keyfield\":\"ID\","//es operatori.ID

                        + "\"keyValue\":\"" + IDmappa + "\","// è l'ID del mio FORM di tipo MAP
                        + "\"keyType\":\"TEXT\","
                        + "\"TBS\":\"" + encodeURIComponent(this.sendToCRUD) + "\" "//es. media
                        + " }]";

                System.out.println("connectors:" + connectors);
                String utils = "\"responseType\":\"text\"";
                String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";

                String iframe = "<iframe id='myMap' "
                        + "width='" + W + "' height='" + H + "' "
                        + "frameborder='0' marginheight='10' "
                        + "marginwidth='10' "
                        + "src='portal?target=requestsManager&gp=";
                iframe += encodeURIComponent(gp);
                iframe += "' ></iframe>";
                htmlCode += iframe;
                System.out.println("iframe:" + iframe);
            }

        } catch (SQLException ex) {
            System.out.println("error in paintMapForm line 1704");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conny.close();
        } catch (SQLException ex) {
            System.out.println("error in fillMaskRow line 2290");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

        String readyCode = "{"
                + "\"respOK\":\"true\","
                + "\"formName\":\"" + this.getName() + "\","
                + "\"formID\":\"" + this.getID() + "\","
                + "\"formType\":\"" + this.getType() + "\","
                + "\"formCopyTag\":\"" + this.getCopyTag() + "\","
                + "\"htmlCode\":\"" + encodeURIComponent(htmlCode) + "\""
                + "}";

        return htmlCode;
    }

    public String fillMapIFRAMEform(String event) {

        System.out.println("MISURE MAP FORM: " + this.formWidth + "-" + this.formHeight);
        String W = this.formWidth;
        String H = this.formHeight;
        String htmlCode = "";
        String SQLphrase = "";
        makeQualifiedQuery();
        SQLphrase = browserArgsReplace(this.query);
        System.out.println("SQLphrase = " + SQLphrase);
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        ResultSet rs;
        try {
            SQLphrase = browserArgsReplace(this.query);

            Statement s = conny.prepareStatement(SQLphrase,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY); //questo serve per poter scorrere i record e riocomeinciare da capo
            rs = s.executeQuery(SQLphrase);
            int lines = 0;

            while (rs.next()) {
                lines++;
                String IDmappa = rs.getString("ID");
                // costruisco una mappa per ogni riga

                String params = "\"params\":\"" + encodeURIComponent(myParams.makePORTALparams()) + "\"";
                String connectors = "\"connectors\":[{\"door\":\"iframe\","
                        + "\"event\":\"" + event + "\","
                        + "\"table\":\"" + this.mainTable + "\","// es operatori
                        + "\"formID\":\"" + this.ID + "\","// è l'ID del mio FORM di tipo MAP
                        + "\"keyfield\":\"ID\","//es operatori.ID

                        + "\"keyValue\":\"" + IDmappa + "\","// è l'ID del mio FORM di tipo MAP
                        + "\"keyType\":\"TEXT\","
                        + "\"TBS\":\"" + encodeURIComponent(this.sendToCRUD) + "\" "//es. media
                        + " }]";

                System.out.println("connectors:" + connectors);
                String utils = "\"responseType\":\"text\"";
                String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";

                String iframe = "<iframe id='myMap' "
                        + "width='" + W + "' height='" + H + "' "
                        + "frameborder='0' marginheight='10' "
                        + "marginwidth='10' "
                        + "src='portal?target=requestsManager&gp=";
                iframe += encodeURIComponent(gp);
                iframe += "' ></iframe>";
                htmlCode += iframe;
                System.out.println("iframe:" + iframe);
            }

        } catch (SQLException ex) {
            System.out.println("error in paintMapForm line 1704");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conny.close();
        } catch (SQLException ex) {
            System.out.println("error in fillMaskRow line 2290");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

//        for (int jj = 0; jj < this.objects.size(); jj++) {
//            SQLphrase = browserArgsReplace(this.objects.get(jj).Origin.getQuery());
//            htmlCode += (this.objects.get(jj).getName() + ") SQLphrase = " + SQLphrase);
//            System.out.println(" Content.Type = " + this.objects.get(jj).Content.Type);
//        }
//        
        String readyCode = "{"
                + "\"respOK\":\"true\","
                + "\"formName\":\"" + this.getName() + "\","
                + "\"formID\":\"" + this.getID() + "\","
                + "\"formType\":\"" + this.getType() + "\","
                + "\"formCopyTag\":\"" + this.getCopyTag() + "\","
                + "\"htmlCode\":\"" + encodeURIComponent(htmlCode) + "\""
                + "}";

        //  System.out.println(htmlCode);
//        formResponse.setHtmlCode(readyCode);
        return htmlCode;
    }

    public ShowItFormResponse paintTimelineForm() {
//        System.out.println("\n\n********************************\npaintTimelineForm\n");
        String htmlCode = "";
        String SQLphrase = "";
////////        makeQualifiedQuery();
////////        System.out.println("this.query = " + this.query);
////////        String SQLphrase = browserArgsReplace(this.query);
////////        System.out.println("SQLphrase = " + SQLphrase);
        gaiaCalendar myCalendar = new gaiaCalendar(myParams, mySettings);
        Calendar targetDay = Calendar.getInstance();
        ArrayList<schedule> panScheduled = new ArrayList<schedule>();
        ArrayList<Job> panJobs = new ArrayList<Job>();
        myCalendar.prepareDates(targetDay);
        for (int jj = 0; jj < this.objects.size(); jj++) {
            SQLphrase = browserArgsReplace(this.objects.get(jj).Origin.getQuery());
            System.out.println(this.objects.get(jj).getName() + ") SQLphrase = " + SQLphrase);
            System.out.println(" Content.Type = " + this.objects.get(jj).Content.Type);
            if (this.objects.get(jj).Content.Type.equalsIgnoreCase("scheduled")) {
                panScheduled = myCalendar.acquireScheduled(SQLphrase, panScheduled);
            } else if (this.objects.get(jj).Content.Type.equalsIgnoreCase("jobs")) {
                panJobs = myCalendar.acquireJobs(SQLphrase, panJobs);
            }

        }

        myCalendar.setScheduled(panScheduled);
        myCalendar.setJobs(panJobs);
        System.out.println(" panScheduled = " + panScheduled.size());
        System.out.println(" panJobs = " + panJobs.size());
        htmlCode = myCalendar.getHtmlCalendar("month");
//////        
//////        //apro le schedulazioni relative a questo servizio
//////        gaiaCalendar myCalendar = new gaiaCalendar(myParams, mySettings);
//////        Calendar targetDay = Calendar.getInstance();
//////        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
//////        System.out.println("Costruisco calendario basato sulla data di oggi: " + formatDate.format(targetDay.getTime()));
//////          htmlCode = myCalendar.getHtmlCalendar( "week", SQLphrase, targetDay);
        String readyCode = "{"
                + "\"respOK\":\"true\","
                + "\"formName\":\"" + this.getName() + "\","
                + "\"formID\":\"" + this.getID() + "\","
                + "\"formType\":\"" + this.getType() + "\","
                + "\"formCopyTag\":\"" + this.getCopyTag() + "\","
                + "\"htmlCode\":\"" + encodeURIComponent(htmlCode) + "\""
                + "}";

        //  System.out.println(htmlCode);
        formResponse.setHtmlCode(readyCode);
        return formResponse;
    }

    public ShowItFormResponse paintSmartForm() {
        makeQualifiedQuery();
        String SQLphrase = browserArgsReplace(this.query);
        System.out.println("paintSmartForm SQLphrase = " + SQLphrase);
        String knots = "{\"nkots\":["
                + "\"knot\":\"OBJ1-XXX\",\"chlds\":["
                + "{\"knot\":\"SUB1-1-YYY\",\"chlds\":[]},"
                + "{\"knot\":\"SUB1-2-YYY\",\"chlds\":[]},"
                + "{\"knot\":\"SUB1-3-YYY\",\"chlds\":[]}"
                + "],"
                + "\"knot\":\"OBJ2-WWW\",\"chlds\":[],"
                + "\"knot\":\"OBJ3-JJJ\",\"chlds\":["
                + "{\"knot\":\"SUB3-1-ZZZ\",\"chlds\":[]},"
                + "{\"knot\":\"SUB3-2-ZZZ\",\"chlds\":[]},"
                + "{\"knot\":\"SUB3-3-ZZZ\",\"chlds\":[]}"
                + "],"
                + "]}";

        String readyCode = "{"
                + "\"respOK\":\"true\","
                + "\"formName\":\"" + this.getName() + "\","
                + "\"formID\":\"" + this.getID() + "\","
                + "\"formType\":\"" + this.getType() + "\","
                + "\"formCopyTag\":\"" + this.getCopyTag() + "\","
                + "\"type\":\"knots\","
                + "\"knots\":\"" + encodeURIComponent(knots) + "\""
                + "}";

        //  System.out.println(htmlCode);
        formResponse.setHtmlCode(readyCode);
        return formResponse;
    }

    public ShowItFormResponse paintDataForm() {

        String htmlCode = "";

//==============================================================================    
        this.setVisualFilter("");
        this.setFirstRow(0);
        this.setNofRows(0);
        this.setVisualType("FORMFIRSTLOAD");
//==============================================================================                
        String topFrameStyle = ("style=\"height:30px;background: yellow;\" ");
        String className = "formTable";

        //  System.out.println("\n\n*********************************************\nPAINTFORM : this.type=" + this.type);
        if (this.type == null) {
            this.type = "TABLE";
        }
        if (this.type.equalsIgnoreCase("FILTER")) {//MENU PANEL o VISUAL PANEL
            className = "formPanel";
            topFrameStyle = "";
        } else if (this.type.equalsIgnoreCase("PANEL")) {//MENU PANEL o VISUAL PANEL
            className = "formPanel";
            topFrameStyle = "";
        } else if (this.type.equalsIgnoreCase("FILTERPANEL")) {//MULTILEVEL SLECTOR
            className = "filterpanel";
            topFrameStyle = "";
        } else if (this.type.equalsIgnoreCase("MLS")) {//MULTILEVEL SLECTOR
            className = "multilevelSelector";
            topFrameStyle = "";
        } else if (this.type.equalsIgnoreCase("SINGLEROWFORM")) {// SCHEDA SINGOLA
            className = "formTable";
        } else if (this.type.equalsIgnoreCase("INSERTROWFORM")) {// SCHEDA SINGOLA
            className = "formTable";
        } else if (this.type.equalsIgnoreCase("MULTF")) {// SCHEDA MULTIPLA
            className = "formTable";
        } else if (this.type.equalsIgnoreCase("CHLNK")) {//SCHEDA SECONDARIA PER LINKING (es. gruppi di appartenenza)
            className = "formTable";
        } else if (this.type.equalsIgnoreCase("SLCTF")) {//SCHEDA DI SELEZIONE MULTILIVELLO
            className = "formTable";
        } else {//PER DEFAULT E' UNA TABLE
            className = "formTable";
            topFrameStyle = ("style=\"height:50px;\" ");
            topFrameStyle = (" ");

        }
        //==============================================================================        

        //System.out.println("\n[]loadType : " + loadType);   
        if (loadType != null) { //questi parametri arrivano dal browser
            if (loadType.startsWith("[") || loadType.startsWith("{")) {
                System.out.println("SONO IN PAINTFORM :this.loadType=" + this.loadType);
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
                    Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {

                //  System.out.println("SONO IN PAINTFORM :this.loadType=" + this.loadType);
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
        System.out.println("this.getVisualType():" + this.getVisualType());
        // System.out.println("SONO IN PAINTFORM :VisualType=" + this.getVisualType());
        if (this.getVisualType().equalsIgnoreCase("singleRow")) {
            System.out.println("SHOWiTfORM-->CASO SINGLE ROW");
            // System.out.println("creo il filtro qualificato per ricerca");
            makeQualifiedQuery();

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
                whereClause = " WHERE " + this.getMainTable() + "." + this.getKEYfieldName() + " = " + pKEYvalue + " ";
            } else {
                String result = null;
                try {
                    result = java.net.URLEncoder.encode(pKEYvalue, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    System.out.println("error in line 1710");
                    Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (updRowWhereClause == null
                        || updRowWhereClause.equalsIgnoreCase("null")
                        || updRowWhereClause.length() < 1) {
                    whereClause = " WHERE " + this.getMainTable() + "." + this.getKEYfieldName() + " = '" + result + "'";
                    System.out.println("QUERY SEMPLICE: USO COME INDICE " + whereClause);
                } else {
                    whereClause = " WHERE " + updRowWhereClause + " ";
                    System.out.println("QUERY COMPLESSA: USO COME INDICE " + whereClause);
                }

            }
            // System.out.println("PAINTFORM_case singleRow_whereClause:" + whereClause);
            setInfoReceived(this.getToBeSent());

            //System.out.println("TIPO DI FORM:" + this.type);
            if (this.type.equalsIgnoreCase("SINGLEROWFORM")) {
                //----SINGOLA RIGA IN MASK-----------------------------------------------------------------------
                htmlCode = fillMaskRow(whereClause);
            } else {
                //----SINGOLA RIGA IN TABLE-----------------------------------------------------------------------
                htmlCode = fillRow(whereClause);
            }
//==============================================================================
            formResponse.setHtmlCode(htmlCode);
            return formResponse;
        } else if (this.getVisualType().equalsIgnoreCase("TABLEFILTER")) {
//            System.out.println("CASO TABLEFILTER");
//            System.out.println("creo il filtro qualificato per ricerca");
            makeQualifiedQuery();
            htmlCode += paintFormAttributes();
            if (this.type.equalsIgnoreCase("SINGLEROWFORM")) {// SCHEDA con pattern
                //----INTERA MASK-----------------------------------------------------------------------
                htmlCode += fillFormMask();
                //==============================================================================
            } else {// per default è una table
                //----INTERA TABLE-----------------------------------------------------------------------
                htmlCode += fillFormTable();
                //==============================================================================
            }
            formResponse.setHtmlCode(htmlCode);
            return formResponse;
        } else if (this.getVisualType()
                .equalsIgnoreCase("FORMFIRSTLOAD")) {
            // System.out.println("CASO FORM FIRST LOAD");
            //System.out.println("è un FORMFIRSTLOAD");
            htmlCode += "<div id=\"FRAME-" + this.getName() + "-" + this.getCopyTag() + "\" "
                    + "class= \"frame scrollableContainer\" >";
        }
        //==============================================================================
        String divPrefix = "CH-" + this.getID() + "-" + this.getCopyTag();
        String elementPrefix = this.getID() + "-" + this.getCopyTag();

        //==============================================================================
        String divTopBar = "";

        divTopBar += "<TABLE style=\"border-collapse: collapse; padding:1px \" id=\"" + elementPrefix + "-topBarTable\">";
        divTopBar += paintFormAbstract();
        divTopBar += paintTopBar();
        divTopBar += paintFormPanel();
        divTopBar += "</TABLE>";

        //==============================================================================
        String divLeft = "<div class=\"leftTab\" style=\"display: table-cell; vertical-align: top;\"  id=\"" + divPrefix + "-L\" ></div>";
        //  divLeft="LEFT";
        //==============================================================================
        String divRight = "";
        divRight += "<TABLE style=\"border-collapse: collapse; padding:1px \"><TR>\n";
        divRight += "</TR><TR><TD>\n";
        divRight += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"" + divPrefix + "-RT\" ></div>\n";
        divRight += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"CH-" + this.getName() + "-RT\" ></div>\n";
        divRight += "</TD></TR><TR><TD>\n";
        divRight += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"" + divPrefix + "-R\" ></div>\n";
        divRight += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"CH-" + this.getName() + "-R\" ></div>\n";
        divRight += "</TD></TR><TR><TD>\n";
        divRight += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"" + divPrefix + "-RB\" ></div>\n";
        divRight += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"CH-" + this.getName() + "-RB\" ></div>\n";
        divRight += "</TD></TR></TABLE>\n";
        //==============================================================================
        String divBottom = "";
        divBottom += "<div id=\"CHILDREN-" + elementPrefix + "\" >";
        divBottom += "<TABLE style=\"border-collapse: collapse; padding:1px \"><TR>\n";
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
        String divBody = "          <div id=\"FORM-" + this.name + "-" + this.getCopyTag() + "\"  ";
        divBody += "class = \"" + className + " \""
                + " style=\" width:" + this.getFormWidth() + "; "
                + " height:" + this.getFormHeight() + ";  "
                + "resize: vertical; "
                + "display: table-cell; vertical-align: top; overflow: auto; \" >";
        //==============================================================================
        String attributes = paintFormAttributes();
        //System.out.println("ho scritto sul form i segg attributi:" + attributes);
        divBody += attributes;
        //==============================================================================

        if (this.type.equalsIgnoreCase(
                "PANEL") || this.type.equalsIgnoreCase("FILTER")) {//MENU PANEL o VISUAL PANEL
            //  System.out.println("vado in fillFormPanel");
            divBody += fillFormPanel();
        } else if (this.type.equalsIgnoreCase(
                "FILTERPANEL")) {// SCHEDA SINGOLA
            makeQualifiedQuery();
            divBody += fillFormFilterpanel();
        } else if (this.type.equalsIgnoreCase(
                "MLS")) {// MULTILEVEL SELECTOR
            int nofSelectors = 0;
            for (int jj = 0; jj < this.objects.size(); jj++) {
                if (this.objects.get(jj).CG.getType().equalsIgnoreCase("levelSelector")) {
                    nofSelectors++;
                }
            }
            nofSelectors--;
            divBody += ("      <INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-LASTLEVEL\" value=" + nofSelectors + ">\n");
            divBody += ("      <DIV id=\"" + this.getID() + "-" + this.getCopyTag() + "-LEVEL-0" + "\">\n");
            divBody += fillFormMLS(0);
            divBody += ("      </DIV>\n");

        } else if (this.type.equalsIgnoreCase(
                "SINGLEROWFORM")) {// SCHEDA SINGOLA
            makeQualifiedQuery();
            divBody += paintFormAttributes();
            divBody += fillFormMask();
        } else if (this.type.equalsIgnoreCase(
                "INSERTROWMASK")) {// NEW ROW SU SCHEDA SINGOLA
            System.out.println("TIPO:" + this.type);
            makeQualifiedQuery();
            divBody += paintFormAttributes();
            divBody += fillFormNewRowMask();
        } else if (this.type.equalsIgnoreCase(
                "MULTITAB")) {// SCHEDA MULTIPLA
            makeQualifiedQuery();
            divBody += fillMultiTab();
        } else if (this.type.equalsIgnoreCase(
                "MULTF")) {// SCHEDA MULTIPLA
            makeQualifiedQuery();
            divBody += fillFormTable();
        } else if (this.type.equalsIgnoreCase(
                "SLCTF")) {//SCHEDA DI SELEZIONE MULTILIVELLO

        } else if (this.type.equalsIgnoreCase(
                "MAP")) {
            makeQualifiedQuery();
            divBody += fillMapIFRAMEform(this.type);
        } else if (this.type.equalsIgnoreCase(
                "SYNOPTICMAPBUILDER")) {
            makeQualifiedQuery();
            divBody += fillMapBuilderForm(this.type);
        } else if (this.type.equalsIgnoreCase(
                "SYNOPTICMAP")) {
            makeQualifiedQuery();
            divBody += fillMapIFRAMEform(this.type);
        } else {//PER DEFAULT E' UNA TABLE
            System.out.println("PER DEFAULT SUPPONGO SIA UNA TABLE:" + this.query);
            makeQualifiedQuery();
            divBody += fillFormTable();
        }

        divBody += "</div>\n"; // chiude FORM-xxxxxx

        //==============================================================================
        String divExtR = "";

        divExtR += "<TABLE style=\"border-collapse: collapse; padding:1px \"><TR>\n";
        divExtR += "</TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"" + divPrefix + "-ExtRT\" ></div>\n";
        divExtR += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"CH-" + this.getName() + "-ExtRT\" ></div>\n";
        divExtR += "</TD></TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"" + divPrefix + "-ExtR\" ></div>\n";
        divExtR += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"CH-" + this.getName() + "-ExtR\" ></div>\n";
        divExtR += "</TD></TR><TR><TD>\n";
        divExtR += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"" + divPrefix + "-ExtRB\" ></div>\n";
        divExtR += "<div class=\"rightTab\"  style=\"display: table-cell; vertical-align: top;\"  id=\"CH-" + this.getName() + "-ExtRB\" ></div>\n";
        divExtR += "</TD></TR></TABLE>\n";

        //==============================================================================
        String divExtB = "<div class=\"bottomTab\" id=\"" + divPrefix + "-ExtB\" ></div>\n";
        divExtB += "<div class=\"bottomTab\" id=\"CH-" + this.getName() + "-ExtB\" ></div>\n";
        //==============================================================================
        String divIntRight = "<div class=\"bottomTab\" id=\"" + divPrefix + "-IntR\" ></div>\n";
        divIntRight += "<div class=\"bottomTab\" id=\"CH-" + this.getName() + "-IntR\" ></div>\n";
        //==============================================================================
        String outL = "<div class=\"sideText\" id=\"" + divPrefix + "-OutL\" ></div>\n";
        outL += "<div class=\"bottomTab\" id=\"CH-" + this.getName() + "-OutL\" ></div>\n";
        //==============================================================================
        String outR = "<div class=\"bottomTab\" id=\"" + divPrefix + "-OutR\" ></div>\n";
        outR += "<div class=\"bottomTab\" id=\"CH-" + this.getName() + "-OutR\" ></div>\n";
        //==============================================================================

        String divBodyUpDown = "";

        divBodyUpDown += "<TABLE style=\"border-collapse: collapse; padding:1px \">";
        divBodyUpDown += "<TR><TD style=\"display: table-cell; vertical-align: top;\">";

        divBodyUpDown += "<TABLE style=\"border-collapse: collapse; padding:1px \"><TR>"
                + "<TD style=\"display: table-cell; vertical-align: top;\">";

        divBodyUpDown += "<DIV id=\"" + this.getID() + "-" + this.getCopyTag() + "-TABDIV\">";
        divBodyUpDown += divBody;
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
        String mainBodyTable = "";
        mainBodyTable += "<TABLE id=\"" + elementPrefix + "-mainBodyTable\" "
                + "style=\"border-collapse: collapse;\""
                + "  width = '" + this.getFormWidth() + "' "
                + ">";

        mainBodyTable += "<TR>";
        mainBodyTable += "<TD colSpan=\"3\">" + divTopBar + "</TD>"; // chiude riga TOPBAR
        mainBodyTable += "</TR>";

        mainBodyTable += "<TR><TD style=\"vertical-align:top\">" + divLeft + "</TD>";
        mainBodyTable += "<TD style=\"vertical-align:top\">" + divBodyUpDown + "</TD>";
        mainBodyTable += "<TD style=\"vertical-align:top\">" + divRight + "</TD></TR>";
        mainBodyTable += "</TABLE>";
//-----------------------------------------------------------------------  
        String fileTable = "";
        fileTable += "<TABLE width = '" + this.getFormWidth() + "' >";
        fileTable += "<TR>" + "<TD style=\"vertical-align:top\">" + mainBodyTable + "</TD>";
        fileTable += "<TD style=\"vertical-align:top\">" + divExtR + "</TD>" + "</TR>";
        fileTable += "<TR>" + "<TD colspan=\"2\" >" + divExtB + "</TD>" + "</TR>";
        fileTable += "</TABLE>";

        String mainQPtable = "";

        mainQPtable += "<TABLE>";
        mainQPtable += "<TR><TD style=\"display: table-cell; vertical-align: top;\">";
        mainQPtable += outL;
        mainQPtable += "</TD><TD style=\"display: table-cell; vertical-align: top; \" >";
        mainQPtable += fileTable;
        mainQPtable += "</TD><TD style=\"display: table-cell; vertical-align: top;\">";
        mainQPtable += outR;
        mainQPtable += "</TD></TR>";
        mainQPtable += "</TABLE>";

        htmlCode += mainQPtable;

        if (this.getVisualType().equalsIgnoreCase("FORMFIRSTLOAD")) {
            htmlCode += ("</div>");
        }
        if (this.getVisualType().equalsIgnoreCase("DATAONLY")) {//richiesta refresh tramite websocket
            htmlCode = divBody;
            System.out.println("CASO DATAONLY");
        }

        htmlCode = "{"
                + "\"respOK\":\"true\","
                + "\"formName\":\"" + this.getName() + "\","
                + "\"formID\":\"" + this.getID() + "\","
                + "\"formType\":\"" + this.getType() + "\","
                + "\"formCopyTag\":\"" + this.getCopyTag() + "\","
                + "\"htmlCode\":\"" + encodeURIComponent(htmlCode) + "\""
                + "}";

        //  System.out.println(htmlCode);
        formResponse.setHtmlCode(htmlCode);
        return formResponse;

    }

    public String paintTopBar() {
        String htmlCode = "";

        htmlCode += "";
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
        String defaultTopColor = myManager.getDirective("TopBarDefaultColor");

        //   System.out.println("JSON PARSING:" + this.getGes_topBar());
        String topColor = "yellow";
        if (defaultTopColor != null && defaultTopColor.length() > 0) {
            topColor = defaultTopColor;
        }
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
                Logger.getLogger(ShowItForm.class
                        .getName()).log(Level.SEVERE, null, ex);
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
                    + "    display:block;\" >";

            htmlCode += "<tr><td>"; // xxxxxxxxxxxxxxxxxxxxx  table che contiene tutti i child

            htmlCode += ("<div "
                    //   + " class=\"topTab\" "
                    + " id=\"CH-" + this.getID() + "-" + this.getCopyTag() + "-T\" ");
            htmlCode += " style=\"width:" + topWidth + ";"
                    + "height:" + topHeight + ";"
                    + "background:" + topColor + ";\" ";
            htmlCode += ("> ");
//-------------------------------------
//T3 OPEN
            htmlCode += ("<TABLE "
                    + "style= \""
                    //+ "height:"+ this.getFormHeight()+"; "
                    + "width: " + this.getFormWidth() + "; "
                    + "\" "
                    + ">"
                    + "<TR> ") // tble che contiene la riga 1
                    ;
            if (this.getLabel() == null || this.getLabel() == "null") {
                this.setLabel(this.getName());
            }
            String key = this.getFatherKEYvalue();

            htmlCode += "<td style=\"font-size:20px; padding-right:3px; \"> ";

            if (togglerIcon.equalsIgnoreCase("true")) {
                htmlCode += " <img style=\"margin:0px 5px 0px 0px;\" id=\"CH-" + this.getID() + "-" + this.getCopyTag() + "-TOGGLERIMG\" "
                        + " height=\"" + togglerHeight + "px\"  align=\"left\" src='./media/icons/gaiaTopHide.png' alt='S/H'"
                        + " onclick=\"javascript:toggleChild('CH-" + this.getID() + "-" + this.getCopyTag() + "')\" >";
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
            htmlCode += "</td><td style=\"font-size:10px; padding-right:10px; \"> ";
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
        htmlCode += (" <tr><td><div  id=\"" + this.getID() + "-" + this.getCopyTag() + "-SNACKBAR\" >");

        htmlCode += (" </div></td></tr>");

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

////////            htmlCode += ("Cerca: <INPUT type=\"TEXT\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-FILTER\" "
////////                    + "value=\"\"  ");
////////            
////////            
////////            htmlCode += ("onkeyup=\"javascript:filterChanges('" + this.getID() + "','" + this.getCopyTag() + "')\" >\n");
////////            htmlCode += (" "
////////                    + "\n");
            htmlCode += ("<INPUT type=\"BUTTON\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-SEARCHBUTTON\" value=\" Vai !\" "
                    + "onClick=\"javascript:filterChanges('" + this.getID() + "','" + this.getCopyTag() + "')\" >\n");
            htmlCode += "</FORM> ";
            htmlCode += "</td></tr> ";
        }

        return htmlCode;
    }

    public String paintFormPanel() {
        String htmlCode = "";

        if (this.formObjects.size() > 0) {
            htmlCode += ("<tr><td>");
            htmlCode += ("<TABLE><tr>");
            for (int jj = 0; jj < this.formObjects.size(); jj++) {
                //    System.out.println("DISEGNO OGGETTO dentro un FORM: " + this.formObjects.get(jj).getName());
                objRight objRights = analyzeRightsRuleJson(this.formObjects.get(jj).Content.getModifiable(), null, null, 400);

                htmlCode += ("<td>");
                htmlCode += paintObject(this.getID() + "-" + this.getCopyTag(), this.formObjects.get(jj));
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
        htmlCode += abst;

        return htmlCode;
    }

    public String paintFormAttributes() {
        //==============================================================================
        String htmlCode = "";
        queryUsed = prepareSQL();
        if (queryUsed == null) {
            queryUsed = "";
        }
        if (this.getSendToCRUD() == null) {
            this.setSendToCRUD("");
        }
//        System.out.println("paintFormAttributes=====> SQL:\n" + queryUsed);
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.name + "-" + this.getCopyTag() + "-ID\" value=\"" + this.getID() + "-" + this.getCopyTag() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-FATHER\" value=\"" + this.getFather() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-FATHERARGS\" value=\"" + this.getFatherFilters() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-FATHERKEYVALUE\" value=\"" + this.getFatherKEYvalue() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-FATHERKEYTYPE\" value=\"" + this.getFatherKEYtype() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-FATHERCOPYTAG\" value=\"" + this.getFatherCopyTag() + "\">\n");

        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-FIELDFILTERED\" value=\"" + this.getFilteredElements() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-TOBESENT\" value=\"" + encodeURIComponent("" + this.getToBeSent() + "") + "\">\n");

        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-EXTENDEDNAME\" value=\"" + this.getName() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-FILSEQ\" value=\"" + this.getFilterSequence() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-KEYfieldName\" value=\"" + this.getKEYfieldName() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-KEYfieldType\" value=\"" + this.getKEYfieldType() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-ROUTINEONLOAD\" value=\"" + this.getGes_routineOnLoad() + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-KEYfieldValue\" value=\"\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-ADDPOS\" value=\"" + this.addRowPosition + "\">\n");

        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-QUERYUSED\" value=\"" + encodeURIComponent(queryUsed) + "\">\n");
        htmlCode += ("<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-STC\" value=\"" + encodeURIComponent(this.getSendToCRUD()) + "\">\n");

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
                Logger
                        .getLogger(ShowItForm.class
                                .getName()).log(Level.SEVERE, null, ex);
            }
        }

//==============================================================================
        return htmlCode;
    }

    public String fillFormMLS(int selectorLevel) {

        if (this.objects.size() < (selectorLevel + 1)) {
            selectorLevel = this.objects.size() - 1;
        }
        if (selectorLevel < 0) {
            selectorLevel = 0;
        }
        // per ogni oggetto presente vado in paintObject...
        String htmlCode = "";

        htmlCode += paintMLS(this.getID(), this.getCopyTag(), selectorLevel, this.objects.get(selectorLevel));

        return htmlCode;
    }

    public String fillFormFilterpanel() {
        this.setToBeSent(browserArgsReplace(this.getToBeSent()));

        // per ogni oggetto presente vado in paintObject...
        String htmlCode = "";
        htmlCode += ("<TABLE class=\"formPanel\"><tr>\n");

        for (int obj = 0; obj < this.objects.size(); obj++) {
            this.objects.get(obj).Content.setThisRowModifiable(5);
            String ValoreDaScrivere = this.objects.get(obj).getLabelHeader();
            if (ValoreDaScrivere == null || ValoreDaScrivere == "null" || ValoreDaScrivere.length() < 1) {
                ValoreDaScrivere = this.objects.get(obj).getLabelHeader();
            }
            if (ValoreDaScrivere == null || ValoreDaScrivere == "null" || ValoreDaScrivere.length() < 1) {
                ValoreDaScrivere = this.objects.get(obj).getName();
            }
            System.out.println("obj:" + obj + "  -->" + ValoreDaScrivere);
            //RICAVO VALORE DA SCRIVERE.
            this.objects.get(obj).setValueToWrite(ValoreDaScrivere);
            System.out.println("fillFormFilterpanel:paintPanelElement(" + this.objects.get(obj).getName() + ")");

            htmlCode += paintPanelfilterElement(this.objects.get(obj));
        }

        htmlCode += ("</tr></TABLE>\n");
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
        // </editor-fold>    
//====END=ROW=================== 
        htmlCode += ("</tr></TABLE>\n");
        return htmlCode;
    }

    public String fillMaskRow(String WhereClause) {
//        System.out.println("-fillMaskRow--this.query:" + this.query);
//        System.out.println("-fillMaskRow--WhereClause:" + WhereClause);
        String htmlCode = "";
        String coda = "";
        String Gcoda = "";
        if (this.query != null && this.query.length() > 1) {

            String smartTile = "";
            String afterWHERE = "";
            String smartPartToKeep = "";
            // System.out.println("----SMART--- query iniziale:" + this.query);
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
                        smartTile = " ORDER BY " + text.substring(lastORDERBYposition + 8, text.length());
                    }
                } else {
                    //c'è un groupby da considerare dopo il where e se c'è conterrà anche l'order by
                    smartPartToKeep = this.query.substring(0, lastGROPUPBYposition);
                    smartTile = " GROUP BY " + text.substring(lastGROPUPBYposition + 8, text.length());
                }

            } else {
                // System.out.println("----SMART--- posizione ultimo WHERE:" + lastWHEREposition);
                //2. tutta la parte di testo prima del WHERE è da tenere          
                smartPartToKeep = this.query.substring(0, lastWHEREposition);
                // System.out.println("----SMART--- da tenere:" + smartPartToKeep);

                afterWHERE = this.query.substring(lastWHEREposition + 5, this.query.length());
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

            this.query = smartPartToKeep + WhereClause + smartTile;
        }
        // System.out.println("-fillMaskRow-synthesis:" + this.query);
        buildObjectsOriginList();
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        ResultSet rs;
        /*
         String htmlCode = "";
         Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalStandardDB();

         ResultSet rs;*/
        // cerca il FORM per nome e se non è compilato per ID
        try {
            String SQLphrase = browserArgsReplace(this.query);
            //  System.out.println("-fillMaskRow-SQLphrase:" + SQLphrase);

            if (SQLphrase == null || SQLphrase == "") {
                return "ERROR LOADING SINGLE ROW FORM. NO QUERY";
            }
            Statement s = conny.prepareStatement(SQLphrase,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY); //questo serve per poter scorrere i record e riocomeinciare da capo
            rs = s.executeQuery(SQLphrase);
            int lines = 0;

            while (rs.next()) {
                lines++;
                //System.out.println("------------------------riga:" + lines);
                htmlCode += paintMaskRow(rs, 0, "normal");
                //System.out.println("------------------------fine riga:" + lines);
                break;
            }

        } catch (SQLException ex) {
            System.out.println("error in fillMaskRow line 2284");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conny.close();
        } catch (SQLException ex) {
            System.out.println("error in fillMaskRow line 2290");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

        return htmlCode;

    }

    public String prepareSQL() {
        String SQLphrase = "";

        // sostituisco nei paramebrowserArgsReplacentri TBS eventuali marker con il loro valore
        this.setToBeSent(browserArgsReplace(this.getToBeSent()));
//        System.out.println("ShowItForm-fillFormTable()-SQL FORM era:" + this.query);
        // interrogo la main table (tramite this.query) per avere le righe da mmostrare
        SQLphrase = browserArgsReplace(this.query);
        System.out.println("ShowItForm-fillFormTable()-SQL FORM diventa:" + SQLphrase);
        return SQLphrase;
    }

    public String fillFormNewRowMask() {

        String htmlCode = "";
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        htmlCode += "<TABLE style=\"width:" + this.getFormWidth() + ";\">";
        //=========================================
        htmlCode += "<tr><td>";
        htmlCode += "<div "
                + " class=\"tabBody\""
                + "style=\" height:" + this.getFormHeight() + ";   "
                + "resize: vertical;\n"
                + "    overflow: auto; \""
                + ">";
        htmlCode += "<TABLE id=\"" + this.getID() + "-" + this.getCopyTag() + "-TABLE\" ";
        htmlCode += "  >";
        htmlCode += "<tbody>";
        int splitterPagesEnabled = 1;
        int righeScritte = 0;
        int righeParsate = 0;
//CICLO RIGHE TABELLA=========================================
        //   System.out.println("\nRIGA ADDING NEW ROW------------------------paintMaskRow...");

        htmlCode += paintMaskRow(null, 0, "adding");
        htmlCode += "</tbody>";
        htmlCode += "</TABLE></div>";
//=========================================
        htmlCode += "</td></tr></table>";

//System.out.println("Righe DATI:" + lines);
        return htmlCode;

    }

    public String fillFormMask() {
        String htmlCode = "";
        if (rowsPerPage < 1) {
            rowsPerPage = 50;
        }
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();

        ResultSet rs;
        // cerca il FORM per nome e se non è compilato per ID
        try {
            String SQLphrase = prepareSQL();

            if (SQLphrase == null || SQLphrase == "") {
                return "ERROR LOADING FORM TABLE.";
            }
            System.out.println("\n*************\n***********\nfillFormMask >>SQLphrase DATI:" + SQLphrase);
            //System.out.println("this.getVisualType():" + this.getVisualType());
//=====CONTO LE RIGHE TOTALI=================================================
            Statement s = conny.prepareStatement(SQLphrase,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY); //questo serve per poter scorrere i record e riocomeinciare da capo
            rs = s.executeQuery(SQLphrase);
            int rowsCounter = 0;
            while (rs.next()) {
                rowsCounter++;
            }
            //System.out.println("trovate:" + rowsCounter + " righe.");
            rs.beforeFirst();

            float rawTotaPages = (float) rowsCounter / rowsPerPage;
            int totalPages = (int) Math.floor(rowsCounter / rowsPerPage);
            if (rawTotaPages > totalPages) {
                totalPages++;
            }
            //System.out.println("in totale ci saranno " + totalPages + " pagine da " + rowsPerPage + " righe ciascuna.");
            if (this.getVisualType().equalsIgnoreCase("FORMFIRSTLOAD")) {
                if (this.addRowPosition != null && this.addRowPosition.equalsIgnoreCase("TOP")) {
                    currentPage = 1;
                } else {
                    currentPage = totalPages;
                }

            }

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }
            if (currentPage < 1) {
                currentPage = 1;
            }

            if (rowsPerPage < 1 || rowsPerPage > 150) {
                rowsPerPage = 50;
            }
            //System.out.println("currentPage:" + currentPage);
            //System.out.println("rowsPerPage:" + rowsPerPage);
            //=====TABELLA PAGINE=================================================
            int lines = 0;

            htmlCode += "<TABLE style=\"width:" + this.getFormWidth() + ";\">";

            htmlCode += "<tr><td>";

            if (rowsCounter > rowsPerPage) {
                // devo creare i pulsanti di navigazione
                htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-CURPAGE\" value=\"" + this.getCurrentPage() + "\">";

                htmlCode += "<TABLE  "
                        + "style=\""
                        + "    font-family: verdana, arial, helvetica, sans-serif;\n"
                        + "    font-size: 9px;\n"
                        + "    cellspacing: 0; \n"
                        + "\"><tr>";
                htmlCode += "<td>PAGES: </td>";
                for (int hh = 1; hh <= totalPages; hh++) {
                    htmlCode += "<td><div  ";
                    htmlCode += "style=\""
                            + " font-size:9px;"
                            + " text-align: center;\n"
                            + " display:block;"
                            + " width:20px;"
                            + " height:20px;";
                    if (hh == currentPage) {
                        htmlCode += " background:lightYellow;"
                                + "    border-collapse: collapse; \n"
                                + "    border-right: 2px solid #9; \n"
                                + "    border-bottom: 2px solid #9; \n"
                                + "    border-top: 1px solid #999; \n"
                                + "    border-left: 1px solid #999; \n";
                    } else {
                        htmlCode += " background:lightGrey;"
                                + "    border-collapse: collapse; \n"
                                + "    border-right: 1px solid #999; \n"
                                + "    border-bottom: 1px solid #999; \n"
                                + "    border-top: 2px solid #9; \n"
                                + "    border-left: 2px solid #9; \n";

                    }
                    htmlCode += "\"   "
                            + "onclick='javascript:changeCurPage(\"" + this.getID() + "\",\"" + this.getCopyTag() + "\"," + hh + ")'";
                    htmlCode += "  >";
                    htmlCode += hh;

                    htmlCode += "</div></td>";

                }

                htmlCode += "</tr></TABLE>";
                htmlCode += "</td></tr>";
                htmlCode += "<tr><td>";
            }

            htmlCode += "</td></tr>";
//=========================================
            htmlCode += "<tr><td>";

            htmlCode += "<div "
                    + " class=\"tabBody\""
                    + "style=\" height:" + this.getFormHeight() + ";   "
                    + "resize: vertical;\n"
                    + "    overflow: auto; \""
                    + ">";
            htmlCode += "<TABLE id=\"" + this.getID() + "-" + this.getCopyTag() + "-TABLE\" ";
            htmlCode += "  >";

            htmlCode += "<tbody>";
            int splitterPagesEnabled = 1;
            int righeScritte = 0;
            int righeParsate = 0;
            System.out.println("===========================================");
            System.out.println("==PREPARO CODICE PE ADDING E TOTAL ROWS====");
            System.out.println("===========================================");

            String totalRowCode = paintMaskRow(rs, 0, "total");
            String addingRowCode = paintMaskRow(rs, 0, "adding");

            System.out.println("===================================");
            System.out.println("==INIZIO PARSING RIGHE MASK ROW====");
            System.out.println("===================================");

            String normalRowsCode = "";
//CICLO RIGHE TABELLA=========================================
            while (rs.next()) {
                righeScritte++;

                objRight rowRights = analyzeRightsRuleJson(this.getDisableRules(), rs, null, 200);
//                System.out.println("++RIGA MASK n." + righeScritte);
                for (int jj = 0; jj < this.objects.size(); jj++) {
//                    System.out.println("++oggetto " + this.objects.get(jj).getName());
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
//                        System.out.println("++partial " + this.objects.get(jj).Content.getActualSum());
                    }
                }

//------------------------------------                
                if (righeScritte > (rowsPerPage * currentPage) && splitterPagesEnabled > 0) {

                    // eseguo comunque la somma per i totali
                } else if (righeScritte <= (rowsPerPage * (currentPage - 1)) && splitterPagesEnabled > 0) {
                    // queste righe sono precedenti alla pagina che mi interessa... non le scrivo

                } else {
                    lines++;
//NORMAL ROW=========================================                    
                    //  System.out.println("------------------------riga:" + lines);
                    String myNewLine = paintMaskRow(rs, righeScritte, "normal");
                    normalRowsCode += myNewLine;
                    // System.out.println(" fillFormMask >>\n" + myNewLine);
                    //System.out.println("------------------------fine riga:" + lines);
                }
            }
            //System.out.println(" fillFormMask >> DECIDO COME ASSEMBLARE LA PAGINA\n");
            //formRightsRules.print();
//            System.out.println("===================================");
//            System.out.println("==FINE PARSING RIGHE MASK ROW====");
//            System.out.println("===================================");

//            System.out.println("addingRowCode >>\n" + addingRowCode);
            if (this.addRowPosition != null && this.addRowPosition.equalsIgnoreCase("TOP")) {
                //===CASO FORMMASK CON ADD IN ALTO
                if (formRightsRules.canCreate > 0) {
                    htmlCode += addingRowCode;
                }
                htmlCode += totalRowCode;
                htmlCode += normalRowsCode;
            } else {
                //===CASO FORMMASK CON ADD IN BASSO   
                htmlCode += normalRowsCode;
                htmlCode += totalRowCode;
                if (formRightsRules.canCreate > 0) {
                    htmlCode += addingRowCode;
                }
            }

//TOTALS========================================
//            System.out.println("------------------------addRowPosition:" + this.addRowPosition);
            htmlCode += "</tbody>";
            htmlCode += "</TABLE></div>";
            //=========================================

            htmlCode += "</td></tr></table>";

            //System.out.println("Righe DATI:" + lines);
        } catch (SQLException ex) {
            System.out.println("error in line 2929");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conny.close();

        } catch (SQLException ex) {
            Logger.getLogger(ShowItForm.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return htmlCode;
    }

    public String fillFormTable() {

        String htmlCode = "";
        if (rowsPerPage < 1) {
            rowsPerPage = 50;
        }
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();

        ResultSet rs;
        // cerca il FORM per nome e se non è compilato per ID
        try {
            String SQLphrase = prepareSQL();

            if (SQLphrase == null || SQLphrase == "") {
                return "ERROR LOADING FORM TABLE.";
            }
            System.out.println("\n-----------------\nfillFormTable >>SQLphrase DATI:\n" + SQLphrase);

//=====CONTO LE RIGHE TOTALI=================================================
            Statement s = conny.prepareStatement(SQLphrase,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY); //questo serve per poter scorrere i record e riocomeinciare da capo
            rs = s.executeQuery(SQLphrase);
            int rowsCounter = 0;
            while (rs.next()) {
                rowsCounter++;
            }
            rs.beforeFirst();

            float rawTotaPages = (float) rowsCounter / rowsPerPage;
            int totalPages = (int) Math.floor(rowsCounter / rowsPerPage);
            if (rawTotaPages > totalPages) {
                totalPages++;
            }

            if (this.getVisualType().equalsIgnoreCase("FORMFIRSTLOAD")) {
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
            htmlCode += "<TABLE style=\"width:" + this.getFormWidth() + ";\">";

            String totalRowCode = "";
            String addingRowCode = "";
            String normalRowsCode = "";

            htmlCode += getPagesCode(rowsCounter, totalPages);

            htmlCode += getHeaderCode();
//=========================================            
//MAIN TABLE=========================================  
            htmlCode += "<tr><td>";

            htmlCode += "<div "
                    + " class=\"tabBody\""
                    + "style=\" height:" + this.getFormHeight() + ";   "
                    + "resize: vertical;\n"
                    + "    overflow: auto; \""
                    + ">";
            // spazio tabella nuda

            htmlCode += "<TABLE id=\"" + this.getID() + "-" + this.getCopyTag() + "-TABLE\" ";
            htmlCode += "  >";
            htmlCode += "<tbody>";
            int splitterPagesEnabled = 1;
            int righeScritte = 0;
            int righeParsate = 0;

            //System.out.println("\n::::::::::::::::\naddingRowCode:" + addingRowCode);
//GESTIONE PERMESSI RIGA PRIMA DEL LOOP RIGHE=========================================            
            /*
            PRIMA DEL CCICLO PER OGNI RIGA DELLA TABELLA devo 
            analizzare gli oggetti e vedere se hanno regole di modifica/visualizzazione generali da applicare
             */
            for (int jj = 0; jj < this.objects.size(); jj++) {

                this.objects.get(jj).objRights = analyzeRightsRuleJson(this.objects.get(jj).Content.getModifiable(), null, null, 400); //curObj.objRights=analyzeRightsRuleJson(curObj.Content.getModifiable(), null);
// prima del ciclo righe ho assegnato a ogni oggetto il suo right in base a modifiable
// durante lil parsing delle righe farò modifiche ai diritti solo se 
// esistono dei triggers sull'oggetto
            }

//CICLO RIGHE TABELLA=========================================
            while (rs.next()) {
                righeScritte++;
                //  System.out.println("\n::::::::::::::::\nSCRIVO RIGA TABELLA. n." + righeScritte);
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
                        System.out.println("Tab_partial " + this.objects.get(jj).Content.getActualSum());

                    }
                }

//------------------------------------                
                if (righeScritte > (rowsPerPage * currentPage) && splitterPagesEnabled > 0) {
                    // eseguo comunque la somma per i totali
                } else if (righeScritte <= (rowsPerPage * (currentPage - 1)) && splitterPagesEnabled > 0) {
                    // queste righe sono precedenti alla pagina che mi interessa... non le scrivo
                } else {
                    lines++;
//NORMAL ROW=========================================    

                    //  System.out.println("------------------------riga:" + lines);
                    normalRowsCode += paintRow(rs, righeScritte, "normal");
                    //  System.out.println("------------------------fine riga:" + lines);
                }
            }

            totalRowCode = paintRow(rs, 0, "total");
            addingRowCode = paintRow(rs, 0, "adding");
            //mostro i TOTALI DEGLI OGGETTI CON HASsUM
            for (int jj = 0; jj < this.objects.size(); jj++) {
                if (this.objects.get(jj).Content.getType() != null
                        && this.objects.get(jj).Content.getType().equalsIgnoreCase("INT")
                        && this.objects.get(jj).Content.getHasSum() > 0) {
                    // System.out.println("++oggetto "+this.objects.get(jj).getName()+" - SUM ="+ this.objects.get(jj).Content.getActualSum());

                }
            }

            //   System.out.println(" fillFormTable >> DECIDO COME ASSEMBLARE LA PAGINA\n");
            // formRightsRules.print();
            if (this.addRowPosition != null && this.addRowPosition.equalsIgnoreCase("TOP")) {
                //===CASO FORMMASK CON ADD IN ALTO
                if (formRightsRules.canCreate > 0) {
                    htmlCode += addingRowCode;
                }
                htmlCode += totalRowCode;
                htmlCode += normalRowsCode;
            } else {
                htmlCode += normalRowsCode;
                htmlCode += totalRowCode;
                if (formRightsRules.canCreate > 0) {
                    htmlCode += addingRowCode;
                }
            }

            htmlCode += "</tbody>";
            htmlCode += "</TABLE></div>";
            //=========================================

            htmlCode += "</td></tr></table>";

            //System.out.println("Righe DATI:" + lines);
        } catch (SQLException ex) {
            System.out.println("error in line 3143");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conny.close();

        } catch (SQLException ex) {
            Logger.getLogger(ShowItForm.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return htmlCode;
    }

    public String fillMultiTab() {
        String htmlCode = "";
//        System.out.println("SONO IN MULTITAB: ");
//        System.out.println("SQL: " + this.getQuery());
//        System.out.println("DESCRITORI: " + this.getFilteredElements());
        String label = "";
        String key = "";
        ArrayList<SelectListLine> parametri = new ArrayList<SelectListLine>();

        String[] blocks = this.getFilteredElements().split(";");
        List<String> block = Arrays.asList(blocks);
        if (block.size() > 1) {
            for (int bb = 0; bb < block.size(); bb++) {
                String nome = "";
                String valore = "";
                String[] couples = block.get(bb).split(":");
                List<String> param = Arrays.asList(couples);
                if (param.size() > 1) {
                    SelectListLine parametro = new SelectListLine();
                    parametro.setLabel(param.get(0));
                    parametro.setValue(param.get(1));
                    parametri.add(parametro);
                }
            }
        }

        for (SelectListLine person : parametri) {
            if (person.getLabel().equalsIgnoreCase("Label")) {
                //result.add(person);
                label = person.getValue();
                break;
            }
        }
        for (SelectListLine person : parametri) {
            if (person.getLabel().equalsIgnoreCase("key")) {
                //result.add(person);
                key = person.getValue();
                break;
            }
        }

        //     System.out.println("Label: " + label + " - key: " + key);
        return htmlCode;
    }

    public String paintMLS(String formID, String copyTag, int level, ShowItObject curObj) {

        String htmlCode = "";
        //String SQLphrase = "";

        String oQuery = browserArgsReplace(curObj.Origin.getQuery());
        String rifForm = formID + "-" + copyTag;
        //    System.out.println("Entro in paint MLS: " + oQuery);
        String oLabelField = curObj.Origin.getLabelField();
        String oValueField = curObj.Origin.getValueField();
        String oValueFieldType = curObj.Origin.getValueFieldType();
        curObj.Origin.setSelectList(new SelectList(myParams, mySettings, oQuery, oLabelField, oValueField, oValueFieldType));
        curObj.Origin.getSelectList().getList();
        //String KEYvalue = "L" + obj;
        SelectList myList = curObj.Origin.getSelectList();

        htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + rifForm + "-" + curObj.name + "-ACTION\" value=\"" + curObj.getActionPerformed() + "\">";
        htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + rifForm + "-" + curObj.name + "-ACTPAR\" value=\"" + curObj.getActionParams() + "\">";
        htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + rifForm + "-" + curObj.name + "-TOBESENT\" value=\"" + curObj.CG.getValue() + "\">";

        htmlCode += "<SELECT id=\"" + rifForm + "-" + curObj.name + "-" + level + "\" ";
        if (oValueFieldType != null && (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT"))) {
            htmlCode += "value=0    ";
        } else {
            htmlCode += "value=\"\"   ";
        }

        if (formRightsRules.canModify > 0) {

            // htmlCode += " onChange=\"javascript:MLSchanged('" + this.getID()+ "-"+this.getCopyTag()  + "-" + this.objects.get(obj).name + "-" + KEYvalue + "')\"  ";
            htmlCode += " onChange='javascript:MLSchanged({\"formID\":\"" + formID + "\",\"copyTag\":\"" + copyTag + "\",\"objName\":\"" + curObj.name + "\",\"objID\":\"" + curObj.ID + "\",\"Level\":" + level + "})'  ";

        } else {
            htmlCode += " disabled ";
        }

        htmlCode += " class=\"MLS\" ";
        htmlCode += " style=\"width:\"" + curObj.C.getWidth() + "; ";
        htmlCode += "   >";

        htmlCode += "<OPTION  ";
        if (oValueFieldType != null && (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT"))) {
            htmlCode += "value=0   ";
        } else {
            htmlCode += "value=\"\"   ";
        }
        htmlCode += ">";
        htmlCode += "->" + curObj.getLabelHeader();
        htmlCode += "</OPTION>";

        for (int hh = 0; hh < myList.list.size(); hh++) {
            htmlCode += "<OPTION  ";
            if (oValueFieldType != null && (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT"))) {
                htmlCode += "value= " + myList.list.get(hh).getValue() + "    ";
            } else {
                htmlCode += "value=\"" + myList.list.get(hh).getValue() + "\"   ";
            }

            htmlCode += ">";
            htmlCode += myList.list.get(hh).getLabel();
            htmlCode += "</OPTION>";
        }
        htmlCode += "</SELECT>";
        int nextSelector = level + 1;
        htmlCode += ("<DIV id=\"" + formID + "-" + copyTag + "-LEVEL-" + nextSelector + "\"></DIV>\n");
        return htmlCode;
    }

    public String paintPanelfilterElement(ShowItObject curObj) {
        String htmlCode = "";
        if (curObj.CG.Type != null && curObj.CG.Type.equalsIgnoreCase("HTML")) {
//System.out.println("PAINT PANEL ELEMENT HTML:" + curObj.getName()  );

            //   System.out.println("VADO in browserArgsReplace per:" + curObj.CG.getValue());
            String code = browserArgsReplace(curObj.CG.getValue());
            //System.out.println("HTML:" + code  );
            htmlCode += code;

        } else {

            htmlCode += "<td style=\"padding-right: 10px;padding-left: 10px;\">";
            //------------------------------------------------- 
            // nel panel il keyvalue viene sostituito dall'ID dell  form   ... tanto per scrivere qualcosa
            // System.out.println("VADO in paintObject per:" + curObj.getName());
            htmlCode += paintObject(this.getID() + "-" + this.getCopyTag(), curObj); //poichè è un pannello non ha una key di riga: quindi uso il nome del pannello (this.ID)
            //------------------------------------------------- 
            htmlCode += "</td>\n";

        }

        return htmlCode;
    }

    public String paintPanelElement(ShowItObject curObj) {

        String htmlCode = "";
//        System.out.println("Diritti in base a :" + curObj.Content.getModifiable());
        if (curObj.Content.getModifiable() != null) {
            if (curObj.Content.getModifiable().startsWith("DEFAULT:4")
                    || curObj.Content.getModifiable().startsWith("DEFAULT:5")) {
                curObj.Content.setModifiable("[{\"ruleType\":\"default\",\"right\":\"255\"}]");
//                System.out.println("in paintPanelElement diventa :" + curObj.Content.getModifiable());
            }
        }

        //---DIRITTI--- DETERMINO I DIRITTI PER OGGETTO IN GENERALE (LIVELLO FORM)
        objRight objRights = analyzeRightsRuleJson(curObj.Content.getModifiable(), null, null, 400); //curObj.objRights=analyzeRightsRuleJson(curObj.Content.getModifiable(), null);
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

            htmlCode += paintObject(panelKeyValue, curObj, objRights);
            //------------------------------------------------- 
            htmlCode += "</td>\n";

        }

        return htmlCode;
    }

    public String getPagesCode(int rowsCounter, int totalPages) {

        String pagesCode = "";
        //=====PAGES=================================================
        if (rowsCounter > rowsPerPage) {
            pagesCode += "<tr><td>";
            // devo creare i pulsanti di navigazione
            pagesCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-CURPAGE\" value=\"" + this.getCurrentPage() + "\">";

            pagesCode += "<TABLE  "
                    + "style=\""
                    + "    font-family: verdana, arial, helvetica, sans-serif;\n"
                    + "    font-size: 9px;\n"
                    + "    cellspacing: 0; \n"
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
                        + "onclick='javascript:changeCurPage(\"" + this.getID() + "\",\"" + this.getCopyTag() + "\"," + hh + ")'";
                pagesCode += "  >";
                pagesCode += hh;

                pagesCode += "</div></td>";

            }
            pagesCode += "</tr></TABLE>";
            pagesCode += "</td></tr>";

        }
//========================================= 
        return pagesCode;
    }

    public String getHeaderCode() {
        //HEADER========================================= 
        String headerCode = "";
        System.out.println("\nthis.showHeader:" + this.showHeader);

        if (this.advancedFiltered != null && this.advancedFiltered.equalsIgnoreCase("TRUE")) {
            System.out.println(" this.advancedFiltered:" + this.advancedFiltered);

            headerCode += "<tr><td>";
            headerCode += "<div class=\"tabHeader\">";
            headerCode += paintAdvancedFilters();
            headerCode += "</div>";
            headerCode += "</td></tr>";
        } else {

            if (this.showHeader != null && this.showHeader.equalsIgnoreCase("false")) {
            } else {
                headerCode += "<tr><td>";
                headerCode += "<div class=\"tabHeader\">";
                headerCode += paintHeader();
                headerCode += "</div>";
                headerCode += "</td></tr>";
            }
        }
//=========================================
        return headerCode;
    }

    public String paintAdvancedFilters() {
        System.out.println(" this.filteredElements:" + this.filteredElements);
        String[] fields;
        fields = this.filteredElements.split(";");
        List<String> filtri = Arrays.asList(fields);
        System.out.println(" filtri.size:" + filtri.size());

        for (int i = 0; i < filtri.size(); i++) {

            System.out.println(i + ") FILTRO:" + filtri.get(i));
        }

        String htmlCode = "";
        htmlCode += "<TABLE>";
        htmlCode += "<tr>";
        if (this.getShowCounter() != null && this.getShowCounter().equalsIgnoreCase("FALSE")) {
            htmlCode += "<td></td>";
        } else {
            //==================================================================    
            // htmlCode += "<td style=\"overflow-x: hidden; width:50px\" >";
            htmlCode += "<td class=\"headerSelector\" style=\"width:40px;\">";

            for (int obj = 0; obj < this.objects.size(); obj++) {
                htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + this.objects.get(obj).name + "-TYPE\" "
                        + "value=\"" + this.objects.get(obj).Content.getType() + "\">";
            }
            htmlCode += "</td>";
        }
        //==================================================================    
        if (formRightsRules.canDelete > 0) {
            htmlCode += "<td class=\"lineDeleter\" style=\"overflow-x: hidden; \" >X</td>";
        }

        //==================================================================    
        for (int obj = 0; obj < this.objects.size(); obj++) {

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
            htmlCode += "<td class=\"tabHeader\"  style=\"";
            if (objVisibile == false) {
                htmlCode += " width:-1px;  ";
            } else {
                String myWidth = "";
                if (this.objects.get(obj).C.getWidth() != null && this.objects.get(obj).C.getWidth() != "null" && this.objects.get(obj).C.getWidth() != "") {
                    myWidth = this.objects.get(obj).C.getWidth();
                    myWidth = myWidth.replace("px", "");
                    int newValue = Integer.parseInt(myWidth);
                    if (newValue > 2) {
                        newValue = newValue - 2;
                    }
                    myWidth = newValue + "px";
                }
                htmlCode += " width:" + myWidth + ";  ";

            }
            htmlCode += "  overflow-x: hidden; text-align:center; \">";
            if (objVisibile == true) {

                htmlCode += fieldName;
            }
            htmlCode += "</td>";

        }
        htmlCode += "</tr>";

        //******************************************************
        htmlCode += "<tr>";
        if (this.getShowCounter() != null && this.getShowCounter().equalsIgnoreCase("FALSE")) {
            htmlCode += "<td></td>";
        } else {
            //==================================================================    
            // htmlCode += "<td style=\"overflow-x: hidden; width:50px\" >";
            htmlCode += "<td class=\"headerSelector\" style=\"width:40px;\">";

            for (int obj = 0; obj < this.objects.size(); obj++) {
                htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + this.objects.get(obj).name + "-TYPE\" "
                        + "value=\"" + this.objects.get(obj).Content.getType() + "\">";
            }
            htmlCode += "</td>";
        }
        //==================================================================    
        if (formRightsRules.canDelete > 0) {
            htmlCode += "<td class=\"lineDeleter\" style=\"overflow-x: hidden; \" >X</td>";
        }

        //==================================================================    
        for (int obj = 0; obj < this.objects.size(); obj++) {

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
            htmlCode += "<td class=\"tabHeader\"  style=\"";
            String myWidth = "";
            if (objVisibile == false) {
                htmlCode += " width:-1px;  ";
            } else {

                if (this.objects.get(obj).C.getWidth() != null && this.objects.get(obj).C.getWidth() != "null" && this.objects.get(obj).C.getWidth() != "") {
                    myWidth = this.objects.get(obj).C.getWidth();
                    myWidth = myWidth.replace("px", "");
                    int newValue = Integer.parseInt(myWidth);
                    if (newValue > 2) {
                        newValue = newValue - 2;
                    }
                    myWidth = newValue + "px";
                }
                htmlCode += " width:" + myWidth + ";  ";

            }
            htmlCode += "  overflow-x: hidden;\">";
            if (objVisibile == true) {

                htmlCode += "<TABLE style = \"border-spacing: 0;  border-collapse: collapse;\"><TR><TD>";
                htmlCode += paintOrderArrows(this.objects.get(obj).getName());
                htmlCode += "</TD><TD>";

                boolean filtered = false;
                for (int i = 0; i < filtri.size(); i++) {
                    if (this.objects.get(obj).getName().equalsIgnoreCase(filtri.get(i))) {
                        filtered = true;
                        break;
                    }
                }
                if (filtered == true) {
                    // qui cambio filtro secondo il tipo di field
                    htmlCode += ("<INPUT type=\"TEXT\" "
                            + "id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + this.objects.get(obj).getName() + "-FILTER\" "
                            //                    + " onChange=\"javascript:filterChanges('" + this.getID() + "','" + this.getCopyTag() + "')\" "
                            + "style=\"background-color:lightBlue;   "
                            + " width:" + myWidth + ";  \""
                            + "onkeydown = \"if (event.keyCode == 13) {"
                            + "advancedFilterChanges('" + this.getID() + "','" + this.getCopyTag() + "') ; "
                            + "event.returnValue=false;"
                            + "event.cancel=true;}\" "
                            + "value=\"\"  >\n");

                }
                htmlCode += "</TD>";
                htmlCode += "</TR></TABLE>";
            }
            htmlCode += "</td>";

        }
        htmlCode += "</tr>";

        htmlCode += "</TABLE>";
        return htmlCode;
    }

    public String paintOrderArrows(String objName) {
        String htmlCode = "<TABLE style = \"border-spacing: 0;  border-collapse: collapse;\"><TR><TD>";
        htmlCode += "<a onclick=\"orderByChanges('" + this.getID() + "', '" + this.getCopyTag() + "', '" + objName + "','A')\"> ";
        htmlCode += "<img  height=\"7\" width=\"7\" align=\"middle\" src='./media/icons/orderASC.png' alt='^' ></img>";
        htmlCode += "</a>";
        htmlCode += "</TD></TR><TR><TD>";
        htmlCode += "<a onclick=\"orderByChanges('" + this.getID() + "', '" + this.getCopyTag() + "', '" + objName + "','D')\"> ";
        htmlCode += " <img  height=\"7\" width=\"7\" align=\"middle\" src='./media/icons/orderDSC.png' alt='v' ></img>";
        htmlCode += "</a>";
        htmlCode += "</TD>";
        htmlCode += "</TR></TABLE>";

        return htmlCode;
    }

    public String paintHeader() {
        String htmlCode = "";
        htmlCode += "<TABLE><tr>";
        if (this.getShowCounter() != null && this.getShowCounter().equalsIgnoreCase("FALSE")) {
            htmlCode += "<td></td>";
        } else {
            //==================================================================    
            // htmlCode += "<td style=\"overflow-x: hidden; width:50px\" >";
            htmlCode += "<td class=\"headerSelector\" style=\"width:40px;\">";

            for (int obj = 0; obj < this.objects.size(); obj++) {
                htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + this.objects.get(obj).name + "-TYPE\" "
                        + "value=\"" + this.objects.get(obj).Content.getType() + "\">";
            }
            htmlCode += "</td>";
        }
        //==================================================================    
        if (formRightsRules.canDelete > 0) {
            htmlCode += "<td class=\"lineDeleter\" style=\"overflow-x: hidden; \" >X</td>";
        }

        //==================================================================    
        for (int obj = 0; obj < this.objects.size(); obj++) {

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
            htmlCode += "<td class=\"tabHeader\"  style=\"";
            if (objVisibile == false) {
                htmlCode += " width:-1px;  ";
            } else {
                String myWidth = "";
                if (this.objects.get(obj).C.getWidth() != null && this.objects.get(obj).C.getWidth() != "null" && this.objects.get(obj).C.getWidth() != "") {
                    myWidth = this.objects.get(obj).C.getWidth();
                    myWidth = myWidth.replace("px", "");
                    int newValue = Integer.parseInt(myWidth);
                    if (newValue > 2) {
                        newValue = newValue - 2;
                    }
                    myWidth = newValue + "px";
                }
                htmlCode += " width:" + myWidth + ";  ";

            }
            htmlCode += "  overflow-x: hidden;\">";
            if (objVisibile == true) {
                htmlCode += fieldName;
            }
            htmlCode += "</td>";

        }
        htmlCode += "</tr></TABLE>";
        return htmlCode;

    }

    public String paintMaskRow(ResultSet rs, int lineNumber, String rowType) {

        objRight rowRights = valutaRightsRiga(this.getDisableRules(), rs);/// analizzo il LOCKER del form per la riga
//        System.out.println("paintMaskRow:\nRiga n. " + lineNumber);
//        System.out.println("UNISCO DIRITTI FORM:\n" + formRightsRules.totalRight + " LEVEL :" + formRightsRules.level);
//        System.out.println("CON DIRITTI RIGA (LOCKERS):" + this.getDisableRules() + ":\n" + rowRights.totalRight + " LEVEL :" + rowRights.level);
        objRight actualRowRights = joinRights(formRightsRules, rowRights);
//        System.out.println("OTTENGO:\n" + actualRowRights.totalRight + " LEVEL :" + actualRowRights.level);

        String htmlCode = "";
        String pattern = this.getHtmlPattern();

        try {
            if (rowType.equalsIgnoreCase("adding")) {
                pattern = getPatternNewRow(pattern);
// <editor-fold defaultstate="collapsed" desc="CASO ADDING ROW.">   

                String KEYvalue = "NEW";
                htmlCode += "<tr id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-NEW-ROW\" class=\"tabAddRow\" >";
                htmlCode += paintRowSelector(lineNumber, KEYvalue);
////////                String xLineNumber = "" + lineNumber;
////////                if (lineNumber == 0) {
////////                    xLineNumber = "NEW";
////////                }
////////                htmlCode += "<td class=\"lineSelector\""
////////                        //+ " style=\"padding: 0;\""
////////                        + "onClick=\"javascript:rowSelected('" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-SEL')\">"
////////                        + "<a id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-SEL\" "
////////                        + "style=\""
////////                        + " height: inherit;"
////////                        + " padding: 1em;"
////////                        + "\"><font size='1'><i><b>" + xLineNumber + "</b></i></font> "
////////                        + ""// -> HERE
////////                        + "</a></td>";
                // delete button-------------

                if (formRightsRules.canDelete > 0) {
                    htmlCode += "<td class=\"lineDeleter\"   >";
                    htmlCode += "</td>";
                }

                htmlCode += " <td>";
                htmlCode += "<TABLE style=\"width:" + this.getFormWidth() + ";\">";

//=========================================
                htmlCode += "<tr id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-ROW\" class=\"unselectedRow\" >";
                htmlCode += " <td>";
////////xxx
////////                //---DIRITTI--- DETERMINO I DIRITTI PER OGGETTO IN QUESTA PRECISA RIGA   
////////                objRight rowRights = this.formRightsRules;
////////                if (this.getDisableRules() != null && this.getDisableRules().length() > 4) {
////////                    System.out.println("Trovato locker di riga:" + this.getDisableRules());
////////                    rowRights = analyzeRightsRuleJson(this.getDisableRules(), rs, null);
////////                    System.out.println("rowRights.canModify:" + rowRights.canModify);
////////                } else {
////////                    rowRights = this.formRightsRules;
////////                }
////////
////////                

//in realtà questa è l'adding row, quindi non so se devo controllare alo stesso modo i diritti di riga (lockers)
                htmlCode += populatePattern(pattern, rs, actualRowRights, KEYvalue);
                htmlCode += "</td></tr></table>";
                // </editor-fold>    
//====END=ROW===================        
                htmlCode += "</td></tr>";
//</editor-fold>  
            } else if (rowType.equalsIgnoreCase("total")) {
// <editor-fold defaultstate="collapsed" desc="TOTALS ROW."> 
                if (formRightsRules.canView > 0) {
                    String totLabel = "";
                    for (int obj = 0; obj < this.objects.size(); obj++) {
                        if (this.objects.get(obj).Content.getHasSum() > 0) {
                            totLabel = "TOT:";
                            break;
                        }
                    }
                    htmlCode += "<tr class=\"tabTotalsRow\" >";

                    htmlCode += " </tr>";

                }

                //</editor-fold> 
            } else {
// <editor-fold defaultstate="collapsed" desc="CASO NORMAL ROW.">   
                // System.out.println(" KEYfieldName :" + this.getKEYfieldName() + " KEYfieldType :" + this.getKEYfieldType());
                //  System.out.println("CASO NORMAL ROW" + htmlCode);
                String KEYvalue = "";
                pattern = eliminaCaseIf(pattern, rs);
                if (this.getKEYfieldName() != null) {
                    if (this.getKEYfieldType() != null && this.getKEYfieldType().equalsIgnoreCase("INT")) {
                        int myKEYvalue;
                        myKEYvalue = rs.getInt(this.getKEYfieldName());
                        KEYvalue = "" + myKEYvalue;
                        //System.out.println("Prendo numerico: KEYfieldName :" + this.getKEYfieldName() + " KEYfieldType :" + this.getKEYfieldType());
                    } else {

                        KEYvalue = rs.getString(this.getKEYfieldName());
                        //System.out.println("Prendo stringa: KEYfieldName :" + this.getKEYfieldName() + " KEYfieldType :" + this.getKEYfieldType());
                    }
                } else {
                    KEYvalue = "" + lineNumber;
//                    System.out.println("-------------");
//                    System.out.println(this.query);
//                    System.out.println("paintRow:AUTO INDICIZZAZIONE ATTIVATA !!!");
//                    System.out.println("-------------");
                }
//=======ROW===================        
                //stabilisco il colore di background
                //       String ValueAssigned = getBGcolor(this.getRowBGcolor(), rs);
////////                objRight rowRights = this.formRightsRules;
////////                if (this.getDisableRules() != null && this.getDisableRules().length() > 4) {
////////                    rowRights = analyzeRightsRuleJson(this.getDisableRules(), rs, null);
////////                } else {
////////                    rowRights = this.formRightsRules;
////////                    rowRights.level = 10;
////////                }

                htmlCode += "<tr id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-ROW\" class=\"unselectedRow\" >";
                htmlCode += paintRowSelector(lineNumber, KEYvalue);
////////                // row selector-------------
////////                String xLineNumber = "" + lineNumber;
////////                if (lineNumber == 0) {
////////                    xLineNumber = "NEW";
////////                }
////////
////////                try {
////////                    if (this.getVisualType() != null && this.getVisualType().equalsIgnoreCase("singleRow")) {
////////                        xLineNumber = "UPD";
////////                    }
////////                } catch (Exception e) {
////////
////////                }
////////                htmlCode += "<td class=\"lineSelector\""
////////                        //+ " style=\"padding: 0;\""
////////                        + "onClick=\"javascript:rowSelected('" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-SEL')\">"
////////                        + "<a id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-SEL\" "
////////                        + "style=\""
////////                        //+ " display: block; \n"
////////                        //+ "    min-height: 100%;\n"
////////                        //+ "    height: auto !important;\n"
////////                        + "    height: inherit;"
////////                        + "padding: 1em;"
////////                        + "\" "
////////                        //+ "onClick=\"javascript:rowSelected('" +this.getID()+ "-"+this.getCopyTag() + "-" + KEYvalue + "-SEL')\">"
////////                        + "><font size='1'><i><b>" + xLineNumber + "</b></i></font> "
////////                        + ""// -> HERE
////////                        + "</a></td>";
                // delete button-------------

                if (formRightsRules.canDelete > 0) {
                    htmlCode += "<td class=\"lineDeleter\"   >"
                            + "<a id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-DEL\"   ";
                    if (actualRowRights.canDelete > 0) {
                        String jsonArgs = "{";
                        jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                        jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                        jsonArgs += "\"objName\":\"\",";
                        jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                        jsonArgs += "\"operation\":\"DEL\",";
                        jsonArgs += "\"cellType\":\"X\",";
                        jsonArgs += "\"filterField\":\"\",";
                        jsonArgs += "\"exitRoutine\":\"dummy()\"}";
                        htmlCode += " onClick='javascript:cellChanged(" + jsonArgs + ")' >";
                        htmlCode += " <img  height=\"15\" width=\"15\" align=\"middle\" src='./media/icons/IconDELETE.gif' alt='ELIMINA' ";

                        htmlCode += " ></img>";
                        htmlCode += "</a> ";
                    }

                    htmlCode += "</td>";
                }

                System.out.println("\n--PaintMaskRow_elaboraRigaRS per riga n." + lineNumber);
                elaboraRigaRS(rs, actualRowRights);
////////                //************************************************************************
////////                for (int obj = 0; obj < this.objects.size(); obj++) {
////////                    this.objects.get(obj).setValueToWrite(ricavoValoreDaScrivere(rs, obj));
////////                    // in principio la modificabilità dell'oggetto nella riga è la stessa dell'oggetto in generale
////////                    this.objects.get(obj).Content.setThisRowModifiable(actualRowRights.canModify);
////////                }
                htmlCode += " <td>";
                htmlCode += "<TABLE style=\"width:" + this.getFormWidth() + ";\">";

//=========================================
//                String pattern = this.getHtmlPattern();
//                System.out.println("\nPATTERN:\n" + pattern);
                KEYvalue = "";
                if (this.getKEYfieldName() != null) {
                    if (this.getKEYfieldType() != null && this.getKEYfieldType().equalsIgnoreCase("INT")) {
                        int myKEYvalue;
                        myKEYvalue = rs.getInt(this.getKEYfieldName());
                        KEYvalue = "" + myKEYvalue;
                    } else {
                        KEYvalue = rs.getString(this.getKEYfieldName());
                    }
                } else {
                    System.out.println("\n\npaintMaskRow:NON RIESCO A RICAVARE IL KEY FIELD DELLA TABELLA!!!\n\n\n");
                }

                htmlCode += "<tr id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-ROW\" class=\"unselectedRow\" >";
                htmlCode += " <td>";
//                System.out.println("PARTO PER POPULATE PATTERN CON DIRITTI DI RIGA:" + rowRights.totalRight + " LEVEL:" + rowRights.level);

                htmlCode += populatePattern(pattern, rs, actualRowRights, KEYvalue);
                //=========================================

                htmlCode += "</td></tr></table>";
                // </editor-fold>    
//====END=ROW===================        
                htmlCode += "</td></tr>";
//</editor-fold> 

            }

        } catch (SQLException ex) {
            System.out.println("SQLException in PAINTROW:");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

        // htmlCode = eliminaCaseIf(htmlCode, rs);
        //System.out.println("\nnew Text=:" + text);
        return htmlCode;
    }

    public String populatePattern(String pattern, ResultSet rs, objRight actualRowRights, String KEYvalue) {
        // String pattern="";
        ArrayList<SelectListLine> XXX = new ArrayList<SelectListLine>();
        // objects-------------
//        System.out.println("+=================================+");
//        System.out.println("|=INIZIO PARSING OGGETTI===|");
//        System.out.println("+=================================+");
        for (int obj = 0; obj < this.objects.size(); obj++) {
//            System.out.println("\n-populatePattern OGGETTO:" + this.objects.get(obj).name
//                    + " VAL:" + this.objects.get(obj).ValueToWrite);
//            System.out.println(" TYPE:" + this.objects.get(obj).C.Type);
            //-------------------------------------------+
            // come nel caso del paintRow (tabella) anche nel form devo valutare i triggers
            String triggeredStyle = feedTriggeredStyle(this.objects.get(obj), rs);
            if (triggeredStyle != null && triggeredStyle.length() > 2) {
//                        System.out.println("Imposto lo stile da trigger come default: " + triggeredStyle);
                this.objects.get(obj).setTriggeredStyle(triggeredStyle);
            } else {
                this.objects.get(obj).setTriggeredStyle("");
            }
            //------------------------------------
            this.objects.get(obj).setValueToWrite(ricavoValoreDaScrivere(rs, obj));
            objRight realObjRights = valutaRightsOggetto(this.objects.get(obj), rs);
            objRight actualObjectRights = joinRights(realObjRights, actualRowRights);
//            System.out.println("populateRow:\nUNISCO DIRITTI RIGA:" + actualRowRights.totalRight + " LEVEL :" + actualRowRights.level);
//            System.out.println("CON DIRITTI OGGETTO:" + realObjRights.totalRight + " LEVEL :" + realObjRights.level);
//            System.out.println("DIRITTO RISULTANTE:" + actualObjectRights.totalRight + " LEVEL: " + actualObjectRights.level);

//------------------------------------------------- 
            String objectCode = "";

            objectCode = paintObject(KEYvalue, this.objects.get(obj), actualObjectRights);
            SelectListLine mybound = new SelectListLine();
            mybound.setMarker("(-(" + this.objects.get(obj).getName() + ")-)");
            mybound.setLabel("(!(" + this.objects.get(obj).getName() + ")!)");
            mybound.setValue(objectCode);
            String labl = this.objects.get(obj).getLabelHeader();
            if (labl == null || labl.length() < 1) {
                labl = this.objects.get(obj).getName();
            }
            mybound.setSpareValue(labl);
            XXX.add(mybound);
            // System.out.println("objectCode per mask:\n" + objectCode);

            if (pattern != null && this.objects.get(obj).getName() != null) {
                pattern = pattern.replace("@@@" + this.objects.get(obj).getName() + "@@@", mybound.getMarker());
                pattern = pattern.replace("@!@" + this.objects.get(obj).getName() + "@!@", mybound.getLabel());
            }
//            System.out.println("pattern:\n" + pattern);
        }
//        System.out.println("+=================================+");
//        System.out.println("|=FINE PARSING OGGETTI===|");
//        System.out.println("+=================================+");
        // QUESTO PER EVITARE SOSTITUZIONI RICORSIVE
        // DENTRO AL TESTO  GIA' SOSTITUITO
        for (int jj = 0; jj < XXX.size(); jj++) {
            pattern = pattern.replace(XXX.get(jj).getMarker(), XXX.get(jj).getValue());
            pattern = pattern.replace(XXX.get(jj).getLabel(), XXX.get(jj).getSpareValue());
        }
//        System.out.println("FINAL pattern:\n" + pattern);
        return pattern;
    }

    public String getRowImageHtmlCode(BufferedImage image, String alternativeString, objectLayout myBox) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String picCode = "";
        String imageString = null;
        int radio = 10;
        if (image != null) {
            try {
                int HH = Integer.parseInt(myBox.getHeight());
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
        picCode += "<img src=\"data:image/gif;base64," + imageString + "\" alt=\"" + alternativeString + "\"";
        picCode += "   width=\"" + myBox.getWidth() + "px\" heigth=\"" + myBox.getHeight() + "px\" ";
        picCode += " />";
        System.out.println("picCode:\n" + picCode);
        return picCode;
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(qualityHints);
        g2.setClip(new RoundRectangle2D.Double(0, 0, w, h, w / 2, h / 2));
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return output;
    }

    public String getPatternNewRow(String text) {
        // System.out.println("\n>> getPatternNewRow:\n" + text);
        String buono = "";
        try {
            String start = "<!--CASE NEWLINE>";
            String end = "<!--ENDCASE NEWLINE>";
            int posizioneInizio = text.indexOf(start) + start.length();
            int posizioneFine = text.indexOf(end);
            buono = text.substring(posizioneInizio, posizioneFine);
//        System.out.println("buono:" + buono);        
        } catch (Exception e) {
            //System.out.println("\n>>NON TROVO IL CODICE SPECIFICO DELLA NEW LINE\n");
            buono = text;
        }
        return buono;
    }

    public String eliminaCaseNewLine(String text) {
        String buono = text;
        //System.out.println("\n>> eliminaCaseNewLine:\n" + text);

        try {
            //System.out.println("eliminaCaseNewLine:");
            String start = "<!--CASE NEWLINE>";
            String end = "<!--ENDCASE NEWLINE>";
            int posizioneInizio = text.indexOf(start);
            int posizioneFine = text.indexOf(end);
            //System.out.println("posizioneInizio:" + posizioneInizio);
            //System.out.println("posizioneFine:" + posizioneFine);

            String prima = text.substring(0, posizioneInizio);
            String dopo = text.substring(posizioneFine + end.length(), text.length());
            //System.out.println("prima:" + prima);
            //System.out.println("dopo:" + dopo);

            buono = prima + dopo;
            //System.out.println("buono:" + buono);
        } catch (Exception e) {
            //System.out.println("\n>>NON RIESCO A ELIMINBARE IL CODICE SPECIFICO DELLA NEW LINE\n");

            buono = text;
        }
        return buono;
    }

    public String eliminaCaseIf(String Xtext, ResultSet rs) {
        //System.out.println("\n>>RIPULISCO IL PATTERN DALLE PARTRI INUTILI\n");

        // qui analizzo la stringa della riga per cercare parti da eliminare in base al trigger di presenza
        // System.out.println("\n>> eliminaCaseIf:\n" + Xtext);
        String text = Xtext;
        try {
            text = eliminaCaseNewLine(Xtext);
        } catch (Exception e) {
            text = Xtext;
        }
//System.out.println("text:" + text);
        String word = "<!--CASE";
        int flagFound = 1;
        int repeats = 0;
        while (text.length() > 0 && flagFound > 0 && repeats < 300) {
            flagFound = 0;
            repeats++;
// find all occurrences forward
            for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                String segnoTest = "==";
                int inizioAperturaTrigger = i;
                int inizioChiusuraTrigger = 0;
                int fineChiusuraTrigger = 0;
                // cerco la riga completa di commento che contiene il trigger
                int fineAperturaTrigger = text.indexOf("-->", i + 1) + 3;
                String aperturaTrigger = text.substring(inizioAperturaTrigger, fineAperturaTrigger);
                //System.out.println(" TRIGGER:" + aperturaTrigger + "(" + aperturaTrigger.length() + ")");
                //ricavo il primo elemento del trigger; il separatore può essere '=='
                String campo = "";
                String valore = "";
                boolean esitoValutazione = false;
                try {
                    if (aperturaTrigger.contains("==")) {
                        segnoTest = "==";
                        int test = aperturaTrigger.indexOf("==");
                        campo = aperturaTrigger.substring(9, test);
                        campo = campo.trim();
                        valore = aperturaTrigger.substring(test + 2, aperturaTrigger.length() - 3);
                        valore = valore.trim();
                        String vCampo = "";
                        try {
                            vCampo = rs.getString(campo);
                        } catch (SQLException ex) {
                            Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //                        System.out.println(" campo:" + campo + " valore:" + valore);
//                        System.out.println("confronto valore di riga:" + rs.getString(campo));
// cerco esito valutazione
                        if (vCampo != null && vCampo.equalsIgnoreCase(valore)) {
                            esitoValutazione = true;
                            //System.out.println("MANTENGO IL TESTO HTML COMPRESO");
                        }

                    }

                    if (aperturaTrigger.contains("!=")) {
                        segnoTest = "!=";
                        int test = aperturaTrigger.indexOf("!=");
                        campo = aperturaTrigger.substring(9, test);
                        campo = campo.trim();
                        valore = aperturaTrigger.substring(test + 2, aperturaTrigger.length() - 3);
                        valore = valore.trim();
                        String vCampo = "";
                        try {
                            vCampo = rs.getString(campo);
                        } catch (SQLException ex) {
                            Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //                        System.out.println(" campo:" + campo + " valore:" + valore);
//                        System.out.println("confronto valore di riga:" + rs.getString(campo));
// cerco esito valutazione
                        if (vCampo == null || !vCampo.equalsIgnoreCase(valore)) {
                            esitoValutazione = true;
                            //System.out.println("MANTENGO IL TESTO HTML COMPRESO");
                        }

                    }
                } catch (Exception e) {
                    System.out.println("\nerror2989:" + e.toString());

                }
                if (esitoValutazione == false) {
                    //System.out.println("ELIMINO IL TESTO HTML COMPRESO");
                    if (campo.length() > 0) {
                        // ho trovato un trigger. adesso cerco la fine, valuto e agisco di conseguenza
                        inizioChiusuraTrigger = text.indexOf("<!--ENDCASE " + campo + segnoTest + valore, i + 1);
                        fineChiusuraTrigger = text.indexOf("-->", inizioChiusuraTrigger + 1) + 3;
                    }
                    String testa = "";
                    String coda = "";
                    testa = text.substring(0, inizioAperturaTrigger);
                    coda = text.substring(fineChiusuraTrigger, text.length());
                    text = testa + coda;
                    //System.out.println("\nnew Text=:" + text);
                    flagFound = 1;
                }

            } // prints "4", "13", "22"
        }
        // myNewLine = text;
        return text;
    }

    private String paintRowSelector(int lineNumber, String KEYvalue) {
        String htmlCode = "";
//        System.out.println("paintRowSelector:" + this.getShowCounter());
        if (this.getShowCounter() != null && this.getShowCounter().equalsIgnoreCase("FALSE")) {
            htmlCode += "<td></td>";
        } else {
            // row selector-------------
            String xLineNumber = "" + lineNumber;
            if (lineNumber == 0) {
                xLineNumber = "NEW";
            }
            try {
                if (this.getVisualType() != null && this.getVisualType().equalsIgnoreCase("singleRow")) {
                    xLineNumber = "UPD";
                }
            } catch (Exception e) {
            }
            htmlCode += "<td class=\"lineSelector\""
                    //+ " style=\"padding: 0;\""
                    + "onClick=\"javascript:rowSelected('" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-SEL')\">"
                    + "<a id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-SEL\" "
                    + "style=\""
                    + " height: inherit;"
                    + " padding: 1em;"
                    + "\"><font size='1'><i><b>" + xLineNumber + "</b></i></font> "
                    + "</a></td>";
        }

        return htmlCode;
    }

    public String paintRow(ResultSet rs, int lineNumber, String rowType) {
        objRight rowRights = valutaRightsRiga(this.getDisableRules(), rs);/// analizzo il LOCKER del form per la riga
//        System.out.println("paintRow:\nRiga n" + lineNumber);
//        System.out.println("UNISCO DIRITTI FORM:\n" + formRightsRules.totalRight + " LEVEL :" + formRightsRules.level);
//        System.out.println("CON DIRITTI RIGA (LOCKERS):" + this.getDisableRules() + ":\n" + rowRights.totalRight + " LEVEL :" + rowRights.level);
        objRight actualRowRights = joinRights(formRightsRules, rowRights);
//        System.out.println("OTTENGO:\n" + actualRowRights.totalRight + " LEVEL :" + actualRowRights.level);

        String htmlCode = "";

        try {
            if (rowType.equalsIgnoreCase("adding")) {
// <editor-fold defaultstate="collapsed" desc="CASO ADDING ROW.">
//                System.out.println("CASO ADDING ROW->formRightsRules.canCreate: " + formRightsRules.canCreate);
                if (actualRowRights.canCreate > 0) {
                    //  htmlCode += "<tr id=\"" + this.getID()+ "-"+this.getCopyTag() + "-" + KEYvalue + "-ROW\" class=\"unselectedRow\" >";            

                    htmlCode += "<tr id=\"" + this.getID() + "-" + this.getCopyTag() + "-NEW-ROW\"  class=\"tabAddRow\" >";

                    //htmlCode += " <div id=\"AR-" + this.getID()+ "-"+this.getCopyTag()  + "\">";
                    htmlCode += "<td  class=\"lineSelector\" > ";
                    //-------------
                    //-- VALUTO se inserire un pulsante di aggiunta riga (solo se c'è un counter )
                    int flag = 0;
                    int presentAddingFields = 0;
                    for (int obj = 0; obj < this.objects.size(); obj++) {
                        if (this.objects.get(obj).Content.isPrimaryFieldAutocompiled()) {
//                            System.out.println("IL CAMPO " + this.objects.get(obj).getName() + " è PRIMARIO E AUTOCOMPILANTE...");
                            flag++;
                        }
                        if (this.objects.get(obj).AddingRow_enabled > 0) {
                            presentAddingFields++;
                        }
                    }
                    if (flag > 0 && presentAddingFields == 0) { // asterisco per nuova riga senza inserimento di un particolare field
                        htmlCode += "<a";
                        String jsonArgs = "{";
                        jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                        jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                        jsonArgs += "\"objName\":\"INSERT_AI\",";
                        jsonArgs += "\"KEYvalue\":\"INSERT_AI\",";
                        jsonArgs += "\"operation\":\"NEW\",";
                        jsonArgs += "\"cellType\":\"AI\",";
                        jsonArgs += "\"valueType\":\"INT\",";
                        jsonArgs += "\"filterField\":\"\",";
                        jsonArgs += "\"exitRoutine\":\"\"}";
                        htmlCode += " onClick='javascript:cellChanged(" + jsonArgs + ")'  style=\"block\">";
                        htmlCode += " <img src=\"./media/iconADD.png\" alt=\"NEW\" "
                                + "style=\"margin-left: auto; margin-right: auto;width:12px;height:12px;border:0\">";
                        htmlCode += "</a>";
                    }
                    //-------------
                    htmlCode += " </td>";

                    if (formRightsRules.canDelete > 0) {
                        htmlCode += "<td  class=\"lineDeleter\"  > </td>";
                    }
//                    System.out.println("COMPILO " + this.objects.size() + " CAMPI IN ADDING ROW");
                    for (int obj = 0; obj < this.objects.size(); obj++) {

                        this.objects.get(obj).setValueToWrite("");

                        htmlCode += "<td  class=\"newlineField\" ";

                        if (this.objects.get(obj).getActuallyVisible() == 0) {

                            htmlCode += " style=\"width:0px;\" ";
                        } else {
                            if (this.objects.get(obj).C.getWidth() != null && this.objects.get(obj).C.getWidth() != "null" && this.objects.get(obj).C.getWidth() != "") {
                                htmlCode += " style=\"width:" + this.objects.get(obj).C.getWidth() + ";\" ";
                            }
                        }

                        htmlCode += ">";

                        htmlCode += "<div id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + this.objects.get(obj).name + "-" + "NEW" + "-PLACE\"  ";
                        htmlCode += ">";
                        if (this.objects.get(obj).defaultValue == null) {

                        } else {
                            if (this.objects.get(obj).defaultValue.length() > 0) {
                                this.objects.get(obj).setAddingRow_enabled(0);
                            }
                        }
                        if (this.objects.get(obj).getAddingRow_enabled() > 0) {
                            //------------------------------------------------- 
                            htmlCode += paintObject("NEW", this.objects.get(obj));
                            //------------------------------------------------- 
                        }
                        htmlCode += "</div></td>";
                        htmlCode += "</td>";
                    }
                    //    htmlCode += "</div> ";     
                    htmlCode += " </tr>";

                }

                //</editor-fold> 
            } else if (rowType.equalsIgnoreCase("total")) {
// <editor-fold defaultstate="collapsed" desc="TOTALS ROW.">

                if (formRightsRules.canView > 0) {
                    String totLabel = "";
                    for (int obj = 0; obj < this.objects.size(); obj++) {
                        if (this.objects.get(obj).Content.getHasSum() > 0) {
                            totLabel = "TOT:";
                            break;
                        }
                    }
                    htmlCode += "<tr class=\"tabTotalsRow\" >";
                    if (this.getShowCounter() != null && this.getShowCounter().equalsIgnoreCase("FALSE")) {
                        htmlCode += "<td></td>";
                    } else {
                        htmlCode += "<td  class=\"lineSelector\" >" + totLabel + "</td>";

                    }

                    if (formRightsRules.canDelete > 0) {
                        htmlCode += "<td  class=\"lineDeleter\"  > </td>";
                    }

                    for (int obj = 0; obj < this.objects.size(); obj++) {

                        this.objects.get(obj).setValueToWrite("");

                        htmlCode += "<td  class=\"lineField\" ";
                        if (this.objects.get(obj).getActuallyVisible() == 0) {
                            htmlCode += " style=\"width:0px;\" ";
                        } else {
                            if (this.objects.get(obj).C.getWidth() != null && this.objects.get(obj).C.getWidth() != "null" && this.objects.get(obj).C.getWidth() != "") {
                                htmlCode += " style=\"width:" + this.objects.get(obj).C.getWidth() + ";\" ";
                            }
                        }
                        htmlCode += ">";
                        htmlCode += "<div id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + this.objects.get(obj).name + "-" + "TOT" + "-PLACE\"  ";
                        htmlCode += "class =\"totalContent ";
                        if (this.objects.get(obj).Content.getActualSum() < 0) {
                            htmlCode += " negativeNumber  ";
                        } else {
                            htmlCode += " positiveNumber  ";
                        }
                        htmlCode += "\" >";

//                        System.out.println("++oggetto " + this.objects.get(obj).getName() + " - HAS SUM =" + this.objects.get(obj).Content.getHasSum() + " -  SUM =" + this.objects.get(obj).Content.getActualSum());
                        if (this.objects.get(obj).Content.getHasSum() > 0) {
                            //------------------------------------------------- 
                            int no = this.objects.get(obj).Content.getActualSum();
                            Locale.setDefault(Locale.ITALY);
                            String str = String.format("%,d", no);
                            htmlCode += str;
                            //------------------------------------------------- 
                        } else {
                            htmlCode += "";
                        }
                        htmlCode += "</div></td>";
                        htmlCode += "</td>";
                    }
                    //  htmlCode += "</div> ";
                    htmlCode += " </tr>";

                }

                //</editor-fold> 
            } else {
// <editor-fold defaultstate="collapsed" desc="CASO NORMAL ROW.">  
                // System.out.println(" KEYfieldName :" + this.getKEYfieldName() + " KEYfieldType :" + this.getKEYfieldType());
                //  System.out.println("CASO NORMAL ROW" + htmlCode);

                String KEYvalue = "";
                if (this.getKEYfieldName() != null) {
                    if (this.getKEYfieldType() != null && this.getKEYfieldType().equalsIgnoreCase("INT")) {
                        int myKEYvalue;
                        myKEYvalue = rs.getInt(this.getKEYfieldName());
                        KEYvalue = "" + myKEYvalue;
                        //System.out.println("Prendo numerico: KEYfieldName :" + this.getKEYfieldName() + " KEYfieldType :" + this.getKEYfieldType());
                    } else {
                        KEYvalue = rs.getString(this.getKEYfieldName());
                        //System.out.println("Prendo stringa: KEYfieldName :" + this.getKEYfieldName() + " KEYfieldType :" + this.getKEYfieldType());
                    }
                } else {
                    KEYvalue = "" + lineNumber;
//                    System.out.println("-------------");
//                    System.out.println(this.query);
//                    System.out.println("paintRow:AUTO INDICIZZAZIONE ATTIVATA !!!");
//                    System.out.println("-------------");
                }
// Questi sono i diritti per la RIGA

// per ogni oggetto controllo: SE ESISTONO LOKERS vanno a prendere il sopravvento sui parametri di riga
//=======ROW===================    
                String ValueAssigned = getBGcolor(this.getRowBGcolor(), rs);
                //stabilisco il colore di background

                //----------------------------------------------------------------------
                htmlCode += "<tr id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-ROW\" class=\"unselectedRow\" >";
                htmlCode += paintRowSelector(lineNumber, KEYvalue);

//////                // row selector-------------
//////                String xLineNumber = "" + lineNumber;
//////                if (lineNumber == 0) {
//////                    xLineNumber = "NEW";
//////                }
//////                try {
//////                    if (this.getVisualType() != null && this.getVisualType().equalsIgnoreCase("singleRow")) {
//////                        xLineNumber = "UPD";
//////                    }
//////                } catch (Exception e) {
//////                }
//////                htmlCode += "<td class=\"lineSelector\""
//////                        //+ " style=\"padding: 0;\""
//////                        + "onClick=\"javascript:rowSelected('" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-SEL')\">"
//////                        + "<a id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-SEL\" "
//////                        + "style=\""
//////                        + " height: inherit;"
//////                        + " padding: 1em;"
//////                        + "\"><font size='1'><i><b>" + xLineNumber + "</b></i></font> "
//////                        + "</a></td>";
//////                
//////                
                // delete button-------------
                if (formRightsRules.canDelete > 0) {
                    htmlCode += "<td class=\"lineDeleter\"   >"
                            + "<a id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + KEYvalue + "-DEL\"   ";
                    if (actualRowRights.canDelete > 0) {
                        String jsonArgs = "{";
                        jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                        jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                        jsonArgs += "\"objName\":\"\",";
                        jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                        jsonArgs += "\"operation\":\"DEL\",";
                        jsonArgs += "\"cellType\":\"X\",";
                        jsonArgs += "\"filterField\":\"\",";
                        jsonArgs += "\"exitRoutine\":\"dummy()\"}";
                        htmlCode += " onClick='javascript:cellChanged(" + jsonArgs + ")' >";
                        htmlCode += " <img  height=\"15\" width=\"15\" align=\"middle\" src='./media/icons/IconDELETE.gif' alt='ELIMINA' ";

                        htmlCode += " ></img>";
                        htmlCode += "</a> ";
                    }

                    htmlCode += "</td>";
                }

                //   System.out.println("\n--PaintRow_elaboraRigaRS per riga n." + lineNumber);
                elaboraRigaRS(rs, actualRowRights);
                // objects-------------
                for (int obj = 0; obj < this.objects.size(); obj++) {
                    // System.out.println("OGGETTO  " + this.objects.get(obj).getName() + ": " + this.objects.get(obj).C.getType());
                    //  System.out.println("OGGETTO n. "+obj+" ThisRowModifiable="+this.objects.get(obj).Content.getThisRowModifiable());
//---TRIGGERED STYLE--(paintRow)-------------------                    
                    String triggeredStyle = feedTriggeredStyle(this.objects.get(obj), rs);
                    if (triggeredStyle != null && triggeredStyle.length() > 2) {
                        //  System.out.println("Imposto lo stile da trigger come default: " + triggeredStyle);
                        this.objects.get(obj).setTriggeredStyle(triggeredStyle);
                    } else {
                        this.objects.get(obj).setTriggeredStyle("");
                    }
//----------------------                    
//---CREO IL TD PER L'OGGETTO------------------- 
                    htmlCode += "<td  class=\"lineField\" ";
                    if (this.objects.get(obj).getActuallyVisible() < 1) {
                        htmlCode += " style=\"width:0px;\" ";

                    } else {
                        if (this.objects.get(obj).C.getWidth() != null && this.objects.get(obj).C.getWidth() != "null" && this.objects.get(obj).C.getWidth() != "") {
                            htmlCode += " style=\"width:" + this.objects.get(obj).C.getWidth() + ";\" ";
                        }
                    }

                    htmlCode += ">";
//----------------------                    
//---INSERISCO L'OGGETTO-------------------                    
                    htmlCode += "<div id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + this.objects.get(obj).name + "-" + KEYvalue + "-PLACE\"  ";

                    htmlCode += ">";
                    if (this.objects.get(obj).C.getType().equalsIgnoreCase("REALTIMESELECT")) {
//                        System.out.println("RICREO SELECT LIST");
                        String oQuery = this.objects.get(obj).Origin.getQuery();
//                        System.out.println(" this.sendToCRUD:" + this.sendToCRUD);
                        oQuery = browserArgsReplace(oQuery);
//                        System.out.println("DOPO:" + oQuery);
                        String oLabelField = this.objects.get(obj).Origin.getLabelField();
                        String oValueField = this.objects.get(obj).Origin.getValueField();
                        String oValueFieldType = this.objects.get(obj).Origin.getValueFieldType();
                        SelectList myList = new SelectList(myParams, mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                        myList.getList();
                        this.objects.get(obj).Origin.setSelectList(myList);
                        objRight realObjRights = valutaRightsOggetto(this.objects.get(obj), rs);
                        objRight actualObjectRights = joinRights(realObjRights, actualRowRights);
                        htmlCode += paintObject(KEYvalue, this.objects.get(obj), actualObjectRights);

                    } else if (this.objects.get(obj).C.getType().equalsIgnoreCase("CUSTOMBOX")) {

                        String progressCode = "";
                        progressCode += "<TABLE>";
                        // cerco il valore nella funzione iidicata
                        // System.out.println("\n\nCUSTOMBOX: funzione =" + this.objects.get(obj).CG.Value);
                        // es {"label":{"type":"field","field":"abbonamento"},"value":{"type":"field","field":"percAbb"}
                        JSONParser jsonParser = new JSONParser();

                        try {

                            JSONObject jBars;
                            JSONObject riga = null;

                            jBars = (JSONObject) jsonParser.parse(this.objects.get(obj).CG.Value);
                            JSONArray array = (JSONArray) jsonParser.parse(jBars.get("bars").toString());

                            for (int r = 0; r < array.size(); r++) {

                                riga = (JSONObject) array.get(r);
                                JSONObject jLabel = (JSONObject) jsonParser.parse(riga.get("label").toString());
                                String LabelField = "";
                                String ValueField = "";
                                String ValueLabel = "";
                                String ValueLabelUM = "";
                                String TextBefore = "";
                                String TextAfter = "";
                                // per adesso suppongo sia un caso "type":"field"
                                try {
                                    LabelField = jLabel.get("field").toString();// NOME DELLA TESSERA
                                } catch (Exception e) {
                                }
                                JSONObject jValue = (JSONObject) jsonParser.parse(riga.get("value").toString());
                                // per adesso suppongo sia un caso "type":"field"
                                try {
                                    ValueField = jValue.get("field").toString();// PERCENTUALE RIMANENTE
                                } catch (Exception e) {
                                }
                                JSONObject jValueLabel = (JSONObject) jsonParser.parse(riga.get("valueText").toString());
                                // per adesso suppongo sia un caso "type":"field"
                                try {
                                    ValueLabel = jValueLabel.get("field").toString();// VALORE RIMANENTE (IN GIORNI O ORE)
                                } catch (Exception e) {
                                }
                                JSONObject jValueLabelUM = (JSONObject) jsonParser.parse(riga.get("valueTextUM").toString());
                                // per adesso suppongo sia un caso "type":"field"
                                try {
                                    ValueLabelUM = jValueLabelUM.get("value").toString();//ETICHETTA IN CODA (es. giorni)
                                } catch (Exception e) {
                                }
                                JSONObject jTextBefore = (JSONObject) jsonParser.parse(riga.get("textBefore").toString());
                                // per adesso suppongo sia un caso "type":"field"
                                try {
                                    TextBefore = jTextBefore.get("value").toString();//ETICHETTA IN CODA (es. giorni)
                                } catch (Exception e) {
                                }
                                JSONObject jTextAfter = (JSONObject) jsonParser.parse(riga.get("textAfter").toString());
                                // per adesso suppongo sia un caso "type":"field"
                                try {
                                    TextAfter = jTextAfter.get("value").toString();//ETICHETTA IN CODA (es. giorni)
                                } catch (Exception e) {
                                }

                                //   System.out.println("\n\nCUSTOMBOX: LabelField =" + LabelField);
                                //  System.out.println("CUSTOMBOX: ValueField =" + ValueField);
                                String myLabel = rs.getString(LabelField);
                                int myPercentage = rs.getInt(ValueField);
                                int myValue = rs.getInt(ValueLabel);
                                String myTextValue = rs.getString(ValueLabel);
                                // System.out.println(myLabel + "=" + myValue);
                                if (myLabel != null && myLabel.length() > 0) {
                                    progressCode += "<TR style=\"border-bottom: 1px solid lightGrey !important;\"><TD>";
                                    progressCode += myLabel;
                                    progressCode += "</TD>";
                                    progressCode += "<TD ";
                                    if (myPercentage < 30) {
                                        progressCode += "style=\"color:red;\"";
                                    } else {
                                        progressCode += "style=\"color:green;\"";
                                    }
                                    progressCode += ">";
                                    progressCode += TextBefore + myTextValue + " " + ValueLabelUM + TextAfter;
                                    progressCode += "</TD></TR>";
                                }
                            }

                            progressCode += "</TABLE>";

                        } catch (ParseException ex) {
                            System.out.println("error in line 3884");
                            Logger
                                    .getLogger(ShowItForm.class
                                            .getName()).log(Level.SEVERE, null, ex);
                        }
                        htmlCode += progressCode;
                    } else {
                        //      System.out.println("\nfield " + this.objects.get(obj).getName() + " RIGHTS:  " + this.objects.get(obj).Content.getModifiable());
////////                        objRight ObjRights = this.objects.get(obj).objRights;
////////                        if (this.objects.get(obj).Content.getModifiable() != null && this.objects.get(obj).Content.getModifiable().startsWith("[{")) {
////////                            ObjRights = analyzeRightsRuleJson(this.objects.get(obj).Content.getModifiable(), rs, null);
////////                        } else {
////////                            ObjRights = this.objects.get(obj).objRights;
////////                        }
////////                        objRight realRights = joinRights(this.objects.get(obj).objRights, ObjRights);
////                        objRight realRights = valutaRightsOggetto(this.objects.get(obj), rs);
////                        objRight actualObjectRights = joinRights(realRights, actualRowRights);
//------------------------------------------------- 
                        objRight realObjRights = valutaRightsOggetto(this.objects.get(obj), rs);
                        objRight actualObjectRights = joinRights(realObjRights, actualRowRights);
////////                        System.out.println("paintRow:\nUNISCO DIRITTI RIGA:" + actualRowRights.totalRight + " LEVEL :" + actualRowRights.level);
////////                        System.out.println("CON DIRITTI OGGETTO:" + realObjRights.totalRight + " LEVEL :" + realObjRights.level);
////////                        System.out.println("DIRITTO RISULTANTE:" + actualObjectRights.totalRight + " LEVEL: " + actualObjectRights.level);

//------------------------------------------------- 
//                        if (this.objects.get(obj).CG.Type.equalsIgnoreCase("ROWPICTURE")) {
//                            htmlCode += "*RP*";
//                        } else {
                        htmlCode += paintObject(KEYvalue, this.objects.get(obj), actualObjectRights);
//                        }

//------------------------------------------------- 
                    }
                    htmlCode += "</div>";
//----------------------                    
//---CHIUDO IL TD-------------------                    
                    htmlCode += "</td>";

                    //System.out.println("Fine oggetto:" + obj);
                }
                // </editor-fold>    
//====END=ROW===================        
                htmlCode += "</tr>";
//</editor-fold> 

            }

        } catch (SQLException ex) {
            System.out.println("SQLException in PAINTROW:");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return htmlCode;
    }

    public void elaboraRigaRS(ResultSet rs, objRight actualRowRights) {
//        System.out.println("-----------------------------");
        rowValues = new ArrayList<boundFields>();
        for (int obj = 0; obj < this.objects.size(); obj++) {
            this.objects.get(obj).setValueToWrite(ricavoValoreDaScrivere(rs, obj));
            boundFields myBF = new boundFields();
//            System.out.println("Name():" + this.objects.get(obj).getName() + " Value():" + this.objects.get(obj).getValueToWrite());
            myBF.setMarker(this.objects.get(obj).getName());
            myBF.setValue(this.objects.get(obj).getValueToWrite());
            rowValues.add(myBF);
            this.objects.get(obj).Content.setThisRowModifiable(actualRowRights.canModify);//ogni oggetto riceve il valore di default della riga
        }
    }

    public String ricavoValoreDaScrivere(ResultSet rs, int obj) {
        String ValoreDaScrivere = "";

        // ValoreDaScrivere = this.objects.get(obj).getLabelHeader();
        try {
            String fieldName = this.objects.get(obj).name;
            String Type = this.objects.get(obj).Content.getType();
            String CGtype = this.objects.get(obj).CG.getType();
            String containerType = this.objects.get(obj).C.getType();
            ValoreDaScrivere = this.objects.get(obj).getLabelHeader();

            if (this.objects.get(obj).C.getType().equalsIgnoreCase("PICTURE")) {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = null;
                String picTable = "";
                String picTableQuery = "";
                String picTableKeyField = "";
                String formQueryKeyField = "";
                String formQueryKeyFieldType = "";
                String ric = this.objects.get(obj).Origin.getLabelField();
//                System.out.println("ric: " + ric);
                if (ric != null && ric.length() > 0 && ric.startsWith("{")) {
                    try {
                        jsonObject = (JSONObject) jsonParser.parse(ric);
                        try {
                            picTableKeyField = jsonObject.get("picTableKeyField").toString();
                        } catch (Exception e) {
                        }
                        try {
                            picTable = jsonObject.get("picTable").toString();
                        } catch (Exception e) {

                        }
                        try {
                            picTableQuery = jsonObject.get("picTableQuery").toString();
                        } catch (Exception e) {

                        }
                        try {
                            formQueryKeyField = jsonObject.get("formQueryKeyField").toString();
                        } catch (Exception e) {
                        }
                        try {
                            formQueryKeyFieldType = jsonObject.get("formQueryKeyFieldType").toString();
                        } catch (Exception e) {
                        }

////////                        System.out.println("ricavoValoreDaScrivere--->PICTURE ");
////////
////////                        System.out.println("picTable: " + picTable);
////////                        System.out.println("picTableQuery: " + picTableQuery);
////////
////////                        System.out.println("usedKeyField: " + picTableKeyField);
////////                        System.out.println("formQueryKeyField: " + formQueryKeyField);
////////                        System.out.println("usedKeyType: " + formQueryKeyFieldType);
////////                        System.out.println("tabella con immagine: " + picTable);
                        // cerco in rs il valore da usare come chiave;
//                        System.out.println("\nLEGGO SULLA RIGA La KEY da usare nella tabella secondaria: ");
                        try {
                            if (formQueryKeyFieldType != null
                                    && formQueryKeyFieldType.equalsIgnoreCase("INT")) {
                                ValoreDaScrivere = "" + rs.getInt(formQueryKeyField);
                            } else {
                                ValoreDaScrivere = rs.getString(formQueryKeyField);

                            }

//                            System.out.println("ValoreDaScrivere: " + ValoreDaScrivere);
                        } catch (Exception e) {
                            System.out.println("Errore in indicizzazione picture: " + e.toString());
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(requestsManager.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else if (this.objects.get(obj).CG.getType().equalsIgnoreCase("FIELD")) {
                if (this.objects.get(obj).C.Type.equalsIgnoreCase("ROWPICTURE")) {

                    objectLayout myBox = new objectLayout();
                    myBox.loadBoxLayout(this.objects.get(obj).C.getJsClass(), "20", "20");

                    Blob blob = null;
                    BufferedImage image = null;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    if (rs != null) {
                        try {
                            blob = rs.getBlob(this.objects.get(obj).getName());

                            InputStream in = null;
                            if (blob != null) {
                                try {
                                    in = blob.getBinaryStream();
                                    image = ImageIO.read(in);
                                } catch (IOException ex) {
                                    Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        } catch (SQLException ex) {
                            // Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                    if (image != null) {
                        ValoreDaScrivere = getRowImageHtmlCode(image, this.objects.get(obj).labelHeader, myBox);
                    } else {
                        ValoreDaScrivere = "";
                    }
                } else {

                    if (this.objects.get(obj).Content.getType() != null
                            && this.objects.get(obj).Content.getType().equalsIgnoreCase("INT")) {
                        int number = 0;
                        try {
                            number = rs.getInt(this.objects.get(obj).name);
                        } catch (Exception ee) {
                        }
                        ValoreDaScrivere = "" + number;
                    } else {
                        String text = "";
                        try {
                            text = rs.getString(this.objects.get(obj).name);
                        } catch (Exception ex) {
                            text = "";
                            // se non trovo il field nel DB, provo con uno dei
                            // parametri mandati con ToBeSent
                            if (this.sentFieldList != null && this.sentFieldList.size() > 0) {
                                for (int jj = 0; jj < this.sentFieldList.size(); jj++) {
                                    if (this.sentFieldList.get(jj).getMarker().equalsIgnoreCase(this.objects.get(obj).name)) {
                                        text = this.sentFieldList.get(jj).getValue();
                                    }
                                }
                            }

                        }
                        if (text == null || text.equalsIgnoreCase("null")) {
                            text = "";
                        }
                        ValoreDaScrivere = "" + text;
                    }
                    //    System.out.println("ValoreDaScrivere:" + ValoreDaScrivere);
                }
            } else if (this.objects.get(obj).CG.getType().equalsIgnoreCase("SENTFIELD")) {
                String text = "";
                for (int jj = 0; jj < this.sentFieldList.size(); jj++) {
                    if (this.sentFieldList.get(jj).getMarker().equalsIgnoreCase(this.objects.get(obj).name)) {
                        text = this.sentFieldList.get(jj).getValue();
                    }
                }
                ValoreDaScrivere = "" + text;

            } else if (this.objects.get(obj).CG.getType().equalsIgnoreCase("CONDITIONAL")) {
                String CGparams = "";
                CGparams = this.objects.get(obj).CG.getParams();

//                System.out.println("CONDITIONAL:" + CGparams);
                String ValueAssigned = "";

                String[] blocks = CGparams.split(";");
                List<String> block = Arrays.asList(blocks);
                if (block.size() > 1) {
                    for (int bb = 0; bb < block.size(); bb++) {
                        String valore = "";
                        String condizione = "";
                        String[] couples = block.get(bb).split(" FORMAT ");
                        List<String> param = Arrays.asList(couples);
                        if (param.size() > 1) {
                            // condizione = valore
                            condizione = param.get(0);
                            valore = param.get(1);
                            // analizzo la condizione
                            String[] terms = condizione.split("=");
                            List<String> term = Arrays.asList(terms);
                            String part1 = term.get(0);
                            String part2 = term.get(1);

                            String field = part1.replace("[", "");
                            field = field.replace("]", "");
                            String rawType = "text";
                            int rawValue = 0;
                            String rawString = "";
                            // System.out.println(part1 + "=" + part2 + " --->" + valore);

                            for (int gg = 0; gg < this.objects.size(); gg++) {
                                if (this.objects.get(gg).getName().equalsIgnoreCase(field)) {
                                    rawType = this.objects.get(gg).Content.getType();
                                    if (rawType != null && rawType.equalsIgnoreCase("INT")) {
                                        int number = rs.getInt(field);
                                        rawString = "" + number;
                                        // sto analizzando il valore del campo che ha come nome [part1]
                                        // rilevato valore numerico... quindi lo confronterò con
                                        // part 2 trasformato in numero
                                        int number2 = Integer.parseInt(part2);
                                        // eseguo il confronto caso NUMERO
                                        if (number == number2) {
                                            ValueAssigned = valore;
                                        }

                                    } else {
                                        String text = "";
                                        try {
                                            text = rs.getString(field);
                                        } catch (Exception ex) {
                                            text = "";
                                        }
                                        if (text == null || text.equalsIgnoreCase("null")) {
                                            text = "";
                                        }
                                        rawString = "" + text;

                                        // eseguo il confronto caso TEXT
                                        if (rawString.equalsIgnoreCase(part2)) {
                                            ValueAssigned = valore;
                                        }

                                    }

                                }
                            }

                        } else {
                            // solo valore
                            valore = block.get(bb);
                            //  System.out.println( " X --->" + valore);
                            ValueAssigned = valore;

                        }

                    }
                }

                try {
                    JSONParser jsonParser = new JSONParser();

                    JSONObject jsonObject = (JSONObject) jsonParser.parse(ValueAssigned);

                    try {
                        this.objects.get(obj).C.setConditionalLabel((String) jsonObject.get("text"));
                    } catch (Exception ex) {
                        this.objects.get(obj).C.setConditionalLabel(null);
                    }

                    try {
                        this.objects.get(obj).C.setConditionalBackColor((String) jsonObject.get("back"));
                    } catch (Exception ex) {
                        this.objects.get(obj).C.setConditionalBackColor(null);
                    }

                } catch (ParseException pe) {
                    System.out.println("error in line 4056");
                }

            }
        } catch (SQLException ex) {
            System.out.println("SQLException in PAINTROW:");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return ValoreDaScrivere;
    }

    public String paintObject(String KEYvalue, ShowItObject curObj) {
        String htmlCode = paintObject(KEYvalue, curObj, new ShowItObject().createVoidRights());

        return htmlCode;
    }

    public String paintObject(String KEYvalue, ShowItObject curObj, objRight objRights) {
        objRights.evaluateRights();
        /*
        
        In linea di proncipio:
        l'oggetto è modificabile se:
        1. il form è modificabile
        2. la riga è modificabile
        3. l'oggetto è modificabile
        
        le prime due le racchiudo in rowRights che sono già stati analizzati per la riga
        devo fondere rowRights con i diritti dell'oggetto specifico
        sia in generale (obj.mpodifiable) , sia in base alla riga (triggers)
         */

//        System.out.println("\n================\npaintObject objRights:" + objRights.totalRight+" - level: "+objRights.level);
        boolean objModifiable = true;
        boolean objVisibile = true;
        boolean objCanPushButton = true;
        String objType = curObj.C.getType();

        if (objRights.canModify <= 0) {
            objModifiable = false;
        } else {
            objModifiable = true;
        }
        if (objRights.canPushButton != 0) {
            objCanPushButton = true;
        } else {
            objCanPushButton = false;
        }
        if (objRights.canView <= 0) {
            objVisibile = false;
        } else {
            objVisibile = true;
        }

//        System.out.println("paintObject objModifiable:" + objModifiable);
        String htmlCode = "";
        String ValoreDaScrivere = curObj.getValueToWrite();
//System.out.println("\n >>paintObject: ValoreDaScrivere:" +ValoreDaScrivere);
        if (KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")) {
            //  System.out.println("OGGETTO IN RIGA NEW ");
            if (curObj.AddingRow_enabled < 1) {
                // System.out.println("NON PRESENTE IN ADDING ROW");
                curObj.Content.setThisRowModifiable(0);
                objVisibile = false;
                objModifiable = false;

            } else {
                // System.out.println("PRESENTE IN ADDING ROW");
                objVisibile = true;
                objModifiable = true;
            }
        }
        if (objType == null) {
            objType = "TEXT";
        }
        // gestisco il fatto che un campo che sarà solo label possa essere in principio compilabile nella newLine
        if (objType != null && objType.equalsIgnoreCase("LABEL") && KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")) {
            objType = "TEXT";
        }
//        System.out.println("_paintObject_objVisibile: " + objVisibile);
//        System.out.println("_paintObject_objModifiable:" + objModifiable);
//        System.out.println("_paintObject_objType:" + objType);

        // se non è una newLine e non hai i diritti di modifica... LABEL!
        if (KEYvalue == null) {
            KEYvalue = "";
        }
        if (!KEYvalue.equalsIgnoreCase("NEW") && !KEYvalue.equalsIgnoreCase("MULTILINELABEL")
                && (objModifiable == false)
                && (objType.equalsIgnoreCase("TEXT")
                //|| objType.equalsIgnoreCase("TEXTAREA")
                || objType.equalsIgnoreCase("PASSWORD") //------------------
                ////////                || (objType != null && 
                ////////                (objType.equalsIgnoreCase("dateTime")
                ////////                || objType.equalsIgnoreCase("date")
                ////////                || objType.equalsIgnoreCase("time")))
                //------------------------------
                ////////                || (curObj.Content.getType() != null
                ////////                && (curObj.Content.getType().equalsIgnoreCase("dateTime")
                ////////                || curObj.Content.getType().equalsIgnoreCase("date")
                ////////                || curObj.Content.getType().equalsIgnoreCase("time")))
                )) {

            // System.out.println("_paintObject_TRASFORMO TEXT IN LABEL");
            objType = "LABEL";
        } else if (!KEYvalue.equalsIgnoreCase("NEW") && KEYvalue.equalsIgnoreCase("TEXTAREA")) {
            objType = "MULTILINELABEL";
        }

// <editor-fold defaultstate="collapsed" desc="LABEL">          
        if (objType.equalsIgnoreCase("LABEL")) {
//==LABEL=========================================================
            // System.out.println("--CASO LABEL ValoreDaScrivere:" + ValoreDaScrivere);
            if (objVisibile == true) {
                // System.out.println("--curObj.Content.getType():" + curObj.Content.getType());

                htmlCode += "<div title=\"" + ValoreDaScrivere + "\" style=\"width : " + curObj.C.getWidth() + ";\n"
                        + " display:inline-block;"
                        + " text-overflow: ellipsis;"
                        + " overflow: hidden;"
                        + " white-space: nowrap;";
                //  + "word-wrap: break-word;"

                //   System.out.println("\n\n\n\n--curObj.C.getDefaultStyle():" + curObj.ges_triggers);
                if (curObj.getTriggeredStyle() != null && curObj.getTriggeredStyle().length() > 2) {
                    htmlCode += curObj.getTriggeredStyle();
                } else if (curObj.C.getDefaultStyle() != null) {
                    //     System.out.println(" TROVATO DFAULT STYLE:" + curObj.C.getDefaultStyle());
                    htmlCode += curObj.C.getDefaultStyle();
                }

                htmlCode += "\" ";
                htmlCode += " class= \" "
                        + " cellContent ";
                if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("INT")) {
                    htmlCode += " contentNumber  ";
                    if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0
                            && ValoreDaScrivere.substring(0, 1).equals("-")) {
                        htmlCode += " negativeNumber  ";
                    }
                }
                if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("FLOAT")) {
                    htmlCode += " contentNumber  ";
                    if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                        // tronco a 3 cifre dopo il punto
                        int posX = ValoreDaScrivere.lastIndexOf(".");
                        posX = posX + 2;
                        if (ValoreDaScrivere.length() > posX) {
                            ValoreDaScrivere = ValoreDaScrivere.substring(0, posX);
                        }

                    }
                }

                htmlCode += "\" ";
                htmlCode += getStyleHtmlCode(curObj, KEYvalue);
                if (this.getShowCounter() != null && this.getShowCounter().equalsIgnoreCase("FALSE")) {
                    htmlCode += " onmouseup=\"javascript:objSelected('" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "')\"";
                }
                htmlCode += " > ";
                htmlCode += ValoreDaScrivere;
                htmlCode += "</div>";

            }

            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";

        } else // </editor-fold>     
        //MULTILINELABEL
        // <editor-fold defaultstate="collapsed" desc="MULTILINELABEL">          
        if (objType.equalsIgnoreCase("MULTILINELABEL")) {
//==LABEL=========================================================
            //System.out.println("\n------------->MULTILINELABEL: " + ValoreDaScrivere);
            // System.out.println("--CASO LABEL ValoreDaScrivere:" + ValoreDaScrivere);
            if (objVisibile == true) {
                // System.out.println("--curObj.Content.getType():" + curObj.Content.getType());

                htmlCode += "<div title=\"" + ValoreDaScrivere + "\" "
                        + "style=\"width : " + curObj.C.getWidth() + "; "
                        //+ " display:inline-block;"
                        //+ " text-overflow: ellipsis;"
                        //+ " overflow: auto;"
                        //+ " white-space: nowrap;"
                        + "";
                //  + "word-wrap: break-word;"

                if (curObj.getTriggeredStyle() != null && curObj.getTriggeredStyle().length() > 2) {
                    htmlCode += curObj.getTriggeredStyle();
                } else if (curObj.C.getDefaultStyle() != null) {
                    System.out.println(" TROVATO DFAULT STYLE:" + curObj.C.getDefaultStyle());
                    htmlCode += curObj.C.getDefaultStyle();
                }

                htmlCode += "\" ";
                htmlCode += " class= \" "
                        + " cellContent ";
                if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("INT")) {
                    htmlCode += " contentNumber  ";
                    if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0
                            && ValoreDaScrivere.substring(0, 1).equals("-")) {
                        htmlCode += " negativeNumber  ";
                    }
                }
                if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("FLOAT")) {
                    htmlCode += " contentNumber  ";
                    if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                        // tronco a 3 cifre dopo il punto
                        int posX = ValoreDaScrivere.lastIndexOf(".");
                        posX = posX + 2;
                        if (ValoreDaScrivere.length() > posX) {
                            ValoreDaScrivere = ValoreDaScrivere.substring(0, posX);
                        }

                    }
                }

                htmlCode += "\" ";
                htmlCode += getStyleHtmlCode(curObj, KEYvalue);
                htmlCode += " > ";
                htmlCode += ValoreDaScrivere;
                htmlCode += "</div>";

            }

            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";

        } else // </editor-fold>         
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="SensibleLABEL">             
        if (objType.equalsIgnoreCase("SensibleLABEL")) {
//==SensibleLABEL=========================================================

            if (curObj.C.getConditionalLabel() != null) {
                ValoreDaScrivere = curObj.C.getConditionalLabel();
            }
            // System.out.println("CASO SensibleLABEL ValoreDaScrivere:" + ValoreDaScrivere + " - curObj.Content.getThisRowModifiable():" + curObj.Content.getThisRowModifiable());
            if (ValoreDaScrivere != null && ValoreDaScrivere.contains("##")) {
                ValoreDaScrivere = browserArgsReplace(ValoreDaScrivere);
            }
            if (objModifiable == true || objCanPushButton == true) {
                htmlCode += "<a class=\"SensibleLABEL "
                        //    + "cellContent"
                        + "\" id=\"" + this.getID() + "-" + curObj.name + "\" ";
                htmlCode += " style= \""
                        /* + "display:block;"
                          + "text-align:center;"
                         + "vertical-align:middle;"
                         + "height:auto;\n"
                         + "padding:3px 0;"*/
                        + " width:" + curObj.C.getWidth() + "; ";

                String defaultStyle = "";
                String retreivedStyle = "";
                //2.0 default style
                if (curObj.C.getDefaultStyle() != null && curObj.C.getDefaultStyle().length() > 0) {
                    defaultStyle = curObj.C.getDefaultStyle();
                }

                //----STYLE DA TRIGGER-------------------
                //  htmlCode += " style= \"";
                if (curObj.getTriggeredStyle() != null && curObj.getTriggeredStyle().length() > 2) {
                    retreivedStyle += curObj.getTriggeredStyle();
                } else if (curObj.C.getDefaultStyle() != null) {
                    //     System.out.println(" TROVATO DFAULT STYLE:" + curObj.C.getDefaultStyle());
                    retreivedStyle += defaultStyle;
                }
                if (retreivedStyle.contains("display:")) {

                } else {
                    retreivedStyle += " display:block; ";
                }
                if (retreivedStyle.contains("background-color:")) {

                } else {

                    EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
                    String defaultBackground = myManager.getDirective("ButtonDefaultBgColor");
                    String defaultColor = "lightGreen";
                    if (defaultBackground != null && defaultBackground.length() > 0) {
                        defaultColor = defaultBackground;
                    }
                    retreivedStyle += " background-color:" + defaultColor + "; ";
                }

                htmlCode += retreivedStyle;
                htmlCode += "\"";
                //-----------------------
                /*  String params = curObj.CG.getParams() + ":" + curObj.CG.getValue();
                 htmlCode += " onclick=\"javascript:clickedLabel('" +  this.getID()+ "-"+this.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "','" + params + "')\"";
                 */
                String params = curObj.getActionParams();
                //  System.out.println("\n\nSensLab " + curObj.name + " getActionParams:" + params);

                if (params == null) {
                    params = "{}";
                }

                if (curObj.getActionPerformed() != null
                        && curObj.getActionPerformed().equalsIgnoreCase("OpenSecForm")) {
                    // se devo aprire un form, devo ricavare la getGes_routineOnLoad di quel form
                }
                //  System.out.println("DISEGNO SensibleLabel:" + curObj.getName() + " " + curObj.getActionPerformed() + " " + curObj.getActionParams());
                String toAdd = "";

                toAdd = ",\"action\":\"" + curObj.getActionPerformed() + "\""
                        + ",\"rifForm\":\"" + this.getID() + "\""
                        + ",\"copyTag\":\"" + this.getCopyTag() + "\""
                        + ",\"fatherForm\":\"" + this.getFather() + "\""
                        + ",\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\""
                        + ",\"rifObj\":\"" + curObj.name + "\""
                        + ",\"keyValue\":\"" + KEYvalue + "\"}";
                params = params.replace("}", toAdd);
                htmlCode += " onclick='javascript:clickedObject( " + params + " )'";
                //params.replace("\"","'")

                htmlCode += " ";
                htmlCode += "> ";

                // cerco una immagine in gFEobjects, campo 'picture' dove ID = id di questo oggetto
                objectLayout myBox = new objectLayout();
                myBox.loadBoxLayout(curObj.C.getJsClass());
                if (myBox.getWidth() == null) {
                    myBox.setWidth("20px");
                }
                if (myBox.getHeight() == null) {
                    myBox.setHeight("20px");
                }
                if (myBox.getType() == null) {
                    myBox.setType("");
                }

                if (myBox.getType().equalsIgnoreCase("picOnly")) {

                    DBimage dbimage = new DBimage(mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myParams);
                    String imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());

                    String SLcode = "";
                    SLcode += "<table style=\" "
                            + "width:" + myBox.getWidth() + ";height:" + myBox.getHeight() + ";  "
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;"
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;"
                            + "\">" + imageCode + "</td>";
                    SLcode += "</tr></table>";
                    htmlCode += SLcode;
                } else if (myBox.getType().equalsIgnoreCase("STANDARD")) {

                    DBimage dbimage = new DBimage(mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myParams);
                    String imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());

                    String SLcode = "";
                    SLcode += "<table style=\" margin-left:auto; \n"
                            + "margin-right:auto; "
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\">" + imageCode + "</td>";
                    SLcode += "</tr><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\" ";
                    SLcode += getStyleHtmlCode(curObj, KEYvalue);// determina il bgColor
                    SLcode += ">" + ValoreDaScrivere + "</td>";
                    SLcode += "</tr></table>";
                    htmlCode += SLcode; 
                } else {
                    htmlCode += ValoreDaScrivere;
                }
                htmlCode += "</a>";
            }

            htmlCode += "<INPUT  class=\"cellContent\"  type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";
        } else // </editor-fold>     
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="smartButton">             
        if (objType.equalsIgnoreCase("smartButton")) {
//==smartButton=========================================================
//eseguiti test per chiamata websocket con pressione pulsante
            if (curObj.C.getConditionalLabel() != null) {
                ValoreDaScrivere = curObj.C.getConditionalLabel();
            }
            // System.out.println("CASO SensibleLABEL ValoreDaScrivere:" + ValoreDaScrivere + " - curObj.Content.getThisRowModifiable():" + curObj.Content.getThisRowModifiable());

            if (objModifiable == true || objCanPushButton == true) {
                //System.out.println("CASO SensibleLABEL ValoreDaScrivere:" + ValoreDaScrivere);
                htmlCode += "<a class=\"SensibleLABEL "
                        //    + "cellContent"
                        + "\" id=\"" + this.getID() + "-" + curObj.name + "\" ";
                htmlCode += " style= \""
                        /* + "display:block;"
                          + "text-align:center;"
                         + "vertical-align:middle;"
                         + "height:auto;\n"
                         + "padding:3px 0;"*/
                        + " width:" + curObj.C.getWidth() + "; ";

                String defaultStyle = "";
                String retreivedStyle = "";
                //2.0 default style
                if (curObj.C.getDefaultStyle() != null && curObj.C.getDefaultStyle().length() > 0) {
                    defaultStyle = curObj.C.getDefaultStyle();
                }

                //----STYLE DA TRIGGER-------------------
                //  htmlCode += " style= \"";
                if (curObj.getTriggeredStyle() != null && curObj.getTriggeredStyle().length() > 2) {
                    retreivedStyle += curObj.getTriggeredStyle();
                } else if (curObj.C.getDefaultStyle() != null) {
                    System.out.println(" TROVATO DFAULT STYLE:" + curObj.C.getDefaultStyle());
                    retreivedStyle += defaultStyle;
                }
                if (retreivedStyle.contains("display:")) {

                } else {
                    retreivedStyle += " display:block; ";
                }
                if (retreivedStyle.contains("background-color:")) {

                } else {
                    retreivedStyle += " background-color:lightGreen; ";
                }

                htmlCode += retreivedStyle;
                htmlCode += "\"";
                //-----------------------
                /*  String params = curObj.CG.getParams() + ":" + curObj.CG.getValue();
                 htmlCode += " onclick=\"javascript:clickedLabel('" +  this.getID()+ "-"+this.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "','" + params + "')\"";
                 */
                String params = curObj.getActionParams();
                //  System.out.println("\n\nSensLab " + curObj.name + " getActionParams:" + params);

                if (params == null) {
                    params = "{}";
                }

                if (curObj.getActionPerformed() != null
                        && curObj.getActionPerformed().equalsIgnoreCase("OpenSecForm")) {
                    // se devo aprire un form, devo ricavare la getGes_routineOnLoad di quel form
                }
                //  System.out.println("DISEGNO SensibleLabel:" + curObj.getName() + " " + curObj.getActionPerformed() + " " + curObj.getActionParams());

                String toAdd = ",\"action\":\"" + curObj.getActionPerformed() + "\""
                        + ",\"rifForm\":\"" + this.getID() + "\""
                        + ",\"copyTag\":\"" + this.getCopyTag() + "\""
                        + ",\"fatherForm\":\"" + this.getFather() + "\""
                        + ",\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\""
                        + ",\"rifObj\":\"" + curObj.name + "\""
                        + ",\"keyValue\":\"" + KEYvalue + "\"}";
                params = params.replace("}", toAdd);
                htmlCode += " onclick='javascript:WSclick( " + params + " )'";
                //params.replace("\"","'")

                htmlCode += " ";
                htmlCode += "> ";

                // cerco una immagine in gFEobjects, campo 'picture' dove ID = id di questo oggetto
                objectLayout myBox = new objectLayout();

                myBox.loadBoxLayout(curObj.C.getJsClass());
                if (myBox.getWidth() == null) {
                    myBox.setWidth("20px");
                }
                if (myBox.getHeight() == null) {
                    myBox.setHeight("20px");
                }
                if (myBox.getType() == null) {
                    myBox.setType("");
                }

                if (myBox.getType().equalsIgnoreCase("picOnly")) {

                    DBimage dbimage = new DBimage(mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myParams);
                    String imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());

                    String SLcode = "";
                    SLcode += "<table style=\" "
                            + "width:" + myBox.getWidth() + ";height:" + myBox.getHeight() + ";  "
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;"
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;"
                            + "\">" + imageCode + "</td>";
                    SLcode += "</tr></table>";
                    htmlCode += SLcode;
                } else if (myBox.getType().equalsIgnoreCase("STANDARD")) {

                    DBimage dbimage = new DBimage(mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myParams);
                    String imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());

                    String SLcode = "";
                    SLcode += "<table style=\" margin-left:auto; \n"
                            + "margin-right:auto; "
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\">" + imageCode + "</td>";
                    SLcode += "</tr><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\" ";
                    SLcode += getStyleHtmlCode(curObj, KEYvalue);
                    SLcode += ">" + ValoreDaScrivere + "</td>";
                    SLcode += "</tr></table>";
                    htmlCode += SLcode;
                } else {
                    htmlCode += ValoreDaScrivere;
                }
                htmlCode += "</a>";
            }

            htmlCode += "<INPUT  class=\"cellContent\"  type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";
        } else // </editor-fold>     
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="datepicker">             
        if (objType.equalsIgnoreCase("datepicker")) {
//== =========================================================

            if (curObj.C.getConditionalLabel() != null) {
                ValoreDaScrivere = curObj.C.getConditionalLabel();
            }
            // System.out.println("CASO datepicker ValoreDaScrivere:" + ValoreDaScrivere + " - curObj.Content.getThisRowModifiable():" + curObj.Content.getThisRowModifiable());
            if (objModifiable == true) {
                htmlCode += ("<div id=\"calendario\"   >");
                htmlCode += ("<font size=\"3\"> ");
                htmlCode += ("<p><div "
                        + "id=\"PICKER-" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                        + "class=\"datePicker\" font-size:'6'>  </p>");
                htmlCode += ("</font> ");
                htmlCode += ("</div>");
            }

            htmlCode += "<INPUT  class=\"cellContent\"  type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";
        } else // </editor-fold>     
        //----------------------------------------------------------     
        // <editor-fold defaultstate="collapsed" desc="MLSfield">             
        if (objType.equalsIgnoreCase("MLSfield")) {

            if (formRightsRules.canModify > 0) {
                if (KEYvalue.equalsIgnoreCase("NEW") || ((objModifiable == true))) {
                    htmlCode += "<INPUT type=\"BUTTON\" value=\">\" "
                            + "onClick='javascript:openSplash(\"MLSfield\","
                            + "\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\")' >";
                    htmlCode += "  ";
                }
            }
            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    + "value=\"" + ValoreDaScrivere + "\">";
            htmlCode += "<div id=\"LABL-" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" class= \" cellContent \" > ";
            //htmlCode += ValoreDaScrivere;

            SelectList myList = curObj.Origin.getSelectList();
            for (int hh = 0; hh < myList.list.size(); hh++) {
                if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                    htmlCode += myList.list.get(hh).getLabel();
                    break;
                }
            }

            htmlCode += "</div>";
        } else // </editor-fold>                  
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="userBadge">  
        if (objType.equalsIgnoreCase("userBadge")) {
//==userBadge=========================================================
            EVOuser myUser = new EVOuser(myParams, mySettings);
            htmlCode += myUser.getBadge(this.getID(), this.getCopyTag());

        } else // </editor-fold>             
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="VOICECHECK">  
        if (objType.equalsIgnoreCase("VOICECHECK")) {
//==VOICECHECK=========================================================
            //  System.out.println("CASO VOICECHECK ValoreDaScrivere:" + ValoreDaScrivere);
            /*    String oQuery = curObj.Origin.getQuery();
             String oLabelField = curObj.Origin.getLabelField();
             String oValueField = curObj.Origin.getValueField();
             String oValueFieldType = curObj.Origin.getValueFieldType();*/
            SelectList myList = curObj.Origin.getSelectList();

            //myList.getLinkList(server, database, this.getFatherKEYvalue(), this.getFatherKEYtype(), curObj.CG.getParams());
            myList.getLinkList(myParams, mySettings, this.getFatherKEYvalue(), this.getFatherKEYtype(), curObj.CG.getParams());

            htmlCode += curObj.getLabelHeader() + "</BR> ";
            htmlCode += "<TABLE class=\"cruises scrollable\" > ";
            for (int jj = 0; jj < myList.list.size(); jj++) {
                htmlCode += "<tr>";

                ValoreDaScrivere = "" + myList.list.get(jj).getChecked();
                //htmlCode += "<td> "+myList.list.get(jj).getChecked()+ "</td>";
                htmlCode += "<td> ";
                htmlCode += "<INPUT  class=\"CMLNKcontent\" type=\"CHECKBOX\" id=\"" + curObj.ID + "-" + curObj.name + "-" + myList.list.get(jj).getValue()
                        + "\" value=" + myList.list.get(jj).getChecked() + "   ";

                if (myList.list.get(jj).getChecked() > 0) {
                    htmlCode += " checked ";
                }

                if (objModifiable == true) {
                    htmlCode += "onChange=\"javascript:CMLNKchanges('" + curObj.ID + "-" + curObj.name + "-" + myList.list.get(jj).getValue()
                            + "','" + curObj.ID + "' ,"
                            + "'" + this.getFatherKEYvalue() + "','" + myList.list.get(jj).getValue() + "' )\"  ";
                } else {
                    htmlCode += " disabled ";
                }

                htmlCode += "/>";

                htmlCode += "</td> ";

                htmlCode += "<td> " + myList.list.get(jj).getLabel() + "</td>";
                //htmlCode += "<td> " + myList.list.get(jj).getValue() + "</td>";
                htmlCode += "</tr>";
            }

            htmlCode += "</TABLE> ";

        } else // </editor-fold>             
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="SPAREVALUE">                          
        if (objType.equalsIgnoreCase("SPAREVALUE")) {
//==SPAREVALUE=========================================================
            //    System.out.println("CASO SPAREVALUE campo da compilare:" + curObj.getName());
            String ValueField = curObj.getName();
            SelectList myList = curObj.Origin.getSelectList();
            myList.getSpareList(server, database, this.getFatherKEYvalue(), this.getFatherKEYtype(), curObj.CG.getParams(), ValueField);
            htmlCode += curObj.getLabelHeader() + "</BR> ";
            htmlCode += "<TABLE class=\"cruises scrollable\" > ";
            for (int jj = 0; jj < myList.list.size(); jj++) {
                htmlCode += "<tr>";

                ValoreDaScrivere = "" + myList.list.get(jj).getSpareValue();
                //htmlCode += "<td> "+myList.list.get(jj).getChecked()+ "</td>";
                htmlCode += "<td > ";
                htmlCode += "<INPUT  class=\"SPAREcontent\" type=\"TEXT\" id=\"" + curObj.ID + "-" + curObj.name + "-" + myList.list.get(jj).getValue()
                        + "\" value=\"" + myList.list.get(jj).getSpareValue() + "\"   ";

                if (formRightsRules.canModify > 0) {
                    htmlCode += "onChange=\"javascript:SPAREchanges('" + curObj.ID + "-" + curObj.name + "-" + myList.list.get(jj).getValue()
                            + "','" + curObj.ID + "' ,"
                            + "'" + this.getFatherKEYvalue() + "','" + myList.list.get(jj).getValue() + "' )\"  ";
                } else {
                    htmlCode += " disabled ";
                }

                htmlCode += ">";

                htmlCode += "</td> ";

                htmlCode += "<td> " + myList.list.get(jj).getLabel() + "</td>";
                //htmlCode += "<td> " + myList.list.get(jj).getValue() + "</td>";
                htmlCode += "</tr>";
            }

            htmlCode += "</TABLE> ";

        } else // </editor-fold>             
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="PICTURE">
        if (objType.equalsIgnoreCase("PICTURE")) {
//==PICTURE=========================================================
            //    System.out.println("CASO PICTURE campo da compilare:" + curObj.getName());
            objectLayout myBox = new objectLayout();

            myBox.loadBoxLayout(curObj.C.getJsClass());
            if (myBox.getWidth() == null) {
                myBox.setWidth("20px");
            }
            if (myBox.getHeight() == null) {
                myBox.setHeight("20px");
            }
            if (myBox.getType() == null) {
                myBox.setType("");
            }
            UUID idOne = null;
            idOne = UUID.randomUUID();
            htmlCode += "<DIV"
                    + " id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-PIC\""
                    // + " draggable=\"true\" "
                    + ">";
            /*
            ATTENZIONE: qui sto creando il segnaposto per l'immagine
            Se proviene da DB ma non è nella tabella mainTble, il valoire di keyValue deve riguardare non l'ID di riga, ma il valore di un campo
            prescelto, inserito in un valore JSON in curObj.Origin.getLabelField
             */
            // situazione standard in cui il keYfield è numerico (autoincrement) e l'immagine si trova nella stessa tabella
            String usedKeyField = curObj.Origin.getLabelField();
            String usedKeyValue = KEYvalue;
            String usedKeyType = curObj.Origin.getValueFieldType();
            String usedPicTable = curObj.Origin.getQuery();

//            System.out.println("\n\nPICTURE:situazione basic--->usedKeyField: " + usedKeyField);
//            System.out.println("situazione basic--->usedKeyValue: " + usedKeyValue);
//            System.out.println("situazione basic--->usedKeyType: " + usedKeyType);
            // situazione complessa in cui l'immagine si trova in altra tabella o in questa tabella con un keyField varchar
//            System.out.println("\ncurObj.Origin.getLabelField(): " + curObj.Origin.getLabelField());
            if (curObj.Origin.getLabelField() != null
                    && curObj.Origin.getLabelField().startsWith("{")) {
//                System.out.println("\n\n\n\nATTENZIONE CREO PICTURE DA ALTRA TABELLA. " + curObj.Origin.getLabelField());
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = null;
                String picTable = curObj.Origin.getQuery();
                String picTableKeyField = "";
                String formQueryKeyField = "";
                String formQueryKeyFieldType = "";
                String ric = curObj.Origin.getLabelField();
                try {
                    jsonObject = (JSONObject) jsonParser.parse(ric);
                    try {
                        usedKeyField = jsonObject.get("picTableKeyField").toString();
                    } catch (Exception e) {
                    }
                    try {
                        usedPicTable = jsonObject.get("picTable").toString();
                    } catch (Exception e) {
                        picTable = curObj.Origin.getQuery();
                    }
                    try {
                        formQueryKeyField = jsonObject.get("formQueryKeyField").toString();
                    } catch (Exception e) {
                    }
                    try {
                        usedKeyType = jsonObject.get("formQueryKeyFieldType").toString();
                    } catch (Exception e) {
                    }
//                    System.out.println("usedKeyField: " + usedKeyField);
//                    System.out.println("formQueryKeyField: " + formQueryKeyField);
//                    System.out.println("usedKeyType: " + usedKeyType);
//                    System.out.println("tabella con immagine: " + usedKeyType);
//                    System.out.println("ValoreDaScrivere: " + ValoreDaScrivere);
                    // cerco in rs il valore da usare come chiave;
                    if (formQueryKeyFieldType != null
                            && formQueryKeyFieldType.equalsIgnoreCase("INT")) {
                        usedKeyValue = ValoreDaScrivere;
                    } else {
                        usedKeyValue = ValoreDaScrivere;

                    }

                } catch (ParseException ex) {
                    Logger.getLogger(requestsManager.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

            String image = "<img  alt=\"...\" src='portal?rnd=" + idOne + "&target=requestsManager&gp=";
            String params = "\"params\":\"" + encodeURIComponent(myParams.makePORTALparams()) + "\"";
            String connectors = "\"connectors\":[{\"door\":\"RenderPic\","
                    + "\"event\":\"fromDB\","
                    + "\"table\":\"" + usedPicTable + "\","// es operatori
                    + "\"keyfield\":\"" + usedKeyField + "\","//es operatori.ID
                    + "\"keyValue\":\"" + usedKeyValue + "\","// es 'pippo'
                    + "\"keyType\":\"" + usedKeyType + "\","
                    + "\"picfield\":\"" + curObj.Origin.getValueField() + "\" "//es. media
                    + " }]";
            System.out.println("OGGETTO PICTURE->" + connectors);

            String utils = "\"responseType\":\"text\"";
            String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";

            image += encodeURIComponent(gp);

            image += "'  width='" + myBox.getWidth() + "px' heigth='" + myBox.getHeight() + "px' >";

            htmlCode += image;

            htmlCode += "</DIV>";
            if (objModifiable == true) {

                htmlCode += "<form method=\"post\" "
                        + " name=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FRM\""
                        + " id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FRM\""
                        + " action=\"portal\""
                        + " enctype=\"multipart/form-data\">\n"
                        //-----
                        + " <input type=\"hidden\" name=\"target\" value= \"uploadManager\"  />"
                        + " <input type=\"hidden\" name=\"gp\" value= \"" + encodeURIComponent(gp) + "\"  />"
                        //------
                        + " <input type=\"hidden\" name=\"formID\" value= \"" + this.getID() + "\"  />"
                        + " <input type=\"hidden\" name=\"formCopyTag\" value= \"" + this.getCopyTag() + "\"  />"
                        + " <input type=\"hidden\" name=\"formObjName\" value= \"" + curObj.name + "\"  />"
                        + " <input type=\"hidden\" name=\"formRowKey\" value= \"" + KEYvalue + "\"  />"
                        + " <input type=\"hidden\" name=\"primaryFieldValue\" value= \"" + KEYvalue + "\"  />"
                        + " <input type=\"hidden\" name=\"primaryFieldName\" value= \"" + curObj.Origin.getLabelField() + "\"  />"
                        + " <input type=\"hidden\" name=\"primaryFieldType\" value= \"" + curObj.Origin.getValueFieldType() + "\"  />"
                        + " <input type=\"hidden\" name=\"formName\" value= \"" + curObj.Origin.getQuery() + "\"  />"
                        + " <input type=\"hidden\" name=\"cellName\" value= \"" + curObj.Origin.getValueField() + "\"  />"
                        + " <input type=\"hidden\" name=\"CKcontextID\" value= \"" + myParams.getCKcontextID() + "\"  />"
                        + " <input type=\"hidden\" name=\"CKtokenID\" value= \"" + myParams.getCKtokenID() + "\"  />"
                        + " <input type=\"hidden\" name=\"CKuserID\" value= \"" + myParams.getCKuserID() + "\"  />"
                        + " <input type=\"hidden\" name=\"CKprojectName\" value= \"" + myParams.getCKprojectName() + "\"  />"
                        + " <input type=\"hidden\" name=\"CKprojectGroup\" value= \"" + myParams.getCKprojectGroup() + "\"  />"
                        + " <input type=\"hidden\" name=\"width\" value= \"" + myBox.getWidth() + "\"  />"
                        + " <input type=\"hidden\" name=\"height\" value= \"" + myBox.getHeight() + "\"  />"
                        + " <input type=\"file\" "
                        + " id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-IMG\" "
                        + "style=\"display:none;\" "
                        + "name=\"media\"    "
                        + "onchange=\"uploadPicture('" + this.getID() + "-" + this.getCopyTag() + "','" + curObj.name + "','" + KEYvalue + "')\""
                        + " />\n"
                        + "<label for=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-IMG\" style=\"display:block; background:lightGrey;\">"
                        + "" + curObj.getName() + ""
                        + "</label>"
                        + ""
                        + "        </form>";

            } else {

            }

        } else // </editor-fold>             
        //----------------------------------------------------------    
        // <editor-fold defaultstate="collapsed" desc="StoredDocument">
        if (objType.equalsIgnoreCase("StoredDocument")) {
//==STORED DOCUMENT=========================================================
            //      System.out.println("CASO StoredDocument campo da compilare:" + curObj.getName());
            String xW = "30px";
            String xH = "30px";
            if (curObj.CG.getParams() != null) {
                String dims = curObj.CG.getParams();
                String[] coque = dims.split(";");
                List<String> coques = Arrays.asList(coque);
                if (coques.size() > 1) {
                    xW = coques.get(0).toString();
                    xH = coques.get(1).toString();
                }

            }

            String jsonString = ValoreDaScrivere;
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject;
            String filepath = "";
            String FileSysName = "";
            String originalName = "";
            String ext = "";
            System.out.println("StoredDocument. ValoreDaScrivere:" + ValoreDaScrivere);
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
                ValoreDaScrivere = originalName + "." + ext;
            } catch (ParseException ex) {
                System.out.println("StoredDocument. error::" + ex.toString());

            }

            System.out.println("StoredDocument. FileSysName:" + FileSysName);
            System.out.println("StoredDocument. originalName:" + originalName);
            System.out.println("StoredDocument. ext:" + ext);

            String fatherID = this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue;
            String image = "";
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0 && !ValoreDaScrivere.equalsIgnoreCase("NULL")) {

                String icon = "media/fileIcon/generic.png";
                if (ValoreDaScrivere.endsWith(".pdf") || ValoreDaScrivere.endsWith(".PDF")) {
                    icon = "media/fileIcon/pdf.png";
                }
                if (ValoreDaScrivere.endsWith(".jpg") || ValoreDaScrivere.endsWith(".JPG")) {
                    icon = "media/fileIcon/pic.png";
                }
                if (ValoreDaScrivere.endsWith(".doc") || ValoreDaScrivere.endsWith(".DOC")) {
                    icon = "media/fileIcon/word.png";
                }
                if (ValoreDaScrivere.endsWith(".dwg") || ValoreDaScrivere.endsWith(".DWG")) {
                    icon = "media/fileIcon/cad.png";
                }
                if (ValoreDaScrivere.endsWith(".xls") || ValoreDaScrivere.endsWith(".XLS")) {
                    icon = "media/fileIcon/excel.png";
                }
                if (ValoreDaScrivere.endsWith(".zip") || ValoreDaScrivere.endsWith(".ZIP")) {
                    icon = "media/fileIcon/zip.png";
                }
                if (ValoreDaScrivere.endsWith(".docx") || ValoreDaScrivere.endsWith(".DOCX")) {
                    icon = "media/fileIcon/word.png";
                }
                if (ValoreDaScrivere.endsWith(".xlsx") || ValoreDaScrivere.endsWith(".XLSX")) {
                    icon = "media/fileIcon/excel.png";
                }

                htmlCode += "<DIV"
                        + " id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FILE\""
                        + ">";

                String bomb = "'" + fatherID + "','ServeFile','" + this.getID() + "','" + this.getCopyTag() + "','" + curObj.name + "','" + KEYvalue + "'";

                image = "<img  alt=\"DOWNLOAD...\" "
                        // + "title=\"DOWNLOAD...'" + ValoreDaScrivere + "'\" "
                        + "src='" + icon + "'  width='" + xW + "' heigth='" + xH + "'"
                        //+ "onclick=\"javascript:downloadFile('" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "','OBJname')\""
                        + "onclick=\"javascript:manageFile(" + bomb + ")\" \n"
                        + " >";
                htmlCode += image;
                htmlCode += "</DIV>";

            }
            if (formRightsRules.canModify > 0) {
                String iconsDiametro = "15px";
                htmlCode += "<TABLE><TR><TD>";
                String bomb = "'" + fatherID + "','askFilenameForm','" + this.getID() + "','" + this.getCopyTag() + "','" + curObj.name + "','" + KEYvalue + "'";
                htmlCode += "  <a style=\"display:block;\" "
                        + "onclick=\"javascript:manageFile(" + bomb + ")\">\n";
                image = "<img  alt=\"Upload\" title=\"Upload\" src='./media/upload.png'  "
                        + "width='" + iconsDiametro + "' heigth='" + iconsDiametro + "'"
                        + "style=\"display:block;\" "
                        + " >";
                htmlCode += image;
                htmlCode += "</a>\n";

                htmlCode += "</td><td>";
                if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0 && !ValoreDaScrivere.equalsIgnoreCase("NULL")) {

                    htmlCode += "  <a style=\"display:block;\" "
                            + "onclick=\"javascript:manageFile('" + fatherID + "','DeleteFile','" + this.getID() + "','" + this.getCopyTag() + "','" + curObj.name + "','" + KEYvalue + "')\">\n";
                    image = "<img  alt=\"Delete\" title=\"Delete\" src='./media/del.png'  "
                            + "width='" + iconsDiametro + "' heigth='" + iconsDiametro + "'"
                            + "style=\"display:block;\" "
                            + " >";
                    htmlCode += image;
                    htmlCode += "</a>\n";
                }
                htmlCode += "</TD><TD>";

                htmlCode += "<DIV id=\"secPanel-" + fatherID + "\" class=\"secondPanelClass\" ></DIV>";

                htmlCode += "</TD></TR></TABLE>";

            } else {

            }

        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="SELECT">  
        if (objType.equalsIgnoreCase("SELECT")) {
//==SELECT LIST=========================================================
            //System.out.println("CASO SELECTLIST ValoreDaScrivere:" + ValoreDaScrivere+ " - curObj.Content.getThisRowModifiable():"+curObj.Content.getThisRowModifiable());
//            System.out.println("CASO SELECTLIST objModifiable:" + objModifiable);
            if (KEYvalue.equalsIgnoreCase("NEW")
                    && (curObj.AddingRow_enabled < 1)) {
                htmlCode += "...";
            } else {
                String oValueFieldType = curObj.Origin.getValueFieldType();
                SelectList myList = curObj.Origin.getSelectList();
                if (this.type.equalsIgnoreCase("FILTER")) {
                    try {
                        ValoreDaScrivere = myList.list.get(0).getValue();
                    } catch (Exception e) {
                    }

                    // se è un pannello filtro, la selectlist serve solo a memorizzare il valore e passarlo all'Action eventuale
                    // in questo caso la selectlist si comporta come una sensibleLabel e esegue l'actrion
                    if (oValueFieldType == null) {
                        oValueFieldType = "";
                    }
                    htmlCode += "<SELECT id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" ";
                    if (KEYvalue.equalsIgnoreCase("NEW")) {
                        htmlCode += "value=\"\"   ";
                    } else {
                        if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                            if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                                ValoreDaScrivere = "null";
                            } else {
                                htmlCode += "value= " + ValoreDaScrivere + "    ";
                            }
                        } else {
                            htmlCode += "value=\"" + ValoreDaScrivere + "\"   ";
                        }
                    }
                    String params = curObj.getActionParams();
                    //  System.out.println("\n\nSensLab " + curObj.name + " getActionParams:" + params);

                    if (params == null) {
                        params = "{}";
                    }

                    if (curObj.getActionPerformed() != null
                            && curObj.getActionPerformed().equalsIgnoreCase("OpenSecForm")) {
                        // se devo aprire un form, devo ricavare la getGes_routineOnLoad di quel form
                    }
                    //  System.out.println("DISEGNO SensibleLabel:" + curObj.getName() + " " + curObj.getActionPerformed() + " " + curObj.getActionParams());
                    String toAdd = "";

                    toAdd = ",\"action\":\"" + curObj.getActionPerformed() + "\""
                            + ",\"rifForm\":\"" + this.getID() + "\""
                            + ",\"copyTag\":\"" + this.getCopyTag() + "\""
                            + ",\"fatherForm\":\"" + this.getFather() + "\""
                            + ",\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\""
                            + ",\"rifObj\":\"" + curObj.name + "\""
                            + ",\"keyValue\":\"" + KEYvalue + "\"}";
                    params = params.replace("}", toAdd);
                    htmlCode += " onchange='javascript:clickedObject( " + params + " )'";

                    htmlCode += " class='cellContent' ";
                    htmlCode += getStyleHtmlCode(curObj, KEYvalue);
                    htmlCode += " >";

                } else {

                    if (oValueFieldType == null) {
                        oValueFieldType = "";
                    }
                    htmlCode += "<SELECT id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" ";
                    if (KEYvalue.equalsIgnoreCase("NEW")) {
                        htmlCode += "value=\"\"   ";
                    } else {
                        if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                            if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                                ValoreDaScrivere = "null";
                            } else {
                                htmlCode += "value= " + ValoreDaScrivere + "    ";
                            }
                        } else {
                            htmlCode += "value=\"" + ValoreDaScrivere + "\"   ";
                        }
                    }
                    if (objModifiable == true) {

                        String jsonArgs = "{";
                        jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                        jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                        jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                        jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                        jsonArgs += "\"operation\":\"SelectChanges\",";
                        jsonArgs += "\"cellType\":\"S\",";
                        jsonArgs += "\"valueType\":\"" + curObj.Content.getType() + "\",";
                        jsonArgs += "\"filterField\":\"\",";
                        jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                        jsonArgs += "\"exitRoutine\":\"\"}";
                        if (KEYvalue.equalsIgnoreCase("NEW")) {
//                            System.out.println("jsonArgs PER NEW LINE=" + jsonArgs);
                        } else {
//                            System.out.println("jsonArgs PER NORMAL LINE=" + jsonArgs);
                        }
                        htmlCode += " onChange='javascript:cellChanged(" + jsonArgs + ")'  ";

                    } else {
                        htmlCode += " disabled ";
                    }

                    if (!KEYvalue.equalsIgnoreCase("NEW")
                            && objModifiable == false) {
                        htmlCode += " disabled ";
                    }

                    htmlCode += " class='cellContent' ";
                    htmlCode += getStyleHtmlCode(curObj, KEYvalue);
                    htmlCode += " >";
                }

                //******************************
                if (this.type.equalsIgnoreCase("FILTER")) {
                } else {
                    htmlCode += "<OPTION   value=null >...select</OPTION>";
                }

                if (myList != null && myList.list != null) {
                    for (int hh = 0; hh < myList.list.size(); hh++) {
                        htmlCode += "<OPTION  ";
                        if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                            htmlCode += "value= " + myList.list.get(hh).getValue() + "    ";
                        } else {
                            htmlCode += "value=\"" + myList.list.get(hh).getValue() + "\"   ";
                        }

                        if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                            htmlCode += " SELECTED ";
                        }
                        htmlCode += ">";
                        htmlCode += myList.list.get(hh).getLabel();
                        htmlCode += "</OPTION>";
                    }
                }

                htmlCode += "</SELECT>";
            }
            //htmlCode += ValoreDaScrivere;
        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="REALTIMESELECT">  
        if (objType.equalsIgnoreCase("REALTIMESELECT")) {
//==SELECT LIST=========================================================
            //System.out.println("CASO REALTIMESELECT ValoreDaScrivere:" + ValoreDaScrivere+ " - curObj.Content.getThisRowModifiable():"+curObj.Content.getThisRowModifiable());
//            System.out.println("CASO SELECTLIST objModifiable:" + objModifiable);
            if (KEYvalue.equalsIgnoreCase("NEW")
                    && (curObj.AddingRow_enabled < 1)) {
                htmlCode += "...";
            } else {
                String oValueFieldType = curObj.Origin.getValueFieldType();
                SelectList myList = curObj.Origin.getSelectList();

                if (oValueFieldType == null) {
                    oValueFieldType = "";
                }
                htmlCode += "<SELECT id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" ";
                if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                    if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                        ValoreDaScrivere = "null";
                    } else {
                        htmlCode += "value= " + ValoreDaScrivere + "    ";
                    }
                } else {
                    htmlCode += "value=\"" + ValoreDaScrivere + "\"   ";
                }

                if (objModifiable == true) {

                    String jsonArgs = "{";
                    jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                    jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                    jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                    jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                    jsonArgs += "\"operation\":\"SelectChanges\",";
                    jsonArgs += "\"cellType\":\"S\",";
                    jsonArgs += "\"valueType\":\"" + curObj.Content.getType() + "\",";
                    jsonArgs += "\"filterField\":\"\",";
                    jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                    jsonArgs += "\"exitRoutine\":\"\"}";

                    htmlCode += " onChange='javascript:cellChanged(" + jsonArgs + ")'  ";

                } else {
                    htmlCode += " disabled ";
                }

                if (!KEYvalue.equalsIgnoreCase("NEW")
                        && objModifiable == false) {
                    htmlCode += " disabled ";
                }

                htmlCode += " class=\"cellContent\" ";
                htmlCode += getStyleHtmlCode(curObj, KEYvalue);
                htmlCode += " >";

                htmlCode += "<OPTION   value=null >...select</OPTION>";

                for (int hh = 0; hh < myList.list.size(); hh++) {
                    htmlCode += "<OPTION  ";
                    if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                        htmlCode += "value= " + myList.list.get(hh).getValue() + "    ";
                    } else {
                        htmlCode += "value=\"" + myList.list.get(hh).getValue() + "\"   ";
                    }

                    if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                        htmlCode += " SELECTED ";
                    }
                    htmlCode += ">";
                    htmlCode += myList.list.get(hh).getLabel();
                    htmlCode += "</OPTION>";
                }
                htmlCode += "</SELECT>";
            }
            //htmlCode += ValoreDaScrivere;
        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="CUSTOMBOX">  
        if (objType.equalsIgnoreCase("CUSTOMBOX")) {
//==CUSTOMBOX=========================================================

            String sourceValue = curObj.CG.Value;
            System.out.println("CUSTOMBOX: sourceValue=" + sourceValue);

        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="MARKER">  
        if (objType.equalsIgnoreCase("MARKER")) {
//==MARKER=========================================================

            String oValueFieldType = curObj.Origin.getValueFieldType();
            SelectList myList = curObj.Origin.getSelectList();

            if (oValueFieldType == null) {
                oValueFieldType = "";
            }
            htmlCode += "<INPUT  type=\"HIDDEN\"  id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    + "value=\"" + ValoreDaScrivere + "\" />";
            if (myList != null && myList.list.size() > 0) {
                //  System.out.println("LA LISTA CONTIENE "+myList.list.size()+" ELEMENI; CERCO " + ValoreDaScrivere);
                for (int hh = 0; hh < myList.list.size(); hh++) {
                    String value = myList.list.get(hh).getValue();
                    if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                        // htmlCode += ValoreDaScrivere;
                        //   System.out.println(" TROVATO " + ValoreDaScrivere + ": LABEL = " + myList.list.get(hh).getLabel());
                        htmlCode += "<SPAN " + getStyleHtmlCode(curObj, KEYvalue) + ">";
                        htmlCode += myList.list.get(hh).getLabel();
                        htmlCode += "</SPAN>";

                        break;
                    }

                }
            }

            //htmlCode += ValoreDaScrivere;
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="RadioButton">  
        if (objType.equalsIgnoreCase("RadioButton")) {
//==RadioButton=========================================================
            //       System.out.println("CASO RADIO BUTTON:" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue);
            String oValueFieldType = curObj.Origin.getValueFieldType();
            SelectList myList = curObj.Origin.getSelectList();

            if (oValueFieldType == null) {
                oValueFieldType = "";
            }

            htmlCode += "<DIV  id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-RADIO\" >";
            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" ";
            if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                    ValoreDaScrivere = "null";
                }
                htmlCode += "value= " + ValoreDaScrivere + "    ";
            } else {
                htmlCode += "value=\"" + ValoreDaScrivere + "\"   ";
            }

            htmlCode += "   >";

            htmlCode += "<FORM "
                    + "id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FORM\" "
                    + "action=\"\">";
            htmlCode += "<TABLE><TR>";
            for (int hh = 0; hh < myList.list.size(); hh++) {
                htmlCode += "<TD> <input "
                        + "type=\"radio\" "
                        + "name=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                        + "id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                        + "value=\"" + myList.list.get(hh).getValue() + "\"";
                if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                    htmlCode += " checked ";
                } else {

                }
                if (objModifiable == true) {
                    String jsonArgs = "{";
                    jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                    jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                    jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                    jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                    jsonArgs += "\"operation\":\"RadioChanges\",";
                    jsonArgs += "\"cellType\":\"R\",";
                    jsonArgs += "\"filterField\":\"\",";
                    jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                    jsonArgs += "\"exitRoutine\":\"\"}";

                    htmlCode += " onChange='javascript:cellChanged(" + jsonArgs + ")'  ";
                } else {
                    htmlCode += " disabled ";
                }

                if (this.getShowCounter() != null && this.getShowCounter().equalsIgnoreCase("FALSE")) {
                    htmlCode += "onmouseup=\"javascript:objSelected('" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "')\"";
                }
                // htmlCode += "onmouseup=\"javascript:objSelected('" +  this.getID()+ "-"+this.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\"";
                htmlCode += ">"
                        + "" + myList.list.get(hh).getLabel() + "</TD>";

            }
            htmlCode += "</TR></TABLE>";
            htmlCode += "</FORM>";

            htmlCode += "</DIV>";

            //htmlCode += ValoreDaScrivere;
        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="RadioFilter">  
        if (objType.equalsIgnoreCase("RadioFilter")) {
//==RadioFilter=========================================================
            //  System.out.println("CASO RADIO FILTER:" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue);
            String oValueFieldType = curObj.Origin.getValueFieldType();
            SelectList myList = curObj.Origin.getSelectList();
//System.out.println("CASO RADIO FILTER:this.getInfoReceived():"+this.getInfoReceived() );

//costruisco un ghost field per ogni valote ricevuto con info received
// es. se ho incarico:CIRCO06bd0 costruisco
//<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-incarico-PANELFILTER\" value=\"CIRCO06bd0\"
            String irec = this.getInfoReceived() + ";";

            List<String> filters = Arrays.asList(irec.split(";"));
            //  System.out.println("CASO RADIO FILTER:filters.size():"+filters.size() );
            for (int jj = 0; jj < filters.size(); jj++) {

                List<String> parts = Arrays.asList(filters.get(jj).split(":"));
                if (parts.size() > 1) {
                    String nome = parts.get(0);
                    String valore = parts.get(1);
                    String codeToAdd = "<INPUT type=\"HIDDEN\" "
                            + "id=\"" + this.getID() + "-"
                            + this.getCopyTag() + "-"
                            + nome + "-PANELFILTER\" "
                            + "value=\"" + valore + "\" ;";

                    //  System.out.println("CASO RADIO FILTER:added ghost "+codeToAdd);
                    htmlCode += codeToAdd;
                }

            }

            //  System.out.println("CASO RADIO FILTER:this.getToBeSent():" + this.getToBeSent());
            if (oValueFieldType == null) {
                oValueFieldType = "";
            }
            String filterName = this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-RADIOFILTER";
            htmlCode += "<DIV  id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-RADIOFILTERDIV\" >";
            //----------CAMPO SEGNAVALORE UTILE PER SHOWCHILDS
            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-PANELFILTER\" ";
            if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                    ValoreDaScrivere = "null";
                }
                htmlCode += "value= " + ValoreDaScrivere + "    ";
            } else {
                htmlCode += "value=\"" + ValoreDaScrivere + "\"   ";
            }

            htmlCode += "   >";

            //------------    
            htmlCode += ("<TABLE border=\"1\"><TR>");
            htmlCode += ("<TD>");
            htmlCode += ("<form> ");

            htmlCode += " <fieldset class='filterRadio' id='" + filterName + "' "
                    + "onchange='javascript:radiofilterChanged(\"" + this.getID() + "\",\"" + this.getCopyTag() + "\",\"" + curObj.getName() + "\")'>";
            for (int hh = 0; hh < myList.list.size(); hh++) {

                htmlCode += ("<input type=\"radio\" "
                        + "value=\"" + myList.list.get(hh).getValue() + "\" "
                        + "name=\"" + filterName + "\" ");
                if (hh == 0) {
                    htmlCode += ("checked=\"checked\" ");
                }
                htmlCode += ("> "
                        + myList.list.get(hh).getLabel() + ""
                        + "<BR>");

            }

            htmlCode += ("    </fieldset>\n ");

            htmlCode += ("</form> ");

            htmlCode += "</TR></TABLE>";
            htmlCode += "</DIV>";

            //htmlCode += ValoreDaScrivere;
        } else // </editor-fold>             
        //----------------------------------------------------------    
        // <editor-fold defaultstate="collapsed" desc="CHECK">  
        if (objType.equalsIgnoreCase("CHECK") || objType.equalsIgnoreCase("CHECKBOX")) {
//==CHECK=========================================================
            //System.out.println(curObj.name + " _ CASO CHECK ValoreDaScrivere:" + ValoreDaScrivere + "  -objModifiable: " + objModifiable);
            htmlCode += "<INPUT "
                    // + " class=\"cellContent\""
                    + " type='CHECKBOX' id='" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "' "
                    + " value='" + ValoreDaScrivere + "'  class='x' ";
            int valore = 0;
            try {
                valore = Integer.parseInt(ValoreDaScrivere);
            } catch (Exception ex) {
                valore = 0;
            }
            if (valore > 0) {
                htmlCode += " checked='checked' ";
            }
            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"CheckChanges\",";
                jsonArgs += "\"cellType\":\"C\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onChange='javascript:cellChanged(" + jsonArgs + ")'  ";
            } else {
                htmlCode += " disabled ";
            }
            // htmlCode += "onmouseup=\"javascript:objSelected('" +  this.getID()+ "-"+this.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\"";
            htmlCode += ">";
            //htmlCode += valore;
        } else // </editor-fold>             
        //----------------------------------------------------------     
        // <editor-fold defaultstate="collapsed" desc="DATEFILTER">  
        if (objType.equalsIgnoreCase("DATEFILTER")) {
//==DATEFILTER=========================================================
            String filterName = this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-FILTER";

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format0 = new SimpleDateFormat("EEEE dd/MM/yyyy");
            String curDate = format0.format(cal.getTime());
            htmlCode += "<TABLE><TR><TD>OGGI è " + curDate + "</td></tr>"
                    + "<TR><TD>Presenze del:</td></tr><TR><TD>";
            format0 = new SimpleDateFormat("dd/MM/yyyy");
            curDate = format0.format(cal.getTime());

            htmlCode += " <INPUT id='" + filterName + "' "
                    + " class=\"datepicker datepickerfilter " + this.getID() + "_panelFilter\" font-size:'6' "
                    + " value= \"" + curDate + "\"";

            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"dateFilterChanges\",";
                jsonArgs += "\"cellType\":\"DF\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onChange='javascript:panelFilterChanged(" + jsonArgs + ")'  ";
                htmlCode += " onLoad='javascript:panelFilterChanged(" + jsonArgs + ")'  ";
            } else {
                htmlCode += " readonly ";
            }

            // htmlCode += "onmouseup=\"javascript:objSelected('" +  this.getID()+ "-"+this.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\"";
            htmlCode += "> </td></tr></table> ";

            htmlCode += ("<script language='JavaScript'>alert('Hello');</script>");
            //htmlCode += valore;
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="TEXTAREA">  
        if (objType.equalsIgnoreCase("AREA") || objType.equalsIgnoreCase("TEXTAREA")) {
//==TEXTAREA=========================================================
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
            }
            objectLayout myBox = new objectLayout();

            myBox.loadBoxLayout(curObj.C.getJsClass());
            if (myBox.getLines() == null) {
                myBox.setLines("4");
            }
            if (myBox.getColumns() == null) {
                myBox.setColumns("50");
            }

            String XcellType = "T";
            // System.out.println("CASO TEXTAREA ValoreDaScrivere:" + ValoreDaScrivere+ " tipo "+objType);
            System.out.println("CASO TEXTAREA  " + curObj.getName() + " objModifiable:" + objModifiable);
            htmlCode += "<TEXTAREA  class=\"cellContent mydiv \"";
            htmlCode += " rows=\"" + myBox.getLines() + "\" ";
            htmlCode += " cols=\"" + myBox.getColumns() + "\" ";
//            htmlCode += " style=\"font-family:'Verdana', Times, serif;"
//                    + "   font-size: 12px;\" ";
            htmlCode += " type=\"TEXTAREA\" ";
            htmlCode += " id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    //                    + "value=\"" + ValoreDaScrivere + "\"   "
                    + " ";
            htmlCode += getStyleHtmlCode(curObj, KEYvalue);
            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"areaChanges\",";
                jsonArgs += "\"cellType\":\"" + XcellType + "\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onChange='javascript:cellChanged(" + jsonArgs + ")'  ";

                //  htmlCode += "onmouseup=\"javascript:objSelected('" +  this.getID()+ "-"+this.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\" ";
            } else {
                htmlCode += " readonly ";
            }
            if (KEYvalue.equalsIgnoreCase("NEW")
                    && (curObj.AddingRow_enabled < 1)) {
                htmlCode += " readonly disabled ";
            }
            htmlCode += ">";
            htmlCode += ValoreDaScrivere;
            htmlCode += "</TEXTAREA>";
            //htmlCode += valore;
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="SUGGESTEDTEXT">  
        if (objType.equalsIgnoreCase("SUGGESTEDTEXT")) {
//==SUGGESTEDTEXT=========================================================
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
            }
            String XcellType = "T";
            // System.out.println("CASO TEXTBOX ValoreDaScrivere:" + ValoreDaScrivere+ " tipo "+objType);
            htmlCode += "<INPUT  class=\"cellContent suggestedText ";

            htmlCode += "\" ";
            htmlCode += "type=\"TEXT\" ";
            htmlCode += "id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    + "value=\"" + ValoreDaScrivere + "\"   ";

            System.out.println("\n*****\nSUGGESTEDTEXT " + curObj.getName() + "\n*****\n");

            if (objModifiable == true) {

                String params = curObj.getActionParams();
                //  System.out.println("\n\nSensLab " + curObj.name + " getActionParams:" + params);

                if (params == null) {
                    params = "{}";
                }

                if (curObj.getActionPerformed() != null
                        && curObj.getActionPerformed().equalsIgnoreCase("OpenSecForm")) {
                    // se devo aprire un form, devo ricavare la getGes_routineOnLoad di quel form
                }

                String toAdd = ",\"action\":\"" + curObj.getActionPerformed() + "\""
                        + ",\"rifForm\":\"" + this.getID() + "\""
                        + ",\"copyTag\":\"" + this.getCopyTag() + "\""
                        + ",\"fatherForm\":\"" + this.getFather() + "\""
                        + ",\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\""
                        + ",\"cellType\":\"" + XcellType + "\""
                        + ",\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\""
                        + ",\"operation\":\"textChanges\""
                        + ",\"cellID\":\"" + KEYvalue + "\""
                        + ",\"exitRoutine\":\"\""
                        + ",\"filterField\":\"\""
                        + ",\"objType\":\"suggestedText\""
                        + ",\"rifObj\":\"" + curObj.name + "\""
                        + ",\"KEYvalue\":\"" + KEYvalue + "\"}";
                params = params.replace("}", toAdd);

                String jsonArgs = params;
//                jsonArgs += "{\"formID\":\"" + this.getID() + "\",";
//                jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
//                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
//                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
//                jsonArgs += "\"operation\":\"textChanges\",";
//                jsonArgs += "\"cellType\":\"" + XcellType + "\","; 
//                jsonArgs += "\"filterField\":\"\",";
//                //jsonArgs += "\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\",";
//                jsonArgs += "\"action\":\"" + curObj.getActionPerformed() + "\",";
//                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
//                jsonArgs += "\"exitRoutine\":\"\"}";
                htmlCode += " onDblClick='javascript:cellChanged(" + jsonArgs + ")'  ";

                //----------------------------------------------
                //---infos per scheda di suggerimento
                jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                jsonArgs += "\"rifObj\":\"" + curObj.name + "\",";
                jsonArgs += "\"keyValue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"getSuggestedList\",";
                jsonArgs += "\"cellType\":\"" + XcellType + "\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"action\":\"" + curObj.getActionPerformed() + "\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";
                htmlCode += "onKeyUp='javascript:suggestList(event," + jsonArgs + ")'  ";

                //----------------------------------------------
            } else {
                htmlCode += " readonly ";
            }

            if (KEYvalue.equalsIgnoreCase("NEW")
                    && (curObj.AddingRow_enabled < 1)) {
                htmlCode += " readonly bgcolor=\"#222222\"";
            }

            htmlCode += "/>";
            htmlCode += "<INPUT  type=\"HIDDEN\"  id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-VAL\" "
                    + "value=\"" + ValoreDaScrivere + "\" />";
        } else // </editor-fold>        
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="GROUPCHECKER">  
        if (objType.equalsIgnoreCase("GROUPCHECKER")) {
//==GROUPCHECKER=========================================================
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
            }
            String XcellType = "T";
            System.out.println("\n*****\nGROUP CHECKER\n*****\n");
            String list = getGroupsList(curObj, KEYvalue);
            if (list == null || list.length() < 1) {
                list = "N.D.";
            }
            System.out.println("ValoreDaScrivere=" + ValoreDaScrivere);
            System.out.println("formRightsRules.canModify=" + formRightsRules.canModify);
            System.out.println("objModifiable=" + objModifiable);
            htmlCode += "<a  class=\"cellContent\" style=\"display:block;width:100%;height:100%;\"";
            htmlCode += "id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    + "value=\"" + ValoreDaScrivere + "\"   ";

            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                jsonArgs += "\"rifObj\":\"" + curObj.name + "\",";
                jsonArgs += "\"keyValue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"getGroups\",";
                jsonArgs += "\"cellType\":\"" + XcellType + "\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onClick='javascript:groupChecker(" + jsonArgs + ")'  ";

                //     htmlCode += "onKeyUp=\"javascript:suggest(" + jsonArgs + ")'  ";
            } else {
                htmlCode += " ";
            }

            htmlCode += ">";
            htmlCode += list;
            htmlCode += "</a>";
        } else // </editor-fold>        
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="SVGMAP">  
        if (curObj.C.getType().equalsIgnoreCase("SVGMAP")) {
//==SVGMAP=========================================================
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
            }
            htmlCode += "MAPPA-" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue
                    + "";

            /*
         1. apro il DB delle mappe e cerco la mappa in questione con questo ID=keyvalue
         2. carico le caratteristiche della mappa
         3. apro il DB dei path e carico i paths di questa mappa
             */
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="TREEVIEW">  
        if (curObj.C.getType().equalsIgnoreCase("TREEVIEW")) {
//==TREEVIEW=========================================================
            SMARTtreeView myTree = new SMARTtreeView(myParams, mySettings);
            String elenco = myTree.buildBaseCode(0, this, curObj);
            htmlCode += elenco;
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="RANGER">  
        if (curObj.C.getType().equalsIgnoreCase("RANGER")) {
            //==RANGER=========================================================

            //------------------------------------
            String oQuery = curObj.Origin.getQuery();
//            System.out.println("\n****\nRANGER  " + curObj.getName() + "--_>APPLICO SOSTITUZIONI:" + oQuery);
            String oValueFieldType = curObj.Origin.getValueFieldType();
            SelectList myList = null;
            if (KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")) {

            } else {
                oQuery = browserArgsReplace(oQuery);
                System.out.println("diventa:" + oQuery);
                System.out.println("getCKprojectName:" + myParams.getCKprojectName());
                String oLabelField = curObj.Origin.getLabelField();
                String oValueField = curObj.Origin.getValueField();
                myList = new SelectList(myParams, mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                myList.getList();
                curObj.Origin.setSelectList(myList);
            }
            if (oValueFieldType == null) {
                oValueFieldType = "";
            }
            objectLayout myBox = new objectLayout();

            myBox.loadBoxLayout(curObj.C.getJsClass());
            if (myBox.getWidth() == null) {
                myBox.setWidth("100px");
            }
            if (myBox.getHeight() == null) {
                myBox.setHeight("20px");
            }
            if (myBox.getType() == null) {
                myBox.setType("");
            }
            String widthTable = "100px";
            if (curObj.C.Width != null && curObj.C.Width.length() > 0) {
                widthTable = curObj.C.Width;
            }

            htmlCode += "<DIV   style=\"width:" + widthTable + ";max-width:" + widthTable + "; \" "
                    + "id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-RADIO\" >";
            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" ";
            if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                    ValoreDaScrivere = "null";
                }
                htmlCode += "value= " + ValoreDaScrivere + " ";
            } else {
                htmlCode += "value=\"" + ValoreDaScrivere + "\" ";
            }
            htmlCode += ">";

            if (myBox.getType().equalsIgnoreCase("picOnly")) {
                //2020-03-04---da rivedere perchè  l'ho preso dal sensibleLabel ma bisogna adattarlo

////////                DBimage dbimage = new DBimage(mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myParams);
////////                String imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());
////////
////////                String SLcode = "";
////////                SLcode += "<table style=\" "
////////                        + "width:" + myBox.getWidth() + ";height:" + myBox.getHeight() + ";  "
////////                        + "margin: 0px 0 0px 0; padding: 1px;"
////////                        + "vertical-align:middle;\" >"
////////                        + "<tr style=\"text-align:center;vertical-align:middle;\">";
////////                SLcode += "<td style=\"text-align:center;"
////////                        + "margin: 0px 0 0px 0; padding: 0px;"
////////                        + "vertical-align:middle;"
////////                        + "\">" + imageCode + "</td>";
////////                SLcode += "</tr></table>";
////////                htmlCode += SLcode;
            } else if (myBox.getType().equalsIgnoreCase("STANDARD")) {
////////                DBimage dbimage = new DBimage(mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myParams);
////////                //DBimage( table,  keyfield,  keyValue,  picfield,  myParams) {
////////
////////                String imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());
////////
////////                String SLcode = "";
////////                SLcode += "<table style=\" margin-left:auto; \n"
////////                        + "margin-right:auto; "
////////                        + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
////////                SLcode += "<td style=\"text-align:center;vertical-align:middle;\">" + imageCode + "</td>";
////////                SLcode += "</tr><tr style=\"text-align:center;vertical-align:middle;\">";
////////                SLcode += "<td style=\"text-align:center;vertical-align:middle;\" ";
////////                SLcode += getStyleHtmlCode(curObj, KEYvalue);
////////                SLcode += ">" + ValoreDaScrivere + "</td>";
////////                SLcode += "</tr></table>";
////////                htmlCode += SLcode;
            } else {// textOnly
////////
            }

            htmlCode += "<FORM "
                    + "id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FORM\" "
                    + "action=\"\">";

            htmlCode += "<TABLE style=\"table-layout: fixed; width: 100% ;"
                    + " border-collapse: collapse;   border: 1px solid black;display: block; overflow-x: auto; \"><TR>";
            if (myList != null) {
                for (int hh = 0; hh < myList.list.size(); hh++) {
                    htmlCode += "<TD style=\"width:" + myBox.getWidth() + ";max-width:" + myBox.getWidth() + ";"
                            + "overflow-wrap: break-word;word-wrap: break-word; text-align:center; vertical-align:top; border-collapse: collapse;   border: 1px solid black;\">";
                    htmlCode += " <input "
                            + "type=\"radio\" "
                            + "name=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                            + "id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                            + "value=\"" + myList.list.get(hh).getValue() + "\"";
                    if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                        htmlCode += " checked ";
                    } else {

                    }
                    if (objModifiable == true) {
                        String jsonArgs = "{";
                        jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                        jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                        jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                        jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                        jsonArgs += "\"operation\":\"RadioChanges\",";
                        jsonArgs += "\"cellType\":\"R\",";
                        jsonArgs += "\"filterField\":\"\",";
                        jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                        jsonArgs += "\"exitRoutine\":\"\"}";

                        htmlCode += " onChange='javascript:cellChanged(" + jsonArgs + ")'  ";
                    } else {
                        htmlCode += " disabled ";
                    }
                    // htmlCode += "onmouseup=\"javascript:objSelected('" +  this.getID()+ "-"+this.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\"";
                    htmlCode += ">"
                            + "" + myList.list.get(hh).getLabel() + "</TD>";
                }
            }
            htmlCode += "</TR></TABLE>";
            htmlCode += "</FORM>";

            htmlCode += "</DIV>";
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="ROWPICTURE">  
        if (curObj.C.getType().equalsIgnoreCase("ROWPICTURE")) {
            //==ROWPICTURE=========================================================
            htmlCode += "<DIV"
                    + " id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-PIC\" ";
            // + " draggable=\"true\" "
            if (this.getShowCounter() != null && this.getShowCounter().equalsIgnoreCase("FALSE")) {
                htmlCode += " onmouseup=\"javascript:objSelected('" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "')\"";
            }

            htmlCode += ">";
            /*
            ATTENZIONE: qui sto creando il segnaposto per l'immagine
            Se proviene da DB ma non è nella tabella mainTble, il valoire di keyValue deve riguardare non l'ID di riga, ma il valore di un campo
            prescelto, inserito in un valore JSON in curObj.Origin.getLabelField
             */
            // situazione standard in cui il keYfield è numerico (autoincrement) e l'immagine si trova nella stessa tabella
            String usedKeyField = curObj.Origin.getLabelField();
            String usedKeyValue = KEYvalue;
            String usedKeyType = curObj.Origin.getValueFieldType();
            String usedPicTable = curObj.Origin.getQuery();

//            System.out.println("\n\nPICTURE:situazione basic--->usedKeyField: " + usedKeyField);
//            System.out.println("situazione basic--->usedKeyValue: " + usedKeyValue);
//            System.out.println("situazione basic--->usedKeyType: " + usedKeyType);
            // situazione complessa in cui l'immagine si trova in altra tabella o in questa tabella con un keyField varchar
//            System.out.println("\ncurObj.Origin.getLabelField(): " + curObj.Origin.getLabelField());
            if (curObj.Origin.getLabelField() != null
                    && curObj.Origin.getLabelField().startsWith("{")) {
//                System.out.println("\n\n\n\nATTENZIONE CREO PICTURE DA ALTRA TABELLA. " + curObj.Origin.getLabelField());
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = null;
                String picTable = curObj.Origin.getQuery();
                String picTableKeyField = "";
                String formQueryKeyField = "";
                String formQueryKeyFieldType = "";
                String ric = curObj.Origin.getLabelField();
                try {
                    jsonObject = (JSONObject) jsonParser.parse(ric);
                    try {
                        usedKeyField = jsonObject.get("picTableKeyField").toString();
                    } catch (Exception e) {
                    }
                    try {
                        usedPicTable = jsonObject.get("picTable").toString();
                    } catch (Exception e) {
                        picTable = curObj.Origin.getQuery();
                    }
                    try {
                        formQueryKeyField = jsonObject.get("formQueryKeyField").toString();
                    } catch (Exception e) {
                    }
                    try {
                        usedKeyType = jsonObject.get("formQueryKeyFieldType").toString();
                    } catch (Exception e) {
                    }

                } catch (ParseException ex) {
                    Logger.getLogger(requestsManager.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

            htmlCode += ValoreDaScrivere;

            htmlCode += "</DIV>";
////////            if (objModifiable == true) {
////////                String params = "\"params\":\"" + encodeURIComponent(myParams.makePORTALparams()) + "\"";
////////                String connectors = "\"connectors\":[{\"door\":\"RenderPic\","
////////                        + "\"event\":\"fromDB\","
////////                        + "\"table\":\"" + usedPicTable + "\","// es operatori
////////                        + "\"keyfield\":\"" + usedKeyField + "\","//es operatori.ID
////////                        + "\"keyValue\":\"" + usedKeyValue + "\","// es 'pippo'
////////                        + "\"keyType\":\"" + usedKeyType + "\","
////////                        + "\"picfield\":\"" + curObj.Origin.getValueField() + "\" "//es. media
////////                        + " }]";
////////                System.out.println("OGGETTO PICTURE->" + connectors);
////////
////////                String utils = "\"responseType\":\"text\"";
////////                String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
////////                htmlCode += "<form method=\"post\" "
////////                        + " name=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FRM\""
////////                        + " id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FRM\""
////////                        + " action=\"portal\""
////////                        + " enctype=\"multipart/form-data\">\n"
////////                        //-----
////////                        + " <input type=\"hidden\" name=\"target\" value= \"uploadManager\"  />"
////////                        + " <input type=\"hidden\" name=\"gp\" value= \"" + encodeURIComponent(gp) + "\"  />"
////////                        //------
////////                        + " <input type=\"hidden\" name=\"formID\" value= \"" + this.getID() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"formCopyTag\" value= \"" + this.getCopyTag() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"formObjName\" value= \"" + curObj.name + "\"  />"
////////                        + " <input type=\"hidden\" name=\"formRowKey\" value= \"" + KEYvalue + "\"  />"
////////                        + " <input type=\"hidden\" name=\"primaryFieldValue\" value= \"" + KEYvalue + "\"  />"
////////                        + " <input type=\"hidden\" name=\"primaryFieldName\" value= \"" + curObj.Origin.getLabelField() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"primaryFieldType\" value= \"" + curObj.Origin.getValueFieldType() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"formName\" value= \"" + curObj.Origin.getQuery() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"cellName\" value= \"" + curObj.Origin.getValueField() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"CKcontextID\" value= \"" + myParams.getCKcontextID() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"CKtokenID\" value= \"" + myParams.getCKtokenID() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"CKuserID\" value= \"" + myParams.getCKuserID() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"CKprojectName\" value= \"" + myParams.getCKprojectName() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"CKprojectGroup\" value= \"" + myParams.getCKprojectGroup() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"width\" value= \"" + myBox.getWidth() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"height\" value= \"" + myBox.getHeight() + "\"  />"
////////                        + " <input type=\"file\" "
////////                        + " id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-IMG\" "
////////                        + "style=\"display:none;\" "
////////                        + "name=\"media\"    "
////////                        + "onchange=\"uploadPicture('" + this.getID() + "-" + this.getCopyTag() + "','" + curObj.name + "','" + KEYvalue + "')\""
////////                        + " />\n"
////////                        + "<label for=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-IMG\" style=\"display:block; background:lightGrey;\">"
////////                        + "" + curObj.getName() + ""
////////                        + "</label>"
////////                        + ""
////////                        + "        </form>";
////////
////////            }

        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="RICHTEXT">  
        if (curObj.C.getType().equalsIgnoreCase("RICHTEXT")) {
            //==RICHTEXT=========================================================
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
            }
            objectLayout myBox = new objectLayout();

            myBox.loadBoxLayout(curObj.C.getJsClass());
            if (myBox.getLines() == null) {
                myBox.setLines("4");
            }
            if (myBox.getColumns() == null) {
                myBox.setColumns("50");
            }

            String XcellType = "T";
            // System.out.println("CASO TEXTAREA ValoreDaScrivere:" + ValoreDaScrivere+ " tipo "+objType);
            System.out.println("CASO RICHTEXT  " + curObj.getName() + " objModifiable:" + objModifiable);
            htmlCode += "<TEXTAREA  class=\"richTextClass cellContent mydiv \"";
            htmlCode += " rows=\"" + myBox.getLines() + "\" ";
            htmlCode += " cols=\"" + myBox.getColumns() + "\" ";
//            htmlCode += " style=\"font-family:'Verdana', Times, serif;"
//                    + "   font-size: 12px;\" ";
            htmlCode += " type=\"RTAREA\" ";
            String objectID = this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue;
            htmlCode += " style=\"display:block; border: 1px solid #000;\" ";
            htmlCode += " id=\"" + objectID + "\" "
                    //                    + "value=\"" + ValoreDaScrivere + "\"   "
                    + " ";
            htmlCode += getStyleHtmlCode(curObj, KEYvalue);
            objModifiable = true;
            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"areaChanges\",";
                jsonArgs += "\"cellType\":\"" + XcellType + "\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onChange='javascript:cellChanged(" + jsonArgs + ")'  ";
//                htmlCode += "onfocusin='javascript:rtfEvent(\"IN\",\"" + objectID + "\");' ";
//                htmlCode += "onfocusout='javascript:rtfEvent(\"OUT\",\"" + objectID + "\");' ";

                //  htmlCode += "onmouseup=\"javascript:objSelected('" +  this.getID()+ "-"+this.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\" ";
            } else {
                //  htmlCode += " readonly ";
            }
            if (KEYvalue.equalsIgnoreCase("NEW")
                    && (curObj.AddingRow_enabled < 1)) {
                htmlCode += " readonly disabled ";
            }
            htmlCode += ">";
            htmlCode += ValoreDaScrivere;
            htmlCode += "</TEXTAREA>";
            //htmlCode += valore;

        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="BORDERBOXOPEN">  
        if (curObj.C.getType().equalsIgnoreCase("BORDERBOXOPEN")) {
            //==BORDERBOXOPEN=========================================================

            htmlCode += "<TABLE>";
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="BORDERBOXCLOSE">  
        if (curObj.C.getType().equalsIgnoreCase("BORDERBOXCLOSE")) {
            //==BORDERBOXOPEN=========================================================

            htmlCode += "</TABLE>";
        } else // </editor-fold>             
        //----------------------------------------------------------     
        // <editor-fold defaultstate="collapsed" desc="PUREHTML">  
        if (curObj.C.getType().equalsIgnoreCase("PUREHTML")) {
            //==PUREHTML=========================================================

            htmlCode += curObj.CG.getValue();
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="RTSYNOPTIC">  
        if (curObj.C.getType().equalsIgnoreCase("RTSYNOPTIC")) {
            //==RTSYNOPTIC=========================================================
            if (curObj.C.getConditionalLabel() != null) {
                ValoreDaScrivere = curObj.C.getConditionalLabel();
            }
            // System.out.println("CASO SensibleLABEL ValoreDaScrivere:" + ValoreDaScrivere + " - curObj.Content.getThisRowModifiable():" + curObj.Content.getThisRowModifiable());
            String RTCode = "";
            if (objModifiable == true || objCanPushButton == true) {
                //System.out.println("CASO SensibleLABEL ValoreDaScrivere:" + ValoreDaScrivere);
                RTCode += "<a class=\"SensibleLABEL "
                        //    + "cellContent"
                        + "\" id=\"" + this.getID() + "-" + curObj.name + "\" ";
                RTCode += " style= \"width:" + curObj.C.getWidth() + "; ";
                String defaultStyle = "";
                String retreivedStyle = "";
                //2.0 default style
                if (curObj.C.getDefaultStyle() != null && curObj.C.getDefaultStyle().length() > 0) {
                    defaultStyle = curObj.C.getDefaultStyle();
                }
                if (curObj.getTriggeredStyle() != null && curObj.getTriggeredStyle().length() > 2) {
                    retreivedStyle += curObj.getTriggeredStyle();
                } else if (curObj.C.getDefaultStyle() != null) {
                    //     System.out.println(" TROVATO DFAULT STYLE:" + curObj.C.getDefaultStyle());
                    retreivedStyle += defaultStyle;
                }
                if (retreivedStyle.contains("display:")) {

                } else {
                    retreivedStyle += " display:block; ";
                }
                if (retreivedStyle.contains("background-color:")) {

                } else {
                    retreivedStyle += " background-color:lightGreen; ";
                }

                RTCode += retreivedStyle;
                RTCode += "\"";
                String params = curObj.getActionParams();
                if (params == null) {
                    params = "{}";
                }
                if (curObj.getActionPerformed() != null
                        && curObj.getActionPerformed().equalsIgnoreCase("OpenSecForm")) {
                }
                String toAdd = ",\"action\":\"" + curObj.getActionPerformed() + "\""
                        + ",\"rifForm\":\"" + this.getID() + "\""
                        + ",\"copyTag\":\"" + this.getCopyTag() + "\""
                        + ",\"fatherForm\":\"" + this.getFather() + "\""
                        + ",\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\""
                        + ",\"triggerEvent\":\"click\""
                        + ",\"rifObj\":\"" + curObj.name + "\""
                        + ",\"keyValue\":\"" + KEYvalue + "\"}";
                params = params.replace("}", toAdd);
                RTCode += " onclick='javascript:RTonClick( " + params + " )'";
//                params.replace(",\"triggerEvent\":\"click\"", ",\"triggerEvent\":\"dblclick\"");
//                RTCode += " ondblclick='javascript:dblclickedObject( " + params + " )'";

                RTCode += " ";
                RTCode += "> ";

                // cerco una immagine in gFEobjects, campo 'picture' dove ID = id di questo oggetto
                objectLayout myBox = new objectLayout();
                myBox.loadBoxLayout(curObj.C.getJsClass());
                if (myBox.getWidth() == null) {
                    myBox.setWidth("20px");
                }
                if (myBox.getHeight() == null) {
                    myBox.setHeight("20px");
                }
                if (myBox.getType() == null) {
                    myBox.setType("");
                }
                String imageCode = " <DIV id=\"" + curObj.name + "-" + KEYvalue + "\" "
                        + "style=\"width:" + myBox.getWidth() + ";"
                        + "height:" + myBox.getHeight() + ";"
                        + "\"></DIV> ";
                if (myBox.getType().equalsIgnoreCase("picOnly")) {
                    String SLcode = "";
                    SLcode += "<table style=\" "
                            + "width:" + myBox.getWidth() + ";height:" + myBox.getHeight() + ";  "
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;"
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;"
                            + "\">" + imageCode + "</td>";
                    SLcode += "</tr></table>";
                    RTCode += SLcode;
                } else if (myBox.getType().equalsIgnoreCase("STANDARD")) {
                    String SLcode = "";
                    SLcode += "<table style=\" margin-left:auto; \n"
                            + "margin-right:auto; "
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\">" + imageCode + "</td>";
                    SLcode += "</tr><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\" ";
                    SLcode += getStyleHtmlCode(curObj, KEYvalue);
                    SLcode += ">" + ValoreDaScrivere + "</td>";
                    SLcode += "</tr></table>";
                    RTCode += SLcode;
                } else {
                    RTCode += ValoreDaScrivere;

                }
                RTCode += "</a>";
            }

            htmlCode += RTCode;
            htmlCode += "<INPUT  class=\"cellContent\"  type=\"HIDDEN\" id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";
        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="TEXT">  
        {
            int flg = 0;
//            if (ValoreDaScrivere.startsWith("{")) {
//                System.out.println("CASO TEXTBOX ValoreDaScrivere:" + ValoreDaScrivere);
//            
//            }
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                flg++;
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
                ValoreDaScrivere = ValoreDaScrivere.replace("<", "&lt;");
                ValoreDaScrivere = ValoreDaScrivere.replace(">", "&gt;");
                ValoreDaScrivere = ValoreDaScrivere.replace("{", "&lbrace;");
                ValoreDaScrivere = ValoreDaScrivere.replace("}", "&rbrace;");
                ValoreDaScrivere = ValoreDaScrivere.replace("'", "&apos;");
            }

//            if (flg > 0) {
//                System.out.println("diventa:" + ValoreDaScrivere);
//            }
            String XcellType = "T";
            // System.out.println("CASO TEXTBOX ValoreDaScrivere:" + ValoreDaScrivere + " tipo " + objType);

            if (this.getType().equalsIgnoreCase("PANEL")) {
                htmlCode += " " + curObj.getLabelHeader();
            }

            htmlCode += "<INPUT  class=\"cellContent ";

            if ((curObj.Content.getType() != null
                    && curObj.Content.getType().equalsIgnoreCase("date"))
                    || (objType != null && objType.equalsIgnoreCase("date"))) {

                // in display devo invertire l'ordine di anno e giorno
                // se si tratta di una data non nulla il formato sarà 2018-01-29 e diventa 29/01/2018
                if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 9
                        && (ValoreDaScrivere.substring(4, 5).equalsIgnoreCase("-") || ValoreDaScrivere.substring(4, 5).equalsIgnoreCase("/"))) {
                    // inverto
                    String xday = ValoreDaScrivere.substring(8, 10);
                    String xmonth = ValoreDaScrivere.substring(5, 7);
                    String xyear = ValoreDaScrivere.substring(0, 4);
                    ValoreDaScrivere = xday + "/" + xmonth + "/" + xyear;
                }
                if (objModifiable == true) {
                    htmlCode += (" datepickerclass ");
                    XcellType = "D";
                }
            } else if ((curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("dateTime"))
                    || (objType != null && objType.equalsIgnoreCase("dateTime"))) {
                htmlCode += (" datetimepickerclass ");
                XcellType = "DT";
            } else if ((curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("Time"))
                    || (objType != null && objType.equalsIgnoreCase("Time"))) {
                htmlCode += (" timepickerclass ");
                XcellType = "TM";
            }

            htmlCode += "\" ";
            htmlCode += "type=\"TEXT\" ";

            htmlCode += getStyleHtmlCode(curObj, KEYvalue);

            htmlCode += "id=\"" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    + "value='" + ValoreDaScrivere + "'  ";
            if (curObj.getActionParams() == null) {
                curObj.setActionParams("");
            }
            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + this.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + this.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"textChanges\",";
                jsonArgs += "\"cellType\":\"" + XcellType + "\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"actionParams\":\"" + encodeURIComponent(curObj.getActionParams()) + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onChange='javascript:cellChanged(" + jsonArgs + ")'  ";
                if (this.getShowCounter() != null && this.getShowCounter().equalsIgnoreCase("FALSE")) {
                    htmlCode += " onmouseup=\"javascript:objSelected('" + this.getID() + "-" + this.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "')\"";
                }
                //  htmlCode += "onmouseup=\"javascript:objSelected('" +  this.getID()+ "-"+this.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\" ";
            } else {
                htmlCode += " readonly ";
            }
            if (KEYvalue.equalsIgnoreCase("NEW") && curObj.AddingRow_enabled < 1) {
                htmlCode += " readonly disabled ";
            }
            /* if (objType.equalsIgnoreCase("date")) {
             htmlCode += (" class=\"datepickerclass\" ");
             // out.println(" class=\"datepicker\" ");
             }
             if (objType.equalsIgnoreCase("dateTime")) {
             htmlCode += (" class=\"datetimepickerclass\" ");
             }*/
            String fontSize = curObj.C.conditionalFontSize;
            htmlCode += "/>";
        }
        // </editor-fold>             
//----------------------------------------------------------      

        return htmlCode;
    }

    public String fillRow(String WhereClause) {
// routine usata da classi esterne per ridisegnare una riga (ed es. una newline appena aggiunta)
//        System.out.println("-fillRow-SHOWITFORM-this.query:" + this.query);
//        System.out.println("-fillRow-SHOWITFORM-WhereClause:" + WhereClause);
        String htmlCode = "";
        String coda = "";
        String Gcoda = "";
        // buildSchema();

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
                //  System.out.println("----SMART--- posizione ultimo WHERE:" + lastWHEREposition);
                //2. tutta la parte di testo prima del WHERE è da tenere          
                smartPartToKeep = this.query.substring(0, lastWHEREposition);
                //  System.out.println("----SMART--- da tenere:" + smartPartToKeep);

                afterWHERE = this.query.substring(lastWHEREposition + 5, this.query.length());
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
                        smartTail = " ORDER BY " + text.substring(lastORDERBYposition + 8, text.length());
                    }
                } else {
                    //c'è un groupby da considerare dopo il where e se c'è conterrà anche l'order by
                    smartTail = " GROUP BY " + text.substring(lastGROPUPBYposition + 8, text.length());

                }

            }

            this.query = smartPartToKeep + WhereClause + smartTail;
        }
        System.out.println("-fillRow-SHOWITFORM this.query prima delle sostituzioni:" + this.query);
        System.out.println(" -fillRow-this.sendToCRUD:" + this.sendToCRUD);
        buildObjectsOriginList();

        EVOpagerDBconnection myCon = new EVOpagerDBconnection();
        System.out.println("\nooooooooooooooooooooooooooooo\nSONO IN fillRow - DB NAME:" + this.database.getDbExtendedName());
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();

        ResultSet rs;

        String SQLphrase = browserArgsReplace(this.query);
        System.out.println("-fillRow-SHOWITFORM:" + SQLphrase);

        try {
            //======================================================
            Statement s = conny.createStatement();
            rs = s.executeQuery(SQLphrase);
            int lines = 0;
            while (rs.next()) {
                lines++;
                //System.out.println("------------------------riga:" + lines);
                htmlCode += paintRow(rs, 0, "normal");
                //System.out.println("------------------------fine riga:" + lines);
                break;
            }

            conny.close();
        } catch (SQLException ex) {
            System.out.println("error in line 5094");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
//System.out.println("--Ritorno:" + htmlCode);
//System.out.println("ooooooooooooooooooooooooooooo CHIUDO IN fillRow  "  );

        return htmlCode;

    }

    public void getFormInformationsFromDB() {
        Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();

//System.out.println("URL:" +server.getURL());
//System.out.println("database.getName():" +database.getName());
        // Cerco in gFE_forms la mainTable di questo Form
        String SQLphrase = "";
        try {
            Statement s = FEconny.createStatement();
            SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_forms() + " WHERE name='" + this.name + "'";
            ResultSet rs = s.executeQuery(SQLphrase);
//            System.out.println("SQLphrase:" + SQLphrase);
            while (rs.next()) {
                this.ID = rs.getString("ID");
                this.mainTable = rs.getString("mainTable");
                this.query = rs.getString("query");
                this.position = rs.getString("position");
                this.type = rs.getString("type");
                this.serverURL = rs.getString("serverURL");
                this.databaseID = rs.getString("databaseID");
                //...

            }
            FEconny.close();
        } catch (SQLException ex) {
            System.out.println("error in line 5119");
        }

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
            // System.out.println("----SMART--- query iniziale:" + this.query);
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
                //System.out.println("----SMART--- posizione ultimo WHERE:" + lastWHEREposition);
                //2. tutta la parte di testo prima del WHERE è da tenere          
                smartPartToKeep = this.query.substring(0, lastWHEREposition);
//                System.out.println("----SMART--- da tenere:" + smartPartToKeep);

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
//            System.out.println("----SMART--- smartTail:" + smartTail);
//            System.out.println("----SMART--- staticWhere:" + staticWhere);
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

                System.out.println("Creo filtro qualificato:makeQualifiedQuery()=" + qry);
                this.setQuery(qry);
            }

        }

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

    public String browserArgsReplace(String query) {
//
//        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>\nbrowserArgsReplace< "
//                + query + "\n>>>>>>>>>>>>>>>>>>>>>>\n" + this.getSendToCRUD());
        //System.out.println("SOSTITUZIONI DISPONIBILI: ");
        if (query == null) {
            return null;
        }

        CRUDorder myCRUD = new CRUDorder(myParams, mySettings);
        myCRUD.setFatherKEYvalue(this.fatherKEYvalue);

        myCRUD.setSendToCRUD(this.sendToCRUD);
        query = myCRUD.standardReplace(query, null);
        // adesso rimpiazzo anche i valori di rowValues contrassegnati con !##xxx##!

        if (rowValues != null) {
//            System.out.println("rowValues: " + rowValues.size());

            for (int jj = 0; jj < rowValues.size(); jj++) {
                String toBeReplaced = "!##" + rowValues.get(jj).getMarker() + "##!";
                System.out.println("rowValues: " + rowValues.get(jj).getMarker() + "-->" + rowValues.get(jj).getValue());
                if (query.contains(toBeReplaced)) {
                    query = query.replace(toBeReplaced, rowValues.get(jj).getValue());
                }
            }
        }
//        System.out.println("\n>browserArgsReplace>>> " + query);
        return query;

    }

    public class labels {

        String labelText;
        String labelField;

        public String getLabelText() {
            return labelText;
        }

        public void setLabelText(String labelText) {
            this.labelText = labelText;
        }

        public String getLabelField() {
            return labelField;
        }

        public void setLabelField(String labelField) {
            this.labelField = labelField;
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

    public String getChildrenList() {

        Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        String SQLphrase = "";

        ArrayList<childLink> myChilds = new ArrayList<childLink>();
        try {
            Statement s = FEconny.createStatement();
            String tabForms = mySettings.getLocalFE_forms();
            String tabForms_childhood = mySettings.getLocalFE_forms_childhood();

            SQLphrase = "SELECT " + tabForms_childhood + ".*, " + tabForms + ".query FROM " + tabForms_childhood + " "
                    + " LEFT JOIN " + tabForms + " ON " + tabForms + ".name = " + tabForms_childhood + ".rifChild "
                    + " WHERE `rifFather`='" + this.getName() + "'";

//            System.out.println("getChildrenList: " + SQLphrase);
            ResultSet rs = s.executeQuery(SQLphrase);

            int lines = 0;
            while (rs.next()) {
                childLink myChild = new childLink();
                lines++;
                myChild.position = rs.getString("destination");
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
            for (int child = 0; child < myChilds.size(); child++) {
                SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_forms() + " WHERE `name`='" + myChilds.get(child).getRifChild() + "' ";

                rs = s.executeQuery(SQLphrase);
                while (rs.next()) {
                    myChilds.get(child).setQuery(rs.getString("query"));
                    myChilds.get(child).setRifChildID(rs.getString("ID"));
                    myChilds.get(child).setRoutineOnLoad(rs.getString("ges_routineOnLoad"));
                    myChilds.get(child).setType(rs.getString("type"));
                    try {
                        myChilds.get(child).setRoutineAfterLoad(rs.getString("ges_routineAfterLoad"));
                    } catch (Exception e) {
                    }
                    System.out.println("\n\ngetChildrenList getRoutineAfterLoad(): " + myChilds.get(child).getRoutineAfterLoad());

                }

            }
            FEconny.close();
        } catch (SQLException ex) {
            System.out.println("error in line 5317");
        }
        String Message = "";
        Message += "{\"childs\":[";
        for (int child = 0; child < myChilds.size(); child++) {
            if (child > 0) {
                Message += ",";
            }
            Message += "{";
            Message += "\"position\":\"" + myChilds.get(child).getPosition() + "\",";
            Message += "\"rifChild\":\"" + myChilds.get(child).getRifChild() + "\",";
            Message += "\"rifChildID\":\"" + myChilds.get(child).getRifChildID() + "\",";
            Message += "\"ges_routineOnLoad\":\"" + myChilds.get(child).getRoutineOnLoad() + "\",";
            Message += "\"ges_routineAfterLoad\":\"" + myChilds.get(child).getRoutineAfterLoad() + "\",";
            Message += "\"type\":\"" + myChilds.get(child).getType() + "\",";
            Message += "\"query\":\"" + myChilds.get(child).getQuery() + "\"";
            Message += "}";

        }
        Message += "]}";
//        System.out.println("getChilds :" + Message);

        this.childrenList = Message;
        return this.childrenList;

    }

    private class childLink {

        String routineOnLoad;
        String routineAfterLoad;
        String position;
        String query;
        String rifChild;
        String rifChildID;
        String type;

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

        public String getRoutineOnLoad() {
            return routineOnLoad;
        }

        public void setRoutineOnLoad(String routineOnLoad) {
            this.routineOnLoad = routineOnLoad;
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

    public void getFormPanel() {
        System.out.println("getFormPanel-->formPanel:" + this.getGes_formPanel());
        if (this.getGes_formPanel() != null && this.getGes_formPanel().length() > 4) {
            String formPanel = "{\"formPanel\":" + this.getGes_formPanel() + "}";
//            System.out.println("formPanel:" + formPanel);

            JSONObject jsonObject = new JSONObject();
            JSONParser jsonParser = new JSONParser();
            ArrayList<lockRule> lockRules = new ArrayList<lockRule>();
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

                    for (Object riga : array) {
//                        lockRule myRule = new lockRule();
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
                                this.addRowPosition = ("bottom");
                            }
                            try {
                                this.refreshOnAdd = (jsonObject.get("refreshOnAdd").toString());
                            } catch (Exception e) {
                                this.refreshOnAdd = ("");
                            }
                            try {
                                this.refreshOnUpdate = (jsonObject.get("refreshOnUpdate").toString());
                                System.out.println("\nrefreshOnUpdate:" + this.refreshOnUpdate);

                            } catch (Exception e) {
                                this.refreshOnUpdate = ("");
                            }
                            try {
                                this.layoutColumns = Integer.parseInt(jsonObject.get("cols").toString());
//                                System.out.println("\nIMPOSTATO LAYOUT COLUMNS:" + this.layoutColumns);
                            } catch (Exception e) {
                            }

                            try {
                                this.updRowWhereClause = (jsonObject.get("updRowWhereClause").toString());
                            } catch (Exception e) {
                                this.updRowWhereClause = ("");
                            }
                            try {
                                this.showHeader = (jsonObject.get("showHeader").toString());
                            } catch (Exception e) {
                                this.showHeader = ("");
                            }
//                            System.out.println("\nRETREIVED showHeader:" + this.showHeader);
                            try {
                                this.showCounter = (jsonObject.get("showCounter").toString());
                            } catch (Exception e) {
                                this.showCounter = ("");
                            }

                            try {
                                this.advancedFiltered = (jsonObject.get("advancedFiltered").toString());
                            } catch (Exception e) {
                                this.advancedFiltered = ("");
                            }
                            try {
                                this.ges_autolinks = (jsonObject.get("autolinks").toString());
                                System.out.println("\nRETREIVED ges_autolinks:" + this.ges_autolinks);
                            } catch (Exception e) {
                                this.ges_autolinks = ("");
                            }

 
                        }
                    }
                }
            } catch (ParseException ex) {
                Logger.getLogger(ShowItForm.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public objRight analyzeRightsRuleJson(String rights, ResultSet rs, Connection Conny, int levelBase) {
        //  System.out.println("-----------------------------------");
// verifica i diritti per stringa genericaJSON
        int gotRight = 0;
        if (rights == null || rights.length() < 5) {
            gotRight = -1;
            objRight rowRights = new ShowItObject().createVoidRights();
            return rowRights;
        }
        //===================================================================        
// <editor-fold defaultstate="collapsed" desc="VALUTO EVENTUALE DISABILITAZIONE">           
        //      System.out.println("\n-----\nanalyzeRightsRuleJson:VALUTO I DIRITTI DELLA RIGA " + rights);

        if (rights.startsWith("DEFAULT")) {
            objRight rowRights = new ShowItObject().createVoidRights();
            EVOuser myUser = new EVOuser(myParams, mySettings);
            myUser.setTABLElinkUserGroups("archivio_correlazioni");
            myUser.setFIELDlinkUserGroupsRifOperatore("partAvalue");
            myUser.setFIELDlinkUserGroupsRifGruppo("partBvalue");

            myUser.setTABLEgruppi("archivio_operatoriGruppi");
            myUser.setFIELDGruppiIDgruppo("IDgruppo");
            myUser.setTABLEoperatori("archivio_operatori");
            myUser.setFIELDoperatoriID("ID");
            int dirittiComplessivi = myUser.getActualRightAdvanced(rights, null);
            rowRights.totalRight = 0;
            rowRights.level = 10;
            if (dirittiComplessivi <= 0) {
                rowRights.canView = 0;
                rowRights.level = levelBase;
            }
            if (dirittiComplessivi > 0) {
                rowRights.canView = 1;
                rowRights.totalRight += 1;
            }
            if (dirittiComplessivi > 1) {
                rowRights.canModify = 1;
                rowRights.canPushButton = 1;
                rowRights.totalRight += 2;
                rowRights.totalRight += 16;
            }
            if (dirittiComplessivi > 2) {
                rowRights.canCreate = 1;

                rowRights.totalRight += 8;
            }
            if (dirittiComplessivi > 3) {
                rowRights.canDelete = 1;

                rowRights.totalRight += 4;
            }
            if (dirittiComplessivi > 4) {
                rowRights.canEverything = 1;

                rowRights.totalRight += 32;
            }

            return rowRights;
        }
        String Xrights = "{\"rights\":" + rights + "}";

//        System.out.println("\nanalyzeRightsRuleJson:" + Xrights);
        JSONObject jsonObject = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        ArrayList<lockRule> lockRules = new ArrayList<lockRule>();
        int tempRight = 0;

        boolean limitUp = false;
        boolean limitDown = false;
        try {
            jsonObject = (JSONObject) jsonParser.parse(Xrights);
            String TRIGGERSarray = jsonObject.get("rights").toString();
            if (TRIGGERSarray != null && TRIGGERSarray.length() > 0) {
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(TRIGGERSarray);

                for (Object riga : array) {
                    lockRule myRule = new lockRule();
                    myRule.level = -1;
                    // System.out.println("\n RIGA: " + riga.toString());

                    jsonObject = (JSONObject) jsonParser.parse(riga.toString());

                    try {
                        myRule.setRuleType(jsonObject.get("ruleType").toString());
                    } catch (Exception e) {
                        myRule.setRuleType("");
                    }
                    try {
                        myRule.setTypeA(jsonObject.get("typeA").toString());
                    } catch (Exception e) {
                        myRule.setTypeA("");
                    }
                    try {
                        myRule.fieldA = jsonObject.get("fieldA").toString();
                    } catch (Exception e) {
                    }
                    try {
                        myRule.fieldTypeA = jsonObject.get("fieldTypeA").toString();
                    } catch (Exception e) {
                        myRule.fieldA = "";
                    }
                    try {
                        myRule.valueA = jsonObject.get("valueA").toString();
                    } catch (Exception e) {
                        myRule.valueA = "";
                    }
                    try {
                        myRule.typeB = jsonObject.get("typeB").toString();
                    } catch (Exception e) {
                        myRule.typeB = "";
                    }
                    try {
                        myRule.fieldB = jsonObject.get("fieldB").toString();
                    } catch (Exception e) {
                        myRule.fieldB = "";
                    }
                    try {
                        myRule.fieldTypeB = jsonObject.get("fieldTypeB").toString();
                    } catch (Exception e) {
                        myRule.fieldTypeB = "";
                    }
                    try {
                        myRule.valueB = jsonObject.get("valueB").toString();
                    } catch (Exception e) {
                        myRule.valueB = "";
                    }
                    try {
                        myRule.test = jsonObject.get("test").toString();
                    } catch (Exception e) {
                        myRule.test = "==";
                    }

                    try {
                        myRule.right = Integer.parseInt(jsonObject.get("right").toString());
                    } catch (Exception e) {
                        myRule.right = 1;
                    }
                    try {
                        myRule.level = Integer.parseInt(jsonObject.get("level").toString());
                    } catch (Exception e) {
                        myRule.level = -1;
                    }
                    try {
                        myRule.limitUp = jsonObject.get("limitUp").toString();
                    } catch (Exception e) {
                        myRule.limitUp = "false";
                    }
                    try {
                        myRule.limitDown = jsonObject.get("limitDown").toString();
                    } catch (Exception e) {
                        myRule.limitDown = "false";
                    }
                    /*
                     String tabCorrelazioni;
        String tabGruppi;
        String fieldIDinTabGruppi;
                     */
                    try {
                        myRule.tabCorrelazioni = jsonObject.get("tabCorrelazioni").toString();
                    } catch (Exception e) {
                        myRule.tabCorrelazioni = "archivio_correlazioni";
                    }
                    try {
                        myRule.tabGruppi = jsonObject.get("tabGruppi").toString();
                    } catch (Exception e) {
                        myRule.tabGruppi = "archivio_operatoriGruppi";
                    }
                    try {
                        myRule.fieldIDinTabGruppi = jsonObject.get("fieldIDinTabGruppi").toString();
                    } catch (Exception e) {
                        myRule.fieldIDinTabGruppi = "IDgruppo";
                    }
                    lockRules.add(myRule);

                }
            }
        } catch (ParseException ex) {
//            Logger.getLogger(ShowItForm.class
//                    .getName()).log(Level.SEVERE, null, ex);
            System.out.println("\nerror analyzeRightsRuleJson:" + Xrights);
        }

        int RuleLevel = 0;
        for (int jj = 0; jj < lockRules.size(); jj++) {

            int valIntA = 0;
            String valStringA = "";
            int valIntB = 0;
            String valStringB = "";
            lockRule myRule = new lockRule();
            myRule = lockRules.get(jj);
            if (myRule.level < 0) {
                myRule.level = 10;
            }
            /*
            TIPI AMMESSI:
            -DEFAULT
            -campareRSvalue
            -userInStandardGroup
            -userInCustomGroup
             */

//            System.out.println(jj + ") rTYPE :" + myRule.getRuleType() + " RIGHT:" + myRule.getRight() + " ->LEVEL: " + myRule.getLevel());
            if (myRule.getRuleType().equalsIgnoreCase("default")) {
                //VERIFICATA
                if (myRule.getLevel() > RuleLevel) {
                    //prevale myRule
                    tempRight = myRule.getRight();
                    RuleLevel = myRule.getLevel();
                } else if (myRule.getLevel() == RuleLevel) {
                    //prevale la più permissiva
                    if (tempRight < myRule.getRight()) {
                        tempRight = myRule.getRight();
                        RuleLevel = myRule.getLevel();
                    }
                } else {
                    // prevale tempRight
                }

            } else if (myRule.getRuleType().equalsIgnoreCase("campareRSvalue") && rs != null) {// caso di regola che confronta un valore di riga rs
//                 System.out.println("lockRules:" + jj + ")  " + myRule.getTypeA());
/*
[{"ruleType":"default","right":"31","limitUp":"true"}, 
{"ruleType":"campareRSvalue","typeA":"rowfield","fieldA":"ultimo","fieldTypeA":"INT","test":"==", "typeB":"value","valueB":"0","right":"25","level":"10"}]
                 */
                if (myRule.getTypeA().equalsIgnoreCase("rowfield")) {
                    // System.out.println("tipo rowfield. Cerco in field :" + myRule.getFieldA());
                    // cerco valore del field in rs
                    if (myRule.getFieldTypeA().equalsIgnoreCase("INT")) {
                        try {
                            valIntA = rs.getInt(myRule.getFieldA());

                        } catch (SQLException ex) {
//                            Logger.getLogger(ShowItForm.class
//                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            valStringA = rs.getString(myRule.getFieldA());

                        } catch (SQLException ex) {
//                            Logger.getLogger(ShowItForm.class
//                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } else if (myRule.getTypeA().equalsIgnoreCase("formfield")) {
//                    System.out.println("tipo rowfield. Cerco in TBS :" + myRule.getFieldA());
                    // cerco valore del field in TBS

                    CRUDorder myCRUD = new CRUDorder(myParams, mySettings);
                    myCRUD.setFatherKEYvalue(this.fatherKEYvalue);

                    myCRUD.setSendToCRUD(this.sendToCRUD);
                    String valFound = myCRUD.standardReplace("###" + myRule.getFieldA() + "###", null);
                    if (myRule.getFieldTypeA().equalsIgnoreCase("INT")) {
                        try {
                            valIntA = Integer.parseInt(valFound);
                        } catch (Exception e) {
                            System.out.println("ERROR 6821:[valFound:" + valFound + "] ->" + e.toString());
                        }
                    } else {
                        valStringA = valFound;
                    }

                }

                if (myRule.getTypeB().equalsIgnoreCase("value")) {
                    // cerco valore del field in rs
                    if (myRule.getFieldTypeA().equalsIgnoreCase("INT")) {
                        try {
                            valIntB = Integer.parseInt(myRule.getValueB());
                            //  System.out.println("CONFRONTO-> rowfield:valIntA = " + valIntA + "  valIntB = " + valIntB + "  myRule.getRight() = " + myRule.getRight());
                            //-----
                            if (valIntA == valIntB) {
                                //VERIFICATA
                                if (myRule.getLevel() > RuleLevel) {
                                    //prevale myRule
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                } else if (myRule.getLevel() == RuleLevel) {
                                    //prevale la più permissiva
                                    if (tempRight < myRule.getRight()) {
                                        tempRight = myRule.getRight();
                                        RuleLevel = myRule.getLevel();
                                    }
                                } else {
                                    // prevale tempRight
                                }
                            }

                            //-----
                        } catch (Exception ex) {

                        }
                    } else {
                        try {
                            valStringB = myRule.getFieldB();
                            if (valStringB.equals(valStringA)) {
                                //VERIFICATA
                                if (myRule.getLevel() > RuleLevel) {
                                    //prevale myRule
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                } else if (myRule.getLevel() == RuleLevel) {
                                    //prevale la più permissiva
                                    if (tempRight < myRule.getRight()) {
                                        tempRight = myRule.getRight();
                                        RuleLevel = myRule.getLevel();
                                    }
                                } else {
                                    // prevale tempRight
                                }
                            }

                        } catch (Exception ex) {

                        }
                    }

                }
            } else if (myRule.getRuleType().equalsIgnoreCase("campareTBSvalue") && rs != null) {// caso di regola che confronta un valore di riga rs
//                 System.out.println("lockRules:" + jj + ")  " + myRule.getTypeA());

                if (myRule.getTypeA().equalsIgnoreCase("rowfield")) {
                    // System.out.println("tipo rowfield. Cerco in field :" + myRule.getFieldA());
                    // cerco valore del field in rs
                    if (myRule.getFieldTypeA().equalsIgnoreCase("INT")) {
                        try {
                            valIntA = rs.getInt(myRule.getFieldA());

                        } catch (SQLException ex) {
//                            Logger.getLogger(ShowItForm.class
//                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            valStringA = rs.getString(myRule.getFieldA());

                        } catch (SQLException ex) {
//                            Logger.getLogger(ShowItForm.class
//                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }

                if (myRule.getTypeB().equalsIgnoreCase("value")) {
                    // cerco valore del field in rs
                    if (myRule.getFieldTypeA().equalsIgnoreCase("INT")) {
                        try {
                            valIntB = Integer.parseInt(myRule.getValueB());
                            //  System.out.println("CONFRONTO-> rowfield:valIntA = " + valIntA + "  valIntB = " + valIntB + "  myRule.getRight() = " + myRule.getRight());
                            //-----
                            if (valIntA == valIntB) {
                                //VERIFICATA
                                if (myRule.getLevel() > RuleLevel) {
                                    //prevale myRule
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                } else if (myRule.getLevel() == RuleLevel) {
                                    //prevale la più permissiva
                                    if (tempRight < myRule.getRight()) {
                                        tempRight = myRule.getRight();
                                        RuleLevel = myRule.getLevel();
                                    }
                                } else {
                                    // prevale tempRight
                                }
                            }

                            //-----
                        } catch (Exception ex) {

                        }
                    } else {
                        try {
                            valStringB = myRule.getFieldB();
                            if (valStringB.equals(valStringA)) {
                                //VERIFICATA
                                if (myRule.getLevel() > RuleLevel) {
                                    //prevale myRule
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                } else if (myRule.getLevel() == RuleLevel) {
                                    //prevale la più permissiva
                                    if (tempRight < myRule.getRight()) {
                                        tempRight = myRule.getRight();
                                        RuleLevel = myRule.getLevel();
                                    }
                                } else {
                                    // prevale tempRight
                                }
                            }

                        } catch (Exception ex) {

                        }
                    }

                }

            } else if (myRule.getRuleType().equalsIgnoreCase("userInStandardGroup")) {//verifico se l'utente appartiene ad un gruppo
                //{"ruleType":"userInStandardGroup","test":"==", "valueB":"MEDICI","right":"17","level":"10"}
                EVOuser myUser = new EVOuser(myParams, mySettings);
                myUser.setTABLElinkUserGroups("archivio_correlazioni");
                myUser.setFIELDlinkUserGroupsRifOperatore("partAvalue");
                myUser.setFIELDlinkUserGroupsRifGruppo("partBvalue");

                myUser.setTABLEgruppi("archivio_operatoriGruppi");
                myUser.setFIELDGruppiIDgruppo("IDgruppo");
                myUser.setTABLEoperatori("archivio_operatori");
                myUser.setFIELDoperatoriID("ID");

                int diritto = -1;
                String rgt = myRule.getValueB() + ":" + myRule.getRight();
                if (Conny == null) { //analisi singola voce con apertura e chiusura connection
                    diritto = myUser.getActualRightAdvanced(rgt, null);
                } else {// analisi voci multiple con connection fornita dalla routine madre
                    diritto = myUser.getActualRightAdvanced(rgt, null, Conny);

                }

//                System.out.println("userInStandardGroup:" + myRule.getValueB() + "-->" + diritto);
                if (diritto > -1) {
                    //VERIFICATA
                    if (myRule.getLevel() > RuleLevel) {
                        //prevale myRule
                        tempRight = myRule.getRight();
                        RuleLevel = myRule.getLevel();
                    } else if (myRule.getLevel() == RuleLevel) {
                        //prevale la più permissiva
                        if (tempRight < myRule.getRight()) {
                            tempRight = myRule.getRight();
                            RuleLevel = myRule.getLevel();
                        }
                    } else {
                        // prevale tempRight
                    }

                }
            } else if (myRule.getRuleType().equalsIgnoreCase("userInCustomGroup")) {//verifico se l'utente appartiene ad un gruppo
                //{"ruleType":"userInStandardGroup","test":"==", "valueB":"MEDICI","right":"17","level":"10"}
                EVOuser myUser = new EVOuser(myParams, mySettings);

                /*
                     String tabCorrelazioni;
        String tabGruppi;
        String fieldIDinTabGruppi;
                 */
                myUser.setTABLElinkUserGroups(myRule.getTabCorrelazioni());//**--

                myUser.setFIELDlinkUserGroupsRifOperatore("partAvalue");
                myUser.setFIELDlinkUserGroupsRifGruppo("partBvalue");

                myUser.setTABLEgruppi(myRule.getTabGruppi());//**--
                myUser.setFIELDGruppiIDgruppo(myRule.getFieldIDinTabGruppi());//**--
                myUser.setTABLEoperatori("archivio_operatori");
                myUser.setFIELDoperatoriID("ID");

                int diritto = -1;
                String rgt = myRule.getValueB() + ":" + myRule.getRight();
                if (Conny == null) { //analisi singola voce con apertura e chiusura connection
                    diritto = myUser.getActualRightAdvanced(rgt, null);
                } else {// analisi voci multiple con connection fornita dalla routine madre
                    diritto = myUser.getActualRightAdvanced(rgt, null, Conny);

                }

//                System.out.println("userInStandardGroup:" + myRule.getValueB() + "-->" + diritto);
                if (diritto > -1) {
                    //VERIFICATA
                    if (myRule.getLevel() > RuleLevel) {
                        //prevale myRule
                        tempRight = myRule.getRight();
                        RuleLevel = myRule.getLevel();
                    } else if (myRule.getLevel() == RuleLevel) {
                        //prevale la più permissiva
                        if (tempRight < myRule.getRight()) {
                            tempRight = myRule.getRight();
                            RuleLevel = myRule.getLevel();
                        }
                    } else {
                        // prevale tempRight
                    }

                }
            } else if (myRule.getRuleType().equalsIgnoreCase("userInOldGroup")) {//verifico se l'utente appartiene ad un gruppo
                //{"ruleType":"userInOldGroup","test":"==", "valueB":"MEDICI","right":"17","level":"10"}
                EVOuser myUser = new EVOuser(myParams, mySettings);
                int diritto = -1;
                String rgt = myRule.getValueB() + ":" + myRule.getRight();
                if (Conny == null) { //analisi singola voce con apertura e chiusura connection
                    diritto = myUser.getActualRight(rgt, null);
                } else {// analisi voci multiple con connection fornita dalla routine madre
                    diritto = myUser.getActualRight(rgt, null, Conny);

                }

//                System.out.println("userInGroup:" + myRule.getValueB() + "-->" + diritto);
                if (diritto > -1) {
                    //VERIFICATA
                    if (myRule.getLevel() > RuleLevel) {
                        //prevale myRule
                        tempRight = myRule.getRight();
                        RuleLevel = myRule.getLevel();
                    } else if (myRule.getLevel() == RuleLevel) {
                        //prevale la più permissiva
                        if (tempRight < myRule.getRight()) {
                            tempRight = myRule.getRight();
                            RuleLevel = myRule.getLevel();
                        }
                    } else {
                        // prevale tempRight
                    }

                }
            }

        }
        objRight rowRights = new ShowItObject().createNewRights(tempRight);
        rowRights.level = RuleLevel;
//        System.out.println(rights + "\n analyzeRightsRuleJson ->genera:" + tempRight + " LEVEL: " + rowRights.level);

//        System.out.println("level*********" + rowRights.level);
//        System.out.println("1.canView***************" + rowRights.canView);
//        System.out.println("2.canModify*************" + rowRights.canModify);
//        System.out.println("4.canDelete*************" + rowRights.canDelete);
//        System.out.println("8.canCreate*************" + rowRights.canCreate);
//        System.out.println("16.canPushButton*********" + rowRights.canPushButton);
//        System.out.println("128.canEverything*********" + rowRights.canEverything);
        return rowRights;
    }

    public objRight joinRights(objRight Arights, objRight Brights) {
        Arights.evaluateRights();
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

        objRight realRights = new ShowItObject().createNewRights(newPerm);
        realRights.level = newLevel;
        realRights.evaluateRights();
//        System.out.println("JOIN A:" + Arights.totalRight + " LEVEL:" + Arights.level);
//        System.out.println("JOIN B:" + Brights.totalRight + " LEVEL:" + Brights.level);
//        System.out.println("RISULTATO::" + realRights.totalRight + " LEVEL:" + realRights.level);

        return realRights;

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
//////
//////    public class objRight {
//////
//////        int level;
//////        int totalRight;
//////        int canView;//1
//////        int canModify;//2
//////        int canDelete;//4
//////        int canCreate;//8
//////        int canPushButton;//16
//////        int canEverything;//128
//////        String Type; // se i diritti sono in una riga di add lo devo sapere per modificarli di conseguenza
//////
//////        public void print() {
//////            System.out.println("level*********" + this.level);
//////            System.out.println("1.canView***************" + this.canView);
//////            System.out.println("2.canModify*************" + this.canModify);
//////            System.out.println("4.canDelete*************" + this.canDelete);
//////            System.out.println("8.canCreate*************" + this.canCreate);
//////            System.out.println("16.canPushButton*********" + this.canPushButton);
//////            System.out.println("128.canEverything*********" + this.canEverything);
//////        }
//////
//////        public objRight(int totalRight) {
//////            this.totalRight = totalRight;
//////            //System.out.println("ANALIZZO RIGHTS:" + totalRight);
//////            evaluateRights();
//////
//////        }
//////
//////        private void evaluateRights() {
//////            int tr = totalRight;
//////            if (tr < 0) {
//////                canEverything = -1;
//////                canPushButton = -1;
//////                canCreate = -1;
//////                canDelete = -1;
//////                canModify = -1;
//////                canView = -1;
//////
//////                return;
//////            }
//////            if ((tr - 128) >= 0) {
//////                canEverything = 1;
//////                tr = tr - 128;
//////                //     System.out.println("  RIGHTS residui :" + tr);
//////            } else {
//////                canEverything = 0;
//////            }
//////            if ((tr - 16) >= 0) {
//////                canPushButton = 1;
//////                tr = tr - 16;
//////                //      System.out.println("  RIGHTS residui :" + tr);
//////            } else {
//////                canPushButton = 0;
//////            }
//////            if ((tr - 8) >= 0) {
//////                canCreate = 1;
//////                tr = tr - 8;
//////                //     System.out.println("  RIGHTS residui :" + tr);
//////            } else {
//////                canCreate = 0;
//////            }
//////            if ((tr - 4) >= 0) {
//////                canDelete = 1;
//////                tr = tr - 4;
//////                //       System.out.println("  RIGHTS residui :" + tr);
//////            } else {
//////                canDelete = 0;
//////            }
//////            if ((tr - 2) >= 0) {
//////                canModify = 1;
//////                tr = tr - 2;
//////                //       System.out.println("  RIGHTS residui :" + tr);
//////            } else {
//////                canModify = 0;
//////            }
//////            if ((tr - 1) >= 0) {
//////                canView = 1;
//////                tr = tr - 1;
//////                //     System.out.println("  RIGHTS residui :" + tr);
//////            } else {
//////                canView = 0;
//////            }
//////        }
//////
//////    }

    public String feedTriggeredStyle(ShowItObject curObj, ResultSet rs) {
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
                            System.out.println("IMPOSTO TRIGGERED STYLE : " + triggeredStyle);
                        } else {
                            //triggeredStyle = "";
                        }

                    } else if (AnalyzedObjType.equalsIgnoreCase("defaultStyle")) {
                        // prendo style...    [{"AnalyzedObjType":"defaultStyle","style":"background-color:grey; " } ]                    
                        curObj.C.setDefaultStyle(style);
                        triggeredStyle += (style);
                        System.out.println(" TRIGGERED STYLE : " + triggeredStyle);
                    }
                }
            }
        } catch (ParseException ex) {
            System.out.println("error in line 2082");
            Logger
                    .getLogger(ShowItForm.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return triggeredStyle;
    }

    private String getStyleHtmlCode(ShowItObject curObj, String KEYvalue) {

        //---S T Y L E -------------------------------------  
        String htmlCode = "style =\" ";
        if (KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")
                && (curObj.AddingRow_enabled < 1)) {
            htmlCode += " background-color:grey; ";
        } else if (KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")
                && (curObj.AddingRow_enabled > 0)) {
            htmlCode += " background-color:yellow; ";
        } else {
            String tempStyle = "";

            //2.0 default style
            if (curObj.C.getDefaultStyle() != null && curObj.C.getDefaultStyle().length() > 0) {
                tempStyle = curObj.C.getDefaultStyle();
                if (curObj.C.getConditionalBackColor() != null && curObj.C.getConditionalBackColor().length() > 0) {
                    tempStyle += " background-color:" + curObj.C.getConditionalBackColor() + "; \" ";
                }
            } else {
                //1. colore sfondo standard
                tempStyle = " background-color:white;  ";
                if (curObj.C.getConditionalBackColor() != null && curObj.C.getConditionalBackColor().length() > 0) {
                    tempStyle = " background-color:" + curObj.C.getConditionalBackColor() + "; \" ";
                }
            }
            //3.0 conditional

            htmlCode += tempStyle;

        }
        //4. chiudo lo style
        htmlCode += "\" ";
        //--------------------------------------------------   
        return htmlCode;
    }

    public String getGroupsList(ShowItObject curObj, String KEYvalue) {
        String params = curObj.CG.getParams();
        System.out.println("\n\ngetGroupsList params:" + params);
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
        ArrayList<SelectListLine> myCheckedLines = new ArrayList<SelectListLine>();

        CRUDorder myCRUD = new CRUDorder(myParams, mySettings);
        myCRUD.setFatherKEYvalue(this.getFatherKEYvalue());
        myCRUD.setSendToCRUD(this.sendToCRUD);
        String SQLphrase = myCRUD.standardReplace(myLinker.getPartBquery(), null);

        // String SQLphrase = partBquery;
        //   System.out.println("SQLphrase partBquery ---------->" + SQLphrase);
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        PreparedStatement ps = null;
        ResultSet rs;

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
                        + "AND partAvalue = '" + KEYvalue + "' "
                        + "AND partBtab = '" + myLinker.getPartBtab() + "' "
                        + "AND partBvalueField = '" + myLinker.getPartBvalueField() + "' "
                        + "AND partBvalue = '" + myLines.get(jj).getValue() + "' ";
                System.out.println("CERCO CHECK ---------->" + SQLphrase);
                myLines.get(jj).setChecked(0);
                ps = conny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();
                while (rs.next()) {
                    myCheckedLines.add(myLines.get(jj));
                    break;
                }
            }

            conny.close();
        } catch (SQLException ex) {

        }

        String HtmlCode = "";
        HtmlCode += "<TABLE>";
        if (myCheckedLines.size() < 1) {
            HtmlCode += "<TR><TD>- N.D. -</TD></TR>";
        } else {
            for (int jj = 0; jj < myCheckedLines.size(); jj++) {

                HtmlCode += "<TR><TD>";
                HtmlCode += myCheckedLines.get(jj).getLabel();
                HtmlCode += " </TD></TR>";
            }
        }
        HtmlCode += "</TABLE>";
        //       System.out.println("CHECK HtmlCode ---------->" + HtmlCode);

        return HtmlCode;
    }

    public String getBGcolor(String rowBGrules, ResultSet rs) {
        String ValueAssigned = "";
        if (rowBGrules == null || rowBGrules.length() < 5) {
            this.setActualRowGBcolor("WHITE");
        } else {
            String[] blocks = rowBGrules.split(";");
            List<String> block = Arrays.asList(blocks);
            if (block.size() > 1) {
                for (int bb = 0; bb < block.size(); bb++) {
                    String valore = "";
                    String condizione = "";
                    String[] couples = block.get(bb).split(":");
                    List<String> param = Arrays.asList(couples);
                    if (param.size() > 1) {
                        // condizione = valore
                        condizione = param.get(0);
                        valore = param.get(1);
                        // analizzo la condizione
                        String[] terms = condizione.split("=");
                        List<String> term = Arrays.asList(terms);
                        if (term.size() > 1) {
                            String part1 = term.get(0);
                            String part2 = term.get(1);

                            String field = part1.replace("[", "");
                            field = field.replace("]", "");
                            String rawType = "text";
                            int rawValue = 0;
                            ValueAssigned = "";
                            String rawString = "";
                            // System.out.println("BG COLOR: " + part1 + "=" + part2 + " --->" + valore);

                            for (int gg = 0; gg < this.objects.size(); gg++) {

                                if (this.objects.get(gg).getName().equalsIgnoreCase(field)) {
                                    rawType = this.objects.get(gg).Content.getType();
                                    if (rawType != null && rawType.equalsIgnoreCase("INT")) {
                                        int number = 0;
                                        try {
                                            number = rs.getInt(field);
                                        } catch (SQLException ex) {
                                            System.out.println("error in line 3596");
                                            Logger
                                                    .getLogger(ShowItForm.class
                                                            .getName()).log(Level.SEVERE, null, ex);
                                        }
                                        rawString = "" + number;
                                        // sto analizzando il valore del campo che ha come nome [part1]
                                        // rilevato valore numerico... quindi lo confronterò con
                                        // part 2 trasformato in numero
                                        int number2 = Integer.parseInt(part2);
                                        // eseguo il confronto caso NUMERO
                                        if (number == number2) {
                                            ValueAssigned = valore;
                                        }

                                    } else {
                                        String text = "";
                                        try {
                                            text = rs.getString(field);
                                        } catch (Exception ex) {
                                            text = "";
                                        }
                                        if (text == null || text.equalsIgnoreCase("null")) {
                                            text = "";
                                        }
                                        rawString = "" + text;

                                        // eseguo il confronto caso TEXT
                                        if (rawString.equalsIgnoreCase(part2)) {
                                            ValueAssigned = valore;
                                        }

                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        return ValueAssigned;
    }

}
