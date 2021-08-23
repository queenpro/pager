function openSplash(type, argID)
{
    document.getElementById("splash-type").value = type;
    document.getElementById("selectors").innerHTML = "Building panel...";
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
            document.getElementById("selectors").innerHTML = xmlhttp.responseText;
        }
    }
    xmlhttp.open("POST", "EVOcatcher", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlhttp.send("event=" + type + "&args=" + argID + "&CKuserID=admin&CKcontextID=matrix&CKtokenID=7a7d27ab-2874-4bec-89f4-78f4f5ac62e6&CKrepID=null&CKmachineID=null&CKprojectName=gaiaweb&CKprojectGroup=&CKargs=null");
    scrollTo("splashPanel");
}

function splashMLSchanges(level, maxLevel, jsonArgs)
{
    if (level == maxLevel) {
        var key = document.getElementById("MLSblock-" + level).value;
        var label = document.getElementById("MLSblock-" + level).label;
        var event = document.getElementById("splashMLSblock-EVENT").value;
        var args = document.getElementById("splashMLSblock-ARGS").value;
        document.getElementById("splashMLSblock-JSONARGS").value = JSON.stringify(jsonArgs);
        document.getElementById("splashMLSblock-VALUE").value = key;
        document.getElementById("splashMLSblock-LABEL").value = label;
    } else {
        var key = document.getElementById("MLSblock-" + level).value;
        var event = document.getElementById("splashMLSblock-EVENT").value;
        var args = document.getElementById("splashMLSblock-ARGS").value;
        var newLevel = level + 1;
        destDiv = "splashMLS-" + newLevel;
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
                document.getElementById(destDiv).innerHTML = xmlhttp.responseText;
            }
        }
        xmlhttp.open("POST", "EVOcatcher", true);
        xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlhttp.send("event=" + event + "&args=" + args + "&level=" + newLevel + "&key=" + key + "&CKuserID=admin&CKcontextID=matrix&CKtokenID=7a7d27ab-2874-4bec-89f4-78f4f5ac62e6&CKrepID=null&CKmachineID=null&CKprojectName=gaiaweb&CKprojectGroup=&CKargs=null");
        scrollTo("splashPanel");
    }
}

function splashOK()
{
    var type = document.getElementById("splash-type").value;
    if (type == "MLS" || type == "MLSfield") {
        var maxLevel = document.getElementById("splashMLSblock-MAXL").value;
        var value = document.getElementById("MLSblock-" + maxLevel).value;
        var label = jQuery('#MLSblock-' + maxLevel + ' option:selected').text();
        var event = document.getElementById("splashMLSblock-EVENT").value;
        var args = document.getElementById("splashMLSblock-ARGS").value;
        var jsonString = document.getElementById("splashMLSblock-JSONARGS").value;
        if (event == "MLSfield") {
            document.getElementById("LABL-" + args).innerHTML = label;
            document.getElementById(args).value = value;
            cellChanged(JSON.parse(jsonString));
            scrollTo(close);
        }
    } else if (type == "details") {
        var rifForm = document.getElementById("splash-rifForm").value;
        var copyTag = document.getElementById("splash-copyTag").value;
        var keyValue = document.getElementById("splash-keyValue").value;
        scrollTo(close);
        repaintRow(rifForm, copyTag, keyValue)
    }
}

function scrollTo(hash) {
    location.hash = "#" + hash;
}



//-----------SUGGESTER

function openSuggester(type, argID)
{
    document.getElementById("splash-type").value = type;
    document.getElementById("selectors").innerHTML = "Building panel...";
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
            document.getElementById("selectors").innerHTML = xmlhttp.responseText;
        }
    }
    xmlhttp.open("POST", "dummy", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlhttp.send("event=" + type + "&args=" + argID + "");
    scrollTo("suggesterPanel");
}
 
function suggesterOK()
{
    var type = document.getElementById("splash-type").value;
    if (type == "MLS" || type == "MLSfield") {
        var maxLevel = document.getElementById("splashMLSblock-MAXL").value;
        var value = document.getElementById("MLSblock-" + maxLevel).value;
        var label = jQuery('#MLSblock-' + maxLevel + ' option:selected').text();
        var event = document.getElementById("splashMLSblock-EVENT").value;
        var args = document.getElementById("splashMLSblock-ARGS").value;
        var jsonString = document.getElementById("splashMLSblock-JSONARGS").value;
        if (event == "MLSfield") {
            document.getElementById("LABL-" + args).innerHTML = label;
            document.getElementById(args).value = value;
            cellChanged(JSON.parse(jsonString));
            scrollTo(close);
        }
    } else if (type == "details") {
        var rifForm = document.getElementById("splash-rifForm").value;
        var copyTag = document.getElementById("splash-copyTag").value;
        var keyValue = document.getElementById("splash-keyValue").value;
        scrollTo(close);
        repaintRow(rifForm, copyTag, keyValue)
    }
}

 function suggesterScrollTo(hash) {
    location.hash = "#" + hash;
}