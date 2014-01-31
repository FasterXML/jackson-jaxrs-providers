package com.fasterxml.jackson.jaxrs.json.resteasy;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import com.fasterxml.jackson.jaxrs.json.JaxrsTestBase;

public class RestEasyProviderLoadingTest extends JaxrsTestBase
{
    public void testLoading() throws Exception
    {
        ResteasyJackson2Provider provider = new ResteasyJackson2Provider();
        assertNotNull(provider); // just to avoid compiler warning
    }
}
