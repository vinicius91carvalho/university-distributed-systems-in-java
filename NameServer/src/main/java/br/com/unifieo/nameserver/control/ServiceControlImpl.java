/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.nameserver.control;

import br.com.unifieo.common.util.MessagesProperties;
import br.com.unifieo.nameserver.util.ConvertUtil;
import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.ServiceProtocol;
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Vinicius
 */
public class ServiceControlImpl extends NameServerControl {
    
    /**
     * Trata as requisições dos serviços
     * @param input
     * @param output
     * @param protocol
     * @throws IOException
     * @throws Exception 
     */
    @Override
    public void treatConnection(ObjectInputStream input, 
            ObjectOutputStream output, IProtocol protocol) throws IOException, Exception {

        ServiceProtocol serviceProtocol = (ServiceProtocol) protocol;        

        addService(ConvertUtil.convertProtocolToService(serviceProtocol), true);
        String message = MessagesProperties.getKey("msg.service_add_sucess");
        output.writeObject(new SimpleMessageProtocol(message, IdentificationHost.SERVER_NAME));
    }
    
}
