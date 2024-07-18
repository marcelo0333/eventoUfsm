package com.events.eventosUfsm.repository;

import com.events.eventosUfsm.model.comments.CommentsDTO;
import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.local.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {

    List<Events> findByEventNameContainingIgnoreCase(String query);
//    @Query(value = "SELECT * FROM events WHERE events.type_event = ?1", nativeQuery = true)
    List<Events> findByTypeEvent(String typeEvent);

    Page<Events> findEventsByTotalBookmarksIsNotNull(Pageable pageable);
    boolean existsByCreatedBy_UserIdAndEventsId(Long userId, Long eventId);
}
