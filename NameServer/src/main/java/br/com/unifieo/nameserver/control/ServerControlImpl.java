/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.nameserver.control;

import br.com.unifieo.nameserver.protocol.ServiceEventProtocol;
import br.com.unifieo.nameserver.util.ConvertUtil;
import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.util.MessagesProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
public class ServerControlImpl extends NameServerControl {
    
    /**
     * Tratar conexões de outro servidor de nomess
     * @param input
     * @param output
     * @param protocol 
     */
    @Override
    public void treatConnection(ObjectInputStream input, ObjectOutputStream output, IProtocol protocol) {
        if (protocol instanceof SimpleMessageProtocol) {
            try {
                output.writeObject(new SimpleMessageProtocol(MessagesProperties.getKey("msg.server_name_online"), IdentificationHost.SERVER_NAME));
            } catch (IOException ex) {
                Logger.getLogger(ServerControlImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        ServiceEventProtocol serviceProtocol = (ServiceEventProtocol) protocol;
        // Para adicionar um serviço
        if (serviceProtocol.getAction().equals(ServiceEventProtocol.Action.ADD)) {
            LogUtils.info("msg.server_name_received_event_add");
            addService(ConvertUtil.convertProtocolToService(serviceProtocol), false);
        } else { // Caso contrário é para removê-lo
            LogUtils.info("msg.server_name_received_event_remove");
            removeService(ConvertUtil.convertProtocolToService(serviceProtocol), false);
        }
        try { 
            output.writeObject(new SimpleMessageProtocol("OK", IdentificationHost.SERVER_NAME));
        } catch (IOException ex) {
            Logger.getLogger(ServerControlImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
