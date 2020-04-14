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
import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author Andrey Kazakov
 */
public class ReceiverEvent extends Event {
    
    public static final EventType<ReceiverEvent> TOKEN_RECEIVED_EVENT_TYPE =
            new EventType<>(Event.ANY, "token-received");
    
    public static final EventType<ReceiverEvent> CODE_RECEIVED_EVENT_TYPE = 
            new EventType<>(Event.ANY, "code-received");
    
    public static final EventType<ReceiverEvent> ERROR_RECEIVED_EVENT_TYPE = 
            new EventType<>(Event.ANY, "error-received");
    
    private final Map<String, String> params;
    
    private final String provider;
    
    private final OAuth2Properties oauthProperties;
    
    private final TokenHandler tokenHandler;
    
    
    public ReceiverEvent(EventType<? extends Event> type, Map<String, String> params,
            String provider, OAuth2Properties props, TokenHandler handler) {
        super(type);
        this.params = params;
        this.provider = provider;
        this.oauthProperties = props;
        this.tokenHandler = handler;
    }
    
    
    public Map<String, String> getParameters() {
        return params;
    }
    
    
    public String getProvider() {
        return provider;
    }
    
    
    public OAuth2Properties getOAuth2Properties() {
        return oauthProperties;
    }
    
    
    public TokenHandler getTokenHandler() {
        return tokenHandler;
    }
    
}
