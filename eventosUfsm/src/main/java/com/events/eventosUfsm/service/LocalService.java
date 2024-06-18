package com.events.eventosUfsm.service;

import com.events.eventosUfsm.model.local.Local;
import com.events.eventosUfsm.repository.LocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocalService {
    private final LocalRepository repository;

    public ResponseEntity<?> findById(Long id){
        return ResponseEntity.status(HttpStatus.OK).body(repository.findById(id));
    }
    public ResponseEntity<?> findAllLocal(){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(repository.findAll());
    }
    public ResponseEntity<?> saveLocal(Local local){

        Local newLocal = Local.builder()
                .nameLocal(local.getNameLocal())
                .address(local.getAddress())
                .city(local.getCity())
                .cep(local.getCep())
                .latitude(local.getLatitude())
                .longitude(local.getLongitude())
                .build();

        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(repository.save(newLocal));
    }
    public ResponseEntity<?> editLocal(Local local) {
        Optional<Local> existingLocal = repository.findById(local.getId());

        if (existingLocal.isPresent()) {
            Local updateLocal = Local.builder()
                    .id(local.getId())
                    .nameLocal(local.getNameLocal())
                    .address(local.getAddress())
                    .city(local.getCity())
                    .cep(local.getCep())
                    .latitude(local.getLatitude())
                    .longitude(local.getLongitude())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(repository.save(updateLocal));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    public ResponseEntity<?> wipeLocal(Long id){
        if (repository.findById(id).isPresent()) {
            repository.deleteById(id);
        }else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

  public Optional<Local> findLocalByEventId(Long eventId) {
    return repository.findLocalByEventIdNative(eventId);
  }
}
