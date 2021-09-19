/*
 * PDFContentUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.pdf.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;


/**
 * The PDF content utility
 * 
 * @author Patrick Meier
 */
public final class PDFUtil {

    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author Patrick Meier
     */
    private static class HOLDER {
        static final PDFUtil INSTANCE = new PDFUtil();
    }

    
    /**
     * Constructor
     */
    private PDFUtil() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static PDFUtil getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Create PDF document
     * 
     * @return document
     */
    public PDDocument createPDFDocument() {
        return new PDDocument();
    }


    /**
     * Add a page
     * 
     * @param document the document
     * @return the page
     */
    public PDPage addPage(PDDocument document) {
        PDPage page = new PDPage();
        document.addPage(page);
        return page;
    }


    /**
     * Create PDF file
     * 
     * @param document the document
     * @return the page content stream 
     * @throws IOException In case of an I/O error
     */
    public PDPageContentStream addContentStream(PDDocument document) throws IOException {
        PDPage page = addPage(document);
        
        // start a new content stream which will "hold" the to be created content
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

        return contentStream;
    }


    /**
     * Create PDF file
     * 
     * @param contentStream the stream
     * @return the stream
     * @throws IOException In case of an I/O error
     */
    public PDPageContentStream addText(PDPageContentStream contentStream) throws IOException {
        // create a new font object selecting one of the PDF base fonts
        PDFont font = PDType1Font.HELVETICA_BOLD;

        // Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Hello World");
        contentStream.endText();
        return contentStream;
    }

    
    /**
     * Close a stream
     * 
     * @param contentStream the stream
     * @throws IOException In case of an I/O error
     */
    public void close(PDPageContentStream contentStream) throws IOException {
        // make sure that the content stream is closed:
        contentStream.close();
    }


    /**
     * Get the PDF document
     *
     * @param document the document
     * @return the byte array
     * @throws IOException In case of an I/O error
     */
    public byte[] getPDFDocument(PDDocument document) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        document.save(output);
        return output.toByteArray();
    }

    
    /**
     * Get the PDF document
     *
     * @param document the document
     * @param file the file 
     * @return the byte array
     * @throws IOException In case of an I/O error
     */
    public File writePDFDocument(PDDocument document, File file) throws IOException {
        document.save(file);
        return file;
    }
    
    
    /**
     * Create alert action
     *
     * @return the alert action
     */
    public PDActionJavaScript createAlertAction() {
        return new PDActionJavaScript("app.alert({cMsg: \"Do you want to answer a question?\", cTitle: \"A message\", nIcon: 2, nType: 3});");        
    }


    /**
     * Create sample document
     *
     * @return the document
     * @throws IOException In case of an I/O error
     */
    public PDDocument createSampleDocument() throws IOException {
        // create sample document
        PDDocument document = createPDFDocument();
        PDPageContentStream stream = addContentStream(document);        
        addText(stream);
        stream.close();
        return document;
    }
}
