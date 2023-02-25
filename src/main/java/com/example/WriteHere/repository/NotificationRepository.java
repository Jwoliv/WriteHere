package com.example.WriteHere.repository;

import com.example.WriteHere.model.notification.Notification;
import com.example.WriteHere.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserId(Long id);
    void deleteAllByUser(User user);
}
