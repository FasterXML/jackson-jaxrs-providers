package com.fasterxml.jackson.jaxrs.json.dw;

import org.eclipse.jetty.server.Server;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.config.ServerFactory;
import com.yammer.dropwizard.json.ObjectMapperFactory;
import com.yammer.dropwizard.validation.Validator;

public class TestService extends Service<TestServiceConfig>
{
    protected final static ObjectMapperFactory MAPPER_FACTORY = new ObjectMapperFactory();
    
    protected Server _jettyServer;

    protected TestService() { }

    public static TestService create(TestServiceConfig config,
            Object... resources)
        throws Exception
    {
        TestService service = new TestService();
        Bootstrap<TestServiceConfig> bootstrap = new Bootstrap<TestServiceConfig>(service);
        final Environment environment = new Environment("TestService", config,
                MAPPER_FACTORY, new Validator());
        for (Object resource : resources) {
            environment.addResource(resource);
        }
        bootstrap.runWithBundles(config, environment);
        service.run(config, environment);
        final Server server = new ServerFactory(config.getHttpConfiguration(),
                "StoreForTests").buildServer(environment);
        service._jettyServer = server;
        return service;
    }

    public void start() throws Exception {
        _jettyServer.start();
    }

    public void stop() throws Exception {
        _jettyServer.stop();
    }
    
    @Override
    public void initialize(Bootstrap<TestServiceConfig> bootstrap) {
    }

    @Override
    public void run(TestServiceConfig configuration, Environment environment)
            throws Exception
    {
    }
}
