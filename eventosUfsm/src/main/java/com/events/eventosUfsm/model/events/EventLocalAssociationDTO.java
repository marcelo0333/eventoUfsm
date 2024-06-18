package com.events.eventosUfsm.model.events;

import lombok.Data;

import java.util.Set;


@Data
public class EventLocalAssociationDTO{
    private Long eventId;
    private Set<Long> localIds;
}