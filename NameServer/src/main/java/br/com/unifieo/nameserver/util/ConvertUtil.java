/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.nameserver.util;

import br.com.unifieo.nameserver.model.ServiceModel;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.ServiceProtocol;

/**
 *
 * @author Vinicius
 */
public class ConvertUtil {

    public static ServiceProtocol convertServiceToProtocol(ServiceModel service) {
        if (service == null) {
            return null;
        }

        return new ServiceProtocol(
                service.getIp(),
                service.getName(),
                service.getPort(),
                IdentificationHost.CLIENT
        );
    }

    public static ServiceModel convertProtocolToService(ServiceProtocol protocol) {
        if (protocol == null) {
            return null;
        }

        return new ServiceModel(
                protocol.getIp(),
                protocol.getName(),
                protocol.getPort()
        );
    }

}
