package com.events.eventosUfsm.repository;

import com.events.eventosUfsm.model.local.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {

  @Query(value = "SELECT l.* " +
    "FROM local l " +
    "INNER JOIN events_to_local etl ON l.local_id = etl.local_id " +
    "WHERE etl.event_id = :eventId",
    nativeQuery = true)
  Optional<Local> findLocalByEventIdNative(@Param("eventId") Long eventId);
}
