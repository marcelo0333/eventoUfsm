package com.events.eventosUfsm.model.types;

import java.util.Set;

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
    private Long typesId;
    @Column(name = "name_types", nullable = false)
    private String nameTypes;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Events> event;
}
