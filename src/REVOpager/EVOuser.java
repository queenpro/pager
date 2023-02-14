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
package REVOpager;

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import models.SelectListLine;
import models.logEvent;
import static showIt.eventManager.encodeURIComponent;
import models.objectLayout;
import showIt.ShowItForm;
import static showIt.ShowItForm.makeRoundedCorner;

/**
 *
 * @author Franco
 */
public class EVOuser {

    private String userID;
    private String username;
    private String name;
    private String surname;
    private String prefix;
    private String birthDate;
    private String cellphone;
    private String email;
    private ArrayList<String> groups;
    private ArrayList<String> reparti;
    private EVOpagerParams myParams;

    private String TABLElinkUserGroups;
    private String FIELDlinkUserGroupsRifOperatore;
    private String FIELDlinkUserGroupsRifGruppo;

    private String TABLEgruppi;
    private String FIELDGruppiIDgruppo;

    private String TABLEoperatori;
    private String FIELDoperatoriID;

    private String TABLEtokens;
    private String FIELDusernameField;
    private String FIELDnameField;
    private String FIELDsurnameField;

    private String FIELDpictureField;
    Settings mySettings;

    public EVOuser(EVOpagerParams xmyParams, Settings xsettings) {
        this.mySettings = xsettings;
        // System.out.println("EVOuser... settings:");
        //  mySettings.printSettings("EVO USER");

        this.myParams = xmyParams;
        this.groups = new ArrayList<String>();
        this.reparti = new ArrayList<String>();

        this.TABLElinkUserGroups = mySettings.getAccount_TABLElinkUserGroups();
        this.FIELDlinkUserGroupsRifOperatore = mySettings.getAccount_FIELDlinkUserGroupsRifOperatore();// "rifOperatore";
        this.FIELDlinkUserGroupsRifGruppo = mySettings.getAccount_FIELDlinkUserGroupsRifGruppo();//"rifGruppo";
        this.TABLEgruppi = mySettings.getAccount_TABLEgruppi(); //"archivio_operatoriGruppi";
        this.FIELDGruppiIDgruppo = mySettings.getAccount_FIELDGruppiIDgruppo();   // "IDgruppo";
        this.TABLEoperatori = mySettings.getAccount_TABLEoperatori();  //"archivio_operatori";
        this.FIELDoperatoriID = mySettings.getAccount_FIELDoperatoriID(); //"ID";
        this.TABLEtokens = mySettings.getAccount_TABLEtokens();//"archivio_operatoriTokens";
        this.FIELDpictureField = mySettings.getAccount_FIELDpictureField();//"picture";
        this.FIELDnameField = mySettings.getAccount_FIELDnameField();//"name";
        this.FIELDsurnameField = mySettings.getAccount_FIELDsurnameField();// "surname";
        this.FIELDusernameField = mySettings.getAccount_FIELDusernameField(); //"username";
        /*
         System.out.println("TABLElinkUserGroups             " + TABLElinkUserGroups);
         System.out.println("FIELDlinkUserGroupsRifOperatore " + FIELDlinkUserGroupsRifOperatore);
         System.out.println("FIELDlinkUserGroupsRifGruppo    " + FIELDlinkUserGroupsRifGruppo);
         System.out.println("TABLEgruppi                     " + TABLEgruppi);
         System.out.println("FIELDGruppiIDgruppo             " + FIELDGruppiIDgruppo);
         System.out.println("TABLEoperatori                  " + TABLEoperatori);
         System.out.println("FIELDoperatoriID                " + FIELDoperatoriID);
         System.out.println("TABLEtokens                     " + TABLEtokens);
         System.out.println("FIELDpictureField               " + FIELDpictureField);
         System.out.println("FIELDnameField                  " + FIELDnameField);
         System.out.println("FIELDsurnameField               " + FIELDsurnameField);
         System.out.println("FIELDusernameField              " + FIELDusernameField);
         */
    }

    public String getTABLElinkUserGroups() {
        return TABLElinkUserGroups;
    }

