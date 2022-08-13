package tools.jackson.jaxrs.json.resteasy;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import tools.jackson.jaxrs.json.JaxrsTestBase;

public class RestEasyProviderLoadingTest extends JaxrsTestBase
{
    public void testLoading() throws Exception
    {
        // 13-Aug-2022, tatu: Won't work, this is Jackson 3.x, not 2.x
        //  Should probably just delete the test but comment out for now

        ResteasyJackson2Provider prov = null;
        /*
        ResteasyJackson2Provider provider = new ResteasyJackson2Provider();
        assertNotNull(provider); // just to avoid compiler warning
        */
    }
}
