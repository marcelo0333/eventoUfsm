package com.events.eventosUfsm.service;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.rating.UserRating;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.repository.EventsRepository;
import com.events.eventosUfsm.repository.UserRatingRepository;
import com.events.eventosUfsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final UserRatingRepository repository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;
    private final  EventsService eventsService;

    public ResponseEntity<?> saveRating(Long userId, UserRating userRating) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Events> event = eventsRepository.findById(userRating.getEvents().getEventsId());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (event.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        UserRating toSave = UserRating.builder()
                .users(user.get())
                .events(event.get())
                .rating(userRating.getRating())
                .build();

        eventsService.updateRating(event.get().getEventsId());
        return ResponseEntity.ok().body(repository.save(toSave));
    }

    public ResponseEntity<?> editRating(Long userId, UserRating userRating) {
        Optional<UserRating> optionalRating = repository.findById(userRating.getId());
        if (optionalRating.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found this rating");
        }
        // Só o autor da avaliação pode editá-la.
        UserRating existing = optionalRating.get();
        if (existing.getUsers() == null || !existing.getUsers().getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Avaliação não pertence ao usuário.");
        }

        existing.setRating(userRating.getRating());
        return ResponseEntity.ok().body(repository.save(existing));
    }
}
