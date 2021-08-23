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

import REVOdbManager.EVOpagerDirectivesManager;
import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.EVOpagerDBconnection;
import REVOsetup.ErrorLogger;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showIt.ShowItForm;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class synopticMap {

    EVOpagerParams myParams;
    Settings mySettings;
    gaiaMapBGpic myBGpic;
    ArrayList<gaiaMapPath> myPaths;
    ArrayList<gaiaMapObject> myMapObjects;
    String mapID;
    String rifFather;
    String BGgroupHead;
    String PATHSgroupHead;

    public synopticMap(EVOpagerParams myParams, Settings mySettings) {
        this.myParams = myParams;
        this.mySettings = mySettings;
        myBGpic = new gaiaMapBGpic();
        myPaths = new ArrayList();
        myMapObjects = new ArrayList();
    }
//


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

    class gaiaMapObject {

        int ID;
        String rifPeriph;
        String rifMap;
        String description;
        String posX;
        String posY;
        String posZ;
        int width;
        int height;
        String rifDevice;
        String onClickParams;
        String tip;
    }

}
