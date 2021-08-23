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
import REVOpager.EVOpagerDBconnection;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import static showIt.ShowItForm.encodeURIComponent;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class picToDB {

    String HTMLcode;

    public String uploadToDB(Part filePart,String sql, EVOpagerParams myParams, Settings mySettings) throws IOException {
        String image = "";
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();

        InputStream inputStream = null;

//                Part filePart = request.getPart("media");
        if (filePart != null) {
            System.out.println(filePart.getName());
            System.out.println(filePart.getSize());
            System.out.println(filePart.getContentType());
            inputStream = filePart.getInputStream();
        }

        //ridimensiono l'immagine-----
        try {
            BufferedImage originalImage = ImageIO.read(inputStream);
            Dimension imgSize = new Dimension(originalImage.getWidth(), originalImage.getHeight());
            Dimension boundary = new Dimension(1920, 1080);
            Dimension nuovaDimensione = getScaledDimension(imgSize, boundary);

            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
                    : originalImage.getType();
            BufferedImage resizeImageJpg = resizeImage(originalImage, type, nuovaDimensione.width, nuovaDimensione.height);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(resizeImageJpg, "jpeg", os);                          // Passing: ​(RenderedImage im, String formatName, OutputStream output)
            inputStream = new ByteArrayInputStream(os.toByteArray());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //----------------------------
        String message = null;  // message will be sent back to client

        try {
//            String sql = "UPDATE " + tabella + " SET "
//                    + fieldMedia + " = ? WHERE " + primaryFieldName
//                    + " ='" + primaryFieldValue + "'";

            System.out.println(sql);
            PreparedStatement statement = conny.prepareStatement(sql);

            if (inputStream != null) {
                // fetches input stream of the upload file for the blob column
                statement.setBlob(1, inputStream);
            }
            int row = statement.executeUpdate();
            if (row > 0) {
                message = "File uploaded and saved into database";
            }
        } catch (SQLException ex) {
            message = "ERROR: " + ex.getMessage();
            ex.printStackTrace();
        } finally {
            if (conny != null) {
                // closes the database connection
                try {
                    conny.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

        }

        return image;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type,
            Integer img_width, Integer img_height) {
        BufferedImage resizedImage = new BufferedImage(img_width, img_height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, img_width, img_height, null);
        g.dispose();

        return resizedImage;
    }

    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
}
