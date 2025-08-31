/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
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
@Table(name = "provider_rating")
@NamedQueries({
    @NamedQuery(name = "ProviderRating.findAll", query = "SELECT p FROM ProviderRating p"),
    @NamedQuery(name = "ProviderRating.findById", query = "SELECT p FROM ProviderRating p WHERE p.id = :id"),
    @NamedQuery(name = "ProviderRating.findByRate", query = "SELECT p FROM ProviderRating p WHERE p.rate = :rate"),
    @NamedQuery(name = "ProviderRating.findByCreatedDate", query = "SELECT p FROM ProviderRating p WHERE p.createdDate = :createdDate"),
    @NamedQuery(name = "ProviderRating.findByUpdatedDate", query = "SELECT p FROM ProviderRating p WHERE p.updatedDate = :updatedDate"),
    @NamedQuery(name = "ProviderRating.findByIsDeleted", query = "SELECT p FROM ProviderRating p WHERE p.isDeleted = :isDeleted")})
public class ProviderRating implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "rate")
    private short rate;
    @Lob
    @Size(max = 65535)
    @Column(name = "comment")
    private String comment;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_deleted")
    private boolean isDeleted;
    @JoinColumn(name = "service_provider_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ServiceProvider serviceProviderId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User userId;

    public ProviderRating() {
    }

    public ProviderRating(Integer id) {
        this.id = id;
    }

    public ProviderRating(Integer id, short rate, Date createdDate, Date updatedDate, boolean isDeleted) {
        this.id = id;
        this.rate = rate;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.isDeleted = isDeleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public short getRate() {
        return rate;
    }

    public void setRate(short rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public ServiceProvider getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(ServiceProvider serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
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
        if (!(object instanceof ProviderRating)) {
            return false;
        }
        ProviderRating other = (ProviderRating) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tba.pojo.ProviderRating[ id=" + id + " ]";
    }
    
}
