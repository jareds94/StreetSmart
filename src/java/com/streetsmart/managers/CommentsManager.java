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
import java.util.List;
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
    
    private int user_id;
    private int username;
    private String comment;
    private int timePosted;
    private Pin selectedPin;
    private int selectedPinId;
    private User selectedUser;
    private List<Comments> commentsList;
    
    
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
    
    public void setSelectedPinId(int id){
        this.selectedPinId = id;
        setCommentsList(commentsFacade.findAllCommentsByPinId(this.getSelectedPin().getId()));
    }
    
    public int getSelectedPinId(){
        return selectedPinId;
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
            Comments currComment = new Comments();
            currComment.setPinId(this.getSelectedPin().getId());
            currComment.setComment(this.comment);
            currComment.setUserId(this.getSelectedUser().getId());
            currComment.setTimePosted(timestamp);
            currComment.setUsername(this.getSelectedUser().getUsername());
            commentsFacade.create(currComment);
            return "index?faces-redirect=true";

        }catch(EJBException e){
            //status code
        }
        return "";
    }
    
    public void setCommentsList(List<Comments> comments){
        this.commentsList =  comments;
    }
    
    public List<Comments> getCommentsList(){
        commentsList = commentsFacade.findAllCommentsByPinId(this.getSelectedPin().getId());
        return commentsList;
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
