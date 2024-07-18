package com.events.eventosUfsm.service;

import com.events.eventosUfsm.model.comments.CommentsDTO;
import com.events.eventosUfsm.model.comments.UserComments;
import com.events.eventosUfsm.repository.UserCommentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final UserCommentsRepository repository;

    public ResponseEntity<?> saveComment(UserComments userComments){

        UserComments userComments1 = UserComments.builder()
                .users(userComments.getUsers())
                .events(userComments.getEvents())
                .content(userComments.getContent())
                .date_publish(userComments.getDate_publish())
                .build();
        return ResponseEntity.ok().body(repository.save(userComments1));
    }
    public ResponseEntity<?> wipeComment(Long id){
       if (repository.findById(id).isPresent()){
           repository.deleteById(id);
           return ResponseEntity.noContent().build();
       }else {
           return ResponseEntity.badRequest().build();
       }
    }
    public List<UserComments> findCommentsByEventId(Long eventId) {
        return repository.findByEventsEventsId(eventId);
    }
}
