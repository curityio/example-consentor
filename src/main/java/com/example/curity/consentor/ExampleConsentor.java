package com.example.curity.consentor;

import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.attribute.AttributeName;
import se.curity.identityserver.sdk.attribute.ConsentAttributes;
import se.curity.identityserver.sdk.oauth.consent.ConsentorResult;
import se.curity.identityserver.sdk.oauth.consent.Consentor;
import se.curity.identityserver.sdk.oauth.consent.ConsentorResultAttributes;
import se.curity.identityserver.sdk.service.SessionManager;

import java.util.Collection;
import java.util.Collections;

public final class ExampleConsentor implements Consentor
{
    public static String APPROVED = "exampleConsentorApproved";
    private final ExampleConsentorConfig _configuration;
    private final SessionManager _sessionManager;

    public ExampleConsentor(ExampleConsentorConfig configuration)
    {
        _configuration = configuration;
        _sessionManager = configuration.getSessionManager();

        // See examples of consentors on GitHub: https://github.com/search?q=topic%3Aconsentor+org%3Acurityio
    }

    /**
     * Apply must return a non-pending result (success or failure) in order to finish the consent flow.
     * @param consentAttributes
     * @param transactionId
     * @return
     */
    @Override
    public ConsentorResult apply(ConsentAttributes consentAttributes, String transactionId)
    {
        _sessionManager.put(Attribute.of(AttributeName.of("textToDisplay"),
                getTextToDisplay(consentAttributes)));

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
     * Returns true if the user approved the request.
     * In this example we use the session manager to determine if the user closed the window.
     * But you could anything here like calling an external API.
     * @return
     */
    private boolean isCompleted() {
        Attribute userApproval = _sessionManager.get(APPROVED);
        if (userApproval != null && userApproval.hasValueOfType(Boolean.class)) {
            return userApproval.getValueOfType(Boolean.class);
        } else {
            return false;
        }
    }

    private String getTextToDisplay(ConsentAttributes consentAttributes) {
        String scopePrefix = _configuration.getScopePrefix() + "_";
        StringBuilder textToDisplayBuilder = new StringBuilder();
        Collection<String> scopes = consentAttributes.getScopeNames();

        scopes.stream().filter((scope -> scope
                .startsWith(scopePrefix)))
                .forEach(scope -> {
                    String scopePostfix = scope.replaceFirst(scopePrefix, "");
                    textToDisplayBuilder.append(scopePostfix).append(" ");
                });
        return textToDisplayBuilder.toString();
    }
}
