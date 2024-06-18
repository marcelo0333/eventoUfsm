package com.events.eventosUfsm.model.user;

public record BookmarksDTO(Long userId, Long eventId) {
    public BookmarksDTO(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }
    public Long userId() {
        return userId;
    }

    @Override
    public Long eventId() {
        return eventId;
    }
}
