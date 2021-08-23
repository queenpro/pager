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
import static showIt.eventManager.encodeURIComponent;

/**
 *
 * @author Franco
 */
public class DBimage {

    String table;
    String keyfield;
    String keyValue;
    String picfield;
    String params;
    EVOpagerParams myParams;

    public DBimage(String table, String keyfield, String keyValue, String picfield, EVOpagerParams myParams) {
        this.table = table;
        this.keyfield = keyfield;
        this.keyValue = keyValue;
        this.picfield = picfield;
        this.myParams = myParams;
    }

    public DBimage( EVOpagerParams myParams) {        
        this.myParams = myParams;
    }

    
     public String getDBimageHtmlCode(String query, String field, String width, String height) {
         String code64="";
         
         
         return code64;
         
     }
    public String getDBimageHtmlCode(String width, String height) {

        String image = "<img  alt=\"...\" src='portal?target=requestsManager&gp=";

        String params = "\"params\":\"" + encodeURIComponent(myParams.makePORTALparams()) + "\"";
        String connectors = "\"connectors\":[{\"door\":\"RenderPic\","
                + "\"event\":\"badge\","
                + "\"table\":\"" + table + "\","
                + "\"keyfield\":\"" + keyfield + "\","
                + "\"keyValue\":\"" + keyValue + "\","
                + "\"picfield\":\"" + picfield + "\""
                + " }]";
        String utils = "\"responseType\":\"text\"";
        String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";

        image += encodeURIComponent(gp);
        image += "&rnd=$$$newRandom$$$'  width='" + width + "' heigth='" + height + "' >";
        image += "</td>";

        return image;
    }
    
    
    
}
