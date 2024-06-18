package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.comments.CommentsDTO;
import com.events.eventosUfsm.model.comments.UserComments;
import com.events.eventosUfsm.model.events.EventLocalAssociationDTO;
import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.service.EventsLocalService;
import com.events.eventosUfsm.service.EventsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventsRoute {

    private final EventsService service;
    private final EventsLocalService eventsLocalService;

   @GetMapping("/date")
   public Page<Events> findByDate(){

     return service.findAllOrderByDate();
   }
    @GetMapping
    public ResponseEntity findAll(){ return service.findAllEvents(); }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id){return service.findById(id);}

    @PostMapping("/save")
    public ResponseEntity saveEvent(@Valid @RequestBody Events events){
        return service.saveEvent(events);
    }
    @PutMapping("/edit")
    public ResponseEntity putEvent(@RequestBody Events events){
        return service.editEvent(events);
    }

    @DeleteMapping("/delete")
    public ResponseEntity wipeEvent(@RequestParam Long id){
        return service.wipeEvent(id);
    }

    @PostMapping("/associate-locals")
    public ResponseEntity<?> associateLocalsToEvent(@RequestBody EventLocalAssociationDTO dto) {
        return eventsLocalService.associateEventWithLocals(dto.getEventId(), dto.getLocalIds());
    }

}
