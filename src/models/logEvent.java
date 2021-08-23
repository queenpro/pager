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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import showIt.eventManager;

public class logEvent {

    int ID;
    String recorded;
    String remoteIP;
    String sessionID;
    String eventCode;
    String user;
    String type;
    String token;
    String info1;
    String info2;

    public void save(EVOpagerParams myParams, Settings mySettings) {

        if (mySettings.isLogEvents()) {

//        System.out.println("getUser: " + this.getUser());
//        System.out.println("getType: " + this.getType());
//        System.out.println("getToken: " + this.getToken());
//        System.out.println("mySettings.getProjectName: " + mySettings.getProjectName());
//        System.out.println("ProjectDBextendedName: " + mySettings.getProjectDBextendedName(myParams));
//        System.out.println("projectName: " + myParams.getCKprojectName());
//        System.out.println("contextID: " + myParams.getCKcontextID());
            Connection qpConny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalQueenpro();
            String SQLphrase = "INSERT INTO `archivio_eventi`(`remoteIP`, `sessionID`, `eventCode`, `user`, `type`, `token`, `info1`, `info2`, `projectName`, `DBname`) VALUES "
                    + "(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = null;
            try {
                ps = qpConny.prepareStatement(SQLphrase);
                ps.setString(1, this.getRemoteIP());
                ps.setString(2, this.getSessionID());
                ps.setString(3, this.getEventCode());
                ps.setString(4, this.getUser());
                ps.setString(5, this.getType());
                ps.setString(6, this.getToken());
                ps.setString(7, this.getInfo1());
                ps.setString(8, this.getInfo2());
                ps.setString(9, myParams.getCKprojectName());
                ps.setString(10, mySettings.getProjectDBextendedName(myParams));
                int i = ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(eventManager.class.getName()).log(Level.SEVERE, null, ex);
                String makeDBphrase = "CREATE TABLE IF NOT EXISTS `archivio_eventi` ( "
                        + "  `ID` int(16) NOT NULL AUTO_INCREMENT, "
                        + "  `recorded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                        + "  `remoteIP` varchar(64) DEFAULT NULL, "
                        + "  `sessionID` varchar(64) DEFAULT NULL, "
                        + "  `eventCode` varchar(32) DEFAULT NULL, "
                        + "  `user` varchar(64) DEFAULT NULL, "
                        + "  `type` varchar(16) DEFAULT NULL, "
                        + "  `token` varchar(64) DEFAULT NULL, "
                        + "  `info1` text, "
                        + "  `info2` text, "
                        + "  `projectName` varchar(64) DEFAULT NULL, "
                        + "  `DBname` varchar(64) DEFAULT NULL, "
                        + "  PRIMARY KEY (`ID`) "
                        + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
                ps = null;
                try {
                    ps = qpConny.prepareStatement(makeDBphrase);
                    int i = ps.executeUpdate();
                } catch (SQLException e) {

                }

            }
        }
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getRecorded() {
        return recorded;
    }

    public void setRecorded(String recorded) {
        this.recorded = recorded;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

}
