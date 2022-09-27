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
package REVOsetup;

import REVOdbManager.EVOpagerDirectivesManager;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.Database;
import REVOpager.EVOpagerDBconnection;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.semaphore;

/**
 *
 * @author Franco
 */
public class EVOsetup {

    boolean updateNeeded;
    boolean backupNeeded;
    ErrorLogger el;
    String feedback = null;
    String feed;
    String PROJECT_ID = "EVO_SETUP";
    Settings mySettings;
    EVOpagerParams myParams;

    public EVOsetup(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        //System.out.println("Entro in EVOsetup...");
        el = new ErrorLogger(myParams, mySettings);
    }

    public semaphore doUpdate() {
        semaphore mySem = new semaphore();
        // se sono qui è perchè la valutazione di necessità è già stata fatta !
        // faccio backup senza controllare nulla
        String backMessage = "";
        feedback = null;
        feed = null;
        updateNeeded = false;
        backupNeeded = false;

        //----------------------------------
        // ESEGUI IL UPDATE-----------------
        //**************
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
        String lastSoftwareVersion = myManager.getDirective("lastSoftwareVersion");
        String actualVersion = mySettings.getSoftwareVersion();
        System.out.println("lastSoftwareVersion: " + lastSoftwareVersion + " - actualVersion:" + actualVersion);
        if (lastSoftwareVersion != null && !lastSoftwareVersion.equalsIgnoreCase(actualVersion)) {
            //Verifico che la versione indicata sia supoeriore alla attuale
            int dataAttuale = 0;
            int dataQP = 0;
            try {
                dataAttuale = Integer.parseInt(actualVersion);
            } catch (Exception e) {

            }
            try {
                dataQP = Integer.parseInt(lastSoftwareVersion);
            } catch (Exception e) {

            }

            if (dataQP > dataAttuale) {

                System.out.println("Lancio Update.");
                String webappsFolder = myManager.getDirective("webappsFolder");
                String autoUpdateSourceURL = myManager.getDirective("autoUpdateSourceURL");

                System.out.println("webappsFolder: " + webappsFolder + " - autoUpdateSourceURL:" + autoUpdateSourceURL);
                String destFolder = webappsFolder;
                String sourceFilename = mySettings.getProjectName() + "/" + lastSoftwareVersion + "_" + mySettings.getProjectName() + ".war";
                String destFilename = mySettings.getProjectName() + ".war";
                String sourceURL = autoUpdateSourceURL + sourceFilename;

                String destPath = webappsFolder + destFilename;
                System.out.println("sourceURL: " + sourceURL + "\n destPath:" + destPath);
                try {
                    URL website = new URL(sourceURL);
                    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                    FileOutputStream fos = new FileOutputStream(destPath);
                    long success = fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    if (success > 0) {
                        mySem.setValue(1);
                        mySem.setTooltipText("AGGIORNAMENTO SOFTWARE ESEGUITO CON SUCCESSO.");
                    } else {
                        mySem.setValue(-1);
                        mySem.setTooltipText("ERRORE:AGGIORNAMENTO SOFTWARE FALLITO.");
                    }

                } catch (Exception e) {
                    mySem.setValue(-1);
                    mySem.setTooltipText("ERRORE IN AGGIORNAMENTO SOFTWARE.");
                }
                System.out.println("Concluso Update.");
            } else {
                // la versione attuale è superiore a quella pubblicata
                System.out.println("la versione attuale è una beta-release.");
                mySem.setTooltipText("la versione attuale è una beta-release.");
                mySem.setValue(0);
            }
        } else {
            mySem.setTooltipText("");
            mySem.setValue(0);
        }

        //***************
        return mySem;
    }

    public semaphore doBackup() {
        semaphore mySem = new semaphore();
        // se sono qui è perchè la valutazione di necessità è già stata fatta !
        // faccio backup senza controllare nulla
        String backMessage = "";
        System.out.println("Entro in doBackup...");
        feedback = null;
        feed = null;
        updateNeeded = false;
        backupNeeded = false;

        //----------------------------------
        // ESEGUI IL BACKUP-----------------
        System.out.println("Lancio fastBackup.");
        mySem = fastBackup();
        System.out.println("Concluso fastBackup.");

        if (mySem.getValue() > 0) {
            System.out.println("AGGIORNO lastBackupDate.");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
            String dateForSave = formatDB.format(cal.getTime());

            // salvo la nuova data per il registro su evoi
            setEvoDirective(myParams, "lastBackupDate", dateForSave);
            backMessage = "backup OK.";
        } else {
            backMessage = "lastBackupDate saving failed.";
        }

        return mySem;
    }

