/*
 * Created by Hung Vu on 2016.04.07  * 
 * Copyright Â© 2016 Hung Vu. All rights reserved. * 
 */

package com.streetsmart.managers;
import com.streetsmart.entitypackage.Photo;
import com.streetsmart.entitypackage.Pin;
import com.streetsmart.entitypackage.User;
import com.streetsmart.sessionbeanpackage.PhotoFacade;
import com.streetsmart.sessionbeanpackage.PinFacade;
import com.streetsmart.sessionbeanpackage.UserFacade;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.primefaces.model.UploadedFile;
 
@Named(value = "accountManager")
@SessionScoped
/**
 *
 * @author Hung
 */
public class AccountManager implements Serializable {
 
    // Instance Variables (Properties) for Users 
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private int security_question;
    private String security_answer;
    private String statusMessage;
    private String userLoc;
        
    private Map<String, Object> security_questions;
    
    private User selected;
    
    private List<Pin> postedPins;
    
    /**
     * The instance variable 'userFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject in
     * this instance variable a reference to the @Stateless session bean UserFacade.
     */
    @EJB
    private UserFacade userFacade;
    
    /**
     * The instance variable 'pinFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject in
     * this instance variable a reference to the @Stateless session bean UserFacade.
     */
    @EJB
    private PinFacade pinFacade;
    
     /**
     * The instance variable 'photoFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject in
     * this instance variable a reference to the @Stateless session bean PhotoFacade.
     */
    @EJB
    private PhotoFacade photoFacade;
    
    private float userLat;
    private float userLong;

    /**
     * Creates a new instance of AccountManager
     */
    public AccountManager() {
        statusMessage = "";
    }

    /**
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public int getSecurity_question() {
        return security_question;
    }

    public void setSecurity_question(int security_question) {
        this.security_question = security_question;
    }

    public String getSecurity_answer() {
        return security_answer;
    }

    public void setSecurity_answer(String security_answer) {
        this.security_answer = security_answer;
    }

    public Map<String, Object> getSecurity_questions() {
        if (security_questions == null) {
            security_questions = new LinkedHashMap<>();
            for (int i = 0; i < Constants.QUESTIONS.length; i++) {
                security_questions.put(Constants.QUESTIONS[i], i);
            }
        }
        return security_questions;
    }
    
    /**
     * @return the statusMessage
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @param statusMessage the statusMessage to set
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public User getSelected() {
        if (selected == null) {
            selected = userFacade.find(FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap().get("user_id"));
        }
        
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }
    
    public String getUserLoc() {
        return this.userLoc;
    }
    
    public void setUserLoc(String userLoc) {
        this.userLoc = userLoc;
        FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put("userLoc", this.userLoc);
        
        String location;
        String[] temp;
        location = userLoc.replace("(", "");
        location = location.replace(")", "");
        temp = location.split(", ");               
        this.setUserLat(Float.parseFloat(temp[0]));
        this.setUserLong(Float.parseFloat(temp[1]));
    }
    
    public List<Pin> getPostedPins() {
        if (this.isLoggedIn()) {
            return pinFacade.findAllPinsWithUserId(this.getSelected().getId());
        }
        return null;
    }

    public String createAccount() {
        
        // Check to see if a user already exists with the username given.
        User aUser = userFacade.findByUsername(username);

        if (aUser != null) {
            username = "";
            statusMessage = "Username already exists! Please select a different one!";
            return "";
        }

        if (statusMessage.isEmpty()) {
            try {
                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);                
                user.setSecurityQuestion(security_question);
                user.setSecurityAnswer(security_answer);
                user.setEmail(email);
                user.setUsername(username);                
                user.setPassword(password);
                userFacade.create(user); 
                sendEmail();
                assignUserDefaultPhoto(user.getId());
                 
            } catch (EJBException e) {
                username = "";
                statusMessage = "Something went wrong while creating your account!";
                return "";
            } catch (MessagingException ex) {
                Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            initializeSessionMap();
            return "index?faces-redirect=true";
        }
        return "";
    }
    
    public String assignUserDefaultPhoto(int user_id)
    {
        String ret = "";
        
        try {
            // Assigns the new name for the user's default photo
            String newNameForPhoto = "u_" + user_id + ".png";

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

    public String updateAccount() {
        if (statusMessage.isEmpty()) {
            int user_id = (int) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user_id");
                User editUser = userFacade.getUser(user_id);
            try {
                editUser.setFirstName(this.selected.getFirstName());
                editUser.setLastName(this.selected.getLastName());            
                editUser.setEmail(this.selected.getEmail());
                editUser.setPassword(this.selected.getPassword());
                userFacade.edit(editUser);
            } catch (EJBException e) {
                username = "";
                statusMessage = "Something went wrong while editing your profile!";
                return "";
            }
            return "MyAccount";
        }
        return "MyAccount";
    }
    
    public String deleteAccount() {
        if (statusMessage.isEmpty()) {
            int user_id = (int) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user_id");
            try {
                this.logout();
                userFacade.deleteUser(user_id);    
            } catch (EJBException e) {
                username = "";
                statusMessage = "Something went wrong while deleting your account!";
                return "";
            }
            return "/index.xhtml?faces-redirect=true";
        }
        return "";
    }
    
    public void validateInformation(ComponentSystemEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();

        UIComponent components = event.getComponent();
        // Get password
        UIInput uiInputPassword = (UIInput) components.findComponent("password");
        String pwd = uiInputPassword.getLocalValue() == null ? ""
                : uiInputPassword.getLocalValue().toString();

        // Get confirm password
        UIInput uiInputConfirmPassword = (UIInput) components.findComponent("confirm-password");
        String confirmPassword = uiInputConfirmPassword.getLocalValue() == null ? ""
                : uiInputConfirmPassword.getLocalValue().toString();

        if (pwd.isEmpty() || confirmPassword.isEmpty()) {
            // Do not take any action. 
            // The required="true" in the XHTML file will catch this and produce an error message.
            return;
        }

        if (!pwd.equals(confirmPassword)) {
            statusMessage = "Passwords must match!";
        } else {
            statusMessage = "";
        }   
    }

    public void initializeSessionMap() {
        User user = userFacade.findByUsername(getUsername());
        FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put("username", username);
        FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put("user_id", user.getId());
        // Retrieve all pins postings associated with this user
        postedPins = this.getPostedPins();
    }

    private boolean correctPasswordEntered(UIComponent components) {
        UIInput uiInputVerifyPassword = (UIInput) components.findComponent("verifyPassword");
        String verifyPassword = uiInputVerifyPassword.getLocalValue() == null ? ""
                : uiInputVerifyPassword.getLocalValue().toString();
        if (verifyPassword.isEmpty()) {
            statusMessage = "";
            return false;
        } else {
            if (verifyPassword.equals(password)) {
                return true;
            } else {
                statusMessage = "Invalid password entered!";
                return false;
            }
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
        username = firstName = lastName = password = email = statusMessage = "";
        security_answer = "";
        security_question = 0;
        
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml?faces-redirect=true";
    }
    /* Added methods */
        
