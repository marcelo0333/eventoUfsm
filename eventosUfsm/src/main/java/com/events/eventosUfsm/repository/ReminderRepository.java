package com.events.eventosUfsm.repository;

import com.events.eventosUfsm.model.reminder.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUserUserId(Long userId);
}
