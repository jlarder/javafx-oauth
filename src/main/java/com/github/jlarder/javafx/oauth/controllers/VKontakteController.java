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

import javax.inject.Inject;
import com.github.jlarder.javafx.oauth.oauth2.OAuth2Properties;
import org.springframework.core.env.Environment;
/**
 *
 * @author Andrey Kazakov
 */
public abstract class VKontakteController extends AbstractOAuth2NodeController {
    
    private static final String PROVIDER = "vkontakte";
    
    @Inject    
    private Environment environment;
    
      
    @Override
    protected OAuth2Properties getOAuth2Properties() {
        OAuth2Properties props = new OAuth2Properties.ImplicitBuilder(
                "https://oauth.vk.com/authorize", "https://oauth.vk.com/blank.html",
                environment.getRequiredProperty("oauth.vkontakte.client-id"))
            .useState()
            .optionalAuthorizationRqeuestParams()
                .scopes(",", environment.getRequiredProperty("oauth.vkontakte.scope", String[].class))
                .parameter("display", "mobile")
                .parameter("v", "5.103")
                .add()
            .buid();
        return props;
    }

    
    @Override
    protected String getProvider() {
        return PROVIDER;
    }
      
}
