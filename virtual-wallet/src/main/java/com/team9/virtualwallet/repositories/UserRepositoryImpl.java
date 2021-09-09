package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<User> implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
        this.sessionFactory = sessionFactory;
    }

    public void verifyNotDuplicate(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", user.getEmail());
            if (query.list().size() > 0) {
                throw new DuplicateEntityException("User", "email", user.getEmail());
            }

            query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", user.getUsername());
            if (query.list().size() > 0) {
                throw new DuplicateEntityException("User", "username", user.getUsername());
            }

            query = session.createQuery("from User where phoneNumber = :phoneNumber", User.class);
            query.setParameter("phoneNumber", user.getPhoneNumber());
            if (query.list().size() > 0) {
                throw new DuplicateEntityException("User", "phone number", user.getPhoneNumber());
            }
        }
    }
}