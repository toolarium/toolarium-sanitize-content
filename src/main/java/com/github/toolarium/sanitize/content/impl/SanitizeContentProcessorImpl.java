/*
 * SanitizeContentProcessorImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl;

import com.github.toolarium.sanitize.content.ISanitizeContentCredentialAccess;
import com.github.toolarium.sanitize.content.ISanitizeContentProcessor;
import com.github.toolarium.sanitize.content.dto.SanitizeContentResult;
import com.github.toolarium.sanitize.content.exception.SanitizeContentException;
import com.github.toolarium.sanitize.content.impl.bleach.ISanitizeContentBleacher;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeContentBleacher;
import com.github.toolarium.sanitize.content.impl.bleach.util.CloseShieldInputStream;
import com.github.toolarium.sanitize.content.impl.bleach.util.StreamUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements the {@link ISanitizeContentProcessor}.
 *
 * @author Patrick Meier
 */
public class SanitizeContentProcessorImpl implements ISanitizeContentProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(SanitizeContentProcessorImpl.class);
    private List<ISanitizeContentBleacher> contentBleachSupportList;


    /**
     * Constructor for SanitizeContentProcessorImpl
     */
    public SanitizeContentProcessorImpl() {
        contentBleachSupportList = new ArrayList<ISanitizeContentBleacher>();
        contentBleachSupportList.add(new PDFSanitizeContentBleacher());
    }


    /**
     * @see com.github.toolarium.sanitize.content.ISanitizeContentProcessor#supportContent(java.lang.String, java.io.InputStream)
     */
    @Override
    public boolean supportContent(String name, InputStream inputStream) throws SanitizeContentException {
        for (ISanitizeContentBleacher contentBleacher : contentBleachSupportList) {
            if (contentBleacher.supportContent(name, inputStream)) {
                return true;
            }
        }

        return false;
    }


    /**
     * @see com.github.toolarium.sanitize.content.ISanitizeContentProcessor#sanitize(java.lang.String, java.io.InputStream, java.io.OutputStream, com.github.toolarium.sanitize.content.ISanitizeContentCredentialAccess)
     */
    @Override
    public SanitizeContentResult sanitize(String name, InputStream inputStream, OutputStream outputStream, ISanitizeContentCredentialAccess credentialAccess) throws SanitizeContentException {

        ByteArrayOutputStream os = null;
        CloseShieldInputStream is = new CloseShieldInputStream(inputStream);
        SanitizeContentResult result = new SanitizeContentResult();

        for (ISanitizeContentBleacher contentBleacher : contentBleachSupportList) {
            if (os != null && is == null) {
                // check if "is" is null to prevent useless object creation
                ByteArrayInputStream bais = new ByteArrayInputStream(os.toByteArray());
                is = new CloseShieldInputStream(new BufferedInputStream(bais, bais.available()));

                try {
                    os.close();
                } catch (IOException e) {
                    LOG.warn("Could not close output stream: " + e.getMessage());
                }
            }

            if (!contentBleacher.supportContent(name, is)) {
                continue;
            }

            LOG.debug("Using bleach: {}", contentBleacher.getClass().getName());
            os = new ByteArrayOutputStream();

            result.add(contentBleacher.bleachContent(name, is, os, credentialAccess));

            try {
                is.close();
                is = null;
            } catch (IOException e) {
                LOG.warn("Could not close input stream: " + e.getMessage());
            }
        }

        try {
            if (os == null) {
                // no bleach is able to handle this file
                StreamUtils.getInstance().copy(is, outputStream);
            } else {
                os.writeTo(outputStream);
            }
        } catch (IOException e) {
            LOG.warn("Could not copy streams: " + e.getMessage(), e);
        }

        return result;
    }
}
