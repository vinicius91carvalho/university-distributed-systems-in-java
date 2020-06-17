/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.nameserver.view;

import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.util.MessagesProperties;
import br.com.unifieo.common.configuration.NameServersConfiguration;
import br.com.unifieo.nameserver.control.ControlFactory;
import br.com.unifieo.nameserver.control.NameServerControl;
import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aluno
 */
public class NameServerView {   

    // ServerSocket para receber conexões
    private ServerSocket server;
    // Socket que tem que ser criado como variável global por causa da Thread
    private Socket hostSocket;

    // Constantes
    private static final String PRIMARY = "PRIMARY";
    private static final String SECONDARY = "SECONDARY";

    // Define por parâmetro passado na execução se é o PRIMARY ou SECONDARY
    public static Boolean primary;

    // Construtor
    public NameServerView() {}

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // Caso não tenha se passado nenhum parâmetro, assuma que o servidor é o secundário
            if (!(args.length > 0)) {
                args = new String[]{PRIMARY};
            }

            new NameServerView().start(args[0]);
        } catch (Exception e) {
            LogUtils.exception("msg.error.main",e);
        }
    }

    /**
     * Executa a configuração do servidor e posteriormente a sua inicialização
     *
     * @param parameter String passada como parâmetro na inicialização do
     * aplicativo
     */
    public void start(String parameter) {
        // Verifica se o servidor irá subir como primario ou secundário
        primary = (parameter != null && parameter.toUpperCase().equals(PRIMARY)) ? Boolean.TRUE : Boolean.FALSE;
        NameServerControl.start(primary);
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
        Integer port = null;
        Integer maxConnections = null;
        String tipo;
        if (primary) {
            port = NameServersConfiguration.PORT_PRIMARY;
            maxConnections = NameServersConfiguration.MAX_CONNECTIONS_PRIMARY;
            tipo = "PRIMARIO";
        } else {
            port = NameServersConfiguration.PORT_SECONDARY;
            maxConnections = NameServersConfiguration.MAX_CONNECTIONS_SECONDARY;
            tipo = "SECUNDARIO";
        }
        LogUtils.msg(MessagesProperties.getKey("msg.server_name_starting")+" "+tipo);
        server = new ServerSocket(port, maxConnections);
        LogUtils.info("msg.server_started");
    }

    /**
     * Coloca o servidor em execução aguardando novas requisições.
     */
    private void executeServer() {
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

                @Override
                public void run() {
                    ObjectInputStream input = null;
                    ObjectOutputStream output = null;
                    try {

                        // Inicializa os objetos de escrita e leitura para o Socket
                        input = new ObjectInputStream(socket.getInputStream());
                        output = new ObjectOutputStream(socket.getOutputStream());

                        // Recebe a resposta do host                        
                        IProtocol protocol = (IProtocol) input.readObject();
                        
                        if (protocol.getIdentification().equals(IdentificationHost.CLIENT)
                                || protocol.getIdentification().equals(IdentificationHost.SERVICE)) {
                            LogUtils.msg(MessagesProperties.getKey("msg.host_is")+ " " + protocol.getIdentification().getDescription());
                        }

                        // Encaminha para o tratamento correto verificando se o host é cliente ou serviço
                        if (protocol.getIdentification().equals(IdentificationHost.CLIENT)) {

                            ControlFactory.getController(IdentificationHost.CLIENT)
                                    .treatConnection(input, output, protocol);

                        } else if (protocol.getIdentification().equals(IdentificationHost.SERVICE)) { // Serviço

                            ControlFactory.getController(IdentificationHost.SERVICE)
                                    .treatConnection(input, output, protocol);

                        } else if (protocol.getIdentification().equals(IdentificationHost.SERVER_NAME)) {
                            
                            ControlFactory.getController(IdentificationHost.SERVER_NAME)
                                .treatConnection(input, output, protocol);
                            
                        } else if (protocol.getIdentification().equals(IdentificationHost.SERVICO_SYNCRO)) {
                            
                            ControlFactory.getController(IdentificationHost.SERVICO_SYNCRO)
                                .treatConnection(input, output, protocol);
                            
                        }                        
                    } catch (Exception e) {                        
                        if (socket != null && socket.isConnected()) {
                            try {
                                output.writeObject(new SimpleMessageProtocol(e.getMessage(), IdentificationHost.SERVER_NAME));
                            } catch (IOException ex) {
                                Logger.getLogger(NameServerView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } finally {
                        try {
                            if (socket != null) {
                                socket.close();
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
