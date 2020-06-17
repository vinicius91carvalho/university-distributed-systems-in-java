/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.protocol;


public class SimpleMessageProtocol implements IProtocol {

    private final IdentificationHost host;
    private final String message;
    
    public SimpleMessageProtocol(String message, IdentificationHost host) {
        this.message = message;
        this.host = host;
    }

    public String getMessage() {
        return message;
    }
    
    @Override
    public IdentificationHost getIdentification() {
        return host;
    }
    
    @Override
    public String toString() {
        return message;
    }
    
}
