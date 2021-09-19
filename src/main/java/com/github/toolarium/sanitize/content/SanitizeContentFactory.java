/*
 * SanitizeContentFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content;

import com.github.toolarium.sanitize.content.impl.SanitizeContentProcessorImpl;


/**
 * Defines the sanitize content factory.
 *
 * @author Patrick Meier
 */
public final class SanitizeContentFactory {

    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author Patrick Meier
     */
    private static class HOLDER {
        static final SanitizeContentFactory INSTANCE = new SanitizeContentFactory();
    }


    /**
     * Constructor
     */
    private SanitizeContentFactory() {
        // NOP
    }


    /**
     * Get the instance
     *
     * @return the instance
     */
    public static SanitizeContentFactory getInstance() {
        return HOLDER.INSTANCE;
    }


    /**
     * Get the sanitize content processor
     *
     * @return sanitize content processor
     */
    public ISanitizeContentProcessor getSanitizeContentProcessor() {
        return new SanitizeContentProcessorImpl();
    }
}
