package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.userInteraction.InteractionType;
import com.events.eventosUfsm.model.userInteraction.UserInteraction;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/interactions")
@RequiredArgsConstructor
public class UserInteractionRoute {

    private final UserInteractionService interactionService;

    @PostMapping("/{userId}/{eventId}/{type}")
    public ResponseEntity<Void> register(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable InteractionType type,
            @AuthenticationPrincipal User currentUser
    ) {
        requireSelf(userId, currentUser);
        log.debug("Registrando interação: userId={}, eventId={}, type={}", currentUser.getUserId(), eventId, type);
        interactionService.registerInteraction(currentUser.getUserId(), eventId, type);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserInteraction>> getUserInteractions(@PathVariable Long userId,
                                                                     @AuthenticationPrincipal User currentUser) {
        requireSelf(userId, currentUser);
        return ResponseEntity.ok(interactionService.getUserInteractions(currentUser.getUserId()));
    }

    private void requireSelf(Long pathUserId, User currentUser) {
        if (currentUser == null || !currentUser.getUserId().equals(pathUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado a dados de outro usuário.");
        }
    }
}
