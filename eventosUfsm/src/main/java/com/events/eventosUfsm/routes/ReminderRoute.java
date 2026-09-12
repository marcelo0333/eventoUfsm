package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.reminder.ReminderDTO;
import com.events.eventosUfsm.model.reminder.ReminderEventDTO;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reminders")
@RequiredArgsConstructor
public class ReminderRoute {

    private final ReminderService reminderService;

    @PostMapping("/save")
    public ResponseEntity<?> createReminder(@RequestBody ReminderDTO reminder,
                                                 @AuthenticationPrincipal User currentUser) {
        // userId derivado do token.
        return reminderService.saveReminder(currentUser.getUserId(), reminder.eventId(), reminder.reminderTime());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReminderEventDTO>> getRemindersByUser(@PathVariable Long userId,
                                                                     @AuthenticationPrincipal User currentUser) {
        requireSelf(userId, currentUser);
        return ResponseEntity.ok(reminderService.getRemindersByUserId(currentUser.getUserId()));
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long reminderId,
                                               @AuthenticationPrincipal User currentUser) {
        // Só o dono do lembrete pode apagá-lo.
        if (!reminderService.isOwnedBy(reminderId, currentUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Lembrete não pertence ao usuário.");
        }
        reminderService.deleteReminder(reminderId);
        return ResponseEntity.noContent().build();
    }

    private void requireSelf(Long pathUserId, User currentUser) {
        if (currentUser == null || !currentUser.getUserId().equals(pathUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado a dados de outro usuário.");
        }
    }
}
