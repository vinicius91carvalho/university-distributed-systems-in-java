/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.protocol;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Vinicius
 */
public class UserProtocol implements Serializable, IProtocol {
    
    private String login;
    private IdentificationHost host;
    
    public UserProtocol() {}

    @Override
    public String toString() {
        return login;
    }

    public UserProtocol(String login, IdentificationHost host) {
        this.login = login;
        this.host = host;
    }        

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public IdentificationHost getHost() {
        return host;
    }

    public void setHost(IdentificationHost host) {
        this.host = host;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.login);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserProtocol other = (UserProtocol) obj;
        if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        return true;
    }

    @Override
    public IdentificationHost getIdentification() {
        return host;
    }
    
}
