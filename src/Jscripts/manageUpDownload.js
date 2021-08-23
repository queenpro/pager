function uploadPicture(P1, P2, P3)
{
    document.getElementById(P1 + "-" + P2 + "-" + P3 + "-PIC").innerHTML = "<img src='wait.gif' alt='wait...' >";
    formName = P1 + "-" + P2 + "-" + P3 + "-FRM";
    var oForm = document.forms[ formName ];
    var formData = new FormData(oForm);
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
            document.getElementById(P1 + "-" + P2 + "-" + P3 + "-PIC").innerHTML = xmlhttp.responseText;
        }
    }
    xmlhttp.open('POST', oForm.getAttribute('action'), false);
    xmlhttp.send(formData);
}


function uploadFile(fatherID)
{
    formName = "formUploadDocument";
    var oForm = document.forms[ formName ];
    var formData = new FormData(oForm);
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
            document.getElementById("secPanel-" + fatherID).innerHTML = "";
            var res = fatherID.split("-");
            var rifForm = res[0];
            var copyTag = res[1];
            var objName = res[2];
            var rowID = res[3];
            repaintRow(rifForm, copyTag, rowID);
        }
    }
    xmlhttp.open('POST', oForm.getAttribute('action'), true);
    xmlhttp.send(formData);
    document.getElementById("secPanel-" + fatherID).innerHTML = "<img src='wait.gif' alt='wait...' >"
}

function downloadFile(objID, rifProcedure)
{
    window.open("ServeFile?rifProcedure=" + rifProcedure + "&filename=" + objID + "&CKuserID=admin&CKcontextID=matrix&CKtokenID=7a7d27ab-2874-4bec-89f4-78f4f5ac62e6&CKrepID=null&CKmachineID=null&CKprojectName=gaiaweb&CKprojectGroup=&CKargs=null", '_blank');
    ;
}

function alertFilename(fatherID)
{
    var thefile = document.getElementById('thefile');
    f = thefile.value;
    f = f.replace(/.*[\/\\]/, '');
    document.getElementById('newfilename').value = f;
    uploadFile(fatherID);
}

function askUpload(fatherID, action)
{
    elements = document.getElementsByClassName("secondPanelClass");
    for (var i = 0; i < elements.length; i++) {
        elements[i].innerHTML = "";
    }
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
            document.getElementById("secPanel-" + fatherID).innerHTML = xmlhttp.responseText;
            document.getElementById("secPanel-" + fatherID).style.display = 'block';
        }
    }
    xmlhttp.open("POST", "uploadAskFilename", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlhttp.send("action=" + action + "&fatherID=" + fatherID + "&CKuserID=admin&CKcontextID=matrix&CKtokenID=7a7d27ab-2874-4bec-89f4-78f4f5ac62e6&CKrepID=null&CKmachineID=null&CKprojectName=gaiaweb&CKprojectGroup=&CKargs=null");
}

function askDeleteUpload(fatherID, action)
{
    elements = document.getElementsByClassName("secondPanelClass");
    for (var i = 0; i < elements.length; i++) {
        elements[i].innerHTML = "";
    }
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
            document.getElementById("secPanel-" + fatherID).innerHTML = "";
            var res = fatherID.split("-");
            var rifForm = res[0];
            var copyTag = res[1];
            var objName = res[2];
            var rowID = res[3];
            repaintRow(rifForm, copyTag, rowID);
        }
    }
    xmlhttp.open("POST", "UploadFileHandler", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlhttp.send("&filename=" + fatherID + "&rifProcedure=OBJname&action=" + action + "&fatherID=" + fatherID + "&CKuserID=admin&CKcontextID=matrix&CKtokenID=7a7d27ab-2874-4bec-89f4-78f4f5ac62e6&CKrepID=null&CKmachineID=null&CKprojectName=gaiaweb&CKprojectGroup=&CKargs=null");
}
