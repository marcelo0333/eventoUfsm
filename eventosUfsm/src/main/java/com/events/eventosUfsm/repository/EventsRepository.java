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


}
