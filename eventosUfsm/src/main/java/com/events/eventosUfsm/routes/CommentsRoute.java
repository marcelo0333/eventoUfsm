package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.comments.UserComments;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.service.CommentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentsRoute {

    private final CommentsService service;

    @PostMapping("/save")
    public ResponseEntity<?> saveEvent(@Valid @RequestBody UserComments userComments,
                                       @AuthenticationPrincipal User currentUser) {
        return service.saveComment(currentUser.getUserId(), userComments);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> wipeEvent(@RequestParam Long id,
                                       @AuthenticationPrincipal User currentUser) {
        return service.wipeComment(id, currentUser.getUserId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<UserComments>> findEventsAndComments(@PathVariable Long id) {
        return ResponseEntity.ok(service.findCommentsByEventId(id));
    }
}
