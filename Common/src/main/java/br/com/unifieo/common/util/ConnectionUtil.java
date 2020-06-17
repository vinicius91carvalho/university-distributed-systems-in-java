package br.com.unifieo.common.util;

import br.com.unifieo.common.configuration.NameServersConfiguration;
import br.com.unifieo.common.protocol.ServiceProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionUtil {

    /**
     * Método responsável por capturar um serviço informado pelo nome
     * @param nameOfService
     * @return 
     */
    public static ServiceProtocol captureService(String nameService) {
        // Conectar ao servidor de nomes        
        Socket socket = connectServerName();
        
        if (socket == null)
            return null;
        
        ObjectOutputStream output;
        ObjectInputStream input;
        Object object;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(new ServiceProtocol(null, nameService, null, IdentificationHost.CLIENT));            
            input = new ObjectInputStream(socket.getInputStream());
            object = input.readObject();
        } catch (Exception ex) {
            Logger.getLogger(ConnectionUtil.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        if (object == null)
            return null;
        else if (object instanceof SimpleMessageProtocol) {
            LogUtils.msg(((SimpleMessageProtocol) object).getMessage());
            return null;
        }                                  
        return (ServiceProtocol) object;          
        
    }
    
    public static Boolean addService(ServiceProtocol service) {
        
        LogUtils.info("msg.service_add_server_name");
        
        Socket socket = connectServerName();
        if (socket == null) {
            return false;
        }
            
        ObjectOutputStream output;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            output.writeObject(service);

            LogUtils.info("msg.server_response");
            SimpleMessageProtocol message = (SimpleMessageProtocol) input.readObject();
            LogUtils.msg(message.getMessage());
            
            output.close();
            input.close();
            
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ConnectionUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }       
    }
    
    public static ServiceProtocol obtainOtherServiceWithName(ServiceProtocol service) {
        service.setHost(IdentificationHost.SERVICO_SYNCRO);
        Socket socket = ConnectionUtil.connectServerName();
        
        if (socket == null)
            return null;
        
        ObjectOutputStream output;
        ObjectInputStream input;
        Object object;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(service);            
            input = new ObjectInputStream(socket.getInputStream());
            object = input.readObject();
        } catch (Exception ex) {
            Logger.getLogger(ConnectionUtil.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        if (object == null)
            return null;
        else if (object instanceof SimpleMessageProtocol) {
            LogUtils.msg(((SimpleMessageProtocol) object).getMessage());
            return null;
        }                                  
        return (ServiceProtocol) object;
        
    }
    
    public static Socket connectServerName() {
        // Conectar ao servidor de nomes
        LogUtils.info("msg.connection_in_server_name");
        try {
            Socket socket = new Socket(NameServersConfiguration.IP_PRIMARY, 
                                                NameServersConfiguration.PORT_PRIMARY);
            LogUtils.info("msg.connection_success");
            return socket;
        } catch (Exception e) {
            LogUtils.info("msg.error.connection_server_primary_fail");
            try {
                Socket c_socket = new Socket(NameServersConfiguration.IP_SECONDARY, 
                                                NameServersConfiguration.PORT_SECONDARY);
                LogUtils.info("msg.connection_success");
                return c_socket;
            } catch (Exception ex) {
                LogUtils.exception("msg.error.connection_servers", ex);
                return null;
            }
        }
        
    }
}
