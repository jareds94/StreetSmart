/*
 * Created by Hung Vu on 2016.04.07  * 
 * Copyright Â© 2016 Hung Vu. All rights reserved. * 
 */
package com.streetsmart.entitypackage;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Kevin
 */
@Entity
@Table(name = "Pin")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pin.findAll", query = "SELECT p FROM Pin p"),
    @NamedQuery(name = "Pin.findById", query = "SELECT p FROM Pin p WHERE p.id = :id"),
    @NamedQuery(name = "Pin.findByLatitude", query = "SELECT p FROM Pin p WHERE p.latitude = :latitude"),
    @NamedQuery(name = "Pin.findByLongitude", query = "SELECT p FROM Pin p WHERE p.longitude = :longitude"),
    @NamedQuery(name = "Pin.findByTitle", query = "SELECT p FROM Pin p WHERE p.title = :title"),
    @NamedQuery(name = "Pin.findByAnonymous", query = "SELECT p FROM Pin p WHERE p.anonymous = :anonymous"),
    @NamedQuery(name = "Pin.findByPhoto", query = "SELECT p FROM Pin p WHERE p.photo = :photo"),
    @NamedQuery(name = "Pin.findByType", query = "SELECT p FROM Pin p WHERE p.type = :type"),
    @NamedQuery(name = "Pin.findByUpvotes", query = "SELECT p FROM Pin p WHERE p.upvotes = :upvotes"),
    @NamedQuery(name = "Pin.findByDownvotes", query = "SELECT p FROM Pin p WHERE p.downvotes = :downvotes"),
    @NamedQuery(name = "Pin.findByReports", query = "SELECT p FROM Pin p WHERE p.reports = :reports"),
    @NamedQuery(name = "Pin.findByTimePosted", query = "SELECT p FROM Pin p WHERE p.timePosted = :timePosted"),
    @NamedQuery(name = "Pin.findByUserId", query = "SELECT p FROM Pin p WHERE p.userId = :userId")})
public class Pin implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "latitude")
    private Float latitude;
    @Column(name = "longitude")
    private Float longitude;
    @Size(max = 255)
    @Column(name = "title")
    private String title;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Column(name = "anonymous")
    private Boolean anonymous;
    @Column(name = "photo")
    private Boolean photo;
    @Size(max = 20)
    @Column(name = "type")
    private String type;
    @Column(name = "upvotes")
    private Integer upvotes;
    @Column(name = "downvotes")
    private Integer downvotes;
    @Column(name = "reports")
    private Integer reports;
    @Column(name = "time_posted")
    private Integer timePosted;
    @Column(name = "user_id")
    private Integer userId;
    @Transient
    private int score;
    @Transient
    private String username;
      
    public Pin() {
        upvotes = 0;
        downvotes = 0;
        score = 0;
        anonymous = false;
        photo = false;
    }

    public int getScore() {
        score = (upvotes - downvotes);
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public Pin(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }
    
    public Boolean getPhoto() {
        return photo;
    }

    public void setPhoto(Boolean photo) {
        this.photo = photo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }

    public Integer getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(Integer downvotes) {
        this.downvotes = downvotes;
    }
    
    public Integer getReports() {
        return reports;
    }

    public void setReports(Integer reports) {
        this.reports = reports;
    }

    public Integer getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(Integer timePosted) {
        this.timePosted = timePosted;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pin)) {
            return false;
        }
        Pin other = (Pin) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.streetsmart.entitypackage.Pin[ id=" + id + " ]";
    }
    
}
