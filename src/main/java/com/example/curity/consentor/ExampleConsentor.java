package com.example.curity.consentor;

import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.attribute.AttributeName;
import se.curity.identityserver.sdk.attribute.ConsentAttributes;
import se.curity.identityserver.sdk.errors.ErrorCode;
import se.curity.identityserver.sdk.http.HttpResponse;
import se.curity.identityserver.sdk.oauth.consent.Consentor;
import se.curity.identityserver.sdk.oauth.consent.ConsentorResult;
import se.curity.identityserver.sdk.oauth.consent.ConsentorResultAttributes;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.SessionManager;
import se.curity.identityserver.sdk.service.WebServiceClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ExampleConsentor implements Consentor
{
    public static String APPROVED = "exampleConsentorApproved";
    private final ExampleConsentorConfig _configuration;
    private final SessionManager _sessionManager;
    private final WebServiceClient _webServiceClient;
    private final Json _jsonService;

    public ExampleConsentor(ExampleConsentorConfig configuration)
    {
        _configuration = configuration;
        _sessionManager = configuration.getSessionManager();
        _webServiceClient = configuration.getWebServiceClient();
        _jsonService = configuration.getJsonService();

        // See examples of consentors on GitHub: https://github.com/search?q=topic%3Aconsentor+org%3Acurityio
    }

    /**
     * Apply must return a non-pending result (success or failure) in order to finish the consent flow.
     * @param consentAttributes list of attributes that can be used in the consent, e.g. scopes
     * @param consentId an id to identify the approval flow
     * @return a success result if the user confirmed the action. Pending otherwise.
     */
    @Override
    public ConsentorResult apply(ConsentAttributes consentAttributes, String consentId)
    {
        Optional<String> transactionId = getTransactionId(consentAttributes);
        Optional<String> textToDisplay = getTextToDisplay(transactionId);

        if (textToDisplay.isPresent()) {
            _sessionManager.put(Attribute.of(AttributeName.of("textToDisplay"),
                    textToDisplay.get()));
        } else {
            return ConsentorResult.unsuccessfulResult("Cannot find order.", ErrorCode.EXTERNAL_SERVICE_ERROR);
        }

        if (isCompleted()) {
            return ConsentorResult.success(ConsentorResultAttributes.of(
                    Attribute.of("client_id", consentAttributes.getClientId()),
                    Attribute.of("subject", consentAttributes.getAuthenticationAttributes().getSubject()),
                    Attribute.of("consentor_id", _configuration.id())
            ));
        }   else {
            return ConsentorResult.pendingWithPromptUserCompletion();
        }
    }

    /**
     * In this example we use the session manager to determine if the user closed the window.
     * But you can implement any other logic here such as making calls to an API.
     * @return Returns true if the user approved the request.
     */
    private boolean isCompleted() {
        Attribute userApproval = _sessionManager.get(APPROVED);
        if (userApproval != null && userApproval.hasValueOfType(Boolean.class)) {
            return userApproval.getValueOfType(Boolean.class);
        } else {
            return false;
        }
    }

    private Optional<String> getTransactionId(ConsentAttributes consentAttributes) {
        String scopePrefix = _configuration.getScopePrefix() + "_";
        Collection<String> scopes = consentAttributes.getScopeNames();

        return scopes.stream()
                .filter((scope -> scope
                .startsWith(scopePrefix)))
                .findFirst()
                .map(scope -> scope.replace(scopePrefix,""));
    }

    private Optional<String> getTextToDisplay(Optional<String> transactionId) {
        Optional<String> textToDisplay = Optional.empty();

        if (transactionId.isPresent()) {
            _webServiceClient.withQuery(String.format("transactionId=%s", transactionId.get()));

            HttpResponse apiResponse = _webServiceClient.request().get().response();
            if (apiResponse.statusCode() == 200) {
                //Map<String, Object> jsonObjectMap = apiResponse.body(HttpResponse.asJsonObject(_configuration.getJsonService()));
                //textToDisplay = "Do you want to order for " + jsonObjectMap.get("total_price") + " SEK?";
                String jsonResponse = apiResponse.body(HttpResponse.asString());
//                String jsonResponse = "[{\"id\": 123, \"item_count\": 2,\"total_price\": 198, \"status\": \"created\", \"transactionId\": 123}]";
                List<?> jsonList = _jsonService.fromJsonArray(jsonResponse);
                Optional<Map> order = jsonList.stream().map(Map.class::cast).findFirst();

                if (order.isPresent()) {
                    textToDisplay = Optional.of("Do you want to order for " + order.get().get("total_price") + " SEK?");
                }
            }
        }

        return textToDisplay;
    }
}
