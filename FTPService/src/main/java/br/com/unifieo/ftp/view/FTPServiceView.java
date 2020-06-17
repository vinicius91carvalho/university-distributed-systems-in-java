/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.ftp.view;

import br.com.unifieo.common.configuration.FTPServiceConfiguration;
import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.ServiceProtocol;
import br.com.unifieo.common.protocol.UserProtocol;
import br.com.unifieo.common.util.ConnectionUtil;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.util.MessagesProperties;
import br.com.unifieo.ftp.control.ControlFactory;
import br.com.unifieo.ftp.control.FTPServiceControl;
import br.com.unifieo.ftp.control.SyncronizeServicesFTP;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aluno
 */
public class FTPServiceView {   

    // ServerSocket para receber conexões
    private ServerSocket server;
    // Socket que tem que ser criado como variável global por causa da Thread
    private Socket hostSocket;
    
    // Constantes
    private static final String PRIMARY = "PRIMARY";
    private static final String SECONDARY = "SECONDARY";
    
    // Define por parâmetro passado na execução se é o PRIMARY ou SECONDARY
    public static Boolean primary;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // Caso não tenha se passado nenhum parâmetro, assuma que o servidor é o secundário
            if (!(args.length > 0)) {
                args = new String[]{PRIMARY};
            }
            
            new FTPServiceView().start(args[0]);
        } catch (Exception e) {
            LogUtils.exception("msg.error.main",e);
        }
    }

    /**
     * Executa a configuração do servidor e posteriormente a sua inicialização
     *
     */
    public void start(String parameter) {
        Integer port = 0;
        try {
            // Verifica se o servidor irá subir como primario ou secundário
            primary = (parameter != null && parameter.toUpperCase().equals(PRIMARY)) ? Boolean.TRUE : Boolean.FALSE;
            port = initializeServer();
        } catch (IOException e) {
            LogUtils.exception("msg.error.initialize_server", e);
            return;
        }
        executeServer(port);
    }

    /**
     * Método criado para inicializar o servidor
     */
    private Integer initializeServer() throws IOException {       
        Integer port = null;
        Integer maxConnections = null;
        String tipo;
        if (primary) {
            port = FTPServiceConfiguration.PORT_PRIMARY;
            maxConnections = FTPServiceConfiguration.MAX_CONNECTIONS_PRIMARY;
            tipo = "PRIMARIO";
        } else {
            port = FTPServiceConfiguration.PORT_SECONDARY;
            maxConnections = FTPServiceConfiguration.MAX_CONNECTIONS_SECONDARY;
            tipo = "SECUNDARIO";
        }
        LogUtils.msg(MessagesProperties.getKey("msg.service_ftp_starting")+" "+tipo);
        server = new ServerSocket(port, maxConnections);
        LogUtils.info("msg.server_started");
        return port;
    }

    /**
     * Coloca o servidor em execução aguardando novas requisições.
     */
    private void executeServer(Integer port) {
        try {
            ServiceProtocol service = new ServiceProtocol(InetAddress.getLocalHost().getHostAddress(), "FTP", port, IdentificationHost.SERVICE);
            FTPServiceControl.service = service;
            
            new SyncronizeServicesFTP();
            
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
                            
                        } else if (protocol.getIdentification().equals(IdentificationHost.SERVICE)) {
                            
                            ControlFactory.getController(IdentificationHost.SERVICE)
                                .treatConnection(output, input, protocol);
                        }
                    } catch (Exception e) {                       
                            Logger.getLogger(FTPServiceView.class.getName()).log(Level.SEVERE, null, e);                            
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
