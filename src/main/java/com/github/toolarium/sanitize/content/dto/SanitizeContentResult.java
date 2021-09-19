/*
 * SanitizeContentResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Defines the sanitize content result.
 *
 * @author Patrick Meier
 */
public class SanitizeContentResult implements Serializable {
    private static final long serialVersionUID = 6841213499153479622L;
    private String contentType;
    private boolean modifiedContent;
    private List<SanitizeContentThreatInformation> threadInformationList;


    /**
     * Constructor for SanitizeContentResult
     */
    public SanitizeContentResult() {
        contentType = null;
        modifiedContent = false;
        threadInformationList = new ArrayList<SanitizeContentThreatInformation>();
    }


    /**
     * Add result
     *
     * @param result the result
     */
    public void add(SanitizeContentResult result) {

        if (result.getContentType() != null && !result.getContentType().isBlank()) {
            if (getContentType() == null || getContentType().isBlank()) {
                contentType = result.getContentType();
            }
        }

        modifiedContent = modifiedContent || result.isModifiedContent();

        if (result.getThreadInformationList() != null) {
            if (threadInformationList == null) {
                threadInformationList = new ArrayList<SanitizeContentThreatInformation>();
            }

            threadInformationList.addAll(result.getThreadInformationList());
        }
    }


    /**
     * Gets the content type.
     *
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }


    /**
     * Sets the content type.
     *
     * @param contentType the content type to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    /**
     * Gets the modifiedContent.
     *
     * @return the modifiedContent
     */
    public boolean isModifiedContent() {
        return modifiedContent;
    }


    /**
     * Sets the modifiedContent.
     *
     * @param modifiedContent the modifiedContent to set
     */
    public void setModifiedContent(boolean modifiedContent) {
        this.modifiedContent = modifiedContent;
    }


    /**
     * Gets the thread information list.
     *
     * @return the thread information list
     */
    public List<SanitizeContentThreatInformation> getThreadInformationList() {
        return threadInformationList;
    }


    /**
     * Sets the thread information list.
     *
     * @param threadInformationList the thread information list to set
     */
    public void setThreadInformationList(List<SanitizeContentThreatInformation> threadInformationList) {
        this.threadInformationList = threadInformationList;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(contentType, modifiedContent, threadInformationList);
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SanitizeContentResult other = (SanitizeContentResult) obj;
        return Objects.equals(contentType, other.contentType) && modifiedContent == other.modifiedContent
                && Objects.equals(threadInformationList, other.threadInformationList);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SanitizeContentResult [contentType=" + contentType + ", modifiedContent=" + modifiedContent
                + ", threadInformationList=" + threadInformationList + "]";
    }
}
