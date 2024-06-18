package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.user.BookmarksDTO;
import com.events.eventosUfsm.service.BookmarksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/delete")
    public ResponseEntity wipeEvent(@RequestParam Long id){
        return service.wipeBookmark(id);
    }

}
