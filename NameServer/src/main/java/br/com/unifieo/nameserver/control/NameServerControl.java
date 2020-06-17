/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unifieo.nameserver.control;

import br.com.unifieo.common.configuration.NameServersConfiguration;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.nameserver.control.assync.NameServerSyncronization;
import br.com.unifieo.common.util.MessagesProperties;
import br.com.unifieo.nameserver.dao.ServiceDAO;
import br.com.unifieo.nameserver.model.ServiceModel;
import br.com.unifieo.nameserver.protocol.ServiceEventProtocol;
import br.com.unifieo.nameserver.protocol.ServiceEventProtocol.Action;
import br.com.unifieo.nameserver.util.ConvertUtil;
import br.com.unifieo.common.protocol.IProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.ServiceProtocol;
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import br.com.unifieo.nameserver.view.NameServerView;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
public abstract class NameServerControl {

    // Lista de serviços que serão carregados e posteriormente utilizados
    private static final List<ServiceModel> servicos;
    
    public static Boolean primary;
    
    private ServiceEventProtocol eventAdd;
    private static ServiceEventProtocol eventRemove;

    protected static ServiceDAO dataAccessObject;
    
    static {
        dataAccessObject = new ServiceDAO();
        servicos = carregaServicos();        
    }
    
    public static void start(Boolean primary) {
        NameServerControl.primary = primary;
        if (primary) {
            checkServicesStatus();
        } else {
            checkServerNamePrimaryStatus();
        }
    }
    
    public abstract void treatConnection(ObjectInputStream input, ObjectOutputStream output, IProtocol protocol) throws IOException, Exception;
    
