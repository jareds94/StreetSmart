/*
 * Created by Mukund Katti on 2016.04.19  * 
 * Copyright © 2016 Mukund Katti. All rights reserved. * 
 */
package com.streetsmart.sessionbeanpackage;

import com.streetsmart.entitypackage.Comments;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author kattim
 */
@Stateless
public class CommentsFacade extends AbstractFacade<Comments> {

    @PersistenceContext(unitName = "StreetSmartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CommentsFacade() {
        super(Comments.class);
    }
    
    //-----------------------------------------------------
    //The following methods are added to the generated code
    //----------------------------------------------------- 
    
        /**
     * Finds all comments given a pin id.
     * @param pinId
     * @return 
     */
    public List<Comments> findAllCommentsByPinId(int pinId) {
        if (em.createQuery("SELECT c FROM Comments c WHERE c.pinId = :pinId")
                .setParameter("pinId", pinId)
                .getResultList().isEmpty()) {
            return null;
        }
        else {
            return (List<Comments>) (em.createQuery("SELECT p FROM Pin p WHERE p.userId = :uid")
                .setParameter("pinId", pinId)
                .getResultList());        
        }
    }
}
