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
    public List<CommentsDTO> findCommentsByEventId(Long eventId) {
        List<UserComments> comments = repository.findByEventsEventsId(eventId);
        return comments.stream().map(comment -> new CommentsDTO(
                comment.getEvents().getEventsId(),
                comment.getEvents().getEventName(),
                comment.getUsers().getFirstName(),
                comment.getUsers().getLastName(),
                comment.getContent()
        )).collect(Collectors.toList());
    }
}
