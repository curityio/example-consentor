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
        return "price-consent";
    }

    @Override
    public Class<? extends ExampleConsentorConfig> getConfigurationType()
    {
        return ExampleConsentorConfig.class;
    }

    @Override
    public Map<String, Class<? extends ConsentorCompletionRequestHandler<?>>> getConsentorRequestHandlerTypes() {
        Map<String, Class<? extends  ConsentorCompletionRequestHandler<?>>> exampleRequestHandlerTypes = new HashMap<>();
        exampleRequestHandlerTypes.put("index", ExampleConsentorHandler.class);
        return exampleRequestHandlerTypes;
    }
}
