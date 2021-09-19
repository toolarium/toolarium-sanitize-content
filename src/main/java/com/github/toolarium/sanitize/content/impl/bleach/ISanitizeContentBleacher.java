/*
 * ISanitizeContentBleacher.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl.bleach;

import com.github.toolarium.sanitize.content.ISanitizeContentCredentialAccess;
import com.github.toolarium.sanitize.content.dto.SanitizeContentResult;
import com.github.toolarium.sanitize.content.exception.SanitizeContentException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Defines the sanitize content bleacher interface.
 *
 * @author Patrick Meier
 */
public interface ISanitizeContentBleacher {

    /**
     * Support content
     *
     * @param name the name of the content
     * @param stream the stream
     * @return true if its supported by this bleacher
     * @throws SanitizeContentException In case the content can't be sanitized.
     */
    boolean supportContent(String name, InputStream stream) throws SanitizeContentException;


    /**
     * Bleach content
     *
     * @param name the name of the content
     * @param inputStream the input stream
     * @param outputStream the output stream
     * @param credentialAccess the credential access
     * @return the sanitize content result
     * @throws SanitizeContentException In case the content can't be sanitized.
     */
    SanitizeContentResult bleachContent(String name, InputStream inputStream, OutputStream outputStream, ISanitizeContentCredentialAccess credentialAccess)
        throws SanitizeContentException;
}
