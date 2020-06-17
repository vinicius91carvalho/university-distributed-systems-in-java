/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.protocol;

import java.io.Serializable;
import java.net.URI;

/**
 *
 * @author Vinicius
 */
public class Arquivo implements Serializable {
    
    private URI uri;
    private String name;
    private Boolean directory;  
    private Boolean sendFile;
    private Boolean refresh;
    
    public Arquivo() {
        this.directory = false;
        this.sendFile = false;
        this.refresh = false;
        name = "";
    }

    public Boolean isRefresh() {
        return refresh;
    }
    
    public Boolean isNotTopAndRoot() {
        return !(isTopDirectory() || isRootDirectory());
    }

    public void setRefresh(Boolean refresh) {
        this.refresh = refresh;
    }

    public Boolean isTopDirectory() {
        if (name == null)
            return false;
        return name.equals("..");
    }

    public Boolean isRootDirectory() {
        if (name == null)
            return false;
        return name.equals(".");
    }

    public Boolean isSendFile() {
        return sendFile;
    }

    public void setSendFile(Boolean sendFile) {
        this.sendFile = sendFile;
    }
    
    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isDirectory() {
        return directory;
    }

    public void setDirectory(Boolean directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (isDirectory()) {
            builder.append("Diretório: ");
        } else {
            builder.append("Arquivo: ");
        }
        builder.append(getName());
        return builder.toString();
    }
    
    
    
}
