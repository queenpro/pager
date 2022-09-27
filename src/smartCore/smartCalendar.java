/*
 * Copyright (C) 2022 Franco Venezia @ www.ffs.it
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
import REVOpager.DBimage;
import REVOpager.EVOpagerDBconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.SelectListLine;
import models.gate;

/**
 *
 * @author Franco Venezia @ www.ffs.it
 */
public class smartCalendar {

    public EVOpagerParams myParams;
    public Settings mySettings;
    String formName = "";

    public smartCalendar(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
    }

    public String getFormName() {
        return formName;
    }

    public String disegnaCalendario(gate myGate, String mode, String routine, ArrayList<SelectListLine> argList) {
        String ANNO = "";
        for (int i = 0; i < argList.size(); i++) {
            if (argList.get(i).getLabel().equalsIgnoreCase("ANNO")) {
                ANNO = argList.get(i).getValue();
                break;
            }
        }
        String MESE = "";
        for (int i = 0; i < argList.size(); i++) {
            if (argList.get(i).getLabel().equalsIgnoreCase("MESE")) {
                MESE = argList.get(i).getValue();
                break;
            }
        }
//        System.out.println("updateCalendar--> ANNO:" + ANNO);
//        System.out.println("updateCalendar--> MESE:" + MESE);

        int year = Integer.parseInt(ANNO);
        int month = Integer.parseInt(MESE);
        if (routine.equalsIgnoreCase("CalNextMonth")) {
            month++;
        } else if (routine.equalsIgnoreCase("CalPrevMonth")) {
            month--;
        } else if (routine.equalsIgnoreCase("CalNextYear")) {
            year++;
        } else if (routine.equalsIgnoreCase("CalPrevYear")) {
            year--;
        }
        if (month > 12) {
            month = 1;
            year++;
        }
        if (month < 1) {
            month = 12;
            year--;
        }

//        System.out.println("updateCalendar--> NEW ANNO:" + year);
//        System.out.println("updateCalendar--> NEW MESE:" + month);
        String htmlCalCode = disegnaCalendario(myGate, year, month, "INSIDEUPDATE");
        return htmlCalCode;
    }

