/*
 * SanitizeContentThreatInformation.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.dto;

import java.util.Objects;

/**
 * The threat information.
 *
 * @author Patrick Meier
 */
public class SanitizeContentThreatInformation {
    private String section;
    private String description;
    private String actionCode;


    /**
     * Constructor for SanitizeContentThreatInformation
     */
    public SanitizeContentThreatInformation() {
    }


    /**
     * Constructor for SanitizeContentThreatInformation
     *
     * @param section the section
     * @param description the desctription
     * @param actionCode the action code
     */
    public SanitizeContentThreatInformation(String section, String description, String actionCode) {
        this.section = section;
        this.description = description;
        this.actionCode = actionCode;
    }


    /**
     * Gets the section.
     *
     * @return the section
     */
    public String getSection() {
        return section;
    }


    /**
     * Sets the section.
     *
     * @param section the section to set
     */
    public void setSection(String section) {
        this.section = section;
    }


    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Gets the action code.
     *
     * @return the action code
     */
    public String getActionCode() {
        return actionCode;
    }


    /**
     * Sets the action code.
     *
     * @param actionCode the action code to set
     */
    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(actionCode, description, section);
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

        SanitizeContentThreatInformation other = (SanitizeContentThreatInformation) obj;
        return Objects.equals(actionCode, other.actionCode) && Objects.equals(description, other.description) && Objects.equals(section, other.section);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SanitizeContentThreatInformation [section=" + section + ", description=" + description + "]";
    }
}
