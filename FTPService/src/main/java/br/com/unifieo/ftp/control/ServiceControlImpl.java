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
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.util.MessagesProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
public class ServiceControlImpl extends FTPServiceControl {

    @Override
    public void treatConnection(ObjectOutputStream output, ObjectInputStream input, IProtocol protocol) throws IOException, Exception {
        if (protocol instanceof SimpleMessageProtocol) {
            SimpleMessageProtocol messageProtocol = (SimpleMessageProtocol) protocol;
            LogUtils.msg(messageProtocol.getMessage());
            output.writeObject(readDirectory(Paths.get(FTPServiceConfiguration.DIR_SERVER)));
            LogUtils.info("msg.ftp_send_files_to");
        } else if (protocol instanceof FileProtocol) {
            FileProtocol file = (FileProtocol) protocol;
            LogUtils.info("msg.ftp_received_file");
            try {
                Path path;
                path = Paths.get(file.getPath().getUri());
                
                LogUtils.msg(MessagesProperties.getKey("msg.ftp_write_file")+ file.getPath().getName() +" em: "+path.toFile().getAbsolutePath());
                Files.write(Paths.get(path.toFile().getAbsolutePath()+"\\"+file.getPath().getName()+"_CRIPTOGRAFADO"),
                    file.getBytes());
                Files.write(Paths.get(path.toFile().getAbsolutePath()+"\\"+file.getPath().getName()),
                    new CryptoUtil().decrypt(file.getBytes()));
                
                output.writeObject(new SimpleMessageProtocol("msg.file_syncro_sucess", IdentificationHost.SERVICE));
            } catch (IOException ex) {
                Logger.getLogger(FTPServiceControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private static List<FileProtocol> readDirectory(Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            List<FileProtocol> files = new ArrayList<>();
            CryptoUtil crypto = new CryptoUtil();
            for (Path path : stream) {
                    if (path.getFileName() != null && path.getFileName().toString().endsWith("_CRIPTOGRAFADO")) {
                        continue;
                    }
                    Arquivo arq = new Arquivo();
                    arq.setDirectory(Files.isDirectory(path));
                    arq.setUri(path.toUri());
                    arq.setName(path.getFileName().toString());
                    FileProtocol file = new FileProtocol(IdentificationHost.SERVER_NAME);
                    file.setBytes(crypto.encrypt(Files.readAllBytes(path)));
                    file.setPath(arq);
                    files.add(file);
            }
            return files;
	} catch (IOException | DirectoryIteratorException e) {}
        return null;
    }
    
}
