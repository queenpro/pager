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

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.ShowItForm;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class SMARTgate {

    public String DOOR = "";
    public String EVENT = "";
    public String FORMID = "";
    public String INSTANCEID = "";
    public String LOADTYPE = "";
    public String OBJNAME = "";
    public String OBJKEY = "";
    public String ACTION = "";
    public String SENDTOCRUD = "";
    public String LEVEL = "";
    public String panJson = "";

 public String deploySMARTgate(  EVOpagerParams myParams,Settings mySettings ) {
     
     mySettings.printSettings("deploySMARTgate");
     myParams.printParams("deploySMARTgate");
             
        String rispostaJSON = "";

        System.out.println("CONNECTOR : DOOR=" + this.getDOOR());
        System.out.println("CONNECTOR : EVENT=" + this.getEVENT());
        System.out.println("CONNECTOR : FORMID=" + this.getFORMID());
        System.out.println("CONNECTOR : INSTANCEID=" + this.getINSTANCEID());
        System.out.println("CONNECTOR : LOADTYPE()=" + this.getLOADTYPE());
        System.out.println("CONNECTOR : OBJKEY=" + this.getOBJKEY());
        System.out.println("CONNECTOR : OBJNAME=" + this.getOBJNAME());
        System.out.println("CONNECTOR : LEVEL=" + this.getLEVEL());
        System.out.println("CONNECTOR : SENDTOCRUD=" + this.getSENDTOCRUD());
        String destSpan = this.getFORMID() + "-"
                + this.getINSTANCEID() + "-"
                + this.getOBJNAME() + "-"
                + this.getOBJKEY();

        ShowItObject curObj = new ShowItObject(this.getOBJNAME());
        curObj.Content.Value = this.getOBJKEY();
        ShowItForm myForm = new ShowItForm(this.getFORMID(), myParams, mySettings);
        myForm.setSendToCRUD(this.getSENDTOCRUD());
        myForm.setID(this.getFORMID());
        myForm.setCopyTag(this.getINSTANCEID());
        myForm.buildSchema();
        SMARTtreeView myTree = new SMARTtreeView(myParams, mySettings);
        int lvl = Integer.parseInt(this.getLEVEL());
        rispostaJSON = myTree.buildChildNodes(lvl, myForm, curObj, destSpan, this.getEVENT());

        System.out.println("rispostaJSON =" + rispostaJSON);
        return rispostaJSON;
    }

    public String getLEVEL() {
        return LEVEL;
    }

    public void setLEVEL(String LEVEL) {
        this.LEVEL = LEVEL;
    }

    public String getDOOR() {
        return DOOR;
    }

    public void setDOOR(String DOOR) {
        this.DOOR = DOOR;
    }

    public String getEVENT() {
        return EVENT;
    }

    public void setEVENT(String EVENT) {
        this.EVENT = EVENT;
    }

    public String getFORMID() {
        return FORMID;
    }

    public void setFORMID(String FORMID) {
        this.FORMID = FORMID;
    }

    public String getINSTANCEID() {
        return INSTANCEID;
    }

    public void setINSTANCEID(String INSTANCEID) {
        this.INSTANCEID = INSTANCEID;
    }

    public String getLOADTYPE() {
        return LOADTYPE;
    }

    public void setLOADTYPE(String LOADTYPE) {
        this.LOADTYPE = LOADTYPE;
    }

    public String getOBJNAME() {
        return OBJNAME;
    }

    public void setOBJNAME(String OBJNAME) {
        this.OBJNAME = OBJNAME;
    }

    public String getOBJKEY() {
        return OBJKEY;
    }

    public void setOBJKEY(String OBJKEY) {
        this.OBJKEY = OBJKEY;
    }

    public String getACTION() {
        return ACTION;
    }

    public void setACTION(String ACTION) {
        this.ACTION = ACTION;
    }

    public String getSENDTOCRUD() {
        return SENDTOCRUD;
    }

    public void setSENDTOCRUD(String SENDTOCRUD) {
        this.SENDTOCRUD = SENDTOCRUD;
    }

    public String getPanJson() {
        return panJson;
    }

    public void setPanJson(String panJson) {
        this.panJson = panJson;
    }

}
