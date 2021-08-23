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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.ShowItForm;

/**
 *
 * @author Franco
 */
public class jsonTranslate {

    public ArrayList<boundFields> makeList(String params) {
        /*
         costruisce una lista di oggetti a partire da una stringa
         contenente coppie label:valore;label:valore
         */
        System.out.println("SONO IN makeList");

        ArrayList<boundFields> myList = new ArrayList<boundFields>();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        String TBSarray = null;
        String xValue = null;
        String xMarker = null;
        if (params != null && params.length() > 0) {
            String tbsJson = "{\"TBS\":" + params + "}";

            try {
                jsonObject = (JSONObject) jsonParser.parse(tbsJson);
                TBSarray = jsonObject.get("TBS").toString();
                if (TBSarray != null && TBSarray.length() > 0) {
                    JSONParser parser = new JSONParser();
                    Object obj;

                    obj = parser.parse(TBSarray);
                    JSONArray array = (JSONArray) obj;
                    int rows = 0;
                    for (Object riga : array) {
                        rows++;
                        boundFields myBound = new boundFields();
                        // jsonObject = (JSONObject) jsonParser.parse(riga.toString());
                        jsonObject = (JSONObject) riga;
                        try {
                            xMarker = jsonObject.get("marker").toString();
                        } catch (Exception e) {
                        }
                        try {
                            xValue = jsonObject.get("value").toString();
                        } catch (Exception e) {
                        }
//                        System.out.println("marker=" + xMarker + "value=" + xValue);
                        if (xValue != null && xMarker != null) {
                            myBound.setValue(xValue.trim());
                            myBound.setMarker(xMarker.trim());
                            myList.add(myBound);
                        }

//                        if (rows < 2) {
//                            System.out.println(" xMarker:" + xMarker + " xValue:" + xValue);
//                        }
                    }
                }
            } catch (ParseException ex) {
                Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return myList;
    }

}
