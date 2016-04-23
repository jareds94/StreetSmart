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

@Named(value = "commentsManager")
@SessionScoped
/**
 *
 * @author kattim
 */
public class CommentManager implements Serializable {
    
    private int userId;
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
        selectedPinId = 0;
    }
    
    public CommentFacade getCommmentsFacade() {
        return commentsFacade;
    }

    public void setCommentsFacade(CommentFacade commentsFacade) {
        this.commentsFacade = commentsFacade;
    }
    
    
    public void setSelectedUser(User user){
        selectedUser = userFacade.findByUserId(userId);
    }
    
    public User getSelectedUser() {
       selectedUser = userFacade.findByUserId(userId);
       return selectedUser;
    }
    
    public void setSelectedPin(Pin pin){
        selectedPin = pin;
    }
    
    public Pin getSelectedPin() {
        return selectedPin;
    }
    
    public void setSelectedPinId(int id){
        this.selectedPinId = id;
        setCommentsList(commentsFacade.findAllCommentsByPinId(id));
    }
    
    public int getSelectedPinId(){
        return selectedPinId;
    }
    
    public void setTimePosted(int timePosted){
        this.timePosted = timePosted;
    }
    
    public int getTimedPosted(){
        return timePosted;
    }
    
    public void setComment(String comment){
        this.comment = comment;
    }
    
    public String getComment(){
        return comment;
    }
    
    
    public void setUserId(int id){
        userId = id;
    }
    
    public int getUserId(){
        return userId;
    }
    
    public String createComment(){
        
        int timestamp = (int) (new Date().getTime() / 1000);
        
        try{
            Comment currComment = new Comment();
            currComment.setPinId(selectedPinId);
            currComment.setComment(comment);
            userId = userFacade.find(FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap().get("user_id")).getId();
            currComment.setUserId(userId);
            timePosted = timestamp;
            currComment.setTimePosted(timePosted);
            commentsFacade.create(currComment);
            comment = "";
            return "";

        }catch(EJBException e){
            System.out.println();
            //status code
        }
        return "";
    }
    
    public void setCommentsList(List<Comment> comments){
        this.commentsList =  comments;
    }
    
    public List<Comment> getCommentsList(){
        commentsList = commentsFacade.findAllCommentsByPinId(selectedPinId);
        if(commentsList == null){
            return commentsList;
        }
            
        for (int i = 0; i < commentsList.size(); i++) {
            int id = commentsList.get(i).getUserId();
            User user = userFacade.findByUserId(id);
            commentsList.get(i).setUsername(user.getFirstName() + " " + user.getLastName());
        }
        
        quicksortByTimePosted(0, (commentsList.size()) - 1);

        return commentsList;
    }
    
    /**
     *
     * @param low
     * @param high
     */
    private void quicksortByTimePosted(int low, int high) {
        int i = low, j = high;

        int pivot = commentsList.get(low + (high - low) / 2).getTimePosted();

        while (i <= j) {

            while (commentsList.get(i).getTimePosted() > pivot) {
                i++;
            }

            while (commentsList.get(j).getTimePosted() < pivot) {
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
     * Generic swap for two Pin objects in a list. Helper method for quicksort
     * implementation.
     *
     * @param i
     * @param j
     */
    private void exchange(int i, int j) {

        Comment temp = commentsList.get(i);
        commentsList.set(i, commentsList.get(j));
        commentsList.set(j, temp);
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
