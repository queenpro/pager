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

 
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import java.util.ArrayList;

/**
 *
 * @author Franco
 */
public class QPdatabase {

    private Server server;
    private String protocol; // es. SQL
    private String group;  // es. NTP
    private String name; // es. WS  --- factory
    private String DB; // es. WS  --- factory
    private String ID;  // es. edb7f254-5e33-40f1-a00e-97885d37e2ff  --- oslat
    private boolean locked;
    public ArrayList<QPtable> tables;

    private account Account;
    EVOpagerParams connParams;
    Settings mySettings;

    public QPdatabase(EVOpagerParams xParams, Settings xSettings) {
        this.connParams = xParams;
        this.mySettings = xSettings;

        this.server = server;
        this.protocol = protocol;
        if (connParams.getCKprojectGroup() != null) {
            this.group = connParams.getCKprojectGroup();
        }
       // this.name = connParams.getCKprojectName();
        this.DB = mySettings.getProjectDB();
        if (connParams.getCKcontextID() != null) {
            this.ID = connParams.getCKcontextID();
        }
        this.tables = new ArrayList<QPtable>();
        this.Account = new account();
    }

    public String getDB() {
        return DB;
    }

    public void setDB(String DB) {
        this.DB = DB;
    }

    public account getAccount() {
        return Account;
    }

    public void setAccount(account Account) {
        this.Account = Account;
    }

    public ArrayList<QPtable> getTables() {

        return tables;
    }

    public void castTable(QPtable myTable) {
        this.tables.add(myTable);

    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public String getDbExtendedName() {
        String dbName = "";
        if (this.group != null && this.group != ""
                && !this.group.equalsIgnoreCase("null")
                && this.group.length() > 0) {
            dbName = this.group + "_";
        }
        dbName = dbName + this.DB;
        if (this.ID != null && this.ID != ""
                && !this.ID.equalsIgnoreCase("null")
                && this.ID.length() > 0) {
            dbName += "_" + this.ID;
        }
     // System.out.println("Returning DB name="+dbName);

        return dbName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

}
