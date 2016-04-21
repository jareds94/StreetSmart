/*
 * Created by Timothy Street on 2016.04.12  * 
 * Copyright © 2016 Timothy Street. All rights reserved. * 
 */
package com.streetsmart.managers;

import com.streetsmart.entitypackage.Pin;
import com.streetsmart.entitypackage.User;
import com.streetsmart.sessionbeanpackage.PinFacade;
import com.streetsmart.sessionbeanpackage.UserFacade;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import javax.faces.application.FacesMessage;
import org.primefaces.model.UploadedFile;

@Named(value = "pinManager")
@SessionScoped
/**
 *
 * @author Tim
 */
public class PinManager implements Serializable {

    // Instance Variables (Properties) for Pins 
    private UploadedFile file;
    private String message = "";
    private String pinTitle;
    private String pinDescription;
    private boolean pinAnonymous;
    private boolean pinPhotoExists;
    private User selected;

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

    }

    public PinFacade getPinFacade() {
        return pinFacade;
    }

    public void setPinFacade(PinFacade pinFacade) {
        this.pinFacade = pinFacade;
    }
    
        // Returns the uploaded file
    public UploadedFile getFile() {
        return file;
    }

    // Obtains the uploaded file
    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
        // Returns the message
    public String getMessage() {
        return message;
    }

    // Obtains the message
    public void setMessage(String message) {
        this.message = message;
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
        // Parse out latitiude and longitude from container
        String locData = (String)FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap().get("userLoc");
        locData = locData.replace("(", "");
        locData = locData.replace(")", "");
        String[] latAndLong = locData.split(", ");

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
            if (file.getSize() != 0){
                pin.setPhoto(true);
                copyPhotoFile(file);
            }
            else {
                pin.setPhoto(false);
            }
            return "index?faces-redirect=true";
        } catch (EJBException e) {
            //statusMessage = "Something went wrong while creating your pin!";
        }
        return "";
    }
    
    
    public FacesMessage copyPhotoFile(UploadedFile file){
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
            File uploadedFile = inputStreamToFile(in, pinID + ".png");
            // use uploadedFile if we want to make thumbnails
            resultMsg = new FacesMessage("Success!", "File Successfully Uploaded!");
            return resultMsg;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FacesMessage("Upload failure!",
            "There was a problem reading the image file. Please try again with a new photo file.");
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
