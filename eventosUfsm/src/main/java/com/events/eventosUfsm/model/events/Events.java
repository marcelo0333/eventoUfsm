package com.events.eventosUfsm.model.events;

import com.events.eventosUfsm.model.bookmarks.UserBookmarks;
import com.events.eventosUfsm.model.comments.UserComments;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity(name = "events")
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventsId;
    @Column(name = "image_event")
    private String imgEvent;
    @Column
    private String contact;
    @Column(name = "center_name")
    private String centerName;
//    private String type_event;
    private String description;
    @Column(name = "event_name")
    private String eventName;
    @Column(name = "date_initial")
    private Date dateInitial;
    @Column(name = "date_final")
    private Date dateFinal;
    @Column(name = "average_rating", nullable = true)
    private Double averageRating;

    @JsonIgnore
    @OneToMany(mappedBy = "events", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserBookmarks> bookmarks;
    @JsonIgnore
    @OneToMany(mappedBy = "events", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserComments> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "events", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<EventsLocal> localSet;

    public void setAverageRating(Double averageRating) {
        this.averageRating = Math.round(averageRating * 100.0) / 100.0;
    }
}
