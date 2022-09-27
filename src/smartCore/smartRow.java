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
package smartCore;

import smartCore.smartForm;
import REVOpager.DBimage;
import REVOpager.EVOpagerDBconnection;
import REVOpager.EVOuser;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import models.CRUDorder;
import models.Linker;
import models.SelectList;
import models.SelectListLine;
import models.ShowItObject;
import models.boundFields;
import models.objectLayout;
import models.requestsManager;
import static showIt.eventManager.encodeURIComponent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.ShowItForm;

/**
 *
 * @author FFS INFORMATICA [info at ffs.it]
 */
public class smartRow {

    smartObjRight formRightsRules;
    smartObjRight actualRowRights;
    int rowNumber;
    smartForm myForm;
    String KEYvalue = "";
    ResultSet rs;
    JSONObject PREVrs;
    ArrayList<boundFields> rowValues;
    String treeObjAddedName = "";

    public smartRow(smartForm xForm, ResultSet Xrs, int rowNumber) {
        this.myForm = xForm;
        this.rowNumber = rowNumber;
        this.rs = Xrs;
        rowValues = elaboraRigaRS(rs, actualRowRights);
//        System.out.println("CREATA smartROW. Il numero di objects è:" + myForm.objects.size());
    }

    public String getTreeObjAddedName() {
        return treeObjAddedName;
    }

    public smartObjRight getFormRightsRules() {
        return formRightsRules;
    }

    public void setFormRightsRules(smartObjRight formRightsRules) {
        this.formRightsRules = formRightsRules;
    }

    public smartObjRight getActualRowRights() {
        return actualRowRights;
    }

    public void setActualRowRights(smartObjRight actualRowRights) {
        this.actualRowRights = actualRowRights;
    }

    public String SMRTpaintRow(String rowType, JSONObject prevRS) {

        this.PREVrs = prevRS;
//        System.out.println("Prev col count: " + PREVrs.size()+" -->"+ PREVrs.toString());      

        return SMRTpaintRow(rowType);

    }

