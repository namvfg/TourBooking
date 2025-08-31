/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.pojo;

import com.tba.enums.State;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "service_provider")
@NamedQueries({
    @NamedQuery(name = "ServiceProvider.findAll", query = "SELECT s FROM ServiceProvider s"),
    @NamedQuery(name = "ServiceProvider.findById", query = "SELECT s FROM ServiceProvider s WHERE s.id = :id"),
    @NamedQuery(name = "ServiceProvider.findByCompanyName", query = "SELECT s FROM ServiceProvider s WHERE s.companyName = :companyName"),
    @NamedQuery(name = "ServiceProvider.findByIsApproved", query = "SELECT s FROM ServiceProvider s WHERE s.isApproved = :isApproved"),
    @NamedQuery(name = "ServiceProvider.findByState", query = "SELECT s FROM ServiceProvider s WHERE s.state = :state"),
    @NamedQuery(name = "ServiceProvider.findByCreatedAt", query = "SELECT s FROM ServiceProvider s WHERE s.createdAt = :createdAt"),
    @NamedQuery(name = "ServiceProvider.findByUpdatedAt", query = "SELECT s FROM ServiceProvider s WHERE s.updatedAt = :updatedAt")})
public class ServiceProvider implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "company_name")
    private String companyName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_approved")
    private boolean isApproved;
    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceProviderId")
    private Set<ProviderRating> providerRatingSet;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OneToOne(optional = false)
    private User userId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceProviderId")
    private Set<ServicePermission> servicePermissionSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceProviderId")
    private Set<ServicePost> servicePostSet;

    public ServiceProvider() {
    }

    public ServiceProvider(Integer id) {
        this.id = id;
    }

    public ServiceProvider(Integer id, String companyName, boolean isApproved, State state, Date createdAt, Date updatedAt) {
        this.id = id;
        this.companyName = companyName;
        this.isApproved = isApproved;
        this.state = state;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ProviderRating> getProviderRatingSet() {
        return providerRatingSet;
    }

    public void setProviderRatingSet(Set<ProviderRating> providerRatingSet) {
        this.providerRatingSet = providerRatingSet;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Set<ServicePermission> getServicePermissionSet() {
        return servicePermissionSet;
    }

    public void setServicePermissionSet(Set<ServicePermission> servicePermissionSet) {
        this.servicePermissionSet = servicePermissionSet;
    }

    public Set<ServicePost> getServicePostSet() {
        return servicePostSet;
    }

    public void setServicePostSet(Set<ServicePost> servicePostSet) {
        this.servicePostSet = servicePostSet;
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
        if (!(object instanceof ServiceProvider)) {
            return false;
        }
        ServiceProvider other = (ServiceProvider) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tba.pojo.ServiceProvider[ id=" + id + " ]";
    }
    
}
