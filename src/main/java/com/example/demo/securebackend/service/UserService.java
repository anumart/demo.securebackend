package com.example.demo.securebackend.service;

import com.example.demo.securebackend.repository.UserRepository;
import com.example.demo.securebackend.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Transactional(propagation = Propagation.REQUIRED)
    public User createUser(User user) {
        return userRepo.save(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<User> getAllUser() {
        return userRepo.findAll();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<User> updateUser(User user) {
        boolean existing = userRepo.exists(Example.of(user));
        if (!existing)
            return Optional.empty();

        userRepo.update(user.getFirstName(), user.getLastName(), user.getEmail(), String.valueOf(user.getRole()), user.getModifiedBy(), user.getId());

        return userRepo.findById(user.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRefreshToken(User user) {
        userRepo.updateRefreshToken(user.getRefreshToken(), user.getId());
    }
}
