package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.bookmarks.UserBookmarks;
import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.user.BookmarksDTO;
import com.events.eventosUfsm.service.BookmarksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookmarksRoute {

    private final BookmarksService service;

    @PostMapping("/save")
    public ResponseEntity saveEvent(@Valid @RequestBody BookmarksDTO bookmarksDTO){
        return service.saveBookmark(bookmarksDTO.userId(), bookmarksDTO.eventId());
    }

    @DeleteMapping("/delete/{userId}/{eventId}")
    public ResponseEntity wipeEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.wipeBookmark(userId, eventId);
    }

    @GetMapping("/{userId}/{eventId}")
    public ResponseEntity<Boolean> getUserHasBookmarked(@PathVariable Long userId, @PathVariable Long eventId) {
        boolean hasBookmarked = service.userHasBookmarked(userId, eventId);
        return ResponseEntity.ok(hasBookmarked);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<Events>> getUserBookmarks(@PathVariable Long userId) {
        List<Events> events = service.getEventsBookmarked(userId);
        return ResponseEntity.ok(events);
    }
}
