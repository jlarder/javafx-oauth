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

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Andrey Kazakov
 */
public class OAuth2Controller implements Initializable, ChangeListener<String> {

    private static final String USER_AGENT = "JavaFx-OAuth2-Client";
    
    private static final Log LOG = LogFactory.getLog(OAuth2Controller.class);
    
    @FXML
    private WebView webView;
    private WebEngine webEngine;
    
    private OAuth2Properties authProps;
    
    private String provider;
    private TokenHandler tokenHandler;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        webEngine = webView.getEngine();
        webEngine.setUserAgent(USER_AGENT);
        webEngine.locationProperty().addListener(this);
    }
    
    
    public void auth(String provider, OAuth2Properties props, TokenHandler handler) { 
        this.provider = provider;
        this.authProps = props;
        this.tokenHandler = handler;
        
        webView.getScene().getWindow().setOnCloseRequest((WindowEvent event) -> {
            LOG.debug("Close auth window");
            fireReceivedEvent(ReceiverEvent.ERROR_RECEIVED_EVENT_TYPE, null);
        });
        
        String request = authProps.getAuthorizationRequestUrl(); 
        LOG.debug("loading authorization page ..." + request );
        webEngine.load(request);
    }

    
    @Override
    public void changed(ObservableValue<? extends String> ov, String s1, String s2) {
        if(s2.matches("^" + authProps.getRedirectUri() + "[\\S]+")) {
            Map<String, String> params = parseRedirectUriParameters(s2); 
            if(authProps.isUseState() && ! validateStateParameter(params)) {
                LOG.error("error getting the token: invalid state parameter");
                fireReceivedEvent(ReceiverEvent.ERROR_RECEIVED_EVENT_TYPE, null);
                return;
            } 
            if(params.containsKey("access_token")) {
                LOG.debug("a token was received");
                fireReceivedEvent(ReceiverEvent.TOKEN_RECEIVED_EVENT_TYPE, params);
            } else if(params.containsKey("code")) {
                LOG.debug("a code was received");
                fireReceivedEvent(ReceiverEvent.CODE_RECEIVED_EVENT_TYPE, params);
            } else if(params.containsKey("error")) {
                LOG.debug("an error was received " + params);
                fireReceivedEvent(ReceiverEvent.ERROR_RECEIVED_EVENT_TYPE, params);
            } else {
                LOG.error("Unknown data" + s2);
                fireReceivedEvent(ReceiverEvent.ERROR_RECEIVED_EVENT_TYPE, null);
            }
        }
        
    }

    
    private void fireReceivedEvent(EventType<ReceiverEvent> type, Map<String, String> params) {
        ReceiverEvent event = new ReceiverEvent(type, params, provider, authProps, tokenHandler);
        webView.getScene().getWindow().fireEvent(event);
    }
    
   
    private boolean validateStateParameter(Map<String, String> params) {    
        String state = params.remove("state");
        return authProps.isUseState() && authProps.getState().equals(state);
    }
    
    
    private Map<String, String> parseRedirectUriParameters(String uri) {
        Map<String, String> params = new HashMap<>();
        Pattern pattern = Pattern.compile("^(\\?([^#]*))?(#(.*))?");
        Matcher matcher = pattern.matcher(uri.substring(authProps.getRedirectUri().length(), uri.length()));  
        if(matcher.find()) {
            String s = "";
            String query = matcher.group(2);
            String fragment = matcher.group(4);
            if(query != null && !query.isEmpty()) {
                s = query;
            } else if(fragment != null && !fragment.isEmpty()) {
                s = fragment;
            }
            String[] ps = s.split("&");
            for(String p : ps) {
                String[] kv = p.split("=");
                if(kv.length == 2 && !kv[0].isEmpty()) {
                    String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                    params.put(key, value);
                }
            }     
        }
        return params;
    }
    
}
