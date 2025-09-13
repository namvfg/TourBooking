package com.tba.dto.request;

import java.math.BigDecimal;
import java.util.Date;

public class ServicePostRequestDTO {
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private int availableSlot;
    private String address;
    private String serviceType;
    private Integer serviceProviderId;

    // ROOM fields
    private Date roomStartDate;
    private Date roomEndDate;

    // TOUR fields
    private Date tourStartDate;
    private Date tourEndDate;

    // TRANSPORTATION fields
    private String transportType;
    private Date transportStartDate;
    private String destination;

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

    public Date getRoomStartDate() { return roomStartDate; }
    public void setRoomStartDate(Date roomStartDate) { this.roomStartDate = roomStartDate; }

    public Date getRoomEndDate() { return roomEndDate; }
    public void setRoomEndDate(Date roomEndDate) { this.roomEndDate = roomEndDate; }

    public Date getTourStartDate() { return tourStartDate; }
    public void setTourStartDate(Date tourStartDate) { this.tourStartDate = tourStartDate; }

    public Date getTourEndDate() { return tourEndDate; }
    public void setTourEndDate(Date tourEndDate) { this.tourEndDate = tourEndDate; }

    public String getTransportType() { return transportType; }
    public void setTransportType(String transportType) { this.transportType = transportType; }

    public Date getTransportStartDate() { return transportStartDate; }
    public void setTransportStartDate(Date transportStartDate) { this.transportStartDate = transportStartDate; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
}