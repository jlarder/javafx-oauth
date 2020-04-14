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
package com.github.jlarder.javafx.oauth.controllers;

import java.util.Random;
import javax.inject.Inject;
import com.github.jlarder.javafx.oauth.oauth2.OAuth2Properties;
import org.springframework.core.env.Environment;

/**
 *
 * @author Andrey Kazakov
 */
public abstract class GoogleController extends AbstractOAuth2NodeController {
    
    private static final String PROVIDER = "google";
    
    @Inject    
    private Environment environment;
    
    
    @Override
    protected OAuth2Properties getOAuth2Properties() {
        String clientId = environment.getRequiredProperty("oauth.google.client-id");
        String clientSecret = environment.getRequiredProperty("oauth.google.client-secret");
        String codeVerifier = generateCodeVerifier();
        
        
        OAuth2Properties props = new OAuth2Properties.AuthorizationCodeBuilder(
                "https://accounts.google.com/o/oauth2/v2/auth", "https://oauth2.googleapis.com/token",
                "http://127.0.0.1:56785/blank.html", clientId)
            .useState()
            .optionalAuthorizationRqeuestParams()
                .scopes(" ", environment.getRequiredProperty("oauth.google.scope", String[].class))
                .parameter("code_challenge", codeVerifier)
                .parameter("code_challenge_method", "plain")
                .add()
            .optionalTokenRequestParams()
                .clientSecret(clientSecret)
                .parameter("code_verifier", codeVerifier)
                .add()
            .optionalRefreshRequestParams()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .add()
            .buid();
        
        return props;
    }

    
    @Override
    protected String getProvider() {
        return PROVIDER;
    }
    
    
    private static final String CHARS = 
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";
    
    private String generateCodeVerifier() {
        StringBuilder builder= new StringBuilder();
        Random random = new Random(System.nanoTime());
        for(int i = 0; i < 64; i++) {
            builder.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return builder.toString();
    }
    
}
