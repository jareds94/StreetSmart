
/*
 * Created by Mykhaylo Bulgakov, Mukund Katti, Jared Schwalbe, Tim Street, and Hung Vu on 2016.04.07  * 
 * Copyright © 2016 Mykhaylo Bulgakov, Mukund Katti, Jared Schwalbe, Tim Street, and Hung Vu. All rights reserved. * 
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

    /**
     * Finds the user by its username.
     * @param username
     * @return the user
     */
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
    
    /**
     * Finds the user by the user ID.
     * @param id
     * @return the user
     */
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
    
    /**
     * Deletes the user using its id.
     * @param id
     */
    public void deleteUser(int id){
        
        User user = em.find(User.class, id);
        em.remove(user);
    }

    
}