package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User getById(User user, int id);

    public List<User> getAll(User user);

    void create(User user);

    void update(User userExecuting, User user, int id);

    void delete(User userExecuting, int id);

    List<User> filter(User user,
                      Optional<String> userName,
                      Optional<String> phoneNumber,
                      Optional<String> email);
}
