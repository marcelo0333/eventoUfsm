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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ReminderService {

    @Autowired
    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;
    public ResponseEntity<?> saveReminder(Long userId, Long eventId, Date reminderTime) {
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
        Reminder saved = reminderRepository.save(reminder);
        // JSON (não texto/plain) — o HttpClient do Angular espera JSON por padrão e falhava
        // o parsing de uma resposta texto/plain, tratando o 200 como erro. O reminderId
        // volta pro front porque ele precisa pra agendar a notificação local do lembrete.
        return ResponseEntity.ok(Map.of("reminderId", saved.getReminderId()));
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

    public boolean isOwnedBy(Long reminderId, Long userId) {
        return reminderRepository.findById(reminderId)
                .map(r -> r.getUser() != null && r.getUser().getUserId().equals(userId))
                .orElse(false);
    }

    public void deleteReminder(Long reminderId) {
        reminderRepository.deleteById(reminderId);
    }
}
