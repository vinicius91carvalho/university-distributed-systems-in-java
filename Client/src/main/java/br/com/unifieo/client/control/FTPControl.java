/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.client.control;

import br.com.unifieo.common.configuration.FTPServiceConfiguration;
import br.com.unifieo.common.protocol.Arquivo;
import br.com.unifieo.common.protocol.FileProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.ServiceProtocol;
import br.com.unifieo.common.protocol.UserProtocol;
import br.com.unifieo.common.util.ConnectionUtil;
import br.com.unifieo.common.util.LogUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
public class FTPControl {
    
    private static ServiceProtocol ftpService;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private static UserProtocol user;
    
    public FileProtocol startConnection() {
        LogUtils.info("msg.client_connect");                
        
        try {
            if (ftpService == null) { // Caso o serviço não esteja cadastrado no SN, tenta obter localmente. (TESTES)
                ftpService = new ServiceProtocol("127.0.0.1", "FTP", FTPServiceConfiguration.PORT_PRIMARY, IdentificationHost.SERVICE);
            }
            
            Socket c_socket = null;
            try {
                 c_socket = new Socket(ftpService.getIp(), ftpService.getPort());
            } catch (Exception e) {
                ftpService = ConnectionUtil.captureService("FTP");
                if (ftpService == null) {
                    return null;
                }
                c_socket = new Socket(ftpService.getIp(), ftpService.getPort());
            }            
            
            output = new ObjectOutputStream(c_socket.getOutputStream());            
            
            output.writeObject(user);
            
            input = new ObjectInputStream(c_socket.getInputStream());
            
            FileProtocol protocol = (FileProtocol) input.readObject();
            
            return protocol;

        } catch (Exception e) {
            Logger.getLogger(ChatControl.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    public static void setUserProtocol(UserProtocol convertUserToProtocol) {
        FTPControl.user = convertUserToProtocol;
    }
    
    public FileProtocol sendAndReceivePaths(Arquivo path) {
        FileProtocol protocol = new FileProtocol(IdentificationHost.CLIENT);
        protocol.setPath(path);
        try {
            output.writeObject(protocol);
        } catch (IOException ex) {
            Logger.getLogger(FTPControl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        try {
            return (FileProtocol) input.readObject();
        } catch (IOException ex) {
            Logger.getLogger(FTPControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FTPControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public FileProtocol sendAndReceivePaths(FileProtocol protocolSend) {
        try {
            output.writeObject(protocolSend);
        } catch (IOException ex) {
            Logger.getLogger(FTPControl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        try {
            return (FileProtocol) input.readObject();
        } catch (IOException ex) {
            Logger.getLogger(FTPControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FTPControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static ServiceProtocol getFtpService() {
        return ftpService;
    }

    public static void setFtpService(ServiceProtocol ftpService) {
        FTPControl.ftpService = ftpService;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }
    
}
