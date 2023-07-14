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
package REVOpager;

import REVOdbManager.EVOpagerDirectivesManager;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOsetup.ErrorLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Franco
 */
public class EVOpagerDBconnection {

//    ErrorLogger el;
    EVOpagerParams myParams;
    Settings mySettings;
    private Connection conMaster;
    
    public EVOpagerDBconnection(Settings xSettings) {
        this.myParams = new EVOpagerParams();
        this.mySettings = xSettings;
        
    }
    public EVOpagerDBconnection(EVOpagerParams xParams, Settings xSettings) {
        this.myParams = xParams;
        this.mySettings = xSettings;

//        System.out.println("\n@@@@@@@@" + mySettings.getGaiaHost().getConfigPath() + " - " + mySettings.getGaiaHost().getConfigData());
//        System.out.println("@@@@@@@@" + mySettings.getGaiaHost().getDbUsername() + " - " + mySettings.getGaiaHost().getDbSeed());
////////        if (mySettings.getGaiaHost().getPwType() != null
////////                && mySettings.getGaiaHost().getDbUsername() != null && mySettings.getGaiaHost().getDbUsername().length() > 0) {
////////            String newPW = null;
////////            if (mySettings.getGaiaHost().getPwType().equalsIgnoreCase("standard")) {
////////                mySettings.setData_DATABASE_USER(mySettings.getGaiaHost().getDbUsername());
////////                newPW = mySettings.getGaiaHost().getDbSeed();
////////                newPW += "_Pa$$_queenpro";
////////                mySettings.setData_DATABASE_PW(newPW);
//////////                System.out.println("@@@@@>>>" +  mySettings.getData_DATABASE_USER()+ " - " +  mySettings.getData_DATABASE_PW());
////////            }}
//        el = new ErrorLogger(myParams, mySettings);
//        el.setPrintOnScreen(false);
//        el.setPrintOnLog(true);
//        el.log("EVOpagerDBconnection", "creando connessione...");
    }

    
    public EVOpagerDBconnection() {
    }

