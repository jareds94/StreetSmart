/*
 * Created by Timothy Street on 2016.04.12  * 
 * Copyright © 2016 Timothy Street. All rights reserved. * 
 */
package com.streetsmart.managers;

import com.streetsmart.sessionbeanpackage.PinFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Tim
 */
@Named(value = "accountManager")
@SessionScoped
public class PinManager implements Serializable {
    
    // Instance Variables (Properties) for Pins 
    private String locationData;
    private String pinTitle;
    private String pinDescription;
    private boolean pinAnonymous;
    
    /**
     * The instance variable 'pinFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject in
     * this instance variable a reference to the @Stateless session bean PhotoFacade.
     */
    @EJB
    private PinFacade pinFacade;
    
    public PinManager() {
        locationData = "0.0 0.0";      
    }
    
}
