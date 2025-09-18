/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.dto.response;

import java.util.Date;

/**
 *
 * @author HP Zbook 15
 */
public class ProviderRatingResponseDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userAvatar;
    private int rate;
    private String comment;
    private Date createdDate;

    public ProviderRatingResponseDTO() {}

    public ProviderRatingResponseDTO(Integer id, Integer userId, String userName, String userAvatar,
                                    int rate, String comment, Date createdDate) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.rate = rate;
        this.comment = comment;
        this.createdDate = createdDate;
    }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
    public int getRate() { return rate; }
    public void setRate(int rate) { this.rate = rate; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}
