/*
 * PDFSanitizeContentAnnotationTest.java
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
import com.github.toolarium.sanitize.content.pdf.util.PDFSanitizeTestUtil;
import com.github.toolarium.sanitize.content.pdf.util.PDFUtil;
import java.io.IOException;
import java.util.Arrays;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDDocumentCatalogAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDPageAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.jupiter.api.Test;



/**
 * Test all PDF actions
 * 
 * @author Patrick Meier
 */
public class PDFSanitizeContentAnnotationTest {
    /**
     * Test open action
     * 
     * @throws IOException In case of an I/O error 
     */
    @Test
    public void sanitizeOpenAction() throws IOException {
        PDDocument document = PDFUtil.getInstance().createPDFDocument();
        document.getDocumentCatalog().setOpenAction(PDFUtil.getInstance().createAlertAction());

        PDPageContentStream stream = PDFUtil.getInstance().addContentStream(document);        
        PDFUtil.getInstance().addText(stream);
        PDFUtil.getInstance().close(stream);
        
        SanitizeContentResult result = PDFSanitizeTestUtil.getInstance().sanitize("test-open-action", document);
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
     * Test the document catalog additional actions
     *
     * @throws IOException In case of an I/O error
     */
    @Test
    void sanitizeDocumentCatalogAdditionalActions() throws IOException {
        PDDocumentCatalogAdditionalActions documentCatalogAdditionalActions = new PDDocumentCatalogAdditionalActions();
        documentCatalogAdditionalActions.setDP(PDFUtil.getInstance().createAlertAction());        
        documentCatalogAdditionalActions.setDS(PDFUtil.getInstance().createAlertAction());
        documentCatalogAdditionalActions.setWC(PDFUtil.getInstance().createAlertAction());
        documentCatalogAdditionalActions.setWP(PDFUtil.getInstance().createAlertAction());
        documentCatalogAdditionalActions.setWS(PDFUtil.getInstance().createAlertAction());
        
        PDDocument document = PDFUtil.getInstance().createPDFDocument();
        document.getDocumentCatalog().setActions(documentCatalogAdditionalActions);

        PDPageContentStream stream = PDFUtil.getInstance().addContentStream(document);
        PDFUtil.getInstance().addText(stream);
        PDFUtil.getInstance().close(stream);
        
        SanitizeContentResult result = PDFSanitizeTestUtil.getInstance().sanitize("test-documentcatalog-actions", document);
        document.close();

        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertTrue(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertEquals(5, result.getThreadInformationList().size());
        for (int i = 0; i < result.getThreadInformationList().size(); i++) {
            assertEquals(PDFSanitizeSections.DOCUMENT_CATALOG_ADDITIONAL_ACTION.name(), result.getThreadInformationList().get(i).getSection());
            
            if (i == 0) {
                assertEquals("Action after printing", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 1) {
                assertEquals("Action after saving", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 2) {
                assertEquals("Action before closing", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 3) {
                assertEquals("Action before printing", result.getThreadInformationList().get(i).getDescription());
            } else {
                assertEquals("Action before saving", result.getThreadInformationList().get(i).getDescription());
            }
            assertEquals(PDFUtil.getInstance().createAlertAction().getAction(), result.getThreadInformationList().get(i).getActionCode());
        }
    }

    
    /**
     * Test the page actions
     *
     * @throws IOException In case of an I/O error
     */
    @Test
    public void sanitizePageActions() throws IOException {
        PDPageAdditionalActions actions = new PDPageAdditionalActions();
        actions.setC(PDFUtil.getInstance().createAlertAction());
        actions.setO(PDFUtil.getInstance().createAlertAction());

        PDDocument document = PDFUtil.getInstance().createPDFDocument();
        PDPage page = PDFUtil.getInstance().addPage(document);
        page.setActions(actions);
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
        PDFUtil.getInstance().addText(stream);
        PDFUtil.getInstance().close(stream);

        SanitizeContentResult result = PDFSanitizeTestUtil.getInstance().sanitize("test-page-actions", document);
        document.close();

        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertTrue(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertEquals(2, result.getThreadInformationList().size());
        for (int i = 0; i < 2; i++) {
            assertEquals(PDFSanitizeSections.PAGE_ACTION.name(), result.getThreadInformationList().get(i).getSection());

            if (i == 0) {
                assertEquals("Action when page is closed", result.getThreadInformationList().get(i).getDescription());
            } else {
                assertEquals("Action when page is opened", result.getThreadInformationList().get(i).getDescription());
            }
            assertEquals(PDFUtil.getInstance().createAlertAction().getAction(), result.getThreadInformationList().get(i).getActionCode());
        }
    }
    
    
    /**
     * Test acro form actions
     * 
     * @throws IOException In case of an I/O error 
     */
    @Test
    public void sanitizeAcroFormActions() throws IOException {
        PDFormFieldAdditionalActions fieldActions = new PDFormFieldAdditionalActions();
        fieldActions.setC(PDFUtil.getInstance().createAlertAction());
        fieldActions.setF(PDFUtil.getInstance().createAlertAction());
        fieldActions.setK(PDFUtil.getInstance().createAlertAction());
        fieldActions.setV(PDFUtil.getInstance().createAlertAction());

        PDDocument document = PDFUtil.getInstance().createPDFDocument();
        PDAcroForm acroForm = new PDAcroForm(document);
        PDTextField field = new PDTextField(acroForm);
        acroForm.setFields(Arrays.asList(field));
        field.setActions(fieldActions);
        
        document.getDocumentCatalog().setAcroForm(acroForm);
        PDPageContentStream stream = PDFUtil.getInstance().addContentStream(document);
        PDFUtil.getInstance().addText(stream);
        PDFUtil.getInstance().close(stream);

        SanitizeContentResult result = PDFSanitizeTestUtil.getInstance().sanitize("test-acroform-actions", document);
        document.close();

        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertTrue(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertEquals(4, result.getThreadInformationList().size());
        for (int i = 0; i < result.getThreadInformationList().size(); i++) {
            assertEquals(PDFSanitizeSections.FORM_ADDITIONAL_ACTION.name(), result.getThreadInformationList().get(i).getSection());
            if (i == 0) {
                assertEquals("Action on value change", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 1) {
                assertEquals("Action to format the value", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 2) {
                assertEquals("Action when the user types a keystoke", result.getThreadInformationList().get(i).getDescription());
            } else {
                assertEquals("Action when the field's value is changed", result.getThreadInformationList().get(i).getDescription());
            }
            assertEquals(PDFUtil.getInstance().createAlertAction().getAction(), result.getThreadInformationList().get(i).getActionCode());
        }
    }


    /**
     * Test annotation actions
     * 
     * @throws IOException In case of an I/O error 
     */
    @Test
    public void sanitizeAnnotationActions() throws IOException {
        PDDocument document = PDFUtil.getInstance().createPDFDocument();
        
        PDAcroForm acroForm = new PDAcroForm(document);
        PDTextField field = new PDTextField(acroForm);
        acroForm.setFields(Arrays.asList(field));        
        PDAnnotationWidget externalWidget = new PDAnnotationWidget();
        externalWidget.setAction(PDFUtil.getInstance().createAlertAction());
        
        PDAnnotationAdditionalActions actions = new PDAnnotationAdditionalActions();
        actions.setBl(PDFUtil.getInstance().createAlertAction());
        actions.setD(PDFUtil.getInstance().createAlertAction());
        actions.setE(PDFUtil.getInstance().createAlertAction());
        actions.setFo(PDFUtil.getInstance().createAlertAction());
        actions.setPC(PDFUtil.getInstance().createAlertAction());
        actions.setPI(PDFUtil.getInstance().createAlertAction());
        actions.setPO(PDFUtil.getInstance().createAlertAction());
        actions.setPV(PDFUtil.getInstance().createAlertAction());
        actions.setU(PDFUtil.getInstance().createAlertAction());
        actions.setX(PDFUtil.getInstance().createAlertAction());        
        externalWidget.setActions(actions);
        
        field.setWidgets(Arrays.asList(externalWidget));

        PDPage page = PDFUtil.getInstance().addPage(document);
        PDAnnotationLink externalLink = new PDAnnotationLink();
        externalLink.setAction(PDFUtil.getInstance().createAlertAction());
        page.setAnnotations(Arrays.asList(externalLink));
        
        document.getDocumentCatalog().setAcroForm(acroForm);
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
        PDFUtil.getInstance().addText(stream);
        PDFUtil.getInstance().close(stream);

        SanitizeContentResult result = PDFSanitizeTestUtil.getInstance().sanitize("test-annotations-actions", document);
        document.close();

        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertTrue(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertEquals(12, result.getThreadInformationList().size());
        for (int i = 0; i < result.getThreadInformationList().size(); i++) {
            assertEquals(PDFSanitizeSections.ANNOTATION_ACTION.name(), result.getThreadInformationList().get(i).getSection());
            if (i == 0) {
                assertEquals("External link", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 1) {
                assertEquals("External widget", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 2) {
                assertEquals("Action on annotation widget to be performed when annotation loses the input focus", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 3) {
                assertEquals("Action on annotation widget to be performed when mouse button is pressed inside the annotation's active area", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 4) {
                assertEquals("Action on annotation widget to be performed when the cursor enters the annotation's active area", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 5) {
                assertEquals("Action on annotation widget to be performed  when the annotation receives the input focus", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 6) {
                assertEquals("Action on annotation widget to be performed when the page containing the annotation is closed", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 7) {
                assertEquals("Action on annotation widget to be performed when the page containing the annotation is no longer visible", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 8) {
                assertEquals("Action on annotation widget to be performed when the page containing the annotation is opened", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 9) {
                assertEquals("Action on annotation widget to be performed the page containing the annotation becomes visible", result.getThreadInformationList().get(i).getDescription());
            } else if (i == 10) {
                assertEquals("Action on annotation widget to be performed when the mouse button is released inside the annotation's active area", result.getThreadInformationList().get(i).getDescription());
            } else {
                assertEquals("Action on annotation widget to be performed when the cursor exits the annotation's active area", result.getThreadInformationList().get(i).getDescription());
            }
            
            assertEquals(PDFUtil.getInstance().createAlertAction().getAction(), result.getThreadInformationList().get(i).getActionCode());
        }
    }


    /**
     * Test the outline action
     * 
     * @throws IOException In case of an I/O error 
     */
    @Test
    public void sanitizeOutlineAction() throws IOException {
        PDDocument document = PDFUtil.getInstance().createPDFDocument();
        PDDocumentOutline outline = new PDDocumentOutline();
        PDOutlineItem item = new PDOutlineItem();
        outline.addFirst(item);
        item.setAction(PDFUtil.getInstance().createAlertAction());
        document.getDocumentCatalog().setDocumentOutline(outline);
        
        PDPageContentStream stream = PDFUtil.getInstance().addContentStream(document);        
        PDFUtil.getInstance().addText(stream);
        PDFUtil.getInstance().close(stream);
        
        SanitizeContentResult result = PDFSanitizeTestUtil.getInstance().sanitize("test-outline-action", document);
        document.close();

        assertNotNull(result);
        assertEquals(PDFSanitizeContentBleacher.APPLICATION_PDF, result.getContentType());
        assertTrue(result.isModifiedContent());
        assertNotNull(result.getThreadInformationList());
        assertEquals(1, result.getThreadInformationList().size());
        assertEquals(PDFSanitizeSections.DOCUMENT_OUTLINE_ITEM_ACTION.name(), result.getThreadInformationList().get(0).getSection());
        assertEquals("Action", result.getThreadInformationList().get(0).getDescription());
        assertEquals(PDFUtil.getInstance().createAlertAction().getAction(), result.getThreadInformationList().get(0).getActionCode());
    }
}
