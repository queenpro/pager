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

import REVOdbManager.EVOpagerParams;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Franco
 */
public class EVObackupper extends Thread { 
////////      public void run() {
//////// 
//////// try {
////////     Thread.sleep(12000);
////////     
////////       String backupDestinationFilename = "ALLdb.sql";
////////            String dbName = "factory_oslat";
////////            String backupMySqlServerPath = "/share/MD0_DATA/.qpkg/qmysql/mysql/bin/";
////////            String backupDestinationPath = "/share/MD0_DATA/Public/";
////////            String dbUserName = "yyyyyy";
////////            String dbPassword = "xxxxxx";
////////
////////            System.out.println(":dbowner:backupMySqlServerPath=" + backupMySqlServerPath);
////////            System.out.println(":dbowner:backupDestinationPath=" + backupDestinationPath);
////////
////////            String dirpath = backupMySqlServerPath; // posizione del DB su disco
////////            String destpath = backupDestinationPath;  // posizione del file prodotto sul disco
////////
//////// //---------------
////////            //---ATTENZIONE:--
////////            //- questa routine funziona solo se il software gira da server !!------
////////            String Response = "DB loc:" + backupMySqlServerPath + "\n  Destination:" + backupDestinationPath + "\n ...qualcosa è andato storto ! Il backup non ha funzionato.";
////////
////////            String executeCmd = dirpath + "mysqldump -u " + dbUserName + " -p" + dbPassword + " --add-drop-database -B " + dbName + " -r " + destpath + backupDestinationFilename;
////////
////////            System.out.println(":EXE:" + executeCmd);
////////            Process runtimeProcess;
////////            try {
////////                runtimeProcess = Runtime.getRuntime().exec(executeCmd);
////////                int processComplete = runtimeProcess.waitFor();
////////
////////                if (processComplete == 0) {
////////                    Response = ("Backup del DB " + dbName + " terminato con successo:" + destpath + backupDestinationFilename);
////////                } else {
////////                    System.out.println("Could not create the backup");
////////                }
////////            } catch (Exception ex) {
////////                ex.printStackTrace();
////////            }
////////        
//////// 
////////         System.out.println("\n\nBACKUP ESEGUITO !!!\n\n");
////////     }
////////  catch (InterruptedException e) {
////////     System.out.println("Thread figlio interrotto");
////////     }  
////////}
////////

}
