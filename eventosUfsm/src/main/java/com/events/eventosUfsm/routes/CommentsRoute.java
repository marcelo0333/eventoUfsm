package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.comments.CommentsDTO;
import com.events.eventosUfsm.model.comments.UserComments;
import com.events.eventosUfsm.model.user.BookmarksDTO;
import com.events.eventosUfsm.service.CommentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentsRoute {

    private final CommentsService service;

    @PostMapping("/save")
    public ResponseEntity saveEvent(@Valid @RequestBody UserComments userComments){
        return service.saveComment(userComments);
    }

    @DeleteMapping("/delete")
    public ResponseEntity wipeEvent(@RequestParam Long id){
        return service.wipeComment(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity findEventsAndComments(@PathVariable Long id){
        List<CommentsDTO> comments = service.findCommentsByEventId(id);
        return ResponseEntity.ok(comments);
    }
}
