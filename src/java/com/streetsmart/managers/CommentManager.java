/*
 * Created by Mukund Katti on 2016.04.19  * 
 * Copyright © 2016 Mukund Katti. All rights reserved. * 
 */
package com.streetsmart.managers;

import com.streetsmart.entitypackage.Comment;
import com.streetsmart.entitypackage.Pin;
import com.streetsmart.entitypackage.User;
import com.streetsmart.sessionbeanpackage.CommentFacade;
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
public class CommentManager implements Serializable {
    
    private int user_id;
    private String comment;
    private int timePosted;
    private Pin selectedPin;
    private int selectedPinId;
    private User selectedUser;
    private List<Comment> commentsList;
    
    
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
    private CommentFacade commentsFacade;
    
    public CommentManager(){
        
    }
    
    public CommentFacade getCommmentsFacade() {
        return commentsFacade;
    }

    public void setCommentsFacade(CommentFacade commentsFacade) {
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
            Comment currComment = new Comment();
            currComment.setPinId(this.getSelectedPin().getId());
            currComment.setComment(this.comment);
            user_id = this.getSelectedUser().getId();
            currComment.setUserId(user_id);
            timePosted = timestamp;
            currComment.setTimePosted(timePosted);
            commentsFacade.create(currComment);
            return "index?faces-redirect=true";

        }catch(EJBException e){
            //status code
        }
        return "";
    }
    
    public void setCommentsList(List<Comment> comments){
        this.commentsList =  comments;
    }
    
    public List<Comment> getCommentsList(){
        commentsList = commentsFacade.findAllCommentsByPinId(this.getSelectedPin().getId());
        return commentsList;
    }
    
    
        /**
     *
     * @param comment
     * @return
     */
    public String getFormattedDate(Comment comment) {
        SimpleDateFormat format = new SimpleDateFormat();
        return format.format(new Date(((long) comment.getTimePosted()) * 1000L));
    }
    
}
