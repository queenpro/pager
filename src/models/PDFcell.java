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

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Franco
 */
public class PDFcell extends PdfPCell {

    public void configCell(String phrase, int size, int cols, int borders) {
        // borders TOP=1, BOTOM=2, LEFT=4, RIGHT=8  TOP-LEFT=5
        if (phrase == null) {
            phrase = "";
        }
        FontSelector selectorQ = new FontSelector();
        Font fQ = FontFactory.getFont(FontFactory.HELVETICA, 18);
        fQ.setColor(BaseColor.BLACK);
        fQ.setSize(size);
        fQ.setStyle("plain");
        selectorQ.addFont(fQ);
        Phrase phQ = selectorQ.process(phrase);
        this.setPhrase(new Phrase(phQ));
        this.setColspan(cols);
        this.setHorizontalAlignment(Element.ALIGN_LEFT);
        this.setNoWrap(true);
        this.setBorder(borders);

    }

    public void configCell(String phrase, int size, int cols, int alignment, BaseColor textColor, BaseColor bgColor, int borders, int borderWidth, boolean noWrap, String style) {
        if (phrase == null) {
            phrase = "";
        }
        
        FontSelector selectorQ = new FontSelector();
        Font fQ = FontFactory.getFont(FontFactory.HELVETICA, 18);
        fQ.setColor(textColor);

        fQ.setSize(size);
         fQ.setStyle(Font.NORMAL);
        if (style == null) {
            fQ.setStyle(Font.NORMAL);
        }else if (style.equalsIgnoreCase("bold")) {
           fQ.setStyle(Font.BOLD);
        } else if (style.equalsIgnoreCase("italic")) {
           fQ.setStyle(Font.ITALIC);
        }  else if (style.equalsIgnoreCase("UNDERLINE")) {
           fQ.setStyle(Font.UNDERLINE);
        }  else if (style.equalsIgnoreCase("BOLDITALIC")) {
           fQ.setStyle(Font.BOLDITALIC);
        } 
        
        
        fQ.setStyle(style);
        selectorQ.addFont(fQ);
        Phrase phQ = selectorQ.process(phrase);
        this.setPhrase(new Phrase(phQ));
        this.setHorizontalAlignment(alignment);// es. Element.ALIGN_LEFT
        this.setColspan(cols);
        this.setBackgroundColor(bgColor);
        this.setBorder(borders);
        this.setBorderWidth(borderWidth);
        this.setLeading(3f, 1f);
        this.setNoWrap(noWrap);

    }

    
    public void fillCell(String phrase,PDFreportRow myRow){
       this.configCell(phrase, 
                 myRow.getFontSize(), 
                 myRow.getCols(),
                 myRow.getAlignment(),
                 myRow.getColor(),
                 myRow.getBackColor(), 
                 myRow.getBorder(),
                 myRow.getBorderWidth(),
                 myRow.isNoWrap(),
                 myRow.getStyle());
       
       
       if ( myRow.getFixedHeight()>0){
           this.setFixedHeight(myRow.getFixedHeight());
           System.out.println("IMPOSTO ALTEZZA FISSA:"+myRow.getFixedHeight());
       }
       
        if ( myRow.getLeading()>0){
           this.setLeading((float)myRow.getLeading(),(float)myRow.getLeading()); 
       }
       
        
    }
    public void fillCell(   Image myLogo,PDFreportRow myRow){
//        System.out.println("\n************\nALIGNMENT\n");
               
                          myLogo.setAlignment( myRow.getAlignment());
                          //---------------
                            this.addElement(myLogo);
                          //--------------
                          
                       //    System.out.println("Aggiunta immagine..."+myLogo.getAbsoluteX());
                            this.setColspan(myRow.getCols());
                            this.setBorder(myRow.getBorder());
                            this.setHorizontalAlignment( myRow.getAlignment());
                            if (myRow.getFixedHeight()>0){
                                this.setFixedHeight(myRow.getFixedHeight());
                            }
                            
                      
                            
                            
    }
}
