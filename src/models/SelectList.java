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
import REVOpager.Database;
import REVOpager.EVOpagerDBconnection;
import REVOpager.Server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import showIt.ShowItForm;

/**
 *
 * @author Franco
 */
public class SelectList {

    EVOpagerParams myParams;
    Settings mySettings;
    private String oQuery;
    private String oLabelField;
    private String oValueField;
    private String oValueFieldType;
    private String oTitle;
    public ArrayList<SelectListLine> list;

    public SelectList(EVOpagerParams xParams, Settings xSettings, String oQuery, String oLabelField, String oValueField, String oValueFieldType) {
        this.oQuery = oQuery;
        this.oLabelField = oLabelField;
        this.oValueField = oValueField;
        this.oValueFieldType = oValueFieldType;
        this.list = new ArrayList<SelectListLine>();
        this.myParams = xParams;
        this.mySettings = xSettings;

    }

    public SelectList() {

    }

    public String getoTitle() {
        return oTitle;
    }

    public void setoTitle(String oTitle) {
        this.oTitle = oTitle;
    }

    public String getoQuery() {
        return oQuery;
    }

    public void setoQuery(String oQuery) {
        this.oQuery = oQuery;
    }

    public String getoLabelField() {
        return oLabelField;
    }

    public void setoLabelField(String oLabelField) {
        this.oLabelField = oLabelField;
    }

    public String getoValueField() {
        return oValueField;
    }

    public void setoValueField(String oValueField) {
        this.oValueField = oValueField;
    }

    public String getoValueFieldType() {
        return oValueFieldType;
    }

    public void setoValueFieldType(String oValueFieldType) {
        this.oValueFieldType = oValueFieldType;
    }

    public void castLine(SelectListLine line) {
        this.list.add(line);
    }

