package com.fasterxml.jackson.datatype.jaxrs;

import java.util.Arrays;

import tools.jackson.core.*;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public abstract class ModuleTestBase
    extends junit.framework.TestCase
{
    protected ObjectMapper mapperWithModule() {
        return JsonMapper.builder()
                .addModule(new Jaxrs2TypesModule())
                .build();
    }

    /*
    /**********************************************************************
    /* Additional assertion methods
    /**********************************************************************
     */

    protected void assertToken(JsonToken expToken, JsonToken actToken)
    {
        if (actToken != expToken) {
            fail("Expected token "+expToken+", current token "+actToken);
        }
    }

    protected void assertToken(JsonToken expToken, JsonParser p)
    {
        assertToken(expToken, p.currentToken());
    }

    protected void verifyException(Throwable e, String... matches)
    {
        String msg = e.getMessage();
        String lmsg = (msg == null) ? "" : msg.toLowerCase();
        for (String match : matches) {
            String lmatch = match.toLowerCase();
            if (lmsg.indexOf(lmatch) >= 0) {
                return;
            }
        }
        fail("Expected an exception with one of substrings ("+Arrays.asList(matches)+"): got one with message \""+msg+"\"");
    }

    /*
    /**********************************************************************
    /* Other helper methods
    /**********************************************************************
     */

    public String quote(String str) {
        return '"'+str+'"';
    }

    protected String aposToQuotes(String json) {
        return json.replace("'", "\"");
    }
}
