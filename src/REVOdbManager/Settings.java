/* 
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
package REVOdbManager;

import REVOpager.EVOpagerDBconnection;
import REVOpager.Server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.TomcatGaiaHost;
import showIt.eventManager;

/**
 *
 * @author Franco
 */
public class Settings {

    private String projectName;
    private String projectDB;
    private String accountDB;
    private String frontendDB;
    private String queenproDB;
    private String userIP;
    private String userDate;
    private String WSendpoint;
    private String softwareTitle;
//    private String softwareName;
    private String softwareVersion;
    // private String applicationURL;

    private String passwordSeed;

    private String AccessType;
    private String RegistrationAllowed;
    private String AdminConfirmationRequested;
    private String AdminConfirmedGroup;

    private String data_SQLdriver;
    private String data_defaultSQLserver;
    private String data_alternativeSQLserver;
    private String data_DATABASE_USER;
    private String data_DATABASE_PW;
    private Server data_server;

    private String FE_SQLdriver;
    private String FE_defaultSQLserver;
    private String FE_alternativeSQLserver;
    private String FE_DATABASE_USER;
    private String FE_DATABASE_PW;
    private Server FE_server;

    private String QP_centralManagerURL;
    private String QP_SQLdriver;
    private String QP_defaultSQLserver;
    private String QP_alternativeSQLserver;
    private String QP_DATABASE_USER;
    private String QP_DATABASE_PW;
    private Server QP_server;

    private String account_SQLdriver;
    private String account_defaultSQLserver;
    private String account_alternativeSQLserver;
    private String account_DATABASE_USER;
    private String account_DATABASE_PW;
    private Server account_server;
    private String account_TABLElinkUserGroups;
    private String account_FIELDlinkUserGroupsRifOperatore;
    private String account_FIELDlinkUserGroupsRifGruppo;
    private String account_TABLEgruppi;
    private String account_FIELDGruppiIDgruppo;
    private String account_TABLEoperatori;
    private String account_FIELDoperatoriID;
    private String account_TABLEtokens;
    private String account_FIELDusernameField;
    private String account_FIELDnameField;
    private String account_FIELDsurnameField;
    private String account_FIELDpictureField;
    private String account_FIELDpincode;
    private String account_FIELDpassword;
    private String account_passwordSEED;

    private String revolution_SQLdriver;
    private String revolution_defaultSQLserver;
    private String revolution_alternativeSQLserver;
    private String revolution_DATABASE_USER;
    private String revolution_DATABASE_PW;
    private Server revolution_server;
    //private EVOpagerParams myParams;

    private String localFE_directives;
    private String localFE_forms;
    private String localFE_reports;

    private String localFE_forms_childhood;
    private String localFE_frames;
    private String localFE_objects;
    private String localFE_screens;
    private String localCUSTOM_directives;
    private String localEVO_directives;
    private boolean lighthouseHookable;
    private boolean showPeople;

    private String updateMode;

    private TomcatGaiaHost gaiaHost;

    boolean logEvents;
    boolean logCRUD;
    boolean logLogin;
    boolean logLogon;
    private String CLIENT_SERVERNAME;
    private String CLIENT_PROJECTNAME;
    private String CLIENT_CONTEXT;
    private String CLIENT_IP;
    private String CLIENT_ID;
    private String CLIENT_TYPE;
    private String CLIENT_MODEL;
    private String CLIENT_USER_ID;
    private String CLIENT_USER_PW;
    
    
    private boolean usesGeoMap;
    private boolean usesAudioRec;
    private boolean usesTree;
    private boolean databrowser;

