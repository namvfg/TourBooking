/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tba.repositories;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.ServicePost;
import com.tba.pojo.Room;
import com.tba.pojo.Tour;
import com.tba.pojo.Transportation;
import java.util.List;
import java.util.Map;

public interface ServicePostRepository {
    public List<ServicePost> getServicePosts(Map<String, String> params);
    
    void softDeleteServicePost(Integer id);

    ServicePost getServicePostById(Integer id);   
    
    void addServicePost(ServicePost post);       
    
    void updateServicePost(ServicePost post); 
    
    List<ServicePost> getServicePostsPaged(int page, int size);
    long countServicePosts();
    
    long countServicePostsWithFilters(Map<String, String> params);
    
    List<ServicePost> getServicePostsByProviderIdPaged(int providerId, int page, int size);
    long countServicePostsByProviderId(int providerId);
    
    long countServicePostsByMonthYear(Integer month, Integer year);
    

    Map<String, Long> countServiceByType(Integer month, Integer year);
    
    Map<String, Long> revenueByServiceType(Integer month, Integer year);
    
}


