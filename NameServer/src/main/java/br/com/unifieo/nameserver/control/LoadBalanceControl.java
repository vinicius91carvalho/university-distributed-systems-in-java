/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.nameserver.control;

import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.nameserver.model.ServiceModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
public class LoadBalanceControl {
    
    private static Map<String, RoundRobin> mapRoundRobins = new HashMap<String, RoundRobin>();
    
    public static ServiceModel treatServices(String nameService) {
        // Caso não exista um balanceamento para o serviço (Ou foi encontrado mais de uma instância)
        if (mapRoundRobins.get(nameService) == null) {
            LogUtils.info("msg.not_load_balance_for_service");
            RoundRobin robin = new RoundRobin(nameService);
            robin.start();
            mapRoundRobins.put(nameService, robin);
        }
        ServiceModel service = null;
        while (service == null) {
            service = mapRoundRobins.get(nameService).getServiceActive();
        }
        LogUtils.msg("Retornado o servico: "+nameService+" no IP: "+service.getIp()+" e na PORTA: "+service.getPort());
        return service;
    }
    
    static class RoundRobin extends Thread {
        
        private ServiceModel serviceActive;
        private String nameService;
        private final Integer quantum = 3000;
        
        public RoundRobin(String nameService) {
            this.nameService = nameService;
        }

        @Override
        public void run() {
            
            while(true) {
                List<ServiceModel> services = getServicesByName(nameService);
                for (ServiceModel service : services) {
                    LogUtils.msg("Load Balance RoundRobin: Ativando o servico "+nameService
                            +" no IP "+service.getIp()+" e na PORTA: "+service.getPort());
                    this.serviceActive = service;
                    try {
                        Thread.sleep(quantum);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(LoadBalanceControl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
        }
        
        public ServiceModel getServiceActive() {
            return serviceActive;
        }
        
        public List<ServiceModel> getServicesByName(String nameService) {
            List<ServiceModel> lstServices = new ArrayList<>();
            for (ServiceModel service : NameServerControl.getServices()) {
                if (service.getName().equals(nameService)) {
                    lstServices.add(service);
                }
            }           
            return lstServices;
        }
        
    }       
    
}