    public void chargeHost() {
        gaiaHost = new TomcatGaiaHost(projectName);
        String newPW = null;
        String newUser = null;

        if (this.getGaiaHost() != null && this.getGaiaHost().getPwType() != null
                && this.getGaiaHost().getDbUsername() != null
                && this.getGaiaHost().getDbUsername().length() > 0) {

            if (this.getGaiaHost().getPwType().equalsIgnoreCase("standard")) {
                newPW = this.getGaiaHost().getDbSeed() + "_Pa$$_queenpro";
                newUser = this.getGaiaHost().getDbUsername();

            }
            if (this.getGaiaHost().getQP_centralManagerURL() != null && this.getGaiaHost().getQP_centralManagerURL().length() > 0) {
                QP_centralManagerURL = this.getGaiaHost().getQP_centralManagerURL() + "centralManager";
            }
        } else {
            newUser = "NAS-SOFTWARE";
            newPW = "buckaroo!22014";
        }

        this.setData_DATABASE_USER(newUser);
        this.setData_DATABASE_PW(newPW);
        this.setAccount_DATABASE_USER(newUser);
        this.setAccount_DATABASE_PW(newPW);
        this.setFE_DATABASE_USER(newUser);
        this.setFE_DATABASE_PW(newPW);
        this.setRevolution_DATABASE_USER(newUser);
        this.setRevolution_DATABASE_PW(newPW);

//        System.out.println("@@@@@chargeHost>>>" + this.getData_DATABASE_USER() + " - QPserver:" + this.getQP_centralManagerURL() + " - ");
    }

    public void printSettings(String mittente) {

        String result = "\n\n**SETTINGS*****<" + mittente + ">*********************\n";
        //   result += "tokenID:" + tokenID;
        //   result += "\nuserID:" + userID;
        //    result += "\ncontextID:" + contextID;
        result += "\nprojectName:" + projectName;
        result += "\nprojectDB:" + projectDB;
        result += "\nfrontendDB:" + frontendDB;
        result += "\naccountDB:" + accountDB;

        //    result += "\nprojectGroup:" + projectGroup;
        result += "\nuserIP:" + userIP;
        result += "\nuserDate:" + userDate;
//        result += "\nsoftwareName:" + softwareName;
        result += "\nsoftwareTitle:" + softwareTitle;
        result += "\nsoftwareVersion:" + softwareVersion;

        result += "\n\ndata_SQLdriver:" + data_SQLdriver;
        result += "\ndata_defaultSQLserver:" + data_defaultSQLserver;
        result += "\ndata_alternativeSQLserver:" + data_alternativeSQLserver;
        result += "\ndata_DATABASE_USER:" + data_DATABASE_USER;
        result += "\ndata_DATABASE_PW:" + data_DATABASE_PW;

        result += "\n\nFE_SQLdriver:" + FE_SQLdriver;
        result += "\nFE_defaultSQLserver:" + FE_defaultSQLserver;
        result += "\nFE_alternativeSQLserver:" + FE_alternativeSQLserver;
        result += "\nFE_DATABASE_USER:" + FE_DATABASE_USER;
        result += "\nFE_DATABASE_PW:" + FE_DATABASE_PW;

        result += "\n\naccount_SQLdriver:" + account_SQLdriver;
        result += "\naccount_defaultSQLserver:" + account_defaultSQLserver;
        result += "\naccount_alternativeSQLserver:" + account_alternativeSQLserver;
        result += "\naccount_DATABASE_USER:" + account_DATABASE_USER;
        result += "\naccount_DATABASE_PW:" + account_DATABASE_PW;

        result += "\n\nrevolution_SQLdriver:" + revolution_SQLdriver;
        result += "\nrevolution_defaultSQLserver:" + revolution_defaultSQLserver;
        result += "\nrevolution_alternativeSQLserver:" + revolution_alternativeSQLserver;
        result += "\nrevolution_DATABASE_USER:" + revolution_DATABASE_USER;
        result += "\nrevolution_DATABASE_PW:" + revolution_DATABASE_PW;

        result += "\n\nlocalEVO_directives:" + localEVO_directives;
        result += "\nlocalCUSTOM_directives:" + localCUSTOM_directives;
        result += "\nlocalFE_directives:" + localFE_directives;
        result += "\nlocalFE_forms:" + localFE_forms;
        result += "\nocalFE_forms_childhood:" + localFE_forms_childhood;
        /**/
        result += "******************************\n";

        System.out.print(result);

    }

    public boolean isUsesTree() {
        return usesTree;
    }

    public void setUsesTree(boolean usesTree) {
        this.usesTree = usesTree;
    }

    public boolean isUsesGeoMap() {
        return usesGeoMap;
    }

    public void setUsesGeoMap(boolean usesGeoMap) {
        this.usesGeoMap = usesGeoMap;
    }

