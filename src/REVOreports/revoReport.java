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

package REVOreports;


import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import REVOpager.EVOpagerDBconnection;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import showIt.ShowItForm;
import models.ShowItObject;

/**
 *
 * @author Franco
 */
public class revoReport {

    Settings mySettings;
    EVOpagerParams myParams;
    OutputStream stream;

    public revoReport(EVOpagerParams myParams, Settings mySettings, OutputStream myStream, ShowItForm myForm) {
        this.mySettings = mySettings;
        this.myParams = myParams;
        this.stream = myStream;
        this.myForm = myForm;
    }

    public revoReport(OutputStream myStream, ShowItForm myForm) {
        this.mySettings = myForm.getMySettings();
        this.myParams = myForm.getMyParams();
        this.stream = myStream;
        this.myForm = myForm;
    }

    private PdfPTable table, intestazione;
    PdfPCell cell;
    int fontsize;

    private Document document;
    private int flagIntestazione = 0;
    private int rowIntestazioneStart = 0;
    private int rowIntestazioneEnd = 0;
    private int flagFondopagina = 0;
    private String labelDate = "";
    private int publishDate;
    private int publishPageNumber;
    private int publishPageTotal;
    private String HeaderSubtitle;
    ArrayList<colonna> colonne;
    ShowItForm myForm;

    class TableHeader extends PdfPageEventHelper {

        String header;
        PdfTemplate total;

        public void setHeader(String header) {
            this.header = header;
        }

