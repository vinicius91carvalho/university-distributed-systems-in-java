/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.client.assync;

import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.protocol.ChatEventProtocol;
import br.com.unifieo.common.protocol.ChatMessageProtocol;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 * @author Vinicius
 */
public class ClientSend {
    
    private ObjectOutputStream output;
    
    public ClientSend(ObjectOutputStream output) {
        this.output = output;
    }
    
    public void sendMessage(ChatMessageProtocol message) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            LogUtils.exception("msg.error.send_message", ex);
        }
    }
    
    public void sendEvent(ChatEventProtocol event) {
        try {
            output.writeObject(event);
        } catch (IOException ex) {
            LogUtils.exception("msg.error.send_event", ex);
        }
    }
    
}
