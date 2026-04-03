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
        User user = assertUser(userRating);
        Events event = assertEvent(userRating);

        UserRating userRating1 = UserRating.builder()
                .users(user)
                .events(event)
                .rating(userRating.getRating())
                .build();

        eventsService.updateRating(event.getEventsId());
        return ResponseEntity.ok().body(repository.save(userRating1));
    }
    public ResponseEntity<?> editRating(UserRating userRating){
        Optional<UserRating> optionalRating = repository.findById(userRating.getId());
        if(optionalRating.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found this rating");
        }else {

            User user = assertUser(userRating);
            Events events = assertEvent(userRating);
            UserRating userRating1 = UserRating.builder()
                    .id(userRating.getId())
                    .users(user)
                    .events(events)
                    .rating(userRating.getRating())
                    .build();

            return ResponseEntity.ok().body(repository.save(userRating1));
        }
    }

    public User assertUser(UserRating userRating) {
        Optional<User> optionalUser = userRepository.findById(userRating.getUsers().getUserId());
        if (optionalUser.isEmpty()) {
            System.out.println("User not found");
        }
        return optionalUser.get();
    }
    public Events assertEvent(UserRating userRating) {
        Optional<Events> optionalEvents = eventsRepository.findById(userRating.getEvents().getEventsId());
        if (optionalEvents.isEmpty()) {
            System.out.println("Event not found");
        }
        return optionalEvents.get();
    }
}
