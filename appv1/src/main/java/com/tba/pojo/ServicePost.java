/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.pojo;

import com.tba.enums.ServiceType;
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "service_post")
@NamedQueries({
    @NamedQuery(name = "ServicePost.findAll", query = "SELECT s FROM ServicePost s"),
    @NamedQuery(name = "ServicePost.findById", query = "SELECT s FROM ServicePost s WHERE s.id = :id"),
    @NamedQuery(name = "ServicePost.findByName", query = "SELECT s FROM ServicePost s WHERE s.name = :name"),
    @NamedQuery(name = "ServicePost.findByImage", query = "SELECT s FROM ServicePost s WHERE s.image = :image"),
    @NamedQuery(name = "ServicePost.findByPrice", query = "SELECT s FROM ServicePost s WHERE s.price = :price"),
    @NamedQuery(name = "ServicePost.findByAvailableSlot", query = "SELECT s FROM ServicePost s WHERE s.availableSlot = :availableSlot"),
    @NamedQuery(name = "ServicePost.findByAddress", query = "SELECT s FROM ServicePost s WHERE s.address = :address"),
    @NamedQuery(name = "ServicePost.findByServiceType", query = "SELECT s FROM ServicePost s WHERE s.serviceType = :serviceType"),
    @NamedQuery(name = "ServicePost.findByCreatedDate", query = "SELECT s FROM ServicePost s WHERE s.createdDate = :createdDate"),
    @NamedQuery(name = "ServicePost.findByUpdatedDate", query = "SELECT s FROM ServicePost s WHERE s.updatedDate = :updatedDate"),
    @NamedQuery(name = "ServicePost.findByIsDeleted", query = "SELECT s FROM ServicePost s WHERE s.isDeleted = :isDeleted")})
public class ServicePost implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Size(max = 500)
    @Column(name = "image")
    private String image;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "available_slot")
    private int availableSlot;
    @Size(max = 500)
    @Column(name = "address")
    private String address;
    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;
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
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "servicePost")
    private Room room;
    @JoinColumn(name = "service_provider_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ServiceProvider serviceProviderId;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "servicePost")
    private Tour tour;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "servicePostId")
    private Set<Transaction> transactionSet;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "servicePost")
    private Transportation transportation;

    public ServicePost() {
    }

    public ServicePost(Integer id) {
        this.id = id;
    }

    public ServicePost(Integer id, String name, BigDecimal price, int availableSlot, ServiceType serviceType, Date createdDate, Date updatedDate, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availableSlot = availableSlot;
        this.serviceType = serviceType;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getAvailableSlot() {
        return availableSlot;
    }

    public void setAvailableSlot(int availableSlot) {
        this.availableSlot = availableSlot;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ServiceProvider getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(ServiceProvider serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public Set<Transaction> getTransactionSet() {
        return transactionSet;
    }

    public void setTransactionSet(Set<Transaction> transactionSet) {
        this.transactionSet = transactionSet;
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof ServicePost)) {
            return false;
        }
        ServicePost other = (ServicePost) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tba.pojo.ServicePost[ id=" + id + " ]";
    }
    
}
