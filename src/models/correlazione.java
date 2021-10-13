/*
 * Copyright (C) 2021 Franco Venezia @ www.ffs.it
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package models;

/**
 *
 * @author Franco Venezia @ www.ffs.it
 */
public class correlazione {
          String linkTab = "";
                                    String partAvalue = "";
                                    String partBvalue = "";
                                    String partAtab = "";
                                    String partAvalueField = "";
                                    String partBtab = "";
                                    String partBvalueField = "";
                                    String partAstatus = "";
                                    String partBstatus = "";
                                    String superStatus = "";

    public String getLinkTab() {
        return linkTab;
    }

    public void setLinkTab(String linkTab) {
        this.linkTab = linkTab;
    }

    public String getPartAvalue() {
        return partAvalue;
    }

    public void setPartAvalue(String partAvalue) {
        this.partAvalue = partAvalue;
    }

    public String getPartBvalue() {
        return partBvalue;
    }

    public void setPartBvalue(String partBvalue) {
        this.partBvalue = partBvalue;
    }

    public String getPartAtab() {
        return partAtab;
    }

    public void setPartAtab(String partAtab) {
        this.partAtab = partAtab;
    }

    public String getPartAvalueField() {
        return partAvalueField;
    }

    public void setPartAvalueField(String partAvalueField) {
        this.partAvalueField = partAvalueField;
    }

    public String getPartBtab() {
        return partBtab;
    }

    public void setPartBtab(String partBtab) {
        this.partBtab = partBtab;
    }

    public String getPartBvalueField() {
        return partBvalueField;
    }

    public void setPartBvalueField(String partBvalueField) {
        this.partBvalueField = partBvalueField;
    }

    public String getPartAstatus() {
        return partAstatus;
    }

    public void setPartAstatus(String partAstatus) {
        this.partAstatus = partAstatus;
    }

    public String getPartBstatus() {
        return partBstatus;
    }

    public void setPartBstatus(String partBstatus) {
        this.partBstatus = partBstatus;
    }

    public String getSuperStatus() {
        return superStatus;
    }

    public void setSuperStatus(String superStatus) {
        this.superStatus = superStatus;
    }
                                    
}