    public String disegnaCalendario(gate myGate, int year, int month, String mode) {
        String htmlCalCode = "";
        String calendarFormID = myGate.getChild_formID();

        if (mode.equalsIgnoreCase("INSIDEUPDATE")) {
            calendarFormID = myGate.getFormID();
        } else {
            calendarFormID = myGate.getChild_formID();
        }

        //1. risaliamo al form che genera la richiesta
        System.out.println("updateCalendar--> form getFormID :" + myGate.getFormID());
        System.out.println("updateCalendar--> form getFormName :" + myGate.getFormName());
        System.out.println("updateCalendar--> form getFormToLoad :" + myGate.getChild_formID());
        //1. risaliamo ala tabella dei duties con la query specifica
        smartForm myForm = new smartForm(calendarFormID, myParams, mySettings);
////////        System.out.println("updateCalendar--> myGate.getSendToCRUD() :" + myGate.getSendToCRUD());
////////        System.out.println("updateCalendar--> myGate.getTBS() :" + myGate.getTBS());
//                myForm.setSendToCRUD(myGate.getTBS());

        myForm.loadSettingsAndPanel();
        myForm.setToBeSent(myGate.getTBS());
        myForm.prepareSQL(myForm.getQuery());

        System.out.println("updateCalendar--> form myForm.getName() :" + myForm.getName());
        this.formName = myForm.getName();

        System.out.println("updateCalendar-->main query :" + myForm.getQuery());
        System.out.println("updateCalendar-->main getQueryUsed :" + myForm.getQueryUsed());
        System.out.println("updateCalendar-->main Ges_formPanel :" + myForm.getGes_formPanel());
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        Date recordedDate = null;
        Calendar clnd = Calendar.getInstance();
        try {
            recordedDate = format.parse(year + "-" + month + "-01");
            clnd.setTime(recordedDate);
        } catch (java.text.ParseException ex) {
            System.out.println("Error 138:" + ex.toString());
        }
        clnd.setFirstDayOfWeek(Calendar.MONDAY);
        int yearStart = clnd.get(Calendar.YEAR);
        int monthStart = 1 + clnd.get(Calendar.MONTH);
        int dayStart = clnd.get(Calendar.DAY_OF_MONTH);
        int dwimStart = clnd.get(Calendar.DAY_OF_WEEK_IN_MONTH) - 1;
        int dowStart = clnd.get(Calendar.DAY_OF_WEEK) - 1;
        System.out.println("updateCalendar-->TODAY :" + dayStart + "/" + monthStart + "/" + yearStart + "   ->" + dowStart + "   ->" + dwimStart);
        clendarDateBox[] boxes = new clendarDateBox[100];

        //------------------------------------------------------------
        String firstDayOfMonth = yearStart + "-" + monthStart + "-01";
        Calendar cx = Calendar.getInstance();
        try {
            recordedDate = format.parse(firstDayOfMonth);
            cx.setTime(recordedDate);
        } catch (java.text.ParseException ex) {
            System.out.println("Error 155:" + ex.toString());
        }
        int cols = 7;

        int dow1 = cx.get(Calendar.DAY_OF_WEEK);

        System.out.println("updateCalendar-->il giorno del mese è un  :" + dow1);
        int nofPreBoxes = dow1 - 2;
        System.out.println("Devo creare " + nofPreBoxes + "  PreBoxes");
        // primo giorno
        Calendar firstDay = Calendar.getInstance();;
        firstDay.setTime(cx.getTime());
        firstDay.add(Calendar.DATE, -(nofPreBoxes));
        System.out.println("updateCalendar-->FIRST DAY :" + calToString(firstDay));
        //ultimo giorno

        int lastDayNumber = cx.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println("updateCalendar-->l'ultimo giorno del mese è:" + lastDayNumber);
        String lastDayOfMonth = yearStart + "-" + monthStart + "-" + lastDayNumber;
        Calendar cy = Calendar.getInstance();
        try {
            recordedDate = format.parse(lastDayOfMonth);
            cy.setTime(recordedDate);
        } catch (java.text.ParseException ex) {
            System.out.println("Error 178:" + ex.toString());
        }
        int dow2 = cy.get(Calendar.DAY_OF_WEEK);

        System.out.println("updateCalendar-->l'ultimo giorno del mese è un :" + dow2);
        Calendar lastDay = Calendar.getInstance();
        lastDay = cy;

        int nofPostBoxes = 0;
        int realdow = dow2 - 1;
        if (realdow < 1) {
            realdow = 7;
        }
        if (realdow < 7) {
            nofPostBoxes = 7 - realdow;
//            System.out.println("updateCalendar-->nofPostBoxes :" + nofPostBoxes);
            lastDay.add(Calendar.DATE, nofPostBoxes);
        }
        System.out.println("updateCalendar-->LAST DAY :" + lastDay.get(Calendar.DAY_OF_MONTH) + "/" + (1 + lastDay.get(Calendar.MONTH)) + "/" + lastDay.get(Calendar.YEAR));

        int boxesDrawn = 0;
        int boxesInRow = 0;
        int boxesCol = 1;
        int boxesRow = 1;
        Calendar boxCalDay = Calendar.getInstance();
        Calendar lastBoxDate = Calendar.getInstance();
        Calendar firstBoxDate = Calendar.getInstance();
        boxCalDay.setTime(firstDay.getTime());

        firstBoxDate.setTime(boxCalDay.getTime());
        while ((boxCalDay.before(lastDay) || boxCalDay.equals(lastDay)) && boxesDrawn < 49) {
            clendarDateBox myBox = new clendarDateBox();
            myBox.DoW = boxCalDay.get(Calendar.DAY_OF_WEEK) - 1;
            if (myBox.DoW < 1) {
                myBox.DoW = 7;
            }
            myBox.cal.setTime(boxCalDay.getTime());
            boxesDrawn++;
            boxes[boxesDrawn] = new clendarDateBox();
            boxes[boxesDrawn] = myBox;
            boxesCol++;
            if (boxesCol > 7) {
                boxesCol = 0;
                boxesRow++;
            }
            lastBoxDate.setTime(boxCalDay.getTime());
//                    System.out.println("updateCalendar-->BOX " + boxesDrawn + "  DAY :" + boxCalDay.get(Calendar.DAY_OF_MONTH) + "/" + (1 + boxCalDay.get(Calendar.MONTH)) + "/" + boxCalDay.get(Calendar.YEAR));
            boxCalDay.add(Calendar.DATE, 1);

        }

        //---------ora riempio i box con gli impegni
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        String SQLphrase = "";
        PreparedStatement ps;
        ResultSet rs;
        int calls = 0;
        // parso i dates
        try {
            SQLphrase = myForm.getQueryUsed();//"SELECT * FROM `Cal_calendarCalls` WHERE rifCalendar = '" + CALENDARIO + "'  ";
            System.out.println("updateCalendar-->SQLphrase :" + SQLphrase);
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                calls++;
                System.out.println("Appuntamento n." + calls);
                clendarDateBox tempBox = new clendarDateBox();
                tempBox.ID = rs.getInt("ID");
                tempBox.dateStart = rs.getString("dateStart");
                tempBox.dateEnd = rs.getString("dateEnd");
                tempBox.dayOfWeek = rs.getInt("dayOfWeek");
                tempBox.label = rs.getString("label");
                tempBox.interval = rs.getInt("interval");
                tempBox.rifTab = rs.getString("rifTab");
                tempBox.timeEnd = rs.getString("timeEnd");
                tempBox.timeStart = rs.getString("timeStart");
                tempBox.type = rs.getString("type");

//                        tempBox.rifCliente = rs.getString("nomeCliente");
//                        tempBox.rifServizio = rs.getString("nomeServizio");
//                        tempBox.rifActivity = rs.getString("nomeActivity");
//                        tempBox.rifIntervento = rs.getString("nomeIntervento");
//                        tempBox.caso = rs.getString("caso");
                if (tempBox.timeStart == null || tempBox.timeStart == "") {
                    tempBox.timeStart = "ND";
                }
                if (tempBox.timeEnd == null || tempBox.timeEnd == "") {
                    tempBox.timeEnd = "ND";
                }

                //-------------------------------------
                if (tempBox.type.equalsIgnoreCase("DOWoffset")) {

                    if (tempBox.label != null && tempBox.label.equalsIgnoreCase("null")) {
                        tempBox.label = "";
                    }
                    if (tempBox.duty != null && tempBox.duty.equalsIgnoreCase("null")) {
                        tempBox.duty = "";
                    }
                    if (tempBox.timeStart != null && tempBox.timeStart.endsWith(":00")) {
                        tempBox.timeStart = tempBox.timeStart.substring(0, tempBox.timeStart.length() - 3);
                    }
                    if (tempBox.timeEnd != null && tempBox.timeEnd.endsWith(":00")) {
                        tempBox.timeEnd = tempBox.timeEnd.substring(0, tempBox.timeEnd.length() - 3);
                    }

                    Calendar tempCallStart = Calendar.getInstance();
                    Calendar tempCallEnd = Calendar.getInstance();
                    try {
                        recordedDate = format.parse(tempBox.dateStart);
                        tempCallStart.setTime(recordedDate);
                    } catch (java.text.ParseException ex) {

                        System.out.println("Error 290" + ex.toString());
                    }
                    try {
                        recordedDate = format.parse(tempBox.dateEnd);
                        tempCallEnd.setTime(recordedDate);
                    } catch (java.text.ParseException ex) {
                        System.out.println("Error 296" + ex.toString());

                    }
                    // a partire da dateStart cerco la prima ricorrenza del dayOfWeek richiesto  
                    System.out.println("a partire da dateStart cerco la prima ricorrenza del dayOfWeek richiesto ");
//                        if (tempCallStart.before(firstBoxDate) && tempCallEnd.before(lastBoxDate)) {
//                            tempCallStart.setTime(firstBoxDate.getTime());// comincio la sere dalla prima data che mi serve realmente
//                        }
                    if ((tempCallStart.before(lastBoxDate) || tempCallStart.equals(lastBoxDate)) && (tempCallEnd.after(lastBoxDate))) {
                        int dowCalStart = normalizeDoW(tempCallStart);
                        System.out.println("updateCalendar-->tempCal iniziale del call :" + calToString(tempCallStart) + " è un giorno " + dowCalStart);
                        System.out.println("updateCalendar-->il mio duty si svolge nel giorno :" + tempBox.dayOfWeek);
                        System.out.println("updateCalendar-->tempCalEnd    :" + calToString(tempCallEnd));
                        int safeCounter = 0;
                        while (dowCalStart != tempBox.dayOfWeek && safeCounter < 10) {
                            safeCounter++;
                            tempCallStart.add(Calendar.DATE, 1);
                            dowCalStart = normalizeDoW(tempCallStart);
                        }
                        System.out.println("updateCalendar-->primo giorno valido per la call :" + calToString(tempCallStart) + " è un giorno " + dowCalStart);
//
//                        System.out.println("updateCalendar-->lastBoxDate    :" + calToString(lastBoxDate));
                        // adesso ho la nuova data inizio da considerare per questa call
                        safeCounter = 0;
                        while ((tempCallStart.before(lastBoxDate) || tempCallStart.equals(lastBoxDate)) && safeCounter < 9999) {//massimo 30 anni
                            safeCounter++;
//                            System.out.println("updateCalendar-->tempCal considerato  :" + calToString(tempCallStart));
                            // devo inserire appuntamento in questa data se rientra nel range tra firstDay e lastDay
                            if ((tempCallStart.after(firstDay) || tempCallStart.equals(firstDay)) && (tempCallStart.before(lastBoxDate) || tempCallStart.equals(lastBoxDate))) {
//                                System.out.println("Aggiungo " + tempBox.duty + " il " + calToString(tempCallStart));
                                for (int i = 1; i <= boxesDrawn; i++) {
                                    if (boxes[i].cal.equals(tempCallStart)) {
                                        boxes[i].duty += "<BR>";
                                        boxes[i].duty += "<TABLE class='calendarCall'><TR>";
                                        boxes[i].duty += "<TH>" + tempBox.timeStart + " > " + tempBox.timeEnd + "</TH>";
                                        boxes[i].duty += "</TR><TR> ";
                                        boxes[i].duty += "<TD>" + tempBox.label + "</TD>";
                                        boxes[i].duty += "</TR></TABLE>";

                                        break;
                                    }
                                }
                            }
                            // ora salto tante settimane quante sono descritte in pausa
                            tempCallStart.add(Calendar.DATE, 7 + (7 * tempBox.interval));
                        }
                    } else {
//                        System.out.println("Questa call non rientra nelle date da mostrare");
                    }

                }

            }
        } catch (SQLException ex) {
            System.out.println("Error 346: " + ex.toString());
        }

