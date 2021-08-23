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
import REVOdbManager.Settings;
import REVOsetup.ErrorLogger;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.mail.Session;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class REVOws {

    public ArrayList<WScontext> contesti = new ArrayList();
    public String lastContextConnected;
    public Settings mySettings;
    public String PROJECT_ID = "WSS"; 
    public static ArrayList<WSclient> clientsConnected = new ArrayList<>();
    public static ArrayList<WSbroadcastMessage> bcMessages = new ArrayList<>();
    public Session GlobalSession;

    public int attempt;
    public String LastBackupStartTime;
    private static boolean backupping = false;
    private static int counter;
    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private static Set<Session> allSessions;
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
 
    
    
    
}
