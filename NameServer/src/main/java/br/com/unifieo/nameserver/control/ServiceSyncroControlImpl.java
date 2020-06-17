/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.nameserver.control;

import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.ServiceProtocol;
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.util.MessagesProperties;
import br.com.unifieo.nameserver.model.ServiceModel;
import br.com.unifieo.nameserver.util.ConvertUtil;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Vinicius
 */
public class ServiceSyncroControlImpl extends NameServerControl {

    @Override
    public void treatConnection(ObjectInputStream input, ObjectOutputStream output, IProtocol protocol) throws IOException, Exception {
            
        ServiceProtocol serviceProtocol = (ServiceProtocol) protocol;
        
        if (serviceProtocol.getIp() != null && serviceProtocol.getPort() != null) {
            LogUtils.info("msg.service_get_other_service");
            ServiceModel serviceModel = getOtherServiceToService(serviceProtocol);
            if (serviceModel != null) {
                LogUtils.info("msg.service_found");
                output.writeObject(ConvertUtil.convertServiceToProtocol(serviceModel));
            } else {
                LogUtils.info("msg.service_not_found");
                output.writeObject(new SimpleMessageProtocol(MessagesProperties.getKey("msg.service_not_found"), IdentificationHost.SERVER_NAME));
            }          
        }
    }
    
    
    
}
