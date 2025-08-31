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
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "transportation")
@NamedQueries({
    @NamedQuery(name = "Transportation.findAll", query = "SELECT t FROM Transportation t"),
    @NamedQuery(name = "Transportation.findByServicePostId", query = "SELECT t FROM Transportation t WHERE t.servicePostId = :servicePostId"),
    @NamedQuery(name = "Transportation.findByTransportType", query = "SELECT t FROM Transportation t WHERE t.transportType = :transportType"),
    @NamedQuery(name = "Transportation.findByStartDate", query = "SELECT t FROM Transportation t WHERE t.startDate = :startDate"),
    @NamedQuery(name = "Transportation.findByDestination", query = "SELECT t FROM Transportation t WHERE t.destination = :destination")})
public class Transportation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_post_id")
    private Integer servicePostId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "transport_type")
    private String transportType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "destination")
    private String destination;
    @JoinColumn(name = "service_post_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private ServicePost servicePost;

    public Transportation() {
    }

    public Transportation(Integer servicePostId) {
        this.servicePostId = servicePostId;
    }

    public Transportation(Integer servicePostId, String transportType, Date startDate, String destination) {
        this.servicePostId = servicePostId;
        this.transportType = transportType;
        this.startDate = startDate;
        this.destination = destination;
    }

    public Integer getServicePostId() {
        return servicePostId;
    }

    public void setServicePostId(Integer servicePostId) {
        this.servicePostId = servicePostId;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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
        if (!(object instanceof Transportation)) {
            return false;
        }
        Transportation other = (Transportation) object;
        if ((this.servicePostId == null && other.servicePostId != null) || (this.servicePostId != null && !this.servicePostId.equals(other.servicePostId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tba.pojo.Transportation[ servicePostId=" + servicePostId + " ]";
    }
    
}
