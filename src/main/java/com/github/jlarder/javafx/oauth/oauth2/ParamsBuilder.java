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


import java.util.Map;

/**
 *
 * @author Andrey Kazakov
 * @param <T>
 */
public class ParamsBuilder<T extends OAuth2Properties.Builder> {
    
    private final Map<String, String> params;
    private final T builder;
    
    
    public ParamsBuilder(T builder,
            Map<String, String> params) {
        this.params = params;
        this.builder = builder;
    }
    
    
    public T add() {
        return builder;
    }
    
     
    public ParamsBuilder<T> scope(String scope) {
        params.put("scope", scope);
        return this;
    }
    
    
    public ParamsBuilder<T> scopes(String delimiter, String... scopes) {
        params.put("scope", String.join(delimiter, scopes));
        return this;
    }
    
    
    public ParamsBuilder<T> clientSecret(String clientSecret) {
        params.put("client_secret", clientSecret);
        return this;
    }
    
    
    public ParamsBuilder<T> clientId(String clientId) {
        params.put("client_id", clientId);
        return this;
    }
    
    
    public ParamsBuilder<T> parameter(String key, String value) {
        params.put(key, value);
        return this;
    }
   
}
