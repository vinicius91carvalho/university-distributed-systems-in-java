/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.chat.control;

import br.com.unifieo.chat.assync.Communicate;
import br.com.unifieo.chat.assync.Receive;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.util.MessagesProperties;
import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.UserProtocol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 *
 * @author Vinicius
 */
public class ClientControlImpl extends ChatServiceControl {
    
    /**
     * Trata as requisições dos Clientes
     * @param socket
     * @param protocol
     * @throws IOException
     * @throws Exception 
     */
    @Override
    public void treatConnection(ObjectOutputStream output, ObjectInputStream input, IProtocol protocol) throws IOException, Exception {
        // Identifica o usuário
        UserProtocol user = (UserProtocol) protocol;        
        LogUtils.msg(user.getLogin()+" "+MessagesProperties.getKey("msg.user_connected"));
        
        Communicate com = includeCommunicateUser(user, output, input);
        
        // Envia a lista de usuários existentes
        List<UserProtocol> users = getUsersConnected();
        users.remove(user);
        com.getOutput().writeObject(users);
                
        // Adiciona o usuário e envia um evento para os outros clientes do bate papo
        addUser(user);             
                
        // Atribui o recebimento apartir do cliente (Input (Receive))
        Receive receive = new Receive(user, com.getInput());
        receive.start();
        receive.join();        
        
    }
}