    /*
     List<String> matchList = new ArrayList<String>();
     Pattern regex = Pattern.compile("\\((.*?)\\)");
     Matcher regexMatcher = regex.matcher("Hello This is (Java) Not (.NET)");

     while (regexMatcher.find()) {//Finds Matching Pattern in String
     matchList.add(regexMatcher.group(1));//Fetching Group from String
     }

     for(String str:matchList) {
     System.out.println(str);
     }
     */
    public ArrayList<SelectListLine> getList() {

        // getList(Server myServer, Database myDatabase)
        try {

            // la lista può avere diversi campi nella riga descrittiva
            // this.oLabelField  li contiene così...
            //  field1:testoEtichetta1 ; field2:testoEtichetta2
            //oppure contiene solo il nome del field
//            System.out.println("PREPARANDO LA SELECTLIST da " + this.oLabelField);
            String[] couple = this.oLabelField.split(";");
            List<String> blocks = Arrays.asList(couple);
            ArrayList<SelectListLine> labels = new ArrayList<SelectListLine>();
            SelectListLine linea = new SelectListLine();

            // System.out.println("blocks.size() :"+blocks.size() );
            if (blocks.size() > 1) {
                for (int jj = 0; jj < blocks.size(); jj++) {
                    String[] descriptor = blocks.get(jj).split(":");
                    List<String> valori = Arrays.asList(descriptor);
                    linea = new SelectListLine();
                    if (valori.size() > 0) {

                        linea.setValue(valori.get(0).toString());
                        if (valori.size() > 1) {
                            linea.setLabel(valori.get(1).toString());
                            if (valori.size() > 2) {
                                int ckd = 0;
                                try {
                                    if (Integer.parseInt(valori.get(2).toString()) > 0) {
                                        linea.setChecked(1);
                                    }
                                } catch (Exception e) {
                                    ckd = 0;
                                }
                                linea.setChecked(ckd);
                            }
                        }
                    }
                    labels.add(linea);

                }

            } else {
                // System.out.println("HO TROVATO un solo argomento"  ); 
                linea = new SelectListLine();
                linea.setLabel("");// non c'è testo da vedere
                linea.setValue(this.oLabelField);
                labels.add(linea);
            }

            // System.out.println("HO TROVATO " + labels.size()+" ELEMENTI:");  
            String CurLabel = "";
            for (int jj = 0; jj < labels.size(); jj++) {
                if (CurLabel.length() > 0) {
                    CurLabel += " - ";
                }

                CurLabel += "";
                if (labels.get(jj).getLabel() != null && labels.get(jj).getLabel().length() > 0) {
                    CurLabel += labels.get(jj).getLabel() + ": ";
                }
                CurLabel += "[" + labels.get(jj).getValue() + "]";
            }
            // System.out.println("LABEL MATRIX: " + CurLabel);
            // Connection conny = myDBC.makeConnection(myServer, myDatabase);
            /*     String driver = myServer.getSQLdriver();
             String DBname = myDatabase.getDbExtendedName();
             String URL = myDatabase.getServer().getDefaultSQLserver();
             String alternativeURL = myServer.getAlternativeSQLserver();
             String USERNAME = myServer.getDATABASE_USER();
             String PASSWORD = myServer.getDATABASE_PW();
             EVOpagerDBconnection myDBC = new EVOpagerDBconnection(myParams, mySettings);
             Connection conny = myDBC.makeConnection(driver, URL, alternativeURL, DBname, USERNAME, PASSWORD);*/

            Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();

            // Cerco in FE_forms la mainTable di questo Form
            String SQLphrase = this.oQuery;

//            System.out.println("getList:" + SQLphrase);
//            System.out.println(" conny.isClosed():" + conny.isClosed());
//              System.out.println("L'etichetta sarà :" + CurLabel);
            ResultSet rs;
            try {
                PreparedStatement ps = null;
                ps = conny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();
                //  System.out.println("Il valore lo prendo dal campo :"+this.oValueField);
                while (rs.next()) {
                    SelectListLine myLine = new SelectListLine();

                    if (this.oValueFieldType != null && this.oValueFieldType.equalsIgnoreCase("INT")) {
                        myLine.setValue("" + rs.getInt(this.oValueField));
                    } else {
                        myLine.setValue(rs.getString(this.oValueField));
                    }

                    String curLabel = "";
                    for (int jj = 0; jj < labels.size(); jj++) {
                        if (curLabel.length() > 0) {
                            curLabel += " ";
                        }

                        curLabel += "";
                        if (labels.get(jj).getLabel() != null && labels.get(jj).getLabel().length() > 0) {
                            curLabel += labels.get(jj).getLabel() + ": ";
                        }
                        String valoreDB = rs.getString(labels.get(jj).getValue());
                        curLabel += valoreDB;
                    }
                    myLine.setLabel(curLabel);

                    myLine.setChecked(0);
//                    System.out.println("getList: aggiungo alla lista "+myLine.getLabel()+" = "+myLine.getValue() +" ("+ this.oValueFieldType+")");
                    this.list.add(myLine);
                }
            } catch (SQLException ex) {
                Logger.getLogger(SelectList.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("sql error in :" + SQLphrase);
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return list;
    }
//    Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();

    public ArrayList<SelectListLine> getLinkList(EVOpagerParams myParams, Settings mySettings, String KEYvalue, String KEYtype, String CGparams) {

        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        // Cerco in FE_forms la mainTable di questo Form
        String SQLphrase = this.oQuery;

        /*
         String whereClause="";
         String[] couple = this.oQuery.split("WHERE");
         List<String> couples = Arrays.asList(couple);
         if (couples.size()>1){
         SQLphrase=couples.get(0);
         whereClause=couples.get(1);
         }
         */
//        System.out.println("getLinkList - this.oQuery:" + this.oQuery);
//        System.out.println("getLinkList - this.oQuery:" + this.oLabelField);
//        System.out.println("getLinkList - this.oQuery:" + this.oValueField);
//        System.out.println("getLinkList - this.oQuery:" + this.oValueFieldType);
//
//        System.out.println("getLinkList - KEYvalue:" + KEYvalue + "  KEYtype:" + KEYtype);
//        System.out.println("getLinkList - CGparams:" + CGparams);

        String coda = "";
        String ordering = "";

        String linkQuery = "";
        String linkTable = "";
        String linkCoppia = "";
        String fieldUno = null;
        String fieldMolti = null;
        System.out.println("suddivido " + CGparams);
        String[] couple = CGparams.split(";");
        List<String> couples = Arrays.asList(couple);

        if (couples.size() > 2) {
            linkQuery = couples.get(0).toString();
            linkTable = couples.get(1).toString();
            linkCoppia = couples.get(2).toString().trim();
            couple = null;
            couples = null;
            couple = linkCoppia.split(":");
            couples = Arrays.asList(couple);
            fieldUno = couples.get(0).toString();
            fieldMolti = couples.get(1).toString();
//            System.out.println(
//                    "linkQuery=" + linkQuery
//                    + "  linkTable=" + linkTable
//                    + "  linkCoppia=" + linkCoppia
//                    + "  fieldUno=" + fieldUno
//                    + "  fieldMolti=" + fieldMolti);

            // in linkQuery devo separare eventuale opzione ORFDER BY e WHERE
            String[] alphaAll = linkQuery.split("ORDER BY");
            List<String> alpha = Arrays.asList(alphaAll);
            if (alpha.size() > 1) {
                ordering = " ORDER BY " + alpha.get(1);
                linkQuery = alpha.get(0);
            }

            String[] betaAll = linkQuery.split("WHERE");
            List<String> beta = Arrays.asList(alphaAll);
            if (beta.size() > 1) {
                coda = " AND (" + beta.get(1) + ")";
                linkQuery = beta.get(0);
            }

        } else {
            System.out.println("non posso suddividere in 3 parti ");
        }
//        System.out.println("SELECTLIST applica questa query:" + SQLphrase);

        ResultSet rs;
        try {
            Statement s = conny.createStatement();
            rs = s.executeQuery(SQLphrase);

            while (rs.next()) {
                SelectListLine myLine = new SelectListLine();
                myLine.setValue(rs.getString(this.oValueField));
                myLine.setLabel(rs.getString(this.oLabelField));
                myLine.setChecked(0);
                System.out.println("myLine.getValue():" + myLine.getValue());
                this.list.add(myLine);
            }

            // adesso devo parsare i link per vedere se c'è corrispondenza con il mio utente (con la mia KEY)
            // mi faccio una lista di link
            ArrayList<LinkUnoMolti> myLinkList = new ArrayList<LinkUnoMolti>();

            String tipo = "VAR";
            try {
                tipo = KEYtype.substring(0, 3);

            } catch (Exception ex) {
                tipo = "VAR";
            }
            if (tipo.equalsIgnoreCase("INT")) {
                SQLphrase = linkQuery + " WHERE " + fieldUno + " = " + KEYvalue + coda + ordering;
            } else {
                SQLphrase = linkQuery + " WHERE " + fieldUno + " = '" + KEYvalue + "'" + coda + ordering;
            }

//            System.out.println("QUERY DI RICERCA LINKS:" + SQLphrase);

            // nel db devo cercare i link che hanno nel fieldUNO il valore della KEY (li prendo tutti e DOPO li confronto con la lista dei valori per aggiornare il check
            rs = s.executeQuery(SQLphrase);

            while (rs.next()) {
                LinkUnoMolti myLink = new LinkUnoMolti();
                myLink.setIDuno(rs.getString(fieldUno));
                myLink.setIDmolti(rs.getString(fieldMolti));
                myLinkList.add(myLink);
            }

            // adesso guardo per ogni valore in lista se esiste un link e nel caso compilo il check
            for (int jj = 0; jj < this.list.size(); jj++) {
                //System.out.println("GRUPPO:" + this.list.get(jj).getValue());
                this.list.get(jj).setChecked(0);
                for (int lnk = 0; lnk < myLinkList.size(); lnk++) {

                    if (this.list.get(jj).getValue().equalsIgnoreCase(myLinkList.get(lnk).getIDmolti())) {
                        // trovata corrispèondenza
                        this.list.get(jj).setChecked(1);
                    }
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(SelectList.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(SelectList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public ArrayList<SelectListLine> getSpareList(EVOpagerParams myParams, Settings mySettings, String KEYvalue, String KEYtype, String CGparams, String ValueField) {

        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        // Cerco in FE_forms la mainTable di questo Form
        String SQLphrase = this.oQuery;

        /*
         String whereClause="";
         String[] couple = this.oQuery.split("WHERE");
         List<String> couples = Arrays.asList(couple);
         if (couples.size()>1){
         SQLphrase=couples.get(0);
         whereClause=couples.get(1);
         }
         */
//        System.out.println("getLinkList - this.oQuery:" + this.oQuery);
//        System.out.println("getLinkList - this.oLabelField:" + this.oLabelField);
//        System.out.println("getLinkList - this.oValueField:" + this.oValueField);
//        System.out.println("getLinkList - this.oValueFieldType:" + this.oValueFieldType);
//
//        System.out.println("getLinkList - KEYvalue:" + KEYvalue + "  KEYtype:" + KEYtype);
//        System.out.println("getLinkList - CGparams:" + CGparams);

        String coda = "";
        String ordering = "";

        String linkQuery = "";
        String linkTable = "";
        String linkCoppia = "";
        String filtro1 = null;
        String filtro2 = null;
        System.out.println("suddivido " + CGparams);
        String[] couple = CGparams.split(";");
        List<String> couples = Arrays.asList(couple);

        if (couples.size() > 2) {
            linkQuery = couples.get(0).toString();
            linkTable = couples.get(1).toString();
            linkCoppia = couples.get(2).toString().trim();
            couple = null;
            couples = null;
            couple = linkCoppia.split(":");
            couples = Arrays.asList(couple);
            filtro1 = couples.get(0).toString();
            filtro2 = couples.get(1).toString();
            System.out.println(
                    "\nlinkQuery=" + linkQuery
                    + "\n  linkTable=" + linkTable
                    + "\n  linkCoppia=" + linkCoppia
                    + "\n  fieldUno=" + filtro1
                    + "\n  fieldMolti=" + filtro2);

            // in linkQuery devo separare eventuale opzione ORFDER BY e WHERE
            String[] alphaAll = linkQuery.split("ORDER BY");
            List<String> alpha = Arrays.asList(alphaAll);
            if (alpha.size() > 1) {
                ordering = " ORDER BY " + alpha.get(1);
                linkQuery = alpha.get(0);
            }

            String[] betaAll = linkQuery.split("WHERE");
            List<String> beta = Arrays.asList(alphaAll);
            if (beta.size() > 1) {
                coda = " AND (" + beta.get(1) + ")";
                linkQuery = beta.get(0);
            }

        } else {
            System.out.println("non posso suddividere in 3 parti ");
        }
        System.out.println("SELECTLIST applica questa query:" + SQLphrase);

        ResultSet rs;
        try {
            Statement s = conny.createStatement();
            rs = s.executeQuery(SQLphrase);

            while (rs.next()) {
                SelectListLine myLine = new SelectListLine();
                myLine.setValue(rs.getString(this.oValueField)); //a.ID (a=archivio_macchine)
                myLine.setLabel(rs.getString(this.oLabelField)); //a.nomeMacchina
                myLine.setChecked(0);
                myLine.setSpareValue("");
                System.out.println("myLine.getValue():" + myLine.getValue() + "getLabel():" + myLine.getLabel());
                this.list.add(myLine);
            }

            // adesso devo parsare i valori della tabella SPARE (es tempi macchina)
            //per vedere se c'è corrispondenza con le mie chiavi: fase e nomeMacchina
            for (int jj = 0; jj < this.list.size(); jj++) {
                // per ogni elemento della lista cerco se esiste un valore in linkQuery
                // se c'è lo uso per compilare lo spareValue
                String tipo = "VAR";
                try {
                    tipo = KEYtype.substring(0, 3);

                } catch (Exception ex) {
                    tipo = "VAR";
                }
                if (tipo.equalsIgnoreCase("INT")) {
                    SQLphrase = linkQuery + " WHERE " + filtro1 + " = " + KEYvalue + "  AND " + filtro2 + " = '" + this.list.get(jj).getValue() + "'" + coda + ordering;
                } else {
                    SQLphrase = linkQuery + " WHERE " + filtro1 + " = '" + KEYvalue + "' AND " + filtro2 + " = '" + this.list.get(jj).getValue() + "'" + coda + ordering;
                }

                rs = s.executeQuery(SQLphrase);
                this.list.get(jj).setSpareValue("");
                while (rs.next()) {
                    this.list.get(jj).setSpareValue(rs.getString(ValueField));
                    break;
                }

            }

            // mi faccio una lista di link
            ArrayList<LinkUnoMolti> myLinkList = new ArrayList<LinkUnoMolti>();

            String tipo = "VAR";
            try {
                tipo = KEYtype.substring(0, 3);

            } catch (Exception ex) {
                tipo = "VAR";
            }
            if (tipo.equalsIgnoreCase("INT")) {
                SQLphrase = linkQuery + " WHERE " + filtro1 + " = " + KEYvalue + coda + ordering;
            } else {
                SQLphrase = linkQuery + " WHERE " + filtro1 + " = '" + KEYvalue + "'" + coda + ordering;
            }

            System.out.println("QUERY DI RICERCA VALORI:" + SQLphrase);

            // nel db devo cercare i link che hanno nel fieldUNO il valore della KEY (li prendo tutti e DOPO li confronto con la lista dei valori per aggiornare il check
            rs = s.executeQuery(SQLphrase);

            while (rs.next()) {
                LinkUnoMolti myLink = new LinkUnoMolti();
                myLink.setIDuno(rs.getString(filtro1));  // es "rifFase"
                myLink.setIDmolti(rs.getString(filtro2)); // es "rifMacchina"
                myLinkList.add(myLink);
            }

            // adesso guardo per ogni valore in lista se esiste un link e nel caso compilo il check
            for (int jj = 0; jj < this.list.size(); jj++) {
                //System.out.println("GRUPPO:" + this.list.get(jj).getValue());
                this.list.get(jj).setChecked(0);
                for (int lnk = 0; lnk < myLinkList.size(); lnk++) {

                    if (this.list.get(jj).getValue().equalsIgnoreCase(myLinkList.get(lnk).getIDmolti())) {
                        // trovata corrispèondenza
                        this.list.get(jj).setChecked(1);
                    }
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(SelectList.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public ArrayList<SelectListLine> getLinkList(Server myServer, Database myDatabase, String KEYvalue, String KEYtype, String CGparams) {

        String driver = myServer.getSQLdriver();
        String DBname = myDatabase.getDbExtendedName();
        String URL = myDatabase.getServer().getDefaultSQLserver();
        String alternativeURL = myServer.getAlternativeSQLserver();
        String USERNAME = myServer.getDATABASE_USER();
        String PASSWORD = myServer.getDATABASE_PW();
        EVOpagerDBconnection myDBC = new EVOpagerDBconnection();
        Connection conny = myDBC.makeConnection(driver, URL, alternativeURL, DBname, USERNAME, PASSWORD);

        // Cerco in FE_forms la mainTable di questo Form
        String SQLphrase = this.oQuery;

        /*
         String whereClause="";
         String[] couple = this.oQuery.split("WHERE");
         List<String> couples = Arrays.asList(couple);
         if (couples.size()>1){
         SQLphrase=couples.get(0);
         whereClause=couples.get(1);
         }
         */
        System.out.println("getLinkList - this.oQuery:" + this.oQuery);
        System.out.println("getLinkList - this.oQuery:" + this.oLabelField);
        System.out.println("getLinkList - this.oQuery:" + this.oValueField);
        System.out.println("getLinkList - this.oQuery:" + this.oValueFieldType);

        System.out.println("getLinkList - KEYvalue:" + KEYvalue + "  KEYtype:" + KEYtype);
        System.out.println("getLinkList - CGparams:" + CGparams);

        String coda = "";
        String ordering = "";

        String linkQuery = "";
        String linkTable = "";
        String linkCoppia = "";
        String fieldUno = null;
        String fieldMolti = null;
        System.out.println("suddivido " + CGparams);
        String[] couple = CGparams.split(";");
        List<String> couples = Arrays.asList(couple);

        if (couples.size() > 2) {
            linkQuery = couples.get(0).toString();
            linkTable = couples.get(1).toString();
            linkCoppia = couples.get(2).toString().trim();
            couple = null;
            couples = null;
            couple = linkCoppia.split(":");
            couples = Arrays.asList(couple);
            fieldUno = couples.get(0).toString();
            fieldMolti = couples.get(1).toString();
            System.out.println(
                    "linkQuery=" + linkQuery
                    + "  linkTable=" + linkTable
                    + "  linkCoppia=" + linkCoppia
                    + "  fieldUno=" + fieldUno
                    + "  fieldMolti=" + fieldMolti);

            // in linkQuery devo separare eventuale opzione ORFDER BY e WHERE
            String[] alphaAll = linkQuery.split("ORDER BY");
            List<String> alpha = Arrays.asList(alphaAll);
            if (alpha.size() > 1) {
                ordering = " ORDER BY " + alpha.get(1);
                linkQuery = alpha.get(0);
            }

            String[] betaAll = linkQuery.split("WHERE");
            List<String> beta = Arrays.asList(alphaAll);
            if (beta.size() > 1) {
                coda = " AND (" + beta.get(1) + ")";
                linkQuery = beta.get(0);
            }

        } else {
            System.out.println("non posso suddividere in 3 parti ");
        }
        System.out.println("SELECTLIST applica questa query:" + SQLphrase);

        ResultSet rs;
        try {
            Statement s = conny.createStatement();
            rs = s.executeQuery(SQLphrase);

            while (rs.next()) {
                SelectListLine myLine = new SelectListLine();
                myLine.setValue(rs.getString(this.oValueField));
                myLine.setLabel(rs.getString(this.oLabelField));
                myLine.setChecked(0);
                System.out.println("myLine.getValue():" + myLine.getValue());
                this.list.add(myLine);
            }

            // adesso devo parsare i link per vedere se c'è corrispondenza con il mio utente (con la mia KEY)
            // mi faccio una lista di link
            ArrayList<LinkUnoMolti> myLinkList = new ArrayList<LinkUnoMolti>();

            String tipo = "VAR";
            try {
                tipo = KEYtype.substring(0, 3);

            } catch (Exception ex) {
                tipo = "VAR";
            }
            if (tipo.equalsIgnoreCase("INT")) {
                SQLphrase = linkQuery + " WHERE " + fieldUno + " = " + KEYvalue + coda + ordering;
            } else {
                SQLphrase = linkQuery + " WHERE " + fieldUno + " = '" + KEYvalue + "'" + coda + ordering;
            }

            System.out.println("QUERY DI RICERCA LINKS:" + SQLphrase);

            // nel db devo cercare i link che hanno nel fieldUNO il valore della KEY (li prendo tutti e DOPO li confronto con la lista dei valori per aggiornare il check
            rs = s.executeQuery(SQLphrase);

            while (rs.next()) {
                LinkUnoMolti myLink = new LinkUnoMolti();
                myLink.setIDuno(rs.getString(fieldUno));
                myLink.setIDmolti(rs.getString(fieldMolti));
                myLinkList.add(myLink);
            }

            // adesso guardo per ogni valore in lista se esiste un link e nel caso compilo il check
            for (int jj = 0; jj < this.list.size(); jj++) {
                //System.out.println("GRUPPO:" + this.list.get(jj).getValue());
                this.list.get(jj).setChecked(0);
                for (int lnk = 0; lnk < myLinkList.size(); lnk++) {

                    if (this.list.get(jj).getValue().equalsIgnoreCase(myLinkList.get(lnk).getIDmolti())) {
                        // trovata corrispèondenza
                        this.list.get(jj).setChecked(1);
                    }
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(SelectList.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public ArrayList<SelectListLine> getSpareList(Server myServer, Database myDatabase, String KEYvalue, String KEYtype, String CGparams, String ValueField) {

        String driver = myServer.getSQLdriver();
        String DBname = myDatabase.getDbExtendedName();
        String URL = myDatabase.getServer().getDefaultSQLserver();
        String alternativeURL = myServer.getAlternativeSQLserver();
        String USERNAME = myServer.getDATABASE_USER();
        String PASSWORD = myServer.getDATABASE_PW();
        EVOpagerDBconnection myDBC = new EVOpagerDBconnection();
        Connection conny = myDBC.makeConnection(driver, URL, alternativeURL, DBname, USERNAME, PASSWORD);

        // Cerco in FE_forms la mainTable di questo Form
        String SQLphrase = this.oQuery;

        /*
         String whereClause="";
         String[] couple = this.oQuery.split("WHERE");
         List<String> couples = Arrays.asList(couple);
         if (couples.size()>1){
         SQLphrase=couples.get(0);
         whereClause=couples.get(1);
         }
         */
        System.out.println("getLinkList - this.oQuery:" + this.oQuery);
        System.out.println("getLinkList - this.oLabelField:" + this.oLabelField);
        System.out.println("getLinkList - this.oValueField:" + this.oValueField);
        System.out.println("getLinkList - this.oValueFieldType:" + this.oValueFieldType);

        System.out.println("getLinkList - KEYvalue:" + KEYvalue + "  KEYtype:" + KEYtype);
        System.out.println("getLinkList - CGparams:" + CGparams);

        String coda = "";
        String ordering = "";

        String linkQuery = "";
        String linkTable = "";
        String linkCoppia = "";
        String filtro1 = null;
        String filtro2 = null;
        System.out.println("suddivido " + CGparams);
        String[] couple = CGparams.split(";");
        List<String> couples = Arrays.asList(couple);

        if (couples.size() > 2) {
            linkQuery = couples.get(0).toString();
            linkTable = couples.get(1).toString();
            linkCoppia = couples.get(2).toString().trim();
            couple = null;
            couples = null;
            couple = linkCoppia.split(":");
            couples = Arrays.asList(couple);
            filtro1 = couples.get(0).toString();
            filtro2 = couples.get(1).toString();
            System.out.println(
                    "\nlinkQuery=" + linkQuery
                    + "\n  linkTable=" + linkTable
                    + "\n  linkCoppia=" + linkCoppia
                    + "\n  fieldUno=" + filtro1
                    + "\n  fieldMolti=" + filtro2);

            // in linkQuery devo separare eventuale opzione ORFDER BY e WHERE
            String[] alphaAll = linkQuery.split("ORDER BY");
            List<String> alpha = Arrays.asList(alphaAll);
            if (alpha.size() > 1) {
                ordering = " ORDER BY " + alpha.get(1);
                linkQuery = alpha.get(0);
            }

            String[] betaAll = linkQuery.split("WHERE");
            List<String> beta = Arrays.asList(alphaAll);
            if (beta.size() > 1) {
                coda = " AND (" + beta.get(1) + ")";
                linkQuery = beta.get(0);
            }

        } else {
            System.out.println("non posso suddividere in 3 parti ");
        }
        System.out.println("SELECTLIST applica questa query:" + SQLphrase);

        ResultSet rs;
        try {
            Statement s = conny.createStatement();
            rs = s.executeQuery(SQLphrase);

            while (rs.next()) {
                SelectListLine myLine = new SelectListLine();
                myLine.setValue(rs.getString(this.oValueField)); //a.ID (a=archivio_macchine)
                myLine.setLabel(rs.getString(this.oLabelField)); //a.nomeMacchina
                myLine.setChecked(0);
                myLine.setSpareValue("");
                System.out.println("myLine.getValue():" + myLine.getValue() + "getLabel():" + myLine.getLabel());
                this.list.add(myLine);
            }

            // adesso devo parsare i valori della tabella SPARE (es tempi macchina)
            //per vedere se c'è corrispondenza con le mie chiavi: fase e nomeMacchina
            for (int jj = 0; jj < this.list.size(); jj++) {
                // per ogni elemento della lista cerco se esiste un valore in linkQuery
                // se c'è lo uso per compilare lo spareValue
                String tipo = "VAR";
                try {
                    tipo = KEYtype.substring(0, 3);

                } catch (Exception ex) {
                    tipo = "VAR";
                }
                if (tipo.equalsIgnoreCase("INT")) {
                    SQLphrase = linkQuery + " WHERE " + filtro1 + " = " + KEYvalue + "  AND " + filtro2 + " = '" + this.list.get(jj).getValue() + "'" + coda + ordering;
                } else {
                    SQLphrase = linkQuery + " WHERE " + filtro1 + " = '" + KEYvalue + "' AND " + filtro2 + " = '" + this.list.get(jj).getValue() + "'" + coda + ordering;
                }

                rs = s.executeQuery(SQLphrase);
                this.list.get(jj).setSpareValue("");
                while (rs.next()) {
                    this.list.get(jj).setSpareValue(rs.getString(ValueField));
                    break;
                }

            }

            // mi faccio una lista di link
            ArrayList<LinkUnoMolti> myLinkList = new ArrayList<LinkUnoMolti>();

            String tipo = "VAR";
            try {
                tipo = KEYtype.substring(0, 3);

            } catch (Exception ex) {
                tipo = "VAR";
            }
            if (tipo.equalsIgnoreCase("INT")) {
                SQLphrase = linkQuery + " WHERE " + filtro1 + " = " + KEYvalue + coda + ordering;
            } else {
                SQLphrase = linkQuery + " WHERE " + filtro1 + " = '" + KEYvalue + "'" + coda + ordering;
            }

            System.out.println("QUERY DI RICERCA VALORI:" + SQLphrase);

            // nel db devo cercare i link che hanno nel fieldUNO il valore della KEY (li prendo tutti e DOPO li confronto con la lista dei valori per aggiornare il check
            rs = s.executeQuery(SQLphrase);

            while (rs.next()) {
                LinkUnoMolti myLink = new LinkUnoMolti();
                myLink.setIDuno(rs.getString(filtro1));  // es "rifFase"
                myLink.setIDmolti(rs.getString(filtro2)); // es "rifMacchina"
                myLinkList.add(myLink);
            }

            // adesso guardo per ogni valore in lista se esiste un link e nel caso compilo il check
            for (int jj = 0; jj < this.list.size(); jj++) {
                //System.out.println("GRUPPO:" + this.list.get(jj).getValue());
                this.list.get(jj).setChecked(0);
                for (int lnk = 0; lnk < myLinkList.size(); lnk++) {

                    if (this.list.get(jj).getValue().equalsIgnoreCase(myLinkList.get(lnk).getIDmolti())) {
                        // trovata corrispèondenza
                        this.list.get(jj).setChecked(1);
                    }
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(SelectList.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

}
