package com.events.eventosUfsm.repository;

import com.events.eventosUfsm.model.bookmarks.UserBookmarks;
import com.events.eventosUfsm.model.rating.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookmarksRepository extends JpaRepository<UserBookmarks, Long> {

    boolean existsByUsersUserIdAndEventsEventsId(Long userId, Long eventId);

    Optional<UserBookmarks> findByUsersUserIdAndEventsEventsId(Long userId, Long eventId);

    List<UserBookmarks> findAllByUsersUserId(Long userId);
    List<UserBookmarks> findAllByEvents_EventsId(Long eventId);


}
