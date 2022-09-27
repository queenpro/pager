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
package REVOpager;

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import models.objectLayout;
import showIt.ShowItForm;
import static showIt.eventManager.encodeURIComponent;

/**
 *
 * @author Franco
 */
public class DBimage {

    String table;
    String keyfield;
    String keyValue;
    String picfield;
    String params;
    EVOpagerParams myParams;

    public DBimage(String table, String keyfield, String keyValue, String picfield, EVOpagerParams myParams) {
        this.table = table;
        this.keyfield = keyfield;
        this.keyValue = keyValue;
        this.picfield = picfield;
        this.myParams = myParams;
    }

    public DBimage(EVOpagerParams myParams) {
        this.myParams = myParams;
    }

    public String getDBimageHtmlCode64(Settings mySettings, String width, String height) {
        String query = "SELECT * FROM " + table + " WHERE " + keyfield + " = '" + keyValue + "'";
        
//        System.out.println("query...."+query);
                
        return getDBimageHtmlCode64(mySettings, query, picfield, width, height);
    }

    public String getDBimageHtmlCode64(Settings mySettings, String query, String field, String width, String height) {
        objectLayout myBox = new objectLayout();
        myBox.loadBoxLayout("");
        myBox.setWidth("20px");
        myBox.setHeight("20px");
        myBox.setType("");
        try {
            myBox.setWidth(width);
        } catch (Exception e) {

        }
        try {
            myBox.setHeight(height);
        } catch (Exception e) {

        }
//        System.out.println("Mbox creato. ");
        Blob blob = null;
        BufferedImage image = null;
        String SQLphrase = query;
        String imageCode = "";
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        PreparedStatement picps = null;
        ResultSet picrs;

        try {
            picps = conny.prepareStatement(SQLphrase);
            picrs = picps.executeQuery();
            while (picrs.next()) {
                if (picrs != null) {
                    try {
                        blob = picrs.getBlob(field);
                        InputStream in = null;
                        if (blob != null) {
                            try {
                                in = blob.getBinaryStream();
                                image = ImageIO.read(in);
                            } catch (IOException ex) {
                                Logger.getLogger(ShowItForm.class
                                        .getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (SQLException ex) {
                        // Logger.getLogger(ShowItForm.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                if (image != null) {
                    imageCode = getRowImageHtmlCode(image, "", myBox);
                } else {
                    imageCode = "";
                }
            }

            conny.close();
        } catch (SQLException ex) {

        }

        return imageCode;

    }

    public String getRowImageHtmlCode(BufferedImage image, String alternativeString, objectLayout myBox) {
        String usedWidth = myBox.getWidth();
        String usedHeight = myBox.getHeight();
        String usedPicWidth = myBox.getPicWidth();
        String usedPicHeight = myBox.getPicHeight();
        if (usedPicWidth == null || usedPicWidth.length() < 1) {
            usedPicWidth = usedWidth;
        }
        if (usedPicHeight == null || usedPicHeight.length() < 1) {
            usedPicHeight = usedHeight;
        }

        try {
            usedPicWidth = usedPicWidth.replace("px", "");
        } catch (Exception e) {

        }
        try {
            usedPicHeight = usedPicHeight.replace("px", "");
        } catch (Exception e) {

        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String picCode = "";
        String imageString = null;
        int radio = 10;
        if (image != null) {
            try {
                int HH = Integer.parseInt(usedPicHeight);
                if (HH > 20) {
                    radio = HH / 2;
                }
            } catch (Error e) {
            }

            BufferedImage Rimage = makeRoundedCorner(image, radio);
            try {

                ImageIO.write(Rimage, "gif", bos);
                byte[] imageBytes = bos.toByteArray();
                imageString = Base64.getEncoder().encodeToString(imageBytes);
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            imageString = "";
        }
        picCode += "<img src=\"data:image/gif;base64," + imageString + "\" alt=\"" + alternativeString + "\"";
        picCode += "   width=\"" + usedPicWidth + "px\" heigth=\"" + usedPicHeight + "px\" ";
        picCode += " />";
//        System.out.println("picCode:\n" + picCode);
        return picCode;
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(qualityHints);
        g2.setClip(new RoundRectangle2D.Double(0, 0, w, h, w / 2, h / 2));
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return output;
    }

    public String getDBimageHtmlCode(String width, String height) {

        String image = "<img  alt=\"...\" src='portal?target=requestsManager&gp=";

        String params = "\"params\":\"" + encodeURIComponent(myParams.makePORTALparams()) + "\"";
        String connectors = "\"connectors\":[{\"door\":\"RenderPic\","
                + "\"event\":\"badge\","
                + "\"table\":\"" + table + "\","
                + "\"keyfield\":\"" + keyfield + "\","
                + "\"keyValue\":\"" + keyValue + "\","
                + "\"picfield\":\"" + picfield + "\""
                + " }]";
        String utils = "\"responseType\":\"text\"";
        String gp = "{" + encodeURIComponent(utils) + "," + encodeURIComponent(params) + "," + encodeURIComponent(connectors) + "}";

        image += encodeURIComponent(gp);
        image += "&rnd=$$$newRandom$$$'  width='" + width + "' heigth='" + height + "' >";
        image += "</td>";

        return image;
    }

}
