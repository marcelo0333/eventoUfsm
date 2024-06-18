package com.events.eventosUfsm.repository;

import com.events.eventosUfsm.model.comments.CommentsDTO;
import com.events.eventosUfsm.model.comments.UserComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCommentsRepository extends JpaRepository<UserComments, Long> {


    List<UserComments> findByEventsEventsId(Long eventId);


}
