package com.events.eventosUfsm.repository;

import com.events.eventosUfsm.model.bookmarks.UserBookmarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBookmarksRepository extends JpaRepository<UserBookmarks, Long> {
}
