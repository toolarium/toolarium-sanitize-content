/*
 * PDFEmbeddedFileTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.sanitize.content.dto.SanitizeContentResult;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeContentBleacher;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeSections;
import com.github.toolarium.sanitize.content.impl.bleach.util.StreamUtils;
import com.github.toolarium.sanitize.content.pdf.util.PDFSanitizeTestUtil;
import com.github.toolarium.sanitize.content.pdf.util.PDFUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test embedded file
 * 
 * @author Patrick Meier
 */
public class PDFEmbeddedFileTest {
    private static final Logger LOG = LoggerFactory.getLogger(PDFEmbeddedFileTest.class);
    
    /**
     * Test embedded PDF tree
     * 
     * @throws IOException In case of an I/O error 
     */
    @Test
    public void validateEmbeddedPDFTree() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamUtils.getInstance().copy(new FileInputStream(Paths.get("src/test/resources", "test.pdf").toFile()), os);
        PDFSanitizeTestUtil.getInstance().assertValidPDF(os.toByteArray());
    }

    
    /**
     * Test embedded PDF tree
     * 
     * @throws IOException In case of an I/O error 
     */
    @Test
    public void sanitizeEmbeddedPDFTree() throws IOException {
        
        SanitizeContentResult result = PDFSanitizeTestUtil.getInstance().sanitize("test-open-action.pdf", "test-open-action-tree-sanitized.pdf", 
                new FileInputStream(Paths.get("src/test/resources", "test-open-action.pdf").toFile()));
        
        LOG.info("Sanitizing result: " + result);

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
     * Test valid pdf file
     * 
     * @throws IOException In case of an I/O error 
     */
    @Test
    public void testValidPDFFile() throws IOException {
        // create pdf file
        PDDocument embeddedPDF = PDFUtil.getInstance().createPDFDocument();
        PDPageContentStream embeddedStream = PDFUtil.getInstance().addContentStream(embeddedPDF);
        embeddedPDF.getDocumentCatalog().setOpenAction(PDFUtil.getInstance().createAlertAction());
        PDFUtil.getInstance().addText(embeddedStream);
        PDFUtil.getInstance().close(embeddedStream);
        byte[] pdfContent = PDFUtil.getInstance().getPDFDocument(embeddedPDF);

        // prepare embedded pdf
        PDEmbeddedFile embeddedFile = new PDEmbeddedFile(embeddedPDF);
        embeddedFile.setSize(pdfContent.length);
        embeddedFile.setModDate(Calendar.getInstance());
        embeddedFile.setSubtype(PDFSanitizeContentBleacher.APPLICATION_PDF);

        PDComplexFileSpecification spec = new PDComplexFileSpecification();
        spec.setFile("embeddedPDFTestFile.pdf");
        spec.setEmbeddedFile(embeddedFile);

        OutputStream os = embeddedFile.createOutputStream();
        StreamUtils.getInstance().copy(new ByteArrayInputStream(pdfContent), os);
        os.flush();
        os.close();

        // create parent pdf
        PDDocument document = PDFUtil.getInstance().createPDFDocument();
        PDDocumentNameDictionary documentNameDictionary = new PDDocumentNameDictionary(document.getDocumentCatalog());
        
        // add tree node and reference file
        PDEmbeddedFilesNameTreeNode treeNode = new PDEmbeddedFilesNameTreeNode();
        Map<String, PDComplexFileSpecification> nameMap = new LinkedHashMap<String, PDComplexFileSpecification>();
        nameMap.put(spec.getFile(), spec);
        treeNode.setNames(nameMap);
        documentNameDictionary.setEmbeddedFiles(treeNode);
        document.getDocumentCatalog().setNames(documentNameDictionary);
        
        PDPageContentStream stream = PDFUtil.getInstance().addContentStream(document);        
        PDFUtil.getInstance().addText(stream);
        PDFUtil.getInstance().close(stream);
        
        // sanitize
        SanitizeContentResult result = PDFSanitizeTestUtil.getInstance().sanitize("test-embeddedfile", document);
        embeddedPDF.close();
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
}
