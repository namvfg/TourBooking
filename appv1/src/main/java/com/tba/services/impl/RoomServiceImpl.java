package com.tba.services.impl;

import com.tba.pojo.Room;
import com.tba.repositories.RoomRepository;
import com.tba.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public void addRoom(Room room) {
        roomRepository.addRoom(room);
    }
}