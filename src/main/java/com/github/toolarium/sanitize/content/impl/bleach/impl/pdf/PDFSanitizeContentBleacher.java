/*
 * PDFSanitizeContentBleacher.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl.bleach.impl.pdf;

import com.github.toolarium.sanitize.content.ISanitizeContentCredentialAccess;
import com.github.toolarium.sanitize.content.dto.SanitizeContentResult;
import com.github.toolarium.sanitize.content.exception.SanitizeContentException;
import com.github.toolarium.sanitize.content.impl.bleach.impl.AbstractSanitizeContentBleacher;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.elements.PDFDocumentBleacher;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.elements.PDFEmbeddedFileBleacher;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.elements.PDFObjectBleacher;
import com.github.toolarium.sanitize.content.impl.bleach.util.StreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.ScratchFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PDF content bleacher
 *
 * @author Patrick Meier
 */
public class PDFSanitizeContentBleacher extends AbstractSanitizeContentBleacher {
    /** APPLICATION PDF Mime type*/
    public static final String APPLICATION_PDF = "application/pdf";

    private static final Logger LOG = LoggerFactory.getLogger(PDFSanitizeContentBleacher.class);
    private static final MemoryUsageSetting MEMORY_USAGE_SETTING = MemoryUsageSetting.setupMixed(1024 * 100);
    private static final byte[] PDF_MAGIC = new byte[]{37, 80, 68, 70};


    /**
     * @see com.github.toolarium.sanitize.content.impl.bleach.ISanitizeContentBleacher#supportContent(java.lang.String, java.io.InputStream)
     */
    @Override
    public boolean supportContent(String filename, InputStream inputStream) {
        return StreamUtils.getInstance().hasHeader(filename, inputStream, PDF_MAGIC);
    }


    /**
     * @see com.github.toolarium.sanitize.content.impl.bleach.ISanitizeContentBleacher#bleachContent(java.lang.String, java.io.InputStream, java.io.OutputStream, com.github.toolarium.sanitize.content.ISanitizeContentCredentialAccess)
     */
    @Override
    public SanitizeContentResult bleachContent(String name, InputStream inputStream, OutputStream outputStream, ISanitizeContentCredentialAccess credentialAccess) throws SanitizeContentException {
        LOG.debug("Bleach PDF content...");

        setName(name);
        try (RandomAccessRead source = new RandomAccessBufferedFileInputStream(inputStream)) {
            PDFDocumentBleacher documentBleacher = new PDFDocumentBleacher(this);
            final PDDocument doc = getDocument(source, credentialAccess);
            final PDDocumentCatalog docCatalog = doc.getDocumentCatalog();
            if (docCatalog != null) {
                PDDocumentNameDictionary names = docCatalog.getNames();
                if (names != null) {
                    new PDFEmbeddedFileBleacher(this, doc).sanitize(names.getEmbeddedFiles());
                    if (names.getJavaScript() != null) {
                        registerThreat(PDFSanitizeSections.NAMES_JAVASCRIPT_ACTION, "Action", null);
                        names.setJavascript(null);
                    }
                }

                documentBleacher.sanitize(docCatalog);
            }

            // bleach the document
            documentBleacher.sanitize(doc.getDocumentCatalog().getDocumentOutline());

            // bleach the additional object
            PDFObjectBleacher objectBleacher = new PDFObjectBleacher(this);
            objectBleacher.sanitizeObjects(doc.getDocument().getObjects());

            if (!doc.getDocument().isClosed()) {
                doc.save(outputStream);
            }
            doc.close();
        } catch (IOException e) {
            throw new SanitizeContentException(e);
        }

        SanitizeContentResult result = new SanitizeContentResult();
        result.setContentType(APPLICATION_PDF);
        result.setModifiedContent(getThreatInformationList() != null && !getThreatInformationList().isEmpty());
        result.setThreadInformationList(getThreatInformationList());
        return result;
    }


    /**
     * Get the pdf document
     *
     * @param source the source
     * @param credentialAccess the credential access
     * @return the docuemnt
     * @throws SanitizeContentException In case of an error
     */
    private PDDocument getDocument(RandomAccessRead source, final ISanitizeContentCredentialAccess credentialAccess) throws SanitizeContentException {

        String credentials = null;
        if (credentialAccess != null) {
            credentials = credentialAccess.getCredentials();
        }

        ScratchFile scratchFile = null;

        try {
            scratchFile = new ScratchFile(MEMORY_USAGE_SETTING);
            PDDocument doc = readDocument(scratchFile, source, credentials);
            return doc;
        } catch (InvalidPasswordException e) {
            LOG.info("Invalid credentials!");
            IOUtils.closeQuietly(scratchFile);
            throw new SanitizeContentException("Invalid credentials!");
        } catch (IOException e) {
            IOUtils.closeQuietly(scratchFile);
            throw new SanitizeContentException(e);
        }
    }


    /**
     * Read the document
     *
     * @param inFile the input file
     * @param source the source
     * @param credentials the credentials
     * @return the document
     * @throws InvalidPasswordException Invalid credentials
     * @throws IOException In case of an I/O error
     */
    private PDDocument readDocument(ScratchFile inFile, RandomAccessRead source, String credentials) throws InvalidPasswordException, IOException {
        try {
            PDFParser parser;
            if (credentials != null) {
                parser = new PDFParser(source, inFile);
            } else {
                parser = new PDFParser(source, credentials, inFile);
            }

            parser.parse();
            PDDocument doc = parser.getPDDocument();
            if (credentials != null) {
                doc.protect(new StandardProtectionPolicy(credentials, credentials, doc.getCurrentAccessPermission()));
            }

            return doc;
        } finally {
            source.rewind((int) source.getPosition());
        }
    }
}
