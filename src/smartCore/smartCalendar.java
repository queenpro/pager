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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.PDFcell;
import models.SelectListLine;
import models.gate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Franco Venezia @ www.ffs.it
 */
public class smartCalendar {

    clendarFrame[] calFrames;
    public EVOpagerParams myParams;
    public Settings mySettings;
    String formName = "";
    int framesDrawn = 0;
    Calendar firstDay;
    Calendar lastDay;
    Calendar lastBoxDate;
    Calendar firstBoxDate;
    smartForm myForm;
    gate myGate;
    int year;
    int month;
    String mode;
    Calendar calPrimoGiorno;

    int CdrawDayWorktime = 0;
    int CdrawBoxWorktime = 0;
    String CdefaultBoxColor = "lightBlue";

    public smartCalendar(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        this.calFrames = new clendarFrame[100];
    }

    public String getFormName() {
        return formName;
    }

    public void step1drawFrames(gate myGate, String mode, String routine, ArrayList<SelectListLine> argList) {
        this.myGate = myGate;
        this.mode = mode;
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
        step1drawFrames(myGate, year, month, mode);
    }

    public void step1drawFrames(gate myGate, int year, int month, String mode) {
        this.myGate = myGate;
        this.mode = mode;
        this.year = year;
        this.month = month;
        String calendarFormID = "";

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
        myForm = new smartForm(calendarFormID, myParams, mySettings);
////////        System.out.println("updateCalendar--> myGate.getSendToCRUD() :" + myGate.getSendToCRUD());
////////        System.out.println("updateCalendar--> myGate.getTBS() :" + myGate.getTBS());
//                myForm.setSendToCRUD(myGate.getTBS());

        myForm.loadSettingsAndPanel();
        myForm.setToBeSent(myGate.getTBS());
        myForm.prepareSQL(myForm.getQuery());

//        System.out.println("updateCalendar--> form myForm.getName() :" + myForm.getName());
        this.formName = myForm.getName();

//        System.out.println("updateCalendar-->main query :" + myForm.getQuery());
//        System.out.println("updateCalendar-->main getQueryUsed :" + myForm.getQueryUsed());
//        System.out.println("updateCalendar-->main Ges_formPanel :" + myForm.getGes_formPanel());
        // <editor-fold defaultstate="collapsed" desc="Prerparo i box necessari">
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

        //------------------------------------------------------------
        String firstDayOfMonth = yearStart + "-" + monthStart + "-01";
        calPrimoGiorno = Calendar.getInstance();
        try {
            recordedDate = format.parse(firstDayOfMonth);
            calPrimoGiorno.setTime(recordedDate);
        } catch (java.text.ParseException ex) {
            System.out.println("Error 155:" + ex.toString());
        }

        int dow1 = calPrimoGiorno.get(Calendar.DAY_OF_WEEK);

//        System.out.println("updateCalendar-->il giorno del mese è un  :" + dow1);
        int nofPreFrames = dow1 - 2;
        System.out.println("Devo creare " + nofPreFrames + "  PreFrames");

        // primo giorno
        firstDay = Calendar.getInstance();;
        firstDay.setTime(calPrimoGiorno.getTime());
        firstDay.add(Calendar.DATE, -(nofPreFrames));
//        System.out.println("updateCalendar-->FIRST DAY :" + calToString(firstDay));
        //ultimo giorno

        int lastDayNumber = calPrimoGiorno.getActualMaximum(Calendar.DAY_OF_MONTH);
//        System.out.println("updateCalendar-->l'ultimo giorno del mese è:" + lastDayNumber);
        String lastDayOfMonth = yearStart + "-" + monthStart + "-" + lastDayNumber;
        Calendar cy = Calendar.getInstance();
        try {
            recordedDate = format.parse(lastDayOfMonth);
            cy.setTime(recordedDate);
        } catch (java.text.ParseException ex) {
            System.out.println("Error 178:" + ex.toString());
        }
        int dow2 = cy.get(Calendar.DAY_OF_WEEK);

//        System.out.println("updateCalendar-->l'ultimo giorno del mese è un :" + dow2);
        lastDay = Calendar.getInstance();
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
//        System.out.println("updateCalendar-->LAST DAY :" + lastDay.get(Calendar.DAY_OF_MONTH) + "/" + (1 + lastDay.get(Calendar.MONTH)) + "/" + lastDay.get(Calendar.YEAR));

        framesDrawn = 0;
        int boxesInRow = 0;
        int boxesCol = 1;
        int boxesRow = 1;
        Calendar boxCalDay = Calendar.getInstance();
        lastBoxDate = Calendar.getInstance();
        firstBoxDate = Calendar.getInstance();
        boxCalDay.setTime(firstDay.getTime());

        firstBoxDate.setTime(boxCalDay.getTime());
        while ((boxCalDay.before(lastDay) || boxCalDay.equals(lastDay)) && framesDrawn < 49) {
            clendarFrame myBox = new clendarFrame();
            myBox.DoW = boxCalDay.get(Calendar.DAY_OF_WEEK) - 1;
            if (myBox.DoW < 1) {
                myBox.DoW = 7;
            }
            myBox.cal.setTime(boxCalDay.getTime());
            framesDrawn++;
            calFrames[framesDrawn] = new clendarFrame();
            calFrames[framesDrawn] = myBox;
            boxesCol++;
            if (boxesCol > 7) {
                boxesCol = 0;
                boxesRow++;
            }
            lastBoxDate.setTime(boxCalDay.getTime());
//            System.out.println("updateCalendar-->BOX " + framesDrawn + "  DAY :" + boxCalDay.get(Calendar.DAY_OF_MONTH) + "/" + (1 + boxCalDay.get(Calendar.MONTH)) + "/" + boxCalDay.get(Calendar.YEAR));
            boxCalDay.add(Calendar.DATE, 1);

        }

        // </editor-fold> 
    }

