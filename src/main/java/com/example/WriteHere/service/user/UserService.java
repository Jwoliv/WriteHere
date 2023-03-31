package com.example.WriteHere.service.user;

import com.example.WriteHere.model.user.Role;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Locale;


@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email).orElse(null);
    }
    public List<User> findByName(String name) {
        return userRepository.findUsersByName(name.toUpperCase(Locale.ROOT));
    }
    @Transactional
    public void save(User user) {
        user.setActive(true);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    @Transactional
    public void saveAfterChange(User user) {
        userRepository.save(user);
    }
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    @Transactional
    public void deleteByEmail(String email) {
        userRepository.deleteUserByEmail(email);
    }
    @Transactional
    public void markAllNotificationsAsCheckedOfUser(Principal principal) {
        if (principal != null) {
            User user = findByEmail(principal.getName());
            user.getNotifications().forEach(x -> x.setCheckedStatus(true));
            saveAfterChange(user);
        }
    }
    @Transactional
    public void deleteByIdIfAuthorizedAdmin(Long id, Principal principal) {
        if (principal != null) {
            User user = findById(id);
            User userOfSession = findByEmail(principal.getName());
            if (userOfSession.getRole().equals(Role.ADMIN) && !user.getRole().equals(Role.ADMIN)) {
                deleteById(id);
            }
        }
    }
    @Transactional
    public void changeNameOfUser(String firstname, String lastname, Principal principal) {
        User user = findByEmail(principal.getName());
        if (firstname != null && lastname != null) {
            user.setFirstname(firstname);
            user.setLastname(lastname);
            saveAfterChange(user);
        }
    }
}