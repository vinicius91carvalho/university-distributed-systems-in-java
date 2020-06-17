/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.chat.view;

import br.com.unifieo.chat.control.ChatServiceControl;
import br.com.unifieo.common.configuration.ChatServiceConfiguration;
import br.com.unifieo.common.util.ConnectionUtil;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.util.MessagesProperties;
import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.ServiceProtocol;
import br.com.unifieo.chat.control.ControlFactory;
import br.com.unifieo.common.protocol.UserProtocol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Aluno
 */
public class ChatServiceView {   

    // ServerSocket para receber conexões
    private ServerSocket server;
    // Socket que tem que ser criado como variável global por causa da Thread
    private Socket hostSocket;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new ChatServiceView().start();
        } catch (Exception e) {
            LogUtils.exception("msg.error.main",e);
        }
    }

    /**
     * Executa a configuração do servidor e posteriormente a sua inicialização
     *
     */
    public void start() {
        try {           
            initializeServer();
        } catch (IOException e) {
            LogUtils.exception("msg.error.initialize_server", e);
            return;
        }
        executeServer();
    }

    /**
     * Método criado para inicializar o servidor
     */
    private void initializeServer() throws IOException {       
        Integer port = ChatServiceConfiguration.PORT;
        Integer maxConnections = ChatServiceConfiguration.MAX_CONNECTIONS;
        String tipo = "Chat Service";
        LogUtils.msg(MessagesProperties.getKey("msg.chat_service_starting")+" "+tipo);
        server = new ServerSocket(port, maxConnections);
        LogUtils.info("msg.server_started");
    }

    /**
     * Coloca o servidor em execução aguardando novas requisições.
     */
    private void executeServer() {        
                
        try {
            ServiceProtocol service = new ServiceProtocol(InetAddress.getLocalHost().getHostAddress(), "CHAT", ChatServiceConfiguration.PORT, IdentificationHost.SERVICE);
            // Cadastra o serviço no Servidor de Nomes            
            new ConnectionUtil().addService(service);
        } catch (UnknownHostException ex){
            LogUtils.exception("msg.error.service_start", ex);
        } catch (Exception e) {
            LogUtils.exception("msg.error.server_name_connection", e);
            //return;
        }
        
        // Aguardando requisições de serviços ou clientes
        while (true) {
            try {
                hostSocket = server.accept();
            } catch (IOException e) {
                LogUtils.msg(new StringBuilder().append(MessagesProperties.getKey("msg.error.host")).append(": ")
                        .append(hostSocket.getInetAddress().getHostAddress()).append("\n ")
                        .append(MessagesProperties.getKey("msg.descripton_error"))
                        .append(e.getLocalizedMessage()).toString());               
                continue;
            }
            Thread thread = new Thread(new Runnable() {

                private Socket socket = hostSocket;
                private UserProtocol userProtocol;
                private IProtocol protocol;

                @Override
                public void run() {
                    try {                      
                        // Recebe a resposta do host       
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        IProtocol protocol = (IProtocol) input.readObject();
                        this.protocol = protocol;
                        
                        if (protocol.getIdentification().equals(IdentificationHost.CLIENT)) {
                            LogUtils.msg(MessagesProperties.getKey("msg.host_is")+ " " + protocol.getIdentification().getDescription());
                        }

                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        
                        // Encaminha para o tratamento correto verificando se o host é cliente ou serviço
                        if (protocol.getIdentification().equals(IdentificationHost.CLIENT)) {
                            userProtocol = (UserProtocol) protocol;
                            ControlFactory.getController(IdentificationHost.CLIENT)
                                    .treatConnection(output, input, userProtocol);

                        } else if (protocol.getIdentification().equals(IdentificationHost.SERVER_NAME)) {
                            
                            ControlFactory.getController(IdentificationHost.SERVER_NAME)
                                .treatConnection(output, input, protocol);
                            
                        }                        
                    } catch (Exception e) {                       
                        if (userProtocol != null) {
                            ChatServiceControl.removeUser(userProtocol);
                        }
                    } finally {
                        try {
                            if (socket != null && !socket.isClosed()) {
                                if (protocol != null && protocol.getIdentification().equals(IdentificationHost.CLIENT)) {
                                    LogUtils.msg(MessagesProperties.getKey("msg.closed_connection")+" "+socket.getInetAddress().getHostAddress());
                                }
                                socket.close();
                                socket = null;
                            }
                        } catch (IOException ex) {
                            LogUtils.exception("msg.error.close_connection", ex);
                        }
                    }
                }
            });
            thread.start();
        }
    }
}
