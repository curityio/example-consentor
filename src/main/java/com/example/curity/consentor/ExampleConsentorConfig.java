package com.example.curity.consentor;

import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.config.annotation.Description;
import se.curity.identityserver.sdk.service.SessionManager;
import se.curity.identityserver.sdk.service.WebServiceClient;

public interface ExampleConsentorConfig extends Configuration
{
    @Description("The prefix of the scope or list of scopes to present to the user")
    String getScopePrefix();

    @Description("Session manager keeps data about the session and therefore helps to determine if consent was given.")
    SessionManager getSessionManager();

    @Description("The web service client is used to call an external API for getting data about a consent.")
    WebServiceClient getWebServiceClient();
}
