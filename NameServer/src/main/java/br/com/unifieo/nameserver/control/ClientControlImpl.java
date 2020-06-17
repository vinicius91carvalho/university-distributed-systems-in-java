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
public class ClientControlImpl extends NameServerControl {
    
    /**
     * Trata as requisições dos Clientes
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
        String nameService;
        // Verifica se o cliente não sabe qual serviço vai utilizar
        if (serviceProtocol.getName() == null) {
            // Envia a lista de serviços cadastrados
            output.writeObject(new SimpleMessageProtocol(getServicesToClient(), IdentificationHost.SERVER_NAME));
            // Recebe o nome do serviço escolhido
            SimpleMessageProtocol servSearch = (SimpleMessageProtocol) input.readObject();
            nameService = servSearch.getMessage();
        } else {
            nameService = serviceProtocol.getName();
        }
        // Procura o serviço
        serviceProtocol = ConvertUtil.convertServiceToProtocol(getServiceByName(nameService));
        
        // Caso não o encontre devolve uma exceção
        if (serviceProtocol == null) {
            throw new Exception(MessagesProperties.getKey("msg.not_service"));
        }
        // Retorna o Serviço ao cliente
        output.writeObject(serviceProtocol);
    }
}
