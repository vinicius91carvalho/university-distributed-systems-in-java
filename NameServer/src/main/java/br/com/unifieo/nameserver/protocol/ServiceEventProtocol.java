/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.nameserver.protocol;

import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.ServiceProtocol;

/**
 *
 * @author Vinicius
 */
public class ServiceEventProtocol extends ServiceProtocol {

    public ServiceEventProtocol(String ip, String name, Integer port, IdentificationHost host, Action action) {
        super(ip, name, port, host);
        this.action = action;
    }
    
    public ServiceEventProtocol(ServiceProtocol protocol, IdentificationHost host, Action action) {
        super(protocol.getIp(), protocol.getName(), protocol.getPort(), host);
        this.action = action;
    }
    
    public enum Action {
        ADD,
        REMOVE;
    }
    
    private Action action;

    public Action getAction() {
        return action;
    }
    
    
}
