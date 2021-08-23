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

import REVOdbManager.Settings;
 
public final class Server {
     
 String SQLdriver;
String DATABASE_USER;
String DATABASE_PW;
String defaultSQLserver;
String AlternativeSQLserver;

    public Server(Settings Xsettings) {
        setSQLdriver(Xsettings.getData_SQLdriver());
        setDATABASE_PW(Xsettings.getData_DATABASE_PW());
        setDATABASE_USER(Xsettings.getData_DATABASE_USER());
        setDefaultSQLserver(Xsettings.getData_defaultSQLserver());
        setAlternativeSQLserver(Xsettings.getData_alternativeSQLserver());

    }

    public Server(String serverType, Settings Xsettings) {

        if (serverType!=null && serverType.equalsIgnoreCase("account")) {

            setSQLdriver(Xsettings.getAccount_SQLdriver());
            setDATABASE_PW(Xsettings.getAccount_DATABASE_PW());
            setDATABASE_USER(Xsettings.getAccount_DATABASE_USER());
            setDefaultSQLserver(Xsettings.getAccount_defaultSQLserver());
            setAlternativeSQLserver(Xsettings.getAccount_alternativeSQLserver());

        } else if (serverType!=null && serverType.equalsIgnoreCase("FE")) {

            setSQLdriver(Xsettings.getFE_SQLdriver());
            setDATABASE_PW(Xsettings.getFE_DATABASE_PW());
            setDATABASE_USER(Xsettings.getFE_DATABASE_USER());
            setDefaultSQLserver(Xsettings.getFE_defaultSQLserver());
            setAlternativeSQLserver(Xsettings.getFE_alternativeSQLserver());

        }  else if (serverType!=null && serverType.equalsIgnoreCase("revolution")) {

            setSQLdriver(Xsettings.getRevolution_SQLdriver());
            setDATABASE_PW(Xsettings.getRevolution_DATABASE_PW());
            setDATABASE_USER(Xsettings.getRevolution_DATABASE_USER());
            setDefaultSQLserver(Xsettings.getRevolution_defaultSQLserver());
            setAlternativeSQLserver(Xsettings.getRevolution_alternativeSQLserver());

        } else {
             setSQLdriver(Xsettings.getData_SQLdriver());
        setDATABASE_PW(Xsettings.getData_DATABASE_PW());
        setDATABASE_USER(Xsettings.getData_DATABASE_USER());
        setDefaultSQLserver(Xsettings.getData_defaultSQLserver());
        setAlternativeSQLserver(Xsettings.getData_alternativeSQLserver());

        }

    }

    public String getSQLdriver() {
        return SQLdriver;
    }

    public void setSQLdriver(String SQLdriver) {
        this.SQLdriver = SQLdriver;
    }

    public String getDATABASE_USER() {
        return DATABASE_USER;
    }

    public void setDATABASE_USER(String DATABASE_USER) {
        this.DATABASE_USER = DATABASE_USER;
    }

    public String getDATABASE_PW() {
        return DATABASE_PW;
    }

    public void setDATABASE_PW(String DATABASE_PW) {
        this.DATABASE_PW = DATABASE_PW;
    }

    public String getDefaultSQLserver() {
        return defaultSQLserver;
    }

    public void setDefaultSQLserver(String defaultSQLserver) {
        this.defaultSQLserver = defaultSQLserver;
    }

    public String getAlternativeSQLserver() {
        return AlternativeSQLserver;
    }

    public void setAlternativeSQLserver(String AlternativeSQLserver) {
        this.AlternativeSQLserver = AlternativeSQLserver;
    }
    
    
    
}
