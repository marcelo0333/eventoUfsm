package com.events.eventosUfsm.model.keyword;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "keywords")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Keywords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long id;
    @Column(name = "name_keywords")
    private String nameKeyword;
    @JsonIgnore
    @OneToMany(mappedBy = "keyword",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<EventsToKeywords> eventsToKeywords;
}
