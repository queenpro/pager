function getTBS4Custom(rifForm)
{
    var toBeSent = document.getElementById(rifForm + "-TOBESENT").value;
    var backBlock = "";
    if (toBeSent.length > 0) {
        var toAnalyze = toBeSent.split(";");
        for (var jj = 0; jj < toAnalyze.length; jj++) {
            var couple = toAnalyze[jj];
            if (couple && couple.length > 0) {
                var local = couple.split("AS");
                var labelName = local[1];
                if (local[0] && local[0].length > 0) {
                    var local2 = local[0].split("[");
                    if (local2[1] && local2[1].length > 0) {
                        var local3 = local2[1].split("]");
                        var fieldName = local3[0].trim();
                        var fatherKEY = document.getElementById(rifForm + "-KEYfieldValue").value;
                        var rif = rifForm + "-" + fieldName + "-" + fatherKEY;
                        var fatherValue = document.getElementById(rif).value;
                        backBlock = backBlock + "&" + labelName.trim() + "=" + fatherValue.trim()
                    }
                }
            }
        }
        return backBlock;
    }
}
function getTBS(masterForm, masterKEYvalue)
{
    var ToBeSent = document.getElementById(masterForm + "-TOBESENT").value;
    var blockToBeSent = "";
    if (ToBeSent != "null" && typeof ToBeSent != "undefined" && ToBeSent.length > 0) {
        var toAnalyze = ToBeSent.split(";");
        for (var jj = 0; jj < toAnalyze.length; jj++) {
            if (toAnalyze[jj] && typeof toAnalyze[jj] != "undefined") {
                var couple = toAnalyze[jj].split("AS");
                var remoteField = couple[1];
                var valLocalField = couple[0];
                valLocalField = valLocalField.replace("[", "");
                valLocalField = valLocalField.replace("]", "");
                valLocalField = valLocalField.trim();
                try {
                    var rif = masterForm + "-" + valLocalField + "-" + masterKEYvalue;
                    var localValue = document.getElementById(rif).value;
                } catch (err) {
                    var localValue = "null";
                }
                alert("TBS valLocalField>" + valLocalField + "=" + localValue);
                if (blockToBeSent.length > 0) {
                    blockToBeSent = blockToBeSent + ";";
                }
                blockToBeSent = blockToBeSent + remoteField + ":" + localValue;
            }
        }
    }
    blockToBeSent = "&TBS=" + blockToBeSent.trim();
    return blockToBeSent;
}
function getJTBS(masterForm, masterCopyTag, masterKEYvalue)
{
    var ToBeSent = "";
    try {
        ToBeSent = document.getElementById(masterForm + "-" + masterCopyTag + "-TOBESENT").value;
    } catch (err) {
    }
    var myStC = "";
    if (ToBeSent.length > 0) {
        ToBeSent = decodeURIComponent(ToBeSent);
        if (ToBeSent != "null" && typeof ToBeSent != "undefined" && ToBeSent.length > 0) {
            var block = '{"TBS":' + ToBeSent + '}';
            var jsonData = JSON.parse(block);
            for (var i = 0; i < jsonData.TBS.length; i++) {
                var thisStC = '';
                var riga = jsonData.TBS[i];
                var childType = riga.childType;
                var childMarker = riga.childMarker;
                var type = riga.type;
                var field = riga.field;
                if (type == "rowField") {
                    try {
                        var rif = masterForm + "-" + masterCopyTag + "-" + field + "-" + masterKEYvalue;
                        var localValue = document.getElementById(rif).value;
                    } catch (err) {
                        var localValue = "null";
                    }
                } else if (type == "formField") {
                    try {
                        var rif = masterForm + "-" + masterCopyTag + "-" + field + "-FORM";
                        var localValue = document.getElementById(rif).value;
                    } catch (err) {
                        var localValue = "null";
                    }
                }
                thisStC = '{ "childType":"' + childType.trim() + '" , "childMarker":"' + childMarker.trim() + '", "value":"' + localValue + '" }';
                if (myStC.length > 0) {
                    myStC = myStC + ', ';
                }
                myStC = myStC + thisStC;
            }
        }
    }
    var elementList;
    try {
        elementList = document.querySelectorAll('.' + masterForm + '_panelFilter');
    } catch (err) {
    }
    for (i = 0; i < elementList.length; i++) {
        var ele = elementList[i];
        var valueToSend = ele.value;
        var res = ele.id.split("-");
        var formID = res[0];
        var copyTag = res[1];
        var filtername = res[2];
        if (valueToSend && filtername && filtername.includes("date")) {
            var dateValue = valueToSend.split("/").join("-");
            xt = dateValue.substring(2, 3);
            if (xt = '-') {
                xday = dateValue.substring(0, 2);
                xmonth = dateValue.substring(3, 5);
                xyear = dateValue.substring(6, 10);
                dateValue = xyear + "-" + xmonth + "-" + xday + "";
            }
            valueToSend = dateValue;
        }
        var thisFilter = '{ "childType":"panelFilter" , "childMarker":"' + filtername.trim() + '", "value":"' + valueToSend + '" }';
        if (myStC.length > 0) {
            myStC = myStC + ', ';
        }
        myStC = myStC + thisFilter;
    }
    try {
        myStC = myStC.trim();
    } catch (err) {
    }
    myStC = '[' + myStC + ']';
    return myStC;
}
