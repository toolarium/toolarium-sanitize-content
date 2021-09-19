/*
 * StreamUtils.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl.bleach.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Stream utility class
 *
 * @author Patrick Meier
 */
public final class StreamUtils {
    private static final Logger LOG = LoggerFactory.getLogger(StreamUtils.class);

    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author Patrick Meier
     */
    private static class HOLDER {
        static final StreamUtils INSTANCE = new StreamUtils();
    }


    /**
     * Constructor
     */
    private StreamUtils() {
        // NOP
    }


    /**
     * Get the instance
     *
     * @return the instance
     */
    public static StreamUtils getInstance() {
        return HOLDER.INSTANCE;
    }


    /**
     * Check content header
     *
     * @param name the name
     * @param stream the stream
     * @param header the header to check
     * @return true if it has the header
     */
    public boolean hasHeader(String name, InputStream stream, byte[] header) {

        if (stream == null) {
            return false;
        }

        if (header == null || header.length == 0) {
            return false;
        }

        stream.mark(header.length);

        byte[] contentMagic = new byte[header.length];
        int length;
        try {
            length = stream.read(contentMagic);

            if (stream instanceof CloseShieldInputStream) {
                CloseShieldInputStream pin1 = (CloseShieldInputStream) stream;
                pin1.unread(contentMagic, 0, length);
            } else if (stream instanceof PushbackInputStream) {
                PushbackInputStream pin = (PushbackInputStream) stream;
                pin.unread(contentMagic, 0, length);
            } else {
                stream.reset();
            }
        } catch (IOException e) {
            LOG.info("Could not verify the content header of [" + name + "]:" + e.getMessage(), e);
            return false;
        }

        return length == header.length && Arrays.equals(contentMagic, header);
    }


    /**
     * Copy stream
     *
     * @param is the input stream
     * @param os the output stream
     * @throws IOException In case of an I/O error
     */
    public void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[10024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
    }


    /**
     * Store a stream into a file
     *
     * @param is the input stream
     * @param file the file
     * @return the file
     * @throws IOException In case of an I/O error
     */
    public File save(InputStream is, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        copy(is, fos);
        fos.close();
        return file;
    }
}
