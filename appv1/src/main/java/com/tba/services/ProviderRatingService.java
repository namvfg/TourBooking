/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tba.services;

/**
 *
 * @author HP Zbook 15
 */

import com.tba.pojo.ProviderRating;
import com.tba.pojo.ServiceProvider;
import com.tba.pojo.User;
import java.util.List;

public interface ProviderRatingService {
    List<ProviderRating> getRatingsByProviderId(int providerId);
    ProviderRating findByUserAndProvider(int userId, int providerId);
    void addRating(User user, ServiceProvider provider, int rate, String comment);
    void updateRating(ProviderRating rating);
}
