/*
 * Created by Hung Vu on 2016.04.07  * 
 * Copyright Â© 2016 Hung Vu. All rights reserved. * 
 */
package com.streetsmart.managers;

import com.streetsmart.entitypackage.User;
import com.streetsmart.sessionbeanpackage.UserFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "loginManager")
@SessionScoped
/**
 *  ManagedBean that handles logging in the User.
 * 
 * @author Kevin
 */
public class LoginManager implements Serializable {

  private String username;
  private String password;
  private String errorMessage;
  
  /**
   * The instance variable 'userFacade' is annotated with the @EJB annotation.
   * This means that the GlassFish application server, at runtime, will inject in
   * this instance variable a reference to the @Stateless session bean UserFacade.
   */
  @EJB
  private UserFacade userFacade;

  /**
   * Creates a new instance of LoginManager
   */
  public LoginManager() {
  }

  /**
   * @return the user
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the user to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return a String representation of the xhtml file to be redirected to
   *         (CreateAccount.xhtml) 
   */
  public String createUser() {
    return "CreateAccount";
  }
  
  /**
   * 
   * @return a String representation of the xhtml file to be redirected to
   *         (EnterUsername.xhtml)
   */
  public String resetPassword() {
      return "EnterUsername?faces-redirect=true";
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
   * @return the errorMessage
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * @param errorMessage the errorMessage to set
   */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * Logs in the specified User.
   * 
   * @return a String redirection based on whether or not the user was logged
   *         in
   */
  public String loginUser() {
    User user = userFacade.findByUsername(getUsername());
    if (user == null) {
      errorMessage = "Invalid username or password!";
      return "";
    } else {
      if (user.getUsername().equals(getUsername()) && user.getPassword().equals(getPassword())) {
        errorMessage = "";
        initializeSessionMap(user);
        return "index?faces-redirect=true";
      }
      errorMessage = "Invalid username or password!";
      return "";
    }
  }

  /**
     * Initializes the session map according to which Customer logged into
     * which account under a username. These properties are used within
     * PinManager and CommentManager to retrieve the currently logged in
     * user's information.
     * 
     * @param user, the currently logged in Customer
     */
  public void initializeSessionMap(User user) {
    FacesContext.getCurrentInstance().getExternalContext().
            getSessionMap().put("first_name", user.getFirstName());
    FacesContext.getCurrentInstance().getExternalContext().
            getSessionMap().put("last_name", user.getLastName());
    FacesContext.getCurrentInstance().getExternalContext().
            getSessionMap().put("username", username);
    FacesContext.getCurrentInstance().getExternalContext().
            getSessionMap().put("user_id", user.getId());
  }
}