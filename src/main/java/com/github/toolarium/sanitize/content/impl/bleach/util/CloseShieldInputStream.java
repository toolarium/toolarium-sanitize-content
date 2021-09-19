/*
 * CloseShieldInputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl.bleach.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;


/**
 * Proxy stream that prevents the underlying input stream from being closed.
 *
 * <p>This class is typically used in cases where an input stream needs to be passed to a component that wants to
 * explicitly close the stream even if more input would still be available to other components.
 * JavaDoc copied from the Apache Commons-IO project, with code changed to match our dependencies. file:
 * org.apache.commons.io.input.CloseShieldInputStream.java
 *
 * @author Patrick Meier
 */
public class CloseShieldInputStream extends PushbackInputStream {

    /**
     * Constructor for CloseShieldInputStream
     *
     * @param inStream the input stream
     */
    public CloseShieldInputStream(InputStream inStream) {
        super(inStream, 100);
    }


    /**
     * @see java.io.FilterInputStream#close()
     */
    @Override
    public void close() throws IOException {
        // no-action
    }


    /**
     * Real close
     *
     * @throws IOException In case of an I/O error
     */
    public void realClose() throws IOException {
        super.close();
    }
}
