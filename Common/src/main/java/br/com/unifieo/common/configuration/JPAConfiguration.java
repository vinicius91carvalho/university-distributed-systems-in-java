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
public class JPAConfiguration {
    
    public static final String IP;
    public static final String USER;
    public static final String PASSWORD;
    
    static {
        IP = ConfigurationProperties.getKey("jpa_ip");        
        USER = ConfigurationProperties.getKey("jpa_user");
        PASSWORD = ConfigurationProperties.getKey("jpa_password");
    }        
}
