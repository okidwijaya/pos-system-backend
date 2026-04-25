package com.kitadevelopers.pos.modules.user.service;

import com.kitadevelopers.pos.modules.user.entity.User;
import com.kitadevelopers.pos.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User getByEmail(String email){
        return repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public User save(User user){
        return repository.save(user);
    }

//    public User createUser(User user){
//        return repository.save(user);
//    }
//
//    public User getById(UUID id){
//        return repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User Not Found"));
//    }
}
