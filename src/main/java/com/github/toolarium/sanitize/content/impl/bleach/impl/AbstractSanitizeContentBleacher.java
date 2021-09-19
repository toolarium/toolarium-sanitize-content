/*
 * AbstractContentBleacher.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl.bleach.impl;

import com.github.toolarium.sanitize.content.dto.SanitizeContentThreatInformation;
import com.github.toolarium.sanitize.content.impl.ISanitizeContentThreatRegistry;
import com.github.toolarium.sanitize.content.impl.bleach.ISanitizeContentBleacher;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Defines the abstract content bleacher
 *
 * @author Patrick Meier
 */
public abstract class AbstractSanitizeContentBleacher implements ISanitizeContentBleacher, ISanitizeContentThreatRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSanitizeContentBleacher.class);
    private List<SanitizeContentThreatInformation> threatInformationList;
    private String name;


    /**
     * Constructor for AbstractContentBleacher
     */
    public AbstractSanitizeContentBleacher() {
        threatInformationList = new ArrayList<SanitizeContentThreatInformation>();
    }


    /**
     * Set the name
     *
     * @param name the name
     */
    protected void setName(String name) {
        this.name = name;
    }


    /**
     * @see com.github.toolarium.sanitize.content.impl.ISanitizeContentThreatRegistry#registerThreat(com.github.toolarium.sanitize.content.impl.ISanitizeContentThreatRegistry.ISection, java.lang.String, java.lang.String)
     */
    @Override
    public void registerThreat(ISanitizeContentThreatRegistry.ISection section, String description, String action) {
        LOG.debug("Threat found in " + name + " #" + (threatInformationList.size() + 1) + ": [" + section + "] / [" + description + "]");
        threatInformationList.add(new SanitizeContentThreatInformation(section.name(), description, action));
    }


    /**
     * Get the threat information list
     *
     * @return the threat information list
     */
    protected List<SanitizeContentThreatInformation> getThreatInformationList() {
        return threatInformationList;
    }
}
