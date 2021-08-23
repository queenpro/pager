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

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.BarcodeDatamatrix;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.PDFdoc;
import models.PDFreportRow;
import models.SelectListLine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Franco
 */
public class smartPDFdoc {

    EVOpagerParams myParams;
    Settings mySettings;
    OutputStream stream;

    String docType;
    String destType;
    ArrayList<SelectListLine> argList;
    ArrayList<SelectListLine> richArgList;
    ArrayList<PDFdoc.scriptVariable> varsList;
    Document masterDocument, document;
    private PdfPTable intestazione;

    Rectangle PDFpageSize;
    float PDFmarginLeft;
    float PDFmarginRight;
    float PDFmarginTop;
    float PDFmarginBottom;
    String PDFfilename;
    PdfWriter writer;
    Connection conny;
    PdfTemplate template;
    String docName;

    private ArrayList<PDFreportRow> scriptLoader(String XdocName, Connection conny) {
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

                    reportRows.add(myRow);
                }
            } catch (ParseException ex) {
                Logger.getLogger(PDFdoc.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        for (int row = 0; row < reportRows.size(); row++) {

            PDFreportRow myRow = reportRows.get(row);
            parseForSettings(myRow);
        }
        return reportRows;
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
                PDFmarginLeft = Float.parseFloat(myRow.getContent());
            } else if (myRow.getType().equalsIgnoreCase("marginRight")) {
                PDFmarginRight = Float.parseFloat(myRow.getContent());

            } else if (myRow.getType().equalsIgnoreCase("marginTop")) {
                PDFmarginTop = Float.parseFloat(myRow.getContent());

            } else if (myRow.getType().equalsIgnoreCase("marginBottom")) {
                PDFmarginBottom = Float.parseFloat(myRow.getContent());

            } else if (myRow.getType().equalsIgnoreCase("filename")) {
                richArgList = cloneArray(argList);
                String fn = browserArgsReplace(myRow.getContent());
                PDFfilename = fn;

            }
        }
    }

    public String browserArgsReplace(String query) {
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
        for (int jj = 0; jj < this.richArgList.size(); jj++) {
            String xMarker = this.richArgList.get(jj).getLabel();
            String xValue = this.richArgList.get(jj).getValue();
            String toBeReplaced = "###" + xMarker + "###";
            if (defVal.contains(toBeReplaced)) {
                defVal = defVal.replace(toBeReplaced, xValue);
            }
        }
        for (int jj = 0; jj < this.varsList.size(); jj++) {
            String xMarker = this.varsList.get(jj).getName();
            String xValue = "" + this.varsList.get(jj).getValueInt();
            String toBeReplaced = "@@@" + xMarker + "@@@";
            if (defVal.contains(toBeReplaced)) {
                defVal = defVal.replace(toBeReplaced, xValue);
            }
        }
        return defVal;
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
        System.out.println("cerco range a partire da " + inizio + " in " + reportRows.size() + " righe totali.");
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
        System.out.println("trovata chiusura in " + rigaFine);
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
}
