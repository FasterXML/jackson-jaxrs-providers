package com.fasterxml.jackson.jaxrs.json.dw;

import com.yammer.dropwizard.config.Configuration;

public class TestServiceConfig extends Configuration
{
    public TestServiceConfig(int port) {
        this.getHttpConfiguration().setPort(port);
    }
}
