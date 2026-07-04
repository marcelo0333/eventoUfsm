package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.userInteraction.InteractionType;
import com.events.eventosUfsm.model.userInteraction.UserInteraction;
import com.events.eventosUfsm.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interactions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserInteractionRoute {

    private final UserInteractionService interactionService;

    @PostMapping("/{userId}/{eventId}/{type}")
    public ResponseEntity<Void> register(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable InteractionType type
    ) {
        System.out.println("Registering interaction: userId=" + userId + ", eventId=" + eventId + ", type=" + type);
        interactionService.registerInteraction(userId, eventId, type);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserInteraction>> getUserInteractions(@PathVariable Long userId) {
        return ResponseEntity.ok(interactionService.getUserInteractions(userId));
    }
}