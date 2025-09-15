/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services.impl;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.ProviderRating;
import com.tba.pojo.ServiceProvider;
import com.tba.pojo.User;
import com.tba.repositories.ProviderRatingRepository;
import com.tba.services.ProviderRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProviderRatingServiceImpl implements ProviderRatingService {

    @Autowired
    private ProviderRatingRepository providerRatingRepository;

    @Override
    public List<ProviderRating> getRatingsByProviderId(int providerId) {
        return providerRatingRepository.getRatingsByProviderId(providerId);
    }

    @Override
    public ProviderRating findByUserAndProvider(int userId, int providerId) {
        return providerRatingRepository.findByUserAndProvider(userId, providerId);
    }

    @Override
    public void addRating(User user, ServiceProvider provider, int rate, String comment) {
        ProviderRating rating = new ProviderRating();
        rating.setUserId(user);
        rating.setServiceProviderId(provider);
        rating.setRate((short) rate);
        rating.setComment(comment);
        rating.setCreatedDate(new Date());
        rating.setUpdatedDate(new Date());
        rating.setIsDeleted(false);
        providerRatingRepository.addRating(rating);
    }

    @Override
    public void updateRating(ProviderRating rating) {
        rating.setUpdatedDate(new Date());
        providerRatingRepository.updateRating(rating);
    }
}