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
import java.io.UnsupportedEncodingException;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.EVOpagerDBconnection;
import REVOpager.EVOuser;
import REVOpager.schema_column;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import showIt.ShowItForm;
import smartCore.smartForm;

/**
 *
 * @author Franco
 */
public class CRUDorder {

    EVOpagerParams myParams;
    Settings mySettings;
    String operation;
    String formName;
    String formID;
    String formCopyTag;
    String cellType;
    String valueType;
    String filterSequence;
    String primaryFieldName;
    String primaryFieldValue;
    String primaryFieldType;
    String filterField;
    String filterValue;
    String cellName;
    String cellID;
    String newValue;
    String fatherKEYvalue;
    String fatherKEYtype;
    String fieldFiltered;
    String toBeSent;
    String sendToCRUD;
    String routineOnChange;
    String mainTable;
    String JSafterHour;
    String ActionParams;

    ShowItForm myForm;
    String formType;
    String addedIndex;

    String AfterOperationRoutineOnChange;
    String AfterOperationRoutineOnNew;
    String AfterOperationRoutineOnDelete;

    public String printParams() {

        String params = "";
        System.out.println("\n\n\nCRUD STANDARD");
        System.out.println("---------------------------------");
        System.out.println("operazione:" + operation);
        System.out.println("-----------");
        System.out.println("formName:" + formName);
        System.out.println("formID:" + formID);
        System.out.println("formCopyTag:" + formCopyTag);
        System.out.println("cellType:" + cellType);
        System.out.println("valueType:" + valueType);
        System.out.println("filterSequence:" + filterSequence);
        System.out.println("primaryFieldName:" + primaryFieldName);
        System.out.println("primaryFieldValue:" + primaryFieldValue);
        System.out.println("primaryFieldType:" + primaryFieldType);
        System.out.println("filterField:" + filterField);
        System.out.println("filterValue:" + filterValue);
        System.out.println("cellName:" + cellName);
        System.out.println("cellID:" + cellID);
        System.out.println("newValue:" + newValue);
        System.out.println("newValue escaped:" + newValue);
        System.out.println("fatherKEYvalue:" + fatherKEYvalue);
        System.out.println("fatherKEYtype:" + fatherKEYtype);
        System.out.println("fieldFiltered:" + fieldFiltered);
        System.out.println("toBeSent:" + toBeSent);
        System.out.println("sendToCRUD:" + sendToCRUD);
        System.out.println("routineOnChange:" + routineOnChange);
        System.out.println("mainTable:" + mainTable);
        System.out.println("ActionParams:" + ActionParams);
        System.out.println("---------------------------------");
        return params;
    }

    public ShowItForm getMyForm() {
        return myForm;
    }

