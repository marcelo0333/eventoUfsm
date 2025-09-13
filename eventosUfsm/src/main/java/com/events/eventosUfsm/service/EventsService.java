package com.events.eventosUfsm.service;

import com.events.eventosUfsm.model.bookmarks.UserBookmarks;
import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.rating.UserRating;
import com.events.eventosUfsm.model.types.TypeEvents;
import com.events.eventosUfsm.model.types.TypeEventsRepository;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.repository.*;
//import com.events.eventosUfsm.repository.UserCommentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventsService {
    private final EventsRepository repository;
    private final UserRatingRepository ratingRepository;
    private final EventsLocalService eventsLocalService;
    private final UserRepository userRepository;
    private final UserBookmarksRepository bookmarksRepository;
    private final TypeEventsRepository typeEventsRepository;

    @Value("${app.image-directory}")
    private String imageDirectory;

    public Page<Events> findAllOrderByDate() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.Direction.DESC,
                "dateInitial"
        );
        return repository.findAll(pageable);
    }
    public Page<Events> findAllOrderByBookmarks() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.Direction.DESC,
                "totalBookmarks"
        );

        return repository.findEventsByTotalBookmarksIsNotNull(pageable);
    }
    public List<Events> typeEvents(Long type) {
        return repository.findEventsByType_Event(type);
    }

    public List<Events> searchEvents(String query) {
        return repository.findByEventNameContainingIgnoreCase(query);
    }

    public ResponseEntity<?> findById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(repository.findById(id));
    }

    public ResponseEntity<?> findAllEvents() {

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(repository.findAll());
    }

    public String saveImage(MultipartFile imgEvent) throws IOException {
        Path directoryPath = Paths.get(imageDirectory);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        String fileExtension = getFileExtension(imgEvent.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path filePath = directoryPath.resolve(uniqueFileName);
        Files.copy(imgEvent.getInputStream(), filePath);
        return "http://localhost:9090/api/images/" + uniqueFileName;
    }

    private String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public ResponseEntity<?> saveEvent(String eventName,
                                       MultipartFile imgEvent,
                                       Long typeEvent,
                                       String description,
                                       String dateInitial,
                                       String dateFinal,
                                       String centerName,
                                       String contact,
                                       Long userId,
                                       Set<Long> localIds) {
        try {
            String imgEventUrl = saveImage(imgEvent);

            Optional<TypeEvents> optionalType = typeEventsRepository.findById(typeEvent);
            if (optionalType.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de evento não encontrado.");
            }
            TypeEvents typeEventDB = optionalType.get();
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
            }
            User user = optionalUser.get();
            Events events = Events.builder()
                    .eventName(eventName)
                    .imgEvent(imgEventUrl)
                    .type(typeEventDB)
                    .description(description)
                    .dateInitial(Date.valueOf(dateInitial))
                    .dateFinal(Date.valueOf(dateFinal))
                    .centerName(centerName)
                    .createdBy(user)
                    .contact(contact)
                    .build();

            repository.save(events);
            eventsLocalService.associateEventWithLocals(events.getEventsId(), localIds);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Evento criado com sucesso!");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar a imagem do evento.");
        }
    }

    public ResponseEntity<?> editEvent(Long eventId,
                                       String eventName,
                                       MultipartFile imgEvent,
                                       Long typeEvent,
                                       String description,
                                       String dateInitial,
                                       String dateFinal,
                                       String centerName,
                                       String contact,
                                       Long userId,
                                       Set<Long> localIds) {
        Optional<Events> existingEventOptional = repository.findById(eventId);

        if (existingEventOptional.isPresent()) {
            try {
                String imgEventUrl = saveImage(imgEvent);

                Optional<TypeEvents> optionalType = typeEventsRepository.findById(typeEvent);
                if (optionalType.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de evento não encontrado.");
                }
                TypeEvents typeEventDB = optionalType.get();
                Optional<User> optionalUser = userRepository.findById(userId);
                if (optionalUser.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
                }
                User user = optionalUser.get();
                Events events = Events.builder()
                        .eventsId(eventId)
                        .eventName(eventName)
                        .imgEvent(imgEventUrl)
                        .type(typeEventDB)
                        .description(description)
                        .dateInitial(Date.valueOf(dateInitial))
                        .dateFinal(Date.valueOf(dateFinal))
                        .centerName(centerName)
                        .createdBy(user)
                        .contact(contact)
                        .build();

                repository.save(events);
                eventsLocalService.associateEventWithLocals(events.getEventsId(), localIds);

                Map<String, String> response = new HashMap<>();
                response.put("message", "Evento editado com sucesso!");
                return ResponseEntity.ok(response);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar a imagem do evento.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<?> wipeEvent(Long id) {
        if (repository.findById(id).isPresent()) {
            repository.deleteById(id);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

    public boolean hasCreate(Long userId, Long eventId) {
        return repository.existsByCreatedBy_UserIdAndEventsId(userId, eventId);
    }

    public void updateRating(Long eventId) {
        List<UserRating> ratings = ratingRepository.findAllByEvents_EventsId(eventId);
        if (!ratings.isEmpty()) {
            Double average = ratings.stream()
                    .mapToInt(UserRating::getRating)
                    .average()
                    .orElse(0.0);
            Events events = repository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid event id " + eventId));
            events.setAverageRating(average);
            repository.save(events);
        }
    }

    public void updateBookmark(Long eventId) {
        List<UserBookmarks> bookmarks = bookmarksRepository.findAllByEvents_EventsId(eventId);
        if (!bookmarks.isEmpty()) {
            int totalBookmarks = bookmarks.size();
            Events event = repository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid event id " + eventId));
            event.setTotalBookmarks(totalBookmarks);
            repository.save(event);
        }
    }
}
