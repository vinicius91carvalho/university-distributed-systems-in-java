/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.ftp.control;

import br.com.unifieo.common.configuration.FTPServiceConfiguration;
import br.com.unifieo.common.encryption.CryptoUtil;
import br.com.unifieo.common.protocol.FileProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.ServiceProtocol;
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import br.com.unifieo.common.util.ConnectionUtil;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.util.MessagesProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
public class SyncronizeServicesFTP {
    
    public SyncronizeServicesFTP() {
        try  {
            start();
        } catch (Exception e) {
            LogUtils.exception("msg.error.ftp_syncronize", e);
        }
    }
    
    public void start() throws Exception {
        LogUtils.info("msg.prepar_syncro");
        ServiceProtocol service = ConnectionUtil.captureService("FTP");
        if (service != null) {
            LogUtils.info("msg.service_ftp_found");
            Path path = Paths.get(FTPServiceConfiguration.DIR_SERVER);            
            
            // Obtém os arquivos do outro nó do servidor FTP
            Socket socket = new Socket(service.getIp(), service.getPort());
            new ObjectOutputStream(socket.getOutputStream()).writeObject(new SimpleMessageProtocol(MessagesProperties.getKey("msg.send_event_syncronize"), IdentificationHost.SERVICE));
            Object object = new ObjectInputStream(socket.getInputStream()).readObject();
            if (object != null) {
                List<FileProtocol> files = (List<FileProtocol>) object;
                
                // Deleta o conteúdo do diretório padrão do servidor FTP
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    for (Path innerPath : stream) {
                        deleteContentRecursive(innerPath);
                    }
                } catch (IOException | DirectoryIteratorException e) {}
                
                for (FileProtocol file : files) {
                    LogUtils.msg("Adicionando o arquivo: "+path.toFile().getAbsolutePath()+"\\"+file.getPath().getName());
                    // RETIRAR ESSA LINHA DE BAIXO (APENAS TESTES)
                    Files.write(Paths.get(path.toFile().getAbsolutePath()+"\\"+file.getPath().getName()+"_CRIPTOGRAFADO"),
                        file.getBytes());
                    Files.write(Paths.get(path.toFile().getAbsolutePath()+"\\"+file.getPath().getName()),
                        new CryptoUtil().decrypt(file.getBytes()));
                }
            }
        } else {
            LogUtils.info("msg.service_ftp_not_found");
        }
    }
    
    public static void deleteContentRecursive(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path innerPath : stream) {
                    deleteContentRecursive(innerPath);
                }
            } catch (IOException | DirectoryIteratorException e) {}            
        }
        try {
            LogUtils.msg(MessagesProperties.getKey("msg.delete")+": "+path);
            Files.delete(path);
        } catch (IOException ex) {
            Logger.getLogger(SyncronizeServicesFTP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
