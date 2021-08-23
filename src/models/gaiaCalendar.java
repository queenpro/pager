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
import REVOpager.EVOpagerDBconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class gaiaCalendar {

    ArrayList<schedule> scheduled;
    EVOpagerParams myParams;
    Settings mySettings;
    Calendar pInizio;
    Calendar pFine;
    ArrayList<Spot> spots;
    ArrayList<Job> jobs;
    int numDays;
    Calendar firstDayInMonth;
    Calendar lastDayInMonth;

    public gaiaCalendar(EVOpagerParams myParamsX, Settings mySettingsX) {
        this.scheduled = new ArrayList<schedule>();
        this.myParams = myParamsX;
        this.mySettings = mySettingsX;
        this.spots = new ArrayList<Spot>();
        this.jobs = new ArrayList<Job>();
    }

    public ArrayList<Job> acquireJobs(String query, ArrayList<Job> foundJobs) {
//SELECT * FROM t_jobs WHERE rifCalendario = 'SFALCIOX92c0aee3'
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");

        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        int JobsParsed = 0;
        String SQLphrase = query;
        System.out.println("\n acquireJobs>>> SQLphrase: " + SQLphrase);
        PreparedStatement ps = null;
        ResultSet rs;

        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                JobsParsed++;

                Job myJob = new Job();
                try {
                    myJob.ID = rs.getInt("ID");
                } catch (Exception ex) {
                }
                try {
                    myJob.rifCalendario = rs.getString("rifCalendario");
                } catch (Exception ex) {
                }
                try {
                    myJob.denominazione = rs.getString("denominazione");
                } catch (Exception ex) {
                }
                try {
                    myJob.dataInizio = rs.getString("dataInizio");
                } catch (Exception ex) {
                }
                try {
                    myJob.oraInizio = rs.getString("oraInizio");
                } catch (Exception ex) {
                }
                try {
                    myJob.dataFine = rs.getString("dataFine");
                } catch (Exception ex) {
                }
                try {
                    myJob.oraFine = rs.getString("oraFine");
                } catch (Exception ex) {
                }
                try {
                    myJob.note = rs.getString("note");
                } catch (Exception ex) {
                }
                try {
                    myJob.rifType = rs.getString("rifType");
                } catch (Exception ex) {
                }

                this.jobs.add(myJob);

            }
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(gaiaCalendar.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("\nnumero finale di jobs passate: " + this.jobs.size());
        if (foundJobs == null) {
            return this.jobs;
        } else {
            for (int jj = 0; jj < this.jobs.size(); jj++) {
                foundJobs.add(this.jobs.get(jj));
            }
            return foundJobs;
        }

    }

    public ArrayList<schedule> acquireScheduled(String query, ArrayList<schedule> foundScheduled) {

        //SELECT * FROM t_sched WHERE rifCalendario = 'SFALCIOX92c0aee3'
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");

        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        int ScheduledParsed = 0;
        String SQLphrase = query;
        System.out.println("\n acquireScheduled>>> SQLphrase: " + SQLphrase);
        PreparedStatement ps = null;
        ResultSet rs;
        int baseFlagProcede = 1;

        Date date;
        baseFlagProcede = 0;

        pInizio = this.firstDayInMonth;
        pFine = Calendar.getInstance();
        pFine.setTime(pInizio.getTime());
        System.out.println("Aggiungo alla data iniziale  " + this.numDays + " giorni.");
        pFine.add(Calendar.DAY_OF_YEAR, this.numDays);
        String dataFineVisualizzazione = sdf.format(pFine.getTime());
        String dataInizioVisualizzazione = formatDate.format(pInizio.getTime());
        System.out.println("\ndataInizioVisualizzazione: " + dataInizioVisualizzazione);
        System.out.println("\ndataFineVisualizzazione: " + dataFineVisualizzazione);
        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                ScheduledParsed++;

                schedule mySchedule = new schedule();
                try {
                    mySchedule.ID = rs.getInt("ID");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.rifJob = rs.getInt("rifJob");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.rifOperatore = rs.getString("rifOperatore");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.rifContesto = rs.getString("rifContesto");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.rifCalendario = rs.getString("rifCalendario");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.tipo = rs.getString("tipo");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.dataInizio = rs.getString("dataInizio");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.oraInizio = rs.getString("oraInizio");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.dataFine = rs.getString("dataFine");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.oraFine = rs.getString("oraFine");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.offsetDays = rs.getInt("offsetDays");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.days = rs.getString("days");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.months = rs.getString("months");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.margineDays = rs.getInt("margineDays");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.data = rs.getString("data");
                } catch (Exception ex) {
                }
                try {
                    mySchedule.status = rs.getInt("status");
                } catch (Exception ex) {
                }
                int flagProcede = 1;

                Calendar xInizio = Calendar.getInstance();
                Calendar xFine = Calendar.getInstance();
                try {
                    if (mySchedule.dataInizio != null) {
                        date = sdf.parse(mySchedule.dataInizio);
                        xInizio.setTime(date);
                        mySchedule.startDate.setTime(date);
                        flagProcede = 0;
                    }
                } catch (ParseException ex) {
                    flagProcede = 1;
                }
                try {
                    if (mySchedule.dataFine != null) {
                        date = sdf.parse(mySchedule.dataFine);
                        xFine.setTime(date);
                        mySchedule.endDate.setTime(date);
                        flagProcede = 0;
                    }
                } catch (ParseException ex) {
                    flagProcede = 1;
                }

                System.out.println("La schedulazione ID = " + mySchedule.ID + " va dal " + mySchedule.dataInizio + " al " + mySchedule.dataFine);

                if (flagProcede == 0 && baseFlagProcede == 0) {
                    // ci sono date da verificare... le verifico
                    // se lo schedule è nel range della mia visualizzazione allora lo registro

                }
                DateUtils du = new DateUtils();
                boolean inRangeInizio = false;
                boolean inRangeFine = false;
                boolean RangeIniziato = du.isBeforeDay(mySchedule.getStartDate(), pInizio);
                boolean RangeNonFinito = du.isAfterDay(mySchedule.getEndDate(), pInizio);
                boolean RangeInizia = du.isSameDay(mySchedule.getStartDate(), pInizio);
                boolean RangeFinisce = du.isSameDay(mySchedule.getEndDate(), pInizio);

                if ((RangeInizia || RangeIniziato) && (RangeFinisce || RangeNonFinito)) {
                    inRangeInizio = true;
                }

                RangeIniziato = du.isBeforeDay(mySchedule.getStartDate(), pFine);
                RangeNonFinito = du.isAfterDay(mySchedule.getEndDate(), pFine);
                RangeInizia = du.isSameDay(mySchedule.getStartDate(), pFine);
                RangeFinisce = du.isSameDay(mySchedule.getEndDate(), pFine);

                if ((RangeInizia || RangeIniziato) && (RangeFinisce || RangeNonFinito)) {
                    inRangeFine = true;
                }

                System.out.println("\nAggiunta schedulazione ID: = " + mySchedule.ID);
                if (inRangeInizio == true || inRangeFine == true) {
                    this.scheduled.add(mySchedule);
                }
            }
            System.out.println("ScheduledParsed: = " + ScheduledParsed);

            // ora per ogni schedulazione cerco il job precedente
            for (int jj = 0; jj < scheduled.size(); jj++) {
                scheduled.get(jj).setLastJobDataInizio(null);
                scheduled.get(jj).setLastJobDataFine(null);
                scheduled.get(jj).setLastJobID(0);

                SQLphrase = "SELECT * FROM t_jobs WHERE  rifCalendario  = '" + scheduled.get(jj).rifCalendario + "' ORDER BY  dataFine DESC";
                System.out.println("SQLphrase  = " + SQLphrase);
                ps = conny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();
                while (rs.next()) {
                    scheduled.get(jj).setLastJobDataInizio(rs.getString("dataInizio"));
                    scheduled.get(jj).setLastJobDataFine(rs.getString("dataFine"));
                    scheduled.get(jj).setLastJobOraInizio(rs.getString("oraInizio"));
                    scheduled.get(jj).setLastJobOraFine(rs.getString("oraFine"));
                    scheduled.get(jj).setLastJobID(rs.getInt("ID"));
                    scheduled.get(jj).setLastJobEsaustivo(rs.getInt("esaustivo"));
                    System.out.println("ULTIMO LAVORO PER SCHEDULAZIONE : dataFine  = " + scheduled.get(jj).getLastJobDataFine());
                    break;
                }
                String datetimeLastJob = scheduled.get(jj).getLastJobDataFine();
                if (scheduled.get(jj).getLastJobOraFine() == null || scheduled.get(jj).getLastJobOraFine().length() < 8) {
                    scheduled.get(jj).setLastJobOraFine("00:00:00");
                }
                datetimeLastJob += " " + scheduled.get(jj).getLastJobOraFine();
                System.out.println("datetimeFine  = " + datetimeLastJob);

                SQLphrase = "UPDATE `t_sched` SET SRVC_lastJobDayTime = '" + datetimeLastJob + "' WHERE ID = " + scheduled.get(jj).getID();
                System.out.println("SQLphrase  = " + SQLphrase);
                ps = conny.prepareStatement(SQLphrase);
                int i = ps.executeUpdate();

                // ho aggiunto a scheduled i valori dell'ultimo lavoro svolto
                // adesso calcolo quando dovrà essere eseguito il prossimo in base alla schedulazione
                if (scheduled.get(jj).getTipo().equalsIgnoreCase("OFFSET") && scheduled.get(jj).getLastJobDataFine() != null) {
                    // metterò un nuovo appuntamento al giorno risultante dall'aggiunta dell'offset

                    SimpleDateFormat formatDateDB = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar lastJ = Calendar.getInstance();
                    Calendar nextJ = Calendar.getInstance();
                    DateFormat format = new SimpleDateFormat("yy/mm/dd hh:mm:ss", Locale.ITALIAN);
                    Date dateX = null;
                    try {
                        dateX = formatDateDB.parse(scheduled.get(jj).getLastJobDataFine());
                        lastJ.setTime(dateX);
                        nextJ.setTime(dateX);
                        nextJ.add(Calendar.DATE, scheduled.get(jj).offsetDays);
                        scheduled.get(jj).setNextJobDataInizio(formatDateDB.format(nextJ.getTime()));
                    } catch (ParseException ex) {
                        Logger.getLogger(gaiaCalendar.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String datetimeNextJob = scheduled.get(jj).getNextJobDataInizio();
                if (scheduled.get(jj).getLastJobOraInizio() == null || scheduled.get(jj).getLastJobOraInizio().length() < 8) {
                    scheduled.get(jj).setLastJobOraInizio ("00:00:00");
                }
                    datetimeNextJob+= " " + scheduled.get(jj).getLastJobOraInizio(); 
                    SQLphrase = "UPDATE `t_sched` SET SRVC_nextJobDayTime = '" + datetimeNextJob + "' WHERE ID = " + scheduled.get(jj).getID();
                    System.out.println("SQLphrase  = " + SQLphrase);
                    ps = conny.prepareStatement(SQLphrase);
                    i = ps.executeUpdate();

                }

            }

            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(gaiaCalendar.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (foundScheduled == null) {
            //aggiungevo schedulazioni da zero
            System.out.println("\nnumero finale di schedulazioni passate: " + this.scheduled.size());
            return this.scheduled;
        } else {
            //aggiungevo schedulazioni dopo aver già cercato altre di altro tipo
            for (int jj = 0; jj < this.scheduled.size(); jj++) {
                foundScheduled.add(this.scheduled.get(jj));
            }

            return foundScheduled;
        }
    }

    public void prepareDates(Calendar targetDay) {

        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formatDay = new SimpleDateFormat("dd");
        SimpleDateFormat formatMonth = new SimpleDateFormat("MM");

        String curDate = formatDate.format(targetDay.getTime());
        String firstDayThisWeekDate = formatDate.format(getFirstDayThisWeek(targetDay).getTime());
        String DoM = formatDay.format(getFirstDayThisWeek(targetDay).getTime());
        String MoY = formatMonth.format(targetDay.getTime());

        String targetDoM = curDate.substring(0, 2);
        String targetMoY = curDate.substring(3, 5);
        String targetYear = curDate.substring(6, 10);
        String firstDay = "01-" + targetMoY + "-" + targetYear;

        firstDayInMonth = Calendar.getInstance();
        lastDayInMonth = Calendar.getInstance();
        Date dateStart = null;
        try {
            dateStart = formatDate.parse(firstDay);
        } catch (ParseException ex) {
            Logger.getLogger(gaiaCalendar.class.getName()).log(Level.SEVERE, null, ex);
        }
        firstDayInMonth.setTime(dateStart);
        lastDayInMonth.setTime(dateStart);
        lastDayInMonth.add(Calendar.MONTH, 1);
        lastDayInMonth.add(Calendar.DAY_OF_MONTH, -1);

        String lastDay = formatDate.format(lastDayInMonth.getTime());
        int daysBetween = (int) ChronoUnit.DAYS.between(firstDayInMonth.toInstant(), lastDayInMonth.toInstant());

        String myDoW = getDoW(lastDayInMonth, "NUMBER");
        int myD = Integer.parseInt(myDoW);
        int aggiunta = 0;
        if (myD < 7) {
            aggiunta = 7 - myD;
        }
        numDays = daysBetween + aggiunta + 1;
    }

    public String getHtmlCalendar(String view) {
//  public String getHtmlCalendar(gaiaCalendar myCalendar, String view, String query, Calendar targetDay) {
        System.out.println("COSTRUITI " + spots.size() + " SPOTS");
        /*
        DATO UN GIORNO, lo inquadra in un calendario MENSILE e mostra gli spot di tutto il mese
         */
        String htmlCode = "";

        //     acquireJobs(query, null, targetDay, numDays);
        //acquisisco le schedulazioni che in qualche modo riguardano il periodo che mi interessa
        //        acquireScheduled(query, null  ); //solo eventi futuri
////////        System.out.println("curDate = " + curDate);
////////        System.out.println("curDoW = " + getDoW(targetDay, "SHORT"));
////////        System.out.println("dayString = " + getDoW(targetDay, "LONG"));
////////        System.out.println("dayNumber = " + getDoW(targetDay, "NUMBER"));
////////        System.out.println("firstDayThisWeek = " + firstDayThisWeekDate);
////////        System.out.println("targetDoM = " + targetDoM);
////////        System.out.println("targetMoY = " + targetMoY);
////////        System.out.println("targetYear = " + targetYear);
////////        System.out.println("firstDay = " + firstDay);
////////        System.out.println("lastDay = " + lastDay);
////////        System.out.println("daysBetween = " + daysBetween);
////////        System.out.println("numDays = " + numDays);
////////        System.out.println("\nquery: = " + query);
////////        System.out.println("N. schedulazioni: = " + this.getScheduled().size());
        int dayCols = 7;
        Calendar calDay = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        htmlCode += "<TABLE border = \"1\" style=\"width:710px;\">";
        htmlCode += "<TR><TD>L</TD><TD>M</TD><TD>M</TD><TD>G</TD><TD>V</TD><TD>S</TD><TD>D</TD></TR>";

        // for (int jj = 0; jj < myCalendar.getScheduled().size(); jj++) {
        calDay.setTime(firstDayInMonth.getTime());
        // System.out.println("SCHED: = " + myCalendar.getScheduled().get(jj).getDataInizio());
        htmlCode += "<TR>";
        int curcol = 0;
        for (int dd = 0; dd < numDays; dd++) {
            curcol++;
            if (curcol > dayCols) {
                curcol = 1;
                htmlCode += "</TR><TR>";
            }

            int toAdd = 0;
            if (dd > 0) {
                toAdd = 1;
            }
            calDay.add(Calendar.DAY_OF_YEAR, toAdd);
            // per ogni giorno nel calendario
            htmlCode += "<TD>";
            htmlCode += getSpotsCode(calDay, today);
            htmlCode += "</TD>";
        }
        htmlCode += "</TR>";
        //  }

        htmlCode += "</TABLE>";

        return htmlCode;
    }

    public ArrayList<schedule> getScheduled() {
        return scheduled;
    }

    public void setScheduled(ArrayList<schedule> scheduled) {
        this.scheduled = scheduled;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public void setJobs(ArrayList<Job> jobs) {
        this.jobs = jobs;
    }

    private String getDoW(String strDate, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        Calendar cal = Calendar.getInstance();
        try {
            date = sdf.parse(strDate);
            cal.setTime(date);
        } catch (ParseException ex) {
            Logger.getLogger(gaiaCalendar.class.getName()).log(Level.SEVERE, null, ex);
        }
        String dow;
        dow = getDoW(cal, type);
        return dow;
    }

    private String getDoW(Calendar cal, String type) {
        SimpleDateFormat formatDoW = new SimpleDateFormat("u");

        String curDoW = formatDoW.format(cal.getTime());
        String dayString = "X";
        String shortDayString = "X";

        String dow;
        if (type != null && type.equalsIgnoreCase("NUMBER")) {
            dow = curDoW;
        } else if (type != null && type.equalsIgnoreCase("SHORT")) {
            switch (curDoW) {
                case "1":
                    shortDayString = "Lu";
                    break;
                case "2":
                    shortDayString = "Ma";
                    break;
                case "3":
                    shortDayString = "Me";
                    break;
                case "4":
                    shortDayString = "Gi";
                    break;
                case "5":
                    shortDayString = "Ve";
                    break;
                case "6":
                    shortDayString = "Sa";
                    break;
                case "7":
                    shortDayString = "Do";
                    break;

            }
            dow = shortDayString;
        } else {
            switch (curDoW) {
                case "1":
                    dayString = "Lunedì";
                    break;
                case "2":
                    dayString = "Martedì";
                    break;
                case "3":
                    dayString = "Mercoledì";
                    break;
                case "4":
                    dayString = "Giovedì";
                    break;
                case "5":
                    dayString = "Venerdì";
                    break;
                case "6":
                    dayString = "Sabato";
                    break;
                case "7":
                    dayString = "Domenica";
                    break;

            }
            dow = dayString;
        }
        return dow;
    }

    private String getMonthName(Calendar cal, String type) {
        SimpleDateFormat formatMonth = new SimpleDateFormat("MM");

        String curDoW = formatMonth.format(cal.getTime());
        String dayString = "X";
        String shortDayString = "X";

        String monthName = "";
        if (type != null && type.equalsIgnoreCase("NUMBER")) {
            monthName = curDoW;
        } else if (type != null && type.equalsIgnoreCase("SHORT")) {
            switch (curDoW) {
                case "1":
                    monthName = "Gen";
                    break;
                case "2":
                    monthName = "Feb";
                    break;
                case "3":
                    monthName = "Mar";
                    break;
                case "4":
                    monthName = "Apr";
                    break;
                case "5":
                    monthName = "Mag";
                    break;
                case "6":
                    monthName = "Giu";
                    break;
                case "7":
                    monthName = "Lug";
                    break;
                case "8":
                    monthName = "Ago";
                    break;
                case "9":
                    monthName = "Set";
                    break;
                case "10":
                    monthName = "Ott";
                    break;
                case "11":
                    monthName = "Nov";
                    break;
                case "12":
                    monthName = "Dic";
                    break;
            }

        } else {
            switch (curDoW) {
                case "1":
                    monthName = "Gennaio";
                    break;
                case "2":
                    monthName = "Febbraio";
                    break;
                case "3":
                    monthName = "Marzo";
                    break;
                case "4":
                    monthName = "Aprile";
                    break;
                case "5":
                    monthName = "Maggio";
                    break;
                case "6":
                    monthName = "Giugno";
                    break;
                case "7":
                    monthName = "Luglio";
                    break;
                case "8":
                    monthName = "Agosto";
                    break;
                case "9":
                    monthName = "Settembre";
                    break;
                case "10":
                    monthName = "Ottobre";
                    break;
                case "11":
                    monthName = "Novembre";
                    break;
                case "12":
                    monthName = "Dicembre";
                    break;
            }

        }
        return monthName;
    }

    private Calendar getFirstDayThisWeek(Calendar today) {
        int curdow = Integer.parseInt(getDoW(today, "NUMBER"));
        int diff = 1 - curdow;
        Calendar firstDayThisWeek = Calendar.getInstance();
        firstDayThisWeek.add(Calendar.DAY_OF_YEAR, diff);
        return firstDayThisWeek;
    }

    private ArrayList<Spot> getSpots(Calendar day, Calendar today) {
        // analizza sia le schedulazioni , sia i jobs registrati
        // crea lo spot del giorno in base alla somma di jjobs e sched

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat DBf = new SimpleDateFormat("yyyy-MM-dd");
        // parsing delle schedulazioni:

        //TIPO A:1.CURDAY == TODAY
        //       2.se una schedulazione è nel range verifico se ha jobs precedenti con scarto inferiore all'intervallo
        //       3. se c'è il job e sono in tolleranza metto lo spot oggi
        //       4. se c'è il job ma sono fuori tolleranza creo il job come NON ESEGUITO e da quella data valuto un nuovo job
        //       5. se non c'è job in intervallo creo lo spot per oggi
        //TIPO B:1.la schedulazione è nel raange del giorno CURDAY
        //       2.passati i giorni dell'intervallo la ricorrenza cade nel CURDAY
        DateUtils du = new DateUtils();
        System.out.println("CONFRONTO TRA  " + sdf.format(day.getTime()) + " e " + sdf.format(today.getTime()));

        boolean compilingToday = du.isSameDay(day, today);
//==============================se compilo il giorno di OGGI
        if (compilingToday == true) {
            System.out.println("\n=*TODAY===\n");
            for (int sch = 0; sch < scheduled.size(); sch++) {
                Spot mySpot = new Spot(day);
                //1. per essere nel range SchedDataInizio <= CURDATE e schedDataFIne >=curDate

                boolean inRange = false;
                boolean RangeIniziato = du.isBeforeDay(scheduled.get(sch).getStartDate(), day);
                boolean RangeNonFinito = du.isAfterDay(scheduled.get(sch).getEndDate(), day);
                boolean RangeInizia = du.isSameDay(scheduled.get(sch).getStartDate(), day);
                boolean RangeFinisce = du.isSameDay(scheduled.get(sch).getEndDate(), day);
//==============================se il giorno curdate è nel RANGE della schedulazione
                if ((RangeInizia || RangeIniziato) && (RangeFinisce || RangeNonFinito)) {
                    inRange = true;
                    System.out.println("\n=TODAY===\n");
                    System.out.println("DATA: = " + DBf.format(day.getTime()));
                    System.out.println("ID: = " + (scheduled.get(sch).getID()));
                    System.out.println("RangeIniziato: = " + RangeIniziato);
                    System.out.println("RangeNonFinito: = " + RangeNonFinito);
                    System.out.println("RangeInizia: = " + RangeInizia);
                    System.out.println("RangeFinisce: = " + RangeFinisce);
                    System.out.println("compilingToday: = " + compilingToday);
                    System.out.println("inRange = " + inRange);
                    // cerco il job porecedente
                    Calendar lastJobEnd = Calendar.getInstance();

                    Date date = null;
                    boolean lastJobPresent = false;
                    try {
                        if (scheduled.get(sch).getLastJobDataFine() != null && scheduled.get(sch).getLastJobID() > 0) {
                            date = DBf.parse(scheduled.get(sch).getLastJobDataFine());
                            lastJobEnd.setTime(date);
                            lastJobPresent = true;
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(gaiaCalendar.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("lastJobPresent = " + lastJobPresent);

                    if (lastJobPresent == true) {
                        System.out.println("getLastJobDataFine = " + scheduled.get(sch).getLastJobDataFine());
                        // A:se è passato meno dell'offset non faccio niente
                        // B:se è passato l'offset:
                        //     B1:se sono in tolleranza lo metto in spot oggi
                        //     B2:se sono fuori tolleranza creo un job NON ESEGUITO in data  (?)
                        long daysFromLastJob = ChronoUnit.DAYS.between(lastJobEnd.toInstant(), day.toInstant());

                        System.out.println("TRA  " + sdf.format(lastJobEnd.getTime()) + " e " + sdf.format(day.getTime()) + " passano " + daysFromLastJob + " giorni");
                        System.out.println("daysFromLastJob = " + daysFromLastJob);
                        System.out.println("OffsetDays = " + scheduled.get(sch).getOffsetDays());
                        System.out.println("Tolleranza = " + scheduled.get(sch).getMargineDays());

                        if (daysFromLastJob < scheduled.get(sch).getOffsetDays()) {
                            System.out.println("Non è trascorso l'offset dall'ultimo job. ");
                        } else {
                            mySpot.rifCalendario = scheduled.get(sch).getRifCalendario();
                            mySpot.date.setTime(day.getTime());
                            mySpot.rifSchedulazione = scheduled.get(sch).getID();
                            mySpot.offsetDays = scheduled.get(sch).getOffsetDays();
                            if (daysFromLastJob <= scheduled.get(sch).getMargineDays()) {

                                mySpot.htmlCode = "<TABLE><TR><TD>SCHEDULED:</TD></TR></TABLE>";

                            } else {

                                mySpot.htmlCode = "<TABLE><TR><TD>--</TD></TR></TABLE>";

                            }
                            spots.add(mySpot);
                        }

                    } else {
                        // lo metto in spot OGGI
                        System.out.println("Non ci sono jobs per questo servizio nel range. ");
                        mySpot.rifCalendario = scheduled.get(sch).getRifCalendario();
                        mySpot.date.setTime(day.getTime());
                        mySpot.htmlCode = "<TABLE><TR><TD style=\"color:Grey;\">RITARDO!</TD></TR></TABLE>";
                        spots.add(mySpot);
                    }

                }
                //=)=======================================================
                // parsing dei jobs:
                // se un job è in data CURDAY lo scrivo sul pannello (creo lo spot)
            }
        } else {
//==============================se compilo un giorno diverso da OGGI
// nei giorni passati non metto schedulazioni
// nei giorni futuri metto schedulazioni che hanno il termmine offset nel giorno specifico oppure hanno data fissata nel giorno specifico
            System.out.println("\n=*NOT TODAY====\n");
            for (int sch = 0; sch < scheduled.size(); sch++) {
                Spot mySpot = new Spot(day);
                //1. per essere nel range SchedDataInizio <= CURDATE e schedDataFIne >=curDate

                boolean inRange = false;
                boolean RangeIniziato = du.isBeforeDay(scheduled.get(sch).getStartDate(), day);
                boolean RangeNonFinito = du.isAfterDay(scheduled.get(sch).getEndDate(), day);
                boolean RangeInizia = du.isSameDay(scheduled.get(sch).getStartDate(), day);
                boolean RangeFinisce = du.isSameDay(scheduled.get(sch).getEndDate(), day);
//==============================se il giorno curdate è nel RANGE della schedulazione
                if ((RangeInizia || RangeIniziato) && (RangeFinisce || RangeNonFinito)) {
                    inRange = true;
                    System.out.println("\n=NOT TODAY====\n");
                    System.out.println("DATA: = " + DBf.format(day.getTime()));
                    System.out.println("ID: = " + (scheduled.get(sch).getID()));
                    System.out.println("RangeIniziato: = " + RangeIniziato);
                    System.out.println("RangeNonFinito: = " + RangeNonFinito);
                    System.out.println("RangeInizia: = " + RangeInizia);
                    System.out.println("RangeFinisce: = " + RangeFinisce);
                    System.out.println("compilingToday: = " + compilingToday);
                    System.out.println("inRange = " + inRange);
                    // cerco il job porecedente
                    Calendar lastJobEnd = Calendar.getInstance();

                    Date date = null;
                    boolean lastJobPresent = false;
                    try {
                        if (scheduled.get(sch).getLastJobDataFine() != null && scheduled.get(sch).getLastJobID() > 0) {
                            date = DBf.parse(scheduled.get(sch).getLastJobDataFine());
                            lastJobEnd.setTime(date);
                            lastJobPresent = true;
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(gaiaCalendar.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("lastJobPresent = " + lastJobPresent);

                    if (lastJobPresent == true) {
                        System.out.println("getLastJobDataFine = " + scheduled.get(sch).getLastJobDataFine());
                        // A:se è passato meno dell'offset non faccio niente
                        // B:se è passato l'offset:
                        //     B1:se sono in tolleranza lo metto in spot oggi
                        //     B2:se sono fuori tolleranza creo un job NON ESEGUITO in data  (?)
                        long daysFromLastJob = ChronoUnit.DAYS.between(lastJobEnd.toInstant(), day.toInstant());

                        if (daysFromLastJob == scheduled.get(sch).getOffsetDays()) {
                            System.out.println("\n=GIORNO PROSSIMO OFFSET====\n");
                            mySpot.rifCalendario = scheduled.get(sch).getRifCalendario();
                            mySpot.date.setTime(day.getTime());
                            mySpot.htmlCode = "<TABLE><TR><TD>PROSSIMA SCAD1</TD></TR></TABLE>";
                            spots.add(mySpot);

                        }

                    }
                    //analizzo anche gli altri spot per questo stesso calendario e 
                    // se uno spot è presente e day =offset , aggiungo uno spot di schedulazione

                    for (int jj = 0; jj < spots.size(); jj++) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(spots.get(jj).date.getTime());
                        cal.add(Calendar.DAY_OF_YEAR, spots.get(jj).offsetDays);
                        boolean isNextOffset = false;
                        isNextOffset = du.isSameDay(cal, day);
                        System.out.println("trovato Spot affine = " + sdf.format(cal.getTime()));
                        System.out.println("confronto con il day = " + sdf.format(day.getTime()));

                        if (isNextOffset == true) {
                            mySpot.rifCalendario = scheduled.get(sch).getRifCalendario();
                            mySpot.date.setTime(day.getTime());
                            mySpot.htmlCode = "<TABLE><TR><TD>PROSSIMA SCAD2</TD></TR></TABLE>";
                            spots.add(mySpot);
                            break;
                        }

                    }

                }
            }
        }
        // adesso aggiungo uno spot per ogni JOB già eseguito in merito a questo calendario
        for (int jbs = 0; jbs < this.jobs.size(); jbs++) {
            Date dateINIZIO = null;
            try {
                dateINIZIO = DBf.parse(this.jobs.get(jbs).dataInizio);
            } catch (ParseException ex) {
                Logger.getLogger(gaiaCalendar.class.getName()).log(Level.SEVERE, null, ex);
            }
            Date dateFINE = null;
            try {
                dateFINE = DBf.parse(this.jobs.get(jbs).dataFine);
            } catch (ParseException ex) {
                Logger.getLogger(gaiaCalendar.class.getName()).log(Level.SEVERE, null, ex);
            }
            Calendar calINIZIO = Calendar.getInstance();
            Calendar calFINE = Calendar.getInstance();
            calINIZIO.setTime(dateINIZIO);
            calFINE.setTime(dateFINE);

            boolean dopoInizio = du.isBeforeDay(calINIZIO, day);
            boolean primaDiFine = du.isAfterDay(calFINE, day);
            boolean dayInizio = du.isSameDay(calINIZIO, day);
            boolean dayFine = du.isSameDay(calFINE, day);
            if ((dopoInizio == true || dayInizio == true) && (primaDiFine == true || dayFine == true)) {
// nel giorno in questione questo job era in svolgimento
// quindi creo uno spot
                Spot mySpot = new Spot(day);
                mySpot.setRifCalendario(this.jobs.get(jbs).getRifCalendario());
                mySpot.date.setTime(day.getTime());
                mySpot.htmlCode = "<TABLE><TR><TD>JOB:" + this.jobs.get(jbs).getDenominazione() + "</TD></TR></TABLE>";
                spots.add(mySpot);
            }

        }

        return spots;
    }

    private String getSpotsCode(Calendar calDay, Calendar today) {
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatDateDB = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formatDay = new SimpleDateFormat("dd");
        SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
        DateUtils du = new DateUtils();

        boolean sameDay = du.isSameDay(today, calDay);
        getSpots(calDay, today); //costruisce tutti gli spots per questo giorno

        String windowStyle = "";
        String DoW = getDoW(calDay, "NUMBER");
        System.out.println(formatDate.format(calDay.getTime()) + " -> DoW = " + DoW);
        if (sameDay == true) {
            windowStyle = "background:yellow;";
        } else if (DoW.equals("6")) {
            windowStyle = "background:#777C7F;";
        } else if (DoW.equals("7")) {
            windowStyle = "background:#997A7A;";
        } else {
            windowStyle = "";
        }
        String htmlCode = "<TABLE  style=\"" + windowStyle + " height:100px; overflow:auto;\">";
        htmlCode += "<TR style=\"  height: 21px;  vertical-align:top;  \">"
                + "<TD  style=\"  width: 100px; height: 21px; vertical-align:top;  \">";
        // htmlCode += getDoW(calDay, "SHORT") + " " + formatDate.format(calDay.getTime());
        htmlCode += "<DIV>" + getMonthName(calDay, "") + "</DIV>";
        htmlCode += "</TD></TR>";

        htmlCode += "<TR style=\"  height: 21px;  vertical-align:top;  \">"
                + "<TD  style=\"  width: 100px; height: 21px; vertical-align:top;  \">";
        // htmlCode += getDoW(calDay, "SHORT") + " " + formatDate.format(calDay.getTime());
        htmlCode += "<DIV style=\" top:0;left:0; border-radius: 10px; background: #73AD21; text-align: center;"
                + "  border: 1px solid #73AD21;  padding: 0px; vertical-align:top; "
                + "  width: 20px; height: 20px; \">" + formatDay.format(calDay.getTime()) + "</DIV>";
        htmlCode += "</TD></TR>";
        String spotsHtml = "";
        int nofSpots = 0;
        for (int jj = 0; jj < spots.size(); jj++) {
            boolean belongs = false;
            du = new DateUtils();
            belongs = du.isSameDay(spots.get(jj).getDate(), calDay);
            if (belongs == true) {
                nofSpots++;
                spotsHtml += "<TR><TD>";
                spotsHtml += spots.get(jj).htmlCode;
                spotsHtml += "</TD></TR>";
            }
        }

        htmlCode += spotsHtml;
        htmlCode += "</TABLE>";
        return htmlCode;
    }

    public class Job {

        int ID;
        String note;
        String oraInizio;
        String oraFine;
        String dataFine;
        String dataInizio;
        String rifType;
        String rifCalendario;

        String denominazione;

        public String getDenominazione() {
            return denominazione;
        }

        public void setDenominazione(String denominazione) {
            this.denominazione = denominazione;
        }

        public String getRifCalendario() {
            return rifCalendario;
        }

        public void setRifCalendario(String rifCalendario) {
            this.rifCalendario = rifCalendario;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getDataFine() {
            return dataFine;
        }

        public void setDataFine(String dataFine) {
            this.dataFine = dataFine;
        }

        public String getDataInizio() {
            return dataInizio;
        }

        public void setDataInizio(String dataInizio) {
            this.dataInizio = dataInizio;
        }

        public String getRifType() {
            return rifType;
        }

        public void setRifType(String rifType) {
            this.rifType = rifType;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getOraInizio() {
            return oraInizio;
        }

        public void setOraInizio(String oraInizio) {
            this.oraInizio = oraInizio;
        }

        public String getOraFine() {
            return oraFine;
        }

        public void setOraFine(String oraFine) {
            this.oraFine = oraFine;
        }

    }

    public class Spot {

        Calendar date;
        String time;
        String htmlCode;
        String rifCalendario;
        int rifSchedulazione;
        int offsetDays;
        String denominazione;
        String type;
        String iconTab;
        String iconField;
        String iconID;

        public Spot(Calendar dateX) {
            date = Calendar.getInstance();
            this.date.setTime(dateX.getTime());
            htmlCode = "";
            time = "";
            rifCalendario = "";
            rifSchedulazione = 0;
            offsetDays = 0;
        }

        public String getDenominazione() {
            return denominazione;
        }

        public void setDenominazione(String denominazione) {
            this.denominazione = denominazione;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIconTab() {
            return iconTab;
        }

        public void setIconTab(String iconTab) {
            this.iconTab = iconTab;
        }

        public String getIconField() {
            return iconField;
        }

        public void setIconField(String iconField) {
            this.iconField = iconField;
        }

        public String getIconID() {
            return iconID;
        }

        public void setIconID(String iconID) {
            this.iconID = iconID;
        }

        public int getRifSchedulazione() {
            return rifSchedulazione;
        }

        public void setRifSchedulazione(int rifSchedulazione) {
            this.rifSchedulazione = rifSchedulazione;
        }

        public int getOffsetDays() {
            return offsetDays;
        }

        public void setOffsetDays(int offsetDays) {
            this.offsetDays = offsetDays;
        }

        public Calendar getDate() {
            return date;
        }

        public void setDate(Calendar date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getHtmlCode() {
            return htmlCode;
        }

        public void setHtmlCode(String htmlCode) {
            this.htmlCode = htmlCode;
        }

        public String getRifCalendario() {
            return rifCalendario;
        }

        public void setRifCalendario(String rifCalendario) {
            this.rifCalendario = rifCalendario;
        }

    }
}
