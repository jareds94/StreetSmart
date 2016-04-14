/*
 * Created by Timothy Street on 2016.04.12  * 
 * Copyright © 2016 Timothy Street. All rights reserved. * 
 */
package com.streetsmart.managers;

import com.streetsmart.entitypackage.Pin;
import com.streetsmart.entitypackage.User;
import com.streetsmart.sessionbeanpackage.PinFacade;
import com.streetsmart.sessionbeanpackage.UserFacade;
import java.io.Serializable;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named(value = "pinManager")
@SessionScoped
/**
 *
 * @author Tim
 */
public class PinManager implements Serializable {
    
    // Instance Variables (Properties) for Pins 
    private String locationData;
    private String pinTitle;
    private String pinDescription;
    private boolean pinAnonymous;
    private User selected;
    
    /**
     * The instance variable 'userFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject in
     * this instance variable a reference to the @Stateless session bean PhotoFacade.
     */
    @EJB
    private UserFacade userFacade;
    
    /**
     * The instance variable 'pinFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject in
     * this instance variable a reference to the @Stateless session bean PhotoFacade.
     */
    @EJB
    private PinFacade pinFacade;
    
    public PinManager() {
        locationData = "No location data yet.";      
    }

    public PinFacade getPinFacade() {
        return pinFacade;
    }

    public void setPinFacade(PinFacade pinFacade) {
        this.pinFacade = pinFacade;
    }  
    
    public String getLocationData() {
        return locationData;
    }

    public void setLocationData(String locationData) {
        this.locationData = locationData;
    }

    public String getPinTitle() {
        return pinTitle;
    }

    public void setPinTitle(String pinTitle) {
        this.pinTitle = pinTitle;
    }

    public String getPinDescription() {
        return pinDescription;
    }

    public void setPinDescription(String pinDescription) {
        this.pinDescription = pinDescription;
    }

    public boolean isPinAnonymous() {
        return pinAnonymous;
    }

    public void setPinAnonymous(boolean pinAnonymous) {
        this.pinAnonymous = pinAnonymous;
    }
         
    public String createPin() {
        
        if(!locationData.equals("No location data yet."))
        {
            // Parse out latitiude and longitude from container
            String[] latAndLong = locationData.split(" ");
            
            // Generate timestamp for pin posting time
            int timestamp = (int) (new Date().getTime()/1000);
                    
            try {
                Pin pin;
                pin = new Pin();
                pin.setAnonymous(this.pinAnonymous);
                
                // If the pin is not anonymous and a User is currently logged
                // in, set the associated User id.
                if(!pinAnonymous) {
                    pin.setUserId(this.getSelected().getId());
                }               
                else { 
                    // Otherwise, set the id to a row in the User table associated
                    // with all anonymous users (i.e. users are anonymous with id
                    // = 1)
                    pin.setUserId(1);
                }
                        
                pin.setDescription(this.pinDescription);
                pin.setDownvotes(0);
                pin.setUpvotes(0);
                //pin.setId(1); // no need for this if pin id is auto increment
                pin.setTitle(this.pinTitle);
                pin.setLatitude(Float.parseFloat(latAndLong[0]));
                pin.setLongitude(Float.parseFloat(latAndLong[1]));
                pin.setTimePosted(timestamp);
                pin.setType("Some_pin_type");
                pin.setReports(0);
                pinFacade.create(pin);
                return "index?faces-redirect=true";
            } catch (EJBException e) {
                    //statusMessage = "Something went wrong while creating your pin!";
            }
        }
        return "";
    }  
    
    public User getSelected() {
        if (selected == null) {
            selected = userFacade.find(FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap().get("user_id"));
        }
        
        return selected;
    }
}