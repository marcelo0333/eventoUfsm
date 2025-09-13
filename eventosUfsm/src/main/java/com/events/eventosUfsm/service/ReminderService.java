package com.events.eventosUfsm.service;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.reminder.Reminder;
import com.events.eventosUfsm.model.reminder.ReminderEventDTO;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.repository.EventsRepository;
import com.events.eventosUfsm.repository.ReminderRepository;
import com.events.eventosUfsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ReminderService {

    @Autowired
    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;
    public ResponseEntity<String> saveReminder(Long userId, Long eventId, Date reminderTime) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        Optional<Events> optionalEvents = eventsRepository.findById(eventId);
        if (optionalEvents.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }
        User user = optionalUser.get();
        Events events = optionalEvents.get();
        Reminder reminder = Reminder.builder()
                .user(user)
                .event(events)
                .reminderTime(reminderTime)
                .build();
        reminderRepository.save(reminder);
        return ResponseEntity.ok("reminder acepted");
    }

    public List<ReminderEventDTO> getRemindersByUserId(Long userId) {
        List<Reminder> reminders = reminderRepository.findByUserUserId(userId);
        return reminders.stream()
                .map(reminder -> new ReminderEventDTO(reminder.getEvent(), reminder.getReminderTime(), reminder.getReminderId()))
                .collect(Collectors.toList());
    }
    public Optional<Reminder> getReminderById(Long reminderId) {
        return reminderRepository.findById(reminderId);
    }

    public void deleteReminder(Long reminderId) {
        reminderRepository.deleteById(reminderId);
    }
}
