/*
 * Created by Timothy Street on 2016.04.12  * 
 * Copyright © 2016 Timothy Street. All rights reserved. * 
 */
package com.streetsmart.managers;


import com.streetsmart.entitypackage.Pin;
import com.streetsmart.entitypackage.User;
import com.streetsmart.sessionbeanpackage.PhotoFacade;
import com.streetsmart.sessionbeanpackage.PinFacade;
import com.streetsmart.sessionbeanpackage.UserFacade;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.faces.application.FacesMessage;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Tim
 */
@Named(value = "pinManager")
@SessionScoped
/**
 * This class handles all interactions between the frontend and backend with
 * pin objects. Primarily handles pin creation and what is displayed on the
 * side menu depending on what filter type is selected.
 * 
 * @author Tim
 */
public class PinManager implements Serializable {

    // Static field used to prevent a component in the side menu from
    // displaying. Component does not retain display width/height with this
    // style
    private static final String NOT_DISPLAYED = "display: none";
    
    // Instance Variables (Properties) for Pins 
    private UploadedFile file;
    // Pin title to be set when creating a pin (referenced in index.xhtml)
    private String newPinTitle;
    // Pin description to be set when creating a pin (referenced in index.xhtml)
    private String newPinDescription;
    // Stores whether or not a pin should be created anonymously (referenced
    // in index.xhtml)
    private boolean newPinAnonymous;
    // Stores whether or not a pin photo exists in the database
    private boolean newPinPhotoExists;
    // Currently selected Pin's ID
    private int selectedPinId;
    // Currently selected Pin
    private Pin selectedPin;
    // The contents of this list are displayed on the side menu depending
    // on which filter type is selected (referenced in index.xhtml)
    private List<Pin> mapMenuPins;
    // Auxiliary list used for quicksort implementation
    private List<Pin> pinValues;
    // The contents of this String are taken from the input text field
    // displayed when filter by distance is selected in the side menu
    private String filterDistance;
    // The contents of this String are taken from the selection field
    // depending on which filter type is selected
    private String filterOption;
    // String representing the style for the filter form in index.xhtml. Used
    // to hide/display the form depending on which filter is selected
    private String distanceFilterStyle;
    // The contents of this String are taken from the keyword input field
    // on the side menu
    private String keywordFilterInput;
    // String representing the style for the keyword filter form in index.xhtml.
    // Used to hide/display the keyword input form depending on which filter
    // type is selected
    private String keywordFilterStyle;
    // Represents the pin to be added to the map after calling createPin()
    private Pin pin;

    /**
     * The instance variable 'userFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject
     * in this instance variable a reference to the @Stateless session bean
     * UserFacade.
     */
    @EJB
    private UserFacade userFacade;

    /**
     * The instance variable 'pinFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject
     * in this instance variable a reference to the @Stateless session bean
     * PinFacade. Handles all interactions with the Pin table in the 
     * StreetSmart database.
     */
    @EJB
    private PinFacade pinFacade;
    
     /**
     * The instance variable 'photoFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject
     * in this instance variable a reference to the @Stateless session bean
     * PhotoFacade.
     */
    @EJB
    private PhotoFacade photoFacade;

    /**
     * Returns the current instance of PhotoFacade.
     * 
     * @return photoFacade, the PhotoFacade instance to be returned. 
     */
    public PhotoFacade getPhotoFacade() {
        return photoFacade;
    }

    /**
     * Sets the current instance of PhotoFacade.
     * 
     * @param photoFacade, the PhotoFacade instance to be set.
     */
    public void setPhotoFacade(PhotoFacade photoFacade) {
        this.photoFacade = photoFacade;
    }

    /**
     * Default constructor.
     */
    public PinManager() {
        // Default filter distance set to 10 miles
        filterDistance = "10.0";
        // Distance and keyword filter styles set initially to "display: none"
        // because pins are sorted by popularity on initial page load
        distanceFilterStyle = NOT_DISPLAYED;
        keywordFilterStyle = NOT_DISPLAYED;
        // No keyword filter input initially
        keywordFilterInput = "";
        // Set to initially filter by popularity
        filterOption = "pop";
        // Initialized to prevent nullpointer exceptions
        selectedPin = new Pin();
        selectedPin.setTimePosted(0); 
    }

