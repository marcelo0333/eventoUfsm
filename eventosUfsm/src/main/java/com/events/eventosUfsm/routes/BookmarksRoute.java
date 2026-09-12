package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.user.BookmarksDTO;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.service.BookmarksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarksRoute {

    private final BookmarksService service;

    @PostMapping("/save")
    public ResponseEntity<?> saveEvent(@Valid @RequestBody BookmarksDTO bookmarksDTO,
                                       @AuthenticationPrincipal User currentUser) {
        // userId derivado do token — nunca do corpo da requisição.
        return service.saveBookmark(currentUser.getUserId(), bookmarksDTO.eventId());
    }

    @DeleteMapping("/delete/{userId}/{eventId}")
    public ResponseEntity<?> wipeEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                       @AuthenticationPrincipal User currentUser) {
        requireSelf(userId, currentUser);
        return service.wipeBookmark(currentUser.getUserId(), eventId);
    }

    @GetMapping("/{userId}/{eventId}")
    public ResponseEntity<Boolean> getUserHasBookmarked(@PathVariable Long userId, @PathVariable Long eventId,
                                                        @AuthenticationPrincipal User currentUser) {
        requireSelf(userId, currentUser);
        return ResponseEntity.ok(service.userHasBookmarked(currentUser.getUserId(), eventId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Events>> getUserBookmarks(@PathVariable Long userId,
                                                         @AuthenticationPrincipal User currentUser) {
        requireSelf(userId, currentUser);
        return ResponseEntity.ok(service.getEventsBookmarked(currentUser.getUserId()));
    }

    // Impede que um usuário autenticado acesse dados de outro (IDOR).
    private void requireSelf(Long pathUserId, User currentUser) {
        if (currentUser == null || !currentUser.getUserId().equals(pathUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado a dados de outro usuário.");
        }
    }
}
