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
import REVOdbManager.Settings;
import REVOpager.Database;
import REVOpager.EVOpagerDBconnection;
import REVOpager.Server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Franco
 */
public class ErrorLogger {

    String LOCAL_SERVER_URL;
    String LOCAL_SERVER_ALTURL;
    String DATABASE_PREFIX = "";
    String CKcontextID = "";
    boolean printOnScreen = true;
    boolean printOnLog = true;
    String Application = "";
    String Message = "";
    EVOpagerParams myParams;
    Settings mySettings;

    public ErrorLogger(EVOpagerParams xParams, Settings xSettings) {
        this.mySettings = xSettings;
        this.myParams = xParams;
        try {
            LOCAL_SERVER_URL = mySettings.getData_defaultSQLserver();
            LOCAL_SERVER_ALTURL = mySettings.getData_alternativeSQLserver();
        } catch (Exception e) {
        }

    }

    public boolean isPrintOnScreen() {
        return printOnScreen;
    }

    public void setPrintOnScreen(boolean printOnScreen) {
        this.printOnScreen = printOnScreen;
    }

    public boolean isPrintOnLog() {
        return printOnLog;
    }

    public void setPrintOnLog(boolean printOnLog) {
        this.printOnLog = printOnLog;
    }

    public String log(String application, String message) {
        String feedback = null;
        this.Application = application;
        this.Message = message;

        if (printOnScreen == true) {
            System.out.println(Message);
        }
        if (printOnLog == true) {

            String purgedMessage = Message.replaceAll("'", "#");
            try {
                Connection localconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalQueenpro();
                if (localconny != null) {
                    String SQLphrase = "INSERT INTO errorLog (application,log) VALUES(?,?)";
                    PreparedStatement statement = localconny.prepareStatement(SQLphrase);
                    statement.setString(1, Application);
                    statement.setString(2, purgedMessage);

                    int i = statement.executeUpdate();
                    statement.close();
                    localconny.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(ErrorLogger.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("FAILED LOGGING ERRORS: " + Message);
            }
        }
        return Message
                + "</BR>";
    }

}
