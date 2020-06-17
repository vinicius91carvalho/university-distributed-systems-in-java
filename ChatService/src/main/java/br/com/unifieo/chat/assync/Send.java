/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.chat.assync;

import static br.com.unifieo.chat.control.ChatServiceControl.getUsers;
import static br.com.unifieo.chat.control.ChatServiceControl.removeUser;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.protocol.ChatEventProtocol;
import br.com.unifieo.common.protocol.ChatMessageProtocol;
import br.com.unifieo.common.protocol.UserProtocol;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aluno
 */
public class Send {
    
    public static void sendMessageToAllHosts(ChatMessageProtocol chatMessage) {
        
        List<UserProtocol> listRemove = new ArrayList<>();
        
        for (UserProtocol user : getUsers().keySet()) {            
            try {
                ObjectOutputStream output = getUsers().get(user).getOutput();
                output.writeObject(chatMessage);
                output.flush();
            } catch (IOException e) {
                LogUtils.exception("msg.error.send_message", e);
                listRemove.add(user);
            }
        }
        for (UserProtocol user : listRemove) {
            removeUser(user);
        }

    }
    
    public static void sendEvent(ChatEventProtocol event) {
        for (UserProtocol user : getUsers().keySet()) {
            try {
                ObjectOutputStream output = getUsers().get(user).getOutput();
                output.writeObject(event);
                output.flush();
            } catch (IOException e) {
                LogUtils.exception("msg.error.send_event", e);
            }
        }
    }

    public static void sendMessageToHost(ChatMessageProtocol chatMessage) {
        try {
            
            // Envia uma mensagem para o Receiver
            ObjectOutputStream output = getUsers().get(chatMessage.getUserReceiver()).getOutput();
            output.writeObject(chatMessage);
            output.flush();
            
            if (!chatMessage.getUserSender().equals(chatMessage.getUserReceiver())) {                            
                
                // Envia a mesma mensagem para o Sender
                ObjectOutputStream outputSender = getUsers().get(chatMessage.getUserSender()).getOutput();
                outputSender.writeObject(chatMessage);
                outputSender.flush();                
                return;
            }
            
        } catch (IOException e) {
            LogUtils.exception("msg.error.send_message", e);
            removeUser(chatMessage.getUserReceiver());
        }
    }
    
    
    
}
