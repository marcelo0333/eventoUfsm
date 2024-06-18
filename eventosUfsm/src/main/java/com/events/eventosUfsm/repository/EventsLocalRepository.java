package com.events.eventosUfsm.repository;

import com.events.eventosUfsm.model.events.EventsLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsLocalRepository extends JpaRepository<EventsLocal, Long> {
}
