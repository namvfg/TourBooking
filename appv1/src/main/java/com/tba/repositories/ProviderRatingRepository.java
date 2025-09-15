/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tba.repositories;

import com.tba.pojo.ProviderRating;
import java.util.List;

/**
 *
 * @author HP Zbook 15
 */
public interface ProviderRatingRepository {
    List<ProviderRating> getRatingsByProviderId(int providerId);
    ProviderRating findByUserAndProvider(int userId, int providerId);
    void addRating(ProviderRating rating);
    void updateRating(ProviderRating rating);
}
