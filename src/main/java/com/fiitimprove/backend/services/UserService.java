package com.fiitimprove.backend.services;

import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        user.setJoinedAt(LocalDate.now());
        user.setVerified(false);
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }


}