    public boolean isUsesAudioRec() {
        return usesAudioRec;
    }

    public void setUsesAudioRec(boolean usesAudioRec) {
        this.usesAudioRec = usesAudioRec;
    }

    public String getCLIENT_ID() {
        return CLIENT_ID;
    }

    public boolean isDatabrowser() {
        return databrowser;
    }

    public void setDatabrowser(boolean databrowser) {
        this.databrowser = databrowser;
    }

    public void setCLIENT_ID(String CLIENT_ID) {
        this.CLIENT_ID = CLIENT_ID;
    }

    public String getCLIENT_TYPE() {
        return CLIENT_TYPE;
    }

    public void setCLIENT_TYPE(String CLIENT_TYPE) {
        this.CLIENT_TYPE = CLIENT_TYPE;
    }

    public String getCLIENT_MODEL() {
        return CLIENT_MODEL;
    }

    public void setCLIENT_MODEL(String CLIENT_MODEL) {
        this.CLIENT_MODEL = CLIENT_MODEL;
    }

    public String getCLIENT_USER_ID() {
        return CLIENT_USER_ID;
    }

    public void setCLIENT_USER_ID(String CLIENT_USER_ID) {
        this.CLIENT_USER_ID = CLIENT_USER_ID;
    }

    public String getCLIENT_USER_PW() {
        return CLIENT_USER_PW;
    }

    public void setCLIENT_USER_PW(String CLIENT_USER_PW) {
        this.CLIENT_USER_PW = CLIENT_USER_PW;
    }

    

    public String getCLIENT_IP() {
        return CLIENT_IP;
    }

    public void setCLIENT_IP(String CLIENT_IP) {
        this.CLIENT_IP = CLIENT_IP;
    }

    public String getCLIENT_SERVERNAME() {
        return CLIENT_SERVERNAME;
    }

    public void setCLIENT_SERVERNAME(String CLIENT_SERVERNAME) {
        this.CLIENT_SERVERNAME = CLIENT_SERVERNAME;
    }

    public String getCLIENT_PROJECTNAME() {
        return CLIENT_PROJECTNAME;
    }

    public void setCLIENT_PROJECTNAME(String CLIENT_PROJECTNAME) {
        this.CLIENT_PROJECTNAME = CLIENT_PROJECTNAME;
    }

    public String getCLIENT_CONTEXT() {
        return CLIENT_CONTEXT;
    }

    public void setCLIENT_CONTEXT(String CLIENT_CONTEXT) {
        this.CLIENT_CONTEXT = CLIENT_CONTEXT;
    }

    public boolean isShowPeople() {
        return showPeople;
    }

    public void setShowPeople(boolean showPeople) {
        this.showPeople = showPeople;
    }

    public boolean isLogEvents() {
        return logEvents;
    }

    public void setLogEvents(boolean logEvents) {
        this.logEvents = logEvents;
    }

    public boolean isLogCRUD() {
        return logCRUD;
    }

    public void setLogCRUD(boolean logCRUD) {
        this.logCRUD = logCRUD;
    }

    public boolean isLogLogin() {
        return logLogin;
    }

    public void setLogLogin(boolean logLogin) {
        this.logLogin = logLogin;
    }

    public boolean isLogLogon() {
        return logLogon;
    }

    public void setLogLogon(boolean logLogon) {
        this.logLogon = logLogon;
    }

    public String getQP_centralManagerURL() {
        return QP_centralManagerURL;
    }

    public void setQP_centralManagerURL(String QP_centralManagerURL) {
        this.QP_centralManagerURL = QP_centralManagerURL;
    }

    public String getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(String updateMode) {
        this.updateMode = updateMode;
    }

    public TomcatGaiaHost getGaiaHost() {
        return gaiaHost;
    }

    public boolean isLighthouseHookable() {
        return lighthouseHookable;
    }

    public void setLighthouseHookable(boolean lighthouseHookable) {
        this.lighthouseHookable = lighthouseHookable;
    }

    public String getLocalFE_reports() {
        return localFE_reports;
    }

    public void setLocalFE_reports(String localFE_reports) {
        this.localFE_reports = localFE_reports;
    }

    public String getWSendpoint() {
        return WSendpoint;
    }

