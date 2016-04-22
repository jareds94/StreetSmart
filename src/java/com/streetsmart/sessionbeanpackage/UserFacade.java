
/*
 * Created by Hung Vu on 2016.04.07  * 
 * Copyright Â© 2016 Hung Vu. All rights reserved. * 
 */
package com.streetsmart.sessionbeanpackage;

import com.streetsmart.entitypackage.User;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Kevin
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {

    @PersistenceContext(unitName = "StreetSmartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }
    
    //-----------------------------------------------------
    //The following methods are added to the generated code
    //-----------------------------------------------------
    
    public User getUser(int id) {
        return em.find(User.class, id);
    }

    public User findByUsername(String username) {
        if (em.createQuery("SELECT u FROM User u WHERE u.username = :username")
                .setParameter("username", username)
                .getResultList().isEmpty()) {
            return null;
        }
        else {
            return (User) (em.createQuery("SELECT u FROM User u WHERE u.username = :username")
                .setParameter("username", username)
                .getSingleResult());        
        }
    }
    
    public User findByUserId(int id) {
        if (em.createQuery("SELECT u FROM User u WHERE u.id = :id")
                .setParameter("id", id)
                .getResultList().isEmpty()) {
            return null;
        }
        else {
            return (User) (em.createQuery("SELECT u FROM User u WHERE u.id = :id")
                .setParameter("id", id)
                .getSingleResult());        
        }
    }
    
    public void deleteUser(int id){
        
        User user = em.find(User.class, id);
        em.remove(user);
    }

    
}