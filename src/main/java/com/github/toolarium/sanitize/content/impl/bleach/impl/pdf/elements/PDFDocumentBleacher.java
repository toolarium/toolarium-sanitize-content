/*
 * PDFDocumentBleacher.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.elements;

import com.github.toolarium.sanitize.content.impl.ISanitizeContentThreatRegistry;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFContentUtil;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeSections;
import java.io.IOException;
import java.util.Iterator;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDDocumentCatalogAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDPageAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a PDF document bleacher
 *
 * @author Patrick Meier
 */
public class PDFDocumentBleacher {
    private static final Logger LOG = LoggerFactory.getLogger(PDFDocumentBleacher.class);
    private ISanitizeContentThreatRegistry threatRegistry;


    /**
     * Constructor for PDFDocumentBleacher
     *
     * @param threatRegistry the thread registry
     */
    public PDFDocumentBleacher(ISanitizeContentThreatRegistry threatRegistry) {
        this.threatRegistry = threatRegistry;
    }


    /**
     * Sanitize the PDF document catalog
     *
     * @param docCatalog the document catalog
     */
    public void sanitize(PDDocumentCatalog docCatalog) {
        if (docCatalog == null) {
            return;
        }

        sanitizeOpenAction(docCatalog);
        sanitizeDocumentActions(docCatalog.getActions());
        sanitizePageActions(docCatalog.getPages());
        sanitizeAcroFormActions(docCatalog.getAcroForm());
    }


    /**
     * Sanitize the PDF document outline
     *
     * @param documentOutline the PDF document outline
     */
    public void sanitize(PDDocumentOutline documentOutline) {
        if (documentOutline == null) {
            return;
        }

        for (Iterator<PDOutlineItem> it = documentOutline.children().iterator(); it.hasNext();) {
            PDOutlineItem outlineItem = it.next();

            if (outlineItem.getAction() != null) {
                threatRegistry.registerThreat(PDFSanitizeSections.DOCUMENT_OUTLINE_ITEM_ACTION, "Action", PDFContentUtil.getInstance().convert(outlineItem.getAction()));
                outlineItem.setAction(null);
            }
        }
    }


    /**
     * Sanitize open action
     *
     * @param docCatalog the catalog
     */
    protected void sanitizeOpenAction(PDDocumentCatalog docCatalog) {
        try {
            PDDestinationOrAction openAction = docCatalog.getOpenAction();
            if (openAction == null) {
                return;
            }

            threatRegistry.registerThreat(PDFSanitizeSections.DOCUMENT_CATALOG_ACTION, "OpenAction", PDFContentUtil.getInstance().convert(openAction));

            docCatalog.setOpenAction(null);
        } catch (IOException e) {
            // NOP
        }
    }


    /**
     * Sanitize document actions
     *
     * @param documentActions the document actions
     */
    protected void sanitizeDocumentActions(PDDocumentCatalogAdditionalActions documentActions) {
        if (documentActions == null) {
            return;
        }

        LOG.debug("Checking additional actions...");
        if (documentActions.getDP() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.DOCUMENT_CATALOG_ADDITIONAL_ACTION, "Action after printing", PDFContentUtil.getInstance().convert(documentActions.getDP()));
            documentActions.setDP(null);
        }

