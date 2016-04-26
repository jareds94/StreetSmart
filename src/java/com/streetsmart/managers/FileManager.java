/*
 * Created by Hung Vu on 2016.04.07  * 
 * Copyright Â© 2016 Hung Vu. All rights reserved. * 
 */
package com.streetsmart.managers;


import com.streetsmart.sessionbeanpackage.UserFacade;
import com.streetsmart.entitypackage.Photo;
import com.streetsmart.entitypackage.User;
import com.streetsmart.sessionbeanpackage.PhotoFacade;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "fileManager")
@SessionScoped
/**
 * ManagedBean that handles any file uploaded to this application.
 * 
 * @author Kevin
 */
public class FileManager {

    // Instance Variables (Properties)
    private UploadedFile file;
    private String message = "";
    
    /**
     * The instance variable 'userFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject in
     * this instance variable a reference to the @Stateless session bean UserFacade.
     */
    @EJB
    private UserFacade userFacade;

    /**
     * The instance variable 'photoFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject in
     * this instance variable a reference to the @Stateless session bean PhotoFacade.
     */
    @EJB
    private PhotoFacade photoFacade;
    
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

    /**
     * "Profile?faces-redirect=true" asks the web browser to display the
     * Profile.xhtml page and update the URL corresponding to that page.
     * @return Profile.xhtml or nothing
     */
    public String upload() 
    {    
        boolean hasBeenUploaded = false;
        
        if (file.getSize() != 0) {
            hasBeenUploaded = copyFile(file);
            if (hasBeenUploaded)
            {
                return "MyAccount?faces-redirect=true";
            }
            message = "File is not an image, please"
                        + " try uploading again.";
            return "";
        } else {
            message = "You need to upload a file first.";
            return "";
        }
    }
    
    /**
     * Redirects the user to the previous page (MyAccount.xhtml).
     * 
     * @return a String representation of the redirection
     */
    public String cancel() {
        message = "";
        return "MyAccount?faces-redirect=true";
    }
    
    /**
     * Copies the file onto the Photo table as the extension "png" explicitly.
     * This method checks whether the file is an image, if it is then give the
     * file an extension of "png" whether the user has uploaded a gif, jpg, etc.
     * If the file is NOT an image, then just return false and display a meaningful
     * error message onto the dialog box.
     * @param file
     * @return true if the user has successfully uploaded their photo, or false
     * if they have not.
     */
    public boolean copyFile(UploadedFile file) {
        try {
            
            // Deletes the photo from the database first, so we can save space
            deletePhoto();
            
            // Initializes the file input stream for the photo
            InputStream in = file.getInputstream();
            
            // Grabs the tempfile from the folder
            File tempFile = inputStreamToFile(in, Constants.TEMP_FILE);
            in.close();
            
            FacesMessage resultMsg;
            
            // Gets the user's user_name while they are logged on
            String user_name = (String) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("username");
            
            // Grabs the actual user and store it inside the User object
            User user = userFacade.findByUsername(user_name);
            
            // Insert photo record into database
            // First, gets the content type of the file
            String extension = file.getContentType();
            
            // Stores the extension type inside a string variable
            String extensionType = extension.split("/")[0];
            
            // Checks whether the file being uploaded is an image or not
            if (extensionType.equals("image"))
            {
                // If it is an image, then force the extension to be a png
                extension = "png";
                
                // Grabs the list of photos that are from the logged in user
                List<Photo> photoList = photoFacade.findPhotosByUserID(user.getId());
                if (!photoList.isEmpty()) {
                    // Deletes the photo from the list if it is not emptied
                    photoFacade.remove(photoList.get(0));
                }
                
                // Creates a new photo instance based on the user id and the file extension
                photoFacade.create(new Photo(extension, user));
                
                // Finds the photo that was just created
                Photo photo = photoFacade.findPhotosByUserID(user.getId()).get(0);
                
                // Places that file with the correct file name inside of the 
                // StreetSmartPhotoStorage folder
                in = file.getInputstream();
                inputStreamToFile(in, photo.getFilename());
                
                return true;
            }
            else
            {
                return false;
            }
           
        } catch (IOException e) {
            e.printStackTrace();
        }
     
        return false;
    }

    /**
     * Finds the photo file and streams it.
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

//    private void saveThumbnail(File inputFile, Photo inputPhoto) {
//        try {
//            BufferedImage original = ImageIO.read(inputFile);
//            //BufferedImage thumbnail = Scalr.resize(original, Constants.THUMBNAIL_SZ);
//            //ImageIO.write(thumbnail, inputPhoto.getExtension(),
//               // new File(Constants.ROOT_DIRECTORY, inputPhoto.getThumbnailName()));
//        } catch (IOException ex) {
//            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    /**
     * Deletes the photo for the currently logged in User.
     */
    public void deletePhoto() {
        FacesMessage resultMsg;
        // Acquire the username from the session map
        String user_name = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");

        // Find the user from the User's table in the StreetSmart database
        User user = userFacade.findByUsername(user_name);

        // Find all photos associated with this user
        List<Photo> photoList = photoFacade.findPhotosByUserID(user.getId());
        if (photoList.isEmpty()) {
            resultMsg = new FacesMessage("Error", "You do not have a photo to delete.");
        } else {
            Photo photo = photoList.get(0);
            // Attempt deletion
            try {
                Files.deleteIfExists(Paths.get(photo.getFilePath()));
                Files.deleteIfExists(Paths.get(photo.getThumbnailFilePath()));
                
                Files.deleteIfExists(Paths.get(Constants.ROOT_DIRECTORY+"tmp_file"));
                 
                photoFacade.remove(photo);
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // Displayed if the photo was successfully deleted
            resultMsg = new FacesMessage("Success", "Photo successfully deleted!");
        }
        // Output the result message, contents depending on success or failure
        // to delete the photo
        FacesContext.getCurrentInstance().addMessage(null, resultMsg);
    }
    
}
