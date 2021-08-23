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
 
public class QPfield {
//    public table table;

    public String ID;
    public String value;
    public String name;
    public String type;
    public int length;
    public String defaultValue;
    public Boolean BoolAutoIncrement;
    public Boolean BoolPrimary;
    public Boolean BoolNotNull;
    public int position;

    public String originQuery;
    public String originLabelField;
    public String originValueField;

    public int autoIncrement;
    public int primary;
    public int notNull;

    public QPfield() {
    }

    public QPfield(String name, String type, int length) {
        this.name = name;
        this.type = type;
        this.length = length;
        this.defaultValue = "NULL";
        this.BoolAutoIncrement = false;
        this.BoolPrimary = false;
        this.BoolNotNull = true;
        this.autoIncrement = 0;
        this.primary = 0;
        this.notNull = 1;

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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getBoolAutoIncrement() {
        return BoolAutoIncrement;
    }

    public void setBoolAutoIncrement(Boolean BoolAutoIncrement) {
        this.BoolAutoIncrement = BoolAutoIncrement;
    }

    public Boolean getBoolPrimary() {
        return BoolPrimary;
    }

    public void setBoolPrimary(Boolean BoolPrimary) {
        this.BoolPrimary = BoolPrimary;
    }

    public Boolean getBoolNotNull() {
        return BoolNotNull;
    }

    public void setBoolNotNull(Boolean BoolNotNull) {
        this.BoolNotNull = BoolNotNull;
    }

    public int getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(int autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public int getPrimary() {
        return primary;
    }

    public void setPrimary(int primary) {
        this.primary = primary;
    }

    public int getNotNull() {
        return notNull;
    }

    public void setNotNull(int notNull) {
        this.notNull = notNull;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
