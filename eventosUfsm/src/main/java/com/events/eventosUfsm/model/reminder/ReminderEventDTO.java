package com.events.eventosUfsm.model.reminder;

import com.events.eventosUfsm.model.events.Events;

import java.util.Date;

public record ReminderEventDTO(
        Events events,
        Date reminderTime,
        Long reminderId
) {
    public ReminderEventDTO( Events events,
                             Date reminderTime,
                             Long reminderId){
        this.events = events;
        this.reminderTime = reminderTime;
        this.reminderId = reminderId;
    }
}