    public EVOsetup(EVOpagerParams myParams, Settings mySettings, String type) {
        semaphore mySem = new semaphore();
        System.out.println("\n\nSONO IN EVOsetup\n\n");

        this.myParams = myParams;
        this.mySettings = mySettings;

        el = new ErrorLogger(myParams, mySettings);
        feedback = null;
        feed = null;
        updateNeeded = false;
        backupNeeded = false;

        el.log("EVOsetup", "myParams.getCKprojectName = " + myParams.getCKprojectName());
        el.log("EVOsetup", "myParams.myParams.getCKcontextID() = " + myParams.getCKcontextID());
        el.log("EVOsetup", "myParams.myParams.getCKcontextID() = " + mySettings.getData_defaultSQLserver());
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);

//String lastBackupDate = getDirective(myParams, "lastBackupDate");
        String lastBackupDate = myManager.getEvoDirective("lastBackupDate");
        if (lastBackupDate == null || lastBackupDate.length() < 10) {
            lastBackupDate = "2000-01-01 00:00:00";
        }
        System.out.println("lastBackupDate letta da EVOdirectives:" + lastBackupDate);

        feed = "lastBackupDate letta da EVOdirectives:" + lastBackupDate;
        feedback = feedback + "\n" + el.log("EVOsetup", feed);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
        Date recordedDate = null;
        try {
            recordedDate = format.parse(lastBackupDate);
        } catch (ParseException ex) {
            try {
                recordedDate = format.parse("1900-01-01 00:00:00");
            } catch (ParseException ex1) {
                Logger.getLogger(EVOsetup.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
        String curDate = format0.format(cal.getTime());
        feed = "curDate:" + curDate;
        feedback = feedback + "\n" + el.log("EVOsetup", feed);

        String backupMySqlServerPath = myManager.getDirective("backupMySqlServerPath");
        String backupDestinationPath = myManager.getDirective("backupDestinationPath");

        feed = "backupMySqlServerPath:" + backupMySqlServerPath;
        feedback = feedback + "\n" + el.log("EVOsetup", feed);

        feed = "backupDestinationPath:" + backupDestinationPath;
        feedback = feedback + "\n" + el.log("EVOsetup", feed);

        Database myDatabase = new Database(myParams, mySettings);
        String dbName = myDatabase.getDbExtendedName();

        SimpleDateFormat format4 = new SimpleDateFormat("yyyyMMddHHmm", Locale.ITALY);
        String dateForName = format4.format(cal.getTime());
        String destfileName = dateForName + "-" + dbName;
        feed = "destfileName:" + destfileName;
        feedback = feedback + "\n" + el.log("EVOsetup", feed);

        SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
        String dateForSave = formatDB.format(cal.getTime());
        feed = "dateForSave:" + dateForSave;
        feedback = feedback + "\n" + el.log("EVOsetup", feed);
        /*
         SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         String formatted = format1.format(cal.getTime());
         */
        if (type == null || type.length() < 3) {
            type = "PERIODIC";
        }
        feed = "\nTYPE OF BACKUP:" + type;
        feedback = feedback + "\n" + el.log("EVOsetup", feed);

        if (type.equalsIgnoreCase("PERIODIC")) {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            try {
                c1.setTime(recordedDate);
            } catch (Exception e) {

            }

            try {
                c2.setTime(cal.getTime());
            } catch (Exception e) {

            }
            long giorni = (c2.getTime().getTime() - c1.getTime().getTime()) / (24 * 3600 * 1000);
            long ore = (c2.getTime().getTime() - c1.getTime().getTime()) / (3600 * 1000);
            long minuti = (c2.getTime().getTime() - c1.getTime().getTime()) / (60 * 1000);
            /*
             System.out.println("Differenza in giorni:" + giorni);
             System.out.println("Differenza in ore:" + ore);
             System.out.println("Differenza in minuti:" + minuti);
             */
//            feed = "\nCerco backupInterval.";
//            feedback = feedback + "\n" + el.log("EVOsetup", feed);
            String intervalSet = myManager.getDirective("backupInterval");
            int interval = 1440; // 1 giorno

            try {
                interval = Integer.parseInt(intervalSet);
            } catch (Exception e) {
                interval = 1440;
            }

            feed = "\nINTERVALLO DI BACKUP IMPOSTATO: " + interval + " minuti.";
            feedback = feedback + "\n" + el.log("EVOsetup", feed);
            feed = "\nTRASCORSI: " + minuti + " minuti.";
            feedback = feedback + "\n" + el.log("EVOsetup", feed);

            if (minuti > interval) {
                ////                //EVObackupper thread = new EVObackupper();
////                //thread.start(); 
                //backupDB(myParams, mySettings, backupMySqlServerPath, backupDestinationPath, dbName, destfileName, curDate);

                el.log("EVOsetup", "INIZIO FASTBACKUP ============");
                mySem = fastBackup();
                if (mySem.getValue() > 0) {
                    // salvo la nuova data per il registro su evoi
                    setEvoDirective(myParams, "lastBackupDate", dateForSave);
                }
                el.log("EVOsetup", "FINE FASTBACKUP ============");
            } else {

            }
        } else if (type.equalsIgnoreCase("FORCED")) {
            el.log("EVOsetup", "INIZIO FASTBACKUP FORZATO============");
            mySem = fastBackup();
            if (mySem.getValue() > 0) {
                // salvo la nuova data per il registro su evoi
                setEvoDirective(myParams, "lastBackupDate", dateForSave);
            }
            el.log("EVOsetup", "FINE FASTBACKUP FORZATO============");
        } else if (type.equalsIgnoreCase("ALLDB")) {
            String newDBname = "ALLdatabases";
            destfileName = dateForName + "-" + newDBname;
            el.log("EVOsetup", "INIZIO BACKUP FORZATO ALL DB============");
            backupDB(myParams, mySettings, backupMySqlServerPath, backupDestinationPath, "--all-databases", destfileName, curDate);
            el.log("EVOsetup", "FINE BACKUP FORZATO ALL DB============");
        }

    }

    public void backupDB(EVOpagerParams myParams, Settings mySettings,
            String backupMySqlServerPath,
            String backupDestinationPath,
            String dbName,
            String destfileName,
            String curDate) {

        feed = "START BACKUP PROCESS *****";
        feedback = feedback + "\n" + el.log("EVOsetup", feed);
        int success = 0;
        String backupDestinationFilename = destfileName + ".sql";

        /* String backupMySqlServerPath = "/share/MD0_DATA/.qpkg/qmysql/mysql/bin/";
         String backupDestinationPath = "/share/MD0_DATA/Public/";*/
        String dbUserName = mySettings.getData_DATABASE_USER();
        String dbPassword = mySettings.getData_DATABASE_PW();

        if (dbName.equalsIgnoreCase("--all-databases")) {
            dbUserName = "backupper";
            dbPassword = "3388020891";
        }

        //   System.out.println("\n\n\n:dbowner:backupMySqlServerPath=" + backupMySqlServerPath);
        //   System.out.println(":dbowner:backupDestinationPath=" + backupDestinationPath);
        //      feed = ":dbowner:backupMySqlServerPath=" + backupMySqlServerPath;
        //    feedback = feedback + "\n" + el.log("EVOsetup", feed);
        //   feed = ":dbowner:backupDestinationPath=" + backupDestinationPath;
        //   feedback = feedback + "\n" + el.log("EVOsetup", feed);
        String dirpath = backupMySqlServerPath; // posizione del DB su disco
        String destpath = backupDestinationPath;  // posizione del file prodotto sul disco
        try {
            dirpath = dirpath.replace("[]", "");
            destpath = destpath.replace("[]", "");
        } catch (Exception e) {

        }

        //---------------
        //---ATTENZIONE:--
        //- questa routine funziona solo se il software gira da server !!------
        String Response = "DB loc:" + backupMySqlServerPath + "\n  Destination:" + backupDestinationPath + "\n ...qualcosa è andato storto ! Il backup non ha funzionato.";
///mnt/ext/opt/mariadb/bin/mysqldump --user=root --password='''costa32!bfh4m''' --add-drop-database -B --all-databases -r  /share/CACHEDEV1_DATA/Public/201811131739-allDB.sql
        String executeCmd = dirpath + "mysqldump --user=" + dbUserName + " --password='''" + dbPassword + "''' --add-drop-database -B " + dbName + " -r " + destpath + backupDestinationFilename;
        el.log("EVOsetup", "executeCmd = " + executeCmd);

        System.out.println(":EXE:" + executeCmd);
        Process runtimeProcess;
        try {
            runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                Response = ("Backup del DB " + dbName + " terminato con successo:" + destpath + backupDestinationFilename);
                el.log("backupDB", Response);
                success = 2;
            } else {
                System.out.println("Could not create the backup");
                el.log("backupDB", "Could not create the backup ");
                success = -1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Impossibile esguire il backup...");
            success = -2;
        }
        System.out.println("backup DONE.\n\n\n");
        el.log("backupDB", "backup DONE. ");
        if (success >= 0) {
            int res = setEvoDirective(myParams, "lastBackupDate", curDate);
            System.out.println("lastBackupDate scritta su EVOdirectives:" + curDate);
            feed = "lastBackupDate scritta su gEVOdirectives:" + curDate;
            feedback = feedback + "\n" + el.log("EVOsetup", feed);
        } else {
            feed = "Errori nell'esecuzione del backup:" + curDate;
            feedback = feedback + "\n" + el.log("EVOsetup", feed);
        }

    }

    public int setEvoDirective(EVOpagerParams myParams, String infoName, String infoValue) {
        int result = 0;

        Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        try {
            String SQLphrase = "DELETE FROM " + mySettings.getLocalEVO_directives() + " WHERE infoName = '" + infoName + "' ";
            PreparedStatement statement = FEconny.prepareStatement(SQLphrase);
            int i = statement.executeUpdate();

            SQLphrase = "INSERT INTO " + mySettings.getLocalEVO_directives() + "  ( infoName, infoValue) VALUES ( ?,? ) ";
            statement = FEconny.prepareStatement(SQLphrase);
            statement.setString(1, infoName);
            statement.setString(2, infoValue);
            i = statement.executeUpdate();

            FEconny.close();
            result = 1;
            el.log("setEvoDirective", SQLphrase);
        } catch (SQLException ex) {
            result = -1;
            Logger.getLogger(EVOsetup.class.getName()).log(Level.SEVERE, null, ex);
            el.log("setEvoDirective", ex.toString());
        }

        return result;
    }

    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public semaphore fastBackup() {
        semaphore mySem = new semaphore();
        String history = "";
        String historyMessage = "";
        //int response = 0;
        //----------------------------------
        historyMessage = "SONO IN fastBackup";
        System.out.println(historyMessage);
        history += historyMessage + "\n";
        //----------------------------------

        int nofBackupFiles = 0;
        int lastNofBackupFile = 0;
        String backupMySqlServerPath = "";//  es. /share/MD0_DATA/.qpkg/qmysql/mysql/bin
        String backupDestinationPath = "";
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
        backupDestinationPath = myManager.getDirective("backupDestinationPath");
        backupMySqlServerPath = myManager.getDirective("backupMySqlServerPath");
        String XnofBackupFiles = myManager.getDirective("nofBackupFiles");
        String backupExtension = myManager.getDirective("backupExtension");
        if (backupExtension == null || backupExtension.length() == 0) {
            backupExtension = ".bak";
        }
        if (!backupExtension.startsWith(".")) {
            backupExtension = "." + backupExtension;
        }

        try {
            nofBackupFiles = Integer.parseInt(XnofBackupFiles);
        } catch (Exception e) {
        }
        String XlastNofBackupFile = myManager.getEvoDirective("lastNofBackupFile");
        try {
            lastNofBackupFile = Integer.parseInt(XlastNofBackupFile);
        } catch (Exception e) {
        }
        //----------------------------------
        historyMessage = ":dbowner:backupMySqlServerPath=" + backupMySqlServerPath;
        System.out.println(historyMessage);
        history += historyMessage + "\n";
        //----------------------------------
        //----------------------------------
        historyMessage = ":dbowner:backupDestinationPath=" + backupDestinationPath;
        System.out.println(historyMessage);
        history += historyMessage + "\n";
        //----------------------------------
        //----------------------------------
        historyMessage = ":dbowner:nofBackupFiles=" + nofBackupFiles;
        System.out.println(historyMessage);
        history += historyMessage + "\n";
        //----------------------------------
        //----------------------------------
        historyMessage = ":dbowner:lastNofBackupFile=" + lastNofBackupFile;
        System.out.println(historyMessage);
        history += historyMessage + "\n";
        //----------------------------------

        String dirpath = backupMySqlServerPath; // posizione del DB su disco
        String destpath = backupDestinationPath;  // posizione del file prodotto sul disco

        try {
            dirpath = dirpath.replace("[]", "");
            destpath = destpath.replace("[]", "");
        } catch (Exception e) {

        }

        if (dirpath == null || dirpath == "" || destpath == null || destpath == "") {
            //----------------------------------
            historyMessage = "Configurazione backup assente!" + backupDestinationPath;
            mySem.setValue(-1);
            mySem.setTooltipText(historyMessage);
            System.out.println(historyMessage);
            history += historyMessage + "\n";
            //----------------------------------

            return mySem;
        }
        //---------------

        //*****FAST BACKUP*****
        if (nofBackupFiles < 1) {
            nofBackupFiles = 1;
        }
        if (lastNofBackupFile < 1 || lastNofBackupFile >= nofBackupFiles) {
            lastNofBackupFile = 1;
        } else {
            lastNofBackupFile++;
        }
        //----------------------------------
        historyMessage = "\nlastNofBackupFile AGGIORNATO = " + lastNofBackupFile;
        System.out.println(historyMessage);
        history += historyMessage + "\n";
        //----------------------------------
        String dbName = mySettings.getProjectDB();
        if (myParams != null && myParams.getCKcontextID() != null && myParams.getCKcontextID().length() > 0) {
            dbName += "_" + myParams.getCKcontextID();
        }
        String project = mySettings.getProjectName();
        project = project.replaceAll("[^a-zA-Z0-9]", "");
        project = project.replaceAll(" ", "");
        historyMessage = "\ndbName = " + dbName;
        String opSystem = "linux";
        try {
            opSystem = myManager.getDirective("opSystem");
        } catch (Exception e) {
        }
        //----------------------------------
        historyMessage = "opSystem:" + opSystem;
        System.out.println(historyMessage);
        history += historyMessage + "\n";
        System.out.println(historyMessage);

////////        
////////        System.out.println("BACKUP PATH: " + backupDestinationPath + dbName + "-" + project + "_BACKUP-" + lastNofBackupFile + backupExtension);
////////        String executeCmd = dirpath + "mysqldump -u "
////////                + mySettings.getData_DATABASE_USER() + " -p"
////////                + mySettings.getData_DATABASE_PW() + " --add-drop-database -B "
////////                + dbName + " -r "
////////                + backupDestinationPath + dbName + "-" + project + "_BACKUP-" + lastNofBackupFile + backupExtension;
////////        if (opSystem != null && opSystem.equalsIgnoreCase("linux")) {
////////            executeCmd = dirpath + "mysqldump -u "
////////                    //                    +"franco"
////////                    + mySettings.getData_DATABASE_USER()
////////                    + " -p"
////////                    + mySettings.getData_DATABASE_PW()
////////                    //                    +"costa32bfh4m"
////////                    + " --add-drop-database -B "
////////                    + dbName + " -r "
////////                    + backupDestinationPath + dbName + "-" + project + "_BACKUP-" + lastNofBackupFile + backupExtension;
////////        }
////////
////////        //----------------------------------
////////        try {
////////            historyMessage = ":EXE:" + executeCmd.replace(mySettings.getData_DATABASE_PW(), "S3CRET");
////////            System.out.println(historyMessage);
////////        } catch (Exception e) {
////////        }
////////        history += historyMessage + "\n";
////////// el.log("EVOsetup", "FASTBACKUP DATABASE");
////////        //----------------------------------
////////        try {
////////
////////            runtimeProcess = Runtime.getRuntime().exec(executeCmd);
////////            processComplete = runtimeProcess.waitFor();
////////            if (runtimeProcess.exitValue() != 0) {
////////                System.out.println();
////////                System.out.println("Command: " + executeCmd);
////////                InputStream errorStream = runtimeProcess.getErrorStream();
////////                int c = 0;
////////                historyMessage = "";
////////                while ((c = errorStream.read()) != -1) {
////////                    System.out.print((char) c);
////////                    historyMessage += ((char) c);
////////                }
////////                history += historyMessage + "\n";
////////            }
////////
////////            historyMessage = "Backup appena eseguito :" + processComplete;
////////            mySem.setTooltipText(historyMessage);
////////            history += historyMessage + "\n";
////////            mySem.setValue(1);
////////            if (runtimeProcess != null && runtimeProcess.exitValue() != 0) {
////////                historyMessage = "Backup appena eseguito: Not Success -exit code " + runtimeProcess.exitValue();
////////                history += historyMessage + "\n";
////////                System.out.println(historyMessage);
////////                mySem.setValue(0);
////////            } else {
////////                history += "Backup appena eseguito !" + "\n";
////////            }
////////        } catch (Exception ex) {
//////////            ex.printStackTrace();
////////            System.out.println("Backup ha generato un errore !");
////////            processComplete = -1;
////////            mySem.setTooltipText("Backup ha generato un errore !");
////////            mySem.setValue(0);
////////
////////            history += "Backup ha generato un errore !" + "\n";
////////        }
////////
////////        //----------------------------------
////////        historyMessage = "processComplete=" + processComplete;
////////        System.out.println(historyMessage);
////////        history += historyMessage + "\n";
////////        //----------------------------------
////////
////////
////////
////////        if (processComplete > -1) {
////////
////////            Response = ("Fast Backup terminato con successo:" + backupDestinationPath + dbName + "-" + project + "_BACKUP-" + lastNofBackupFile + backupExtension);
////////
////////            //----------------------------------
////////            historyMessage = Response;
////////            System.out.println(historyMessage);
////////            history += historyMessage + "\n";
////////            setEvoDirective(myParams, "lastNofBackupFile", "" + lastNofBackupFile);
////////
////////            //----------------------------------
////////        } else {
////////            //----------------------------------
////////            historyMessage = "Could not create the backup";
////////            System.out.println(historyMessage);
////////            history += historyMessage + "\n";
////////            mySem.setValue(0);
////////        }
        backupOrder myBo = new backupOrder();
        myBo.backupDestinationPath = backupDestinationPath;
        myBo.dbName = dbName;
        myBo.project = project;
        myBo.backupExtension = backupExtension;
        myBo.opSystem = opSystem;
        myBo.dirpath = dirpath;
        myBo.historyMessage = historyMessage;
        myBo.history = history;
        myBo.mySem = mySem;
        myBo.lastNofBackupFile = lastNofBackupFile;
        myBo = goBackupDB(myBo);
        history = myBo.history;
        mySem = myBo.mySem;

        if (myBo.processComplete > -1) {
            //----------------------------------
            historyMessage = "Fast Backup terminato con successo:" + myBo.backupDestinationPath + myBo.dbName + "-" + myBo.project + "_BACKUP-" + myBo.lastNofBackupFile + myBo.backupExtension;
            System.out.println(myBo.historyMessage);
            history += myBo.historyMessage + "\n";
            setEvoDirective(myParams, "lastNofBackupFile", "" + myBo.lastNofBackupFile);
            //----------------------------------
        } else {
            //----------------------------------
            historyMessage = "Could not create the backup";
            System.out.println(historyMessage);
            history += historyMessage + "\n";
            mySem.setValue(0);
        }
//  secondaryDB

        String secondaryDB = myManager.getDirective("secondaryDB");
        if (secondaryDB != null && secondaryDB.length() > 0) {
            myBo.dbName = secondaryDB;
            myBo = goBackupDB(myBo);
        }
// el.log("EVOsetup", "FASTBACKUP FILES");
        //----------------------------------
// prendo la cartella in backupFilesSourcePath e la copio in backupFilesDestinationPath
        Process runtimeProcess;
        int processComplete;
        String backupFilesSourcePath = myManager.getDirective("backupFilesSourcePath");
        String backupFilesDestinationPath = myManager.getDirective("backupFilesDestinationPath");

        String command = "cp -avr ";
        if (opSystem != null && opSystem.equalsIgnoreCase("windows")) {
            command = "Xcopy /E /I /Y ";
        }

        if ((backupFilesDestinationPath != null && backupFilesDestinationPath.length() > 0)
                && (backupFilesSourcePath != null && backupFilesSourcePath.length() > 0)) {
            String executeCmd2 = command + backupFilesSourcePath + " " + backupFilesDestinationPath;
            historyMessage = "Backup cartella files: " + executeCmd2;
            System.out.println(historyMessage);
            history += historyMessage + "\n";
// el.log("EVOsetup", "FASTBACKUP FILES->"+historyMessage);
            try {
                runtimeProcess = Runtime.getRuntime().exec(executeCmd2);
                processComplete = runtimeProcess.waitFor();

                //response += 2;
                historyMessage = "backup files concluso con successo.";
                System.out.println(historyMessage);
                history += historyMessage + "\n";

// el.log("EVOsetup", "FASTBACKUP FILES->"+historyMessage);
            } catch (Exception ex) {
                //ex.printStackTrace();

                processComplete = -1;
                el.log("EVOsetup", "FASTBACKUP ERROR:" + executeCmd2 + " --> " + ex.toString());
                //response = 0;
                historyMessage = "backup files folder error: " + executeCmd2;

                System.out.println(historyMessage);
                history += historyMessage + "\n";
            }
        }

// el.log("EVOsetup", "FASTBACKUP FILES->DONE" );
        setEvoDirective(myParams, "lastHistory", history);
        return mySem;
    }

    private class backupOrder {

        String backupDestinationPath;
        String dbName;
        String project;
        String backupExtension;
        String opSystem;
        String dirpath;
        String historyMessage;
        String history;
        semaphore mySem;
        int lastNofBackupFile;
        int processComplete;
    }

    private backupOrder goBackupDB(backupOrder bo) {
        //---ATTENZIONE:--
        //- questa routine funziona solo se il software gira da server !!------

        Process runtimeProcess;
        bo.processComplete = -1;
        System.out.println("BACKUP PATH: " + bo.backupDestinationPath + bo.dbName + "-" + bo.project + "_BACKUP-" + bo.lastNofBackupFile + bo.backupExtension);
        String executeCmd = bo.dirpath + "mysqldump -u "
                + mySettings.getData_DATABASE_USER() + " -p"
                + mySettings.getData_DATABASE_PW() + " --add-drop-database -B "
                + bo.dbName + " -r "
                + bo.backupDestinationPath + bo.dbName + "-" + bo.project + "_BACKUP-" + bo.lastNofBackupFile + bo.backupExtension;
        if (bo.opSystem != null && bo.opSystem.equalsIgnoreCase("linux")) {
            executeCmd = bo.dirpath + "mysqldump -u "
                    //                    +"franco"
                    + mySettings.getData_DATABASE_USER()
                    + " -p"
                    + mySettings.getData_DATABASE_PW()
                    //                    +"costa32bfh4m"
                    + " --add-drop-database -B "
                    + bo.dbName + " -r "
                    + bo.backupDestinationPath + bo.dbName + "-" + bo.project + "_BACKUP-" + bo.lastNofBackupFile + bo.backupExtension;
        }

        //----------------------------------
        try {
            bo.historyMessage = ":EXE:" + executeCmd.replace(mySettings.getData_DATABASE_PW(), "S3CRET");
            System.out.println(bo.historyMessage);
        } catch (Exception e) {
        }
        bo.history += bo.historyMessage + "\n";
// el.log("EVOsetup", "FASTBACKUP DATABASE");
        //----------------------------------
        try {

            runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            bo.processComplete = runtimeProcess.waitFor();
            if (runtimeProcess.exitValue() != 0) {
                System.out.println();
                System.out.println("Command: " + executeCmd);
                InputStream errorStream = runtimeProcess.getErrorStream();
                int c = 0;
                bo.historyMessage = "";
                while ((c = errorStream.read()) != -1) {
                    System.out.print((char) c);
                    bo.historyMessage += ((char) c);
                }
                bo.history += bo.historyMessage + "\n";
            }

            bo.historyMessage = "Backup appena eseguito :" + bo.processComplete;
            bo.mySem.setTooltipText(bo.historyMessage);
            bo.history += bo.historyMessage + "\n";
            bo.mySem.setValue(1);
            if (runtimeProcess != null && runtimeProcess.exitValue() != 0) {
                bo.historyMessage = "Backup appena eseguito: Not Success -exit code " + runtimeProcess.exitValue();
                bo.history += bo.historyMessage + "\n";
                System.out.println(bo.historyMessage);
                bo.mySem.setValue(0);
            } else {
                bo.history += "Backup appena eseguito !" + "\n";
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
            System.out.println("Backup ha generato un errore !");
            bo.processComplete = -1;
            bo.mySem.setTooltipText("Backup ha generato un errore !");
            bo.mySem.setValue(0);

            bo.history += "Backup ha generato un errore !" + "\n";
        }

        //----------------------------------
        bo.historyMessage = "processComplete=" + bo.processComplete;
        System.out.println(bo.historyMessage);
        bo.history += bo.historyMessage + "\n";
        //----------------------------------

        return bo;
    }

}