    public void setWSendpoint(String WSendpoint) {
        this.WSendpoint = WSendpoint;
    }

    public String getLocalFE_directives() {
        return localFE_directives;
    }

    public void setLocalFE_directives(String localFE_directives) {
        this.localFE_directives = localFE_directives;
    }

    public String getLocalFE_forms() {
        return localFE_forms;
    }

    public void setLocalFE_forms(String localFE_forms) {
        this.localFE_forms = localFE_forms;
    }

    public String getLocalFE_forms_childhood() {
        return localFE_forms_childhood;
    }

    public void setLocalFE_forms_childhood(String localFE_forms_childhood) {
        this.localFE_forms_childhood = localFE_forms_childhood;
    }

    public String getLocalFE_frames() {
        return localFE_frames;
    }

    public void setLocalFE_frames(String localFE_frames) {
        this.localFE_frames = localFE_frames;
    }

    public String getLocalFE_objects() {
        return localFE_objects;
    }

    public void setLocalFE_objects(String localFE_objects) {
        this.localFE_objects = localFE_objects;
    }

    public String getLocalFE_screens() {
        return localFE_screens;
    }

    public void setLocalFE_screens(String localFE_screens) {
        this.localFE_screens = localFE_screens;
    }

    public String getLocalCUSTOM_directives() {
        return localCUSTOM_directives;
    }

    public void setLocalCUSTOM_directives(String localCUSTOM_directives) {
        this.localCUSTOM_directives = localCUSTOM_directives;
    }

    public String getLocalEVO_directives() {
        return localEVO_directives;
    }

    public void setLocalEVO_directives(String localEVO_directives) {
        this.localEVO_directives = localEVO_directives;
    }

    public String getAccount_passwordSEED() {
        return account_passwordSEED;
    }

    public void setAccount_passwordSEED(String account_passwordSEED) {
        this.account_passwordSEED = account_passwordSEED;
    }

    public String getAccount_FIELDpincode() {
        return account_FIELDpincode;
    }

    public void setAccount_FIELDpincode(String account_FIELDpincode) {
        this.account_FIELDpincode = account_FIELDpincode;
    }

    public String getAccount_FIELDpassword() {
        return account_FIELDpassword;
    }

    public void setAccount_FIELDpassword(String account_FIELDpassword) {
        this.account_FIELDpassword = account_FIELDpassword;
    }

    public String getAccountDB() {
        return accountDB;
    }

    public void setAccountDB(String accountDB) {
        this.accountDB = accountDB;
    }

    public String getFrontendDB() {
        return frontendDB;
    }

    public void setFrontendDB(String frontendDB) {
        this.frontendDB = frontendDB;
    }

    public String getAccount_TABLElinkUserGroups() {
        return account_TABLElinkUserGroups;
    }

    public void setAccount_TABLElinkUserGroups(String account_TABLElinkUserGroups) {
        this.account_TABLElinkUserGroups = account_TABLElinkUserGroups;
    }

    public String getAccount_TABLEgruppi() {
        return account_TABLEgruppi;
    }

    public void setAccount_TABLEgruppi(String account_TABLEgruppi) {
        this.account_TABLEgruppi = account_TABLEgruppi;
    }

    public String getAccount_TABLEoperatori() {
        return account_TABLEoperatori;
    }

    public void setAccount_TABLEoperatori(String account_TABLEoperatori) {
        this.account_TABLEoperatori = account_TABLEoperatori;
    }

    public String getAccount_TABLEtokens() {
        return account_TABLEtokens;
    }

    public void setAccount_TABLEtokens(String account_TABLEtokens) {
        this.account_TABLEtokens = account_TABLEtokens;
    }

//    public EVOpagerParams getMyParams() {
//        return myParams;
//    }
//
//    public void setMyParams(EVOpagerParams myParams) {
//        this.myParams = myParams;
//    }
    public String getAccount_FIELDlinkUserGroupsRifOperatore() {
        return account_FIELDlinkUserGroupsRifOperatore;
    }

    public void setAccount_FIELDlinkUserGroupsRifOperatore(String account_FIELDlinkUserGroupsRifOperatore) {
        this.account_FIELDlinkUserGroupsRifOperatore = account_FIELDlinkUserGroupsRifOperatore;
    }

