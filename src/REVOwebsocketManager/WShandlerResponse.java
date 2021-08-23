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

import REVOwebsocketManager.WSclient;
import org.json.simple.JSONObject;
import smartCore.smartAction;

/**
 *
 * @author Franco
 */
public class WShandlerResponse {
    JSONObject jObj;
    smartAction myAction;
     String decodedMessage;
     String actionType="service";
     WSclient senderClient;

    public WSclient getSenderClient() {
        return senderClient;
    }

    public void setSenderClient(WSclient senderClient) {
        this.senderClient = senderClient;
    }

    public WShandlerResponse() {
        actionType="service";
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public JSONObject getjObj() {
        return jObj;
    }

    public void setjObj(JSONObject jObj) {
        this.jObj = jObj;
    }

    public smartAction getMyAction() {
        return myAction;
    }

    public void setMyAction(smartAction myAction) {
        this.myAction = myAction;
    }

    public String getDecodedMessage() {
        return decodedMessage;
    }

    public void setDecodedMessage(String decodedMessage) {
        this.decodedMessage = decodedMessage;
    }
    
}
