package com.klu.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.klu.entity.User;
import com.klu.controller.WebSocketController;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private WebSocketController webSocketController;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ✅ REGISTER WITH FILE UPLOAD
    public User register(String name, String email, String password, String role, String voterId, MultipartFile file) {

        String uploadDir = "uploads/";
        String fileName = file.getOriginalFilename();

        try {
            // folder create + file save
            Path path = Paths.get(uploadDir + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // user create
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setVoterId(voterId);
        user.setFilePath(fileName); // ✅ save file name

        User savedUser = repo.save(user);

        // Broadcast real-time update
        webSocketController.broadcastVoteUpdate("New user registered: " + name);

        return savedUser;
    }

    // ✅ LOGIN
    public User login(String email, String password) {
        User user = repo.findByEmail(email);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        return null;
    }
}