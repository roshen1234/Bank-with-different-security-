package com.eazybytes.springsecsection1.doa;

import com.eazybytes.springsecsection1.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDAO{

    EntityManager entityManager;

    public UserDaoImpl(EntityManager theEntityManager) {

        this.entityManager = theEntityManager;
    }

    @Override
    public User findUserByUsername(String name) {
        TypedQuery<User> theQuery = entityManager.createQuery("from User where userName=:uName and enabled=true", User.class);
        theQuery.setParameter("uName", name);

        User theUser = null;
        try {
            theUser = theQuery.getSingleResult();
        } catch (Exception e) {
            theUser = null;
        }

        return theUser;
    }

}
