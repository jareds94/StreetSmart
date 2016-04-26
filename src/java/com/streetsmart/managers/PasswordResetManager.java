/*
 * Created by Hung Vu on 2016.04.07  * 
 * Copyright Â© 2016 Hung Vu. All rights reserved. * 
 */
package com.streetsmart.managers;

import com.streetsmart.entitypackage.User;
import com.streetsmart.sessionbeanpackage.UserFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Named;

@Named(value = "passwordResetManager")
@SessionScoped
/**
 *
 * @author Balci
 */
public class PasswordResetManager implements Serializable{
    
    // Instance Variables (Properties)
    private String username;
    private String message = "";
    private String answer;
    private String password;
    
    /**
     * The instance variable 'userFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject in
     * this instance variable a reference to the @Stateless session bean UserFacade.
     */
    @EJB
    private UserFacade userFacade;

    /**
     * Retrieves the username.
     * 
     * @return a String representation of the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * 
     * @param username, the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the message.
     * 
     * @return a String representation of the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message String.
     * 
     * @param message, the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
     
    /**
     * Redirects to ask the user to answer a security question to reset his
     * or her password depending on whether or not the entered username
     * exists in the Customer table in the OrderPizzaDB.
     * 
     * @return a String representation of a redirection to an xhtml page,
     *         depending on success or not
     */
    public String usernameSubmit() {
        User user = userFacade.findByUsername(username);
        if (user == null) {
            message = "Entered username does not exist!";
            return "EnterUsername?faces-redirect=true";
        }
        else {
            message = "";
            return "SecurityQuestion?faces-redirect=true";
        }
    }
    
    /**
     * 
     * @return 
     */
    public String securityquestionSubmit() {
        User user = userFacade.findByUsername(username);
        if (user.getSecurityAnswer().equals(answer)) {
            message = "";
            return "ResetPassword?faces-redirect=true";
        }
        else {
            message = "Answer incorrect";
            return "SecurityQuestion?faces-redirect=true";
        }
    }
    
    /**
     * Gets the user's security question.
     * 
     * @return a String representation of the user's security question
     */
    public String getSecurityQuestion() {
        int question = userFacade.findByUsername(username).getSecurityQuestion();
        return Constants.QUESTIONS[question];
    }

    /**
     * Gets the user's security answer.
     * 
     * @return a String representation of the security answer 
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Sets the security question answer.
     * 
     * @param answer, the answer to set
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Validates the current user's information to allow for a password change.
     * 
     * @param event, the ComponentSystemEvent
     */
    public void validateInformation(ComponentSystemEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();

        UIComponent components = event.getComponent();
        // get password
        UIInput uiInputPassword = (UIInput) components.findComponent("password");
        String pwd = uiInputPassword.getLocalValue() == null ? ""
                : uiInputPassword.getLocalValue().toString();

        // get confirm password
        UIInput uiInputConfirmPassword = (UIInput) components.findComponent("confirm-password");
        String confirmPassword = uiInputConfirmPassword.getLocalValue() == null ? ""
                : uiInputConfirmPassword.getLocalValue().toString();

        if (pwd.isEmpty() || confirmPassword.isEmpty()) {
            // Do not take any action. 
            // The required="true" in the XHTML file will catch this and produce an error message.
            message = "Please enter a new password!";
            return;
        }

        if (!pwd.equals(confirmPassword)) {
            message = "Passwords must match!";
        } else {
            message = "";
        }
        
        if (pwd.length() < 4) {
            message = "Password must be at least 4 characters.";
        }
    }   

    /**
     * Retrieves the user's password.
     * 
     * @return a String representation of the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     * 
     * @param password, the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Handles reseting the user's password.
     * 
     * @return a String representation of the homepage's xhtml name (index) on 
     *           success, a String representation of the ResetPassword xhtml
     *           page
     */
    public String resetPassword() {
        if (message.equals("")) {
            message = "";
            User user = userFacade.findByUsername(username);
            try {
                user.setPassword(password);
                userFacade.edit(user);
                username = answer = password = "";                
            } catch (EJBException e) {
                message = "Something went wrong editing your profile, please try again!";
                return "ResetPassword?faces-redirect=true";            
            }
            return "index?faces-redirect=true";            
        }
        else {
            return "ResetPassword?faces-redirect=true";            
        }
    }
            
}
