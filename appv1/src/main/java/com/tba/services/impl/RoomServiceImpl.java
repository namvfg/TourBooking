package com.tba.services.impl;

/**
 *
 * @author HP Zbook 15
 */

import com.tba.pojo.Room;
import com.tba.pojo.ServicePost;
import com.tba.repositories.RoomRepository;
import com.tba.services.RoomService;
import com.tba.services.ServicePostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private ServicePostService servicePostService; 

    @Override
    public void addRoom(ServicePost post, Room room) {
        servicePostService.addServicePost(post);
        room.setServicePostId(post.getId());
        roomRepository.addRoom(room);
    }

 
}