/*
 * ISanitizeContentProcessor.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content;

import com.github.toolarium.sanitize.content.dto.SanitizeContentResult;
import com.github.toolarium.sanitize.content.exception.SanitizeContentException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Defines the sanitize content processor
 *
 * @author Patrick Meier
 */
public interface ISanitizeContentProcessor {

    /**
     * Support content
     *
     * @param name The name of the content
     * @param stream the stream
     * @return true if its supported by this sanitizing implementation
     * @throws SanitizeContentException In case the content can't be sanitized.
     */
    boolean supportContent(String name, InputStream stream) throws SanitizeContentException;


    /**
     * Sanitize the content
     *
     * @param name the name of the content
     * @param inputStream the input stream
     * @param outputStream the output stream
     * @param credentialAccess the credential access or null
     * @return the sanitize result
     * @throws SanitizeContentException In case the content can't be sanitized.
     */
    SanitizeContentResult sanitize(String name, InputStream inputStream, OutputStream outputStream, ISanitizeContentCredentialAccess credentialAccess)
        throws SanitizeContentException;

}
