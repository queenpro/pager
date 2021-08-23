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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.ShowItForm;
import static showIt.ShowItForm.makeRoundedCorner;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class SVGform {

    EVOpagerParams myParams;
    Settings mySettings;
    gaiaMapBGpic myBGpic;
    ArrayList<gaiaMapPath> myPaths;
    ArrayList<gaiaMapObject> myMapObjects;
    ArrayList<gaiaMapRoutine> myMapRoutines;
    ArrayList<gaiaMapPoster> myMapPosters;
    String BGgroupHead = "";
    String PATHSgroupHead = "";
    String objectsCode = "";
    String recordsCode = "";// usato per creare un input hidden per ogni path

    String layerBG;
    String layerObjects;
    String layerRoutines;
    String layerPosters;
    String layerPaths;
    String layerRecords;

    String layersDIVcode;

    public SVGform(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        myPaths = new ArrayList();
        myMapObjects = new ArrayList();
        myMapRoutines = new ArrayList();
        myMapPosters = new ArrayList();
    }

    public String getLayersDIVcode() {
        String htmlCode
                = "<link rel=\"stylesheet\" type=\"text/css\" href=\"external/mapHandler/mainx.css\" />\n"
                + " <div id=\"container\" "
                + "style=\"width: 1920px; height:1024px;border:0px;\">\n"
                + "<svg   \n"
                + "	xmlns=\"http://www.w3.org/2000/svg\"\n"
                + "	style=\""
                + "	width: 1920px; \n"
                + "	min-width: 1920px;"
                + "	height: 1080px; \n"
                + "	min-height: 1080px;"
                + "     border:5px solid black;"
                + "     display:inline;"
                + "\" \n"
                + "	version=\"1.1\" id=\"qp-map\"\n"
                + "     onload=\"SVGOBJmakeDraggable(evt)\" "
                + ""
                + ""
                + "   >\n"
                + "  \n";

        htmlCode += getLayerBG();
        htmlCode += getLayerPaths();
        htmlCode += getLayerObjects();
        htmlCode += getLayerRoutines();
        htmlCode += getLayerPosters();
        htmlCode += getLayerRecords();

        htmlCode += "</svg>\n"
                + "</p>\n"
                + "</div>\n";
        htmlCode += "<script src=\"https://code.jquery.com/jquery-2.2.4.min.js\"></script>\n"
                + "<script src=\"external/mapHandler/main.js\"></script>\n"
                + "<script src=\"external/mapHandler/svg-pan-zoom.js\"></script>\n";
        layersDIVcode = htmlCode;
        return layersDIVcode;
    }

    public String getLayerRecords() {
        return layerRecords;
    }

    public void setLayerRecords(String layerRecords) {
        this.layerRecords = layerRecords;
    }

    public String getLayerBG() {
        return layerBG;
    }

    public void setLayerBG(String layerBG) {
        this.layerBG = layerBG;
    }

    public String getLayerObjects() {
        return layerObjects;
    }

    public void setLayerObjects(String layerObjects) {
        this.layerObjects = layerObjects;
    }

    public String getLayerRoutines() {
        return layerRoutines;
    }

    public void setLayerRoutines(String layerRoutines) {
        this.layerRoutines = layerRoutines;
    }

    public String getLayerPosters() {
        return layerPosters;
    }

    public void setLayerPosters(String layerPosters) {
        this.layerPosters = layerPosters;
    }

    public String getLayerPaths() {
        return layerPaths;
    }

    public void setLayerPaths(String layerPaths) {
        this.layerPaths = layerPaths;
    }

    public void makeMap(ShowItForm myForm, CRUDorder myCRUD, Connection conny, String mode) {
        String query = "";
        System.out.println("layers :" + myForm.getObjects().size());

        String SQLphrase = "";
        PreparedStatement ps = null;
        ResultSet rs;

        String jsonBackground = "";
        String FileSysName = "";
        for (int oo = 0; oo < myForm.getObjects().size(); oo++) {
            System.out.println("OGGETTO " + oo + ") C.Type:" + myForm.getObjects().get(oo).C.Type);
            //ciclo per ogni kit di oggetti
            if (myForm.getObjects().get(oo).C.Type.equalsIgnoreCase("RTSYNOPTIC")) {
//                System.out.println("OGGETTO " + oo + ") C.Type:" + myForm.getObjects().get(oo).C.Type);

                if (myForm.getObjects().get(oo).CG != null
                        && myForm.getObjects().get(oo).CG.Type != null) {
                    //----------------------------------------------------------   
                    // <editor-fold defaultstate="collapsed" desc="OBJECTS">
                    if (myForm.getObjects().get(oo).CG.Type.equalsIgnoreCase("OBJECTS")) {
                        System.out.println("OGGETTO " + oo + ") .Content.Type:" + myForm.getObjects().get(oo).CG.Type);
                        try {
                            String ObjParams = "";
//                    query = "SELECT perifMapObjects.*,lanPeriphs.ID AS periphID , lanPeriphs.direction, lanPeriphs.*,perifTypes.* 
//                              FROM perifMapObjects  LEFT JOIN lanPeriphs ON lanPeriphs.ID = rifPeriph  
//                              LEFT JOIN perifTypes ON perifTypes.rifDirection =  lanPeriphs.direction AND perifTypes.ID = lanPeriphs.type 
//                              WHERE rifMap = '###MAPID###'";
                            query = myForm.getObjects().get(oo).Origin.query;
                            SQLphrase = myCRUD.standardReplace(query, null);
//                            System.out.println("\n SVGobjects>>> SQLphrase: " + SQLphrase);
                            ps = conny.prepareStatement(SQLphrase);
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                gaiaMapObject myObject = new gaiaMapObject();
                                myObject.formObjectName = myForm.getObjects().get(oo).getID();
                                try {
                                    myObject.description = rs.getString("description");
                                    myObject.tip = rs.getString("description");
                                } catch (Exception ex) {
                                }
                                try {
                                    myObject.ID = rs.getInt("ID");
                                } catch (Exception ex) {
                                }
                                try {
                                    myObject.posX = rs.getString("posX");
                                } catch (Exception ex) {
                                }
                                try {
                                    myObject.posY = rs.getString("posY");
                                } catch (Exception ex) {
                                }
                                try {
                                    myObject.posZ = rs.getString("posZ");
                                } catch (Exception ex) {
                                }
                                try {
                                    myObject.rifDevice = rs.getString("rifDevice");
                                } catch (Exception ex) {
                                }
                                try {
                                    myObject.rifMap = rs.getString("rifMap");
                                } catch (Exception ex) {
                                }
                                try {
                                    myObject.rifPeriph = rs.getString("rifOrigin");
                                } catch (Exception ex) {
                                }
                                try {
                                    myObject.width = rs.getInt("width");
                                } catch (Exception ex) {
                                }
                                try {
                                    myObject.height = rs.getInt("height");
                                } catch (Exception ex) {
                                }
                                try {
                                    myObject.value = rs.getInt("value");
                                } catch (Exception ex) {
                                }
                                try {
                                    myObject.Enabled = rs.getInt("Enabled");
                                } catch (Exception ex) {
                                }

                                String picField = "pic1";
                                String picCode = "";
                                if (myObject.value == 0) {
                                    picField = "pic0";
                                }
                                if (myObject.Enabled > 0) {
                                    picField = "picDisabled";
                                }

//                                System.out.println("TIP per object:" + myObject.tip
//                                        + " STATUS:" + myObject.value
//                                        + " ENABLED:" + myObject.Enabled
//                                        + " picField:" + picField);
                                //ora ottengo l'immagine
                                String boxClass = "{\"type\":\"picOnly\",\"picWidth\":\"" + myObject.width + "px\","
                                        + "\"picHeight\":\"" + myObject.height + "px\"}";
                                objectLayout myBox = new objectLayout();
                                myBox.loadBoxLayout(boxClass, "" + myObject.width, "" + myObject.height);
                                Blob blob = null;
                                BufferedImage image = null;
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                if (rs != null) {
                                    try {
                                        blob = rs.getBlob(picField);
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
                                        // Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }
                                if (image != null) {
                                    picCode = getRowImageHtmlCode(image, "xxx", myBox, false);
                                } else {
                                    picCode = "";
                                }
                                System.out.println("picCode: " + picCode);
                                myObject.actualPic = picCode;

                                //-------------------------------------------
                                String params = myForm.objects.get(oo).getActionParams();
                                if (params == null) {
                                    params = "{}";
                                }
                                String toAdd = ",\"action\":\"" + myForm.objects.get(oo).getActionPerformed() + "\""
                                        + ",\"rifForm\":\"" + myForm.getID() + "\""
                                        + ",\"copyTag\":\"" + myForm.getCopyTag() + "\""
                                        + ",\"fatherForm\":\"" + myForm.getFather() + "\""
                                        + ",\"ges_routineOnLoad\":\"" + myForm.getGes_routineOnLoad() + "\""
                                        + ",\"triggerEvent\":\"click\""
                                        + ",\"rifObj\":\"" + myForm.objects.get(oo).name + "\"";

////////////                                System.out.println("nrifObj:" + myForm.objects.get(oo).name);
////////////                                System.out.println("\tgetActionParams:" + myForm.objects.get(oo).getActionParams());
////////////                                System.out.println("\tgetActionPerformed:" + myForm.objects.get(oo).getActionPerformed());
////////////                                System.out.println("\trifForm:" + myForm.getID());
////////////                                System.out.println("\tcopyTag:" + myForm.getCopyTag());
////////////                                System.out.println("\tfatherForm:" + myForm.getFather());
////////////                                System.out.println("\tgetGes_routineOnLoad:" + myForm.getGes_routineOnLoad());
////////////                                System.out.println("\trifForm:" + myForm.getID());
////////////                                System.out.println("\tcopyTag:" + myForm.getCopyTag());
                                //------------------------------
                                String spotCode = toAdd;
                                spotCode += ",\"keyValue\":\"" + myObject.rifPeriph + "\"}";

                                ObjParams = params.replace("}", spotCode);
                                myObject.onClickParams = ObjParams;

                                myMapObjects.add(myObject);

                            }

                        } catch (SQLException ex) {
                            System.out.println(ex.toString());
                        }
                    } // </editor-fold>  
                    //----------------------------------------------------------   
                    // <editor-fold defaultstate="collapsed" desc="ROUTINES">
                    else if (myForm.getObjects().get(oo).CG.Type.equalsIgnoreCase("ROUTINES")) {
                        System.out.println("ROUTINES " + oo + ") .Content.Type:" + myForm.getObjects().get(oo).CG.Type);
                        try {
                            String ObjParams = "";
//                    query = "SELECT perifMapObjects.*,lanPeriphs.ID AS periphID , lanPeriphs.direction, lanPeriphs.*,perifTypes.* 
//                              FROM perifMapObjects  LEFT JOIN lanPeriphs ON lanPeriphs.ID = rifPeriph  
//                              LEFT JOIN perifTypes ON perifTypes.rifDirection =  lanPeriphs.direction AND perifTypes.ID = lanPeriphs.type 
//                              WHERE rifMap = '###MAPID###'";
                            query = myForm.getObjects().get(oo).Origin.query;
                            SQLphrase = myCRUD.standardReplace(query, null);
                            System.out.println("\n SVGroutines>> SQLphrase: " + SQLphrase);
                            ps = conny.prepareStatement(SQLphrase);
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                gaiaMapRoutine myRoutine = new gaiaMapRoutine();
                                myRoutine.formObjectName = myForm.getObjects().get(oo).getID();
                                try {
                                    myRoutine.description = rs.getString("description");
                                    myRoutine.tip = rs.getString("description");
                                } catch (Exception ex) {
                                }
                                try {
                                    myRoutine.ID = rs.getInt("ID");
                                } catch (Exception ex) {
                                }
                                try {
                                    myRoutine.posX = rs.getString("posX");
                                } catch (Exception ex) {
                                }
                                try {
                                    myRoutine.posY = rs.getString("posY");
                                } catch (Exception ex) {
                                }
                                try {
                                    myRoutine.posZ = rs.getString("posZ");
                                } catch (Exception ex) {
                                }
                                try {
                                    myRoutine.rifDevice = rs.getString("rifDevice");
                                } catch (Exception ex) {
                                }

                                try {
                                    myRoutine.rifMap = rs.getString("rifMap");
                                } catch (Exception ex) {
                                }
                                try {
                                    myRoutine.rifPeriph = rs.getString("routineName");
                                } catch (Exception ex) {
                                }
                                try {
                                    myRoutine.width = rs.getInt("width");
                                } catch (Exception ex) {
                                }
                                try {
                                    myRoutine.height = rs.getInt("height");
                                } catch (Exception ex) {
                                }
                                try {
                                    myRoutine.value = myRoutine.ID;
                                } catch (Exception ex) {
                                }
                                try {
                                    myRoutine.Enabled = rs.getInt("Enabled");
                                } catch (Exception ex) {
                                }

                                String picField = "pic0";
                                String picCode = "";
//                                if (myRoutine.value == 0) {
//                                    picField = "pic0";
//                                }
//                                if (myRoutine.Enabled > 0) {
//                                    picField = "picDisabled";
//                                }

//                                System.out.println("TIP per object:" + myRoutine.tip
//                                        + " STATUS:" + myRoutine.value
//                                        + " ENABLED:" + myRoutine.Enabled
//                                        + " picField:" + picField);
                                //ora ottengo l'immagine
                                String boxClass = "{\"type\":\"picOnly\",\"picWidth\":\"" + myRoutine.width + "px\","
                                        + "\"picHeight\":\"" + myRoutine.height + "px\"}";
                                objectLayout myBox = new objectLayout();
                                myBox.loadBoxLayout(boxClass, "" + myRoutine.width, "" + myRoutine.height);
                                Blob blob = null;
                                BufferedImage image = null;
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                if (rs != null) {
                                    try {
                                        blob = rs.getBlob(picField);
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
                                        // Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }
                                if (image != null) {
                                    picCode = getRowImageHtmlCode(image, "xxx", myBox, false);
                                } else {
                                    picCode = "";
                                }
                                System.out.println("picCode: " + picCode);
                                myRoutine.actualPic = picCode;

                                //-------------------------------------------
                                String params = myForm.objects.get(oo).getActionParams();
                                if (params == null) {
                                    params = "{}";
                                }
                                String toAdd = ",\"action\":\"" + myForm.objects.get(oo).getActionPerformed() + "\""
                                        + ",\"rifForm\":\"" + myForm.getID() + "\""
                                        + ",\"copyTag\":\"" + myForm.getCopyTag() + "\""
                                        + ",\"fatherForm\":\"" + myForm.getFather() + "\""
                                        + ",\"ges_routineOnLoad\":\"" + myForm.getGes_routineOnLoad() + "\""
                                        + ",\"triggerEvent\":\"click\""
                                        + ",\"rifObj\":\"" + myForm.objects.get(oo).name + "\"";

////////////                                System.out.println("nrifObj:" + myForm.objects.get(oo).name);
////////////                                System.out.println("\tgetActionParams:" + myForm.objects.get(oo).getActionParams());
////////////                                System.out.println("\tgetActionPerformed:" + myForm.objects.get(oo).getActionPerformed());
////////////                                System.out.println("\trifForm:" + myForm.getID());
////////////                                System.out.println("\tcopyTag:" + myForm.getCopyTag());
////////////                                System.out.println("\tfatherForm:" + myForm.getFather());
////////////                                System.out.println("\tgetGes_routineOnLoad:" + myForm.getGes_routineOnLoad());
////////////                                System.out.println("\trifForm:" + myForm.getID());
////////////                                System.out.println("\tcopyTag:" + myForm.getCopyTag());
                                //------------------------------
                                String spotCode = toAdd;
                                spotCode += ",\"keyValue\":\"" + myRoutine.ID + "\"}";

                                ObjParams = params.replace("}", spotCode);
                                myRoutine.onClickParams = ObjParams;

                                myMapRoutines.add(myRoutine);

                            }

                        } catch (SQLException ex) {
                            System.out.println(ex.toString());
                        }
                    } // </editor-fold>  
                    //----------------------------------------------------------  
                    // <editor-fold defaultstate="collapsed" desc="BGIMAGE">
                    else if (myForm.getObjects().get(oo).CG.Type.equalsIgnoreCase("BGIMAGE")) {
                        System.out.println("OGGETTO " + oo + ") .Content.Type:" + myForm.getObjects().get(oo).CG.Type);
                        try {
//            query = " SELECT * FROM perifMaps WHERE  perifMaps.ID = '###MAPID###'";
                            query = myForm.getObjects().get(oo).Origin.query;
                            SQLphrase = myCRUD.standardReplace(query, null);
                            System.out.println("\n SVGbackground image>>> SQLphrase: " + SQLphrase);

                            ps = conny.prepareStatement(SQLphrase);
                            rs = ps.executeQuery();
                            while (rs.next()) {
//                objectLayout myBox = new objectLayout();
//                myBox.loadBoxLayout("{\"picWidth\":\"1920\",\"picHeight\":\"1080\"", "20", "20");
                                jsonBackground = rs.getString("map");//{"FileSysName":"MAP_BG/map1.jpg", "originalName":"map1 - Copia", "ext":".jpg"}
                            }

                        } catch (SQLException ex) {
                            System.out.println(ex.toString());
                        }
                        String jsonString = jsonBackground;
                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject;
                        String filepath = "";
                        String originalName = "";
                        String ext = "";
                        {
                            try {
                                jsonObject = (JSONObject) jsonParser.parse(jsonString);
                                try {
                                    FileSysName = jsonObject.get("FileSysName").toString();
                                } catch (Exception e) {
                                }
                                try {
                                    originalName = jsonObject.get("originalName").toString();
                                } catch (Exception e) {
                                }
                                try {
                                    ext = jsonObject.get("ext").toString();
                                } catch (Exception e) {
                                }
                            } catch (ParseException ex) {
                                System.out.println(ex);
                            }
                        }
                        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
                        String localWebserverBaseURL = myManager.getDirective("localWebserverBaseURL");
                        UUID idOne = null;
                        idOne = UUID.randomUUID();
                        String newRandom = "?rnd=" + idOne;
//                        String URLserver = "http://192.168.5.100/queenproFileFolder/";
                        String URLserver = localWebserverBaseURL;
                        if (myParams.getCKcontextID() != null && myParams.getCKcontextID().length() > 0) {
                            URLserver += myParams.getCKcontextID() + "/";
                        }
                        URLserver += FileSysName + newRandom;

                        BGgroupHead = "<g  id=\"layer1\"\n"
                                + "     style=\"display:inline\"\n"
                                + ">\n"
                                + "<image  xlink:href=\"" + URLserver + "\"\n"
                                + "width=\"1920\" height=\"1080\" "
                                + "id=\"backImage\" "
                                + "transform=\"translate(0,0)\""
                                + "  />\n"
                                + "</g>";
                    }// </editor-fold>   
                    //----------------------------------------------------------   
                    // <editor-fold defaultstate="collapsed" desc="POSTERS">
                    else if (myForm.getObjects().get(oo).CG.Type.equalsIgnoreCase("POSTERS")) {
                        System.out.println("OGGETTO " + oo + ") .Content.Type:" + myForm.getObjects().get(oo).CG.Type);
                        try {
                            String ObjParams = "";
                            query = myForm.getObjects().get(oo).Origin.query;
                            SQLphrase = myCRUD.standardReplace(query, null);
                            System.out.println("\n SVGposters>>> SQLphrase: " + SQLphrase);
                            ps = conny.prepareStatement(SQLphrase);
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                gaiaMapPoster myPoster = new gaiaMapPoster();
                                myPoster.formObjectName = myForm.getObjects().get(oo).getID();
                                try {
                                    myPoster.description = rs.getString("description");
                                    myPoster.tip = rs.getString("description");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.ID = rs.getInt("ID");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.posX = rs.getString("posX");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.posY = rs.getString("posY");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.posZ = rs.getString("posZ");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.rifDevice = rs.getString("rifDevice");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.rifMap = rs.getString("rifMap");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.rifPeriph = rs.getString("rifOrigin");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.width = rs.getInt("width");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.height = rs.getInt("height");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.value = rs.getInt("value");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.Enabled = rs.getInt("Enabled");
                                } catch (Exception ex) {
                                }
                                try {
                                    myPoster.text = rs.getString("value");
                                } catch (Exception ex) {
                                }

//                                System.out.println("TIP per object:" + myObject.tip
//                                        + " STATUS:" + myObject.value
//                                        + " ENABLED:" + myObject.Enabled
//                                        + " picField:" + picField);
                                //ora ottengo l'immagine
                                String boxClass = "{\"type\":\"picOnly\",\"picWidth\":\"" + myPoster.width + "px\","
                                        + "\"picHeight\":\"" + myPoster.height + "px\"}";
                                objectLayout myBox = new objectLayout();
                                myBox.loadBoxLayout(boxClass, "" + myPoster.width, "" + myPoster.height);

                                //-------------------------------------------
                                String params = myForm.objects.get(oo).getActionParams();
                                if (params == null) {
                                    params = "{}";
                                }
                                String toAdd = ",\"action\":\"" + myForm.objects.get(oo).getActionPerformed() + "\""
                                        + ",\"rifForm\":\"" + myForm.getID() + "\""
                                        + ",\"copyTag\":\"" + myForm.getCopyTag() + "\""
                                        + ",\"fatherForm\":\"" + myForm.getFather() + "\""
                                        + ",\"ges_routineOnLoad\":\"" + myForm.getGes_routineOnLoad() + "\""
                                        + ",\"triggerEvent\":\"click\""
                                        + ",\"rifObj\":\"" + myForm.objects.get(oo).name + "\"";
                                String spotCode = toAdd;
                                spotCode += ",\"keyValue\":\"" + myPoster.rifPeriph + "\"}";

                                ObjParams = params.replace("}", spotCode);
                                myPoster.onClickParams = ObjParams;

                                myMapPosters.add(myPoster);

                            }

                        } catch (SQLException ex) {
                            System.out.println(ex.toString());
                        }
                    }// </editor-fold>  
                }
            }
        }   //fine ciclo per ogni kit di oggetti
        //===============================================================================  
        this.layerBG = BGgroupHead;
        System.out.println("loadObjectsCode");
        this.layerObjects = loadObjectsCode(mode);
        System.out.println("loadRoutinesCode");
        this.layerRoutines = loadRoutinesCode(mode);
        System.out.println("loadPostersCode");
        this.layerPosters = loadPostersCode(mode);

        this.layerPaths = "";
        this.layerRecords = "";
    }

    public void makeMap(gate MapGate, String mode) {
        System.out.println("synopticMap--> table:" + MapGate.getTable());
        System.out.println("synopticMap--> IDmappa:" + MapGate.getKeyValue());
        System.out.println("synopticMap--> TBS:" + MapGate.getTBS());
        ShowItForm myForm = new ShowItForm(MapGate.getFormID(), myParams, mySettings);
        myForm.loadFormSettings();
        myForm.makeQualifiedQuery();
        myForm.buildSchema();
        System.out.println("synopticMap--> getQuery:" + myForm.getQuery());
        System.out.println("synopticMap--> getFormWidth:" + myForm.getFormWidth());
        System.out.println("synopticMap--> getFormHeight:" + myForm.getFormHeight());
        CRUDorder myCRUD = new CRUDorder(myParams, mySettings);
        myCRUD.setSendToCRUD(MapGate.getTBS());
        myCRUD.setPrimaryFieldValue(MapGate.getKeyValue());
//        String query = myCRUD.standardReplace(myForm.getQuery(), null);
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        makeMap(myForm, myCRUD, conny, mode);
    }

    private String loadObjectsCode(String mode) {

        String objectsCode = "";
        for (int jj = 0; jj < myMapObjects.size(); jj++) {
            String tipNow = myMapObjects.get(jj).tip;
            if (tipNow == null || tipNow.length() < 1) {
                tipNow = "" + myMapObjects.get(jj).ID;
            }

//            System.out.println("TIP per object:" + tipNow);
            String curX = "1";
            String curY = "1";
            curX = myMapObjects.get(jj).posX;
            curY = myMapObjects.get(jj).posY;
            int curWidth = 30;
            int curHeight = 30;
            curWidth = myMapObjects.get(jj).width;
            curHeight = myMapObjects.get(jj).height;
//            objectsCode += " <g  id=\"" + myMapObjects.get(jj).ID + "\" style=\"display:inline\">\n";
            if (mode.equalsIgnoreCase("builder")) {
                objectsCode += " <image "
                        //                    + " xlink:href=\"lampON.png\" "
                        + " xlink:href=\"" + myMapObjects.get(jj).actualPic + "\" "
                        + " width=\"" + curWidth + "\" height=\"" + curHeight + "\" "
                        + " transform=\"translate(" + curX + "," + curY + ")\" ";
                objectsCode += "    tip=\"" + tipNow + "\" ";
                objectsCode += " onmouseover='javascript:SVGOBJoverItem(\"" + myMapObjects.get(jj).ID + "\")'";
                objectsCode += " onmouseout='javascript:SVGOBJoutItem(\"" + myMapObjects.get(jj).ID + "\")'";

                objectsCode += " class='draggable ' ";
                objectsCode += " id=\"" + myMapObjects.get(jj).formObjectName + "-" + myMapObjects.get(jj).ID + "\" ";
                objectsCode += "    value=\"" + myMapObjects.get(jj).rifPeriph + "\" ";
                // implementa il draggable e le operazioni al momento del rilascio

            } else if (mode.equalsIgnoreCase("synoptic")) {
//                objectsCode += "<g "
//                        + " id=\"syn-" + myMapObjects.get(jj).rifPeriph + "\" "
//                        + "style=\"width:" + curWidth + "px; height:" + curHeight + "px;\">";

                objectsCode += " <image "
                        //                    + " xlink:href=\"lampON.png\" "
                        + " href=\"" + myMapObjects.get(jj).actualPic + "\" "
                        + " width=\"" + curWidth + "\" height=\"" + curHeight + "\" "
                        + " transform=\"translate(" + curX + "," + curY + ")\" ";
                objectsCode += "    tip=\"" + tipNow + "\" ";
                objectsCode += " onmouseover='javascript:SVGOBJoverItem(\"" + myMapObjects.get(jj).ID + "\")'";
                objectsCode += " onmouseout='javascript:SVGOBJoutItem(\"" + myMapObjects.get(jj).ID + "\")'";

//                objectsCode += " id=\"" + myMapObjects.get(jj).formObjectName + "-" + myMapObjects.get(jj).rifPeriph + "\" ";
                objectsCode += " id=\"syn-" + myMapObjects.get(jj).rifPeriph + "\" ";
//                objectsCode += " name=\"syn-" + myMapObjects.get(jj).rifPeriph + "\" ";
                objectsCode += " value=\"" + myMapObjects.get(jj).ID + "\" ";
                objectsCode += " onclick='javascript:parent.RTonClick( " + myMapObjects.get(jj).onClickParams + " )'";
                objectsCode += " ontouchend='javascript:parent.RTonClick( " + myMapObjects.get(jj).onClickParams + " )'";
            }

            objectsCode += "    />\n";
//            objectsCode += "  </g> ";
        }

        return objectsCode;
    }

    private String loadRoutinesCode(String mode) {
        System.out.println("loadRoutinesCode: " + mode);
        String objectsCode = "";
        for (int jj = 0; jj < myMapRoutines.size(); jj++) {
            String tipNow = myMapRoutines.get(jj).description;
            if (tipNow == null || tipNow.length() < 1) {
                tipNow = "" + myMapRoutines.get(jj).ID;
            }

//            System.out.println("TIP per object:" + tipNow);
            String curX = "1";
            String curY = "1";
            curX = myMapRoutines.get(jj).posX;
            curY = myMapRoutines.get(jj).posY;

            float cX = 0;
            float cY = 0;
            try {
                cX = Float.parseFloat(curX);
                cY = Float.parseFloat(curY);
            } catch (Exception e) {
            }

            int curWidth = 30;
            int curHeight = 30;
            curWidth = myMapRoutines.get(jj).width;
            curHeight = myMapRoutines.get(jj).height;
//            objectsCode += " <g  id=\"" + myMapRoutines.get(jj).ID + "\" style=\"display:inline\">\n";
            if (mode.equalsIgnoreCase("builder")) {
                objectsCode += " <image "
                        //                    + " xlink:href=\"lampON.png\" "
                        + " xlink:href=\"" + myMapRoutines.get(jj).actualPic + "\" "
                        + " width=\"" + curWidth + "\" height=\"" + curHeight + "\" "
                        + " transform=\"translate(" + curX + "," + curY + ")\" ";
                objectsCode += "    tip=\"" + tipNow + "\" ";
                objectsCode += " onmouseover='javascript:SVGOBJoverItem(\"" + myMapRoutines.get(jj).ID + "\")'";
                objectsCode += " onmouseout='javascript:SVGOBJoutItem(\"" + myMapRoutines.get(jj).ID + "\")'";

                objectsCode += " class='draggable routine ' ";
                objectsCode += " id=\"" + myMapRoutines.get(jj).formObjectName + "-" + myMapRoutines.get(jj).ID + "\" ";
                objectsCode += "    value=\"" + myMapRoutines.get(jj).ID + "\" ";
                objectsCode += "    />\n";

            } else if (mode.equalsIgnoreCase("synoptic")) {
                objectsCode += " <image "
                        + " href=\"" + myMapRoutines.get(jj).actualPic + "\" "
                        + " width=\"" + curWidth + "\" height=\"" + curHeight + "\" "
                        + " transform=\"translate(" + curX + "," + curY + ")\" ";
                objectsCode += "    tip=\"" + tipNow + "\" ";
                objectsCode += " onmouseover='javascript:SVGOBJoverItem(\"" + myMapRoutines.get(jj).ID + "\")'";
                objectsCode += " onmouseout='javascript:SVGOBJoutItem(\"" + myMapRoutines.get(jj).ID + "\")'";
                objectsCode += " id=\"syn-ROUTINE-" + myMapRoutines.get(jj).rifPeriph + "\" ";
                objectsCode += " onclick='javascript:parent.RTonClick( " + myMapRoutines.get(jj).onClickParams + " )'";
                objectsCode += " ontouchend='javascript:parent.RTonClick( " + myMapRoutines.get(jj).onClickParams + " )'";
                objectsCode += "    />\n";

                int textX = (int) (curWidth + cX);
                int textY = (int) (cY);
                objectsCode += "<text "
                        + " x='" + textX + "', y='" + textY + "')\" "
                        + "  fill=\"red\">" + myMapRoutines.get(jj).description + "</text>";
            }

            System.out.println("routinesCode:" + objectsCode);
        }

        return objectsCode;
    }

    private String loadPostersCode(String mode) {

        String objectsCode = "";
        for (int jj = 0; jj < myMapPosters.size(); jj++) {
            String tipNow = myMapPosters.get(jj).tip;
            if (tipNow == null || tipNow.length() < 1) {
                tipNow = "" + myMapPosters.get(jj).ID;
            }

//            System.out.println("TIP per object:" + tipNow);
            String curX = "1";
            String curY = "1";
            curX = myMapPosters.get(jj).posX;
            curY = myMapPosters.get(jj).posY;
            int curWidth = 30;
            int curHeight = 30;
            curWidth = myMapPosters.get(jj).width;
            curHeight = myMapPosters.get(jj).height;
//            objectsCode += " <g  id=\"" + myMapPosters.get(jj).ID + "\" style=\"display:inline\">\n";
            if (mode.equalsIgnoreCase("builder")) {
                objectsCode += ""
                        + "  <foreignObject x=\"" + curX + "\" y=\"" + curY + "\" "
                        + " width=\"" + curWidth + "\" height=\"" + curHeight + "\" "
                        + "  ";

                objectsCode += "    tip=\"" + tipNow + "\" ";
                objectsCode += " onmouseover='javascript:SVGPOSTERoverItem(\"" + myMapPosters.get(jj).ID + "\")'";
                objectsCode += " onmouseout='javascript:SVGPOSTERoutItem(\"" + myMapPosters.get(jj).ID + "\")'";

                objectsCode += " class='draggable poster ' ";
                objectsCode += " id=\"" + myMapPosters.get(jj).formObjectName + "-" + myMapPosters.get(jj).ID + "\" ";
                objectsCode += "    value=\"" + myMapPosters.get(jj).rifPeriph + "\" ";

                // implementa il draggable e le operazioni al momento del rilascio
            } else if (mode.equalsIgnoreCase("synoptic")) {
                objectsCode += ""
                        + "  <foreignObject x=\"" + curX + "\" y=\"" + curY + "\" "
                        + " width=\"" + curWidth + "\" height=\"" + curHeight + "\" "
                        + "  ";

                objectsCode += "    tip=\"" + tipNow + "\" ";
                objectsCode += " onmouseover='javascript:SVGPOSTERoverItem(\"" + myMapPosters.get(jj).ID + "\")'";
                objectsCode += " onmouseout='javascript:SVGPOSTERoutItem(\"" + myMapPosters.get(jj).ID + "\")'";
                objectsCode += " id=\"syn-" + myMapPosters.get(jj).rifPeriph + "\" ";
                objectsCode += " value=\"" + myMapPosters.get(jj).ID + "\" ";
                objectsCode += " onclick='javascript:parent.RTonClick( " + myMapPosters.get(jj).onClickParams + " )'";

            }

            objectsCode += ">\n";
            objectsCode += "    <div style=\" border: 1px solid black; display:block;\""
                    + " xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                    + myMapPosters.get(jj).text
                    + "    </div>\n"
                    + "  </foreignObject>";
//            objectsCode += "  </g> ";
        }
        System.out.println("postersCode:" + objectsCode);
        return objectsCode;
    }

    class gaiaMapBGpic {

        String file;
        String header;

    }

    class gaiaMapPath {

        int ID;
        String rifObj;
        String pathID;
        String draw;
        String style;
        String tip;
        String onClickParams;
        String type;
        String group;
        String fill;
        String opacity;
        String stroke;

        String tipOverride;
        String fillOverride;

    }

    class gaiaMapObject {

        int ID;
        String rifPeriph;
        String rifMap;
        String description;
        String posX;
        String posY;
        String posZ;
        int width;
        int height;
        String rifDevice;
        String onClickParams;
        String tip;
        String actualPic;
        int value;
        int Enabled;
        String formObjectName;

    }

    class gaiaMapRoutine {

        int ID;
        String rifPeriph;
        String rifMap;
        String description;
        String posX;
        String posY;
        String posZ;
        int width;
        int height;
        String rifDevice;
        String onClickParams;
        String tip;
        String actualPic;
        int value;
        int Enabled;
        String formObjectName;
        String routineName;

    }

    class gaiaMapPoster {

        int ID;
        String text;
        String rifPeriph;
        String rifMap;
        String description;
        String posX;
        String posY;
        String posZ;
        int width;
        int height;
        String rifDevice;
        String onClickParams;
        String tip;
        String actualPic;
        int value;
        int Enabled;
        String formObjectName;

    }

    public String getRowImageHtmlCode(BufferedImage image, String alternativeString, objectLayout myBox, boolean closed) {

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

            BufferedImage Rimage = makeRoundedCorner(image, radio);
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

        if (closed == true) {
            picCode += "<image src='data:image/gif;base64," + imageString + "' ";
            picCode += " alt='" + alternativeString + "' "
                    + " width='" + myBox.getWidth() + "' "
                    + " heigth='" + myBox.getHeight() + "px' ";
            picCode += " />";
        } else {
            picCode += "data:image/gif;base64," + imageString + " ";
        }
//        System.out.println("picCode:\n" + picCode);
        return picCode;
    }

}
