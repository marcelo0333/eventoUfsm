package com.events.eventosUfsm.service;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.rating.UserRating;
import com.events.eventosUfsm.repository.*;
//import com.events.eventosUfsm.repository.UserCommentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventsService {
    private final EventsRepository repository;
    private final UserRatingRepository ratingRepository;
  public Page<Events> findAllOrderByDate() {

    Pageable pageable = PageRequest.of(
      0,
      10,
      Sort.Direction.DESC,
      "dateInitial"
    );

    return repository.findAll(pageable);
  }

  public ResponseEntity<?> findById(Long id){
        return ResponseEntity.status(HttpStatus.OK).body(repository.findById(id));
    }
    public ResponseEntity<?> findAllEvents(){

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(repository.findAll());
    }
    public ResponseEntity<?> saveEvent(Events events) {
        Events events1 = Events.builder()
                .imgEvent(events.getImgEvent())
                .eventName(events.getEventName())
                .centerName(events.getCenterName())
                .contact(events.getContact())
                .comments(events.getComments())
                .description(events.getDescription())
                .dateInitial(events.getDateInitial())
                .dateFinal(events.getDateFinal())
                .build();

        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(repository.save(events1));
    }
    public ResponseEntity<?> editEvent(Events events) {
        Optional<Events> existingEventOptional = repository.findById(events.getEventsId());

        if (existingEventOptional.isPresent()) {
            var updateEvent = Events.builder()
                    .eventsId(events.getEventsId())
                    .imgEvent(events.getImgEvent())
                    .eventName(events.getEventName())
                    .centerName(events.getCenterName())
                    .contact(events.getContact())
                    .comments(events.getComments())
                    .localSet(events.getLocalSet())
                    .description(events.getDescription())
                    .dateInitial(events.getDateInitial())
                    .dateFinal(events.getDateFinal())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(repository.save(updateEvent));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    public ResponseEntity<?> wipeEvent(Long id){
        if (repository.findById(id).isPresent()) {
            repository.deleteById(id);
        }else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

    public void updateRating(Long eventId){
        List<UserRating> ratings = ratingRepository.findAllByEvents_EventsId(eventId);
        if (!ratings.isEmpty()){
            Double average = ratings.stream()
                    .mapToInt(UserRating::getRating)
                    .average()
                    .orElse(0.0);
            Events events = repository.findById(eventId)
                    .orElseThrow(()-> new IllegalArgumentException("Invalid event id "+eventId));
            events.setAverageRating(average);
            repository.save(events);
        }
    }
}
