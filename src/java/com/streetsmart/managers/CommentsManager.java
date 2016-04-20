/*
 * Created by Mukund Katti on 2016.04.19  * 
 * Copyright © 2016 Mukund Katti. All rights reserved. * 
 */
package com.streetsmart.managers;

import com.streetsmart.entitypackage.Comments;
import com.streetsmart.entitypackage.Pin;
import com.streetsmart.entitypackage.User;
import com.streetsmart.sessionbeanpackage.CommentsFacade;
import com.streetsmart.sessionbeanpackage.PinFacade;
import com.streetsmart.sessionbeanpackage.UserFacade;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named(value = "commmentsManager")
@SessionScoped
/**
 *
 * @author kattim
 */
public class CommentsManager implements Serializable {
    
    private int pin_id;
    private int user_id;
    private int username;
    private String comment;
    private int timePosted;
    private Pin selectedPin;
    private User selectedUser;
    
    
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
    
    /**
     * The instance variable 'pinFacade' is annotated with the @EJB annotation.
     * This means that the GlassFish application server, at runtime, will inject
     * in this instance variable a reference to the @Stateless session bean
     * PhotoFacade.
     */
    @EJB
    private CommentsFacade commentsFacade;
    
    public CommentsManager(){
        
    }
    
    public CommentsFacade getCommmentsFacade() {
        return commentsFacade;
    }

    public void setCommentsFacade(CommentsFacade commentsFacade) {
        this.commentsFacade = commentsFacade;
    }
    
    public User getSelectedUser() {
        if (selectedUser == null) {
            selectedUser = userFacade.find(FacesContext.getCurrentInstance().
                    getExternalContext().getSessionMap().get("user_id"));
        }

        return selectedUser;
    }
    
    public Pin getSelectedPin() {
        if (selectedPin == null){
            selectedPin = pinFacade.find(FacesContext.getCurrentInstance().
                    getExternalContext().getSessionMap().get("pin_id"));
        }
        return selectedPin;
    }
    
    public void setComment(String comment){
        this.comment = comment;
    }
    
    public String getComment(){
        return comment;
    }
    
    public String createComment(){
        
        int timestamp = (int) (new Date().getTime() / 1000);
        
        try{
            
            
            
            
        }catch(EJBException e){
            
        }
        
        
        
        return "";
    }
    
    
        /**
     *
     * @param comment
     * @return
     */
    public String getFormattedDate(Comments comment) {
        SimpleDateFormat format = new SimpleDateFormat();
        return format.format(new Date(((long) comment.getTimePosted()) * 1000L));
    }
    
}
