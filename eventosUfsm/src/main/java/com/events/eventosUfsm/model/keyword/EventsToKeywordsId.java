package com.events.eventosUfsm.model.keyword;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EventsToKeywordsId implements Serializable {

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "keyword_id")
    private Long keywordId;
}