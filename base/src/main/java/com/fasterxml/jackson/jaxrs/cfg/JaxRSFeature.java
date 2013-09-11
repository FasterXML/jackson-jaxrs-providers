package com.fasterxml.jackson.jaxrs.cfg;

import com.fasterxml.jackson.databind.cfg.ConfigFeature;

/**
 * Enumeration that defines simple on/off features that can be
 * used on all Jackson JAX-RS providers, regardless of
 * underlying data format.
 */
public enum JaxRSFeature implements ConfigFeature
{
    /*
    /******************************************************
    /* HTTP headers
    /******************************************************
     */
    
    /**
     * Feature that can be enabled to make provider automatically
     * add "nosniff" (see
     * <a href="http://security.stackexchange.com/questions/20413/how-can-i-prevent-reflected-xss-in-my-json-web-services">this entry</a>
     * for details
     *<p>
     * Feature is disabled by default.
     */
    ADD_NO_SNIFF_HEADER(false),

    /*
    /******************************************************
    /* Other
    /******************************************************
     */

    ;

    private final boolean _defaultState;
    
    private JaxRSFeature(boolean defaultState) {
        _defaultState = defaultState;
    }

    @Override
    public boolean enabledByDefault() { return _defaultState; }

    @Override
    public int getMask() { return (1 << ordinal()); }
}