        /**
         * Creates the PdfTemplate that will hold the total number of pages.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
         * com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 16); //width, Height
        }

        public void onEndPage(PdfWriter writer, Document document) {
            paintHeader(writer, document, myForm.getLabel(), myParams);

        }

        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber() - 1)),
                    2, 2, 0);
        }
    }

    public void formToPdf(Document masterDocument ) {

        System.out.println("===getPageCols");
        colonne = getPageCols();
        System.out.println("===getPageCols...DONE");
//------------FASE 1: APRO IL JSON che descrive il Report------------------------        
//--------------------e coompilo l'oggetto REPORT  e  i suoi REPORTFIELD---------        
//-----------FASE 2  : Stampo l'intestazione del documento e l'intestazione della pagina------------------------------------------------        
        // step 1
        //---QUI CREO IL DOCUMENTO  
        if (masterDocument == null  ) {
            System.out.println("=========================");
            System.out.println("CREO UN NUOVO REPORT");
            System.out.println("=========================");
            myParams.printParams(" formToPdf");
            labelDate = "°°/°°/°°°°";

            publishDate = 1;
            publishPageNumber = 1;
            publishPageTotal = 1;

            document = new Document(PageSize.A4, 26, 26, 180, 26);
            document.addTitle("TITOLO DOCUMENTO");
            //Document(Rectangle pageSize, float marginLeft, float marginRight, float marginTop, float marginBottom) {
            // step 2
            PdfWriter writer;
            try {
                writer = PdfWriter.getInstance(document, stream);
                TableHeader event = new TableHeader();
                writer.setPageEvent(event);
                // step 3
            } catch (DocumentException ex) {
                Logger.getLogger(revoReport.class.getName()).log(Level.SEVERE, null, ex);
            }
            document.open();

            // paintHeader(writer, document, "TITOLO ", myParams);
        } else {
            //System.out.println("=========================" );          
            System.out.println("   =========================");
            System.out.println("   CREO UN SOTTOREPORT");
            System.out.println("   =========================");

            document = masterDocument;
        }

//-----------FASE 3 : APRO IL DB delle righe che devo stampare------------------------------------------------
//--------------------e compilo l'oggetto PRESCRIZIONE  e  stampo le caratteristiche richieste dal Report-----
        flagIntestazione = 0;
        rowIntestazioneStart = 0;
        rowIntestazioneEnd = 0;
        flagFondopagina = 0;
        String SQLphrase = myForm.prepareSQL();
        Connection conny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();
        System.out.println("   SQLreport:" + SQLphrase);
        ResultSet rs;
        Statement s;
        try {
            s = conny.prepareStatement(SQLphrase,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY); //questo serve per poter scorrere i record e riocomeinciare da capo
       
        rs = s.executeQuery(SQLphrase);
        // CONTEGGIO RIGHE TOTALI
        int rowsCounter = 0;
        while (rs.next()) {
            rowsCounter++;
        }
        rs.beforeFirst();
        //FINE CONTEGGIO RIGHE
        System.out.println("Saranno scritte " + rowsCounter + " righe.");
            try {
                paintTab(rs);
            } catch (DocumentException ex) {
                Logger.getLogger(revoReport.class.getName()).log(Level.SEVERE, null, ex);
            }
        conny.close();
 } catch (SQLException ex) {
            Logger.getLogger(revoReport.class.getName()).log(Level.SEVERE, null, ex);
        }
//==============================================================================
//--FINE CREAZIONE QUERY--------------------------------------------------------
//==============================================================================
        //System.out.println("Sono in reportCDR fase 3. SQL:" + SQLphrase);
        //         scriviRigaReport(rs, myReport, stream, myContext, currentKEY, currentDATE, reportJSON, masterDocument);
        if (masterDocument == null) {// nell'invio alla funzione ho messo argomento 'null' vuol dire che è ul docuemnto principale
            System.out.println("Chiudo il documento.");
            document.close();
        }

    }

    public ArrayList<colonna> getPageCols() {
        myForm.buildSchema();
        colonne = new ArrayList<colonna>();
        for (int obj = 0; obj < myForm.objects.size(); obj++) {
            colonna myColonna = new colonna();

            String fieldName = myForm.objects.get(obj).getName();
            myColonna.setFieldName(fieldName);

            String fieldLabel = myForm.objects.get(obj).getLabelHeader();

            if (fieldLabel == null || fieldLabel == "") {
                fieldLabel = myForm.objects.get(obj).getName();

            }
            try {
                fieldLabel = fieldName.toUpperCase();
            } catch (Exception ex) {
                fieldLabel = "";
            }
            myColonna.setLabel(fieldLabel);

            if (myForm.objects.get(obj).getActuallyVisible() < 0) {
                // non stampare colonna
            } else {
                String myWidth = "";
                if (myForm.objects.get(obj).C.getWidth() != null
                        && myForm.objects.get(obj).C.getWidth() != "null"
                        && myForm.objects.get(obj).C.getWidth() != "") {
                    myWidth = myForm.objects.get(obj).C.getWidth();
                    myWidth = myWidth.replace("px", "");
                    int newValue = Integer.parseInt(myWidth);
                    if (newValue > 2) {
                        newValue = newValue - 2;
                    }
                    myWidth = newValue + "px";
                    myColonna.setWidth(newValue);
                }
                // screivi fieldName
                if (myColonna.getWidth() > 0) {
                    colonne.add(myColonna);
                }
            }

        }
// calcolo le percentuali per ogni colonna
        int totalWidth = 0;
        for (int cc = 0; cc < colonne.size(); cc++) {
            totalWidth += colonne.get(cc).getWidth();
        }
        int used = 0;
        for (int cc = 0; cc < colonne.size(); cc++) {
            colonne.get(cc).setOnPaperWidth((100 * colonne.get(cc).getWidth()) / totalWidth);
            used += colonne.get(cc).getOnPaperWidth();
            // System.out.println("Colonna " + cc + ") " + colonne.get(cc).getFieldName() + " ->" + colonne.get(cc).getLabel() + " SIZE:" + colonne.get(cc).getOnPaperWidth() + "/100.");

        }
        colonne.get(colonne.size() - 1).setOnPaperWidth(colonne.get(colonne.size() - 1).getOnPaperWidth() + (100 - used));
        used = 0;
        for (int cc = 0; cc < colonne.size(); cc++) {
            used += colonne.get(cc).getOnPaperWidth();
            System.out.println("Colonna " + cc + ") " + colonne.get(cc).getFieldName() + " ->" + colonne.get(cc).getLabel() + " SIZE:" + colonne.get(cc).getOnPaperWidth() + "/100.");

        }
        System.out.println("TOT: " + used);

        return colonne;
    }

    public void paintTab(ResultSet rs) throws SQLException, DocumentException {
        System.out.println("ENTRO IN PAINTTAB");
        table = new PdfPTable(100); // cols sono le colonne
        table.setWidthPercentage(95);
        int[] cs = new int[colonne.size()];
        int used = 0;

        table.setSplitLate(false);

        int splitterPagesEnabled = 1;
        int righeScritte = 0;
        int righeParsate = 0;
        int lines = 0;
//CICLO RIGHE TABELLA=========================================
        while (rs.next()) {
            righeScritte++;
            for (int jj = 0; jj < myForm.objects.size(); jj++) {
//-----------------------------------------------
//--ESEGIUO LE SOMME DELLE COLONNE CON TOTALI----             
//-----------------------------------------------               
                // System.out.println("++oggetto "+this.objects.get(jj).getName()+" - HAS SUM ="+ this.objects.get(jj).Content.getHasSum() );
                if (myForm.objects.get(jj).Content.getType() != null
                        && myForm.objects.get(jj).Content.getType().equalsIgnoreCase("INT")
                        && myForm.objects.get(jj).Content.getHasSum() > 0) {
                    int partial = myForm.objects.get(jj).Content.getActualSum();
                    int thisValue = 0;
                    try {
                        thisValue = rs.getInt(myForm.objects.get(jj).getName());
                    } catch (Exception e) {
                        thisValue = 0;
                    }
                    myForm.objects.get(jj).Content.setActualSum(partial + thisValue);
                }
            }

//------------------------------------                
            lines++;
//NORMAL ROW=========================================                    
            System.out.println("------------------------riga:" + lines);
            paintRow(rs, righeScritte, "normal");
            // System.out.println("------------------------fine riga:" + lines);

        }
//TOTALS========================================
        //     htmlCode += paintRow(rs, 0, "total");

        //---------------------------
        System.out.println("butto tutto in tabella");
        document.add(table);
        System.out.println("ESCO DA PAINTTAB");
    }

    public void paintTabHeader() {

    }

    public void paintRow(ResultSet rs, int lineNumber, String rowType) {
        ShowItObject curObj = null;
        PdfPCell mycell = null;
        for (int jj = 0; jj < colonne.size(); jj++) {

            for (int obs = 0; obs < myForm.objects.size(); obs++) {
                if (colonne.get(jj).getFieldName().equalsIgnoreCase(myForm.objects.get(obs).getName())) {
                    curObj = myForm.objects.get(obs);

                    curObj.setValueToWrite(myForm.ricavoValoreDaScrivere(rs, obs));
                    System.out.println("oggetto:" + curObj.getName() + " : " + curObj.getValueToWrite());
                    mycell = paintObject(curObj, colonne.get(jj));
                    table.addCell(mycell);
                    break;
                }

            }

        }

    }

    public PdfPCell paintObject(ShowItObject curObj, colonna myColonna) {
        System.out.println("OGGETTO "+curObj.name+" TYPE: "+curObj.C.Type);
        
        PdfPCell mycell = null;
        myCell xCell = new myCell();
        mycell = xCell.configCell(curObj.getValueToWrite(), 9, myColonna.onPaperWidth, Element.ALIGN_LEFT, BaseColor.BLACK, BaseColor.WHITE, 1);

        return mycell;

    }

    public class colonna {

        String fieldName;
        String label;
        int width;
        int onPaperWidth;

        public int getOnPaperWidth() {
            return onPaperWidth;
        }

        public void setOnPaperWidth(int onPaperWidth) {
            this.onPaperWidth = onPaperWidth;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

    }

    public void paintHeader(PdfWriter writer, Document document, String headerText, EVOpagerParams myParams) {
        PdfPTable table = new PdfPTable(3);

        Image myLogo;
        Connection blobconny;
        String blobSQLphrase;

        blobconny = new EVOpagerDBconnection(myParams, mySettings).ConnLocalDataDB();

        PreparedStatement blobps;
        ResultSet blobrs;

        blobSQLphrase = "SELECT * FROM " + mySettings.getLocalFE_directives() + " WHERE infoName = 'softwareLogo'";

        Blob blob = null;
        try {
            blobps = blobconny.prepareStatement(blobSQLphrase);
            blobrs = blobps.executeQuery();
            while (blobrs.next()) {
                blob = blobrs.getBlob("media");
            }
            if (blob != null && blob.length() > 3) {
                System.out.println("PICTURE trovata:" + blobSQLphrase + " \n");
            }
            blobconny.close();
        } catch (SQLException ex) {
        }

        try {
            table.setWidths(new int[]{12, 24, 12});
            table.setTotalWidth(527);
            table.setLockedWidth(true);
            table.getDefaultCell().setFixedHeight(70);
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            // logo myLogo a sx        
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            InputStream in = null;
            try {
                in = blob.getBinaryStream();
            } catch (SQLException ex) {
            }
            BufferedImage bi = ImageIO.read(in);
            myLogo = Image.getInstance(bi, null);
            myLogo.scaleAbsoluteWidth(140);
            myLogo.scaleAbsoluteHeight(70);
            //document.add(image2);
            table.addCell(myLogo);

            // titolo in centro    
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerText);

            Font myFont = new Font();
            float fntSize, lineSpacing;
            fntSize = 6.7f;
            lineSpacing = 10f;
            table.addCell(new Paragraph(new Phrase(lineSpacing, "",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, fntSize)))); // segnaposto per coprire ultima olonna

        } catch (BadElementException ex) {
        } catch (IOException ex) {
        } catch (DocumentException ex) {

        }

        table.getDefaultCell().setFixedHeight(20);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        table.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());

    }

    /* 
     private void scriviRigaReport(ResultSet rs ,Report myReport, ServletOutputStream stream, String currentKEY, String currentDATE, String reportJSON, Document masterDocument)
     {
     
     // System.out.println("Entro in scriviRigaReport.");
    
     try{
     //   System.out.println("myReport.getNofElements():" + myReport.getNofElements());
     //||||||||||||||||CICLO RIGHE  ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||| 
     int elementiInviabili=0; 
    
    
     for (int nf=1;nf<= myReport.getNofElements();nf++){
        
     //  System.out.println("Cerco il campo nominato :"+ myReport.reportElement[nf].getFieldName());
     if ( flagIntestazione==0 || (rowIntestazioneEnd >0 && nf > rowIntestazioneEnd)  )    {
    
     // System.out.println("XXX punto 0. Elemento:"+ nf +", Tipo:" + myReport.reportElement[nf].getFieldType() );   
    
     if ( myReport.reportElement[nf].getFieldType()==null) myReport.reportElement[nf].setFieldType("text");
     //--------------------------------------------------------------------------------------------------
     if (myReport.reportElement[nf].getFieldType().equalsIgnoreCase("intestazioneStart")){
     flagIntestazione=0;     
     rowIntestazioneStart=nf;
     } else
     //--------------------------------------------------------------------------------------------------
     if (myReport.reportElement[nf].getFieldType().equalsIgnoreCase("intestazioneEnd")){
     flagIntestazione=1; 
     rowIntestazioneEnd=nf;
     } else
     //--------------------------------------------------------------------------------------------------
     if (myReport.reportElement[nf].getFieldType().equalsIgnoreCase("tableOpen")){
     int cols=0; 
     try{
     cols =  myReport.reportElement[nf].getSizeX();//n. di colonne
     } catch(Exception ex){
     cols=0;
     }
                       
     if (cols < 0) {
     cols = 1;
     }

                             
     if (myReport.getNofColumns() > 1) {

     if (myReport.getColonnaCorrente() == 1) { // solo se sono alla prima delle colonne grandi
     table = new PdfPTable(cols * myReport.getNofColumns()); // cols sono le colonne
     // System.out.println("creata tabella ." + cols * myReport.getNofColumns() + " colonne");

     table.setWidthPercentage(95);
     // table.setWidths(new int[]{2, 1, 1});
     table.setSplitLate(false);
     }
     } else {
     table = new PdfPTable(cols); // cols sono le colonne                        
     table.setWidthPercentage(95);
     // table.setWidths(new int[]{2, 1, 1});
     table.setSplitLate(false);
     }
     if (myReport.getReportKeepTogether()!= null && myReport.getReportKeepTogether().equalsIgnoreCase("TRUE")) table.setKeepTogether(true) ;              
     }
     //--------------------------------------------------------------------------------------------------                    
     else  if (myReport.reportElement[nf].getFieldType().equalsIgnoreCase("tableClose")) {
     //   System.out.println("##richiesta tableClose alla colonna " + myReport.getColonnaCorrente() + " .");
     if (myReport.getNofColumns() > 1) {

     if (myReport.getColonnaCorrente() >= myReport.getNofColumns()) {
     //   System.out.println("##eseguita tableClose alla colonna " + myReport.getColonnaCorrente() + " .");
     document.add(table);
                              
     }
     }else{
     document.add(table);
                        
     }
                        
                      
     } else//--------------------------------------------------------------------------------------------------
     if (myReport.reportElement[nf].getFieldType().equalsIgnoreCase("tableCell")){
                        
                        
                      
     int cols=0; 
     try{
     cols =  myReport.reportElement[nf].getSizeX();//n. di colonne
     } catch(Exception ex){
     cols=1;
     }
     if (cols <0 ) cols=1;
                      
                   
                      
     int rows=0; 
     try{
     rows =  myReport.reportElement[nf].getSizeY();//n. di righe
     } catch(Exception ex){
     rows=1;
     }
     if (rows <0 ) rows=1;             
                      
                      
     //----------------------------------------------------   
     //----------------------------------------------------                      
     //----------------------------------------------------                      
     String ValoreDaScrivere="";  
     if(myReport.reportElement[nf].getFieldBox().equalsIgnoreCase("field")){
     // la phrase sarà il contenuto del campo indicato
            
     elementiInviabili++;
     try {     
     //System.out.println("Leggo valore dal campo nominato "+myReport.reportElement[nf].getFieldName());
     String valoreLetto=rs.getString(myReport.reportElement[nf].getFieldName());
     //System.out.println("Valore letto: " +  valoreLetto);
                             
                              
     if(valoreLetto.equalsIgnoreCase("null")) valoreLetto="";
                              
     myReport.reportElement[nf].setFieldValue(valoreLetto);
     ValoreDaScrivere=valoreLetto;
                                
                                
                                
     if(myReport.reportElement[nf].getFieldContentType().equalsIgnoreCase("timestamp")){ 
     FormFunctionServer myForm = new FormFunctionServer();
     ValoreDaScrivere= myForm.decodeTimeStamp(ValoreDaScrivere);
     myForm=null;
     } 
                                
                                
     } catch (Exception ex) {
     myReport.reportElement[nf].setFieldValue("");
     ValoreDaScrivere="";
     }        
                          
     if (myReport.reportElement[nf].getFieldTag()!=""){
     ValoreDaScrivere= myReport.reportElement[nf].getFieldTag()+" "+ValoreDaScrivere;
     }

     // devo aggiungere il caso di un nome funzione per mettere un contenuto frutto di funzione
     } else if(myReport.reportElement[nf].getFieldBox().equalsIgnoreCase("function")){
     // la phrase sarà il contenuto del campo indicato
     //System.out.println("TROVATO ELEMENTO FUNCTION: chiamo functioneserver " );
                             
     // creo una classe FUNCTIONSERVER a cui mando rs (il resultSet) che mi ritorna la stringa giusta in base al nome della funzione
     ReportFunctionServer myFunctionServer = new ReportFunctionServer( );
            
     String valoreLetto= myFunctionServer.getFunction(rs,myReport.reportElement[nf].getFieldValue(), currentDATE,currentDATE, myContext);
     System.out.println("valoreLetto: " + valoreLetto);
     elementiInviabili++;
     try {     
                               
     if(valoreLetto.equalsIgnoreCase("null")) valoreLetto="";
     ValoreDaScrivere=valoreLetto;
     } catch (Exception ex) {
     ValoreDaScrivere="";
     }        
                          
     } else {
           
     // la phrase sarà ciò che c'è scritto in value 
            
     ValoreDaScrivere = myReport.reportElement[nf].getFieldValue();
              
     }
     if (ValoreDaScrivere==null || ValoreDaScrivere.isEmpty()) ValoreDaScrivere=" ";                     
     FontSelector selector1 = new FontSelector();
     Font f1;
     if (myReport.reportElement[nf].getFieldFontBold().equalsIgnoreCase("TRUE")){
     f1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
     }else{
     f1 = FontFactory.getFont(FontFactory.HELVETICA, 12);
     }


     f1.setColor(BaseColor.BLACK);
 
     try{ 
     fontsize=Integer.parseInt(myReport.reportElement[nf].getFieldFontSize());
     }catch (Exception ex){
     fontsize=12;
     }
     f1.setSize(fontsize);
     selector1.addFont(f1);
 


      
     Phrase ph = selector1.process(ValoreDaScrivere);      
     cell = new PdfPCell(new Phrase(ph));       
  
     //System.out.println("BGcolor:" + myReport.reportElement[nf].getFieldBackgroundColor());
     if (myReport.reportElement[nf].getFieldBackgroundColor()!=null && !myReport.reportElement[nf].getFieldBackgroundColor().isEmpty() ) {
     String color=myReport.reportElement[nf].getFieldBackgroundColor();
     if (color.equalsIgnoreCase("BLACK"))cell.setBackgroundColor(BaseColor.BLACK);
     if (color.equalsIgnoreCase("BLUE")) cell.setBackgroundColor(BaseColor.BLUE);
     if (color.equalsIgnoreCase("CYAN")) cell.setBackgroundColor(BaseColor.CYAN);
     if (color.equalsIgnoreCase("DARK_GRAY")) cell.setBackgroundColor(BaseColor.DARK_GRAY);
     if (color.equalsIgnoreCase("LIGHT_GRAY")) cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
     if (color.equalsIgnoreCase("GRAY"))cell.setBackgroundColor(BaseColor.GRAY);
     if (color.equalsIgnoreCase("GREEN")) cell.setBackgroundColor(BaseColor.GREEN);
     if (color.equalsIgnoreCase("MAGENTA")) cell.setBackgroundColor(BaseColor.MAGENTA);
     if (color.equalsIgnoreCase("ORANGE")) cell.setBackgroundColor(BaseColor.ORANGE);
     if (color.equalsIgnoreCase("PINK")) cell.setBackgroundColor(BaseColor.PINK);    
     if (color.equalsIgnoreCase("RED")) cell.setBackgroundColor(BaseColor.RED);
     if (color.equalsIgnoreCase("WHITE")) cell.setBackgroundColor(BaseColor.WHITE);
     if (color.equalsIgnoreCase("YELLOW")) cell.setBackgroundColor(BaseColor.YELLOW);
    
     }        
     if (myReport.reportElement[nf].getFieldHalign()!=null && !myReport.reportElement[nf].getFieldHalign().isEmpty() ) {
     String Halign = myReport.reportElement[nf].getFieldHalign();

     if (Halign.equalsIgnoreCase("CENTER")) cell.setHorizontalAlignment(Element.ALIGN_CENTER);
     if (Halign.equalsIgnoreCase("RIGHT")) cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                             
        
     }


     cell.setColspan(cols);//quante colonne occupa questa frase
     // cell.setRowspan(rows);//quante righe occupa questa frase
     cell.setBorder(Rectangle.NO_BORDER);
     cell.setLeading(3f, 1.2f);
     int borderDef=0;
        
     try{
     if (myReport.reportElement[nf].getFieldBorders().substring(0, 1).equalsIgnoreCase("1") ) borderDef+=  Rectangle.TOP  ;
     }catch (Exception ex){
            
     }
     try{
     if (myReport.reportElement[nf].getFieldBorders().substring(1, 2).equalsIgnoreCase("1") ) borderDef+=   Rectangle.BOTTOM ;
     }catch (Exception ex){
            
     }
     try{
     if (myReport.reportElement[nf].getFieldBorders().substring(2, 3).equalsIgnoreCase("1") ) borderDef+=   Rectangle.LEFT ;
        
     }catch (Exception ex){
            
     }
     try{
     if (myReport.reportElement[nf].getFieldBorders().substring(3, 4).equalsIgnoreCase("1") ) borderDef+=  Rectangle.RIGHT ;        
     }catch (Exception ex){
            
     }
        
     if (borderDef>0){
         
     cell.setBorder(borderDef);
     }
     table.addCell(cell);
         
        
                                       
     } else
     //--------------------------------------------------------------------------------------------------
     if (myReport.reportElement[nf].getFieldType().equalsIgnoreCase("text")){
     elementiInviabili++; 
     String ValoreDaScrivere="";  
     elementiInviabili++;
     try {     
     String valoreLetto=rs.getString(myReport.reportElement[nf].getFieldName());
     if(valoreLetto.equalsIgnoreCase("null")) valoreLetto="";
     myReport.reportElement[nf].setFieldValue(valoreLetto);
     ValoreDaScrivere=valoreLetto;
                                
     } catch (Exception ex) {
     myReport.reportElement[nf].setFieldValue("");
     ValoreDaScrivere="";
     }        
     //System.out.println("Trovato :"+ rs.getString(myReport.reportElement[nf].getFieldName()));
                          
     FontSelector selector1 = new FontSelector();
     Font f1 = FontFactory.getFont(FontFactory.HELVETICA, 12);
     f1.setColor(BaseColor.BLACK);
     try{ 
     fontsize=Integer.parseInt(myReport.reportElement[nf].getFieldFontSize());
     }catch (Exception ex){
     fontsize=12;
     }
     f1.setSize(fontsize);
     selector1.addFont(f1);
     Phrase ph = selector1.process(ValoreDaScrivere);
     document.add(new Paragraph(ph));
                                       
     } else
     //--------------------------------------------------------------------------------------------------
     if (myReport.reportElement[nf].getFieldType().equalsIgnoreCase("label")){
     elementiInviabili++; 
     String ValoreDaScrivere="";  
     elementiInviabili++;
     try {     
     String valoreLetto=myReport.reportElement[nf].getFieldValue();
     if(valoreLetto.equalsIgnoreCase("null")) valoreLetto="";                                 
     ValoreDaScrivere=valoreLetto;                                
     } catch (Exception ex) {                                 
     ValoreDaScrivere="";
     }        
     //System.out.println("Trovato :"+ rs.getString(myReport.reportElement[nf].getFieldName()));
     FontSelector selector1 = new FontSelector();
     Font f1 = FontFactory.getFont(FontFactory.HELVETICA, 12);
     f1.setColor(BaseColor.BLACK);
     try{ 
     fontsize=Integer.parseInt(myReport.reportElement[nf].getFieldFontSize());
     }catch (Exception ex){
     fontsize=12;
     }
     f1.setSize(fontsize);
     selector1.addFont(f1);
     Phrase ph = selector1.process(ValoreDaScrivere);
     document.add(new Paragraph(ph));
                                       
     } else                   
     //------------------------------------------------------------------------------------------------------------
     if (myReport.reportElement[nf].getFieldType().equalsIgnoreCase("report")){
                         
     String jSonName =myReport.reportElement[nf].getFieldValue(); 
     System.out.println("Creo un sottoreport dal jSon :"+ jSonName);
     // qui posso passare al sottoreport una serie di variabili dinamiche 
     // sotto forma di singola riga SQL
     // es.  pino= pinoAttiale AND  peso>pesoAttuale AND  Altezza < altezzaAttuale
     // questa phrase la devo costruire ora perchè i valori dinamici li so solo adesso...
     //i valori dinamici li trovo in rs
     // String valoreLetto=rs.getString(fieldName);
     String parametriSottoreport=null; 
     // System.out.println("PARAMETRI SOTTOREPORT:" + parametriSottoreport); e fieldChildParamsValues contengono i valori per creare il params del sottoreport
     //System.out.println("getFieldChildParamsList():" + myReport.reportElement[nf].getFieldChildParamsList());
     //System.out.println("getFieldChildParamsValues():" + myReport.reportElement[nf].getFieldChildParamsValues());
                         
                         
     String [] filtVariabile = new String [100];
     String [] filtValore = new String [100];
     String varX = myReport.reportElement[nf].getFieldChildParamsList() +",";
     int nofParams=0;                          
     for ( String retval: varX.split(",")){
     nofParams ++;
     filtVariabile[nofParams]= retval;
     System.out.println("PARTE TROVATA:" + retval);
     }
     varX = myReport.reportElement[nf].getFieldChildParamsValues()+","; 
     int nofValues=0;
     for ( String retval: varX.split(",")){
     nofValues ++;
     filtValore[nofValues]= retval;
     System.out.println("PARTE TROVATA:" + retval);
                       
     // questi valori possono essere di 3 tipi: 
     //FUNCT: vado a prendere una funzione standard dal ReportFunctionServer passando come parametro quello che segue FUNC:
     //FIELD: prendo il valore da un campo
     //VALUE: uso il valore che mi viene passato
     int flag=0;
     if (retval.length()>=6){
     // esiste il prefisso che classifica il valore
     String varZ= retval.substring(0, 6);
     if (varZ.equalsIgnoreCase("FUNCT:")){
     // caso function
                                    
     flag=1;  
     }else if (varZ.equalsIgnoreCase("VALUE:")){
     // caso valore
                                    
     flag=2;   
     }else if (varZ.equalsIgnoreCase("FIELD:")){
     // per default cerco il campo. mi basta tenere flag a 0;
     // Qui dovrei eliminare la scritta FIELD:
     }
                                   
     }
     if (flag == 0){
     // caso field
     // suppongo ci sia solo il nome del field
     try{
     System.out.println("cerco CAMPO :" + retval);
     filtValore[nofValues]= rs.getString(retval);
     } catch (Exception ex){
     filtValore[nofValues]=""; 
     } 
                                
     }
                            
                            
                            
                            
     }
     parametriSottoreport="";
     // System.out.println("trovate " + nofValues + " COMPONENTI DI FILTRO");
     for (int jj =1; jj <= nofValues; jj++) {
     if (filtVariabile[jj].length()>0 &&  filtValore[jj].length()>0 ){
     if (parametriSottoreport.length()>0)  parametriSottoreport +=" "; // L'EVENTUALE 'and' O 'OR' VA MESSO NEI PARAMETRI
     parametriSottoreport +=filtVariabile[jj] + filtValore[jj];
     int lunghezzaFiltro=filtVariabile[jj].length() -1 ;
     if (filtVariabile[jj].substring(lunghezzaFiltro).equalsIgnoreCase("'")){
     parametriSottoreport += "'"; // gli AND OR = >= li devo mettere nei parametri  
     }
     }
     }
                        
                        
     // System.out.println("PARAMETRI SOTTOREPORT:" + parametriSottoreport);
     new reportCDR().createPdf( stream, myContext, currentKEY, currentDATE, parametriSottoreport, jSonName, document);
 
                                       
     }
                    
                    
     } // fine if not fuorui intestazione     

     } // fine for nf
   
     } catch (Exception ex) {
     }    
     
     }
     */
    class myCell {

        public PdfPCell configCell(PdfPCell cell, String phrase, int size, int cols) {
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
            cell = new PdfPCell(new Phrase(phQ));
            cell.setColspan(cols);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.BOX);
            cell.setLeading(3f, 1.2f);

            return cell;
        }

        public PdfPCell configCell(String phrase, int size, int cols, int alignment, BaseColor textColor, BaseColor bgColor, int borderWidth) {
            PdfPCell cell = null;
            if (phrase == null) {
                phrase = "";
            }
            FontSelector selectorQ = new FontSelector();
            Font fQ = FontFactory.getFont(FontFactory.HELVETICA, 18);
            fQ.setColor(textColor);

            fQ.setSize(size);
            fQ.setStyle("plain");
            selectorQ.addFont(fQ);
            Phrase phQ = selectorQ.process(phrase);
            cell = new PdfPCell(new Phrase(phQ));
            cell.setHorizontalAlignment(alignment);// es. Element.ALIGN_LEFT
            cell.setColspan(cols);
            cell.setBackgroundColor(bgColor);
            //cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.BOX);
            cell.setBorderWidth(borderWidth);
            cell.setLeading(3f, 1.2f);

            return cell;
        }
    }

}
