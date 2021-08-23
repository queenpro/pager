function openWS() {
    var fatherServerConnected=0;
    wsURI = "ws://" + document.location.host + "/" + overallProjectName + "/WS/" + user;
    ws = new WebSocket(wsURI);
    ws.onopen = function (evt) {
        onOpen(evt)
        mySpan = document.getElementById("statusLine");
        info = "CONNECTED (" + wsURI + ")";
        fatherServerConnected=1;
        writeInfo();
    };
    ws.onclose = function (evt) {
        mySpan = document.getElementById("statusLine");
        info = "DISCONNECTED (" + wsURI + ")";
        fatherServerConnected=0;
        setTimeout(function(){openWS()}, 5000);
        writeInfo();
    };
    ws.onmessage = function (evt) {
        onMessage(evt)
    };
    ws.onerror = function (event) {
        console.log("Error ", event);
    }
}
function reconnect(){
    
}

function onMessage(evt) {
    if (typeof evt.data == "string") {
        parseText(evt.data);
    } else {
        //  drawImageBinary(evt.data);
    }
    evt = null;
}

function speak(toberead) {
    var recVoice = new SpeechSynthesisUtterance();
    recVoice.text = toberead;
    recVoice.lang = 'it-IT';
    recVoice.rate = 1.2;
    recVoice.onend = function (event) {
    }
    speechSynthesis.speak(recVoice);
    //recognition.start();

}



function parseText(data) {
    var json = JSON.parse(data);
    msg = json.VALUE;
    type = json.TYPE;
    sender = json.SENDER;
    phrase = " " + json.PHRASE;
    map = json.map;
    if (type == "time") {
        mySpan = document.getElementById("timeGoesHere");
        info = document.location.host + " - " + msg;
    }
    else if (type == "tokenAssign") {
        var paramj = JSON.stringify(map);
        var myarg = JSON.parse(paramj);

        var clientId = myarg.clientId;
        var sessionID = myarg.sessionID;
        var newToken = myarg.newToken;
        document.getElementById("WStoken").value = newToken;
        document.getElementById("WSstatus").value = "CONNECTED";
        document.getElementById("WSsessionID").value = sessionID;
        document.getElementById("WSclientId").value = clientId;
        mySpan = document.getElementById("recv");
        info = "NEW TOKEN:" + newToken;
        sendHandshake(newToken);
    }
    else if (type == "message") {
        image = null;

        speak("ricevuto " + msg + "da " + sender);


    }
    else if (type == "status") {
        mySpan = document.getElementById("statusLine");
        info = document.location.host + " - " + msg;
    }
    else
    {
        image = null;
    }
    writeInfo();
}
function writeInfo() {
    try {
        mySpan.innerHTML = info;
    }
    catch (err) {
    }
}
function writeToScreen(message) {
    top.innerHTML += message + "<br>";
}
function sendToServer(message) {
    ws.send(message);
}
function sendHandshake(token) {
    var txtParams = document.getElementById("txtParams").value;
    var message = '{"token":"' + token + '","type":"handshake","params":"' + encodeURIComponent(txtParams) + '"}';
    sendToServer(encodeURIComponent(message));
}
function sendFormsMap() {
    var token = document.getElementById("WStoken").value;
    var txtParams = document.getElementById("txtParams").value;
    var message = '{"token":"' + token + '","type":"formsMap","payload":' + JSON.stringify(activeForms) + ',"params":"' + encodeURIComponent(txtParams) + '"}';
    sendToServer(encodeURIComponent(message));
}
function onOpen() {
    mySpan = document.getElementById("statusLine");
    info = "CONNECTED (" + wsURI + ")";
    writeInfo();
}
function sendName() {
    var status = document.getElementById("WSstatus").value;
    var name = document.getElementById("nameField").value;
    if (name == null || name == "") {
        openWS();
    }
    sendToServer(name);
}

function populateWSspace()
{
    var txtParams = document.getElementById("txtParams").value;
    var xmlhttp;
    if (window.XMLHttpRequest)
    {// code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp = new XMLHttpRequest();
    }
    else
    {// code for IE6, IE5
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function ()
    {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
        {
            document.getElementById("WSspace").innerHTML = xmlhttp.responseText;
        }
    }
    var connector = '{"door":"PopulateWS","event":"PopulateWS","params":' + txtParams + '}';
    xmlhttp.open("POST", "gaiaGate", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlhttp.send("args=" + encodeURIComponent(connector));
}