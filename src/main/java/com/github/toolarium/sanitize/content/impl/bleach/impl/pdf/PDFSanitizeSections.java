/*
 * PDFSanitizeSections.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl.bleach.impl.pdf;

import com.github.toolarium.sanitize.content.impl.ISanitizeContentThreatRegistry;

/**
 * Defines the PDF sanitize sections
 *
 * @author Patrick Meier
 */
public enum PDFSanitizeSections implements ISanitizeContentThreatRegistry.ISection {
    FORM_ADDITIONAL_ACTION,
    PAGE_ACTION,
    DOCUMENT_CATALOG_ADDITIONAL_ACTION,
    DOCUMENT_CATALOG_ACTION,
    DOCUMENT_OUTLINE_ITEM_ACTION,
    ANNOTATION_ACTION,
    NAMES_JAVASCRIPT_ACTION;
}