        if (documentActions.getDS() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.DOCUMENT_CATALOG_ADDITIONAL_ACTION, "Action after saving", PDFContentUtil.getInstance().convert(documentActions.getDS()));
            documentActions.setDS(null);
        }

        if (documentActions.getWC() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.DOCUMENT_CATALOG_ADDITIONAL_ACTION, "Action before closing", PDFContentUtil.getInstance().convert(documentActions.getWC()));
            documentActions.setWC(null);
        }

        if (documentActions.getWP() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.DOCUMENT_CATALOG_ADDITIONAL_ACTION, "Action before printing", PDFContentUtil.getInstance().convert(documentActions.getWP()));
            documentActions.setWP(null);
        }

        if (documentActions.getWS() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.DOCUMENT_CATALOG_ADDITIONAL_ACTION, "Action before saving", PDFContentUtil.getInstance().convert(documentActions.getWS()));
            documentActions.setWS(null);
        }
    }


    /**
     * Sanitize the page actions
     *
     * @param pages the pages
     */
    protected void sanitizePageActions(PDPageTree pages) {
        if (pages == null) {
            return;
        }

        LOG.debug("Checking Pages Actions");
        for (PDPage page : pages) {
            sanitizePageActions(page.getActions());

            try {
                for (PDAnnotation annotation : page.getAnnotations()) {
                    sanitizeAnnotation(annotation);
                }
            } catch (IOException e) {
                // NOP
            }
        }
    }


    /**
     * Sanitize page action
     *
     * @param pageActions the page action
     */
    protected void sanitizePageActions(PDPageAdditionalActions pageActions) {
        if (pageActions == null) {
            return;
        }

        LOG.debug("Checking page actions...");
        if (pageActions.getC() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.PAGE_ACTION, "Action when page is closed", PDFContentUtil.getInstance().convert(pageActions.getC()));
            pageActions.setC(null);
        }

        if (pageActions.getO() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.PAGE_ACTION, "Action when page is opened", PDFContentUtil.getInstance().convert(pageActions.getO()));
            pageActions.setO(null);
        }
    }


    /**
     * Sanitize the acro form actions
     *
     * @param acroForm the acro form actions
     */
    protected void sanitizeAcroFormActions(PDAcroForm acroForm) {
        if (acroForm == null) {
            return;
        }

        LOG.debug("Checking AcroForm actions...");
        for (Iterator<PDField> it = acroForm.getFieldIterator(); it.hasNext();) {
            PDField field = it.next();

            if (field != null) {
                // sanitize annotations
                for (PDAnnotationWidget annotationWidget : field.getWidgets()) {
                    sanitizeAnnotation(annotationWidget);
                }

                // sanitize field actions
                sanitizeFieldAdditionalActions(field.getActions());
            }
        }
    }


    /**
     * Sanitize the field actions
     *
     * @param fieldActions the field actions
     */
    protected void sanitizeFieldAdditionalActions(PDFormFieldAdditionalActions fieldActions) {
        if (fieldActions == null) {
            return;
        }

        LOG.debug("Checking field actions...");
        if (fieldActions.getC() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.FORM_ADDITIONAL_ACTION, "Action on value change", PDFContentUtil.getInstance().convert(fieldActions.getC()));
            fieldActions.setC(null);
        }

        if (fieldActions.getF() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.FORM_ADDITIONAL_ACTION, "Action to format the value", PDFContentUtil.getInstance().convert(fieldActions.getF()));
            fieldActions.setF(null);
        }

        if (fieldActions.getK() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.FORM_ADDITIONAL_ACTION, "Action when the user types a keystoke", PDFContentUtil.getInstance().convert(fieldActions.getK()));
            fieldActions.setK(null);
        }

        if (fieldActions.getV() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.FORM_ADDITIONAL_ACTION, "Action when the field's value is changed", PDFContentUtil.getInstance().convert(fieldActions.getV()));
            fieldActions.setV(null);
        }
    }


    /**
     * Sanitize the annotation
     *
     * @param annotation the annotation
     */
    public void sanitizeAnnotation(PDAnnotation annotation) {
        if (annotation == null) {
            return;
        }

        if (annotation instanceof PDAnnotationLink) {
            PDAnnotationLink annotationLink = (PDAnnotationLink) annotation;
            if (annotationLink.getAction() != null) {
                threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION, "External link", PDFContentUtil.getInstance().convert(annotationLink.getAction()));
                annotationLink.setAction(null);
            }
        }

        if (annotation instanceof PDAnnotationWidget) {
            PDAnnotationWidget annotationWidget = (PDAnnotationWidget) annotation;
            if (annotationWidget.getAction() != null) {
                threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION, "External widget", PDFContentUtil.getInstance().convert(annotationWidget.getAction()));
                annotationWidget.setAction(null);
            }

            sanitizeAnnotationActions(annotationWidget.getActions());
        }
    }


    /**
     * Sanitize annotation actions
     *
     * @param annotationAdditionalActions the annotatin actions
     */
    protected void sanitizeAnnotationActions(PDAnnotationAdditionalActions annotationAdditionalActions) {
        if (annotationAdditionalActions == null) {
            return;
        }

        if (annotationAdditionalActions.getBl() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION,
                    "Action on annotation widget to be performed when annotation loses the input focus",
                    PDFContentUtil.getInstance().convert(annotationAdditionalActions.getBl()));
            annotationAdditionalActions.setBl(null);
        }

        if (annotationAdditionalActions.getD() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION,
                    "Action on annotation widget to be performed when mouse button is pressed inside the annotation's active area",
                    PDFContentUtil.getInstance().convert(annotationAdditionalActions.getD()));
            annotationAdditionalActions.setD(null);
        }

        if (annotationAdditionalActions.getE() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION,
                    "Action on annotation widget to be performed when the cursor enters the annotation's active area",
                    PDFContentUtil.getInstance().convert(annotationAdditionalActions.getE()));
            annotationAdditionalActions.setE(null);
        }

        if (annotationAdditionalActions.getFo() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION,
                    "Action on annotation widget to be performed  when the annotation receives the input focus",
                    PDFContentUtil.getInstance().convert(annotationAdditionalActions.getFo()));
            annotationAdditionalActions.setFo(null);
        }

        if (annotationAdditionalActions.getPC() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION,
                    "Action on annotation widget to be performed when the page containing the annotation is closed",
                    PDFContentUtil.getInstance().convert(annotationAdditionalActions.getPC()));
            annotationAdditionalActions.setPC(null);
        }

        if (annotationAdditionalActions.getPI() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION,
                    "Action on annotation widget to be performed when the page containing the annotation is no longer visible",
                    PDFContentUtil.getInstance().convert(annotationAdditionalActions.getPI()));
            annotationAdditionalActions.setPI(null);
        }

        if (annotationAdditionalActions.getPO() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION,
                    "Action on annotation widget to be performed when the page containing the annotation is opened",
                    PDFContentUtil.getInstance().convert(annotationAdditionalActions.getPO()));
            annotationAdditionalActions.setPO(null);
        }

        if (annotationAdditionalActions.getPV() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION,
                    "Action on annotation widget to be performed the page containing the annotation becomes visible",
                    PDFContentUtil.getInstance().convert(annotationAdditionalActions.getPV()));
            annotationAdditionalActions.setPV(null);
        }

        if (annotationAdditionalActions.getU() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION,
                    "Action on annotation widget to be performed when the mouse button is released inside the annotation's active area",
                    PDFContentUtil.getInstance().convert(annotationAdditionalActions.getU()));
            annotationAdditionalActions.setU(null);
        }

        if (annotationAdditionalActions.getX() != null) {
            threatRegistry.registerThreat(PDFSanitizeSections.ANNOTATION_ACTION,
                    "Action on annotation widget to be performed when the cursor exits the annotation's active area",
                    PDFContentUtil.getInstance().convert(annotationAdditionalActions.getX()));
            annotationAdditionalActions.setX(null);
        }
    }
}
