/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.chat.control;

import br.com.unifieo.chat.assync.Communicate;
import br.com.unifieo.chat.assync.Send;
import br.com.unifieo.common.util.Action;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.protocol.ChatEventProtocol;
import br.com.unifieo.common.protocol.ChatMessageProtocol;
import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.UserProtocol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Vinicius
 */
public abstract class ChatServiceControl {

    // Mapa com informações de usuários dentro do servidor de chat
    private static Map<UserProtocol, Communicate> users;
    
    static {
        // Mapa que permite a concorrência entre Threads
        users = new ConcurrentHashMap<>();
    }      
    
    public abstract void treatConnection(ObjectOutputStream output, ObjectInputStream input, IProtocol protocol) throws IOException, Exception;
    
    public static Map<UserProtocol, Communicate> getUsers() {
        return users;
    }
    
    public static Communicate includeCommunicateUser(UserProtocol user, ObjectOutputStream output, ObjectInputStream input) {
        Communicate com = new Communicate(output, input);
        getUsers().put(user, com);
        return com;
    }
    
    public static void addUser(UserProtocol user) {
        LogUtils.info("msg.client_add");
        ChatEventProtocol chatEvent = new ChatEventProtocol(
                    IdentificationHost.SERVICE, 
                    Action.ADD, 
                    user);
        Send.sendEvent(chatEvent);
    }
    
    public static void removeUser(UserProtocol user) {
        if (getUsers().containsKey(user)) {
            LogUtils.info("msg.client_remove");
            getUsers().remove(user);
            ChatEventProtocol chatEvent = new ChatEventProtocol(
                    IdentificationHost.SERVICE, 
                    Action.REMOVE, 
                    user);
            Send.sendEvent(chatEvent);
        }
    }
    
    public List<UserProtocol> getUsersConnected() {
        return new ArrayList(getUsers().keySet());
    }
    
    public static void controlSendToHosts(ChatMessageProtocol chatMessage) throws IOException {
        // Verifica se existe um usuário logado
        if (getUsers().isEmpty()) {
            LogUtils.info("msg.client_not_connected");
            return;
        }   
        // Caso a mensagem sejam para todos dentro da sala        
        if (chatMessage.isAllUsers()) {            
            Send.sendMessageToAllHosts(chatMessage);            
        } else { // Se a mensagem é somente para um destinatário            
            Send.sendMessageToHost(chatMessage);            
        }        
    }       
}
