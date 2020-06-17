package br.com.unifieo.client.control;

import br.com.unifieo.client.assync.ClientReceive;
import br.com.unifieo.client.assync.ClientSend;
import br.com.unifieo.client.view.CockpitController;
import br.com.unifieo.common.configuration.ChatServiceConfiguration;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.protocol.ChatEventProtocol;
import br.com.unifieo.common.protocol.ChatMessageProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.ServiceProtocol;
import br.com.unifieo.common.protocol.UserProtocol;
import br.com.unifieo.common.util.ConnectionUtil;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;

public class ChatControl {
    
    private static ServiceProtocol chatService;
    private static UserProtocol userProtocol;
    private LinkedList<UserProtocol> listUsers;
    private ClientSend clientSend;
    
    private static EventHandler<Event> handlerMessage;
    private static EventHandler<Event> handlerEvent;
    
    public ChatControl(EventHandler<Event> handlerMessage, EventHandler<Event> handlerEvent) {
        ChatControl.handlerMessage = handlerMessage;
        ChatControl.handlerEvent = handlerEvent;
    }

    public ServiceProtocol startConnection() {
        LogUtils.info("msg.client_connect");                
        
        try {
            if (chatService == null) { // Caso o serviço não esteja cadastrado no SN, tenta obter localmente. (TESTES)
                chatService = new ServiceProtocol("127.0.0.1", "CHAT", ChatServiceConfiguration.PORT, IdentificationHost.SERVICE);
            }
            Socket c_socket = null;
            try {
                 c_socket = new Socket(chatService.getIp(), chatService.getPort());
            } catch (Exception e) {
                chatService = ConnectionUtil.captureService("CHAT");
                if (chatService == null) {
                    return null;
                }
                c_socket = new Socket(chatService.getIp(), chatService.getPort());
            }
            
            ObjectOutputStream output = new ObjectOutputStream(c_socket.getOutputStream());
            
            userProtocol.setHost(IdentificationHost.CLIENT);
            // Envio o usuário para o servidor
            output.writeObject(userProtocol);
            LogUtils.info("msg.send_user");
            
            ObjectInputStream input = new ObjectInputStream(c_socket.getInputStream());
            
            // Recebo a lista de usuários cadastrados
            listUsers = new LinkedList<>((List) input.readObject());                      
            listUsers.add(0, new UserProtocol("Todos", IdentificationHost.CLIENT));
            LogUtils.info("msg.list_user_received");

            ClientReceive receive = new ClientReceive(input);
            receive.start();      
            
            clientSend = new ClientSend(output);
        } catch (Exception e) {
            Logger.getLogger(ChatControl.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return chatService;
    }
    
    public static void treatEvent(ChatEventProtocol event) {
        LogUtils.info("msg.treat_event");
        handlerEvent.handle(new Event(event, null, null));
    }
    
    public static void treatMessage(ChatMessageProtocol message) {
        LogUtils.info("msg.treat_message");
        handlerMessage.handle(new Event(message, null, null));
    }
    
    public void sendMessage(String message, UserProtocol userReceiver, byte[] file) {
        ChatMessageProtocol messageProtocol = 
                new ChatMessageProtocol(IdentificationHost.CLIENT, null, userProtocol, userReceiver, file);        
        getClientSend().sendMessage(messageProtocol);
    }
        

    public List<UserProtocol> getListUsers() {
        return listUsers;
    }

    public void setListUsers(List<UserProtocol> listUsers) {
        this.listUsers = new LinkedList<>(listUsers);
    }
    
    public static ServiceProtocol getChatService() {
        return chatService;
    }

    public static void setChatService(ServiceProtocol chatService) {
        ChatControl.chatService = chatService;
    }

    public static UserProtocol getUserProtocol() {
        return userProtocol;
    }

    public static void setUserProtocol(UserProtocol userProtocol) {
        ChatControl.userProtocol = userProtocol;
    }

    public ClientSend getClientSend() {
        return clientSend;
    }

    public void setClientSend(ClientSend clientSend) {
        this.clientSend = clientSend;
    }
    
}