    /**
     * Returns the current instance of PinFacade.
     * 
     * @return the PinFacade instance to be returned. 
     */
    public PinFacade getPinFacade() {
        return pinFacade;
    }

    /**
     * Sets the current instance of PinFacade.
     * 
     * @param pinFacade, the PinFacade instance to be set.
     */
    public void setPinFacade(PinFacade pinFacade) {
        this.pinFacade = pinFacade;
    }

    /**
     * Returns the uploaded file.
     * 
     * @return the UploadedFile object.
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * Obtains the uploaded file.
     * 
     * @param file, the UploadedFile object to be set.
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /**
     * Retrieves the title for the pin to be posted. Called in pinCreate().
     * 
     * @return the new pin title to set.
     */
    public String getNewPinTitle() {
        return newPinTitle;
    }

    /**
     * Sets the title for the pin to be posted. Called in pinCreate().
     * 
     * @param newPinTitle, the new pin title to be set.
     */
    public void setNewPinTitle(String newPinTitle) {
        this.newPinTitle = newPinTitle;
    }

    /**
     * Retrieves the description for the pin to be posted. Called in 
     * pinCreate().
     * 
     * @return the new pin title to set.
     */
    public String getNewPinDescription() {
        return newPinDescription;
    }

    /**
     * Sets the description for the pin to be posted. Called in pinCreate().
     * 
     * @param newPinDescription, the new pin title to be set.
     */
    public void setNewPinDescription(String newPinDescription) {
        this.newPinDescription = newPinDescription;
    }

    /**
     * Retrieves whether or not the pin should be posted as anonymous 
     * for the pin to be posted.
     * 
     * @return whether or not the pin should be posted as anonymous 
     */
    public boolean isNewPinAnonymous() {
        return newPinAnonymous;
    }

    /**
     * Sets whether or not the pin should be posted as anonymous for the pin
     * to be posted.
     * 
     * @param newPinAnonymous, the anonymity setting to be set
     */
    public void setNewPinAnonymous(boolean newPinAnonymous) {
        this.newPinAnonymous = newPinAnonymous;
    }

    /**
     * Returns whether or not a pin was uploaded to the current pin to be
     * created.
     * 
     * @return true if there was a photo uploaded, false otherwise
     */
    public boolean isNewPinPhotoExists() {
        return newPinPhotoExists;
    }

    /**
     * Sets whether or not a pin was uploaded to the current pin to be created.
     * 
     * @param newPinPhotoExists, the parameter to be set depending on whether
     *                           the photo was uploaded
     */
    public void setNewPinPhotoExists(boolean newPinPhotoExists) {
        this.newPinPhotoExists = newPinPhotoExists;
    }

    /**
     * Retrieves the filter distance mapped to the input distance field
     * when filtering by distance in index.xhtml.
     * 
     * @return the filter distance represented as a String
     */
    public String getFilterDistance() {
        return filterDistance;
    }

    /**
     * Sets the filter distance mapped to the input distance field
     * when filtering by distance in index.xhtml.
     * 
     * @param filterDistance, the filter distance to be set by the user
     */
    public void setFilterDistance(String filterDistance) {

        if (filterDistance.isEmpty()) {
            this.filterDistance = "10.0";
            return;
        }

        /* Attempt to parse the string*/
        try {
            Double.parseDouble(filterDistance);
        } catch (NumberFormatException | NullPointerException e) {
            this.filterDistance = "10.0";
            return;
        }

        this.filterDistance = filterDistance;
    }

    /**
     * Retrieves the selected pin's id.
     * 
     * @return the pin id represented as an int
     */
    public int getSelectedPinId() {
        return selectedPinId;
    }

    /**
     * Sets the selected pin's id.
     * 
     * @param selectedPinId, the int Id to be set on the selected pin
     */
    public void setSelectedPinId(int selectedPinId) {
        this.selectedPinId = selectedPinId;
        this.selectedPin = pinFacade.findPinWithId(selectedPinId);
    }

    /**
     * Retrieves the input from the keyword filter input field.
     * 
     * @return the keywordFilterInput
     */
    public String getKeywordFilterInput() {
        return keywordFilterInput;
    }

    /**
     * Sets the input from the keyword filter input field.
     * 
     * @param keywordFilterInput, the keywordFilterInput to be set
     */
    public void setKeywordFilterInput(String keywordFilterInput) {
        this.keywordFilterInput = keywordFilterInput;
    }

