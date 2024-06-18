package com.events.eventosUfsm.model.comments;

import lombok.Data;

import java.io.Serializable;
import java.io.Serializable;

public record CommentsDTO(Long eventId,
                          String eventName,
                          String firstName,
                          String lastName,
                          String commentContent){


    public CommentsDTO(Long eventId,
                       String eventName,
                       String firstName,
                       String lastName,
                       String commentContent) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.commentContent = commentContent;
    }
    @Override
    public String firstName() {
        return firstName;
    }

    @Override
    public String lastName() {
        return lastName;
    }

    @Override
    public Long eventId() {
        return eventId;
    }

    @Override
    public String eventName() {
        return eventName;
    }



    @Override
    public String commentContent() {
        return commentContent;
    }
}

