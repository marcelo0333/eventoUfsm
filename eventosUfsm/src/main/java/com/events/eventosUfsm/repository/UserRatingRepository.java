package com.events.eventosUfsm.repository;

import com.events.eventosUfsm.model.rating.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {

    List<UserRating> findAllByEvents_EventsId(Long eventId);
}
