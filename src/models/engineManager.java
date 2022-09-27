/*
 * SOFTWARE BY FFS RELEASED UNDER AGPL LICENSE.
 * REFER TO WWW.FFS.IT AND INFO@FFS.IT FOR INFO.
 * Author: Franco Venezia
 * 
 * Copyright (C) <2019>  <Franco Venezia @ ffs.it>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package models;

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.EVOpagerDBconnection;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Franco Venezia @ www.ffs.it
 */
public class engineManager {

    Settings mySettings;
    EVOpagerParams myParams;

    public engineManager(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
    }

    public void makeEngineFile(String databaseID) {

        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        PreparedStatement ps = null;
        ResultSet rs;
        database myDatabase = new database();

        String SQLphrase = "SELECT * FROM `databases` WHERE `ID` = '" + databaseID + "'";
        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();

            while (rs.next()) {
                myDatabase.setName(rs.getString("name"));
                break;
            }

        } catch (SQLException ex) {

        }

        //=======================================      
        String database = myDatabase.getName();
        System.out.println(" found database: " + database);
//        ArrayList<Table> Tables = new ArrayList<Table>();
//        SQLphrase = "SELECT * FROM tables WHERE rifDatabase = '" + databaseID + "' AND alive>0";
//        System.out.println("SQLphrase: " + SQLphrase);
//        try {
//            ps = conny.prepareStatement(SQLphrase);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//                Table myTable = new Table();
//                try {
//                    myTable.setID(rs.getString("ID"));
//                    myTable.setName(rs.getString("name"));
//                    myTable.setRifDatabase(database);
//                    myTable.setOperatore(myParams.getCKuserID());
//                    Tables.add(myTable);
//                } catch (Exception e) {
//                    System.out.println(" ERROR 182: " + e.toString());
//                }
//            }
//
//        } catch (SQLException ex) {
//
//        }
        try {
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(engineManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class Project {

        /*
            CREATE TABLE `projects` (
          `ID` varchar(64) NOT NULL DEFAULT '',
          `name` varchar(64) DEFAULT NULL,
          `version` int(11) DEFAULT NULL,
          `release` int(11) DEFAULT NULL,
          `minSWrelease` int(11) DEFAULT '0',
          `recorded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
          `operatore` varchar(64) DEFAULT NULL,
            `alive` int(11) DEFAULT '1'
            ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
         */
        String ID;
        String name;
        int version;
        int release;
        int minSWrelease;
        String recorded;
        String operatore;
        int alive;
        ArrayList<database> Databases;
        ArrayList<gEVO_directives> EVO_directives;

        public Project() {
            this.Databases = new ArrayList<>();
            this.EVO_directives = new ArrayList<>();

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

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public int getRelease() {
            return release;
        }

        public void setRelease(int release) {
            this.release = release;
        }

        public int getMinSWrelease() {
            return minSWrelease;
        }

        public void setMinSWrelease(int minSWrelease) {
            this.minSWrelease = minSWrelease;
        }

        public String getRecorded() {
            return recorded;
        }

        public void setRecorded(String recorded) {
            this.recorded = recorded;
        }

        public String getOperatore() {
            return operatore;
        }

        public void setOperatore(String operatore) {
            this.operatore = operatore;
        }

        public int getAlive() {
            return alive;
        }

        public void setAlive(int alive) {
            this.alive = alive;
        }

        public ArrayList<database> getDatabases() {
            return Databases;
        }

        public void setDatabases(ArrayList<database> Databases) {
            this.Databases = Databases;
        }

        public ArrayList<gEVO_directives> getEVO_directives() {
            return EVO_directives;
        }

        public void setEVO_directives(ArrayList<gEVO_directives> EVO_directives) {
            this.EVO_directives = EVO_directives;
        }

    }

    class database {

        /*
                CREATE TABLE `databases` (
              `ID` varchar(64) NOT NULL DEFAULT '',
              `name` varchar(256) DEFAULT NULL,
              `rifProject` varchar(128) DEFAULT NULL,
              `recorded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
              `operatore` varchar(64) DEFAULT NULL,
              `alive` int(11) DEFAULT '1',
              `version` int(11) DEFAULT '1',
              `release` int(11) DEFAULT '1',
              `minSWrelease` int(11) DEFAULT '1'
            ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
         */
        String ID;
        String name;
        String rifProject;
        String recorded;
        String operatore;
        int alive;
        int version;
        int release;
        int minSWrelease;

        ArrayList<gFEscreen> Screens;
        ArrayList<gFE_reports> Reports;
        ArrayList<gFE_directives> Directives;

        public database() {
            this.Screens = new ArrayList<>();
            this.Reports = new ArrayList<>();
            this.Directives = new ArrayList<>();

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

        public String getRecorded() {
            return recorded;
        }

        public void setRecorded(String recorded) {
            this.recorded = recorded;
        }

        public String getOperatore() {
            return operatore;
        }

        public void setOperatore(String operatore) {
            this.operatore = operatore;
        }

        public int getAlive() {
            return alive;
        }

        public void setAlive(int alive) {
            this.alive = alive;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public int getRelease() {
            return release;
        }

        public void setRelease(int release) {
            this.release = release;
        }

        public int getMinSWrelease() {
            return minSWrelease;
        }

        public void setMinSWrelease(int minSWrelease) {
            this.minSWrelease = minSWrelease;
        }

        public ArrayList<gFEscreen> getScreens() {
            return Screens;
        }

        public void setScreens(ArrayList<gFEscreen> Screens) {
            this.Screens = Screens;
        }

        public ArrayList<gFE_reports> getReports() {
            return Reports;
        }

        public void setReports(ArrayList<gFE_reports> Reports) {
            this.Reports = Reports;
        }

        public ArrayList<gFE_directives> getDirectives() {
            return Directives;
        }

        public void setDirectives(ArrayList<gFE_directives> Directives) {
            this.Directives = Directives;
        }

    }

    class gFEscreen {

        /*
                    CREATE TABLE `gFE_screens` (
              `ID` varchar(64) NOT NULL DEFAULT '',
              `name` varchar(64) DEFAULT NULL,
              `rifDatabase` varchar(64) DEFAULT NULL,
              `rifVersion` varchar(64) DEFAULT NULL,
              `version` int(11) DEFAULT '1',
              `distro` varchar(1) DEFAULT 'B',
              `alive` int(11) DEFAULT '1',
              `operatore` varchar(256) DEFAULT NULL,
              `position` int(11) DEFAULT '1'
            ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
         */
        String ID;
        String name;
        String rifDatabase;
        String rifVersion;
        int version;
        String distro;
        int alive;
        String operatore;
        int position;
        String replicaID;

        ArrayList<gFEframe> frames;

        public gFEscreen() {
            this.frames = new ArrayList<>();
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

        public String getRifDatabase() {
            return rifDatabase;
        }

        public void setRifDatabase(String rifDatabase) {
            this.rifDatabase = rifDatabase;
        }

        public String getRifVersion() {
            return rifVersion;
        }

        public void setRifVersion(String rifVersion) {
            this.rifVersion = rifVersion;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getDistro() {
            return distro;
        }

        public void setDistro(String distro) {
            this.distro = distro;
        }

        public int getAlive() {
            return alive;
        }

        public void setAlive(int alive) {
            this.alive = alive;
        }

        public String getOperatore() {
            return operatore;
        }

        public void setOperatore(String operatore) {
            this.operatore = operatore;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getReplicaID() {
            return replicaID;
        }

        public void setReplicaID(String replicaID) {
            this.replicaID = replicaID;
        }

        public ArrayList<gFEframe> getFrames() {
            return frames;
        }

        public void setFrames(ArrayList<gFEframe> frames) {
            this.frames = frames;
        }

    }

    class gFEframe {

        /*
        
CREATE TABLE `gFE_frames` (
  `ID` varchar(64) NOT NULL DEFAULT '',
  `Name` varchar(64) DEFAULT NULL,
  `rifDatabase` varchar(64) DEFAULT NULL,
  `rifVersion` varchar(64) DEFAULT NULL,
  `rifScreen` varchar(64) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `position` int(11) DEFAULT '10',
  `size` int(11) DEFAULT '1',
  `sizeType` varchar(16) DEFAULT 'PIXEL',
  `alive` int(11) DEFAULT '1',
  `operatore` varchar(256) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
         */
        String ID;
        String Name;
        String rifDatabase;
        String rifVersion;
        String rifScreen;
        String type;
        int position;
        int size;
        int sizeType;
        int alive;
        String operatore;
        String replicaID;
        ArrayList<gFEform> Forms;

        public gFEframe() {
            this.Forms = new ArrayList<>();
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getRifDatabase() {
            return rifDatabase;
        }

        public void setRifDatabase(String rifDatabase) {
            this.rifDatabase = rifDatabase;
        }

        public String getRifVersion() {
            return rifVersion;
        }

        public void setRifVersion(String rifVersion) {
            this.rifVersion = rifVersion;
        }

        public String getRifScreen() {
            return rifScreen;
        }

        public void setRifScreen(String rifScreen) {
            this.rifScreen = rifScreen;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getSizeType() {
            return sizeType;
        }

        public void setSizeType(int sizeType) {
            this.sizeType = sizeType;
        }

        public int getAlive() {
            return alive;
        }

        public void setAlive(int alive) {
            this.alive = alive;
        }

        public String getOperatore() {
            return operatore;
        }

        public void setOperatore(String operatore) {
            this.operatore = operatore;
        }

        public String getReplicaID() {
            return replicaID;
        }

        public void setReplicaID(String replicaID) {
            this.replicaID = replicaID;
        }

        public ArrayList<gFEform> getForms() {
            return Forms;
        }

        public void setForms(ArrayList<gFEform> Forms) {
            this.Forms = Forms;
        }

    }

    class gFEform {

        /*
        

CREATE TABLE `gFE_forms` (
  `ID` varchar(64) NOT NULL DEFAULT '',
  `name` varchar(64) DEFAULT NULL,
  `rifDatabase` varchar(64) DEFAULT NULL,
  `rifVersion` varchar(64) DEFAULT NULL,
  `rifFrame` varchar(64) DEFAULT NULL,
  `serverURL` varchar(256) DEFAULT NULL,
  `databaseID` varchar(64) DEFAULT NULL,
  `query` text,
  `mainTable` text,
  `position` int(11) DEFAULT '10',
  `type` varchar(64) DEFAULT NULL,
  `filteredElements` text,
  `height` varchar(64) DEFAULT NULL,
  `width` varchar(64) DEFAULT NULL,
  `userRights` text,
  `hasSearchFilter` int(11) DEFAULT '0',
  `toBeSent` text,
  `Label` varchar(256) DEFAULT NULL,
  `disableRules` text,
  `htmlPattern` text,
  `rowBGcolor` text,
  `ges_size` text,
  `ges_topBar` text,
  `ges_background` text,
  `ges_formPanel` text,
  `ges_routineOnLoad` text,
  `ges_routineAfterLoad` text,
  `picture` blob,
  `rightCols` int(11) DEFAULT '0',
  `leftCols` int(11) DEFAULT '0',
  `bottomSpaces` int(11) DEFAULT '0',
  `topSpaces` int(11) DEFAULT '0',
  `father` varchar(64) DEFAULT NULL,
  `fatherFilters` text,
  `operatore` varchar(256) DEFAULT NULL,
  `recorded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `alive` int(11) DEFAULT '1'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

         */
        String ID;
        String name;
        String rifDatabase;
        String rifVersion;
        String rifFrame;
        String serverURL;
        String databaseID;
        String query;
        String mainTable;
        int position;
        String type;
        String filteredElements;
        String height;
        String width;
        String userRights;
        int hasSearchFilter;
        String toBeSent;
        String Label;
        String disableRules;
        String htmlPattern;
        String rowBGcolor;
        String ges_size;
        String ges_topBar;
        String ges_background;
        String ges_formPanel;
        String ges_routineOnLoad;
        String ges_routineAfterLoad;
        Blob picture;
        int rightCols;
        int leftCols;
        int bottomSpaces;
        int topSpaces;
        String father;
        String fatherFilters;
        String operatore;
        String recorded;
        int alive;
        String replicaID;
        ArrayList<gFEobject> objects;
        ArrayList<gFEformChildhood> childhoods;

        public gFEform() {
            this.objects = new ArrayList<>();
            this.childhoods = new ArrayList<>();
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

        public String getRifDatabase() {
            return rifDatabase;
        }

        public void setRifDatabase(String rifDatabase) {
            this.rifDatabase = rifDatabase;
        }

        public String getRifVersion() {
            return rifVersion;
        }

        public void setRifVersion(String rifVersion) {
            this.rifVersion = rifVersion;
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

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFilteredElements() {
            return filteredElements;
        }

        public void setFilteredElements(String filteredElements) {
            this.filteredElements = filteredElements;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getUserRights() {
            return userRights;
        }

        public void setUserRights(String userRights) {
            this.userRights = userRights;
        }

        public int getHasSearchFilter() {
            return hasSearchFilter;
        }

        public void setHasSearchFilter(int hasSearchFilter) {
            this.hasSearchFilter = hasSearchFilter;
        }

        public String getToBeSent() {
            return toBeSent;
        }

        public void setToBeSent(String toBeSent) {
            this.toBeSent = toBeSent;
        }

        public String getLabel() {
            return Label;
        }

        public void setLabel(String Label) {
            this.Label = Label;
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

        public String getRowBGcolor() {
            return rowBGcolor;
        }

        public void setRowBGcolor(String rowBGcolor) {
            this.rowBGcolor = rowBGcolor;
        }

        public String getGes_size() {
            return ges_size;
        }

        public void setGes_size(String ges_size) {
            this.ges_size = ges_size;
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

        public String getGes_routineAfterLoad() {
            return ges_routineAfterLoad;
        }

        public void setGes_routineAfterLoad(String ges_routineAfterLoad) {
            this.ges_routineAfterLoad = ges_routineAfterLoad;
        }

        public Blob getPicture() {
            return picture;
        }

        public void setPicture(Blob picture) {
            this.picture = picture;
        }

        public int getRightCols() {
            return rightCols;
        }

        public void setRightCols(int rightCols) {
            this.rightCols = rightCols;
        }

        public int getLeftCols() {
            return leftCols;
        }

        public void setLeftCols(int leftCols) {
            this.leftCols = leftCols;
        }

        public int getBottomSpaces() {
            return bottomSpaces;
        }

        public void setBottomSpaces(int bottomSpaces) {
            this.bottomSpaces = bottomSpaces;
        }

        public int getTopSpaces() {
            return topSpaces;
        }

        public void setTopSpaces(int topSpaces) {
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

        public String getOperatore() {
            return operatore;
        }

        public void setOperatore(String operatore) {
            this.operatore = operatore;
        }

        public String getRecorded() {
            return recorded;
        }

        public void setRecorded(String recorded) {
            this.recorded = recorded;
        }

        public int getAlive() {
            return alive;
        }

        public void setAlive(int alive) {
            this.alive = alive;
        }

        public String getReplicaID() {
            return replicaID;
        }

        public void setReplicaID(String replicaID) {
            this.replicaID = replicaID;
        }

        public ArrayList<gFEobject> getObjects() {
            return objects;
        }

        public void setObjects(ArrayList<gFEobject> objects) {
            this.objects = objects;
        }

        public ArrayList<gFEformChildhood> getChildhoods() {
            return childhoods;
        }

        public void setChildhoods(ArrayList<gFEformChildhood> childhoods) {
            this.childhoods = childhoods;
        }

    }

    class gFEobject {

        /*
        

            CREATE TABLE `gFE_objects` (
              `ID` varchar(64) NOT NULL DEFAULT '',
              `name` varchar(64) DEFAULT NULL,
              `rifDatabase` varchar(64) DEFAULT NULL,
              `rifVersion` varchar(64) DEFAULT NULL,
              `rifForm` varchar(64) DEFAULT NULL,
              `position` int(11) DEFAULT '10',
              `labelHeader` varchar(64) DEFAULT NULL,
              `CGtype` varchar(32) DEFAULT 'FIELD',
              `CGparams` text,
              `CGvalue` text,
              `contentType` varchar(32) DEFAULT NULL,
              `defaultValue` varchar(128) DEFAULT NULL,
              `containerType` varchar(32) DEFAULT 'LABEL',
              `containerClass` varchar(256) DEFAULT NULL,
              `containerWidth` varchar(32) DEFAULT NULL,
              `containerHeight` varchar(32) DEFAULT NULL,
              `AddingRow_enabled` int(11) DEFAULT '0',
              `AddingRow_params` text,
              `originQuery` text,
              `originLabelField` varchar(256) DEFAULT NULL,
              `originValueField` varchar(256) DEFAULT NULL,
              `originValueFieldType` varchar(64) DEFAULT NULL,
              `userRights` text,
              `actionPerformed` varchar(64) DEFAULT NULL,
              `actionParams` text,
              `visible` varchar(256) DEFAULT 'DEFAULT:TRUE',
              `routineOnChange` text,
              `hasSum` int(11) DEFAULT '0',
              `modifiable` text,
              `alive` int(11) DEFAULT '1',
              `operatore` varchar(256) DEFAULT NULL,
              `picture` blob,
              `ges_triggers` text
            ) ENGINE=MyISAM DEFAULT CHARSET=utf8;

         */
        String ID;
        String name;
        String rifDatabase;
        String rifVersion;
        String rifForm;
        int position;
        String labelHeader;
        String CGtype;
        String CGparams;
        String CGvalue;
        String contentType;
        String defaultValue;
        String containerType;
        String containerClass;
        String containerWidth;
        String containerHeight;
        int AddingRow_enabled;
        String AddingRow_params;
        String originQuery;
        String originLabelField;
        String originValueField;
        String originValueFieldType;
        String userRights;
        String actionPerformed;
        String actionParams;
        String visible;
        String routineOnChange;
        int hasSum;
        String modifiable;
        int alive;
        String operatore;
        Blob picture;
        String ges_triggers;
        String replicaID;

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

        public String getRifDatabase() {
            return rifDatabase;
        }

        public void setRifDatabase(String rifDatabase) {
            this.rifDatabase = rifDatabase;
        }

        public String getRifVersion() {
            return rifVersion;
        }

        public void setRifVersion(String rifVersion) {
            this.rifVersion = rifVersion;
        }

        public String getRifForm() {
            return rifForm;
        }

        public void setRifForm(String rifForm) {
            this.rifForm = rifForm;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getLabelHeader() {
            return labelHeader;
        }

        public void setLabelHeader(String labelHeader) {
            this.labelHeader = labelHeader;
        }

        public String getCGtype() {
            return CGtype;
        }

        public void setCGtype(String CGtype) {
            this.CGtype = CGtype;
        }

        public String getCGparams() {
            return CGparams;
        }

        public void setCGparams(String CGparams) {
            this.CGparams = CGparams;
        }

        public String getCGvalue() {
            return CGvalue;
        }

        public void setCGvalue(String CGvalue) {
            this.CGvalue = CGvalue;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getContainerType() {
            return containerType;
        }

        public void setContainerType(String containerType) {
            this.containerType = containerType;
        }

        public String getContainerClass() {
            return containerClass;
        }

        public void setContainerClass(String containerClass) {
            this.containerClass = containerClass;
        }

        public String getContainerWidth() {
            return containerWidth;
        }

        public void setContainerWidth(String containerWidth) {
            this.containerWidth = containerWidth;
        }

        public String getContainerHeight() {
            return containerHeight;
        }

        public void setContainerHeight(String containerHeight) {
            this.containerHeight = containerHeight;
        }

        public int getAddingRow_enabled() {
            return AddingRow_enabled;
        }

        public void setAddingRow_enabled(int AddingRow_enabled) {
            this.AddingRow_enabled = AddingRow_enabled;
        }

        public String getAddingRow_params() {
            return AddingRow_params;
        }

        public void setAddingRow_params(String AddingRow_params) {
            this.AddingRow_params = AddingRow_params;
        }

        public String getOriginQuery() {
            return originQuery;
        }

        public void setOriginQuery(String originQuery) {
            this.originQuery = originQuery;
        }

        public String getOriginLabelField() {
            return originLabelField;
        }

        public void setOriginLabelField(String originLabelField) {
            this.originLabelField = originLabelField;
        }

        public String getOriginValueField() {
            return originValueField;
        }

        public void setOriginValueField(String originValueField) {
            this.originValueField = originValueField;
        }

        public String getOriginValueFieldType() {
            return originValueFieldType;
        }

        public void setOriginValueFieldType(String originValueFieldType) {
            this.originValueFieldType = originValueFieldType;
        }

        public String getUserRights() {
            return userRights;
        }

        public void setUserRights(String userRights) {
            this.userRights = userRights;
        }

        public String getActionPerformed() {
            return actionPerformed;
        }

        public void setActionPerformed(String actionPerformed) {
            this.actionPerformed = actionPerformed;
        }

        public String getActionParams() {
            return actionParams;
        }

        public void setActionParams(String actionParams) {
            this.actionParams = actionParams;
        }

        public String getVisible() {
            return visible;
        }

        public void setVisible(String visible) {
            this.visible = visible;
        }

        public String getRoutineOnChange() {
            return routineOnChange;
        }

        public void setRoutineOnChange(String routineOnChange) {
            this.routineOnChange = routineOnChange;
        }

        public int getHasSum() {
            return hasSum;
        }

        public void setHasSum(int hasSum) {
            this.hasSum = hasSum;
        }

        public String getModifiable() {
            return modifiable;
        }

        public void setModifiable(String modifiable) {
            this.modifiable = modifiable;
        }

        public int getAlive() {
            return alive;
        }

        public void setAlive(int alive) {
            this.alive = alive;
        }

        public String getOperatore() {
            return operatore;
        }

        public void setOperatore(String operatore) {
            this.operatore = operatore;
        }

        public Blob getPicture() {
            return picture;
        }

        public void setPicture(Blob picture) {
            this.picture = picture;
        }

        public String getGes_triggers() {
            return ges_triggers;
        }

        public void setGes_triggers(String ges_triggers) {
            this.ges_triggers = ges_triggers;
        }

        public String getReplicaID() {
            return replicaID;
        }

        public void setReplicaID(String replicaID) {
            this.replicaID = replicaID;
        }

    }

    class gFEformChildhood {

        /*
        
CREATE TABLE `gFE_forms_childhood` (
  `ID` int(11) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `rifDatabase` varchar(64) DEFAULT NULL,
  `rifVersion` varchar(64) DEFAULT NULL,
  `rifFather` varchar(64) DEFAULT NULL,
  `rifChild` varchar(64) DEFAULT NULL,
  `destination` varchar(64) DEFAULT NULL,
  `childLinkQuery` text,
  `alive` int(11) DEFAULT '1',
  `operatore` varchar(256) DEFAULT NULL,
  `position` int(11) DEFAULT '1'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

         */
        String ID;
        String name;
        String rifDatabase;
        String rifVersion;
        String rifFather;
        String rifChild;
        String destination;
        String childLinkQuery;
        String conditions;
        int alive;
        String operatore;
        int position;

        public String getConditions() {
            return conditions;
        }

        public void setConditions(String conditions) {
            this.conditions = conditions;
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

        public String getRifDatabase() {
            return rifDatabase;
        }

        public void setRifDatabase(String rifDatabase) {
            this.rifDatabase = rifDatabase;
        }

        public String getRifVersion() {
            return rifVersion;
        }

        public void setRifVersion(String rifVersion) {
            this.rifVersion = rifVersion;
        }

        public String getRifFather() {
            return rifFather;
        }

        public void setRifFather(String rifFather) {
            this.rifFather = rifFather;
        }

        public String getRifChild() {
            return rifChild;
        }

        public void setRifChild(String rifChild) {
            this.rifChild = rifChild;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getChildLinkQuery() {
            return childLinkQuery;
        }

        public void setChildLinkQuery(String childLinkQuery) {
            this.childLinkQuery = childLinkQuery;
        }

        public int getAlive() {
            return alive;
        }

        public void setAlive(int alive) {
            this.alive = alive;
        }

        public String getOperatore() {
            return operatore;
        }

        public void setOperatore(String operatore) {
            this.operatore = operatore;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

    }

    class gFE_directives {

        /*
        
CREATE TABLE `gFE_directives` (
  `ID` varchar(64) NOT NULL DEFAULT '',
  `name` varchar(64) DEFAULT NULL,
  `rifDatabase` varchar(64) DEFAULT 'ALL',
  `rifInstance` varchar(64) DEFAULT 'ALL',
  `rifUser` varchar(64) DEFAULT 'ALL',
  `customByInstance` varchar(16) DEFAULT 'FALSE',
  `group` varchar(128) DEFAULT NULL,
  `infoName` varchar(256) DEFAULT NULL,
  `infoValue` text,
  `media` blob
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

         */
        String ID;
        String name;
        String rifDatabase;
        String rifInstance;
        String rifUser;
        String customByInstance;
        String group;
        String infoName;
        String infoValue;
        Blob media;

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

        public String getRifDatabase() {
            return rifDatabase;
        }

        public void setRifDatabase(String rifDatabase) {
            this.rifDatabase = rifDatabase;
        }

        public String getRifInstance() {
            return rifInstance;
        }

        public void setRifInstance(String rifInstance) {
            this.rifInstance = rifInstance;
        }

        public String getRifUser() {
            return rifUser;
        }

        public void setRifUser(String rifUser) {
            this.rifUser = rifUser;
        }

        public String getCustomByInstance() {
            return customByInstance;
        }

        public void setCustomByInstance(String customByInstance) {
            this.customByInstance = customByInstance;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
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

        public Blob getMedia() {
            return media;
        }

        public void setMedia(Blob media) {
            this.media = media;
        }

    }

    class gFE_reports {

        /*
        CREATE TABLE `gFE_reports` (
          `ID` varchar(64) NOT NULL,
          `name` varchar(128) DEFAULT NULL,
          `reportGroup` varchar(64) DEFAULT NULL,
          `content` text,
          `rifDatabase` varchar(64) DEFAULT NULL,
          `rifInstance` varchar(64) NOT NULL
        ) ENGINE=MyISAM DEFAULT CHARSET=latin1;
         */
        String ID;
        String name;
        String reportGroup;
        String content;
        String rifDatabase;
        String rifInstance;

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

        public String getReportGroup() {
            return reportGroup;
        }

        public void setReportGroup(String reportGroup) {
            this.reportGroup = reportGroup;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRifDatabase() {
            return rifDatabase;
        }

        public void setRifDatabase(String rifDatabase) {
            this.rifDatabase = rifDatabase;
        }

        public String getRifInstance() {
            return rifInstance;
        }

        public void setRifInstance(String rifInstance) {
            this.rifInstance = rifInstance;
        }

    }

    class Table {

        /*
            CREATE TABLE `tables` (
              `ID` varchar(64) NOT NULL DEFAULT '',
              `name` varchar(128) DEFAULT NULL,
              `rifDatabase` varchar(64) DEFAULT NULL,
              `recorded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
              `operatore` varchar(64) DEFAULT NULL,
              `alive` int(11) DEFAULT '1',
              `position` int(11) DEFAULT '0'
            ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
         */
        String ID;
        String name;
        String rifDatabase;
        String recorded;
        String operatore;
        int alive;
        int position;
        ArrayList<Field> Fields;

        public Table() {
            this.Fields = new ArrayList<>();
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

        public String getRifDatabase() {
            return rifDatabase;
        }

        public void setRifDatabase(String rifDatabase) {
            this.rifDatabase = rifDatabase;
        }

        public String getRecorded() {
            return recorded;
        }

        public void setRecorded(String recorded) {
            this.recorded = recorded;
        }

        public String getOperatore() {
            return operatore;
        }

        public void setOperatore(String operatore) {
            this.operatore = operatore;
        }

        public int getAlive() {
            return alive;
        }

        public void setAlive(int alive) {
            this.alive = alive;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public ArrayList<Field> getFields() {
            return Fields;
        }

        public void setFields(ArrayList<Field> Fields) {
            this.Fields = Fields;
        }

    }

    class Field {

        /*

            CREATE TABLE `fields` (
              `ID` varchar(64) NOT NULL DEFAULT '',
              `name` varchar(128) DEFAULT NULL,
              `type` varchar(64) DEFAULT NULL,
              `length` int(11) DEFAULT NULL,
              `rifTable` varchar(64) DEFAULT NULL,
              `defaultValue` varchar(128) DEFAULT NULL,
              `autoIncrement` int(11) DEFAULT NULL,
              `primary` int(11) DEFAULT NULL,
              `notNull` int(11) DEFAULT NULL,
              `position` int(11) DEFAULT NULL,
              `insertToken` varchar(128) DEFAULT NULL,
              `recorded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
              `operatore` varchar(64) DEFAULT NULL,
              `alive` int(11) DEFAULT '1'
            ) ENGINE=MyISAM DEFAULT CHARSET=utf8;

         */
        String ID;
        String name;
        String type;
        int length;
        String rifTable;
        String defaultValue;
        int autoIncrement;
        int primary;
        int notNull;
        int position;
        String insertToken;
        String recorded;
        String operatore;
        int alive;

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getRifTable() {
            return rifTable;
        }

        public void setRifTable(String rifTable) {
            this.rifTable = rifTable;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public int getAutoIncrement() {
            return autoIncrement;
        }

        public void setAutoIncrement(int autoIncrement) {
            this.autoIncrement = autoIncrement;
        }

        public int getPrimary() {
            return primary;
        }

        public void setPrimary(int primary) {
            this.primary = primary;
        }

        public int getNotNull() {
            return notNull;
        }

        public void setNotNull(int notNull) {
            this.notNull = notNull;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getInsertToken() {
            return insertToken;
        }

        public void setInsertToken(String insertToken) {
            this.insertToken = insertToken;
        }

        public String getRecorded() {
            return recorded;
        }

        public void setRecorded(String recorded) {
            this.recorded = recorded;
        }

        public String getOperatore() {
            return operatore;
        }

        public void setOperatore(String operatore) {
            this.operatore = operatore;
        }

        public int getAlive() {
            return alive;
        }

        public void setAlive(int alive) {
            this.alive = alive;
        }

    }
// </editor-fold> 

    class gEVO_directives {

        /*
            CREATE TABLE `gEVO_directives` (
              `ID` int(11) NOT NULL,
              `infoName` varchar(128) DEFAULT NULL,
              `infoValue` text,
              `instanceInfoValue` text,
              `note` text,
              `rifProjects` varchar(64) DEFAULT NULL,
              `instance` varchar(256) DEFAULT 'ALL',
              `superGroup` varchar(256) DEFAULT 'FrontEnd',
              `group` varchar(256) DEFAULT 'Aspect',
              `modifiable` varchar(256) DEFAULT 'DEFAULT:FALSE'
            ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
         */
        int ID;
        String infoName;
        String infoValue;
        String instanceInfoValue;
        String note;
        String rifProjects;
        String instance;
        String superGroup;
        String group;
        String modifiable;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
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

        public String getInstanceInfoValue() {
            return instanceInfoValue;
        }

        public void setInstanceInfoValue(String instanceInfoValue) {
            this.instanceInfoValue = instanceInfoValue;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getRifProjects() {
            return rifProjects;
        }

        public void setRifProjects(String rifProjects) {
            this.rifProjects = rifProjects;
        }

        public String getInstance() {
            return instance;
        }

        public void setInstance(String instance) {
            this.instance = instance;
        }

        public String getSuperGroup() {
            return superGroup;
        }

        public void setSuperGroup(String superGroup) {
            this.superGroup = superGroup;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getModifiable() {
            return modifiable;
        }

        public void setModifiable(String modifiable) {
            this.modifiable = modifiable;
        }

    }
}
