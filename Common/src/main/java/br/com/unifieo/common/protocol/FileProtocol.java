/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.protocol;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Vinicius
 */
public class FileProtocol implements IProtocol, Serializable {

    private static final Long serialVersionUID = 1L;
    
    private IdentificationHost host;
    private Arquivo path;
    private List<Arquivo> paths;
    private byte[] bytes;

    public FileProtocol(IdentificationHost host) {
        this.host = host;
    }   

    public IdentificationHost getHost() {
        return host;
    }

    public void setHost(IdentificationHost host) {
        this.host = host;
    }

    public Arquivo getPath() {
        return path;
    }

    public void setPath(Arquivo path) {
        this.path = path;
    }

    public List<Arquivo> getPaths() {
        return paths;
    }

    public void setPaths(List<Arquivo> paths) {
        this.paths = paths;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public IdentificationHost getIdentification() {
        return host;
    }    
    
}
