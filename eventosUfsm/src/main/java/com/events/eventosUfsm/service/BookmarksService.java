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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookmarksService {
    private final UserBookmarksRepository bookmarksRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;
    private final EventsService eventsService;
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

        eventsService.updateBookmark(eventId);
        return ResponseEntity.ok("Bookmark accepted");
    }
    public ResponseEntity<?> wipeBookmark(Long userId, Long eventId) {
        Optional<UserBookmarks> bookmarkOptional = bookmarksRepository.findByUsersUserIdAndEventsEventsId(userId, eventId);
        if (bookmarkOptional.isPresent()) {
            bookmarksRepository.delete(bookmarkOptional.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().body("Bookmark not found");
        }
    }

    public boolean userHasBookmarked(Long userId, Long eventId) {
        return bookmarksRepository.existsByUsersUserIdAndEventsEventsId(userId, eventId);
    }
    public List<Events> getEventsBookmarked(Long userId) {
        List<UserBookmarks> bookmarks = bookmarksRepository.findAllByUsersUserId(userId);
        return bookmarks.stream()
                .map(UserBookmarks::getEvents)
                .collect(Collectors.toList());
    }
}
