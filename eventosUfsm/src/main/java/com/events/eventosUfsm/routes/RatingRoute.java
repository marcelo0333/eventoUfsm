package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.rating.UserRating;
import com.events.eventosUfsm.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RatingRoute {

    private final RatingService service;
    @PostMapping("/save")
    public ResponseEntity saveEvent(@Valid @RequestBody UserRating userRating){
        return service.saveRating(userRating);
    }
    @PutMapping("/edit")
    public ResponseEntity putEvent(@RequestBody UserRating userRating){
        return service.editRating(userRating);
    }
}
