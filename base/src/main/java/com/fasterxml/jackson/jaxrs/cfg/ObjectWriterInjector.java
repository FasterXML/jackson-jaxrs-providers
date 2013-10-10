package com.fasterxml.jackson.jaxrs.cfg;

import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.*;

/**
 * Based on ideas from [Issue#32], this class allows "overriding" of {@link ObjectWriter}
 * that JAX-RS Resource will use; usually this is done from a Servlet or JAX-RS filter
 * before execution reaches resource.
 * 
 * @author apemberton@github, Tatu Saloranta
 * 
 * @since 2.3
 */
public class ObjectWriterInjector
{
   protected static final ThreadLocal<ObjectWriter> _threadLocal = new ThreadLocal<ObjectWriter>();

   /**
    * Simple marker used to optimize out {@link ThreadLocal} access in cases
    * where this feature is not being used
    */
   protected final AtomicBoolean _hasBeenSet = new AtomicBoolean(false);

   public ObjectWriterInjector() { }
   
   public void set(ObjectWriter r) {
       _hasBeenSet.set(true);
       _threadLocal.set(r);
   }

   public ObjectWriter get() {
       return _hasBeenSet.get() ? _threadLocal.get() : null;
   }
   
   public ObjectWriter getAndClear() {
       ObjectWriter w = get();
       if (w != null) {
           _threadLocal.remove();
       }
       return w;
   }
}
