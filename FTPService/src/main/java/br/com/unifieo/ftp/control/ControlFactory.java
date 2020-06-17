/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.ftp.control;

import br.com.unifieo.common.protocol.IdentificationHost;

/**
 *
 * @author Vinicius
 */
public class ControlFactory {
    
    private ControlFactory() {}
    
    public static FTPServiceControl getController(IdentificationHost host) {
        
        if (host.equals(IdentificationHost.CLIENT)) {
            return new ClientControlImpl();
        } else if (host.equals(IdentificationHost.SERVER_NAME)) {
            return new ServerControlImpl();
        } else {
            return new ServiceControlImpl();
        }
        
    }    
    
    
    
}
