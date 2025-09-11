package com.tba.dto.request;

import java.math.BigDecimal;

public class ServicePostRequestDTO {
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private int availableSlot;
    private String address;
    private String serviceType;    
    private Integer serviceProviderId;

    // Getter & Setter
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

    public Integer getServiceProviderId() { return serviceProviderId; }
    public void setServiceProviderId(Integer serviceProviderId) { this.serviceProviderId = serviceProviderId; }
}