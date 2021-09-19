/*
 * SanitizeContentException.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.exception;

import java.io.IOException;

/**
 * Defines a sanitize content exception
 *
 * @author Patrick Meier
 */
public class SanitizeContentException extends IOException {
    private static final long serialVersionUID = 7281614526616647879L;


    /**
     * Constructor for SanitizeContentException
     *
     * @param message the message
     */
    public SanitizeContentException(String message) {
        super(message);
    }


    /**
     * Constructor for SanitizeContentException
     *
     * @param e the exception
     */
    public SanitizeContentException(IOException e) {
        super(e);
    }
}
