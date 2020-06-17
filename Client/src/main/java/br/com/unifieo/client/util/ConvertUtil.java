/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.client.util;

import br.com.unifieo.client.model.UserModel;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.UserProtocol;

/**
 *
 * @author Vinicius
 */
public class ConvertUtil {
    
    public static UserProtocol convertUserToProtocol(UserModel user) {
        if (user == null) {
            return null;
        }

        return new UserProtocol(
                user.getLogin(),
                IdentificationHost.CLIENT
        );
    }

    public static UserModel convertProtocolToUser(UserProtocol protocol) {
        if (protocol == null) {
            return null;
        }

        return new UserModel(
                protocol.getLogin(),
                null
        );
    }
    
}
