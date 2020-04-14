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

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import com.github.jlarder.javafx.oauth.controllers.VKontakteController;
import com.github.jlarder.javafx.oauth.oauth2.Token;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Andrey Kazakov
 */
public class VkontakteExampleController extends VKontakteController {
    
    @FXML
    private AnchorPane pane;
    
    @FXML
    private Label label;
    

    @Override
    protected Node getNode() {
        return pane;
    }

    
    @Override
    protected void onGetToken(Token token) {
        label.setText("Assess token: " + token.getAccessToken());
    }

}
