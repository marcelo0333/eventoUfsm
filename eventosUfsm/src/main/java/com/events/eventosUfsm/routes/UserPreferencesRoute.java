package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.model.userPreferences.UserPreferences;
import com.events.eventosUfsm.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class UserPreferencesRoute {

    private final UserPreferencesService preferencesService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserPreferences> save(
            @PathVariable Long userId,
            @RequestBody UserPreferences body,
            @AuthenticationPrincipal User currentUser
    ) {
        requireSelf(userId, currentUser);
        log.debug("Recebendo preferências do usuário {}", currentUser.getUserId());
        return ResponseEntity.ok(
                preferencesService.saveOrUpdate(
                        currentUser.getUserId(),
                        body.getPreferredTypes(),
                        body.getCourse(),
                        body.getPreferredCenter()
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserPreferences> get(@PathVariable Long userId,
                                               @AuthenticationPrincipal User currentUser) {
        requireSelf(userId, currentUser);
        return preferencesService.getByUserId(currentUser.getUserId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private void requireSelf(Long pathUserId, User currentUser) {
        if (currentUser == null || !currentUser.getUserId().equals(pathUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado a dados de outro usuário.");
        }
    }
}
