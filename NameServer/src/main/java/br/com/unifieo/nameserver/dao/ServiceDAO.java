/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.nameserver.dao;

import br.com.unifieo.common.configuration.JPAConfiguration;
import br.com.unifieo.nameserver.model.ServiceModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_DRIVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_PASSWORD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_URL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_USER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_SESSION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_THREAD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_TIMESTAMP;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_SERVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TRANSACTION_TYPE;
import org.eclipse.persistence.config.TargetServer;

/**
 *
 * @author Vinicius
 */
public class ServiceDAO implements IPersistence<ServiceModel> {
    
    @Override
    public void add(ServiceModel service) {  
        EntityManager manager = getEntityManager();
        manager.getTransaction().begin();
        manager.persist(service);
        manager.getTransaction().commit();
        manager.close();
    }
    
    @Override
    public void remove(ServiceModel service) {        
        EntityManager manager = getEntityManager();
        manager.getTransaction().begin();
        ServiceModel searchModel = search(service);
        if (service == null){
            return;
        }        
        manager.remove(manager.merge(searchModel));
        manager.getTransaction().commit();
        manager.close();
    }
    
    public ServiceModel search(ServiceModel service) {
        EntityManager manager = getEntityManager();
        Query query = manager.createQuery("SELECT s FROM ServiceModel s WHERE s.ip = :ip AND s.name = :name AND s.port = :port");
        query.setParameter("ip", service.getIp());
        query.setParameter("name", service.getName());
        query.setParameter("port", service.getPort());       
        try {
            Object result = query.getSingleResult();
            if (result != null) {
                return (ServiceModel) result;
            } 
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    
    @Override
    public List<ServiceModel> list() {
        EntityManager manager = getEntityManager();
        Query query = manager.createQuery("SELECT s FROM ServiceModel s");
        List<ServiceModel> listServices = (List<ServiceModel>) query.getResultList();
        manager.close();
        return listServices;
    }

    @Override
    public EntityManager getEntityManager() {
        
        Map properties = new HashMap();

        // Ensure RESOURCE_LOCAL transactions is used.
        properties.put(TRANSACTION_TYPE, PersistenceUnitTransactionType.RESOURCE_LOCAL.name());

        // Configure the internal EclipseLink connection pool
        properties.put(JDBC_DRIVER, "oracle.jdbc.OracleDriver");
        properties.put(JDBC_URL, JPAConfiguration.IP);
        properties.put(JDBC_USER, JPAConfiguration.USER);
        properties.put(JDBC_PASSWORD, JPAConfiguration.PASSWORD);

        // Configure logging. FINE ensures all SQL is shown
        properties.put(LOGGING_LEVEL, "FINE");
        properties.put(LOGGING_TIMESTAMP, "false");
        properties.put(LOGGING_THREAD, "false");
        properties.put(LOGGING_SESSION, "false");
        
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
        properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION); 

        // Ensure that no server-platform is configured
        properties.put(TARGET_SERVER, TargetServer.None);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("br.com.unifieo_NameServer_jar_1.0-SNAPSHOTPU", properties);

        return emf.createEntityManager();
    }
    
}