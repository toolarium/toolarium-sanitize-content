/*
 * ISanitizeContentCredentialAccess.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content;


/**
 * Defines the sanitize content credential access interface
 *
 * @author Patrick Meier
 */
public interface ISanitizeContentCredentialAccess {

    /**
     * Get the user name or null.
     *
     * @return the user name
     */
    String getUsername();


    /**
     * Get the credentials.
     *
     * @return the credentials
     */
    String getCredentials();
}
