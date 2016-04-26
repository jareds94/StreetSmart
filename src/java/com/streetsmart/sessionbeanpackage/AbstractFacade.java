/*
 * Created by Hung Vu on 2016.04.07  * 
 * Copyright Â© 2016 Hung Vu. All rights reserved. * 
 */
package com.streetsmart.sessionbeanpackage;

import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Kevin
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();
    
    /*
        Creates the entity in the database.
    */
    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    /*
        Edit or update the database entry for the entity.
    */
    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    /*
        Remove the entity's database entry.
    */
    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    /*
        Finds and returns the entity's database entry.
    */
    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    /*
        Finds all the database entries related to the entity.
    */
    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /*
        Finds a range of all the database entries related to the entity.
    */
    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    /*
        Returns the number of database entries.
    */
    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
}
