/*
 * Copyright (C) 2022 Franco Venezia @ www.ffs.it
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
package REVOsetup;

import REVOdbManager.EVOpagerDirectivesManager;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Franco Venezia @ www.ffs.it
 */
public class OSenv {

    String OSbasePath;
    String OSslash;

    public OSenv() {
        String slash = "/";
        this.OSbasePath = System.getProperty("user.dir");
//        System.out.println("Working Directory = " + systemBasepath);
        if (this.OSbasePath.contains("\\")) {
            slash = "\\";
        } else {
            slash = "/";
        }
        System.out.println("slash = " + slash);
        this.OSslash = slash;
    }

    public String normalizePath(String dirtyPath) {
        String cleanPath = dirtyPath;
        if (this.OSbasePath.contains("\\")) {
            cleanPath = cleanPath.replace("/", "\\");
        } else {
            cleanPath = cleanPath.replace("\\", "/");
        }

        return cleanPath;
    }

    public String getOSbasePath() {
        return OSbasePath;
    }

    public String getOSslash() {
        return OSslash;
    }

    public String getBasePath(EVOpagerParams myParams, Settings mySettings) {
        String completePath = "";
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
        String percorsoBase = myManager.getDirective("uploadBasePath");

        String nomeContesto = "";
        if (myParams.getCKcontextID() != null && myParams.getCKcontextID().length() > 0) {
            nomeContesto = myParams.getCKcontextID() + this.OSslash;
        }
        completePath = this.OSbasePath + percorsoBase + nomeContesto;
        if (percorsoBase.startsWith("[]")) {
            String usatoPerFile = percorsoBase + nomeContesto;
            usatoPerFile = usatoPerFile.replace("[]", "");
            completePath = usatoPerFile;
        }
        completePath = normalizePath(completePath);
        System.out.println(">>>>BasePath:" + completePath);
        return completePath;
    }

    public String getCompletePathFilename(String fileToSearch, String folderPath, EVOpagerParams myParams, Settings mySettings) {
        String filepath = fileToSearch;
        String completePathFilename = filepath;
        EVOpagerDirectivesManager myManager = new EVOpagerDirectivesManager(myParams, mySettings);
        String percorsoBase = myManager.getDirective("uploadBasePath");

        String nomeContesto = "";
        if (myParams.getCKcontextID() != null && myParams.getCKcontextID().length() > 0) {
            nomeContesto = myParams.getCKcontextID() + this.OSslash;
        }
        completePathFilename = this.OSbasePath + percorsoBase + nomeContesto + folderPath + filepath;
        if (percorsoBase.startsWith("[]")) {
            String usatoPerFile = percorsoBase + nomeContesto + folderPath + filepath;
            usatoPerFile = usatoPerFile.replace("[]", "");
            completePathFilename = usatoPerFile;
        }

        completePathFilename = normalizePath(completePathFilename);
//        System.out.println(">>>>CompletePathFilename:" + completePathFilename);
        return completePathFilename;
    }

    public ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> myList = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                myList.add(fileEntry.getName());
//            System.out.println(fileEntry.getName());
            }
        }
        return myList;
    }

}
