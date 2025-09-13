package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.reminder.Reminder;
import com.events.eventosUfsm.model.reminder.ReminderDTO;
import com.events.eventosUfsm.model.reminder.ReminderEventDTO;
import com.events.eventosUfsm.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reminders")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReminderRoute {

    @Autowired
    private ReminderService reminderService;


    @PostMapping("/save")
    public ResponseEntity<ResponseEntity<String>> createReminder(@RequestBody ReminderDTO reminder) {
        ResponseEntity<String> savedReminder = reminderService.saveReminder(reminder.userId(), reminder.eventId(), reminder.reminderTime());
        return ResponseEntity.ok(savedReminder);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReminderEventDTO>> getRemindersByUser(@PathVariable Long userId) {
        List<ReminderEventDTO> reminders = reminderService.getRemindersByUserId(userId);
        return ResponseEntity.ok(reminders);
    }
    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long reminderId) {
        reminderService.deleteReminder(reminderId);
        return ResponseEntity.noContent().build();
    }
}