    public void setMyForm(ShowItForm myForm) {
        this.myForm = myForm;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getActionParams() {
        return ActionParams;
    }

    public void setActionParams(String ActionParams) {
        this.ActionParams = ActionParams;
    }

    public CRUDorder(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
    }

    public String getMainTable() {
        return mainTable;
    }

    public void setMainTable(String mainTable) {
        this.mainTable = mainTable;
    }

    public String getAfterOperationRoutineOnDelete() {
        return AfterOperationRoutineOnDelete;
    }

    public void setAfterOperationRoutineOnDelete(String AfterOperationRoutineOnDelete) {
        this.AfterOperationRoutineOnDelete = AfterOperationRoutineOnDelete;
    }

    public String getAfterOperationRoutineOnChange() {
        return AfterOperationRoutineOnChange;
    }

    public void setAfterOperationRoutineOnChange(String AfterOperationRoutineOnChange) {
        this.AfterOperationRoutineOnChange = AfterOperationRoutineOnChange;
    }

    public String getAfterOperationRoutineOnNew() {
        return AfterOperationRoutineOnNew;
    }

    public void setAfterOperationRoutineOnNew(String AfterOperationRoutineOnNew) {
        this.AfterOperationRoutineOnNew = AfterOperationRoutineOnNew;
    }

    public String getRoutineOnChange() {
        return routineOnChange;
    }

    public void setRoutineOnChange(String routineOnChange) {
        this.routineOnChange = routineOnChange;
    }

    public String getSendToCRUD() {
        return sendToCRUD;
    }

    public void setSendToCRUD(String sendToCRUD) {
        this.sendToCRUD = sendToCRUD;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getFormCopyTag() {
        return formCopyTag;
    }

    public void setFormCopyTag(String formCopyTag) {
        this.formCopyTag = formCopyTag;
    }

    public String getToBeSent() {
        return toBeSent;
    }

    public void setToBeSent(String toBeSent) {
        this.toBeSent = toBeSent;
    }

    public EVOpagerParams getMyParams() {
        return myParams;
    }

    public void setMyParams(EVOpagerParams myParams) {
        this.myParams = myParams;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public String getFilterSequence() {
        return filterSequence;
    }

    public void setFilterSequence(String filterSequence) {
        this.filterSequence = filterSequence;
    }

    public String getPrimaryFieldName() {
        return primaryFieldName;
    }

    public void setPrimaryFieldName(String primaryFieldName) {
        this.primaryFieldName = primaryFieldName;
    }

    public String getPrimaryFieldValue() {
        return primaryFieldValue;
    }

    public void setPrimaryFieldValue(String primaryFieldValue) {
        this.primaryFieldValue = primaryFieldValue;
    }

    public String getPrimaryFieldType() {
        return primaryFieldType;
    }

    public void setPrimaryFieldType(String primaryFieldType) {
        this.primaryFieldType = primaryFieldType;
    }

    public String getFilterField() {
        return filterField;
    }

    public void setFilterField(String filterField) {
        this.filterField = filterField;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public String getCellID() {
        return cellID;
    }

    public void setCellID(String cellID) {
        this.cellID = cellID;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getFatherKEYvalue() {
        return fatherKEYvalue;
    }

    public void setFatherKEYvalue(String fatherKEYvalue) {
        this.fatherKEYvalue = fatherKEYvalue;
    }

    public String getFatherKEYtype() {
        return fatherKEYtype;
    }

    public void setFatherKEYtype(String fatherKEYtype) {
        this.fatherKEYtype = fatherKEYtype;
    }

    public String getFieldFiltered() {
        return fieldFiltered;
    }

    public void setFieldFiltered(String fieldFiltered) {
        this.fieldFiltered = fieldFiltered;
    }

    public String getJSafterHour() {
        return JSafterHour;
    }

    public void setJSafterHour(String JSafterHour) {
        this.JSafterHour = JSafterHour;
    }

    public String executeCMLNK() {
        Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
        String answer = "";
        //ho selezionato un checkbox tra quelli disponibili in un pannello accessorio
        //dati occorrenti:
        // 1.nome della tabella contenente i link. avendo il nome oggetto la vado a ricavare nel DB---> uso cellName
        // 2.nome delcampo UNO    ---> uso primaryFieldName
        //+ 3.valore del campo UNO    ---> uso primaryFieldValue
        // 4.nome del campo MOLTI    ---> uso filterField
        //+ 6.valore del campo MOLTI    ---> uso  filterValue
        //+ 6.valore da inserire: se=0 cancello il link, se=1 0creo il link ---> uso newValue

        try {
            Statement s = FEconny.createStatement();
            String SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_objects() + " WHERE ID='" + this.getCellName() + "'";
            System.out.println("CMLNK  SQLphrase=" + SQLphrase);
            ResultSet rs = s.executeQuery(SQLphrase);
            String CGparams = "";
            String linkTable = "";
            String linkField = "";
            String linkFieldUNO = "";
            String linkFieldMOLTI = "";
            while (rs.next()) {
                CGparams = rs.getString("CGparams");
            }

            String[] couple = CGparams.split(";");
            List<String> couples = Arrays.asList(couple);
            if (couples.size() > 2) {
                linkTable = couples.get(1).trim();
                linkField = couples.get(2).trim();
            }

            couple = linkField.split(":");
            couples = Arrays.asList(couple);
            if (couples.size() > 1) {
                linkFieldUNO = couples.get(0);
                linkFieldMOLTI = couples.get(1);
            }
            System.out.println("var operation=" + operation);
            System.out.println("var cellName=" + cellName);
            System.out.println("var primaryFieldValue=" + primaryFieldValue);
            System.out.println("var filterValue=" + filterValue);
            System.out.println("var newValue=" + newValue);
            System.out.println("var linkTable=" + linkTable);
            System.out.println("var linkFieldUNO=" + linkFieldUNO);
            System.out.println("var linkFieldMOLTI=" + linkFieldMOLTI);

            if (this.getNewValue().equalsIgnoreCase("TRUE")) {
                // inserisco riga se non esiste
                SQLphrase = "SELECT * FROM " + linkTable + " WHERE " + linkFieldUNO + "='" + this.getPrimaryFieldValue() + "'  AND " + linkFieldMOLTI + "='" + this.getFilterValue() + "'";
                System.out.println("CMLNK CERCO SQLphrase=" + SQLphrase);
                rs = s.executeQuery(SQLphrase);
                int lines = 0;
                while (rs.next()) {
                    lines++;
                }
                if (lines == 0) {
                    SQLphrase = "INSERT INTO " + linkTable + " ( " + linkFieldUNO + "," + linkFieldMOLTI + ") VALUES ('" + this.getPrimaryFieldValue() + "','" + this.getFilterValue() + "')";
                    System.out.println("CMLNK AGGIUNGO SQLphrase=" + SQLphrase);
                    int x = s.executeUpdate(SQLphrase);
                } else {
                    System.out.println("CMLNK ESISTE GIA' NON OCCORRE AGGIUNGERE=");
                }
            } else {
                // elimino eventuale riga
                SQLphrase = "DELETE FROM " + linkTable + " WHERE " + linkFieldUNO + "='" + this.getPrimaryFieldValue() + "'  AND " + linkFieldMOLTI + "='" + this.getFilterValue() + "'";
                System.out.println("CMLNK ELIMINO SQLphrase=" + SQLphrase);
                int x = s.executeUpdate(SQLphrase);
            }
            String jsonAnswer = "{\"sender\":\"CRUD\",\"operation\":\"CMLNK\",\"code\":\"OK\"}";
            answer = jsonAnswer;

            FEconny.close();
        } catch (SQLException ex) {

            String jsonAnswer = "{\"sender\":\"CRUD\",\"operation\":\"CMLNK\",\"code\":\"ERR\",\"mess\":\"ERRORE IN CMLNK.\"}";
            answer = jsonAnswer;

            System.out.println("CMLNK answer:" + answer + "_");
        }

        return answer;
    }

    public void buildMyForm() {
        myForm = new ShowItForm(this.getFormID(), myParams, mySettings);
        if (this.getFormName() != null) {
            myForm.setName(this.getFormName());
        }
        // se conosco il nome questo prevale sull'ID e l'ID viene invece ricavato di conseguenza dal DB

        myForm.setCopyTag(this.getFormCopyTag());
        myForm.setSendToCRUD(this.getSendToCRUD());
        myForm.buildSchema();

    }

    public smartForm buildMySmartForm() {
        smartForm mySmartForm = new smartForm(this.getFormID(), myParams, mySettings);
        if (this.getFormName() != null) {
            mySmartForm.setName(this.getFormName());
        }
        // se conosco il nome questo prevale sull'ID e l'ID viene invece ricavato di conseguenza dal DB

        mySmartForm.setCopyTag(this.getFormCopyTag());
        mySmartForm.setSendToCRUD(this.getSendToCRUD());
        mySmartForm.buildSchema();
        return mySmartForm;
    }

    public String executeCRUD() {
        //ShowItForm 

        buildMyForm();

        JSONObject jsonAnswer = new JSONObject();
        logEvent myEvent = new logEvent();
        myEvent.setType("CRUD");
        myEvent.setUser(myParams.getCKuserID());
        myEvent.setToken(myParams.getCKtokenID());

        String errorMessage = "";
        String answer = "";
        String pKEY = "";
        String pKEYtype = "";
        String pKEYvalue = "";
        System.out.println("INIZIO CRUD value:" + this.getNewValue());
        String newHtmlCode = "";
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        if (this.getNewValue() != null) {
            String autovalue = null;
            try {
                autovalue = java.net.URLDecoder.decode(this.getNewValue(), "UTF-8");
                this.setNewValue(autovalue);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                autovalue = java.net.URLDecoder.decode(this.getNewValue(), "UTF-8");
                this.setNewValue(autovalue);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("armoinizzato value:" + this.getNewValue());
        if (this.getCellType() == null || this.getCellType() == "") {
            this.setCellType("T");
        }

        //myParams.printParams();
        //      this.printParams();
        //  if (newValue!=null && newValue.length()>0)    newValue = newValue.replaceAll("'", "''");
////////        //ShowItForm ATTENZIONE! 2011-10-15 ho spostato le prossime righe all'inizio e le trasformerò in routine a sè stante
////////        myForm = new ShowItForm(this.getFormID(), myParams, mySettings);
////////        if (this.getFormName() != null) {
////////            myForm.setName(this.getFormName());
////////        }
////////        // se conosco il nome questo prevale sull'ID e l'ID viene invece ricavato di conseguenza dal DB
////////
////////        myForm.setCopyTag(this.getFormCopyTag());
////////        myForm.setSendToCRUD(this.getSendToCRUD());
////////        myForm.buildSchema();
        this.formType = myForm.getType(); //per gestire il tipo tree

        String SQLphrase = "";
        String whereClause = "";

        // Cerco in FE_forms la mainTable di questo Form
        try {
            Statement s = conny.createStatement();

            System.out.println("\n-----------------\n"
                    + "SONO IN ExecuteCRUD\nCerco le info in base al nome del form:" + myForm.getName());
            myForm.getFormInformationsFromDB();

            //myForm.printVals();
            String dbTable = myForm.getMainTable();
            String formType = myForm.getType();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = null;
            String afterDel = "";
            String routineOnFormChange = "";
            String routineOnNew = "";
            for (int oo = 0; oo < myForm.objects.size(); oo++) {
                // System.out.println("oggetto:" + myForm.objects.get(oo).getID()+" --->" +myForm.objects.get(oo).getName());
                if (myForm.objects.get(oo).getID().equals(cellID) || myForm.objects.get(oo).getName().equals(cellName)) {
                    String ActionParamsDB = myForm.objects.get(oo).getActionParams();
//                    System.out.println("ActionParamsDBa:" + ActionParamsDB);
                    this.setActionParams(ActionParamsDB);
                    break;
                }
            }
            /*
questo sopra apre una strada im portante per i controlli diurante un CRUD perchè accedo ai valori dell'oggetto
direttamente dal DB gFE_ e non da quanto mi passa il browser:: posso controllare anche i diritti per il form e l'identità di chi oepra
             */
            if (myForm.getGes_topBar() != null && myForm.getGes_topBar().length() > 1) {
                try {
                    jsonObject = (JSONObject) jsonParser.parse(myForm.getGes_topBar());
                    try {
                        afterDel = jsonObject.get("actionsAfterDel").toString();
                    } catch (Exception e) {
                    }
                    jsonObject = (JSONObject) jsonParser.parse(myForm.getGes_topBar());
                    try {
                        routineOnNew = jsonObject.get("routineOnNew").toString();
                    } catch (Exception e) {
                    }
                    try {
                        routineOnFormChange = jsonObject.get("routineOnFormChange").toString();
                    } catch (Exception e) {
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            System.out.println("[CRUD]routineOnNew:" + routineOnNew);
            this.setAfterOperationRoutineOnNew(routineOnNew);
            System.out.println("[CRUD]routineOnFormChange:" + routineOnFormChange);
            this.setAfterOperationRoutineOnChange(routineOnFormChange);
            if ((this.getAfterOperationRoutineOnChange() != null || this.getAfterOperationRoutineOnChange().length() > 4)
                    && (this.getRoutineOnChange() == null || this.getRoutineOnChange().length() < 5)) {
                this.setRoutineOnChange(routineOnFormChange);
            }

//            for (int jj = 0; jj < myForm.objects.size(); jj++) {
//                System.out.println("OBJECT:" + myForm.objects.get(jj).name + "\t ->" + myForm.objects.get(jj).Content.getActualModifiable());
//                System.out.println("OBJECT:" + myForm.objects.get(jj).name + "\t ->" + myForm.objects.get(jj).Content.getThisRowModifiable());
//
//            }
            String actionPassed = "";
            System.out.println("[CRUD]myForm.getRefreshOnUpdate():" + myForm.getRefreshOnUpdate());
            if (myForm.getRefreshOnUpdate() != null && myForm.getRefreshOnUpdate().equalsIgnoreCase("TRUE")) {
                actionPassed = "repaintRow";
            }


            /*
            VERIFICHE DA IMPLEMENTARE:
            1. esistenza utente e token valido con sessione aperta
            2. in base al FORM, analisi dei diritti di scrittura sul form e sul singolo object
             */
            if (this.getPrimaryFieldType() != null && this.getPrimaryFieldType().length() > 2
                    && (this.getPrimaryFieldType().substring(0, 3).equalsIgnoreCase("INT")
                    || this.getPrimaryFieldType().substring(0, 3).equalsIgnoreCase("FLO")
                    || this.getPrimaryFieldType().substring(0, 3).equalsIgnoreCase("BIT"))) {
                pKEYtype = "INT";
                whereClause = " WHERE " + this.getPrimaryFieldName() + " = " + this.getPrimaryFieldValue() + " ;";
            } else {
                whereClause = " WHERE " + this.getPrimaryFieldName() + " = \"" + this.getPrimaryFieldValue() + "\";";
            }

            myEvent.setEventCode(this.getOperation());
//----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="DEL">   
            //DEL///////////////////////////////////////////////////////////////                
            if (this.getOperation().equalsIgnoreCase("DEL")) {
                SQLphrase = "DELETE FROM `" + dbTable + "`  " + whereClause;
//                System.out.println("DEL:SQLphrase:" + SQLphrase);

                myEvent.setInfo1(SQLphrase);
                int Result = s.executeUpdate(SQLphrase);

                if (Result > 0) {
                    jsonAnswer = new JSONObject();
                    jsonAnswer.put("sender", "CRUD");
                    jsonAnswer.put("operation", "DEL");
                    jsonAnswer.put("code", "OK");
                    jsonAnswer.put("mess", "DELETED");
                    jsonAnswer.put("routineResponse", afterDel);

                    // String jsonAnswer = "{\"sender\":\"CRUD\",\"operation\":\"DEL\",\"code\":\"OK\",\"mess\":\"DELETED\",\"routineResponse\":" + afterDel + "}";
//                    answer = jsonAnswer;
                } else {
                    jsonAnswer = new JSONObject();
                    jsonAnswer.put("sender", "CRUD");
                    jsonAnswer.put("operation", "DEL");
                    jsonAnswer.put("code", "ERR");
                    jsonAnswer.put("mess", "ERROR WHILE DELETING.");
                    jsonAnswer.put("routineResponse", "");
//                    String jsonAnswer = "{\"sender\":\"CRUD\",\"operation\":\"DEL\",\"code\":\"ERR\",\"mess\":\"ERROR WHILE DELETING.\"}";
//                    answer = jsonAnswer;
                }
                System.out.println("DEL:answer:" + answer);

            } else // </editor-fold>    
            // <editor-fold defaultstate="collapsed" desc="ADD">   
            //ADD/////////////////////////////////////////////////////////////// 
            if (this.getOperation().equalsIgnoreCase("ADD")) {
                //prima di uscire dovrò sapere quale key è stata usata o assegnata
                // per compilare la nuova riga
                //quondi interrogo lo schema per conoscere le caratteristiche

                Connection schemaconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalSchema();

                Statement schemast = schemaconny.createStatement();
                // Statement localSt = localconny.createStatement();
                SQLphrase = "SELECT * FROM COLUMNS\n"
                        + "           WHERE TABLE_NAME = '" + dbTable + "'\n"
                        + "             AND TABLE_SCHEMA = '" + mySettings.getProjectDBextendedName(myParams) + "' ORDER BY ORDINAL_POSITION;";
//                System.out.println("ADD: SCHEMA SQLphrase=" + SQLphrase);

                ResultSet schemars = schemast.executeQuery(SQLphrase);
                int i = 0;
                /* 
                 String IS_NULLABLE[]=new String[100]; // ES. 'YES' oppure 'NO'
                 String COLUMN_KEY[]=new String[100]; // ES. 'PRI'=PRIMARY
                 String EXTRA[]=new String[100]; // ES. 'auto_increment'
                 */

                ArrayList<schema_column> columns = new ArrayList<schema_column>();
                while (schemars.next()) {
                    i++;
                    schema_column column = new schema_column();
                    column.setCOLUMN_NAME(schemars.getString("COLUMN_NAME"));
//                    System.out.println("CRUD ORDER reperita colonna " + column.getCOLUMN_NAME());
                    column.setCOLUMN_DEFAULT(schemars.getString("COLUMN_DEFAULT"));
                    column.setCOLUMN_KEY(schemars.getString("COLUMN_KEY"));
                    column.setDATA_TYPE(schemars.getString("DATA_TYPE"));
                    column.setEXTRA(schemars.getString("EXTRA"));
                    column.setIS_NULLABLE(schemars.getString("IS_NULLABLE"));
                    column.setCOLUMN_TYPE(schemars.getString("COLUMN_TYPE"));
                    column.setORDINAL_POSITION(schemars.getInt("ORDINAL_POSITION"));

                    BigDecimal result = schemars.getBigDecimal("CHARACTER_MAXIMUM_LENGTH");
                    column.setCHARACTER_MAXIMUM_LENGTH(result == null ? null : result.toBigInteger());

                    column.setNUMERIC_PRECISION(schemars.getInt("NUMERIC_PRECISION"));

                    columns.add(column);
                    //System.out.println("inglobata colonna "+ column.getCOLUMN_NAME());                  
                }
                schemaconny.close();

                //===CERCO LO SCHEMA PER INDIVIDUARE COLONNA PRIMARIA E ALTRE CARATTERISTICHE
                for (int jj = 0; jj < columns.size(); jj++) {
                    //System.out.println("Colonna [" + jj + "] = '" + columns.get(jj).getCOLUMN_NAME() + "' -COLUMN_KEY:" + columns.get(jj).getCOLUMN_KEY() + " - EXTRA:" + columns.get(jj).getEXTRA());
                    if (columns.get(jj).getCOLUMN_KEY() != null
                            && columns.get(jj).getCOLUMN_KEY().length() > 2
                            && columns.get(jj).getCOLUMN_KEY().substring(0, 3).equalsIgnoreCase("PRI")) {
                        //System.out.println("Colonna primaria");
                        pKEY = columns.get(jj).getCOLUMN_NAME();
                        this.setPrimaryFieldName(pKEY);

                        pKEYtype = "VARCHAR";
                        if (columns.get(jj).getDATA_TYPE().substring(0, 3).equalsIgnoreCase("INT")) {
                            pKEYtype = "INT";
                        }
                        if (columns.get(jj).getEXTRA().equalsIgnoreCase("auto_increment")) {
                            pKEYtype = "AUTOINCREMENT";
                        }
//                        System.out.println("pKEY:" + pKEY + "   -   pKEYtype:" + pKEYtype);
                    }
                }

                ArrayList<boundFields> boundFieldList = new ArrayList<boundFields>();

                // <editor-fold defaultstate="collapsed" desc="ANALIZZO filterSequence">   
                System.out.println("\nADD================\n2.ANALIZZO filterSequence:" + this.getFilterSequence());
                //analizzo la filterSequence per aggiungere i campi autocompilati

                if (this.getFilterSequence() != null && this.getFilterSequence().length() > 0) {
                    String filters = this.getFilterSequence();
                    this.setFilterSequence(filters + " ; ");

                    String[] QUERYphrase = this.getFilterSequence().split(";");
                    List<String> QUERYparts = Arrays.asList(QUERYphrase);
                    if (QUERYparts.size() > 1) {

                        for (int jj = 0; jj < QUERYparts.size(); jj++) {

                            System.out.println("blocco " + jj + " : " + QUERYparts.get(jj).toString());
                            String[] LINEphrase = QUERYparts.get(jj).toString().split("=");
                            List<String> LINEparts = Arrays.asList(LINEphrase);
                            if (LINEparts.size() > 1) {
                                boundFields myField = new boundFields();
                                myField.setMarker(LINEparts.get(0));
                                myField.setValue(LINEparts.get(1));

                                String valoreDefault = LINEparts.get(1);

                                // ora applico sostituzioni al valoreDefault in base ai dati sendToCRUD
                                System.out.println("ora applico sostituzioni al valoreDefault in base ai dati sendToCRUD " + valoreDefault);
                                valoreDefault = standardReplace(valoreDefault, null);
                                System.out.println("diventa " + valoreDefault);

                                myField.setValue(valoreDefault);
                                if (!myField.getMarker().contains(".")) {// se il campo proviene da un'altra tabella non lo inserisco
                                    boundFieldList.add(myField);
                                }

                                System.out.println("aggiunto da filtersequence:" + myField.getMarker() + " =" + valoreDefault);
                            }
                        }

                    }
                    // oltre alle sostituzioni con i campi facciop anche le sostituzioni standard       

                }
// </editor-fold>    
                // <editor-fold defaultstate="collapsed" desc="Cerco field con DEFAULT VALUE">   
                boundFields myField = new boundFields();

                //   3.inoltre voglio aggiungere campi compilati dal valore di default impostato in evolution es. $$$NOW$$$ o $$$KEY$$$
                int FlagPanelFIlter = 0;
                System.out.println("\n3.Cerco negli oggetti i field con DEFAULT VALUE");

                SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_objects() + " WHERE rifForm = '" + this.getFormID() + "'";
                System.out.println("SQLphrase" + SQLphrase);
                ResultSet rs = s.executeQuery(SQLphrase);

                try {
                    while (rs.next()) {
                        // parso tutti gli Oggetti dello schema FEobjects e cerco quelli con default Value
                        String fieldName = rs.getString("name");

                        String defVal = rs.getString("defaultValue");
                        if (defVal != null && !defVal.equalsIgnoreCase("NULL") && defVal.length() > 0) {
                            // questo oggetto ha un default value indicato !
                            System.out.println("OGGETTO " + fieldName + " DEFAULT=" + defVal);
                            int flag = 0;
                            //controllo se fa parte della boundFieldList esistente
                            for (int jj = 0; jj < boundFieldList.size(); jj++) {
                                if (fieldName.equalsIgnoreCase(boundFieldList.get(jj).getMarker())) {
                                    flag++;
                                }
                            }
                            if (fieldName.equalsIgnoreCase(this.getCellName())) {
                                // controllo che non sia proprio il campo che sto aggiornando    
                                flag++;
                            }

                            if (flag == 0) {
                                String radix = newValue;

                                System.out.println("579 prima=" + defVal);
                                defVal = standardReplace(defVal, radix);
                                System.out.println("579 dopo=" + defVal);

                                if (this.getNewValue().equalsIgnoreCase("PANELFILTER")) {
                                    FlagPanelFIlter++;
                                }

                                // sostituisco i valori da FUNZIONE @@@xxx@@@
                                /*   customFunction myfuncs = new customFunction(this.getNewValue());
                                 System.out.println(">>CERCO LE FUNZIONI A PARTIRE DAL NUOVO VALORE: " + this.getNewValue());
                                 for (int kk = 0; kk < myfuncs.getFunctions().size(); kk++) {
                                 String funzione = myfuncs.getFunctions().get(kk);
                                 //    System.out.println(">>SOSTITUISCO: "+"@@@"+funzione+"@@@");
                                 //   System.out.println(">>CON: "+myfuncs.executeFunction(funzione));
                                 defVal = defVal.replace("@@@" + funzione + "@@@", myfuncs.executeFunction(funzione));
                                 }*/
                                //   System.out.println(">>QUI POSSO SOSTITUIRE  ..." + this.getSendToCRUD());
                                myField = new boundFields();
                                myField.setMarker(fieldName);
                                myField.setValue(defVal);
                                //System.out.println("aggiungo "+fieldName+" alla boundList poichè ha un valore di DEFAULT"); 
                                boundFieldList.add(myField);
                                // aggiungo alla boundfields 

                            }
                        } else {
                            // System.out.println("OGGETTO " + fieldName + " NON HA DEFAULT VALUE");
                        }
                    }
                } catch (SQLException ex) {
                    answer = errorMessage;
                    System.out.println("errorMessage 1334:" + errorMessage + "_");
                }
                if (this.getCellName() != null && !this.getCellName().equalsIgnoreCase("INSERT_AI")) {
                    //FINE 3.=INSERISCO NUOVO VALORE==================================================================                               
                    myField = new boundFields();
                    myField.setMarker(this.getCellName());
                    myField.setValue("'" + this.getNewValue() + "'");
                    boundFieldList.add(myField);
                }

                String fieldsList = "";
                String valuesList = "";
                System.out.println("\n4.=======================\nTrovati in tutto " + boundFieldList.size() + " campi da inserire.");
// </editor-fold>    
                // <editor-fold defaultstate="collapsed" desc="INSERIMENTO">   
                //===INSERIMENTO=====================================================      
                int Result = 0;
                if (boundFieldList.size() > 0) {
                    for (int jj = 0; jj < boundFieldList.size(); jj++) {
                        if (boundFieldList.get(jj).getMarker().equalsIgnoreCase(this.getPrimaryFieldName())) {
                            this.setPrimaryFieldValue(boundFieldList.get(jj).getValue());
                        }

                        if (jj > 0) {
                            fieldsList += ", ";
                        }
                        fieldsList += "`" + boundFieldList.get(jj).getMarker() + "`";
                        if (jj > 0) {
                            valuesList += ", ";
                        }

                        String xValue = boundFieldList.get(jj).getValue();
                        String yValue = xValue;
                        if (xValue != null) {
                            int lunghezza = xValue.length();
                            if (xValue.length() > 2
                                    && xValue.substring(0, 1).equals("'")
                                    && xValue.substring(lunghezza - 1, lunghezza).equals("'")) {
                                yValue = xValue.substring(1, lunghezza - 1);
                                yValue = "'" + yValue.replace("'", "''") + "'";
                            }
                        }
                        valuesList += yValue;

                    }

                    SQLphrase = "INSERT INTO `" + dbTable + "` (";
                    SQLphrase += fieldsList;
                    SQLphrase += " ) VALUES (";
                    SQLphrase += valuesList;
                    SQLphrase += " );";

//                    System.out.println("Prima di replace: " + SQLphrase);
                    SQLphrase = standardReplace(SQLphrase, this.getPrimaryFieldValue());
//                    System.out.println("Dopo replace: " + SQLphrase);

                    myEvent.setInfo1(SQLphrase);
                    System.out.println("\n5.===============\nSQLphrase di inserimento:" + SQLphrase);
                    errorMessage = "ERRORE IN INSERIMENTO (probabile duplicazione codice).";

                    Result = s.executeUpdate(SQLphrase);
                }
                //================================================                  
                System.out.println("Result:" + Result);
// </editor-fold>    

                int newID = 0;
                if (Result > 0) {

                    // se la key era un autoincrement devo cercare il suo valore massimo
                    // altrimenti la conosco già ( primaryKEY  e  primaryKEYtype)
//                    System.out.println("pKEYtype:" + pKEYtype);
                    if (pKEYtype.equalsIgnoreCase("AUTOINCREMENT")) {
                        SQLphrase = "SELECT * FROM `" + dbTable + "` WHERE ";

                        fieldsList = "";
                        for (int jj = 0; jj < boundFieldList.size(); jj++) {
                            if (!boundFieldList.get(jj).getValue().contains("CURTIME")
                                    && !boundFieldList.get(jj).getValue().contains("NOW")) {

                                if (fieldsList.length() > 0) {
                                    fieldsList += " AND ";
                                }
                                fieldsList += "`" + boundFieldList.get(jj).getMarker() + "`=" + boundFieldList.get(jj).getValue();
                            }
                        }
                        SQLphrase += fieldsList + " ORDER BY `" + pKEY + "` DESC ";

                        SQLphrase = standardReplace(SQLphrase, null);

//                        System.out.println("SQLphrase di ricerca nuovo ID:" + SQLphrase);
                        rs = s.executeQuery(SQLphrase);
                        while (rs.next()) {
                            newID = rs.getInt(this.getPrimaryFieldName());
                            break;
                        }
                        pKEY = this.getPrimaryFieldName();
                        pKEYvalue = "" + newID;
                        pKEYtype = "INT";
                    } else {
                        // se sono qui non è autoincrement, quindi uso la mia primary key già nota
                        pKEYvalue = this.getPrimaryFieldValue().replace("'", "");
                        pKEYtype = this.getPrimaryFieldType();
                    }
                    jsonAnswer = new JSONObject();
                    jsonAnswer.put("sender", "CRUD");
                    jsonAnswer.put("operation", "ADD");
                    jsonAnswer.put("code", "OK");
                    jsonAnswer.put("mess", "");
                    jsonAnswer.put("routineResponse", "");
                    jsonAnswer.put("newID", pKEYvalue);
                    jsonAnswer.put("afterHour", JSafterHour);

//                    String jsonAnswer = "{\"sender\":\"CRUD\",\"operation\":\"ADD\",\"code\":\"OK\",\"newID\":\"" + pKEYvalue + "\",\"afterHour\":\"" + JSafterHour + "\"}";
//                    answer = jsonAnswer;
                } else {
                    jsonAnswer = new JSONObject();
                    jsonAnswer.put("sender", "CRUD");
                    jsonAnswer.put("operation", "ADD");
                    jsonAnswer.put("code", "ERR");
                    jsonAnswer.put("mess", "ERRORE IN INSERIMENTO (probabile duplicazione codice).");
//                    jsonAnswer.put("routineResponse", "");
//                    jsonAnswer.put("newID", "");
//                    jsonAnswer.put("afterHour", "");
//
//                    String jsonAnswer = "{\"sender\":\"CRUD\",\"operation\":\"ADD\",\"code\":\"ERR\",\"mess\":\"ERRORE IN INSERIMENTO (probabile duplicazione codice).\"}";
//                    answer = jsonAnswer;
                }
//                System.out.println("answer:" + answer + "_");
            } else // </editor-fold>    
            // <editor-fold defaultstate="collapsed" desc="UPD"> 
            //UPD/////////////////////////////////////////////////////////////// 
            if (this.getOperation().equalsIgnoreCase("UPD")) {
                SQLphrase = "UPDATE `" + dbTable + "` SET  `" + this.getCellName() + "` = ? " + whereClause;
                PreparedStatement statement = conny.prepareStatement(SQLphrase);
                System.out.println("FACCIO UPDATE:this.getCellType()= " + this.getCellType() + "  -  this.getNewValue()= " + this.getNewValue()
                        + " _");

                int numval = 999999;
                try {
                    numval = Integer.parseInt(this.getNewValue());
                } catch (Exception ex) {

                }
                boolean isNumber = false;
                if (this.getNewValue().trim().equalsIgnoreCase("" + numval)) {
                    isNumber = true;
                }

                if (this.getCellType().equalsIgnoreCase("T")
                        || (this.getCellType().equalsIgnoreCase("R") && isNumber == false)
                        || this.getCellType().equalsIgnoreCase("D")
                        || this.getCellType().equalsIgnoreCase("TM")
                        || (this.getCellType().equalsIgnoreCase("S") && isNumber == false)
                        || this.getCellType().equalsIgnoreCase("DT")) {

                    statement.setString(1, this.getNewValue());

                } else {
                    int newNumber = 0;
                    try {
                        newNumber = Integer.parseInt(this.getNewValue());
                    } catch (Exception e) {
                        newNumber = 0;
                    }
                    statement.setInt(1, newNumber);

                }
                myEvent.setInfo1(statement.toString());
                System.out.println("executeCRUD-->SQLphrase:" + statement.toString());
                errorMessage = "ERROR ON UPDATE";
                int Result = statement.executeUpdate();
                if (Result > 0) {
                    jsonAnswer = new JSONObject();
                    jsonAnswer.put("sender", "CRUD");
                    jsonAnswer.put("operation", "UPD");
                    jsonAnswer.put("code", "OK");
                    jsonAnswer.put("mess", "UPDATED");
                    jsonAnswer.put("formType", formType);
                    jsonAnswer.put("actionPassed", actionPassed);

//                    String jsonAnswer = "{\"sender\":\"CRUD\",\"operation\":\"UPD\",\"code\":\"OK\",\"mess\":\"UPDATED\",\"formType\":\"" + formType + "\",\"actionPassed\":\"" + actionPassed + "\" }";
//                    answer = jsonAnswer;
                } else {
                    jsonAnswer = new JSONObject();
                    jsonAnswer.put("sender", "CRUD");
                    jsonAnswer.put("operation", "UPD");
                    jsonAnswer.put("code", "ERR");
                    jsonAnswer.put("mess", "ERROR WHILE UPDATING.");
//                    String jsonAnswer = "{\"sender\":\"CRUD\",\"operation\":\"UPD\",\"code\":\"ERR\",\"mess\":\"ERROR WHILE UPDATING.\" }";
//                    answer = jsonAnswer;
                }

            }
// </editor-fold>     
        } catch (SQLException ex) {
            jsonAnswer = new JSONObject();
            jsonAnswer.put("sender", "CRUD");
            jsonAnswer.put("operation", "NOP");
            jsonAnswer.put("code", "ERR");
            jsonAnswer.put("mess", errorMessage);
//            String jsonAnswer = "{\"sender\":\"CRUD\",\"operation\":\"NOP\",\"code\":\"ERR\",\"message\":\"" + errorMessage + "\"}";
//            answer = jsonAnswer;

            System.out.println("errorMessage 1497:" + errorMessage + "_" + ex.toString());
        }
        try {
            conny.close();
        } catch (SQLException ex) {

        }

        /*
        FATTO IL CRUD se era un tipo tree, manderò come risposta in caso di ADD la leaf per il nuovo LI
        in caso di UPDATE la leaf aggiornata con la dicitura corretta (però in un treer l'update per il momento non è previsto
        in caso di DEL l'ID del LI da eliminare
         */
        if (this.formType != null && this.formType.equalsIgnoreCase("SMARTTREE") && this.getOperation().equalsIgnoreCase("ADD")) {

        }
        answer = jsonAnswer.toString();
        //--------------------------
        myEvent.setInfo2(answer);
        myEvent.save(myParams, mySettings);
        return answer;
    }

    public String standardReplace(String defVal, String radix) {

        if (radix != null && radix.length() > 0) {
            radix = radix.replaceAll("[^\\w\\d]", "");//toglie tutti i caratteri speciali
        } else {
            radix = "";
        }

        //       System.out.println("standardReplace Step 1 :" + defVal);
//        System.out.println("Step mySettings.getProjectName() :" + mySettings.getProjectName());
        int step = 0;
        try {
            //`databases`
            try {
                defVal = defVal.replace(" databases ", "`databases`");
            } catch (Exception e) {
////                System.out.println("CRUD ORDER: ERROR standardReplace :" + e.toString());
////                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);

            }
            try {
                defVal = defVal.replace("$$$KEY$$$", this.getFatherKEYvalue());
            } catch (Exception e) {
//                System.out.println("CRUD ORDER: $$$KEY$$$ ERROR standardReplace :" + e.toString());
//                 Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);
            }
            try {
                defVal = defVal.replace("$$$account_TABLEoperatori$$$", mySettings.getAccount_TABLEoperatori());
            } catch (Exception e) {
//                System.out.println("CRUD ORDER: $$$KEY$$$ ERROR standardReplace :" + e.toString());
//                 Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);
            }
            try {
                defVal = defVal.replace("$$$NOW$$$", " NOW() ");
            } catch (Exception e) {
//                System.out.println("CRUD ORDER: ERROR standardReplace :" + e.toString());
//                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);

            }
            try {
                defVal = defVal.replace("$$$DATETIMENOW$$$", " NOW() ");
            } catch (Exception e) {
//                System.out.println("CRUD ORDER: ERROR standardReplace :" + e.toString());
//                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);

            }
            try {
                defVal = defVal.replace("$$$TODAY$$$", " date(NOW()) ");
            } catch (Exception e) {
//                System.out.println("CRUD ORDER: ERROR standardReplace :" + e.toString());
//                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);

            }
            try {
                defVal = defVal.replace("$$$TIMENOW$$$", " CURTIME() ");
            } catch (Exception e) {
//                System.out.println("CRUD ORDER: ERROR standardReplace :" + e.toString());
//                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);

            }

            try {
                defVal = defVal.replace("$$$CKuserID$$$", myParams.getCKuserID());
            } catch (Exception e) {
            }
            try {
                defVal = defVal.replace("$$$CKuser$$$", myParams.getCKuserID());
            } catch (Exception e) {
            }
            try {
                defVal = defVal.replace("$$$USER$$$", myParams.getCKuserID());
            } catch (Exception e) {
//                System.out.println("CRUD ORDER: ERROR standardReplace :" + e.toString());
                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);

            }
            try {
                defVal = defVal.replace("$$$userID$$$", myParams.getCKuserID());
            } catch (Exception e) {
            }
            try {
//                EVOpagerDirectivesManager myDirective = new EVOpagerDirectivesManager(myParams, mySettings);
                //defVal = defVal.replace("$$$APPNAME$$$", myDirective.getDirective("softwareTitle"));
                defVal = defVal.replace("$$$APPNAME$$$", mySettings.getSoftwareTitle());
            } catch (Exception e) {
//                System.out.println("CRUD ORDER:$$$APPNAME$$$ ERROR standardReplace :" + e.toString());
//                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);

            }

            try {
                if (defVal.contains("$$$CURUSERNAME$$$")) {
                    EVOuser myUser = new EVOuser(myParams, mySettings);
                    myUser.loadDBinfos();
                    String nomeUtente = myUser.getExtendedName();
                    nomeUtente = nomeUtente.replaceAll("<BR>", " ");
                    System.out.println("nomeUtente :" + nomeUtente);
                    step = 9;
                    defVal = defVal.replace("$$$CURUSERNAME$$$", nomeUtente);

                }
            } catch (Exception e) {
//                System.out.println("CRUD ORDER: ERROR standardReplace :" + e.toString());
//                Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);

            }
            String newID = radix.trim() + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

            newID = newID.replaceAll("[^\\w\\s]", "");

            UUID idOne = null;
            idOne = UUID.randomUUID();
            String newToken = "" + idOne;
            newToken = newToken.replace("-", "");
            newToken += "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

            String RT10newID = newID.substring(0, 8) + newToken.substring(0, 8);
            RT10newID = RT10newID.replace("-", "");
            defVal = defVal.replace("$$$RANDOMTEXT10$$$", RT10newID);
            step = 12;

            String RT16newID = newID.substring(0, 8) + newToken.substring(0, 16);
            RT16newID = RT16newID.replace("-", "");
            defVal = defVal.replace("$$$RANDOMTEXT16$$$", RT16newID);
            step = 13;

            String RT32newID = newID.substring(0, 8) + newToken.substring(0, 32);
            RT32newID = RT32newID.replace("-", "");
            defVal = defVal.replace("$$$RANDOMTEXT32$$$", RT32newID);
            step = 14;

            String RT64newID = newID.substring(0, 8) + newToken.substring(0, 64);
            RT64newID = RT64newID.replace("-", "");
            defVal = defVal.replace("$$$RANDOMTEXT64$$$", RT64newID);
            step = 15;

            newID = newID.substring(0, 5) + "_" + newToken;
            newID = newID.replace("-", "");
            defVal = defVal.replace("$$$RANDOMTEXT$$$", newID);
            step = 16;

            defVal = defVal.replace("$$$newRandom$$$", newToken);

        } catch (Exception e) {
//            System.out.println("step :" + step+" - CRUD ORDER: ERROR standardReplace :" + e.toString());
//            Logger.getLogger(CRUDorder.class.getName()).log(Level.SEVERE, null, e);

        }
        //   System.out.println("Step 1 RRESULT:" + defVal);

        //   System.out.println("Step 2 :");
        if (myParams.getCKuserID()
                != null) {
            // if (query.contains("$$$RANGO$$$")){
            EVOuser myUser = new EVOuser(myParams, mySettings);
            int xRango;
            xRango = myUser.getStoredRango(myParams.getCKuserID());
            defVal = defVal.replace("$$$RANGO$$$", "" + xRango);
        }
        //    System.out.println("Step 2 RRESULT:" + defVal);

        // ora inizio il parsing delle info da StC
//           System.out.println("standardReplace Step 3 getSendToCRUD:" + params);
//        System.out.println("standardReplace--->applico sostituzioni con SendToCRUD:" + this.getSendToCRUD());
        defVal = replaceMarkers(defVal, decodeURLstring(this.getSendToCRUD()));
//        System.out.println("standardReplace--->applico sostituzioni con ToBeSent:" + this.getToBeSent());
        defVal = replaceMarkers(defVal, decodeURLstring(this.getToBeSent()));

        System.out.println(" *REPLACE RESULT:" + defVal);
        return defVal;
    }

    private String decodeURLstring(String encoded) {
        String decoded = "";
        decoded = encoded;
        if (decoded != null && decoded.length() > 0) {

            String beforeDecoding = "";
            int attempt = 0;
            while (!beforeDecoding.equals(decoded) && attempt < 10) {
                attempt++;
                try {
                    beforeDecoding = decoded;
                    decoded = java.net.URLDecoder.decode(decoded, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                    decoded = beforeDecoding;
                }
            }

        }
//        System.out.println("decoded=" + decoded);

        return decoded;
    }

    private String replaceMarkers(String phrase, String params) {

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        String TBSarray = null;
        String xValue = null;
        String xMarker = null;
        String xType = null;
        String phraseReplaced = phrase;

        String tbsJson = "{\"TBS\":" + params + "}";
        if (params != null && params.length() > 0) {
//            System.out.println("CRUDorder tbsJson:" + tbsJson);
            try {
                jsonObject = (JSONObject) jsonParser.parse(tbsJson);
                TBSarray = jsonObject.get("TBS").toString();
                if (TBSarray != null && TBSarray.length() > 0) {
                    JSONParser parser = new JSONParser();
                    Object obj;
                    obj = parser.parse(TBSarray);
                    JSONArray array = (JSONArray) obj;
                    for (Object riga : array) {
                        //rows++;
                        jsonObject = (JSONObject) jsonParser.parse(riga.toString());

                        try {
                            xType = jsonObject.get("childType").toString();
                        } catch (Exception e) {
                        }

                        try {
                            xMarker = jsonObject.get("childMarker").toString();
                        } catch (Exception e) {
                        }

                        try {
                            xValue = jsonObject.get("value").toString();
                        } catch (Exception e) {
                        }

                        PreparedStatement ps;

//   System.out.println(" xMarker:" + xMarker+" xValue:" + xValue+" xType:" + xType);
                        if (xValue != null && xMarker != null) {
                            xValue = encodeMYSQLstring(xValue);
//                        System.out.println("* xMarker:" + xMarker + " xValue:" + xValue + " xType:" + xType);
                            if (xType.equalsIgnoreCase("formField")) {
                                String toBeReplaced = "###" + xMarker + "###";
                                if (phraseReplaced.contains(toBeReplaced)) {
                                    phraseReplaced = phraseReplaced.replace(toBeReplaced, xValue);
                                }
                                toBeReplaced = "@@@" + xMarker + "@@@";
                                if (phraseReplaced.contains(toBeReplaced)) {
                                    phraseReplaced = phraseReplaced.replace(toBeReplaced, xValue);
                                }
                            }
                            if (xType.equalsIgnoreCase("panelFilter")) {
                                String toBeReplaced = "###" + xMarker + "###";
                                if (phraseReplaced.contains(toBeReplaced)) {
                                    phraseReplaced = phraseReplaced.replace(toBeReplaced, xValue);
                                }
                            } else if (xType.equalsIgnoreCase("rowField")) {
                                String toBeReplaced = "@@@" + xMarker + "@@@";
                                if (phraseReplaced.contains(toBeReplaced)) {
                                    phraseReplaced = phraseReplaced.replace(toBeReplaced, xValue);
                                }
                            } else if (xType.equalsIgnoreCase("overall")) {

                            }
                        }
//                        if (rows < 2) {
//                            System.out.println(" xMarker:" + xMarker + " xValue:" + xValue);
//                        }
                    }
                }
            } catch (ParseException ex) {
                System.out.println(" err3:" + ex);
            }

        }

        return phraseReplaced;
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
