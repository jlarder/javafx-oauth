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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Andrey Kazakov
 */
@Service
public class OAuth2Service {   
       
    private static final Log LOG = LogFactory.getLog(OAuth2Service.class);
    
    private static final String AUTH_FXML_FILE = "/com/github/jlarder/javafx/oauth/fxml/auth.fxml";
    
    @Inject
    private TokenRepository tokenRepository;
    
    @Inject
    private FXMLLoader fxmlLoader;
    
    private final RestTemplate restTemplate;
    
    private String authWindowTitle;
    private Integer authWindowWidth = 500;
    private Integer authWindowHeight = 600; 
    
    
    public OAuth2Service() {
        restTemplate = getConfiguredRestTemplate(); 
    }
    
       
    public void getToken(String provider, OAuth2Properties props,
            TokenHandler tokenHandler) {                                   
        try { 
            Token token = tokenRepository.getToken(provider);          
            if(!token.isExpired()) {
                tokenHandler.handle(token, null);                
            } else if(token.getRefreshToken() == null) {
                showAuthenticationWindow(provider, props, tokenHandler);      
            } else {
                refreshToken(token, provider, props, tokenHandler);      
            }
        } catch (TokenNotFoundException ex) {
            LOG.warn(ex.getMessage());
            showAuthenticationWindow(provider, props, tokenHandler);
        } catch (TokenRepositoryException ex) {
            LOG.error(ex.getMessage(), ex);
            showAuthenticationWindow(provider, props, tokenHandler);
        }
    }
    
    
    public void refreshToken(Token token, String provider, OAuth2Properties props,
            TokenHandler tokenHandler) {
        if(token == null || token.getRefreshToken() == null) {
            showAuthenticationWindow(provider, props, tokenHandler);
        } else {
            Map<String, String> params = props.getTokenRefreshParameters(token.getRefreshToken());
            try {
                Map<String, String> response = requestToken(params, props.getTokenRequestEndpoint());
                Token t = processingResponse(response);
                token.update(t);
                tokenRepository.saveToken(provider, token);
                tokenHandler.handle(token, null);
            } catch(TokenErrorException ex) {
                tokenHandler.handle(null, ex.getError());
            }
        }
    }
    
    
    public void removeToken(String provider) {
        tokenRepository.removeToken(provider);
    }

    
    private void showAuthenticationWindow(String provider, OAuth2Properties props,
            TokenHandler tokenHandler) {                                                
        
        if(fxmlLoader.getLocation() != null) fxmlLoader = getNewFXMLLoader(fxmlLoader);        
        fxmlLoader.setLocation(getClass().getResource(AUTH_FXML_FILE));
        Parent view;
        try {
            view = fxmlLoader.load();            
        } catch (IOException ex) {
            LOG.error("error load " + AUTH_FXML_FILE, ex);          
            tokenHandler.handle(null, new TokenError());
            return;
        }
        
        Stage stage = new Stage();
        stage.setScene(new Scene(view));
        stage.setTitle(authWindowTitle != null ? authWindowTitle : "Authentication");
        stage.setWidth(authWindowWidth);
        stage.setHeight(authWindowHeight);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setAlwaysOnTop(true);
        
        stage.addEventHandler(ReceiverEvent.TOKEN_RECEIVED_EVENT_TYPE, (ReceiverEvent event) -> {
            Token token = Token.fromMap(event.getParameters(), System.currentTimeMillis());
            tokenRepository.saveToken(provider, token);
            event.getTokenHandler().handle(token, null);
            stage.close();
        });
        
        stage.addEventHandler(ReceiverEvent.CODE_RECEIVED_EVENT_TYPE, (ReceiverEvent event) -> {
            exchangeCodeForToken(event.getParameters(), event.getProvider(),
                    event.getOAuth2Properties(), event.getTokenHandler());
            stage.close();
        });
        
        stage.addEventHandler(ReceiverEvent.ERROR_RECEIVED_EVENT_TYPE, (ReceiverEvent event) -> {
            event.getTokenHandler().handle(null, event.getParameters() != null ?
                    TokenError.fromMap(event.getParameters()) : new TokenError());
            stage.close();
        });
        
        stage.show();
        
        OAuth2Controller oauthController = fxmlLoader.getController();
        oauthController.auth(provider, props, tokenHandler); 
    }
    
    
    private void exchangeCodeForToken(Map<String, String> params, String provider,
            OAuth2Properties props, TokenHandler handler) {
        LOG.debug("code is exchanged for token ...");
        try {
            Map<String, String> response = requestToken(props.getTokenRequestParameters(params.get("code")),
                    props.getTokenRequestEndpoint());
            Token token = processingResponse(response);
            tokenRepository.saveToken(provider, token);
            handler.handle(token, null);
        } catch (TokenErrorException ex) {
            handler.handle(null, ex.getError());
        }
    }
    
    
    private Token processingResponse(Map<String, String> response) {
        if(response.containsKey("access_token")) {
            LOG.debug("a token was received");
            return Token.fromMap(response, System.currentTimeMillis());
        } else if(response.containsKey("error")) {
            LOG.debug("an error was received " + response);
            throw new TokenErrorException(TokenError.fromMap(response));
        } else {
            LOG.error("Unknown response " + response);
            throw new TokenErrorException(new TokenError());
        }
    }
    
    
    private Map<String, String> requestToken(Map<String, String> params,
            String requestTokenUri) {
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(); 
        params.entrySet().forEach((entry) -> {
            map.add(entry.getKey(), entry.getValue());
        });                                                                          
       
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = 
                new HttpEntity<>(map, headers);
        
        try {
            Map<String, Object> response = restTemplate
                    .postForObject(requestTokenUri, request, Map.class);
            if(response == null) return null;
            
            Map<String, String> responseString = new HashMap<>();
            response.entrySet().forEach((entry) -> {
                responseString.put(entry.getKey(), entry.getValue().toString());
            });
            return responseString;
        } catch (RuntimeException ex){
            LOG.error(ex);
            throw new TokenErrorException(new TokenError());
        }        
    }
    
    
    public void setAuthWindowTitle(String title) {
        authWindowTitle = title;
    }
    
    
    public String getAuthWindowTitle() {
        return authWindowTitle;
    }
    
    
    public void setAuthWindowWidth(Integer width) {
        authWindowWidth = width;
    }
    
    
    public Integer getAuthWindowWidth() {
        return authWindowWidth;
    }
    
    
    public void setAuthWindowHeight(Integer heidht) {
        authWindowHeight = heidht;
    }
    
    
    public Integer getAuthWindowHeight() {
        return authWindowHeight;
    }
    
    
    private FXMLLoader getNewFXMLLoader(FXMLLoader loader) {
        FXMLLoader newLoader = new FXMLLoader();
        newLoader.setControllerFactory(loader.getControllerFactory());
        return newLoader;
    }
    
    
    private RestTemplate getConfiguredRestTemplate() {
        RestTemplate restTemp = new RestTemplate();
        
        ObjectMapper objectMapper = new ObjectMapper();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = 
                new MappingJackson2HttpMessageConverter(objectMapper);
        restTemp.getMessageConverters().add(mappingJackson2HttpMessageConverter);
                
        restTemp.setErrorHandler(new ResponseErrorHandler() {
            
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException { 
                return !(response.getStatusCode() == HttpStatus.OK ||
                        response.getStatusCode() == HttpStatus.BAD_REQUEST);
            }
            
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                throw new RuntimeException("Response Error: status code " 
                        + response.getStatusCode());
            }
        });
        return restTemp;
    }
    
}