    /**
     * Retrieves the currently selected pin.
     * 
     * @return the selected Pin object
     */
    public Pin getSelectedPin() {
        return selectedPin;
    }

    /**
     * Sets the currently selected pin.
     * 
     * @param selectedPin, the pin to be set as the selected pin
     */
    public void setSelectedPin(Pin selectedPin) {
        this.selectedPin = selectedPin;
    }

    /**
     * Retrieves the filter style on the keyword filter input field. Alternates
     * between "DISPLAY: NONE" and "" to hide and show the input field.
     * 
     * @return the keywordFilterStyle as a String
     */
    public String getKeywordFilterStyle() {
        return keywordFilterStyle;
    }

    /**
     * Sets the filter style on the keyword filter input field.
     * 
     * @param keywordFilterStyle, the filter style to be set
     */
    public void setKeywordFilterStyle(String keywordFilterStyle) {
        this.keywordFilterStyle = keywordFilterStyle;
    }
    
    /**
     * Handles pin creation. Called when "Create Pin" is clicked in the create
     * pin dialogue menu.
     * 
     * @return 
     */
    public String createPin() {
        
        // Retrieve the current user from the session map by user id
        User user = userFacade.find(FacesContext.getCurrentInstance().
                        getExternalContext().getSessionMap().get("user_id"));

        // Parse the user's current location from the xhtml element containing
        // the information 
        String[] latAndLong = this.getParsedUserLoc();

        // Generate timestamp for pin posting time
        int timestamp = (int) (new Date().getTime() / 1000);

        try {
            
            // Initialize a new pin and set the associated properties
            pin = new Pin();
            pin.setAnonymous(this.newPinAnonymous);
            pin.setUserId(user.getId());
            pin.setDescription(this.newPinDescription);
            pin.setDownvotes(0);
            pin.setUpvotes(0);
            //pin.setId(1); // no need for this if pin id is auto increment
            pin.setTitle(this.newPinTitle);
            // Parse the user's location as a float before setting
            pin.setLatitude(Float.parseFloat(latAndLong[0]));
            pin.setLongitude(Float.parseFloat(latAndLong[1]));
            pin.setTimePosted(timestamp);
            pin.setType("Some_pin_type"); //remove this after db updated
            pin.setReports(0);              // remove this after db updated
            // Insert the pin entry into the Pin table in StreetSmartDB
            pinFacade.create(pin); 
            
            // If a file was uploaded, map the photo to the previously
            // inserted pin
            if (file.getSize() != 0) {
                pin.setPhoto(true);
                pinFacade.edit(pin);
                copyPhotoFile(file);
            } else {
                // Otherwise, depending on anonymity, mao the created pin to
                // a default user profile picture from resources
                if (pin.getAnonymous()){
                    pin.setPhoto(true);
                    pinFacade.edit(pin);
                    assignPinDefaultPhoto();
                }
                else {
                    pin.setPhoto(false);
                    pinFacade.edit(pin);
                }
            }
            this.newPinTitle = "";
            this.newPinDescription = "";
            this.newPinPhotoExists = false;
            this.newPinAnonymous = false;
            return "index?faces-redirect=true&id=" + pin.getId();
        } catch (EJBException e) {
            //TODO: Print useful error message somehow
        }
        return "";
    }
    
    /**
     * 
     * @param pin 
     */
    public void deletePin(Pin pin) {
        try {
            pinFacade.remove(pin);
        } catch (EJBException e) {
            // TODO: do something
        }
    }
    
    /**
     *
     * @param pin
     * @return
     */
    public String getUsernameFromPin(Pin pin) {
        if (pin == null) return "";
        if (pin.getAnonymous()) return "Anonymous";
        User user = userFacade.findByUserId(pin.getUserId());
        if (user == null) return "";
        String username = user.getFirstName() + " " + user.getLastName();
        pin.setUsername(username);
        return username;
    }
    
    /**
     * Grabs the file name for the pin associated image.
     * @return 
     */
    public String getImageFromPin()
    {
        if (Files.exists(Paths.get(Constants.ROOT_DIRECTORY + "/p_" + selectedPin.getId() + ".png")))
        {
            return "p_" + selectedPin.getId() + ".png";
        }
        else
        {
            return "u_" + selectedPin.getUserId() + ".png";
        }
    }
    
