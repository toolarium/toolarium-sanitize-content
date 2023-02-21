/*
 * SanitizeContentTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.sanitize.content.dto.SanitizeContentCredentialAccess;
import com.github.toolarium.sanitize.content.dto.SanitizeContentResult;
import com.github.toolarium.sanitize.content.exception.SanitizeContentException;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeContentBleacher;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeSections;
import com.github.toolarium.sanitize.content.pdf.util.PDFUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;


/**
 * Sanitizing content test. 
 * 
 * @author Patrick Meier
 */
public class SanitizeContentTest {

    private static final String BUILD = "build";
    private static final String SRC_TEST_RESOURCES = "src/test/resources";


    /**
     * Test the PDF file usage
     * 
     * @throws FileNotFoundException In case the file could not be found 
     * @throws SanitizeContentException In case of a sanitizing error
     */
    @Test
    public void usagePDFTest() throws SanitizeContentException, FileNotFoundException {
        
        String filename = "test.pdf";
        SanitizeContentResult result = SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize(filename, 
                new FileInputStream(Paths.get(SRC_TEST_RESOURCES,filename).toFile()),
                new FileOutputStream(Paths.get(BUILD, filename).toFile()), 
                null);
        
        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertFalse(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertTrue(result.getThreadInformationList().isEmpty());
    }


    /**
     * Test the PDF file needs to be sanitized 
     * 
     * @throws FileNotFoundException In case the file could not be found 
     * @throws SanitizeContentException In case of a sanitizing error
     */
    @Test
    public void testFileNeedsToBeSanitized() throws SanitizeContentException, FileNotFoundException {
        
        String filename = "FileNeedsToBeSanitized.pdf";
        SanitizeContentResult result = SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize(filename, 
                new FileInputStream(Paths.get(SRC_TEST_RESOURCES,filename).toFile()),
                new FileOutputStream(Paths.get(BUILD, filename).toFile()), 
                null);
        
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
     * Test the PDF file needs to be sanitized 
     * 
     * @throws FileNotFoundException In case the file could not be found 
     * @throws SanitizeContentException In case of a sanitizing error
     */
    @Test
    public void testFileNeedsToBeSanitized2() throws SanitizeContentException, FileNotFoundException {
        
        String filename = "FileNeedsToBeSanitized2.pdf"; // sample from formatix.de/wp-content/uploads/2019/04/Rentenversicherungsnummer.pdf
        SanitizeContentResult result = SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize(filename, 
                new FileInputStream(Paths.get(SRC_TEST_RESOURCES,filename).toFile()),
                new FileOutputStream(Paths.get(BUILD, filename).toFile()), 
                null);
        
        assertTrue(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertEquals(14, result.getThreadInformationList().size());
        
        assertEquals(PDFSanitizeSections.NAMES_JAVASCRIPT_ACTION.name(), result.getThreadInformationList().get(0).getSection());
        assertEquals("Action", result.getThreadInformationList().get(0).getDescription());
        assertNull(result.getThreadInformationList().get(0).getActionCode());
        
        assertEquals(PDFSanitizeSections.PAGE_ACTION.name(), result.getThreadInformationList().get(1).getSection());
        assertEquals("Action when page is opened", result.getThreadInformationList().get(1).getDescription());
        //assertEquals("CS_PDF_Util2_OnFormLoad();\napp.focusRect=true\n", result.getThreadInformationList().get(1).getActionCode());
        
        assertEquals(PDFSanitizeSections.ANNOTATION_ACTION.name(), result.getThreadInformationList().get(2).getSection());
        assertEquals("Action on annotation widget to be performed when annotation loses the input focus", result.getThreadInformationList().get(2).getDescription());
        
        assertEquals(PDFSanitizeSections.ANNOTATION_ACTION.name(), result.getThreadInformationList().get(3).getSection());
        assertEquals("Action on annotation widget to be performed when annotation loses the input focus", result.getThreadInformationList().get(3).getDescription());
        
        assertEquals(PDFSanitizeSections.ANNOTATION_ACTION.name(), result.getThreadInformationList().get(4).getSection());
        assertEquals("External widget", result.getThreadInformationList().get(4).getDescription());
        
        assertEquals(PDFSanitizeSections.FORM_ADDITIONAL_ACTION.name(), result.getThreadInformationList().get(5).getSection());
        assertEquals("Action to format the value", result.getThreadInformationList().get(5).getDescription());
        
        assertEquals(PDFSanitizeSections.FORM_ADDITIONAL_ACTION.name(), result.getThreadInformationList().get(6).getSection());
        assertEquals("Action when the user types a keystoke", result.getThreadInformationList().get(6).getDescription());
        
        assertEquals(PDFSanitizeSections.FORM_ADDITIONAL_ACTION.name(), result.getThreadInformationList().get(7).getSection());
        assertEquals("Action when the field's value is changed", result.getThreadInformationList().get(7).getDescription());
        
        assertEquals(PDFSanitizeSections.FORM_ADDITIONAL_ACTION.name(), result.getThreadInformationList().get(8).getSection());
        assertEquals("Action to format the value", result.getThreadInformationList().get(8).getDescription());
        
        assertEquals(PDFSanitizeSections.FORM_ADDITIONAL_ACTION.name(), result.getThreadInformationList().get(9).getSection());
        assertEquals("Action when the user types a keystoke", result.getThreadInformationList().get(9).getDescription());
        
        assertEquals(PDFSanitizeSections.FORM_ADDITIONAL_ACTION.name(), result.getThreadInformationList().get(10).getSection());
        assertEquals("Action when the field's value is changed", result.getThreadInformationList().get(10).getDescription());
        
        assertEquals(PDFSanitizeSections.FORM_ADDITIONAL_ACTION.name(), result.getThreadInformationList().get(11).getSection());
        assertEquals("Action to format the value", result.getThreadInformationList().get(11).getDescription());
        
        assertEquals(PDFSanitizeSections.FORM_ADDITIONAL_ACTION.name(), result.getThreadInformationList().get(12).getSection());
        assertEquals("Action when the user types a keystoke", result.getThreadInformationList().get(12).getDescription());
        
        assertEquals(PDFSanitizeSections.FORM_ADDITIONAL_ACTION.name(), result.getThreadInformationList().get(13).getSection());
        assertEquals("Action when the field's value is changed", result.getThreadInformationList().get(13).getDescription());
    }

    
    /**
     * Test protected PDF file 
     * 
     * @throws FileNotFoundException In case the file could not be found 
     * @throws SanitizeContentException In case of a sanitizing error
     */
    @Test
    public void testPDFProtectedTest() throws SanitizeContentException, FileNotFoundException {

        Throwable exception = assertThrows(IOException.class, () -> {
            String filename = "test-protected.pdf";
            SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize(filename, 
                    new FileInputStream(Paths.get(SRC_TEST_RESOURCES,filename).toFile()),
                    new FileOutputStream(Paths.get(BUILD, filename).toFile()), 
                    null);
        });
        
        assertEquals("com.github.toolarium.sanitize.content.exception.SanitizeContentException: Invalid credentials!", exception.getMessage().replace("\r", ""));
    }

    
    /**
     * Test protected PDF file with credentials 
     * 
     * @throws FileNotFoundException In case the file could not be found 
     * @throws SanitizeContentException In case of a sanitizing error
     */
    @Test
    public void testPDFProtectedWithCredentialsTest() throws SanitizeContentException, FileNotFoundException {
        
        String filename = "test-protected.pdf";
        SanitizeContentResult result = SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize(filename, 
                new FileInputStream(Paths.get(SRC_TEST_RESOURCES,filename).toFile()),
                new FileOutputStream(Paths.get(BUILD, filename).toFile()), 
                new SanitizeContentCredentialAccess("1234"));
        
        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertTrue(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertEquals(1, result.getThreadInformationList().size());
        assertEquals(PDFSanitizeSections.DOCUMENT_CATALOG_ACTION.name(), result.getThreadInformationList().get(0).getSection());
        assertEquals("OpenAction", result.getThreadInformationList().get(0).getDescription());
    }
    

    /**
     * Test the PDF file usage
     * 
     * @throws FileNotFoundException In case the file could not be found 
     * @throws SanitizeContentException In case of a sanitizing error
     */
    @Test
    public void testInvalidFile() throws SanitizeContentException, FileNotFoundException {
        
        String filename = "InvalidFile.pdf";
        
        assertThrows(IOException.class, () -> {
            SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize(filename, 
                    new FileInputStream(Paths.get(SRC_TEST_RESOURCES,filename).toFile()),
                    new FileOutputStream(Paths.get(BUILD, filename).toFile()), 
                    null);
        });
    }


    /**
     * Test the PDF file usage
     * 
     * @throws FileNotFoundException In case the file could not be found 
     * @throws SanitizeContentException In case of a sanitizing error
     */
    @Test
    public void testPNGFile() throws SanitizeContentException, FileNotFoundException {
        
        String filename = "test.png";
        SanitizeContentResult result = SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize(filename, 
                new FileInputStream(Paths.get(SRC_TEST_RESOURCES,filename).toFile()),
                new FileOutputStream(Paths.get(BUILD, filename).toFile()), 
                null);
        
        assertNotNull(result);
        //assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertFalse(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertTrue(result.getThreadInformationList().isEmpty());
    }


    /**
     * Test the PDF file usage
     * 
     * @throws FileNotFoundException In case the file could not be found 
     * @throws SanitizeContentException In case of a sanitizing error
     */
    @Test
    public void testPageActionTest() throws SanitizeContentException, FileNotFoundException {
        
        String filename = "PageActionTest.pdf";
        SanitizeContentResult result = SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize(filename, 
                new FileInputStream(Paths.get(SRC_TEST_RESOURCES,filename).toFile()),
                new FileOutputStream(Paths.get(BUILD, filename).toFile()), 
                null);
        
        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertTrue(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertEquals(1, result.getThreadInformationList().size());
        assertEquals("DOCUMENT_CATALOG_ACTION", result.getThreadInformationList().get(0).getSection());
        assertEquals("OpenAction", result.getThreadInformationList().get(0).getDescription());
        assertEquals("", result.getThreadInformationList().get(0).getActionCode());
    }
}
