package com.tba.repositories.impl;

/**
 *
 * @author HP Zbook 15
 */

import com.tba.pojo.Room;
import com.tba.repositories.RoomRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RoomRepositoryImpl implements RoomRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    @Transactional
    public void addRoom(Room room) {
        Session session = factory.getObject().getCurrentSession();
        session.persist(room);
    }
}