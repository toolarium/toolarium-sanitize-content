/*
 * PDFSanitizeUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.pdf.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.sanitize.content.SanitizeContentFactory;
import com.github.toolarium.sanitize.content.dto.SanitizeContentResult;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeContentBleacher;
import com.github.toolarium.sanitize.content.impl.bleach.util.StreamUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PDF sanitize test utility 
 * 
 * @author Patrick Meier
 */
public final class PDFSanitizeTestUtil {
    /** BASEPATH */
    public static final String BASEPATH = "build/temp";
    
    private static final Logger LOG = LoggerFactory.getLogger(PDFSanitizeTestUtil.class);
    
    

    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author Patrick Meier
     */
    private static class HOLDER {
        static final PDFSanitizeTestUtil INSTANCE = new PDFSanitizeTestUtil();
    }

    
    /**
     * Constructor
     */
    private PDFSanitizeTestUtil() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static PDFSanitizeTestUtil getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Sanitize test
     *
     * @param testcaseName the testcase name
     * @param document the document
     * @return the result
     * @throws IOException In case of an I/O error
     */
    public SanitizeContentResult sanitize(String testcaseName, PDDocument document) throws IOException {
        new File(BASEPATH).mkdirs();
        byte[] pdfContent = PDFUtil.getInstance().getPDFDocument(document);
        File file = PDFUtil.getInstance().writePDFDocument(document, Paths.get(BASEPATH, testcaseName + "-org.pdf").toFile()); 
        return sanitize(file.getName(), testcaseName + "-sanitized.pdf", new ByteArrayInputStream(pdfContent));
    }

    
    /**
     * Sanitize test
     *
     * @param sourceFilename the source file nane
     * @param sanitizeFilename the sanitize filename
     * @param is the input stream
     * @return the result
     * @throws IOException In case of an I/O error
     */
    public SanitizeContentResult sanitize(String sourceFilename, String sanitizeFilename, InputStream is) throws IOException {
        new File(BASEPATH).mkdirs();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SanitizeContentResult result = SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize(sourceFilename, is, os, null);
        LOG.info("Sanitizing result: " + result);

        StreamUtils.getInstance().save(new ByteArrayInputStream(os.toByteArray()), Paths.get(BASEPATH, sanitizeFilename).toFile());
        
        assertValidPDF(os.toByteArray());
        return result;
    }


    /**
     * Assert valid PDF file
     *
     * @param content the pdf content
     * @return the result
     * @throws IOException In case of an I/O error
     */
    public SanitizeContentResult assertValidPDF(byte[] content) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SanitizeContentResult result = SanitizeContentFactory.getInstance().getSanitizeContentProcessor().sanitize("", new ByteArrayInputStream(content), os, null);
        LOG.info("Sanitizing result: " + result);

        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertFalse(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertTrue(result.getThreadInformationList().isEmpty());
        return result;
    }
}