        htmlCalCode += "<DIV id=\"" + myForm.getID() + "-X-ROWSTABLE\">";
        String nomeElemento = "";
        nomeElemento = myForm.getID() + "-X-YEAR";
        String headerCalCode = "";

        String prevYcode = "";
        String Ycode = "";
        String nextYcode = "";
        String prevMcode = "";
        String Mcode = "";
        String nextMcode = "";

        String SLcode = "";
        prevYcode = getButtonAnchorCode(myGate, myForm, "" + year, "CalPrevYear", new DBimage("tab_icons", "name", "arrowLeft", "picture", myForm.getMyParams()).getDBimageHtmlCode64(mySettings, "20px", "20px"));
        nextYcode = getButtonAnchorCode(myGate, myForm, "" + year, "CalNextYear", new DBimage("tab_icons", "name", "arrowRight", "picture", myForm.getMyParams()).getDBimageHtmlCode64(mySettings, "20px", "20px"));
        Ycode = "<INPUT type=\"hidden\" id=\"" + myForm.getName() + "-" + myForm.getCopyTag() + "-YEAR\" value=\"" + year + "\" readonly/>";
        Ycode += "<p style=\"font-size:20px;\">" + year + "</p>";
        prevMcode = getButtonAnchorCode(myGate, myForm, "" + year, "CalPrevMonth", new DBimage("tab_icons", "name", "arrowLeft", "picture", myForm.getMyParams()).getDBimageHtmlCode64(mySettings, "20px", "20px"));
        nextMcode = getButtonAnchorCode(myGate, myForm, "" + year, "CalNextMonth", new DBimage("tab_icons", "name", "arrowRight", "picture", myForm.getMyParams()).getDBimageHtmlCode64(mySettings, "20px", "20px"));
        Mcode = "<INPUT type=\"hidden\"  id=\"" + myForm.getName() + "-" + myForm.getCopyTag() + "-MONTH\" value=\"" + month + "\" readonly/>";
        Mcode += "<p style=\"font-size:20px;\">" + monthToString(month) + "</p>";
        SLcode += "<table style=\" margin-left:auto; margin-right:auto; vertical-align:middle;\" ><tr style=\"text-align:center;vertical-align:middle;\">";
        SLcode += "<tr>";
        SLcode += "<td style=\"text-align:center;vertical-align:middle;\">";
        SLcode += prevYcode;
        SLcode += "</td>";
        SLcode += "<td style=\"text-align:center;vertical-align:middle;\">";
        SLcode += Ycode;
        SLcode += "</td>";
        SLcode += "<td style=\"text-align:center;vertical-align:middle;\">";
        SLcode += nextYcode;
        SLcode += "</td>";
        SLcode += "<td width= '100px'> </td>";
        SLcode += "<td style=\"text-align:center;vertical-align:middle;\">";
        SLcode += prevMcode;
        SLcode += "</td>";
        SLcode += "<td style=\"text-align:center;vertical-align:middle;\">";
        SLcode += Mcode;
        SLcode += "</td>";
        SLcode += "<td style=\"text-align:center;vertical-align:middle;\">";
        SLcode += nextMcode;
        SLcode += "</td>";
        SLcode += "</tr></table>";

