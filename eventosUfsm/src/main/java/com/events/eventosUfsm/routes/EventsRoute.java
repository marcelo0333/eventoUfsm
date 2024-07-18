package com.events.eventosUfsm.routes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.events.eventosUfsm.model.comments.CommentsDTO;
import com.events.eventosUfsm.model.comments.UserComments;
import com.events.eventosUfsm.model.events.EventLocalAssociationDTO;
import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.service.EventsLocalService;
import com.events.eventosUfsm.service.EventsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventsRoute {

    private final EventsService service;
    private final EventsLocalService eventsLocalService;
    private final ObjectMapper objectMapper;
    @GetMapping("/date")
    public Page<Events> findByDate(){

      return service.findAllOrderByDate();
    }
    @GetMapping("/bookmarks")
    public Page<Events> findByBookmarks(){

        return service.findAllOrderByBookmarks();
    }
     @GetMapping("/search")
     public ResponseEntity<List<Events>> searchEvents(@RequestParam String query){
        List<Events> events = service.searchEvents(query);
        return ResponseEntity.ok(events);
     }
    @GetMapping("/type")
    public ResponseEntity<List<Events>> typeEvents(@RequestParam String type){
        List<Events> events = service.typeEvents(type);
        System.out.println(events+type);
        return ResponseEntity.ok(events);
    }
    @GetMapping
    public ResponseEntity findAll(){ return service.findAllEvents(); }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id){return service.findById(id);}

    @GetMapping("/check-event")
    public ResponseEntity<Boolean> userCreated(@RequestParam Long userId, @RequestParam Long eventId) {
        boolean hasCreate = service.hasCreate(userId, eventId);
        return ResponseEntity.ok(hasCreate);
    }
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveEvent(
            @RequestParam("eventName") String eventName,
            @RequestParam("imgEvent") MultipartFile imgEvent,
            @RequestParam("typeEvent") String typeEvent,
            @RequestParam("description") String description,
            @RequestParam("dateInitial") String dateInitial,
            @RequestParam("dateFinal") String dateFinal,
            @RequestParam("centerName") String centerName,
            @RequestParam("contact") String contact,
            @RequestParam("userId") Long userId,
            @RequestParam("localIds") String localIdsJson
    ) {
        try {
            Set<Long> localIds = objectMapper.readValue(localIdsJson, new TypeReference<Set<Long>>() {});
            return service.saveEvent(eventName, imgEvent, typeEvent, description, dateInitial, dateFinal, centerName, contact, userId, localIds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid localIds format");
        }
    }

    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity putEvent( @RequestParam("eventId") Long eventId,
                                    @RequestParam("eventName") String eventName,
                                    @RequestParam("imgEvent") MultipartFile imgEvent,
                                    @RequestParam("typeEvent") String typeEvent,
                                    @RequestParam("description") String description,
                                    @RequestParam("dateInitial") String dateInitial,
                                    @RequestParam("dateFinal") String dateFinal,
                                    @RequestParam("centerName") String centerName,
                                    @RequestParam("contact") String contact,
                                    @RequestParam("userId") Long userId,
                                    @RequestParam("localIds") String localIdsJson){
        try {
            Set<Long> localIds = objectMapper.readValue(localIdsJson, new TypeReference<Set<Long>>() {});
            return service.editEvent(eventId, eventName, imgEvent, typeEvent, description, dateInitial, dateFinal, centerName, contact, userId, localIds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid localIds format");
        }
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
