package com.example.WriteHere.service;

import com.example.WriteHere.model.notification.Notification;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    public List<Notification> findAllByUserId(Long id) {
        return notificationRepository.findAllByUserId(id);
    }
    public Notification findById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }
    @Transactional
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }
    @Transactional
    public void deleteById(Long id) {
        Notification notification = findById(id);
        notification.setUser(null);
        save(notification);
        notificationRepository.deleteById(id);
    }
    @Transactional
    public void deleteAllByUser(User user) {
        notificationRepository.deleteAllByUser(user);
    }
}
