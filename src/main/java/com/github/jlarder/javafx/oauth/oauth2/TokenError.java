/*
 * Copyright 2020 Andrey Kazakov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jlarder.javafx.oauth.oauth2;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Andrey Kazakov
 */
public class TokenError {
    
    public static final String COMMON_ERROR = "common_error";
    public static final String INVALID_REQUEST = "invalid_request";
    public static final String INVALID_CLIENT = "invalid_client";
    public static final String INVALID_GRANT = "invalid_grant";
    public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";
    public static final String ACCESS_DENIED = "access_denied";
    public static final String UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    public static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
    public static final String INVALID_SCOPE = "invalid_scope";
    public static final String SERVER_ERROR = "server_error";
    public static final String TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";
    
    
    private final Map<String, String> parameters;

    
    public TokenError() {
        parameters = new HashMap<>();
        parameters.put("error", COMMON_ERROR);
    }

    
    public String getError() {
        return parameters.get("error");
    }

    
    public void setError(String error) {
        parameters.put("error", error);
    }
    
    
    public String getErrorDescription() {
        return parameters.get("error_description");
    }
    
    
    public void setErrorDescription(String description) {
        parameters.put("error_description", description);
    }
    
    
    public String getErrorUri() {
        return parameters.get("error_uri");
    }
    
    
    public void setErrorUri(String uri) {
        parameters.put("error_uri", uri);
    }
    
    
    public void set(String key, String value) {
        parameters.put(key, value);
    }
    
    
    public String get(String key) {
        return parameters.get(key);
    }

    
    public Map<String, String> getAll() {
        return parameters;
    }

    
    public void setAll(Map<String, String> parameters) {
        this.parameters.putAll(parameters);
    }
    
    
    public static TokenError fromMap(Map<String, String> map) {
        if(!map.containsKey("error")) 
            throw new IllegalArgumentException("Error parameter is missing"); 
        TokenError tokenError = new TokenError();
        tokenError.setAll(map);
        return tokenError;
    }

    
    @Override
    public String toString() {
        return "TokenError{" + "parameters=" + parameters + '}';
    }
    
}
