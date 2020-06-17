/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.nameserver.dao;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Vinicius
 * @param <T>
 */
public interface IPersistence<T> {
    
    public EntityManager getEntityManager();
    
    public void add(T t);    
    public void remove(T t);
    public List<T> list();
    
    
}