        headerCalCode += SLcode;

        //------------- box riempiti
        htmlCalCode += headerCalCode;
        htmlCalCode += "<TABLE class=\"calendarGrid\"><TR>";
        htmlCalCode += "<TH>LUN</TH>";
        htmlCalCode += "<TH>MAR</TH>";
        htmlCalCode += "<TH>MER</TH>";
        htmlCalCode += "<TH>GIO</TH>";
        htmlCalCode += "<TH>VEN</TH>";
        htmlCalCode += "<TH>SAB</TH>";
        htmlCalCode += "<TH>DOM</TH>";
        htmlCalCode += "</TR><TR>";
        int col = 0;
        int row = 1;

        for (int i = 1; i <= boxesDrawn; i++) {
            col++;
            if (col > cols) {
                col = 1;
                row++;
                htmlCalCode += "</TR><TR>";
            }

            String style1 = "";
            String style2 = "";
            String DayColor = "";

            if (boxes[i].cal.get(Calendar.MONTH) != cx.get(Calendar.MONTH)) {
                DayColor = "grey";
                if (boxes[i].cal.get(Calendar.DAY_OF_WEEK) == 7) {
                    DayColor = "#FF6A00";
                } else if (boxes[i].cal.get(Calendar.DAY_OF_WEEK) == 1) {
                    DayColor = "#FF0000";
                }
                style1 = " style=\"color:" + DayColor + ";font-size:10px;\" ";
                style2 = " style=\"border-collapse: collapse; border: 1px solid black; background-color:#EFEFEF ; vertical-align: top; text-align: left;\" ";
            } else {
                DayColor = "black";
                if (boxes[i].cal.get(Calendar.DAY_OF_WEEK) == 7) {
                    DayColor = "#FF6A00";
                } else if (boxes[i].cal.get(Calendar.DAY_OF_WEEK) == 1) {
                    DayColor = "#FF0000";
                }
                style1 = " style=\"width:20px; color:" + DayColor + ";font-size:12px;background:#EFEFEF;  border-radius: 10px; text-align: center;\" ";
                style2 = " style=\"border-collapse: collapse; border: 1px solid black; vertical-align: top; text-align: left;\" ";
                Calendar today = Calendar.getInstance();;
                if (boxes[i].cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) && boxes[i].cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                    style2 = " style=\"border-collapse: collapse; border: 1px solid black; background-color:#FFE97F; vertical-align: top; text-align: left;\" ";
                }
            }
            htmlCalCode += "<TD";
            htmlCalCode += style2;
            htmlCalCode += ">";
            htmlCalCode += "<TABLE>";
            htmlCalCode += "<TR><TD>";
            htmlCalCode += "    <TABLE>";
            htmlCalCode += "    <TR>";
            htmlCalCode += "    <TD";
            htmlCalCode += style1;
            htmlCalCode += ">" + boxes[i].cal.get(Calendar.DAY_OF_MONTH) + "</TD>";
            htmlCalCode += "    </TR></TABLE>";
            htmlCalCode += "</TD></TR>";
            htmlCalCode += "<TR>";
            htmlCalCode += "<TD>" + boxes[i].duty + "</TD>";
            htmlCalCode += "</TR>";
            htmlCalCode += "</TABLE>";

