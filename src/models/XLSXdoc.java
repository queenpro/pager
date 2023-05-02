/*
 * Copyright (C) 2023 Franco
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
package models;

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.EVOpagerDBconnection;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.PDFdoc.scriptVariable;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import smartCore.smartForm;
import smartCore.smartObjRight;
import smartCore.smartRow;

/**
 *
 * @author Franco
 */
public class XLSXdoc {

    String masterQuery = "";
    String currentHeaderDoc = "";
    EVOpagerParams myParams;
    Settings mySettings;
    OutputStream stream;
    String docType;
    String destType;
    String Title;
    ArrayList<SelectListLine> argList;
    ArrayList<SelectListLine> richArgList;
    ArrayList<scriptVariable> varsList;
    private gate xlsxMyGate;
    private smartForm xlsxMyForm;
    private portalClass xlsxMyPortal;

    public XLSXdoc(OutputStream stream, EVOpagerParams myParams, Settings mySettings) {
        if (argList == null) {
            argList = new ArrayList<>();
        }
        if (richArgList == null) {
            richArgList = new ArrayList<>();
        }
        if (varsList == null) {
            varsList = new ArrayList<>();
        }

        this.myParams = myParams;
        this.mySettings = mySettings;
        this.stream = stream;
        this.argList = argList;
        this.richArgList = richArgList;
        this.varsList = varsList;

    }

