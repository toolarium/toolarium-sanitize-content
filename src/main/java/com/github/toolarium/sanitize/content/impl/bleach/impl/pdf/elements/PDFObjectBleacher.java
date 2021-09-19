/*
 * PDFObjectBleacher.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.elements;

import com.github.toolarium.sanitize.content.impl.ISanitizeContentThreatRegistry;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFContentUtil;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeSections;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDF object bleacher
 *
 * @author Patrick Meier
 */
public class PDFObjectBleacher {
    private static final Logger LOG = LoggerFactory.getLogger(PDFDocumentBleacher.class);
    private ISanitizeContentThreatRegistry threatRegistry;


    /**
     * Constructor for PDFObjectBleacher
     *
     * @param threatRegistry the thread registry
     */
    public PDFObjectBleacher(ISanitizeContentThreatRegistry threatRegistry) {
        this.threatRegistry = threatRegistry;
    }


    /**
     * Sanitize objects
     *
     * @param objects the objects
     */
    public void sanitizeObjects(Collection<COSObject> objects) {
        if (objects == null || objects.isEmpty()) {
            return;
        }

        LOG.debug("Checking all objects...");
        for (COSObject object : objects) {
            crawl(object);
        }
    }


    /**
     * Crawl all opbjects
     *
     * @param base the base
     */
    protected void crawl(COSBase base) {
        if (base == null) {
            return;
        }

        if (base instanceof COSName
                || base instanceof COSString || base instanceof COSStream || base instanceof COSNull
                || base instanceof COSObject || base instanceof COSNumber || base instanceof COSBoolean) {
            return;
        }

        if (base instanceof COSDictionary) {
            COSDictionary dict = (COSDictionary) base;

            for (Iterator<Entry<COSName, COSBase>> it = dict.entrySet().iterator(); it.hasNext();) {
                Map.Entry<COSName, COSBase> entry = it.next();

                if ("JS".equals(entry.getKey().getName()) || "JavaScript".equals(entry.getKey().getName())) {
                    threatRegistry.registerThreat(PDFSanitizeSections.NAMES_JAVASCRIPT_ACTION, "Script Action " + entry.getKey().getName(), PDFContentUtil.getInstance().convert(entry.getValue()));
                    it.remove();
                    continue;
                }

                if ("S".equals(entry.getKey().getName())) {
                    if (entry.getValue() instanceof COSName) {
                        if ("JavaScript".equals(((COSName) entry.getValue()).getName())) {
                            threatRegistry.registerThreat(PDFSanitizeSections.NAMES_JAVASCRIPT_ACTION, "Script Action", PDFContentUtil.getInstance().convert(entry.getValue()));
                            it.remove();
                            continue;
                        }
                    }
                }

                if ("AA".equals(entry.getKey().getName())) {
                    threatRegistry.registerThreat(PDFSanitizeSections.NAMES_JAVASCRIPT_ACTION, "Additional Action", PDFContentUtil.getInstance().convert(entry.getValue()));
                    it.remove();
                    continue;
                }

                crawl(entry.getValue());
            }
        } else if (base instanceof COSArray) {
            COSArray ar = (COSArray) base;
            for (COSBase item : ar) {
                crawl(item);
            }
        } else {
            LOG.warn("Unknown COS type: {}", base);
        }
    }
}
