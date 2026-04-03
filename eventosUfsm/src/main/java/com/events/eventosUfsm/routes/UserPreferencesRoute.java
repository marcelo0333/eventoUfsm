package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.userInteraction.InteractionType;
import com.events.eventosUfsm.model.userInteraction.UserInteraction;
import com.events.eventosUfsm.model.userPreferences.UserPreferences;
import com.events.eventosUfsm.service.UserInteractionService;
import com.events.eventosUfsm.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor

public class UserPreferencesRoute {

    private final UserPreferencesService preferencesService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserPreferences> save(
            @PathVariable Long userId,
            @RequestBody UserPreferences body
    ) {
        return ResponseEntity.ok(
                preferencesService.saveOrUpdate(
                        userId,
                        body.getPreferredTypes(),
                        body.getCourse(),
                        body.getPreferredCenter()
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserPreferences> get(@PathVariable Long userId) {
        return preferencesService.getByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}