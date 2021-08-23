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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.ShowItForm;

/**
 *
 * @author Franco
 */
public class TomcatGaiaHost {

    String projectName;
    String path;
    String data;
    JSONArray serverParams;
    JSONObject params;
    String dbUsername;
    String dbSeed;
    String pwType;
    String defaultContext;
    String QP_centralManagerURL;

//    public TomcatGaiaHost() {
//    }
    public TomcatGaiaHost(String XprjectName) {
        this.projectName = XprjectName;

//        System.out.println("\n<<<<<<< data:" + data);
        try {
            getConfigPath();
            try {
                getConfigData();
                getProjectInfos();
            } catch (Exception e) {

            }
        } catch (Exception e) {

        }

    }

    public String getQP_centralManagerURL() {
        return QP_centralManagerURL;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbSeed() {
        return dbSeed;
    }

    public String getDefaultContext() {
        return defaultContext;
    }

    public String getPwType() {
        return pwType;
    }

    public String getConfigPath() {

        String pth;
        pth = System.getProperty("user.dir");
        String separator = "\\";
        if (pth.endsWith("/")) {
            separator = "";
        } else {
            separator = "\\";
        }
        path = pth + separator + "GaiaSettings.txt";

        System.out.println("\n>>>>>>>>> user.dir:" + pth + "     >>>>>>>>> ConfigPath:" + path);
        return path;
    }

    public String getConfigData() {

        try {
            FileInputStream fis = null;
            fis = new FileInputStream(path);
            if (fis != null) {
                data = IOUtils.toString(fis, "UTF-8");
                System.out.println("\n>>>>>>>>> data:" + data);
            } else {
                System.out.println("\n>>>>>>>>>ERROR: CONFIG DATA NOT FOUND IN:" + path);
                System.out.println("TRY TO MAKE IT:" + path);
                data = null;

            }
//            System.out.println("\n>>>>>>>>> DATA FROM DISK:" + data);

        } catch (FileNotFoundException ex) {
            System.out.println("\n>>>>>>>>>FILE NOT FOUND");
        } catch (IOException ex) {
            System.out.println("\n>>>>>>>>>FILE NOT REACHABLE");
        }
        if (data == null) {

            try {
                File myObj = new File(path);
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                } else {
                    System.out.println("File already exists.");
                }
                try {
                    FileWriter myWriter = new FileWriter(path);
                    data= "{\"apps\":[{\"appName\":\"" + this.projectName + "\",\"dbUsr\":\"Anakim\",\"dbSeed\":\"Padme\",\"pwType\":\"standard\"}]}";
                    myWriter.write(data);
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred writing file.");
                    e.printStackTrace();
                }

            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        return data;
    }

    public void getProjectInfos() {
        System.out.println("\n<<<<<>>>>>> data:" + data);
        if (data != null && data.length() > 0) {
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(data);

//            System.out.println(">>>>>>>>> jsonObject:" + jsonObject.toString());
                JSONArray array = (JSONArray) jsonObject.get("apps");
                for (Object riga : array) {
                    JSONParser jParser = new JSONParser();
                    JSONObject jObject = (JSONObject) jParser.parse(riga.toString());
                    String paramAppName = jObject.get("appName").toString();
//                System.out.println(">>>>>>>>> paramAppName:" + paramAppName);
                    if (paramAppName.equalsIgnoreCase(projectName)) {
                        dbUsername = jObject.get("dbUsr").toString();
                        dbSeed = jObject.get("dbSeed").toString();
                        pwType = jObject.get("pwType").toString();
                        QP_centralManagerURL = jObject.get("QP_centralManagerURL").toString();
                        break;
                    }
                }
            } catch (ParseException ex) {
                Logger.getLogger(TomcatGaiaHost.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            dbUsername = "Anakim";
            dbSeed = "Padme";
            pwType = "standard";
            QP_centralManagerURL = "http://queenpro.myqnapcloud.com:9080/qpmanager/centralManager";
        }
    }
}
