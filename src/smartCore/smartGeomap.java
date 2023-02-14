/*
 * Copyright (C) 2022 Franco
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
package smartCore;

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import java.util.Calendar;
import models.gate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Franco
 */
public class smartGeomap {

    public EVOpagerParams myParams;
    public Settings mySettings;
    String formName = "";
    smartForm myForm;
    gate myGate;
    String mode;
    String Latitude;
    String Longitude;
    int zoom;
    public JSONArray Pins;

    public smartGeomap(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        this.Pins = new JSONArray();
    }

    public void step1getInfos(gate myGate, String mode) {
        this.myGate = myGate;
        this.mode = mode;
        String mapFormID = "";

        if (mode.equalsIgnoreCase("INSIDEUPDATE")) {
            mapFormID = myGate.getFormID();
        } else if (mode.equalsIgnoreCase("getPoint")) {
            mapFormID = myGate.getFormID();
        } else {
            mapFormID = myGate.getChild_formID();
        }

        //1. risaliamo al form che genera la richiesta
        System.out.println("smartGeomap --> form mode :" + mode);
        System.out.println("smartGeomap --> form getFormID :" + myGate.getFormID());
        System.out.println("smartGeomap --> form getFormName :" + myGate.getFormName());
        System.out.println("smartGeomap --> form getFormToLoad :" + myGate.getChild_formID());

        //1. risaliamo ala tabella dei duties con la query specifica
        myForm = new smartForm(mapFormID, myParams, mySettings);
        System.out.println("smartGeomap--> myGate.getSendToCRUD() :" + myGate.getSendToCRUD());
        System.out.println("smartGeomap--> myGate.getTBS() :" + myGate.getTBS());
        myForm.setSendToCRUD(myGate.getTBS());

        myForm.loadSettingsAndPanel();
        myForm.setToBeSent(myGate.getTBS());
        myForm.prepareSQL(myForm.getQuery());
        this.formName = myForm.getName();
        try {
            this.Pins = myForm.createGeomapPointsArray();
        } catch (Exception e) {
        }

    }
public String makeMapHtmlCode(){
     String htmlMapCode = "";
     String formH = myForm.getFormHeight();
                String formW = myForm.getFormWidth();
                if (formH == null || formH.length() < 3) {
                    formH = "500px";
                }
                System.out.println("# getPoint -->Pins.size:" + Pins.size());
                System.out.println("# getPoint -->Lat:" +  Latitude );
                System.out.println("# getPoint -->Lon:" +  Longitude );
 
                htmlMapCode += ("<div id=\"map\" style=\"height: " + formH + ";\"></div>");
     return htmlMapCode;
}
    public JSONObject makegeomapInfos(String latitudine, String longitudine) {
   JSONObject geomapInfos = new JSONObject();
                geomapInfos.put("Latitude", latitudine);
                geomapInfos.put("Longitude", longitudine);
                geomapInfos.put("Pins", Pins);
                return geomapInfos;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public void setMyForm(smartForm myForm) {
        this.myForm = myForm;
    }

    public void setMyGate(gate myGate) {
        this.myGate = myGate;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String Latitude) {
        this.Latitude = Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String Longitude) {
        this.Longitude = Longitude;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public EVOpagerParams getMyParams() {
        return myParams;
    }

    public Settings getMySettings() {
        return mySettings;
    }

    public String getFormName() {
        return formName;
    }

    public smartForm getMyForm() {
        return myForm;
    }

    public gate getMyGate() {
        return myGate;
    }

    public String getMode() {
        return mode;
    }

}
