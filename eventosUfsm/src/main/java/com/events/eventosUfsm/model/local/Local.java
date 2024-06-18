package com.events.eventosUfsm.model.local;


import com.events.eventosUfsm.model.events.EventsLocal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity(name = "local")
@Table(name = "local")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "local_id")
    private Long id;
    @Column(name = "name_local")
    private String nameLocal;
    @Column
    public String address;
    @Column
    public String city;
    @Column
    private String cep;
    @Column
    private String latitude;
    @Column
    private String longitude;
    @JsonIgnore
    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<EventsLocal> localSet;


}
