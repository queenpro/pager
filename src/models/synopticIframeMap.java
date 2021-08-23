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
import showIt.ShowItForm;
import java.util.ArrayList;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import REVOpager.EVOpagerDBconnection;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class synopticIframeMap {

    EVOpagerParams myParams;
    Settings mySettings;
    gaiaMapBGpic myBGpic;
    ArrayList<gaiaMapPath> myPaths;
    String mapID;
    String rifFather;
    String BGgroupHead;
    String PATHSgroupHead;

    public synopticIframeMap(EVOpagerParams myParams, Settings mySettings) {
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

        SVGform mySVG = new SVGform(myParams, mySettings);
        mySVG.makeMap(MapGate, "synoptic");
        //-----------------------------------------------------------------
        String htmlCode = "";
        htmlCode += "<!DOCTYPE html>\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "<head>\n"
                + "<title>MAP1 iFrame synoptic</title>\n"
                + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n"
                + " \n"
                + "</head>\n"
                + "<body onload=\"startup()\">\n"
                + "<div id=\"msg\"></div>\n"
                + "<p>\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"external/mapHandler/mainx.css\" />\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
////////                + " <div id=\"container\" "
////////                + "style=\"width: " + myForm.getFormWidth() + "; height: " + myForm.getFormHeight() + "; "
////////                //                + "border:1px solid black; " 
////////                + "border:0px; "
////////                + "\">\n"
////////                + "";

  htmlCode += mySVG.getLayersDIVcode();

////////        htmlCode += "<svg   \n"
////////                + "	xmlns=\"http://www.w3.org/2000/svg\"\n"
////////                + "	style=\"display: inline; \n"
////////                + "	width: inherit; \n"
////////                + "	min-width: inherit; \n"
////////                + "	max-width: inherit;    \n"
////////                + "	height: inherit; \n"
////////                + "	min-height: inherit; \n"
////////                + "	max-height: inherit; border:5px solid black; \" \n"
////////                + "	version=\"1.1\" id=\"qp-map\"\n"
////////                + "   >\n"
////////                + "  \n";
////////
////////        htmlCode += mySVG.layerBG;
////////        htmlCode += mySVG.layerObjects;
////////        htmlCode += mySVG.layerPosters;
////////        htmlCode += "</svg>\n";
//        htmlCode += " </object> ";
        htmlCode += "</div>\n"
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
                + "function SVGOBJoverItem(pathID){"
                + "var pth = document.getElementById( pathID);"
                + "console.log(\"OVER ITEM \"+pathID);"
                + "}"
                + "function SVGOBJoutItem(pathID){"
                + "var pth = document.getElementById( pathID);"
                + "}"
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
                + " function repaintIcon(imgID,newValue) {\n"
                + "var a = document.getElementById(\"qp-map\");"
                + "var svgDoc  = (a.contentWindow || a.contentDocument);"
                + "  var objToUpdate =document.getElementById(imgID);"
                + "if (objToUpdate){"
                + "objToUpdate.setAttribute( 'href',newValue);"
                + "}"
                + "} \n"
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
