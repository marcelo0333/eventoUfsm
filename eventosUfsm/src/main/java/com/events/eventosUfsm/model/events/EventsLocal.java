package com.events.eventosUfsm.model.events;

import com.events.eventosUfsm.model.local.Local;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "events_to_local")
@Table(name = "events_to_local")

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventsLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id")

    private Local local;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Events events;
}
