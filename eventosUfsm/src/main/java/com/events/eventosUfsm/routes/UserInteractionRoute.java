package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.userInteraction.InteractionType;
import com.events.eventosUfsm.model.userInteraction.UserInteraction;
import com.events.eventosUfsm.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class UserInteractionRoute {

    private final UserInteractionService interactionService;

    @PostMapping("/{userId}/{eventId}/{type}")
    public ResponseEntity<Void> register(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable InteractionType type
    ) {
        interactionService.registerInteraction(userId, eventId, type);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserInteraction>> getUserInteractions(@PathVariable Long userId) {
        return ResponseEntity.ok(interactionService.getUserInteractions(userId));
    }
}