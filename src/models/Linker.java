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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class Linker {
    

        String partAtab = "";
        String partAfield = "";
        String partBtab = "";
        String partBquery = "";
        String partBvalueField = "";
        String partBlabelField = "";
        String partBiconField = "";
        String linkTableName = "";

        public void readParamsJson(String params) {
            if (params != null && params.length() > 4) {
                try {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(params);

                    try {
                        partAtab = jsonObject.get("partAtab").toString();
                    } catch (Exception ex) {
                    }
                    try {
                        partAfield = jsonObject.get("partAvalueField").toString();
                    } catch (Exception ex) {
                    }
                    try {
                        partBtab = jsonObject.get("partBtab").toString();
                    } catch (Exception ex) {
                    }
                    try {
                        partBquery = jsonObject.get("partBquery").toString();
                    } catch (Exception ex) {
                    }
                    try {
                        partBvalueField = jsonObject.get("partBvalueField").toString();
                    } catch (Exception ex) {
                    }
                    try {
                        partBlabelField = jsonObject.get("partBlabelField").toString();
                    } catch (Exception ex) {
                    }
                    try {
                        partBiconField = jsonObject.get("partBiconField").toString();
                    } catch (Exception ex) {
                    }                   try {
                        linkTableName = jsonObject.get("linkTableName").toString();
                    } catch (Exception ex) {
                    }
                } catch (org.json.simple.parser.ParseException pe) {
                    
                      System.out.println("\n----LINKER ERROR:" + pe.toString());
                 }
                
                if (linkTableName == null || linkTableName.length() < 5) {
                    linkTableName = "archivio_correlazioni";
                }
//                  System.out.println("\n----linkTableName:" + linkTableName);
            }
        }

        public String getPartAtab() {
            return partAtab;
        }

        public void setPartAtab(String partAtab) {
            this.partAtab = partAtab;
        }

        public String getPartAfield() {
            return partAfield;
        }

        public void setPartAfield(String partAfield) {
            this.partAfield = partAfield;
        }

        public String getPartBtab() {
            return partBtab;
        }

        public void setPartBtab(String partBtab) {
            this.partBtab = partBtab;
        }

        public String getPartBquery() {
            return partBquery;
        }

        public void setPartBquery(String partBquery) {
            this.partBquery = partBquery;
        }

        public String getPartBvalueField() {
            return partBvalueField;
        }

        public void setPartBvalueField(String partBvalueField) {
            this.partBvalueField = partBvalueField;
        }

        public String getPartBlabelField() {
            return partBlabelField;
        }

        public void setPartBlabelField(String partBlabelField) {
            this.partBlabelField = partBlabelField;
        }

        public String getPartBiconField() {
            return partBiconField;
        }

        public void setPartBiconField(String partBiconField) {
            this.partBiconField = partBiconField;
        }

        public String getLinkTableName() {
            return linkTableName;
        }

        public void setLinkTableName(String linkTableName) {
            this.linkTableName = linkTableName;
        }

}
