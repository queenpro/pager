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

 
public class field {
    private table table;
    private String ID;
    private String name;
    private String type;
    private int length;
    private String defaultValue;
    private Boolean autoIncrement;
    private Boolean primary;
    private Boolean notNull;
    private int position;
    
    private String originQuery;
    private String originLabelField;
    private String originValueField;

    public field(String name, String type, int length) {
        this.name = name;
        this.type = type;
        this.length = length;
        this.defaultValue = "NULL";
        this.autoIncrement = false;
        this.primary = false;
        this.notNull=true;
        
        
    }

    public String getOriginQuery() {
        return originQuery;
    }

    public void setOriginQuery(String originQuery) {
        this.originQuery = originQuery;
    }

    public String getOriginLabelField() {
        return originLabelField;
    }

    public void setOriginLabelField(String originLabelField) {
        this.originLabelField = originLabelField;
    }

    public String getOriginValueField() {
        return originValueField;
    }

    public void setOriginValueField(String originValueField) {
        this.originValueField = originValueField;
    }

    public field() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }

    public table getTable() {
        return table;
    }

    public void setTable(table table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    
    
    
    
    
}
