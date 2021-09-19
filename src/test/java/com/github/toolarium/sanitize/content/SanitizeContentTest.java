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
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.sanitize.content.dto.SanitizeContentResult;
import com.github.toolarium.sanitize.content.exception.SanitizeContentException;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeContentBleacher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

/**
 * 
 * 
 * @author Patrick Meier
 */
public class SanitizeContentTest {

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
                new FileInputStream(Paths.get("src/test/resources",filename).toFile()),
                new FileOutputStream(Paths.get("build", filename).toFile()), 
                null);
        
        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertFalse(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertTrue(result.getThreadInformationList().isEmpty());
    }
}
