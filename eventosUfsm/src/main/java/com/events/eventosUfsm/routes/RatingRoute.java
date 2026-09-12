package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.rating.UserRating;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
public class RatingRoute {

    private final RatingService service;

    @PostMapping("/save")
    public ResponseEntity<?> saveEvent(@Valid @RequestBody UserRating userRating,
                                       @AuthenticationPrincipal User currentUser) {
        return service.saveRating(currentUser.getUserId(), userRating);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> putEvent(@RequestBody UserRating userRating,
                                      @AuthenticationPrincipal User currentUser) {
        return service.editRating(currentUser.getUserId(), userRating);
    }
}
