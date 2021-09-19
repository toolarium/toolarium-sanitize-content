/*
 * PDFContentUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl.bleach.impl.pdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionEmbeddedGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionImportData;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionLaunch;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionRemoteGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionSubmitForm;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionThread;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;


/**
 * The PDF content utility
 *
 * @author Patrick Meier
 */
public final class PDFContentUtil {

    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author Patrick Meier
     */
    private static class HOLDER {
        static final PDFContentUtil INSTANCE = new PDFContentUtil();
    }

    /**
     * Constructor
     */
    private PDFContentUtil() {
        // NOP
    }

    /**
     * Get the instance
     *
     * @return the instance
     */
    public static PDFContentUtil getInstance() {
        return HOLDER.INSTANCE;
    }


    /**
     * Convert
     *
     * @param action the action
     * @return the string representation
     */
    public String convert(PDDestinationOrAction action) {
        if (action == null) {
            return null;
        }

        if (action instanceof PDAction) {
            return convert((PDAction)action);
        }

        return "" + action;
    }


    /**
     * Convert
     *
     * @param action the action
     * @return the string representation
     */
    public String convert(PDAction action) {
        if (action == null) {
            return null;
        }

        if (action instanceof PDActionEmbeddedGoTo) {
            return convert((PDActionEmbeddedGoTo)action);
        }

        if (action instanceof PDActionURI) {
            return convert((PDActionURI)action);
        }

        if (action instanceof PDActionThread) {
            return convert((PDActionThread)action);
        }

        if (action instanceof PDActionSubmitForm) {
            return convert((PDActionSubmitForm)action);
        }

        if (action instanceof PDActionRemoteGoTo) {
            return convert((PDActionRemoteGoTo)action);
        }

        if (action instanceof PDActionLaunch) {
            return convert((PDActionLaunch)action);
        }

        if (action instanceof PDActionJavaScript) {
            return convert((PDActionJavaScript)action);
        }

        if (action instanceof PDActionImportData) {
            return convert((PDActionImportData)action);
        }

        if (action instanceof PDActionGoTo) {
            return convert((PDActionGoTo)action);
        }

        return "" + action;
    }


    /**
     * Convert
     *
     * @param action the action
     * @return the string representation
     */
    public String convert(PDActionEmbeddedGoTo action) {
        if (action == null) {
            return null;
        }

        try {
            return convert(action.getDestination());
        } catch (IOException e) {
            return "" + action;
        }
    }


    /**
     * Convert
     *
     * @param action the action
     * @return the string representation
     */
    public String convert(PDActionURI action) {
        if (action == null) {
            return null;
        }

        return action.getURI();
    }


    /**
     * Convert
     *
     * @param action the action
     * @return the string representation
     */
    public String convert(PDActionThread action) {
        if (action == null) {
            return null;
        }

        try {
            return convert(action.getFile());
        } catch (IOException e) {
            return "" + action;
        }
    }


    /**
     * Convert
     *
     * @param action the action
     * @return the string representation
     */
    public String convert(PDActionSubmitForm action) {
        if (action == null) {
            return null;
        }

        try {
            return convert(action.getFile());
        } catch (IOException e) {
            return "" + action;
        }
    }


    /**
     * Convert
     *
     * @param action the action
     * @return the string representation
     */
    public String convert(PDActionRemoteGoTo action) {
        if (action == null) {
            return null;
        }

        try {
            return convert(action.getFile());
        } catch (IOException e) {
            return "" + action;
        }
    }


    /**
     * Convert
     *
     * @param action the action
     * @return the string representation
     */
    public String convert(PDActionLaunch action) {
        if (action == null) {
            return null;
        }

        try {
            return convert(action.getFile());
        } catch (IOException e) {
            return "" + action;
        }
    }


    /**
     * Convert
     *
     * @param action the action
     * @return the string representation
     */
    public String convert(PDActionJavaScript action) {
        if (action == null) {
            return null;
        }

        return action.getAction();
    }


    /**
     * Convert
     *
     * @param action the action
     * @return the string representation
     */
    public String convert(PDActionImportData action) {
        if (action == null) {
            return null;
        }

        try {
            return convert(action.getFile());
        } catch (IOException e) {
            return "" + action;
        }
    }


    /**
     * Convert
     *
     * @param action the action
     * @return the string representation
     */
    public String convert(PDActionGoTo action) {
        if (action == null) {
            return null;
        }

        try {
            return convert(action.getDestination());
        } catch (IOException e) {
            return "" + action;
        }
    }


    /**
     * Convert a PDF file specification
     *
     * @param spec the specification
     * @return the string representation
     */
    public String convert(PDFileSpecification spec) {
        if (spec == null) {
            return null;
        }

        return spec.getFile();
    }


    /**
     * Convert a PDF destination
     *
     * @param dest the destination
     * @return the string destination
     */
    public String convert(PDDestination dest) {
        if (dest == null) {
            return null;
        }

        return "" + dest;
    }


    /**
     * Convert a PDF value
     *
     * @param value the value
     * @return the string value
     */
    public String convert(COSBase value) {
        if (value == null) {
            return null;
        }

        return "" + value;
    }
}
