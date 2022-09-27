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

/**
 *
 * @author Franco
 */
public class SelectListLine {

    private String Value;
    private String Label;
    private int Checked;
    private String SpareValue;
    private int Type;
    private String Marker;
    private String infoType; 

 
    

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }
    
    

    public String getMarker() {
        return Marker;
    }

    public void setMarker(String Marker) {
        this.Marker = Marker;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public String getSpareValue() {
        return SpareValue;
    }

    public void setSpareValue(String SpareValue) {
        this.SpareValue = SpareValue;
    }

    public int getChecked() {
        return Checked;
    }

    public void setChecked(int Checked) {
        this.Checked = Checked;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String Label) {
        this.Label = Label;
    }

}
