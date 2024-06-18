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
    @JoinColumn
    private Local local;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Events events;
}
