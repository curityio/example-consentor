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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ExampleConsentor implements Consentor
{
    public static String APPROVED = "exampleConsentorApproved";
    private final String _scopePrefix;
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
        _scopePrefix = configuration.getScopePrefix();
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
        if (isCompleted()) {
            return ConsentorResult.success(ConsentorResultAttributes.of(
                    Attribute.of("client_id", consentAttributes.getClientId()),
                    Attribute.of("subject", consentAttributes.getAuthenticationAttributes().getSubject()),
                    Attribute.of("consentor_id", _configuration.id()),
                    _sessionManager.get("price") // That's what the user consented to.
            ));
        }  else {
            Optional<String> transactionId = getTransactionId(consentAttributes);
            Optional<Integer> price = getPriceForTransaction(transactionId);

            if (price.isPresent()) {
                _sessionManager.put(Attribute.of(AttributeName.of("price"), price.get()));
                _sessionManager.put(Attribute.of(AttributeName.of("currency"), "SEK"));
                return ConsentorResult.pendingWithPromptUserCompletion();
            } else {
                return ConsentorResult.unsuccessfulResult("Cannot find order.", ErrorCode.EXTERNAL_SERVICE_ERROR);
            }
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
        Collection<String> scopes = consentAttributes.getScopeNames();

        return scopes.stream()
                .filter((scope -> scope
                .startsWith(_scopePrefix)))
                .findFirst()
                .map(scope -> scope.replace(_scopePrefix,""));
    }

    private Optional<Integer> getPriceForTransaction(Optional<String> transactionId) {
        Optional<Integer> price = Optional.empty();

        if (transactionId.isPresent()) {
            Map<String, Collection<String>> queryParameters = new HashMap<>();
            queryParameters.put("transactionId", Collections.singleton(transactionId.get()));

            HttpResponse apiResponse = _webServiceClient.withQueries(
                    queryParameters)
                    .request()
                    .get()
                    .response();
            if (apiResponse.statusCode() == 200) {
                String jsonResponse = apiResponse.body(HttpResponse.asString());
                List<?> jsonList = _jsonService.fromJsonArray(jsonResponse);
                Optional<Map> order = jsonList.stream().map(Map.class::cast).findFirst();

                if (order.isPresent()) {
                    price = Optional.of((Integer) order.get().get("total_price"));
                }
            }
        }

        return price;
    }
}
