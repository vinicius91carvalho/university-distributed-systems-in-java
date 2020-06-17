/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.configuration;

/**
 *
 * @author Vinicius
 */
// Design Pattern Factory Static Method
public class ChatServiceConfiguration {
    
    public static final Integer PORT;
    public static final Integer MAX_CONNECTIONS;
    public static final String DIR_DOWNLOADS;
    
    static {
        PORT = Integer.parseInt(ConfigurationProperties.getKey("port_chat.service"));        
        MAX_CONNECTIONS = Integer.parseInt(ConfigurationProperties.getKey("max_connections_chat.service"));
        DIR_DOWNLOADS = ConfigurationProperties.getKey("dir_downloads");
    }
}
