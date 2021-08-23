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
package REVOdbManager;

import REVOpager.EVOpagerDBconnection;
import REVOsetup.ErrorLogger;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
//import models.PDFdocument;
import showIt.eventManager;

/**
 *
 * @author Franco
 */
public class EVOpagerDirectivesManager {

    EVOpagerParams myParams;
    Settings mySettings;
    ErrorLogger el;

    public EVOpagerDirectivesManager(EVOpagerParams myParams, Settings settings) {
        this.mySettings = settings;
        this.myParams = new EVOpagerParams();
        this.myParams = myParams;
        this.myParams.setCKcontextID(myParams.getCKcontextID());
        this.myParams.setCKprojectName(myParams.getCKprojectName());
        // ATTENZIONE 
        // se apro un EVOpagerDirectivesManager senza aver fatto il login potrei non avere il valore di ContextID
        // se è nullo meglio cercarlo in queenpro come si fa durante il login
        String extension = "";
        if (this.myParams == null
                || this.myParams.getCKprojectName() == null
                || (this.myParams.getCKcontextID() == null
                || this.myParams.getCKcontextID().length() < 1)) {
//            System.out.println("..3" );
            // el = new ErrorLogger(this.myParams, this.mySettings);

            Connection QPconny = new EVOpagerDBconnection(this.myParams, this.mySettings).ConnLocalQueenpro();
            String SQLphrase = "SELECT * FROM definitions WHERE ID='" + this.mySettings.getProjectName() + "'";
            PreparedStatement ps;
            if (QPconny != null) {
                try {
                    ps = QPconny.prepareStatement(SQLphrase);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        extension = rs.getString("definition");
                    }
                    QPconny.close();
                    this.myParams.setCKprojectName(this.mySettings.getProjectName());
                    this.myParams.setCKcontextID(extension);
//                System.out.println("INSERITO D'UFFICIO IL CONTEXT " + extension);
                } catch (SQLException ex) {
                    Logger.getLogger(eventManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public String getDirective(String infoName) {

        //System.out.println("\n-------------------\ngetDirective:" + infoName);
        // se sto cercando una direttiva PRIMA del login, potrei non avere compilao il contextID 
        // el.log("EVOpagerDirectivesManager", "Cerco informazione:" + infoName);
        //     EVOpagerDBconnection myDBC;
//        System.out.println("getDirective]myParams.getCKprojectName(): " + myParams.getCKprojectName());
//        System.out.println("getDirective]myParams.getCKcontextID(): " + myParams.getCKcontextID());
//            
//        
        String defaultValue = null;
        String infoID = null;
        String customValue = null;
        String infoValue = null;
        String customByInstance = null;
        String typeFound = "NONE";
        Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();

        String SQLphrase;
        PreparedStatement ps;
        ResultSet rs;
        SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_directives() + " WHERE infoName='" + infoName + "'";
//        System.out.println(" - SQLphrase:" + SQLphrase);
        //  el.log("EVOpagerDirectivesManager", SQLphrase);
        int lines = 0;
        if (FEconny != null) {
            try {
                ps = FEconny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();

                while (rs.next()) {
                    lines++;
                    defaultValue = rs.getString("infoValue");
                    infoID = rs.getString("ID");
                    customByInstance = rs.getString("customByInstance");
                    //System.out.println("Trovato valore di DEFAULT:" + defaultValue);
                    typeFound = "DEFAULT";
                }
            } catch (SQLException ex) {
                System.out.println("Error:" + ex);
            }

            ps = null;
            rs = null;
            try {
                if (lines > 0 && customByInstance.equalsIgnoreCase("TRUE")) {

                    SQLphrase = "SELECT * FROM " + mySettings.getLocalCUSTOM_directives()
                            + " WHERE ID='" + infoID + "'"
                            + " OR  infoName='" + infoName + "'";
                    //System.out.println("cerco anche in " + mySettings.getLocalCUSTOM_directives() + ":\n" + SQLphrase);

                    ps = FEconny.prepareStatement(SQLphrase);

                    // System.out.println("Preparato statement... ");
                    rs = ps.executeQuery();
                    // System.out.println("Eseguita richiesta... ");
                    while (rs.next()) {
                        //System.out.println("Trovato valore infoName:" + rs.getString("infoName"));
                        customValue = rs.getString("infoValue");
                        //System.out.println("Trovato valore CUSTOM:" + customValue);
                        defaultValue = customValue;
                        typeFound = "CUSTOM";
                    }
                    //System.out.println("Conclusa ricerca... \n");
                }

            } catch (SQLException ex) {
                System.out.println("Error:" + ex);
            }
            try {
                FEconny.close();
            } catch (SQLException ex) {
                Logger.getLogger(EVOpagerDirectivesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        infoValue = defaultValue;
        //System.out.println("Restituisco valore [" + typeFound + "] :" + infoValue);
        return infoValue;
    }

    public Image getDirectiveMedia(String infoName) {

//        System.out.println("Cerco media info:" + infoName);
        // se sto cercando una direttiva PRIMA del login, potrei non avere compilao il contextID 
        // el.log("EVOpagerDirectivesManager", "Cerco informazione:" + infoName);
        //     EVOpagerDBconnection myDBC;
        String defaultValue = null;
        String infoID = null;
        String customValue = null;
        String infoValue = null;
        String customByInstance = "";
        Image myPic = null;
        Image myPic2 = null;
        try {
            Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();

            String SQLphrase;
            PreparedStatement ps;
            ResultSet rs;
            SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_directives() + " WHERE infoName='" + infoName + "'";
            //System.out.println("SQLphrase:" + SQLphrase);
            //  el.log("EVOpagerDirectivesManager", SQLphrase);

            ps = FEconny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            int lines = 0;
            while (rs.next()) {
                lines++;

                Blob blob;
                try {
                    infoID = rs.getString("ID");
                    customByInstance = rs.getString("customByInstance");
                    blob = rs.getBlob("media");
                    if (blob != null) {
                        InputStream in = null;
                        try {
                            in = blob.getBinaryStream();

                            BufferedImage bi;
                            myPic = null;
                            try {
                                bi = ImageIO.read(in);
                                try {
                                    myPic = Image.getInstance(bi, null);
                                } catch (BadElementException ex) {
                                    Logger.getLogger(EVOpagerDirectivesManager.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            } catch (IOException ex) {
                                Logger.getLogger(EVOpagerDirectivesManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (SQLException ex) {

                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(EVOpagerDirectivesManager.class.getName()).log(Level.SEVERE, null, ex);
                    blob = null;
                }

                break;
            }

            if (lines > 0 && customByInstance.equalsIgnoreCase("TRUE")) {

                SQLphrase = "SELECT * FROM " + mySettings.getLocalCUSTOM_directives() + " WHERE ID='" + infoID + "'";
                // System.out.println("cerco anche in " + mySettings.getLocalCUSTOM_directives() + ":" + SQLphrase);

                ps = FEconny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();
                int lines2 = 0;
                while (rs.next()) {
                    lines2++;

                    Blob blob;
                    try {
                        blob = rs.getBlob("media");
                        if (blob != null) {
                            InputStream in = null;
                            try {
                                in = blob.getBinaryStream();

                                BufferedImage bi;
                                myPic2 = null;
                                try {
                                    bi = ImageIO.read(in);
                                    try {
                                        myPic2 = Image.getInstance(bi, null);
                                    } catch (BadElementException ex) {
                                        Logger.getLogger(EVOpagerDirectivesManager.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                } catch (IOException ex) {
                                    Logger.getLogger(EVOpagerDirectivesManager.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } catch (SQLException ex) {
                            }
                        }

                    } catch (SQLException ex) {
                        Logger.getLogger(EVOpagerDirectivesManager.class.getName()).log(Level.SEVERE, null, ex);
                        blob = null;
                    }

                    break;
                }

                if (lines2 > 0) {
                    // significa che ho trovato un valore personalizzato
                    // quindi prevarica sul valore standard
                    myPic = myPic2;

                }

            }

            FEconny.close();

        } catch (SQLException ex) {

            System.out.println("Error:" + ex);
            //   el.log("EVOpagerDirectivesManager", "Connection error:" + ex.toString());

        }
        return myPic;
    }

//////    public String setDirective(String infoName, String infoValue) {
//////
//////        // System.out.println("Cerco informazione:" + infoName);
//////        EVOpagerDBconnection myDBC;
//////
//////        Boolean existFEdirective = false;
//////        Boolean existCUSTOMdirective = false;
//////
//////        String defaultValue = null;
//////        String infoID = null;
//////        String customValue = null;
//////        //
//////        String customByInstance = null;
//////        try {
//////            Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();
//////
//////            String SQLphrase;
//////            PreparedStatement ps;
//////            ResultSet rs;
//////            SQLphrase = "SELECT * FROM " + mySettings.getLocalFE_directives() + " WHERE infoName='" + infoName + "'";
//////            ps = FEconny.prepareStatement(SQLphrase);
//////            rs = ps.executeQuery();
//////            int lines = 0;
//////            while (rs.next()) {
//////                lines++;
//////                defaultValue = rs.getString("infoValue");
//////                infoID = rs.getString("ID");
//////                customByInstance = rs.getString("customByInstance");
//////               // System.out.println("Trovato valore di DEFAULT:" + defaultValue);
//////                break;
//////            }
//////            if (lines > 0 && customByInstance.equalsIgnoreCase("TRUE")) {
//////
//////                SQLphrase = "SELECT * FROM " + mySettings.getLocalCUSTOM_directives() + " WHERE ID='" + infoID + "'";
//////                ps = FEconny.prepareStatement(SQLphrase);
//////                rs = ps.executeQuery();
//////                int lines2 = 0;
//////                while (rs.next()) {
//////                    lines2++;
//////                    customValue = rs.getString("infoValue");
//////                  //  System.out.println("Trovato valore CUSTOM:" + customValue);
//////                    break;
//////
//////                }
//////
//////                if (lines2 > 0) {
//////                    // significa che ho trovato un valore personalizzato
//////                    // quindi prevarica sul valore standard
//////                    defaultValue = customValue;
//////
//////                }
//////
//////            }
//////
//////            FEconny.close();
//////
//////        } catch (SQLException ex) {
//////
//////            System.out.println("Error:" + ex);
//////
//////        }
//////
//////        //   System.out.println("Restituisco valore:" + infoValue);
//////        return infoValue;
//////    }
    public String getEvoDirective(String infoName) {

        //System.out.println("339_getEvoDirective:" + infoName);
        //   el.log("EVOpagerDirectivesManager", "Cerco informazione EVO:" + infoName);
        String defaultValue = null;
        String infoID = null;
        String customValue = null;
        String infoValue = null;
        String customByInstance = null;
        try {
            Connection FEconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalFE();

            if (FEconny != null) {

                String SQLphrase;
                PreparedStatement ps;
                ResultSet rs;
                SQLphrase = "SELECT * FROM " + mySettings.getLocalEVO_directives() + " WHERE infoName='" + infoName + "'";
                //       el.log("EVOpagerDirectivesManager", SQLphrase);

                ps = FEconny.prepareStatement(SQLphrase);
                rs = ps.executeQuery();
                int lines = 0;
                while (rs.next()) {
                    lines++;
                    defaultValue = rs.getString("infoValue");
                    break;
                }

                infoValue = defaultValue;
                FEconny.close();
            }
        } catch (SQLException ex) {

            System.out.println("\n\n**********Error:" + ex);
            //  el.log("EVOpagerDirectivesManager ERROR", ex.toString());

        }

        // System.out.println("Restituisco valore:" + infoValue);
        return infoValue;
    }

}
