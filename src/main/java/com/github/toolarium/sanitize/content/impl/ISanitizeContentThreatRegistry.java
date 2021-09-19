/*
 * ISanitizeContentThreatRegistry.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.impl;

/**
 * Defines the sanitize content threat registry.
 *
 * @author Patrick Meier
 */
public interface ISanitizeContentThreatRegistry {

    /**
     * Register a threat
     *
     * @param section the section
     * @param description the description
     * @param action the action code
     */
    void registerThreat(ISection section, String description, String action);


    /**
     * Defines the section interface
     */
    interface ISection {
        /**
         * Get the unique name
         *
         * @return the name
         */
        String name();
    }
}
