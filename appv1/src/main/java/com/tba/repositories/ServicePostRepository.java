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
import java.util.List;

public interface ServicePostRepository {
    List<ServicePost> getAllServicePosts();
    
    void softDeleteServicePost(Integer id);

    ServicePost getServicePostById(Integer id);   
    
    void addServicePost(ServicePost post);       
    
    void updateServicePost(ServicePost post); 
    
    List<ServicePost> getServicePostsPaged(int page, int size);
    long countServicePosts();
}
