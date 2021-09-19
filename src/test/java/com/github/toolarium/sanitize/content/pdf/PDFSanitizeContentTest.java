/*
 * FileSanitizerTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.sanitize.content.SanitizeContentFactory;
import com.github.toolarium.sanitize.content.dto.SanitizeContentResult;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeContentBleacher;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeSections;
import com.github.toolarium.sanitize.content.pdf.util.PDFSanitizeTestUtil;
import com.github.toolarium.sanitize.content.pdf.util.PDFUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PDF sanitize basic tests
 * 
 * @author Patrick Meier
 */
public class PDFSanitizeContentTest {
    private static final Logger LOG = LoggerFactory.getLogger(PDFSanitizeContentTest.class);
    
    
    /**
     * Test valid pdf file
     * 
     * @throws IOException In case of an I/O error 
     */
    @Test
    public void testValidPDFFile() throws IOException {
        PDDocument document = PDFUtil.getInstance().createPDFDocument();
        PDPageContentStream stream = PDFUtil.getInstance().addContentStream(document);        
        PDFUtil.getInstance().addText(stream);
        PDFUtil.getInstance().close(stream);
        
        SanitizeContentResult result = PDFSanitizeTestUtil.getInstance().sanitize("test", document);
        document.close();
        
        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertFalse(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertTrue(result.getThreadInformationList().isEmpty());
    }

    
    /**
     * Test valid pdf file with javascript action
     * 
     * @throws IOException In case of an I/O error 
     */
    @Test
    public void testPDFFileWithJavascript() throws IOException {
        PDDocument document = PDFUtil.getInstance().createPDFDocument();
        document.getDocumentCatalog().setOpenAction(PDFUtil.getInstance().createAlertAction());

        PDPageContentStream stream = PDFUtil.getInstance().addContentStream(document);        
        PDFUtil.getInstance().addText(stream);
        PDFUtil.getInstance().close(stream);
        
        SanitizeContentResult result = PDFSanitizeTestUtil.getInstance().sanitize("test-js", document);
        document.close();
        
        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertTrue(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertEquals(1, result.getThreadInformationList().size());
        assertEquals(PDFSanitizeSections.DOCUMENT_CATALOG_ACTION.name(), result.getThreadInformationList().get(0).getSection());
        assertEquals("OpenAction", result.getThreadInformationList().get(0).getDescription());
        assertEquals(PDFUtil.getInstance().createAlertAction().getAction(), result.getThreadInformationList().get(0).getActionCode());
    }

    
    /**
     * Test invalid input
     * 
     * @throws IOException In case of an I/O error 
     */
    @Test
    public void testInvalidInput() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SanitizeContentResult result = SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize("invalid", new ByteArrayInputStream("NO_PDF_CONTENT".getBytes()), os, null);
        LOG.info("Sanitizing result: " + result);
        
        assertNotNull(result);
        assertNull(result.getContentType());
        assertFalse(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertTrue(result.getThreadInformationList().isEmpty());
    }
}
