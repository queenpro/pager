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

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class synopticMapBuilder {

    EVOpagerParams myParams;
    Settings mySettings;
    String mapID;
    String rifFather;

    public synopticMapBuilder(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        System.out.println("\n\n-----------\nCREO synopticMapBuilder");
    }

    public String prepareCode(gate MapGate) {
        SVGform mySVG = new SVGform(myParams, mySettings);
        mySVG.makeMap(MapGate, "builder");
        //-----------------------------------------------------------------
        String htmlCode = "";
        htmlCode += "<!DOCTYPE html>\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "<head>\n";

        htmlCode += "<div id=\"info-box\"></div>\n"
                + "<script>\n"
                + ""
                + "var selectedElement;\n"
                + "var offset;\n"
                + "var svg;\n"
                //----------------------------------------------------------------
                + "  function SVGOBJstartDrag(evt) {\n"
                + " if (evt.target.classList.contains('draggable')) {\n" 
                + "    selectedElement = evt.target;\n"
                + "    offset = getMousePosition(evt);\n"
                + "    var transforms = selectedElement.transform.baseVal;\n"
                + "    if (transforms.length === 0 ||\n"
                + "        transforms.getItem(0).type !== SVGTransform.SVG_TRANSFORM_TRANSLATE) {\n"
                + "      var translate = svg.createSVGTransform();\n"
                + "      translate.setTranslate(0, 0);\n"
                + "      selectedElement.transform.baseVal.insertItemBefore(translate, 0);\n"
                + "    }\n"
                + "    transform = transforms.getItem(0);\n"
                + "    offset.x -= transform.matrix.e;\n"
                + "    offset.y -= transform.matrix.f;\n"
                + "  }"
                + "  }\n"
                //----------------------------------------------------------------
                + "  function SVGOBJdrag(evt) {\n"
                + " if (selectedElement) {\n"
                + "    evt.preventDefault();\n"
                + "    var coord = getMousePosition(evt);\n"
                + "    transform.setTranslate(coord.x - offset.x, coord.y - offset.y);\n"
                + "  }"
                + "  }\n"
                //----------------------------------------------------------------
                + "  function SVGOBJendDrag(evt) {\n"
                + " if (evt.target.classList.contains('draggable')) {\n"
                + "     selectedElement = evt.target;\n"
                + "     var objValue = $(event.target).attr('value');"
                + "     var objID = $(event.target).attr('ID');;"
                + "     var coord = getMousePosition(evt);\n"
                + "     var newX= coord.x - offset.x;"
                + "     var newY= coord.y - offset.y;"
                + "     console.log(\"SPOSTAMENTO di \"+ objID +\" Name:\"+ objValue +\" IN X:\"+newX+\"  Y:\"+newY);"
                + "     evt.target.transform=\"translate(newX,newY)\"   ;"
                + "     selectedElement = null;"
                //****
                + ""
                + "     routine = \"translateObject\";"
                + "     if (evt.target.classList.contains('poster')) {\n"
                + "         routine = \"translatePoster\";"
                + "     }" 
                + "     if (evt.target.classList.contains('routine')) {\n"
                + "         routine = \"translateRoutineButton\";"
                + "     console.log(\"translateRoutineButton\");"
                + "     }"
                + "     rifForm = \"" + MapGate.getFormID() + "\";"
                + "     copyTag = \"X\";"
                + "     position = \"\";"
                + "     triggerEvent = \"\";"
                + "     exitScript = \"\";"
                + "     destDiv = \"\";"
                + "     paramsToSend = \"\";" 
                + "     action = \"translate\";" 
                + "     formToLoad = \"\";"  
                + "     ges_routineOnLoad = \"\";" 
                + "     console.log(\"ACTION EXECUTEROUTINE \"+routine );\n"
                + "        var fatherKEYvalue = objValue;\n"
                + "        var fatherKEYtype = \"TXT\";\n"
                + "     console.log(\"getJTBS per EXECUTEROUTINE: rifForm\"+rifForm+\" - copyTag: \"+copyTag+\" - fatherKEYvalue: \"+fatherKEYvalue );\n"
                + "        var JTBS = getJTBS(newX,newY,0);\n"
                + "     console.log(\"JTBS: \"+JTBS );\n"
                + "        var destDiv = \"CH-\" + rifForm + \"-\" + copyTag + \"-\" + position;\n"
                + "        var xmlhttp;\n"
                + "        if (window.XMLHttpRequest)\n"
                + "        {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "            xmlhttp = new XMLHttpRequest();\n"
                + "        } else\n"
                + "        {// code for IE6, IE5\n"
                + "            xmlhttp = new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "        }\n"
                + "        xmlhttp.onreadystatechange = function ()\n"
                + "        {\n"
                + "            if (xmlhttp.readyState == 4 && xmlhttp.status == 200)\n"
                + "            {\n"
                + "                        lunghezza = xmlhttp.responseText.length - 1;\n"
                + "                        receivedResponse = xmlhttp.responseText.substring(0, lunghezza);\n"
                + "     console.log(\"FEEDBACK: \"+receivedResponse );"
                
                + "         try{"
                + "             var jsonData = JSON.parse(receivedResponse);\n"
                + "             parent.handleResponse(jsonData, rifForm, copyTag, destDiv, JTBS, ges_routineOnLoad);"
                + "         }catch (err){console.log(err);}"
                + "            }\n"
                + "        }\n"
                + "     var params='\"params\":'+ parent.document.getElementById(\"portalParams\").value;"
                + "     var utils='\"responseType\":\"text\"';"
                + "     var connectors='\"connectors\":[{\"door\":\"executeRoutine\","
                + "     \"event\":\"ExecuteRoutine\", "
                + "     \"rifForm\":\"' + rifForm + '\", "
                + "     \"copyTag\":\"' + copyTag + '\", "
                + "     \"loadType\":\"FORMFIRSTLOAD;0;0;1;1\", "
                + "     \"rifObj\":\"' +objID+ '\", "
                + "     \"keyValue\":\"' + objValue + '\", "
                + "     \"action\":\"' + action + '\", "
                + "     \"formToLoad\":\"' + formToLoad + '\", "
                + "     \"position\":\"' + position + '\", "
                + "     \"triggerEvent\":\"' + triggerEvent + '\", "
                + "     \"routine\":\"' + routine + '\", "
                + "     \"exitScript\":\"' +  exitScript + '\", "
                + "     \"destDiv\":\"' + destDiv + '\", "
                + "     \"paramsToSend\":\"' + paramsToSend + '\", "
                + "     \"sendToCRUD\":' +  encodeURIComponent(JTBS) + '"
                + "      }]';"
                + "     var gp='{'+utils+','+params+','+connectors+'}';"
                + "    xmlhttp.open(\"POST\", \"portal\", true);\n"
                + "    xmlhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n"
                + "    xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                //****
                + "     }"
                + "  }"
                //----------------------------------------------------------------
                + "function getMousePosition(evt) {\n"
                + "  var CTM = svg.getScreenCTM();\n"
                + "  return {\n"
                + "    x: (evt.clientX - CTM.e) / CTM.a,\n"
                + "    y: (evt.clientY - CTM.f) / CTM.d\n"
                + "  };\n"
                + "}"
                + "function SVGOBJmakeDraggable(evt){\n"
                + " svg = evt.target;\n"
                + "  svg.addEventListener('mousedown', SVGOBJstartDrag);\n"
                + "  svg.addEventListener('mousemove', SVGOBJdrag);\n"
                + "  svg.addEventListener('mouseup', SVGOBJendDrag);\n"
                + "  svg.addEventListener('mouseleave', SVGOBJendDrag);\n"
                + "}"
                + "function startup(){"
                + "}"
                + "function SVGOBJoverItem(itemID){\n"
                + "var pth = document.getElementById( itemID);\n"
                //                + "pth.style.opacity=\"0.9\";   \n"
                + "}\n"
                + "function SVGOBJoutItem(itemID){\n"
                + "var pth = document.getElementById( itemID);\n"
                //                + "pth.style.opacity=\"0.0\";   \n"
                + "}\n"
                + "function getJTBS(vX,vY,vZ){"
                + "var myStC=\"\";"
                + "  var thisFilter = '{"
                + "\"childType\":\"panelFilter\", "
                + "\"childMarker\":\"X\", "
                + "\"value\":\"' + vX + '\" }';\n"
                + "  if (myStC.length > 0) { myStC = myStC + ', '; }\n"
                + "  myStC = myStC + thisFilter;\n"
                //--------------------------------------------
                + "  var thisFilter = '{"
                + "\"childType\":\"panelFilter\", "
                + "\"childMarker\":\"Y\", "
                + "\"value\":\"' + vY + '\" }';\n"
                + "  if (myStC.length > 0) { myStC = myStC + ', '; }\n"
                + "  myStC = myStC + thisFilter;\n"
                //--------------------------------------------
                + "  var thisFilter = '{"
                + "\"childType\":\"panelFilter\", "
                + "\"childMarker\":\"Z\", "
                + "\"value\":\"' + vZ + '\" }';\n"
                + "  if (myStC.length > 0) { myStC = myStC + ', '; }\n"
                + "  myStC = myStC + thisFilter;\n"
                //--------------------------------------------
                + ""
                + ""
                + ""
                + "    try {\n"
                + "        myStC = myStC.trim();\n"
                + "    } catch (err) {\n"
                + "    }\n"
                + "    myStC = '[' + myStC + ']';\n"
                // + "console.log(\"myStC:\"+ myStC);\n"
                + "    return myStC;\n"
                + "}"
                + ""
                + "</script>\n"
                + "<title>MAP1 synoptic</title>\n"
                + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n"
                + " \n"
                + "</head>\n";

        htmlCode += "<body onload=\"startup()\">\n";
        htmlCode += "<div id=\"msg\"></div>\n"
                + "<p>\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"external/mapHandler/mainx.css\" />\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "\n";
        htmlCode += mySVG.getLayersDIVcode();

        htmlCode += "<script src=\"https://code.jquery.com/jquery-2.2.4.min.js\"></script>\n"
                + "<script src=\"external/mapHandler/main.js\"></script>\n"
                + "<script src=\"external/mapHandler/svg-pan-zoom.js\"></script>\n";
        htmlCode += "</body>\n"
                + "</html> ";

        return htmlCode;
    }
}