    public String getAccount_FIELDlinkUserGroupsRifGruppo() {
        return account_FIELDlinkUserGroupsRifGruppo;
    }

    public void setAccount_FIELDlinkUserGroupsRifGruppo(String account_FIELDlinkUserGroupsRifGruppo) {
        this.account_FIELDlinkUserGroupsRifGruppo = account_FIELDlinkUserGroupsRifGruppo;
    }

    public String getAccount_FIELDGruppiIDgruppo() {
        return account_FIELDGruppiIDgruppo;
    }

    public void setAccount_FIELDGruppiIDgruppo(String account_FIELDGruppiIDgruppo) {
        this.account_FIELDGruppiIDgruppo = account_FIELDGruppiIDgruppo;
    }

    public String getAccount_FIELDoperatoriID() {
        return account_FIELDoperatoriID;
    }

    public void setAccount_FIELDoperatoriID(String account_FIELDoperatoriID) {
        this.account_FIELDoperatoriID = account_FIELDoperatoriID;
    }

    public String getAccount_FIELDusernameField() {
        return account_FIELDusernameField;
    }

    public void setAccount_FIELDusernameField(String account_FIELDusernameField) {
        this.account_FIELDusernameField = account_FIELDusernameField;
    }

    public String getAccount_FIELDnameField() {
        return account_FIELDnameField;
    }

    public void setAccount_FIELDnameField(String account_FIELDnameField) {
        this.account_FIELDnameField = account_FIELDnameField;
    }

    public String getAccount_FIELDsurnameField() {
        return account_FIELDsurnameField;
    }

    public void setAccount_FIELDsurnameField(String account_FIELDsurnameField) {
        this.account_FIELDsurnameField = account_FIELDsurnameField;
    }

    public String getAccount_FIELDpictureField() {
        return account_FIELDpictureField;
    }

    public void setAccount_FIELDpictureField(String account_FIELDpictureField) {
        this.account_FIELDpictureField = account_FIELDpictureField;
    }

    public String getData_SQLdriver() {
        return data_SQLdriver;
    }

    public void setData_SQLdriver(String data_SQLdriver) {
        this.data_SQLdriver = data_SQLdriver;
    }

    public String getData_defaultSQLserver() {
        return data_defaultSQLserver;
    }

    public void setData_defaultSQLserver(String data_defaultSQLserver) {
        this.data_defaultSQLserver = data_defaultSQLserver;
    }

    public String getData_alternativeSQLserver() {
        return data_alternativeSQLserver;
    }

    public void setData_alternativeSQLserver(String data_alternativeSQLserver) {
        this.data_alternativeSQLserver = data_alternativeSQLserver;
    }

    public String getData_DATABASE_USER() {
        return data_DATABASE_USER;
    }

    public void setData_DATABASE_USER(String data_DATABASE_USER) {
        this.data_DATABASE_USER = data_DATABASE_USER;
    }

    public String getData_DATABASE_PW() {
        return data_DATABASE_PW;
    }

    public void setData_DATABASE_PW(String data_DATABASE_PW) {
        this.data_DATABASE_PW = data_DATABASE_PW;
    }

    public Server getData_server() {
        return data_server;
    }

    public void setData_server(Server data_server) {
        this.data_server = data_server;
    }

    public Server getRevolution_server() {
        return revolution_server;
    }

    public void setRevolution_server(Server revolution_server) {
        this.revolution_server = revolution_server;
    }

    public String getFE_SQLdriver() {
        return FE_SQLdriver;
    }

    public void setFE_SQLdriver(String FE_SQLdriver) {
        this.FE_SQLdriver = FE_SQLdriver;
    }

    public String getFE_defaultSQLserver() {
        return FE_defaultSQLserver;
    }

    public void setFE_defaultSQLserver(String FE_defaultSQLserver) {
        this.FE_defaultSQLserver = FE_defaultSQLserver;
    }

    public String getFE_alternativeSQLserver() {
        return FE_alternativeSQLserver;
    }

    public void setFE_alternativeSQLserver(String FE_alternativeSQLserver) {
        this.FE_alternativeSQLserver = FE_alternativeSQLserver;
    }

