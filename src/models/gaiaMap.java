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
import REVOpager.EVOpagerDBconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import showIt.ShowItForm;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class gaiaMap {

    EVOpagerParams myParams;
    Settings mySettings;
    gaiaMapBGpic myBGpic;
    ArrayList<gaiaMapPath> myPaths;
    String mapID;
    String rifFather;
    String BGgroupHead;
    String PATHSgroupHead;

    public gaiaMap(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        myBGpic = new gaiaMapBGpic();
        myPaths = new ArrayList();
    }

    public String prepareCode(gate MapGate) {

        System.out.println("table:" + MapGate.getTable());
        System.out.println("IDmappa:" + MapGate.getKeyValue());
        System.out.println("TBS:" + MapGate.getTBS());
        ShowItForm myForm = new ShowItForm(MapGate.getFormID(), myParams, mySettings);
        myForm.loadFormSettings();
        myForm.makeQualifiedQuery();
        myForm.buildSchema();
        System.out.println("getQuery:" + myForm.getQuery());
        System.out.println("getFormWidth:" + myForm.getFormWidth());
        System.out.println("getFormHeight:" + myForm.getFormHeight());
        CRUDorder myCRUD = new CRUDorder(myParams, mySettings);
        myCRUD.setSendToCRUD(MapGate.getTBS());
        myCRUD.setPrimaryFieldValue(MapGate.getKeyValue());
        String query = myCRUD.standardReplace(myForm.getQuery(), null);
        System.out.println("query MAPPA :" + query);
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();

        String SQLphrase = "";
        //SQLphrase = "SELECT * FROM SVGmaps WHERE ID = '" + MapGate.getKeyValue() + "'";

        PreparedStatement ps = null;
        ResultSet rs;
        String BGgroupHead = "";
        String PATHSgroupHead = "";
        String PATHSqueryHead = "";
        String mapID = "";
//------------------------------------------------------------------------------------------------
        try {
            SQLphrase = query;
            System.out.println("\n SVGmaps>>> SQLphrase: " + SQLphrase);
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {

                try {
                    BGgroupHead = rs.getString("BGgroupHead");
                } catch (Exception ex) {
                }
                try {
                    mapID = rs.getString("ID");
                } catch (Exception ex) {
                }
                try {
                    PATHSgroupHead = rs.getString("PATHSgroupHead");
                } catch (Exception ex) {
                }
                try {
                    PATHSqueryHead = rs.getString("PATHSqueryHead");
                } catch (Exception ex) {
                }
                break;
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        if (PATHSqueryHead == null || PATHSqueryHead.length() < 5) {
            PATHSqueryHead = "SELECT * FROM SVGpaths ";
        }

        String recordsCode = "";
//------------------------------------------------------------------------------------------------        

//
//            if (myForm.objects.get(jj).getActionPerformed() != null
//                    && myForm.objects.get(jj).getActionPerformed().equalsIgnoreCase("OpenSecForm")) {
//                // se devo aprire un form, devo ricavare la getGes_routineOnLoad di quel form
//            }
//            System.out.println("\nTrovato oggetto type :" + myForm.objects.get(jj).C.Type);
//------------------------------------------------------------------------------------------------
        try {
            String ObjParams = "";
            SQLphrase = PATHSqueryHead + " WHERE SVGpaths.rifMap = '" + mapID + "'";
            System.out.println("\n SVGpaths>>> SQLphrase: " + SQLphrase);
            ps = conny.prepareStatement(SQLphrase);
            rs = ps.executeQuery();
            while (rs.next()) {
                gaiaMapPath myPath = new gaiaMapPath();
                try {
                    myPath.draw = rs.getString("draw");
                } catch (Exception ex) {
                }
                try {
                    myPath.tip = rs.getString("tip");
                } catch (Exception ex) {
                }
                try {
                    myPath.pathID = rs.getString("pathID");
                } catch (Exception ex) {
                }
                try {
                    myPath.ID = rs.getInt("ID");
                } catch (Exception ex) {
                }
                try {
                    myPath.style = rs.getString("style");
                } catch (Exception ex) {
                }
                try {
                    myPath.rifObj = rs.getString("rifObj");
                } catch (Exception ex) {
                }
                try {
                    myPath.type = rs.getString("type");
                } catch (Exception ex) {
                }
                try {
                    myPath.group = rs.getString("group");
                } catch (Exception ex) {
                }
                try {
                    myPath.fill = rs.getString("fill");
                } catch (Exception ex) {
                }
                try {
                    myPath.opacity = rs.getString("opacity");
                } catch (Exception ex) {
                }
                try {
                    myPath.stroke = rs.getString("stroke");
                } catch (Exception ex) {
                }
                try {
                    myPath.tipOverride = rs.getString("tipOverride");
                } catch (Exception ex) {
                }
                try {
                    myPath.fillOverride = rs.getString("fillOverride");
                } catch (Exception ex) {
                }

                for (int jj = 0; jj < myForm.objects.size(); jj++) {

                    String params = myForm.objects.get(jj).getActionParams();
                    //  System.out.println("\n\nSensLab " + curObj.name + " getActionParams:" + params);

                    if (params == null) {
                        params = "{}";
                    }

                    if (myForm.objects.get(jj).C.Type.equalsIgnoreCase("SVGpath")) {

                        String toAdd = ",\"action\":\"" + myForm.objects.get(jj).getActionPerformed() + "\""
                                + ",\"rifForm\":\"" + myForm.getID() + "\""
                                + ",\"copyTag\":\"" + myForm.getCopyTag() + "\""
                                + ",\"fatherForm\":\"" + myForm.getFather() + "\""
                                + ",\"ges_routineOnLoad\":\"" + myForm.getGes_routineOnLoad() + "\""
                                + ",\"rifObj\":\"" + myForm.objects.get(jj).name + "\"";
//                        + ",\"keyValue\":\"" + KEYvalue + "\"}";
//                params = params.replace("}", toAdd);
                        /*               htmlCode += " onclick='javascript:clickedObject( " + params + " )'";
                //params.replace("\"","'")

                htmlCode += " ";
                htmlCode += "> ";

                         */
                        System.out.println("nrifObj:" + myForm.objects.get(jj).name);
                        System.out.println("\tgetActionParams:" + myForm.objects.get(jj).getActionParams());
                        System.out.println("\tgetActionPerformed:" + myForm.objects.get(jj).getActionPerformed());
                        System.out.println("\trifForm:" + myForm.getID());
                        System.out.println("\tcopyTag:" + myForm.getCopyTag());
                        System.out.println("\tfatherForm:" + myForm.getFather());
                        System.out.println("\tgetGes_routineOnLoad:" + myForm.getGes_routineOnLoad());
                        System.out.println("\trifForm:" + myForm.getID());
                        System.out.println("\tcopyTag:" + myForm.getCopyTag());

                        //------------------------------
                        String spotCode = toAdd;
                        spotCode += ",\"keyValue\":\"" + myPath.rifObj + "\"}";

                        ObjParams = params.replace("}", spotCode);
                        myPath.onClickParams = ObjParams;

                        //--------------------------------
                    } else {
                        System.out.println("OGGETTI NON AGGIUNGONO PATHS ");

                        recordsCode += "<INPUT type=\"HIDDEN\" id=\"" + myForm.getID() + "-" + myForm.getCopyTag() + "-" + myForm.objects.get(jj).name + "-" + myPath.rifObj + "\" value=\"" + myPath.rifObj + "\"/>";

                    }

                }// chiude per ogni oggetto        

                myPaths.add(myPath);

            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        System.out.println("TROVATI " + myPaths.size() + " PATHS");

//------------------------------------------------------------------------------------------------
//        
        try {
            conny.close();
        } catch (SQLException ex) {
            Logger.getLogger(gaiaMap.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("BGgroupHead:" + BGgroupHead);
        System.out.println("PATHSgroupHead:" + PATHSgroupHead);

        //-----------------------------------------------------------------
        String htmlCode = "";
        htmlCode += "<!DOCTYPE html>\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "<head>\n"
                + "<title>MAP1</title>\n"
                + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n"
                + " \n"
                + "</head>\n"
                + "<body onload=\"startup()\">\n"
                + "<div id=\"msg\"></div>\n"
                + "<p>\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"external/mapHandler/mainx.css\" />\n"
                + "\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "\n"
                + " <div id=\"container\" "
                + "style=\"width: " + myForm.getFormWidth() + "; height: " + myForm.getFormHeight() + "; "
                //                + "border:1px solid black; " 
                + "border:0px; "
                + "\">\n"
                + ""
                + "<svg   \n"
                + "	xmlns=\"http://www.w3.org/2000/svg\"\n"
                + "	style=\"display: inline; \n"
                + "	width: inherit; \n"
                + "	min-width: inherit; \n"
                + "	max-width: inherit;    \n"
                + "	height: inherit; \n"
                + "	min-height: inherit; \n"
                + "	max-height: inherit; border:5px solid black; \" \n"
                + "	version=\"1.1\" id=\"qp-map\"\n"
                + "   >\n"
                + "  \n";

        htmlCode += BGgroupHead;
        htmlCode += PATHSgroupHead;

        String pathsCode = "";
//                 pathsCode +=  "<g id=\"pathsGroup\"> ";
        for (int jj = 0; jj < myPaths.size(); jj++) {
            //"fill:#ffffff;stroke:#000000;stroke-width:0.97885245px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"

            String fillNow = myPaths.get(jj).fill;
            if (myPaths.get(jj).fillOverride != null && myPaths.get(jj).fillOverride.length() > 0) {
                fillNow = myPaths.get(jj).fillOverride;
            }

            String tipNow = myPaths.get(jj).tip;
            if (tipNow == null || tipNow.length() < 1) {
                tipNow = "" + myPaths.get(jj).ID;
            }
            if (myPaths.get(jj).tipOverride != null && myPaths.get(jj).tipOverride.length() > 0) {
                tipNow = myPaths.get(jj).tipOverride;
            }

            String styleNow = "";
            if (fillNow != null && fillNow.length() > 0) {
                styleNow += "fill:" + fillNow + ";";
            }
            if (myPaths.get(jj).opacity != null && myPaths.get(jj).opacity.length() > 0) {
                styleNow += "opacity:" + myPaths.get(jj).opacity + ";";
            }
            if (myPaths.get(jj).stroke != null && myPaths.get(jj).stroke.length() > 0) {
                styleNow += "stroke:" + myPaths.get(jj).stroke + ";";
            }

            pathsCode += " \n<path ";
            pathsCode += "   style=\"" + styleNow + "\" ";
            pathsCode += "    d=\"" + myPaths.get(jj).draw + "\" ";
            pathsCode += "    id=\"" + myPaths.get(jj).rifObj + "\" ";
//            pathsCode += "    aria-placeholder=\"#00ff00\" ";    
            pathsCode += "    tip=\"" + tipNow + "\" ";
            pathsCode += "    value=\"" + myPaths.get(jj).ID + "\" ";
            pathsCode += " onclick='javascript:parent.clickedObject( " + myPaths.get(jj).onClickParams + " )'";
            pathsCode += " onmouseover='javascript:overItem( \"" + myPaths.get(jj).rifObj + "\" )'";
            pathsCode += " onmouseout='javascript:outItem( \"" + myPaths.get(jj).rifObj + "\" )'";

            pathsCode += "    />\n";
        }
        pathsCode += "  </g> ";
        htmlCode += pathsCode;
        htmlCode += recordsCode;
        htmlCode += "</svg>\n"
                + "</div>\n"
                + "</p>\n"
                + "\n"
                + "<div id=\"info-box\"></div>\n"
                + "<script src=\"https://code.jquery.com/jquery-2.2.4.min.js\"></script>\n"
                + "<script src=\"external/mapHandler/main.js\"></script>\n"
                + "<script src=\"external/mapHandler/svg-pan-zoom.js\"></script>\n"
                + "     <script>\n"
                + "function highlight(pathID){"
                //                + "console.log(\"highlight PATH \"+ pathID);\n"
                + "var pth = document.getElementById( pathID);"
                //                + "console.log(\"PATH \"+ pth.value);\n"
                + "pth.style.opacity=\"0.9\";   \n"
                //                + "pth.style.fill=\"#00ff00\";\n"
                + "pth.onclick();"
                + "}"
                + "function overItem(pathID){"
                //                + "console.log(\"over PATH \"+ pathID);\n"
                + "var pth = document.getElementById( pathID);"
                + "pth.style.opacity=\"0.9\";   \n"
                //                + "pth.style.fill=\"#ff0000\";\n"
                + "}"
                + "function outItem(pathID){"
                //                + "console.log(\"out PATH \"+ pathID);\n"
                + "var pth = document.getElementById( pathID);"
                //                + "console.log(\"aria-placeholder: \"+ pth.getAttribute(\"aria-placeholder\"));\n"
                + "pth.style.opacity=\"0.0\";   \n"
                //+ "pth.style.fill=\"#ff0000\";\n"
                + "}"
                //////////                + "function makeCircle(){"
                //////////                + "    // create a circle\n"
                //////////                + "    const cir1 = document.createElementNS(\"http://www.w3.org/2000/svg\", \"path\");\n"
                //////////            + "    cir1.setAttribute(\"d\", \"m 437.30458,56.135768 -0.86253,43.126684 -14.66307,-0.862534 20.7008,19.838272 18.11321,-17.25067 -14.66307,-1.725068 1.72507,-43.126684 z\");\n"
                //////////                + "    cir1.setAttribute(\"id\", \"iconPoint\");\n"
                //////////                //                + "\n"
                //////////                //                + "    // attach it to the container\n"
                //////////                //                + "    svg1.appendChild(cir1);\n"
                //////////                //                + "\n"
                //////////                + "    // attach container to document\n"
                //////////                + "    document. getElementById(\"layer2\").appendChild(cir1);"
                //////////                + "initialize();"
                //////////                + ""
                //////////                + "}"
                //////////                + ""
                //////////                + ""
                //////////                + "function pressButt(buttonID){"
                //////////                + "console.log(\"PRESSED PATH \"+ buttonID);\n"
                //////////                + "makeCircle();"
                //////////                + "}"
                + "function startup(){"
                //                + "initialize();"
                + "}"
                + "function initialize(){"
                + "console.log(\"initizlize ptz !\");\n"
                + "        // Expose to window namespase for testing purposes\n"
                + "        window.zoomTiger = svgPanZoom('#qp-map', {\n"
                + "          zoomEnabled: true,\n"
                + "          controlIconsEnabled: true,\n"
                + "          fit: true,\n"
                + "          center: true,\n"
                + "        });\n"
                //                + "		document.getElementById(\"msg\").innerHTML=\"___\";\n"
                + "		var ua = navigator.userAgent.toLowerCase();\n"
                + "		var isAndroid = ua.indexOf(\"android\") > -1; //&& ua.indexOf(\"mobile\");\n"
                + "		if(isAndroid) {\n"
                + "			document.getElementById(\"msg\").innerHTML=\"ANDROID DETECTED\";\n"
                + "			document.addEventListener('touchstart', handleTouch);\n"
                + "			document.addEventListener('touchend', handleTouchEnd);\n"
                + "		}\n"
                + "\n"
                + "}"
                + "     </script>\n"
                + "     <script>\n"
                + "   window.onload = initialize();"
                //                + "      window.onload = function() {\n"
                //                + "        // Expose to window namespase for testing purposes\n"
                //                + "        window.zoomTiger = svgPanZoom('#qp-map', {\n"
                //                + "          zoomEnabled: true,\n"
                //                + "          controlIconsEnabled: true,\n"
                //                + "          fit: true,\n"
                //                + "          center: true,\n"
                //                + "        });\n"
                //                //                + "		document.getElementById(\"msg\").innerHTML=\"___\";\n"
                //                + "		var ua = navigator.userAgent.toLowerCase();\n"
                //                + "		var isAndroid = ua.indexOf(\"android\") > -1; //&& ua.indexOf(\"mobile\");\n"
                //                + "		if(isAndroid) {\n"
                //                + "			document.getElementById(\"msg\").innerHTML=\"ANDROID DETECTED\";\n"
                //                + "			document.addEventListener('touchstart', handleTouch);\n"
                //                + "			document.addEventListener('touchend', handleTouchEnd);\n"
                //                + "		}\n"
                //                + "\n"
                //                + "      };\n"
                + "		function handleTouch(event) {\n"
                + "	   document.getElementById(\"msg\").innerHTML= \"\";\n"
                + "		   if (event.target==\"[object SVGPathElement]\"){\n"
                //                + "			   document.getElementById(\"msg\").innerHTML= event.target.id;\n"
                + "				document.getElementById( event.target.id).style.opacity=\"0.8\";   \n"
                + "		   }\n"
                + "		} \n"
                + "		\n"
                + "	function handleTouchEnd(event) {\n"
                + "	   document.getElementById(\"msg\").innerHTML= \"\";\n"
                + "		   if (event.target==\"[object SVGPathElement]\"){\n"
                //                + "			   document.getElementById(\"msg\").innerHTML= event.target.id;\n"
                + "				document.getElementById( event.target.id).style.opacity=\"0.1\";   \n"
                + "		   }\n"
                + "		} \n"
                + "    </script>\n"
                + "</body>\n"
                + "</html> ";

        return htmlCode;
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
}
