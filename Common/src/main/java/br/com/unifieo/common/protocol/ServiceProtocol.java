/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.common.protocol;


public class ServiceProtocol implements IProtocol {
    
    public ServiceProtocol(String ip, String name, Integer port, IdentificationHost host) {
        this.ip = ip;
        this.name = name;
        this.port = port;
        this.host = host;
    }
    
    private String ip;
    private String name;
    private Integer port;
    private IdentificationHost host;
    
    public String getIp() {
        return ip;
    }    
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public Integer getPort() {
        return port;
    }

    public IdentificationHost getHost() {
        return host;
    }

    public void setHost(IdentificationHost host) {
        this.host = host;
    }
    
    
    public void setPort(Integer port) {
        this.port = port;
    } 
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    } 

    @Override
    public IdentificationHost getIdentification() {
        return host;
    }

}