    public Connection ConnLocalDataDB() {
        String SQLdriver = mySettings.getData_SQLdriver();

        String DBname = getDbExtendedName(myParams.getCKprojectGroup(), mySettings.getProjectDB(), myParams.getCKcontextID());

        String URL = mySettings.getData_defaultSQLserver();
        String alternativeURL = mySettings.getData_alternativeSQLserver();
        String USERNAME = mySettings.getData_DATABASE_USER();
        String PASSWORD = mySettings.getData_DATABASE_PW();
        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );

    }

    public Connection ConnLocalSchema() {
        String SQLdriver = mySettings.getFE_SQLdriver();
        String DBname = "information_schema";
        String URL = mySettings.getFE_defaultSQLserver();
        String alternativeURL = mySettings.getFE_alternativeSQLserver();
        String USERNAME = mySettings.getFE_DATABASE_USER();
        String PASSWORD = mySettings.getFE_DATABASE_PW();
        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );

    }

    public Connection ConnLocalFE() {

        String SQLdriver = mySettings.getFE_SQLdriver();
        String DBname = getDbExtendedName(myParams.getCKprojectGroup(),
                mySettings.getFrontendDB(), myParams.getCKcontextID());
//        System.out.println("ConnLocalFE - DBname:" + DBname);
//        System.out.println("ConnLocalFE - getCKcontextID:" + myParams.getCKcontextID());
//        System.out.println("ConnLocalFE - getFrontendDB:" +  mySettings.getFrontendDB());
//          System.out.println("ConnLocalFE - getFE_DATABASE_USER:" + mySettings.getFE_DATABASE_USER());
//        System.out.println("ConnLocalFE - getFE_DATABASE_PW:" +mySettings.getFE_DATABASE_PW());
//        
//        
        String URL = mySettings.getFE_defaultSQLserver();
        String alternativeURL = mySettings.getFE_alternativeSQLserver();
        String USERNAME = mySettings.getFE_DATABASE_USER();
        String PASSWORD = mySettings.getFE_DATABASE_PW();
        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );

    }

    public Connection ConnAccountDB() {
        //getDbExtendedName(String group, String name, String instance)
        String SQLdriver = mySettings.getAccount_SQLdriver();
        String DBname = getDbExtendedName(myParams.getCKprojectGroup(), mySettings.getAccountDB(), myParams.getCKcontextID());
        String URL = mySettings.getAccount_defaultSQLserver();
        String alternativeURL = mySettings.getAccount_alternativeSQLserver();
        String USERNAME = mySettings.getAccount_DATABASE_USER();
        String PASSWORD = mySettings.getAccount_DATABASE_PW();

//        System.out.println( " ConnAccountDB - DBname:" + DBname); 
        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );

    }

    public Connection ConnGesStandardDB() {

        String SQLdriver = mySettings.getRevolution_SQLdriver();
        //getDbExtendedName(String group, String name, String instance)
        String DBname = "gaiaEngineSetter_ffs";
        String URL = mySettings.getRevolution_defaultSQLserver();
        String alternativeURL = mySettings.getRevolution_alternativeSQLserver();
        String USERNAME = mySettings.getRevolution_DATABASE_USER();
        String PASSWORD = mySettings.getRevolution_DATABASE_PW();
//
//        System.out.println("\n\n\nConnGesStandardDB---> DBname:" + DBname
//                + " - "
//                + "URL:" + URL
//                + " - "
//                + "USERNAME:" + USERNAME
//                + " - "
//                + "PASSWORD:" + PASSWORD
//                + "");

        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );

    }

    public Connection ConnGesSchema() {

        String SQLdriver = mySettings.getRevolution_SQLdriver();
        String DBname = "information_schema";
        String URL = mySettings.getRevolution_defaultSQLserver();
        String alternativeURL = mySettings.getRevolution_alternativeSQLserver();
        String USERNAME = mySettings.getRevolution_DATABASE_USER();
        String PASSWORD = mySettings.getRevolution_DATABASE_PW();
        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );

    }

    public Connection ConnLocalNoDB() {
        String SQLdriver = mySettings.getFE_SQLdriver();
        String DBname = "";
        String URL = mySettings.getFE_defaultSQLserver();
        String alternativeURL = mySettings.getFE_alternativeSQLserver();
        String USERNAME = mySettings.getFE_DATABASE_USER();
        String PASSWORD = mySettings.getFE_DATABASE_PW();
        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );
    }

    public Connection ConnLocalDMZ(String DBname, String USERNAME, String PASSWORD) {
        String SQLdriver = mySettings.getFE_SQLdriver();
        String URL = mySettings.getFE_defaultSQLserver();
        String alternativeURL = mySettings.getFE_alternativeSQLserver();

//         System.out.println("ConnLocalQueenpro: DATABASE_USER: " + USERNAME);
        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );
    }

    public Connection ConnLocalDMZ(String DBname) {
        String SQLdriver = mySettings.getFE_SQLdriver();
        String URL = mySettings.getFE_defaultSQLserver();
        String alternativeURL = mySettings.getFE_alternativeSQLserver();
        String USERNAME = mySettings.getFE_DATABASE_USER();
        String PASSWORD = mySettings.getFE_DATABASE_PW();
//         System.out.println("ConnLocalQueenpro: DATABASE_USER: " + USERNAME);
        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );
    }
    public Connection ConnLocalDMZ() {
        String SQLdriver = mySettings.getFE_SQLdriver();
        String URL = mySettings.getFE_defaultSQLserver();
        String alternativeURL = mySettings.getFE_alternativeSQLserver();
        String USERNAME = mySettings.getFE_DATABASE_USER();
        String PASSWORD = mySettings.getFE_DATABASE_PW();
        EVOpagerDirectivesManager myDirective = new EVOpagerDirectivesManager(myParams, mySettings);//QUesto compila automaticamente il context standard
        String DBname = mySettings.getAccountDB() + "_" + myParams.getCKcontextID();
//        System.out.println("DBname: " + DBname);
        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );
    }
