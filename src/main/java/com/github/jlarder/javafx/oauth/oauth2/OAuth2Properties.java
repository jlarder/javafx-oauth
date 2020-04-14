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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Andrey Kazakov
 */
public class OAuth2Properties {
    
    private final GrantType grantType;
    
    private final String authorizationEndpoin;
    private final String tokenRequestEndpoint;
        
    private final Map<String, String> authorizationParams; 
    private final Map<String, String> tokenRequestParams;
    private final Map<String, String> tokenRefreshParams;
    

    private OAuth2Properties(GrantType grantType, String authorizationEndpoin,
            String tokenRequestEndpoint,Map<String, String> authorizationParams,
            Map<String, String> tokenRequestParams, Map<String, String> tokenRefreshParams) {
        this.grantType = grantType;
        this.authorizationEndpoin = authorizationEndpoin;
        this.tokenRequestEndpoint = tokenRequestEndpoint;
        this.authorizationParams = authorizationParams;
        this.tokenRequestParams = tokenRequestParams;
        this.tokenRefreshParams = tokenRefreshParams;
    }

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OAuth2Properties2{grantType=").append(grantType);
        sb.append(", authorizationEndpoin=").append(authorizationEndpoin);
        sb.append(", tokenRequestEndpoint=").append(tokenRequestEndpoint);
        sb.append(", authorizationParams=").append(authorizationParams);
        sb.append(", tokenRequestParams=").append(tokenRequestParams);
        sb.append(", tokenRefreshParams=").append(tokenRefreshParams);
        sb.append('}');
        return sb.toString();
    }
    
       
    public static final class AuthorizationCodeBuilder extends Builder<AuthorizationCodeBuilder> {
        
        public AuthorizationCodeBuilder(String autorizationEndpoint, String tokenRequestEndpoint,
                String redirectUri, String clientId) {
            super(GrantType.AUTHORIZATION_CODE, autorizationEndpoint, tokenRequestEndpoint);
            
            super.authorizationParams.put("response_type", "code");
            super.authorizationParams.put("client_id", clientId);
            super.authorizationParams.put("redirect_uri", redirectUri);
            
            super.tokenRequestParams.put("grant_type", "authorization_code");
            super.tokenRequestParams.put("client_id", clientId);
            super.tokenRequestParams.put("redirect_uri", redirectUri);
        }
        
        public ParamsBuilder<AuthorizationCodeBuilder> optionalAuthorizationRqeuestParams() {
            return new ParamsBuilder<>(this, super.authorizationParams);
        }

        public ParamsBuilder<AuthorizationCodeBuilder> optionalTokenRequestParams() {
            return new ParamsBuilder<>(this, super.tokenRequestParams);
        }
    }
    
    
    public static final class ImplicitBuilder extends Builder<ImplicitBuilder> {
      
        public ImplicitBuilder(String authorizationEndpoint, String redirectUri,
                String clientId) {
            super(GrantType.IMPLICIT, authorizationEndpoint, null);
            
            super.authorizationParams.put("response_type", "token");
            super.authorizationParams.put("client_id", clientId);
            super.authorizationParams.put("redirect_uri", redirectUri);
            
        }
        
        public ParamsBuilder<ImplicitBuilder> optionalAuthorizationRqeuestParams() {
            return new ParamsBuilder<>(this, super.authorizationParams);
        } 
    }
    
    
    public static final class ResourceOwnerCredentialsBuilder extends Builder<ResourceOwnerCredentialsBuilder> {
        
        public ResourceOwnerCredentialsBuilder(String tokenRequestEndpoint,
                String username, String password) {
            super(GrantType.RESOURCE_OWNER_CREDENTIALS, null, tokenRequestEndpoint);
            
            super.tokenRequestParams.put("grant_type", "password");
            super.tokenRequestParams.put("username", username);
            super.tokenRequestParams.put("password", password);
        }
        
        public ParamsBuilder<ResourceOwnerCredentialsBuilder> optionalTokenRequestParams() {
            return new ParamsBuilder<>(this, super.tokenRequestParams);
        }
    }
    
    
    protected static class Builder<T extends Builder> {
        
        private final GrantType grantType;
        
        private final String authorizationEndpoin;
        private final String tokenRequestEndpoint;
        
        private final Map<String, String> authorizationParams = new HashMap<>();
        private final Map<String, String> tokenRequestParams = new HashMap<>();
        private final Map<String, String> tokenRefreshParams = new HashMap<>();

        
        public Builder(GrantType grantType, String authorizationEndpoint, String tokenRequestEndpoint) {
            this.grantType = grantType;
            this.authorizationEndpoin = authorizationEndpoint;
            this.tokenRequestEndpoint = tokenRequestEndpoint;
            this.tokenRefreshParams.put("grant_type", "refresh_token");
        }
        
        public OAuth2Properties buid() {
            return new OAuth2Properties(grantType, authorizationEndpoin, tokenRequestEndpoint,
                    authorizationParams, tokenRequestParams, tokenRefreshParams);
        }
        
        
        public ParamsBuilder<T> optionalRefreshRequestParams() {
            return new ParamsBuilder<>((T) this, tokenRefreshParams);
        }
        
        
        public T useState() {
            authorizationParams.put("state", UUID.randomUUID().toString());
            return (T) this;
        }       
    }
    
    
    public GrantType getGrantType() {
        return grantType;
    }

    
    public String getAuthorizationEndpoin() {
        return authorizationEndpoin;
    }

    
    public String getTokenRequestEndpoint() {
        return tokenRequestEndpoint;
    }
        
     
    public Map<String, String> getAuthorizationParameters() {
        return Collections.unmodifiableMap(authorizationParams);
    }
    
    
    public Map<String, String> getTokenRequestParameters(String code) {
        tokenRequestParams.put("code", code);
        return Collections.unmodifiableMap(tokenRequestParams);
    }
    
     
    public Map<String, String> getTokenRefreshParameters(String refreshToken) {
        tokenRefreshParams.put("refresh_token", refreshToken);
        return Collections.unmodifiableMap(tokenRefreshParams);
    }
    
    
    public String getAuthorizationRequestUrl() {
        StringBuilder builder = new StringBuilder(authorizationEndpoin);
        if(authorizationParams.size() > 0) builder.append("?");
  
        Iterator<Map.Entry<String, String>> iterator = authorizationParams.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            builder.append( URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append( URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            if(iterator.hasNext()) builder.append("&");
        }
        return builder.toString();
    }
    
    
    public String getRedirectUri() {
        return authorizationParams.get("redirect_uri");
    }
    
    
    public boolean isUseState() {
        return authorizationParams.containsKey("state");
    }
    
    
    public String getState() {
        return authorizationParams.get("state");
    }

}
