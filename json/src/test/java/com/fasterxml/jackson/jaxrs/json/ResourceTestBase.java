package com.fasterxml.jackson.jaxrs.json;

import java.io.*;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ResourceTestBase extends JaxrsTestBase
{
    protected static abstract class JsonApplication extends Application
    {
        protected final Object _resource;

        protected JsonApplication(Object r) { _resource = r; }
        
        @Override
        public Set<Object> getSingletons() {
            HashSet<Object> singletons = new HashSet<Object>();
            singletons.add(new JacksonJsonProvider());
            singletons.add(_resource);
            return singletons;
        }
    }

    protected String aposToQuotes(String json) {
        return json.replace("'", "\"");
    }
    
    protected String readUTF8(InputStream in) throws IOException
    {
        return new String(readAll(in), "UTF-8");
    }
    
    protected byte[] readAll(InputStream in) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(100);
        byte[] buffer = new byte[500];
        int count;

        while ((count = in.read(buffer)) > 0) {
            bytes.write(buffer, 0, count);
        }
        in.close();
        return bytes.toByteArray();
    }
}
