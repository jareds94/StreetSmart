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
 * This class handles all interactions between the frontend and backend with pin
 * objects. Primarily handles pin creation and what is displayed on the side
 * menu depending on what filter type is selected.
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
     * PinFacade. Handles all interactions with the Pin table in the StreetSmart
     * database.
     */
    @EJB
    private PinFacade pinFacade;

    /**
     * The instance variable 'photoFacade' is annotated with the @EJB
     * annotation. This means that the GlassFish application server, at runtime,
     * will inject in this instance variable a reference to the @Stateless
     * session bean PhotoFacade.
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
     * Retrieves whether or not the pin should be posted as anonymous for the
     * pin to be posted.
     *
     * @return whether or not the pin should be posted as anonymous
     */
    public boolean isNewPinAnonymous() {
        return newPinAnonymous;
    }

    /**
     * Sets whether or not the pin should be posted as anonymous for the pin to
     * be posted.
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
     * the photo was uploaded
     */
    public void setNewPinPhotoExists(boolean newPinPhotoExists) {
        this.newPinPhotoExists = newPinPhotoExists;
    }

    /**
     * Retrieves the filter distance mapped to the input distance field when
     * filtering by distance in index.xhtml.
     *
     * @return the filter distance represented as a String
     */
    public String getFilterDistance() {
        return filterDistance;
    }

    /**
     * Sets the filter distance mapped to the input distance field when
     * filtering by distance in index.xhtml.
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
            } else // Otherwise, depending on anonymity, set the created pin to
            // a default user profile picture from resources
            if (pin.getAnonymous()) {
                pin.setPhoto(true);
                pinFacade.edit(pin);
                assignPinDefaultPhoto();
            } else {
                pin.setPhoto(false);
                pinFacade.edit(pin);
            }
            // Reset all globals for the next pin posting
            this.newPinTitle = "";
            this.newPinDescription = "";
            this.newPinPhotoExists = false;
            this.newPinAnonymous = false;
            // Redirect to the index
            return "index?faces-redirect=true&id=" + pin.getId();
        } catch (EJBException e) {
            return "";
        }
    }

    /**
     * Deletes a pin from the Pin table in the StreetSmartDB.
     *
     * @param pin, the Pin to be removed.
     */
    public void deletePin(Pin pin) {
        try {
            // Call to PinFacade which calls SQL DELETE statement.
            pinFacade.remove(pin);
        } catch (EJBException e) {
            return;
        }
    }

    /**
     * Retrieves the username for an associated pin posting.
     *
     * @param pin, the pin of interest
     * @return the username associated with the pin
     */
    public String getUsernameFromPin(Pin pin) {
        // If the pin DNE, do nothing
        if (pin == null) {
            return "";
        }
        // If the pin is anonymous, dont return a username
        if (pin.getAnonymous()) {
            return "Anonymous";
        }
        // Use UserFacade to query the Pin table and find the associated user
        User user = userFacade.findByUserId(pin.getUserId());
        if (user == null) {
            return "";
        }
        // Format the user's first name and last name into one String 
        // and return it
        String username = user.getFirstName() + " " + user.getLastName();
        // Set the pin's username
        pin.setUsername(username);
        return username;
    }

    /**
     * Grabs the file name for the pin's associated image.
     *
     * @return the filename of the image, including type
     */
    public String getImageFromPin() {
        if (Files.exists(Paths.get(Constants.ROOT_DIRECTORY + "/p_" + selectedPin.getId() + ".png"))) {
            return "p_" + selectedPin.getId() + ".png";
        } else {
            return "u_" + selectedPin.getUserId() + ".png";
        }
    }

    /**
     * Retrieves the image associated with a pin.
     * 
     * @param pin, the Pin to retrieve the image from
     * @return the filename of the image, including type
     */
    public String getImageFromPin(Pin pin) {
        if (Files.exists(Paths.get(Constants.ROOT_DIRECTORY + "/p_" + pin.getId() + ".png"))) {
            return "p_" + pin.getId() + ".png";
        } else {
            return "u_" + pin.getUserId() + ".png";
        }
    }

    /**
     * Handles uploading a photo from a pin posting.
     * 
     * @param file, the file to be uploaded to the the StreetSmartDB
     * @return
     */
    public FacesMessage copyPhotoFile(UploadedFile file) {
        try {
            // Initialize the input stream
            InputStream in = file.getInputstream();

            File tempFile = inputStreamToFile(in, Constants.TEMP_FILE);
            in.close();

            FacesMessage resultMsg;

            // Retrieves the Id of the most recently added pin
            int pinID = pinFacade.findLastID();

            // Finds the pin with the associated pin Id
            Pin pin = pinFacade.findPinWithId(pinID);

            // Find the image based on extension
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
     * Assigns a default photo to a Pin if the user did not upload one.
     * 
     * @return the filename represented as a String.
     */
    public String assignPinDefaultPhoto() {
        String ret = "";

        try {
            // Assigns the new name for the user's default photo
            String newNameForPhoto = "p_" + pinFacade.findLastID() + ".png";

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
            if (Files.exists(source)) {
                // Creates a new source path for the file to be created in. 
                // Since we are not using a new directory, we will use the same
                // Root Directory with a new photo name.
                Path newSource = Paths.get(Constants.ROOT_DIRECTORY + "/" + newNameForPhoto);
                ret = newSource.getFileName().toString();

                // Copies the photo with the new file name
                Files.copy(source, newSource);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Handles upvoting the currently selected Pin. Increments the score in
     * the Pin table accordingly.
     * 
     * @return the navigation outcome
     */
    public String upvotePin() {
        // Retrieve the currently selected pin
        Pin pinToUpdate = this.getSelectedPin();
        // Set the upvotes on the current pin
        pinToUpdate.setUpvotes(pinToUpdate.getUpvotes() + 1);
        // Edit the table entry accordingly in the database
        pinFacade.edit(pinToUpdate);
        // No navigation on action
        return "";
    }

    /**
     * Handles downvoting the currently selected Pin. Decrements the score in
     * the Pin table accordingly.
     * 
     * @return the navigation outcome
     */
    public String downvotePin() {
        // Retrieve the currently selected Pin
        Pin pinToUpdate = this.getSelectedPin();
        // Set the downvotes on the current Pin
        pinToUpdate.setDownvotes(pinToUpdate.getDownvotes() + 1);
        // Edit the table entry accordingly in the database
        pinFacade.edit(pinToUpdate);
        // No navigation on action
        return "";
    }

    /**
     * Retrieves the date in a formatted manner from the database for the
     * associated pin. Used for dating pin postings.
     * 
     * @param pin, the pin to retrieve the date from
     * @return a String representation of the date, delimited as 
     *           "MM/dd"
     */
    public String getFormattedDate(Pin pin) {
        // Set the formatting for the date
        SimpleDateFormat format = new SimpleDateFormat("M/d");
        // Convert the database Integer to a Date object and return it
        return format.format(new Date(((long) pin.getTimePosted()) * 1000L));
    }

    /**
     * Retrieves the date in a formatted manner from the database for the
     * associated pin. Used for dating comments and various other things.
     * 
     * @param pin
     * @return a String representation of the date, delimited as
     *         "MM/dd/yyyy HH:mm"
     */
    public String getFullFormattedDate(Pin pin) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy h:mm a");
        return format.format(new Date(((long) pin.getTimePosted()) * 1000L));
    }

    /**
     * Retrieves the list of Pins that are displayed on the side menu bar.
     * 
     * @return a list of pins
     */
    public List<Pin> getMapMenuPins() {
        return mapMenuPins;
    }

    /**
     * Sets the list of pins that are displayed on the side menu bar.
     * 
     * @param mapMenuPins, the list of pins to be set
     */
    public void setMapMenuPins(List<Pin> mapMenuPins) {
        this.mapMenuPins = mapMenuPins;
    }

    /**
     * Gets the current filter option. Determined by which dropdown selection
     * item is selected on the side menu (keyword, distance, popularity, new)
     * 
     * @return the filter option formatted as a String
     */
    public String getFilterOption() {
        return filterOption;
    }

    /**
     *
     * Sets the current filter option and updates the display of index.xhtml
     * accordingly.
     * 
     * @param filterOption, the filter option depending on which dropdown
     *                      menu item is selected
     */
    public void setFilterOption(String filterOption) {
        
        // If the filter option is empty or nonexistant, do nothing
        if (filterOption == null || filterOption.isEmpty()) {
            return;
        }

        // Depending on the filter option, hide or display the keyword input
        // field, distance input field. Also sort the mapMenuList depending
        // on which filter type is selected.
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
        // Lastly, set the current filter option for later use
        this.filterOption = filterOption;
    }

    /**
     * Retrieves the filter style for the distance form element.
     * 
     * @return a String representation of the filter style
     */
    public String getDistanceFilterStyle() {
        return distanceFilterStyle;
    }

    /**
     * Sets the filter style for the distance form element.
     * 
     * @param distanceFilterStyle, the filter style String to be set
     */
    public void setDistanceFilterStyle(String distanceFilterStyle) {
        this.distanceFilterStyle = distanceFilterStyle;
    }

    /**
     * Retrieve the user location from the hidden xhtml element "userLoc",
     * parse it, and then return it.
     * 
     * @return a String array where the first index is the user's latitude
     *         and the second index is the user's longitude
     */
    public String[] getParsedUserLoc() {
        // Parse out latitiude and longitude from container
        String locData = (String) FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap().get("userLoc");
        // If locData could not be retrieved, stop parsing
        if (locData == null) {
            return null;
        }
        // Parse out extra characters to retrieve a clean representation
        // of the user's location
        locData = locData.replace("(", "");
        locData = locData.replace(")", "");
        return locData.split(", ");
    }

    /**
     * Handles filtering the mapMenuList by keyword. The keyword comes as 
     * input from the keyword input field and is stored in the instance field
     * "keywordFilterInput".
     */
    public void filterByKeyword() {
        
        // Retrieve all pins from the database
        List<Pin> keywordPins = pinFacade.findAll();
        // If no keyword is entered, set the mapMenuList as an unfiltered
        // list of pins from the database
        if (keywordFilterInput == null || keywordFilterInput.isEmpty()) {
            this.setMapMenuPins(keywordPins);
            return;
        }

        // We want to find all pins in MapMenuPins matching the keyword so:
        // Iterate over the list removing any pins that do not contain the
        // keyword in either the title of description of the pin.
        for (int i = 0; i < keywordPins.size(); i++) {
            Pin pin = keywordPins.get(i);
            if (!(pin.getTitle().toLowerCase().contains(keywordFilterInput.toLowerCase())
                    || pin.getDescription().toLowerCase().contains(keywordFilterInput.toLowerCase()))) {
                keywordPins.remove(pin);
                // Decrement the counter after removing an element from the list
                // to retain the proper list index
                i--;
            }
        }
        // Lastly, set the mapManuPins to the filtered list keywordPins
        this.setMapMenuPins(keywordPins);
    }

    /**
     * Handles filtering the mapMenuList by distance. The filter distance
     * depends on what the user inputs into the distance input text in the
     * side menu.
     */
    public void filterByDistance() {
        // Retrieve the parsed user location
        String[] currentUserLoc = this.getParsedUserLoc();
        // Parse the input filter distance as a double
        Double filterDist = Double.parseDouble(this.filterDistance);

        // If the user's current location is not null, proceed
        if (currentUserLoc != null) {
            // Extract the latitude and longitude of the user's location
            // from the currentUserLoc String array
            float userLat = Float.valueOf(currentUserLoc[0]);
            float userLong = Float.valueOf(currentUserLoc[1]);

            // Query the Pin table and retrieve all pins
            List<Pin> distancePins;
            distancePins = pinFacade.findAll();

            // Initialize a new arrayList of doubles which will hold the
            // distances of select pins
            ArrayList<Double> distances = new ArrayList<Double>();

            // Iterate over the distancePins list and remove any pin that 
            // exceeds the filter distance's distance
            for (int i = 0; i < distancePins.size(); i++) {
                Pin pin = distancePins.get(i);
                // Calculate the distance from the user's current location
                // to the current pin in the list
                double distanceInMiles = this.getDistanceFromLatLongInMiles(userLat, userLong,
                        pin.getLatitude(), pin.getLongitude());
                // If the pin's distance from the user's location exceeds the
                // the filter, remove it from the list
                if (!(distanceInMiles <= filterDist)) {
                    distancePins.remove(i);
                    i--;
                } else {
                    // Otherwise, add the distance to the distances array list
                    distances.add(distanceInMiles);
                }
            }

            // Selection sort implementation to sort the list by distance
            // The outer for-loop keeps track of the current element. Each
            // iteration potentially swaps the current element with another
            // element in the list of lesser value.
            for (int i = 0; i < distancePins.size(); i++) {
                double max = -Double.MAX_VALUE;
                int maxIndex = -1;
                // The inner for loop handles finding the minimal value 
                for (int j = i; j < distancePins.size(); j++) {
                    Pin pin = distancePins.get(j);
                    if (distances.get(j) >= max) {
                        max = distances.get(j);
                        maxIndex = j;
                    }
                }
                
                // Swap the elements if a max index was found
                if (maxIndex != -1) {
                    Pin pin = distancePins.remove(maxIndex);
                    distancePins.add(0, pin);
                    Double dist = distances.remove(maxIndex);
                    distances.add(0, dist);
                }
            }
            
            // Lastly set the mapMenuPins to the distancePin list
            this.setMapMenuPins(distancePins);
        }
    }

    /**
     * Filters the mapMenuList by newest (lowest integer values from the
     * date column in the Pin table first). Calls an implementation of
     * quicksort to handle the sorting.
     */
    public void filterByNewest() {

        this.sortPins(pinFacade.findAll(), "time");
        this.setMapMenuPins(this.pinValues);
    }

    /**
     * Filters the mapMenuList by popularity (i.e. highest score, which is
     * upvotes - downvotes). Calls an implementation of quicksort to handle
     * the sorting.
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
     * @param sortType, a String representation which determines what quicksort
     * implementation should be called based on the filter
     */
    private void sortPins(List<Pin> pins, String sortType) {
        // check for empty or null array
        if (pins == null || pins.isEmpty()
                || sortType == null || sortType.isEmpty()) {
            return;
        }

        // Call the associated quicksort implementation depending on the input
        // sortType
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
     * Reads in an image to be uploaded.
     * 
     * @param inputStream, the input stream
     * @param childName, the childName
     * @return the File
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