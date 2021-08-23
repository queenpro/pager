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

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class schedule {

    /*
    es type OFFSET ---------------------------------------   
    dal 1/5/19 al 1/10/19 ogni 30 giorni dall'evento precedente
    type: OFFSET
    offsetDays: 30
    margineDays: 25 (se entro 25 giorni non viene eseguito si valuta come NON ESEGUITO e ricorre la scadenza successiva:
                    viene creato un job NON ESEGUITO in data corretta (es dopo 30 giorni dall'esecuzione del 16/6, quindi il 16/7) 
                    e si inizia di nuovo il conto dal 16/7 schedulando per il 16/8
    es type FIXEDDATE-----------------------------------------
    dal 15/5/19 al 15/5/19
    offsetDays;0
    margineDays: 7 (entro la settimana)
    
    
     */
    int ID;

    int rifJob;
    String rifOperatore;
    String rifContesto;
    String rifCalendario;//in team si riferisce al servizio

    String tipo;
    String dataInizio;
    String oraInizio;
    String dataFine;
    String oraFine;
    int offsetDays;
    String days;
    String months;
    String years;
    int margineDays;
    String data;
    int status;

    Calendar startDate;
    Calendar endDate;

    String lastJobDataInizio;
    String lastJobDataFine;
    String lastJobOraInizio;
    String lastJobOraFine;
    int lastJobEsaustivo;

    String nextJobDataInizio;
    String nextJobDataFine;
    String nextJobOraInizio;
    String nextJobOraFine;

    int lastJobID;

    String JSON_SCHED = "";
    String JSON_SCHEDTYPE = "";
    String JSON_DAYTYPE = "";
    String JSON_YEARTYPE = "";
    String JSON_DAYS = "";
    String JSON_MONTHS = "";
    String JSON_YEARS = "";

    ArrayList<String> giorniFit;
    ArrayList<String> mesiFit;
    ArrayList<String> anniFit;

    public schedule() {
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();

    }

    public int getLastJobEsaustivo() {
        return lastJobEsaustivo;
    }

    public void setLastJobEsaustivo(int lastJobEsaustivo) {
        this.lastJobEsaustivo = lastJobEsaustivo;
    }

    public String getLastJobOraInizio() {
        return lastJobOraInizio;
    }

    public void setLastJobOraInizio(String lastJobOraInizio) {
        this.lastJobOraInizio = lastJobOraInizio;
    }

    public String getLastJobOraFine() {
        return lastJobOraFine;
    }

    public void setLastJobOraFine(String lastJobOraFine) {
        this.lastJobOraFine = lastJobOraFine;
    }

    public String getNextJobDataInizio() {
        return nextJobDataInizio;
    }

    public void setNextJobDataInizio(String nextJobDataInizio) {
        this.nextJobDataInizio = nextJobDataInizio;
    }

    public String getNextJobDataFine() {
        return nextJobDataFine;
    }

    public void setNextJobDataFine(String nextJobDataFine) {
        this.nextJobDataFine = nextJobDataFine;
    }

    public String getNextJobOraInizio() {
        return nextJobOraInizio;
    }

    public void setNextJobOraInizio(String nextJobOraInizio) {
        this.nextJobOraInizio = nextJobOraInizio;
    }

    public String getNextJobOraFine() {
        return nextJobOraFine;
    }

    public void setNextJobOraFine(String nextJobOraFine) {
        this.nextJobOraFine = nextJobOraFine;
    }

    public ArrayList<String> getGiorniFit() {
        return giorniFit;
    }

    public void setGiorniFit(ArrayList<String> giorniFit) {
        this.giorniFit = giorniFit;
    }

    public ArrayList<String> getMesiFit() {
        return mesiFit;
    }

    public void setMesiFit(ArrayList<String> mesiFit) {
        this.mesiFit = mesiFit;
    }

    public ArrayList<String> getAnniFit() {
        return anniFit;
    }

    public void setAnniFit(ArrayList<String> anniFit) {
        this.anniFit = anniFit;
    }

    public String getJSON_SCHED() {
        return JSON_SCHED;
    }

    public void setJSON_SCHED(String JSON_SCHED) {
        this.JSON_SCHED = JSON_SCHED;
    }

    public String getJSON_SCHEDTYPE() {
        return JSON_SCHEDTYPE;
    }

    public void setJSON_SCHEDTYPE(String JSON_SCHEDTYPE) {
        this.JSON_SCHEDTYPE = JSON_SCHEDTYPE;
    }

    public String getJSON_DAYTYPE() {
        return JSON_DAYTYPE;
    }

    public void setJSON_DAYTYPE(String JSON_DAYTYPE) {
        this.JSON_DAYTYPE = JSON_DAYTYPE;
    }

    public String getJSON_YEARTYPE() {
        return JSON_YEARTYPE;
    }

    public void setJSON_YEARTYPE(String JSON_YEARTYPE) {
        this.JSON_YEARTYPE = JSON_YEARTYPE;
    }

    public String getJSON_DAYS() {
        return JSON_DAYS;
    }

    public void setJSON_DAYS(String JSON_DAYS) {
        this.JSON_DAYS = JSON_DAYS;
    }

    public String getJSON_MONTHS() {
        return JSON_MONTHS;
    }

    public void setJSON_MONTHS(String JSON_MONTHS) {
        this.JSON_MONTHS = JSON_MONTHS;
    }

    public String getJSON_YEARS() {
        return JSON_YEARS;
    }

    public void setJSON_YEARS(String JSON_YEARS) {
        this.JSON_YEARS = JSON_YEARS;
    }

    public String getYears() {
        return years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    public String getLastJobDataInizio() {
        return lastJobDataInizio;
    }

    public void setLastJobDataInizio(String lastJobDataInizio) {
        this.lastJobDataInizio = lastJobDataInizio;
    }

    public String getLastJobDataFine() {
        return lastJobDataFine;
    }

    public void setLastJobDataFine(String lastJobDataFine) {
        this.lastJobDataFine = lastJobDataFine;
    }

    public int getLastJobID() {
        return lastJobID;
    }

    public void setLastJobID(int lastJobID) {
        this.lastJobID = lastJobID;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getRifOperatore() {
        return rifOperatore;
    }

    public void setRifOperatore(String rifOperatore) {
        this.rifOperatore = rifOperatore;
    }

    public String getRifContesto() {
        return rifContesto;
    }

    public void setRifContesto(String rifContesto) {
        this.rifContesto = rifContesto;
    }

    public String getRifCalendario() {
        return rifCalendario;
    }

    public void setRifCalendario(String rifCalendario) {
        this.rifCalendario = rifCalendario;
    }

    public int getRifJob() {
        return rifJob;
    }

    public void setRifJob(int rifJob) {
        this.rifJob = rifJob;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(String dataInizio) {
        this.dataInizio = dataInizio;
    }

    public String getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(String oraInizio) {
        this.oraInizio = oraInizio;
    }

    public String getDataFine() {
        return dataFine;
    }

    public void setDataFine(String dataFine) {
        this.dataFine = dataFine;
    }

    public String getOraFine() {
        return oraFine;
    }

    public void setOraFine(String oraFine) {
        this.oraFine = oraFine;
    }

    public int getOffsetDays() {
        return offsetDays;
    }

    public void setOffsetDays(int offsetDays) {
        this.offsetDays = offsetDays;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getMonths() {
        return months;
    }

    public void setMonths(String months) {
        this.months = months;
    }

    public int getMargineDays() {
        return margineDays;
    }

    public void setMargineDays(int margineDays) {
        this.margineDays = margineDays;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTextFromJson() throws UnsupportedEncodingException {
        String result = "";
        String testoDays;
        String testoMonths;
        String testoYears;
        if (this.JSON_SCHED != null) {
            JSONParser jsonParser = new JSONParser();
            String rifProposta = "";
            String schArray = "";
            String schedule = java.net.URLDecoder.decode(this.JSON_SCHED, "UTF-8");
            JSONObject jsonObject;
            String schedType = "";
            String DAYTYPE = "";
            String YEARTYPE = "";
            String DAYS = "";
            String MONTHS = "";
            String YEARS = "";
            System.out.println("schedule = " + schedule);
            try {
                //JSON:{"NPR":"72","SCH":[{"schedType":"CUSTOM","DAYTYPE":"DoW","YEARTYPE":"All","DAYS":"1-7","MONTHS":"1-12","YEARS":"All"}]}
                jsonObject = (JSONObject) jsonParser.parse(schedule);

                rifProposta = jsonObject.get("NPR").toString();
                System.out.println("rifProposta = " + rifProposta);
                schArray = jsonObject.get("SCH").toString();

                if (schArray != null && schArray.length() > 0) {
                    JSONParser parser = new JSONParser();
                    Object obj;
                    obj = parser.parse(schArray);
                    JSONArray array = (JSONArray) obj;
                    for (Object riga : array) {
                        jsonObject = (JSONObject) jsonParser.parse(riga.toString());
                        try {
                            setJSON_SCHEDTYPE(jsonObject.get("schedType").toString());
                        } catch (Exception e) {
                        }
                        try {
                            setJSON_DAYTYPE(jsonObject.get("DAYTYPE").toString());
                        } catch (Exception e) {
                        }
                        try {
                            setJSON_YEARTYPE(jsonObject.get("YEARTYPE").toString());
                        } catch (Exception e) {
                        }
                        try {
                            setJSON_DAYS(jsonObject.get("DAYS").toString());
                        } catch (Exception e) {
                        }
                        try {
                            setJSON_MONTHS(jsonObject.get("MONTHS").toString());
                        } catch (Exception e) {
                        }
                        try {
                            setJSON_YEARS(jsonObject.get("YEARS").toString());
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (ParseException ex) {

            }

            String daysPhrase = "";
            String monthPhrase = "";
            String yearPhrase = "";
            giorniFit = new ArrayList<String>();
            mesiFit = new ArrayList<String>();
            anniFit = new ArrayList<String>();
//1. analizzo days
            if (JSON_DAYTYPE.equalsIgnoreCase("DoW")) {
                // giorni della settimana
                String[] blocks = JSON_DAYS.split(",");
                List<String> block = Arrays.asList(blocks);
                if (block.size() > 0) {
                    for (int bb = 0; bb < block.size(); bb++) {
                        if (block.get(bb).length() > 0) {

                            if (daysPhrase.length() > 0) {
                                daysPhrase += ", ";
                            }
                            String valore = "";
                            String condizione = "";
                            String[] couples = block.get(bb).split("-");

                            List<String> param = null;
                            param = Arrays.asList(couples);
                            System.out.println("param.size = " + param.size());
                            if (param.size() > 1) {
                                // è un range

                                String startX = param.get(0);
                                String endX = param.get(1);
                                if (startX.equals("1") && endX.equals("7")) {
                                    daysPhrase += "Tutti i giorni della settimana ";
                                    for (int g = 1; g < 8; g++) {
                                        giorniFit.add(getNomeGiorno(g));
                                    }

                                } else {
                                    daysPhrase += "da " + getNomeGiorno(startX) + " a " + getNomeGiorno(endX);
                                    int st = Integer.parseInt(startX);
                                    int en = Integer.parseInt(endX);
                                    for (int g = st; g < (en + 1); g++) {
                                        giorniFit.add(getNomeGiorno(g));
                                    }

                                }

                            } else {
                                // è un numero 

                                daysPhrase += "il " + getNomeGiorno(block.get(bb)) + " ";
                                giorniFit.add(getNomeGiorno(block.get(bb)));

                            }
                        }
                    }
                }
            } else {
                // giuorni del mese
                String[] blocks = JSON_DAYS.split(",");
                List<String> block = Arrays.asList(blocks);
                if (block.size() > 0) {
                    for (int bb = 0; bb < block.size(); bb++) {
                        if (block.get(bb).length() > 0) {
                            if (daysPhrase.length() > 0) {
                                daysPhrase += ", ";
                            }
                            String[] couples = block.get(bb).split("-");
                            List<String> param = Arrays.asList(couples);

                            if (param.size() > 1) {
                                // è un range

                                String startX = param.get(0);
                                String endX = param.get(1);

                                if (startX.equals("1") && endX.equals("31")) {
                                    daysPhrase += "tutti i giorni del mese ";
                                    for (int g = 1; g < 32; g++) {
                                        mesiFit.add("" + g);
                                    }
                                } else {
                                    daysPhrase += "dal " + startX + " al " + endX + " ";
                                    int st = Integer.parseInt(startX);
                                    int en = Integer.parseInt(endX);
                                    for (int g = st; g < (en + 1); g++) {
                                        giorniFit.add("" + g);
                                    }

                                }
                            } else {
                                // è un numero
                                daysPhrase += "il " + block.get(bb)+ " ";
                                giorniFit.add(block.get(bb));
                            }
                        }
                    }
                }

            }
            daysPhrase = PerfezionaPhrase(daysPhrase);
//        int i = daysPhrase.lastIndexOf(",");
//        if (daysPhrase != null && i > 0 && i < daysPhrase.length()) {
//            String testa = daysPhrase.substring(0, i);
//            String coda = daysPhrase.substring(i + 1, daysPhrase.length());
//
//            daysPhrase = testa + " e" + coda;
//
//        }

//2. analizzo months      
            if (JSON_MONTHS != null) {
                String[] blocks = JSON_MONTHS.split(",");
                List<String> block = Arrays.asList(blocks);
                if (block.size() > 0) {
                    for (int bb = 0; bb < block.size(); bb++) {
                        if (block.get(bb).length() > 0) {
                            if (monthPhrase.length() > 0) {
                                monthPhrase += ", ";
                            }
                            String valore = "";
                            String condizione = "";
                            String[] couples = block.get(bb).split("-");

                            List<String> param = null;
                            param = Arrays.asList(couples);
                            System.out.println("param.size = " + param.size());
                            if (param.size() > 1) {
                                // è un range

                                String startX = param.get(0);
                                String endX = param.get(1);
                                if (startX.equals("1") && endX.equals("12")) {
                                    monthPhrase += " di tutti i mesi.";
                                    for (int g = 1; g < 13; g++) {
                                        mesiFit.add("" + g);
                                    }

                                } else {
                                    monthPhrase += "da " + getNomeMese(startX) + " a " + getNomeMese(endX);
                                    int st = Integer.parseInt(startX);
                                    int en = Integer.parseInt(endX);
                                    for (int g = st; g < (en + 1); g++) {
                                        mesiFit.add("" + g);
                                    }
                                }
                            } else {
                                // è un numero 
                                monthPhrase += "di " + getNomeMese(block.get(bb));
                                mesiFit.add(block.get(bb));
                            }
                        }
                    }
                }
                monthPhrase = PerfezionaPhrase(monthPhrase);
            }

            if (JSON_YEARS != null) {
                String[] blocks = JSON_YEARS.split(",");
                List<String> block = Arrays.asList(blocks);
                if (block.size() > 0) {
                    for (int bb = 0; bb < block.size(); bb++) {
                        if (block.get(bb).length() > 0) {
                            if (monthPhrase.length() > 0) {
                                monthPhrase += ", ";
                            }
                            String valore = "";
                            String condizione = "";
                            String[] couples = block.get(bb).split("-");

                            List<String> param = null;
                            param = Arrays.asList(couples);

                            if (param.size() > 1) {
                                // è un range

                                String startX = param.get(0);
                                String endX = param.get(1);

                                monthPhrase += "dal " + startX + " al " + endX;
                                int st = Integer.parseInt(startX);
                                int en = Integer.parseInt(endX);
                                for (int g = st; g < (en + 1); g++) {
                                    anniFit.add("" + g);
                                }

                            } else {
                                if (block.get(bb).equals("All")) {
                                    monthPhrase += " di ogni anno.";
                                } else {
                                    // è un numero 
                                    monthPhrase += " nel " + block.get(bb);
                                    anniFit.add(block.get(bb));
                                }
                            }
                        }
                    }
                }
                yearPhrase = PerfezionaPhrase(yearPhrase);
            }

            result = daysPhrase + monthPhrase;

            System.out.println("GIORNIFIT: " + giorniFit.size());
            for (int jj = 0; jj < giorniFit.size(); jj++) {
                System.out.println("\tGIORNIFIT " + jj + ") " + giorniFit.get(jj));
            }
            System.out.println("MESIFIT: " + mesiFit.size());
            for (int jj = 0; jj < mesiFit.size(); jj++) {
                System.out.println("\tMESIFIT " + jj + ") " + mesiFit.get(jj));
            }
            System.out.println("ANNIFIT: " + anniFit.size());
            for (int jj = 0; jj < anniFit.size(); jj++) {
                System.out.println("\tANNIFIT " + jj + ") " + anniFit.get(jj));
            }
        }
        return result;
    }

    public int getActiveToday(String dayDate) {
        int fits = 0;
        String year = dayDate.substring(0, 4);
        String month = dayDate.substring(5, 7);
        String day = dayDate.substring(8, 10);
        dayDate = year + "-" + month + "-" + day;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        Calendar oggi = Calendar.getInstance();
        Calendar inizio = Calendar.getInstance();
        Calendar fine = Calendar.getInstance();

        String dataInizio = this.getDataInizio();
        String dataFine = this.getDataFine();
        boolean inRangeInizio = true;
        boolean inRangeFine = true;
        boolean inRangeYears = false;
        if (dayDate != null) {
            try {
                oggi.setTime(sdf.parse(dayDate));
                int dayOfWeek = oggi.get(Calendar.DAY_OF_WEEK);
                //  System.out.println("il giorno è il giorno:" + dayOfWeek +" della settimana.");
                int dayOfMonth = oggi.get(Calendar.DAY_OF_MONTH);
                //  System.out.println("il giorno è il giorno:" + dayOfMonth +" del mese.");    
            } catch (Exception ex) {
                System.out.println("Errore in elaborazione data ricevuta++++++++++++++++");
            }
        }

        // controllo in base a anno
        String resp = "";
        int i = this.getYearFits(year);
        resp = ("In base agli anni coinvolti la data di oggi (" + year + ") ");
        if (i < 1) {
            resp += (" NON è ADEGUATA ");
        } else {
            resp += (" è adeguata");
            inRangeYears = true;

        }

        if (dataInizio != null) {
            try {
                inizio.setTime(sdf.parse(dataInizio));
                DateUtils du = new DateUtils();

                boolean RangeIniziato = du.isBeforeDay(inizio, oggi);
                boolean RangeInizia = du.isSameDay(inizio, oggi);
                if ((RangeInizia || RangeIniziato)) {
                    inRangeInizio = true;
                } else {
                    inRangeInizio = false;
                }
            } catch (Exception e) {

            }

        }
        if (dataFine != null) {
            try {
                fine.setTime(sdf.parse(dataFine));
                DateUtils du = new DateUtils();

                boolean RangeNonFinito = du.isAfterDay(fine, oggi);
                boolean RangeFinisce = du.isSameDay(fine, oggi);
                if ((RangeFinisce || RangeNonFinito)) {
                    inRangeFine = true;
                } else {
                    inRangeFine = false;
                }
            } catch (Exception e) {

            }

        }
        System.out.println("TIPO SCHEDULAZIONE:" + this.getTipo());
        System.out.println("TIPO getJSON_DAYTYPE:" + this.getJSON_DAYTYPE());
        System.out.println("TIPO getJSON_SCHEDTYPE:" + this.getJSON_SCHEDTYPE());
        

        System.out.println(" ++" + resp + "   inizio:"
                + dataInizio + " fine:" + dataFine
                + " in range inizio:" + inRangeInizio
                + " in range fine:" + inRangeFine
        );

        if (inRangeYears == true && inRangeFine == true && inRangeInizio == true) {
            fits = 1;
        }

        return fits;
    }

    public int getYearFits(String year) {
        int fits = 0;
        if (anniFit != null) {
            for (int jj = 0; jj < anniFit.size(); jj++) {
                if (anniFit.get(jj).equals(year)) {
                    fits++;
                    break;
                }
            }
        }
        if (this.getJSON_YEARTYPE().equalsIgnoreCase("ALL")) {
            fits++;
        }
        return fits;
    }

    public int getYearFits(int year) {
        int fits = 0;
        if (anniFit != null) {
            for (int jj = 0; jj < anniFit.size(); jj++) {
                if (anniFit.get(jj).equals("" + year)) {
                    fits++;
                    break;
                }
            }
        }
        if (this.getJSON_YEARTYPE().equalsIgnoreCase("ALL")) {
            fits++;
        }
        return fits;
    }

    public int getDoWfits(int day) {
        int fits = 0;
        if (giorniFit != null) {
            for (int jj = 0; jj < giorniFit.size(); jj++) {
                if (giorniFit.get(jj).equals(getNomeGiorno(day))) {
                    fits++;
                    break;
                }
            }
        }
        return fits;
    }

    public int getDoMfits(int day) {
        int fits = 0;
        if (giorniFit != null) {
            for (int jj = 0; jj < giorniFit.size(); jj++) {
                if (giorniFit.get(jj).equals("" + day)) {
                    fits++;
                    break;
                }
            }
        }
        return fits;
    }

    public int getDoWfits(String day) {
        int fits = 0;
        if (giorniFit != null) {
            for (int jj = 0; jj < giorniFit.size(); jj++) {
                if (giorniFit.get(jj).equals(day)) {
                    fits++;
                    break;
                }
            }
        }
        return fits;
    }

    public int getDoMfits(String day) {
        int fits = 0;
        if (giorniFit != null) {
            for (int jj = 0; jj < giorniFit.size(); jj++) {
                if (giorniFit.get(jj).equals(day)) {
                    fits++;
                    break;
                }
            }
        }
        return fits;
    }

    public int getMonthFits(String month) {
        int fits = 0;
        if (mesiFit != null) {
            for (int jj = 0; jj < mesiFit.size(); jj++) {
                if (mesiFit.get(jj).equals(month)) {
                    fits++;
                    break;
                }
            }
        }
        return fits;
    }

    public int getMonthFits(int month) {
        int fits = 0;
        if (mesiFit != null) {
            for (int jj = 0; jj < mesiFit.size(); jj++) {
                if (mesiFit.get(jj).equals("" + month)) {
                    fits++;
                    break;
                }
            }
        }
        return fits;
    }

    public String PerfezionaPhrase(String phrase) {
        int i = phrase.lastIndexOf(",");
        if (phrase != null && i > 0 && i < phrase.length()) {
            String testa = phrase.substring(0, i);
            String coda = phrase.substring(i + 1, phrase.length());

            phrase = testa + " e" + coda;

        }
        return phrase;
    }

    public String getNomeGiorno(String day) {
        return getNomeGiorno(Integer.parseInt(day));

    }

    public String getNomeGiorno(int day) {
        String nomeGiorno = "";
        switch (day) {
            case 1:
                nomeGiorno = "Lunedì";
                break;
            case 2:
                nomeGiorno = "Martedì";
                break;
            case 3:
                nomeGiorno = "Mercoledì";
                break;
            case 4:
                nomeGiorno = "Giovedì";
                break;
            case 5:
                nomeGiorno = "Venerdì";
                break;
            case 6:
                nomeGiorno = "Sabato";
                break;
            case 7:
                nomeGiorno = "Domenica";
                break;
        }
        return nomeGiorno;

    }

    public String getNomeMese(String monthX) {
        int month = Integer.parseInt(monthX);
        return getNomeMese(month);
    }

    public String getNomeMese(int month) {
        String nomeMese = "";
        switch (month) {
            case 1:
                nomeMese = "Gennaio";
                break;
            case 2:
                nomeMese = "Febbraio";
                break;
            case 3:
                nomeMese = "Marzo";
                break;
            case 4:
                nomeMese = "Aprile";
                break;
            case 5:
                nomeMese = "Maggio";
                break;
            case 6:
                nomeMese = "Giugno";
                break;
            case 7:
                nomeMese = "Luglio";
                break;
            case 8:
                nomeMese = "Agosto";
                break;
            case 9:
                nomeMese = "Settembre";
                break;
            case 10:
                nomeMese = "Ottobre";
                break;
            case 11:
                nomeMese = "Novembre";
                break;
            case 12:
                nomeMese = "Dicembre";
                break;
        }
        return nomeMese;

    }

    public void compilaMatrici() {
        // per ogni giorno e mese devo sapere se la schedulazione fitta

    }

}
