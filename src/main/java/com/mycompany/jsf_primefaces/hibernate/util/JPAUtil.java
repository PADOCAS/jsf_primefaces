/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.jsf_primefaces.hibernate.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author lucia
 */
@ApplicationScoped
public class JPAUtil {
    
    private EntityManagerFactory factory = null;

    public JPAUtil() {
        try {
            if (factory == null) {
                //Vai ler o arquivo persistence com o nome (jsf_persistence_unit) >> No arquivo persistence.xml: <persistence-unit name="jsf_persistence_unit">
                //Cria apenas 1 vez, quando o factory é NULO ainda...
                factory = Persistence.createEntityManagerFactory("jsf_persistence_unit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Produces
    @RequestScoped
    public EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    /**
     * Método para retornar a primary key de uma entidade
     *
     * @param entity
     * @return Primary Key da entidade
     */
    public Object getPrimaryKey(Object entity) {
        return factory.getPersistenceUnitUtil().getIdentifier(entity);
    }
    
}
