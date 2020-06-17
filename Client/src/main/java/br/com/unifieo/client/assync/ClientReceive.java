/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.client.assync;

import br.com.unifieo.client.control.ChatControl;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.protocol.ChatEventProtocol;
import br.com.unifieo.common.protocol.ChatMessageProtocol;
import br.com.unifieo.common.protocol.IProtocol;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *
 * @author Aluno
 */
public class ClientReceive extends Thread {
    
    private ObjectInputStream input;
    
    public ClientReceive(ObjectInputStream input) throws IOException {
        this.input = input;
    }
    
    @Override
     public void run() {
         
        while (true) {
            try {
                Object object = input.readObject();
                IProtocol protocol = (IProtocol) object;
                if (protocol instanceof ChatEventProtocol) {
                    ChatControl.treatEvent((ChatEventProtocol) protocol);
                } else if (protocol instanceof ChatMessageProtocol) {
                    ChatControl.treatMessage((ChatMessageProtocol) protocol);
                }
                
            } catch (Exception e) {
                LogUtils.exception("msg.error.receive_message", e);
            }
        }

    }
}
