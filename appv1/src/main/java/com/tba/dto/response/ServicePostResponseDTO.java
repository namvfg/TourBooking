package com.tba.dto.response;

import java.math.BigDecimal;
import java.util.Date;

public class ServicePostResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private int availableSlot;
    private String address;
    private String serviceType;
    private Date createdDate;
    private Date updatedDate;
    private boolean isDeleted;
    private Integer serviceProviderId;
    private String companyName;

    // Getter & Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getAvailableSlot() { return availableSlot; }
    public void setAvailableSlot(int availableSlot) { this.availableSlot = availableSlot; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }

    public boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(boolean isDeleted) { this.isDeleted = isDeleted; }

    public Integer getServiceProviderId() { return serviceProviderId; }
    public void setServiceProviderId(Integer serviceProviderId) { this.serviceProviderId = serviceProviderId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}