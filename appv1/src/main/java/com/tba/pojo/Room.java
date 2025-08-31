/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "room")
@NamedQueries({
    @NamedQuery(name = "Room.findAll", query = "SELECT r FROM Room r"),
    @NamedQuery(name = "Room.findByServicePostId", query = "SELECT r FROM Room r WHERE r.servicePostId = :servicePostId"),
    @NamedQuery(name = "Room.findByStartDate", query = "SELECT r FROM Room r WHERE r.startDate = :startDate"),
    @NamedQuery(name = "Room.findByEndDate", query = "SELECT r FROM Room r WHERE r.endDate = :endDate")})
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_post_id")
    private Integer servicePostId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @JoinColumn(name = "service_post_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private ServicePost servicePost;

    public Room() {
    }

    public Room(Integer servicePostId) {
        this.servicePostId = servicePostId;
    }

    public Room(Integer servicePostId, Date startDate) {
        this.servicePostId = servicePostId;
        this.startDate = startDate;
    }

    public Integer getServicePostId() {
        return servicePostId;
    }

    public void setServicePostId(Integer servicePostId) {
        this.servicePostId = servicePostId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ServicePost getServicePost() {
        return servicePost;
    }

    public void setServicePost(ServicePost servicePost) {
        this.servicePost = servicePost;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (servicePostId != null ? servicePostId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Room)) {
            return false;
        }
        Room other = (Room) object;
        if ((this.servicePostId == null && other.servicePostId != null) || (this.servicePostId != null && !this.servicePostId.equals(other.servicePostId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tba.pojo.Room[ servicePostId=" + servicePostId + " ]";
    }
    
}
