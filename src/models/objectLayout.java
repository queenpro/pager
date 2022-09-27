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
import org.json.simple.parser.ParseException;

/**
 *
 * @author Franco
 */
public class objectLayout {

    String type;
    String width;
    String height;

    String picWidth;
    String picHeight;
    String fontSize;
    String font;
    String background;
    String color;
    String lines;
    String columns;
    boolean droppable;

    public void loadBoxLayout(String jSonCode, String picWidth, String picHeight) {

        this.picWidth = picWidth;
        this.picHeight = picHeight;
        this.width = picWidth;
        this.height = picHeight;
        try {
            loadBoxLayout(jSonCode);
        } catch (Exception e) {
            System.out.println("error box layout:" + e.toString());
        }
//        System.out.println("creating box picWidth:" +  this.picWidth);
//        System.out.println("creating box picHeight:" + this.picHeight);
//        System.out.println("creating box width:" + this.width);
//        System.out.println("creating box height:" + this.height);

    }

    public void loadBoxLayout(String jSonCode) { 
        if (jSonCode != null) {
//            System.out.println("curObj.C.getJsClass():" + jSonCode);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject;
            try {
                jsonObject = (JSONObject) jsonParser.parse(jSonCode);

                try {
                    type = jsonObject.get("type").toString();
                } catch (Exception e) {
                    type = null;
                }
                try {
                    String pW = jsonObject.get("picWidth").toString();
                    if (pW != null && !pW.equalsIgnoreCase("null")) {
                        pW = pW.replace("px", "");
                        picWidth = pW;
                    }

                } catch (Exception e) {

                }
                try {
                    String pH = jsonObject.get("picHeight").toString();
                    if (pH != null && !pH.equalsIgnoreCase("null")) {
                        pH = pH.replace("px", "");
                        picHeight = pH;
                    }
                } catch (Exception e) {

                }
                try {
                    this.droppable = false;
                    String drp = jsonObject.get("droppable").toString();
                    if (drp.equalsIgnoreCase("true")) {
                        this.droppable = true;
                    }
                } catch (Exception e) {
                    width = null;
                }
                try {
                    width = jsonObject.get("width").toString();
                } catch (Exception e) {
                    width = null;
                }
                try {
                    height = jsonObject.get("height").toString();
                } catch (Exception e) {
                    height = null;
                }
                try {
                    fontSize = jsonObject.get("fontSize").toString();
                } catch (Exception e) {
                    fontSize = null;
                }
                try {
                    font = jsonObject.get("font").toString();
                } catch (Exception e) {
                    font = null;
                }
                try {
                    background = jsonObject.get("background").toString();
                } catch (Exception e) {
                    background = null;
                }
                try {
                    color = jsonObject.get("color").toString();
                } catch (Exception e) {
                    color = null;
                }
                try {
                    lines = jsonObject.get("lines").toString();
                } catch (Exception e) {
                    lines = null;
                }
                try {
                    columns = jsonObject.get("columns").toString();
                } catch (Exception e) {
                    columns = null;
                }

                if (width == null || width.equalsIgnoreCase("null")) {
                    width = picWidth;
                }
                if (height == null || height.equalsIgnoreCase("null")) {
                    height = picHeight;
                }
                if (type == null || type.equalsIgnoreCase("null")) {
                    type = "";
                }
//                System.out.println("picWidth FINAL:" + width);
            } catch (ParseException ex) {

            }
        }
    }

    public boolean isDroppable() {
        return droppable;
    }

    public String getPicWidth() {
        return picWidth;
    }

    public void setPicWidth(String picWidth) {
        this.picWidth = picWidth;
    }

    public String getPicHeight() {
        return picHeight;
    }

    public void setPicHeight(String picHeight) {
        this.picHeight = picHeight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

}
