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
public class FTPServiceConfiguration {
 
    public static final String DIR_SERVER;
    public static final String DIR_CLIENT;
    public static final Integer PORT_PRIMARY;
    public static final Integer PORT_SECONDARY;
    public static final Integer MAX_CONNECTIONS_PRIMARY;   
    public static final Integer MAX_CONNECTIONS_SECONDARY;
    
    
    static {
        DIR_SERVER = (ConfigurationProperties.getKey("directory_ftp_server"));        
        DIR_CLIENT = (ConfigurationProperties.getKey("directory_ftp_client"));
        PORT_PRIMARY = Integer.parseInt(ConfigurationProperties.getKey("port_ftp.service_primary"));
        PORT_SECONDARY = Integer.parseInt(ConfigurationProperties.getKey("port_ftp.service_secondary"));
        MAX_CONNECTIONS_PRIMARY = Integer.parseInt(ConfigurationProperties.getKey("max_connections_ftp.service_primary"));
        MAX_CONNECTIONS_SECONDARY = Integer.parseInt(ConfigurationProperties.getKey("max_connections_ftp.service_secondary"));
    }
    
}