    /**
     *
     * @param pin
     * @return
     */
    public String getImageFromPin(Pin pin)
    {
        if (Files.exists(Paths.get(Constants.ROOT_DIRECTORY + "/p_" + pin.getId() + ".png")))
        {
            return "p_" + pin.getId() + ".png";
        }
        else
        {
            return "u_" + pin.getUserId() + ".png";
        }
    }

    /**
     *
     * @param file
     * @return
     */
    public FacesMessage copyPhotoFile(UploadedFile file) {
        try {
            InputStream in = file.getInputstream();

            File tempFile = inputStreamToFile(in, Constants.TEMP_FILE);
            in.close();

            FacesMessage resultMsg;

            int pinID = pinFacade.findLastID();

            Pin pin = pinFacade.findPinWithId(pinID);

            String extension = file.getContentType();
            extension = extension.startsWith("image/") ? extension.subSequence(6, extension.length()).toString() : "png";

            in = file.getInputstream();
            inputStreamToFile(in, "p_" + pinID + ".png");
            // use uploadedFile if we want to make thumbnails
            resultMsg = new FacesMessage("Success!", "File Successfully Uploaded!");
            return resultMsg;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FacesMessage("Upload failure!",
                "There was a problem reading the image file. Please try again with a new photo file.");
    }
    
    /**
     *
     * @return
     */
    public String assignPinDefaultPhoto()
    {
        String ret = "";
        
        try {
            // Assigns the new name for the user's default photo
            String newNameForPhoto = "p_" +  pinFacade.findLastID() + ".png";

            // Initialize the String array of directory path with picture names
            String[] defaultDirectoryNames = {Constants.ROOT_DIRECTORY + "/default-1.png", 
            Constants.ROOT_DIRECTORY + "/default-2.png", Constants.ROOT_DIRECTORY + "/default-3.png",
            Constants.ROOT_DIRECTORY + "/default-4.png", Constants.ROOT_DIRECTORY + "/default-5.png"};
            
            Random randomProfileDefaultPicture = new Random();
            
            // Grabs the random index of the photo
            int index = randomProfileDefaultPicture.nextInt(defaultDirectoryNames.length);
            
            // Grabs the source of the path file
            Path source = Paths.get(defaultDirectoryNames[index]);
            
            // If the files exist inside the directory
            if (Files.exists(source))
            {
                // Creates a new source path for the file to be created in. 
                // Since we are not using a new directory, we will use the same
                // Root Directory with a new photo name.
                Path newSource = Paths.get(Constants.ROOT_DIRECTORY + "/" + newNameForPhoto);
                ret = newSource.getFileName().toString();
                
                // Copies the photo with the new file name
                Files.copy(source, newSource);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return ret;
    }
    
    /**
     *
     * @return
     */
    public String upvotePin(){
        Pin pinToUpdate = this.getSelectedPin();
        pinToUpdate.setUpvotes(pinToUpdate.getUpvotes()+1);
        pinFacade.edit(pinToUpdate);
        return "";
    }
    
    /**
     *
     * @return
     */
    public String downvotePin(){
        Pin pinToUpdate = this.getSelectedPin();
        pinToUpdate.setDownvotes(pinToUpdate.getDownvotes()+1);
        pinFacade.edit(pinToUpdate);
        return "";
    }

    /**
     *
     * @param pin
     * @return
     */
    public String getFormattedDate(Pin pin) {
        SimpleDateFormat format = new SimpleDateFormat("M/d");
        return format.format(new Date(((long) pin.getTimePosted()) * 1000L));
    }
    
    /**
     *
     * @param pin
     * @return
     */
    public String getFullFormattedDate(Pin pin) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        return format.format(new Date(((long) pin.getTimePosted()) * 1000L));
    }

    /**
     *
     * @return
     */
    public List<Pin> getMapMenuPins() {
        return mapMenuPins;
    }

    /**
     *
     * @param mapMenuPins
     */
    public void setMapMenuPins(List<Pin> mapMenuPins) {
        this.mapMenuPins = mapMenuPins;
    }

    /**
     *
     * @return
     */
    public String getFilterOption() {
        return filterOption;
    }

    /**
     *
     * @param filterOption
     */
    public void setFilterOption(String filterOption) {
        if (filterOption == null || filterOption.isEmpty()) {
            return;
        }
        
        switch (filterOption) {
            case "dist":
                this.filterByDistance();
                this.setDistanceFilterStyle("");
                this.setKeywordFilterStyle(NOT_DISPLAYED);
                this.setKeywordFilterInput("");
                break;
            case "pop":
                this.filterByPopularity();
                this.setDistanceFilterStyle(NOT_DISPLAYED);
                this.setKeywordFilterStyle(NOT_DISPLAYED);
                this.setKeywordFilterInput("");
                break;
            case "new":
                this.filterByNewest();
                this.setDistanceFilterStyle(NOT_DISPLAYED);
                this.setKeywordFilterStyle(NOT_DISPLAYED);
                this.setKeywordFilterInput("");
                break;
            case "key":
                this.filterByKeyword();
                this.setDistanceFilterStyle(NOT_DISPLAYED);
                this.setKeywordFilterStyle("");
            default:
                break;
        }
        this.filterOption = filterOption;
    }

    /**
     *
     * @return
     */
    public String getDistanceFilterStyle() {
        return distanceFilterStyle;
    }

    /**
     *
     * @param distanceFilterStyle
     */
    public void setDistanceFilterStyle(String distanceFilterStyle) {
        this.distanceFilterStyle = distanceFilterStyle;
    }

    /**
     *
     * @return
     */
    public String[] getParsedUserLoc() {
        // Parse out latitiude and longitude from container
        String locData = (String) FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap().get("userLoc");
        // If locData could not be retrieved, stop parsing
        if (locData == null) {
            return null;
        }
        locData = locData.replace("(", "");
        locData = locData.replace(")", "");
        return locData.split(", ");
    }

    /**
     *
     */
    public void filterByKeyword() {

        List<Pin> keywordPins = pinFacade.findAll();
        if (keywordFilterInput == null || keywordFilterInput.isEmpty()) {
            this.setMapMenuPins(keywordPins);
            return;
        }

        // We want to find all pins in MapMenuPins matching the keyword so:
        for (int i = 0; i < keywordPins.size(); i++) {
            Pin pin = keywordPins.get(i);
            if (!(pin.getTitle().toLowerCase().contains(keywordFilterInput.toLowerCase())
                    || pin.getDescription().toLowerCase().contains(keywordFilterInput.toLowerCase()))) {
                keywordPins.remove(pin);
                i--;
            }
        }      
        this.setMapMenuPins(keywordPins);
    }

    /**
     *
     */
    public void filterByDistance() {
        String[] currentUserLoc = this.getParsedUserLoc();
        Double filterDist = Double.parseDouble(this.filterDistance);

        if (currentUserLoc != null) {
            float userLat = Float.valueOf(currentUserLoc[0]);
            float userLong = Float.valueOf(currentUserLoc[1]);
            
            List<Pin> distancePins;
            distancePins = pinFacade.findAll();
            
            ArrayList<Double> distances = new ArrayList<Double>();
            
            for (int i = 0; i < distancePins.size(); i++) {
                Pin pin = distancePins.get(i);
                double distanceInMiles = this.getDistanceFromLatLongInMiles(userLat, userLong,
                        pin.getLatitude(), pin.getLongitude());
                if (!(distanceInMiles <= filterDist)) {
                    distancePins.remove(i);
                    i--;
                } else {
                    distances.add(distanceInMiles);
                }
            }
            
            for (int i = 0; i < distancePins.size(); i++) {
                double max = -Double.MAX_VALUE;
                int maxIndex = -1;
                for (int j = i; j < distancePins.size(); j++) {
                    Pin pin = distancePins.get(j);
                    if (distances.get(j) >= max) {
                        max = distances.get(j);
                        maxIndex = j;
                    }
                }
                if (maxIndex != -1) {
                    Pin pin = distancePins.remove(maxIndex);
                    distancePins.add(0, pin);
                    Double dist = distances.remove(maxIndex);
                    distances.add(0, dist);
                }
            }
            
            this.setMapMenuPins(distancePins);
        }
    }

    /**
     *
     */
    public void filterByNewest() {  
        
        this.sortPins(pinFacade.findAll(), "time");
        this.setMapMenuPins(this.pinValues);
    }

    /**
     *
     */
    public void filterByPopularity() {       

        this.sortPins(pinFacade.findAll(), "popularity");
        this.setMapMenuPins(this.pinValues);
    }
    
    /**
     * Pre populates variables for faster filtering and sets the initial state
     * of the side menu to filter all pins by popularity.
     */
    public void prePopulateMenu() {
        this.filterByPopularity();       
    }

    /**
     * Determines the distance between two geographical coordinates. Heavily
     * based upon the Haversine formula, which accounts for the earth's shape,
     * curvature, average radius etc. Calculated in miles.
     * 
     * @param lat1, latitude from the first point
     * @param long1, longitude from the first point
     * @param lat2, latitude from the second point
     * @param long2, longitude from the second point
     * @return the distance between the two geographical coordinates in miles
     */
    public double getDistanceFromLatLongInMiles(float lat1, float long1,
            float lat2, float long2) {
        double R = 3958.756; // Mean radius of the earth in miles
        double dLat = deg2rad(lat2 - lat1);
        double dLon = deg2rad(long2 - long1);
        double a
                = ((Math.sin(dLat / 2) * Math.sin(dLat / 2))
                + (Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2)));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (R * c); // Distance in miles
    }

    /**
     * Converts degrees to radians. Helper method for 
     * getDistanceFromLatLongInMiles().
     * 
     * @param deg, the 0-360 degree angle
     * @return the degree in radians
     */
    private float deg2rad(float deg) {
        return (float) (deg * (Math.PI / 180));
    }

    /**
     * Quicksort implementation for sorting pins depending on the sort type.
     *
     * @param pins, the list of pins to be sorted
     * @param sortType, a String representation which determines what
     *                  quicksort implementation should be called based on
     *                  the filter
     */
    private void sortPins(List<Pin> pins, String sortType) {
        // check for empty or null array
        if (pins == null || pins.isEmpty()
                || sortType == null || sortType.isEmpty()) {
            return;
        }

        this.pinValues = pins;
        if (sortType.equals("time")) {
            quicksortByTimePosted(0, (pinValues.size()) - 1);
        } else if (sortType.equals("popularity")) {
            quicksortByPopularity(0, (pinValues.size()) - 1);
        }
    }

    /**
     * Quicksort implementation that handles sorting by pin posting time. Pin
     * times are represented as an integer, so sorting orders the lin of pins
     * from lowest to highest integer values.
     * 
     * @param low, low index for sorting
     * @param high, high index for sorting
     */
    private void quicksortByTimePosted(int low, int high) {
        int i = low, j = high;

        int pivot = pinValues.get(low + (high - low) / 2).getTimePosted();

        while (i <= j) {

            while (pinValues.get(i).getTimePosted() > pivot) {
                i++;
            }

            while (pinValues.get(j).getTimePosted() < pivot) {
                j--;
            }

            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }

        if (low < j) {
            quicksortByTimePosted(low, j);
        }
        if (i < high) {
            quicksortByTimePosted(i, high);
        }
    }

    /**
     * Quicksort implementation that handles sorting by pin popularity. 
     * Popularity is determined by subtracting the downvote count from the
     * upvote count
     * 
     * @param low, low index for sorting
     * @param high, high index for sorting
     */
    private void quicksortByPopularity(int low, int high) {
        int i = low, j = high;

        int pivot = pinValues.get(low + (high - low) / 2).getScore();

        while (i <= j) {

            while (pinValues.get(i).getScore() > pivot) {
                i++;
            }

            while (pinValues.get(j).getScore() < pivot) {
                j--;
            }

            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }

        if (low < j) {
            quicksortByPopularity(low, j);
        }
        if (i < high) {
            quicksortByPopularity(i, high);
        }
    }

    /**
     * Generic swap for two Pin objects in a list. Helper method for quicksort
     * implementation.
     *
     * @param i
     * @param j
     */
    private void exchange(int i, int j) {
        Pin temp = pinValues.get(i);
        pinValues.set(i, pinValues.get(j));
        pinValues.set(j, temp);
    }

    /**
     * 
     * @param inputStream
     * @param childName
     * @return
     * @throws IOException 
     */
    private File inputStreamToFile(InputStream inputStream, String childName)
            throws IOException {
        // Read in the series of bytes from the input stream
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);

        // Write the series of bytes on file.
        File targetFile = new File(Constants.ROOT_DIRECTORY, childName);

        OutputStream outStream;
        outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.close();

        // Save reference to the current image.
        return targetFile;
    }
}