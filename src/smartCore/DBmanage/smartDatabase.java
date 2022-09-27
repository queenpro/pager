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
package smartCore.DBmanage;

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.EVOpagerDBconnection;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import showIt.ShowItForm;

/**
 *
 * @author Franco Venezia @ www.ffs.it
 */
public class smartDatabase {

    EVOpagerParams myParams;
    Settings mySettings;
    public String name;
    public String ID;
    public ArrayList<Table> tables;
    public int index;
    public ArrayList<tableToClone> tablesToClone;

    public smartDatabase(EVOpagerParams myParams, Settings mySettings, int index) {
        tablesToClone = new ArrayList();
        this.myParams = myParams;
        this.mySettings = mySettings;
        tables = new ArrayList();
    }

    public JSONArray cloneByMap() {

////////        tableToClone myMapTable = new tableToClone();
////////        myMapTable.tableName = "databases";
////////        myMapTable.keyField = "ID";
////////        myMapTable.keyType = "VARCHAR";
////////        myMapTable.keyValue = "matrix";
////////
////////        myMapTable.Map = new map();
////////
////////        mapLink myLink = new mapLink();
////////        myLink.position = 0;
////////        myLink.fatherTable = "databases";
////////        myLink.fatherField = "ID";
////////        myLink.childTable = "tables";
////////        myLink.childField = "rifDatabase";
////////        myMapTable.Map.links.add(myLink);
////////
////////        myLink = new mapLink();
////////        myLink.position = 1;
////////        myLink.fatherTable = "tables";
////////        myLink.fatherField = "ID";
////////        myLink.childTable = "fields";
////////        myLink.childField = "rifTable";
////////        myMapTable.Map.links.add(myLink);
////////
////////        tablesToClone.add(myMapTable);
        //-------------------------------------
        JSONArray storedTables = new JSONArray();

        ArrayList<Table> DBtables = new ArrayList();
        for (int ttc = 0; ttc < tablesToClone.size(); ttc++) {
            JSONObject newStoredTable = new JSONObject();
            newStoredTable.put("type", "STOREDTABLE");
            newStoredTable.put("index", index);
            newStoredTable.put("name", tablesToClone.get(ttc).tableName);
            newStoredTable.put("keyField", tablesToClone.get(ttc).keyField);
            newStoredTable.put("keyValue", tablesToClone.get(ttc).keyValue);
            newStoredTable.put("keyType", tablesToClone.get(ttc).keyType);
            Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
            Table DBtable = loadTabRows(conny, tablesToClone.get(ttc));
            newStoredTable.put("rows", DBtable.contentJSON);
            storedTables.add(newStoredTable);

            // verifico se questa tabella che ho copiato ha dei link su altre tabelle
            // devo evitare ricorsività 
            for (exportMapLink xLink : tablesToClone.get(ttc).Map.links) {
                if (xLink.fatherTable.equalsIgnoreCase(tablesToClone.get(ttc).tableName)) {
                    // ho trovato un link alla tabella che sto clonando
                    /*
                    myLink.position = 0;
        myLink.fatherTable = "databases";
        myLink.fatherField = "ID";
        myLink.childTable = "tables";
        myLink.childField = "rifDatabase";
                     */

                    //PER OGNI RIGA DELLA TABELLA APPENA COPIATA...(tcc)
                    //[{"keyField":"ID","keyValue":"matrix","name":"databases","type":"STOREDTABLE","keyType":"VARCHAR","rows":[{"alive":1,"release":5,"minSWrelease":1,"name":"matrix","ID":"matrix","operatore":null,"rifProject":"matrix","recorded":"2022-03-23 15:48:52.856","version":1}]}]
                    for (Object xtab : storedTables) {
                        JSONObject Xtab = (JSONObject) xtab;
                        if (Xtab.get("name").toString().equals(tablesToClone.get(ttc).tableName)) {
                            Object xrows = Xtab.get("rows");
                            JSONArray Xrows = (JSONArray) xrows;

                            System.out.println("\n\n************\n\n INIZIO PARSING RIGHE CHILD DI " + xLink.childTable);
                            System.out.println("LE RIGHE DA PARSARE SONO: " + Xrows.size());
                            int rowsParsed = 0;
                            for (Object trow : Xrows) {
                                rowsParsed++;
                                JSONObject Trow = (JSONObject) trow;
                                System.out.println("\n------RIGA DI TABELLA " + rowsParsed + "\nREAD JSON: " + Trow.toString());
                                // ho isolato una riga... la devo parsare secondo i link che le competono
                                // la tabella child da andare a recuperare è in xLink.childTable
                                // nella tabella child devo avere il campo xLink.childField uguale al campo xLink.fatherfield (che prima devo conoscere)
                                String fatherType;
                                String fatherFieldValueTXT;
                                int fatherFieldValueINT = 0;

                                String SQLphrase = "";
                                for (Schema_column fatherColumn : DBtable.columns) {
                                    if (fatherColumn.COLUMN_NAME.equals(xLink.fatherField)) {
                                        SQLphrase = "SELECT * FROM " + xLink.childTable + " WHERE " + xLink.childField + " = ";
                                        if (fatherColumn.DATA_TYPE.contains("int")) {
                                            fatherType = "INT";
                                            try {
                                                fatherFieldValueINT = Integer.parseInt(Trow.get(xLink.fatherField).toString());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            SQLphrase += " " + fatherFieldValueINT + " ";
                                        } else {
                                            fatherType = "VARCHAR";
                                            fatherFieldValueTXT = Trow.get(xLink.fatherField).toString();
                                            SQLphrase += " '" + fatherFieldValueTXT + "' ";
                                        }
                                        System.out.println("CHILD SQL: " + SQLphrase);
                                        tableToClone myMapTable = new tableToClone();

                                        myMapTable.tableName = xLink.childTable;
                                        myMapTable.keyField = xLink.childField;
                                        myMapTable.keyType = fatherType;
                                        myMapTable.keyValue = Trow.get(xLink.fatherField).toString();

                                        myMapTable.Map.links = tablesToClone.get(ttc).Map.links;
                                        myMapTable.Map.autocompiles = tablesToClone.get(ttc).Map.autocompiles;
//                                        exportMapLink myLink = new exportMapLink(); 
//                                        myLink.position = 0;
//                                        myLink.fatherTable = "databases";
//                                        myLink.fatherField = "ID";
//                                        myLink.childTable = "tables";
//                                        myLink.childField = "rifDatabase";
//                                        myMapTable.Map.links.add(myLink);

//                                        myLink = new exportMapLink();
//                                        myLink.position = 1;
//                                        myLink.fatherTable = "tables";
//                                        myLink.fatherField = "ID";
//                                        myLink.childTable = "fields";
//                                        myLink.childField = "rifTable";
//                                        myMapTable.Map.links.add(myLink);
                                        int newIndex = 1 + this.index;
                                        smartDatabase mySM = new smartDatabase(myParams, mySettings, newIndex);
                                        mySM.tablesToClone.add(myMapTable);
                                        JSONArray childTab = mySM.cloneByMap();
                                        for (Object oggetto : childTab) {
                                            storedTables.add(oggetto);
                                        }
//                                        System.out.println(newIndex + ")childTab JSON: " + childTab.toString());
                                        break;

                                    }
                                }

                            }
                            break;
                        }
                    }// fine ciclo per StoredTables (ricavate dall'elenco iniziale)

                }
            }// fine ciclo per ogni link

        }

//        System.out.println(index + ") EXPORT JSON: " + storedTables.toString());
        return storedTables;
    }

    public static String[] getNames(JSONObject x) {
        Set<String> names = x.keySet();
        String[] r = new String[names.size()];
        int i = 0;
        for (String name : names) {
            r[i++] = name;
        }
        return r;
    }

    private Table loadTabRows(Connection conny, tableToClone myTTC) {
        Table myDBtable = new Table();
        try {
            myDBtable.name = myTTC.tableName;
            System.out.println("TABELLA DA DUPLICARE: " + myDBtable.name);
            myDBtable = getDBfields(myDBtable);
        } catch (SQLException ex) {
            Logger.getLogger(smartDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        int cc = 0;
        for (Schema_column column : myDBtable.columns) {
            cc++;
            System.out.println("Colonna n" + cc + "): " + column.COLUMN_NAME);
        }
//FASE 1. leggo e acquisisco su JSON tutte le righe della tabella che soddisfano la quesry
//in questa fase il campi KEY viene ricreato con un marcatore virtuale del tipo ###table@ID###
// i dati vanno caricati su un JSON
        JSONArray myTabContents = new JSONArray();
        String SQLphrase = "SELECT * FROM `" + myDBtable.name + "` WHERE  " + myTTC.keyField + " = ";
        if (myTTC.keyType.equalsIgnoreCase("INT")) {
            SQLphrase += " " + myTTC.keyValue + " ";
        } else {
            SQLphrase += " '" + myTTC.keyValue + "' ";
        }

        System.out.println("SQLphrase: " + SQLphrase);
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            System.out.println("INIZIO PARSING RIGHE.");
            int rowsParsed = 0;
            while (rs.next()) {
                rowsParsed++;
                System.out.println("\n---------PARSING RIGA: " + rowsParsed);
                JSONObject myRow = new JSONObject();
                for (Schema_column column : myDBtable.columns) {

//                    System.out.println("NAME: " + column.COLUMN_NAME + " TYPE:" + column.DATA_TYPE);
                    switch (column.DATA_TYPE) {
                        case "int":
                            myRow.put(column.COLUMN_NAME, rs.getInt(column.COLUMN_NAME));
                            break;
                        case "float":
                            myRow.put(column.COLUMN_NAME, rs.getFloat(column.COLUMN_NAME));
                            break;
                        case "double":
                            myRow.put(column.COLUMN_NAME, rs.getDouble(column.COLUMN_NAME));
                            break;
                        case "timestamp":
                            Timestamp tsTemp = new Timestamp(System.currentTimeMillis());
                            try {
                                tsTemp = rs.getTimestamp(column.COLUMN_NAME);
                            } catch (Exception e) {
                            }
                            myRow.put(column.COLUMN_NAME, tsTemp.toString());
                            break;
                        case "time":
                            myRow.put(column.COLUMN_NAME, rs.getString(column.COLUMN_NAME));
                            break;

                        case "blob":
                            String picCode = "";
                            Blob blob = null;
                            BufferedImage image = null;
                            blob = rs.getBlob(column.COLUMN_NAME);
                            InputStream in = null;
                            if (blob != null) {
                                try {
                                    in = blob.getBinaryStream();
                                    image = ImageIO.read(in);
                                } catch (IOException ex) {
                                    Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            if (image != null) {
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                String imageString = null;
                                if (image != null) {

                                    try {
                                        ImageIO.write(image, "gif", bos);
                                        byte[] imageBytes = bos.toByteArray();
                                        imageString = Base64.getEncoder().encodeToString(imageBytes);
                                        bos.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    imageString = "";
                                }
                                picCode = imageString;
                            } else {
                                picCode = "";
                            }
                            myRow.put(column.COLUMN_NAME, picCode);
                            break;
                        default:
                            String value = rs.getString(column.COLUMN_NAME);
                            myRow.put(column.COLUMN_NAME, value);
                    }

                }
                myTabContents.add(myRow);
            }

            System.out.println("TUTTE LE RIGHE DELLA TABELLA '" + myDBtable.name + "' (" + rowsParsed + "): " + myTabContents.toString());

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        myDBtable.contentJSON = myTabContents;

        return myDBtable;
    }

    public Table getDBfields(Table table) throws SQLException {
        String dbTable = table.name;
        Connection schemaconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalSchema();
        Statement schemast = schemaconny.createStatement();
        String SQLphrase = "SELECT * FROM COLUMNS\n"
                + "           WHERE TABLE_NAME = '" + dbTable + "'\n"
                + "             AND TABLE_SCHEMA = '" + mySettings.getProjectDBextendedName(myParams) + "' ORDER BY ORDINAL_POSITION;";
        ResultSet schemars = schemast.executeQuery(SQLphrase);
        int i = 0;
        /* 
                 String IS_NULLABLE[]=new String[100]; // ES. 'YES' oppure 'NO'
                 String COLUMN_KEY[]=new String[100]; // ES. 'PRI'=PRIMARY
                 String EXTRA[]=new String[100]; // ES. 'auto_increment'
         */
        ArrayList<Schema_column> columns = new ArrayList<Schema_column>();
        while (schemars.next()) {
            i++;
            Schema_column column = new Schema_column();
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
            if (result == null) {
//                column.setCHARACTER_MAXIMUM_LENGTH= null;
            } else {
                column.setCHARACTER_MAXIMUM_LENGTH(result.toBigInteger().intValueExact());
            }
            column.setNUMERIC_PRECISION(schemars.getInt("NUMERIC_PRECISION"));
            columns.add(column);
            System.out.println("inglobata colonna " + column.getCOLUMN_NAME() + " -->DATATYPE " + column.getDATA_TYPE() + " -->COLUMNTYPE " + column.getCOLUMN_TYPE() + " --> " + column.getCOLUMN_KEY() + " -->EXTRA: " + column.getEXTRA());
            if (column.getCOLUMN_KEY().contains("PRI")) {
                table.kyeField = column.getCOLUMN_NAME();
                if (column.getDATA_TYPE().contains("int")) {
                    table.keyType = "INT";
                } else {
                    table.keyType = "VARCHAR";
                }

            }

        }
        schemaconny.close();
        table.columns = columns;
        return table;
    }

//    public class map {
//
//        String ID;
//
//        public ArrayList<mapLink> links;
//        public ArrayList<mapAutocompile> autocompiles;
//
//        public map() {
//            links = new ArrayList();
//            autocompiles = new ArrayList();
//        }
//
//    }
//
//    public class tableToClone {
//
//        String tableName;
//        String keyField;
//        String keyType;
//        String keyValue;
//        map Map = new map();
//
//    }
//
//    public class mapLink {
//
//        int position;
//        String fatherTable;
//        String fatherField;
//        String childTable;
//        String childField;
//    }
//
//    public class mapAutocompile {
//
//        int position;
//        String fatherTable;
//        String fatherField;
//        String fatherFieldType;
//        String childTable;
//        String childField;
//    }
    public class Table {

        public ArrayList<Field> fields;
        public ArrayList<Schema_column> columns;
        public String name;
        public String ID;
        public String transferID;
        public String kyeField;
        public String keyType; //varchar oppure int
        public JSONArray contentJSON;

        public Table() {
            fields = new ArrayList();
            columns = new ArrayList();
        }

    }

    public class Field {

        public String name;
        public String type;
        public String value;
        public String ID;
        public String defaultValue;
        public int lenght;
        public int autoIncrement;
        public int primary;
        public int notNull;
        public int position;
    }

    public class Schema_column {

        String COLUMN_NAME;
        String COLUMN_DEFAULT;
        int ORDINAL_POSITION;
        String DATA_TYPE;
        int CHARACTER_MAXIMUM_LENGTH;
        String IS_NULLABLE; // ES. 'YES' oppure 'NO'
        String COLUMN_KEY; // ES. 'PRI'=PRIMARY
        String EXTRA; // ES. 'auto_increment'
        String COLUMN_TYPE;
        int NUMERIC_PRECISION;

        public String getCOLUMN_TYPE() {
            return COLUMN_TYPE;
        }

        public void setCOLUMN_TYPE(String COLUMN_TYPE) {
            this.COLUMN_TYPE = COLUMN_TYPE;
        }

        public int getNUMERIC_PRECISION() {
            return NUMERIC_PRECISION;
        }

        public void setNUMERIC_PRECISION(int NUMERIC_PRECISION) {
            this.NUMERIC_PRECISION = NUMERIC_PRECISION;
        }

        public String getCOLUMN_NAME() {
            return COLUMN_NAME;
        }

        public void setCOLUMN_NAME(String COLUMN_NAME) {
            this.COLUMN_NAME = COLUMN_NAME;
        }

        public String getCOLUMN_DEFAULT() {
            return COLUMN_DEFAULT;
        }

        public void setCOLUMN_DEFAULT(String COLUMN_DEFAULT) {
            this.COLUMN_DEFAULT = COLUMN_DEFAULT;
        }

        public int getORDINAL_POSITION() {
            return ORDINAL_POSITION;
        }

        public void setORDINAL_POSITION(int ORDINAL_POSITION) {
            this.ORDINAL_POSITION = ORDINAL_POSITION;
        }

        public String getDATA_TYPE() {
            return DATA_TYPE;
        }

        public void setDATA_TYPE(String DATA_TYPE) {
            this.DATA_TYPE = DATA_TYPE;
        }

        public int getCHARACTER_MAXIMUM_LENGTH() {
            return CHARACTER_MAXIMUM_LENGTH;
        }

        public void setCHARACTER_MAXIMUM_LENGTH(int CHARACTER_MAXIMUM_LENGTH) {
            this.CHARACTER_MAXIMUM_LENGTH = CHARACTER_MAXIMUM_LENGTH;
        }

        public String getIS_NULLABLE() {
            return IS_NULLABLE;
        }

        public void setIS_NULLABLE(String IS_NULLABLE) {
            this.IS_NULLABLE = IS_NULLABLE;
        }

        public String getCOLUMN_KEY() {
            return COLUMN_KEY;
        }

        public void setCOLUMN_KEY(String COLUMN_KEY) {
            this.COLUMN_KEY = COLUMN_KEY;
        }

        public String getEXTRA() {
            return EXTRA;
        }

        public void setEXTRA(String EXTRA) {
            this.EXTRA = EXTRA;
        }

    }
}
