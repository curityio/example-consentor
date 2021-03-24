/*
 * Copyright (C) 2019 Curity AB. All rights reserved.
 *
 * The contents of this file are the property of Curity AB.
 * You may not copy or use this file, in either source code
 * or executable form, except in compliance with terms
 * set by Curity AB.
 *
 * For further information, please contact Curity AB.
 */

package com.example.curity.consentor;

import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.attribute.AttributeName;
import se.curity.identityserver.sdk.oauth.consent.ConsentorCompletionRequestHandler;
import se.curity.identityserver.sdk.oauth.consent.ConsentorCompletionResult;
import se.curity.identityserver.sdk.service.SessionManager;
import se.curity.identityserver.sdk.web.Request;
import se.curity.identityserver.sdk.web.Response;
import se.curity.identityserver.sdk.web.ResponseModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExampleConsentorHandler implements ConsentorCompletionRequestHandler<Request>
{
    private final SessionManager _sessionManager;

    public ExampleConsentorHandler(ExampleConsentorConfig config) {
        this._sessionManager = config.getSessionManager();
    }

    @Override
    public Optional<ConsentorCompletionResult> get(Request request, Response response) {
        return Optional.empty();
    }

    @Override
    public Optional<ConsentorCompletionResult> post(Request request, Response response) {
        _sessionManager.put(Attribute.of(AttributeName.of(
                ExampleConsentor.APPROVED), true));
        return Optional.of(ConsentorCompletionResult.complete());
    }

    @Override
    public Request preProcess(Request request, Response response) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("_price", _sessionManager.get("price").getValue());
        templateVariables.put("_currency", _sessionManager.get("currency").getValue());
        response.setResponseModel(ResponseModel.templateResponseModel(templateVariables, "index")
                , Response.ResponseModelScope.ANY);
        return request;
    }
}
