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
public class ShowItFormResponse {
    String HtmlCode="";
    String ges_routineOnLoad="";

    public String getHtmlCode() {
        return HtmlCode;
    }

    public void setHtmlCode(String HtmlCode) {
        this.HtmlCode = HtmlCode;
    }

    public String getGes_routineOnLoad() {
        return ges_routineOnLoad;
    }

    public void setGes_routineOnLoad(String ges_routineOnLoad) {
        this.ges_routineOnLoad = ges_routineOnLoad;
    }
    
}
