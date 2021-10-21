/*
 * SanitizeContentCredentialAccess.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.sanitize.content.dto;

import com.github.toolarium.sanitize.content.ISanitizeContentCredentialAccess;
import java.util.Objects;


/**
 * Implements the {@link ISanitizeContentCredentialAccess}.
 * 
 * @author patrick
 */
public class SanitizeContentCredentialAccess implements ISanitizeContentCredentialAccess {
    private final String username;
    private final String credentials;

    
    /**
     * Constructor
     * 
     * @param credentials the credentials
     */
    public SanitizeContentCredentialAccess(final String credentials) {
        this.username = null;
        this.credentials = credentials;
        
    }

    
    /**
     * Constructor
     * 
     * @param username the username
     * @param credentials the credentials
     */
    public SanitizeContentCredentialAccess(final String username, final String credentials) {
        this.username = username;
        this.credentials = credentials;
        
    }

    
    /**
     * @see com.github.toolarium.sanitize.content.ISanitizeContentCredentialAccess#getUsername()
     */
    @Override
    public String getUsername() {
        return username;
    }


    /**
     * @see com.github.toolarium.sanitize.content.ISanitizeContentCredentialAccess#getCredentials()
     */
    @Override
    public String getCredentials() {
        return credentials;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(credentials, username);
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
        
        SanitizeContentCredentialAccess other = (SanitizeContentCredentialAccess) obj;
        return Objects.equals(credentials, other.credentials) && Objects.equals(username, other.username);
    }
}
