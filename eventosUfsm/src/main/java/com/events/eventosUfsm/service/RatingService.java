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
    public ResponseEntity<?> saveRating(UserRating userRating){
        Optional<User> optionalUser = userRepository.findById(userRating.getUsers().getUserId());
        if(!optionalUser.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        Optional<Events> optionalEvents = eventsRepository.findById(userRating.getEvents().getEventsId());
        if (!optionalEvents.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }
        User user = optionalUser.get();
        Events event = optionalEvents.get();

        UserRating userRating1 = UserRating.builder()
                .users(user)
                .events(event)
                .rating(userRating.getRating())
                .build();

        eventsService.updateRating(event.getEventsId());
        return ResponseEntity.ok().body(repository.save(userRating));
    }
    public ResponseEntity<?> editRating(UserRating userRating){
        Optional<UserRating> optionalRating = repository.findById(userRating.getId());
        if(!optionalRating.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found this rating");
        }else {
            Optional<User> optionalUser = userRepository.findById(userRating.getUsers().getUserId());
            if (!optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            Optional<Events> optionalEvents = eventsRepository.findById(userRating.getEvents().getEventsId());
            if (!optionalEvents.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
            }
            User user = optionalUser.get();
            Events events = optionalEvents.get();
            UserRating userRating1 = UserRating.builder()
                    .id(userRating.getId())
                    .users(user)
                    .events(events)
                    .rating(userRating.getRating())
                    .build();

            return ResponseEntity.ok().body(repository.save(userRating));
        }
    }

}