    private static void checkServerNamePrimaryStatus() {
        new Thread(
                new Runnable() {
                    public void run() {
                        while(true) {                            
                            Socket socket = null;
                            try {
                                socket = new Socket(NameServersConfiguration.IP_PRIMARY, NameServersConfiguration.PORT_PRIMARY);
                                new ObjectOutputStream(socket.getOutputStream()).writeObject(new SimpleMessageProtocol(MessagesProperties.getKey("msg.sn_say"), IdentificationHost.SERVER_NAME));
                                LogUtils.msg(((SimpleMessageProtocol) new ObjectInputStream(socket.getInputStream()).readObject()).getMessage());                                                                    
                                if (primary) {
                                    LogUtils.info("msg.server_name_primary_yes_response");
                                    NameServerView.primary = false;
                                    NameServerControl.primary = false;
                                    LogUtils.info("msg.stop_check_services");
                                }
                                Thread.sleep(5000);
                            } catch (Exception ex) {
                                LogUtils.info("msg.server_name_primary_not_response");
                                if (!primary) {
                                    LogUtils.info("msg.error.server_name_stop");
                                    NameServerView.primary = true;
                                    NameServerControl.primary = true;
                                    LogUtils.info("msg.start_services");
                                    checkServicesStatus();
                                }
                            } finally {
                                if (socket != null && socket.isConnected()) {
                                    try {
                                        socket.close();
                                    } catch (IOException ex) {
                                        Logger.getLogger(NameServerControl.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                    }
        }).start();
    }
    
    /**
     * Checa se os status estão online
     */
    private static void checkServicesStatus() {
        // Uso de Lambda do Java 8 (Possível graças as interfaces funcionais
        Thread thread = new Thread(
                new Runnable() {
                    public void run() {
                        while(true) {
                            if (!primary) { // Se não for o primário, não cheque os serviços
                                break;
                            } 
                            List<ServiceModel> listRemove = new ArrayList<ServiceModel>();
                            for (ServiceModel service : servicos) {
                                ServiceProtocol serviceProtocol = ConvertUtil.convertServiceToProtocol(service);
                                try {
                                    LogUtils.msg(new StringBuilder()
                                            .append(MessagesProperties.getKey("msg.verify_service")).append(serviceProtocol.getName())
                                            .append(MessagesProperties.getKey("msg.verify_service_1")).append(serviceProtocol.getIp())
                                            .append(MessagesProperties.getKey("msg.and_port")).append(serviceProtocol.getPort()).toString());
                                    Socket socket = new Socket(serviceProtocol.getIp(), serviceProtocol.getPort());
                                    new ObjectOutputStream(socket.getOutputStream()).writeObject(new SimpleMessageProtocol(MessagesProperties.getKey("msg.effective_connection"), IdentificationHost.SERVER_NAME));
                                    if (!socket.isConnected()) 
                                        listRemove.add(service);
                                    else 
                                        socket.close();
                                } catch (Exception ex) {
                                    LogUtils.exception("msg.error.check_status", ex);
                                    listRemove.add(service);
                                }                    
                            }
                            removeService(listRemove);
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(NameServerControl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
        });
        thread.start();
    }
    
    /**
     * Procura um serviço pelo nome
     * @param nameService
     * @return 
     */
    public ServiceModel getOtherServiceToService(ServiceProtocol serviceProtocol) {
        for (ServiceModel service : servicos) {
            if (service.getName().toUpperCase().equals(serviceProtocol.getName())
                    && 
                    (!service.getIp().equals(serviceProtocol.getIp())
                    || !service.getPort().equals(serviceProtocol.getPort()))) {
                return service;
            }
        }
        return null;
    }

    /**
     * Procura um serviço pelo nome
     * @param nameService
     * @return 
     */
    public ServiceModel getServiceByName(String nameService) {
        List<ServiceModel> servicesFound = new ArrayList<ServiceModel>();
        for (ServiceModel service : servicos) {
            if (service.getName().toUpperCase().equals(nameService.toUpperCase())) {
                servicesFound.add(service);
            }
        }
        if (servicesFound.size() == 1) {
            return servicesFound.get(0);
        } else if (servicesFound.size() > 1) { // Caso haja mais de um serviço com o mesmo nome, chama o balanceamento de carga
            LogUtils.info("msg.more_services");
            return LoadBalanceControl.treatServices(nameService);
        }
        return null;
    }

    /**
     * Devolve uma String formatada contendo os nomes dos serviços disponíveis
     * @return 
     */
    public String getServicesToClient() {
        StringBuilder builder = new StringBuilder();
        if (servicos.size() > 0) {
            builder.append(MessagesProperties.getKey("msg.service_send_name")).append("\n");
            for (ServiceModel service : servicos) {
                builder.append(MessagesProperties.getKey("msg.label.nome")).append(": ").append(service.getName()).append("\n");
            }
            return builder.toString();
        } else {
            return MessagesProperties.getKey("msg.services_not_found");
        }
    }

    private static List<ServiceModel> carregaServicos() {
        return dataAccessObject.list();
    }

    /**
     * Chama o DAO para adicionar o serviço e dispara um evento para adição do serviço no outro NameServer
     * @param serv 
     * @param fromThisServer 
     */
    protected void addService(ServiceModel serv, Boolean fromThisServer) {
        servicos.add(serv);
        if (fromThisServer) {
            dataAccessObject.add(serv);
            eventAdd = new ServiceEventProtocol(ConvertUtil.convertServiceToProtocol(serv), 
                    IdentificationHost.SERVER_NAME, Action.ADD);
            
            new Thread(new Runnable() {
                public void run() {
                    NameServerSyncronization.syncronizeServiceWithOtherServer(eventAdd, primary);
                }
            }).start();
            
        }    
    }

    /**
     * Chama o DAO para remover o serviço e dispara um evento para remoção do serviço no outro NameServer
     * @param serv 
     * @param fromThisServer 
     */
    protected static void removeService(ServiceModel serv, Boolean fromThisServer) {
        if (servicos.contains(serv)) {
            servicos.remove(serv);
            if (fromThisServer) {
                dataAccessObject.remove(serv);
                eventRemove = new ServiceEventProtocol(ConvertUtil.convertServiceToProtocol(serv), 
                        IdentificationHost.SERVER_NAME, Action.REMOVE);

                new Thread(new Runnable() {
                    public void run() {
                        NameServerSyncronization.syncronizeServiceWithOtherServer(eventRemove, primary);
                    }
                }).start();

            } 
        }
        
    }

    protected static void removeService(List<ServiceModel> listRemove) {
        for (ServiceModel service : listRemove) {
            removeService(service, true);
        }    
    }
    
    public static List<ServiceModel> getServices() {
        return servicos;
    }
    
}