/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.chat.assync;

import br.com.unifieo.chat.control.ChatServiceControl;
import br.com.unifieo.common.util.Action;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.util.MessagesProperties;
import br.com.unifieo.common.protocol.ChatEventProtocol;
import br.com.unifieo.common.protocol.ChatMessageProtocol;
import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.UserProtocol;
import java.io.ObjectInputStream;

/**
 *
 * @author Aluno
 */
public class Receive extends Thread {
    
    private ObjectInputStream input;
    private UserProtocol user;
    
    public Receive(UserProtocol user, ObjectInputStream input) {
        this.input = input;
        this.user = user;
    }
    
    @Override
     public void run() {
         
        try {
            while (true) {
                    IProtocol protocol = (IProtocol) input.readObject();
                    if (protocol instanceof ChatMessageProtocol) {
                        ChatMessageProtocol chatMessage = (ChatMessageProtocol) protocol;
                        StringBuilder builder = new StringBuilder();
                        builder.append(MessagesProperties.getKey("msg.message_received_of")).append(" ")
                                .append(chatMessage.getUserSender()).append(" ")
                                .append(MessagesProperties.getKey("msg.message_send_for")).append(" ")
                                .append((
                        chatMessage.isAllUsers() ? MessagesProperties.getKey("msg.label.all")  : chatMessage.getUserReceiver()));
                        LogUtils.msg(builder.toString());
                        ChatServiceControl.controlSendToHosts(chatMessage);                        
                    } else {
                        ChatEventProtocol event = (ChatEventProtocol) protocol;
                        if (event.getAction().equals(Action.REMOVE)) {
                            LogUtils.msg(MessagesProperties.getKey("msg.received_event_remove")+" "+event.getUser().getLogin());
                            ChatServiceControl.removeUser(event.getUser());
                        }
                    }
            }
        } catch (Exception e) {
            ChatServiceControl.removeUser(user);
        }

    }
}