            htmlCalCode += "</TD>";
        }
        htmlCalCode += "<TR></TABLE>";
        //********************************************************************
        htmlCalCode += " </DIV>";
        try {
            conny.close();
        } catch (SQLException ex) {
            System.out.println("Error 477: " + ex.toString());
        }
        return htmlCalCode;
    }

    private String getButtonAnchorCode(gate myGate, smartForm myForm, String value, String buttonName, String contentCode) {
        String AnchorCode = "";
        AnchorCode += "<a class=\"SensibleLABEL\" id=\"" + myForm.getID() + "-" + buttonName + "\" ";
        AnchorCode += " style= \"margin: 0px 0px 0px 0px; padding: 0px;vertical-align:middle;width:40px;\"";
        String params = myGate.params;
        if (params == null || params.length() < 2) {
            params = "{}";
        }
        String toAdd = "";
        if (params.length() > 2) {
            toAdd += ",";
        }

        toAdd += "\"action\":\"ExecuteRoutine\""
                + ",\"rifForm\":\"" + myForm.getID() + "\""
                + ",\"copyTag\":\"" + myForm.getCopyTag() + "\""
                + ",\"rifObj\":\"" + buttonName + "\""
                + ",\"keyValue\":\"" + value + "\"}";
        params = params.replace("}", toAdd);
        AnchorCode += " onclick='javascript:smartButtonClick( " + params + " )'> ";
        AnchorCode += contentCode;
        AnchorCode += "</a>";
        return AnchorCode;
    }

    private int normalizeDoW(Calendar cy) {
        int dow2 = cy.get(Calendar.DAY_OF_WEEK);
        int realdow = dow2 - 1;
        if (realdow < 1) {
            realdow = 7;
        }
        return realdow;
    }

    private String calToString(Calendar cy) {
        String dt = "";
        dt = cy.get(Calendar.DAY_OF_MONTH) + "/" + (1 + cy.get(Calendar.MONTH)) + "/" + cy.get(Calendar.YEAR);
        return dt;
    }

    private String monthToString(int mnt) {
        String month = "Gennaio";
        switch (mnt) {
            case 1:
                month = "Gennaio";
                break;
            case 2:
                month = "Febbraio";
                break;
            case 3:
                month = "Marzo";
                break;
            case 4:
                month = "Aprile";
                break;
            case 5:
                month = "Maggio";
                break;
            case 6:
                month = "Giugno";
                break;
            case 7:
                month = "Luglio";
                break;
            case 8:
                month = "Agosto";
                break;
            case 9:
                month = "Settembre";
                break;
            case 10:
                month = "Ottobre";
                break;
            case 11:
                month = "Novembre";
                break;
            case 12:
                month = "Dicembre";
                break;
            default:
            // code block
        }

        return month;
    }

    class clendarDateBox {

        Calendar cal = Calendar.getInstance();
        int DoW = 0;

        int ID;
        String rifTab;
        String rifCalendar;
        String dateStart;
        String dateEnd;
        String type;
        int interval;
        int dayOfWeek;
        String timeStart;
        String timeEnd;
        String duty = "";
        String label = "";
        String rifCliente = "";
        String rifServizio = "";
        String rifActivity = "";
        String rifIntervento = "";
        String caso = "";

        public clendarDateBox() {
            duty = "";
        }
    }

}
