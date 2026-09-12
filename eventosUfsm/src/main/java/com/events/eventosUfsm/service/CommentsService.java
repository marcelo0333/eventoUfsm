package com.events.eventosUfsm.service;

import com.events.eventosUfsm.model.comments.UserComments;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.repository.UserCommentsRepository;
import com.events.eventosUfsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final UserCommentsRepository repository;
    private final UserRepository userRepository;

    public ResponseEntity<?> saveComment(Long userId, UserComments userComments) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        // O autor é sempre o usuário autenticado, não o que veio no corpo.
        UserComments toSave = UserComments.builder()
                .users(user)
                .events(userComments.getEvents())
                .content(userComments.getContent())
                .build();
        return ResponseEntity.ok().body(repository.save(toSave));
    }

    public ResponseEntity<?> wipeComment(Long id, Long userId) {
        UserComments comment = repository.findById(id).orElse(null);
        if (comment == null) {
            return ResponseEntity.badRequest().body("Comment not found");
        }
        boolean isOwner = comment.getUsers() != null
                && comment.getUsers().getUserId().equals(userId);
        if (!isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Comentário não pertence ao usuário.");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public List<UserComments> findCommentsByEventId(Long eventId) {
        return repository.findByEventsEventsId(eventId);
    }
}
