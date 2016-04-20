/*
 * Created by Hung Vu on 2016.04.19  * 
 * Copyright Â© 2016 Hung Vu. All rights reserved. * 
 */
package com.streetsmart.sessionbeanpackage;

import com.streetsmart.entitypackage.Photo;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Kevin
 */
@Stateless
public class PhotoFacade extends AbstractFacade<Photo> {

    @PersistenceContext(unitName = "StreetSmartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PhotoFacade() {
        super(Photo.class);
    }
    
        // The following findPhotosByUserID method is added to the generated code.
    
    public List<Photo> findPhotosByUserID(Integer userID) {
        return (List<Photo>) em.createNamedQuery("Photo.findPhotosByUserId")
                .setParameter("userId", userID)
                .getResultList();
    }
    
}
