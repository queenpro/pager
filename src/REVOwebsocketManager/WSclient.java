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
package REVOwebsocketManager;

import REVOdbManager.EVOpagerParams;
import org.json.simple.JSONObject;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class WSclient {

    String user;
    String clientID;
    String sessionID;
    String localUserID;
    // String softwareName;
    String softwareContext;
    String softwareGroup;
    String recorded;
    String tokenAssigned;
    String uid;
    EVOpagerParams clientParams;
    int tempFound;
    boolean handshaked;
    int nofErrors;

    String token;
    String clientModel;
    String clientType;
    String userID;
    String userPassword;

    String URI;
    String status;
    String clientIP;
    String clientIPtype;

    String CLIENT_SERVERNAME;
    String CLIENT_PROJECTNAME;
    String CLIENT_CONTEXT;
    String WSUUID ;
    long lastMessageRecorded;
    
    public WSclient(String XclientID, String sessionID, String tokenAssigned, String uid) {
        this.clientID = XclientID;
        this.sessionID = sessionID;
        this.clientParams = new EVOpagerParams();
        this.tokenAssigned = tokenAssigned;
        this.uid = uid;
        handshaked = false;
    }
    public WSclient(String XclientID, String sessionID, String tokenAssigned) {
        this.clientID = XclientID;
        this.sessionID = sessionID;
        this.clientParams = new EVOpagerParams();
        this.tokenAssigned = tokenAssigned;
        handshaked = false;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getLastMessageRecorded() {
        return lastMessageRecorded;
    }

    public void setLastMessageRecorded(long lastMessageRecorded) {
        this.lastMessageRecorded = lastMessageRecorded;
    }

   

    public String getWSUUID() {
        return WSUUID;
    }

    public void setWSUUID(String WSUUID) {
        this.WSUUID = WSUUID;
    }

    public String getClientIPtype() {
        return clientIPtype;
    }

    public void setClientIPtype(String clientIPtype) {
        this.clientIPtype = clientIPtype;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClientModel() {
        return clientModel;
    }

    public void setClientModel(String clientModel) {
        this.clientModel = clientModel;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public int getNofErrors() {
        return nofErrors;
    }

    public void setNofErrors(int nofErrors) {
        this.nofErrors = nofErrors;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public boolean isHandshaked() {
        return handshaked;
    }

    public void setHandshaked(boolean handshaked) {
        this.handshaked = handshaked;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public int getTempFound() {
        return tempFound;
    }

    public void setTempFound(int tempFound) {
        this.tempFound = tempFound;
    }

    public EVOpagerParams getClientParams() {
        return clientParams;
    }

    public void setClientParams(EVOpagerParams clientParams) {
        this.clientParams = clientParams;
    }

    public String getRecorded() {
        return recorded;
    }

    public void setRecorded(String recorded) {
        this.recorded = recorded;
    }

    public String getTokenAssigned() {
        return tokenAssigned;
    }

    public void setTokenAssigned(String tokenAssigned) {
        this.tokenAssigned = tokenAssigned;
    }

    public String getLocalUserID() {
        return localUserID;
    }

    public void setLocalUserID(String localUserID) {
        this.localUserID = localUserID;
    }

    /*    public String getSoftwareName() {
            return softwareName;
        }

        public void setSoftwareName(String softwareName) {
            this.softwareName = softwareName;
        }
     */
    public String getSoftwareContext() {
        return softwareContext;
    }

    public void setSoftwareContext(String softwareContext) {
        this.softwareContext = softwareContext;
    }

    public String getSoftwareGroup() {
        return softwareGroup;
    }

    public void setSoftwareGroup(String softwareGroup) {
        this.softwareGroup = softwareGroup;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

}