    /* Check session map for username to see if anyone is logged in */
    public boolean isLoggedIn() {
        return FacesContext.getCurrentInstance().getExternalContext().
               getSessionMap().get("username") != null;
    } 
      
    
    public String userPhoto() {
        String user_name = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");
        User user = userFacade.findByUsername(user_name);
        List<Photo> photoList = photoFacade.findPhotosByUserID(user.getId());
        if (photoList.isEmpty()) {
            return assignUserDefaultPhoto(user.getId());
        }
        //return photoList.get(0).getThumbnailName();
        return photoList.get(0).getFilename();
    }

    public double getUserLat() {
        return userLat;
    }

    public void setUserLat(float userLat) {
        this.userLat = userLat;
    }

    public double getUserLong() {
        return userLong;
    }

    public void setUserLong(float userLong) {
        this.userLong = userLong;
    }
    
   public void sendEmail() throws MessagingException, IOException {
       
        String body = emailToString();
        String host = "smtp.gmail.com";
        String user = "streetsmartservice@gmail.com";
        String pass = "StreetSmart";
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", user);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props, null);


        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("streetsmartservice@gmail.com", "StreetSmart"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(email, firstName));
            msg.setSubject(firstName + ", thanks for joining StreetSmart!");
            msg.setText(body);
            Transport.send(msg, user, pass);

        } catch (AddressException e) {
            // ...
        } catch (MessagingException e) {
            // ...
        }

    }
   
   public String emailToString(){
       
       StringBuilder sb = new StringBuilder();
       sb.append("Welcome to StreetSmart, ");
       sb.append(firstName);
       sb.append("!\n\n");
       
       sb.append("StreetSmart is your one stop location for everything that's going on around you. ");
       sb.append("With an account at StreetSmart, you can create a pin at anywhere in the world for a concert you attended,");
       sb.append("a restaurant that you loved, or anything else you think people around the world should know about. ");
       sb.append("You'll also be to comment, upvote or downvote on other user's pins if you agree or disagree with their pin. ");
       sb.append("We hope you enjoy StreetSmart! If you have questions, comments or concerns, please feel free to contact us at streetsmartservice@gmail.com.");
       
       return sb.toString();
       
       
       
   }
    
    
}