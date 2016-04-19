/*
 * Created by Hung Vu on 2016.04.07  * 
 * Copyright Â© 2016 Hung Vu. All rights reserved. * 
 */
package com.streetsmart.sessionbeanpackage;

import com.streetsmart.entitypackage.Pin;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Kevin
 */
@Stateless
public class PinFacade extends AbstractFacade<Pin> {

    @PersistenceContext(unitName = "StreetSmartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PinFacade() {
        super(Pin.class);
    }
    
    //-----------------------------------------------------
    //The following methods are added to the generated code
    //-----------------------------------------------------
    
    /**
     * Finds all pins under an associated user id in the Pin table.
     * @param userId
     * @return 
     */
    public List<Pin> findAllPinsWithUserId(int userId) {
        if (em.createQuery("SELECT p FROM Pin p WHERE p.userId = :uid")
                .setParameter("uid", userId)
                .getResultList().isEmpty()) {
            return null;
        }
        else {
            return (List<Pin>) (em.createQuery("SELECT p FROM Pin p WHERE p.userId = :uid")
                .setParameter("uid", userId)
                .getResultList());        
        }
    }
    
    /**
     * Retrieves all pins from the Pin table. List results are filtered
     * later.
     * 
     * @return 
     */
    public List<Pin> findAllPins() {
        if (em.createQuery("SELECT p FROM Pin p")
                .getResultList().isEmpty()) {
            return null;
        }
        else {
            return (List<Pin>) (em.createQuery("SELECT p FROM Pin p")
                .getResultList());   
            
        }
    }
}
