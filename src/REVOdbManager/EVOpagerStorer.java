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
 *//*
DEPRECATED !!!!!!!!!!!!!!!!!
 */
package REVOdbManager;

import REVOpager.Database;
import REVOpager.EVOpagerDBconnection;
import REVOpager.Server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Franco
 */
public class EVOpagerStorer {

    EVOpagerParams myParams;
    Settings mySettings;
    Server server;
    Database database;
    boolean verbose = false;

    private String mainTable = "";
    private String[] fieldName = new String[100];
    private String[] fieldValue = new String[100];
    private String[] fieldType = new String[100];
    private int NofFields;
    private String[] campo = new String[100];
    private String[] tipo = new String[100];
    private String[] valore = new String[100];
    private String[] modificato = new String[100];

    private String whereClause = "";

    // private ServletContext myContext;
    private String returnedValue = "";

    public EVOpagerStorer(Server myServer, Database myDatabase) {

        System.out.println("EVOpagerStorer: acquisito server.");
        this.server = myServer;
        this.database = myDatabase;
        whereClause = null;
        returnedValue = "";

        System.out.println(" server." + server.getDefaultSQLserver());

    }

    public EVOpagerStorer(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public String getReturnedValue() {
        return returnedValue;
    }

    public void setReturnedValue(String returnedValue) {
        this.returnedValue = returnedValue;
    }

    public String[] getCampo() {
        return campo;
    }

    public void setCampo(String[] campo) {
        this.campo = campo;
    }

    public String[] getTipo() {
        return tipo;
    }

    public void setTipo(String[] tipo) {
        this.tipo = tipo;
    }

    public String[] getValore() {
        return valore;
    }

    public void setValore(String[] valore) {
        this.valore = valore;
    }

    public String[] getModificato() {
        return modificato;
    }

    public void setModificato(String[] modificato) {
        this.modificato = modificato;
    }

    public String getMainTable() {
        return mainTable;
    }

    public void setMainTable(String mainTable) {
        this.mainTable = mainTable;
    }

    public String getXFieldName(int position) {
        return fieldName[position];
    }

    public void setXFieldName(int position, String fieldName) {
        this.fieldName[position] = fieldName;
    }

    public String getXFieldValue(int position) {
        return fieldValue[position];
    }

    public void setXFieldValue(int position, String fieldValue) {
        this.fieldValue[position] = fieldValue;
    }

    public String getXFieldType(int position) {
        return fieldType[position];
    }

    public void setXFieldType(int position, String fieldType) {
        this.fieldType[position] = fieldType;
    }

    public int getNofFields() {
        return NofFields;
    }

    public void setNofFields(int NofFields) {
        this.NofFields = NofFields;
    }

    public int insertRecord() {

        System.out.println("insertRecord>INSERISCO " + NofFields + " fields.");
        int response = 0;

        int flagAsIs = 0;
        for (int jj = 1; jj <= NofFields; jj++) {

            //.DATE................................     
            if (tipo[jj].equalsIgnoreCase("date") || tipo[jj].equalsIgnoreCase("datetime")) {
                if (valore[jj] == null || valore[jj] == "") {

                    fieldValue[jj] = null;
                    fieldType[jj] = "string";
                } else {
                    String giorno = valore[jj].substring(0, 2);
                    String mese = valore[jj].substring(3, 5);
                    String anno = valore[jj].substring(6, 10);
                    fieldValue[jj] = anno + "-" + mese + "-" + giorno;
                    fieldType[jj] = "string";
                }
            } else //.INT................................       
            if (tipo[jj].equalsIgnoreCase("int") || tipo[jj].equalsIgnoreCase("chk")) {
                if (valore[jj] == null || valore[jj] == "") {
                    valore[jj] = null;
                }
                fieldValue[jj] = valore[jj];
                fieldType[jj] = "int";
            } else if (tipo[jj].equalsIgnoreCase("AsIs")) {
                flagAsIs++;
                if (valore[jj] == null || valore[jj] == "") {
                    valore[jj] = null;
                }
                fieldValue[jj] = valore[jj];
                fieldType[jj] = "AsIs";
            } else //.TUTTI GLI ALTRI CASI................................       
            {
                fieldValue[jj] = valore[jj];
                fieldType[jj] = "string";
            }

//................................
// adesso ufficializzo anche il valore del nome campo che potrebbe essere cambiato in base alle personalizzazioni                     
            fieldName[jj] = campo[jj];
            System.out.println("insertRecord>fieldName=" + fieldName[jj] + "  .");

        }   // chiude il FOR

        Connection conny;
        PreparedStatement ps;

        EVOpagerDBconnection myCon = new EVOpagerDBconnection();
        // conny = myCon.makeConnection(server, database);
        //Connection makeConnection(String driver, String URL, String alternativeURL, String DBname, String USERNAME, String PASSWORD) {
      
        String DRIVER = server.getSQLdriver();
        String DBname = database.getDbExtendedName();
        String URL = database.getServer().getDefaultSQLserver();
        String alternativeURL = database.getServer().getAlternativeSQLserver();
        String USERNAME = database.getServer().getDATABASE_USER();
        String PASSWORD = database.getServer().getDATABASE_PW();
        conny = myCon.makeConnection(DRIVER, URL, alternativeURL, DBname, USERNAME, PASSWORD);

        String updateString = "INSERT INTO `" + mainTable + "` ";

        System.out.println("insertRecord>NofFields =" + NofFields);
        if (flagAsIs < 1) {

            if (NofFields > 0) {

                try {
                    // creo la stringa del ps
                    updateString += "(";
                    for (int jj = 1; jj <= NofFields; jj++) {
                        if (jj > 1) {
                            updateString += ",";
                        }
                        updateString += fieldName[jj];
                    }
                    updateString += ")VALUES(";
                    for (int jj = 1; jj <= NofFields; jj++) {
                        if (jj > 1) {
                            updateString += ",";
                        }
                        updateString += "?";
                    }
                    updateString += ")";

                    ps = conny.prepareStatement(updateString);
                    System.out.println("ps: " + ps);
                    // carico i valori nel ps
                    for (int jj = 1; jj <= NofFields; jj++) {
                        if (fieldType[jj].equalsIgnoreCase("int")) {
                            int number = 0;
                            if (fieldValue[jj] != null && !fieldValue[jj].isEmpty()) {
                                try {
                                    number = Integer.parseInt(fieldValue[jj]);
                                } catch (Exception ex) {
                                    number = 0;
                                }
                            }
                            ps.setInt(jj, number);
                            System.out.println("Valore #" + jj + " NUMERICO = " + number);

                        } else {
                            ps.setString(jj, fieldValue[jj]);
                            System.out.println("Valore #" + jj + " TESTO = " + fieldValue[jj]);
                        }
                    }   // chiude for

                    System.out.println(ps.toString());

                    int i = ps.executeUpdate();
                    if (i > 0) {
                        System.out.println("insert ok !");
                        response = 1;

                    } else {
                        System.out.println("errore in fase di inserimento!");
                        response = 0;
                    }

                    conny.close();
                    conny = null;
                    myCon.close();
                    myCon = null;

                } catch (SQLException ex) {
                    Logger.getLogger(EVOpagerStorer.class.getName()).log(Level.SEVERE, null, "insertRecord:" + ex);

                }

            }
        } else {

            try {
                /*flag AsIs è positivo !!!!
                 fieldValue[jj]
                 fieldType[jj]
                 fieldName[jj]
                 */
                System.out.println("AsIs positive ! ");
                updateString += "(";
                for (int jj = 1; jj <= NofFields; jj++) {
                    if (jj > 1) {
                        updateString += ",";
                    }
                    updateString += fieldName[jj];
                }
                updateString += ")VALUES(";
                for (int jj = 1; jj <= NofFields; jj++) {
                    if (jj > 1) {
                        updateString += ",";
                    }
                    if (fieldType[jj].equalsIgnoreCase("string")) {
                        updateString += "'";
                    }
                    updateString += fieldValue[jj];
                    if (fieldType[jj].equalsIgnoreCase("string")) {
                        updateString += "'";
                    }

                }
                updateString += ")";
                System.out.println("updateString: " + updateString);

                ps = conny.prepareStatement(updateString);
                System.out.println("ps: " + ps.toString());

                int i = ps.executeUpdate();
                conny.close();
                conny = null;
                myCon.close();
                myCon = null;

            } catch (SQLException ex) {
                Logger.getLogger(EVOpagerStorer.class.getName()).log(Level.SEVERE, null, "insertRecord[AsIs]:" + ex);

            }

        }

        conny = null;
        return response;

    }

    public int updateRecord() {
        int response = 0;

        int flagAsIs = 0;
        for (int jj = 1; jj <= NofFields; jj++) {

            //.DATE................................     
            if (tipo[jj].equalsIgnoreCase("date") || tipo[jj].equalsIgnoreCase("datetime")) {
                if (valore[jj] == null || valore[jj] == "") {

                    fieldValue[jj] = null;
                    fieldType[jj] = "string";
                } else {
                    String giorno = valore[jj].substring(0, 2);
                    String mese = valore[jj].substring(3, 5);
                    String anno = valore[jj].substring(6, 10);
                    fieldValue[jj] = anno + "-" + mese + "-" + giorno;
                    fieldType[jj] = "string";
                }
            } else //.INT................................       
            if (tipo[jj].equalsIgnoreCase("int") || tipo[jj].equalsIgnoreCase("chk")) {
                if (valore[jj] == null || valore[jj] == "") {
                    valore[jj] = null;
                }
                fieldValue[jj] = valore[jj];
                fieldType[jj] = "int";
            } else if (tipo[jj].equalsIgnoreCase("AsIs")) {
                flagAsIs++;
                if (valore[jj] == null || valore[jj] == "") {
                    valore[jj] = null;
                }
                fieldValue[jj] = valore[jj];
                fieldType[jj] = "AsIs";
            } else //.TUTTI GLI ALTRI CASI................................       
            {
                fieldValue[jj] = valore[jj];
                fieldType[jj] = "string";
            }

//................................
// adesso ufficializzo anche il valore del nome campo che potrebbe essere cambiato in base alle personalizzazioni                     
            fieldName[jj] = campo[jj];

        }   // chiude il FOR

        Connection conny;
        PreparedStatement ps;
        EVOpagerDBconnection myCon = new EVOpagerDBconnection();
        
        String DRIVER = server.getSQLdriver();
        String DBname = database.getDbExtendedName();
        String URL = database.getServer().getDefaultSQLserver();
        String alternativeURL = database.getServer().getAlternativeSQLserver();
        String USERNAME = database.getServer().getDATABASE_USER();
        String PASSWORD = database.getServer().getDATABASE_PW();
        conny = myCon.makeConnection(DRIVER, URL, alternativeURL, DBname, USERNAME, PASSWORD);

        String updateString = "UPDATE `" + mainTable + "` SET ";

        if (flagAsIs < 1) {

            if (NofFields > 0) {
                try {
                    // creo la stringa del ps
                    updateString += " ";
                    for (int jj = 1; jj <= NofFields; jj++) {
                        if (jj > 1) {
                            updateString += ",";
                        }
                        updateString += fieldName[jj];
                        updateString += "=";
                        updateString += "?";

                    }
                    if (whereClause != null && whereClause != "null") {
                        updateString += " " + whereClause;
                    }
                    ps = conny.prepareStatement(updateString);
                    System.out.println("ps: " + ps);
                    // carico i valori nel ps
                    for (int jj = 1; jj <= NofFields; jj++) {
                        if (fieldType[jj].equalsIgnoreCase("int")) {
                            int number = 0;
                            if (fieldValue[jj] != null && !fieldValue[jj].isEmpty()) {
                                try {
                                    number = Integer.parseInt(fieldValue[jj]);
                                } catch (Exception ex) {
                                    number = 0;
                                }
                            }
                            ps.setInt(jj, number);
                            System.out.println("Valore #" + jj + " NUMERICO = " + number);

                        } else {
                            ps.setString(jj, fieldValue[jj]);
                            System.out.println("Valore #" + jj + " TESTO = " + fieldValue[jj]);
                        }
                    }   // chiude for

                    System.out.println(ps.toString());

                    int i = ps.executeUpdate();
                    if (i > 0) {
                        System.out.println("update OK !");
                        response = 1;

                    } else {
                        System.out.println("CDR:C'è stato un errore in fase di AGGIUNTA!");
                        response = 0;
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(EVOpagerStorer.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } else {

            try {
                /*flag AsIs è positivo !!!!
                 fieldValue[jj]
                 fieldType[jj]
                 fieldName[jj]
                 */
                // non uso il ps con il '?'
                System.out.println("AsIs positive ! ");
                for (int jj = 1; jj <= NofFields; jj++) {
                    if (jj > 1) {
                        updateString += ",";
                    }
                    updateString += fieldName[jj];
                    updateString += "=";
                    if (fieldType[jj].equalsIgnoreCase("string")) {
                        updateString += "'";
                    }
                    updateString += fieldValue[jj];
                    if (fieldType[jj].equalsIgnoreCase("string")) {
                        updateString += "'";
                    }
                }

                updateString += " " + whereClause;

                ps = conny.prepareStatement(updateString);
                System.out.println("ps: " + ps.toString());

                int i = ps.executeUpdate();
                ps = conny.prepareStatement(updateString);

            } catch (SQLException ex) {
                Logger.getLogger(EVOpagerStorer.class.getName()).log(Level.SEVERE, null, "updateRecord:" + ex);
            }
        }
        try {
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(EVOpagerStorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        conny = null;

        myCon.close();
        myCon = null;

        return response;

    }

    public int deleteRecord() {
        int resp = 0;

        Connection conny;
        PreparedStatement ps;
        EVOpagerDBconnection myCon = new EVOpagerDBconnection();
        
        String DRIVER = server.getSQLdriver();
        String DBname = database.getDbExtendedName();
        String URL = database.getServer().getDefaultSQLserver();
        String alternativeURL = database.getServer().getAlternativeSQLserver();
        String USERNAME = database.getServer().getDATABASE_USER();
        String PASSWORD = database.getServer().getDATABASE_PW();
        conny = myCon.makeConnection(DRIVER, URL, alternativeURL, DBname, USERNAME, PASSWORD);
        String updateString = "DELETE FROM `" + mainTable + "` ";

        if (whereClause != null && whereClause != "null") {
            try {
                updateString += " " + whereClause;
                ps = conny.prepareStatement(updateString);
                System.out.println("ps: " + ps);

                System.out.println(ps.toString());

                int i = ps.executeUpdate();
                if (i > 0) {
                    System.out.println("update OK !");
                    resp = 1;

                } else {
                    System.out.println("CDR:C'è stato un errore(1) in fase di ELIMINAZIONE!");
                    resp = 0;
                }
                conny.close();

            } catch (SQLException ex) {
                System.out.println("CDR:C'è stato un errore(2) in fase di ELIMINAZIONE!");
                resp = 0;
            }

        }
        conny = null;

        myCon.close();
        myCon = null;
        System.out.println("Esco da DELETE." + resp);
        return resp;

    }

    public int verificaEsistenza(String table, String field, String Value, String fieldType, String Action) {
        verbose = false;
        int response = 1;
        String SQLphrase = "";

        try {

            Connection conny;
            PreparedStatement ps;
            ResultSet rs;
            /*   EVOpagerDBconnection myCon = new EVOpagerDBconnection(myParams,mySettings);
             conny = myCon.ConnLocalStandardDB();
             */
            EVOpagerDBconnection myCon = new EVOpagerDBconnection();
            
        String DRIVER = server.getSQLdriver();
        String DBname = database.getDbExtendedName();
        String URL = database.getServer().getDefaultSQLserver();
        String alternativeURL = database.getServer().getAlternativeSQLserver();
        String USERNAME = database.getServer().getDATABASE_USER();
        String PASSWORD = database.getServer().getDATABASE_PW();
        conny = myCon.makeConnection(DRIVER, URL, alternativeURL, DBname, USERNAME, PASSWORD);

            if (whereClause != null && whereClause != "") {
                SQLphrase = "SELECT * FROM " + table + " " + whereClause;
            } else {
                SQLphrase = "SELECT * FROM " + table + " WHERE " + field + "='" + Value + "'";
            }
            if (verbose) {
                System.out.println("verificaEsistenza:" + SQLphrase);
            }
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            int lines = 0;
            //System.out.println("Action:" + Action.substring(0,3));
            while (rs.next()) {
                if (Action != null && Action.substring(0, 3).equalsIgnoreCase("get")) {
                    String fieldToSearch = Action.substring(4);
                    // System.out.println("Cerco campo:" + fieldToSearch);

                    returnedValue = rs.getString(fieldToSearch);

                }

                lines++;
                break;
            }
            if (lines > 0) {
                response = 1;
            } else {
                response = 0;
            }
//System.out.println("Response:" + response + "verificaEsistenza:" + SQLphrase);
            rs = null;
            conny.close();
            myCon.close();
            conny = null;
            myCon = null;

        } catch (SQLException ex) {
            Logger.getLogger(EVOpagerStorer.class.getName()).log(Level.SEVERE, null, "verificaEsistenza:" + ex);
            response = -1;
        }
        if (verbose) {
            System.out.println("verificaEsistenza.response=" + response);
        }
        return response;
    }

    public int trovaMinMax(String table, String field, String fieldType, String Action) {
        verbose = false;
        int response = 1;
        String SQLphrase = "";

        String comparator = "MAX";
        if (Action.equalsIgnoreCase("MIN")) {
            comparator = "MIN";
        }

        try {

            Connection conny;
            PreparedStatement ps;
            ResultSet rs;
            EVOpagerDBconnection myCon = new EVOpagerDBconnection(myParams, mySettings);
            conny = myCon.ConnLocalDataDB();
            if (whereClause != null && whereClause != "") {
                SQLphrase = "SELECT " + comparator + "(" + field + ") FROM " + table + " " + whereClause;
            } else {
                SQLphrase = "SELECT " + comparator + "(" + field + ") FROM " + table + " ";
            }
            if (verbose) {
                System.out.println("verificaEsistenza:" + SQLphrase);
            }
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            int lines = 0;
            //System.out.println("Action:" + Action.substring(0,3));
            while (rs.next()) {
                if (Action != null && Action.substring(0, 3).equalsIgnoreCase("get")) {
                    String fieldToSearch = Action.substring(4);
                    // System.out.println("Cerco campo:" + fieldToSearch);

                    returnedValue = rs.getString(fieldToSearch);

                }

                lines++;
                break;
            }
            if (lines > 0) {
                response = 1;
            } else {
                response = 0;
            }
//System.out.println("Response:" + response + "verificaEsistenza:" + SQLphrase);
            rs = null;
            conny.close();
            myCon.close();
            conny = null;
            myCon = null;

        } catch (SQLException ex) {
            Logger.getLogger(EVOpagerStorer.class.getName()).log(Level.SEVERE, null, "verificaEsistenza:" + ex);
            response = -1;
        }
        if (verbose) {
            System.out.println("verificaEsistenza.response=" + response);
        }
        return response;
    }

}
