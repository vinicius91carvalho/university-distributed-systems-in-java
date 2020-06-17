/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.protocol;

import br.com.unifieo.common.util.Action;

/**
 *
 * @author Vinicius
 */
public class ChatEventProtocol implements IProtocol {

    private IdentificationHost host;    
    private Action action;
    private UserProtocol user;

    public ChatEventProtocol(IdentificationHost host, Action action, UserProtocol user) {
        this.host = host;
        this.action = action;
        this.user = user;
    }   

    public IdentificationHost getHost() {
        return host;
    }

    public UserProtocol getUser() {
        return user;
    }

    public void setUser(UserProtocol user) {
        this.user = user;
    }

    public void setHost(IdentificationHost host) {
        this.host = host;
    }

    public Action getAction() {
        return action;
    }    
    
    @Override
    public IdentificationHost getIdentification() {
        return host;
    }
    
}