public Connection ConnLocalDMZqueenpro() {
        String SQLdriver = mySettings.getFE_SQLdriver();
        String URL = mySettings.getFE_defaultSQLserver();
        String alternativeURL = mySettings.getFE_alternativeSQLserver();
        String USERNAME = mySettings.getFE_DATABASE_USER();
        String PASSWORD = mySettings.getFE_DATABASE_PW();
        EVOpagerDirectivesManager myDirective = new EVOpagerDirectivesManager(myParams, mySettings);//QUesto compila automaticamente il context standard
        String DBname = mySettings.getQueenproDB();
        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );
    }
    public Connection ConnLocalQueenpro() {
        String SQLdriver = mySettings.getFE_SQLdriver();
        String DBname = mySettings.getQueenproDB();
        String URL = mySettings.getFE_defaultSQLserver();
        String alternativeURL = mySettings.getFE_alternativeSQLserver();

        String USERNAME = mySettings.getFE_DATABASE_USER();
        String PASSWORD = mySettings.getFE_DATABASE_PW();

//         System.out.println("ConnLocalQueenpro: DATABASE_USER: " + USERNAME);
        return makeConnection(
                SQLdriver,
                URL,
                alternativeURL,
                DBname,
                USERNAME,
                PASSWORD
        );
    }

    public Connection makeConnection(String driver, String URL, String alternativeURL, String DBname, String USERNAME, String PASSWORD) {
        //(Server server, Database database)
        /*   String DBname = database.getDbExtendedName();
         String URL = database.getServer().getDefaultSQLserver();
         String alternativeURL = database.getServer().getAlternativeSQLserver();
         String USERNAME=  database.getServer().getDATABASE_USER();
         String PASSWORD = database.getServer().getDATABASE_PW();*/
        if (driver == null || driver.length() < 2 || driver.equalsIgnoreCase("NULL")) {
            driver = "mySQL";
        }

//////////        String myLog = ("Connecting " + URL + " --> DB:" + DBname);
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("\nWRONG(1) [ClassNotFound ]! ERROR " + ex);
        }

        try {

            conMaster = DriverManager.getConnection(URL + DBname + "?useSSL=false",
                    USERNAME, PASSWORD);
////////            myLog = ("Connected " + URL + " --> DB:" + DBname);
        } catch (SQLException ex) {
////////            myLog = ("error Connecting " + URL + " --> DB:" + DBname);
            URL = alternativeURL;
////////            myLog = ("...so Connecting " + URL + " --> DB:" + DBname);
////////            System.out.println("myLog2: " + myLog);
            try {
                conMaster = DriverManager.getConnection(URL + DBname,
                        USERNAME, PASSWORD);
////////                myLog = ("Connected " + URL + " --> DB:" + DBname);
////////                 System.out.println("myLog3: " + myLog);
            } catch (SQLException ex1) {
////////                System.out.println("\n*WRONG(2) [makeConnection] ! ERROR " + ex1);
////////                System.out.println("DATABASE_LOCATION: " + URL + DBname);
////////                System.out.println("DBname: " + DBname);
////////                System.out.println("DATABASE_USER: " + USERNAME);
                conMaster = null;
                return null;
            }
        }
        // System.out.println(myLog);
        return conMaster;

    }

    public void close() {

        try {
            conMaster.close();
            conMaster = null;
        } catch (SQLException ex) {
            conMaster = null;
            System.out.println("</br>WRONG [connectionClose] ! ERROR " + ex);
        }
    }

    public String getDbExtendedName(String group, String name, String instance) {
        String dbName = "";
        if (group != null && group != ""
                && !group.equalsIgnoreCase("null")
                && group.length() > 0) {
            dbName = group + "_";
        }
        dbName = dbName + name;
        if (instance != null && instance != ""
                && !instance.equalsIgnoreCase("null")
                && instance.length() > 0) {
            dbName += "_" + instance;
        }
        // System.out.println("Returning DB name="+dbName);

        return dbName;
    }
}
