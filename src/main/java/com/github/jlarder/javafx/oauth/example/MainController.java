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
package com.github.jlarder.javafx.oauth.example;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javax.inject.Inject;

/**
 * FXML Controller class
 *
 * @author Andrey Kazakov
 */
public class MainController implements Initializable {
    
    @Inject
    private FXMLLoader fxmlLoader;
    
    @FXML
    private BorderPane borderPane;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    
    @FXML
    public void onMenuFacebookExampleAction() throws IOException {
        loadMainPane("/com/github/jlarder/javafx/oauth/example/fxml/facebook.fxml");
    }
    
    
    @FXML
    public void onMenuVkontakteExampleAction() throws IOException {
        loadMainPane("/com/github/jlarder/javafx/oauth/example/fxml/vkontakte.fxml");
    }
    
    
    @FXML
    public void onMenuYouTubeExampleAction() throws IOException {
        loadMainPane("/com/github/jlarder/javafx/oauth/example/fxml/youtube.fxml");
    }
    
    
    private void loadMainPane(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setControllerFactory(fxmlLoader.getControllerFactory());
        AnchorPane pane = loader.load();
        borderPane.setCenter(pane);
    }
    
}
