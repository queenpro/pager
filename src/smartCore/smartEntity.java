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
import REVOpager.EVOuser;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import models.CRUDorder;
import models.boundFields;
import models.jsonTranslate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Franco
 */
public class smartEntity {

    EVOpagerParams myParams;
    Settings mySettings;
    String fatherKEYvalue;
    String sendToCRUD;

    public int rowsCounter;

    public smartEntity(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
    }

    public int getRowsCounter() {
        return rowsCounter;
    }

    public void setRowsCounter(int rowsCounter) {
        this.rowsCounter = rowsCounter;
    }

    public void setFatherKEYvalue(String fatherKEYvalue) {
        this.fatherKEYvalue = fatherKEYvalue;
    }

    public void setSendToCRUD(String sendToCRUD) {
        this.sendToCRUD = sendToCRUD;
    }

    public smartObjRight analyzeRightsRuleJson(String rights, Connection Conny, int levelBase) {
        smartObjRight myRight = analyzeRightsRuleJson(rights, null, Conny, levelBase);
        return myRight;
    }

    public smartObjRight analyzeRightsRuleJson(String rights, ResultSet rs, Connection Conny, int levelBase) {

// verifica i diritti per stringa genericaJSON
        int gotRight = 0;
        if (rights == null || rights.length() < 5) {
            gotRight = -1;
            smartObjRight rowRights = new smartObjRight(-1);
            return rowRights;
        }
        //===================================================================        
// <editor-fold defaultstate="collapsed" desc="VALUTO EVENTUALE DISABILITAZIONE">           
        //      System.out.println("\n-----\nanalyzeRightsRuleJson:VALUTO I DIRITTI DELLA RIGA " + rights);

        if (rights.startsWith("DEFAULT")) {
            smartObjRight rowRights = new smartObjRight(-1);
            EVOuser myUser = new EVOuser(myParams, mySettings);
            myUser.setTABLElinkUserGroups("archivio_correlazioni");
            myUser.setFIELDlinkUserGroupsRifOperatore("partAvalue");
            myUser.setFIELDlinkUserGroupsRifGruppo("partBvalue");

            myUser.setTABLEgruppi("archivio_operatoriGruppi");
            myUser.setFIELDGruppiIDgruppo("IDgruppo");
            myUser.setTABLEoperatori("archivio_operatori");
            myUser.setFIELDoperatoriID("ID");
            int dirittiComplessivi = myUser.getActualRightAdvanced(rights, null);
            rowRights.totalRight = 0;
            rowRights.level = 10;
            if (dirittiComplessivi <= 0) {
                rowRights.canView = 0;
                rowRights.level = levelBase;
            }
            if (dirittiComplessivi > 0) {
                rowRights.canView = 1;
                rowRights.totalRight += 1;
            }
            if (dirittiComplessivi > 1) {
                rowRights.canModify = 1;
                rowRights.canPushButton = 1;
                rowRights.totalRight += 2;
                rowRights.totalRight += 16;
            }
            if (dirittiComplessivi > 2) {
                rowRights.canCreate = 1;

                rowRights.totalRight += 8;
            }
            if (dirittiComplessivi > 3) {
                rowRights.canDelete = 1;

                rowRights.totalRight += 4;
            }
            if (dirittiComplessivi > 4) {
                rowRights.canEverything = 1;

                rowRights.totalRight += 32;
            }

            return rowRights;
        }
        String Xrights = "{\"rights\":" + rights + "}";

//        System.out.println("\nanalyzeRightsRuleJson:" + Xrights);
        JSONObject jsonObject = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        ArrayList<lockRule> lockRules = new ArrayList<lockRule>();
        int tempRight = 0;

        boolean limitUp = false;
        boolean limitDown = false;
        try {
            jsonObject = (JSONObject) jsonParser.parse(Xrights);
            String TRIGGERSarray = jsonObject.get("rights").toString();
            if (TRIGGERSarray != null && TRIGGERSarray.length() > 0) {
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(TRIGGERSarray);

                for (Object riga : array) {
                    lockRule myRule = new lockRule();
                    myRule.level = -1;
                    // System.out.println("\n RIGA: " + riga.toString());

                    jsonObject = (JSONObject) jsonParser.parse(riga.toString());

                    try {
                        myRule.setRuleType(jsonObject.get("ruleType").toString());
                    } catch (Exception e) {
                        myRule.setRuleType("");
                    }
                    try {
                        myRule.setTypeA(jsonObject.get("typeA").toString());
                    } catch (Exception e) {
                        myRule.setTypeA("");
                    }
                    try {
                        myRule.fieldA = jsonObject.get("fieldA").toString();
                    } catch (Exception e) {
                    }
                    try {
                        myRule.fieldTypeA = jsonObject.get("fieldTypeA").toString();
                    } catch (Exception e) {
                        myRule.fieldA = "";
                    }
                    try {
                        myRule.valueA = jsonObject.get("valueA").toString();
                    } catch (Exception e) {
                        myRule.valueA = "";
                    }
                    try {
                        myRule.typeB = jsonObject.get("typeB").toString();
                    } catch (Exception e) {
                        myRule.typeB = "";
                    }
                    try {
                        myRule.fieldB = jsonObject.get("fieldB").toString();
                    } catch (Exception e) {
                        myRule.fieldB = "";
                    }
                    try {
                        myRule.fieldTypeB = jsonObject.get("fieldTypeB").toString();
                    } catch (Exception e) {
                        myRule.fieldTypeB = "";
                    }
                    try {
                        myRule.valueB = jsonObject.get("valueB").toString();
                    } catch (Exception e) {
                        myRule.valueB = "";
                    }
                    try {
                        myRule.test = jsonObject.get("test").toString();
                    } catch (Exception e) {
                        myRule.test = "==";
                    }

                    try {
                        myRule.right = Integer.parseInt(jsonObject.get("right").toString());
                    } catch (Exception e) {
                        myRule.right = 1;
                    }
                    try {
                        myRule.level = Integer.parseInt(jsonObject.get("level").toString());
                    } catch (Exception e) {
                        myRule.level = -1;
                    }
                    try {
                        myRule.limitUp = jsonObject.get("limitUp").toString();
                    } catch (Exception e) {
                        myRule.limitUp = "false";
                    }
                    try {
                        myRule.limitDown = jsonObject.get("limitDown").toString();
                    } catch (Exception e) {
                        myRule.limitDown = "false";
                    }
                    /*
                     String tabCorrelazioni;
        String tabGruppi;
        String fieldIDinTabGruppi;
                     */
                    try {
                        myRule.tabCorrelazioni = jsonObject.get("tabCorrelazioni").toString();
                    } catch (Exception e) {
                        myRule.tabCorrelazioni = "archivio_correlazioni";
                    }
                    try {
                        myRule.tabGruppi = jsonObject.get("tabGruppi").toString();
                    } catch (Exception e) {
                        myRule.tabGruppi = "archivio_operatoriGruppi";
                    }
                    try {
                        myRule.fieldIDinTabGruppi = jsonObject.get("fieldIDinTabGruppi").toString();
                    } catch (Exception e) {
                        myRule.fieldIDinTabGruppi = "IDgruppo";
                    }
                    lockRules.add(myRule);

                }
            }
        } catch (ParseException ex) {
//            Logger.getLogger(ShowItForm.class
//                    .getName()).log(Level.SEVERE, null, ex);
            System.out.println("\nerror analyzeRightsRuleJson:" + Xrights);
        }

        int RuleLevel = 0;
        for (int jj = 0; jj < lockRules.size(); jj++) {

            int valIntA = 0;
            String valStringA = "";
            int valIntB = 0;
            String valStringB = "";
            lockRule myRule = new lockRule();
            myRule = lockRules.get(jj);
            if (myRule.level < 0) {
                myRule.level = 10;
            }
//            System.out.println("\nANALIZZO REGOLA :" + jj + ": " + myRule.getRuleType());

            /*
            TIPI AMMESSI:
            -DEFAULT
            -campareRSvalue
            -userInStandardGroup
            -userInCustomGroup
             */
//            System.out.println(jj + ") rTYPE :" + myRule.getRuleType() + " RIGHT:" + myRule.getRight() + " ->LEVEL: " + myRule.getLevel());
// </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="default">
            if (myRule.getRuleType().equalsIgnoreCase("default")) {
                //VERIFICATA
                if (myRule.getLevel() > RuleLevel) {
                    //prevale myRule
                    tempRight = myRule.getRight();
                    RuleLevel = myRule.getLevel();
                } else if (myRule.getLevel() == RuleLevel) {
                    //prevale la più permissiva
                    if (tempRight < myRule.getRight()) {
                        tempRight = myRule.getRight();
                        RuleLevel = myRule.getLevel();
                    }
                } else {
                    // prevale tempRight
                }

            } //*********************************************************************************************            
            // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="compareRSvalue">
            else if (myRule.getRuleType().equalsIgnoreCase("compareRSvalue") || myRule.getRuleType().equalsIgnoreCase("campareRSvalue")) {// caso di regola che confronta un valore di riga rs
                boolean confrontoNumerico = false;
                String tipoConfronto = myRule.getTest();
                if (myRule.getFieldTypeA().equalsIgnoreCase("INT")) {
                    confrontoNumerico = true;
                }

//                 System.out.println("lockRules:" + jj + ")  " + myRule.getTypeA());
/*
[{"ruleType":"default","right":"31","limitUp":"true"}, 
{"ruleType":"campareRSvalue","typeA":"rowfield","fieldA":"ultimo","fieldTypeA":"INT","test":"==", "typeB":"value","valueB":"0","right":"25","level":"10"}]
                 */
                if (myRule.getTypeA().equalsIgnoreCase("rowfield") && rs != null) {
//                    System.out.println("A tipo rowfield. Cerco in field :" + myRule.getFieldA());
                    // cerco valore del field in rs
                    if (confrontoNumerico == true) {
                        try {
                            valIntA = rs.getInt(myRule.getFieldA());
//                            System.out.println("valIntA :" + valIntA);
                        } catch (SQLException ex) {
                        }
                    } else {
                        try {
                            valStringA = rs.getString(myRule.getFieldA());
//                            System.out.println("valStringA :" + valStringA);
                        } catch (SQLException ex) {
                        }
                    }

                } else if (myRule.getTypeA().equalsIgnoreCase("formfield")) {
                    CRUDorder myCRUD = new CRUDorder(myParams, mySettings);
                    myCRUD.setFatherKEYvalue(this.fatherKEYvalue);
                    myCRUD.setSendToCRUD(this.sendToCRUD);
                    String valFound = myCRUD.standardReplace("###" + myRule.getFieldA() + "###", null);
                    if (myRule.getFieldTypeA().equalsIgnoreCase("INT")) {
                        try {
                            valIntA = Integer.parseInt(valFound);
//                            System.out.println("valIntA :" + valIntA);
                        } catch (Exception e) {
//                            System.out.println("ERROR 6821:[valFound:" + valFound + "] ->" + e.toString());
                        }
                    } else {
                        valStringA = valFound;
//                        System.out.println("valStringA :" + valStringA);
                    }

                }

                if (myRule.getTypeB().equalsIgnoreCase("value")) {

//                    System.out.println("B tipo value. Cerco in field :" + myRule.getValueB());
                    // cerco valore del field in rs
                    if (confrontoNumerico == true) {
                        try {
                            valIntB = Integer.parseInt(myRule.getValueB());
                            //  System.out.println("CONFRONTO-> rowfield:valIntA = " + valIntA + "  valIntB = " + valIntB + "  myRule.getRight() = " + myRule.getRight());
                        } catch (Exception ex) {

                        }
                    } else {
                        try {
                            valStringB = myRule.getFieldB();
                        } catch (Exception ex) {

                        }
                    }

                } else if (myRule.getTypeB().equalsIgnoreCase("rowfield") && rs != null) {
//                    System.out.println("B tipo rowfield. Cerco in field :" + myRule.getFieldB());

                    // System.out.println("tipo rowfield. Cerco in field :" + myRule.getFieldA());
                    // cerco valore del field in rs
                    if (myRule.getFieldTypeB().equalsIgnoreCase("INT")) {
                        try {
                            valIntB = rs.getInt(myRule.getFieldB());
//                            System.out.println("valIntB  :" + valIntB);
                        } catch (SQLException ex) {
                        }
                    } else {
                        try {
                            valStringB = rs.getString(myRule.getFieldB());

//                            System.out.println("valStringB  :" + valStringB);
                        } catch (SQLException ex) {
                        }
                    }

                } else if (myRule.getTypeB().equalsIgnoreCase("formfield")) {
                    CRUDorder myCRUD = new CRUDorder(myParams, mySettings);
                    myCRUD.setFatherKEYvalue(this.fatherKEYvalue);
                    myCRUD.setSendToCRUD(this.sendToCRUD);
                    String valFound = myCRUD.standardReplace("###" + myRule.getFieldA() + "###", null);
                    if (myRule.getFieldTypeB().equalsIgnoreCase("INT")) {
                        try {
                            valIntB = Integer.parseInt(valFound);
//                            System.out.println("valIntB  :" + valIntB);
                        } catch (Exception e) {
                            System.out.println("ERROR 6821:[valFound:" + valFound + "] ->" + e.toString());
                        }
                    } else {
                        valStringB = valFound;
//                        System.out.println("valStringB  :" + valStringB);
                    }

                }

                // Adesso eseguo il test*************************************************************
//                System.out.println("confrontoNumerico:" + confrontoNumerico);
//                System.out.println("tipoConfronto:" + tipoConfronto);
                if (confrontoNumerico == true) {
                    if (tipoConfronto.equalsIgnoreCase("==")) {
//                        System.out.println("valIntA =" + valIntA + ",  valIntB =" + valIntB);
                        if (valIntA == valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
//                                System.out.println("prevale myRule =" +tempRight);
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
//                                    System.out.println("prevale myRule perchè piu permissiva =" +tempRight);
                                }
                            } else {
                                // prevale tempRight
//                                System.out.println("prevale la regola precedente =" +tempRight);
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase(">")) {
                        if (valIntA > valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
//                                System.out.println("prevale myRule =" +tempRight);
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
//                                    System.out.println("prevale myRule perchè piu permissiva =" +tempRight);
                                }
                            } else {
                                // prevale tempRight
//                                System.out.println("prevale la regola precedente =" +tempRight);
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase("<")) {
                        if (valIntA < valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase(">=")) {
                        if (valIntA >= valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase("<=")) {
                        if (valIntA <= valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    }

                } else {
                    if (tipoConfronto.equalsIgnoreCase("==")) {
                        if (valStringB.equals(valStringA)) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    }
                }

            } //*********************************************************************************************             
            // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="compareRSsize">
            else if (myRule.getRuleType().equalsIgnoreCase("coampareRSsize") || myRule.getRuleType().equalsIgnoreCase("campareRSsize")) {// caso di regola che conteggia le righe in rs
                boolean confrontoNumerico = true;
                String tipoConfronto = myRule.getTest();
                if (myRule.getFieldTypeA().equalsIgnoreCase("INT")) {
                    confrontoNumerico = true;
                }

                /*
[{"ruleType":"default","right":"31","limitUp":"true"}, 
{"ruleType":"campareRSvalue","typeA":"rowfield","fieldA":"ultimo","fieldTypeA":"INT","test":"==", "typeB":"value","valueB":"0","right":"25","level":"10"}]
                 */
                if (myRule.getTypeA().equalsIgnoreCase("NofRows")) {
//                    System.out.println("A tipo rowfield. Cerco in field :" + myRule.getFieldA());
                    // cerco valore del field in rs 

                    valIntA = rowsCounter;

                    try {
                        valIntB = Integer.parseInt(myRule.getValueB());
                        //  System.out.println("CONFRONTO-> rowfield:valIntA = " + valIntA + "  valIntB = " + valIntB + "  myRule.getRight() = " + myRule.getRight());
                    } catch (Exception ex) {

                    }

                }
//                System.out.println("campareRSsize:" + valIntA + " " + tipoConfronto + " " + valIntB);
                // Adesso eseguo il test*************************************************************
//                System.out.println("confrontoNumerico:" + confrontoNumerico);
//                System.out.println("tipoConfronto:" + tipoConfronto);
                if (confrontoNumerico == true) {
                    if (tipoConfronto.equalsIgnoreCase("==")) {
//                        System.out.println("valIntA =" + valIntA + ",  valIntB =" + valIntB);
                        if (valIntA == valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
//                                System.out.println("prevale myRule =" +tempRight);
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
//                                    System.out.println("prevale myRule perchè piu permissiva =" +tempRight);
                                }
                            } else {
                                // prevale tempRight
//                                System.out.println("prevale la regola precedente =" +tempRight);
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase(">")) {
                        if (valIntA > valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
//                                System.out.println("prevale myRule =" +tempRight);
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
//                                    System.out.println("prevale myRule perchè piu permissiva =" +tempRight);
                                }
                            } else {
                                // prevale tempRight
//                                System.out.println("prevale la regola precedente =" +tempRight);
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase("<")) {
                        if (valIntA < valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase(">=")) {
                        if (valIntA >= valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase("<=")) {
                        if (valIntA <= valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    }

                } else {
                    if (tipoConfronto.equalsIgnoreCase("==")) {
                        if (valStringB.equals(valStringA)) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    }
                }
                System.out.println("campareRSsize tempRight:" + tempRight);
            } //*********************************************************************************************                
            // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="compareTBSvalue">
            else if (myRule.getRuleType().equalsIgnoreCase("compareTBSvalue") || myRule.getRuleType().equalsIgnoreCase("campareTBSvalue")) {// caso di regola che confronta un valore di riga rs
                boolean confrontoNumerico = true;
                String tipoConfronto = myRule.getTest();
                if (myRule.getFieldTypeA().equalsIgnoreCase("INT")) {
                    confrontoNumerico = true;
                }
//                System.out.println("\n*\ncompareTBSvalue  :" + myRule.getTypeA());
                if (myRule.getTypeA().equalsIgnoreCase("formField") || myRule.getTypeA().equalsIgnoreCase("rowField")) {

//                    System.out.println("-----------------------------------this.sendToCRUD:" + this.sendToCRUD);
                    String xValueInSTC = null;
                    try {
                        jsonTranslate myJT = new jsonTranslate();
                        ArrayList<boundFields> readSTC = myJT.readSTC(this.sendToCRUD);
//                        System.out.println("tipo formField. Cerco in TBS field :" + myRule.getFieldA());
                        for (boundFields riga : readSTC) {
//                            System.out.println("compareTBSvalue____ xMarker:" + riga.getMarker() + "  --> " + riga.getValue());
                            if (riga.getMarker().equals(myRule.getFieldA())) {
                                xValueInSTC = riga.getValue();
                                break;
                            }
                        }

                    } catch (Exception e) {
                        System.out.println("compareTBSvalue____ err :" + e.toString());
                    }
                    if (xValueInSTC != null) {
//                        System.out.println("myRule.getFieldTypeA():" + myRule.getFieldTypeA());

                        // cerco valore del field in rs
                        if (myRule.getFieldTypeA().equalsIgnoreCase("INT")) {
                            try {
                                valIntA = Integer.parseInt(xValueInSTC);
//                                System.out.println("valIntA  :" + valIntA);
                            } catch (Exception ex) {
                            }
                        } else {
                            try {
                                valStringA = xValueInSTC;
                            } catch (Exception ex) {
                            }
                        }
                    }

                }
                if (confrontoNumerico == true) {
                    if (tipoConfronto.equalsIgnoreCase("==")) {
//                        System.out.println("valIntA =" + valIntA + ",  valIntB =" + valIntB);
                        if (valIntA == valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
//                                System.out.println("prevale myRule =" +tempRight);
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
//                                    System.out.println("prevale myRule perchè piu permissiva =" +tempRight);
                                }
                            } else {
                                // prevale tempRight
//                                System.out.println("prevale la regola precedente =" +tempRight);
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase(">")) {
                        if (valIntA > valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
//                                System.out.println("prevale myRule =" +tempRight);
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
//                                    System.out.println("prevale myRule perchè piu permissiva =" +tempRight);
                                }
                            } else {
                                // prevale tempRight
//                                System.out.println("prevale la regola precedente =" +tempRight);
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase("<")) {
                        if (valIntA < valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase(">=")) {
                        if (valIntA >= valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    } else if (tipoConfronto.equalsIgnoreCase("<=")) {
                        if (valIntA <= valIntB) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    }

                } else {
                    if (tipoConfronto.equalsIgnoreCase("==")) {
                        if (valStringB.equals(valStringA)) {
                            //VERIFICATA
                            if (myRule.getLevel() > RuleLevel) {
                                //prevale myRule
                                tempRight = myRule.getRight();
                                RuleLevel = myRule.getLevel();
                            } else if (myRule.getLevel() == RuleLevel) {
                                //prevale la più permissiva
                                if (tempRight < myRule.getRight()) {
                                    tempRight = myRule.getRight();
                                    RuleLevel = myRule.getLevel();
                                }
                            } else {
                                // prevale tempRight
                            }
                        }
                    }
                }

////////                if (myRule.getTypeB().equalsIgnoreCase("value")) {
////////                    // cerco valore del field in rs
////////                    if (myRule.getFieldTypeA().equalsIgnoreCase("INT")) {
////////                        try {
////////                            valIntB = Integer.parseInt(myRule.getValueB());
//////////                            System.out.println("CONFRONTO-> rowfield:valIntA = " + valIntA + "  valIntB = " + valIntB + "  myRule.getRight() = " + myRule.getRight());
////////                            //-----
////////                            if (valIntA == valIntB) {
////////                                //VERIFICATA
////////                                if (myRule.getLevel() > RuleLevel) {
////////                                    //prevale myRule
////////                                    tempRight = myRule.getRight();
////////                                    RuleLevel = myRule.getLevel();
////////                                } else if (myRule.getLevel() == RuleLevel) {
////////                                    //prevale la più permissiva
////////                                    if (tempRight < myRule.getRight()) {
////////                                        tempRight = myRule.getRight();
////////                                        RuleLevel = myRule.getLevel();
////////                                    }
////////                                } else {
////////                                    // prevale tempRight
////////                                }
////////                            }
////////
////////                            //-----
////////                        } catch (Exception ex) {
////////                            
////////                        }
////////                    } else {
////////                        try {
////////                            valStringB = myRule.getFieldB();
////////                            if (valStringB.equals(valStringA)) {
////////                                //VERIFICATA
////////                                if (myRule.getLevel() > RuleLevel) {
////////                                    //prevale myRule
////////                                    tempRight = myRule.getRight();
////////                                    RuleLevel = myRule.getLevel();
////////                                } else if (myRule.getLevel() == RuleLevel) {
////////                                    //prevale la più permissiva
////////                                    if (tempRight < myRule.getRight()) {
////////                                        tempRight = myRule.getRight();
////////                                        RuleLevel = myRule.getLevel();
////////                                    }
////////                                } else {
////////                                    // prevale tempRight
////////                                }
////////                            }
////////                            
////////                        } catch (Exception ex) {
////////                            
////////                        }
////////                    }
////////                    
////////                }
            } // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="userInStandardGroup">
            else if (myRule.getRuleType().equalsIgnoreCase("userInStandardGroup")) {//verifico se l'utente appartiene ad un gruppo
                //{"ruleType":"userInStandardGroup","test":"==", "valueB":"MEDICI","right":"17","level":"10"}
                EVOuser myUser = new EVOuser(myParams, mySettings);
                myUser.setTABLElinkUserGroups("archivio_correlazioni");
                myUser.setFIELDlinkUserGroupsRifOperatore("partAvalue");
                myUser.setFIELDlinkUserGroupsRifGruppo("partBvalue");

                myUser.setTABLEgruppi("archivio_operatoriGruppi");
                myUser.setFIELDGruppiIDgruppo("IDgruppo");
                myUser.setTABLEoperatori("archivio_operatori");
                myUser.setFIELDoperatoriID("ID");

                int diritto = -1;
                String rgt = myRule.getValueB() + ":" + myRule.getRight();
                if (Conny == null) { //analisi singola voce con apertura e chiusura connection
                    diritto = myUser.getActualRightAdvanced(rgt, null);
                } else {// analisi voci multiple con connection fornita dalla routine madre
                    diritto = myUser.getActualRightAdvanced(rgt, null, Conny);

                }

//                System.out.println("userInStandardGroup:" + myRule.getValueB() + "-->" + diritto);
                if (diritto > -1) {
                    //VERIFICATA
                    if (myRule.getLevel() > RuleLevel) {
                        //prevale myRule
                        tempRight = myRule.getRight();
                        RuleLevel = myRule.getLevel();
                    } else if (myRule.getLevel() == RuleLevel) {
                        //prevale la più permissiva
                        if (tempRight < myRule.getRight()) {
                            tempRight = myRule.getRight();
                            RuleLevel = myRule.getLevel();
                        }
                    } else {
                        // prevale tempRight
                    }

                }
            } // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="userInCustomGroup">
            else if (myRule.getRuleType().equalsIgnoreCase("userInCustomGroup")) {//verifico se l'utente appartiene ad un gruppo
                //{"ruleType":"userInStandardGroup","test":"==", "valueB":"MEDICI","right":"17","level":"10"}
                EVOuser myUser = new EVOuser(myParams, mySettings);

                /*
                     String tabCorrelazioni;
        String tabGruppi;
        String fieldIDinTabGruppi;
                 */
                myUser.setTABLElinkUserGroups(myRule.getTabCorrelazioni());//**--

                myUser.setFIELDlinkUserGroupsRifOperatore("partAvalue");
                myUser.setFIELDlinkUserGroupsRifGruppo("partBvalue");

                myUser.setTABLEgruppi(myRule.getTabGruppi());//**--
                myUser.setFIELDGruppiIDgruppo(myRule.getFieldIDinTabGruppi());//**--
                myUser.setTABLEoperatori("archivio_operatori");
                myUser.setFIELDoperatoriID("ID");

                int diritto = -1;
                String rgt = myRule.getValueB() + ":" + myRule.getRight();
                if (Conny == null) { //analisi singola voce con apertura e chiusura connection
                    diritto = myUser.getActualRightAdvanced(rgt, null);
                } else {// analisi voci multiple con connection fornita dalla routine madre
                    diritto = myUser.getActualRightAdvanced(rgt, null, Conny);

                }

//                System.out.println("userInStandardGroup:" + myRule.getValueB() + "-->" + diritto);
                if (diritto > -1) {
                    //VERIFICATA
                    if (myRule.getLevel() > RuleLevel) {
                        //prevale myRule
                        tempRight = myRule.getRight();
                        RuleLevel = myRule.getLevel();
                    } else if (myRule.getLevel() == RuleLevel) {
                        //prevale la più permissiva
                        if (tempRight < myRule.getRight()) {
                            tempRight = myRule.getRight();
                            RuleLevel = myRule.getLevel();
                        }
                    } else {
                        // prevale tempRight
                    }

                }
            } // </editor-fold>             
            //----------------------------------------------------------    
            // <editor-fold defaultstate="collapsed" desc="userInOldGroup">
            else if (myRule.getRuleType().equalsIgnoreCase("userInOldGroup")) {//verifico se l'utente appartiene ad un gruppo
                //{"ruleType":"userInOldGroup","test":"==", "valueB":"MEDICI","right":"17","level":"10"}
                EVOuser myUser = new EVOuser(myParams, mySettings);
                int diritto = -1;
                String rgt = myRule.getValueB() + ":" + myRule.getRight();
                if (Conny == null) { //analisi singola voce con apertura e chiusura connection
                    diritto = myUser.getActualRight(rgt, null);
                } else {// analisi voci multiple con connection fornita dalla routine madre
                    diritto = myUser.getActualRight(rgt, null, Conny);

                }

//                System.out.println("userInGroup:" + myRule.getValueB() + "-->" + diritto);
                if (diritto > -1) {
                    //VERIFICATA
                    if (myRule.getLevel() > RuleLevel) {
                        //prevale myRule
                        tempRight = myRule.getRight();
                        RuleLevel = myRule.getLevel();
                    } else if (myRule.getLevel() == RuleLevel) {
                        //prevale la più permissiva
                        if (tempRight < myRule.getRight()) {
                            tempRight = myRule.getRight();
                            RuleLevel = myRule.getLevel();
                        }
                    } else {
                        // prevale tempRight
                    }

                }
            }
// </editor-fold>             
            //----------------------------------------------------------    

        }
        smartObjRight rowRights = new smartObjRight(tempRight);
        rowRights.level = RuleLevel;
//        System.out.println(rights + "\n analyzeRightsRuleJson ->genera:" + tempRight + " LEVEL: " + rowRights.level);
//
//        System.out.println("level*********" + rowRights.level);
//        System.out.println("1.canView***************" + rowRights.canView);
//        System.out.println("2.canModify*************" + rowRights.canModify);
//        System.out.println("4.canDelete*************" + rowRights.canDelete);
//        System.out.println("8.canCreate*************" + rowRights.canCreate);
//        System.out.println("16.canPushButton*********" + rowRights.canPushButton);
//        System.out.println("128.canEverything*********" + rowRights.canEverything);
        return rowRights;
    }

    public class lockRule {

        String ruleType;
        String typeA;
        String fieldA;
        String fieldTypeA;
        String valueA;
        String typeB;
        String fieldB;
        String fieldTypeB;
        String valueB;
        String test = "";
        int level;
        int right;
        String limitUp;
        String limitDown;

        String tabCorrelazioni;
        String tabGruppi;
        String fieldIDinTabGruppi;

        public String getTabCorrelazioni() {
            return tabCorrelazioni;
        }

        public void setTabCorrelazioni(String tabCorrelazioni) {
            this.tabCorrelazioni = tabCorrelazioni;
        }

        public String getTabGruppi() {
            return tabGruppi;
        }

        public void setTabGruppi(String tabGruppi) {
            this.tabGruppi = tabGruppi;
        }

        public String getFieldIDinTabGruppi() {
            return fieldIDinTabGruppi;
        }

        public void setFieldIDinTabGruppi(String fieldIDinTabGruppi) {
            this.fieldIDinTabGruppi = fieldIDinTabGruppi;
        }

        public String getRuleType() {
            return ruleType;
        }

        public void setRuleType(String ruleType) {
            this.ruleType = ruleType;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getFieldTypeA() {
            return fieldTypeA;
        }

        public void setFieldTypeA(String fieldTypeA) {
            this.fieldTypeA = fieldTypeA;
        }

        public String getFieldTypeB() {
            return fieldTypeB;
        }

        public void setFieldTypeB(String fieldTypeB) {
            this.fieldTypeB = fieldTypeB;
        }

        public String getTypeA() {
            return typeA;
        }

        public void setTypeA(String typeA) {
            this.typeA = typeA;
        }

        public String getFieldA() {
            return fieldA;
        }

        public void setFieldA(String fieldA) {
            this.fieldA = fieldA;
        }

        public String getValueA() {
            return valueA;
        }

        public void setValueA(String valueA) {
            this.valueA = valueA;
        }

        public String getTypeB() {
            return typeB;
        }

        public void setTypeB(String typeB) {
            this.typeB = typeB;
        }

        public String getFieldB() {
            return fieldB;
        }

        public void setFieldB(String fieldB) {
            this.fieldB = fieldB;
        }

        public String getValueB() {
            return valueB;
        }

        public void setValueB(String valueB) {
            this.valueB = valueB;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }

        public String getLimitUp() {
            return limitUp;
        }

        public void setLimitUp(String limitUp) {
            this.limitUp = limitUp;
        }

        public String getLimitDown() {
            return limitDown;
        }

        public void setLimitDown(String limitDown) {
            this.limitDown = limitDown;
        }

    }

}
