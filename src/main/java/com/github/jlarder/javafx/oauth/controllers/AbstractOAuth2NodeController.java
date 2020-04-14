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

import com.github.jlarder.javafx.oauth.oauth2.OAuth2Properties;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javax.inject.Inject;
import com.github.jlarder.javafx.oauth.oauth2.OAuth2Service;
import com.github.jlarder.javafx.oauth.oauth2.Token;
import com.github.jlarder.javafx.oauth.oauth2.TokenError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Andrey Kazakov
 */
public abstract class AbstractOAuth2NodeController implements Initializable {
    
    private static final Log LOG = LogFactory
            .getLog(AbstractOAuth2NodeController.class);
    
    @Inject
    private OAuth2Service oauth2Service;
    
    private Token token;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(getNode() != null) getNode().setDisable(true);              
        oauth2Service.getToken(getProvider(), getOAuth2Properties(),
                (Token t, TokenError error) -> {                             
            if(error == null) {
                if(getNode() != null) getNode().setDisable(false);
                token = t;                                                 
            } else {
                LOG.error("Error get token " + error);
            }
        });
    }
    
    
    public Token getToken() {
        if(token != null && token.isExpired()) refreshToken();
        return token;
    }
    
    
    public void refreshToken() {
        if(getNode() != null) getNode().setDisable(true);
        oauth2Service.refreshToken(token, getProvider(), getOAuth2Properties(),
                (Token t, TokenError error) -> {
             if(error == null) {
                if(getNode() != null) getNode().setDisable(false);
                token = t;
            } else {
                LOG.error("Error refresh token " + error);
            }
        });
    }
    
    
    public void removeToken() {
        oauth2Service.removeToken(getProvider());
    }
    
    
    protected abstract Node getNode();
    

    protected abstract OAuth2Properties getOAuth2Properties();
    
    
    protected abstract String getProvider();
    
}