    private class calendarQuery {

        String query = "";
        int drawDayWorktime = 0;
        int drawBoxWorktime = 0;
        String defaultBoxColor = "";
    }

    public void step2addBoxes() {
        JSONParser parser = new JSONParser();
        int queriesNumber = 0;
        String panel = "";
        JSONArray cArray = null;
        ArrayList<calendarQuery> mycals = new ArrayList<>();
        try {
            panel = "{\"formPanel\":" + myForm.getGes_formPanel() + "}";
            System.out.println("step2addBoxes-->CALENDARS: " + panel);
            ArrayList<calendarQuery> queries = new ArrayList<>();
            JSONObject json = (JSONObject) parser.parse(panel);
            JSONArray jArray = (JSONArray) json.get("formPanel");
            for (Object info : jArray) {
                JSONObject myInfo = (JSONObject) info;
                if (myInfo.get("infoType").toString().equalsIgnoreCase("calendars")) {
                    cArray = (JSONArray) myInfo.get("calendars");
                    System.out.println("step2addBoxes-->CALENDARS: " + cArray.toString());
                    queriesNumber = cArray.size();
                    for (Object qr : cArray) {
                        JSONObject jQry = (JSONObject) qr;
                        calendarQuery myCalendarQuery = new calendarQuery();
                        myCalendarQuery.query = jQry.get("query").toString();

                        myCalendarQuery.drawBoxWorktime = 0;
                        myCalendarQuery.drawBoxWorktime = 0;
                        try {
                            myCalendarQuery.defaultBoxColor = jQry.get("defaultBoxColor").toString();
                        } catch (Exception e) {
                        }
                        try {
                            String vx = jQry.get("drawBoxWorktime").toString();
                            if (vx.equalsIgnoreCase("True")) {
                                myCalendarQuery.drawBoxWorktime = 1;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            String vx = jQry.get("drawDayWorktime").toString();
                            if (vx.equalsIgnoreCase("True")) {
                                myCalendarQuery.drawDayWorktime = 1;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mycals.add(myCalendarQuery);
                    }

                }

            }

        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(smartCalendar.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!mycals.isEmpty()) {
            for (calendarQuery mycal : mycals) {

                CdrawDayWorktime = mycal.drawDayWorktime;
                CdrawBoxWorktime = mycal.drawBoxWorktime;
                CdefaultBoxColor = mycal.defaultBoxColor;
                mycal.query = myForm.prepareSQL(mycal.query);

                System.out.println("updateCalendar-->CdrawDayWorktime :" + CdrawDayWorktime);
                System.out.println("updateCalendar-->CdrawBoxWorktime :" + CdrawBoxWorktime);
                System.out.println("updateCalendar-->CdefaultBoxColor :" + CdefaultBoxColor);
                System.out.println("updateCalendar-->query :" + mycal.query);

                step2addBoxes(mycal.query);
            }

        } else {
            step2addBoxes(myForm.getQueryUsed());
        }
    }

    public void step2addBoxes(String queries) {
        Date recordedDate = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        //---------ora riempio i box con gli impegni
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        String SQLphrase = "";
        PreparedStatement ps;
        ResultSet rs;
        int calls = 0;
        // parso i dates
        try {
            SQLphrase = queries;//"SELECT * FROM `Cal_calendarCalls` WHERE rifCalendar = '" + CALENDARIO + "'  ";
            System.out.println("updateCalendar-->SQLphrase :" + SQLphrase);
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                calls++;
                System.out.println("Appuntamento n." + calls);
                clendarDateCall tempDateCall = new clendarDateCall();
                tempDateCall.ID = rs.getInt("ID");
                tempDateCall.dateStart = rs.getString("dateStart");
                tempDateCall.dateEnd = rs.getString("dateEnd");
                tempDateCall.dayOfWeek = rs.getInt("dayOfWeek");
                tempDateCall.label = rs.getString("label");
                tempDateCall.interval = rs.getInt("interval");
                tempDateCall.rifTab = rs.getString("rifTab");
                tempDateCall.timeEnd = rs.getString("timeEnd");
                tempDateCall.timeStart = rs.getString("timeStart");
                tempDateCall.type = rs.getString("type");
                try {
                    tempDateCall.color = rs.getString("color");
                } catch (Exception e) {
                }
                if (tempDateCall.timeStart == null || tempDateCall.timeStart == "") {
                    tempDateCall.timeStart = "ND";
                }
                if (tempDateCall.timeEnd == null || tempDateCall.timeEnd == "") {
                    tempDateCall.timeEnd = "ND";
                }
                if (tempDateCall.label != null && tempDateCall.label.equalsIgnoreCase("null")) {
                    tempDateCall.label = "";
                }
                if (tempDateCall.duty != null && tempDateCall.duty.equalsIgnoreCase("null")) {
                    tempDateCall.duty = "";
                }
                if (tempDateCall.timeStart != null && tempDateCall.timeStart.endsWith(":00")) {
                    tempDateCall.timeStart = tempDateCall.timeStart.substring(0, tempDateCall.timeStart.length() - 3);
                }
                if (tempDateCall.timeEnd != null && tempDateCall.timeEnd.endsWith(":00")) {
                    tempDateCall.timeEnd = tempDateCall.timeEnd.substring(0, tempDateCall.timeEnd.length() - 3);
                }
                if (tempDateCall.color == null || tempDateCall.color.length() < 1) {
                    tempDateCall.color = this.CdefaultBoxColor;
                }
                Calendar tempCallStart = Calendar.getInstance();
                Calendar tempCallEnd = Calendar.getInstance();
                try {
                    recordedDate = format.parse(tempDateCall.dateStart);
                    tempCallStart.setTime(recordedDate);
                } catch (java.text.ParseException ex) {

                    System.out.println("Error 290" + ex.toString());
                }
                try {
                    recordedDate = format.parse(tempDateCall.dateEnd);
                    tempCallEnd.setTime(recordedDate);
                } catch (java.text.ParseException ex) {
                    System.out.println("Error 296" + ex.toString());

                }
                //-------------------------------------
                if (tempDateCall.type.equalsIgnoreCase("DOWoffset")) {

                    // a partire da dateStart cerco la prima ricorrenza del dayOfWeek richiesto  
//                    System.out.println("a partire da dateStart cerco la prima ricorrenza del dayOfWeek richiesto ");
//                        if (tempCallStart.before(firstBoxDate) && tempCallEnd.before(lastBoxDate)) {
//                            tempCallStart.setTime(firstBoxDate.getTime());// comincio la sere dalla prima data che mi serve realmente
//                        }
                    if ((tempCallStart.before(lastBoxDate)
                            || tempCallStart.equals(lastBoxDate))
                            && (tempCallEnd.after(firstBoxDate))) {
                        //inizia prima della fine del calendario rfinisce dopo il primo diorno dio calendario
                        int dowCalStart = normalizeDoW(tempCallStart);
//                        System.out.println("updateCalendar-->tempCal iniziale del call :" + calToString(tempCallStart) + " è un giorno " + dowCalStart);
//                        System.out.println("updateCalendar-->il mio duty si svolge nel giorno :" + tempDateCall.dayOfWeek);
//                        System.out.println("updateCalendar-->tempCalEnd    :" + calToString(tempCallEnd));
                        int safeCounter = 0;
                        while (dowCalStart != tempDateCall.dayOfWeek && safeCounter < 10) {
                            safeCounter++;
                            tempCallStart.add(Calendar.DATE, 1);
                            dowCalStart = normalizeDoW(tempCallStart);
                        }
//                        System.out.println("updateCalendar-->primo giorno valido per la call :" + calToString(tempCallStart) + " è un giorno " + dowCalStart);
//
//                        System.out.println("updateCalendar-->lastBoxDate    :" + calToString(lastBoxDate));
                        // adesso ho la nuova data inizio da considerare per questa call
                        safeCounter = 0;
                        while ((tempCallStart.before(lastBoxDate) || tempCallStart.equals(lastBoxDate)) && safeCounter < 9999) {//massimo 30 anni
                            safeCounter++;
//                            System.out.println("updateCalendar-->tempCal considerato  :" + calToString(tempCallStart));
                            // devo inserire appuntamento in questa data se rientra nel range tra firstDay e lastDay
                            if ((tempCallStart.after(firstDay) || tempCallStart.equals(firstDay)) && (tempCallStart.before(lastBoxDate) || tempCallStart.equals(lastBoxDate))) {
//                                System.out.println("Aggiungo in unda dei " + framesDrawn + "boxesDorawn, il " + calToString(tempCallStart));
                                for (int i = 1; i <= framesDrawn; i++) {
                                    // creo un box per il mio frame
                                    clendarDateBox myTempBox = new clendarDateBox();
                                    myTempBox.DoW = tempDateCall.DoW;
                                    myTempBox.ID = tempDateCall.ID;
                                    myTempBox.cal = tempDateCall.cal;
                                    myTempBox.caso = tempDateCall.caso;
                                    myTempBox.collision = false;
                                    myTempBox.color = tempDateCall.color;
                                    myTempBox.dateEnd = tempDateCall.dateEnd;
                                    myTempBox.dateStart = tempDateCall.dateStart;
                                    myTempBox.duty = tempDateCall.duty;
                                    myTempBox.interval = tempDateCall.interval;
                                    myTempBox.label = tempDateCall.label;
                                    myTempBox.position = 0;
                                    myTempBox.rifActivity = tempDateCall.rifActivity;
                                    myTempBox.rifCalendar = tempDateCall.rifCalendar;
                                    myTempBox.rifCliente = tempDateCall.rifCliente;
                                    myTempBox.rifIntervento = tempDateCall.rifIntervento;
                                    myTempBox.rifServizio = tempDateCall.rifServizio;
                                    myTempBox.rifTab = tempDateCall.rifTab;
                                    myTempBox.timeEnd = tempDateCall.timeEnd;
                                    myTempBox.timeStart = tempDateCall.timeStart;
                                    myTempBox.totalMins = 0;
                                    myTempBox.type = tempDateCall.type;

                                    String preDuty = "<div id='warning'></div>";

                                    String totalMins = "<div id='timeUsed'></div>";
                                    if (calFrames[i].cal.equals(tempCallStart) && (calFrames[i].cal.before(tempCallEnd)|| calFrames[i].cal.equals(tempCallEnd))) {
                                        myTempBox.duty = "<BR>";
                                        myTempBox.duty += "<TABLE class='calendarCall'><TR>";
                                        myTempBox.duty += "<TH>" + preDuty + tempDateCall.timeStart + " > " + tempDateCall.timeEnd + totalMins + "</TH>";
                                        myTempBox.duty += "</TR><TR> ";
                                        myTempBox.duty += "<TD  bgcolor=\"" + tempDateCall.color + "\">" + tempDateCall.label + "</TD>";
                                        myTempBox.duty += "</TR></TABLE>";
                                        calFrames[i].boxes.add(myTempBox);
                                        myTempBox = null;
                                        break;
                                    }
                                }
                            }
                            // ora salto tante settimane quante sono descritte in pausa
                            tempCallStart.add(Calendar.DATE, 7 + (7 * tempDateCall.interval));
                        }
                    } else {
//                        System.out.println("Questa call non rientra nelle date da mostrare");
                    }

                } else //-------------------------------------
                if (tempDateCall.type.equalsIgnoreCase("DATEfixed")) {
                    for (int i = 1; i <= framesDrawn; i++) {
                        String preDuty = "<div id='warning'></div>";
                        String totalMins = "<div id='timeUsed'></div>";
                        if (calFrames[i].cal.equals(tempCallStart)) {
                            clendarDateBox myTempBox = new clendarDateBox();
                            myTempBox.DoW = tempDateCall.DoW;
                            myTempBox.ID = tempDateCall.ID;
                            myTempBox.cal = tempDateCall.cal;
                            myTempBox.caso = tempDateCall.caso;
                            myTempBox.collision = false;
                            myTempBox.color = tempDateCall.color;
                            myTempBox.dateEnd = tempDateCall.dateEnd;
                            myTempBox.dateStart = tempDateCall.dateStart;
                            myTempBox.duty = tempDateCall.duty;
                            myTempBox.interval = tempDateCall.interval;
                            myTempBox.label = tempDateCall.label;
                            myTempBox.position = 0;
                            myTempBox.rifActivity = tempDateCall.rifActivity;
                            myTempBox.rifCalendar = tempDateCall.rifCalendar;
                            myTempBox.rifCliente = tempDateCall.rifCliente;
                            myTempBox.rifIntervento = tempDateCall.rifIntervento;
                            myTempBox.rifServizio = tempDateCall.rifServizio;
                            myTempBox.rifTab = tempDateCall.rifTab;
                            myTempBox.timeEnd = tempDateCall.timeEnd;
                            myTempBox.timeStart = tempDateCall.timeStart;
                            myTempBox.totalMins = 0;
                            myTempBox.type = tempDateCall.type;
                            myTempBox.rawDuty = tempDateCall.label;
                            myTempBox.duty = "<BR>";
                            myTempBox.duty += "<TABLE class='calendarCall'><TR>";
                            myTempBox.duty += "<TH>" + preDuty + tempDateCall.timeStart + " > " + tempDateCall.timeEnd;
                            myTempBox.duty += totalMins;
                            myTempBox.duty += "</TH>";
                            myTempBox.duty += "</TR><TR> ";
                            myTempBox.duty += "<TD  bgcolor=\"" + tempDateCall.color + "\">" + tempDateCall.label + "</TD>";
                            myTempBox.duty += "</TR></TABLE>";
                            calFrames[i].boxes.add(myTempBox);
                            myTempBox = null;
                            break;
                        }
                    }
                }

            }
        } catch (SQLException ex) {
            System.out.println("Error 346: " + ex.toString());
        }
        try {
            conny.close();
        } catch (SQLException ex) {
            System.out.println("Error 477: " + ex.toString());
        }

//        int cnt = 0;
//        System.out.println("\nEnumero i box del frame 33 dopo la creazione");
//        for (clendarDateBox box : calFrames[33].boxes) {
//            cnt++;
//            System.out.println("Box " + cnt + " -> " + box.duty);
//        }
//        System.out.println("\n**************************************************\n");
    }

    private ArrayList<clendarDateBox> Xwarn(ArrayList<clendarDateBox> Xlist, int frameIndex) {
//        System.out.println("Warn FRAME #" + frameIndex);
        ArrayList<clendarDateBox> list = new ArrayList<>();
        for (int x = 0; x < Xlist.size(); x++) {
            list.add(Xlist.get(x));
        }
        ArrayList<clendarDateBox> warnedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
//            System.out.println("Box n. " + i + "/" + list.size());
            int supers = 0;
            for (int others = 0; others < list.size(); others++) {

                String startTime = list.get(i).timeStart;
                String endTime = list.get(i).timeEnd;

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date dateStart = null;
                try {
                    dateStart = sdf.parse(startTime);
                } catch (ParseException ex) {
                    Logger.getLogger(smartCalendar.class.getName()).log(Level.SEVERE, null, ex);
                }

                Date dateEnd = null;
                try {
                    dateEnd = sdf.parse(endTime);
                } catch (ParseException ex) {
                    Logger.getLogger(smartCalendar.class.getName()).log(Level.SEVERE, null, ex);
                }

                long elapsed2 = dateEnd.getTime() - dateStart.getTime();
                try {
                    list.get(i).totalMins = (int) elapsed2 / 60000;
                } catch (Exception e) {
                }
                if (this.CdrawBoxWorktime > 0) {
                    try {
                        String tempoDaScrivere = minsToHours(list.get(i).totalMins);
                        list.get(i).duty = list.get(i).duty.replace("<div id='timeUsed'></div>", "  <a id='timeUsed' style='font-size:8px;font-style: italic;'>[" + tempoDaScrivere + "]</a>");
                    } catch (Exception e) {
                    }
                }
                if (i != others) {
                    String othersStartTime = list.get(others).timeStart;
                    String othersEndTime = list.get(others).timeEnd;
                    Date dateOthersStart = null;
                    try {
                        dateOthersStart = sdf.parse(othersStartTime);
                    } catch (ParseException ex) {
                        Logger.getLogger(smartCalendar.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Date dateOthersEnd = null;
                    try {
                        dateOthersEnd = sdf.parse(othersEndTime);
                    } catch (ParseException ex) {
                        Logger.getLogger(smartCalendar.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    long elapsed = dateOthersStart.getTime() - dateStart.getTime();
                    long elapsed1 = dateOthersStart.getTime() - dateEnd.getTime();
                    long elapsed3 = dateOthersEnd.getTime() - dateStart.getTime();
                    long elapsed4 = dateOthersEnd.getTime() - dateEnd.getTime();
                    /*
                    c'è sovrapposizione se dateOthersStart >= dateStart (Elapsed >= 0) AND dateOthersStart <= dateEnd (elapsed2 <0)
                    oppure se  dateOthersEnd  >= dateStart  ( Elapsed3 >= 0) AND  dateOthersEnd<=dateEnd (elapsed4 < 0)
                     */
                    list.get(i).setCollision(false);
                    if ((elapsed > 0 && elapsed1 < 0) || (elapsed3 > 0 && elapsed4 < 0)) {
                        list.get(i).setCollision(true);
//                        System.out.println("Box n. " + i + "/" + list.size() + " SET COLLISION.");
//                        System.out.println(" -->" + startTime + "-" + endTime + " vs " + othersStartTime + "-" + othersEndTime + ": il Box è in collisione!!!!!!!!!!");
                        break;
                    }

                }
            }
        }
        for (int x = 0; x < list.size(); x++) { // questo serve solo per un conteggio 
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).position == x) {
                    warnedList.add(list.get(i));
                }
            }

        }
        return warnedList;

    }

    private String minsToHours(int mins) {
        String tempoDaScrivere = "" + mins + " min.";
        int no = 0;
        try {
            no = mins;
            int hours = (int) (no / 60); //since both are ints, you get an int
            int minutes = (int) (no % 60);
            tempoDaScrivere = "";
            if (hours > 0) {
                tempoDaScrivere = hours + "h ";
            }
            if (minutes > 0) {
                tempoDaScrivere += minutes + "m";
            }
        } catch (Exception e) {
        }
        return tempoDaScrivere;
    }

    private ArrayList<clendarDateBox> Xsort(ArrayList<clendarDateBox> Xlist, int frameIndex) {
//        System.out.println("Sort FRAME #" +frameIndex);
        ArrayList<clendarDateBox> list = new ArrayList<>();
        for (int x = 0; x < Xlist.size(); x++) {
            list.add(Xlist.get(x));
        }
        ArrayList<clendarDateBox> sortedList = new ArrayList<>();
//        System.out.println("list.size(). " + list.size());
        for (int i = 0; i < list.size(); i++) {
//            System.out.println("Box n. " + i + "/" + list.size());
            int supers = 0;
            for (int others = 0; others < list.size(); others++) {
                if (i != others) {
                    String startTime = list.get(i).timeStart;
                    String endTime = list.get(i).timeEnd;
                    String othersStartTime = list.get(others).timeStart;
                    String othersEndTime = list.get(others).timeEnd;
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date dateStart = null;
                    try {
                        dateStart = sdf.parse(startTime);
                    } catch (ParseException ex) {
                        Logger.getLogger(smartCalendar.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Date dateOthersStart = null;
                    try {
                        dateOthersStart = sdf.parse(othersStartTime);
                    } catch (ParseException ex) {
                        Logger.getLogger(smartCalendar.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    long elapsed = dateOthersStart.getTime() - dateStart.getTime();
                    if (elapsed < 0) {
                        supers++;
//                        System.out.println(startTime + " : il Box delle " + othersStartTime + " viene prima");
                    }

                }
            }
            list.get(i).setPosition(supers);
//            System.out.println("duty start: " + list.get(i).timeStart + " -> position:" + list.get(i).position + " (" + supers + ")");
        }
        for (int x = 0; x < list.size(); x++) { // questo serve solo per un conteggio 

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).position == x) {
//                    System.out.println(i + "] Aggiungo alla sorted list: " + list.get(i).duty);
                    sortedList.add(list.get(i));
                }
            }

        }

        return sortedList;
    }

    public void step3chargeDuties() {
        for (int f = 1; f <= framesDrawn; f++) {
//            System.out.println("\n---------\nGIORNO:" + calFrames[f].cal.get(Calendar.DAY_OF_MONTH));

//            System.out.println("# il FRAME " + f + "(" + calFrames[f].cal.get(Calendar.DAY_OF_MONTH) + ") contiene " + calFrames[f].boxes.size() + " appuntamenti.");
//---------------------------------------------------------------------------      
            ArrayList<clendarDateBox> warnedBoxes = Xwarn(calFrames[f].boxes, f);
            calFrames[f].boxes = new ArrayList<clendarDateBox>();
            for (int x = 0; x < warnedBoxes.size(); x++) {
                calFrames[f].boxes.add(warnedBoxes.get(x));
            }

//            if (f == 33) {
//                int cnt = 0;
//                System.out.println("\nEnumero i box del frame 33 dopo il warning");
//                for (clendarDateBox box : calFrames[f].boxes) {
//                    cnt++;
//                    System.out.println("Box " + cnt + " -> " + box.duty);
//                }
//            }
//---------------------------------------------------------------------------            
            ArrayList<clendarDateBox> sortedBoxes = Xsort(calFrames[f].boxes, f);
            calFrames[f].boxes = new ArrayList<clendarDateBox>();
            for (int x = 0; x < sortedBoxes.size(); x++) {
                calFrames[f].boxes.add(sortedBoxes.get(x));
            }
//----------------------------------------------------

////////            cnt = 0;
////////            for (clendarDateBox box : calFrames[i].boxes) {
////////                cnt++;
////////                System.out.println("sortedBox " + cnt + " -> " + box.duty);
////////            }
//            calFrames[i].setBoxes(sortedBoxes);
            calFrames[f].duty = "";
            int actualmins = 0;
            for (int b = 0; b < calFrames[f].boxes.size(); b++) {
                clendarDateBox boxduty = calFrames[f].boxes.get(b);
                String actualDuty = boxduty.duty;
                if (boxduty.isCollision() == true) {
                    System.out.println("Frame " + f + " DAY:" + calFrames[f].cal.get(Calendar.DAY_OF_MONTH) + " -> Box " + b + " IN COLLISIONE.");
                    try {
                        actualDuty = boxduty.duty.replace("<div id='warning'></div>", "<a id='warning'><img src='warning.png' alt='Warning!' width='16' height='14'></a>");
                    } catch (Exception e) {
                    }
                }
                calFrames[f].duty += actualDuty;
                actualmins += boxduty.totalMins;
            }
            calFrames[f].totalMinutes = actualmins;
//            System.out.println("calFrames[i].duty--> " + calFrames[i].duty);

        }
    }

    public String step4makeHTML() {
        String htmlCalCode = "";

        htmlCalCode += "<DIV id=\"" + myForm.getID() + "-X-ROWSTABLE\">";
        // <editor-fold defaultstate="collapsed" desc="Costruisco il frame di navigazione">

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
        prevYcode = getButtonAnchorCode(myGate, "" + year, "CalPrevYear", new DBimage("tab_icons", "name", "arrowLeft", "picture", myForm.getMyParams()).getDBimageHtmlCode64(mySettings, "20px", "20px"));
        nextYcode = getButtonAnchorCode(myGate, "" + year, "CalNextYear", new DBimage("tab_icons", "name", "arrowRight", "picture", myForm.getMyParams()).getDBimageHtmlCode64(mySettings, "20px", "20px"));
        Ycode = "<INPUT type=\"hidden\" id=\"" + myForm.getName() + "-" + myForm.getCopyTag() + "-YEAR\" value=\"" + year + "\" readonly/>";
        Ycode += "<p style=\"font-size:20px;\">" + year + "</p>";
        prevMcode = getButtonAnchorCode(myGate, "" + year, "CalPrevMonth", new DBimage("tab_icons", "name", "arrowLeft", "picture", myForm.getMyParams()).getDBimageHtmlCode64(mySettings, "20px", "20px"));
        nextMcode = getButtonAnchorCode(myGate, "" + year, "CalNextMonth", new DBimage("tab_icons", "name", "arrowRight", "picture", myForm.getMyParams()).getDBimageHtmlCode64(mySettings, "20px", "20px"));
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
        // </editor-fold> 

        // <editor-fold defaultstate="collapsed" desc="Apro tabella dei box">        
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
        // </editor-fold> 

        // <editor-fold defaultstate="collapsed" desc="Riempio ogni box con i suoi contenuti">
        int cols = 7;
        for (int i = 1; i <= framesDrawn; i++) {
            col++;
            if (col > cols) {
                col = 1;
                row++;
                htmlCalCode += "</TR><TR>";
            }

            String style1 = "";
            String style2 = "";
            String DayColor = "";

            if (calFrames[i].cal.get(Calendar.MONTH) != calPrimoGiorno.get(Calendar.MONTH)) {
                DayColor = "grey";
                if (calFrames[i].cal.get(Calendar.DAY_OF_WEEK) == 7) {
                    DayColor = "#FF6A00";
                } else if (calFrames[i].cal.get(Calendar.DAY_OF_WEEK) == 1) {
                    DayColor = "#FF0000";
                }
                style1 = " style=\"color:" + DayColor + ";font-size:10px;\" ";
                style2 = " style=\"border-collapse: collapse; border: 1px solid black; background-color:#EFEFEF ; vertical-align: top; text-align: left;\" ";
            } else {
                DayColor = "black";
                if (calFrames[i].cal.get(Calendar.DAY_OF_WEEK) == 7) {
                    DayColor = "#FF6A00";
                } else if (calFrames[i].cal.get(Calendar.DAY_OF_WEEK) == 1) {
                    DayColor = "#FF0000";
                }
                style1 = " style=\"width:20px; color:" + DayColor + ";font-size:12px;background:#EFEFEF;  border-radius: 10px; text-align: center;\" ";
                style2 = " style=\"border-collapse: collapse; border: 1px solid black; vertical-align: top; text-align: left;\" ";
                Calendar today = Calendar.getInstance();;
                if (calFrames[i].cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) && calFrames[i].cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
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
            htmlCalCode += ">" + calFrames[i].cal.get(Calendar.DAY_OF_MONTH) + "</TD>";
            String tempo = "";
            if (this.CdrawDayWorktime > 0) {
                tempo = minsToHours(calFrames[i].totalMinutes);
            }
            htmlCalCode += " <td></td>   <TD style=\"text-align:right;float:right; margin-left:6px;;\">" + tempo + "</TD>";
            htmlCalCode += "    </TR></TABLE>";
            htmlCalCode += "</TD></TR>";
            htmlCalCode += "<TR>";
            htmlCalCode += "<TD>" + calFrames[i].duty + "</TD>";
            htmlCalCode += "</TR>";
            htmlCalCode += "</TABLE>";

            htmlCalCode += "</TD>";
        }

        // </editor-fold> 
        //********************************************************************
        // <editor-fold defaultstate="collapsed" desc="Chiudo tabella dei box">        
        htmlCalCode += "<TR></TABLE>";
        // </editor-fold> 
        htmlCalCode += " </DIV>";

        return htmlCalCode;
    }

    public PdfPTable step5drawPDF() {
        PDFcell cellOne;

        for (int f = 1; f <= framesDrawn; f++) {
            // costruisco le tabelline da inserire nella stampa
            for (int b = 0; b < calFrames[f].boxes.size(); b++) {
                clendarDateBox boxduty = calFrames[f].boxes.get(b);
                PdfPTable myTable = new PdfPTable(9);
                myTable.setWidthPercentage(100);
                String warn = "";
                if (boxduty.collision == true) {
                    warn = "!!!";
                }
                cellOne = new PDFcell();
                cellOne.setPadding(1);
                cellOne.configCell(warn, 6, 1, 0);
                myTable.addCell(cellOne);
                cellOne = new PDFcell();
                cellOne.setPadding(1);
                cellOne.configCell(boxduty.timeStart + " > " + boxduty.timeEnd, 6, 5, 0);
                myTable.addCell(cellOne);
                cellOne = new PDFcell();
                cellOne.setPadding(1);
                String tempoDaScrivere = minsToHours(boxduty.totalMins);
                cellOne.configCell(tempoDaScrivere, 6, 3, 0);
                myTable.addCell(cellOne);
                cellOne = new PDFcell();
                cellOne.setPadding(1);
                cellOne.configCell(boxduty.label, 5, 9, Element.ALIGN_LEFT, BaseColor.BLACK, BaseColor.WHITE, 0, 0, false, "plain");

//                cellOne.configCell(boxduty.label, 6, 9, 0);
                myTable.addCell(cellOne);

                calFrames[f].boxes.get(b).setBoxTable(myTable);
            }
        }
        String[] day = new String[7];
        PdfPTable myTable = new PdfPTable(7);
        myTable.setWidthPercentage(100);
        cellOne = new PDFcell();
        cellOne.setPadding(1);
        String mese = "";
        String anno = "" + this.year;
        switch (this.month) {
            case 1:
                mese = "GENNAIO";
                break;
            case 2:
                mese = "FEBBRAIO";
                break;
            case 3:
                mese = "MARZO";
                break;
            case 4:
                mese = "APRILE";
                break;
            case 5:
                mese = "MAGGIO";
                break;
            case 6:
                mese = "GIUGNO";
                break;
            case 7:
                mese = "LUGLIO";
                break;
            case 8:
                mese = "AGOSTO";
                break;
            case 9:
                mese = "SETTEMBRE";
                break;
            case 10:
                mese = "OTTOBRE";
                break;
            case 11:
                mese = "NOVEMBRE";
                break;
            case 12:
                mese = "DICEMBRE";
                break;

        }
        String intestazioneMeseAnno = "CALENDARIO " + mese + " " + anno;

        cellOne.configCell("", 6, 7, 15);

        day[0] = "LUN";
        day[1] = "MAR";
        day[2] = "MER";
        day[3] = "GIO";
        day[4] = "VEN";
        day[5] = "SAB";
        day[6] = "DOM";

        for (int d = 0; d < 7; d++) {
            cellOne = new PDFcell();
            cellOne.setPadding(1);
            cellOne.configCell(day[d], 6, 1, 15);
            myTable.addCell(cellOne);
        }
// <editor-fold defaultstate="collapsed" desc="Riempio ogni box con i suoi contenuti">

        for (int f = 1; f <= framesDrawn; f++) {
            PdfPTable T1frameTable = new PdfPTable(1);
            T1frameTable.setWidthPercentage(100);
            PDFcell C1FrameTop = new PDFcell();
            PDFcell C1FrameBottom = new PDFcell();

            PdfPTable T2frameTopTable = new PdfPTable(6);
            T2frameTopTable.setWidthPercentage(100);
            PdfPTable T2frameBodyTable = new PdfPTable(1);
            T2frameBodyTable.setWidthPercentage(100);
            cellOne = new PDFcell();
            cellOne.setPadding(1);
//            cellOne.configCell("" + calFrames[f].cal.get(Calendar.DAY_OF_MONTH), 6, 1, 15);
            if (calFrames[f].cal.get(Calendar.MONTH) + 1 == this.month) {
                cellOne.configCell("" + calFrames[f].cal.get(Calendar.DAY_OF_MONTH), 8, 1, Element.ALIGN_CENTER, BaseColor.WHITE, BaseColor.BLACK, 15, 2, false, "BOLD");

            } else {
                cellOne.configCell("" + calFrames[f].cal.get(Calendar.DAY_OF_MONTH), 7, 1, Element.ALIGN_CENTER, BaseColor.WHITE, BaseColor.GRAY, 0, 0, false, "ITALIC");

            }

            T2frameTopTable.addCell(cellOne);
            cellOne = new PDFcell();
            cellOne.setPadding(1);
            cellOne.configCell("", 6, 3, 0);
            T2frameTopTable.addCell(cellOne);
            String tempo = "";
            if (this.CdrawDayWorktime > 0) {
                tempo = minsToHours(calFrames[f].totalMinutes);
            }
            cellOne = new PDFcell();
            cellOne.setPadding(1);
            cellOne.configCell(tempo, 6, 2, 0);
            T2frameTopTable.addCell(cellOne);

            for (int b = 0; b < calFrames[f].boxes.size(); b++) {
                cellOne = new PDFcell();
                cellOne.setPadding(1);
                cellOne.addElement(calFrames[f].boxes.get(b).boxTable);
                T2frameBodyTable.addCell(cellOne);
//                System.out.println("Aggiungo box:" + b);
            }

            C1FrameTop.addElement(T2frameTopTable);
            C1FrameBottom.addElement(T2frameBodyTable);
            T1frameTable.addCell(C1FrameTop);
            T1frameTable.addCell(C1FrameBottom);

            cellOne = new PDFcell();
            cellOne.setPadding(1);
            cellOne.addElement(T1frameTable);
            myTable.addCell(cellOne);
        }

        // </editor-fold> 
        return myTable;
    }

    private String getButtonAnchorCode(gate myGate, String value, String buttonName, String contentCode) {
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

    class clendarFrame {

        ArrayList<clendarDateBox> boxes;
        Calendar cal = Calendar.getInstance();
        int DoW = 0;
        int ID;
        String type;
        int dayOfWeek;
        String timeStart;
        String timeEnd;
        String duty = "";
        String color = "";

        int totalMinutes;

        public clendarFrame() {
            duty = "";
            boxes = new ArrayList<>();
        }

        public void setBoxes(ArrayList<clendarDateBox> boxes) {
            this.boxes = boxes;
        }

    }

    class clendarDateCall {

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
        String color = "";
    }

    class clendarDateBox {

        boolean collision = false;
        Calendar cal = Calendar.getInstance();
        int DoW = 0;
        int position = 0;
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
        String color = "";
        int totalMins = 0;
        String rawDuty = "";

        PdfPTable boxTable;

        public PdfPTable getBoxTable() {
            return boxTable;
        }

        public void setBoxTable(PdfPTable boxTable) {
            this.boxTable = boxTable;
        }

        public clendarDateBox() {
            duty = "";
            collision = false;
        }

        public boolean isCollision() {
            return collision;
        }

        public void setCollision(boolean collision) {
            this.collision = collision;
        }

        public String getDateStart() {
            return dateStart;
        }

        public void setDateStart(String dateStart) {
            this.dateStart = dateStart;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getTimeStart() {
            return timeStart;
        }

        public void setTimeStart(String timeStart) {
            this.timeStart = timeStart;
        }

    }
}
