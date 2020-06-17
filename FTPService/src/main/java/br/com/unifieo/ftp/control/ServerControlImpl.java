/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.ftp.control;

import br.com.unifieo.common.protocol.IProtocol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Vinicius
 */
public class ServerControlImpl extends FTPServiceControl {
    
    /**
     * Trata as requisições do Servidor de Nomes ao serviço
     * @param socket
     * @param protocol
     * @throws IOException
     * @throws Exception 
     */
    @Override
    public void treatConnection(ObjectOutputStream output, ObjectInputStream input, IProtocol protocol) throws IOException, Exception {
          //LogUtils.info("msg.server_name.check");
    }
    
}
