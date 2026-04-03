package com.events.eventosUfsm.model.keyword;

import com.events.eventosUfsm.model.events.Events;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity(name = "events_to_keywords")
@Table(name = "events_to_keywords")
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class EventsToKeywords {

    @EmbeddedId
    private EventsToKeywordsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private Events event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("keywordId")
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keywords keyword;
}