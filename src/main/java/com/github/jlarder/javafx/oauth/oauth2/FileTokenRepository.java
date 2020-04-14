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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Andrey Kazakov
 */
@Repository
public class FileTokenRepository implements TokenRepository {
    
    private static final Log LOG = LogFactory.getLog(FileTokenRepository.class);
    
    private static final String DEFAULT_FILES_SUFFIX = ".token";
    
    private static final String DEFAULT_LOCATION = "tokens";
    
    private String location;
    
    private String fileSuffix; 
    
    @Inject
    private Environment environment;

           
    @Override
    public void saveToken(String provider, Token token) {
        Properties properties = new Properties();
        properties.putAll(token.getAll());
        saveProperties(provider, properties);
    }
    
    
    @Override
    public Token getToken(String provider) {
        Properties properties = loadProperties(provider);
        if(properties == null || !properties.containsKey("access_token")) {
            throw new TokenRepositoryException("Invalid token " + properties);
        }
        Map<String, String> params = new HashMap<>();
        properties.entrySet().forEach((entry) -> {
            params.put((String) entry.getKey(), (String) entry.getValue());
        });  
        return Token.fromMap(params);
    }
    
    
    @Override
    public void removeToken(String provider) {
        File file = new File(provider + fileSuffix);
        if(file.delete()) {
            LOG.debug("File " + file + " deleted" );
        } else {
            LOG.error("Error deleting file " + file);
        }
    }
    
    
    private Properties loadProperties(String provider) {
        File file = getFile(provider);
        try(InputStream in = new FileInputStream(file)){
            Properties properties = new Properties();
            properties.load(in);     
            LOG.debug("Saved the token file " + file);
            return properties;
        } catch (FileNotFoundException ex) {
            throw new TokenNotFoundException(provider, "Token for " + provider + " not found", ex);
        } catch (IOException ex) {
            throw new TokenRepositoryException("Error loading token from file " + file, ex);
        } 
    }
    
    
    private void saveProperties(String provider, Properties properties) {
        File file = getFile(provider);
        try(OutputStream out = new FileOutputStream(file)){
            properties.store(out, provider + " token");
            LOG.debug("Loaded the token file " + file);
        } catch (IOException ex) {
            throw new TokenRepositoryException("Error saving token file " + file, ex);
        }
    }
    
    
    public String getLocation() {
        if(location == null) {
            location = (environment != null) ? 
                    environment.getProperty("token.files.location", DEFAULT_LOCATION) 
                    : DEFAULT_LOCATION;
        }
        return location;
    }
    
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    
    public String getFileSuffix() {
        if(fileSuffix == null) {
            fileSuffix = (environment != null) ? 
                    environment.getProperty("token.files.suffix", DEFAULT_FILES_SUFFIX) 
                    : DEFAULT_FILES_SUFFIX;
        }
        return fileSuffix;
    }
    
    
    public void setFileSuffix(String suffix) {
        this.fileSuffix = suffix;
    }
    
    
    private File getFile(String provider) {
        String loc = getLocation();
        if(loc.isBlank()) return new File(provider + getFileSuffix());
        
        File dir = new File(loc);
        if(!dir.exists() && !dir.mkdirs()) {
            throw new TokenRepositoryException("Unable to create a folder " + loc);
        }
        return new File(dir, provider + getFileSuffix());
    }
     
    
}
