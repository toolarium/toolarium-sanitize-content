/*
 * EmbeddedFileContentBleacher.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.elements;

import com.github.toolarium.sanitize.content.exception.SanitizeContentException;
import com.github.toolarium.sanitize.content.impl.bleach.impl.pdf.PDFSanitizeContentBleacher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The PDF embedded file bleacher
 *
 * @author Patrick Meier
 */
public class PDFEmbeddedFileBleacher {
    private static final Logger LOG = LoggerFactory.getLogger(PDFEmbeddedFileBleacher.class);
    private PDFSanitizeContentBleacher contentBleacher;
    private final PDDocument doc;


    /**
     * Constructor for EmbeddedFileContentBleacher
     *
     * @param contentBleacher the content bleacher
     * @param doc the document the document
     */
    public PDFEmbeddedFileBleacher(PDFSanitizeContentBleacher contentBleacher, PDDocument doc) {
        this.contentBleacher = contentBleacher;
        this.doc = doc;
    }


    /**
     * Sanitize embedded files
     *
     * @param embeddedFiles the files
     */
    public void sanitize(PDEmbeddedFilesNameTreeNode embeddedFiles) {
        sanitizeRecursiveNameTree(embeddedFiles, this::sanitizeEmbeddedFile);
    }


    /**
     * Sanitize recursive name tree
     *
     * @param <T> the generic type
     * @param efTree the tree
     * @param callback the callback
     */
    protected <T extends COSObjectable> void sanitizeRecursiveNameTree(PDNameTreeNode<T> efTree, Consumer<T> callback) {
        if (efTree == null) {
            return;
        }

        Map<String, T> nameMap;
        try {
            nameMap = efTree.getNames();
        } catch (IOException e) {
            return;
        }

        if (nameMap != null) {
            nameMap.values().forEach(callback);
        }

        if (efTree.getKids() == null) {
            return;
        }

        for (PDNameTreeNode<T> node : efTree.getKids()) {
            sanitizeRecursiveNameTree(node, callback);
        }
    }


    /**
     * Sanitize file specification
     *
     * @param fileSpec he file specification
     */
    @SuppressWarnings("deprecation")
    protected void sanitizeEmbeddedFile(PDComplexFileSpecification fileSpec) {
        if (fileSpec == null) {
            return;
        }

        String filename = fileSpec.getFilename();
        LOG.debug("Embedded file found: " + filename);
        fileSpec.setEmbeddedFile(sanitizeEmbeddedFile(filename, fileSpec.getEmbeddedFile()));
        fileSpec.setEmbeddedFileDos(sanitizeEmbeddedFile(filename, fileSpec.getEmbeddedFileDos()));
        fileSpec.setEmbeddedFileMac(sanitizeEmbeddedFile(filename, fileSpec.getEmbeddedFileMac()));
        fileSpec.setEmbeddedFileUnicode(sanitizeEmbeddedFile(filename, fileSpec.getEmbeddedFileUnicode()));
        fileSpec.setEmbeddedFileUnix(sanitizeEmbeddedFile(filename, fileSpec.getEmbeddedFileUnix()));
    }


    /**
     * Sanitize the embedded file
     *
     * @param filename the filename
     * @param file the file
     * @return the embedded file
     */
    protected PDEmbeddedFile sanitizeEmbeddedFile(String filename, PDEmbeddedFile file) {
        if (file == null) {
            return null;
        }

        String fileInformation = "Found file " + filename + " (size: " +  file.getSize() + ", mime-type " + file.getSubtype() + ")";
        LOG.debug(fileInformation);

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            contentBleacher.bleachContent(filename, new ByteArrayInputStream(file.toByteArray()), os, null);

            // create new embedded file
            PDEmbeddedFile ef = new PDEmbeddedFile(doc, new ByteArrayInputStream(os.toByteArray()), COSName.FLATE_DECODE);
            ef.setCreationDate(file.getCreationDate());
            ef.setModDate(file.getModDate());

            // copy the properties of the original embedded file
            ef.setSubtype(file.getSubtype());
            ef.setSize(os.size());
            ef.setMacCreator(file.getMacCreator());
            ef.setMacResFork(file.getMacResFork());
            ef.setMacSubtype(file.getMacSubtype());

            // remove the real file
            file.setSize(0);
            file.setFile(null);

            try {
                file.createOutputStream().close();
            } catch (IOException e) {
                LOG.warn("Could not remove embedded file " + filename + ": " + e.getMessage());
            }

            return ef;
        } catch (SanitizeContentException e) {
            LOG.warn("Could not bleach embedded file " + filename + ": " + e.getMessage());
            return null;
        } catch (IOException e) {
            LOG.warn("Could not read embedded file " + filename + ": " + e.getMessage());
            return null;
        }
    }
}
