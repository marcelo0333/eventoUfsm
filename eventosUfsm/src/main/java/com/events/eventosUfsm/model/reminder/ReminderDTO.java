package com.events.eventosUfsm.model.reminder;

import java.util.Date;

public record ReminderDTO(
        Long userId,
        Long eventId,
        Date reminderTime
) {
    public ReminderDTO(
            Long userId,
            Long eventId,
            Date reminderTime
    ){
        this.userId = userId;
        this.eventId = eventId;
        this.reminderTime = reminderTime;
    }
    public Long userId() {
        return userId;
    }

    @Override
    public Long eventId() {
        return eventId;
    }

    @Override
    public Date reminderTime() {
        return reminderTime;
    }
}
