package com.example.curity.consentor;

import se.curity.identityserver.sdk.oauth.consent.Consentor;
import se.curity.identityserver.sdk.oauth.consent.ConsentorCompletionRequestHandler;
import se.curity.identityserver.sdk.plugin.descriptor.ConsentorPluginDescriptor;

import java.util.HashMap;
import java.util.Map;

public final class ExampleConsentorDescriptor implements ConsentorPluginDescriptor<ExampleConsentorConfig>
{
    @Override
    public Class<? extends Consentor> getConsentorType()
    {
        return ExampleConsentor.class;
    }

    @Override
    public String getPluginImplementationType()
    {
        return "example";
    }

    @Override
    public Class<? extends ExampleConsentorConfig> getConfigurationType()
    {
        return ExampleConsentorConfig.class;
    }

    @Override
    public Map<String, Class<? extends ConsentorCompletionRequestHandler<?>>> getConsentorRequestHandlerTypes() {
        Map exampleRequestHandlerTypes = new HashMap<String, Class<? extends  ConsentorCompletionRequestHandler<?>>>();
        exampleRequestHandlerTypes.put("index", ExampleConsentorHandler.class);
        return exampleRequestHandlerTypes;
    }
}