    public String SMRTpaintRow(String rowType) {

        String htmlCode = "";

        if (rowType.equalsIgnoreCase("adding")) {
// <editor-fold defaultstate="collapsed" desc="CASO ADDING ROW.">
//                System.out.println("CASO ADDING ROW->formRightsRules.canCreate: " + formRightsRules.canCreate);
//                System.out.println("CASO ADDING ROW->actualRowRights.canCreate: " + actualRowRights.canCreate);
            if (formRightsRules.canCreate > 0) {
// caso Pattern
                if (myForm.getHtmlPattern() != null && myForm.getHtmlPattern().length() > 0) {
                    htmlCode += "<tr id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-NEW-ROW\"  class=\"tabAddRow\" >";
                    htmlCode += "<td  class=\"lineSelector\" > ";
                    int flag = 0;
                    int presentAddingFields = 0;
                    for (int obj = 0; obj < myForm.objects.size(); obj++) {
                        if (myForm.objects.get(obj).Content.isPrimaryFieldAutocompiled()) {
                            flag++;
                        }
                        if (myForm.objects.get(obj).AddingRow_enabled > 0) {
                            presentAddingFields++;
                        }
                    }
                    if (flag > 0 && presentAddingFields == 0) { // asterisco per nuova riga senza inserimento di un particolare field
                        htmlCode += "<a";
                        String jsonArgs = "{";
                        jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                        jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                        jsonArgs += "\"objName\":\"INSERT_AI\",";
                        jsonArgs += "\"KEYvalue\":\"INSERT_AI\",";
                        jsonArgs += "\"operation\":\"NEW\",";
                        jsonArgs += "\"cellType\":\"AI\",";
                        jsonArgs += "\"valueType\":\"INT\",";
                        jsonArgs += "\"filterField\":\"\",";
                        jsonArgs += "\"exitRoutine\":\"\"}";
                        htmlCode += " onClick='javascript:smartCellChanged(" + jsonArgs + ")'  style=\"block\">";
                        htmlCode += " <img src=\"./media/iconADD.png\" alt=\"NEW\" "
                                + "style=\"margin-left: auto; margin-right: auto;width:12px;height:12px;border:0\">";
                        htmlCode += "</a>";
                    }
                    //-------------
                    htmlCode += " </td>";

                    if (formRightsRules.canDelete > 0) {
                        htmlCode += "<td  class=\"lineDeleter\"  > </td>";
                    }
                    try {
                        htmlCode += encodeAddingPatternRow();
                    } catch (SQLException ex) {
                        Logger.getLogger(smartRow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    htmlCode += " </tr>";
                } else // caso gridTable               
                {
                    htmlCode += "<tr id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-NEW-ROW\"  class=\"tabAddRow\" >";
                    htmlCode += "<td  class=\"lineSelector\" > ";
                    int flag = 0;
                    int presentAddingFields = 0;
                    for (int obj = 0; obj < myForm.objects.size(); obj++) {
                        if (myForm.objects.get(obj).Content.isPrimaryFieldAutocompiled()) {
                            flag++;
                        }
                        if (myForm.objects.get(obj).AddingRow_enabled > 0) {
                            presentAddingFields++;
                        }
                    }
                    if (flag > 0 && presentAddingFields == 0) { // asterisco per nuova riga senza inserimento di un particolare field
                        htmlCode += "<a";
                        String jsonArgs = "{";
                        jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                        jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                        jsonArgs += "\"objName\":\"INSERT_AI\",";
                        jsonArgs += "\"KEYvalue\":\"INSERT_AI\",";
                        jsonArgs += "\"operation\":\"NEW\",";
                        jsonArgs += "\"cellType\":\"AI\",";
                        jsonArgs += "\"valueType\":\"INT\",";
                        jsonArgs += "\"filterField\":\"\",";
                        jsonArgs += "\"exitRoutine\":\"\"}";
                        htmlCode += " onClick='javascript:smartCellChanged(" + jsonArgs + ")'  style=\"block\">";
                        htmlCode += " <img src=\"./media/iconADD.png\" alt=\"NEW\" "
                                + "style=\"margin-left: auto; margin-right: auto;width:12px;height:12px;border:0\">";
                        htmlCode += "</a>";
                    }
                    //-------------
                    htmlCode += " </td>";

                    if (formRightsRules.canDelete > 0) {
                        htmlCode += "<td  class=\"lineDeleter\"  > </td>";
                    }
//                    System.out.println("COMPILO " + myForm.objects.size() + " CAMPI IN ADDING ROW");
                    for (int obj = 0; obj < myForm.objects.size(); obj++) {
                        if (!myForm.objects.get(obj).CG.getType().equalsIgnoreCase("FORMBUTTON")) {
                            myForm.objects.get(obj).setValueToWrite("");

                            htmlCode += "<td  class=\"newlineField\" ";
                            if (myForm.objects.get(obj).getActuallyVisible() < 1) {
                                htmlCode += " style=\"width:0px; display:none;\" ";
                            } else {
                                if (myForm.objects.get(obj).C.getWidth() != null && myForm.objects.get(obj).C.getWidth() != "null" && myForm.objects.get(obj).C.getWidth() != "") {
                                    htmlCode += " style=\"width:" + myForm.objects.get(obj).C.getWidth() + ";\" ";
                                }
                            }

                            htmlCode += ">";

                            htmlCode += "<div id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + myForm.objects.get(obj).name + "-" + "NEW" + "-PLACE\"  ";

                            htmlCode += ">";
                            if (myForm.objects.get(obj).defaultValue == null) {

                            } else {
                                if (myForm.objects.get(obj).defaultValue.length() > 0) {
                                    myForm.objects.get(obj).setAddingRow_enabled(0);
                                }
                            }
                            if (myForm.objects.get(obj).getAddingRow_enabled() > 0) {
                                //------------------------------------------------- 
////////                            htmlCode += paintObject("NEW", myForm.objects.get(obj));

                                htmlCode += paintObject("NEW", myForm.objects.get(obj), formRightsRules);
                                //------------------------------------------------- 
                            } else {

                            }
                            htmlCode += "</div></td>";
                            htmlCode += "</td>";
                        }
                    }
                    //    htmlCode += "</div> ";     
                    htmlCode += " </tr>";
                }
            }

            //</editor-fold> 
        } else if (rowType.equalsIgnoreCase("total")) {
// <editor-fold defaultstate="collapsed" desc="TOTALS ROW.">

            if (formRightsRules.canView > 0) {
                String totLabel = "";
                for (int obj = 0; obj < myForm.objects.size(); obj++) {
                    if (myForm.objects.get(obj).Content.getHasSum() > 0) {
                        totLabel = "TOT:";
                        break;
                    }
                }
                htmlCode += "<tr class=\"tabTotalsRow\" >";
                if (myForm.getShowCounter() != null && myForm.getShowCounter().equalsIgnoreCase("FALSE")) {
                    htmlCode += "<td></td>";
                } else {
                    htmlCode += "<td  class=\"lineSelector\" >" + totLabel + "</td>";

                }

                if (formRightsRules.canDelete > 0) {
                    htmlCode += "<td  class=\"lineDeleter\"  > </td>";
                }

                for (int obj = 0; obj < myForm.objects.size(); obj++) {

                    if (!myForm.objects.get(obj).CG.getType().equalsIgnoreCase("FORMBUTTON")) {
                        myForm.objects.get(obj).setValueToWrite("");

                        htmlCode += "<td  class=\"lineField\" ";
                        if (myForm.objects.get(obj).getActuallyVisible() == 0) {
                            htmlCode += " style=\"width:0px;\" ";
                        } else {
                            if (myForm.objects.get(obj).C.getWidth() != null && myForm.objects.get(obj).C.getWidth() != "null" && myForm.objects.get(obj).C.getWidth() != "") {
                                htmlCode += " style=\"width:" + myForm.objects.get(obj).C.getWidth() + ";\" ";
                            }
                        }
                        htmlCode += ">";
                        htmlCode += "<div id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + myForm.objects.get(obj).name + "-" + "TOT" + "-PLACE\"  ";
                        htmlCode += "class =\"totalContent ";
                        if (myForm.objects.get(obj).Content.getActualSum() < 0) {
                            htmlCode += " negativeNumber  ";
                        } else {
                            htmlCode += " positiveNumber  ";
                        }
                        htmlCode += "\" >";

//                        System.out.println("TOTALS ROW ++oggetto " + myForm.objects.get(obj).getName() + " - HAS SUM =" + myForm.objects.get(obj).Content.getHasSum() + " -  SUM =" + myForm.objects.get(obj).Content.getActualSum());
                        if (myForm.objects.get(obj).Content.getHasSum() > 0) {
                            //------------------------------------------------- 
                            float no = myForm.objects.get(obj).Content.getActualSum();
                            Locale.setDefault(Locale.ITALY);
                            String str = String.format("%,f", no);
                            if (myForm.objects.get(obj).Content.getType() != null && myForm.objects.get(obj).Content.getType().equalsIgnoreCase("FLOAT")) {

                                str = "" + myForm.objects.get(obj).Content.getActualSum();

                                float number = 0;
                                try {

                                    String strno = str;
                                    strno = strno.replace(",", ".");
                                    number = Float.valueOf(strno);
                                } catch (Exception e) {
                                    System.out.println("error rounding:  " + e.toString());
                                }
                                str = "" + number;

////                                if (str != null && str.length() > 0) {
////                                    // tronco a 3 cifre dopo il punto
////                                    int posX = str.length() + 1;
////                                    if (str.contains(".")) {
////                                        posX = str.lastIndexOf(".");
////                                        posX = posX + 2;
////                                        if (str.length() > posX) {
////                                            str = str.substring(0, posX + 1);
////                                        }
////                                    } else if (str.contains(",")) {
////                                        posX = str.lastIndexOf(",");
////                                        posX = posX + 2;
////                                        if (str.length() > posX) {
////                                            str = str.substring(0, posX + 1);
////                                        }
////                                    }
////                                }
                            } else if (myForm.objects.get(obj).Content.getType() != null && myForm.objects.get(obj).Content.getType().equalsIgnoreCase("EURO")) {
//                                System.out.println("DEVO ARROTONDARE NUMERO  " + no);
                                DecimalFormat df = new DecimalFormat("#.00");
                                float number = no;
                                try {

                                    String strno = df.format(no);
                                    strno = strno.replace(",", ".");
                                    number = Float.valueOf(strno);
                                } catch (Exception e) {
                                    System.out.println("error rounding:  " + e.toString());
                                }
                                str = "€ " + number;
                            } else if (myForm.objects.get(obj).Content.getType() != null &&( myForm.objects.get(obj).Content.getType().equalsIgnoreCase("MINtoHOURS")|| myForm.objects.get(obj).Content.getType().equalsIgnoreCase("MINStoHOURS"))) {
                                try {
                                    int hours = (int) (no / 60); //since both are ints, you get an int
                                    int minutes = (int) (no % 60);
                                    str = hours + "h " + minutes + "m";
                                } catch (Exception e) {
                                }
                            }

                            htmlCode += str;
                            //------------------------------------------------- 
                        } else {
                            htmlCode += "";
                        }
                        htmlCode += "</div></td>";
                        htmlCode += "</td>";
                    }
                }
                //  htmlCode += "</div> ";
                htmlCode += " </tr>";

            }

            //</editor-fold> 
        } else if (rowType.equalsIgnoreCase("bubble")) {
// <editor-fold defaultstate="collapsed" desc="bubble ROW."> 
            if (myForm.getKEYfieldName() != null) {
                if (myForm.getKEYfieldType() != null && myForm.getKEYfieldType().equalsIgnoreCase("INT")) {
                    int myKEYvalue;
                    try {
                        myKEYvalue = rs.getInt(myForm.getKEYfieldName());
                        KEYvalue = "" + myKEYvalue;
                    } catch (SQLException ex) {
                        Logger.getLogger(smartRow.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    KEYvalue = "" + rowNumber;
                    try {
                        KEYvalue = rs.getString(myForm.getKEYfieldName());
                    } catch (SQLException ex) {
                    }
                }
            } else {
                KEYvalue = "" + rowNumber;
            }
            htmlCode += encodeBubble();

            //</editor-fold> 
        } else if (rowType.equalsIgnoreCase("calendarDate")) {
// <editor-fold defaultstate="collapsed" desc="calendarDate."> 
            if (myForm.getKEYfieldName() != null) {
                if (myForm.getKEYfieldType() != null && myForm.getKEYfieldType().equalsIgnoreCase("INT")) {
                    int myKEYvalue;
                    try {
                        myKEYvalue = rs.getInt(myForm.getKEYfieldName());
                        KEYvalue = "" + myKEYvalue;
                    } catch (SQLException ex) {
                        Logger.getLogger(smartRow.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    KEYvalue = "" + rowNumber;
                    try {
                        KEYvalue = rs.getString(myForm.getKEYfieldName());
                    } catch (SQLException ex) {
                    }
                }
            } else {
                KEYvalue = "" + rowNumber;
            }
            htmlCode += encodeBubble();

            //</editor-fold>           
        } else {
            // <editor-fold defaultstate="collapsed" desc="CASO NORMAL ROW."> 
            htmlCode += encodeNormalRow();
//</editor-fold> 
        }

        return htmlCode;
    }


    public String encodeBubble() {
        String htmlCode = "";
        if (myForm.getKEYfieldName() != null) {
            if (myForm.getKEYfieldType() != null && myForm.getKEYfieldType().equalsIgnoreCase("INT")) {
                int myKEYvalue;
                try {
                    myKEYvalue = rs.getInt(myForm.getKEYfieldName());
                    KEYvalue = "" + myKEYvalue;
                } catch (SQLException ex) {
                    Logger.getLogger(smartRow.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                KEYvalue = "" + rowNumber;
                try {
                    KEYvalue = rs.getString(myForm.getKEYfieldName());
                } catch (SQLException ex) {
                }
            }
        } else {
            KEYvalue = "" + rowNumber;
        }
        htmlCode += "<div "
                + "draggable=\"true\"  ondragstart=\"drag(event)\"  "
                + "id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-"
                + KEYvalue + "-ROW\" class=\"bubbles\"  ";//---resta aperta per style eventuale

        String objectsCode = "";
        String actualStyle = "";
        for (int obj = 0; obj < myForm.objects.size(); obj++) {

            if (!myForm.objects.get(obj).CG.getType().equalsIgnoreCase("FORMBUTTON")) {
                String triggeredStyle = feedTriggeredStyle(myForm.objects.get(obj), rs);
                if (triggeredStyle != null && triggeredStyle.length() > 2) {
                    System.out.println("Imposto lo stile da trigger come default: " + triggeredStyle);
                    myForm.objects.get(obj).setTriggeredStyle(triggeredStyle);
                } else {
                    myForm.objects.get(obj).setTriggeredStyle("");
                }

//                htmlCode += "<a style=\"display:block;\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + myForm.objects.get(obj).name + "-" + KEYvalue + "-PLACE\" >";
//------------------------------------------------- 
                smartObjRight realObjRights = valutaRightsOggetto(myForm.objects.get(obj), rs);
                smartObjRight actualObjectRights = joinRights(realObjRights, actualRowRights);
                String objCode = "";

                objCode += paintObject(KEYvalue, myForm.objects.get(obj), actualObjectRights);
                if (objCode.length() > 0 && myForm.objects.get(obj).getTriggeredStyle().length() > 0) {
                    objCode = "<a style=\"" + myForm.objects.get(obj).getTriggeredStyle() + "\" >" + objCode + "</a>";
                    actualStyle = myForm.objects.get(obj).getTriggeredStyle();
                }
                objectsCode += objCode;
//-------------------------------------------------  
//                htmlCode += "</a>";
            }
        }

        if (actualStyle.length() > 0) {
            htmlCode += "  style=\"" + actualStyle + "\" >";
        } else {
            htmlCode += ">";
        }

        htmlCode += objectsCode;
        htmlCode += "</div>";
//====END=ROW===================        
        return htmlCode;
    }

    public JSONObject encodeTreeRow() { //per TREEVIEW

        JSONObject myBrand = new JSONObject();
        System.out.println("SONO IN encodeTreeRow");
        if (myForm.getKEYfieldName() != null) {
            if (myForm.getKEYfieldType() != null && myForm.getKEYfieldType().equalsIgnoreCase("INT")) {
                int myKEYvalue;
                try {
                    myKEYvalue = rs.getInt(myForm.getKEYfieldName());
                    KEYvalue = "" + myKEYvalue;
                } catch (SQLException ex) {
                    Logger.getLogger(smartRow.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                KEYvalue = "" + rowNumber;
                try {
                    KEYvalue = rs.getString(myForm.getKEYfieldName());
                } catch (SQLException ex) {
                }
            }
        } else {
            KEYvalue = "" + rowNumber; //                    System.out.println("paintRow:AUTO INDICIZZAZIONE ATTIVATA !!!"); 
        }

        myBrand.put("ID", myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-ROW");
        String value = "";

        for (int obj = 0; obj < myForm.objects.size(); obj++) {
            if (!myForm.objects.get(obj).CG.getType().equalsIgnoreCase("FORMBUTTON")) {
                String triggeredStyle = feedTriggeredStyle(myForm.objects.get(obj), rs);
                if (triggeredStyle != null && triggeredStyle.length() > 2) {
                    //  System.out.println("Imposto lo stile da trigger come default: " + triggeredStyle);
                    myForm.objects.get(obj).setTriggeredStyle(triggeredStyle);
                } else {
                    myForm.objects.get(obj).setTriggeredStyle("");
                }

                smartObjRight realObjRights = valutaRightsOggetto(myForm.objects.get(obj), rs);
                System.out.println("realObjRights OGGETTO " + myForm.objects.get(obj).name + " "
                        + "paintObject objVisibile:" + realObjRights.canView + " ");

                System.out.println("actualRowRights OGGETTO " + myForm.objects.get(obj).name + " "
                        + "paintObject objVisibile:" + actualRowRights.canView + " ");

                smartObjRight actualObjectRights = joinRights(realObjRights, actualRowRights);

                value += paintLeafObject(KEYvalue, myForm.objects.get(obj), actualObjectRights);
            }
        }
        myBrand.put("value", value);

        return myBrand;
    }

    public String paintLeafObject(String KEYvalue, smartObject curObj, smartObjRight objRights) {
        objRights.evaluateRights();
        boolean objModifiable = true;
        boolean objVisibile = true;
        boolean objCanPushButton = true;
        String objType = curObj.C.getType();

        if (objRights.canModify <= 0) {
            objModifiable = false;
        } else {
            objModifiable = true;
        }
        if (objRights.canPushButton != 0) {
            objCanPushButton = true;
        } else {
            objCanPushButton = false;
        }
        if (objRights.canView <= 0) {
            objVisibile = false;
        } else {
            objVisibile = true;
        }

        System.out.println("OGGETTO " + curObj.name + " "
                + "paintObject objVisibile:" + objVisibile + " "
                + "paintObject objModifiable:" + objModifiable + " "
                + "paintObject objCanPushButton:" + objCanPushButton);

        String htmlCode = "";

        String ValoreDaScrivere = curObj.getValueToWrite();
//        System.out.println("\n >>paintObject: ValoreDaScrivere:" + ValoreDaScrivere);
        if (KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")) {
            //  System.out.println("OGGETTO IN RIGA NEW ");
            if (curObj.AddingRow_enabled < 1) {
//                System.out.println("NON PRESENTE IN ADDING ROW");
                curObj.Content.setThisRowModifiable(0);
                objVisibile = false;
                objModifiable = false;

            } else {
//                System.out.println("PRESENTE IN ADDING ROW");
                objVisibile = true;
                objModifiable = true;
            }
        }
        if (objType == null) {
            objType = "TEXT";
        }
        // gestisco il fatto che un campo che sarà solo label possa essere in principio compilabile nella newLine
        if (objType != null && objType.equalsIgnoreCase("LABEL") && KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")) {
            objType = "TEXT";
        }
        if (KEYvalue == null) {
            KEYvalue = "";
        }
        if (!KEYvalue.equalsIgnoreCase("NEW") && !KEYvalue.equalsIgnoreCase("MULTILINELABEL")
                && (objModifiable == false)
                && (objType.equalsIgnoreCase("TEXT")
                || objType.equalsIgnoreCase("PASSWORD"))) {
            objType = "LABEL";
        } else if (!KEYvalue.equalsIgnoreCase("NEW") && KEYvalue.equalsIgnoreCase("TEXTAREA")) {
            objType = "MULTILINELABEL";
        }
        if (!objType.equalsIgnoreCase("LABEL")) {
        } else {
//            System.out.println("curObj.visible: "+curObj.visible);
            if (objVisibile == true) {
                htmlCode += ValoreDaScrivere + " ";
            }
        }

        return htmlCode;
    }

    public String encodeNormalRow() {
        String htmlCode = "";

        if (myForm.getKEYfieldName() != null) {
            if (myForm.getKEYfieldType() != null && myForm.getKEYfieldType().equalsIgnoreCase("INT")) {
                int myKEYvalue;
                try {
                    myKEYvalue = rs.getInt(myForm.getKEYfieldName());
                    KEYvalue = "" + myKEYvalue;

                } catch (SQLException ex) {
                    Logger.getLogger(smartRow.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                //System.out.println("Prendo numerico: KEYfieldName :" + myForm.getKEYfieldName() + " KEYfieldType :" + myForm.getKEYfieldType());
            } else {
                KEYvalue = "" + rowNumber;
                try {
                    KEYvalue = rs.getString(myForm.getKEYfieldName());
                    //System.out.println("Prendo stringa: KEYfieldName :" + myForm.getKEYfieldName() + " KEYfieldType :" + myForm.getKEYfieldType());
                } catch (SQLException ex) {
//                    Logger.getLogger(smartRow.class.getName()).log(Level.SEVERE, null, ex);

                }
            }
        } else {
            KEYvalue = "" + rowNumber;
//                    System.out.println("-------------");
//                    System.out.println(myForm.query);
//                    System.out.println("paintRow:AUTO INDICIZZAZIONE ATTIVATA !!!");
//                    System.out.println("-------------");
        }
        String ValueAssigned = getBGcolor(myForm.getRowBGcolor(), rs);
        //stabilisco il colore di background

        //----------------------------------------------------------------------
        htmlCode += "<tr id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-"
                + KEYvalue + "-ROW\" class=\"unselectedRow\" >";

//                System.out.println("-------->paintRowSelector() ");
        htmlCode += paintRowSelector(rowNumber, KEYvalue);

        // delete button-------------
        if (formRightsRules.canDelete > 0) {//per il FORM
            htmlCode += "<td class=\"lineDeleter\"   >"
                    + "<a id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-DEL\"   ";
            if (actualRowRights.canDelete > 0) {// PER LA SINGOLA RIGA
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"DEL\",";
                jsonArgs += "\"cellType\":\"X\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"exitRoutine\":\"dummy()\"}";
                htmlCode += " onClick='javascript:smartCellChanged(" + jsonArgs + ")' >";
                htmlCode += " <img  height=\"15\" width=\"15\" align=\"middle\" src='./media/icons/IconDELETE.gif' alt='ELIMINA' ";

                htmlCode += " ></img>";
                htmlCode += "</a> ";
            }

            htmlCode += "</td>";
        }

       // System.out.println("\n--PaintRow_elaboraRigaRS per riga n." + rowNumber);
        ArrayList<boundFields> rowValues = elaboraRigaRS(rs, actualRowRights);
        if (myForm.getHtmlPattern() != null && myForm.getHtmlPattern().length() > 0) {
            try {
                htmlCode += encodeNormalPatternRow();
            } catch (SQLException ex) {
                Logger.getLogger(smartRow.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                htmlCode += encodeNormalGridRow();
            } catch (SQLException ex) {
                Logger.getLogger(smartRow.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        htmlCode += "</tr>";

//         System.out.println("\n--PaintRow_Row encoded: " + htmlCode);
        return htmlCode;
    }

    public String encodeNormalGridRow() throws SQLException {
//          System.out.println("encodeNormalGridRow." );
        String htmlCode = "";

        for (int obj = 0; obj < myForm.objects.size(); obj++) {

            if (!myForm.objects.get(obj).CG.getType().equalsIgnoreCase("FORMBUTTON")) {
                String triggeredStyle = feedTriggeredStyle(myForm.objects.get(obj), rs);
                if (triggeredStyle != null && triggeredStyle.length() > 2) {
                    //  System.out.println("Imposto lo stile da trigger come default: " + triggeredStyle);
                    myForm.objects.get(obj).setTriggeredStyle(triggeredStyle);
                } else {
                    myForm.objects.get(obj).setTriggeredStyle("");
                }

//----------------------                    
//---CREO IL TD PER L'OGGETTO------------------- 
                htmlCode += "<td  class=\"lineField\" ";
                if (myForm.objects.get(obj).getActuallyVisible() < 1) { // se è visibile a livello FORM
                    htmlCode += " style=\"width:0px; display:none;\" ";
                } else {
                    if (myForm.objects.get(obj).C.getWidth() != null && myForm.objects.get(obj).C.getWidth() != "null" && myForm.objects.get(obj).C.getWidth() != "") {
                        String myWidth = myForm.objects.get(obj).C.getWidth();
                        htmlCode += " style=\"width:" + myWidth + ";\" ";
                    }
                }

                htmlCode += ">";
//----------------------                    
//---INSERISCO L'OGGETTO-------------------                    
                htmlCode += "<div id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + myForm.objects.get(obj).name + "-" + KEYvalue + "-PLACE\"  ";

                htmlCode += ">";

                if (myForm.objects.get(obj).C.getType().equalsIgnoreCase("REALTIMESELECT")) {
//                        System.out.println("RICREO SELECT LIST");
                    String oQuery = myForm.objects.get(obj).Origin.getQuery();
//                System.out.println(" myForm.sendToCRUD:" + myForm.sendToCRUD);

                    //essendo un realtime select il replace va fatto qui sulla row e non con il replace del form
                    oQuery = browserRowArgsReplace(oQuery);
//                System.out.println("REALTIMESELECT, DOPO:" + oQuery);
                    String oLabelField = myForm.objects.get(obj).Origin.getLabelField();
                    String oValueField = myForm.objects.get(obj).Origin.getValueField();
                    String oValueFieldType = myForm.objects.get(obj).Origin.getValueFieldType();
                    SelectList myList = new SelectList(myForm.myParams, myForm.mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                    myList.getList();
                    myForm.objects.get(obj).Origin.setSelectList(myList);
                    smartObjRight realObjRights = valutaRightsOggetto(myForm.objects.get(obj), rs);
                    smartObjRight actualObjectRights = joinRights(realObjRights, actualRowRights);
////////                        htmlCode += paintObject(KEYvalue, myForm.objects.get(obj), actualObjectRights);
                    htmlCode += paintObject(KEYvalue, myForm.objects.get(obj), actualObjectRights);
                } else if (myForm.objects.get(obj).C.getType().equalsIgnoreCase("CUSTOMBOX")) {

                    String progressCode = "";
                    progressCode += "<TABLE>";
                    // cerco il valore nella funzione iidicata
                    // System.out.println("\n\nCUSTOMBOX: funzione =" + myForm.objects.get(obj).CG.Value);
                    // es {"label":{"type":"field","field":"abbonamento"},"value":{"type":"field","field":"percAbb"}
                    JSONParser jsonParser = new JSONParser();

                    try {

                        JSONObject jBars;
                        JSONObject riga = null;

                        jBars = (JSONObject) jsonParser.parse(myForm.objects.get(obj).CG.Value);
                        JSONArray array = (JSONArray) jsonParser.parse(jBars.get("bars").toString());

                        for (int r = 0; r < array.size(); r++) {

                            riga = (JSONObject) array.get(r);
                            JSONObject jLabel = (JSONObject) jsonParser.parse(riga.get("label").toString());
                            String LabelField = "";
                            String ValueField = "";
                            String ValueLabel = "";
                            String ValueLabelUM = "";
                            String TextBefore = "";
                            String TextAfter = "";
                            // per adesso suppongo sia un caso "type":"field"
                            try {
                                LabelField = jLabel.get("field").toString();// NOME DELLA TESSERA
                            } catch (Exception e) {
                            }
                            JSONObject jValue = (JSONObject) jsonParser.parse(riga.get("value").toString());
                            // per adesso suppongo sia un caso "type":"field"
                            try {
                                ValueField = jValue.get("field").toString();// PERCENTUALE RIMANENTE
                            } catch (Exception e) {
                            }
                            JSONObject jValueLabel = (JSONObject) jsonParser.parse(riga.get("valueText").toString());
                            // per adesso suppongo sia un caso "type":"field"
                            try {
                                ValueLabel = jValueLabel.get("field").toString();// VALORE RIMANENTE (IN GIORNI O ORE)
                            } catch (Exception e) {
                            }
                            JSONObject jValueLabelUM = (JSONObject) jsonParser.parse(riga.get("valueTextUM").toString());
                            // per adesso suppongo sia un caso "type":"field"
                            try {
                                ValueLabelUM = jValueLabelUM.get("value").toString();//ETICHETTA IN CODA (es. giorni)
                            } catch (Exception e) {
                            }
                            JSONObject jTextBefore = (JSONObject) jsonParser.parse(riga.get("textBefore").toString());
                            // per adesso suppongo sia un caso "type":"field"
                            try {
                                TextBefore = jTextBefore.get("value").toString();//ETICHETTA IN CODA (es. giorni)
                            } catch (Exception e) {
                            }
                            JSONObject jTextAfter = (JSONObject) jsonParser.parse(riga.get("textAfter").toString());
                            // per adesso suppongo sia un caso "type":"field"
                            try {
                                TextAfter = jTextAfter.get("value").toString();//ETICHETTA IN CODA (es. giorni)
                            } catch (Exception e) {
                            }

                            //   System.out.println("\n\nCUSTOMBOX: LabelField =" + LabelField);
                            //  System.out.println("CUSTOMBOX: ValueField =" + ValueField);
                            String myLabel = rs.getString(LabelField);
                            int myPercentage = rs.getInt(ValueField);
                            int myValue = rs.getInt(ValueLabel);
                            String myTextValue = rs.getString(ValueLabel);
                            // System.out.println(myLabel + "=" + myValue);
                            if (myLabel != null && myLabel.length() > 0) {
                                progressCode += "<TR style=\"border-bottom: 1px solid lightGrey !important;\"><TD>";
                                progressCode += myLabel;
                                progressCode += "</TD>";
                                progressCode += "<TD ";
                                if (myPercentage < 30) {
                                    progressCode += "style=\"color:red;\"";
                                } else {
                                    progressCode += "style=\"color:green;\"";
                                }
                                progressCode += ">";
                                progressCode += TextBefore + myTextValue + " " + ValueLabelUM + TextAfter;
                                progressCode += "</TD></TR>";
                            }
                        }

                        progressCode += "</TABLE>";

                    } catch (ParseException ex) {
                        System.out.println("error in line 3884");

                    }
                    htmlCode += progressCode;
                } else {
//------------------------------------------------- 
//                        System.out.println("OGGETTO:"+myForm.objects.get(obj).name);

                    smartObjRight realObjRights = valutaRightsOggetto(myForm.objects.get(obj), rs);
//                    System.out.println("realObjRights:"+realObjRights.toString());
                    smartObjRight actualObjectRights = joinRights(realObjRights, actualRowRights);
//                    System.out.println("actualObjectRights:"+actualObjectRights.toString());
                    htmlCode += paintObject(KEYvalue, myForm.objects.get(obj), actualObjectRights);
//------------------------------------------------- 
                }
                htmlCode += "</div>";
//----------------------                    
//---CHIUDO IL TD-------------------                    
                htmlCode += "</td>";

                //System.out.println("Fine oggetto:" + obj);
            }
        }
//====END=ROW===================        
        return htmlCode;
    }

    public String encodeNormalPatternRow() throws SQLException {

        String htmlCode = "<TD>";
        String pattern = myForm.getHtmlPattern();
        pattern = eliminaCaseIf(pattern, rs);
        htmlCode += populatePattern(pattern, rs, actualRowRights, KEYvalue);
        htmlCode += "</TD>";

        return htmlCode;

    }

    public String encodeAddingPatternRow() throws SQLException {
//System.out.println("sono in encodeAddingPatternRow:");
        String htmlCode = "<TD>";
        String pattern = myForm.getHtmlPattern();

//System.out.println("sono in encodeAddingPatternRow. Il PATTERN è :\n"+pattern);
        pattern = mantieniCaseNewLine(pattern);
//System.out.println("Mantengo solo il pattern per new line. Il PATTERN è :\n"+pattern);

        htmlCode += populateAddingPattern(pattern, actualRowRights, "NEW");
        htmlCode += "</TD>";

        return htmlCode;

    }

    public String populateAddingPattern(String pattern, smartObjRight actualRowRights, String KEYvalue) {
        ArrayList<SelectListLine> XXX = new ArrayList<SelectListLine>();

        System.out.println("\n\n\n***********\nINIZIO popolazione della riga di inserimento. ");

        smartObjRight actualObjectRights = new smartObjRight(31);// il right è impostatu su max permessi
        for (int obj = 0; obj < myForm.objects.size(); obj++) {
//            System.out.println("--populateAddingPattern--OGGETTO:" + myForm.objects.get(obj).name);

            if (!myForm.objects.get(obj).CG.getType().equalsIgnoreCase("FORMBUTTON")) {
                try {
                    String triggeredStyle = feedTriggeredStyle(myForm.objects.get(obj), rs);
                    if (triggeredStyle != null && triggeredStyle.length() > 2) {
                        myForm.objects.get(obj).setTriggeredStyle(triggeredStyle);
                    } else {
                        myForm.objects.get(obj).setTriggeredStyle("");
                    }
                    //------------------------------------ 
                    myForm.objects.get(obj).setValueToWrite("");
//                System.out.println("ottengo i rights. ");
//                smartObjRight realObjRights = valutaRightsOggetto(myForm.objects.get(obj), null);
//                actualObjectRights = joinRights(realObjRights, actualRowRights);
//                System.out.println("--\t\tactualObjectRights:" + actualObjectRights.totalRight);
                } catch (Exception e) {
                    System.out.println("Error retreiving rights. ");
                }

                String objectCode = "";

                if (myForm.objects.get(obj).getAddingRow_enabled() > 0) {
                    try {
                        objectCode = paintObject(KEYvalue, myForm.objects.get(obj), actualObjectRights);
//                        System.out.println("got code ");

                    } catch (Exception e) {
                        System.out.println("--\t\tpopulateAddingPattern Error painting object." + e.toString());
                    }
                } else {
                    objectCode = "";

//                    System.out.println("Obj not in adding row ");
                }

//                System.out.println("Eseguo sostituzioni ");
                try {
                    SelectListLine mybound = new SelectListLine();
                    mybound.setMarker("(-(" + myForm.objects.get(obj).getName() + ")-)");
                    mybound.setLabel("(!(" + myForm.objects.get(obj).getName() + ")!)");
                    mybound.setValue(objectCode);
                    String labl = myForm.objects.get(obj).getLabelHeader();
                    if (labl == null || labl.length() < 1) {
                        labl = myForm.objects.get(obj).getName();
                    }

                    mybound.setSpareValue(labl);
                    XXX.add(mybound);
                    if (pattern != null && myForm.objects.get(obj).getName() != null) {
                        pattern = pattern.replace("@@@" + myForm.objects.get(obj).getName() + "@@@", mybound.getMarker());
                        pattern = pattern.replace("@!@" + myForm.objects.get(obj).getName() + "@!@", mybound.getLabel());
                    }
                } catch (Exception e) {
                    System.out.println("Error in replacing. ");
                }

//            System.out.println("pattern:\n" + pattern);
            }
        }
        // QUESTO PER EVITARE SOSTITUZIONI RICORSIVE
        // DENTRO AL TESTO  GIA' SOSTITUITO
        for (int jj = 0; jj < XXX.size(); jj++) {
            pattern = pattern.replace(XXX.get(jj).getMarker(), XXX.get(jj).getValue());
            pattern = pattern.replace(XXX.get(jj).getLabel(), XXX.get(jj).getSpareValue());
        }
//        System.out.println("FINAL adding pattern:\n" + pattern);
        return pattern;
    }

    public String populatePattern(String pattern, ResultSet rs, smartObjRight actualRowRights, String KEYvalue) {
        // String pattern="";
        ArrayList<SelectListLine> XXX = new ArrayList<SelectListLine>();
        for (int obj = 0; obj < myForm.objects.size(); obj++) {

            if (!myForm.objects.get(obj).CG.getType().equalsIgnoreCase("FORMBUTTON")) {
//****************************
                if (myForm.objects.get(obj).C.getType().equalsIgnoreCase("REALTIMESELECT")) {
//                        System.out.println("RICREO SELECT LIST");
                    String oQuery = myForm.objects.get(obj).Origin.getQuery();
//                System.out.println(" myForm.sendToCRUD:" + myForm.sendToCRUD);

                    //essendo un realtime select il replace va fatto qui sulla row e non con il replace del form
                    oQuery = browserRowArgsReplace(oQuery);
//                System.out.println("REALTIMESELECT, DOPO:" + oQuery);
                    String oLabelField = myForm.objects.get(obj).Origin.getLabelField();
                    String oValueField = myForm.objects.get(obj).Origin.getValueField();
                    String oValueFieldType = myForm.objects.get(obj).Origin.getValueFieldType();
                    SelectList myList = new SelectList(myForm.myParams, myForm.mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                    myList.getList();
                    myForm.objects.get(obj).Origin.setSelectList(myList);

                }

//                System.out.println("--populatePattern--OGGETTO:" + myForm.objects.get(obj).name);
                String triggeredStyle = feedTriggeredStyle(myForm.objects.get(obj), rs);
                if (triggeredStyle != null && triggeredStyle.length() > 2) {
                    myForm.objects.get(obj).setTriggeredStyle(triggeredStyle);
                } else {
                    myForm.objects.get(obj).setTriggeredStyle("");
                }
                //------------------------------------ 
                myForm.objects.get(obj).setValueToWrite(ricavoValoreDaScrivere(rs, obj));
//                System.out.println("--\t\tValueToWrite:" + myForm.objects.get(obj).ValueToWrite);

                smartObjRight realObjRights = valutaRightsOggetto(myForm.objects.get(obj), rs);
//                System.out.println("1.trealObjRights: " + realObjRights.totalRight + " LEVEL: " + realObjRights.level);
//                System.out.println("2.actualRowRights: " + actualRowRights.totalRight + " LEVEL: " + actualRowRights.level);
                smartObjRight actualObjectRights = joinRights(realObjRights, actualRowRights);
//                System.out.println("3.actualObjectRights: " + actualObjectRights.totalRight + " LEVEL: " + actualObjectRights.level);

                String objectCode = "";
                try {
                    objectCode = paintObject(KEYvalue, myForm.objects.get(obj), actualObjectRights);
                } catch (Exception e) {
                    System.out.println("--\t\tError painting object." + e.toString());
                }
                SelectListLine mybound = new SelectListLine();
                mybound.setMarker("(-(" + myForm.objects.get(obj).getName() + ")-)");
                mybound.setLabel("(!(" + myForm.objects.get(obj).getName() + ")!)");
                mybound.setValue(objectCode);
                String labl = myForm.objects.get(obj).getLabelHeader();
                if (labl == null || labl.length() < 1) {
                    labl = myForm.objects.get(obj).getName();
                }

                mybound.setSpareValue(labl);
                XXX.add(mybound);
                if (pattern != null && myForm.objects.get(obj).getName() != null) {
                    pattern = pattern.replace("@@@" + myForm.objects.get(obj).getName() + "@@@", mybound.getMarker());
                    pattern = pattern.replace("@!@" + myForm.objects.get(obj).getName() + "@!@", mybound.getLabel());
                }
//            System.out.println("pattern:\n" + pattern);
            }
        }
        // QUESTO PER EVITARE SOSTITUZIONI RICORSIVE
        // DENTRO AL TESTO  GIA' SOSTITUITO
        for (int jj = 0; jj < XXX.size(); jj++) {
            pattern = pattern.replace(XXX.get(jj).getMarker(), XXX.get(jj).getValue());
            pattern = pattern.replace(XXX.get(jj).getLabel(), XXX.get(jj).getSpareValue());
        }

        pattern = pattern.replace("!!!KEY!!!", KEYvalue);
//        System.out.println("FINAL pattern:\n" + pattern);
        return pattern;
    }

    public String eliminaCaseNewLine(String text) {
        String buono = text;
        //System.out.println("\n>> eliminaCaseNewLine:\n" + text);

        try {
            //System.out.println("eliminaCaseNewLine:");
            String start = "<!--CASE NEWLINE>";
            String end = "<!--ENDCASE NEWLINE>";
            int posizioneInizio = text.indexOf(start);
            int posizioneFine = text.indexOf(end);
            //System.out.println("posizioneInizio:" + posizioneInizio);
            //System.out.println("posizioneFine:" + posizioneFine);

            String prima = text.substring(0, posizioneInizio);
            String dopo = text.substring(posizioneFine + end.length(), text.length());
            //System.out.println("prima:" + prima);
            //System.out.println("dopo:" + dopo);

            buono = prima + dopo;
            //System.out.println("buono:" + buono);
        } catch (Exception e) {
            //System.out.println("\n>>NON RIESCO A ELIMINBARE IL CODICE SPECIFICO DELLA NEW LINE\n");

            buono = text;
        }
        return buono;
    }

    public String mantieniCaseNewLine(String text) {
        String buono = text;
        //System.out.println("\n>> mantieniCaseNewLine:\n" + text);

        try {
            //System.out.println("mantieniCaseNewLine:");
            String start = "<!--CASE NEWLINE>";
            String end = "<!--ENDCASE NEWLINE>";
            int posizioneInizio = text.indexOf(start) + 17;
            int posizioneFine = text.indexOf(end);
            buono = text.substring(posizioneInizio, posizioneFine);
//            System.out.println("mantieniCaseNewLine -->buono:" + buono);
        } catch (Exception e) {
//            System.out.println("\n>>NON RIESCO A ISOLARE IL CODICE SPECIFICO DELLA NEW LINE\n");

            buono = text;
        }
        return buono;
    }

    public String eliminaCaseIf(String Xtext, ResultSet rs) {
        //System.out.println("\n>>RIPULISCO IL PATTERN DALLE PARTRI INUTILI\n");

        // qui analizzo la stringa della riga per cercare parti da eliminare in base al trigger di presenza
        // System.out.println("\n>> eliminaCaseIf:\n" + Xtext);
        String text = Xtext;
        try {
            text = eliminaCaseNewLine(Xtext);
        } catch (Exception e) {
            text = Xtext;
        }
//System.out.println("text:" + text);
        String word = "<!--CASE";
        int flagFound = 1;
        int repeats = 0;
        while (text.length() > 0 && flagFound > 0 && repeats < 300) {
            flagFound = 0;
            repeats++;
// find all occurrences forward
            for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                String segnoTest = "==";
                int inizioAperturaTrigger = i;
                int inizioChiusuraTrigger = 0;
                int fineChiusuraTrigger = 0;
                // cerco la riga completa di commento che contiene il trigger
                int fineAperturaTrigger = text.indexOf("-->", i + 1) + 3;
                String aperturaTrigger = text.substring(inizioAperturaTrigger, fineAperturaTrigger);
                //System.out.println(" TRIGGER:" + aperturaTrigger + "(" + aperturaTrigger.length() + ")");
                //ricavo il primo elemento del trigger; il separatore può essere '=='
                String campo = "";
                String valore = "";
                boolean esitoValutazione = false;
                try {
                    if (aperturaTrigger.contains("==")) {
                        segnoTest = "==";
                        int test = aperturaTrigger.indexOf("==");
                        campo = aperturaTrigger.substring(9, test);
                        campo = campo.trim();
                        valore = aperturaTrigger.substring(test + 2, aperturaTrigger.length() - 3);
                        valore = valore.trim();
                        String vCampo = "";
                        try {
                            vCampo = rs.getString(campo);
                        } catch (SQLException ex) {
                            Logger.getLogger(ShowItForm.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                        //                        System.out.println(" campo:" + campo + " valore:" + valore);
//                        System.out.println("confronto valore di riga:" + rs.getString(campo));
// cerco esito valutazione
                        if (vCampo != null && vCampo.equalsIgnoreCase(valore)) {
                            esitoValutazione = true;
                            //System.out.println("MANTENGO IL TESTO HTML COMPRESO");
                        }

                    }

                    if (aperturaTrigger.contains("!=")) {
                        segnoTest = "!=";
                        int test = aperturaTrigger.indexOf("!=");
                        campo = aperturaTrigger.substring(9, test);
                        campo = campo.trim();
                        valore = aperturaTrigger.substring(test + 2, aperturaTrigger.length() - 3);
                        valore = valore.trim();
                        String vCampo = "";
                        try {
                            vCampo = rs.getString(campo);
                        } catch (SQLException ex) {
                            Logger.getLogger(ShowItForm.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                        //                        System.out.println(" campo:" + campo + " valore:" + valore);
//                        System.out.println("confronto valore di riga:" + rs.getString(campo));
// cerco esito valutazione
                        if (vCampo == null || !vCampo.equalsIgnoreCase(valore)) {
                            esitoValutazione = true;
                            //System.out.println("MANTENGO IL TESTO HTML COMPRESO");
                        }

                    }
                } catch (Exception e) {
                    System.out.println("\nerror2989:" + e.toString());

                }
                if (esitoValutazione == false) {
                    //System.out.println("ELIMINO IL TESTO HTML COMPRESO");
                    if (campo.length() > 0) {
                        // ho trovato un trigger. adesso cerco la fine, valuto e agisco di conseguenza
                        inizioChiusuraTrigger = text.indexOf("<!--ENDCASE " + campo + segnoTest + valore, i + 1);
                        fineChiusuraTrigger = text.indexOf("-->", inizioChiusuraTrigger + 1) + 3;
                    }
                    String testa = "";
                    String coda = "";
                    testa = text.substring(0, inizioAperturaTrigger);
                    coda = text.substring(fineChiusuraTrigger, text.length());
                    text = testa + coda;
                    //System.out.println("\nnew Text=:" + text);
                    flagFound = 1;
                }

            } // prints "4", "13", "22"
        }
        // myNewLine = text;
        return text;
    }

    public String paintMaskRow(ResultSet rs, int lineNumber, String rowType) {

        String htmlCode = "";
////////        ShowItObject.objRight rowRights = valutaRightsRiga(myForm.getDisableRules(), rs);/// analizzo il LOCKER del form per la riga
//////////        System.out.println("paintMaskRow:\nRiga n. " + lineNumber);
//////////        System.out.println("UNISCO DIRITTI FORM:\n" + formRightsRules.totalRight + " LEVEL :" + formRightsRules.level);
//////////        System.out.println("CON DIRITTI RIGA (LOCKERS):" + myForm.getDisableRules() + ":\n" + rowRights.totalRight + " LEVEL :" + rowRights.level);
////////        ShowItObject.objRight actualRowRights = joinRights(formRightsRules, rowRights);
//////////        System.out.println("OTTENGO:\n" + actualRowRights.totalRight + " LEVEL :" + actualRowRights.level);
////////
////////        String pattern = myForm.getHtmlPattern();
////////
////////            if (rowType.equalsIgnoreCase("adding")) {
////////                pattern = getPatternNewRow(pattern);
////////// <editor-fold defaultstate="collapsed" desc="CASO ADDING ROW.">   
////////
////////                String KEYvalue = "NEW";
////////                htmlCode += "<tr id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-NEW-ROW\" class=\"tabAddRow\" >";
////////                htmlCode += paintRowSelector(lineNumber, KEYvalue);
////////////////                String xLineNumber = "" + lineNumber;
////////////////                if (lineNumber == 0) {
////////////////                    xLineNumber = "NEW";
////////////////                }
////////////////                htmlCode += "<td class=\"lineSelector\""
////////////////                        //+ " style=\"padding: 0;\""
////////////////                        + "onClick=\"javascript:rowSelected('" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-SEL')\">"
////////////////                        + "<a id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-SEL\" "
////////////////                        + "style=\""
////////////////                        + " height: inherit;"
////////////////                        + " padding: 1em;"
////////////////                        + "\"><font size='1'><i><b>" + xLineNumber + "</b></i></font> "
////////////////                        + ""// -> HERE
////////////////                        + "</a></td>";
////////                // delete button-------------
////////
////////                if (formRightsRules.canDelete > 0) {
////////                    htmlCode += "<td class=\"lineDeleter\"   >";
////////                    htmlCode += "</td>";
////////                }
////////
////////                htmlCode += " <td>";
////////                htmlCode += "<TABLE style=\"width:" + myForm.getFormWidth() + ";\">";
////////
//////////=========================================
////////                htmlCode += "<tr id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-ROW\" class=\"unselectedRow\" >";
////////                htmlCode += " <td>";
////////////////xxx
////////////////                //---DIRITTI--- DETERMINO I DIRITTI PER OGGETTO IN QUESTA PRECISA RIGA   
////////////////                objRight rowRights = myForm.formRightsRules;
////////////////                if (myForm.getDisableRules() != null && myForm.getDisableRules().length() > 4) {
////////////////                    System.out.println("Trovato locker di riga:" + myForm.getDisableRules());
////////////////                    rowRights = analyzeRightsRuleJson(myForm.getDisableRules(), rs, null);
////////////////                    System.out.println("rowRights.canModify:" + rowRights.canModify);
////////////////                } else {
////////////////                    rowRights = myForm.formRightsRules;
////////////////                }
////////////////
////////////////                
////////
//////////in realtà questa è l'adding row, quindi non so se devo controllare alo stesso modo i diritti di riga (lockers)
////////                htmlCode += populatePattern(pattern, rs, actualRowRights, KEYvalue);
////////                htmlCode += "</td></tr></table>";
////////                // </editor-fold>    
//////////====END=ROW===================        
////////                htmlCode += "</td></tr>";
//////////</editor-fold>  
////////            } else if (rowType.equalsIgnoreCase("total")) {
////////// <editor-fold defaultstate="collapsed" desc="TOTALS ROW."> 
////////                if (formRightsRules.canView > 0) {
////////                    String totLabel = "";
////////                    for (int obj = 0; obj < myForm.objects.size(); obj++) {
////////                        if (myForm.objects.get(obj).Content.getHasSum() > 0) {
////////                            totLabel = "TOT:";
////////                            break;
////////                        }
////////                    }
////////                    htmlCode += "<tr class=\"tabTotalsRow\" >";
////////
////////                    htmlCode += " </tr>";
////////
////////                }
////////
////////                //</editor-fold> 
////////            } else {
////////// <editor-fold defaultstate="collapsed" desc="CASO NORMAL ROW.">   
////////                // System.out.println(" KEYfieldName :" + myForm.getKEYfieldName() + " KEYfieldType :" + myForm.getKEYfieldType());
////////                //  System.out.println("CASO NORMAL ROW" + htmlCode);
////////                String KEYvalue = "";
////////                pattern = eliminaCaseIf(pattern, rs);
////////                if (myForm.getKEYfieldName() != null) {
////////                    if (myForm.getKEYfieldType() != null && myForm.getKEYfieldType().equalsIgnoreCase("INT")) {
////////                        int myKEYvalue;
////////                        myKEYvalue = rs.getInt(myForm.getKEYfieldName());
////////                        KEYvalue = "" + myKEYvalue;
////////                        //System.out.println("Prendo numerico: KEYfieldName :" + myForm.getKEYfieldName() + " KEYfieldType :" + myForm.getKEYfieldType());
////////                    } else {
////////
////////                        KEYvalue = rs.getString(myForm.getKEYfieldName());
////////                        //System.out.println("Prendo stringa: KEYfieldName :" + myForm.getKEYfieldName() + " KEYfieldType :" + myForm.getKEYfieldType());
////////                    }
////////                } else {
////////                    KEYvalue = "" + lineNumber;
//////////                    System.out.println("-------------");
//////////                    System.out.println(myForm.query);
//////////                    System.out.println("paintRow:AUTO INDICIZZAZIONE ATTIVATA !!!");
//////////                    System.out.println("-------------");
////////                }
//////////=======ROW===================        
////////                //stabilisco il colore di background
////////                //       String ValueAssigned = getBGcolor(myForm.getRowBGcolor(), rs);
////////////////                objRight rowRights = myForm.formRightsRules;
////////////////                if (myForm.getDisableRules() != null && myForm.getDisableRules().length() > 4) {
////////////////                    rowRights = analyzeRightsRuleJson(myForm.getDisableRules(), rs, null);
////////////////                } else {
////////////////                    rowRights = myForm.formRightsRules;
////////////////                    rowRights.level = 10;
////////////////                }
////////
////////                htmlCode += "<tr id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-ROW\" class=\"unselectedRow\" >";
////////                htmlCode += paintRowSelector(lineNumber, KEYvalue);
////////////////                // row selector-------------
////////////////                String xLineNumber = "" + lineNumber;
////////////////                if (lineNumber == 0) {
////////////////                    xLineNumber = "NEW";
////////////////                }
////////////////
////////////////                try {
////////////////                    if (myForm.getVisualType() != null && myForm.getVisualType().equalsIgnoreCase("singleRow")) {
////////////////                        xLineNumber = "UPD";
////////////////                    }
////////////////                } catch (Exception e) {
////////////////
////////////////                }
////////////////                htmlCode += "<td class=\"lineSelector\""
////////////////                        //+ " style=\"padding: 0;\""
////////////////                        + "onClick=\"javascript:rowSelected('" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-SEL')\">"
////////////////                        + "<a id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-SEL\" "
////////////////                        + "style=\""
////////////////                        //+ " display: block; \n"
////////////////                        //+ "    min-height: 100%;\n"
////////////////                        //+ "    height: auto !important;\n"
////////////////                        + "    height: inherit;"
////////////////                        + "padding: 1em;"
////////////////                        + "\" "
////////////////                        //+ "onClick=\"javascript:rowSelected('" +myForm.getID()+ "-"+myForm.getCopyTag() + "-" + KEYvalue + "-SEL')\">"
////////////////                        + "><font size='1'><i><b>" + xLineNumber + "</b></i></font> "
////////////////                        + ""// -> HERE
////////////////                        + "</a></td>";
////////                // delete button-------------
////////
////////                if (formRightsRules.canDelete > 0) {
////////                    htmlCode += "<td class=\"lineDeleter\"   >"
////////                            + "<a id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-DEL\"   ";
////////                    if (actualRowRights.canDelete > 0) {
////////                        String jsonArgs = "{";
////////                        jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
////////                        jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
////////                        jsonArgs += "\"objName\":\"\",";
////////                        jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
////////                        jsonArgs += "\"operation\":\"DEL\",";
////////                        jsonArgs += "\"cellType\":\"X\",";
////////                        jsonArgs += "\"filterField\":\"\",";
////////                        jsonArgs += "\"exitRoutine\":\"dummy()\"}";
////////                        htmlCode += " onClick='javascript:smartCellChanged(" + jsonArgs + ")' >";
////////                        htmlCode += " <img  height=\"15\" width=\"15\" align=\"middle\" src='./media/icons/IconDELETE.gif' alt='ELIMINA' ";
////////
////////                        htmlCode += " ></img>";
////////                        htmlCode += "</a> ";
////////                    }
////////
////////                    htmlCode += "</td>";
////////                }
////////
////////                System.out.println("\n--PaintMaskRow_elaboraRigaRS per riga n." + lineNumber);
////////                elaboraRigaRS(rs, actualRowRights);
////////////////                //************************************************************************
////////////////                for (int obj = 0; obj < myForm.objects.size(); obj++) {
////////////////                    myForm.objects.get(obj).setValueToWrite(ricavoValoreDaScrivere(rs, obj));
////////////////                    // in principio la modificabilità dell'oggetto nella riga è la stessa dell'oggetto in generale
////////////////                    myForm.objects.get(obj).Content.setThisRowModifiable(actualRowRights.canModify);
////////////////                }
////////                htmlCode += " <td>";
////////                htmlCode += "<TABLE style=\"width:" + myForm.getFormWidth() + ";\">";
////////
//////////=========================================
//////////                String pattern = myForm.getHtmlPattern();
//////////                System.out.println("\nPATTERN:\n" + pattern);
////////                KEYvalue = "";
////////                if (myForm.getKEYfieldName() != null) {
////////                    if (myForm.getKEYfieldType() != null && myForm.getKEYfieldType().equalsIgnoreCase("INT")) {
////////                        int myKEYvalue;
////////                        myKEYvalue = rs.getInt(myForm.getKEYfieldName());
////////                        KEYvalue = "" + myKEYvalue;
////////                    } else {
////////                        KEYvalue = rs.getString(myForm.getKEYfieldName());
////////                    }
////////                } else {
////////                    System.out.println("\n\npaintMaskRow:NON RIESCO A RICAVARE IL KEY FIELD DELLA TABELLA!!!\n\n\n");
////////                }
////////
////////                htmlCode += "<tr id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-ROW\" class=\"unselectedRow\" >";
////////                htmlCode += " <td>";
//////////                System.out.println("PARTO PER POPULATE PATTERN CON DIRITTI DI RIGA:" + rowRights.totalRight + " LEVEL:" + rowRights.level);
////////
////////                htmlCode += populatePattern(pattern, rs, actualRowRights, KEYvalue);
////////                //=========================================
////////
////////                htmlCode += "</td></tr></table>";
////////                // </editor-fold>    
//////////====END=ROW===================        
////////                htmlCode += "</td></tr>";
//////////</editor-fold> 
////////
////////            }
////////
////////
////////        // htmlCode = eliminaCaseIf(htmlCode, rs);
////////        //System.out.println("\nnew Text=:" + text);
        return htmlCode;
    }

    public String getBGcolor(String rowBGrules, ResultSet rs) {
        String ValueAssigned = "";
        if (rowBGrules == null || rowBGrules.length() < 5) {
            myForm.setActualRowGBcolor("WHITE");
        } else {
            String[] blocks = rowBGrules.split(";");
            List<String> block = Arrays.asList(blocks);
            if (block.size() > 1) {
                for (int bb = 0; bb < block.size(); bb++) {
                    String valore = "";
                    String condizione = "";
                    String[] couples = block.get(bb).split(":");
                    List<String> param = Arrays.asList(couples);
                    if (param.size() > 1) {
                        // condizione = valore
                        condizione = param.get(0);
                        valore = param.get(1);
                        // analizzo la condizione
                        String[] terms = condizione.split("=");
                        List<String> term = Arrays.asList(terms);
                        if (term.size() > 1) {
                            String part1 = term.get(0);
                            String part2 = term.get(1);

                            String field = part1.replace("[", "");
                            field = field.replace("]", "");
                            String rawType = "text";
                            int rawValue = 0;
                            ValueAssigned = "";
                            String rawString = "";
                            // System.out.println("BG COLOR: " + part1 + "=" + part2 + " --->" + valore);

                            for (int gg = 0; gg < myForm.objects.size(); gg++) {

                                if (myForm.objects.get(gg).getName().equalsIgnoreCase(field)) {
                                    rawType = myForm.objects.get(gg).Content.getType();
                                    if (rawType != null && rawType.equalsIgnoreCase("INT")) {
                                        int number = 0;
                                        try {
                                            number = rs.getInt(field);
                                        } catch (SQLException ex) {
                                            System.out.println("error in line 3596");
                                            Logger
                                                    .getLogger(ShowItForm.class
                                                            .getName()).log(Level.SEVERE, null, ex);
                                        }
                                        rawString = "" + number;
                                        // sto analizzando il valore del campo che ha come nome [part1]
                                        // rilevato valore numerico... quindi lo confronterò con
                                        // part 2 trasformato in numero
                                        int number2 = Integer.parseInt(part2);
                                        // eseguo il confronto caso NUMERO
                                        if (number == number2) {
                                            ValueAssigned = valore;
                                        }

                                    } else {
                                        String text = "";
                                        try {
                                            text = rs.getString(field);
                                        } catch (Exception ex) {
                                            text = "";
                                        }
                                        if (text == null || text.equalsIgnoreCase("null")) {
                                            text = "";
                                        }
                                        rawString = "" + text;

                                        // eseguo il confronto caso TEXT
                                        if (rawString.equalsIgnoreCase(part2)) {
                                            ValueAssigned = valore;
                                        }

                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        return ValueAssigned;
    }

    private String paintRowSelector(int lineNumber, String KEYvalue) {
        String htmlCode = "";
//        System.out.println("paintRowSelector:" + myForm.getShowCounter());
        if (myForm.getShowCounter() != null && myForm.getShowCounter().equalsIgnoreCase("FALSE")) {
            htmlCode += "<td></td>";
        } else {
            // row selector-------------
            String xLineNumber = "" + lineNumber;
            if (lineNumber == 0) {
                xLineNumber = "NEW";
            }
            try {
                if (myForm.getVisualType() != null && myForm.getVisualType().equalsIgnoreCase("singleRow")) {
                    xLineNumber = "UPD";
                }
            } catch (Exception e) {
            }
            htmlCode += "<td class=\"lineSelector\""
                    //+ " style=\"padding: 0;\""
                    + "onClick=\"javascript:smartRowSelected('" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-SEL')\">"
                    + "<a id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-SEL\" "
                    + "style=\""
                    + " height: inherit;"
                    + " padding: 1em;"
                    + "\"><font size='1'><i><b>" + xLineNumber + "</b></i></font> "
                    + "</a></td>";
        }

        return htmlCode;
    }

    public String feedTriggeredStyle(smartObject curObj, ResultSet rs) {
        // rs rappresenta la riga del database da visualizzare
        // le colonne di rs sono le colonne della tabella richiesta dall'utente
        String trigs = curObj.getGes_triggers();
        if (trigs == null || trigs.length() < 2) {
            return "";
            // trigs = "[]";
        }

////////        ResultSetMetaData rsmd;
////////        try {
////////            rsmd = rs.getMetaData();
////////            for (int jj = 0; jj < rsmd.getColumnCount(); jj++) {
////////                String name = rsmd.getColumnName(jj + 1);
////////                // System.out.println("COLONNA " + jj + " ->" + name); 
////////            }
////////        } catch (SQLException ex) {
////////            Logger.getLogger(ShowItForm.class
////////                    .getName()).log(Level.SEVERE, null, ex);
////////        }
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        String TRIGGERSarray = null;
        String triggeredStyle = "";
//        String totalTtriggeredStyle = "";

        String AnalyzedObjType = "";
        String rowField = "";
        String test = "";
        String valueType = "";
        String value = "";
        String style = "";

        String tbsJson = "{\"TRIGGERS\":" + trigs + "}";
        //   System.out.println(tbsJson);
//childType childMarker value
        try {
            jsonObject = (JSONObject) jsonParser.parse(tbsJson);
            TRIGGERSarray = jsonObject.get("TRIGGERS").toString();
            if (TRIGGERSarray != null && TRIGGERSarray.length() > 0) {
                JSONParser parser = new JSONParser();
                Object obj;

                obj = parser.parse(TRIGGERSarray);
                JSONArray array = (JSONArray) obj;
                /*
                         [{"MarkerType":"rowField",
                         "Marker":"rifTipoSomm",
                         "test":"==",
                         "valueType":"text",
                         "value":"ORL",
                         "style":"background-color:grey; " } ]  
                 */

                for (Object riga : array) {
//                    bound_Fields myBound = new bound_Fields();
                    jsonObject = (JSONObject) jsonParser.parse(riga.toString());

                    try {
                        AnalyzedObjType = jsonObject.get("AnalyzedObjType").toString();
                    } catch (Exception e) {
                    }
                    try {
                        rowField = jsonObject.get("rowField").toString();
                    } catch (Exception e) {
                    }
                    try {
                        test = jsonObject.get("test").toString();
                    } catch (Exception e) {
                    }
                    try {
                        valueType = jsonObject.get("valueType").toString();
                    } catch (Exception e) {
                    }
                    try {
                        value = jsonObject.get("value").toString();
                    } catch (Exception e) {
                    }
                    try {
                        style = jsonObject.get("style").toString();
//                        System.out.println("\n\n>>>>>>>\nstyle: " + style);
                    } catch (Exception e) {
                    }

                    //    System.out.println("rowField: " + rowField + "  --test: " + test + "   -  value: " + value);
//----------------------------------------------------------------------------------------
                    if (AnalyzedObjType.equalsIgnoreCase("rowField") && rs != null) {
                        int flagVerified = 0;
// cerco il valore del field indicato
                        //String marker = 

                        if (valueType.equalsIgnoreCase("INT")) {
                            int xValue = Integer.parseInt(value);
                            try {
                                int dbVal = rs.getInt(rowField);
                                //String nome = rs.getString("nome");
//                                System.out.println("dbVal: " + dbVal + "  --test: " + test + "   -  value: " + value);
                                if (test.equalsIgnoreCase("==")) {
                                    if (dbVal == xValue) {
                                        flagVerified = 1;
                                    }
                                } else if (test.equalsIgnoreCase(">")) {
                                    if (dbVal > xValue) {
                                        flagVerified = 1;
                                    }
                                } else if (test.equalsIgnoreCase("<")) {
                                    if (dbVal < xValue) {
                                        flagVerified = 1;
                                    }
                                } else if (test.equalsIgnoreCase(">=")) {
                                    if (dbVal >= xValue) {
                                        flagVerified = 1;
                                    }
                                } else if (test.equalsIgnoreCase("<=")) {
                                    if (dbVal <= xValue) {
                                        flagVerified = 1;

                                    }
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(ShowItForm.class
                                        .getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {

                        }

                        if (flagVerified > 0) {
                            triggeredStyle += (style);
                            System.out.println("IMPOSTO TRIGGERED STYLE : " + triggeredStyle);
                        } else {
                            //triggeredStyle = "";
                        }

                    } else if (AnalyzedObjType.equalsIgnoreCase("defaultStyle")) {
                        // prendo style...    [{"AnalyzedObjType":"defaultStyle","style":"background-color:grey; " } ]                    
                        curObj.C.setDefaultStyle(style);
                        triggeredStyle += (style);
//                        System.out.println(" TRIGGERED STYLE : " + triggeredStyle);
                    }
                }
            }
        } catch (ParseException ex) {
            System.out.println("error in line 2082");

        }
        return triggeredStyle;
    }

    private smartObjRight valutaRightsOggetto(smartObject myObject, ResultSet rs) {
        /*
        DIRITTI: ogni regola ha un suo level che ne determina la potenza
        DA FORM:    può essere una impostazione di default o cambiare per un gruppo sppecifico o per un valore TBS
                    A) USERRIGHTS
                    B) LOCKERS
        
        DA OGGETTO  può cambiare in base all'appartenenza dell'utente ad un gruppo o a un valore specifico nella riga
                    C) VISIBLE
                    D) MODIFIABLE   
         */
//myObject.objRights contiene i diritti a priori assegnati all'oggetto in base ai diritti del FORM (A e B) e ai diritti di visibilità C
//        System.out.println("DIRITTI BASE OGGETTO ->" + myObject.getName() + " : " + myObject.objRights.totalRight);
        smartObjRight realRights = myObject.objRights;
        if (myObject.Content.getModifiable() != null && myObject.Content.getModifiable().startsWith("[{")) {
//            System.out.println("DIRITTI COMPLESSI OGGETTO ->" + myObject.getName() + " : " + myObject.Content.getModifiable());

            realRights = analyzeRightsRuleJson(myObject.Content.getModifiable(), rs, null, 400);
        } else {
            realRights = myObject.objRights;
        }

        realRights.evaluateRights();
//        System.out.println("  CHE DIVENTA: " + realRights.totalRight + " LEVEL: " + realRights.level);

        return realRights;
    }

    public smartObjRight valutaRightsRiga(String LOCKERS, ResultSet rs) {
        /*
        DIRITTI: ogni regola ha un suo level che ne determina la potenza
        DA FORM:    può essere una impostazione di default o cambiare per un gruppo sppecifico o per un valore TBS
                    A) USERRIGHTS
                    B) LOCKERS
         */

        smartObjRight ObjRights = this.formRightsRules;
        if (LOCKERS != null && LOCKERS.startsWith("[{")) {
            ObjRights = analyzeRightsRuleJson(LOCKERS, rs, null, 200);
        } else {
            ObjRights = this.formRightsRules;
        }

        return ObjRights;
    }

    public smartObjRight analyzeRightsRuleJson(String rights, ResultSet rs, Connection Conny, int levelBase) {

        smartEntity myEntity = new smartEntity(myForm.myParams, myForm.mySettings);
        myEntity.setFatherKEYvalue(myForm.fatherKEYvalue);
        myEntity.setSendToCRUD(myForm.sendToCRUD);
        smartObjRight rowRights = new smartObjRight(-1);
        rowRights = myEntity.analyzeRightsRuleJson(rights, rs, Conny, levelBase);

        return rowRights;
    }

    public ArrayList<boundFields> elaboraRigaRS(ResultSet rs, smartObjRight actualRowRights) {
//        System.out.println("-----------------------------");
        ArrayList<boundFields> rowValues = new ArrayList<boundFields>();

        for (int obj = 0; obj < myForm.objects.size(); obj++) {
            try {
                myForm.objects.get(obj).setValueToWrite(ricavoValoreDaScrivere(rs, obj));
                boundFields myBF = new boundFields();
//            System.out.println("Name():" + myForm.objects.get(obj).getName() + " Value():" + myForm.objects.get(obj).getValueToWrite());
                myBF.setMarker(myForm.objects.get(obj).getName());
                myBF.setValue(myForm.objects.get(obj).getValueToWrite());
                rowValues.add(myBF);
                myForm.objects.get(obj).Content.setThisRowModifiable(actualRowRights.canModify);//ogni oggetto riceve il valore di default della riga
            } catch (Exception e) {

            }
        }

        return rowValues;
    }

    public String ricavoValoreDaScrivere(ResultSet rs, int obj) {
        String ValoreDaScrivere = "";

        // ValoreDaScrivere = myForm.objects.get(obj).getLabelHeader();
        try {
            String fieldName = myForm.objects.get(obj).name;
            String Type = myForm.objects.get(obj).Content.getType();
            String CGtype = myForm.objects.get(obj).CG.getType();
            String containerType = myForm.objects.get(obj).C.getType();
            ValoreDaScrivere = myForm.objects.get(obj).getLabelHeader();

            if (myForm.objects.get(obj).C.getType().equalsIgnoreCase("PICTURE")) {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = null;
                String picTable = "";
                String picTableQuery = "";
                String picTableKeyField = "";
                String formQueryKeyField = "";
                String formQueryKeyFieldType = "";
                String ric = myForm.objects.get(obj).Origin.getLabelField();
//                System.out.println("ric: " + ric);
                if (ric != null && ric.length() > 0 && ric.startsWith("{")) {
                    try {
                        jsonObject = (JSONObject) jsonParser.parse(ric);
                        try {
                            picTableKeyField = jsonObject.get("picTableKeyField").toString();
                        } catch (Exception e) {
                        }
                        try {
                            picTable = jsonObject.get("picTable").toString();
                        } catch (Exception e) {

                        }
                        try {
                            picTableQuery = jsonObject.get("picTableQuery").toString();
                        } catch (Exception e) {

                        }
                        try {
                            formQueryKeyField = jsonObject.get("formQueryKeyField").toString();
                        } catch (Exception e) {
                        }
                        try {
                            formQueryKeyFieldType = jsonObject.get("formQueryKeyFieldType").toString();
                        } catch (Exception e) {
                        }

////////                        System.out.println("ricavoValoreDaScrivere--->PICTURE ");
////////
////////                        System.out.println("picTable: " + picTable);
////////                        System.out.println("picTableQuery: " + picTableQuery);
////////
////////                        System.out.println("usedKeyField: " + picTableKeyField);
////////                        System.out.println("formQueryKeyField: " + formQueryKeyField);
////////                        System.out.println("usedKeyType: " + formQueryKeyFieldType);
////////                        System.out.println("tabella con immagine: " + picTable);
                        // cerco in rs il valore da usare come chiave;
//                        System.out.println("\nLEGGO SULLA RIGA La KEY da usare nella tabella secondaria: ");
                        try {
                            if (rs != null) {
                                if (formQueryKeyFieldType != null
                                        && formQueryKeyFieldType.equalsIgnoreCase("INT")) {
                                    ValoreDaScrivere = "" + rs.getInt(formQueryKeyField);
                                } else {
                                    ValoreDaScrivere = rs.getString(formQueryKeyField);
                                }
                            }
//                            System.out.println("ValoreDaScrivere: " + ValoreDaScrivere);
                        } catch (Exception e) {

                            System.out.println("Errore in indicizzazione field:" + formQueryKeyField + " ... picture: " + e.toString());
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(requestsManager.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else if (myForm.objects.get(obj).CG.getType().equalsIgnoreCase("FIELD")) {
                if (myForm.objects.get(obj).C.Type.equalsIgnoreCase("ROWPICTURE")) {

                    objectLayout myBox = new objectLayout();
                    myBox.loadBoxLayout(myForm.objects.get(obj).C.getJsClass(), "20", "20");

                    Blob blob = null;
                    BufferedImage image = null;
                    if (rs != null) {
                        try {
                            blob = rs.getBlob(myForm.objects.get(obj).getName());

                            InputStream in = null;
                            if (blob != null) {
                                try {
                                    in = blob.getBinaryStream();
                                    image = ImageIO.read(in);
                                } catch (IOException ex) {
                                    Logger.getLogger(ShowItForm.class
                                            .getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        } catch (SQLException ex) {
                            // Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                    if (image != null) {
                        ValoreDaScrivere = getRowImageHtmlCode(image, myForm.objects.get(obj).labelHeader, myBox);
                    } else {
                        ValoreDaScrivere = "";
                    }
                } else {

                    if (myForm.objects.get(obj).Content.getType() != null
                            && myForm.objects.get(obj).Content.getType().equalsIgnoreCase("INT")) {
                        int number = 0;
                        try {
                            number = rs.getInt(myForm.objects.get(obj).name);
                        } catch (Exception ee) {
                        }
                        ValoreDaScrivere = "" + number;
                    } else {
                        String text = "";
                        try {
                            text = rs.getString(myForm.objects.get(obj).name);
                        } catch (Exception ex) {
                            text = "";
                            // se non trovo il field nel DB, provo con uno dei
                            // parametri mandati con ToBeSent
                            if (myForm.sentFieldList != null && myForm.sentFieldList.size() > 0) {
                                for (int jj = 0; jj < myForm.sentFieldList.size(); jj++) {
                                    if (myForm.sentFieldList.get(jj).getMarker().equalsIgnoreCase(myForm.objects.get(obj).name)) {
                                        text = myForm.sentFieldList.get(jj).getValue();
                                    }
                                }
                            }

                        }
                        if (text == null || text.equalsIgnoreCase("null")) {
                            text = "";
                        }
                        ValoreDaScrivere = "" + text;
                    }
                    //    System.out.println("ValoreDaScrivere:" + ValoreDaScrivere);
                }
            } else if (myForm.objects.get(obj).CG.getType().equalsIgnoreCase("SENTFIELD")) {
                String text = "";
                for (int jj = 0; jj < myForm.sentFieldList.size(); jj++) {
                    if (myForm.sentFieldList.get(jj).getMarker().equalsIgnoreCase(myForm.objects.get(obj).name)) {
                        text = myForm.sentFieldList.get(jj).getValue();
                    }
                }
                ValoreDaScrivere = "" + text;

            } else if (myForm.objects.get(obj).CG.getType().equalsIgnoreCase("CONDITIONAL")) {
                String CGparams = "";
                CGparams = myForm.objects.get(obj).CG.getParams();

//                System.out.println("CONDITIONAL:" + CGparams);
                String ValueAssigned = "";

                String[] blocks = CGparams.split(";");
                List<String> block = Arrays.asList(blocks);
                if (block.size() > 1) {
                    for (int bb = 0; bb < block.size(); bb++) {
                        String valore = "";
                        String condizione = "";
                        String[] couples = block.get(bb).split(" FORMAT ");
                        List<String> param = Arrays.asList(couples);
                        if (param.size() > 1) {
                            // condizione = valore
                            condizione = param.get(0);
                            valore = param.get(1);
                            // analizzo la condizione
                            String[] terms = condizione.split("=");
                            List<String> term = Arrays.asList(terms);
                            String part1 = term.get(0);
                            String part2 = term.get(1);

                            String field = part1.replace("[", "");
                            field = field.replace("]", "");
                            String rawType = "text";
                            int rawValue = 0;
                            String rawString = "";
                            // System.out.println(part1 + "=" + part2 + " --->" + valore);

                            for (int gg = 0; gg < myForm.objects.size(); gg++) {
                                if (myForm.objects.get(gg).getName().equalsIgnoreCase(field)) {
                                    rawType = myForm.objects.get(gg).Content.getType();
                                    if (rawType != null && rawType.equalsIgnoreCase("INT")) {
                                        int number = rs.getInt(field);
                                        rawString = "" + number;
                                        // sto analizzando il valore del campo che ha come nome [part1]
                                        // rilevato valore numerico... quindi lo confronterò con
                                        // part 2 trasformato in numero
                                        int number2 = Integer.parseInt(part2);
                                        // eseguo il confronto caso NUMERO
                                        if (number == number2) {
                                            ValueAssigned = valore;
                                        }

                                    } else {
                                        String text = "";
                                        try {
                                            text = rs.getString(field);
                                        } catch (Exception ex) {
                                            text = "";
                                        }
                                        if (text == null || text.equalsIgnoreCase("null")) {
                                            text = "";
                                        }
                                        rawString = "" + text;

                                        // eseguo il confronto caso TEXT
                                        if (rawString.equalsIgnoreCase(part2)) {
                                            ValueAssigned = valore;
                                        }

                                    }

                                }
                            }

                        } else {
                            // solo valore
                            valore = block.get(bb);
                            //  System.out.println( " X --->" + valore);
                            ValueAssigned = valore;

                        }

                    }
                }

                try {
                    JSONParser jsonParser = new JSONParser();

                    JSONObject jsonObject = (JSONObject) jsonParser.parse(ValueAssigned);

                    try {
                        myForm.objects.get(obj).C.setConditionalLabel((String) jsonObject.get("text"));
                    } catch (Exception ex) {
                        myForm.objects.get(obj).C.setConditionalLabel(null);
                    }

                    try {
                        myForm.objects.get(obj).C.setConditionalBackColor((String) jsonObject.get("back"));
                    } catch (Exception ex) {
                        myForm.objects.get(obj).C.setConditionalBackColor(null);
                    }

                } catch (ParseException pe) {
                    System.out.println("error in line 4056");
                }

            }
        } catch (SQLException ex) {
            System.out.println("SQLException in PAINTROW:");
        }
        return ValoreDaScrivere;
    }

    public smartObjRight joinRights(smartObjRight Arights, smartObjRight Brights) {
        Arights.evaluateRights();
        Brights.evaluateRights();

        int newPerm = 0;
        int newLevel = 0;
        if (Arights.level <= Brights.level) {
            newLevel = Brights.level;
            //prervale permesso oggetto
            /*
              int canView;//1
        int canModify;//2
        int canDelete;//4
        int canCreate;//8
        int canPushButton;//16
        int canEverything;//128
             */
            if (Brights.canView >= 0) {
                newPerm += 1 * Brights.canView;//uso il permesso dell'oggetto
            } else {
                newPerm += 1 * Arights.canView;//prendo il permesso di riga
            }
            if (Brights.canModify >= 0) {
                newPerm += 2 * Brights.canModify;//uso il permesso dell'oggetto
            } else {
                newPerm += 2 * Arights.canModify;//prendo il permesso di riga
            }
            if (Brights.canDelete >= 0) {
                newPerm += 4 * Brights.canDelete;//uso il permesso dell'oggetto
            } else {
                newPerm += 4 * Arights.canDelete;//prendo il permesso di riga
            }
            if (Brights.canCreate >= 0) {
                newPerm += 8 * Brights.canCreate;//uso il permesso dell'oggetto
            } else {
                newPerm += 8 * Arights.canCreate;//prendo il permesso di riga
            }
            if (Brights.canPushButton >= 0) {
                newPerm += 16 * Brights.canPushButton;//uso il permesso dell'oggetto
            } else {
                newPerm += 16 * Arights.canPushButton;//prendo il permesso di riga
            }
            if (Brights.canEverything >= 0) {
                newPerm += 128 * Brights.canEverything;//uso il permesso dell'oggetto
            } else {
                newPerm += 128 * Arights.canEverything;//prendo il permesso di riga
            }

        } else if (Arights.level == Brights.level) {
            // se i livelli sono uguali prevale il più permissivo
            if (Brights.canView >= 0 || Arights.canView >= 0) {
                newPerm += 1 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 1 * 0;//prendo il permesso di riga
            }
            if (Brights.canModify >= 0 || Arights.canModify >= 0) {
                newPerm += 2 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 2 * 0;//prendo il permesso di riga
            }
            if (Brights.canDelete >= 0 || Arights.canDelete >= 0) {
                newPerm += 4 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 4 * 2;//prendo il permesso di riga
            }
            if (Brights.canCreate >= 0 || Arights.canCreate >= 0) {
                newPerm += 8 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 8 * 0;//prendo il permesso di riga
            }
            if (Brights.canPushButton >= 0 || Arights.canPushButton >= 0) {
                newPerm += 16 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 16 * 0;//prendo il permesso di riga
            }
            if (Brights.canEverything >= 0 || Arights.canEverything >= 0) {
                newPerm += 128 * 1;//uso il permesso dell'oggetto
            } else {
                newPerm += 128 * 0;//prendo il permesso di riga
            }
        } else {
            newLevel = Arights.level;
            if (Arights.canView >= 0) {
                newPerm += 1 * Arights.canView;//uso il permesso dell'oggetto
            } else {
                newPerm += 1 * Brights.canView;//prendo il permesso di riga
            }
            if (Arights.canModify >= 0) {
                newPerm += 2 * Arights.canModify;//uso il permesso dell'oggetto
            } else {
                newPerm += 2 * Brights.canModify;//prendo il permesso di riga
            }
            if (Arights.canDelete >= 0) {
                newPerm += 4 * Arights.canDelete;//uso il permesso dell'oggetto
            } else {
                newPerm += 4 * Brights.canDelete;//prendo il permesso di riga
            }
            if (Arights.canCreate >= 0) {
                newPerm += 8 * Arights.canCreate;//uso il permesso dell'oggetto
            } else {
                newPerm += 8 * Brights.canCreate;//prendo il permesso di riga
            }
            if (Arights.canPushButton >= 0) {
                newPerm += 16 * Arights.canPushButton;//uso il permesso dell'oggetto
            } else {
                newPerm += 16 * Brights.canPushButton;//prendo il permesso di riga
            }
            if (Arights.canEverything >= 0) {
                newPerm += 128 * Arights.canEverything;//uso il permesso dell'oggetto
            } else {
                newPerm += 128 * Brights.canEverything;//prendo il permesso di riga
            }
        }

        if (newPerm < -1) {
            newPerm = -1;
        }

        smartObjRight realRights = new smartObjRight(newPerm);
        realRights.level = newLevel;
        realRights.evaluateRights();
//        System.out.println("JOIN A:" + Arights.totalRight + " LEVEL:" + Arights.level);
//        System.out.println("JOIN B:" + Brights.totalRight + " LEVEL:" + Brights.level);
//        System.out.println("RISULTATO::" + realRights.totalRight + " LEVEL:" + realRights.level);

        return realRights;

    }

    public String getRowImageHtmlCode(BufferedImage image, String alternativeString, objectLayout myBox) {
        String usedWidth = myBox.getWidth();
        String usedHeight = myBox.getHeight();
        String usedPicWidth = myBox.getPicWidth();
        String usedPicHeight = myBox.getPicHeight();
        if (usedPicWidth == null || usedPicWidth.length() < 1) {
            usedPicWidth = usedWidth;
        }
        if (usedPicHeight == null || usedPicHeight.length() < 1) {
            usedPicHeight = usedHeight;
        }

        try {
            usedPicWidth = usedPicWidth.replace("px", "");
        } catch (Exception e) {

        }
        try {
            usedPicHeight = usedPicHeight.replace("px", "");
        } catch (Exception e) {

        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String picCode = "";
        String imageString = null;
        int radio = 10;
        if (image != null) {
            try {
                int HH = Integer.parseInt(usedPicHeight);
                if (HH > 20) {
                    radio = HH / 2;
                }
            } catch (Error e) {
            }

            BufferedImage Rimage = makeRoundedCorner(image, radio);
            try {

                ImageIO.write(Rimage, "gif", bos);
                byte[] imageBytes = bos.toByteArray();
                imageString = Base64.getEncoder().encodeToString(imageBytes);
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            imageString = "";
        }
        picCode += "<img src=\"data:image/gif;base64," + imageString + "\" alt=\"" + alternativeString + "\"";
        picCode += "   width=\"" + usedPicWidth + "px\" heigth=\"" + usedPicHeight + "px\" ";
        picCode += " />";
//        System.out.println("picCode:\n" + picCode);
        return picCode;
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(qualityHints);
        g2.setClip(new RoundRectangle2D.Double(0, 0, w, h, w / 2, h / 2));
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return output;
    }

    public String paintObject(smartObject curObj, String KEYvalue) {
        String htmlCode = paintObject(KEYvalue, curObj, new smartObjRight(-1));

        return htmlCode;
    }

    public String paintObject(String KEYvalue, smartObject curObj, smartObjRight objRights) {
        objRights.evaluateRights();
        /*
        
        In linea di proncipio:
        l'oggetto è modificabile se:
        1. il form è modificabile
        2. la riga è modificabile
        3. l'oggetto è modificabile
        
        le prime due le racchiudo in rowRights che sono già stati analizzati per la riga
        devo fondere rowRights con i diritti dell'oggetto specifico
        sia in generale (obj.mpodifiable) , sia in base alla riga (triggers)
         */

//        System.out.println("\n================\npaintObject objRights:" + objRights.totalRight+" - level: "+objRights.level);
        boolean objModifiable = true;
        boolean objVisibile = true;
        boolean objCanPushButton = true;
        String objType = curObj.C.getType();

        if (objRights.canModify <= 0) {
            objModifiable = false;
        } else {
            objModifiable = true;
        }
        if (objRights.canPushButton != 0) {
            objCanPushButton = true;
        } else {
            objCanPushButton = false;
        }
        if (objRights.canView <= 0) {
            objVisibile = false;
        } else {
            objVisibile = true;
        }

//        System.out.println("OGGETTO " + curObj.name + " paintObject objVisible:" + objVisibile + " paintObject objModifiable:" + objModifiable + " paintObject objCanPushButton:" + objCanPushButton);
        String htmlCode = "";

        String ValoreDaScrivere = curObj.getValueToWrite();
//        System.out.println("\n >>paintObject: ValoreDaScrivere:" + ValoreDaScrivere);
        if (KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")) {
            //  System.out.println("OGGETTO IN RIGA NEW ");
            if (curObj.AddingRow_enabled < 1) {
//                System.out.println("NON PRESENTE IN ADDING ROW");
                curObj.Content.setThisRowModifiable(0);
                objVisibile = false;
                objModifiable = false;

            } else {
//                System.out.println("PRESENTE IN ADDING ROW");
                objVisibile = true;
                objModifiable = true;
            }
        }
        if (objType == null) {
            objType = "TEXT";
        }
        // gestisco il fatto che un campo che sarà solo label possa essere in principio compilabile nella newLine
        if (objType != null && objType.equalsIgnoreCase("LABEL") && KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")) {
            objType = "TEXT";
        }
//        System.out.println("_paintObject_objVisibile: " + objVisibile);
//        System.out.println("_paintObject_objModifiable:" + objModifiable);
//        System.out.println("_paintObject_objType:" + objType);

        // se non è una newLine e non hai i diritti di modifica... LABEL!
        if (KEYvalue == null) {
            KEYvalue = "";
        }
        if (!KEYvalue.equalsIgnoreCase("NEW") && !KEYvalue.equalsIgnoreCase("MULTILINELABEL")
                && (objModifiable == false)
                && (objType.equalsIgnoreCase("TEXT")
                || objType.equalsIgnoreCase("PASSWORD"))) {
            // System.out.println("_paintObject_TRASFORMO TEXT IN LABEL");
            objType = "LABEL";
        } else if (!KEYvalue.equalsIgnoreCase("NEW") && KEYvalue.equalsIgnoreCase("TEXTAREA")) {
            objType = "MULTILINELABEL";
        }

// <editor-fold defaultstate="collapsed" desc="LABEL">          
        if (objType.equalsIgnoreCase("LABEL")) {
//==LABEL=========================================================
            // System.out.println("--CASO LABEL ValoreDaScrivere:" + ValoreDaScrivere);
            if (objVisibile == true) {
                // System.out.println("--curObj.Content.getType():" + curObj.Content.getType());
                objectLayout myBox = new objectLayout();
                if (curObj.C.JsClass != null && curObj.C.JsClass.length() > 0) {
                    myBox.loadBoxLayout(curObj.C.JsClass);
                }

                String drp = "";
                if (myBox.isDroppable() == true) {
//                    System.out.println("--curObj:" + curObj.name + " DROPPABLE = " + myBox.isDroppable());
                    drp = "  ondrop=\"drop(event,'" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "')\" ondragover=\"allowDrop(event)\" ";
                } else {
//                    System.out.println("--curObj:" + curObj.name + " DROPPABLE = " + myBox.isDroppable() + "  --->" + curObj.C.JsClass);
                }

                

                htmlCode += "<div "
                        + drp  
                        + " title=\"" + ValoreDaScrivere + "\" style=\"width : " + curObj.C.getWidth() + ";\n"
                        + " display:inline-block;"
                        + " text-overflow: ellipsis;"
                        + " overflow: hidden;"
                        + " white-space: nowrap;";
                //  + "word-wrap: break-word;"

                //   System.out.println("\n\n\n\n--curObj.C.getDefaultStyle():" + curObj.ges_triggers);
                if (curObj.getTriggeredStyle() != null && curObj.getTriggeredStyle().length() > 2) {
                    htmlCode += curObj.getTriggeredStyle();
                } else if (curObj.C.getDefaultStyle() != null) {
                    //     System.out.println(" TROVATO DFAULT STYLE:" + curObj.C.getDefaultStyle());
                    htmlCode += curObj.C.getDefaultStyle();
                }

                htmlCode += "\" ";
                htmlCode += " class= \" "
                        + " cellContent ";
                if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("INT")) {
                    htmlCode += " contentNumber  ";
                    if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0
                            && ValoreDaScrivere.substring(0, 1).equals("-")) {
                        htmlCode += " negativeNumber  ";
                    }
                } else if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("FLOAT")) {
                    htmlCode += " contentNumber  ";
                    if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                        // tronco a 3 cifre dopo il punto
                        int posX = ValoreDaScrivere.length() + 1;
                        if (ValoreDaScrivere.contains(".")) {
                            posX = ValoreDaScrivere.lastIndexOf(".");
                            posX = posX + 2;
                            if (ValoreDaScrivere.length() > posX) {
                                ValoreDaScrivere = ValoreDaScrivere.substring(0, posX + 1);
                            }
                        }
                    }

                } else if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("EURO")) {
                    htmlCode += " contentNumber  ";
                    if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                        // tronco a 3 cifre dopo il punto
                        if (ValoreDaScrivere != null && ValoreDaScrivere.contains(".")) {
                            ValoreDaScrivere = ValoreDaScrivere + "00";
                            int posX = ValoreDaScrivere.lastIndexOf(".");
                            posX = posX + 2;
                            if (ValoreDaScrivere.length() > posX) {
                                ValoreDaScrivere = ValoreDaScrivere.substring(0, posX + 1);
                            }
                        } else {
                            ValoreDaScrivere = ValoreDaScrivere + ".00";
                        }
                        if (ValoreDaScrivere.startsWith("-")) {
                            ValoreDaScrivere = "<font color='red'>" + ValoreDaScrivere + "</font>";
                        }
                        ValoreDaScrivere = "€ " + ValoreDaScrivere;
                    }
                } else if (curObj.Content.getType() != null && (curObj.Content.getType().equalsIgnoreCase("MINtoHOURS")||curObj.Content.getType().equalsIgnoreCase("MINStoHOURS"))) {
                    htmlCode += " contentNumber  ";
                    int no = 0;
                    try {
                        no = Integer.parseInt(ValoreDaScrivere);
                        int hours = (int) (no / 60); //since both are ints, you get an int
                        int minutes = (int) (no % 60);
                        ValoreDaScrivere = hours + "h " + minutes + "m";
                    } catch (Exception e) {
                    }

                }

                htmlCode += "\" ";
                htmlCode += getStyleHtmlCode(curObj, KEYvalue);
                
                
//                String fcs = "";
//                if (myForm.getChildsOnFocus()!= null && myForm.getChildsOnFocus().equalsIgnoreCase("TRUE")) {
//                    fcs = "onClick=\"javascript:smartRowSelected('" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-SEL')\">";
//                }
                
                if ((myForm.getChildsOnFocus()!= null && myForm.getChildsOnFocus().equalsIgnoreCase("TRUE")) 
                        || (myForm.getShowCounter() != null && myForm.getShowCounter().equalsIgnoreCase("FALSE"))) {
                     htmlCode +=  "onClick=\"javascript:smartRowSelected('" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + KEYvalue + "-SEL')\" ";
                    
                    
                } else {
//                    String jsonArgs = "{";
//                    jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
//                    jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
//                    jsonArgs += "\"objName\":\"" + curObj.name + "\",";
//                    jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
//                    jsonArgs += "\"operation\":\"SelectChanges\",";
//                    jsonArgs += "\"cellType\":\"S\",";
//                    jsonArgs += "\"valueType\":\"" + curObj.Content.getType() + "\",";
//                    jsonArgs += "\"filterField\":\"\",";
//                    jsonArgs += "\"routineOnChange\":\"\",";
//                    jsonArgs += "\"exitRoutine\":\"\"}";
                    htmlCode += " onClick=\"javascript:smartObjFocused(event, this)\" ";
                }
                htmlCode += " > ";
                //--------------
                String actualValoreDaScrivere = ValoreDaScrivere;
                String jSonCode = curObj.C.JsClass;
                if (jSonCode != null && jSonCode.length() > 0) {
//            System.out.println("curObj.C.getJsClass():" + jSonCode);
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject;
                    try {
                        jsonObject = (JSONObject) jsonParser.parse(jSonCode);
                        String xGrouped = "";
                        try {
                            xGrouped = jsonObject.get("grouped").toString();
                        } catch (Exception e) {
                            xGrouped = "";
                        }
                        String prevValue = "";
                        boolean changed = true;
                        if (PREVrs != null && PREVrs.size() > 0) {
                            try {
                                prevValue = PREVrs.get(curObj.name).toString();
//                                System.out.println("Valore precedente per " + curObj.name + " = " + prevValue + "; Valore attuale: " + actualValoreDaScrivere);
                                if (prevValue.equalsIgnoreCase(actualValoreDaScrivere)) {
                                    changed = false;
                                }
                            } catch (Exception e) {
//                                System.out.println("Valore precedente not found");
                            }
                        }//                            Logger.getLogger(smartRow.class.getName()).log(Level.SEVERE, null, ex);
                        if (xGrouped != null && xGrouped.equalsIgnoreCase("true") && changed == false) {
                            actualValoreDaScrivere = "";
                        }
                    } catch (ParseException ex) {
//                        Logger.getLogger(smartRow.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                //--------------
                htmlCode += actualValoreDaScrivere;
                htmlCode += "</div>";

            }

            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";

        } else // </editor-fold>     
        // <editor-fold defaultstate="collapsed" desc="BUBBLE">          
        if (objType.equalsIgnoreCase("BUBBLE")) {
//==BUBBLE=========================================================
            // System.out.println("--CASO BUBBLE ValoreDaScrivere:" + ValoreDaScrivere);
            if (objVisibile == true) {
                htmlCode += ValoreDaScrivere;
            }

//            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";
        } else // </editor-fold>     
        //MULTILINELABEL
        // <editor-fold defaultstate="collapsed" desc="MULTILINELABEL">          
        if (objType.equalsIgnoreCase("MULTILINELABEL")) {
//==LABEL=========================================================
            //System.out.println("\n------------->MULTILINELABEL: " + ValoreDaScrivere);
            // System.out.println("--CASO LABEL ValoreDaScrivere:" + ValoreDaScrivere);
            if (objVisibile == true) {
                // System.out.println("--curObj.Content.getType():" + curObj.Content.getType());

                htmlCode += "<div title=\"" + ValoreDaScrivere + "\" "
                        + "style=\"width : " + curObj.C.getWidth() + "; "
                        //+ " display:inline-block;"
                        //+ " text-overflow: ellipsis;"
                        //+ " overflow: auto;"
                        //+ " white-space: nowrap;"
                        + "";
                //  + "word-wrap: break-word;"

                if (curObj.getTriggeredStyle() != null && curObj.getTriggeredStyle().length() > 2) {
                    htmlCode += curObj.getTriggeredStyle();
                } else if (curObj.C.getDefaultStyle() != null) {
                    System.out.println(" TROVATO DFAULT STYLE:" + curObj.C.getDefaultStyle());
                    htmlCode += curObj.C.getDefaultStyle();
                }

                htmlCode += "\" ";
                htmlCode += " class= \" "
                        + " cellContent ";
                if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("INT")) {
                    htmlCode += " contentNumber  ";
                    if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0
                            && ValoreDaScrivere.substring(0, 1).equals("-")) {
                        htmlCode += " negativeNumber  ";
                    }
                }
                if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("FLOAT")) {
                    htmlCode += " contentNumber  ";
                    if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                        // tronco a 3 cifre dopo il punto
                        int posX = ValoreDaScrivere.lastIndexOf(".");
                        posX = posX + 2;
                        if (ValoreDaScrivere.length() > posX) {
                            ValoreDaScrivere = ValoreDaScrivere.substring(0, posX);
                        }

                    }
                }

                htmlCode += "\" ";
                htmlCode += getStyleHtmlCode(curObj, KEYvalue);
                htmlCode += " > ";
                htmlCode += ValoreDaScrivere;
                htmlCode += "</div>";

            }

            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";

        } else // </editor-fold>         
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="SensibleLABEL">             
        if (objType.equalsIgnoreCase("SensibleLABEL")) {
//==SensibleLABEL=========================================================

            if (curObj.C.getConditionalLabel() != null) {
                ValoreDaScrivere = curObj.C.getConditionalLabel();
            }
            // System.out.println("CASO SensibleLABEL ValoreDaScrivere:" + ValoreDaScrivere + " - curObj.Content.getThisRowModifiable():" + curObj.Content.getThisRowModifiable());

            if (objModifiable == true || objCanPushButton == true) {
                //System.out.println("CASO SensibleLABEL ValoreDaScrivere:" + ValoreDaScrivere);
                htmlCode += "<a class=\"SensibleLABEL "
                        //    + "cellContent"
                        + "\" id=\"" + myForm.getID() + "-" + curObj.name + "\" ";
                htmlCode += " style= \""
                        /* + "display:block;"
                          + "text-align:center;"
                         + "vertical-align:middle;"
                         + "height:auto;\n"
                         + "padding:3px 0;"*/
                        + " width:" + curObj.C.getWidth() + "; ";

                String defaultStyle = "";
                String retreivedStyle = "";
                //2.0 default style
                if (curObj.C.getDefaultStyle() != null && curObj.C.getDefaultStyle().length() > 0) {
                    defaultStyle = curObj.C.getDefaultStyle();
                }

                //----STYLE DA TRIGGER-------------------
                //  htmlCode += " style= \"";
                if (curObj.getTriggeredStyle() != null && curObj.getTriggeredStyle().length() > 2) {
                    retreivedStyle += curObj.getTriggeredStyle();
                } else if (curObj.C.getDefaultStyle() != null) {
                    //     System.out.println(" TROVATO DFAULT STYLE:" + curObj.C.getDefaultStyle());
                    retreivedStyle += defaultStyle;
                }
                if (retreivedStyle.contains("display:")) {

                } else {
                    retreivedStyle += " display:block; ";
                }
                if (retreivedStyle.contains("background-color:")) {

                } else {
                    retreivedStyle += " background-color:#B7B7B7; ";
                }

                htmlCode += retreivedStyle;
                htmlCode += "\"";
                //-----------------------
                /*  String params = curObj.CG.getParams() + ":" + curObj.CG.getValue();
                 htmlCode += " onclick=\"javascript:clickedLabel('" +  myForm.getID()+ "-"+myForm.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "','" + params + "')\"";
                 */
                String params = curObj.getActionParams();
                //  System.out.println("\n\nSensLab " + curObj.name + " getActionParams:" + params);

                if (params == null) {
                    params = "{}";
                }

                if (curObj.getActionPerformed() != null
                        && curObj.getActionPerformed().equalsIgnoreCase("OpenSecForm")) {
                    // se devo aprire un form, devo ricavare la getGes_routineOnLoad di quel form
                }
                //  System.out.println("DISEGNO SensibleLabel:" + curObj.getName() + " " + curObj.getActionPerformed() + " " + curObj.getActionParams());
                String toAdd = "";

                toAdd = ",\"action\":\"" + curObj.getActionPerformed() + "\""
                        + ",\"rifForm\":\"" + myForm.getID() + "\""
                        + ",\"copyTag\":\"" + myForm.getCopyTag() + "\""
                        + ",\"fatherForm\":\"" + myForm.getFather() + "\""
                        + ",\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\""
                        + ",\"rifObj\":\"" + curObj.name + "\""
                        + ",\"keyValue\":\"" + KEYvalue + "\"}";
                params = params.replace("}", toAdd);
                htmlCode += " onclick='javascript:clickedObject( " + params + " )'";
                //params.replace("\"","'")

                htmlCode += " ";
                htmlCode += "> ";

                // cerco una immagine in gFEobjects, campo 'picture' dove ID = id di questo oggetto
                objectLayout myBox = new objectLayout();
                myBox.loadBoxLayout(curObj.C.getJsClass());
                if (myBox.getWidth() == null) {
                    myBox.setWidth("20px");
                }
                if (myBox.getHeight() == null) {
                    myBox.setHeight("20px");
                }
                if (myBox.getType() == null) {
                    myBox.setType("");
                }

                if (myBox.getType().equalsIgnoreCase("picOnly")) {

                    DBimage dbimage = new DBimage(myForm.mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myForm.myParams);
                    String imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());

                    String SLcode = "";
                    SLcode += "<table style=\" "
                            + "width:" + myBox.getWidth() + ";height:" + myBox.getHeight() + ";  "
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;"
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;"
                            + "\">" + imageCode + "</td>";
                    SLcode += "</tr></table>";
                    htmlCode += SLcode;
                } else if (myBox.getType().equalsIgnoreCase("STANDARD")) {

                    DBimage dbimage = new DBimage(myForm.mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myForm.myParams);
                    String imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());

                    String SLcode = "";
                    SLcode += "<table style=\" margin-left:auto; \n"
                            + "margin-right:auto; "
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\">" + imageCode + "</td>";
                    SLcode += "</tr><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\" ";
                    SLcode += getStyleHtmlCode(curObj, KEYvalue);
                    SLcode += ">" + ValoreDaScrivere + "</td>";
                    SLcode += "</tr></table>";
                    htmlCode += SLcode;
                } else {
                    htmlCode += ValoreDaScrivere;
                }
                htmlCode += "</a>";
            }

            htmlCode += "<INPUT  class=\"cellContent\"  type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";
        } else // </editor-fold>     
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="smartButton">             
        if (objType.equalsIgnoreCase("smartButton")) {
//==smartButton=========================================================
//eseguiti test per chiamata websocket con pressione pulsante
            if (curObj.C.getConditionalLabel() != null) {
                ValoreDaScrivere = curObj.C.getConditionalLabel();
            }
//            System.out.println("CASO smartButton ValoreDaScrivere:" + ValoreDaScrivere + " - curObj.Content.getThisRowModifiable():" + curObj.Content.getThisRowModifiable());
//            System.out.println("objModifiable:" + objModifiable + " - objCanPushButton:" + objCanPushButton);

            if (objModifiable == true || objCanPushButton == true) {
//                System.out.println("DISEGNO UNO smartButton ValoreDaScrivere:" + ValoreDaScrivere);
                htmlCode += "<a class=\"SensibleLABEL "
                        //    + "cellContent"
                        + "\" id=\"" + myForm.getID() + "-" + curObj.name + "\" ";
                htmlCode += " style= \""
                        /* + "display:block;"
                          + "text-align:center;"
                         + "vertical-align:middle;"
                         + "height:auto;\n"
                         + "padding:3px 0;"*/
                        + "margin: 0px 0px 0px 0px; padding: 0px;"
                        + "vertical-align:middle;"
                        + " width:" + curObj.C.getWidth() + "; ";

                String defaultStyle = "";
                String retreivedStyle = "";
                //2.0 default style
                if (curObj.C.getDefaultStyle() != null && curObj.C.getDefaultStyle().length() > 0) {
                    defaultStyle = curObj.C.getDefaultStyle();
                }

                //----STYLE DA TRIGGER-------------------
                //  htmlCode += " style= \"";
                if (curObj.getTriggeredStyle() != null && curObj.getTriggeredStyle().length() > 2) {
                    retreivedStyle += curObj.getTriggeredStyle();
                } else if (curObj.C.getDefaultStyle() != null) {
//                    System.out.println(" TROVATO DFAULT STYLE:" + curObj.C.getDefaultStyle());
                    retreivedStyle += defaultStyle;
                }
                if (retreivedStyle.contains("display:")) {

                } else {
                    retreivedStyle += " display:block; ";
                }
                if (retreivedStyle.contains("background-color:")) {

                } else {
                    retreivedStyle += " background-color:darkGrey; ";
                }

                htmlCode += retreivedStyle;
                htmlCode += "\"";

                if (objModifiable == true || objCanPushButton == true) {
                    String params = curObj.getActionParams();
                    //  System.out.println("\n\nSensLab " + curObj.name + " getActionParams:" + params);

                    if (params == null) {
                        params = "{}";
                    }

                    String toAdd = ",\"action\":\"" + curObj.getActionPerformed() + "\""// questa info la mando per eventuali esecuzioni del browser in base all'azione (es. clear childs su OpenSecForm)
                            + ",\"rifForm\":\"" + myForm.getID() + "\""
                            + ",\"copyTag\":\"" + myForm.getCopyTag() + "\""
                            //                        + ",\"fatherForm\":\"" + myForm.getFather() + "\""
                            //                        + ",\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\""
                            + ",\"rifObj\":\"" + curObj.name + "\""
                            + ",\"keyValue\":\"" + KEYvalue + "\"}";
                    params = params.replace("}", toAdd);
                    htmlCode += " onclick='javascript:smartButtonClick( " + params + " )'";
                }

                htmlCode += "> ";

                objectLayout myBox = new objectLayout();

                myBox.loadBoxLayout(curObj.C.getJsClass());
                if (myBox.getWidth() == null) {
                    myBox.setWidth("20px");
                }
                if (myBox.getHeight() == null) {
                    myBox.setHeight("20px");
                }
                if (myBox.getType() == null) {
                    myBox.setType("");
                }

                if (myBox.getType().equalsIgnoreCase("picOnly")) {

                    DBimage dbimage = new DBimage(myForm.mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myForm.myParams);
                    String imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());

                    String SLcode = "";
                    SLcode += "<table style=\" "
                            + "width:" + myBox.getWidth() + ";height:" + myBox.getHeight() + ";  "
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;"
                            + "\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;"
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;"
                            + "\">" + imageCode + "</td>";
                    SLcode += "</tr></table>";
                    htmlCode += SLcode;
                } else if (myBox.getType().equalsIgnoreCase("STANDARD")) {

                    DBimage dbimage = new DBimage(myForm.mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myForm.myParams);
                    String imageCode = "";
                    if (!myBox.getPicHeight().equalsIgnoreCase("none")) {
                        imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());
                    }

                    String SLcode = "";
                    SLcode += "<table style=\" margin-left:auto; \n"
                            + "margin-right:auto; "
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\">" + imageCode + "</td>";
                    SLcode += "</tr><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\" ";
                    SLcode += getStyleHtmlCode(curObj, KEYvalue);
                    SLcode += ">" + ValoreDaScrivere + "</td>";
                    SLcode += "</tr></table>";
                    htmlCode += SLcode;
                } else if (myBox.getType().equalsIgnoreCase("DYNAMIC_ICON")) {
//                    System.out.println("DynamicIcon:" + curObj.getName());
//                    System.out.println("query:" + curObj.Origin.query);
//                    System.out.println("labelField:" + curObj.Origin.labelField);
//                    System.out.println("valueField:" + curObj.Origin.valueField);
                    String myQuery = curObj.Origin.query;
                    ArrayList<SelectListLine> rowValues = new ArrayList();
                    try {
                        //--------------------
                        int count = rs.getMetaData().getColumnCount();
                        for (int cc = 1; cc <= count; cc++) {
                            String colName = rs.getMetaData().getColumnLabel(cc);
                            String colType = rs.getMetaData().getColumnTypeName(cc);
//                    System.out.println("Colonna n." + cc + " ) " + colName + " (" + colType + ")");
                            try {
                                String Value = rs.getString(colName);
                                SelectListLine myLine = new SelectListLine();
                                myLine.setLabel(colName);
                                myLine.setValue(Value);
                                rowValues.add(myLine);
                            } catch (Exception e) {
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(smartRow.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                    for (int i = 0; i < rowValues.size(); i++) {
//            System.out.println("RIMPIAZZO " + "@@@" + rowValues.get(i).getLabel() + "@@@   ---> " + rowValues.get(i).getValue());
                        String query = myQuery;
                        String replacer = rowValues.get(i).getValue();
                        if (replacer == null) {
                            replacer = "";
                        }
                        myQuery = query.replace("@@@" + rowValues.get(i).getLabel() + "@@@", replacer);

                    }

                    String oQuery = browserRowArgsReplace(myQuery);
//                    System.out.println("diventa:" + oQuery);
                    //***********************

                    myBox = new objectLayout();
//                    System.out.println("loadBoxLayout:" + curObj.C.getJsClass());
                    myBox.loadBoxLayout(curObj.C.getJsClass(), "15", "15");
//                    myBox.loadBoxLayout(curObj.C.getJsClass() );

                    Blob blob = null;
                    BufferedImage image = null;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    String SQLphrase = oQuery;
                    String imageCode = "";
//                    System.out.println("DYNAMIC_ICON SQLphrase partBquery ---------->" + SQLphrase);
                    Connection conny = new EVOpagerDBconnection(myForm.myParams, myForm.mySettings).ConnLocalDataDB();
                    PreparedStatement picps = null;
                    ResultSet picrs;

                    try {
                        picps = conny.prepareStatement(SQLphrase);
                        picrs = picps.executeQuery();
                        while (picrs.next()) {
                            if (picrs != null) {
                                try {
                                    blob = picrs.getBlob(curObj.Origin.labelField);
                                    InputStream in = null;
                                    if (blob != null) {
                                        try {
                                            in = blob.getBinaryStream();
                                            image = ImageIO.read(in);
                                        } catch (IOException ex) {
                                            Logger.getLogger(ShowItForm.class
                                                    .getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                } catch (SQLException ex) {
                                    // Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }
                            if (image != null) {
                                imageCode = getRowImageHtmlCode(image, curObj.Origin.valueField, myBox);
                            } else {
                                imageCode = "";
                            }
                        }

                        conny.close();
                    } catch (SQLException ex) {

                    }

                    String SLcode = "";
                    SLcode += "<table style=\" "
                            + "margin-left:auto; \n"
                            + "margin-right:auto;\n"
                            + "margin-top:0; \n"
                            + "margin-bottom:0; \n"
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\">" + imageCode + "</td></tr>";
//                    SLcode += "<tr style=\"text-align:center;vertical-align:middle;\">";
//                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\" ";
//                    SLcode += getStyleHtmlCode(curObj, KEYvalue);
//                    SLcode += ">" + ValoreDaScrivere + "</td></tr>";
                    SLcode += "</table>";

                    SLcode = imageCode;

                    htmlCode += SLcode;
                } else {
//
//                    DBimage dbimage = new DBimage(myForm.mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myForm.myParams);
//                    String imageCode = "";
//                    if (!myBox.getPicHeight().equalsIgnoreCase("none")) {
//                        imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());
//                    }

                    String SLcode = "";
                    SLcode += "<table style=\" margin-left:auto; margin-right:auto;  vertical-align:middle;\" >";
//                     SLcode += "<tr style=\"text-align:center;vertical-align:middle;\">";
//                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\">" + imageCode + "</td></tr>";
                    SLcode += "<tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\" ";
                    SLcode += getStyleHtmlCode(curObj, KEYvalue);
                    SLcode += ">" + ValoreDaScrivere + "</td>";
                    SLcode += "</tr></table>";
                    htmlCode += SLcode;

//                    htmlCode += ValoreDaScrivere;
                }
                htmlCode += "</a>";
            }

            htmlCode += "<INPUT  class=\"cellContent\"  type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";
        } else // </editor-fold>     
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="datepicker">             
        if (objType.equalsIgnoreCase("datepicker")) {
//== =========================================================

            if (curObj.C.getConditionalLabel() != null) {
                ValoreDaScrivere = curObj.C.getConditionalLabel();
            }
            // System.out.println("CASO datepicker ValoreDaScrivere:" + ValoreDaScrivere + " - curObj.Content.getThisRowModifiable():" + curObj.Content.getThisRowModifiable());
            if (objModifiable == true) {
                htmlCode += ("<div id=\"calendario\"   >");
                htmlCode += ("<font size=\"3\"> ");
                htmlCode += ("<p><div "
                        + "id=\"PICKER-" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                        + "class=\"datePicker\" font-size:'6'>  </p>");
                htmlCode += ("</font> ");
                htmlCode += ("</div>");
            }

            htmlCode += "<INPUT  class=\"cellContent\"  type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";
        } else // </editor-fold>     
        //----------------------------------------------------------     
        // <editor-fold defaultstate="collapsed" desc="MLSfield">             
        if (objType.equalsIgnoreCase("MLSfield")) {

            if (formRightsRules.canModify > 0) {
                if (KEYvalue.equalsIgnoreCase("NEW") || ((objModifiable == true))) {
                    htmlCode += "<INPUT type=\"BUTTON\" value=\">\" "
                            + "onClick='javascript:openSplash(\"MLSfield\","
                            + "\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\")' >";
                    htmlCode += "  ";
                }
            }
            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    + "value=\"" + ValoreDaScrivere + "\">";
            htmlCode += "<div id=\"LABL-" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" class= \" cellContent \" > ";
            //htmlCode += ValoreDaScrivere;

            SelectList myList = curObj.Origin.getSelectList();
            for (int hh = 0; hh < myList.list.size(); hh++) {
                if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                    htmlCode += myList.list.get(hh).getLabel();
                    break;
                }
            }

            htmlCode += "</div>";
        } else // </editor-fold>                  
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="userBadge">  
        if (objType.equalsIgnoreCase("userBadge")) {
//==userBadge=========================================================
            EVOuser myUser = new EVOuser(myForm.myParams, myForm.mySettings);
            htmlCode += myUser.getBadge(myForm.getID(), myForm.getCopyTag());

        } else // </editor-fold>             
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="VOICECHECK">  
        if (objType.equalsIgnoreCase("VOICECHECK")) {
//==VOICECHECK=========================================================
            //  System.out.println("CASO VOICECHECK ValoreDaScrivere:" + ValoreDaScrivere);
            /*    String oQuery = curObj.Origin.getQuery();
             String oLabelField = curObj.Origin.getLabelField();
             String oValueField = curObj.Origin.getValueField();
             String oValueFieldType = curObj.Origin.getValueFieldType();*/
            SelectList myList = curObj.Origin.getSelectList();

            //myList.getLinkList(server, database, this.getFatherKEYvalue(), this.getFatherKEYtype(), curObj.CG.getParams());
            myList.getLinkList(myForm.myParams, myForm.mySettings, myForm.getFatherKEYvalue(), myForm.getFatherKEYtype(), curObj.CG.getParams());

            htmlCode += curObj.getLabelHeader() + "</BR> ";
            htmlCode += "<TABLE class=\"cruises scrollable\" > ";
            for (int jj = 0; jj < myList.list.size(); jj++) {
                htmlCode += "<tr>";

                ValoreDaScrivere = "" + myList.list.get(jj).getChecked();
                //htmlCode += "<td> "+myList.list.get(jj).getChecked()+ "</td>";
                htmlCode += "<td> ";
                htmlCode += "<INPUT  class=\"CMLNKcontent\" type=\"CHECKBOX\" id=\"" + curObj.ID + "-" + curObj.name + "-" + myList.list.get(jj).getValue()
                        + "\" value=" + myList.list.get(jj).getChecked() + "   ";

                if (myList.list.get(jj).getChecked() > 0) {
                    htmlCode += " checked ";
                }

                if (objModifiable == true) {
                    htmlCode += "onChange=\"javascript:CMLNKchanges('" + curObj.ID + "-" + curObj.name + "-" + myList.list.get(jj).getValue()
                            + "','" + curObj.ID + "' ,"
                            + "'" + myForm.getFatherKEYvalue() + "','" + myList.list.get(jj).getValue() + "' )\"  ";
                } else {
                    htmlCode += " disabled ";
                }

                htmlCode += "/>";

                htmlCode += "</td> ";

                htmlCode += "<td> " + myList.list.get(jj).getLabel() + "</td>";
                //htmlCode += "<td> " + myList.list.get(jj).getValue() + "</td>";
                htmlCode += "</tr>";
            }

            htmlCode += "</TABLE> ";

        } else // </editor-fold>             
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="SPAREVALUE">                          
        if (objType.equalsIgnoreCase("SPAREVALUE")) {
//==SPAREVALUE=========================================================
            //    System.out.println("CASO SPAREVALUE campo da compilare:" + curObj.getName());
            String ValueField = curObj.getName();
            SelectList myList = curObj.Origin.getSelectList();
            myList.getSpareList(myForm.server, myForm.database, myForm.getFatherKEYvalue(), myForm.getFatherKEYtype(), curObj.CG.getParams(), ValueField);
            htmlCode += curObj.getLabelHeader() + "</BR> ";
            htmlCode += "<TABLE class=\"cruises scrollable\" > ";
            for (int jj = 0; jj < myList.list.size(); jj++) {
                htmlCode += "<tr>";

                ValoreDaScrivere = "" + myList.list.get(jj).getSpareValue();
                //htmlCode += "<td> "+myList.list.get(jj).getChecked()+ "</td>";
                htmlCode += "<td > ";
                htmlCode += "<INPUT  class=\"SPAREcontent\" type=\"TEXT\" id=\"" + curObj.ID + "-" + curObj.name + "-" + myList.list.get(jj).getValue()
                        + "\" value=\"" + myList.list.get(jj).getSpareValue() + "\"   ";

                if (formRightsRules.canModify > 0) {
                    htmlCode += "onChange=\"javascript:SPAREchanges('" + curObj.ID + "-" + curObj.name + "-" + myList.list.get(jj).getValue()
                            + "','" + curObj.ID + "' ,"
                            + "'" + myForm.getFatherKEYvalue() + "','" + myList.list.get(jj).getValue() + "' )\"  ";
                } else {
                    htmlCode += " disabled ";
                }

                htmlCode += ">";

                htmlCode += "</td> ";

                htmlCode += "<td> " + myList.list.get(jj).getLabel() + "</td>";
                //htmlCode += "<td> " + myList.list.get(jj).getValue() + "</td>";
                htmlCode += "</tr>";
            }

            htmlCode += "</TABLE> ";

        } else // </editor-fold>             
        //----------------------------------------------------------      
        // <editor-fold defaultstate="collapsed" desc="PICTURE">
        if (objType.equalsIgnoreCase("PICTURE")) {
//==PICTURE=========================================================
            //    System.out.println("CASO PICTURE campo da compilare:" + curObj.getName());
            objectLayout myBox = new objectLayout();

            myBox.loadBoxLayout(curObj.C.getJsClass());
            if (myBox.getWidth() == null) {
                myBox.setWidth("20px");
            }
            if (myBox.getHeight() == null) {
                myBox.setHeight("20px");
            }
            if (myBox.getType() == null) {
                myBox.setType("");
            }
            UUID idOne = null;
            idOne = UUID.randomUUID();
            htmlCode += "<DIV"
                    + " id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-PIC\""
                    // + " draggable=\"true\" "
                    + ">";
            if (objVisibile == true) {

                /*
            ATTENZIONE: qui sto creando il segnaposto per l'immagine
            Se proviene da DB ma non è nella tabella mainTble, il valoire di keyValue deve riguardare non l'ID di riga, ma il valore di un campo
            prescelto, inserito in un valore JSON in curObj.Origin.getLabelField
                 */
                // situazione standard in cui il keYfield è numerico (autoincrement) e l'immagine si trova nella stessa tabella
                String usedKeyField = curObj.Origin.getLabelField();
                String usedKeyValue = KEYvalue;
                String usedKeyType = curObj.Origin.getValueFieldType();
                String usedPicTable = curObj.Origin.getQuery();

//            System.out.println("\n\nPICTURE:situazione basic--->usedKeyField: " + usedKeyField);
//            System.out.println("situazione basic--->usedKeyValue: " + usedKeyValue);
//            System.out.println("situazione basic--->usedKeyType: " + usedKeyType);
                // situazione complessa in cui l'immagine si trova in altra tabella o in questa tabella con un keyField varchar
//            System.out.println("\ncurObj.Origin.getLabelField(): " + curObj.Origin.getLabelField());
                if (curObj.Origin.getLabelField() != null
                        && curObj.Origin.getLabelField().startsWith("{")) {
//                System.out.println("\n\n\n\nATTENZIONE CREO PICTURE DA ALTRA TABELLA. " + curObj.Origin.getLabelField());
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = null;
                    String picTable = curObj.Origin.getQuery();
                    String picTableKeyField = "";
                    String formQueryKeyField = "";
                    String formQueryKeyFieldType = "";
                    String ric = curObj.Origin.getLabelField();
                    try {
                        jsonObject = (JSONObject) jsonParser.parse(ric);
                        try {
                            usedKeyField = jsonObject.get("picTableKeyField").toString();
                        } catch (Exception e) {
                        }
                        try {
                            usedPicTable = jsonObject.get("picTable").toString();
                        } catch (Exception e) {
                            picTable = curObj.Origin.getQuery();
                        }
                        try {
                            formQueryKeyField = jsonObject.get("formQueryKeyField").toString();
                        } catch (Exception e) {
                        }
                        try {
                            usedKeyType = jsonObject.get("formQueryKeyFieldType").toString();
                        } catch (Exception e) {
                        }
//                    System.out.println("usedKeyField: " + usedKeyField);
//                    System.out.println("formQueryKeyField: " + formQueryKeyField);
//                    System.out.println("usedKeyType: " + usedKeyType);
//                    System.out.println("tabella con immagine: " + usedKeyType);
//                    System.out.println("ValoreDaScrivere: " + ValoreDaScrivere);
                        // cerco in rs il valore da usare come chiave;
                        if (formQueryKeyFieldType != null
                                && formQueryKeyFieldType.equalsIgnoreCase("INT")) {
                            usedKeyValue = ValoreDaScrivere;
                        } else {
                            usedKeyValue = ValoreDaScrivere;

                        }

                    } catch (ParseException ex) {
                        Logger.getLogger(requestsManager.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }

                String image = "<img  alt=\"...\" src='portal?rnd=" + idOne + "&target=requestsManager&gp=";
                String params = "\"params\":\"" + encodeURIComponent(myForm.myParams.makePORTALparams()) + "\"";
                String connectors = "\"connectors\":[{\"door\":\"RenderPic\","
                        + "\"event\":\"fromDB\","
                        + "\"table\":\"" + usedPicTable + "\","// es operatori
                        + "\"keyfield\":\"" + usedKeyField + "\","//es operatori.ID
                        + "\"keyValue\":\"" + usedKeyValue + "\","// es 'pippo'
                        + "\"keyType\":\"" + usedKeyType + "\","
                        + "\"picfield\":\"" + curObj.Origin.getValueField() + "\" "//es. media
                        + " }]";
//            System.out.println("OGGETTO PICTURE->" + connectors);

                String utils = "\"responseType\":\"text\"";
                String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";

                image += encodeURIComponent(gp);

                image += "'  width='" + myBox.getWidth() + "px' heigth='" + myBox.getHeight() + "px' >";
//--------------------------------------
                htmlCode += image;
                htmlCode += "</DIV>";
                if (objModifiable == true) {

                    htmlCode += "<form method=\"post\" "
                            + " name=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FRM\""
                            + " id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FRM\""
                            + " action=\"portal\""
                            + " enctype=\"multipart/form-data\">\n"
                            //-----
                            + " <input type=\"hidden\" name=\"target\" value= \"uploadManager\"  />"
                            + " <input type=\"hidden\" name=\"gp\" value= \"" + encodeURIComponent(gp) + "\"  />"
                            //------
                            + " <input type=\"hidden\" name=\"formID\" value= \"" + myForm.getID() + "\"  />"
                            + " <input type=\"hidden\" name=\"formCopyTag\" value= \"" + myForm.getCopyTag() + "\"  />"
                            + " <input type=\"hidden\" name=\"formObjName\" value= \"" + curObj.name + "\"  />"
                            + " <input type=\"hidden\" name=\"formRowKey\" value= \"" + KEYvalue + "\"  />"
                            + " <input type=\"hidden\" name=\"primaryFieldValue\" value= \"" + KEYvalue + "\"  />"
                            + " <input type=\"hidden\" name=\"primaryFieldName\" value= \"" + curObj.Origin.getLabelField() + "\"  />"
                            + " <input type=\"hidden\" name=\"primaryFieldType\" value= \"" + curObj.Origin.getValueFieldType() + "\"  />"
                            + " <input type=\"hidden\" name=\"formName\" value= \"" + curObj.Origin.getQuery() + "\"  />"
                            + " <input type=\"hidden\" name=\"cellName\" value= \"" + curObj.Origin.getValueField() + "\"  />"
                            + " <input type=\"hidden\" name=\"CKcontextID\" value= \"" + myForm.myParams.getCKcontextID() + "\"  />"
                            + " <input type=\"hidden\" name=\"CKtokenID\" value= \"" + myForm.myParams.getCKtokenID() + "\"  />"
                            + " <input type=\"hidden\" name=\"CKuserID\" value= \"" + myForm.myParams.getCKuserID() + "\"  />"
                            + " <input type=\"hidden\" name=\"CKprojectName\" value= \"" + myForm.myParams.getCKprojectName() + "\"  />"
                            + " <input type=\"hidden\" name=\"CKprojectGroup\" value= \"" + myForm.myParams.getCKprojectGroup() + "\"  />"
                            + " <input type=\"hidden\" name=\"width\" value= \"" + myBox.getWidth() + "\"  />"
                            + " <input type=\"hidden\" name=\"height\" value= \"" + myBox.getHeight() + "\"  />"
                            + " <input type=\"file\" "
                            + " id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-IMG\" "
                            + "style=\"display:none;\" "
                            + "name=\"media\"    "
                            + "onchange=\"uploadPicture('" + myForm.getID() + "-" + myForm.getCopyTag() + "','" + curObj.name + "','" + KEYvalue + "')\""
                            + " />\n"
                            + "<label for=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-IMG\" style=\"display:block; background:lightGrey;\">"
                            + "" + curObj.getName() + ""
                            + "</label>"
                            + ""
                            + "        </form>";

                }
            } else {
                htmlCode += "</DIV>";
            }

        } else // </editor-fold>             
        //----------------------------------------------------------    
        // <editor-fold defaultstate="collapsed" desc="StoredDocument">
        if (objType.equalsIgnoreCase("StoredDocument")) {
//==STORED DOCUMENT=========================================================
            //      System.out.println("CASO StoredDocument campo da compilare:" + curObj.getName());
            String xW = "15px";
            String xH = "15px";
            if (curObj.CG.getParams() != null) {
                String dims = curObj.CG.getParams();
                String[] coque = dims.split(";");
                List<String> coques = Arrays.asList(coque);
                if (coques.size() > 1) {
                    xW = coques.get(0).toString();
                    xH = coques.get(1).toString();
                }

            }
            int inlinePicHeightLimit = 20;
            int actualPicHeigth = inlinePicHeightLimit;
            try {
                actualPicHeigth = Integer.parseInt(xH.replace("px", ""));
            } catch (Exception e) {

            }

            String jsonString = ValoreDaScrivere;
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject;
            String filepath = "";
            String FileSysName = "";
            String originalName = "";
            String ext = "";
//            System.out.println("StoredDocument. ValoreDaScrivere:" + ValoreDaScrivere);
            try {
                jsonObject = (JSONObject) jsonParser.parse(jsonString);
                try {
                    FileSysName = jsonObject.get("FileSysName").toString();
                } catch (Exception e) {
                }
                try {
                    originalName = jsonObject.get("originalName").toString();
                } catch (Exception e) {
                }
                try {
                    ext = jsonObject.get("ext").toString();
                } catch (Exception e) {
                }
                ValoreDaScrivere = originalName + "." + ext;
            } catch (ParseException ex) {
                System.out.println("StoredDocument. error::" + ex.toString());

            }

////////            System.out.println("StoredDocument. FileSysName:" + FileSysName);
////////            System.out.println("StoredDocument. originalName:" + originalName);
////////            System.out.println("StoredDocument. ext:" + ext);
            String fatherID = myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue;
            String image = "";

            htmlCode += "<TABLE>";
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0 && !ValoreDaScrivere.equalsIgnoreCase("NULL")) {

                String icon = "media/fileIcon/generic.png";
                if (ValoreDaScrivere.endsWith(".pdf") || ValoreDaScrivere.endsWith(".PDF")) {
                    icon = "media/fileIcon/pdf.png";
                }
                if (ValoreDaScrivere.endsWith(".jpg") || ValoreDaScrivere.endsWith(".JPG")) {
                    icon = "media/fileIcon/pic.png";
                }
                if (ValoreDaScrivere.endsWith(".doc") || ValoreDaScrivere.endsWith(".DOC")) {
                    icon = "media/fileIcon/word.png";
                }
                if (ValoreDaScrivere.endsWith(".dwg") || ValoreDaScrivere.endsWith(".DWG")) {
                    icon = "media/fileIcon/cad.png";
                }
                if (ValoreDaScrivere.endsWith(".xls") || ValoreDaScrivere.endsWith(".XLS")) {
                    icon = "media/fileIcon/excel.png";
                }
                if (ValoreDaScrivere.endsWith(".zip") || ValoreDaScrivere.endsWith(".ZIP")) {
                    icon = "media/fileIcon/zip.png";
                }
                if (ValoreDaScrivere.endsWith(".docx") || ValoreDaScrivere.endsWith(".DOCX")) {
                    icon = "media/fileIcon/word.png";
                }
                if (ValoreDaScrivere.endsWith(".xlsx") || ValoreDaScrivere.endsWith(".XLSX")) {
                    icon = "media/fileIcon/excel.png";
                }
                htmlCode += " <TR><TD>";
                htmlCode += "<DIV"
                        + " id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FILE\""
                        + ">";

                String bomb = "'" + fatherID + "','ServeFile','" + myForm.getID() + "','" + myForm.getCopyTag() + "','" + curObj.name + "','" + KEYvalue + "'";

                image = "<img  alt=\"DOWNLOAD...\"  title=\"DOWNLOAD...\"  "
                        // + "title=\"DOWNLOAD...'" + ValoreDaScrivere + "'\" "
                        + "src='" + icon + "'  width='" + xW + "' heigth='" + xH + "'"
                        //+ "onclick=\"javascript:downloadFile('" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "','OBJname')\""
                        + "onclick=\"javascript:manageFile(" + bomb + ")\" \n"
                        + " >";
                //System.out.println("image:" + image);
                htmlCode += image;
                htmlCode += "</DIV>";
                htmlCode += "</TD>";
                if (actualPicHeigth > inlinePicHeightLimit) {
                    htmlCode += "</TR>";
                }
            }
            if (formRightsRules.canModify > 0) {
                String iconsDiametro = "15px";
                if (actualPicHeigth > inlinePicHeightLimit) {
                    htmlCode += "<TR>";
                }
                htmlCode += "<TD>";
                String bomb = "'" + fatherID + "','askFilenameForm','" + myForm.getID() + "','" + myForm.getCopyTag() + "','" + curObj.name + "','" + KEYvalue + "'";
                htmlCode += "  <a style=\"display:block;\" "
                        + "onclick=\"javascript:manageFile(" + bomb + ")\">\n";
                image = "<img  alt=\"Upload\" title=\"Upload\" src='./media/upload.png'  "
                        + "width='" + iconsDiametro + "' heigth='" + iconsDiametro + "'"
                        + "style=\"display:block;\" "
                        + " >";
                htmlCode += image;
                htmlCode += "</a>\n";

                htmlCode += "</td><td>";
                if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0 && !ValoreDaScrivere.equalsIgnoreCase("NULL")) {

                    htmlCode += "  <a style=\"display:block;\" "
                            + "onclick=\"javascript:manageFile('" + fatherID + "','DeleteFile','" + myForm.getID() + "','" + myForm.getCopyTag() + "','" + curObj.name + "','" + KEYvalue + "')\">\n";
                    image = "<img  alt=\"Delete\" title=\"Delete\" src='./media/del.png'  "
                            + "width='" + iconsDiametro + "' heigth='" + iconsDiametro + "'"
                            + "style=\"display:block;\" "
                            + " >";
                    htmlCode += image;
                    htmlCode += "</a>\n";
                }
                htmlCode += "</TD><TD>";

                htmlCode += "<DIV id=\"secPanel-" + fatherID + "\" class=\"secondPanelClass\" ></DIV>";

                htmlCode += "</TD></TR> ";

            } else {

            }
            htmlCode += " </TABLE>";
        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="SELECT">  
        if (objType.equalsIgnoreCase("SELECT")) {
//==SELECT LIST=========================================================
            //System.out.println("CASO SELECTLIST ValoreDaScrivere:" + ValoreDaScrivere+ " - curObj.Content.getThisRowModifiable():"+curObj.Content.getThisRowModifiable());
//            System.out.println("CASO SELECTLIST objModifiable:" + objModifiable);
            if (KEYvalue.equalsIgnoreCase("NEW")
                    && (curObj.AddingRow_enabled < 1)) {
                htmlCode += "...";
            } else {
                String oValueFieldType = curObj.Origin.getValueFieldType();
                SelectList myList = curObj.Origin.getSelectList();
                if (myForm.type.equalsIgnoreCase("FILTER")) {
                    try {
                        ValoreDaScrivere = myList.list.get(0).getValue();
                    } catch (Exception e) {
                    }

                    // se è un pannello filtro, la selectlist serve solo a memorizzare il valore e passarlo all'Action eventuale
                    // in questo caso la selectlist si comporta come una sensibleLabel e esegue l'actrion
                    if (oValueFieldType == null) {
                        oValueFieldType = "";
                    }
                    htmlCode += "<SELECT id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" ";
                    if (KEYvalue.equalsIgnoreCase("NEW")) {
                        htmlCode += "value=\"\"   ";
                    } else {
                        if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                            if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                                ValoreDaScrivere = "null";
                            } else {
                                htmlCode += "value= " + ValoreDaScrivere + "    ";
                            }
                        } else {
                            htmlCode += "value=\"" + ValoreDaScrivere + "\"   ";
                        }
                    }
                    String params = curObj.getActionParams();
                    //  System.out.println("\n\nSensLab " + curObj.name + " getActionParams:" + params);

                    if (params == null) {
                        params = "{}";
                    }

                    //  System.out.println("DISEGNO SensibleLabel:" + curObj.getName() + " " + curObj.getActionPerformed() + " " + curObj.getActionParams());
                    String toAdd = "";

                    toAdd = ",\"action\":\"" + curObj.getActionPerformed() + "\""
                            + ",\"rifForm\":\"" + myForm.getID() + "\""
                            + ",\"copyTag\":\"" + myForm.getCopyTag() + "\""
                            + ",\"fatherForm\":\"" + myForm.getFather() + "\""
                            + ",\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\""
                            + ",\"rifObj\":\"" + curObj.name + "\""
                            + ",\"keyValue\":\"" + KEYvalue + "\"}";
                    params = params.replace("}", toAdd);
                    htmlCode += " onchange='javascript:clickedObject( " + params + " )'";

                    htmlCode += " class='cellContent' ";
                    htmlCode += getStyleHtmlCode(curObj, KEYvalue);
                    htmlCode += " >";

                } else {

                    if (oValueFieldType == null) {
                        oValueFieldType = "";
                    }
                    htmlCode += "<SELECT id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" ";
                    if (KEYvalue.equalsIgnoreCase("NEW")) {
                        htmlCode += "value=\"\"   ";
                    } else {
                        if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                            if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                                ValoreDaScrivere = "null";
                            } else {
                                htmlCode += "value= " + ValoreDaScrivere + "    ";
                            }
                        } else {
                            htmlCode += "value=\"" + ValoreDaScrivere + "\"   ";
                        }
                    }
                    if (objModifiable == true) {

                        String jsonArgs = "{";
                        jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                        jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                        jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                        jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                        jsonArgs += "\"operation\":\"SelectChanges\",";
                        jsonArgs += "\"cellType\":\"S\",";
                        jsonArgs += "\"valueType\":\"" + curObj.Content.getType() + "\",";
                        jsonArgs += "\"filterField\":\"\",";
                        jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                        jsonArgs += "\"exitRoutine\":\"\"}";
                        if (KEYvalue.equalsIgnoreCase("NEW")) {
//                            System.out.println("jsonArgs PER NEW LINE=" + jsonArgs);
                        } else {
//                            System.out.println("jsonArgs PER NORMAL LINE=" + jsonArgs);
                        }
                        htmlCode += " onChange='javascript:smartCellChanged(" + jsonArgs + ")'  ";

                    } else {
                        htmlCode += " disabled ";
                    }

                    if (!KEYvalue.equalsIgnoreCase("NEW")
                            && objModifiable == false) {
                        htmlCode += " disabled ";
                    }

                    htmlCode += " class='cellContent' ";
                    htmlCode += getStyleHtmlCode(curObj, KEYvalue);
                    htmlCode += " >";
                }

                //******************************
                if (myForm.type.equalsIgnoreCase("FILTER")) {
                } else {
                    htmlCode += "<OPTION   value=null >...select</OPTION>";
                }

                if (myList != null && myList.list != null) {
                    for (int hh = 0; hh < myList.list.size(); hh++) {
                        htmlCode += "<OPTION  ";
                        if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                            htmlCode += "value= " + myList.list.get(hh).getValue() + "    ";
                        } else {
                            htmlCode += "value=\"" + myList.list.get(hh).getValue() + "\"   ";
                        }

                        if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                            htmlCode += " SELECTED ";
                        }
                        htmlCode += ">";
                        htmlCode += myList.list.get(hh).getLabel();
                        htmlCode += "</OPTION>";
                    }
                }

                htmlCode += "</SELECT>";
            }
            //htmlCode += ValoreDaScrivere;
        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="REALTIMESELECT">  
        if (objType.equalsIgnoreCase("REALTIMESELECT")) {
//==SELECT LIST=========================================================
//            System.out.println("CASO REALTIMESELECT ValoreDaScrivere:" + ValoreDaScrivere + " - curObj.Content.getThisRowModifiable():" + curObj.Content.getThisRowModifiable());
//            System.out.println("CASO REALTIMESELECT objModifiable:" + objModifiable);
            if (KEYvalue.equalsIgnoreCase("NEW")
                    && (curObj.AddingRow_enabled < 1)) {
                htmlCode += "...";
            } else {
                String oValueFieldType = curObj.Origin.getValueFieldType();
//                System.out.println("Cpopolo la selectList.");

                SelectList myList = curObj.Origin.getSelectList();

//                System.out.println("trovate righe:" + myList.list.size());
                if (oValueFieldType == null) {
                    oValueFieldType = "";
                }
                htmlCode += "<SELECT id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" ";
                if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                    if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                        ValoreDaScrivere = "null";
                    } else {
                        htmlCode += "value= " + ValoreDaScrivere + "    ";
                    }
                } else {
                    htmlCode += "value=\"" + ValoreDaScrivere + "\"   ";
                }

                if (objModifiable == true) {

                    String jsonArgs = "{";
                    jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                    jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                    jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                    jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                    jsonArgs += "\"operation\":\"SelectChanges\",";
                    jsonArgs += "\"cellType\":\"S\",";
                    jsonArgs += "\"valueType\":\"" + curObj.Content.getType() + "\",";
//                    jsonArgs += "\"filterField\":\"\",";
                    jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
//                    jsonArgs += "\"exitRoutine\":\"\"";
                    jsonArgs += "}";

                    htmlCode += " onChange='javascript:smartCellChanged(" + jsonArgs + ")'  ";

                } else {
                    htmlCode += " disabled ";
                }

                if (!KEYvalue.equalsIgnoreCase("NEW")
                        && objModifiable == false) {
                    htmlCode += " disabled ";
                }

                htmlCode += " class=\"cellContent\" ";
                htmlCode += getStyleHtmlCode(curObj, KEYvalue);
                htmlCode += " >";

                htmlCode += "<OPTION   value=null >...select</OPTION>";

                for (int hh = 0; hh < myList.list.size(); hh++) {
                    htmlCode += "<OPTION  ";
                    if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                        htmlCode += "value= " + myList.list.get(hh).getValue() + "    ";
                    } else {
                        htmlCode += "value=\"" + myList.list.get(hh).getValue() + "\"   ";
                    }

                    if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                        htmlCode += " SELECTED ";
                    }
                    htmlCode += ">";
                    htmlCode += myList.list.get(hh).getLabel();
                    htmlCode += "</OPTION>";
                }
                htmlCode += "</SELECT>";
            }
            //htmlCode += ValoreDaScrivere;
        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="CUSTOMBOX">  
        if (objType.equalsIgnoreCase("CUSTOMBOX")) {
//==CUSTOMBOX=========================================================

            String sourceValue = curObj.CG.Value;
            System.out.println("CUSTOMBOX: sourceValue=" + sourceValue);

        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="MARKER">  
        if (objType.equalsIgnoreCase("MARKER")) {
//==MARKER=========================================================

            String oValueFieldType = curObj.Origin.getValueFieldType();
            SelectList myList = curObj.Origin.getSelectList();

            if (oValueFieldType == null) {
                oValueFieldType = "";
            }
            htmlCode += "<INPUT  type=\"HIDDEN\"  id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    + "value=\"" + ValoreDaScrivere + "\" />";
            if (myList != null && myList.list.size() > 0) {
                //  System.out.println("LA LISTA CONTIENE "+myList.list.size()+" ELEMENI; CERCO " + ValoreDaScrivere);
                for (int hh = 0; hh < myList.list.size(); hh++) {
                    String value = myList.list.get(hh).getValue();
                    if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                        // htmlCode += ValoreDaScrivere;
                        //   System.out.println(" TROVATO " + ValoreDaScrivere + ": LABEL = " + myList.list.get(hh).getLabel());
                        htmlCode += "<SPAN " + getStyleHtmlCode(curObj, KEYvalue) + ">";
                        htmlCode += myList.list.get(hh).getLabel();
                        htmlCode += "</SPAN>";

                        break;
                    }

                }
            }

            //htmlCode += ValoreDaScrivere;
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="RadioButton">  
        if (objType.equalsIgnoreCase("RadioButton")) {
//==RadioButton=========================================================
            //       System.out.println("CASO RADIO BUTTON:" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue);
            String oValueFieldType = curObj.Origin.getValueFieldType();
            SelectList myList = curObj.Origin.getSelectList();

            if (oValueFieldType == null) {
                oValueFieldType = "";
            }

            htmlCode += "<DIV  id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-RADIO\" >";
            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" ";
            if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                    ValoreDaScrivere = "null";
                }
                htmlCode += "value= " + ValoreDaScrivere + "    ";
            } else {
                htmlCode += "value=\"" + ValoreDaScrivere + "\"   ";
            }

            htmlCode += "   >";

            htmlCode += "<FORM "
                    + "id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FORM\" "
                    + "action=\"\">";
            htmlCode += "<TABLE><TR>";
            for (int hh = 0; hh < myList.list.size(); hh++) {
                htmlCode += "<TD> <input "
                        + "type=\"radio\" "
                        + "name=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                        + "id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                        + "value=\"" + myList.list.get(hh).getValue() + "\"";
                if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                    htmlCode += " checked ";
                } else {

                }
                if (objModifiable == true) {
                    String jsonArgs = "{";
                    jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                    jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                    jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                    jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                    jsonArgs += "\"operation\":\"RadioChanges\",";
                    jsonArgs += "\"cellType\":\"R\",";
                    jsonArgs += "\"filterField\":\"\",";
                    jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                    jsonArgs += "\"exitRoutine\":\"\"}";

                    htmlCode += " onChange='javascript:smartCellChanged(" + jsonArgs + ")'  ";
                } else {
                    htmlCode += " disabled ";
                }

                if (myForm.getShowCounter() != null && myForm.getShowCounter().equalsIgnoreCase("FALSE")) {
                    htmlCode += "onmouseup=\"javascript:objSelected('" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "')\"";
                }
                // htmlCode += "onmouseup=\"javascript:objSelected('" +  myForm.getID()+ "-"+myForm.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\"";
                htmlCode += ">"
                        + "" + myList.list.get(hh).getLabel() + "</TD>";

            }
            htmlCode += "</TR></TABLE>";
            htmlCode += "</FORM>";

            htmlCode += "</DIV>";

            //htmlCode += ValoreDaScrivere;
        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="RadioFilter">  
        if (objType.equalsIgnoreCase("RadioFilter")) {
//==RadioFilter=========================================================
            //  System.out.println("CASO RADIO FILTER:" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue);
            String oValueFieldType = curObj.Origin.getValueFieldType();
            SelectList myList = curObj.Origin.getSelectList();
//System.out.println("CASO RADIO FILTER:this.getInfoReceived():"+this.getInfoReceived() );

//costruisco un ghost field per ogni valote ricevuto con info received
// es. se ho incarico:CIRCO06bd0 costruisco
//<INPUT type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-incarico-PANELFILTER\" value=\"CIRCO06bd0\"
            String irec = myForm.getInfoReceived() + ";";

            List<String> filters = Arrays.asList(irec.split(";"));
            //  System.out.println("CASO RADIO FILTER:filters.size():"+filters.size() );
            for (int jj = 0; jj < filters.size(); jj++) {

                List<String> parts = Arrays.asList(filters.get(jj).split(":"));
                if (parts.size() > 1) {
                    String nome = parts.get(0);
                    String valore = parts.get(1);
                    String codeToAdd = "<INPUT type=\"HIDDEN\" "
                            + "id=\"" + myForm.getID() + "-"
                            + myForm.getCopyTag() + "-"
                            + nome + "-PANELFILTER\" "
                            + "value=\"" + valore + "\" ;";

                    //  System.out.println("CASO RADIO FILTER:added ghost "+codeToAdd);
                    htmlCode += codeToAdd;
                }

            }

            //  System.out.println("CASO RADIO FILTER:this.getToBeSent():" + this.getToBeSent());
            if (oValueFieldType == null) {
                oValueFieldType = "";
            }
            String filterName = myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-RADIOFILTER";
            htmlCode += "<DIV  id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-RADIOFILTERDIV\" >";
            //----------CAMPO SEGNAVALORE UTILE PER SHOWCHILDS
            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-PANELFILTER\" ";
            if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                    ValoreDaScrivere = "null";
                }
                htmlCode += "value= " + ValoreDaScrivere + "    ";
            } else {
                htmlCode += "value=\"" + ValoreDaScrivere + "\"   ";
            }

            htmlCode += "   >";

            //------------    
            htmlCode += ("<TABLE border=\"1\"><TR>");
            htmlCode += ("<TD>");
            htmlCode += ("<form> ");

            htmlCode += " <fieldset class='filterRadio' id='" + filterName + "' "
                    + "onchange='javascript:radiofilterChanged(\"" + myForm.getID() + "\",\"" + myForm.getCopyTag() + "\",\"" + curObj.getName() + "\")'>";
            for (int hh = 0; hh < myList.list.size(); hh++) {

                htmlCode += ("<input type=\"radio\" "
                        + "value=\"" + myList.list.get(hh).getValue() + "\" "
                        + "name=\"" + filterName + "\" ");
                if (hh == 0) {
                    htmlCode += ("checked=\"checked\" ");
                }
                htmlCode += ("> "
                        + myList.list.get(hh).getLabel() + ""
                        + "<BR>");

            }

            htmlCode += ("    </fieldset>\n ");

            htmlCode += ("</form> ");

            htmlCode += "</TR></TABLE>";
            htmlCode += "</DIV>";

            //htmlCode += ValoreDaScrivere;
        } else // </editor-fold>             
        //----------------------------------------------------------    
        // <editor-fold defaultstate="collapsed" desc="CHECK">  
        if (objType.equalsIgnoreCase("CHECK") || objType.equalsIgnoreCase("CHECKBOX")) {
//==CHECK=========================================================
            //System.out.println(curObj.name + " _ CASO CHECK ValoreDaScrivere:" + ValoreDaScrivere + "  -objModifiable: " + objModifiable);
            htmlCode += "<INPUT "
                    // + " class=\"cellContent\""
                    + " type='CHECKBOX' id='" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "' "
                    + " value='" + ValoreDaScrivere + "'  class='x' ";
            int valore = 0;
            try {
                valore = Integer.parseInt(ValoreDaScrivere);
            } catch (Exception ex) {
                valore = 0;
            }
            if (valore > 0) {
                htmlCode += " checked='checked' ";
            }
            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"CheckChanges\",";
                jsonArgs += "\"cellType\":\"C\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onChange='javascript:smartCellChanged(" + jsonArgs + ")'  ";
            } else {
                htmlCode += " disabled ";
            }
            // htmlCode += "onmouseup=\"javascript:objSelected('" +  myForm.getID()+ "-"+myForm.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\"";
            htmlCode += ">";
            //htmlCode += valore;
        } else // </editor-fold>             
        //----------------------------------------------------------     
        // <editor-fold defaultstate="collapsed" desc="DATEFILTER">  
        if (objType.equalsIgnoreCase("DATEFILTER")) {
//==DATEFILTER=========================================================
            String filterName = myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-FILTER";

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format0 = new SimpleDateFormat("EEEE dd/MM/yyyy");
            SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd");
            String DBdate = formatDB.format(cal.getTime());
            String curDate = format0.format(cal.getTime());
            htmlCode += "<INPUT id='" + filterName + "' type=\"hidden\" value= \"" + DBdate + "\"  >";

            htmlCode += "<TABLE><TR><TD>OGGI è " + curDate + "</td></tr>"
                    + "<TR><TD>Data selezionata:</td></tr><TR><TD>";
            format0 = new SimpleDateFormat("dd/MM/yyyy");
            curDate = format0.format(cal.getTime());

            htmlCode += " <INPUT id='" + filterName + "block' "
                    + " class=\"datepicker datepickerfilter " + myForm.getID() + "_panelFilter\" font-size:'6' "
                    + " value= \"" + curDate + "\"";

            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"dateFilterChanges\",";
                jsonArgs += "\"cellType\":\"DF\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onChange='javascript:smartPanelFilterChanged(" + jsonArgs + ")'  ";
                htmlCode += " onLoad='javascript:smartPanelFilterChanged(" + jsonArgs + ")'  ";
            } else {
                htmlCode += " readonly ";
            }

            // htmlCode += "onmouseup=\"javascript:objSelected('" +  myForm.getID()+ "-"+myForm.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\"";
            htmlCode += "> </td></tr></table> ";

            //htmlCode += valore;
        } else // </editor-fold>             
        //----------------------------------------------------------     
        // <editor-fold defaultstate="collapsed" desc="YEARFILTER">  
        if (objType.equalsIgnoreCase("YEARFILTER")) {
//==YEARFILTER=========================================================
            String filterName = myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-FILTER";

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format0 = new SimpleDateFormat("yyyy");
            String curDate = format0.format(cal.getTime());
            int questanno = Integer.parseInt(curDate);
            int min = questanno - 30;
            int max = questanno + 2;
            String optionCode = "";
            for (int hh = min; hh < max; hh++) {
                optionCode += "<OPTION  ";
                optionCode += "value= " + hh + "    ";

                if (hh == questanno) {
                    optionCode += " SELECTED ";
                }
                optionCode += ">";
                optionCode += "" + hh;
                optionCode += "</OPTION>";
            }

            htmlCode += "<SELECT id=\"" + filterName + "\" ";

            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"dateFilterChanges\",";
                jsonArgs += "\"cellType\":\"YF\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onChange='javascript:smartPanelFilterChanged(" + jsonArgs + ")'  ";
                htmlCode += " onLoad='javascript:smartPanelFilterChanged(" + jsonArgs + ")'  ";
            } else {
                htmlCode += " readonly ";
            }
            htmlCode += ">";

            htmlCode += optionCode;
            htmlCode += "</SELECT>";
            // htmlCode += "onmouseup=\"javascript:objSelected('" +  myForm.getID()+ "-"+myForm.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\"";

            //htmlCode += valore;
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="MONTHFILTER">  
        if (objType.equalsIgnoreCase("MONTHFILTER")) {
//==MONTHFILTER=========================================================
            String filterName = myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-FILTER";

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format0 = new SimpleDateFormat("MM");
            String curDate = format0.format(cal.getTime());
            int questoMese = Integer.parseInt(curDate);
            String jsonArgs = "";
            jsonArgs = "{";
            jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
            jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
            jsonArgs += "\"objName\":\"" + curObj.name + "\",";
            jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
            jsonArgs += "\"operation\":\"dateFilterChanges\",";
            jsonArgs += "\"cellType\":\"MF\",";
            jsonArgs += "\"filterField\":\"\",";
            jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
            jsonArgs += "\"exitRoutine\":\"\"}";

            htmlCode += ("<form width='" + curObj.C.getWidth() + "'> ");

            htmlCode += " <fieldset class='filterRadio' id='" + filterName + "row' ";
            htmlCode += " onChange='javascript:smartPanelFilterChanged(" + jsonArgs + ")'  ";
            htmlCode += " onLoad='javascript:smartPanelFilterChanged(" + jsonArgs + ")'  ";
            htmlCode += ">";

            htmlCode += "<TABLE width = '" + curObj.C.getWidth() + "'>";
            for (int hh = 1; hh < 13; hh++) {
                String meseName = "";
                meseName = "Gennaio";
                if (hh == 2) {
                    meseName = "Febbraio";
                }
                if (hh == 3) {
                    meseName = "Marzo";
                }
                if (hh == 4) {
                    meseName = "Aprile";
                }
                if (hh == 5) {
                    meseName = "Maggio";
                }
                if (hh == 6) {
                    meseName = "Giugno";
                }
                if (hh == 7) {
                    meseName = "Luglio";
                }
                if (hh == 8) {
                    meseName = "Agosto";
                }
                if (hh == 9) {
                    meseName = "Settembre";
                }
                if (hh == 10) {
                    meseName = "Ottobre";
                }
                if (hh == 11) {
                    meseName = "Novembre";
                }
                if (hh == 12) {
                    meseName = "Dicembre";
                }

                htmlCode += "<TR><TD>";
                htmlCode += "<input type=\"radio\" value=\"" + hh + "\"  name=\"" + filterName + "row\" ";

                if (hh == questoMese) {
                    htmlCode += ("checked=\"checked\" ");
                }
                htmlCode += ("> " + meseName + "");
                htmlCode += "</TD></TR>";

            }

            htmlCode += ("    </fieldset>\n ");
            htmlCode += "<input type=\"hidden\" value=\"" + questoMese + "\"  id=\"" + filterName + "\"  name=\"" + filterName + "\" >";
            htmlCode += ("</form> ");

            //htmlCode += valore;
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="TEXTAREA">  
        if (objType.equalsIgnoreCase(
                "AREA") || objType.equalsIgnoreCase("TEXTAREA")) {
//==TEXTAREA=========================================================
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
            }
            objectLayout myBox = new objectLayout();

            myBox.loadBoxLayout(curObj.C.getJsClass());
            if (myBox.getLines() == null) {
                myBox.setLines("4");
            }
            if (myBox.getColumns() == null) {
                myBox.setColumns("50");
            }

            String XcellType = "T";
            // System.out.println("CASO TEXTAREA ValoreDaScrivere:" + ValoreDaScrivere+ " tipo "+objType);
            System.out.println("CASO TEXTAREA  " + curObj.getName() + " objModifiable:" + objModifiable);
            htmlCode += "<TEXTAREA  class=\"cellContent mydiv \"";
            htmlCode += " rows=\"" + myBox.getLines() + "\" ";
            htmlCode += " cols=\"" + myBox.getColumns() + "\" ";
//            htmlCode += " style=\"font-family:'Verdana', Times, serif;"
//                    + "   font-size: 12px;\" ";
            htmlCode += " type=\"TEXTAREA\" ";
            htmlCode += " id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    //                    + "value=\"" + ValoreDaScrivere + "\"   "
                    + " ";
            htmlCode += getStyleHtmlCode(curObj, KEYvalue);
            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"areaChanges\",";
                jsonArgs += "\"cellType\":\"" + XcellType + "\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onChange='javascript:smartCellChanged(" + jsonArgs + ")'  ";

                //  htmlCode += "onmouseup=\"javascript:objSelected('" +  myForm.getID()+ "-"+myForm.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\" ";
            } else {
                htmlCode += " readonly ";
            }
            if (KEYvalue.equalsIgnoreCase("NEW")
                    && (curObj.AddingRow_enabled < 1)) {
                htmlCode += " readonly disabled ";
            }
            htmlCode += ">";
            htmlCode += ValoreDaScrivere;
            htmlCode += "</TEXTAREA>";
            //htmlCode += valore;
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="SUGGESTEDTEXT">  
        if (objType.equalsIgnoreCase(
                "SUGGESTEDTEXT")) {
//==SUGGESTEDTEXT=========================================================
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
            }
            String XcellType = "T";
            // System.out.println("CASO TEXTBOX ValoreDaScrivere:" + ValoreDaScrivere+ " tipo "+objType);
            htmlCode += "<INPUT  class=\"cellContent suggestedText ";

            htmlCode += "\" ";
            htmlCode += "type=\"TEXT\" ";
            htmlCode += "id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    + "value=\"" + ValoreDaScrivere + "\"   ";

            System.out.println("\n*****\nsmartRow--->SUGGESTEDTEXT " + curObj.getName() + "\n*****\n");

            if (objModifiable == true) {

                String params = curObj.getActionParams();
                //  System.out.println("\n\nSensLab " + curObj.name + " getActionParams:" + params);

                if (params == null) {
                    params = "{}";
                }

                if (curObj.getActionPerformed() != null
                        && curObj.getActionPerformed().equalsIgnoreCase("OpenSecForm")) {
                    // se devo aprire un form, devo ricavare la getGes_routineOnLoad di quel form
                }

                String toAdd = ",\"action\":\"" + curObj.getActionPerformed() + "\""
                        + ",\"rifForm\":\"" + myForm.getID() + "\""
                        + ",\"copyTag\":\"" + myForm.getCopyTag() + "\""
                        + ",\"fatherForm\":\"" + myForm.getFather() + "\""
                        + ",\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\""
                        + ",\"cellType\":\"" + XcellType + "\""
                        + ",\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\""
                        + ",\"operation\":\"textChanges\""
                        + ",\"cellID\":\"" + KEYvalue + "\""
                        + ",\"exitRoutine\":\"\""
                        + ",\"filterField\":\"\""
                        + ",\"objType\":\"suggestedText\""
                        + ",\"rifObj\":\"" + curObj.name + "\""
                        + ",\"KEYvalue\":\"" + KEYvalue + "\"}";
                params = params.replace("}", toAdd);

                String jsonArgs = params;
//                jsonArgs += "{\"formID\":\"" + myForm.getID() + "\",";
//                jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
//                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
//                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
//                jsonArgs += "\"operation\":\"textChanges\",";
//                jsonArgs += "\"cellType\":\"" + XcellType + "\","; 
//                jsonArgs += "\"filterField\":\"\",";
//                //jsonArgs += "\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\",";
//                jsonArgs += "\"action\":\"" + curObj.getActionPerformed() + "\",";
//                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
//                jsonArgs += "\"exitRoutine\":\"\"}";
                htmlCode += " onDblClick='javascript:smartCellChanged(" + jsonArgs + ")'  ";

                //----------------------------------------------
                //---infos per scheda di suggerimento
                jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                jsonArgs += "\"rifObj\":\"" + curObj.name + "\",";
                jsonArgs += "\"keyValue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"getSuggestedList\",";
                jsonArgs += "\"cellType\":\"" + XcellType + "\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"action\":\"" + curObj.getActionPerformed() + "\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";
                htmlCode += "onKeyUp='javascript:suggestSmartList(event," + jsonArgs + ")'  ";

                //----------------------------------------------
            } else {
                htmlCode += " readonly ";
            }

            if (KEYvalue.equalsIgnoreCase("NEW")
                    && (curObj.AddingRow_enabled < 1)) {
                htmlCode += " readonly bgcolor=\"#222222\"";
            }

            htmlCode += "/>";
            htmlCode += "<INPUT  type=\"HIDDEN\"  id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-VAL\" "
                    + "value=\"" + ValoreDaScrivere + "\" />";
        } else // </editor-fold>        
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="GROUPCHECKER">  
        if (objType.equalsIgnoreCase(
                "GROUPCHECKER")) {
//==GROUPCHECKER=========================================================
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
            }
            String XcellType = "T";
//            System.out.println("\n*****\nGROUP CHECKER\n*****\n");

            ArrayList<SelectListLine> rowValues = new ArrayList();
            try {
                //--------------------
                int count = rs.getMetaData().getColumnCount();
                for (int cc = 1; cc <= count; cc++) {
                    String colName = rs.getMetaData().getColumnLabel(cc);
                    String colType = rs.getMetaData().getColumnTypeName(cc);
//                    System.out.println("Colonna n." + cc + " ) " + colName + " (" + colType + ")");
                    try {
                        String Value = rs.getString(colName);
                        SelectListLine myLine = new SelectListLine();
                        myLine.setLabel(colName);
                        myLine.setValue(Value);
                        rowValues.add(myLine);
                    } catch (Exception e) {
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(smartRow.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
//System.out.println("getGroupsList" );
            //-------------------------------------
            String list = getGroupsList(curObj, KEYvalue, rowValues);
            if (list == null || list.length() < 1) {
                list = "N.D.";
            }
//            System.out.println("list length:" +list.length());
//            System.out.println("ValoreDaScrivere=" + ValoreDaScrivere);
//            System.out.println("formRightsRules.canModify=" + formRightsRules.canModify);
//            System.out.println("objModifiable=" + objModifiable);
            htmlCode += "<a  class=\"cellContent\" style=\"display:block;width:100%;height:100%;\"";
            htmlCode += "id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    + "value=\"" + ValoreDaScrivere + "\"   ";

            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                jsonArgs += "\"rifObj\":\"" + curObj.name + "\",";
                jsonArgs += "\"keyValue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"getGroups\",";
                jsonArgs += "\"cellType\":\"" + XcellType + "\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onClick='javascript:smartGroupChecker(" + jsonArgs + ")'  ";

                //     htmlCode += "onKeyUp=\"javascript:suggest(" + jsonArgs + ")'  ";
            } else {
                htmlCode += " ";
            }

            htmlCode += ">";
            htmlCode += list;
            htmlCode += "</a>";
        } else // </editor-fold>        
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="SVGMAP">  
        if (curObj.C.getType()
                .equalsIgnoreCase("SVGMAP")) {
//==SVGMAP=========================================================
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
            }
            htmlCode += "MAPPA-" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue
                    + "";

            /*
         1. apro il DB delle mappe e cerco la mappa in questione con questo ID=keyvalue
         2. carico le caratteristiche della mappa
         3. apro il DB dei path e carico i paths di questa mappa
             */
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="TREEVIEW">  
        if (curObj.C.getType()
                .equalsIgnoreCase("TREEVIEW")) {
//==TREEVIEW=========================================================
////////            SMARTtreeView myTree = new SMARTtreeView(myParams, mySettings);
////////            String elenco = myTree.buildBaseCode(0,  this, curObj);
////////            htmlCode += elenco;
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="RANGER">  
        if (curObj.C.getType()
                .equalsIgnoreCase("RANGER")) {
            //==RANGER=========================================================

            //------------------------------------
            String oQuery = curObj.Origin.getQuery();
//            System.out.println("\n****\nRANGER  " + curObj.getName() + "--_>APPLICO SOSTITUZIONI:" + oQuery);
            String oValueFieldType = curObj.Origin.getValueFieldType();
            SelectList myList = null;
            if (KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")) {

            } else {
                oQuery = browserRowArgsReplace(oQuery);
//                System.out.println("diventa:" + oQuery);
//                System.out.println("getCKprojectName:" + myForm.myParams.getCKprojectName());
                String oLabelField = curObj.Origin.getLabelField();
                String oValueField = curObj.Origin.getValueField();
                myList = new SelectList(myForm.myParams, myForm.mySettings, oQuery, oLabelField, oValueField, oValueFieldType);
                myList.getList();
                curObj.Origin.setSelectList(myList);
            }
            if (oValueFieldType == null) {
                oValueFieldType = "";
            }
            objectLayout myBox = new objectLayout();

            myBox.loadBoxLayout(curObj.C.getJsClass());
            if (myBox.getWidth() == null) {
                myBox.setWidth("100px");
            }
            if (myBox.getHeight() == null) {
                myBox.setHeight("20px");
            }
            if (myBox.getType() == null) {
                myBox.setType("");
            }
            String widthTable = "100px";
            if (curObj.C.Width != null && curObj.C.Width.length() > 0) {
                widthTable = curObj.C.Width;
            }

            htmlCode += "<DIV   style=\"width:" + widthTable + ";max-width:" + widthTable + "; \" "
                    + "id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-RADIO\" >";
            htmlCode += "<INPUT type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" ";
            if (oValueFieldType.equalsIgnoreCase("INT") || oValueFieldType.equalsIgnoreCase("FLOAT")) {
                if (ValoreDaScrivere == null || ValoreDaScrivere == "") {
                    ValoreDaScrivere = "null";
                }
                htmlCode += "value= " + ValoreDaScrivere + " ";
            } else {
                htmlCode += "value=\"" + ValoreDaScrivere + "\" ";
            }
            htmlCode += ">";

            if (myBox.getType().equalsIgnoreCase("picOnly")) {
                //2020-03-04---da rivedere perchè  l'ho preso dal sensibleLabel ma bisogna adattarlo

////////                DBimage dbimage = new DBimage(mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myParams);
////////                String imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());
////////
////////                String SLcode = "";
////////                SLcode += "<table style=\" "
////////                        + "width:" + myBox.getWidth() + ";height:" + myBox.getHeight() + ";  "
////////                        + "margin: 0px 0 0px 0; padding: 1px;"
////////                        + "vertical-align:middle;\" >"
////////                        + "<tr style=\"text-align:center;vertical-align:middle;\">";
////////                SLcode += "<td style=\"text-align:center;"
////////                        + "margin: 0px 0 0px 0; padding: 0px;"
////////                        + "vertical-align:middle;"
////////                        + "\">" + imageCode + "</td>";
////////                SLcode += "</tr></table>";
////////                htmlCode += SLcode;
            } else if (myBox.getType().equalsIgnoreCase("STANDARD")) {
////////                DBimage dbimage = new DBimage(mySettings.getLocalFE_objects(), "ID", curObj.getID(), "picture", myParams);
////////                //DBimage( table,  keyfield,  keyValue,  picfield,  myParams) {
////////
////////                String imageCode = dbimage.getDBimageHtmlCode(myBox.getWidth(), myBox.getHeight());
////////
////////                String SLcode = "";
////////                SLcode += "<table style=\" margin-left:auto; \n"
////////                        + "margin-right:auto; "
////////                        + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
////////                SLcode += "<td style=\"text-align:center;vertical-align:middle;\">" + imageCode + "</td>";
////////                SLcode += "</tr><tr style=\"text-align:center;vertical-align:middle;\">";
////////                SLcode += "<td style=\"text-align:center;vertical-align:middle;\" ";
////////                SLcode += getStyleHtmlCode(curObj, KEYvalue);
////////                SLcode += ">" + ValoreDaScrivere + "</td>";
////////                SLcode += "</tr></table>";
////////                htmlCode += SLcode;
            } else {// textOnly
////////
            }

            htmlCode += "<FORM "
                    + "id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FORM\" "
                    + "action=\"\">";

            htmlCode += "<TABLE style=\"table-layout: fixed; width: 100% ; border-collapse: collapse;   border: 1px solid black;display: block; overflow-x: auto; \"><TR>";
            if (myList != null) {
                for (int hh = 0; hh < myList.list.size(); hh++) {
                    htmlCode += "<TD style=\"width:" + myBox.getWidth() + ";max-width:" + myBox.getWidth() + ";"
                            + "overflow-wrap: break-word;word-wrap: break-word; text-align:center; vertical-align:top; border-collapse: collapse;   border: 1px solid black;\">";
                    htmlCode += " <input "
                            + "type=\"radio\" "
                            + "name=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                            + "id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                            + "value=\"" + myList.list.get(hh).getValue() + "\"";
                    if (myList.list.get(hh).getValue() != null && myList.list.get(hh).getValue().equals(ValoreDaScrivere)) {
                        htmlCode += " checked ";
                    } else {

                    }
                    if (objModifiable == true) {
                        String jsonArgs = "{";
                        jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                        jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                        jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                        jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                        jsonArgs += "\"operation\":\"RadioChanges\",";
                        jsonArgs += "\"cellType\":\"R\",";
                        jsonArgs += "\"filterField\":\"\",";
                        jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                        jsonArgs += "\"exitRoutine\":\"\"}";

                        htmlCode += " onChange='javascript:smartCellChanged(" + jsonArgs + ")'  ";
                    } else {
                        htmlCode += " disabled ";
                    }
                    // htmlCode += "onmouseup=\"javascript:objSelected('" +  myForm.getID()+ "-"+myForm.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\"";
                    htmlCode += ">"
                            + "" + myList.list.get(hh).getLabel() + "</TD>";
                }
            }
            htmlCode += "</TR></TABLE>";
            htmlCode += "</FORM>";

            htmlCode += "</DIV>";
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="ROWPICTURE">  
        if (curObj.C.getType()
                .equalsIgnoreCase("ROWPICTURE")) {
            //==ROWPICTURE=========================================================
            htmlCode += "<DIV"
                    + " id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-PIC\" ";
            // + " draggable=\"true\" "
            if (myForm.getShowCounter() != null && myForm.getShowCounter().equalsIgnoreCase("FALSE")) {
                htmlCode += " onmouseup=\"javascript:objSelected('" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "')\"";
            }

            htmlCode += ">";
            /*
            ATTENZIONE: qui sto creando il segnaposto per l'immagine
            Se proviene da DB ma non è nella tabella mainTble, il valoire di keyValue deve riguardare non l'ID di riga, ma il valore di un campo
            prescelto, inserito in un valore JSON in curObj.Origin.getLabelField
             */
            // situazione standard in cui il keYfield è numerico (autoincrement) e l'immagine si trova nella stessa tabella
            String usedKeyField = curObj.Origin.getLabelField();
            String usedKeyValue = KEYvalue;
            String usedKeyType = curObj.Origin.getValueFieldType();
            String usedPicTable = curObj.Origin.getQuery();

//            System.out.println("\n\nPICTURE:situazione basic--->usedKeyField: " + usedKeyField);
//            System.out.println("situazione basic--->usedKeyValue: " + usedKeyValue);
//            System.out.println("situazione basic--->usedKeyType: " + usedKeyType);
            // situazione complessa in cui l'immagine si trova in altra tabella o in questa tabella con un keyField varchar
//            System.out.println("\ncurObj.Origin.getLabelField(): " + curObj.Origin.getLabelField());
            if (curObj.Origin.getLabelField() != null
                    && curObj.Origin.getLabelField().startsWith("{")) {
//                System.out.println("\n\n\n\nATTENZIONE CREO PICTURE DA ALTRA TABELLA. " + curObj.Origin.getLabelField());
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = null;
                String picTable = curObj.Origin.getQuery();
                String picTableKeyField = "";
                String formQueryKeyField = "";
                String formQueryKeyFieldType = "";
                String ric = curObj.Origin.getLabelField();
                try {
                    jsonObject = (JSONObject) jsonParser.parse(ric);
                    try {
                        usedKeyField = jsonObject.get("picTableKeyField").toString();
                    } catch (Exception e) {
                    }
                    try {
                        usedPicTable = jsonObject.get("picTable").toString();
                    } catch (Exception e) {
                        picTable = curObj.Origin.getQuery();
                    }
                    try {
                        formQueryKeyField = jsonObject.get("formQueryKeyField").toString();
                    } catch (Exception e) {
                    }
                    try {
                        usedKeyType = jsonObject.get("formQueryKeyFieldType").toString();
                    } catch (Exception e) {
                    }

                } catch (ParseException ex) {
                    Logger.getLogger(requestsManager.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

            htmlCode += ValoreDaScrivere;

            htmlCode += "</DIV>";
////////            if (objModifiable == true) {
////////                String params = "\"params\":\"" + encodeURIComponent(myParams.makePORTALparams()) + "\"";
////////                String connectors = "\"connectors\":[{\"door\":\"RenderPic\","
////////                        + "\"event\":\"fromDB\","
////////                        + "\"table\":\"" + usedPicTable + "\","// es operatori
////////                        + "\"keyfield\":\"" + usedKeyField + "\","//es operatori.ID
////////                        + "\"keyValue\":\"" + usedKeyValue + "\","// es 'pippo'
////////                        + "\"keyType\":\"" + usedKeyType + "\","
////////                        + "\"picfield\":\"" + curObj.Origin.getValueField() + "\" "//es. media
////////                        + " }]";
////////                System.out.println("OGGETTO PICTURE->" + connectors);
////////
////////                String utils = "\"responseType\":\"text\"";
////////                String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
////////                htmlCode += "<form method=\"post\" "
////////                        + " name=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FRM\""
////////                        + " id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-FRM\""
////////                        + " action=\"portal\""
////////                        + " enctype=\"multipart/form-data\">\n"
////////                        //-----
////////                        + " <input type=\"hidden\" name=\"target\" value= \"uploadManager\"  />"
////////                        + " <input type=\"hidden\" name=\"gp\" value= \"" + encodeURIComponent(gp) + "\"  />"
////////                        //------
////////                        + " <input type=\"hidden\" name=\"formID\" value= \"" + myForm.getID() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"formCopyTag\" value= \"" + myForm.getCopyTag() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"formObjName\" value= \"" + curObj.name + "\"  />"
////////                        + " <input type=\"hidden\" name=\"formRowKey\" value= \"" + KEYvalue + "\"  />"
////////                        + " <input type=\"hidden\" name=\"primaryFieldValue\" value= \"" + KEYvalue + "\"  />"
////////                        + " <input type=\"hidden\" name=\"primaryFieldName\" value= \"" + curObj.Origin.getLabelField() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"primaryFieldType\" value= \"" + curObj.Origin.getValueFieldType() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"formName\" value= \"" + curObj.Origin.getQuery() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"cellName\" value= \"" + curObj.Origin.getValueField() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"CKcontextID\" value= \"" + myParams.getCKcontextID() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"CKtokenID\" value= \"" + myParams.getCKtokenID() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"CKuserID\" value= \"" + myParams.getCKuserID() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"CKprojectName\" value= \"" + myParams.getCKprojectName() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"CKprojectGroup\" value= \"" + myParams.getCKprojectGroup() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"width\" value= \"" + myBox.getWidth() + "\"  />"
////////                        + " <input type=\"hidden\" name=\"height\" value= \"" + myBox.getHeight() + "\"  />"
////////                        + " <input type=\"file\" "
////////                        + " id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-IMG\" "
////////                        + "style=\"display:none;\" "
////////                        + "name=\"media\"    "
////////                        + "onchange=\"uploadPicture('" + myForm.getID() + "-" + myForm.getCopyTag() + "','" + curObj.name + "','" + KEYvalue + "')\""
////////                        + " />\n"
////////                        + "<label for=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-IMG\" style=\"display:block; background:lightGrey;\">"
////////                        + "" + curObj.getName() + ""
////////                        + "</label>"
////////                        + ""
////////                        + "        </form>";
////////
////////            }

        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="RICHTEXT">  
        if (curObj.C.getType()
                .equalsIgnoreCase("RICHTEXT")) {
            //==RICHTEXT=========================================================
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
            }
            objectLayout myBox = new objectLayout();

            myBox.loadBoxLayout(curObj.C.getJsClass());
            if (myBox.getLines() == null) {
                myBox.setLines("4");
            }
            if (myBox.getColumns() == null) {
                myBox.setColumns("50");
            }

            String XcellType = "T";
            // System.out.println("CASO TEXTAREA ValoreDaScrivere:" + ValoreDaScrivere+ " tipo "+objType);
            System.out.println("CASO RICHTEXT  " + curObj.getName() + " objModifiable:" + objModifiable);
            htmlCode += "<TEXTAREA  class=\"richTextClass cellContent mydiv \"";
            htmlCode += " rows=\"" + myBox.getLines() + "\" ";
            htmlCode += " cols=\"" + myBox.getColumns() + "\" ";
//            htmlCode += " style=\"font-family:'Verdana', Times, serif;"
//                    + "   font-size: 12px;\" ";
            htmlCode += " type=\"RTAREA\" ";
            String objectID = myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue;
            htmlCode += " style=\"display:block; border: 1px solid #000;\" ";
            htmlCode += " id=\"" + objectID + "\" "
                    //                    + "value=\"" + ValoreDaScrivere + "\"   "
                    + " ";
            htmlCode += getStyleHtmlCode(curObj, KEYvalue);
            objModifiable = true;
            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"areaChanges\",";
                jsonArgs += "\"cellType\":\"" + XcellType + "\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onChange='javascript:smartCellChanged(" + jsonArgs + ")'  ";
//                htmlCode += "onfocusin='javascript:rtfEvent(\"IN\",\"" + objectID + "\");' ";
//                htmlCode += "onfocusout='javascript:rtfEvent(\"OUT\",\"" + objectID + "\");' ";

                //  htmlCode += "onmouseup=\"javascript:objSelected('" +  myForm.getID()+ "-"+myForm.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\" ";
            } else {
                //  htmlCode += " readonly ";
            }
            if (KEYvalue.equalsIgnoreCase("NEW")
                    && (curObj.AddingRow_enabled < 1)) {
                htmlCode += " readonly disabled ";
            }
            htmlCode += ">";
            htmlCode += ValoreDaScrivere;
            htmlCode += "</TEXTAREA>";
            //htmlCode += valore;

        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="BORDERBOXOPEN">  
        if (curObj.C.getType()
                .equalsIgnoreCase("BORDERBOXOPEN")) {
            //==BORDERBOXOPEN=========================================================

            htmlCode += "<TABLE>";
        } else // </editor-fold>             
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="BORDERBOXCLOSE">  
        if (curObj.C.getType()
                .equalsIgnoreCase("BORDERBOXCLOSE")) {
            //==BORDERBOXOPEN=========================================================

            htmlCode += "</TABLE>";
        } else // </editor-fold>             
        //----------------------------------------------------------     
        // <editor-fold defaultstate="collapsed" desc="SYNOPTICLABEL">  
        if (curObj.C.getType().equalsIgnoreCase("SYNOPTICLABEL")) {
            //==SYNOPTICLABEL=========================================================
            int flg = 0;
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                flg++;
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
                ValoreDaScrivere = ValoreDaScrivere.replace("<", "&lt;");
                ValoreDaScrivere = ValoreDaScrivere.replace(">", "&gt;");
                ValoreDaScrivere = ValoreDaScrivere.replace("{", "&lbrace;");
                ValoreDaScrivere = ValoreDaScrivere.replace("}", "&rbrace;");
                ValoreDaScrivere = ValoreDaScrivere.replace("'", "&apos;");
            }
            String XcellType = "T";
            if (myForm.getType().equalsIgnoreCase("PANEL")) {
                htmlCode += " " + curObj.getLabelHeader();
            }

            htmlCode += " <INPUT  class=\"cellContent ";

            if ((curObj.Content.getType() != null
                    && curObj.Content.getType().equalsIgnoreCase("date"))
                    || (objType != null && objType.equalsIgnoreCase("date"))) {
                if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 9
                        && (ValoreDaScrivere.substring(4, 5).equalsIgnoreCase("-") || ValoreDaScrivere.substring(4, 5).equalsIgnoreCase("/"))) {
                    String xday = ValoreDaScrivere.substring(8, 10);
                    String xmonth = ValoreDaScrivere.substring(5, 7);
                    String xyear = ValoreDaScrivere.substring(0, 4);
                    ValoreDaScrivere = xday + "/" + xmonth + "/" + xyear;
                }
            } else if ((curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("dateTime"))
                    || (objType != null && objType.equalsIgnoreCase("dateTime"))) {
                htmlCode += (" datetimepickerclass ");
                XcellType = "DT";
            } else if ((curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("Time"))
                    || (objType != null && objType.equalsIgnoreCase("Time"))) {
                htmlCode += (" timepickerclass ");
                XcellType = "TM";
////////            } else if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("FLOAT")) {
////////
//////////                            System.out.println("SynopticLabel caso float: " + curObj.name+" V:"+ValoreDaScrivere);
////////                if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
////////666
////////                    float number = 0;
////////                    try {
////////
////////                        String strno = ValoreDaScrivere;
////////                        strno = strno.replace(",", ".");
////////                        number = Float.valueOf(strno);
////////                    } catch (Exception e) {
////////                        System.out.println("error rounding:  " + e.toString());
////////                    }
////////                    ValoreDaScrivere = "" + number;
////////
////////                    
////////                }

            } else if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("INT")) {
                htmlCode += " contentNumber  ";
                if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0
                        && ValoreDaScrivere.substring(0, 1).equals("-")) {
                    htmlCode += " negativeNumber  ";
                }
            } else if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("FLOAT")) {
                htmlCode += " contentNumber  ";
                if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                    // tronco a 3 cifre dopo il punto
                    int posX = ValoreDaScrivere.length() + 1;
                    if (ValoreDaScrivere.contains(".")) {
                        posX = ValoreDaScrivere.lastIndexOf(".");
                        posX = posX + 2;
                        if (ValoreDaScrivere.length() > posX) {
                            ValoreDaScrivere = ValoreDaScrivere.substring(0, posX + 1);
                        }
                    }
                }

            } else if (curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("EURO")) {
                htmlCode += " contentNumber  ";
                if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                    // tronco a 3 cifre dopo il punto
                    if (ValoreDaScrivere != null && ValoreDaScrivere.contains(".")) {
                        ValoreDaScrivere = ValoreDaScrivere + "00";
                        int posX = ValoreDaScrivere.lastIndexOf(".");
                        posX = posX + 2;
                        if (ValoreDaScrivere.length() > posX) {
                            ValoreDaScrivere = ValoreDaScrivere.substring(0, posX + 1);
                        }
                    } else {
                        ValoreDaScrivere = ValoreDaScrivere + ".00";
                    }
                    if (ValoreDaScrivere.startsWith("-")) {
                        ValoreDaScrivere = "<font color='red'>" + ValoreDaScrivere + "</font>";
                    }
                    ValoreDaScrivere = "€ " + ValoreDaScrivere;
                }
            } else if (curObj.Content.getType() != null && (curObj.Content.getType().equalsIgnoreCase("MINtoHOURS")||curObj.Content.getType().equalsIgnoreCase("MINStoHOURS"))) {
                htmlCode += " contentNumber  ";
                int no = 0;
                try {
                    no = Integer.parseInt(ValoreDaScrivere);
                    int hours = (int) (no / 60); //since both are ints, you get an int
                    int minutes = (int) (no % 60);
                    ValoreDaScrivere = hours + "h " + minutes + "m";
                } catch (Exception e) {
                }

            }
            htmlCode += "\" ";
            htmlCode += "type=\"TEXT\" ";

            htmlCode += getStyleHtmlCode(curObj, KEYvalue);

            htmlCode += "id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    + "value='" + ValoreDaScrivere + "'  ";
            if (curObj.getActionParams() == null) {
                curObj.setActionParams("");
            }
            htmlCode += " readonly ";
//            String fontSize = curObj.C.conditionalFontSize;
            htmlCode += "/> ";
        } else // </editor-fold>     
        //----------------------------------------------------------     
        // <editor-fold defaultstate="collapsed" desc="PUREHTML">  
        if (curObj.C.getType().equalsIgnoreCase("PUREHTML")) {
            //==PUREHTML=========================================================
            System.out.println("DISEGNO PUREHTML ---------->" + curObj.CG.getValue());
            htmlCode += curObj.CG.getValue();
        } else // </editor-fold>     
        //----------------------------------------------------------     
        // <editor-fold defaultstate="collapsed" desc="FIELDHTML">  
        if (curObj.C.getType().equalsIgnoreCase("FIELDHTML")) {
            //==FIELDHTML=========================================================
//            System.out.println("DISEGNO FIELDHTML ---------->" + curObj.CG.getValue());
            htmlCode += ValoreDaScrivere;
        } else // </editor-fold>     
        //----------------------------------------------------------                 
        // <editor-fold defaultstate="collapsed" desc="POLLER">  
        if (curObj.C.getType().equalsIgnoreCase("POLLER")) {
            //==POLLER=========================================================
            String pollingObject = "";

            pollingObject += "<TABLE><TR><TD>";
            pollingObject += "<input type=\"hidden\" ";
            pollingObject += "value=0 ";
            pollingObject += " id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-COUNTER\"> ";
            pollingObject += "<input type=\"checkbox\" ";
            pollingObject += "class=\"pollingObj\" checked=\"checked\" ";
            pollingObject += "name=\"" + curObj.name + "\" ";
            pollingObject += " id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\"> ";
            pollingObject += "</TD><td>" + curObj.CG.getValue() + "</td>";
            pollingObject += "</TR></TABLE>";
            pollingObject += "<div";
            pollingObject += " id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "-SYNOPTIC\"  ";
            pollingObject += ">";
            pollingObject += "</div>";
            htmlCode += pollingObject;

        } else // </editor-fold>   
        //---------------------------------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="RTSYNOPTIC">  
        if (curObj.C.getType()
                .equalsIgnoreCase("RTSYNOPTIC")) {
            //==RTSYNOPTIC=========================================================
            if (curObj.C.getConditionalLabel() != null) {
                ValoreDaScrivere = curObj.C.getConditionalLabel();
            }
            // System.out.println("CASO SensibleLABEL ValoreDaScrivere:" + ValoreDaScrivere + " - curObj.Content.getThisRowModifiable():" + curObj.Content.getThisRowModifiable());
            String RTCode = "";
            if (objModifiable == true || objCanPushButton == true) {
                //System.out.println("CASO SensibleLABEL ValoreDaScrivere:" + ValoreDaScrivere);
                RTCode += "<a class=\"SensibleLABEL "
                        //    + "cellContent"
                        + "\" id=\"" + myForm.getID() + "-" + curObj.name + "\" ";
                RTCode += " style= \"width:" + curObj.C.getWidth() + "; ";
                String defaultStyle = "";
                String retreivedStyle = "";
                //2.0 default style
                if (curObj.C.getDefaultStyle() != null && curObj.C.getDefaultStyle().length() > 0) {
                    defaultStyle = curObj.C.getDefaultStyle();
                }
                if (curObj.getTriggeredStyle() != null && curObj.getTriggeredStyle().length() > 2) {
                    retreivedStyle += curObj.getTriggeredStyle();
                } else if (curObj.C.getDefaultStyle() != null) {
                    //     System.out.println(" TROVATO DFAULT STYLE:" + curObj.C.getDefaultStyle());
                    retreivedStyle += defaultStyle;
                }
                if (retreivedStyle.contains("display:")) {

                } else {
                    retreivedStyle += " display:block; ";
                }
                if (retreivedStyle.contains("background-color:")) {

                } else {
                    retreivedStyle += " background-color:lightGreen; ";
                }

                RTCode += retreivedStyle;
                RTCode += "\"";
                String params = curObj.getActionParams();
                if (params == null) {
                    params = "{}";
                }
                if (curObj.getActionPerformed() != null
                        && curObj.getActionPerformed().equalsIgnoreCase("OpenSecForm")) {
                }
                String toAdd = ",\"action\":\"" + curObj.getActionPerformed() + "\""
                        + ",\"rifForm\":\"" + myForm.getID() + "\""
                        + ",\"copyTag\":\"" + myForm.getCopyTag() + "\""
                        + ",\"fatherForm\":\"" + myForm.getFather() + "\""
                        + ",\"ges_routineOnLoad\":\"" + curObj.getGes_routineOnLoad() + "\""
                        + ",\"triggerEvent\":\"click\""
                        + ",\"rifObj\":\"" + curObj.name + "\""
                        + ",\"keyValue\":\"" + KEYvalue + "\"}";
                params = params.replace("}", toAdd);
                RTCode += " onclick='javascript:RTonClick( " + params + " )'";
//                params.replace(",\"triggerEvent\":\"click\"", ",\"triggerEvent\":\"dblclick\"");
//                RTCode += " ondblclick='javascript:dblclickedObject( " + params + " )'";

                RTCode += " ";
                RTCode += "> ";

                // cerco una immagine in gFEobjects, campo 'picture' dove ID = id di questo oggetto
                objectLayout myBox = new objectLayout();
                myBox.loadBoxLayout(curObj.C.getJsClass());
                if (myBox.getWidth() == null) {
                    myBox.setWidth("20px");
                }
                if (myBox.getHeight() == null) {
                    myBox.setHeight("20px");
                }
                if (myBox.getType() == null) {
                    myBox.setType("");
                }
                String imageCode = " <DIV id=\"" + curObj.name + "-" + KEYvalue + "\" "
                        + "style=\"width:" + myBox.getWidth() + ";"
                        + "height:" + myBox.getHeight() + ";"
                        + "\"></DIV> ";
                if (myBox.getType().equalsIgnoreCase("picOnly")) {
                    String SLcode = "";
                    SLcode += "<table style=\" "
                            + "width:" + myBox.getWidth() + ";height:" + myBox.getHeight() + ";  "
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;"
                            + "margin: 0px 0 0px 0; padding: 0px;"
                            + "vertical-align:middle;"
                            + "\">" + imageCode + "</td>";
                    SLcode += "</tr></table>";
                    RTCode += SLcode;
                } else if (myBox.getType().equalsIgnoreCase("STANDARD")) {
                    String SLcode = "";
                    SLcode += "<table style=\" margin-left:auto; \n"
                            + "margin-right:auto; "
                            + "vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\">" + imageCode + "</td>";
                    SLcode += "</tr><tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\" ";
                    SLcode += getStyleHtmlCode(curObj, KEYvalue);
                    SLcode += ">" + ValoreDaScrivere + "</td>";
                    SLcode += "</tr></table>";
                    RTCode += SLcode;
                } else {
                    String SLcode = "";
                    SLcode += "<table style=\" margin-left:auto; \n"
                            + "margin-right:auto; "
                            + "vertical-align:middle;\" >";
//                    SLcode += "<tr style=\"text-align:center;vertical-align:middle;\"><td style=\"text-align:center;vertical-align:middle;\">" + imageCode + "</td></tr>";
                    SLcode += "<tr style=\"text-align:center;vertical-align:middle;\">";
                    SLcode += "<td style=\"text-align:center;vertical-align:middle;\" ";
                    SLcode += getStyleHtmlCode(curObj, KEYvalue);
                    SLcode += ">" + ValoreDaScrivere + "</td>";
                    SLcode += "</tr></table>";
                    RTCode += SLcode;
//                    RTCode += ValoreDaScrivere;

                }
                RTCode += "</a>";
            }

            htmlCode += RTCode;
            htmlCode += "<INPUT  class=\"cellContent\"  type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" value=\"" + ValoreDaScrivere + "\">";
        } else // </editor-fold>             
        //----------------------------------------------------------   
        // <editor-fold defaultstate="collapsed" desc="TEXT">  
        {
            int flg = 0;
//            if (ValoreDaScrivere.startsWith("{")) {
//                System.out.println("CASO TEXTBOX ValoreDaScrivere:" + ValoreDaScrivere);
//            
//            }
            if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 0) {
                flg++;
                ValoreDaScrivere = ValoreDaScrivere.replace("\"", "&quot;");
                ValoreDaScrivere = ValoreDaScrivere.replace("<", "&lt;");
                ValoreDaScrivere = ValoreDaScrivere.replace(">", "&gt;");
                ValoreDaScrivere = ValoreDaScrivere.replace("{", "&lbrace;");
                ValoreDaScrivere = ValoreDaScrivere.replace("}", "&rbrace;");
                ValoreDaScrivere = ValoreDaScrivere.replace("'", "&apos;");
            }

//            if (flg > 0) {
//                System.out.println("diventa:" + ValoreDaScrivere);
//            }
            String XcellType = "T";
            // System.out.println("CASO TEXTBOX ValoreDaScrivere:" + ValoreDaScrivere + " tipo " + objType);

            if (myForm.getType().equalsIgnoreCase("PANEL")) {
                htmlCode += " " + curObj.getLabelHeader();
            }

            htmlCode += "<INPUT  class=\"cellContent ";

            if ((curObj.Content.getType() != null
                    && curObj.Content.getType().equalsIgnoreCase("date"))
                    || (objType != null && objType.equalsIgnoreCase("date"))) {

                // in display devo invertire l'ordine di anno e giorno
                // se si tratta di una data non nulla il formato sarà 2018-01-29 e diventa 29/01/2018
                if (ValoreDaScrivere != null && ValoreDaScrivere.length() > 9
                        && (ValoreDaScrivere.substring(4, 5).equalsIgnoreCase("-") || ValoreDaScrivere.substring(4, 5).equalsIgnoreCase("/"))) {
                    // inverto
                    String xday = ValoreDaScrivere.substring(8, 10);
                    String xmonth = ValoreDaScrivere.substring(5, 7);
                    String xyear = ValoreDaScrivere.substring(0, 4);
                    ValoreDaScrivere = xday + "/" + xmonth + "/" + xyear;
                }
                if (objModifiable == true) {
                    htmlCode += (" datepickerclass ");
                    XcellType = "D";
                }
            } else if ((curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("dateTime"))
                    || (objType != null && objType.equalsIgnoreCase("dateTime"))) {
                htmlCode += (" datetimepickerclass ");
                XcellType = "DT";
            } else if ((curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("Time"))
                    || (objType != null && objType.equalsIgnoreCase("Time"))) {
                htmlCode += (" timepickerclass ");
                XcellType = "TM";
            } else if ((curObj.Content.getType() != null && curObj.Content.getType().equalsIgnoreCase("FLOAT"))) {
                htmlCode += (" contentNumber ");
                XcellType = "T";
            }

            htmlCode += "\" ";
            htmlCode += "type=\"TEXT\" ";

            htmlCode += getStyleHtmlCode(curObj, KEYvalue);

            htmlCode += "id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "\" "
                    + "value='" + ValoreDaScrivere + "'  ";
            if (curObj.getActionParams() == null) {
                curObj.setActionParams("");
            }
            if (objModifiable == true) {
                String jsonArgs = "{";
                jsonArgs += "\"formID\":\"" + myForm.getID() + "\",";
                jsonArgs += "\"copyTag\":\"" + myForm.getCopyTag() + "\",";
                jsonArgs += "\"objName\":\"" + curObj.name + "\",";
                jsonArgs += "\"KEYvalue\":\"" + KEYvalue + "\",";
                jsonArgs += "\"operation\":\"textChanges\",";
                jsonArgs += "\"cellType\":\"" + XcellType + "\",";
                jsonArgs += "\"filterField\":\"\",";
                jsonArgs += "\"routineOnChange\":\"" + curObj.getRoutineOnChange() + "\",";
                jsonArgs += "\"actionParams\":\"" + encodeURIComponent(curObj.getActionParams()) + "\",";
                jsonArgs += "\"exitRoutine\":\"\"}";

                htmlCode += " onChange='javascript:smartCellChanged(" + jsonArgs + ")'  ";
                if (myForm.getShowCounter() != null && myForm.getShowCounter().equalsIgnoreCase("FALSE")) {
                    htmlCode += " onmouseup=\"javascript:objSelected('" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + curObj.name + "-" + KEYvalue + "')\"";
                }
                //  htmlCode += "onmouseup=\"javascript:objSelected('" +  myForm.getID()+ "-"+myForm.getCopyTag()  + "-" + curObj.name + "-" + KEYvalue + "')\" ";
            } else {
                htmlCode += " readonly ";
            }
            if (KEYvalue.equalsIgnoreCase("NEW") && curObj.AddingRow_enabled < 1) {
                htmlCode += " readonly disabled ";
            }
            /* if (objType.equalsIgnoreCase("date")) {
             htmlCode += (" class=\"datepickerclass\" ");
             // out.println(" class=\"datepicker\" ");
             }
             if (objType.equalsIgnoreCase("dateTime")) {
             htmlCode += (" class=\"datetimepickerclass\" ");
             }*/
            String fontSize = curObj.C.conditionalFontSize;
            htmlCode += "/>";
        }
        // </editor-fold>             
//----------------------------------------------------------      

        return htmlCode;
    }

    private String getStyleHtmlCode(smartObject obj, String KEYvalue) {

        //---S T Y L E -------------------------------------  
        String htmlCode = "style =\" ";
        if (KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")
                && (obj.AddingRow_enabled < 1)) {
            htmlCode += " background-color:grey; ";
        } else if (KEYvalue != null && KEYvalue.equalsIgnoreCase("NEW")
                && (obj.AddingRow_enabled > 0)) {
            htmlCode += " background-color:yellow; ";
        } else {
            String tempStyle = "";

            //2.0 default style
            if (obj.C.getDefaultStyle() != null && obj.C.getDefaultStyle().length() > 0) {
                tempStyle = obj.C.getDefaultStyle();
                if (obj.C.getConditionalBackColor() != null && obj.C.getConditionalBackColor().length() > 0) {
                    tempStyle += " background-color:" + obj.C.getConditionalBackColor() + "; \" ";
                }
            } else {
                if (obj.C.getType().equalsIgnoreCase("SYNOPTICLABEL")) {
                    tempStyle = " background-color:lightGrey; \" ";
                } else {
                    //1. colore sfondo standard
                    tempStyle = " background-color:white;  ";
                    if (obj.C.getConditionalBackColor() != null && obj.C.getConditionalBackColor().length() > 0) {
                        tempStyle = " background-color:" + obj.C.getConditionalBackColor() + "; \" ";
                    }
                }
            }
            //3.0 conditional

            htmlCode += tempStyle;

        }
        //4. chiudo lo style
        htmlCode += "\" ";
        //--------------------------------------------------   
        return htmlCode;
    }

    public String getGroupsList(smartObject obj, String KEYvalue, ArrayList<SelectListLine> rowValues) {
        String params = obj.CG.getParams();
//        System.out.println("\n\nsmartRow-->getGroupsList params:" + params);
        /*.
        {"partAtab":"contacts",
        "partAfield":"ID" 
        "partBquery":"SELECT * FROM gruppiInteresse",
        "partBvalueField":"ID",
        "partBlabelField":"descrizione",
        "partBiconField":"" }
         */
        Linker myLinker = new Linker();
        myLinker.readParamsJson(params);
        ArrayList<SelectListLine> myLines = new ArrayList<SelectListLine>();
        ArrayList<SelectListLine> myCheckedLines = new ArrayList<SelectListLine>();

        CRUDorder myCRUD = new CRUDorder(myForm.myParams, myForm.mySettings);
        myCRUD.setFatherKEYvalue(myForm.getFatherKEYvalue());
        myCRUD.setSendToCRUD(myForm.sendToCRUD);

//        System.out.println("myLinker.getPartAtab() " + myLinker.getPartAtab());
//        System.out.println("myLinker.getPartBtab() " + myLinker.getPartBtab());
//        System.out.println("myLinker.getPartBquery() " + myLinker.getPartBquery());
//System.out.println("rowValues.size(): " + rowValues.size());
        //Sostituisco in partAtab e partBtab i valori@@@field@@@ con i value in rs 
        for (int i = 0; i < rowValues.size(); i++) {
//            System.out.println("RIMPIAZZO " + "@@@" + rowValues.get(i).getLabel() + "@@@   ---> " + rowValues.get(i).getValue());
            String query = myLinker.getPartBquery();
            String replacer = rowValues.get(i).getValue();
            if (replacer == null) {
                replacer = "";
            }
            query = query.replace("@@@" + rowValues.get(i).getLabel() + "@@@", replacer);
            myLinker.setPartBquery(query);
        }

        String SQLphrase = myCRUD.standardReplace(myLinker.getPartBquery(), null);
//        System.out.println("SQLphrase partBquery ---------->" + SQLphrase);
        Connection conny = new EVOpagerDBconnection(myForm.myParams, myForm.mySettings).ConnLocalDataDB();
        PreparedStatement ps = null;
        ResultSet rs;

        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                SelectListLine myLine = new SelectListLine();
                myLine.setLabel(rs.getString(myLinker.getPartBlabelField()));
                myLine.setValue(rs.getString(myLinker.getPartBvalueField()));
                myLines.add(myLine);
            }
            for (int jj = 0; jj < myLines.size(); jj++) {
                SQLphrase = "SELECT * FROM " + myLinker.getLinkTableName() + " WHERE "
                        + "partAtab = '" + myLinker.getPartAtab() + "' "
                        + "AND partAvalueField = '" + myLinker.getPartAfield() + "' "
                        + "AND partAvalue = '" + KEYvalue + "' "
                        + "AND partBtab = '" + myLinker.getPartBtab() + "' "
                        + "AND partBvalueField = '" + myLinker.getPartBvalueField() + "' "
                        + "AND partBvalue = '" + myLines.get(jj).getValue() + "' ";
//                System.out.println("CERCO CHECK ---------->" + SQLphrase);
                myLines.get(jj).setChecked(0);
                ps = conny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();
                while (rs.next()) {
                    myCheckedLines.add(myLines.get(jj));
                    break;
                }
            }

            conny.close();
        } catch (SQLException ex) {

        }

        String HtmlCode = "";
        HtmlCode += "<TABLE>";
        if (myCheckedLines.size() < 1) {
            HtmlCode += "<TR><TD>- N.D. -</TD></TR>";
        } else {
            for (int jj = 0; jj < myCheckedLines.size(); jj++) {

                HtmlCode += "<TR><TD>";
                HtmlCode += myCheckedLines.get(jj).getLabel();
                HtmlCode += " </TD></TR>";
            }
        }
        HtmlCode += "</TABLE>";
        //       System.out.println("CHECK HtmlCode ---------->" + HtmlCode);

        return HtmlCode;
    }

    public String browserRowArgsReplace(String query) {
        if (query == null) {
            return null;
        }
        CRUDorder myCRUD = new CRUDorder(myForm.myParams, myForm.mySettings);
        myCRUD.setFatherKEYvalue(myForm.fatherKEYvalue);

        myCRUD.setSendToCRUD(myForm.sendToCRUD);
        query = myCRUD.standardReplace(query, null);

        if (rowValues != null) {
            for (int jj = 0; jj < rowValues.size(); jj++) {
                String toBeReplaced = "!##" + rowValues.get(jj).getMarker() + "##!";
//                  System.out.println("smartRow->browserArgsReplace->rowValues: " + rowValues.get(jj).getMarker() + "-->" + rowValues.get(jj).getValue());             
                if (query.contains(toBeReplaced)) {
                    query = query.replace(toBeReplaced, rowValues.get(jj).getValue());
                }
            }
        }

//        System.out.println("\n>browserArgsReplace>>> " + query);
        return query;

    }
}
