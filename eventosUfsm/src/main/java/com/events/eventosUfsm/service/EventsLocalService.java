package com.events.eventosUfsm.service;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.events.EventsLocal;
import com.events.eventosUfsm.model.local.Local;
import com.events.eventosUfsm.repository.EventsLocalRepository;
import com.events.eventosUfsm.repository.EventsRepository;
import com.events.eventosUfsm.repository.LocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventsLocalService {

    private final EventsLocalRepository repository;
    private final EventsRepository eventsRepository;
    private final LocalRepository localRepository;
    public ResponseEntity<?> associateEventWithLocals(Long eventId, Set<Long> localIds) {
        Optional<Events> optionalEvents = eventsRepository.findById(eventId);
        if (!optionalEvents.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        Events events = optionalEvents.get();
        Set<EventsLocal> eventsLocals = new HashSet<>();

        for (Long localId : localIds) {
            Optional<Local> optionalLocal = localRepository.findById(localId);
            if (optionalLocal.isPresent()) {
                EventsLocal eventsLocal = EventsLocal.builder()
                        .events(events)
                        .local(optionalLocal.get())
                        .build();
                eventsLocals.add(eventsLocal);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Local with ID " + localId + " not found");
            }
        }

        repository.saveAll(eventsLocals);
        return ResponseEntity.ok("Locals associated with event successfully");
    }

}
