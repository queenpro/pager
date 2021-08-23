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
 * @author Franco
 */
public class storedFile {
     EVOpagerParams myParams;
     Settings mySettings ;
     
     
    String labelType;
    String namingMap; //JSON contenente i blocchi che determinano il nome file
    // es. {"nameParts":[{"position":"1","Marker":"$$$KEY$$$"},
    //                      {"position":"2","Marker":"###descrizione###"}]}
    String fileName;
    String nomeVideo;
    // usati da portal in case appliucationX
    
    byte[] bites;
    String fileContentType;
    String servedFilename;

    public storedFile(EVOpagerParams myParams, Settings mySettings, String fileName) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        this.fileName = fileName;
    }

  public storedFile(){ //usato in portal
      
  }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public byte[] getBites() {
        return bites;
    }

    public void setBites(byte[] bites) {
        this.bites = bites;
    }

    public String getServedFilename() {
        return servedFilename;
    }

    public void setServedFilename(String servedFilename) {
        this.servedFilename = servedFilename;
    }
  
  
    public void setNomeVideo(String nomeVideo) {
        this.nomeVideo = nomeVideo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public EVOpagerParams getMyParams() {
        return myParams;
    }

    public void setMyParams(EVOpagerParams myParams) {
        this.myParams = myParams;
    }

    public Settings getMySettings() {
        return mySettings;
    }

    public void setMySettings(Settings mySettings) {
        this.mySettings = mySettings;
    }

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public String getNamingMap() {
        return namingMap;
    }

    public void setNamingMap(String namingMap) {
        this.namingMap = namingMap;
    }

  public String getNomeVideo(){
      int posizioneSlash = fileName.lastIndexOf("/");
            System.out.println("posizioneSlash ---------->" + posizioneSlash);
            String nomeVideo = fileName.substring(posizioneSlash);
            nomeVideo = nomeVideo.replace("/", "");
            nomeVideo = nomeVideo.replace(" ", "_");
            System.out.println("nomeVideo ---------->" + nomeVideo);
            return nomeVideo;
  }
   public String getContentType() {
        String extension[] = { // File Extensions
            "txt", //0 - plain text
            "htm", //1 - hypertext
            "jpg", //2 - JPEG image
            "png", //2 - JPEG image
            "gif", //3 - gif image
            "pdf", //4 - adobe pdf
            "doc", //5 - Microsoft Word
            "docx", //6 - Microsoft Word
            "xls", //7 - Microsoft Excel
            "xlsx", //8 - Microsoft Excel
            "dwg", //9 - Autocad
        }; // you can add more
        String mimeType[] = { // mime types
            "text/plain", //0 - plain text
            "text/html", //1 - hypertext
            "image/jpg", //2 - image
            "image/jpg", //2 - image
            "image/gif", //3 - image
            "application/pdf", //4 - Adobe pdf
            "application/msword", //5 - Microsoft Word
            "application/msword", //6 - Microsoft Word
            "application/msexcel", //7 - Microsoft Excel
            "application/msexcel", //8 - Microsoft Excel
            "application/autocad", //9 - Autocad
        }, // you can add more
                contentType = "text/html";    // default type
        // dot + file extension
        int dotPosition = fileName.lastIndexOf('.');
        // get file extension
        String fileExtension
                = fileName.substring(dotPosition + 1);
        // match mime type to extension
        for (int index = 0; index < mimeType.length; index++) {
            if (fileExtension.equalsIgnoreCase(extension[index])) {
                contentType = mimeType[index];
                break;
            }
        }
        return contentType;
    }   
}
