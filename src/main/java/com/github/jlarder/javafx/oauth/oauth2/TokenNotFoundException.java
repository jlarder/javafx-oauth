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

/**
 *
 * @author Andrey Kazakov
 */
public class TokenNotFoundException extends TokenRepositoryException {
    
    private final String provider;
    
    public TokenNotFoundException(String provider, String message) {
        super(message);
        this.provider = provider;
    }

    public TokenNotFoundException(String provider, String message, Throwable cause) {
        super(message, cause);
        this.provider = provider;
    }
    
    public String getProvider() {
        return provider;
    }
}