    public void creaExcel(String docName, String reportScript) {
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();

        System.out.println("currentHeaderDoc ->" + this.currentHeaderDoc);
        System.out.println("destType ->" + this.destType);
        System.out.println("docType ->" + this.docType);
        System.out.println("masterQuery ->" + this.masterQuery);
        System.out.println("argList ->" + this.argList.toString());
        System.out.println("varsList ->" + this.varsList.toString());
        System.out.println("QueryUsed ->" + this.xlsxMyForm.getQueryUsed());

        // cerco elenco campi
        for (int i = 0; i < xlsxMyForm.objects.size(); i++) {

            System.out.println("COLONNA ->" + xlsxMyForm.objects.get(i).name + " >> " + xlsxMyForm.objects.get(i).labelHeader
                    + " >> " + xlsxMyForm.objects.get(i).visible);
        }

        if (reportScript == null || reportScript.length() == 0) { // metto su excel esattamente cosa mi arriva dalla masterquery

////////        //declare file name to be create   
////////        String filename = "F:\\Demo Data\\Balance.xlsx";
//creating an instance of HSSFWorkbook class  
            HSSFWorkbook workbook = new HSSFWorkbook();
//invoking creatSheet() method and passing the name of the sheet to be created   
            HSSFSheet sheet = workbook.createSheet("Foglio 1");
//creating the 0th row using the createRow() method  
            HSSFRow rowhead = sheet.createRow((short) 0);
            //creating cell by using the createCell() method and setting the values to the cell by using the setCellValue() method  

            for (int i = 0; i < xlsxMyForm.objects.size(); i++) {
                if (!xlsxMyForm.objects.get(i).visible.equalsIgnoreCase("DEFAULT:0")) {
                    rowhead.createCell(i).setCellValue(xlsxMyForm.objects.get(i).labelHeader);
                }

            }

            try {
                String SQLphrase = "";
                PreparedStatement ps;
                ResultSet rs;
                SQLphrase = this.xlsxMyForm.getQueryUsed();
//                System.out.println("SQLphrase:" + SQLphrase);
//                System.out.println("Types.BIGINT: " + Types.BIGINT);
//                System.out.println("Types.INTEGER: " + Types.INTEGER);
//                System.out.println("Types.FLOAT: " + Types.FLOAT);
//                System.out.println("Types.DECIMAL: " + Types.DECIMAL);
//                System.out.println("Types.DOUBLE: " + Types.DOUBLE);
//                System.out.println("Types.BIGINT: " + Types.REAL);
//                System.out.println("Types.TINYINT: " + Types.TINYINT);

                try {
                    ps = conny.prepareStatement(SQLphrase);
                    
                    System.out.println("SQLphrase: " +SQLphrase);
                    rs = ps.executeQuery();
                    int righeScritte = 0;
                    while (rs.next()) {
                        righeScritte++;
                        System.out.println("\nRIGA DATABASE: " + righeScritte);
                        if (righeScritte == 1) {
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int numColumns = rsmd.getColumnCount();
                            for (int i = 1; i <= numColumns; i++) {
                                String column_name = rsmd.getColumnName(i);
                                String column_name2 = rsmd.getColumnLabel(i);
                                int column_type = rsmd.getColumnType(i);
                                int colType = 0;
                                System.out.println(">: " + column_name
                                        + " >getColumnLabel: " + column_name2
                                        + " >column_type: " + column_type);

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
                                    for (int o = 0; o < xlsxMyForm.objects.size(); o++) {
                                        if (xlsxMyForm.objects.get(o).getName().equalsIgnoreCase(column_name)
                                                || column_name.endsWith("." + xlsxMyForm.objects.get(o).getName())) {
                                            xlsxMyForm.objects.get(o).listChecked = colType;
                                            xlsxMyForm.objects.get(o).DBfieldType = column_type;
                                            break;
                                        }

                                    }

                                }

                            }

                        }

                        HSSFRow xrow = sheet.createRow((short) righeScritte); // creo la riga in excel
                        for (int c = 0; c < xlsxMyForm.objects.size(); c++) {
                            if (!xlsxMyForm.objects.get(c).visible.equalsIgnoreCase("DEFAULT:0")) {
                                if (xlsxMyForm.objects.get(c).getListChecked() == 1) {//è un numero
                                    if (xlsxMyForm.objects.get(c).getDBfieldType() == Types.FLOAT) {
                                        try {
                                            float myVal = rs.getFloat(xlsxMyForm.objects.get(c).getName());
                                            xrow.createCell(c).setCellValue(myVal);
                                            System.out.println("Lettura FLOAT " + xlsxMyForm.objects.get(c).getName() + " : " + myVal);
                                        } catch (Exception e) {
                                            System.out.println("Errore lettura FLOAT " + xlsxMyForm.objects.get(c).getName());
                                        }
                                    } else if (xlsxMyForm.objects.get(c).getDBfieldType() == Types.DOUBLE) {
                                        try {
                                            double myVal = rs.getDouble(xlsxMyForm.objects.get(c).getName());
                                            xrow.createCell(c).setCellValue(myVal);
                                            System.out.println("Lettura DOUBLE " + xlsxMyForm.objects.get(c).getName() + " : " + myVal);
                                        } catch (Exception e) {
                                            System.out.println("Errore lettura DOUBLE " + xlsxMyForm.objects.get(c).getName());
                                        }
                                    } else {
                                        try {
                                            xrow.createCell(c).setCellValue(rs.getInt(xlsxMyForm.objects.get(c).getName()));
                                        } catch (Exception e) {
                                            System.out.println("Errore lettura INT " + xlsxMyForm.objects.get(c).getName());
                                        }
                                    }
                                } else {
                                    try {
                                        xrow.createCell(c).setCellValue(rs.getString(xlsxMyForm.objects.get(c).getName()));
                                    } catch (Exception e) {
                                        System.out.println("Errore lettura STRING " + xlsxMyForm.objects.get(c).getName());
                                    }
                                }
                            }
                        }
                        System.out.println("FINE RIGA: " + righeScritte);
                    }
                } catch (SQLException ex) {

                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

////creating the 1st row  
//            HSSFRow row = sheet.createRow((short) 1);
////inserting data in the first row  
//            row.createCell(0).setCellValue("1");
//            row.createCell(1).setCellValue("John William");
//            row.createCell(2).setCellValue("9999999");
//            row.createCell(3).setCellValue("william.john@gmail.com");
//            row.createCell(4).setCellValue("700000.00");
            try {
                ////////        FileOutputStream fileOut = new FileOutputStream(filename);
                workbook.write(stream);
            } catch (IOException ex) {
                Logger.getLogger(XLSXdoc.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            //////////closing the Stream
////////        fileOut.close();
//////////closing the workbook  
////////        workbook.close();
//prints the message on the console  .
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(XLSXdoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Excel file has been generated successfully.");

    }

    public gate getXlsxMyGate() {
        return xlsxMyGate;
    }

    public void setXlsxMyGate(gate xlsxMyGate) {
        this.xlsxMyGate = xlsxMyGate;
    }

    public smartForm getXlsxMyForm() {
        return xlsxMyForm;
    }

    public void setXlsxMyForm(smartForm xlsxMyForm) {
        this.xlsxMyForm = xlsxMyForm;
    }

    public portalClass getXlsxMyPortal() {
        return xlsxMyPortal;
    }

    public void setXlsxMyPortal(portalClass xlsxMyPortal) {
        this.xlsxMyPortal = xlsxMyPortal;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getMasterQuery() {
        return masterQuery;
    }

    public void setMasterQuery(String masterQuery) {
        this.masterQuery = masterQuery;
    }

    public String getCurrentHeaderDoc() {
        return currentHeaderDoc;
    }

    public void setCurrentHeaderDoc(String currentHeaderDoc) {
        this.currentHeaderDoc = currentHeaderDoc;
    }

    public EVOpagerParams getMyParams() {
        return myParams;
    }

    public void setMyParams(EVOpagerParams myParams) {
        this.myParams = myParams;
    }

    public Settings getMySettings() {
        return mySettings;
    }

    public void setMySettings(Settings mySettings) {
        this.mySettings = mySettings;
    }

    public OutputStream getStream() {
        return stream;
    }

    public void setStream(OutputStream stream) {
        this.stream = stream;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDestType() {
        return destType;
    }

    public void setDestType(String destType) {
        this.destType = destType;
    }

    public ArrayList<SelectListLine> getArgList() {
        return argList;
    }

    public void setArgList(ArrayList<SelectListLine> argList) {
        this.argList = argList;
    }

    public ArrayList<SelectListLine> getRichArgList() {
        return richArgList;
    }

    public void setRichArgList(ArrayList<SelectListLine> richArgList) {
        this.richArgList = richArgList;
    }

    public ArrayList<scriptVariable> getVarsList() {
        return varsList;
    }

    public void setVarsList(ArrayList<scriptVariable> varsList) {
        this.varsList = varsList;
    }

}
