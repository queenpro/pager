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
 
import REVOdbManager.Settings;
import java.io.IOException;
import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//import java.util.logging.Level;
//import java.util.logging.Logger;

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class WebsocketClientEndpoint {

    Session userSession = null;
    private MessageHandler messageHandler;
    private OpenHandler openHandler;
    private CloseHandler closeHandler;
    private Settings mySettings;

    public WebsocketClientEndpoint(URI endpointURI, Settings xmySettings) {
        this.mySettings = xmySettings;
        try {
            WebSocketContainer container;
            container = ContainerProvider.getWebSocketContainer();
            Session connectToServer = container.connectToServer(this, endpointURI);
        } catch (IOException | DeploymentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("\n\nCLIENT LIGHTHOUSE opening websocket");
        this.userSession = userSession;
        //  this.openHandler.handleOpen();

    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("\n\nCLIENT LIGHTHOUSE closing websocket session:" + userSession + " - REASON:" + reason.toString());
        this.userSession = null;
            this.closeHandler.handleClose();

    }

    /**
     * Callback hook for Message Events. This method will be invoked when a
     * client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {

//        System.out.println("\n\nCLIENT LIGHTHOUSE RICEVE MESSAGGIO:" + message);
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }

 
    }

  
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void addOpenHandler(OpenHandler opeHandler) {
        this.openHandler = opeHandler;
    }

    public void addCloseHandler(CloseHandler clsHandler) {
        this.closeHandler = clsHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);

    }

    /**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */
    public static interface MessageHandler {

        public void handleMessage(String message);
    }

    public static interface OpenHandler {

        public void handleOpen();
    }

    public static interface CloseHandler {

        public void handleClose();
    }

    public Session getUserSession() {
        return userSession;
    }

    public void setUserSession(Session userSession) {
        this.userSession = userSession;
    }

}