    public String getFE_DATABASE_USER() {
        return FE_DATABASE_USER;
    }

    public void setFE_DATABASE_USER(String FE_DATABASE_USER) {
        this.FE_DATABASE_USER = FE_DATABASE_USER;
    }

    public String getFE_DATABASE_PW() {
        return FE_DATABASE_PW;
    }

    public void setFE_DATABASE_PW(String FE_DATABASE_PW) {
        this.FE_DATABASE_PW = FE_DATABASE_PW;
    }

    public Server getFE_server() {
        return FE_server;
    }

    public void setFE_server(Server FE_server) {
        this.FE_server = FE_server;
    }

    public String getAccount_SQLdriver() {
        return account_SQLdriver;
    }

    public void setAccount_SQLdriver(String account_SQLdriver) {
        this.account_SQLdriver = account_SQLdriver;
    }

    public String getAccount_defaultSQLserver() {
        return account_defaultSQLserver;
    }

    public void setAccount_defaultSQLserver(String account_defaultSQLserver) {
        this.account_defaultSQLserver = account_defaultSQLserver;
    }

    public String getAccount_alternativeSQLserver() {
        return account_alternativeSQLserver;
    }

    public void setAccount_alternativeSQLserver(String account_alternativeSQLserver) {
        this.account_alternativeSQLserver = account_alternativeSQLserver;
    }

    public String getAccount_DATABASE_USER() {
        return account_DATABASE_USER;
    }

    public void setAccount_DATABASE_USER(String account_DATABASE_USER) {
        this.account_DATABASE_USER = account_DATABASE_USER;
    }

    public String getAccount_DATABASE_PW() {
        return account_DATABASE_PW;
    }

    public void setAccount_DATABASE_PW(String account_DATABASE_PW) {
        this.account_DATABASE_PW = account_DATABASE_PW;
    }

    public Server getAccount_server() {
        return account_server;
    }

    public void setAccount_server(Server account_server) {
        this.account_server = account_server;
    }

    /*  public String getApplicationURL() {
        return applicationURL;
    }

    public void setApplicationURL(String applicationURL) {
        this.applicationURL = applicationURL;
    }
     */
    public String getAdminConfirmedGroup() {
        return AdminConfirmedGroup;
    }

    public void setAdminConfirmedGroup(String AdminConfirmedGroup) {
        this.AdminConfirmedGroup = AdminConfirmedGroup;
    }

    public String getAdminConfirmationRequested() {
        return AdminConfirmationRequested;
    }

    public void setAdminConfirmationRequested(String AdminConfirmationRequested) {
        this.AdminConfirmationRequested = AdminConfirmationRequested;
    }

    public String getAccessType() {
        return AccessType;
    }

    public void setAccessType(String AccessType) {
        this.AccessType = AccessType;
    }

    public String getRegistrationAllowed() {
        return RegistrationAllowed;
    }

    public void setRegistrationAllowed(String RegistrationAllowed) {
        this.RegistrationAllowed = RegistrationAllowed;
    }

    public String getProjectDB() {
        return projectDB;
    }

    public void setProjectDB(String projectDB) {
        this.projectDB = projectDB;
    }

    public String getRevolution_SQLdriver() {
        return revolution_SQLdriver;
    }

    public void setRevolution_SQLdriver(String revolution_SQLdriver) {
        this.revolution_SQLdriver = revolution_SQLdriver;
    }

    public String getRevolution_defaultSQLserver() {
        return revolution_defaultSQLserver;
    }

    public void setRevolution_defaultSQLserver(String revolution_defaultSQLserver) {
        this.revolution_defaultSQLserver = revolution_defaultSQLserver;
    }

    public String getRevolution_alternativeSQLserver() {
        return revolution_alternativeSQLserver;
    }

    public void setRevolution_alternativeSQLserver(String revolution_alternativeSQLserver) {
        this.revolution_alternativeSQLserver = revolution_alternativeSQLserver;
    }

    public String getRevolution_DATABASE_USER() {
        return revolution_DATABASE_USER;
    }

    public void setRevolution_DATABASE_USER(String revolution_DATABASE_USER) {
        this.revolution_DATABASE_USER = revolution_DATABASE_USER;
    }

