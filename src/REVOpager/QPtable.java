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

import java.util.ArrayList;
 
public class QPtable {

    public Database database;
    public ArrayList<QPfield> fields;
    public String name;
    public String ID;
    public String rifDatabase;

    public QPtable(String name) {
        this.name = name;
        this.fields = new ArrayList<QPfield>();

    }

    public QPtable() {
        this.fields = new ArrayList<QPfield>();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRifDatabase() {
        return rifDatabase;
    }

    public void setRifDatabase(String rifDatabase) {
        this.rifDatabase = rifDatabase;
    }

    public ArrayList<QPfield> getFields() {

        return fields;
    }

    public void castField(QPfield myField) {
        if (myField != null) {
            this.fields.add(myField);
        }
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
