package com.events.eventosUfsm.service;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.model.bookmarks.UserBookmarks;
import com.events.eventosUfsm.repository.EventsRepository;
import com.events.eventosUfsm.repository.UserBookmarksRepository;
import com.events.eventosUfsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BookmarksService {
    private final UserBookmarksRepository bookmarksRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    public ResponseEntity<?> saveBookmark(Long userId, Long eventId){
        Optional<User> optionalUser =  userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        Optional<Events> optionalEvents = eventsRepository.findById(eventId);
        if (optionalEvents.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }
        User user = optionalUser.get();
        Events events = optionalEvents.get();
        UserBookmarks userBookmarks1 = UserBookmarks.builder()
                .users(user)
                .events(events)
                .build();
        bookmarksRepository.save(userBookmarks1);

        return ResponseEntity.ok("Bookmark accepted");
    }
    public ResponseEntity<?> wipeBookmark(Long id){
        if (bookmarksRepository.findById(id).isPresent()) {
            bookmarksRepository.deleteById(id);
        }else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

}