    public String getRevolution_DATABASE_PW() {
        return revolution_DATABASE_PW;
    }

    public void setRevolution_DATABASE_PW(String revolution_DATABASE_PW) {
        this.revolution_DATABASE_PW = revolution_DATABASE_PW;
    }

//    public String getSoftwareName() {
//        return softwareName;
//    }
//
//    public void setSoftwareName(String softwareName) {
//        this.softwareName = softwareName;
//    }
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getProjectDBextendedName(EVOpagerParams myParams) {
        String dbName = "";
        if (myParams.getCKprojectGroup() != null && myParams.getCKprojectGroup() != ""
                && !myParams.getCKprojectGroup().equalsIgnoreCase("null")
                && myParams.getCKprojectGroup().length() > 0) {
            dbName = myParams.getCKprojectGroup() + "_";
        }
        dbName = dbName + this.projectDB;
        if (myParams.getCKcontextID() != null && myParams.getCKcontextID() != ""
                && !myParams.getCKcontextID().equalsIgnoreCase("null")
                && myParams.getCKcontextID().length() > 0) {
            dbName += "_" + myParams.getCKcontextID();
        }
        // System.out.println("Returning DB name="+dbName);

        return dbName;
    }

    /*
     public Server getServer() {
        
     return server;
     }
    
     */
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    public String getUserDate() {
        return userDate;
    }

    public void setUserDate(String userDate) {
        this.userDate = userDate;
    }

    public String getQueenproDB() {
        return queenproDB;
    }

    public void setQueenproDB(String queenproDB) {
        this.queenproDB = queenproDB;
    }

    public String getQP_SQLdriver() {
        return QP_SQLdriver;
    }

    public void setQP_SQLdriver(String QP_SQLdriver) {
        this.QP_SQLdriver = QP_SQLdriver;
    }

    public String getQP_defaultSQLserver() {
        return QP_defaultSQLserver;
    }

    public void setQP_defaultSQLserver(String QP_defaultSQLserver) {
        this.QP_defaultSQLserver = QP_defaultSQLserver;
    }

    public String getQP_alternativeSQLserver() {
        return QP_alternativeSQLserver;
    }

    public void setQP_alternativeSQLserver(String QP_alternativeSQLserver) {
        this.QP_alternativeSQLserver = QP_alternativeSQLserver;
    }

    public String getQP_DATABASE_USER() {
        return QP_DATABASE_USER;
    }

    public void setQP_DATABASE_USER(String QP_DATABASE_USER) {
        this.QP_DATABASE_USER = QP_DATABASE_USER;
    }

    public String getQP_DATABASE_PW() {
        return QP_DATABASE_PW;
    }

    public void setQP_DATABASE_PW(String QP_DATABASE_PW) {
        this.QP_DATABASE_PW = QP_DATABASE_PW;
    }

    public Server getQP_server() {
        return QP_server;
    }

    public void setQP_server(Server QP_server) {
        this.QP_server = QP_server;
    }

    public String getPasswordSeed() {
        return passwordSeed;
    }

    public void setPasswordSeed(String passwordSeed) {
        this.passwordSeed = passwordSeed;
    }

    public String getPasswordKey(String secondarySeed) {

        String pwKEY = this.passwordSeed + "buckaroodt3ftBFH4M";
        return pwKEY;
    }

    public String getSoftwareTitle() {
        return softwareTitle;
    }

    public void setSoftwareTitle(String softwareTitle) {
        this.softwareTitle = softwareTitle;
    }

    public String getInstallationName(EVOpagerParams myParams) {
        String serverName = "";
        Connection QPconny = new EVOpagerDBconnection(myParams, this).ConnLocalQueenpro();
        PreparedStatement ps;
        String SQLphrase = "SELECT * FROM definitions WHERE ID='*SERVERNAME*'";
        try {
            ps = QPconny.prepareStatement(SQLphrase);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                serverName = rs.getString("definition");
            }
        } catch (SQLException ex) {
            Logger.getLogger(eventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (serverName == null || serverName.equalsIgnoreCase("NULL")) {
            serverName = "";
        }
        String installationName = getProjectName() + "-" + serverName;

        return installationName;
    }
}
