package com.events.eventosUfsm.model.types;

import java.util.Set;

import javax.annotation.processing.Generated;

import com.events.eventosUfsm.model.events.Events;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity(name = "type_events")
@Table(name = "type_events")
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TypeEvents{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "type_events_seq")
    @SequenceGenerator(name = "type_events_seq", sequenceName = "type_events_seq", allocationSize = 1)
    @Column(name = "types_id")
    private Long typesId;
    @Column(name = "name_types", nullable = false)
    private String nameTypes;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Events> event;
}
