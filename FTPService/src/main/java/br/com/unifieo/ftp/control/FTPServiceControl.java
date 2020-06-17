/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.ftp.control;

import br.com.unifieo.common.configuration.FTPServiceConfiguration;
import br.com.unifieo.common.encryption.CryptoUtil;
import br.com.unifieo.common.protocol.Arquivo;
import br.com.unifieo.common.protocol.FileProtocol;
import br.com.unifieo.common.protocol.IProtocol;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
public abstract class FTPServiceControl {          
    
    public static ServiceProtocol service;
    public static ServiceProtocol otherService;

    private static void syncronizeFileWithOtherFTP(FileProtocol fileProtocol) {
        fileProtocol.setHost(IdentificationHost.SERVICE);
        LogUtils.info("msg.ftp_syncro");        
        try {            
            if (otherService == null) {
                otherService = ConnectionUtil.obtainOtherServiceWithName(service);                
                if (otherService == null) {
                    LogUtils.info("msg.ftp_syncro_error");
                    return;
                }
            }
            Socket socket = null;
            try {
                socket = new Socket(otherService.getIp(), otherService.getPort());
            } catch (Exception e) {
                LogUtils.info("msg.treat_ftp");
                otherService = ConnectionUtil.obtainOtherServiceWithName(service);
                if (otherService == null) {
                    LogUtils.info("msg.ftp_syncro_error");
                    return;
                }
                socket = new Socket(otherService.getIp(), otherService.getPort());                
            }
            
            new ObjectOutputStream(socket.getOutputStream()).writeObject(fileProtocol);
            LogUtils.msg(((SimpleMessageProtocol)new ObjectInputStream(socket.getInputStream()).readObject()).getMessage());
        } catch (Exception ex) {
            Logger.getLogger(FTPServiceControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    
    public abstract void treatConnection(ObjectOutputStream output, ObjectInputStream input, IProtocol protocol) throws IOException, Exception;       
    
    public static FileProtocol controlSendFiles(FileProtocol fileProtocol) {
        LogUtils.info("msg.ftp_start_process_request");
        FileProtocol protocol = new FileProtocol(IdentificationHost.SERVICE);
        // Se ele envia o Path vázio envia os Paths da pasta ROOT
        if (fileProtocol.getPath() == null || fileProtocol.getPath().isRootDirectory()) {            
            LogUtils.info("msg.ftp_send_path_null");
            Path path = Paths.get(FTPServiceConfiguration.DIR_SERVER);
            protocol.setPaths(readDirectory(path));
        } else if (fileProtocol.getPath().isSendFile()) { // Enviou um arquivo
            LogUtils.info("msg.ftp_send_a_file");
            try {
                Path path;
                if (fileProtocol.getPath().getUri() == null) {
                    LogUtils.info("msg.ftp_no_uri");
                    path = Paths.get(FTPServiceConfiguration.DIR_SERVER);
                } else {
                    path = Paths.get(fileProtocol.getPath().getUri());
                    path = path.getParent();
                }                
                
                LogUtils.msg(MessagesProperties.getKey("msg.ftp_write_file")+ fileProtocol.getPath().getName() +" em: "+path.toFile().getAbsolutePath());
                
                fileProtocol.getPath().setUri(path.toUri());
                syncronizeFileWithOtherFTP(fileProtocol);
                
                Files.write(Paths.get(path.toFile().getAbsolutePath()+"\\"+fileProtocol.getPath().getName()+"_CRIPTOGRAFADO"),
                    fileProtocol.getBytes());
                Files.write(Paths.get(path.toFile().getAbsolutePath()+"\\"+fileProtocol.getPath().getName()),
                    new CryptoUtil().decrypt(fileProtocol.getBytes()));
                protocol.setPaths(readDirectory(path));
            } catch (IOException ex) {
                Logger.getLogger(FTPServiceControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (fileProtocol.getPath().isTopDirectory()) {
            LogUtils.msg(MessagesProperties.getKey("msg.ftp_directory_top"));
            Path pathTop = Paths.get(fileProtocol.getPath().getUri());            
            pathTop = pathTop.getParent().getParent();
            protocol.setPaths(readDirectory(pathTop));
        } else if (fileProtocol.getPath().isDirectory()) { // Se o Path enviado for um diretório
            LogUtils.msg(MessagesProperties.getKey("msg.ftp_enter_directory")+fileProtocol.getPath().getUri().getPath());
            protocol.setPaths(readDirectory(Paths.get(fileProtocol.getPath().getUri())));
        } else if (fileProtocol.getPath().isRefresh()) {
            Path path;
            if (fileProtocol.getPath().getUri() != null) {
                path = Paths.get(fileProtocol.getPath().getUri()).getParent();
            } else {
                path = Paths.get(FTPServiceConfiguration.DIR_SERVER);
            }
            LogUtils.msg(MessagesProperties.getKey("msg.ftp_refresh_directory")+path);
            protocol.setPaths(readDirectory(path));
        } else {           
            
            try {
                // É um arquivo
                LogUtils.msg(MessagesProperties.getKey("msg.whould_like_file")+fileProtocol.getPath().getUri().getPath());
                byte[] retorno = Files.readAllBytes(Paths.get(fileProtocol.getPath().getUri())); //leio o arquivo e armazeno
                CryptoUtil crypto = new CryptoUtil();                
                protocol.setBytes(crypto.encrypt(retorno));
            } catch (IOException ex) {
                Logger.getLogger(FTPServiceControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        LogUtils.info("msg.ftp_term_process");
        return protocol;
    }
    
    private static List<Arquivo> readDirectory(Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            List<Arquivo> paths = new ArrayList<>();
            for (Path path : stream) {
                    if (path.getFileName() != null && path.getFileName().toString().endsWith("_CRIPTOGRAFADO")) {
                        continue;
                    }
                    Arquivo arq = new Arquivo();
                    arq.setDirectory(Files.isDirectory(path));
                    arq.setUri(path.toUri());
                    arq.setName(path.getFileName().toString());
                    paths.add(arq);
            }
            return paths;
	} catch (IOException | DirectoryIteratorException e) {}
        return null;
    }
    
}
