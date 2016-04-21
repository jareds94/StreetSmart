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
import java.text.SimpleDateFormat;
import java.util.List;

@Named(value = "pinManager")
@SessionScoped
/**
 *
 * @author Tim
 */
public class PinManager implements Serializable {

    private static final double DEFAULT_DISTANCE_FILTER = 10.0;
    
    // Instance Variables (Properties) for Pins 
    private String pinTitle;
    private String pinDescription;
    private boolean pinAnonymous;
    private User selected;
    private List<Pin> mapMenuPins;
    private List<Pin> allPins;
    private List<Pin> pinValues;
    private String filterDistanceStr;
    private String filterOption;
    private String distanceFilterStyle;

    /**
     * The instance variable 'userFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject
     * in this instance variable a reference to the @Stateless session bean
     * PhotoFacade.
     */
    @EJB
    private UserFacade userFacade;

    /**
     * The instance variable 'pinFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject
     * in this instance variable a reference to the @Stateless session bean
     * PhotoFacade.
     */
    @EJB
    private PinFacade pinFacade;

    public PinManager() {
         filterDistanceStr = "10.0";
         distanceFilterStyle = "display: none";
    }

    public PinFacade getPinFacade() {
        return pinFacade;
    }

    public void setPinFacade(PinFacade pinFacade) {
        this.pinFacade = pinFacade;
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

    public String getFilterDistanceStr() {
        return filterDistanceStr;
    }

    public void setFilterDistanceStr(String filterDistanceStr) {
       
        if(filterDistanceStr.isEmpty()) {
            this.filterDistanceStr = "10.0";
            return;
        }
        
        /* Attempt to parse the string*/
        try {
            Double.parseDouble(filterDistanceStr);
        } catch (NumberFormatException | NullPointerException e) {
            filterDistanceStr = "10.0";
            return;
        }
   
        this.filterDistanceStr = filterDistanceStr;
    }

    public String createPin() {
        
        String[] latAndLong = this.getParsedUserLoc();

        // Generate timestamp for pin posting time
        int timestamp = (int) (new Date().getTime() / 1000);

        try {
            Pin pin;
            pin = new Pin();
            pin.setAnonymous(this.pinAnonymous);

            // If the pin is not anonymous and a User is currently logged
            // in, set the associated User id.
            if (!pinAnonymous) {
                pin.setUserId(this.getSelected().getId());
            } else {
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
        return "";
    }

    public User getSelected() {
        if (selected == null) {
            selected = userFacade.find(FacesContext.getCurrentInstance().
                    getExternalContext().getSessionMap().get("user_id"));
        }

        return selected;
    }

    /**
     *
     * @param pin
     * @return
     */
    public String getFormattedDate(Pin pin) {
        SimpleDateFormat format = new SimpleDateFormat();
        return format.format(new Date(((long) pin.getTimePosted()) * 1000L));
    }
    
    public List<Pin> getMapMenuPins() {
        return mapMenuPins;
    }

    public void setMapMenuPins(List<Pin> mapMenuPins) {
        this.mapMenuPins = mapMenuPins;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public String getFilterOption() {
        return filterOption;
    }
    
    public String getDistanceFilterStyle() {
        return distanceFilterStyle;
    }

    public void setDistanceFilterStyle(String distanceFilterStyle) {
        this.distanceFilterStyle = distanceFilterStyle;
    }
    
    public void setFilterOption(String filterOption) {
        switch (filterOption) {
            case "dist":
                this.filterByDistance();
                this.setDistanceFilterStyle("");               
                break;
            case "pop":
                this.filterByPopularity();
                this.setDistanceFilterStyle("display: none");
                break;
            case "new":
                this.filterByNewest();
                this.setDistanceFilterStyle("display: none");
                break;
            default:
                break;
        }
        this.filterOption = filterOption;
    }
      
    public String[] getParsedUserLoc() {
        // Parse out latitiude and longitude from container
        String locData = (String)FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap().get("userLoc");
        // If locData could not be retrieved, stop parsing
        if(locData == null) {
            return null;
        }
        locData = locData.replace("(", "");
        locData = locData.replace(")", "");
        return locData.split(", ");
    }
    
    /**
     * Sets map menu pins list to be populated with pins within
     * 10 miles of the user's current location (regardless of whether
     * or not a user is logged in).
     */
    public void setDefaultMapMenuPins() {
       String[] currentUserLoc = this.getParsedUserLoc();
       if(currentUserLoc != null) {
            float userLat = Float.valueOf(currentUserLoc[0]);
            float userLong = Float.valueOf(currentUserLoc[1]);
            allPins = pinFacade.findAllPins();

            for(int i = 0; i < allPins.size(); i++) {
                Pin pin = allPins.get(i);
                double distanceInMiles = this.getDistanceFromLatLongInMiles(userLat, userLong, 
                        pin.getLatitude(), pin.getLongitude());
                if(!(distanceInMiles <= DEFAULT_DISTANCE_FILTER)) {
                    allPins.remove(i);
                }
            }       
            this.setMapMenuPins(allPins);
        }
    }
    
    /**
     * 
     * @param keyword 
     */
    public void filterByKeyword(String keyword) {
        allPins = pinFacade.findAllPins();
        
        for(int i = 0; i < allPins.size(); i++) {
            Pin pin = allPins.get(i);
            if(!pin.getTitle().contains(keyword)) {
                allPins.remove(i);
            }
        }
    }
    
    /**
     * 
     */
    public void filterByDistance() {
        String[] currentUserLoc = this.getParsedUserLoc();
        Double filterDistance = Double.parseDouble(this.filterDistanceStr);
        
        if(currentUserLoc != null) {
            float userLat = Float.valueOf(currentUserLoc[0]);
            float userLong = Float.valueOf(currentUserLoc[1]);
            allPins = pinFacade.findAllPins();

            for(int i = 0; i < allPins.size(); i++) {
                Pin pin = allPins.get(i);
                double distanceInMiles = this.getDistanceFromLatLongInMiles(userLat, userLong, 
                                              pin.getLatitude(), pin.getLongitude());
                if(!(distanceInMiles <= filterDistance)) {
                    allPins.remove(i);
                }
            }       
            this.setMapMenuPins(allPins);
        }       
    }
    
    /**
     * 
     */
    public void filterByNewest() {
        allPins = pinFacade.findAllPins();
        this.sortPinsByNewest(allPins);
        this.setMapMenuPins(this.pinValues);
    }
    
    /**
     * 
     */
    public void filterByPopularity() {
        
    }
    
    /**
     * 
     * @param lat1
     * @param long1
     * @param lat2
     * @param long2
     * @return 
     */
    private double getDistanceFromLatLongInMiles(float lat1, float long1, 
                                                float lat2, float long2) {
        double R = 3958.756; // Mean radius of the earth in miles
        double dLat = deg2rad(lat2-lat1);
        double dLon = deg2rad(long2-long1); 
        double a = 
            ((Math.sin(dLat/2) * Math.sin(dLat/2)) +
                (Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                Math.sin(dLon/2) * Math.sin(dLon/2))); 
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        return (R * c); // Distance in miles
    }

    /**
     * 
     * @param deg
     * @return 
     */
    private float deg2rad(float deg) {
        return (float) (deg * (Math.PI/180));
    }
    
    /**
     * Quicksort implementation for sorting pins by newest.
     * 
     * @param pins 
     */
    public void sortPinsByNewest(List<Pin> pins) {
        // check for empty or null array
        if (pins == null || pins.isEmpty()){
          return;
        }
        this.pinValues = pins;
        quicksort(0, (pinValues.size()) - 1);
    }

    /**
     * 
     * @param low
     * @param high 
     */
    private void quicksort(int low, int high) {
        int i = low, j = high;

        int pivot = pinValues.get(low + (high-low)/2).getTimePosted();


        while (i <= j) {

            while (pinValues.get(i).getTimePosted() < pivot) {
                i++;
            }

            while (pinValues.get(j).getTimePosted() > pivot) {
                j--;
            }

            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }
        
        if (low < j) {
            quicksort(low, j);
        }
        if (i < high) {
            quicksort(i, high); 
        }
      
    }

    /**
     * 
     * @param i
     * @param j 
     */
    private void exchange(int i, int j) {
        
        Pin temp = pinValues.get(i);
        pinValues.set(i, pinValues.get(j));
        pinValues.set(j, temp);
    }
}