    public void setTABLElinkUserGroups(String TABLElinkUserGroups) {
        this.TABLElinkUserGroups = TABLElinkUserGroups;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTABLEgruppi() {
        return TABLEgruppi;
    }

    public void setTABLEgruppi(String TABLEgruppi) {
        this.TABLEgruppi = TABLEgruppi;
    }

    public String getTABLEoperatori() {
        return TABLEoperatori;
    }

    public void setTABLEoperatori(String TABLEoperatori) {
        this.TABLEoperatori = TABLEoperatori;
    }

    public String getTABLEtokens() {
        return TABLEtokens;
    }

    // <editor-fold defaultstate="collapsed" desc="GETTERS & SETTERS">
    public void setTABLEtokens(String TABLEtokens) {
        this.TABLEtokens = TABLEtokens;
    }

    public String getFIELDlinkUserGroupsRifOperatore() {
        return FIELDlinkUserGroupsRifOperatore;
    }

    public void setFIELDlinkUserGroupsRifOperatore(String FIELDlinkUserGroupsRifOperatore) {
        this.FIELDlinkUserGroupsRifOperatore = FIELDlinkUserGroupsRifOperatore;
    }

    public String getFIELDlinkUserGroupsRifGruppo() {
        return FIELDlinkUserGroupsRifGruppo;
    }

    public void setFIELDlinkUserGroupsRifGruppo(String FIELDlinkUserGroupsRifGruppo) {
        this.FIELDlinkUserGroupsRifGruppo = FIELDlinkUserGroupsRifGruppo;
    }

    public String getFIELDGruppiIDgruppo() {
        return FIELDGruppiIDgruppo;
    }

    public void setFIELDGruppiIDgruppo(String FIELDGruppiIDgruppo) {
        this.FIELDGruppiIDgruppo = FIELDGruppiIDgruppo;
    }

    public String getFIELDoperatoriID() {
        return FIELDoperatoriID;
    }

    public void setFIELDoperatoriID(String FIELDoperatoriID) {
        this.FIELDoperatoriID = FIELDoperatoriID;
    }

    public String getFIELDusernameField() {
        return FIELDusernameField;
    }

    public void setFIELDusernameField(String FIELDusernameField) {
        this.FIELDusernameField = FIELDusernameField;
    }

    public String getFIELDnameField() {
        return FIELDnameField;
    }

    public void setFIELDnameField(String FIELDnameField) {
        this.FIELDnameField = FIELDnameField;
    }

    public String getFIELDsurnameField() {
        return FIELDsurnameField;
    }

    public void setFIELDsurnameField(String FIELDsurnameField) {
        this.FIELDsurnameField = FIELDsurnameField;
    }

    public String getFIELDpictureField() {
        return FIELDpictureField;
    }

    public void setFIELDpictureField(String FIELDpictureField) {
        this.FIELDpictureField = FIELDpictureField;
    }

    public EVOpagerParams getMyParams() {
        return myParams;
    }

    public void setMyParams(EVOpagerParams myParams) {
        this.myParams = myParams;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getNameShowed() {
        String nameShowed = this.getName() + " " + this.getSurname();
        if ((this.getName() == null || this.getName().equalsIgnoreCase("null") || this.getName().length() < 1)
                && (this.getSurname() == null || this.getSurname().equalsIgnoreCase("null") || this.getSurname().length() < 1)) {
            nameShowed = this.getUsername();

        }
        if ((this.getUsername() == null || this.getUsername().equalsIgnoreCase("null") || this.getUsername().length() < 1)) {
            nameShowed = "";
        }
        if ((this.getSurname() == null || this.getSurname().equalsIgnoreCase("null") || this.getSurname().length() < 1)) {
            nameShowed = "";
        }
        if ((this.getName() == null || this.getName().equalsIgnoreCase("null") || this.getName().length() < 1)) {
            nameShowed = "";
        }
        return nameShowed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public ArrayList<String> getReparti() {
        return reparti;
    }

    public void setReparti(ArrayList<String> reparti) {
        this.reparti = reparti;
    }

    // </editor-fold> 
    //--------------------------------------
    // <editor-fold defaultstate="collapsed" desc="getActualRight a ciclo singlo">
    public int getActualRight(String formRights, String ownerID) {
        //  System.out.println("---getActualRight riceve formRoghts: " + formRights);

        int right = 0;
        try {
            /*
             0. Accesso negato
             1. Lettura
             2. Creazione
             3. Modifica
             4. Eliminazione
             5. Comnpleta
             */

            Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
            right = getActualRight(formRights, ownerID, accountConny);
            accountConny.close();
        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("---ERRORE getActualRight 298: " + ex.toString());
        }

        return right;

    }

    public int getActualRight(String formRights, String ownerID, Connection accountConny) {
        //  System.out.println("---getActualRight riceve formRights: " + formRights);

        if (formRights == null) {
            formRights = "DEFAULT:3;";
        }

        int right = -1;
        int startRight = -1;

        this.groups = new ArrayList<String>();

        if (TABLElinkUserGroups.equalsIgnoreCase("archivio_operatoriLinkGruppi")) {
            try {
                /*
             0. Accesso negato
             1. Lettura
             2. Creazione
             3. Modifica
             4. Eliminazione
             5. Comnpleta
                 */

                String SQLphrase = "SELECT * FROM `" + TABLElinkUserGroups + "` WHERE `"
                        + FIELDlinkUserGroupsRifOperatore + "` = '" + myParams.getCKuserID() + "'";
//            System.out.println(SQLphrase);
                Statement s = accountConny.createStatement();
                ResultSet rs = s.executeQuery(SQLphrase);

//              System.out.println("\n---ELENCO COMPLETO DI GRUPPI A CUI APARTIENE " + myParams.getCKuserID());
                while (rs.next()) {
                    String grp = "";
                    grp = rs.getString(FIELDlinkUserGroupsRifGruppo);
                    if (grp != null) {
                        groups.add(grp);
                        //System.out.println("---GRUPPO: " + grp);
                        if (grp.equalsIgnoreCase("superAdmin")) {
                            startRight = 5;
                        }
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("---ERRORE ");
            }
            // System.out.println("FINE ELENCO."  );

        } else {/*
  account_TABLElinkUserGroups = "correlazioni_ruoli;
        account_FIELDlinkUserGroupsRifOperatore = "partAvalue";
        account_FIELDlinkUserGroupsRifGruppo = "partBvalue";
        
        
        account_TABLEgruppi = "T_ruoli";
        account_FIELDGruppiIDgruppo = "ID";

        account_TABLEoperatori = "archivio_operatori";
        account_FIELDoperatoriID = "ID";
             */
            try {
                String SQLphrase = "SELECT * FROM " + TABLElinkUserGroups + " "
                        + "WHERE  "
                        + " partAtab = '" + TABLEoperatori + "' AND "
                        + " partBtab = '" + TABLEgruppi + "' AND "
                        + " " + FIELDlinkUserGroupsRifOperatore + " = '" + myParams.getCKuserID() + "'";
//            System.out.println(SQLphrase);
                Statement s = accountConny.createStatement();
                ResultSet rs = s.executeQuery(SQLphrase);

//              System.out.println("\n---ELENCO COMPLETO DI GRUPPI A CUI APARTIENE " + myParams.getCKuserID());
                while (rs.next()) {
                    String grp = "";
                    grp = rs.getString(FIELDlinkUserGroupsRifGruppo);
                    if (grp != null) {
                        groups.add(grp);
                        //System.out.println("---GRUPPO: " + grp);
                        if (grp.equalsIgnoreCase("superAdmin")) {
                            startRight = 5;
                        }
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("---ERRORE ");
            }
        }

        String lastSentence = "";

        String[] items = formRights.split(";");
        List<String> roules = Arrays.asList(items);

        for (int jj = 0; jj < roules.size(); jj++) {
            //System.out.println("---ANALIZZO " + roules.get(jj));
            if (roules.get(jj) != null && roules.get(jj).length() > 3) {

                String[] part = roules.get(jj).split(":");
                List<String> parts = Arrays.asList(part);
                if (parts.size() > 1) {
                    String gruppo = parts.get(0).toString();

                    String rgt = parts.get(1).toString();
                    int thisRight = 0;
                    try {
                        thisRight = Integer.parseInt(rgt);
                    } catch (Exception e) {
                        thisRight = 0;
                    }
                    //System.out.println("---VALUTO GRUPPO " + gruppo + " CON DIRITTO = " + thisRight);

                    if (gruppo.equalsIgnoreCase("DEFAULT") && right < thisRight) {
                        right = thisRight;

                    } else {
                        for (int kk = 0; kk < groups.size(); kk++) {
                            if (groups.get(kk).equalsIgnoreCase(gruppo)) {
                                if (right < thisRight) {
                                    right = thisRight;
                                }
                                lastSentence = ("---GRUPPO " + gruppo + " concede diritto " + right);
                                break;
                            }
                        }
                    }
                }
            }
        }

        // System.out.println(lastSentence);
        if (right < startRight) {
            right = startRight;
        }
        lastSentence = ("---CONCESSO INFINE DIRITTO " + right);

        return right;

    }

    public int getActualRightAdvanced(String formRights, String ownerID) {
        int right = 0;

        try {
            Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
            right = getActualRightAdvanced(formRights, ownerID, accountConny);
            accountConny.close();
        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("---ERRORE ");
        }
        return right;
    }
    // </editor-fold> 

    public int getActualRightAdvanced(String formRights, String ownerID, Connection accountConny) {
        //  System.out.println("---getActualRightAdvanced riceve formRoghts: " + formRights);

        if (formRights == null) {
            formRights = "DEFAULT:3;";
        }

        int right = -1;
        int startRight = -1;

        this.groups = new ArrayList<String>();
        try {
            /*
             0. Accesso negato
             1. Lettura
             2. Creazione
             3. Modifica
             4. Eliminazione
             5. Comnpleta
             */
            //  Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
            String SQLphrase = "SELECT * FROM `" + TABLElinkUserGroups + "` WHERE `"
                    + FIELDlinkUserGroupsRifOperatore + "` = '" + myParams.getCKuserID() + "' "
                    + "AND partAtab = '" + TABLEoperatori + "' AND partBtab = '" + TABLEgruppi + "' "
                    + "AND partAvalueField = '" + FIELDoperatoriID + "' and partBvalueField = '" + FIELDGruppiIDgruppo + "'";
//            System.out.println("getActualRightAdvanced-->"+SQLphrase);
            Statement s = accountConny.createStatement();
            ResultSet rs = s.executeQuery(SQLphrase);
//            System.out.println("\n---ELENCO COMPLETO DI GRUPPI A CUI APARTIENE " + myParams.getCKuserID());
            while (rs.next()) {
                String grp = "";
                grp = rs.getString(FIELDlinkUserGroupsRifGruppo);
                if (grp != null) {
                    groups.add(grp);
//                    System.out.println("---GRUPPO: " + grp);
                    if (grp.equalsIgnoreCase("superAdmin")) {
                        startRight = 5;
                    }
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("---ERRORE ");
        }
        // System.out.println("FINE ELENCO."  );

        String lastSentence = "";

        String[] items = formRights.split(";");
        List<String> roules = Arrays.asList(items);
        for (int jj = 0; jj < roules.size(); jj++) {
//            System.out.println("---ANALIZZO " + roules.get(jj));
            if (roules.get(jj) != null && roules.get(jj).length() > 3) {
                String[] part = roules.get(jj).split(":");
                List<String> parts = Arrays.asList(part);
                if (parts.size() > 1) {
                    String gruppo = parts.get(0).toString();
                    String rgt = parts.get(1).toString();
                    int thisRight = 0;
                    try {
                        thisRight = Integer.parseInt(rgt);
                    } catch (Exception e) {
                        thisRight = 0;
                    }
//                    System.out.println("---VALUTO GRUPPO " + gruppo + " CON DIRITTO = " + thisRight);
                    if (gruppo.equalsIgnoreCase("DEFAULT") && right < thisRight) {
                        right = thisRight;
                    } else {
                        for (int kk = 0; kk < groups.size(); kk++) {
                            if (groups.get(kk).equalsIgnoreCase(gruppo)) {
                                if (right < thisRight) {
                                    right = thisRight;
                                }
                                lastSentence = ("---GRUPPO " + gruppo + " concede diritto " + right);
                                break;
                            }
                        }
                    }
                }
            }
        }

//         System.out.println(lastSentence);
        if (right < startRight) {
            right = startRight;
        }
        lastSentence = ("---CONCESSO INFINE DIRITTO " + right);
//System.out.println(lastSentence);
        return right;

    }
// </editor-fold> 
    //--------------------------------------

    public int getStoredRango(String user) {
        int rango = 0;
        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
        String SQLphrase;
        PreparedStatement ps;

        try {
            SQLphrase = "SELECT * FROM `archivio_operatori` WHERE ID = '" + user + "'";
//            System.out.println("SQLphrase :" + SQLphrase);
            ps = accountConny.prepareStatement(SQLphrase);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rango = rs.getInt("rango");
                break;
            }

        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            accountConny.close();
        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rango;
    }

    public int getActualRango(String user, String TabU, String FieldU, String TabG, String FieldG, String TabLink, String FieldLinkU, String FieldLinkG) {
        String xTABLEgruppi = TabG;//T_ruoli
        String xTABLElinkUserGroups = TabLink;// correlazioni_ruoli
        String xFIELDGruppiIDgruppo = FieldG;// ID es. in T_ruoli
        String xFIELDlinkUserGroupsRifGruppo = FieldLinkG;// partAvalue
        String xFIELDlinkUserGroupsRifOperatore = FieldLinkU;// partBvalue

        if (user == null) {
            return 0;
        }
        int rango = 0;
        String gruppo = "";

        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

        if (xTABLElinkUserGroups.equalsIgnoreCase("archivio_operatoriLinkGruppi")) {
            try {
                String SQLphrase = "SELECT * FROM `" + xTABLElinkUserGroups + "` "
                        + " LEFT JOIN " + xTABLEgruppi + " ON " + xTABLEgruppi + "." + xFIELDGruppiIDgruppo
                        + " = " + xTABLElinkUserGroups + "." + xFIELDlinkUserGroupsRifGruppo + ""
                        + " WHERE `" + xFIELDlinkUserGroupsRifOperatore + "` = '" + user + "'";

                // System.out.println("ActualRango SQLphrase: " + SQLphrase);
                Statement s;

                s = accountConny.createStatement();
                ResultSet rs = s.executeQuery(SQLphrase);
                // System.out.println("ELENCO COMPLETO DI GRUPPI A CUI APARTIENE "+myParams.getCKuserID() );
                while (rs.next()) {
                    int xRango = rs.getInt("rango");
                    if (xRango > rango) {
                        gruppo = rs.getString(xFIELDlinkUserGroupsRifGruppo);
                        rango = xRango;
                    }
                }
                // System.out.println("GRUPPO: "+gruppo +" - Rango = "+rango);

                accountConny.close();
            } catch (SQLException ex) {
                Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                String SQLphrase = "SELECT "
                        + xTABLEgruppi + ".rango, "
                        + xTABLElinkUserGroups + "." + xFIELDlinkUserGroupsRifGruppo + " "
                        + "FROM " + xTABLElinkUserGroups + " "
                        + "LEFT JOIN " + xTABLEgruppi + " ON "
                        + xTABLEgruppi + "." + xFIELDGruppiIDgruppo + " = "
                        + xTABLElinkUserGroups + "." + xFIELDlinkUserGroupsRifGruppo + " "
                        + "WHERE " + xFIELDlinkUserGroupsRifOperatore + " = '" + user + "' "
                        + "ORDER BY rango DESC LIMIT 1 ";

//                System.out.println("ActualRango SQLphrase: " + SQLphrase);
                Statement s;

                s = accountConny.createStatement();
                ResultSet rs = s.executeQuery(SQLphrase);
                // System.out.println("ELENCO COMPLETO DI GRUPPI A CUI APARTIENE "+myParams.getCKuserID() );
                while (rs.next()) {
                    int xRango = rs.getInt("rango");
                    if (xRango > rango) {
                        gruppo = rs.getString(xFIELDlinkUserGroupsRifGruppo);
                        rango = xRango;
                    }
                }
//                System.out.println("GRUPPO: " + gruppo + " - Rango = " + rango);

                accountConny.close();
            } catch (SQLException ex) {
                Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //System.out.println("rango: " + rango);
        return rango;
    }

    public int getActualRango(String user) {
        if (user == null) {
            return 0;
        }
        int rango = 0;
        String gruppo = "";

        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

        if (TABLElinkUserGroups.equalsIgnoreCase("archivio_operatoriLinkGruppi")) {
            try {
                String SQLphrase = "SELECT * FROM `" + TABLElinkUserGroups + "` "
                        + " LEFT JOIN " + TABLEgruppi + " ON " + TABLEgruppi + "." + FIELDGruppiIDgruppo
                        + " = " + TABLElinkUserGroups + "." + FIELDlinkUserGroupsRifGruppo + ""
                        + " WHERE `" + FIELDlinkUserGroupsRifOperatore + "` = '" + user + "'";

                // System.out.println("ActualRango SQLphrase: " + SQLphrase);
                Statement s;

                s = accountConny.createStatement();
                ResultSet rs = s.executeQuery(SQLphrase);
                // System.out.println("ELENCO COMPLETO DI GRUPPI A CUI APARTIENE "+myParams.getCKuserID() );
                while (rs.next()) {
                    int xRango = rs.getInt("rango");
                    if (xRango > rango) {
                        gruppo = rs.getString(FIELDlinkUserGroupsRifGruppo);
                        rango = xRango;
                    }
                }
                // System.out.println("GRUPPO: "+gruppo +" - Rango = "+rango);

                accountConny.close();
            } catch (SQLException ex) {
                Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                String SQLphrase = "SELECT "
                        + TABLEgruppi + ".rango, "
                        + TABLElinkUserGroups + "." + FIELDlinkUserGroupsRifGruppo + " "
                        + "FROM " + TABLElinkUserGroups + " "
                        + "LEFT JOIN " + TABLEgruppi + " ON "
                        + TABLEgruppi + "." + FIELDGruppiIDgruppo + " = "
                        + TABLElinkUserGroups + "." + FIELDlinkUserGroupsRifGruppo + " "
                        + "WHERE " + FIELDlinkUserGroupsRifOperatore + " = '" + user + "' "
                        + "ORDER BY rango DESC LIMIT 1 ";

//                System.out.println("ActualRango SQLphrase: " + SQLphrase);
                Statement s;

                s = accountConny.createStatement();
                ResultSet rs = s.executeQuery(SQLphrase);
                // System.out.println("ELENCO COMPLETO DI GRUPPI A CUI APARTIENE "+myParams.getCKuserID() );
                while (rs.next()) {
                    int xRango = rs.getInt("rango");
                    if (xRango > rango) {
                        gruppo = rs.getString(FIELDlinkUserGroupsRifGruppo);
                        rango = xRango;
                    }
                }
//                System.out.println("GRUPPO: " + gruppo + " - Rango = " + rango);

                accountConny.close();
            } catch (SQLException ex) {
                Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //System.out.println("rango: " + rango);
        return rango;
    }

    public void loadDBinfos() {
        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

        String SQLphrase = "SELECT * FROM `" + TABLEoperatori + "` WHERE `" + FIELDoperatoriID
                + "` = '" + myParams.getCKuserID() + "'";
        //System.out.println(">" + SQLphrase);
        Statement s;
        try {
            s = accountConny.createStatement();
            ResultSet rs = s.executeQuery(SQLphrase);

            while (rs.next()) {

                this.setUsername(rs.getString(FIELDusernameField));
                this.setName(rs.getString(FIELDnameField));
                this.setSurname(rs.getString(FIELDsurnameField));
                break;
            }
//            System.out.println(" username>" + username);
//            System.out.println(" name>" + name);
//            System.out.println(" surname>" + surname);
        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            accountConny.close();
        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public BufferedImage loadDBpicture() {
        BufferedImage image = null;
        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

        String SQLphrase = "SELECT * FROM " + TABLEoperatori + " WHERE " + FIELDoperatoriID
                + " = '" + this.userID + "'";
//        System.out.println("\n>" + SQLphrase);
//
//        System.out.println("\nFIELDpictureField>" + this.FIELDpictureField);
        Statement s;
        try {
            s = accountConny.createStatement();
            ResultSet rs = s.executeQuery(SQLphrase);

            while (rs.next()) {
                Blob blob = null;
                image = null;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                if (rs != null) {
                    try {
                        blob = rs.getBlob(this.FIELDpictureField);
                        InputStream in = null;
                        if (blob != null) {
                            try {
                                in = blob.getBinaryStream();
                                image = ImageIO.read(in);
                            } catch (IOException ex) {
                                Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (SQLException ex) {

                    }

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            accountConny.close();
//               System.out.println("\nCARICATA IMMAGINE>" + image.getHeight());

        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

    public String getExtendedName() {
        loadDBinfos();
        String htmlCode = this.name + "<BR>" + this.surname;
        if ((this.name == null || this.name.equalsIgnoreCase("null") || this.name.length() < 1)
                && (this.surname == null || this.surname.equalsIgnoreCase("null") || this.surname.length() < 1)) {
            htmlCode = this.username;
            if ((this.username == null || this.username.equalsIgnoreCase("null") || this.username.length() < 1)) {
                htmlCode = this.userID;
            }
        }

        return htmlCode;
    }

    public String getUserPicture() {
        String ValoreDaScrivere = "";
        objectLayout myBox = new objectLayout();
        myBox.setPicWidth("50");
        myBox.setPicHeight("50");

        myBox.setWidth("50");
        myBox.setHeight("50");
        BufferedImage image = loadDBpicture();
        if (image != null) {
            ValoreDaScrivere = getRowImageHtmlCode(image, "", myBox);
        } else {
            ValoreDaScrivere = "";
        }

//////        int xW = 50;
//////        int xH = 50;
//////        String image = "<img  alt=\"...\" src='portal?target=requestsManager&gp=";
//////        String params = "\"params\":\"" + encodeURIComponent(myParams.makePORTALparams()) + "\"";
//////        String connectors = "\"connectors\":[{\"door\":\"RenderPic\","
//////                + "\"event\":\"badge\","
//////                + "\"table\":\"" + TABLEoperatori + "\","
//////                + "\"keyfield\":\"" + FIELDoperatoriID + "\","
//////                + "\"keyValue\":\"" + myParams.getCKuserID() + "\","
//////                + "\"picfield\":\"" + FIELDpictureField + "\","
//////                + " }]";
//////        String utils = "\"responseType\":\"text\"";
//////        String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";
//////
//////        image += encodeURIComponent(gp);
//////        image += "&rnd=$$$newRandom$$$'  width='" + xW + "px' heigth='" + xH + "px' >";
//////        image += "</td>";
        return ValoreDaScrivere;
    }

    public String getRowImageHtmlCode(BufferedImage image, String alternativeString, objectLayout myBox) {
//        System.out.println("\ngetRowImageHtmlCode>" + image.getHeight());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String picCode = "";
        String imageString = null;
        int radio = 10;
        if (image != null) {
            try {
                int HH = Integer.parseInt(myBox.getHeight());
                if (HH > 20) {
                    radio = HH / 2;
                }
            } catch (Error e) {
            }
            BufferedImage newImage = convertToBufferedImage(image.getScaledInstance(250, 250, Image.SCALE_DEFAULT));
            BufferedImage Rimage = makeRoundedCorner(newImage, radio);
            try {
                ImageIO.write(Rimage, "gif", bos);
                byte[] imageBytes = bos.toByteArray();
                imageString = Base64.getEncoder().encodeToString(imageBytes);
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            imageString = "";
        }
        picCode += "<img src=\"data:image/gif;base64," + imageString + "\" alt=\"" + alternativeString + "\"";
        picCode += "   width=\"" + myBox.getWidth() + "px\" heigth=\"" + myBox.getHeight() + "px\" ";
        picCode += " />";

        return picCode;
    }

    public static BufferedImage convertToBufferedImage(Image image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(qualityHints);
        g2.setClip(new RoundRectangle2D.Double(0, 0, w, h, w / 2, h / 2));
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return output;
    }

    public String getFrontendUpdateButton() {

        String htmlCode = "<TABLE><TR>"
                + "<TD>"
                + "<a href=\"javascript:frontendUpdate()\" title=\"Update\">\n"
                + "<img border=\"0\" alt=\"Update\" src=\"./media/icons/gaiaTopUpdate.png\" width=\"30\" height=\"30\">\n"
                + "</a>"
                + "</TD><TD>"
                + "<div id=\"EVObridge\" ></div></TD>"
                + "</TR></TABLE>";

        return htmlCode;
    }

    public String getUserAccountHolderButton() {

        String htmlCode = "";
        htmlCode += "<table "
                + "style=\"  border-radius: 25px;\n"
                + "  background: url(back1.jpg);\n"
                + "  background-position: left top;\n"
                + "  background-repeat: repeat;\n"
                + "  padding: 5px;\n"
                + "  width: 130px;\n"
                + "  height: 30px;\" "
                + " onclick =\"javascript:handleAccount()\" "
                + "><tr  title=\"Account\" ><td>";
        htmlCode += getUserPicture();
        htmlCode += "</td><td "
                + "style=\"width:100px; height:50px; overflow:hidden;  word-wrap: break-word; display:block;\" "
                + ">";
        htmlCode += "<P>" + getExtendedName() + "</P>";
        htmlCode += "</td>";
        htmlCode += "</tr></table> ";

        return htmlCode;
    }

    public String getUserLogoutButton() {
        String htmlCode = "<a href=\"javascript:PagerLogout()\">\n"
                + "<img border=\"0\" alt=\"Logout\" title=\"Logout\" src=\"./media/icons/gaiaTopLogout.png\" "
                + "width=\"30\" height=\"30\">\n"
                + "</a>";

        return htmlCode;
    }

    public String getBadge(String rifForm, String copyTag) {
        //System.out.println(">>>>>>>loadDBinfos() ");

        loadDBinfos();
        String htmlCode = "";

        htmlCode += "<TABLE><TR><TD>";

        // String image="<img src='renderPic?KeyField=ID&currentTbl=archivio_operatori&currentKey="+myParams.getCKuserID()+"&Prfx=user&rnd=$$$newRandom$$$'  width='50px' heigth='50px' >";
        String image = "<img src='formShow?loadType=RENDERPIC"
                + "&args=" + TABLEoperatori + ";" + FIELDoperatoriID + ";"
                + myParams.getCKuserID() + ";" + FIELDpictureField
                + "&rnd=$$$newRandom$$$"
                + "&" + myParams.makeAsyncParamsPhrase() + "'  width='30px' heigth='30px' >";

        UUID idOne = null;
        idOne = UUID.randomUUID();
        String newRandom = "" + idOne;
        htmlCode = htmlCode.replace("$$$newRandom$$$", newRandom);

        //System.out.println(">>>>>>>image: " + image);
        htmlCode += image;
        htmlCode += "</TD><TD>";

        htmlCode += "<TABLE class=\"cellContent\" ><TR><TD style=\"background:lightGrey;\">";

        htmlCode += "<a class=\"SensibleLABEL\"  ";

        String toAdd = "{\"action\":\"OpenSecForm\""
                + ",\"rifForm\":\"" + rifForm + "\""
                + ",\"copyTag\":\"" + copyTag + "\""
                + ",\"destDiv\":\"B\""
                + ",\"formToLoad\":\"accountHolder\""
                + ",\"keyValue\":\"" + myParams.getCKuserID() + "\"}";
        htmlCode += " onclick='javascript:clickedObject( " + toAdd + " )'>";

        String name = "";
        try {
            name = this.getSurname().toUpperCase();
        } catch (Exception e) {
        }
        try {
            name += " " + this.getName();
        } catch (Exception e) {
        }
        //System.out.println(">>>>>>>name=" + name);

        htmlCode += name;
        htmlCode += "</a>";
        htmlCode += "</TD></TR><TR><TD style=\"background:lightGrey;\">";
        htmlCode += "<a class=\"SensibleLABEL\" onclick=\"javascript:logout()\">LOGOUT</a>";
        htmlCode += "</TD></TR></TABLE>";
        htmlCode += "</TD></TR></TABLE>";

        return htmlCode;
    }

    public int pokeSession() {
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

        String SQLphrase;
        PreparedStatement ps;

        SQLphrase = "UPDATE `" + TABLEtokens + "` SET `updated`= NOW()  WHERE rifUser='"
                + myParams.getCKuserID() + "' AND token='" + myParams.getCKtokenID() + "'";
        try {
            ps = conny.prepareStatement(SQLphrase);
            int i = ps.executeUpdate();
            conny.close();
        } catch (SQLException ex) {
            System.out.println("Error:" + ex);
        }
        return 0;
    }

    public int verifyUserIsOwner(String ownerID) {
        int response = -1;
        boolean identity = false;
        if (ownerID.equals(myParams.getCKuserID())) {
            identity = true;
            response = 0;
        }
        if (identity) {
            response = verifyUserTimeout();
        }

        return response;
    }

    public int verifyUserTimeout() {
//        System.out.println("\n*\nSONO IN verifyUserTimeout.Verifico per " + myParams.getCKtokenID());
        if (myParams.getCKtokenID().startsWith("OVER_")) {
            System.out.println("\n*\nSONO IN verifyUserTimeout. Nego accesso per token consumato.");
            return 0;
        }
        //  System.out.println("SONO IN verifyUserTimeout" );
        //  myParams.printParams(" verifyUserTimeout ");
        //  mySettings.printSettings(" verifyUserTimeout ");
        int sessionTimeout = 90;
        int sessionFound = 0;
        String sto = "";
        String recorded = "";
        String lastTouch = "";
        String totalTime = "";
        String pauseTime = "";
        int loggedStatus = 0;

        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

        try {

            String SQLphrase;
            PreparedStatement ps;
            ResultSet rs;
//==== acquisisco il timeout impostato
            // se qualcosa va storto imposto timeout=90
            SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_directives() + " WHERE infoName='userSessionTimeout'";
            // System.out.println(SQLphrase);

            ps = accountConny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                sto = rs.getString("infoValue");
                break;
            }
//            System.out.println("userSessionTimeout: " + sto);

            try {
                sessionTimeout = Integer.parseInt(sto);
            } catch (Exception e) {
                sessionTimeout = 600; //10 minuti...
            }
            if (sessionTimeout < 1) {
                sessionTimeout = 0;
            }
            //  System.out.println("sessionTimeout FROM gEVO_directives -> " + sessionTimeout);

            //===acquisisco le informazioni di timestamp del login e lastTouch (updated)         
            //se qualcosa va storto esco subito con permesso=-1
            ps = null;
            rs = null;
            SQLphrase = "SELECT " + TABLEtokens + ".* , TIMESTAMPDIFF(SECOND, " + TABLEtokens + ".recorded, "
                    + "NOW()) as totalTime , TIMESTAMPDIFF(SECOND, " + TABLEtokens + ".updated, "
                    + "NOW()) as pauseTime FROM " + TABLEtokens + " "
                    + "WHERE rifUser='" + myParams.getCKuserID() + "' AND token='" + myParams.getCKtokenID() + "'";
            // System.out.println("verifyUserTimeout ->SQLphrase:" + SQLphrase);

            ps = accountConny.prepareStatement(SQLphrase);
            try {
                rs = ps.executeQuery();
            } catch (Exception e) {
                System.out.println("verifyUserTimeout ->ERROR :" + SQLphrase);
                SQLphrase = "SELECT " + TABLEtokens.toLowerCase() + ".* , TIMESTAMPDIFF(SECOND, " + TABLEtokens.toLowerCase() + ".recorded, "
                        + "NOW()) as totalTime , TIMESTAMPDIFF(SECOND, " + TABLEtokens.toLowerCase() + ".updated, "
                        + "NOW()) as pauseTime FROM " + TABLEtokens.toLowerCase() + " "
                        + "WHERE rifUser='" + myParams.getCKuserID() + "' AND token='" + myParams.getCKtokenID() + "'";
                // System.out.println("verifyUserTimeout ->SQLphrase:" + SQLphrase);

                ps = accountConny.prepareStatement(SQLphrase);
                try {
                    rs = ps.executeQuery();
                    System.out.println("verifyUserTimeout ->LOWERCASE OK ! :" + SQLphrase);
                } catch (Exception ex) {
                    System.out.println("verifyUserTimeout ->ERROR :" + SQLphrase);
                    ex.printStackTrace();

                }

            }

            loggedStatus = 0;
            while (rs.next()) {
                sessionFound = 1;
                recorded = rs.getString("recorded");
                lastTouch = rs.getString("updated");
                totalTime = rs.getString("totalTime");
                pauseTime = rs.getString("pauseTime");
                loggedStatus = rs.getInt("loggedStatus");
                /*
                 System.out.println("\nrecorded:" + recorded);
                 System.out.println("lastTouch:" + lastTouch);
                 System.out.println("timeout set by admin:" + sto);
                 System.out.println("totalTime:" + totalTime);
                 System.out.println("pauseTime:" + pauseTime + "\n\n\n");
                 */
            }

        } catch (SQLException ex) {
            try {
                accountConny.close();
            } catch (SQLException ex1) {
                Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex1);
            }
            System.out.println("Error:" + ex);
            sessionFound = -1;
            System.out.println("sessionFound:" + sessionFound);
            loggedStatus = -1;
            return -1;
        }

        int response = 0;
        if (loggedStatus < 1) {
            response = -1; // accesso negato
            //   System.out.println(" loggedStatus < 1:" + response);
        } else if (sessionTimeout == 0 && sessionFound > 0) { // è stato impostato zero ---> non c'è timeout
            response = 1; // accesso consentito: sessione presente e timeout non definito
            // System.out.println("accesso consentito: sessione presente e timeout non definito");
        } else {
            if (sessionFound < 1) {// non trovo il token ---> vado in timeout
                response = -1; // accesso negato
                // System.out.println(" non trovo il token ---> vado in timeout:" + response);
            } else {// valuto la sessione sul DB
                int timepassed = 24 * 60 * 60;
                try {
                    timepassed = Integer.parseInt(pauseTime);
                } catch (Exception e) {
                    timepassed = 24 * 60 * 60;
                }

                if (timepassed > sessionTimeout) {
                    response = 0; // accesso negato
                    System.out.println(" accesso negato ---> timepassed:" + timepassed);
                } else {
////////                    pokeSession();
                    //System.out.println(" accesso consentito ---> timepassed:" + timepassed);
                    response = 1;

                }
            }
        }
        if (response < 1) {
            logEvent myEvent = new logEvent();
            myEvent.setType("Logout");
            myEvent.setUser(myParams.getCKuserID());
            myEvent.setToken(myParams.getCKtokenID());
            String token = myParams.getCKtokenID();
            if (token != null && token != "null") {
                try {
                    String SQLphrase;
                    PreparedStatement ps;
                    SQLphrase = "UPDATE `" + TABLEtokens + "` SET `loggedStatus`= 0, `token`= 'OVER_" + myParams.getCKtokenID() + "'  "
                            + "WHERE rifUser='" + myParams.getCKuserID() + "' AND token='" + myParams.getCKtokenID() + "'";
//                    System.out.println("\n*\nverifyUserTimeout-->SONO IN logout." + SQLphrase);
                    ps = accountConny.prepareStatement(SQLphrase);
                    int i = ps.executeUpdate();
                    accountConny.close();
                } catch (SQLException ex) {
                    try {
                        String SQLphrase;
                        PreparedStatement ps;
                        SQLphrase = "UPDATE `" + TABLEtokens.toLowerCase() + "` SET `loggedStatus`= 0, `token`= 'OVER_" + myParams.getCKtokenID() + "'  "
                                + "WHERE rifUser='" + myParams.getCKuserID() + "' AND token='" + myParams.getCKtokenID() + "'";
//                    System.out.println("\n*\nverifyUserTimeout-->SONO IN logout." + SQLphrase);
                        ps = accountConny.prepareStatement(SQLphrase);
                        int i = ps.executeUpdate();
                        accountConny.close();
                    } catch (SQLException ee) {
                        System.out.println("Error:" + ee);
                    }
                }
                myEvent.setEventCode("tokenExpired");
            } else {
                myEvent.setEventCode("tokenNotFound");
            }
            try {
                if (myEvent.getUser() != null && myEvent.getUser() != "" && myEvent.getToken() != null && myEvent.getToken() != "") {
                    myEvent.save(myParams, mySettings);
                }
            } catch (Exception e) {
            }
        } else {
            pokeSession();
////////            try {
////////                String SQLphrase;
////////                PreparedStatement ps;
////////                SQLphrase = "UPDATE `archivio_operatoriTokens` SET `updated`= NOW()  "
////////                        + "WHERE rifUser='" + myParams.getCKuserID() + "' AND token='" + myParams.getCKtokenID() + "'";
//////////                System.out.println("\n*\nverifyUserTimeout-->SONO IN updated." + SQLphrase);
////////                ps = accountConny.prepareStatement(SQLphrase);
////////                int i = ps.executeUpdate();
////////                accountConny.close();
////////            } catch (SQLException ex) {
////////                System.out.println("Error:" + ex);
////////            }

        }
        try {
            accountConny.close();
        } catch (SQLException ex1) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex1);
        }
        return response;
    }

    public int authorizeUser() {

        // ora verifico su ArchivioAccessi che la riga con questo TOKEN abbia un valore compatibile di timeout            
        int timeoutState = verifyUserTimeout();

        //System.out.println("authorizeUser:" + timeoutState + "\n\n\n");
        if (timeoutState < 1 || myParams.getCKuserID() == null) {
            return 0;

        } else {
            return 1;
        }
    }

    public void updateRangos() {
        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

        ArrayList<SelectListLine> myList = new ArrayList<SelectListLine>();
        try {
            String SQLphrase;
            PreparedStatement ps;

            SQLphrase = "SELECT * FROM `" + TABLEoperatori + "`   ";
            //System.out.println(" creo lista operatori ---> SQLphrase:" + SQLphrase);

            ps = accountConny.prepareStatement(SQLphrase);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SelectListLine myLine = new SelectListLine();
                myLine.setLabel(rs.getString(FIELDoperatoriID));
                myLine.setChecked(getActualRango(myLine.getLabel()));
                //System.out.println(" user --->"+myLine.getLabel()+".  Rango --->" + myLine.getChecked());
                myList.add(myLine);
            }

            for (int jj = 0; jj < myList.size(); jj++) {
                SQLphrase = "UPDATE `" + TABLEoperatori + "` SET rango= " + myList.get(jj).getChecked() + " WHERE " + FIELDoperatoriID + " = '" + myList.get(jj).getLabel() + "'";
                ps = accountConny.prepareStatement(SQLphrase);
                int res = ps.executeUpdate();
            }

            accountConny.close();
        } catch (SQLException ex) {
            System.out.println("Error:" + ex);
        }

    }

    public void updateRango(String user, String TabU, String FieldU, String TabG, String FieldG, String TabLink, String FieldLinkU, String FieldLinkG) {
        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();
        int newRango = getActualRango(user, TabU, FieldU, TabG, FieldG, TabLink, FieldLinkU, FieldLinkG);

        System.out.println("Aggiorno il rango di :" + user + " al valore di " + newRango);

        String SQLphrase;
        PreparedStatement ps;

        try {
            SQLphrase = "UPDATE `" + TabU + "` SET rango= " + newRango
                    + " WHERE " + FieldU + " = '" + user + "'";
//            System.out.println("SQLphrase:" + SQLphrase);

            ps = accountConny.prepareStatement(SQLphrase);
            int res = ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            accountConny.close();
        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void updateRango(String userID) {
        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

        String SQLphrase;
        PreparedStatement ps;

        try {
            SQLphrase = "UPDATE `" + TABLEoperatori + "` SET rango= " + getActualRango(userID)
                    + " WHERE " + FIELDoperatoriID + " = '" + userID + "'";
            ps = accountConny.prepareStatement(SQLphrase);
            int res = ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            accountConny.close();
        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int removeRegistrationByIDandMail(Connection accountConny) {
        int done = 0;
        try {

            String SQLphrase = "DELETE FROM " + TABLEoperatori + " WHERE "
                    + FIELDoperatoriID + " = '" + userID + "' "
                    + " AND email = '" + email + "'";
            Statement s = accountConny.createStatement();
            done = s.executeUpdate(SQLphrase);

        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return done;
    }

    public void setOverOldTokens() {
        Connection accountConny = new EVOpagerDBconnection(myParams, mySettings).ConnAccountDB();

        String SQLphrase;
        PreparedStatement ps;

        try {
            SQLphrase = "UPDATE  ";
//            System.out.println("SQLphrase:" + SQLphrase);

            ps = accountConny.prepareStatement(SQLphrase);
            int res = ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            accountConny.close();
        } catch (SQLException ex) {
            Logger.getLogger(EVOuser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
