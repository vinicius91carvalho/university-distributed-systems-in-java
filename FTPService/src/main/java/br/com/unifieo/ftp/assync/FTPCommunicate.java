/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.ftp.assync;

import br.com.unifieo.ftp.control.FTPServiceControl;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.protocol.FileProtocol;
import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import br.com.unifieo.common.protocol.UserProtocol;
import br.com.unifieo.common.util.MessagesProperties;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import sun.misc.resources.Messages;

/**
 *
 * @author Aluno
 */
public class FTPCommunicate extends Thread {
    
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private UserProtocol user;
    
    public FTPCommunicate(ObjectInputStream input, ObjectOutputStream output, UserProtocol user) {
        this.input = input;
        this.output = output;
        this.user = user;
    }
    
    @Override
     public void run() {
         
        try {
            while (true) {
                IProtocol protocol = (IProtocol) input.readObject();
                if (protocol instanceof FileProtocol) {
                    LogUtils.msg(MessagesProperties.getKey("msg.receive_file_protocol")+user.getLogin());
                    FileProtocol file = FTPServiceControl.controlSendFiles((FileProtocol) protocol);
                    LogUtils.msg(MessagesProperties.getKey("msg.send_file_protocol")+user.getLogin());
                    output.writeObject(file);
                } else if (protocol instanceof SimpleMessageProtocol) {
                    LogUtils.msg(MessagesProperties.getKey("msg.label.user")+": "+user.getLogin()+" "+MessagesProperties.getKey("msg.send_simple_message_close"));
                    LogUtils.msg(((SimpleMessageProtocol) protocol).getMessage());
                    break;
                }
            }
        } catch (Exception e) {
            LogUtils.exception("msg.error.thread_connection", e);
            e.printStackTrace();
        }

    }
}