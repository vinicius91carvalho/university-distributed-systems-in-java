/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.nameserver.control.assync;

import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.configuration.NameServersConfiguration;
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import br.com.unifieo.nameserver.protocol.ServiceEventProtocol;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Vinicius
 */
public class NameServerSyncronization {
    
    public static void syncronizeServiceWithOtherServer(ServiceEventProtocol service, Boolean primary) {     
            String ip;
            Integer port;
            
            // Verifica se é o servidor secundário
            if (primary) {
                ip = NameServersConfiguration.IP_SECONDARY;
                port = NameServersConfiguration.PORT_SECONDARY;

                try (Socket socket = new Socket(ip, port)) {
                    LogUtils.info("msg.syncro_with_other_server_name");
                    new ObjectOutputStream(socket.getOutputStream()).writeObject(service);
                    SimpleMessageProtocol message = (SimpleMessageProtocol) new ObjectInputStream(socket.getInputStream()).readObject();
                    LogUtils.msg(message.getMessage());
                } catch (Exception ex) {
                    LogUtils.exception("msg.error.syncronization", ex);
                }
            }
    }    
    
}
