package com.fasterxml.jackson.jaxrs.xml;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;

public class TestJsonView extends JaxrsTestBase
{
    static class MyView1 { }
    static class MyView2 { }

    static class Bean {
        @JsonView(MyView1.class)
        public int value1 = 1;

        @JsonView(MyView2.class)
        public int value2 = 2;
    }

    @JsonView({ MyView1.class })
    public void bogus() { }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    // [JACKSON-578]
    public void testViews() throws Exception
    {
        JacksonXMLProvider prov = new JacksonXMLProvider();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Bean bean = new Bean();
        Method m = getClass().getDeclaredMethod("bogus");
        JsonView view = m.getAnnotation(JsonView.class);
        assertNotNull(view); // just a sanity check
        prov.writeTo(bean, bean.getClass(), bean.getClass(), new Annotation[] { view },
                MediaType.APPLICATION_JSON_TYPE, null, out);
        assertEquals("<Bean><value1>1</value1></Bean>", out.toString("UTF-8"));
    }
}
