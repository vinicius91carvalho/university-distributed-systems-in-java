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
public class NameServersConfiguration {
    
    public static final String IP_PRIMARY;
    public static final String IP_SECONDARY;
    public static final Integer PORT_PRIMARY;
    public static final Integer PORT_SECONDARY;
    public static final Integer MAX_CONNECTIONS_PRIMARY;
    public static final Integer MAX_CONNECTIONS_SECONDARY;
    
    static {
        IP_PRIMARY = ConfigurationProperties.getKey("ip_server.name_primary");
        IP_SECONDARY = ConfigurationProperties.getKey("ip_server.name_secondary");
        PORT_PRIMARY = Integer.parseInt(ConfigurationProperties.getKey("port_server.name_primary"));
        PORT_SECONDARY = Integer.parseInt(ConfigurationProperties.getKey("port_server.name_secondary"));
        MAX_CONNECTIONS_PRIMARY = Integer.parseInt(ConfigurationProperties.getKey("max_connections_primary"));
        MAX_CONNECTIONS_SECONDARY = Integer.parseInt(ConfigurationProperties.getKey("max_connections_secondary"));
    }
}
