package com.events.eventosUfsm.repository;

import com.events.eventosUfsm.model.userInteraction.InteractionType;
import com.events.eventosUfsm.model.userInteraction.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

    List<UserInteraction> findByUser_UserId(Long userId);

    List<UserInteraction> findByEvent_EventsId(Long eventId);

    Optional<UserInteraction> findByUser_UserIdAndEvent_EventsIdAndType(
            Long userId, Long eventId, InteractionType type
    );

    boolean existsByUser_UserIdAndEvent_EventsIdAndType(
            Long userId, Long eventId, InteractionType type
    );
}