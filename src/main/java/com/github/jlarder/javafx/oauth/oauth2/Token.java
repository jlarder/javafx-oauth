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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Andrey Kazakov
 */

public class Token {
    
    private static final Log LOG = LogFactory.getLog(Token.class);
    
    private final Map<String, String> parameters;
    
    
    private Token() {
        parameters = new HashMap<>();
    }
    
    
    public Token(String accessToken) {
        parameters = new HashMap<>();
        parameters.put("access_token", accessToken);
    }
    
    
    public void setAccessToken(String accessToken) {
        parameters.put("access_token", accessToken);
    }
    
    
    public String getAccessToken() {
        return parameters.get("access_token");
    }
    
    
    public void setRefreshToken(String refreshToken) {
        parameters.put("refresh_token", refreshToken);
    }
    
    
    public String getRefreshToken() {
        return parameters.get("refresh_token");
    }

    
    public Integer getExpiresIn() {
        try {
            return Integer.parseInt(parameters.get("expires_in"));
        } catch(NumberFormatException ex) {
            LOG.error("Invalid expires_in value", ex);
            return null;
        }
    }

    
    public void setExpiresIn(Integer expiresIn) {
        parameters.put("expires_in", expiresIn.toString());
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
    
    
    public void setAll(Map<String, String> params) {
        parameters.putAll(params);
    }
    
    
    public Long getCreationTimestamp() {
        try {
            return Long.parseLong(parameters.get("creation_timestamp"));
        } catch(NumberFormatException ex) {
            LOG.error("Invalid creation_time value", ex);
            return null;
        }
    }
    
    
    public void setCreationTimestamp(Long timestamp) {
        parameters.put("creation_timestamp", timestamp.toString());
    }
    
    
    public boolean isExpired() {
        Integer expiresIn = getExpiresIn();
        Long creationTimestamp = getCreationTimestamp();
        if(expiresIn == null || creationTimestamp == null) return false;         
        return expiresIn != 0 
                && System.currentTimeMillis() >= creationTimestamp + expiresIn * 1000; 
    }
    
    
    public void update(Token newToken) {
        setAll(newToken.getAll());
    } 
    
    
    
    public static Token fromMap(Map<String, String> map) {
        return fromMap(map, null);
    }
    
  
    public static Token fromMap(Map<String, String> map, Long creationTimestamp) { 
        if(!map.containsKey("access_token")) throw new IllegalArgumentException("access_token is missing");
        Token accessToken = new Token();
        accessToken.setAll(map);
        if(creationTimestamp != null) accessToken.setCreationTimestamp(creationTimestamp);
        return accessToken;
    }
    

    @Override
    public String toString() {
        return "Token{" + "parameters=" + parameters + '}';
    }
   
}
