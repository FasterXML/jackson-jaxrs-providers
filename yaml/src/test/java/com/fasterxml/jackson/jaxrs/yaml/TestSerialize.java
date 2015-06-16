package com.fasterxml.jackson.jaxrs.yaml;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class TestSerialize extends JaxrsTestBase
{
    // for [Issue-1]
    public class Message {
        public String text;
        
        public Message() { }
        public Message(String s) { text = s; }
    }

    @JsonRootName("m")
    @JsonPropertyOrder({ "pageNumber", "messages" })
    public class Messages {
        protected List<Message> messages = new ArrayList<Message>();
        protected int pageNumber;

        protected Messages() {}

        public Messages(List<Message> messages, int pageNumber) {
            this.messages = messages;
            this.pageNumber = pageNumber;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public int getPageNumber() {
            return pageNumber;
        }
    }

    /*
    /**********************************************************************
    /* Unit tests
    /**********************************************************************
     */
    
    // [Issue-1]
    public void testSimpleWriteTo() throws Exception
    {
        Messages msgs = new Messages();
        msgs.pageNumber = 3;
        msgs.messages.add(new Message("foo"));
        msgs.messages.add(new Message("bar"));

        JacksonYAMLProvider prov = new JacksonYAMLProvider();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MediaType mt = MediaType.APPLICATION_JSON_TYPE;
        prov.writeTo(msgs, Messages.class, Messages.class, new Annotation[0], mt, null, out);

        String yaml = out.toString("UTF-8");

        assertEquals("---\n" +
                        "pageNumber: 3\n" +
                        "messages:\n" +
                        "- text: \"foo\"\n" +
                        "- text: \"bar\"\n", yaml);
    }
}
