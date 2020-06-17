/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.ftp.control;

import br.com.unifieo.common.protocol.FileProtocol;
import br.com.unifieo.ftp.assync.FTPCommunicate;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.UserProtocol;
import br.com.unifieo.common.util.MessagesProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Vinicius
 */
public class ClientControlImpl extends FTPServiceControl {
    
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
        
        LogUtils.info("msg.send_files");
        output.writeObject(FTPServiceControl.controlSendFiles(new FileProtocol(IdentificationHost.SERVICE)));
        LogUtils.info("msg.send_list_files");
        // Atribui o recebimento apartir do cliente (Input (FTPCommunicate))
        FTPCommunicate receive = new FTPCommunicate(input, output, (UserProtocol) protocol);       
        receive.start();        
        receive.join();
        LogUtils.msg(MessagesProperties.getKey("msg.thread_user")+((UserProtocol) protocol).getLogin()+MessagesProperties.getKey("msg.closed_connection_it"));
    }
    
}
