package com.fasterxml.jackson.jaxrs.smile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Assert;

import com.fasterxml.jackson.core.*;

public abstract class JaxrsTestBase
    extends junit.framework.TestCase
{
    /*
    /**********************************************************
    /* Additional assertion methods
    /**********************************************************
     */

    protected void assertToken(JsonToken expToken, JsonToken actToken)
    {
        if (actToken != expToken) {
            fail("Expected token "+expToken+", current token "+actToken);
        }
    }

    protected void assertToken(JsonToken expToken, JsonParser jp)
    {
        assertToken(expToken, jp.getCurrentToken());
    }

    protected void assertType(Object ob, Class<?> expType)
    {
        if (ob == null) {
            fail("Expected an object of type "+expType.getName()+", got null");
        }
        Class<?> cls = ob.getClass();
        if (!expType.isAssignableFrom(cls)) {
            fail("Expected type "+expType.getName()+", got "+cls.getName());
        }
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
    
    protected void _verifyBytes(byte[] actBytes, byte... expBytes)
    {
        Assert.assertArrayEquals(expBytes, actBytes);
    }

    /**
     * Method that gets textual contents of the current token using
     * available methods, and ensures results are consistent, before
     * returning them
     */
    protected String getAndVerifyText(JsonParser jp)
        throws IOException, JsonParseException
    {
        // Ok, let's verify other accessors
        int actLen = jp.getTextLength();
        char[] ch = jp.getTextCharacters();
        String str2 = new String(ch, jp.getTextOffset(), actLen);
        String str = jp.getText();

        if (str.length() !=  actLen) {
            fail("Internal problem (jp.token == "+jp.getCurrentToken()+"): jp.getText().length() ['"+str+"'] == "+str.length()+"; jp.getTextLength() == "+actLen);
        }
        assertEquals("String access via getText(), getTextXxx() must be the same", str, str2);

        return str;
    }
    
    /*
    /**********************************************************
    /* Other helper methods
    /**********************************************************
     */

    public String quote(String str) {
        return '"'+str+'"';
    }

    protected String aposToQuotes(String json) {
        return json.replace("'", "\"");
    }
    
    protected String readUTF8(InputStream in) throws IOException {
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
