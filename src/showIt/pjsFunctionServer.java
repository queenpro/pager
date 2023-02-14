/*
 * Copyright (C) 2023 Franco
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
package showIt;

import REVOdbManager.EVOpagerDirectivesManager;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;

/**
 *
 * @author Franco
 */
public class pjsFunctionServer {

    EVOpagerParams myParams;
    Settings mySettings;
    boolean showLighthouse;

    public pjsFunctionServer(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
        String hookQPwebsocket = myManager.getDirective("hookQPwebsocket");

        if ((hookQPwebsocket == null
                || hookQPwebsocket.equalsIgnoreCase("null")
                || hookQPwebsocket.length() == 0
                || hookQPwebsocket.equalsIgnoreCase("TRUE"))
                && mySettings.isLighthouseHookable() == true) {
            showLighthouse = true;
        } else {
            showLighthouse = false;
        }
    }
public String getWebsocketJS() {
        String dbCode = "";
        //dbCode = myDirective.getDirective("gaia.function.manageUserActions");
        String WSendpoint = "ws";
        WSendpoint = mySettings.getWSendpoint();

        EVOpagerDirectivesManager myDirective = new EVOpagerDirectivesManager(myParams, mySettings);
        String dbWSE = myDirective.getDirective("WebsocketEndpoint");
        String fullWSEpath = "";
        if (dbWSE != null && dbWSE.length() > 1 && !dbWSE.equalsIgnoreCase("ws")) {
            WSendpoint = dbWSE;
            fullWSEpath = WSendpoint + "";
        } else {
            fullWSEpath = "ws://\" + document.location.host + \"/\" + overallProjectName+ \"/";
        }

        //-------------------------------------------------------------
        // <editor-fold defaultstate="collapsed" desc="openWS">
        dbCode += "function openWS() {\n"
                + "    var fatherServerConnected=0;\n"
                //  + "    wsURI = \"" + mySettings.getWSendpoint() + "://\" + document.location.host + \"/\" + overallProjectName + \"/WS/\" + CKtokenID;\n"
                ////////                + "    wsURI = \"" + WSendpoint + "://\" + document.location.host + \"/\" + overallProjectName + \"/WS/\" + CKtokenID;\n"
                + "    wsURI = \"" + fullWSEpath + "PWS/\" + CKtokenID;\n"
                + "         console.log(\"wsURI:\"+wsURI);"
                //  + "    wsURI = \"wss://queenpro.net:8080/gaiaweb/WS/\" + CKtokenID;\n"
                //+ "    wsURI = \"" + mySettings.getWSendpoint() +"\";\n"

                + "    ws = new WebSocket(wsURI);\n"
                //  + "ws.binaryType = \"arraybuffer\";"

                + "    ws.onopen = function (evt) {\n"
                + "        onOpen(evt)\n"
                + "        mySpan = document.getElementById(\"statusLine\");\n"
                + "        info = \"CONNECTED (\" + wsURI + \")\";\n"
                + "        fatherServerConnected=1;\n"
                //                + "        writeInfo();\n"
                + "    };\n"
                + "    ws.onclose = function (evt) {\n"
                + "         console.log(\"WS: evento chiusura.\");"
                + "        if (WSactive>0){"
                + "         mySpan = document.getElementById(\"statusLine\");\n"
                + "         info = \"DISCONNECTED_ (\" + wsURI + \")\";\n"
                + "         Semaphore(document.getElementById(\"ledLine\"),32,\"red\",info);"
                + "         fatherServerConnected=0;\n"
                + "         setTimeout(function(){openWS()}, 10000);\n"// dopo 10 seconfi tento la riconnessione
                //                + "         writeInfo();\n"
                + "        }"
                + "    };\n"
                + "    ws.onmessage = function (evt) {\n"
                + "        onMessage(evt)\n"
                + "    };\n"
                + "    ws.onerror = function (event) {\n"
                + "        console.log(\"WEBSOCKET Error \", event);\n"
                //                + "        console.log(\"WEBSOCKET Error \", event);\n"
                + "    }\n"
                + "}\n";

        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="openQPWS">
        dbCode += "function openQPWS() {\n"
                + "}\n";

        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="reconnect">
        dbCode += "function reconnect(){\n"
                + "    \n"
                + "}\n"
                + "\n";

        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="Semaphore(target,size,colorn,toolTipText)">
        dbCode += "function Semaphore(target,size,color,toolTipText) {\n"
                + "    ledWall = target;\n"
                + "if (ledWall==null){return;}\n"
                + "if (color==\"red\"){"
                + " shift = 0*size;"
                + "}"
                + "else if (color==\"green\"){"
                + " shift = 1*size;"
                + "}"
                + "else if (color==\"orange\"){"
                + " shift = 2*size;"
                + "}"
                + "else if (color==\"disabled\"){"
                + " shift = 3*size;"
                + "}"
                + " var posX= \"-\"+shift+\"px\"; var posY= \"0px\"; "
                + " ledWall.innerHTML = \"<img id='\" + target.id +\"Pic' src='./media/icons/semaphoreMap\"+size+\".png' "
                + "alt='.' title='\"+toolTipText+\"' "
                + "style=' object-fit: none; object-position: \" + posX+\" \"+posY+\"; width: \"+size+\"px; height: \"+size+\"px;'   >\";"
                + "}"
                + "";
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="onMessage">
        dbCode += "function onMessage(evt) {\n"
                + " Semaphore(document.getElementById(\"ledLine\"),32,\"orange\",\"\");"
                + "    if (typeof evt.data == \"string\") {\n"
                + "        parseText(evt.data);\n"
                + "    } else {\n"
                + "        //  drawImageBinary(evt.data);\n"
                + "    }\n"
                + "    evt = null;\n"
                + "    info = \"CONNECTED (\" + wsURI + \")\";\n"
                + "    Semaphore(document.getElementById(\"ledLine\"),32,\"green\",info);"
                + "}\n"
                + "\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="speak">
        dbCode += "function speak(toberead) {\n"
                + "    var recVoice = new SpeechSynthesisUtterance();\n"
                + "    recVoice.text = toberead;\n"
                + "    recVoice.lang = 'it-IT';\n"
                + "    recVoice.rate = 1.2;\n"
                + "    recVoice.onend = function (event) {\n"
                + "    }\n"
                + "    speechSynthesis.speak(recVoice);\n"
                + "    //recognition.start();\n"
                + "\n"
                + "}\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="parseText">
        dbCode += "function parseText(data) {\n"
                + "     console.log(\"ricevuto-->\"+data );"
                + "     var jsonData = JSON.parse(data);\n"
                + "     type = jsonData.TYPE;\n"
                //                + "     console.log(\"type-->\"+type );"
                + "     if (type && type== \"wsMultiResponse\" && jsonData.commandItems.length>0){"
                //                + "         console.log(\"jsonData.commandItems.length-->\"+jsonData.commandItems.length );"
                + "         for (var i = 0; i < jsonData.commandItems.length; i++) {\n"
                + "             var itemNumber = i+1;"
                + "             console.log(\" ***********PARSING ITEM \"+itemNumber+\" su \"+jsonData.commandItems.length);"
                + "             var item = jsonData.commandItems[i];\n"
                + "             parseCommandItem( JSON.stringify(item));"
                + "         }\n"
                + "     }else{\n"
                + "         parseCommandItem(data);\n"
                + "     }\n"
                + ""
                + "}\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="parseCommandItem">
        dbCode += "function parseCommandItem(JSONitem) {\n"
                + "var myarg;\n"
                + "var msg;\n"
                + "var htmlCode;\n"
                + "    var json = JSON.parse(JSONitem);\n"
                + "    type = json.TYPE;\n"
                + "    sender = json.SENDER;\n"
                + "    payload = json.payload;\n"
                + "if (payload){\n"
                + " var paramj = JSON.stringify(payload);\n"
                + " myarg = JSON.parse(paramj);\n"
                + " msg = myarg.VALUE;\n"
                + " var code = myarg.CODE;\n"
                //                + " console.log(\"code-->\"+code );"
                + " if (code){\n"
                + "     code = decodeURIComponent(code);\n" // dentro code ci sono anche altre info
                + "     console.log(\"code-->\"+code );"
                + "     codejson = JSON.parse(code);\n"
                + "     htmlCode = codejson.htmlCode;\n"
                + "     htmlCode = decodeURIComponent(htmlCode);\n"
                //                + "     console.log(\"htmlCode-->\"+htmlCode );"
                + " }\n"
                + " var action = myarg.ACTION;\n"
                + " var destDiv = myarg.DESTDIV;\n"
                + " var phrase = \" \" + myarg.PHRASE;\n"
                + "}\n"
                + "console.log(\"Ricevuto WS message del tipo : \"+type+\" -> ACTION : \"+action);"
                //                + "console.log(\"payload: \"+payload);"
                // <editor-fold defaultstate="collapsed" desc="time">        
                + "    if (type == \"time\") {\n"
                + "        mySpan = document.getElementById(\"timeGoesHere\");\n"
                + "        info =  msg;\n"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="infoReqest">
                + "    else if (type == \"infoReqest\") {\n"
                + "        mySpan = document.getElementById(\"timeGoesHere\");\n"
                + "        info =  msg;\n"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="tokenAssign">
                + "    else if (type == \"tokenAssign\") {\n"
                + "console.log(\"#RICEVO TOKEN ASSIGN\" );"
                + "        var paramj = JSON.stringify(payload);\n"
                + "        var myarg = JSON.parse(paramj);\n"
                + "\n"
                + "        var clientId = myarg.clientId;\n"
                + "        var sessionID = myarg.sessionID;\n"
                + "        var newToken = myarg.newToken;\n"
                + "        document.getElementById(\"WStoken\").value = newToken;\n"
                + "        document.getElementById(\"WSstatus\").value = \"CONNECTED\";\n"
                + "        document.getElementById(\"WSsessionID\").value = sessionID;\n"
                + "        document.getElementById(\"WSclientId\").value = clientId;\n"
                + "        mySpan = document.getElementById(\"recv\");\n"
                + "        info = \"NEW TOKEN:\" + newToken;\n"
                + "console.log(\"#\"+info);"
                + "        sendHandshake(newToken);\n"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="heartBeat">
                + "    else if (type == \"heartBeat\") {\n"
                + "        info = \"RICEVO HEARTBEAT:\" + document.getElementById(\"WStoken\").value;\n"
                + "console.log(\"#\"+info);"
                + "        sendHandshake(document.getElementById(\"WStoken\").value);\n"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="message">
                + "    else if (type == \"message\") {\n"
                + "        image = null;\n"
                + "         console.log(\"ricevuto per speak: \"+msg);"
                + "toast(\"ricevuto \" + msg + \"da \" + sender,null);"
                //                + "        speak(\"ricevuto \" + msg + \"da \" + sender);\n"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="errlog">
                + "    else if (type == \"errlog\") {\n"
                + "    msg = json.VALUE;\n"
                + "    mySpan = document.getElementById(\"statusLine\");\n"
                + "    info = msg;\n"
                + "    console.log(\"ERROR LOG: \"+msg);\n"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="status">
                + "    else if (type == \"status\") {\n"
                + "    msg = json.VALUE;\n"
                + "        mySpan = document.getElementById(\"statusLine\");\n"
                + "        info = msg;\n"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="lighthouseStatus">
                + "    else if (type == \"lighthouseStatus\") {\n"
                + "    msg = json.VALUE;\n"
                + "        mySpan = document.getElementById(\"lighthouseStatusLine\");\n"
                + "         if (msg && msg=='Lighthouse connesso'){\n"
                + "             Semaphore(document.getElementById(\"LHledLine\"),32,\"green\",\"\");\n"
                + "         }else{\n"
                + "             Semaphore(document.getElementById(\"LHledLine\"),32,\"red\",\"\");\n"
                + "         }\n"
                + "        info = msg;\n"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="backupTest">
                + "    else if (type == \"backupTest\") {\n"
                + "    code = json.CODE;\n"
                //                + "     console.log(\"code: \"+code);"
                + "     info =  \"\";\n"
                + "     document.getElementById(\"backupLine\").innerHTML=code;"
                + "     Semaphore(document.getElementById(\"backupLine\"),16,code,msg);\n"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="refreshContacts">
                + "    else if (type == \"refreshContacts\") {\n"
                + "       populateRightSpace();\n"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="receivedOK">
                + "    else if (type == \"receivedOK\") {\n"
                + "console.log(\"ricevuto OK. \" );"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="RTSYNOPTIC">
                + "    else if (type == \"RTSYNOPTIC\") {\n"
                + " var newHtmlCode=\"\";"
                + " var objName=\"\";"
                + "        rec = decodeURIComponent(msg);\n"
                + "        for (var i = 0; i < payload.update.length; i++) {\n"
                + "console.log (\"----------\");"
                + "             var row =  payload.update[i];\n"
                + "             var direction=\"dIN\";"
                + "             var objToUpdate = document.getElementById(row.ID);"
                + "             try{"
                + "                 direction = row.D;"
                + "                }catch (err){console.log(\"1.\" + err.message);}\n"
                + "             var TAGtype= payload.TAGtype;"
                //--------------------------------------------------------------------------
                + "             if ( TAGtype==\"buttonImage\" && objToUpdate){"
                + "                 console.log(\"N:\"+objName+\"  D:\"+direction+\"  V:\"+row.V+\"  E:\"+row.E+\"   T:\"+row.T+\"   SP:\"+row.SP);"
                + "             console.log(\"row.ID:\"+row.ID+\"      TAGtype:\"+TAGtype+\"      TAGcontent:\"+ row.TAGcontent);"
                + "                 newHtmlCode =\" <img src='data:image/gif;base64,\" + row.TAGcontent + \"' alt='\" + row.V+\"' title='\" + row.V+\"' width='100%' height='100%'>\";\n"
                + "                 objToUpdate.innerHTML = newHtmlCode;"
                //------------------------------------------------------------------
                + "             } else if ( TAGtype==\"svgMapImage\"){\n"
                + "var frame = document.getElementById(\"myMap\");\n"
                + "if (frame==null){console.log(\"frame--->null\");}else{\n"
                + "}"
                + "var cntWin  = (frame.contentWindow || frame.contentDocument);\n"
                + "if (cntWin){\n"
                + "var newVal =  \"data:image/gif;base64,\" + row.TAGcontent ;\n"
                + " cntWin.repaintIcon(row.ID,newVal);\n"
                + "}\n"
                + "             }\n"
                + "if (row.SP && row.SP.length>0 && row.SP!=\"null\"){\n"
                + "speak(row.SP);\n"
                + "}\n"
                + "        }\n"
                + "    }\n" // </editor-fold> 
                //*****************************************************
                // <editor-fold defaultstate="collapsed" desc="wsResponse*********************">
                + "else if (type == \"wsResponse\") {\n"
                /*
CONCETTO IMPORTANTE
tutte le funzioni di seguito sono richieste da una risposta in arrivo dal server
1. le risposte non devono indurre a richiedere altre informazioni, semmai le devono fornire direttamente
2. 
                 */
                //--------------------------------
                // <editor-fold defaultstate="collapsed" desc="ROUTINE_RESPONSE"> 
                + "     if (action == \"ROUTINE_RESPONSE\"){"
                + "         var needs = myarg.NEEDS;\n"//oggetto array con i singoli comandi da eseguire 
                + "         for (var i = 0; i < needs.length; i++) {\n"
                + "           try{\n"
                + "             var myarg = needs[i];\n"
                + "             console.log(\"NEEDS---->action: \"+myarg.action );\n"
                //----------------------------------------------------------------------------------------
                + "             if(myarg.action=='repaintRow'){\n"
                // qui mi limito a inserire nella posizione target il contenuto già fornito
                + "                 var targetPosition = myarg.target;\n"
                //                + "             console.log(\"repaintRow_targetPosition: \"+targetPosition );\n"
                + "                 var htmlCode =   decodeURIComponent(myarg.htmlCode);\n"
                + "                 if (targetPosition && targetPosition.length>0){\n"
                + "                     document.getElementById(targetPosition).innerHTML = htmlCode ;\n"
                //                + "             console.log(\"Aggiornato contenuto riga.\"  );\n"
                + "                 }\n"
                //----------------------------------------------------------------------------------------
                + "             }else if(myarg.action=='repaintMap'){\n"//2022-12-01 
                + "                 var geomapInfos =   myarg.geomapInfos;\n"
                + "                 GM_initGeomap(geomapInfos);\n"
                + "             } "
                //----------------------------------------------------------------------------------------
                + "             else if(myarg.action=='moveMap'){\n"//2022-12-01 
                + "                 var geomapInfos =   myarg.geomapInfos;\n"
                + "                 moveGeomap(geomapInfos);\n"
                + "             } "
                //----------------------------------------------------------------------------------------
                + "             else if(myarg.action=='repaintForm'){\n"//2021-03-18
                + "                 var targetPosition = myarg.target;\n"
                + "                 console.log(\"repaintForm_targetPosition: \"+targetPosition );\n"
                + "                 var htmlCode =   decodeURIComponent(myarg.htmlCode);\n"
                + "                 if (targetPosition && targetPosition.length>0){\n"
                + "                     document.getElementById(targetPosition).outerHTML = htmlCode ;\n"
                + "                 }\n"
                + "             } "
                //----------------------------------------------------------------------------------------
                + "             else if(myarg.action=='synopticUpdate'){\n"//2022-01-28
                + "                 var targetPosition = myarg.target;\n"
                + "                 var myArr=myarg.updates;\n"
                //                + "                 console.log(\"synopticUpdate lnght: \"+myArr.length );\n"
                + "                 for (var i=0; i< myArr.length; i++) {\n"
                + "                     var X = myArr[i];\n"
                + "                     try{document.getElementById(X.n).value = X.v; "
                + "                     }catch(err){"
                + "                         console.log(\"synopticUpdate \"+i+\": \"+X.n+\" NOT FOUND!\" );\n"
                + "                     }"
                //                + "                     console.log(\">> \"+ X.n+\" = \"+ X.v);\n"
                + "                 }\n"
                + "             } "
                //----------------------------------------------------------------------------------------
                + "             else if(myarg.action=='refreshForm'){\n"
                + "                 var target = myarg.target;\n"
                + "                 var copyTag = myarg.copyTag;\n"
                + "                 var targetID = document.getElementById(target+\"-\"+copyTag+\"-ID\").value;\n"
                + "                 var res = targetID.split(\"-\");\n"
                + "                 var destination = \"FORM-\" + target + \"-\" + copyTag;\n"
                + "                 formRefresh(res[0], res[1], target, '',destination);\n" // questo usa AJAX  
                //                + "                 console.log(\"reimposto visibile : \"+target + \"-\" + copyTag + \"-mainBodyTable\");\n"
                //                + "    document.getElementById(target + \"-\" + copyTag + \"-mainBodyTable\").style.display = \"block\";\n"
                //repaintValueByName
                //----------------------------------------------------------------------------------------
                + "             } else if(myarg.action=='repaintObjByName'){\n"
                + "                 console.log(\"sono in repaintObjByName. \" );\n"
                // qui mi limito a inserire nella posizione target il contenuto già fornito
                + "                 var targetPosition = myarg.target;"
                + "                 console.log(\"targetPosition: \"+targetPosition );\n"
                + "                 var htmlCode =   decodeURIComponent(myarg.htmlCode);\n"
                + "                 if (targetPosition && targetPosition.length>0){"
                + "                     document.getElementById(targetPosition).innerHTML = htmlCode ;\n"
                + "                 }"
                //----------------------------------------------------------------------------------------
                + "             }else  if(myarg.action=='clearForm'){"
                + "                 var target = myarg.target;"
                + "                 var targetID = document.getElementById(target+\"-\"+copyTag+\"-ID\").value;"
                + "                 var res = targetID.split(\"-\");"
                + "                 var destination = \"FRAME-\" + target + \"-\" + copyTag;\n"
                + "                 console.log(\"destination: \"+destination );\n"
                + "                 document.getElementById(destination).innerHTML = \"\";\n"
                //----------------------------------------------------------------------------------------
                + "             }else  if(myarg.action=='focusOnRow'){\n"
                + "                 console.log(\"focusOnRow-->myarg.target: \"+myarg.target );\n"
                + "                 var target = myarg.target;"
                + "                 javascript:smartRowSelected(target);"
                //----------------------------------------------------------------------------------------
                + "             }else  if(myarg.action=='clickObject'){\n"
                + "                 console.log(\"clickObject-->myarg.target: \"+myarg.target );\n"
                + "                 var target = myarg.target;"
                + "                 var obj = document.getElementById(myarg.target);"
                + "                 console.log(\"obj.value: \"+ obj.value );\n"
                + "                 obj.onchange();"
                //----------------------------------------------------------------------------------------
                + "             }else  if(myarg.action=='clickButton'){\n"
                + "                 console.log(\"clickObject-->myarg.target: \"+myarg.target );\n"
                + "                 var target = myarg.target;"
                + "                 var obj = document.getElementById(myarg.target);"
                + "                 var target = myarg.target;"
                + "                 obj.onclick();"
                //----------------------------------------------------------------------------------------
                + "             }else  if(myarg.action=='toast'){"
                + "                 var phrase = myarg.phrase;"
                + "                 var speak = myarg.speak;"
                + "                 if (phrase && phrase.length >0){"
                + "                  toast(phrase,speak);\n"
                + "                 }"
                //----------------------------------------------------------------------------------------
                + "             }else  if(myarg.action=='splash'){"
                + "                 var phrase = myarg.phrase;"
                + "                 if (phrase && phrase.length >0){"
                + "                     document.getElementById(\"selectors\").innerHTML = \" \"+myarg.phrase+\" \";\n"
                + "                     scrollTo(\"splashPanel\");\n"
                + "                 }"
                //----------------------------------------------------------------------------------------
                + "             }else  if(myarg.action=='Fdownload'){"
                + "                 var content = myarg.content;"
                + "                 var filename = myarg.filename;"
                + "                 if (content && content.length >0){"
                + "                     Fdownload(filename,content);\n"
                + "                 }"
                //-------------------------------------------------------------------------------------

                + "             }else  if(myarg.action=='synoptic'){"
                + "                 var htmlCode = myarg.htmlCode;"
                + "                 console.log(\"*synoptic-->myarg.target: \"+myarg.target );\n"
                + "                 document.getElementById(myarg.target).innerHTML = htmlCode;\n"
                + "                 "
                //----------------------------------------------------------------------------------------
                + "             }else  if(myarg.action=='STOPsynoptic'){"
                + "                 var htmlCode = myarg.htmlCode;"
                + "                 console.log(\"*STOPsynoptic-->myarg.target: \"+myarg.target );\n"
                + "                 document.getElementById(myarg.target).checked = false;\n"
                + "                 "
                //----------------------------------------------------------------------------------------
                + "             }else  if(myarg.action=='PAINTBRANCH'){"
                + "         console.log(\"PAINTBRANCH\"   );\n"
                + "         var data =  myarg.DATA;\n"
                + "         console.log(\"node: \"+node );\n"
                + "         data = decodeURIComponent(data);\n"
                + "         console.log(\"DATA:\" + data  );\n"
                + "         var jsonData = JSON.parse(data);\n"
                + "         var node= jsonData.node;\n"
                + "         var keyField= jsonData.keyField;\n"
                + "         var values= jsonData.values;\n"
                + "         var objName= jsonData.objName;\n"
                + "         var addEnabled= jsonData.addEnabled;\n"
                + "             console.log(\"node:\" + node  );\n"
                + "                 var res = node.split(\"-\");\n"
                + "         var formID = res[0];\n"
                + "         var copyTag = res[1];\n"
                + "         var keyValue = res[2];\n"
                + "         var newNode = formID+\"-\"+copyTag+\"-\"+keyValue;\n   "
                + "         console.log(\"newNode: \"+newNode);\n"
                + "         console.log(\"objName: \"+objName);\n"
                + "             var destRow = document.getElementById(node) ;\n"
                + "         if (addEnabled==\"true\"){"
                + "                 var ul = document.createElement(\"ul\");\n"
                + "                 ul.setAttribute(\"Id\", newNode+\"-ROWSTABLE\");  \n"
                + "                 var li = document.createElement(\"li\");\n"
                + "                 li.setAttribute(\"Id\", newNode+\"-NEWBUTTON\");  \n"
                + "                 var btn = document.createElement(\"BUTTON\");\n"
                + "                 btn.innerHTML = \"X\";\n"
                + "                 btn.setAttribute(\"onclick\", \"toggleTreeAdder(event,'\"+newNode+\"');\");\n"
                + "                 li.appendChild(btn);\n"
                + "                 ul.appendChild(li);\n"
                + "         }"
                + "             var leafs = values;\n"//oggetto array con i singoli comandi da eseguire 

                + "             console.log(\"values: \" + values.length);"
                + "             for (var i = 0; i < leafs.length; i++) {\n"
                + "                 try{\n"
                + "                 var myarg = leafs[i];\n"
                + "                 var leafID= myarg.ID;\n"
                + "                 var leafValue= myarg.value;\n"
                + "                 var li = createLeaf(leafID,leafValue);\n"
                + "                 ul.appendChild(li);\n"
                + "                 }catch(err){}\n"
                + "             }\n"
                + "        if (ul){ destRow.appendChild(ul);}\n"
                + "                 "
                //----------------------------------------------------------------------------------------
                + "             }else  if(myarg.action=='printReport'){"
                + "                 var token = myarg.token;"
                + "                 console.log(\"printReport: token = \"+token );\n"
                + "                 var params='\"params\":'+ document.getElementById(\"portalParams\").value;\n"
                + "                 var utils='\"responseType\":\"text\"';\n"
                + "                 var connectors='\"connectors\":[{\"door\":\"printPDF\",\"event\":\"printTokenReport\",\"formToLoad\":\"' + token +'\"}]';\n"
                + "                 var reportToLoad = \"reportHandler\";\n"
                + "                 var gp='{'+utils+','+params+','+connectors+'}';\n"
                + "                 console.log(\"Apro servlet: \"+reportToLoad +\" - GP: \"+ gp);\n"
                + "                 var w = window.open(reportToLoad + \"?target=requestsManager&gp=\" + encodeURIComponent(gp), '_blank');\n"
                + "                 w.document.title = 'documento in preparazione...';\n"
                + "             } \n"
                + "             } catch (err) {\n"
                + "                 console.log(\"Errore in elaborazione del need : \"+err );\n"
                + "             }\n"
                + "         }\n"
                + "     }else"
                // </editor-fold> 
                //--------------------------------
                // <editor-fold defaultstate="collapsed" desc="SHOWGROUPSPANEL">           
                + "     if (action == \"SHOWGROUPSPANEL\"){\n"
                + "         var targetElement = myarg.TARGETELEMENT;\n"
                + "             var element = document.getElementById(targetElement);\n"
                + "             var rect = element.getBoundingClientRect();\n"
                + "             var objtop = parseInt(rect.top, 10);"
                + "             var objleft = parseInt(rect.left, 10);"
                + "             var panel1 = document.getElementById(\"dropPanel\");\n"
                + "             panel1.style.top=objtop+\"px\";"
                + "             panel1.style.left=objleft+\"px\";"
                + "             panel1.style.width=\"300px\";"
                + "             panel1.style.height=\"300px\";"
                + "             document.getElementById(\"dropPanelFigcaption\").style.width=\"300px\";\n"
                + "             document.getElementById(\"dropPanelFigcaption\").style.height=\"300px\";\n"
                + "             var rect2 = panel1.getBoundingClientRect();\n"
                + "             document.getElementById(\"dropSpace\").innerHTML = htmlCode;\n"
                + "             scrollTo(\"dropPanel\");\n"
                + "     }else"
                // </editor-fold> 
                //--------------------------------
                // <editor-fold defaultstate="collapsed" desc="LOGOUT">                
                + "         if (action == \"LOGOUT\"){"
                + "         console.log(\"ricevuto LOGOUT. \" );"
                + "         toast(\"SESSIONE CONCLUSA PER TIMEOUT. RIESEGUIRE IL LOGIN.\",1);"
                + "setTimeout(() => {   PagerLogout(); }, 2000);"
                + "       "
                + "     }";// </editor-fold> 
//-------------------------------- 
        if (mySettings.isUsesAudioRec()) {
// <editor-fold defaultstate="collapsed" desc="action=='audioPanel'">   
            dbCode += "             else  if(myarg.action=='audioPanel'){"
                    + "                elements = document.getElementsByClassName(\"secondPanelClass\");\n"
                    + "                 for (var i = 0; i < elements.length; i++) { "
                    + "                     elements[i].innerHTML = \"\"; "
                    + "                 }\n"
                    + "                 var token = myarg.token;"
                    + "                 audioToken = token;"
                    + "                 console.log(\"audioPanel: token = \"+token );\n"
                    + "                 var targetDiv = myarg.targetDiv;"
                    //                    + "                 console.log(\"audioPanel: targetDiv = \"+targetDiv );\n"
                    + "                 ApanelCode= '<div id=\"controls\">';"
                    + "                 ApanelCode+='<button id=\"recordButton\" onclick=\"javascript:startRecording()\">O</button>';"
                    + "                 ApanelCode+='<button id=\"pauseButton\" disabled onclick=\"javascript: pauseRecording()\">||</button>';"
                    + "                 ApanelCode+='<button id=\"stopButton\" disabled onclick=\"javascript:stopRecording()\">X</button>';"
                    + "                 ApanelCode+='</div>';"
                    + "                 ApanelCode+='<div id=\"formats\" style=\"display:none;\"></div>';"
                    //                    + "                 ApanelCode+='<p><strong>Recordings:</strong></p>';"
                    //                    + "                 ApanelCode+='<ol id=\"recordingsList\" style=\"display:none;\"></ol>';"
                    + "                 document.getElementById(targetDiv).innerHTML = ApanelCode;\n"
                    + "}";
//----------------------------------------------------------------------------------------
// </editor-fold> 
        }

        if (mySettings.isUsesTree()) {
            //--------------------------------
// <editor-fold defaultstate="collapsed" desc="POPULATETREE">
            dbCode += "     else if (action == \"POPULATETREE\"){"
                    + "         console.log(\"POPULATETREE\"   );\n"
                    + "         var targetPosition = myarg.DESTDIV;\n"
                    + "         console.log(\"repaintForm_targetPosition: \"+targetPosition );\n"
                    + "         if (destDiv && destDiv.length>0){\n"
                    + "             document.getElementById(destDiv).innerHTML = htmlCode ;\n"
                    + "         }\n"
                    + "         try{"
                    + "         var fW= myArg.W;\n"
                    + "         var fH= myArg.H;\n"
                    + "         }catch(err){}"
                    + "         var data =  myarg.DATA;\n"
                    + "         data = decodeURIComponent(data);\n"
                    + "         console.log(\"DATA:\" + data  );\n"
                    + "         var jsonData = JSON.parse(data);\n"
                    + "         var node= jsonData.node;\n"
                    + "         var objName= jsonData.objName;\n"
                    + "             var formIDCT = node ;"
                    + "             console.log(\"formIDCT:\" + formIDCT  );\n"
                    + "             var ul = document.getElementById(formIDCT + \"-ROOT-ROWSTABLE\") ;\n"
                    + "                 var li = document.createElement(\"li\");\n"
                    + "                 li.setAttribute(\"Id\", node+\"-ROOT-NEWBUTTON\");  \n"
                    + "                 var btn = document.createElement(\"BUTTON\");\n"
                    + "                 btn.innerHTML = \"X\";\n"
                    + "                 btn.setAttribute(\"onclick\", \"toggleTreeAdder(event,'\"+node+\"-ROOT');\");"
                    + "                 li.appendChild(btn);"
                    + "                 ul.appendChild(li);"
                    + "             var leafs = jsonData.values;\n"//oggetto array con i singoli comandi da eseguire 
                    + "             for (var i = 0; i < leafs.length; i++) {\n"
                    + "                 try{\n"
                    + "                 var myarg = leafs[i];\n"
                    + "                 var leafID= myarg.ID;\n"
                    + "                 var leafValue= myarg.value;\n"
                    + "                 var li = createLeaf(leafID,leafValue);"
                    + "                 ul.appendChild(li);"
                    + "                 }catch(err){}"
                    + "             }"
                    + "     }"//* FINE POPULATETREE
                    // </editor-fold> 
                    //--------------------------------
                    // <editor-fold defaultstate="collapsed" desc="PAINTBRANCH">
                    + "     else if (action == \"PAINTBRANCH\"){"
                    + "         console.log(\"PAINTBRANCH\"   );\n"
                    + "         var data =  myarg.DATA;\n"
                    + "         console.log(\"node: \"+node );\n"
                    + "         data = decodeURIComponent(data);\n"
                    + "         console.log(\"DATA:\" + data  );\n"
                    + "         var jsonData = JSON.parse(data);\n"
                    + "         var node= jsonData.node;\n"
                    + "         var keyField= jsonData.keyField;\n"
                    + "         var values= jsonData.values;\n"
                    + "         var objName= jsonData.objName;\n"
                    + "         var addEnabled= jsonData.addEnabled;\n"
                    + "             console.log(\"node:\" + node  );\n"
                    + "                 var res = node.split(\"-\");\n"
                    + "         var formID = res[0];\n"
                    + "         var copyTag = res[1];\n"
                    + "         var keyValue = res[2];\n"
                    + "         var newNode = formID+\"-\"+copyTag+\"-\"+keyValue;\n   "
                    + "         console.log(\"newNode: \"+newNode);\n"
                    + "         console.log(\"objName: \"+objName);\n"
                    + "             var destRow = document.getElementById(node) ;\n"
                    + "         if (addEnabled==\"true\"){"
                    + "                 var ul = document.createElement(\"ul\");\n"
                    + "                 ul.setAttribute(\"Id\", newNode+\"-ROWSTABLE\");  \n"
                    + "                 var li = document.createElement(\"li\");\n"
                    + "                 li.setAttribute(\"Id\", newNode+\"-NEWBUTTON\");  \n"
                    + "                 var btn = document.createElement(\"BUTTON\");\n"
                    + "                 btn.innerHTML = \"X\";\n"
                    + "                 btn.setAttribute(\"onclick\", \"toggleTreeAdder(event,'\"+newNode+\"');\");\n"
                    + "                 li.appendChild(btn);\n"
                    + "                 ul.appendChild(li);\n"
                    + "         }"
                    + "             var leafs = values;\n"//oggetto array con i singoli comandi da eseguire 

                    + "             console.log(\"values: \" + values.length);"
                    + "             for (var i = 0; i < leafs.length; i++) {\n"
                    + "                 try{\n"
                    + "                 var myarg = leafs[i];\n"
                    + "                 var leafID= myarg.ID;\n"
                    + "                 var leafValue= myarg.value;\n"
                    + "                 var li = createLeaf(leafID,leafValue);\n"
                    + "                 ul.appendChild(li);\n"
                    + "                 }catch(err){}\n"
                    + "             }\n"
                    + "        if (ul){ destRow.appendChild(ul);}\n"
                    + "     }"//* FINE PAINTBRANCH
                    // </editor-fold> 
                    //--------------------------------
                    // <editor-fold defaultstate="collapsed" desc="SHOWADDEDLEAF">      
                    + "     if (action == \"SHOWADDEDLEAF\"){"
                    /*
{"code":{"ID":"autom30e1a-X-TOYOTAXXd819c37c-ROW","value":"TOYOTA "},
"sender":"CRUD","newID":"TOYOTAXXd819c37c","routineResponse":"","mess":"",
"afterHour":null,"operation":"ADD"}
                     */
                    //                + "         console.log(\"DEVO MOSTRARE LA RIGA AGGIUNTA\");"
                    + "         CRUDrepsonse = myarg.CRUDrepsonse;"
                    + "         var CRUDrx = decodeURIComponent(CRUDrepsonse);\n"
                    + "         console.log(\"CRUDrx: \"+CRUDrx);"
                    + "         crudArgs = JSON.parse(CRUDrx);\n"
                    + "         var code = crudArgs.code;"
                    + "         var codex = JSON.stringify(code);\n"
                    + "         codeArgs = JSON.parse(codex);\n"
                    + "         operation = crudArgs.operation;\n"//ADD
                    + "         nodeName = crudArgs.nodeName;\n"//ADD
                    + "         fatherNodeName = crudArgs.fatherNodeName;\n"//ADD
                    + "         var leafID = codeArgs.ID;\n"
                    + "         var leafValue = codeArgs.value ;\n"
                    + "         var answerNewID = codeArgs.newID;\n"//"TOYOTAXXd819c37c" 
                    + "         console.log(\"leafID: \"+leafID + \"   -   leafValue: \"+leafValue + \"    -   answerNewID: \"+answerNewID );"
                    + "         myCNT = myarg.CONNECTOR;"
                    + "         var CNTx = JSON.stringify(myCNT);\n"
                    + "         var CNTrx = (CNTx);\n"
                    //                + "         console.log(\"CNTrx: \"+CNTrx);"
                    + "         cntArgs = JSON.parse(CNTrx);\n"
                    + "         var fatherValueBefore = cntArgs.thisFormFather;\n"
                    + "         var cellName = cntArgs.cellName;\n"
                    + "         var thisFormFather=\"\";"
                    + "         try {thisFormFather = document.getElementById(rifForm + \"-FATHER\").value;  } catch (err) {}\n"
                    + "         formName = cntArgs.formName;\n"
                    + "         formID = cntArgs.formID;\n"
                    + "         var copyTag = cntArgs.copyTag;\n"
                    + "         var rifForm = formID+\"-\"+copyTag ;"
                    + "         console.log(\"thisFormFather:\"+thisFormFather+\"  -  rifForm:\"+rifForm +\"  -  formName:\"+formName   );"
                    + "         var fatherValue = \"\";\n"
                    + "         try {\n"
                    + "              if (thisFormFather != \"null\" && thisFormFather != \"\")\n"
                    + "              fatherValue = document.getElementById(thisFormFather + \"-KEYfieldValue\").value;\n"
                    + "         } catch (err) {\n"
                    + "              thisFormFather = \"\";\n"
                    + "         }\n"
                    + "         console.log(\"AGGIUNGO LEAF...\");"
                    + "         var toAnalyze = leafID.split(\"-\");\n"
                    + "         if ( toAnalyze.length >1  ) {\n"
                    + "         formID = toAnalyze[0];\n"
                    + "         copyTag = toAnalyze[1];\n"
                    + "         try{\n"
                    + "         var destinationBranch = document.getElementById(nodeName+\"-ROWSTABLE\");\n" //autom30e1a-X-ROWSTABLE  
                    + "         var li = createLeaf(leafID,leafValue);\n"
                    + "         destinationBranch.appendChild(li);\n"
                    + "         document.getElementById(nodeName+\"-NEW\").value=\"\";\n"
                    + "         }catch(err){}\n"
                    + "     }\n "
                    // adesso cancello il contenuto della casella di inserimento

                    + "     }\n";//* FINE SHOWADDEDLEAF
// </editor-fold>     
//--------------------------------
        }

// <editor-fold defaultstate="collapsed" desc="SHOWADDEDROW">
        dbCode += "    else if (action == \"SHOWADDEDROW\"){"
                //                + "         console.log(\"DEVO MOSTRARE LA RIGA AGGIUNTA\");"
                + "         CRUDrepsonse = myarg.CRUDrepsonse;"
                + "         var CRUDrx = decodeURIComponent(CRUDrepsonse);\n"
                //                + "         console.log(\"CRUDrx: \"+CRUDrx);"
                + "         crudArgs = JSON.parse(CRUDrx);\n"
                + "         operation = crudArgs.operation;\n"
                + "         code = crudArgs.code;\n"
                + "         var answerNewID = crudArgs.newID;\n"
                //                + "         console.log(\"operation:\"+operation+\"  -  code:\"+code+\"   -   answerNewID:\"+answerNewID);"
                + "         myCNT = myarg.CONNECTOR;"
                + "         var CNTx = JSON.stringify(myCNT);\n"
                + "         var CNTrx = (CNTx);\n"
                //                + "         console.log(\"CNTrx: \"+CNTrx);"
                + "         cntArgs = JSON.parse(CNTrx);\n"
                + "         var fatherValueBefore = cntArgs.thisFormFather;\n"
                + "         var cellName = cntArgs.cellName;\n"
                + "         var thisFormFather=\"\";"
                + "         try {thisFormFather = document.getElementById(rifForm + \"-FATHER\").value;  } catch (err) {}\n"
                + "         formName = cntArgs.formName;\n"
                + "         formID = cntArgs.formID;\n"
                + "         var copyTag = cntArgs.copyTag;\n"
                + "         var rifForm = formID+\"-\"+copyTag ;"
                + "         console.log(\"thisFormFather:\"+thisFormFather+\"  -  rifForm:\"+rifForm +\"  -  formName:\"+formName   );"
                + "         var ADDposition = document.getElementById(rifForm + \"-ADDPOS\").value;\n"
                + "         console.log(\"Posizione ADDER:\"+ ADDposition);"
                + "         var fatherValue = \"\";\n"
                + "         try {\n"
                + "              if (thisFormFather != \"null\" && thisFormFather != \"\")\n"
                + "              fatherValue = document.getElementById(thisFormFather + \"-KEYfieldValue\").value;\n"
                + "         } catch (err) {\n"
                + "              thisFormFather = \"\";\n"
                + "         }\n"
                + "         if (fatherValue == fatherValueBefore) {\n"//se non è cambiata la schermata durante il CRUD...
                + "         if (ADDposition==\"top\" || ADDposition==\"Top\"|| ADDposition==\"TOP\"){\n"
                //                + "         console.log(\"Posizione ADDER usata: TOP\" );"
                // aggiungo una riga prima della terza (lascio in basso i totals e l'addNewLine
                + "console.log (\"FORM DI ADDING:\"+rifForm + \"-TABLE\");"
                + "         var tableRef = document.getElementById(rifForm + \"-ROWSTABLE\");\n"
                + "         var lines = tableRef.rows.length;\n"
                + "console.log (\"lines:\"+lines);"
                + "         var newLinePosition = 0;\n"
                + "     if (lines > 0) {\n"
                + "         newLinePosition = 2;\n"
                + "     } else {\n"
                + "         newLinePosition = 1;\n"
                + "     }\n"
                + "console.log (\"newLinePosition:\"+newLinePosition);"
                + "     var newRow = tableRef.insertRow(newLinePosition);\n"
                + "     newRow.id = rifForm + \"-\" + answerNewID + \"-ROW\";\n"
                + "     newRow.innerHTML = \"NEW LINE\";\n"
                + "     newRow.class = \"unselectedRow\";\n"
                + "     repaintRow(formID, copyTag, answerNewID);\n"
                + "     }else{\n"
                //                + "         console.log(\"Posizione ADDER usata: BOTTOM --->\"+rifForm + \"-ROWSTABLE\" );"
                //      aggiungo una riga prima della penultima (lascio in basso i totals e l'addNewLine
                + "     var tableRef = document.getElementById(rifForm + \"-ROWSTABLE\");\n"
                + "     var lines = tableRef.rows.length;\n"
                + "         console.log(\"Posizione ADDER usata: BOTTOM --->\"+rifForm + \"-ROWSTABLE  -->LINES FOUND: \"+lines );"
                + "     var newLinePosition = 0;\n"
                + "     if (lines > 1) {\n"
                + "         newLinePosition = lines - 2;\n"
                + "     } else {\n"
                + "         newLinePosition = lines - 1;\n"
                + "     }\n"
                + "         console.log(\"newLinePosition --->\"+newLinePosition );"
                + "     var newRow = tableRef.insertRow(newLinePosition);\n"
                + "     newRow.id = rifForm + \"-\" + answerNewID + \"-ROW\";\n"
                + "     newRow.innerHTML = \"NEW LINE\";\n"
                + "     newRow.class = \"unselectedRow\";\n"
                + "     repaintRow(formID, copyTag, answerNewID);\n"
                + "     }\n"
                + "     }\n"
                // adesso cancello il contenuto della casella di inserimento

                + "     try {\n"
                + "         document.getElementById(rifForm + \"-NEW-ROW\").style.dislpay = \"block\";\n"
                + "         var ID = rifForm + \"-\" + cellName + \"-NEW\";\n"
                + "         document.getElementById(ID).value = \"\";\n"
                + "         } catch (err) {\n"
                + "         }\n"
                + "     }else"
                // </editor-fold> 
                //--------------------------------
                // <editor-fold defaultstate="collapsed" desc="REFRESHFORM">                
                + "         if (action == \"REFRESHFORM\"){"
                + "         console.log(\"ricevuto REFRESHFORM. \" );"
                //+ " console.log(\"destDiv : \"+destDiv  );\n"
                + " var collapseOrder = myarg.COLLAPSEFATHER;"
                + " console.log(\"collapseOrder : \"+collapseOrder);\n"
                + " if (destDiv){"
                + "     document.getElementById(destDiv).innerHTML = htmlCode ;\n"
                + "   if (collapseOrder && collapseOrder==\"true\"){"
                + "     var toAnalyze = destDiv.split(\"-\");\n"
                + "     if ( toAnalyze.length >1  ) {"
                + "         fathForm = toAnalyze[1];"
                //+ "         console.log(\"fathForm : \"+fathForm);\n"
                + "         var ftrName= document.getElementById(fathForm + \"-X-EXTENDEDNAME\").value;\n"
                //+ "         console.log(\"ftrName : \"+ftrName);\n"
                + "         try{smartHideBody(ftrName);}catch(err){}"
                + "     }"
                + "   }"
                + " } "
                + "       "
                + "     }"
                // </editor-fold> 
                //--------------------------------
                // <editor-fold defaultstate="collapsed" desc="DEFAULT">   
                + "else{"
                + " console.log(\"destDiv : \"+destDiv  );\n"
                + " if (destDiv){"
                + "     document.getElementById(destDiv).innerHTML = htmlCode ;\n"
                //                + "try{"
                //                + "var prnt = destDiv.replace(\"ROWSDIV\", \"mainBodyTable\");"
                //                + " console.log(\"prnt : \"+prnt  );\n"
                //                + "         document.getElementById(prnt).style.dislpay = \"block\";\n"
                //                + "}catch (err){}"
                + " }"
                + " }\n"
                // </editor-fold> 
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="busy">
                + "     if (type == \"busy\") {\n"
                + "     msg = json.VALUE;\n"
                + "     if (msg == \"1\"){"
                + "     document.body.style.cursor = 'wait';"
                + "     }else{"
                + "     document.body.style.cursor = 'auto';"
                + "     }"
                + "     busyLine = document.getElementById(\"busyLine\");\n"
                + "     busyLine.innerHTML = \"<img src='./media/loader\"+msg+\".gif' style='width: 32px; height: 32px;'   >\";"
                + "    }\n"
                // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="default">
                + "    else\n"
                + "    {\n"
                + "        image = null;\n"
                + "    }\n"
                // </editor-fold> 
                + "    writeInfo();\n"
                + "}\n"
                // </editor-fold> 
                //--------------------------------
                // <editor-fold defaultstate="collapsed" desc="disabledEventPropagation">
                + "function disabledEventPropagation(event)\n"
                + "{\n"
                + " try{   if (event.stopPropagation){\n"
                + "       event.stopPropagation();\n"
                + "     }\n"
                + "     else if(window.event){\n"
                + "      window.event.cancelBubble=true;\n"
                + "     }\n"
                + "     event.preventDefault();\n"
                + "     event.stopImmediatePropagation();"
                + " }catch(err){}"
                + "}";
        // </editor-fold>

        //--------------------------------
        if (mySettings.isUsesTree()) {
            // <editor-fold defaultstate="collapsed" desc="createLeaf">
            dbCode += "function createLeaf(leafID,leafValue){"
                    + "         var li = document.createElement(\"li\");\n"
                    + "         li.appendChild(document.createTextNode(leafValue));\n"
                    + "         li.setAttribute(\"id\",leafID);  \n"
                    + "         li.setAttribute(\"onclick\", \"leafSelected(event, '\"+leafID+\"');\");"
                    + "         li.setAttribute(\"oncontextmenu\", \"leafSelected(event, '\"+leafID+\"');\");"
                    + "         return li;"
                    + "}\n"
                    + ""
                    // </editor-fold>
                    //--------------------------------
                    // <editor-fold defaultstate="collapsed" desc="toggleTreeAdder">
                    + "function toggleTreeAdder(event, node){\n"
                    + "var x = event.target;"
                    + "var father =  x.parentNode.id ;"
                    + "console.log(\"PARENT: \"+ father);"
                    + "disabledEventPropagation(event);\n"
                    + "var containerName = node+\"-NEWBUTTON\";\n"
                    + "var inputName = node+\"-NEW\";\n"
                    + " if(document.getElementById(inputName)){\n"
                    + "     removeElementsByClass(\"treeAdder\");\n"
                    + "     return;\n"
                    + " }\n"
                    + "removeElementsByClass(\"treeAdder\");"
                    + " console.log(\"containerName : \" + containerName  );\n"
                    + " console.log(\"inputName : \" + inputName  );\n"
                    + "if (containerName){"
                    + " var input = document.createElement(\"input\");\n"
                    + " input.type = \"text\";\n"
                    + " input.className = \"treeAdder\";\n"
                    + " input.id = inputName;\n"
                    + " input.setAttribute(\"onChange\",\"addTreeElement('\"+node+\"','\"+father+\"')\");"
                    + " input.setAttribute(\"onClick\",\"focusOnTreeAdder(event,'\"+node+\"','\"+father+\"')\");"
                    + " var container =  document.getElementById(containerName);\n"
                    + " if(container){\n"
                    + "     container.appendChild(input);\n"
                    + " }\n"
                    + " }\n"
                    + "}\n"
                    // </editor-fold>
                    //--------------------------------
                    // <editor-fold defaultstate="collapsed" desc="focusOnTreeAdder">
                    + "function focusOnTreeAdder(event,node,father){\n"
                    + "disabledEventPropagation(event);\n"
                    + "    }\n"
                    // </editor-fold>
                    //--------------------------------
                    // <editor-fold defaultstate="collapsed" desc="removeElementsByClass">
                    + "function removeElementsByClass(className){\n"
                    + "    const elements = document.getElementsByClassName(className);\n"
                    + "    while(elements.length > 0){\n"
                    + "        elements[0].parentNode.removeChild(elements[0]);\n"
                    + "    }\n"
                    + "}"
                    // </editor-fold>
                    //--------------------------------
                    // <editor-fold defaultstate="collapsed" desc="addTreeElement">
                    + "function addTreeElement(node, fatherNode){"
                    + "var jsonRequest ={};\n"
                    + "console.log(\"ADD TREE ELEMENT node: \"+node);\n"
                    + "console.log(\"ADD TREE ELEMENT fatherNode: \"+fatherNode);\n"
                    + "var formID=\"\";\n"
                    + "var copyTag=\"X\";\n"
                    + " if (node) {\n"
                    + "     var toAnalyze = node.split(\"-\");\n"
                    + "     if ( toAnalyze.length >1  ) {"
                    + "     formID = toAnalyze[0];"
                    + "     copyTag = toAnalyze[1];"
                    + "         console.log(\"CASO ROOT formID: \"+formID);\n"
                    + "         console.log(\"CASO ROOT copyTag: \"+copyTag);\n"
                    + "     }"
                    + " }"
                    //                + "console.log(\"ADD TREE ELEMENT  formID: \"+formID);\n"
                    //                + "console.log(\"ADD TREE ELEMENT  copyTag: \"+copyTag);\n"
                    //                + "console.log(\"ADD TREE ELEMENT  objName: \"+objName);\n"
                    + " myObj = {};"
                    + " myObj.formID = formID;"
                    + " myObj.copyTag = copyTag;"
                    + " myObj.fatherNode = fatherNode;"
                    + " myObj.objName = \"\";"
                    + " myObj.KEYvalue = \"NEW\";"
                    + " myObj.operation = \"textChanges\";"
                    + " myObj.cellType = \"T\";"
                    + " myObj.filterField = \"\";"
                    + " myObj.routineOnChange = \"\";"
                    + " myObj.actionParams = formID;"
                    + " myObj.exitRoutine = \"\";"
                    + " myObj.nodeName = node;"
                    + "smartTreeChanged(myObj);\n"
                    + "}\n"
                    // </editor-fold>
                    //--------------------------------
                    // <editor-fold defaultstate="collapsed" desc="leafSelected">
                    + "function leafSelected(event,objID){"
                    + "disabledEventPropagation(event);\n"
                    + "var rightclick;\n"
                    + "	if (!event) var e = window.event;\n"
                    + "	if (event.which) rightclick = (event.which == 3);\n"
                    + "	else if (event.button) rightclick = (event.button == 2);\n"
                    //                + "	alert('Rightclick: ' + rightclick); // true or false"
                    + "console.log('Rightclick: ' + rightclick);\n"
                    + "console.log(\"leafSelected: \"+objID);\n"
                    + "if (rightclick==true){"
                    //+ "     rightClickedLeaf(event,objID);"
                    + "}else{"
                    + "     var childObj =  document.getElementById(objID+\"STABLE\");"
                    + "     if ( childObj){"
                    + "     console.log(\"leaf aperto viene chiuso: \"+childObj);\n"
                    + "     childObj.remove();"
                    + "     }else{"
                    + "     console.log(\"leaf viene aperto : \"+childObj);\n"
                    + "     var level = getLeafLevel(objID);"
                    + "     console.log(\"Level LEAF : \"+level);\n"
                    + "     smartTreeGetBranch(objID,level);"
                    + "     }"
                    + "}"
                    + "}\n"
                    + ""
                    // </editor-fold>
                    //--------------------------------
                    // <editor-fold defaultstate="collapsed" desc="getLeafLevel">
                    + "function getLeafLevel(objID){"
                    + "var x = document.getElementById(objID);"
                    + "var type = \"UL\";"
                    + "var level=0;"
                    + "var y = x.parentElement ;"
                    + "type = y.tagName;"
                    + "while(type == \"UL\" || type == \"LI\"){"
                    + " level = level +1;"
                    + " y = y.parentElement;"
                    + " type = y.tagName;"
                    + "}"
                    + "if (level>1) {level= 1+((level-1)/2);}"
                    + "return level;}\n"
                    // </editor-fold>
                    //-------------------------------------------------------
                    // <editor-fold defaultstate="collapsed" desc="function rightClickedLeaf">
                    + "function rightClickedLeaf(event,objID){\n"
                    + "cmenu = document.getElementById(\"contextmenu\");\n"
                    + "var ctxmCode=\"<table>\";\n"
                    + " ctxmCode+=\"<tr>"
                    + "<td>SELEZIONA!</td>"
                    + "<td> </td>"
                    + "</tr>\";"
                    + " ctxmCode+=\"</table>\";\n"
                    + " cmenu.innerHTML = ctxmCode;"
                    + "setTreeContextMenuPosition( \"show\",objID,event );"
                    + "}\n"
                    //=================================================
                    // </editor-fold>
                    //--------------------------------
                    // <editor-fold defaultstate="collapsed" desc="setTreeContextMenuPosition">
                    + "function setTreeContextMenuPosition( command,objID,event ) {\n"
                    + "console.log (\"objID:\"+objID );"
                    + "  cmenu = document.getElementById(\"contextmenu\");\n"
                    + "if (command==\"show\"){"
                    + " menuVisible = true;\n"
                    + "var e = window.event;\n"
                    + "    var objleft = e.clientX;\n"
                    + "    var objtop = e.clientY;"
                    + " cmenu.style.position= \"absolute\";"
                    + " cmenu.style.top=objtop+\"px\";"
                    + " cmenu.style.left=objleft+\"px\";"
                    + " cmenu.style.display = \"block\";"
                    + " cmenu.onmouseout = \"javascript:setTreeContextMenuPosition('hide','','')\";"
                    + "}else{"
                    + " menuVisible = false;\n"
                    + " cmenu.style.display = \"none\";"
                    + "}"
                    + "};\n"
                    + "";
            // </editor-fold>
            //--------------------------------
            // <editor-fold defaultstate="collapsed" desc="smartTreeChanged">
            dbCode += "function smartTreeChanged(params) {\n"
                    + "    var cellChangedParams=params ; \n"
                    + "    var paramj = JSON.stringify(params);\n"
                    + "    var unquotedParams=paramj.replace('&quot;', '\\\"'); \n"
                    + "    var myarg = JSON.parse(unquotedParams);\n"
                    + "    var formID = myarg.formID;\n"
                    + "    var copyTag = myarg.copyTag;\n"
                    + "    var fatherNode = myarg.fatherNode;\n"
                    + "    var rifForm = formID + \"-\" + copyTag ;\n"
                    + "    var cellName = myarg.objName;\n"
                    + "    var cellID = myarg.KEYvalue;\n"
                    + "    var operation = myarg.operation;\n"
                    + "    var action = myarg.action;\n"
                    + "    var routineOnChange = myarg.routineOnChange;\n"
                    + "    if (cellID == \"NEW\") {\n"
                    + "        operation = \"NEW\";\n"
                    + "    }\n"
                    + "    var objType = \"\";\n"
                    + "    var filterField = \"\";\n"
                    + "    var exitRoutine=\"\";\n"
                    + "    var cellType =\"\";\n"
                    + "    var qualifiedFilter = \"\";\n"
                    + "    var primaryFieldValue = cellID;\n"
                    + "    var primaryFieldName  =\"\";\n"
                    + "    var primaryFieldType  =\"\";\n"
                    + "    var filterSequence =\"\";\n"
                    + "    var thisFormName  =\"\";\n"
                    + "    var thisFormFather =\"\";\n"
                    + "    var fatherKEYvalue =\"\";\n"
                    + "    var fatherKEYtype =\"\";\n"
                    + "    var fieldFiltered =\"\";\n"
                    + "    var nodeName  =\"\";\n"
                    + "  try {nodeName = myarg.nodeName;   } catch (err) {}\n"
                    + "  try {objType = myarg.objType;   } catch (err) {}\n"
                    + "  try {filterField = myarg.filterField;   } catch (err) {}\n"
                    + "      try {exitRoutine = myarg.exitRoutine;  } catch (err) {}\n"
                    + "      try {cellType = myarg.cellType;  } catch (err) {}\n"
                    + "      try {primaryFieldName = document.getElementById(rifForm + \"-KEYfieldName\").value;  } catch (err) {}\n"
                    + "      try {primaryFieldType = document.getElementById(rifForm + \"-KEYfieldType\").value;  } catch (err) {}\n"
                    + "      try {filterSequence = document.getElementById(rifForm + \"-FILSEQ\").value;  } catch (err) {}\n"
                    + "      try {thisFormName = document.getElementById(rifForm + \"-EXTENDEDNAME\").value;  } catch (err) {}\n"
                    + "      try {thisFormFather = document.getElementById(rifForm + \"-FATHER\").value;  } catch (err) {}\n"
                    + "      try {fatherKEYvalue = document.getElementById(rifForm + \"-FATHERKEYVALUE\").value;  } catch (err) {}\n"
                    + "      try {fatherKEYtype = document.getElementById(rifForm + \"-FATHERKEYTYPE\").value;  } catch (err) {}\n"
                    + "      try {fieldFiltered = document.getElementById(rifForm + \"-FIELDFILTERED\").value;  } catch (err) {}\n"
                    + "    var fatherValueBefore = \"\";\n"
                    + "    try {\n"
                    + "        if (thisFormFather != \"null\" && thisFormFather != \"\")\n"
                    + "            fatherValueBefore = document.getElementById(thisFormFather + \"-KEYfieldValue\").value;\n"
                    + "    } catch (err) {\n"
                    + "        thisFormFather = \"\";\n"
                    + "    }\n"
                    + "    if (filterSequence.length > 4) {\n"
                    + "        var res = filterSequence.split(\";\");\n"
                    + "        for (jj = 0; jj < res.length; jj++) {\n"
                    + "            filter = res[jj];\n"
                    + "            if (filter.length > 3) {\n"
                    + "                var part = filter.split(\":\");\n"
                    + "                if (part.length > 1 && part[1].length > 1) {\n"
                    + "                    xt = part[1].substring(0, 1);\n"
                    + "                    if (xt == '#') {\n"
                    + "                        var tempDOMid = part[1].substring(1, part[1].length);\n"
                    + "                        var tempDOMvalue = \"\";\n"
                    + "                        try {\n"
                    + "                            tempDOMvalue = document.getElementById(tempDOMid).value;\n"
                    + "                        } catch (err) {\n"
                    + "                            tempDOMvalue = \"\";\n"
                    + "                        }\n"
                    + "                        if (qualifiedFilter != \"\") {\n"
                    + "                            qualifiedFilter = qualifiedFilter + \";\"\n"
                    + "                        }\n"
                    + "                        qualifiedFilter = part[0] + \":\" + tempDOMvalue;\n"
                    + "                    }\n"
                    + "                }\n"
                    + "            }\n"
                    + "        }\n"
                    + "    }\n"
                    //---NEW--------------------------------------------------

                    // <editor-fold defaultstate="collapsed" desc="NEW">
                    + "    if (operation == \"NEW\") {\n"
                    + "         console.log(\"operation= NEW;  \"+cellID);\n"
                    + "        var res = fatherNode.split(\"-\");\n"
                    + "var fatherOBJ = \"\";\n"
                    + "if (res && res.length >2){"
                    + "fatherOBJ = res[2];"
                    + "}"
                    + ""
                    + "        var ID = formID + \"-\" + copyTag+ \"-\" + fatherOBJ+ \"-NEW\";\n"
                    + "         console.log(\"ID:\"+ID+\"    myarg.objName:\"+myarg.objName);"
                    + "        var rawValue = \"\";\n"
                    + "        operation = \"ADD\";\n"
                    + "         if (myarg.objName != \"INSERT_AI\"){"
                    + "             rawValue = document.getElementById(ID).value;\n"
                    + "         }"
                    + "        if (cellType == \"C\") {\n"
                    + "            var rawValue = 1;\n"
                    + "            if (document.getElementById(ID).checked == false)\n"
                    + "                rawValue = 0;\n"
                    + "        } else if (cellType == \"DT\") {\n"
                    + "            var dateValue = rawValue.split(\"/\").join(\"-\");\n"
                    + "            var rawValue = dateValue;\n"
                    + "        } else if (cellType == \"D\") {\n"
                    + "            var dateValue = rawValue.split(\"/\").join(\"-\");\n"
                    + "            xt = dateValue.substring(2, 3);\n"
                    + "            if (xt == '-') {\n"
                    + "                xday = dateValue.substring(0, 2);\n"
                    + "                xmonth = dateValue.substring(3, 5);\n"
                    + "                xyear = dateValue.substring(6, 10);\n"
                    + "                rawValue = xyear + \"-\" + xmonth + \"-\" + xday + \"\";\n"
                    + "            }\n"
                    + "        } else if (cellType == \"S\") {\n"
                    + "            var e = document.getElementById(ID);\n"
                    + "            var rawValue = e.options[e.selectedIndex].value;\n"
                    + "        } else if (cellType == \"AI\") {\n"
                    + "            var rawValue = \"INSERT_AI\";\n"
                    + "        }\n"
                    + "        var newValue = encodeURIComponent(rawValue);\n"
                    //                + "        var addrow = document.getElementById(rifForm + \"-NEW-ROW\").innerHTML;\n"
                    //                + "        document.getElementById(rifForm + \"-NEW\").style.dislpay = \"none\";\n"
                    + ""
                    + ""
                    + "var JTBS = getJTBS(formID, copyTag, fatherKEYvalue);\n"
                    + "var jsonRequest ={};\n"
                    + "var connectors=[];"
                    + "var connector={};"
                    //                + "connector = getFormActionArguments(connector,formID, copyTag);\n"

                    + "connector = getPaginationArguments(connector,formID, copyTag,null,null);\n"
                    + "connector.door=\"formPager\"; \n"
                    + "connector.event=\"CELLCHANGED\"; \n"
                    + "connector.operation=operation; \n"
                    + "connector.sendToCRUD=encodeURIComponent(JTBS) ; \n"
                    + "connector.formID=formID; \n"
                    + "connector.copyTag=copyTag; \n"
                    + "connector.primaryFieldName=primaryFieldName; \n"
                    + "connector.primaryFieldType=primaryFieldType; \n"
                    + "connector.primaryFieldValue= primaryFieldValue ; \n"
                    + "connector.filterSequence=filterSequence; \n"
                    + "connector.filterField=filterField; \n"
                    //                + "connector.filterValue=filterValue; \n"
                    + "connector.cellType=cellType; \n"
                    + "connector.newValue=encodeURIComponent(newValue); \n"
                    + "connector.cellName=cellName; \n"
                    + "connector.fatherKEYvalue=fatherKEYvalue; \n"
                    + "connector.fatherKEYtype=fatherKEYtype; \n"
                    + "connector.routineOnChange=routineOnChange; \n"
                    //                + "connector.fieldID=cellID; \n"
                    + "connector.cellID=cellID; \n"
                    + "connector.objName=null; \n"
                    + "connector.thisFormFather=thisFormFather; \n"
                    + "connector.nodeName=nodeName; \n"
                    + "connector.fatherNodeName=fatherNode; \n"
                    + "connectors.push (connector);"
                    + "jsonRequest.connectors = connectors;\n"
                    + "var myJSON = JSON.stringify(jsonRequest);\n"
                    + "if (newValue && newValue.length>0){\n"
                    + " sendRequest(myJSON);\n"
                    + "}\n"
                    + ""
                    + "    } "
                    // </editor-fold>             
                    // <editor-fold defaultstate="collapsed" desc="DEL">  
                    + "else if (operation == \"DEL\") {\n"
                    + "        var strconfirm = confirm(\"Eliminare ?\");\n"
                    + "        if (strconfirm != true) {\n"
                    + "            return;\n"
                    + "        }\n"
                    + "        document.getElementById(rifForm + \"-\" + cellID + \"-ROW\").style.display = \"none\";\n"
                    + "         console.log(\"ID:\"+ID+\"    myarg.objName:\"+myarg.objName);"
                    + "var JTBS = getJTBS(formID, copyTag, fatherKEYvalue);\n"
                    + "var jsonRequest ={};\n"
                    + "var connectors=[];"
                    + "var connector={};"
                    //                + "connector = getFormActionArguments(connector,formID, copyTag);\n"
                    + "connector = getPaginationArguments(connector,formID, copyTag,null,primaryFieldValue);\n"
                    + "connector.door=\"formPager\"; \n"
                    + "connector.event=\"CELLCHANGED\"; \n"
                    + "connector.operation=operation; \n"
                    + "connector.sendToCRUD=encodeURIComponent(JTBS) ; \n"
                    + "connector.formID=formID; \n"
                    + "connector.copyTag=copyTag; \n"
                    + "connector.primaryFieldName=primaryFieldName; \n"
                    + "connector.primaryFieldType=primaryFieldType; \n"
                    + "connector.primaryFieldValue= primaryFieldValue ; \n"
                    + "connector.filterSequence=filterSequence; \n"
                    + "connector.filterField=filterField; \n"
                    //                + "connector.filterValue=filterValue; \n"
                    + "connector.cellType=cellType; \n"
                    + "connector.newValue=encodeURIComponent(newValue); \n"
                    + "connector.cellName=cellName; \n"
                    + "connector.fatherKEYvalue=fatherKEYvalue; \n"
                    + "connector.fatherKEYtype=fatherKEYtype; \n"
                    + "connector.routineOnChange=routineOnChange; \n"
                    //                + "connector.fieldID=cellID; \n"
                    + "connector.cellID=cellID; \n"
                    + "connector.objName=null; \n"
                    + "connector.thisFormFather=thisFormFather; \n"
                    + "connectors.push (connector);"
                    + "jsonRequest.connectors = connectors;\n"
                    + "var myJSON = JSON.stringify(jsonRequest);"
                    + "sendRequest(myJSON);"
                    + ""
                    + "    } "
                    // </editor-fold>             
                    // <editor-fold defaultstate="collapsed" desc="UPD">  
                    + "else {\n"
                    //--------

                    + "            console.log(\"operation name =\" + operation);\n"
                    + "operation = \"UPD\" ;"
                    + "        var ID = rifForm + \"-\" + myarg.objName + \"-\" + myarg.KEYvalue;\n"
                    + "        var rawValue = document.getElementById(ID).value;\n"
                    + "        document.getElementById(rifForm + \"-\" + cellID + \"-ROW\").classList.add('rowUpdating');\n"
                    + "        if (cellType == \"C\") {\n"
                    + "            var newValue = 1;\n"
                    + "            if (document.getElementById(ID).checked == false)\n"
                    + "                newValue = 0;\n"
                    + "        } else if (cellType == \"R\") {//radioButton\n"
                    + "            var val;\n"
                    + "            console.log(\"form obj name =\" + ID + \"-FORM\");\n"
                    + "            var form = document.getElementById(ID + \"-FORM\");\n"
                    + "            var radios = form.elements[ID];\n"
                    + "            for (var i = 0, len = radios.length; i < len; i++) {\n"
                    + "                if (radios[i].checked) { // radio checked?\n"
                    + "                    val = radios[i].value; // if so, hold its value in val\n"
                    + "                    var newValue = encodeURIComponent(val);\n"
                    + "                    break; // and break out of for loop\n"
                    + "                }\n"
                    + "            }\n"
                    + "\n"
                    + "            console.log(\"RADIO VALUE:\" + newValue);\n"
                    + "        } else if (cellType == \"DT\") {\n"
                    + "            var dateValue = rawValue.split(\"/\").join(\"-\");\n"
                    + "            var newValue = encodeURIComponent(dateValue);\n"
                    + "        } else if (cellType == \"D\") {\n"
                    + "            var dateValue = rawValue.split(\"/\").join(\"-\");\n"
                    + "            xt = dateValue.substring(2, 3);\n"
                    + "            if (xt == '-') {\n"
                    + "                xday = dateValue.substring(0, 2);\n"
                    + "                xmonth = dateValue.substring(3, 5);\n"
                    + "                xyear = dateValue.substring(6, 10);\n"
                    + "                dateValue = xyear + \"-\" + xmonth + \"-\" + xday + \"\";\n"
                    + "            }\n"
                    + "            var newValue = encodeURIComponent(dateValue);\n"
                    + "        } else {\n"
                    + "            var newValue = encodeURIComponent(rawValue);\n"
                    + "        }\n"
                    //--------
                    + "         console.log(\"ID:\"+ID+\"    myarg.objName:\"+myarg.objName);"
                    + "var JTBS = getJTBS(formID, copyTag, fatherKEYvalue);\n"
                    + "var jsonRequest ={};\n"
                    + "var connectors=[];"
                    + "var connector={};"
                    //                + "connector = getFormActionArguments(connector,formID, copyTag);\n" 
                    + "connector = getPaginationArguments(connector,formID, copyTag,null,primaryFieldValue);\n"
                    + "         console.log(\"connector from getPaginationArguments:\"+JSON.stringify(connector));"
                    + "connector.door=\"formPager\"; \n"
                    + "connector.event=\"CELLCHANGED\"; \n"
                    + "connector.operation=operation; \n"
                    + "connector.sendToCRUD=encodeURIComponent(JTBS) ; \n"
                    + "connector.formID=formID; \n"
                    + "connector.copyTag=copyTag; \n"
                    + "connector.primaryFieldName=primaryFieldName; \n"
                    + "connector.primaryFieldType=primaryFieldType; \n"
                    + "connector.primaryFieldValue= primaryFieldValue ; \n"
                    + "connector.filterSequence=filterSequence; \n"
                    + "connector.filterField=filterField; \n"
                    //                + "connector.filterValue=filterValue; \n"
                    + "connector.cellType=cellType; \n"
                    + "connector.newValue=encodeURIComponent(newValue); \n"
                    + "connector.cellName=cellName; \n"
                    + "connector.fatherKEYvalue=fatherKEYvalue; \n"
                    + "connector.fatherKEYtype=fatherKEYtype; \n"
                    + "connector.routineOnChange=routineOnChange; \n"
                    //                + "connector.fieldID=cellID; \n"
                    + "connector.cellID=cellID; \n"
                    + "connector.objName=null; \n"
                    + "connector.thisFormFather=thisFormFather; \n"
                    + "connectors.push (connector);"
                    + "jsonRequest.connectors = connectors;\n"
                    + "var myJSON = JSON.stringify(jsonRequest);"
                    + "         console.log(\"sendRequest(myJSON):\"+myJSON);"
                    + "sendRequest(myJSON);"
                    + ""
                    + "    } "
                    + "}\n";
            // </editor-fold>
            // </editor-fold>
        }
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="writeInfo">
        dbCode += "function writeInfo() {\n"
                + "    try {\n"
                + "        mySpan.innerHTML = info;\n"
                + "    }\n"
                + "    catch (err) {\n"
                + "    }\n"
                + "}\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="writeToScreen">
        dbCode += "function writeToScreen(message) {\n"
                + "    top.innerHTML += message + \"<br>\";\n"
                + "}\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="sendToServer">
        dbCode += "function sendToServer(message) {\n"
                + " console.log(\"SONO IN sendToServer:invio-->> \"+message);"
                + " var messageEncoded=encodeURIComponent(message);"
                + " Semaphore(document.getElementById(\"ledLine\"),32,\"orange\",\"sending\");"
                + " console.log(\"SONO IN sendToServer:codificato-->> \"+message);"
                + " ws.send(messageEncoded);\n"
                + "    info = \"CONNECTED (\" + wsURI + \")\";\n"
                + "    Semaphore(document.getElementById(\"ledLine\"),32,\"green\",info);"
                //                                + " console.log(\"SONO IN sendToServer:FATTO.\");"
                + "}\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="sendHandshake">
        dbCode += "function sendHandshake(token) {\n"
                + "    var txtParams = document.getElementById(\"txtParams\").value;\n"
                + "    var message = '{"
                + "\"token\":\"' + token + '\","
                + "\"type\":\"handshake\","
                + "\"params\":\"' + encodeURIComponent(txtParams) + '\"}';\n"
                + "    sendToServer(encodeURIComponent(message));\n"
                + "}\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="sendFormsMap">
        dbCode += "function sendFormsMap() {\n"
                + "    var token = document.getElementById(\"WStoken\").value;\n"
                + "if (token==null || token.length<1){"
                + "console.log(\"sendFormsMap--->Annullo invio su WS perchè token non è compilato.\");"
                + "openWS();"
                + "return;"
                + "};"
                + "    var txtParams = document.getElementById(\"txtParams\").value;\n"
                + "mapraw = JSON.stringify(activeForms) ;\n"
                + "map = encodeURIComponent(mapraw);\n"
                + "    var message = '{\"token\":\"' + token + '\",\"type\":\"formsMap\",\"payload\":\"' + map + '\",\"params\":\"' + encodeURIComponent(txtParams) + '\"}';\n"
                + "    sendToServer(encodeURIComponent(message));\n"
                + "}\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="onOpen">
        dbCode += "function onOpen() {\n"
                //                + "    mySpan = document.getElementById(\"statusLine\");\n"
                + "    info = \"CONNECTED (\" + wsURI + \")\";\n"
                + "    Semaphore(document.getElementById(\"ledLine\"),32,\"green\",info);"
                //                + "    writeInfo();\n"
                + "}\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="sendName">
        dbCode += "function sendName() {\n"
                + "    var status = document.getElementById(\"WSstatus\").value;\n"
                + "    var name = document.getElementById(\"nameField\").value;\n"
                + "    if (name == null || name == \"\") {\n"
                + "        openWS();\n"
                + "    }\n"
                + "    sendToServer(name);\n"
                + "}\n"
                + "\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="sendRequest">
        dbCode += "function sendRequest(wsRequest) {\n"
                //                                 + "wsRequest = wsRequest.replace(/'/g, '%27');"
                + "console.log(\"sendRequest--> .\");"
                + "    var token = document.getElementById(\"WStoken\").value;\n"
                + "if (token==null || token.length<1){"
                + "console.log(\"sendRequest-->Annullo invio su WS perchè token non è compilato.\");"
                + "openWS();"
                + "return;"
                + "};"
                + "    var txtParams = document.getElementById(\"txtParams\").value;\n"
                + "    var message = '{"
                + "\"token\":\"' + token + '\","
                + "\"type\":\"wsRequest\","
                + "\"payload\":\"' + encodeURIComponent(wsRequest) + '\","
                + "\"params\":\"' + encodeURIComponent(txtParams) + '\"}';\n"
                + "console.log(\"sendRequest ha preparato il messaggio:\"+message);"
                + "try{msgEncoded = encodeURIComponent(message);"
                + "message = msgEncoded;"
                //                                + "console.log(\"messaggio codificato per invio:\"+message);"
                + "}catch(err){"
                + "console.log(\"sendRequest-->errore in URIencoding:\"+message);"
                + "}"
                + "    sendToServer( message );\n"
                + "}\n";
        // </editor-fold>
        //-------------------------------- 
        // <editor-fold defaultstate="collapsed" desc="populateTitleBarSpace">
        dbCode += "function populateTitleBarSpace() {\n"
                + "    var txtParams = document.getElementById(\"txtParams\").value;\n"
                + "    var xmlhttp;\n"
                + "    if (window.XMLHttpRequest)\n"
                + "    {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "        xmlhttp = new XMLHttpRequest();\n"
                + "    }\n"
                + "    else\n"
                + "    {// code for IE6, IE5\n"
                + "        xmlhttp = new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "    }\n"
                + "    xmlhttp.onreadystatechange = function ()\n"
                + "    {\n"
                + "        if (xmlhttp.readyState == 4 && xmlhttp.status == 200)\n"
                + "        {\n"
                + "            document.getElementById(\"titleBarSpace\").innerHTML = xmlhttp.responseText;\n"
                + "        }\n"
                + "    }\n"
                + "var params='\"params\":'+ document.getElementById(\"portalParams\").value;"
                + "var utils='\"responseType\":\"text\"';"
                + "var connectors='\"connectors\":[{\"door\":\"session\","
                + "     \"event\":\"PopulateTitleBar\" "
                + " }]';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                + "    xmlhttp.open(\"POST\", \"portal\", true);\n"
                + "    xmlhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n"
                + "    xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + "}\n"
                + "\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="populateSessionSpace">
        dbCode += "function populateSessionSpace() {\n"
                + "    var txtParams = document.getElementById(\"txtParams\").value;\n"
                + "    var xmlhttp;\n"
                + "    if (window.XMLHttpRequest)\n"
                + "    {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "        xmlhttp = new XMLHttpRequest();\n"
                + "    }\n"
                + "    else\n"
                + "    {// code for IE6, IE5\n"
                + "        xmlhttp = new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "    }\n"
                + "    xmlhttp.onreadystatechange = function ()\n"
                + "    {\n"
                + "        if (xmlhttp.readyState == 4 && xmlhttp.status == 200)\n"
                + "        {\n"
                + "            document.getElementById(\"SessionSpace\").innerHTML = xmlhttp.responseText;\n"
                + "        }\n"
                + "    }\n"
                + "var params='\"params\":'+ document.getElementById(\"portalParams\").value;"
                + "var utils='\"responseType\":\"text\"';"
                + "var connectors='\"connectors\":[{\"door\":\"session\","
                + "     \"event\":\"PopulateSession\" "
                + " }]';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                + "    xmlhttp.open(\"POST\", \"portal\", true);\n"
                + "    xmlhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n"
                + "    xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + "}\n"
                + "\n";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="populateWSspace">
        dbCode += "function populateWSspace()\n"
                + "{\n"
                + "    var txtParams = document.getElementById(\"txtParams\").value;\n"
                + "    var xmlhttp;\n"
                + "    if (window.XMLHttpRequest)\n"
                + "    {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "        xmlhttp = new XMLHttpRequest();\n"
                + "    }\n"
                + "    else\n"
                + "    {// code for IE6, IE5\n"
                + "        xmlhttp = new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "    }\n"
                + "    xmlhttp.onreadystatechange = function ()\n"
                + "    {\n"
                + "        if (xmlhttp.readyState == 4 && xmlhttp.status == 200)\n"
                + "        {\n"
                + "            document.getElementById(\"WSspace\").innerHTML = xmlhttp.responseText;\n"
                + "        }\n"
                + "    }\n"
                + "var params='\"params\":'+ document.getElementById(\"portalParams\").value;"
                + "var utils='\"responseType\":\"text\"';"
                + "var connectors='\"connectors\":[{\"door\":\"session\","
                + "     \"event\":\"PopulateWS\" "
                + " }]';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                + "    xmlhttp.open(\"POST\", \"portal\", true);\n"
                + "    xmlhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n"
                + "    xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + "}";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="populateQPWSspace">
        dbCode += "function populateQPWSspace()\n"
                + "{\n"
                + "    var txtParams = document.getElementById(\"txtParams\").value;\n"
                + "    var xmlhttp;\n"
                + "    if (window.XMLHttpRequest)\n"
                + "    {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "        xmlhttp = new XMLHttpRequest();\n"
                + "    }\n"
                + "    else\n"
                + "    {// code for IE6, IE5\n"
                + "        xmlhttp = new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "    }\n"
                + "    xmlhttp.onreadystatechange = function ()\n"
                + "    {\n"
                + "        if (xmlhttp.readyState == 4 && xmlhttp.status == 200)\n"
                + "        {\n"
                + "            document.getElementById(\"LHWSspace\").innerHTML = xmlhttp.responseText;\n"
                + "        }\n"
                + "    }\n"
                + "var params='\"params\":'+ document.getElementById(\"portalParams\").value;"
                + "var utils='\"responseType\":\"text\"';"
                + "var connectors='\"connectors\":[{\"door\":\"session\","
                + "     \"event\":\"PopulateQPWS\" "
                + " }]';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                + "    xmlhttp.open(\"POST\", \"portal\", true);\n"
                + "    xmlhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n"
                + "    xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + "}";
        // </editor-fold>
        //--------------------------------
        // <editor-fold defaultstate="collapsed" desc="populateRightSpace">
        dbCode += "function populateRightSpace()\n"
                + "{\n"
                + "    var txtParams = document.getElementById(\"txtParams\").value;\n"
                + "    var xmlhttp;\n"
                + "    if (window.XMLHttpRequest)\n"
                + "    {// code for IE7+, Firefox, Chrome, Opera, Safari\n"
                + "        xmlhttp = new XMLHttpRequest();\n"
                + "    }\n"
                + "    else\n"
                + "    {// code for IE6, IE5\n"
                + "        xmlhttp = new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                + "    }\n"
                + "    xmlhttp.onreadystatechange = function ()\n"
                + "    {\n"
                + "        if (xmlhttp.readyState == 4 && xmlhttp.status == 200)\n"
                + "        {\n"
                + "            document.getElementById(\"contactsSpace\").innerHTML = xmlhttp.responseText;\n"
                + "        }\n"
                + "    }\n"
                + "var params='\"params\":'+ document.getElementById(\"portalParams\").value;"
                + "var utils='\"responseType\":\"text\"';"
                + "var connectors='\"connectors\":[{\"door\":\"session\","
                + "     \"event\":\"PopulatePeople\" "
                + " }]';"
                + "var gp='{'+utils+','+params+','+connectors+'}';"
                + "    xmlhttp.open(\"POST\", \"portal\", true);\n"
                + "    xmlhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n"
                + "    xmlhttp.send(\"target=requestsManager&gp=\" + encodeURIComponent(gp));\n"
                + "}"
                + "";
        // </editor-fold>
        //--------------------------------
        return dbCode;
    }

}
