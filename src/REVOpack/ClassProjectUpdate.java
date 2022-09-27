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
 */package REVOpack;
 
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.Database;
import REVOpager.EVOpagerDBconnection;
import REVOpager.Server;
import REVOpager.field;
import REVOpager.table;
import REVOsetup.ErrorLogger;
import java.io.IOException;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/*
ATTENZIONE
2022-01-15
questa classe è stata sostituita da ClassQPmanageUpdate che non fa più uso della connessione diretta 
su porta 3306 aul server queenpro, ma interroga un servizio che risponde con le info necessarie

*/

public class ClassProjectUpdate {

    int flagTimestampUsed = 0;

    ErrorLogger el;

    public String mode = "";
    String feed = "";
    String feedback = "";
    EVOpagerParams myParams;
    Settings mySettings;
    String PROJECT_ID;
    String LOCAL_SERVER_URL;
    String LOCAL_SERVER_ALTURL;
    //String engineDB;
    //String gesDB;

    public ClassProjectUpdate(EVOpagerParams xParams, Settings xSettings) {
        this.myParams = new EVOpagerParams();
        this.mySettings = xSettings;
        this.myParams = xParams; 
        el = new ErrorLogger(myParams, mySettings);
        el.setPrintOnScreen(true);
        el.setPrintOnLog(true);
        el.log(PROJECT_ID, "creando oggetto...");
        this.mode = "TOTAL";
        PROJECT_ID = " " + myParams.getCKprojectName();
        LOCAL_SERVER_URL = mySettings.getData_defaultSQLserver();
        LOCAL_SERVER_ALTURL = mySettings.getData_alternativeSQLserver();
        //  engineDB = "gaiaEngineSetter_ffs";
        // gesDB = "gaiaEngineSetter";
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String autoUpdate() throws IOException {

        //String url = "http://localhost:7080/gaiaEngineSetter/updaterGaia";
        System.out.println("\n\n-----SONO IN autoUpdate() .!.\n----\n\n");
        el.log("ClassProjectUpdate", "SONO IN autoUpdate()");
//        engineDB = "gaiaEngineSetter_ffs";
//        feed = "\n>>>>>>>>>>\n autoUpdate. mode:" + mode + " - ENGINEsetterDB:" + engineDB + "\n>>>>>>>>>>\n";
//        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

        //1. retreive version & release in EVO archive
        el.log("ClassProjectUpdate", "creo connettore");
        EVOpagerDBconnection EVODBconn = new EVOpagerDBconnection(myParams, mySettings);
        el.log("ClassProjectUpdate", "eseguo connessione");
        Connection evoconny = EVODBconn.ConnGesStandardDB();
// si connette a gaiaEngineSetter_ffs usando il server remoro mySettings.getRevolution_defaultSQLserver()
        el.log("ClassProjectUpdate", "connessione eseguita");

        String databaseID = "";
        String EVOprjVersion = "";
        String EVOprjRelease = "";
        String EVOminSWrelease = "";
        int success = 0;
        try {
            Statement evos = evoconny.createStatement();
            String SQLphrase = "SELECT * FROM `databases` WHERE `name`='" + myParams.getCKprojectName() + "';";
            System.out.println("->autoUpdate() ->SQLphrase: " + SQLphrase);
            ResultSet evors = evos.executeQuery(SQLphrase);
            while (evors.next()) {
                databaseID = evors.getString("ID");
                EVOprjVersion = evors.getString("version");
                EVOprjRelease = evors.getString("release");
                EVOminSWrelease = evors.getString("minSWrelease");
            }
            evoconny.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
            feed = ex.toString();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            success = -10;

        }
        System.out.println("EVOprjVersion: " + EVOprjVersion + " - EVOprjRelease: " + EVOprjRelease);
        el.log(PROJECT_ID, "EVOprjVersion: " + EVOprjVersion + " - EVOprjRelease: " + EVOprjRelease);
        //=================================================================================
        //2. retreive version & release in LOCAL archive
        int authorize = 0;

        myParams.printParams(" ClassProjectUpdate-autoUpdate");
        System.out.println("\n------------\nConnessione al DB locale con i seg. parametri: ");

        Connection localconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        String LOCALprjVersion = "";
        String LOCALprjRelease = "";
        String LOCALSwVersion = "";
        if (localconny != null) {
            try {
                Statement locals = localconny.createStatement();
                String SQLphrase = "SELECT * FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='FE_version';";
                System.out.println("->autoUpdate() ->SQLphrase: " + SQLphrase);

                ResultSet localrs = locals.executeQuery(SQLphrase);
                while (localrs.next()) {
                    LOCALprjVersion = localrs.getString("infoValue");
                    break;
                }
                SQLphrase = "SELECT * FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='FE_release';";
                System.out.println("->autoUpdate() ->SQLphrase: " + SQLphrase);

                localrs = locals.executeQuery(SQLphrase);
                while (localrs.next()) {
                    LOCALprjRelease = localrs.getString("infoValue");
                    break;
                }

                LOCALSwVersion = mySettings.getSoftwareVersion();

            } catch (SQLException ex) {
                Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
                // non trovo info riguardo a version e release;
                System.out.println("\nnon trovo info riguardo a version e release\n ");
                feed = ex.toString();

                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                authorize = 1;
            }
        } else {
            System.out.println("\nImpossibile connettersi al DB\n ");
            feed = "\nImpossibile connettersi al DB\n ";

            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            authorize = 1;
        }

        if (LOCALSwVersion == null || LOCALSwVersion.length() < 1 || LOCALprjVersion == null || LOCALprjVersion == "" || LOCALprjRelease == null || LOCALprjRelease == "") {
            authorize = 1;
        } else {
            int locVer = 0;
            int evoVer = 0;
            int locRel = 0;
            int evoRel = 0;
            int locSW = 0;
            int evoSW = 0;

            try {
                locVer = Integer.parseInt(LOCALprjVersion);
            } catch (Exception e) {
            }
            try {
                evoVer = Integer.parseInt(EVOprjVersion);
            } catch (Exception e) {
            }
            try {
                locRel = Integer.parseInt(LOCALprjRelease);
            } catch (Exception e) {
            }
            try {
                evoRel = Integer.parseInt(EVOprjRelease);
            } catch (Exception e) {
            }
            try {
                locSW = Integer.parseInt(LOCALSwVersion);
            } catch (Exception e) {
            }
            try {
                evoSW = Integer.parseInt(EVOminSWrelease);
            } catch (Exception e) {
            }
            String causale = "";

            if (locSW >= evoSW) {
                feed = ">>>>>>>>>>VERSIONE DEL SOFTWARE ADEGUATA ALL'AGGIORNAMENTO DEL TEMPLATE.";
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                if (evoVer > locVer && (locSW >= evoSW)) {
                    authorize++;
                }
                if ((evoVer == locVer) && (evoRel > locRel) && (locSW >= evoSW)) {
                    authorize++;
                }
                if (authorize > 0) {
                    feed = ">>>>>>>>>>AGGIORNAMENTO DEL TEMPLATE DISPONIBILE.";
                    feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                }

            } else {
                feed = ">>>>>>>>>>VERSIONE DEL SOFTWARE INADEGUATA ALL'AGGIORNAMENTO DEL TEMPLATE.";
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

            }
            feed = ">>>>>>>>>>LOCAL V. " + locVer + "." + locRel + " SW Rel. :" + LOCALSwVersion + ".";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            feed = ">>>>>>>>>>EVO   V. " + evoVer + "." + evoRel + " SW min Rel. :" + EVOminSWrelease + ".";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

        }

        System.out.println(">>>>>>>>>>authorize:" + authorize);
        if (mode.equalsIgnoreCase("FORCEDTOTAL")) {
            authorize = 1;
            mode = "TOTAL";
            feed = ">>>>>>>>>>AGGIORNAMENTO DEL TEMPLATE E DELLE TABELLE FORZATO DALL'UTENTE.";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }
        if (mode.equalsIgnoreCase("FORCED")) {
            authorize = 1;
            mode = "";
            feed = ">>>>>>>>>>AGGIORNAMENTO DEL TEMPLATE FORZATO DALL'UTENTE.";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }

        //3. EXECUTE UPDATE  
        if (authorize > 0) {

            feed = ">>>>>>>>>>ESEGUO UPDATE. mode:" + mode;
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

            if (mode.equalsIgnoreCase("TOTAL")) {

                feed = "\n\n--------- 1. MAKE MODEL";
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                success += makeModel(myParams);
            }
            if (!mode.equalsIgnoreCase("LIGHT")) {
                feed = "\n\n--------- 2. normalizeFEtbContent";
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                success += normalizeFEtbContent(myParams, mySettings);

            }

            success++;

        } else {
            feed = ">>>>>>>>>>NON OCCORRE UPDATE.";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            success = 0;
        }

        if (success > 0) {

            feed = "\n\n--------- 3. CONCLUDO CREANDO E COMPLETANDO EVO DIRECTIVES (in locale).";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            success += fillEVOdirectives(myParams, mySettings, EVOprjVersion, EVOprjRelease);

        } else {
            feed = "\n\n--------- 3. NON AGGIORNO EVO DIRECTIVES in quanto non è avvenuto l'update.";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }

        try {
            if (localconny != null) {
                localconny.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
            feed = "ERRORE:" + ex.toString();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }

        feed = "Procedura Conclusa.";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

        if (mode.equalsIgnoreCase("LIGHT")) {
            // feedback="";
        }

        return feedback;
    }

    public String verifyUpdate() { // eseguita durante i login per aggiornare informazioni server->local
        feed = "SONO IN EVO verifyUpdate. ";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

        System.out.println("DATABASE_PREFIX " + myParams.getCKprojectGroup());
        System.out.println("CKprojectName " + myParams.getCKprojectName());
        System.out.println("CKcontextID " + myParams.getCKcontextID());

        //1. retreive version & release in EVO archive
        Connection evoconny = new EVOpagerDBconnection(myParams, mySettings).ConnGesStandardDB();
        String prjID = "";
        String EVOprjVersion = "";
        String EVOprjRelease = "";
        String EVOminSWrelease = "";
        int success = 0;
        try {
            Statement evos = evoconny.createStatement();
            String SQLphrase = "SELECT * FROM `projects` WHERE `name`='" + myParams.getCKprojectName() + "';";
            System.out.println("verifyUpdate--->SQLphrase: " + SQLphrase);
            ResultSet evors = evos.executeQuery(SQLphrase);
            while (evors.next()) {
                prjID = evors.getString("ID");
                EVOprjVersion = evors.getString("version");
                EVOprjRelease = evors.getString("release");
                EVOminSWrelease = evors.getString("minSWrelease");
            }

            evoconny.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
            feed = ex.toString();

            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            success = -10;
        }
        System.out.println("EVOprjVersion: " + EVOprjVersion + " X EVOprjRelease: " + EVOprjRelease);

        //=================================================================================
        //2. retreive version & release in LOCAL archive
        int authorize = 0;
        System.out.println("Eseguo connessione FE. ");
        Connection localconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        String LOCALprjVersion = "";
        String LOCALprjRelease = "";
        String LOCALSwVersion = "";
        if (localconny != null) {
            try {
                Statement locals = localconny.createStatement();
                String SQLphrase = "SELECT * FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='FE_version';";
                ResultSet localrs = locals.executeQuery(SQLphrase);
                while (localrs.next()) {
                    LOCALprjVersion = localrs.getString("infoValue");
                    break;
                }
                SQLphrase = "SELECT * FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='FE_release';";
                localrs = locals.executeQuery(SQLphrase);
                while (localrs.next()) {
                    LOCALprjRelease = localrs.getString("infoValue");
                    break;
                }
                /*   
                 SQLphrase = "SELECT * FROM `EVO_directives` WHERE `infoName`='SW_version';";
                 localrs = locals.executeQuery(SQLphrase);
                 while (localrs.next()) {
                 LOCALSwVersion = localrs.getString("infoValue");
                 break;
                 }
                 */
                LOCALSwVersion = mySettings.getSoftwareVersion();
            } catch (SQLException ex) {
                Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
                // non trovo info riguardo a version e release;
                System.out.println("\nnon trovo info riguardo a version e release\n ");
                feed = ex.toString();

                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                authorize = 1;
            }
        } else {
            System.out.println("\nImpossibile connettersi al DBlocale (FE). Viene comunque autorizzata la prosecuzione.\n ");
            feed = "\nImpossibile connettersi al DBlocale (FE). Viene comunque autorizzata la prosecuzione.\n ";

            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            authorize = 1;
        }

        if (LOCALSwVersion == null || LOCALSwVersion.length() < 2 || LOCALprjVersion == null || LOCALprjVersion == "" || LOCALprjRelease == null || LOCALprjRelease == "") {
            authorize = 1;
        } else {
            int locVer = 0;
            int evoVer = 0;
            int locRel = 0;
            int evoRel = 0;
            int locSW = 0;
            int evoSW = 0;

            try {
                locVer = Integer.parseInt(LOCALprjVersion);
            } catch (Exception e) {
            }
            try {
                evoVer = Integer.parseInt(EVOprjVersion);
            } catch (Exception e) {
            }
            try {
                locRel = Integer.parseInt(LOCALprjRelease);
            } catch (Exception e) {
            }
            try {
                evoRel = Integer.parseInt(EVOprjRelease);
            } catch (Exception e) {
            }
            try {
                locSW = Integer.parseInt(LOCALSwVersion);
            } catch (Exception e) {
            }
            try {
                evoSW = Integer.parseInt(EVOminSWrelease);
            } catch (Exception e) {
            }

            if (evoVer > locVer && (locSW >= evoSW)) {
                authorize++;
            }
            if ((evoVer == locVer) && (evoRel > locRel) && (locSW >= evoSW)) {
                authorize++;
            }
            feed = ">>>>>>>>>>LOCAL V. " + locVer + "." + locRel + " SW Rel. :" + LOCALSwVersion + ".";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            feed = ">>>>>>>>>>EVO   V. " + evoVer + "." + evoRel + " SW min Rel. :" + EVOminSWrelease + ".";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

        }
        System.out.println(">>>>>>>>>>authorize:" + authorize);

        if (authorize > 0) {
            feed = ">>>>>>>>>>UPDATE DISPONIBILE. mode:" + mode;
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        } else {
            feed = ">>>>>>>>>>UPDATE NON DISPONIBILE.";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }

        // non voglio che si aggiornino in locale i vakiri di versione e release
        // perchè qui in realtà non eseguo un update
        EVOprjVersion = LOCALprjVersion;
        EVOprjRelease = LOCALprjRelease;

        feed = "\n\n--------- STEP:Compilo LOCAL EVO DIRECTIVES (da EVO in locale).";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        success += fillEVOdirectives(myParams, mySettings, EVOprjVersion, EVOprjRelease);

        try {
            localconny.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
            feed = "ERRORE:" + ex.toString();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }

        feed = "Procedura Conclusa.";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

        if (mode.equalsIgnoreCase("LIGHT")) {
            // feedback="";
        }

        if (mode.equalsIgnoreCase("LIGHT")) {
            String HTMLcode = "";

            HTMLcode += "<TABLE><TR><TD><A onclick=\"javascript:showDetails()\"><img border=\"0\" src=\"./media/lente.png\" height=\"20\" width=\"20\" onclick=\"javascript:showDetails()\" /></A></TD>";
            HTMLcode += "<TD>";
            HTMLcode += "<DIV id=\"serverResponse\" style=\"display:none;\">";
            HTMLcode += feedback;
            HTMLcode += "</DIV>";
            HTMLcode += "</TD>";
            HTMLcode += "</TR></TABLE>";
            feedback = HTMLcode;
        }

        return feedback;

    }

    public int fillEVOdirectives(EVOpagerParams myParams, Settings mySettings, String EVOprjVersion, String EVOprjRelease) {
        feed = "-> REVOpack.ClassProjectUpdate.fillEVOdirectives";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        //CONNESSIONE SERVER//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Server myServer = new Server(mySettings);
        // Database myDatabase = new Database(myParams, mySettings);
        //  EVOpagerDBconnection myDBC = new EVOpagerDBconnection();
        //  Connection localconny = myDBC.makeConnection(myServer, myDatabase);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Connection localconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();

        Statement locals;
        String SQLphrase = null;
        try {
            locals = localconny.createStatement();

            //
            SQLphrase = "CREATE TABLE IF NOT EXISTS " + mySettings.getLocalEVO_directives() + " ("
                    + "  `ID` int(11) NOT NULL AUTO_INCREMENT,\n"
                    + "  `infoName` varchar(128) DEFAULT NULL,\n"
                    + "  `infoValue` text,\n"
                    + "  `instanceInfoValue` text,\n"
                    + "  `note` text,\n"
                    + "  `rifProjects` varchar(64) DEFAULT NULL,\n"
                    + "  `instance` varchar(256) DEFAULT 'ALL',\n"
                    + "  `superGroup` varchar(256) DEFAULT 'FrontEnd',\n"
                    + "  `group` varchar(256) DEFAULT 'Aspect',\n"
                    + "  `modifiable` varchar(256) DEFAULT 'DEFAULT:FALSE',\n"
                    + "  PRIMARY KEY (`ID`)\n"
                    + ") ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;";
            feed = "->  " + SQLphrase;
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            int i = locals.executeUpdate(SQLphrase);

            SQLphrase = "DELETE FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='FE_version';";
            i = locals.executeUpdate(SQLphrase);
            SQLphrase = "DELETE FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='FE_release';";
            i = locals.executeUpdate(SQLphrase);
            SQLphrase = "INSERT INTO " + mySettings.getLocalEVO_directives() + "  (`infoName`,`infoValue`) VALUES ('FE_version','" + EVOprjVersion + "');";
            feed = "SQLphrase:" + SQLphrase;
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            i = locals.executeUpdate(SQLphrase);
            SQLphrase = "INSERT INTO " + mySettings.getLocalEVO_directives() + "  (`infoName`,`infoValue`) VALUES ('FE_release','" + EVOprjRelease + "');";
            feed = "SQLphrase:" + SQLphrase;
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            i = locals.executeUpdate(SQLphrase);

            Connection evoconny = new EVOpagerDBconnection(myParams, mySettings).ConnGesStandardDB();
            String prjID = null;
            int lines = 0;

            ArrayList<evoDirective> directives = new ArrayList<evoDirective>();
            try {
                Statement s = evoconny.createStatement();
                SQLphrase = "SELECT * FROM `gEVO_directives` WHERE rifProjects='" + myParams.getCKprojectName() + "' ";

                String instance = "";
                if (myParams.getCKcontextID() != null && !myParams.getCKcontextID().equalsIgnoreCase("null")) {
                    instance = myParams.getCKcontextID();
                    SQLphrase += " AND instance='" + instance + "';";

                } else {
                    SQLphrase += " AND ( instance IS NULL OR  instance='' )";
                }

                // SQLphrase = "SELECT * FROM `gEVO_directives` WHERE rifProjects='" + myParams.getCKprojectName() + "' AND instance='" +instance + "';";
                feed = "SQLphrase:" + SQLphrase;
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                ResultSet rs = s.executeQuery(SQLphrase);

                String defaultValue = "";
                while (rs.next()) {
                    lines++;

                    evoDirective myDirective = new evoDirective();
                    try {
                        myDirective.setInfoName(rs.getString("infoName"));
                    } catch (Exception e) {
                    }
                    feed = "FOUND:" + myDirective.getInfoName();
                    feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

                    try {
                        myDirective.setInfoValue(rs.getString("infoValue"));
                    } catch (Exception e) {
                    }

                    try {
                        myDirective.setGroup(rs.getString("group"));
                    } catch (Exception e) {
                    }

                    try {
                        myDirective.setSuperGroup(rs.getString("superGroup"));
                    } catch (Exception e) {
                    }

                    try {
                        myDirective.setNote(rs.getString("note"));
                    } catch (Exception e) {
                    }

                    try {
                        myDirective.setInstanceInfoValue(rs.getString("instanceInfoValue"));
                    } catch (Exception e) {
                    }

                    directives.add(myDirective);

                }

                evoconny.close();
            } catch (SQLException ex) {
                feed = "ERRORE IN CONNESSIONE CON GES...  " + ex.toString();
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            }

            for (int jj = 0; jj < directives.size(); jj++) {
                SQLphrase = "DELETE FROM " + mySettings.getLocalEVO_directives() + " WHERE `infoName`='" + directives.get(jj).getInfoName() + "';";
                i = locals.executeUpdate(SQLphrase);
                SQLphrase = "INSERT INTO " + mySettings.getLocalEVO_directives() + "  (`infoName`,`infoValue`) "
                        + "VALUES "
                        + "('" + directives.get(jj).getInfoName() + "','" + directives.get(jj).getInfoValue() + "');";
                feed = "SQLphrase:" + SQLphrase;
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                i = locals.executeUpdate(SQLphrase);

            }

            localconny.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
            feed = "ERRORE:" + ex.toString();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }

        return 0;
    }

    public int normalizeFEtbContent(EVOpagerParams myParams, Settings mySettings) {

        String CKprojectName = myParams.getCKprojectName();
        String CKcontextID = myParams.getCKcontextID();

        feed = "================================================";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        feed = "SONO IN EVO normalizeFEtbContent. ";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        feed = "SARANNO POPOLATE IN LOCALE LE TABELLE FE. ";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

        feed = "================================================";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

        Connection localconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();

        Connection evoconny = new EVOpagerDBconnection(myParams, mySettings).ConnGesStandardDB();
        String prjID = "";
        try {
            Statement evos = evoconny.createStatement();
            String SQLphrase = "SELECT * FROM `projects` WHERE `name`='" + CKprojectName + "';";
            System.out.println("normalizeFEtbContent_SQLphrase: " + SQLphrase);
            ResultSet evors = evos.executeQuery(SQLphrase);
            while (evors.next()) {
                prjID = evors.getString("ID");

            }
            System.out.println("Progetto ID: " + prjID);
            // cerco il database che si chiama come il mio progetto
            String dbID = "";
            SQLphrase = "SELECT * FROM `databases` WHERE `rifProject`='" + prjID + "' AND `name`='" + CKprojectName + "'";

            System.out.println("Cerco in EVO i DB: " + SQLphrase);
            evors = null;
            evors = evos.executeQuery(SQLphrase);
            while (evors.next()) {
                dbID = evors.getString("ID");

            }
            System.out.println("Trovato: " + dbID);
            String tableID;

            SQLphrase = "SELECT * FROM `tables` WHERE `rifDatabase`='" + dbID + "'  ;";
            evors = null;
            evors = evos.executeQuery(SQLphrase);
            System.out.println("SQLphrase: " + SQLphrase);
            ArrayList<EvoTable> tabs = new ArrayList<EvoTable>();
            while (evors.next()) {
                tableID = evors.getString("ID");
                String tableName = evors.getString("name");
                System.out.println("Tabella: " + tableName);
                if (tableName != null && tableName != "" && tableName.length() > 3
                        && tableName.substring(0, 4).equalsIgnoreCase("gFE_")) {
                    System.out.println("Da aggiungere!");
                    EvoTable myTable = new EvoTable();
                    myTable.name = tableName;
                    myTable.ID = tableID;
                    // adesso apro la tabella e copio l'elenco dei campi per fare il cast in myTable
                    Statement evosFields = evoconny.createStatement();
                    SQLphrase = "SELECT * FROM fields WHERE rifTable='" + tableID + "'  ;";
                    ResultSet evorsFields = evosFields.executeQuery(SQLphrase);
                    while (evorsFields.next()) {
                        EvoField myField = new EvoField();
                        myField.name = evorsFields.getString("name");
                        myField.type = evorsFields.getString("type");
                        myTable.field.add(myField);
                    }

                    tabs.add(myTable);// contiene tutte le tabelle da clonare

                }

            }
            System.out.println("\n-------\nTrovate " + tabs.size() + " tabelle tecniche Front-end da ripopolare.");
            // la struttura delle tabelle è già giusta perchè garantita dalla funzione
            // makeModel(String CKprojectName, String CKcontextID) che viene eseguita per prima
            // quindi mi posso concentrare sul ripopolamento.

            // 1. prendo il nome della prima tabella e in locale la svuoto
            Statement locals = localconny.createStatement();
            //=============  CICLO PER CONENUTI RELATIVI AL PROGETTO SPECIFICO    

            int flagBLOB = 0;
            for (int tbl = 0; tbl < tabs.size(); tbl++) {
                System.out.println("\nTABELLA:" + tabs.get(tbl).name);
                for (int jj = 0; jj < tabs.get(tbl).field.size(); jj++) {
                    System.out.println("    Field " + jj + ":" + tabs.get(tbl).field.get(jj).name + " (" + tabs.get(tbl).field.get(jj).type + ")");
                    if (tabs.get(tbl).field.get(jj).type.equalsIgnoreCase("BLOB")) {
                        flagBLOB++;
                    }

                }
                String localFEtableName = tabs.get(tbl).name + "_" + mySettings.getProjectName();
                System.out.println("\nsvuoto la tabella esistente (se esiste) :" + tabs.get(tbl).name);

                SQLphrase = "TRUNCATE TABLE `" + localFEtableName + "`  ;";
                int result = locals.executeUpdate(SQLphrase);

                //2. copio il contenuto della tabella in evo dentro la tabella in local    
                /*
                 INSERT INTO `FE_frames`(`ID`, `Name`, `rifScreen`, `type`, `position`, `size`, `sizeType`) 
                 VALUES ([value-1],[value-2],[value-3],[value-4],[value-5],[value-6],[value-7])
                 */
                SQLphrase = "SELECT * FROM `" + tabs.get(tbl).name + "` WHERE `rifDatabase`='" + dbID + "' OR `rifDatabase`='ALL' ORDER BY rifDatabase ;";

                System.out.println("Cerco i valori in EVO:" + SQLphrase);
                evors = null;
                evors = evos.executeQuery(SQLphrase);
                // inserisco i valori trovati in EVO per questo progetto nella tabella corrispondente
                int lines = 0;
                int firstinserted = 0;
                while (evors.next()) {
                    //----per ogni riga in EVO
                    lines++;
                    SQLphrase = "INSERT INTO `" + localFEtableName + "`(";
                    firstinserted = 0;
                    for (int jj = 0; jj < tabs.get(tbl).field.size(); jj++) {
                        if (tabs.get(tbl).field.get(jj).type.equalsIgnoreCase("BLOB")) {

                        } else {
                            if (jj > 0 && firstinserted > 0) {
                                SQLphrase += ", ";
                            }
                            firstinserted++;
                            SQLphrase += "`" + tabs.get(tbl).field.get(jj).name + "`";
                        }
                    }

                    SQLphrase += ")VALUES(";
                    firstinserted = 0;
                    for (int jj = 0; jj < tabs.get(tbl).field.size(); jj++) {
                        if (tabs.get(tbl).field.get(jj).type.equalsIgnoreCase("BLOB")) {

                        } else {
                            if (jj > 0 && firstinserted > 0) {
                                SQLphrase += ", ";
                            }
                            firstinserted++;
                            SQLphrase += "?";
                        }
                    }

                    SQLphrase += ");";
                    PreparedStatement statement = localconny.prepareStatement(SQLphrase);
                    // compilo i  punti interrogativi dello statement...
                    firstinserted = 0;
                    for (int jj = 0; jj < tabs.get(tbl).field.size(); jj++) {
                        if (tabs.get(tbl).field.get(jj).type.equalsIgnoreCase("BLOB")) {

                        } else {
                            firstinserted++;
                            String newValue = "";
                            String newType = "varchar";
                            newValue = evors.getString(tabs.get(tbl).field.get(jj).name);
                            newType = tabs.get(tbl).field.get(jj).type;
                            if (newType.equalsIgnoreCase("INT")) {
                                int numberValue = 0;
                                try {
                                    numberValue = Integer.parseInt(newValue);
                                } catch (Exception e) {
                                    numberValue = 0;
                                }
                                statement.setInt(firstinserted, numberValue);
                            } else {
                                statement.setString(firstinserted, newValue);
                            }
                        }

                    }

                    System.out.println("LI RICOPIO IN LOCALE." + localFEtableName);
                    result = statement.executeUpdate();
                    statement.close();

                }
                System.out.println("Per questa tabella trovate in EVO " + lines + " linee");

                if (flagBLOB > 0) {
                    System.out.println("SONO PRESENTI CAMPI BLOB DA RIPORTARE IN LOCALE:");

                    SQLphrase = "SELECT * FROM `" + tabs.get(tbl).name + "` WHERE `rifDatabase`='" + dbID + "'  ;";

                    System.out.println("Cerco i valori in EVO:" + SQLphrase);
                    evors = null;
                    evors = evos.executeQuery(SQLphrase);
                    while (evors.next()) {
                        String currentIndex = evors.getString("ID");

                        for (int jj = 0; jj < tabs.get(tbl).field.size(); jj++) {
                            if (tabs.get(tbl).field.get(jj).type.equalsIgnoreCase("BLOB")) {
                                Blob blob = null;

                                blob = evors.getBlob(tabs.get(tbl).field.get(jj).getName());

                                if (blob != null) {
                                    InputStream in = blob.getBinaryStream();

                                    String sql = "UPDATE " + localFEtableName + " SET " + tabs.get(tbl).field.get(jj).getName() + " = ? WHERE ID ='" + currentIndex + "'";

                                    System.out.println(sql);
                                    PreparedStatement statement = localconny.prepareStatement(sql);
                                    statement.setBlob(1, in);
                                    int row = statement.executeUpdate();

                                }
                            }
                        }

                    }

                }

            }

        } catch (SQLException ex) {
            feed = "ERRORE ...  " + ex.toString();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }

        try {
            localconny.close();
            evoconny.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
            feed = "ERRORE IN CHIUSURA CONNESSIONE LOCALE...  " + ex.toString();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }

        return 1;
    }
 
    public int makeModel(EVOpagerParams myParams) {

        String CKprojectName = myParams.getCKprojectName();
        String CKcontextID = myParams.getCKcontextID();

        boolean newMadeDatabase = false;
        System.out.println("+-----------------------------------------------------------+");
        System.out.println("+---PROCEDURA MAKE MODEL------------------------------------+");
        System.out.println("+-----------------------------------------------------------+");
        System.out.println("+-----------------------------------------------------------+");
        feed = "PROCEDURA MAKE MODEL.";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

//CONNESSIONE SERVER//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Server myServer = new Server(mySettings);
        Database myDatabase = new Database(myParams, mySettings);
        Connection conny = null;
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        feed = "dbName = \"" + myDatabase.getDbExtendedName() + "\"\n\n\n";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

        int connectionOK = 0;
        try {
            conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
            connectionOK = 1;
            conny.close();
        } catch (Exception e) {
            conny = null;
            connectionOK = 0;
            newMadeDatabase = true;
        }

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++           
        if (connectionOK < 1) {
            feed = "IL DB LOCALE NON ESISTE ";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            // qui valuto Token di permesso di costruire :-p
            newMadeDatabase = true;
        } else {
            feed = "IL DB LOCALE ESISTE GIà ";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }
        // creo un model per il nuovo DB
        // basandomi sui dati presi dal DB evolutionDefinitioons

        String EVOLUTION_SERVER_URL = mySettings.getRevolution_defaultSQLserver();
        //  String EVOLUTION_SERVER_ALTURL = mySettings.getRevolution_alternativeSQLserver();
        //  String DATABASE_PREFIX = myParams.getCKprojectGroup();

        feed = "CONNESSIONE CON EVOLUTION...  " + EVOLUTION_SERVER_URL;
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

        Connection evoconny = new EVOpagerDBconnection(myParams, mySettings).ConnGesStandardDB();
        String prjID = null;
        int lines = 0;
        try {
            Statement s = evoconny.createStatement();
            String SQLphrase = "SELECT * FROM projects WHERE name='" + CKprojectName + "';";
            ResultSet rs = s.executeQuery(SQLphrase);

            String defaultValue = "";
            while (rs.next()) {
                lines++;
                prjID = rs.getString("ID");
            }
        } catch (SQLException ex) {
            feed = "ERRORE IN CONNESSIONE CON EVOLUTION...  " + ex.toString();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }

        System.out.println("CERCATO SU EVOLUTION il progetto corrispondente a  " + CKprojectName);
        if (lines < 1 || prjID == null) {
            feed = "Non esiste nessun project in EVOLUTION con questo nome: " + CKprojectName;
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            return 0;
        }
        System.out.println("In EVOLUTION esiste un progetto con questo nome ");
        feed = "In EVOLUTION esiste un progetto con questo nome " + CKprojectName;
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        String[] dbID = new String[10];
        String[] dbNm = new String[10];
        // cerco i database per questo progetto (di solito è uno solo)
        int DBlines = 0;
        try {

            Statement s = evoconny.createStatement();
            String SQLphrase = "SELECT * FROM `databases` WHERE rifProject='" + prjID + "' AND name='" + CKprojectName + "'  ;";
            System.out.println("SQLphrase:  " + SQLphrase);
            ResultSet rs = s.executeQuery(SQLphrase);

            String defaultValue = "";
            while (rs.next()) {
                DBlines++;
                dbID[DBlines] = rs.getString("ID");
                dbNm[DBlines] = rs.getString("name");
            }
        } catch (SQLException ex) {
            feed = "ERRORE lettura databases in EVOLUTION...  " + ex.toString();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }
        //System.out.println("In EVOLUTION il progetto contiene " + DBlines + " database.");
        // per ognuno dei database apro le tabelle corrispondenti e in ogni tabella faccio il cast dei suoi fields
        ///myServer = new server("mySql", "STANDARD_URL", "STANDARD_ALT_URL");
        feed = "In EVOLUTION il progetto contiene " + DBlines + " database.";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

//=================================================
//==CICLO PER OGNI DATABASE========================
//=================================================
//CONNESSIONE SERVER//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        Server xServer = new Server(mySettings);

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //   for (int thisDB = 1; thisDB <= DBlines; thisDB++) {
        // creo l'oggetto DB su cui fare il cast-----------------------
        //                    database(server server, String protocol, String group, String name, String ID)
        int thisDB = 1;// annullando l'eventualità di molteplici DB scelgo solo il primo dela lista
        String instanceName = dbNm[thisDB];

        EVOpagerParams xParams = new EVOpagerParams();
        xParams.setCKprojectGroup(myParams.getCKprojectGroup());
        xParams.setCKprojectName(instanceName);
        xParams.setCKcontextID(myParams.getCKcontextID());
        Settings revoSettings = new Settings();
        revoSettings.setProjectDB(instanceName);
        Database toCastDatabase = new Database(xParams, revoSettings);

        //--------------------------------------------------------------
        feed = "Database " + thisDB + ")" + instanceName + " viene caricato.";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        table[] toCastTable = new table[200];

        int tabLines = 0;
        try {
            Statement s = evoconny.createStatement();
            String SQLphrase = "SELECT * FROM tables WHERE rifDatabase='" + dbID[thisDB]
                    + "' ORDER BY name;";
            ResultSet rs = s.executeQuery(SQLphrase);

            String defaultValue = "";
            while (rs.next()) {
                tabLines++;
                String newName = rs.getString("name");
                String newID = rs.getString("ID");
                toCastTable[tabLines] = new table(newName);
                toCastTable[tabLines].setRifDatabase(dbID[thisDB]);
                toCastTable[tabLines].setID(newID);
                feed = "Tabella " + tabLines + ":" + newID + "]" + newName;
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            }
        } catch (SQLException ex) {
            feed = "ERRORE NEL CASTING DELLE TABELLE DA EVO. " + ex.toString();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }
        System.out.println("Trovate " + tabLines + " tabelle nel modello.");
        feed = "Trovate " + tabLines + " tabelle nel modello.";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        // reparsing delle tabelle per cercare i fields e fare il cast
        feed = "\n\n\nreparsing delle tabelle per cercare i fields e fare il cast";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

//=================================================
//==CICLO PER OGNI TABLE===========================
//=================================================                
        for (int thisTable = 1; thisTable <= tabLines; thisTable++) {
            System.out.println("parsing tabella EVO " + thisTable + " ..." + toCastTable[thisTable].getID() + "]" + toCastTable[thisTable].getName());
            try {
                Statement s = evoconny.createStatement();
                String SQLphrase = "SELECT * FROM fields WHERE rifTable='" + toCastTable[thisTable].getID() + "' ORDER BY position;";
                ResultSet rs = s.executeQuery(SQLphrase);
                while (rs.next()) {
                    String name = rs.getString("name");
                    String ID = rs.getString("ID");
                    String rifTable = toCastTable[thisTable].getID();
                    String defaultValue = rs.getString("defaultValue");
                    String type = rs.getString("type");
                    int lenght = rs.getInt("length");
                    int autoIncrement = rs.getInt("autoIncrement");
                    int primary = rs.getInt("primary");
                    int notNull = rs.getInt("notNull");
                    int position = rs.getInt("position");
                    //field(String name, String type, int length)
                    field thisField = new field(name, type, lenght);

                    if (autoIncrement > 0) {
                        thisField.setAutoIncrement(Boolean.TRUE);
                    } else {
                        thisField.setAutoIncrement(Boolean.FALSE);
                    }
                    if (primary > 0) {
                        thisField.setPrimary(Boolean.TRUE);
                    } else {
                        thisField.setPrimary(Boolean.FALSE);
                    }
                    if (notNull > 0) {
                        thisField.setNotNull(Boolean.TRUE);
                    } else {
                        thisField.setNotNull(Boolean.FALSE);
                    }
                    thisField.setPosition(position);
                    thisField.setDefaultValue(defaultValue);
                    thisField.setID(ID);

                    toCastTable[thisTable].castField(thisField);
                    System.out.println("Eseguito cast del campo " + name + " (" + type + ")");
                }

            } catch (SQLException ex) {
                feed = "ERRORE  ...  " + ex.toString();
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            }

            toCastDatabase.castTable(toCastTable[thisTable]);
            //System.out.println("Eseguito cast della tabella nell'oggetto database  ");
        }

        // ricreo le tabelle 
        feed = "\n\n</BR></BR>Eseguo la procedura di remake per questo DATABASE (completo di tables e fields!) -> " + toCastDatabase.getName() + "</BR></BR>";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

        remakeDB(myServer, toCastDatabase, myDatabase);

        System.out.println("OK! ");

        // }
        System.out.println("newMadeDatabase= " + newMadeDatabase);
        if (newMadeDatabase) {
            Statement s = null;

//CONNESSIONE SERVER//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //in questo caso il DB non esisteva e devo creare l'utente ADMIN e il gruppo superUsers
            String SQLphrase = "INSERT INTO `archivio_operatori` (`ID`, `username`, `name`, `surname`, `alive`, `pincode`, `picture`, `gaiaID`, `grado`, `owner`, `recorded`, `rango`) VALUES ('admin', 'admin', 'admin', 'admin', '1', 'admin', NULL, NULL, '999', NULL, '2017-09-29 08:56:35', '999')";
            try {
                s = conny.createStatement();
                int Result = s.executeUpdate(SQLphrase);

                SQLphrase = "INSERT INTO " + mySettings.getAccount_TABLEgruppi() + " (`IDgruppo`, `descrizione`, `rango`) VALUES ('superAdmin', 'superAdmin', '1000')";
                Result = s.executeUpdate(SQLphrase);
                SQLphrase = "INSERT INTO " + mySettings.getAccount_TABLElinkUserGroups() + "  (`id`, `rifOperatore`, `rifGruppo`) VALUES (NULL, 'admin', 'superAdmin')";
                Result = s.executeUpdate(SQLphrase);
                SQLphrase = "INSERT INTO " + mySettings.getAccount_TABLElinkUserGroups() + " (`id`, `rifOperatore`, `rifGruppo`) VALUES (NULL, 'admin', 'admin')";
                Result = s.executeUpdate(SQLphrase);

            } catch (SQLException ex) {
                feed = "ERRORE IN Connessione a" + myDatabase.getDbExtendedName();
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            }

        }

        try {
            evoconny.close();
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
            feed = "ERRORE  ...  " + ex.toString();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        }
        return 0;
    }

    public int remakeDB(Server myServer, Database myDatabase, Database destinationDatabase) {
        feed = "SONO IN EVO remakeDB. ";
        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
        int tabs = myDatabase.table.size();
        System.out.println("\n\n+-----------------------------------------------------------+");
        System.out.println("+---PROCEDURA REMAKE DATABASE-------------------------------+");
        System.out.println("+TABS:" + tabs);
        System.out.println("+-----------------------------------------------------------+");

        String SQLphrase = "";
        String routine = "";
        try {
            Connection globalConny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalNoDB();
            feed = "Connessione al server LOCALE";
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            Statement gs = globalConny.createStatement();
            routine = "1.CREATE DATABASE IF NOT EXISTS. ";
            //String dbName=myDatabase.getGroup()+"_"+myDatabase.getName()+"_"+myDatabase.getID();
            System.out.println("+--------------------------------------------------------------+");
            System.out.println("CREO IL DATABASE:" + destinationDatabase.getDbExtendedName());
            int Result = gs.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + destinationDatabase.getDbExtendedName() + "`");
            System.out.println("Result:" + Result);
            //System.out.println("+--------------------------------------------------------------+");
            //System.out.println("Connessione allo schema mySQL");
            /* Connection schemaconny = myDBC.makeConnection(myServer, "information_schema");
             Statement schemast = schemaconny.createStatement();
            
             */

            //MI CONNETTO ALLO SCHEMA LOCALE
            Connection schemaconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalSchema();
            Statement schemast = schemaconny.createStatement();
            //System.out.println("OK 1");
            //System.out.println("+--------------------------------------------------------------+");
            //System.out.println("Connessione al database nuovo creato:" + myDatabase.getDbExtendedName());

            feed = "@@Connessione al database nuovo creato[conny]:" + destinationDatabase.getDbExtendedName();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

            System.out.println("OK 2\n" + feed);
            System.out.println("+--------------------------------------------------------------+");
            int nofTables = myDatabase.getTables().size();

            System.out.println("+--------------------------------------------------------------+");
            flagTimestampUsed = 0;
            feed = "NUMERO TABELLE:" + nofTables;
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            feed = "@@Usando le Settings del sw:" + mySettings.getProjectDB();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

//CONNESSIONE SERVER//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
            Statement s = conny.createStatement();
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//=================================================
//==CICLO PER OGNI TABLE===========================
//=================================================                       
            for (int currentTable = 0; currentTable < nofTables; currentTable++) {

                int exists = 0;
                table myTable = myDatabase.getTables().get(currentTable);
                String tableName = myTable.getName();

                if (tableName != null && tableName != "" && tableName.length() > 3
                        && (tableName.substring(0, 4).equalsIgnoreCase("gFE_")
                        || tableName.substring(0, 5).equalsIgnoreCase("gEVO_"))) {

                    tableName = tableName + "_" + mySettings.getProjectName();
                }

                int numFields = myTable.getFields().size();

                feed = "\n\n-----\n>> " + (1 + currentTable) + "/" + nofTables
                        + " . AGGIORNAMENTO TABELLA  ->" + tableName;
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                try {
                    SQLphrase = "SELECT * FROM COLUMNS\n"
                            + "           WHERE TABLE_NAME = '" + tableName + "'\n"
                            + "             AND TABLE_SCHEMA = '" + destinationDatabase.getDbExtendedName() + "';";
                    feed = "RICERCA SCHEMA TABELLA: " + SQLphrase;
                    feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

//ResultSet rs = schemast.executeQuery(SQLphrase); 
                    PreparedStatement ps = schemaconny.prepareStatement(SQLphrase);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        exists++;
                        break;
                    }

                } catch (SQLException ex) {
                    feed = "RICERCA SCHEMA TABELLA: " + SQLphrase;
                    feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                    feed = "ERRORE IN RICERCA SCHEMA TABELLA: " + ex.toString();
                    feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                }
                System.out.println("CONTROLLO ESISTENZA EFFETTUATO: " + exists + " TAB:" + tableName);
//==========SE LA TABELLA NON ESISTE LA CREO ED E' DI SICURO AGGIORNATA===============================                
                if (exists < 1) {
                    feed = "LA TABELLA NON ESISE IN LOCALE. ";
                    feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

                    routine = "2.CREATE TABLE IF NOT EXISTS. ";
                    SQLphrase = "CREATE TABLE IF NOT EXISTS ";
                    SQLphrase += tableName + " ( ";
                    System.out.println("tableName (" + currentTable + "):" + tableName);

                    for (int jj = 0; jj < numFields; jj++) {
                        field myField = myDatabase.getTables().get(currentTable).getFields().get(jj);
                        String name = myField.getName();
                        Boolean primary = myField.getPrimary();
                        if (jj > 0) {
                            SQLphrase += ", ";
                        }
                        SQLphrase += " `" + name + "` ";
                        SQLphrase += parseFieldQuery(myField, jj);

                        if (primary == true) {
                            SQLphrase += " PRIMARY KEY \n";
                        }
                    }
                    SQLphrase += " ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

                    try {
                        Result = s.executeUpdate(SQLphrase); // creazione tabella

                    } catch (SQLException ex) {
                        feed = "943.ERRORE IN Connessione a " + destinationDatabase.getDbExtendedName();
                        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                        feed = "943.ERRORE  " + s.getWarnings();
                        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                        feed = "943.SQLphrase: " + SQLphrase;
                        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

                    }
                    if (Result >= 0) {
                        feed = ">> 2. CREATA TABELLA: " + SQLphrase;
                        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                    } else {
                        feed = ">> 2. ERRORE ROUTINE DI CREAZIONE TABELLA: " + SQLphrase;
                        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                    }

                } else {
//==========SE LA TABELLA ESISTE GIA' VADO A CONTROLLARE L'ESISTENZA DI TUTTI I FIELDSA NECESSARI===============================                    
                    feed = "LA TABELLA ESISTE GIà IN LOCALE. ";
                    feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                    flagTimestampUsed = 0;
                    routine = "3. SELECT * FROM COLUMNS per verificarne esistenza in SCHEMA.";
                    for (int jj = 0; jj < numFields; jj++) {

                        field myField = myDatabase.getTables().get(currentTable).getFields().get(jj);

                        String name = myField.getName();
                        Boolean primary = myField.getPrimary();
                        SQLphrase = "SELECT * FROM COLUMNS\n"
                                + "           WHERE TABLE_NAME = '" + tableName + "'\n"
                                + "             AND TABLE_SCHEMA = '" + destinationDatabase.getDbExtendedName() + "'\n"
                                + "             AND COLUMN_NAME = '" + name + "' ;";
                        //System.out.println("SQLphrase="+SQLphrase);
                        System.out.println(">>> CERCO COLONNA " + name);

                        ResultSet rs = schemast.executeQuery(SQLphrase);
                        int i = 0;
                        while (rs.next()) {
                            i++;
                        }
                        routine = "4. ALTER TABLE PER ADDING FIELDS.";
//==========SE UN FIELD NON ESISTE LO CREO ADESSO===============================                            
                        if (i < 1) {
                            try {
                                System.out.println(">>> COLONNA " + name + " NON ESISTE.");
                                SQLphrase = "  ALTER TABLE `" + tableName + "` ADD ";
                                SQLphrase += " `" + name + "` ";
                                SQLphrase += parseFieldQuery(myField, jj);
                                if (primary == true) {
                                    SQLphrase += " PRIMARY KEY \n";
                                }
                                System.out.println("ROUTINE DI ADD FIELD: " + SQLphrase);
                                Result = s.executeUpdate(SQLphrase); // creazione field 

                            } catch (SQLException ex) {
                                feed = "ROUTINE AGGIUNTA FIELD: " + SQLphrase;
                                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                                feed = "ERRORE IN AGGIUNTA FIELD: " + ex.toString();
                                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                            }

                        }

                    }

                }

                flagTimestampUsed = 0;
                routine = "5. ALTER TABLE PER CHANGING FIELDS.";

//==CHANGE=========================================
//==CICLO PER OGNI FIELD===========================
//=================================================             
                feed = "\n\n\n</BR></BR>-INIZIO FASE DI CHANGING---------------------</BR></BR>\n\n\n";
                feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

                for (int jj = 0; jj < numFields; jj++) {
                    try {
                        field myField = myDatabase.getTables().get(currentTable).getFields().get(jj);
                        String name = myField.getName();

                        feed = "ROUTINE DI CHANGE TABELLA: " + tableName + " FIELD: " + name + "   (" + jj + "/" + numFields + ")";
                        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);

                        SQLphrase = "ALTER TABLE `" + tableName + "` ";
                        SQLphrase += "CHANGE `" + name + "` `" + name + "` ";
                        SQLphrase += parseFieldQuery(myField, jj);
//                        System.out.println("SQL:" + SQLphrase);
                        Result = s.executeUpdate(SQLphrase); // creazione field
                    } catch (SQLException ex) {
                        feed = "ROUTINE DI CHANGE TABELLA: " + SQLphrase;
                        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                        feed = "ERRORE IN CHANGE TABELLA: " + ex.toString();
                        feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
                        Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);

                    } finally {

                    }
                    SQLphrase = "";
                }

            }// fine ciclo per ogni tabella
            globalConny.close();
            schemaconny.close();
            conny.close();
        } catch (Exception ex) {
            feed = "ROUTINE DI CHANGE TABELLA: " + SQLphrase;
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            feed = "ERRORE IN REMAKE DB: " + ex.toString();
            feedback = feedback + "\n" + el.log(PROJECT_ID, feed);
            Logger.getLogger(ClassProjectUpdate.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    public String parseFieldQuery(field myField, int jj) {
        String SQLphrase = "";
        String name = myField.getName();
        String type = myField.getType();
        int length = myField.getLength();
        String defaultValue = myField.getDefaultValue();
        Boolean autoIncrement = myField.getAutoIncrement();
        Boolean primary = myField.getPrimary();
        Boolean notnull = myField.getNotNull();

        Boolean virgolette = Boolean.TRUE;
        Boolean acceptDefault = Boolean.TRUE;
        if (flagTimestampUsed < 0) {
            flagTimestampUsed = 0;
        }
        int flag = flagTimestampUsed;

        if (type == null) {
            System.out.println("CAMPO " + name + " ha type vuoto. Diventa VARCHAR");
            type = "VARCHAR";
        }

        if (type.equalsIgnoreCase("INT")) {
            SQLphrase += " INT(" + length + ") ";
            virgolette = Boolean.FALSE;
        } else if (type.equalsIgnoreCase("FLOAT")) {
            SQLphrase += " FLOAT ";
            virgolette = Boolean.FALSE;
        } else if (type.equalsIgnoreCase("DOUBLE")) {
            SQLphrase += " DOUBLE ";
            virgolette = Boolean.FALSE;
        } else if (type.equalsIgnoreCase("DATE")) {
            SQLphrase += " DATE ";
        } else if (type.equalsIgnoreCase("TIME")) {
            SQLphrase += " TIME ";
        } else if (type.equalsIgnoreCase("TIMESTAMP")) {
            System.out.println("flagTimestampUsed: " + flag);
            if (flag < 1 && defaultValue != null && defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                SQLphrase += "  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ";
                flag++;
            } else {
                SQLphrase += "  TIMESTAMP ";
            }
            acceptDefault = Boolean.FALSE;

        } else if (type.equalsIgnoreCase("DATETIME")) {
            SQLphrase += " DATETIME ";
            acceptDefault = Boolean.FALSE;
        } else if (type.equalsIgnoreCase("TEXT")) {
            SQLphrase += " TEXT ";
        } else if (type.equalsIgnoreCase("MEDIUMTEXT")) {
            SQLphrase += " MEDIUMTEXT ";
            acceptDefault = Boolean.FALSE;
        } else if (type.equalsIgnoreCase("BLOB")) {
            SQLphrase += " BLOB ";
            acceptDefault = Boolean.FALSE;
        } else if (type.equalsIgnoreCase("MEDIUMBLOB")) {
            SQLphrase += " MEDIUMBLOB ";
        } else if (type.equalsIgnoreCase("LONGBLOB")) {
            acceptDefault = Boolean.FALSE;
            SQLphrase += " LONGBLOB ";
        } else if (type.equalsIgnoreCase("BIT")) {
            SQLphrase += " BIT(1) ";
            virgolette = Boolean.FALSE;
        } else if (type.equalsIgnoreCase("VARCHAR")) {
            SQLphrase += " VARCHAR(" + length + ") ";
        }

        if (defaultValue == null || defaultValue.isEmpty() || defaultValue == "" || defaultValue.equalsIgnoreCase("NULL")) {
            if (notnull == Boolean.FALSE && !type.equalsIgnoreCase("TIMESTAMP") && autoIncrement != true && primary != true) {
                defaultValue = "NULL";
                virgolette = Boolean.FALSE;
                acceptDefault = Boolean.TRUE;
            } else {
                acceptDefault = Boolean.FALSE;
            }
        }

        if (acceptDefault == Boolean.TRUE) {
            SQLphrase += " DEFAULT ";
            if (virgolette == Boolean.FALSE) {
                SQLphrase += defaultValue + "\n";
            } else {
                SQLphrase += "'" + defaultValue + "'  \n";
            }
        }

        if (autoIncrement == true) {
            SQLphrase += " AUTO_INCREMENT \n";
        }
        flagTimestampUsed = flag;
        return SQLphrase;
    }

    public class evoDirective {

        String infoName;
        String infoValue;
        String group;
        String superGroup;
        String note;
        String modifiable;
        String instanceInfoValue;

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getInstanceInfoValue() {
            return instanceInfoValue;
        }

        public void setInstanceInfoValue(String instanceInfoValue) {
            this.instanceInfoValue = instanceInfoValue;
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

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getSuperGroup() {
            return superGroup;
        }

        public void setSuperGroup(String superGroup) {
            this.superGroup = superGroup;
        }

        public String getNoe() {
            return note;
        }

        public void setNoe(String note) {
            this.note = note;
        }

        public String getModifiable() {
            return modifiable;
        }

        public void setModifiable(String modifiable) {
            this.modifiable = modifiable;
        }

    }
}
