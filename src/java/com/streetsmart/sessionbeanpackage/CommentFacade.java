/*
 * Created by Mykhaylo Bulgakov, Mukund Katti, Jared Schwalbe, Tim Street, and Hung Vu on 2016.04.19  * 
 * Copyright © 2016 Mykhaylo Bulgakov, Mukund Katti, Jared Schwalbe, Tim Street, and Hung Vu. All rights reserved. * 
 */
package com.streetsmart.sessionbeanpackage;

import com.streetsmart.entitypackage.Comment;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author kattim
 */
@Stateless
public class CommentFacade extends AbstractFacade<Comment> {

    @PersistenceContext(unitName = "StreetSmartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CommentFacade() {
        super(Comment.class);
    }
    
    //-----------------------------------------------------
    //The following methods are added to the generated code
    //----------------------------------------------------- 
    
        /**
     * Finds all comments given a pin id.
     * @param pinId
     * @return 
     */
    public List<Comment> findAllCommentsByPinId(int pinId) {
        if (em.createQuery("SELECT c FROM Comment c WHERE c.pinId = :pinId")
                .setParameter("pinId", pinId)
                .getResultList().isEmpty()) {
            return null;
        }
        else {
            return (List<Comment>) (em.createQuery("SELECT c FROM Comment c WHERE c.pinId = :pinId")
                .setParameter("pinId", pinId)
                .getResultList());        
        }
    }
}
