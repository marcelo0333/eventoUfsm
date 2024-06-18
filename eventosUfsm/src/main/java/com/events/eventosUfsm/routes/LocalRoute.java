package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.model.local.Local;
import com.events.eventosUfsm.service.LocalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LocalRoute {
    private final LocalService service;

    @GetMapping
    public ResponseEntity findAll(){return service.findAllLocal();}
    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id){
        return service.findById(id);
    }
    @PostMapping("/save")
    public ResponseEntity saveLocal(@RequestBody Local local){return service.saveLocal(local);}
    @PutMapping("/edit")
    public ResponseEntity editLocal(@RequestBody Local local){return service.editLocal(local);}
    @DeleteMapping("/delete")
    public ResponseEntity wipeLocal(@RequestParam Long id){return  service.wipeLocal(id);}

    @GetMapping("/events/{eventId}")
    public ResponseEntity<Local> getLocationByEventId(@PathVariable Long eventId) {
      Optional<Local> local = service.findLocalByEventId(eventId);

      if (local.isPresent()) {
        return ResponseEntity.ok(local.get());
      } else {
        return ResponseEntity.notFound().build();
      }
    }
}
