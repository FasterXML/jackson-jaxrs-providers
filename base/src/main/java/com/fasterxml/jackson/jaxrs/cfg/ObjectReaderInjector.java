package com.fasterxml.jackson.jaxrs.cfg;

import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.*;

/**
 * Based on ideas from [Issue#32], this class allows "overriding" of {@link ObjectReader}
 * that JAX-RS Resource will use; usually this is done from a Servlet or JAX-RS filter
 * before execution reaches resource.
 * 
 * @author apemberton@github, Tatu Saloranta
 * 
 * @since 2.3
 */
public class ObjectReaderInjector
{
    protected static final ThreadLocal<ObjectReader> _threadLocal = new ThreadLocal<ObjectReader>();

    /**
     * Simple marker used to optimize out {@link ThreadLocal} access in cases
     * where this feature is not being used
     */
    protected final AtomicBoolean _hasBeenSet = new AtomicBoolean(false);
    
    public ObjectReaderInjector() { }
    
    public void set(ObjectReader r) {
        _hasBeenSet.set(true);
        _threadLocal.set(r);
    }

    public ObjectReader get() {
        return _hasBeenSet.get() ? _threadLocal.get() : null;
    }
    
    public ObjectReader getAndClear() {
        ObjectReader r = get();
        if (r != null) {
            _threadLocal.remove();
        }
        return r;
    }
}
