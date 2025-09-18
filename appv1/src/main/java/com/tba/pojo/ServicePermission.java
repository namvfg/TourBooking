/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.pojo;

import com.tba.enums.ServiceType;
import com.tba.enums.State;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "service_permission")
@NamedQueries({
    @NamedQuery(name = "ServicePermission.findAll", query = "SELECT s FROM ServicePermission s"),
    @NamedQuery(name = "ServicePermission.findById", query = "SELECT s FROM ServicePermission s WHERE s.id = :id"),
    @NamedQuery(name = "ServicePermission.findByServiceType", query = "SELECT s FROM ServicePermission s WHERE s.serviceType = :serviceType"),
    @NamedQuery(name = "ServicePermission.findByState", query = "SELECT s FROM ServicePermission s WHERE s.state = :state"),
    @NamedQuery(name = "ServicePermission.findByCreatedDate", query = "SELECT s FROM ServicePermission s WHERE s.createdDate = :createdDate"),
    @NamedQuery(name = "ServicePermission.findByUpdatedDate", query = "SELECT s FROM ServicePermission s WHERE s.updatedDate = :updatedDate")})
public class ServicePermission implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;
    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;
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
    @JoinColumn(name = "service_provider_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ServiceProvider serviceProviderId;

    public ServicePermission() {
    }

    public ServicePermission(Integer id) {
        this.id = id;
    }

    public ServicePermission(Integer id, ServiceType serviceType, State state, Date createdDate, Date updatedDate) {
        this.id = id;
        this.serviceType = serviceType;
        this.state = state;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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

    public ServiceProvider getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(ServiceProvider serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof ServicePermission)) {
            return false;
        }
        ServicePermission other = (ServicePermission) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tba.pojo.ServicePermission[ id=" + id + " ]";
    }
    
}
