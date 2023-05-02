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

import REVOdbManager.EVOpagerDirectivesManager;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.EVOpagerDBconnection;
import REVOpager.EVOuser;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;

import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.BarcodeDatamatrix;
import com.itextpdf.text.pdf.ColumnText;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import smartCore.smartCalendar;
import smartCore.smartForm;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class PDFdoc {

    String masterQuery = "";
    String currentHeaderDoc = "";
    EVOpagerParams myParams;
    Settings mySettings;
    OutputStream stream;
    Document masterDocument, document;
    private PdfPTable intestazione;
    String docType;
    String destType;
    ArrayList<SelectListLine> argList;
    ArrayList<SelectListLine> richArgList;

    ArrayList<scriptVariable> varsList;

    int tableStack;
    int triggerStack;
    PdfPTable table = null;

    PdfPTable[] Tables = null;
    int cols[] = null;
    Boolean[] isDatabaseTable = null;

    ResultSet[] rs = null;
    PDFcell[] openCell = null;
    String[] outputDestination;
    reportRowTrigger[] triggers;

    int TablesStack;
    int CellStack;
    String docName = "";
    PdfPTable mainDestTable; // usata in chiamata inner document

    //ArrayList<PDFreportRow> reportRows;
    private int flagIntestazione = 0;
    private int rowIntestazioneStart = 0;
    private int rowIntestazioneEnd = 0;
    private int flagFondopagina = 0;
    private String labelDate = "";
    private int publishDate;
    private int publishPageNumber;
    private int publishPageTotal;
    Rectangle PDFpageSize;
    float PDFmarginLeft;
    float PDFmarginRight;
    float PDFmarginTop;
    float PDFmarginBottom;
    String PDFfilename;
    PdfWriter writer;
    Connection conny;
    PdfTemplate template;
    test av;

    private gate calendarMyGate;
    private smartForm calendarMyForm;
    private portalClass calendarMyPortal;
    private int calendarMonth;
    private int calendarYear;

    public int getCalendarMonth() {
        return calendarMonth;
    }

    public void setCalendarMonth(int calendarMonth) {
        this.calendarMonth = calendarMonth;
    }

    public int getCalendarYear() {
        return calendarYear;
    }

    public void setCalendarYear(int calendarYear) {
        this.calendarYear = calendarYear;
    }

    public gate getCalendarMyGate() {
        return calendarMyGate;
    }

    public void setCalendarMyGate(gate calendarMyGate) {
        this.calendarMyGate = calendarMyGate;
    }

    public smartForm getCalendarMyForm() {
        return calendarMyForm;
    }

    public void setCalendarMyForm(smartForm calendarMyForm) {
        this.calendarMyForm = calendarMyForm;
    }

    public portalClass getCalendarMyPortal() {
        return calendarMyPortal;
    }

    public void setCalendarMyPortal(portalClass calendarMyPortal) {
        this.calendarMyPortal = calendarMyPortal;
    }

    public void setPDFpageSize(Rectangle PDFpageSize) {
        this.PDFpageSize = PDFpageSize;
    }

    public void setPDFmarginLeft(float PDFmarginLeft) {
        this.PDFmarginLeft = PDFmarginLeft;
    }

    public void setPDFmarginRight(float PDFmarginRight) {
        this.PDFmarginRight = PDFmarginRight;
    }

    public void setPDFmarginTop(float PDFmarginTop) {
        this.PDFmarginTop = PDFmarginTop;
    }

    public void setPDFmarginBottom(float PDFmarginBottom) {
        this.PDFmarginBottom = PDFmarginBottom;
    }

    public Rectangle getPDFpageSize() {
        return PDFpageSize;
    }

    public float getPDFmarginLeft() {
        return PDFmarginLeft;
    }

    public float getPDFmarginRight() {
        return PDFmarginRight;
    }

    public float getPDFmarginTop() {
        return PDFmarginTop;
    }

    public float getPDFmarginBottom() {
        return PDFmarginBottom;
    }

    public String getPDFfilename() {
        return PDFfilename;
    }

    public PDFdoc(OutputStream stream, EVOpagerParams xParams, Settings xSettings) {
        PDFpageSize = PageSize.A4;
        PDFmarginLeft = 16;
        PDFmarginRight = 16;
        PDFmarginTop = 80;
        PDFmarginBottom = 36;
        PDFfilename = "doc_";
        this.stream = stream;
        docType = "mainDocument";
        this.myParams = xParams;
        this.mySettings = xSettings;
        this.argList = new ArrayList<SelectListLine>();
        this.richArgList = new ArrayList<SelectListLine>();
        this.varsList = new ArrayList<scriptVariable>();
//        reportRows = new ArrayList<PDFreportRow>();
        triggers = new reportRowTrigger[90];
        openCell = new PDFcell[90];
        outputDestination = new String[90];
        cols = new int[90];
    }

    public PDFdoc(OutputStream stream, Document masterDocument, PdfWriter Xwriter, ArrayList<SelectListLine> xArgList, EVOpagerParams xParams, Settings xSettings) {
        this.stream = stream;
        docType = "innerTable";
        this.myParams = xParams;
        this.mySettings = xSettings;
        this.argList = new ArrayList<SelectListLine>();
//        reportRows = new ArrayList<PDFreportRow>();
        this.argList = xArgList;
        this.richArgList = new ArrayList<SelectListLine>();
        this.varsList = new ArrayList<scriptVariable>();
        richArgList = cloneArray(argList);
        this.writer = Xwriter;
        this.document = masterDocument;
        //   System.out.println("\n>>>\n\n writer è stato passato:" + writer.toString());
        triggers = new reportRowTrigger[90];
        openCell = new PDFcell[90];
        outputDestination = new String[90];
        cols = new int[90];
    }

    public Object fillDocumentDataTable(ArrayList<PDFreportRow> XreportRows, String XdestType) {

        conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();

// chiamata a documento che sarà inserito in ...
        Object retObj = new Object();

        this.destType = XdestType;
        mainDestTable = new PdfPTable(1);
//        System.out.println("\n>>>>>>>>>SONO IN fillDocumentDataTable destType:" + destType);
//        System.out.println("CHIAMATO FORM con tabella DATI; " + docName);
//        for (SelectListLine xx : argList) {
//            System.out.println("-argList MARK:" + xx.getLabel() + " = " + xx.getValue());
//        }
        paintDocument(XreportRows, XdestType, conny);
        retObj = mainDestTable;
//        System.out.println("\n>>>>>>>>>RITORNO CON OGGETTO CONTENENTE TABELLA PREPARATO DA paintDocument.");
        return retObj;
    }

    public Object fillDocumentWithMasterQuery(String XdocName, String destType, String query) {
        this.masterQuery = query;
        Object retObj = fillDocument(XdocName, destType);
        return retObj;
    }

    public Object fillDocument(String XdocName, String destType) {
//        System.out.println(">>>>>>>>>SONO IN fillDocument destType:" + destType + "   -  XdocName:" + XdocName);
        conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        Object retObj = new Object();
        docName = XdocName;
        // chiamata a documento che sarà inserito in ...
        if (destType.equalsIgnoreCase("table")) {
            this.destType = destType;
            mainDestTable = new PdfPTable(1);
//            System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>DESTtype=" + destType);
//            System.out.println("fillDocument_ CHIAMATO FORM SECONDARIO; " + docName);
            goFillDocument(docName, conny);
            retObj = mainDestTable;
            //            System.out.println(">>>>>>>>>RITORNO CON OGGETTO mainDestTable CONTENENTE TABELLA PREPARATO DA goFillDocument.");
        } else {// per default è document
            destType = "document";
            this.destType = destType;
            goFillDocument(docName, conny);
            retObj = null;
            //            System.out.println(">>>>>>>>>RITORNO OGGETTO NULLO  docName:" + docName);

        }
        // if (destType.equalsIgnoreCase("table")) {            

        // }
        return retObj;
    }

    public void goFillDocument(String XdocName, Connection conny) {
//        System.out.println("\n>>>>>>>>>SONO IN goFillDocument destType:" + destType + "   -  XdocName:" + XdocName);

        ArrayList<PDFreportRow> reportRows = scriptLoader(XdocName, conny);
        paintDocument(reportRows, destType, conny);
        //        System.out.println(">>>>>>>>>RITORNO CON OGGETTO CONTENENTE TABELLA PREPARATO DA goFillDocument.");

    }

    public void paintDocument(ArrayList<PDFreportRow> reportRows, String xdestType, Connection conny) {

        if (xdestType == null) {
            xdestType = "document";
        }
//        System.out.println("\n############### SONO IN paintDocument --> xdestType:" + xdestType);

        this.destType = xdestType;
        int liness = 0;
        for (SelectListLine xx : argList) {
            liness++;
//            System.out.println(liness + ") PD-argList MARK:" + xx.getLabel() + " = " + xx.getValue());
        }

        Tables = new PdfPTable[90];
        triggerStack = 0;
        TablesStack = 0;
        CellStack = 0;
        isDatabaseTable = new Boolean[90];
        rs = new ResultSet[90];
        for (int jj = 0; jj < 90; jj++) {
            isDatabaseTable[jj] = false;
        }

        PdfContentByte cb;

        PdfPCell retCell = new PdfPCell();
        retCell.setColspan(1);
        retCell.setRowspan(1);
        if (docType != null && docType.equalsIgnoreCase("mainDocument")) {
            System.out.println("=========================");
            System.out.println("CREO UN NUOVO REPORT");
            System.out.println("pageSize=" + PDFpageSize.toString()
                    + " marginLeft=" + PDFmarginLeft
                    + " PDFmarginRight=" + PDFmarginRight
                    + " PDFmarginTop=" + PDFmarginTop
                    + " PDFmarginBottom=" + PDFmarginBottom
            );
            System.out.println("=========================");
// public Document(Rectangle pageSize, float marginLeft, float marginRight, float marginTop, float marginBottom) {

            document = new Document(PDFpageSize, PDFmarginLeft, PDFmarginRight, PDFmarginTop, PDFmarginBottom);

            TableHeader event = new TableHeader();
            try {
                writer = PdfWriter.getInstance(document, stream);
                document.addTitle("QP PDF");
                av = new test();
                writer.setPageEvent(av);
                document.open();
                cb = writer.getDirectContent();
                document.addTitle(PDFfilename);
            } catch (DocumentException ex) {
                Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
            }
            richArgList = cloneArray(argList);// aggiungo a argList la MasterQuery per poter compilare gli stessi campi del form di provenienza
        }

        int row = 0;
//        System.out.println("INIZIO paint Document:" + reportRows.size() + " righe. = " + docName);
        //reportRows = cloneScript(reportRows);

        while (row < reportRows.size()) {

            int rigaInizio;
            int rigaFine = 0;
            PDFreportRow myRow = reportRows.get(row);
            //-------------------------------
//            System.out.println("parseReportRow da main: " + myRow.getElement());
            parseReportRow(myRow, row, reportRows);

            //-------------------------------
            if (myRow.getElement().equalsIgnoreCase("openDataTable")
                    || myRow.getElement().equalsIgnoreCase("openDatabase")) {

//                System.out.println("\n\n-->openDataTable in riga: " + row);
                // sono entrato in una riga che apre una tabella dati
                // ceco la prima chiusura di tabella disponibile
                rigaInizio = row;
                rigaFine = getRange(rigaInizio, reportRows, myRow.getElement());
//-------------------------------------
//                System.out.println(docName + ")LOOP da riga " + row + " a " + rigaFine);

                String query = browserArgsReplace(myRow.getQuery(), "mysql");
//                System.out.println("\n\n-PDF->Query: " + query);
//                System.out.println("TablesStack: " + TablesStack);

                int DBrows = 0;
                try {

                    PreparedStatement ps = conny.prepareStatement(query);
                    rs[TablesStack] = ps.executeQuery();
                    int lines = 0;
                    ArrayList<SelectListLine> fields = new ArrayList<SelectListLine>();
                    while (rs[TablesStack].next()) { // per ogni riga in tabella
                        DBrows++;
//                        System.out.println("\nRIGA DATABASE: " + DBrows);
                        if (DBrows == 1) {
                            ResultSetMetaData rsmd = rs[TablesStack].getMetaData();
                            int numColumns = rsmd.getColumnCount();
                            // System.out.println(docName + " =================assegno Args [" + richArgList.size());

                            for (int i = 1; i <= numColumns; i++) {
                                // System.out.println("________________________________");
                                String column_name = rsmd.getColumnName(i);
                                String column_name2 = rsmd.getColumnLabel(i);
                                int column_type = rsmd.getColumnType(i);
                                int colType = 0;
//                                System.out.println(">: " + column_name
//                                        + " >getColumnLabel: " + column_name2
//                                        + " >column_type: " + column_type);

                                if (column_name2 != null
                                        && column_name2 != "") {
                                    column_name = column_name2;
                                }
                                if (column_type != Types.BLOB
                                        && column_type != Types.CLOB
                                        && column_type != Types.TIMESTAMP
                                        && column_type != -4) {
                                    if (column_type == Types.BIGINT
                                            || column_type == Types.INTEGER
                                            || column_type == Types.FLOAT
                                            || column_type == Types.DECIMAL
                                            || column_type == Types.DOUBLE
                                            || column_type == Types.REAL
                                            || column_type == Types.TINYINT) {
                                        colType = 1;
                                    }

                                    SelectListLine myField = new SelectListLine();
                                    myField.setChecked(colType);
                                    myField.setLabel(column_name);
                                    myField.setType(column_type);
                                    fields.add(myField);
                                }

                            }

                        }

                        richArgList = cloneArray(argList);
                        for (int jj = 0; jj < fields.size(); jj++) {
                            SelectListLine myLine = new SelectListLine();
                            myLine.setLabel(fields.get(jj).getLabel());
                            if (fields.get(jj).getChecked() == 1) {
                                // numero
                                myLine.setValue("" + rs[TablesStack].getInt(fields.get(jj).getLabel()));
                            } else {
                                // testo
                                myLine.setValue(rs[TablesStack].getString(fields.get(jj).getLabel()));
//                                System.out.println("Nome field = " + fields.get(jj).getLabel() + " value = " + myLine.getValue() + " type = " + fields.get(jj).getType());

                            }

                            richArgList.add(myLine);
                        }
                        //------FINE CREAZIONE LISTA ARGOMENTI--------------------------------------

                        //  System.out.println(docName + " =================inizio  parsing righe script Args [" + richArgList.size() + "/" + argList.size());
                        int loopRows = 0;
                        for (int curRow = rigaInizio + 1; curRow < rigaFine; curRow++) { //per ogni riga di script
                            loopRows++;

                            PDFreportRow loopRow = new PDFreportRow();
                            loopRow = reportRows.get(curRow);
//                            System.out.println("RIGA LOOP: " + loopRows + " con parsing della riga " + curRow + " ->" + loopRow.getElement());

                            //-------------------------------
                            //System.out.println("parseReportRow da LOOP");
                            parseReportRow(loopRow, curRow, reportRows);

                            //-------------------------------
                            if (curRow > rigaInizio + 1 && loopRow.getElement().equalsIgnoreCase("openDataTable")) {

//                                System.out.println("\n\n-->open SUBDataTable in riga: " + curRow);
                                // sono entrato in una riga che apre una tabella dati
                                // ceco la prima chiusura di tabella disponibile
                                int SUBrigaInizio = curRow;
                                int SUBrigaFine = getRange(curRow, reportRows, "openDataTable");
//-------------------------------------
                                //      System.out.println(docName + ")SUB Rieseguo da riga " + SUBrigaInizio + " a " + SUBrigaFine);

                                ArrayList<PDFreportRow> SUBreportRows = new ArrayList<PDFreportRow>();
                                for (int kk = SUBrigaInizio; kk <= SUBrigaFine; kk++) {
//                                    System.out.println(kk + ") MANDO " + reportRows.get(kk).getElement());
                                    SUBreportRows.add(reportRows.get(kk));
                                }

//                                System.out.println("\n***************************\n***PREPARO UN DOCUMENTO SECONDARIO ()TAB DATI) DI " + SUBreportRows.size() + " righe.");
                                for (int hh = 0; hh < SUBreportRows.size(); hh++) {
//                                    System.out.println("***RIGA " + hh + ")" + SUBreportRows.get(hh).getElement());

                                }
                                PDFdoc innerDoc = new PDFdoc(stream, document, writer, richArgList, myParams, mySettings);
                                PdfPTable myTable = (PdfPTable) innerDoc.fillDocumentDataTable(SUBreportRows, "table");
//                                System.out.println("calculateHeights:" + myTable.calculateHeights());
                                Tables[TablesStack] = myTable;
                                // parsing della chiusura
                                curRow = SUBrigaFine - 1;
//                                System.out.println("FINE LOOP per tabella interna ; Riprendo da riga " + curRow);
//                                System.out.println("\n***************************\n ");

                            } else if (loopRow.getElement().equalsIgnoreCase("if")) {

                                if (triggers[triggerStack].isVerificato()) {
                                    //  System.out.println("= IN DB TABLE HO APPENA PARSATO UN IF VERIFICATO..." + curRow);

                                } else {
                                    // System.out.println("= IN DB TABLE HO APPENA PARSATO UN IF NON VERIFICATO..." + curRow);
                                    // jump alla riga di exit
                                    curRow = triggers[triggerStack].getBlockEnd();
                                    // System.out.println("= IN DB TABLE SALTO ALLA RIGA " + curRow);
                                }

                            } else if (loopRow.getElement().equalsIgnoreCase("else")) {
                                // System.out.println("= IN DB TABLE HO APPENA PARSATO UN ELSE..." + curRow);
                                if (curRow >= triggers[triggerStack].getBlockEnd()) {

                                    curRow = triggers[triggerStack].getExitRow() - 1;
                                    //   System.out.println("= IN DB TABLE SALTO ALLA RIGA " + curRow);
                                }

                            }
                        } //fine loop righe script
                        //      System.out.println("FINE LOOP righe dello script per singola riga database " + DBrows);

                        //  System.out.println(docName + " =================fine  parsing righe script Args [" + richArgList.size() + "/" + argList.size());
                        richArgList = cloneArray(argList);
                    }// fine loop del database
//                    System.out.println("FINE LOOP righe del database ");

                } catch (SQLException ex) {
                    Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
                }
                isDatabaseTable[TablesStack] = false;
                row = rigaFine - 1;// comunque dovrò eseguire la riga di chiusura
                //    System.out.println("riprendo regolare esecuzione da " + row + "+1 per chiudere la tabella");
            } else if (myRow.getElement().equalsIgnoreCase("if")) {

                if (triggers[triggerStack].isVerificato()) {
                    // System.out.println("=HO APPENA PARSATO UN IF VERIFICATO..." + row);

                } else {
                    // System.out.println("=HO APPENA PARSATO UN IF NON VERIFICATO..." + row);
                    // jump alla riga di exit
                    row = triggers[triggerStack].getBlockEnd();
                    //  System.out.println("=SALTO ALLA RIGA " + row);
                }

            } else if (myRow.getElement().equalsIgnoreCase("else")) {
                if (row >= triggers[triggerStack].getBlockEnd()) {
                    row = triggers[triggerStack].getExitRow() - 1;
                }

            }
            row++;
        }
//        System.out.println("Fine report rows:" + row);
        //-------------------------------------------
        //chiudo il documento 
        if (docType != null && docType.equalsIgnoreCase("mainDocument")) {
//            System.out.println("=========================CHIUDO " + docName);
            try {
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class TableHeader extends PdfPageEventHelper {

        PdfTemplate total;

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 16); //width, Height
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
//            if (writer.getCurrentPageNumber() == 1) {
//                return;
//            }

//            System.out.println("TableHeader -> onEndPage. Sono in fine pagina: currentHeaderDoc:" + currentHeaderDoc);
//            System.out.println("page number:" + String.valueOf(writer.getPageNumber() - 1));
//            System.out.println("page current number:" + String.valueOf(writer.getCurrentPageNumber() - 1));
//            System.out.println("*************************************\n\n");
            if (currentHeaderDoc != null && currentHeaderDoc.length() > 1) {
                PdfPTable table = new PdfPTable(1);
                table.setTotalWidth(470);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(10);
                table.getDefaultCell().setBorder(0);
                PdfPCell cell = new PdfPCell();
                //cell.setBorder(Rectangle.BOTTOM);
                cell.setBorder(0);
                PDFdoc innerDoc = new PDFdoc(stream, document, writer, richArgList, myParams, mySettings);
                PdfPTable innetTable = new PdfPTable(1);
                innetTable = (PdfPTable) innerDoc.fillDocument(currentHeaderDoc, "table");

                cell.addElement(innetTable);
                table.addCell(cell);
                float THeight = table.getTotalHeight();
                float pageHeight = PDFpageSize.getHeight();
                float Ypos = pageHeight - THeight;
//                System.out.println("THeight:" + THeight + "   pageHeight:" + pageHeight + "   Ypos:" + Ypos);
                //(int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte canvas) 
                //  Rectangle(842 ,595 );
                ////table.writeSelectedRows(0, -1, 64, 833, writer.getDirectContent());
                table.writeSelectedRows(0, -1, 64, 580, writer.getDirectContent());

            }

        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            //public static void showTextAligned(PdfContentByte canvas, int alignment, Phrase phrase, float x, float y, float rotation) {
            Font font = new Font(Font.FontFamily.HELVETICA, 10);
            Phrase phraseX = new Phrase(String.valueOf(writer.getPageNumber() - 1), font);

            Phrase totPages = new Phrase(String.valueOf(writer.getPageNumber() - 1));
            System.out.println("  TOTAL PAGES : " + String.valueOf(writer.getPageNumber() - 1));
            totPages = new Phrase("ZZZZZ");
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT, phraseX, 2, 2, 0);

        }
    }

    public class test extends PdfPageEventHelper {

        private int _pg = 0;
        private BaseFont font;

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            _pg++;
            if (writer.getCurrentPageNumber() == 1) {
//                return;
            }
//            System.out.println("Sono in fine pagina: currentHeaderDoc:" + currentHeaderDoc);
//            System.out.println("page number:" + String.valueOf(writer.getPageNumber() - 1));
//            System.out.println("page current number:" + String.valueOf(writer.getCurrentPageNumber() - 1));

            if (currentHeaderDoc != null && currentHeaderDoc.length() > 1) {
                PdfPTable table = new PdfPTable(1);
                table.setTotalWidth(470);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(10);
                table.getDefaultCell().setBorder(0);
                PdfPCell cell = new PdfPCell();
                //cell.setBorder(Rectangle.BOTTOM);
                cell.setBorder(0);
                PDFdoc innerDoc = new PDFdoc(stream, document, writer, richArgList, myParams, mySettings);
                PdfPTable innetTable = new PdfPTable(1);
                innetTable = (PdfPTable) innerDoc.fillDocument(currentHeaderDoc, "table");

                cell.addElement(innetTable);
                table.addCell(cell);
//                table.writeSelectedRows(0, -1, 64, 833, writer.getDirectContent());
                float THeight = table.getTotalHeight();
                float pageHeight = PDFpageSize.getHeight();
                float Ypos = pageHeight;
//                System.out.println("THeight:" + THeight + "   pageHeight:" + pageHeight + "   Ypos:" + Ypos);
                table.writeSelectedRows(0, -1, PDFmarginLeft, Ypos, writer.getDirectContent());

            }

            PdfContentByte cb = writer.getDirectContent();
            cb.beginText();
            try {
                Rectangle pageSize = document.getPageSize();
                cb.setFontAndSize(font, 8);
                cb.setTextMatrix(pageSize.getLeft(40), pageSize.getBottom(15));
                String s = "Pag. " + _pg + "/";
                cb.showText(s);
                cb.addTemplate(template, pageSize.getLeft(40) + font.getWidthPoint(s, 8), pageSize.getBottom(15));
            } catch (Exception exc) {
                System.out.println("Errore in aggiunta TOTAL:" + exc.toString());
            }
            cb.endText();
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            super.onOpenDocument(writer, document);
            template = writer.getDirectContent().createTemplate(50, 50);
            try {
                font = BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            } catch (Exception exc) {
                System.out.println("Errore in aggiunta TOTAL:" + exc.toString());

            }
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            super.onCloseDocument(writer, document);

            template.beginText();
            try {
                template.setFontAndSize(font, 8);
                template.setTextMatrix(0f, 0f);
                template.showText("" + (writer.getPageNumber() - 1));
            } catch (Exception ex) {
                System.out.println("Errore in aggiunta TOTAL:" + ex.toString());

            }
            template.endText();
        }

    }

    public String loadScript(String scriptName, Connection conny) {
        String SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_reports() + " WHERE name = '" + scriptName + "'";

        PreparedStatement ps = null;
        ResultSet rs = null;
        String PDFcode = "";
        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            //   System.out.println("  DATA TABLE ESEGUO RIGHE DA: " + SQLphrase);
            while (rs.next()) {
                PDFcode = rs.getString("content");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
        }

        //   System.out.println(PDFcode);
        return PDFcode;
    }

    private void parseReportRow(PDFreportRow myRow, int rowM, ArrayList<PDFreportRow> reportRows) {
        int stackUsed = 0;
        try {
            int widthPercent = 95;
            if (TablesStack > 1 || destType != "") {
                widthPercent = 100;
            }
            //  System.out.println(docName + " - " + destType + " ->Riga " + rowM + ") element=" + myRow.getElement() + " cols:" + myRow.getCols() + " content:" + myRow.getContent() + " align:" + myRow.getAlignment() + " fontSize:" + myRow.getFontSize() + " backColor:" + myRow.getBackColor().toString());
            if (myRow.getElement().equalsIgnoreCase("openTable")) {
                TablesStack++;
                rs[TablesStack] = null;
                //       System.out.println("\n@@@" + docName + "\n\n LO STACK DI DTABELLADB PASSA A " + TablesStack);
                Tables[TablesStack] = new PdfPTable(myRow.getCols()); // cols sono le colonne
                cols[TablesStack] = myRow.getCols();
                Tables[TablesStack].setWidthPercentage(widthPercent);
                Tables[TablesStack].setSplitLate(false);
                isDatabaseTable[TablesStack] = false;
                if (TablesStack > 0) {
                    rs[TablesStack] = rs[TablesStack - 1];
                }
            } else if (myRow.getElement().equalsIgnoreCase("openDataTable")) {
                TablesStack++;
//                System.out.println("Riga script: " + myRow.position + " --- entro in tabella di " + TablesStack + " livello");
                Tables[TablesStack] = new PdfPTable(myRow.getCols());
                cols[TablesStack] = myRow.getCols();
                Tables[TablesStack].setWidthPercentage(widthPercent);
                Tables[TablesStack].setSplitLate(myRow.isSplitLate());

//                try {
//                    Tables[TablesStack].setSplitRows(myRow.isSplitRows());
//                } catch (Exception e) {
//                }
//                try {
//                    Tables[TablesStack].setKeepTogether(myRow.isKeepTogether());
//                } catch (Exception e) {
//                }
                isDatabaseTable[TablesStack] = true;
            } else if (myRow.getElement().equalsIgnoreCase("openDataBase")) {
//                System.out.println("--- openDataBase entro in tabella di " + TablesStack + " livello");
                TablesStack++;
                isDatabaseTable[TablesStack] = true;
            } else if (myRow.getElement().equalsIgnoreCase("cell")) {
                String phrase = "";
                PDFcell cellOne = new PDFcell();
                // elemento cell in tabella dati
                //  if (isDatabaseTable[TablesStack] == true) {
                // tabella dati elemento FIELD
                if (myRow.getType().equalsIgnoreCase("field")) {
//--------------------- 
                    stackUsed = TablesStack;
                    int flag = 0;
//                    System.out.println("\n#\n");
                    // field tipo INT

//                    System.out.println("myRow.getFieldType() :" + myRow.getFieldType());
//                    System.out.println("myRow.getLayout() :" + myRow.getLayout());
                    if (myRow.getFieldType().equalsIgnoreCase("INT")) {
                        if (myRow.getLayout().equalsIgnoreCase("checkbox")) {
                            int value = 0;
                            try {
                                value = rs[stackUsed].getInt(myRow.getFieldName());
                            } catch (SQLException ex) {
                                Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (value > 0) {
                                phrase = "4";
                            } else {
                                phrase = "r";
                            }
                            Font font = new Font(Font.FontFamily.ZAPFDINGBATS, myRow.getFontSize());
                            Phrase phraseX = new Phrase(phrase, font);
                            cellOne.setPhrase(phraseX);
                            cellOne.setColspan(myRow.getCols());
                            cellOne.setHorizontalAlignment(myRow.getAlignment());
                            cellOne.setNoWrap(true);
                            cellOne.setBorder(myRow.getBorder());
                        } else if (myRow.getLayout().equalsIgnoreCase("checkbox1No")) {
                            int value = 0;
                            try {
                                value = rs[stackUsed].getInt(myRow.getFieldName());
                            } catch (SQLException ex) {
                                Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (value > 0) {
                                phrase = "8";
                            } else {
                                phrase = "r";
                            }

                            Font font = new Font(Font.FontFamily.ZAPFDINGBATS, myRow.getFontSize());
                            Phrase phraseX = new Phrase(phrase, font);

                            cellOne.setPhrase(phraseX);
                            cellOne.setColspan(myRow.getCols());
                            cellOne.setHorizontalAlignment(myRow.getAlignment());
                            cellOne.setNoWrap(true);
                            cellOne.setBorder(myRow.getBorder());

                        } else if (myRow.getLayout().equalsIgnoreCase("checkbox1Yes0No")) {
                            int value = 0;
                            try {
                                value = rs[stackUsed].getInt(myRow.getFieldName());
                            } catch (SQLException ex) {
                                Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (value > 0) {
                                phrase = "4";
                            } else {
                                phrase = "8";
                            }

                            Font font = new Font(Font.FontFamily.ZAPFDINGBATS, myRow.getFontSize());
                            Phrase phraseX = new Phrase(phrase, font);

                            cellOne.setPhrase(phraseX);
                            cellOne.setColspan(myRow.getCols());
                            cellOne.setHorizontalAlignment(myRow.getAlignment());
                            cellOne.setNoWrap(true);
                            cellOne.setBorder(myRow.getBorder());

                        } else {
                            //  System.out.println("INT type: null");
                            try {
                                phrase = "" + rs[stackUsed].getInt(myRow.getFieldName());
                                cellOne.fillCell(phrase, myRow);
                            } catch (SQLException ex) {
                                Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } // field tipo DATE
                    else if (myRow.getFieldType().equalsIgnoreCase("DATE")) {
                        try {
                            phrase = rs[stackUsed].getString(myRow.getFieldName());
                            if (phrase != null && phrase.length() > 9) {
                                String sep = phrase.substring(4, 5);

                                if (sep.equalsIgnoreCase("-")) {
                                    String anno = phrase.substring(0, 4);
                                    String mese = phrase.substring(5, 7);
                                    String giorno = phrase.substring(8, 10);
                                    phrase = giorno + "/" + mese + "/" + anno;
                                }
                            } else {
//                                System.out.println("data non presente o non corretta in " + myRow.getFieldName());
                                phrase = "#" + rs[stackUsed].getString(myRow.getFieldName());//data non presente o non corretta
                            }
                            cellOne.fillCell(phrase, myRow);
                        } catch (SQLException ex) {
                            Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } // field tipo PIC
                    else if (myRow.getFieldType().equalsIgnoreCase("PIC")) {
//                        System.out.println("richiesta stampa immagine da field " + myRow.getFieldName());
                        Blob blob;
                        try {
                            blob = rs[stackUsed].getBlob(myRow.getFieldName());
                        } catch (SQLException ex) {
                            Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
                            blob = null;
                        }
                        InputStream in = null;

                        try {
                            if (blob != null) {
                                in = blob.getBinaryStream();
                            }
                        } catch (SQLException ex) {
                        }
                        EVOpagerDirectivesManager myDirective = new EVOpagerDirectivesManager(myParams, mySettings);

                        BufferedImage bi;
                        Image myLogo = null;
                        if (in == null) {
//                            System.out.println("L'immagine è vuota... occorre caricare un segnaposto");

                            myLogo = myDirective.getDirectiveMedia("imagePlaceholder");

                        } else {

                            try {

                                if (in != null) {
                                    bi = ImageIO.read(in);
                                    if (bi != null) {
                                        try {
                                            myLogo = Image.getInstance(bi, null);
                                        } catch (BadElementException ex) {
                                            Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    } else {
//                                        System.out.println("L'immagine è vuota!... occorre caricare un segnaposto");
                                        myLogo = myDirective.getDirectiveMedia("imagePlaceholder");
                                    }
                                }

                            } catch (IOException ex) {
                                Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (myLogo != null) {
//                            System.out.println("myLogo non è vuoto..");

                            if (myRow.getBoxWidth() > 0 && myRow.getBoxHeight() > 0) {
                                Rectangle bs;
                                bs = new Rectangle(myRow.getBoxWidth(), myRow.getBoxWidth());
                                myLogo.scaleAbsoluteWidth(myRow.getBoxWidth());
                                myLogo.scaleAbsoluteHeight(myRow.getBoxWidth());
                                myLogo.scaleToFit(bs);
                            }

                            cellOne.fillCell(myLogo, myRow);
                        } else {
//                            System.out.println("myLogo è vuoto..");
                            cellOne.fillCell(" ", myRow);
                        }
                    } else if (myRow.getFieldType().equalsIgnoreCase("minToHours")) {
                        try {
                            phrase = rs[stackUsed].getString(myRow.getFieldName());
                            try {
//                                System.out.println("phrase:" + phrase);
                                int no = Integer.parseInt(phrase);
//                                System.out.println("no:" + no);
                                int hours = (int) (no / 60); //since both are ints, you get an int
                                int minutes = (int) (no % 60);
                                phrase = hours + "h " + minutes + "m";
//                                System.out.println("phrase:" + phrase);
                            } catch (Exception e) {
                            }
                        } catch (SQLException ex) {
                            System.out.println("\n@@@\n\n ERRORE su minToHours ");
                        }
                        cellOne.fillCell(phrase, myRow);

                    } else if (myRow.getFieldType().equalsIgnoreCase("euro")) {
                        try {

                            float Xamount = rs[stackUsed].getFloat(myRow.getFieldName());
                            DecimalFormat df = new DecimalFormat("#.00");
                            float number = Xamount;
                            try {

                                String strno = df.format(Xamount);
                                strno = strno.replace(",", ".");
                                number = Float.valueOf(strno);
                            } catch (Exception e) {
                                System.out.println("error rounding:  " + e.toString());
                            }
                            phrase = "€ " + number;
                        } catch (SQLException ex) {
                            System.out.println("\n@@@\n\n ERRORE su euro ");
                        }
                        cellOne.fillCell(phrase, myRow);
                    } else if (myRow.getFieldType().equalsIgnoreCase("PERCENT")) {
                        try {

                            float Xamount = rs[stackUsed].getFloat(myRow.getFieldName());
                            DecimalFormat df = new DecimalFormat("#.00");
                            float number = Xamount;
                            try {

                                String strno = df.format(Xamount);
                                strno = strno.replace(",", ".");
                                number = Float.valueOf(strno);
                            } catch (Exception e) {
                                System.out.println("error rounding:  " + e.toString());
                            }
                            phrase = "" + number+"%";
                        } catch (SQLException ex) {
                            System.out.println("\n@@@\n\n ERRORE su PERCENT ");
                        }
                        cellOne.fillCell(phrase, myRow);
                    } // field tipo TEXT 
                    else { // per default è un text
                        try {
//                            System.out.println("Cerco campo: " + myRow.getFieldName());
                            phrase = rs[stackUsed].getString(myRow.getFieldName());
//                            System.out.println("Assegno contenuto alla cella(" + myRow.getFieldName() + ") :" + phrase);

//                            System.out.println("myRow.getFieldType() :" + myRow.getFieldType() + "   FONT SIZE:" + myRow.getFontSize());
                            if (myRow.getLayout().equalsIgnoreCase("barcode")) {
                                Image BCimage = getBarcode(phrase, myRow.getStyle());
                                BCimage.setScaleToFitLineWhenOverflow(true);
                                BCimage.scaleAbsoluteHeight(myRow.getFontSize());
                                BCimage.scalePercent(98);
                                cellOne.fillCell(BCimage, myRow);

                            } else if (myRow.getLayout().equalsIgnoreCase("minToHours")) {
                                int no = 0;
                                try {
                                    no = Integer.parseInt(phrase);
                                    int hours = (int) (no / 60); //since both are ints, you get an int
                                    int minutes = (int) (no % 60);
                                    phrase = hours + "h " + minutes + "m";
                                } catch (Exception e) {
                                }
                                cellOne.fillCell(phrase, myRow);

                            } else {

                                cellOne.fillCell(phrase, myRow);
                            }
                        } catch (SQLException ex) {

                            System.out.println("\n@@@\n\n ERRORE :"
                                    + " - TablesStack: " + TablesStack
                                    + " - myRow.getFieldName(): " + myRow.getFieldName()
                            );
                            Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    //  System.out.println("### TablesStack:" + TablesStack + " stackUsed:" + stackUsed + " FieldName:" + myRow.getFieldName() + " phrase:" + phrase);

                } else if (myRow.getType().equalsIgnoreCase("variable")) {
                    for (int jj = 0; jj < this.varsList.size(); jj++) {
                        String xMarker = this.varsList.get(jj).getName();
                        String xValue = "" + this.varsList.get(jj).getValueInt();
                        if (myRow.getName().equalsIgnoreCase(xMarker)) {
                            phrase = "" + xValue;
                            cellOne.fillCell(phrase, myRow);
                        }
                    }
                } // tabella dati elemento LABEL
                else if (myRow.getType().equalsIgnoreCase("PDFpageNumber")) {
                    PdfPTable PNT = new PdfPTable(2);
                    PNT.setWidthPercentage(widthPercent);
                    phrase = String.format("Pag. %d ", writer.getPageNumber());
                    PDFcell cellX = new PDFcell();
                    cellX.fillCell(phrase, myRow);
                    cellX.setBorder(0);
                    PNT.addCell(cellX);
                    PdfPCell cell;
                    cell = new PdfPCell();
                    cell.setBorder(0);
                    Font font = new Font(Font.FontFamily.HELVETICA, myRow.getFontSize());
                    Phrase phraseX = new Phrase(" ", font);
                    cell.setPhrase(phraseX);
                    PNT.addCell(cell);
                    cellOne.setColspan(myRow.getCols());
                    cellOne.setHorizontalAlignment(myRow.getAlignment());
                    cellOne.setNoWrap(true);
                    cellOne.setBorder(myRow.getBorder());
                    cellOne.addElement(PNT);
                } else {
                    // LABEL
                    phrase = myRow.getContent();// suppongo per default che sia una label  
//                    System.out.println("label ->phrase :: " + phrase);
                    if (phrase == null || phrase.length() < 1) {
                        phrase = " ";
                    } else {
                        phrase = browserArgsReplace(phrase, null);
                    }

                    if (myRow.getLayout().equalsIgnoreCase("checkbox")) {
                        String Lphrase;
                        if (phrase.equalsIgnoreCase("")
                                || phrase.equalsIgnoreCase(" ")
                                || phrase.equalsIgnoreCase("0")) {
                            Lphrase = "r";
                        } else {
                            Lphrase = "4";
                        }
                        Font font = new Font(Font.FontFamily.ZAPFDINGBATS, myRow.getFontSize());
                        Phrase phraseX = new Phrase(Lphrase, font);
                        cellOne.setPhrase(phraseX);
                        cellOne.setColspan(myRow.getCols());
                        cellOne.setHorizontalAlignment(myRow.getAlignment());
                        cellOne.setNoWrap(true);
                        cellOne.setBorder(myRow.getBorder());

                    } else {
                        cellOne.fillCell(phrase, myRow);
                    }

                }
//stackUsed = TablesStack;
                try {
                    Tables[TablesStack].addCell(cellOne);
                } catch (Exception e) {
                }
            } else if (myRow.getElement().equalsIgnoreCase("lineSpace")
                    || myRow.getElement().equalsIgnoreCase("spaceLine")
                    || myRow.getElement().equalsIgnoreCase("space")) {
                String phrase = " ";
                PDFcell cellOne = new PDFcell();
                myRow.setCols(cols[TablesStack]);
                //     System.out.println("\n aggiungo riga vuota da " + cols[TablesStack] + " colonne.\n");
                cellOne.fillCell(phrase, myRow);
                Tables[TablesStack].addCell(cellOne);
            } else if (myRow.getElement().equalsIgnoreCase("closeTable")) {

                if (CellStack > 0) {
//                    System.out.println("\n " + docName + " CTCTCTCTTCTCTCCT CHIUDO TABLE IN openCELL");
                    openCell[CellStack].addElement(Tables[TablesStack]);
                } else {
                    if (destType.equalsIgnoreCase("table")) {
//                        System.out.println("\n " + docName + " CTCTCTCTTCTCTCCT CHIUDO TABLE IN mainDestTable");
                        PDFcell myCell = new PDFcell();
                        myCell.setColspan(1);
                        myCell.setBorder(0);
                        myCell.addElement(Tables[TablesStack]);
                        mainDestTable.setKeepTogether(myRow.isKeepTogether());
//                        mainDestTable.setSplitRows(false);
                        mainDestTable.setWidthPercentage(100);
                        mainDestTable.addCell(myCell);
                    } else {
//                        System.out.println("\n " + docName + " CTCTCTCTTCTCTCCT CHIUDO TABLE IN document");
                        document.add(Tables[TablesStack]);
                    }
                }

//                System.out.println("--- closeDatabase  chiudo tabella   di " + TablesStack + " livello");
                isDatabaseTable[TablesStack] = false;
                rs[TablesStack] = null;
                Tables[TablesStack] = null;
                TablesStack--;
//                System.out.println("--- TablesStack:" + TablesStack);
            } else if (myRow.getElement().equalsIgnoreCase("closeDatabase")) {
//                System.out.println("--- closeDatabase  chiudo tabella virtuale di " + TablesStack + " livello");
                isDatabaseTable[TablesStack] = false;
                rs[TablesStack] = null;
                Tables[TablesStack] = null;
                TablesStack--;
            } else if (myRow.getElement().equalsIgnoreCase("openCell")) {
                //  System.out.println("\n " + docName + " OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                CellStack++;
                openCell[CellStack] = new PDFcell();
                openCell[CellStack].setColspan(myRow.getCols());
                openCell[CellStack].setBorder(myRow.getBorder());
                //openCell[CellStack].setBorderColor(BaseColor.RED);
                openCell[CellStack].setPaddingLeft(0);

                openCell[CellStack].setPaddingRight(0);
                openCell[CellStack].setPaddingTop(0);
                openCell[CellStack].setPaddingBottom(0);
                //    System.out.println("\n!!!APRO CELLA opencell");

            } else if (myRow.getElement().equalsIgnoreCase("closeCell")) {
                //  System.out.println("\n " + docName + " CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
                try {
                    Tables[TablesStack].addCell(openCell[CellStack]);
                } catch (Exception e) {
                }

                openCell[CellStack] = null;
                CellStack--;
                //   System.out.println("\n " + docName + " CHIUDO CELLA opencell");
            } else if (myRow.getElement().equalsIgnoreCase("PDFdoc")) {
                if (myRow.getType().equalsIgnoreCase("Header")) {
                    currentHeaderDoc = myRow.getContent(); // assegno il nome del documento HEADER
//                    System.out.println("\nASSEGNO HEADER DOC: " + currentHeaderDoc);
                } else if (myRow.getType().equalsIgnoreCase("Calendar")) {
//                    System.out.println("=========================" + docName + "==CHIAMA DOC CALENDARIO=======================" + myRow.content);
//                    System.out.println("argList =" + argList.toString());
//************************************************
                    Calendar clnd = Calendar.getInstance();
                    int year = clnd.get(Calendar.YEAR);
                    int month = 1 + clnd.get(Calendar.MONTH);

                    String anno = "###ANNO###";
                    String mese = "###MESE###";

                    anno = ArgsReplace(anno, argList);
//                    System.out.println("anno =" + anno);
                    mese = ArgsReplace(mese, argList);
//                    System.out.println("mese =" + mese);
                    try {
                        year = Integer.parseInt(anno);
                        month = Integer.parseInt(mese);
                    } catch (Exception e) {
                    }
                    smartCalendar myCalendar = new smartCalendar(calendarMyPortal.myParams, calendarMyPortal.mySettings);
//                    System.out.println("!!!myGate.getSendToCRUD() =" + calendarMyGate.getSendToCRUD());
//                    System.out.println("!!!myGate.getTBS() =" + calendarMyGate.getTBS());
                    myCalendar.step1drawFrames(calendarMyGate, year, month, "INSIDEUPDATE");
                    myCalendar.step2addBoxes();
                    myCalendar.step3chargeDuties();
                    document.add(myCalendar.step5drawPDF());

                } else {
//                    System.out.println("=========================" + docName + "==CHIAMA DOC FIGLIO=======================");
                    PDFdoc innerDoc = new PDFdoc(stream, document, writer, richArgList, myParams, mySettings);
                    if (CellStack > 0) {

                        System.out.println("\n " + docName + " Sono in cella aperta, inserisco DOC in table:");
                        PdfPTable innetTable = new PdfPTable(1);
                        innetTable = (PdfPTable) innerDoc.fillDocument(myRow.getContent(), "table");
                        // openCell[CellStack].setBorderColor(BaseColor.RED);
                        openCell[CellStack].addElement(innetTable);
                        innetTable = null;

                    } else {
//                        System.out.println("\n " + docName + " inserisco un DOC in coda al DOC principale");

                        innerDoc.fillDocument(myRow.getContent(), "document");
//                        System.out.println("...fatto");

                    }
                    innerDoc = null;
                }
            } else if (myRow.getElement().equalsIgnoreCase("if")) {
                triggerStack++;
                // 1. trovo la fine del blocco (else oppure endif
                //2. valuto se il trigger è verificato...
                // se si continuo, altrimenti jump al fine blocco
                triggers[triggerStack] = new reportRowTrigger();
                triggers[triggerStack] = getTriggerBlockEnd(rowM, reportRows);
                triggers[triggerStack].setTrigger(myRow.getContent());

                String[] items = myRow.getContent().split("==");
                if (items.length > 0 && items[0] != null && items[1] != null) {
                    try {
                        triggers[triggerStack].elementoA = browserArgsReplace(items[0], null);
                    } catch (Exception e) {
                    }
                    try {
                        triggers[triggerStack].elementoB = browserArgsReplace(items[1], null);
                    } catch (Exception e) {
                    }
                    triggers[triggerStack].confronto = "==";

                    if (triggers[triggerStack].elementoA.equals(triggers[triggerStack].elementoB)) {
                        triggers[triggerStack].verificato = true;
//                        System.out.println("VALUTO TRIGGER :"
//                                + triggers[triggerStack].elementoA
//                                + triggers[triggerStack].confronto
//                                + triggers[triggerStack].elementoB
//                                + " ==> VERIFICATO ");
                    } else {
                        triggers[triggerStack].verificato = false;
//                        System.out.println("VALUTO TRIGGER :"
//                                + triggers[triggerStack].elementoA
//                                + triggers[triggerStack].confronto
//                                + triggers[triggerStack].elementoB
//                                + " ==> NON VERIFICATO ");
                    }

                } else {
                    items = myRow.getContent().split("!=");
                    if (items.length > 0) {

                    } else {

                    }
                }

                /*
                     if (row >= triggers[triggerStack].getBlockEnd()) {
                        // jump alla riga di exit
                        row = triggers[triggerStack].getExitRow();
                    }
                 */
            } else if (myRow.getElement().equalsIgnoreCase("else")) {

            } else if (myRow.getElement().equalsIgnoreCase("endif")) {
                triggers[triggerStack] = null;
                triggerStack--;
            } else if (myRow.getElement().equalsIgnoreCase("variable")) {
                String nome = myRow.getName();
                scriptVariable myVar = new scriptVariable();
                int flagFound = -1;
                for (int jj = 0; jj < varsList.size(); jj++) {
                    if (varsList.get(jj).name.equalsIgnoreCase(nome)) {
                        flagFound = jj;
                    }
                    break;
                }
                if (flagFound >= 0) {
                    // la variabile esiste e la aggiorno
                    if (myRow.getOperation().equalsIgnoreCase("==")) {
                        try {
                            varsList.get(flagFound).valueInt = Integer.parseInt(myRow.getContent());
                        } catch (Exception e) {
                        }
                    } else if (myRow.getOperation().equalsIgnoreCase("+=")) {
                        try {
                            int actual = varsList.get(flagFound).valueInt;
                            varsList.get(flagFound).valueInt = actual + Integer.parseInt(myRow.getContent());
                        } catch (Exception e) {
                            System.out.println("Errore 1388:" + e.toString());
                        }
                    }

                } else {
                    // la variabile non esiste e la creo

                    myVar = new scriptVariable();
                    myVar.name = nome;
                    if (myRow.getOperation().equalsIgnoreCase("==")) {
                        try {
                            myVar.valueInt = Integer.parseInt(myRow.getContent());
                        } catch (Exception e) {
                            System.out.println("Errore 1401:" + e.toString());
                        }
                    } else if (myRow.getOperation().equalsIgnoreCase("+=")) {
                        try {
                            myVar.valueInt = Integer.parseInt(myRow.getContent());
                        } catch (Exception e) {
                            System.out.println("Errore 1406:" + e.toString());
                        }
                    }
                    varsList.add(myVar);
                }

            } else if (myRow.getElement().equalsIgnoreCase("newPage")) {

                document.newPage();
                writer.setPageEmpty(false);
            } else if (myRow.getElement().equalsIgnoreCase("PDFsettings")) {

            }
        } catch (DocumentException ex) {
            Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<PDFreportRow> scriptLoader(String XdocName, Connection conny) {

//        System.out.println("\n\n*******\nSONO IN SCRIPT LOADER");
        ArrayList<PDFreportRow> reportRows = new ArrayList<PDFreportRow>();
        docName = XdocName;
        String myScript;
        myScript = loadScript(docName, conny);
        //----end parsing script 
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        String documentArray = myScript;

//        System.out.println("\nmyScript:" + myScript + "\n");
        JSONArray docArray;
        JSONParser parser;
        int riga = 0;
        if (documentArray != null && documentArray.length() > 0) {
            JSONParser docParser = new JSONParser();
            Object docObj;

            try {
                docObj = docParser.parse(documentArray);
                docArray = (JSONArray) docObj;
                for (Object bloccoDocumento : docArray) {
                    riga++;
                    PDFreportRow myRow = new PDFreportRow();
                    myRow.position = riga;
                    // System.out.println("==>"+bloccoDocumento.toString());
                    jsonObject = (JSONObject) jsonParser.parse(bloccoDocumento.toString());
                    try {
                        myRow.setElement(jsonObject.get("element").toString());
                    } catch (Exception e) {
                        myRow.setElement("");
                    }
                    try {
                        myRow.setContent(jsonObject.get("content").toString());
                    } catch (Exception e) {
                        myRow.setContent("");
                    }
                    try {
                        myRow.setOperation(jsonObject.get("operation").toString());
                    } catch (Exception e) {
                        myRow.setOperation("");
                    }
                    //System.out.println("=========================");
                    try {
                        myRow.setType(jsonObject.get("type").toString());
                    } catch (Exception e) {
                        myRow.setType("");
                    }
                    try {
                        myRow.setName(jsonObject.get("name").toString());
                    } catch (Exception e) {
                        myRow.setName("");
                    }
                    try {
                        myRow.setRows(Integer.parseInt(jsonObject.get("rows").toString()));
                    } catch (Exception e) {
                        myRow.setRows(1);
                    }
                    try {
                        myRow.setCols(Integer.parseInt(jsonObject.get("cols").toString()));
                    } catch (Exception e) {
                        myRow.setCols(1);
                    }
                    try {
                        myRow.setQuery(jsonObject.get("query").toString());
                    } catch (Exception e) {
                        myRow.setQuery("");
                    }
                    try {
                        myRow.setFieldName(jsonObject.get("fieldName").toString());
                    } catch (Exception e) {
                        myRow.setFieldName("");
                    }
                    try {
                        myRow.setFieldType(jsonObject.get("fieldType").toString());
                    } catch (Exception e) {
                        myRow.setFieldType("");
                    }
                    try {
                        myRow.setLayout(jsonObject.get("layout").toString());
                    } catch (Exception e) {
                        myRow.setLayout("");
                    }
                    String align = "";
                    try {
                        align = (jsonObject.get("align").toString());
                    } catch (Exception e) {
                        align = ("LEFT");
                    }
                    if (align.equalsIgnoreCase("CENTER")) {
                        myRow.setAlignment(Element.ALIGN_CENTER);
                    } else if (align.equalsIgnoreCase("RIGHT")) {
                        myRow.setAlignment(Element.ALIGN_RIGHT);
                    } else if (align.equalsIgnoreCase("TOP")) {
                        myRow.setAlignment(Element.ALIGN_TOP);
                    } else if (align.equalsIgnoreCase("TOPCENTER")) {
                        myRow.setAlignment(Element.ALIGN_TOP);

                    } else {
                        myRow.setAlignment(Element.ALIGN_LEFT);
                    }

                    String color = "";
                    try {
                        color = jsonObject.get("color").toString();
                    } catch (Exception e) {
                        color = ("");
                    }
                    myRow.setColor(BCtransform(color, BaseColor.BLACK));
                    // System.out.println("color:" + color + " -  setColor:" + myRow.getColor().toString());

                    try {
                        color = jsonObject.get("backColor").toString();
                    } catch (Exception e) {
                        color = ("");
                    }

                    myRow.setBackColor(BCtransform(color, BaseColor.WHITE));
                    // System.out.println("color:" + color + " -  setBackColor:" + myRow.getBackColor().toString());

                    try {
                        myRow.setFontSize(Integer.parseInt(jsonObject.get("fontSize").toString()));
                    } catch (Exception e) {
                        myRow.setFontSize(10);
                    }
                    try {
                        myRow.setStyle(jsonObject.get("style").toString());
                    } catch (Exception e) {
                        myRow.setStyle("plain");
                    }
                    try {
                        myRow.setBorder(Integer.parseInt(jsonObject.get("border").toString()));
                    } catch (Exception e) {
                        myRow.setBorder(0);
                    }
                    try {
                        myRow.setBorderWidth(Integer.parseInt(jsonObject.get("borderWidth").toString()));
                    } catch (Exception e) {
                        myRow.setBorderWidth(1);
                    }
                    int noWrap = 0;
                    try {
                        noWrap = (Integer.parseInt(jsonObject.get("noWrap").toString()));
                    } catch (Exception e) {
                        noWrap = 0;
                    }
                    if (noWrap > 0) {
                        myRow.setNoWrap(true);
                    } else {
                        myRow.setNoWrap(false);
                    }
                    String keepTogether = "false";
                    try {
                        keepTogether = jsonObject.get("keepTogether").toString();
                    } catch (Exception e) {
                        keepTogether = "false";
                    }
                    if (keepTogether.equalsIgnoreCase("true")) {
                        myRow.setKeepTogether(true);
                    } else {
                        myRow.setKeepTogether(false);
                    }

                    String SplitRows = "false";
                    try {
                        SplitRows = jsonObject.get("SplitRows").toString();
                    } catch (Exception e) {
                        SplitRows = "false";
                    }
                    if (keepTogether.equalsIgnoreCase("true")) {
                        myRow.setSplitRows(true);
                    } else {
                        myRow.setSplitRows(false);
                    }
                    String SplitLate = "false";
                    try {
                        SplitLate = jsonObject.get("SplitLate").toString();
                    } catch (Exception e) {
                        SplitLate = "false";
                    }
                    if (keepTogether.equalsIgnoreCase("true")) {
                        myRow.setSplitLate(true);
                    } else {
                        myRow.setSplitLate(false);
                    }

                    try {
                        myRow.setBoxWidth(Integer.parseInt(jsonObject.get("boxWidth").toString()));
                    } catch (Exception e) {
                        myRow.setBoxWidth(20);
                    }
                    try {
                        myRow.setBoxHeight(Integer.parseInt(jsonObject.get("boxHeight").toString()));
                    } catch (Exception e) {
                        myRow.setBoxHeight(20);
                    }
                    try {
                        myRow.setFixedHeight(Integer.parseInt(jsonObject.get("fixedHeight").toString()));
                        System.out.println("DA DATABASE IMPOSTO SU SCRIPT ALTEZZA FISSA:" + myRow.getFixedHeight());
                    } catch (Exception e) {
                        myRow.setFixedHeight(0);
                    }

                    try {
                        myRow.setPaddingLeft(Integer.parseInt(jsonObject.get("paddingLeft").toString()));
                    } catch (Exception e) {
                        myRow.setPaddingLeft(0);
                    }
                    try {
                        myRow.setPaddingRight(Integer.parseInt(jsonObject.get("paddingRight").toString()));
                    } catch (Exception e) {
                        myRow.setPaddingRight(0);
                    }
                    try {
                        myRow.setPaddingTop(Integer.parseInt(jsonObject.get("paddingTop").toString()));
                    } catch (Exception e) {
                        myRow.setPaddingRight(0);
                    }
                    try {
                        myRow.setPaddingBottom(Integer.parseInt(jsonObject.get("paddingBottom").toString()));
                    } catch (Exception e) {
                        myRow.setPaddingBottom(0);
                    }
                    try {
                        myRow.setLeading(Integer.parseInt(jsonObject.get("leading").toString()));
                        System.out.println("DA DATABASE IMPOSTO LEADING:" + myRow.getLeading());

                    } catch (Exception e) {
                        myRow.setLeading(-1);
                    }
                    reportRows.add(myRow);
                }
            } catch (ParseException ex) {
                Logger.getLogger(PDFdoc.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }

        for (int row = 0; row < reportRows.size(); row++) {

            PDFreportRow myRow = reportRows.get(row);
            parseForSettings(myRow);
        }
        return reportRows;
    }

    private void parseForSettings(PDFreportRow myRow) {
        if (myRow.getElement().equalsIgnoreCase("PDFsettings")) {

            if (myRow.getType().equalsIgnoreCase("pageSize")) {
                String box = myRow.getContent();
                if (box == null || box.length() < 1) {
                    box = "pageSize.A4";
                }
                //Rectangle(int x, int y, int width, int height)
                System.out.println("PageSize.A4= " + PageSize.A4.toString());
                PDFpageSize = PageSize.A4;
            } else if (myRow.getType().equalsIgnoreCase("pageBox")) {
                float width = 595;
                float height = 842; //misure in pixel su 72dpi
                float converter = (float) 2.8333334;
                // ricevo le misure in mm e le converto in pixel moltiplicando X 2.8333334
                width = (float) (myRow.getBoxWidth() * converter);
                height = (float) (myRow.getBoxHeight() * converter);
                PDFpageSize = new Rectangle(width, height);

            } else if (myRow.getType().equalsIgnoreCase("marginLeft")) {
                try {
                    PDFmarginLeft = Float.parseFloat(myRow.getContent());
                } catch (Exception e) {
                }
            } else if (myRow.getType().equalsIgnoreCase("marginRight")) {
                try {
                    PDFmarginRight = Float.parseFloat(myRow.getContent());
                } catch (Exception e) {
                }
            } else if (myRow.getType().equalsIgnoreCase("marginTop")) {
                try {
                    PDFmarginTop = Float.parseFloat(myRow.getContent());
                } catch (Exception e) {
                }
            } else if (myRow.getType().equalsIgnoreCase("marginBottom")) {
                try {
                    PDFmarginBottom = Float.parseFloat(myRow.getContent());
                } catch (Exception e) {
                }

            } else if (myRow.getType().equalsIgnoreCase("filename")) {
                richArgList = cloneArray(argList);
                String fn = browserArgsReplace(myRow.getContent(), null);
                try {
                    fn = java.net.URLDecoder.decode(fn, "UTF-8");
                } catch (Exception e) {
                }
                PDFfilename = fn;

            }
        }
    }

    public String ArgsReplace(String query, ArrayList<SelectListLine> argList) {

//         for (int jj = 0; jj < this.richArgList.size(); jj++) {
//              System.out.println("richArgList# " +  this.richArgList.get(jj).getLabel()+": " +  this.richArgList.get(jj).getValue());
//         }
//        for (int jj = 0; jj < this.varsList.size(); jj++) {
//              System.out.println("varsList@ " +  this.varsList.get(jj).getName()+": " +  this.varsList.get(jj).getValueInt());
//         }
//        
        if (query == null) {
            return null;
        }

        String defVal = query;
        try {
            defVal = defVal.replace("$$$USER$$$", myParams.getCKuserID());
        } catch (Exception e) {
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.getTime();
            SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat formatDateTime = new SimpleDateFormat("dd-MM-yyyy  HH:mm ");
            TimeZone timeZone = TimeZone.getTimeZone("Europe/Rome");
            formatDate.setTimeZone(timeZone);
            formatDateTime.setTimeZone(timeZone);
            String giorno = formatDate.format(cal.getTime());
            String giornoEora = formatDateTime.format(cal.getTime());
            defVal = defVal.replace("$$$PRINTDATE$$$", giorno);
            defVal = defVal.replace("$$$PRINTDATETIME$$$", giornoEora);
        } catch (Exception e) {
        }

        try {
            System.out.println("defVal: " + defVal);
            if (defVal.contains("$$$CURUSERNAME$$$")) {
                EVOuser myUser = new EVOuser(myParams, mySettings);
                String nomeUtente = myUser.getExtendedName();
                nomeUtente = nomeUtente.replaceAll("<BR>", " ");
                System.out.println("nomeUtente :" + nomeUtente);
                defVal = defVal.replace("$$$CURUSERNAME$$$", nomeUtente);

            }
        } catch (Exception e) {
//                System.out.println("CRUD ORDER: ERROR standardReplace :" + e.toString());
//                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);

        }

        for (int jj = 0; jj < argList.size(); jj++) {
            String xMarker = argList.get(jj).getLabel();
            String xValue = argList.get(jj).getValue();
            String toBeReplaced = "###" + xMarker + "###";
            if (defVal.contains(toBeReplaced)) {
                defVal = defVal.replace(toBeReplaced, xValue);
            }
        }

        return defVal;
    }

    public String browserArgsReplace(String query, String destination) {

//         for (int jj = 0; jj < this.richArgList.size(); jj++) {
//              System.out.println("richArgList# " +  this.richArgList.get(jj).getLabel()+": " +  this.richArgList.get(jj).getValue());
//         }
//        for (int jj = 0; jj < this.varsList.size(); jj++) {
//              System.out.println("varsList@ " +  this.varsList.get(jj).getName()+": " +  this.varsList.get(jj).getValueInt());
//         }
//        
        if (query == null) {
            return null;
        }

        String defVal = query;
        try {
            defVal = defVal.replace("$$$USER$$$", myParams.getCKuserID());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.getTime();
            SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat formatDateTime = new SimpleDateFormat("dd-MM-yyyy  HH:mm ");
            TimeZone timeZone = TimeZone.getTimeZone("Europe/Rome");
            formatDate.setTimeZone(timeZone);
            formatDateTime.setTimeZone(timeZone);
            String giorno = formatDate.format(cal.getTime());
            String giornoEora = formatDateTime.format(cal.getTime());
            defVal = defVal.replace("$$$PRINTDATE$$$", giorno);
            defVal = defVal.replace("$$$PRINTDATETIME$$$", giornoEora);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
//            System.out.println("defVal: " + defVal);
            if (defVal.contains("$$$CURUSERNAME$$$")) {
                EVOuser myUser = new EVOuser(myParams, mySettings);
                String nomeUtente = myUser.getExtendedName();
                nomeUtente = nomeUtente.replaceAll("<BR>", " ");
                defVal = defVal.replace("$$$CURUSERNAME$$$", nomeUtente);
            }
        } catch (Exception e) {
            System.out.println("browserArgsReplace: ERROR standardReplace :" + e.toString());
//                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);

        }
        try {
            if (this.richArgList != null && this.richArgList.size() > 0) {
//        System.out.println("\n#\n");
                for (int jj = 0; jj < this.richArgList.size(); jj++) {
                    String xMarker = this.richArgList.get(jj).getLabel();
                    String xValue = this.richArgList.get(jj).getValue();
                    if (destination != null && destination.equalsIgnoreCase("mysql")
                            && !xMarker.equalsIgnoreCase("masterQuery") //In caso di masterQuery non devo toglierre gli apici
                            ) {

                        xValue = encodeMYSQLstring(xValue);

                    } else {
                        try {
                            //                            String temp = java.net.URLDecoder.decode(xValue, "UTF-8");
                            String temp = replacer(xValue);
                            xValue = temp;
                        } catch (Exception e) {

                        }
                    }
                    String toBeReplaced = "###" + xMarker + "###";
//            System.out.println("toBeReplaced :" + toBeReplaced + "  ---> " + xValue);
                    if (defVal != null && defVal.contains(toBeReplaced)) {
                        defVal = defVal.replace(toBeReplaced, xValue);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("browserArgsReplace: richArgList ERROR   :" + e.toString());

        }
        try {
            if (this.varsList != null && this.varsList.size() > 0) {
                for (int jj = 0; jj < this.varsList.size(); jj++) {
                    String xMarker = this.varsList.get(jj).getName();
                    String xValue = "" + this.varsList.get(jj).getValueInt();
                    String toBeReplaced = "@@@" + xMarker + "@@@";
//            System.out.println("toBeReplaced :" + toBeReplaced + "  ---> " + xValue);

                    if (defVal != null && defVal.contains(toBeReplaced)) {
                        defVal = defVal.replace(toBeReplaced, xValue);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("browserArgsReplace: varsList ERROR   :" + e.toString());

        }
        return defVal;
    }

    public static String replacer(String text) {
        String data = text;
        try {
            data = data.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            data = data.replaceAll("\\+", "%2B");
            data = URLDecoder.decode(data, "utf-8");
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return data;
    }

    public ArrayList<SelectListLine> getArgList() {
        return argList;
    }

    public void setArgList(ArrayList<SelectListLine> argList) {
        this.argList = argList;
    }

    public BaseColor BCtransform(String color, BaseColor defaultBaseColor) {
        BaseColor myBaseColor = defaultBaseColor;
        if (color != null) {
            if (color.equalsIgnoreCase("BLUE")) {
                myBaseColor = BaseColor.BLUE;
            } else if (color.equalsIgnoreCase("CYAN")) {
                myBaseColor = BaseColor.CYAN;
            } else if (color.equalsIgnoreCase("DARKGRAY")) {
                myBaseColor = BaseColor.DARK_GRAY;
            } else if (color.equalsIgnoreCase("GRAY") || color.equalsIgnoreCase("GREY")) {
                myBaseColor = BaseColor.GRAY;
            } else if (color.equalsIgnoreCase("GREEN")) {
                myBaseColor = BaseColor.GREEN;
            } else if (color.equalsIgnoreCase("LIGHTGRAY") || color.equalsIgnoreCase("LIGHTGREY")) {
                myBaseColor = BaseColor.LIGHT_GRAY;
                //  System.out.println("settato lightGray");
            } else if (color.equalsIgnoreCase("MAGENTA")) {
                myBaseColor = BaseColor.MAGENTA;
            } else if (color.equalsIgnoreCase("ORANGE")) {
                myBaseColor = BaseColor.ORANGE;
            } else if (color.equalsIgnoreCase("PINK")) {
                myBaseColor = BaseColor.PINK;
            } else if (color.equalsIgnoreCase("RED")) {
                myBaseColor = BaseColor.RED;
            } else if (color.equalsIgnoreCase("WHITE")) {
                myBaseColor = BaseColor.WHITE;
            } else if (color.equalsIgnoreCase("YELLOW")) {
                myBaseColor = BaseColor.YELLOW;
            } else if (color.equalsIgnoreCase("BLACK")) {
                myBaseColor = BaseColor.BLACK;
            } else {
                myBaseColor = defaultBaseColor;
            }
        }
        return myBaseColor;
    }

    public JSONObject rsToJson(ResultSet Xrs) throws SQLException {
        JSONObject json = new JSONObject();
        ResultSetMetaData rsmd = Xrs.getMetaData();

        JSONObject obj = new JSONObject();
        //   while (Xrs.next()) {
        int numColumns = rsmd.getColumnCount();

        for (int i = 1; i <= numColumns; i++) {
            String column_name = rsmd.getColumnName(i);
            obj.put(column_name, Xrs.getObject(column_name));
        }

        //   }
        return obj;
    }

    public ArrayList<PDFreportRow> cloneScript(ArrayList<PDFreportRow> master) {
        ArrayList<PDFreportRow> clonedArray = new ArrayList<PDFreportRow>();
        for (int jj = 0; jj < master.size(); jj++) {
            clonedArray.add(master.get(jj));
        }
        return clonedArray;

    }

    public ArrayList<SelectListLine> cloneArray(ArrayList<SelectListLine> master) {

        ArrayList<SelectListLine> clonedArray = new ArrayList<SelectListLine>();
        for (int jj = 0; jj < master.size(); jj++) {
            clonedArray.add(master.get(jj));
        }
        return clonedArray;

    }

    public reportRowTrigger getTriggerBlockEnd(int inizio, ArrayList<PDFreportRow> reportRows) {
        reportRowTrigger myTrigger = new reportRowTrigger();
        int rigaFine = inizio - 1;// perchè per tornare a zero nel bilancio delle tables devo conteggiare anche quella corrrente
        int flagFound = 0;
        int triggersCount = 0;
        //System.out.println("Cerco conclusioone trigger da riga " + inizio);
        // per il triggerscount contano solo if (apre) e nendif (chiude)
        // se sono su un livello triggersCount diverso da UNO devgo ignorare gli else
        //se sono su livello UNO un else chiude iol blocco di esecuzione
        // se sono su livello UNO un endif porta a zero e chiude il blocco di exit (ripèrenderò l'esecuzione da quella riga in poi
        myTrigger.setBlockEnd(-1);
        while (rigaFine < reportRows.size() - 1) {
            rigaFine++;
            //System.out.println("getTriggerBlockEnd Riga " + rigaFine + " " + reportRows.get(rigaFine).getElement());
            if (reportRows.get(rigaFine).getElement().equalsIgnoreCase("if")) {
                triggersCount++;

            }

            if (reportRows.get(rigaFine).getElement().equalsIgnoreCase("else")) {
                if (triggersCount == 1) {
                    myTrigger.setBlockEnd(rigaFine);
                } else {

                }
            }
            if (reportRows.get(rigaFine).getElement().equalsIgnoreCase("endif")) {
                triggersCount--;
                if (triggersCount == 0) {
                    myTrigger.setExitRow(rigaFine);
                    if (myTrigger.getBlockEnd() < 0) {
                        myTrigger.setBlockEnd(rigaFine);
                    }
                    break;
                }

            }

        }

        //  System.out.println(" TRIGGER if di riga " + inizio + " finisce in riga  " + myTrigger.getBlockEnd() + " e ha EXIT in riga  " + myTrigger.getExitRow());
        return myTrigger;
    }

    public int getRange(int inizio, ArrayList<PDFreportRow> reportRows, String type) {
        //  int rigaInizio = inizio;
        int rigaFine = inizio - 1;// perchè per tornare a zero nel bilancio delle tables devo conteggiare anche quella corrrente
        int flagFound = 0;
        int tablesCount = 0;
//------------------------------------------
//valuto RANGE di righe da rieseguire
//        System.out.println("cerco range a partire da " + inizio + " in " + reportRows.size() + " righe totali.");
        if (type.equalsIgnoreCase("openTable") || type.equalsIgnoreCase("openDataTable")) {
            while (rigaFine < reportRows.size() - 1) {
                rigaFine++;
                if (reportRows.get(rigaFine).getElement().equalsIgnoreCase("openTable")
                        || reportRows.get(rigaFine).getElement().equalsIgnoreCase("openDataTable")) {
                    tablesCount++;

                }
                if (reportRows.get(rigaFine).getElement().equalsIgnoreCase("closeTable")) {
                    tablesCount--;

                }

                if ((reportRows.get(rigaFine).getElement().equalsIgnoreCase("closeTable"))
                        && tablesCount == 0) {
                    flagFound++;
                    break;
                }

                //    System.out.println("riga " + rigaFine + " -->" + reportRows.get(rigaFine).getElement() + " count=" + tablesCount);
            }
        } else {
            while (rigaFine < reportRows.size() - 1) {
                rigaFine++;
                if (reportRows.get(rigaFine).getElement().equalsIgnoreCase("openDatabase")) {
                    tablesCount++;

                }
                if (reportRows.get(rigaFine).getElement().equalsIgnoreCase("closeDatabase")) {
                    tablesCount--;

                }

                if ((reportRows.get(rigaFine).getElement().equalsIgnoreCase("closeDatabase"))
                        && tablesCount == 0) {
                    flagFound++;
                    break;
                }

                //    System.out.println("riga " + rigaFine + " -->" + reportRows.get(rigaFine).getElement() + " count=" + tablesCount);
            }
        }

        if (flagFound < 1) {
            //c'è un errore... tabella non chiusa
            System.out.println("errore... tabella non chiusa");
            rigaFine = -1;
        }
//        System.out.println("trovata chiusura in " + rigaFine);
        return rigaFine;

    }

    public class scriptVariable {

        String name;
        int valueInt;
        String valueString;
        String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValueInt() {
            return valueInt;
        }

        public void setValueInt(int valueInt) {
            this.valueInt = valueInt;
        }

        public String getValueString() {
            return valueString;
        }

        public void setValueString(String valueString) {
            this.valueString = valueString;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

    public class rowRange {

        int inizio;
        int fine;

        public int getInizio() {
            return inizio;
        }

        public void setInizio(int inizio) {
            this.inizio = inizio;
        }

        public int getFine() {
            return fine;
        }

        public void setFine(int fine) {
            this.fine = fine;
        }

    }

    public class reportRowTrigger {

        String trigger;
        String confronto;
        String elementoA;
        String elementoB;
        boolean verificato;
        int blockEnd;
        int exitRow;

        public String getTrigger() {
            return trigger;
        }

        public void setTrigger(String trigger) {
            this.trigger = trigger;
        }

        public String getConfronto() {
            return confronto;
        }

        public void setConfronto(String confronto) {
            this.confronto = confronto;
        }

        public String getElementoA() {
            return elementoA;
        }

        public void setElementoA(String elementoA) {
            this.elementoA = elementoA;
        }

        public String getElementoB() {
            return elementoB;
        }

        public void setElementoB(String elementoB) {
            this.elementoB = elementoB;
        }

        public boolean isVerificato() {
            return verificato;
        }

        public void setVerificato(boolean verificato) {
            this.verificato = verificato;
        }

        public int getBlockEnd() {
            return blockEnd;
        }

        public void setBlockEnd(int blockEnd) {
            this.blockEnd = blockEnd;
        }

        public int getExitRow() {
            return exitRow;
        }

        public void setExitRow(int exitRow) {
            this.exitRow = exitRow;
        }

    }

    public Image getBarcode(String text, String type) throws BadElementException {
        Image BCimage = null;
        if (type.equalsIgnoreCase("barcode128")) {
            Barcode128 code1281 = new Barcode128();
            code1281.setCode(text.trim());
            // code1281.setCodeType(Barcode128.CODE128_UCC);
            // code1281.setCodeType(Barcode128.CODE128); 
            code1281.setFont(null);
            code1281.setTextAlignment(Element.ALIGN_RIGHT);
            BCimage = code1281.createImageWithBarcode(writer.getDirectContent(), null, null);
        } else if (type.equalsIgnoreCase("barcode2D")) {
            BarcodeDatamatrix pf = new BarcodeDatamatrix();
            try {
                BarcodeDatamatrix dm = new BarcodeDatamatrix();
                dm.generate(text);
                BCimage = dm.createImage();

            } catch (UnsupportedEncodingException ex) {
                System.out.println("Errore UnsupportedEncodingException:" + ex.toString());
            }
        } else {
            Barcode39 code1281 = new Barcode39();
            code1281.setCode(text.trim());
            code1281.setFont(null);
            code1281.setTextAlignment(Element.ALIGN_RIGHT);
            BCimage = code1281.createImageWithBarcode(writer.getDirectContent(), null, null);
//            BCimage.scaleAbsoluteHeight(10);
        }

        return BCimage;
    }

    public Image get2DBarcode(String text) throws BadElementException {
        Image codeEANImage = null;
        BarcodeDatamatrix pf = new BarcodeDatamatrix();
        try {
            BarcodeDatamatrix dm = new BarcodeDatamatrix();
            dm.generate(text);
            codeEANImage = dm.createImage();

        } catch (UnsupportedEncodingException ex) {
            System.out.println("Errore UnsupportedEncodingException 1875:" + ex.toString());

        }

        return codeEANImage;
    }

    public static String encodeMYSQLstring(String s) {
        String result = s;
        try {
            result = s.replaceAll("\\'", "\\\\'");
        } catch (Exception e) {
            result = s;
        }
        return result;
    }
}
