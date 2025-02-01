package com.fasterxml.jackson.jaxrs.json.resteasy;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.jaxrs.json.JaxrsTestBase;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RestEasyProviderLoadingTest extends JaxrsTestBase
{
    @Test
    public void testLoading() throws Exception
    {
        ResteasyJackson2Provider provider = new ResteasyJackson2Provider();
        assertNotNull(provider); // just to avoid compiler warning
    }
}
