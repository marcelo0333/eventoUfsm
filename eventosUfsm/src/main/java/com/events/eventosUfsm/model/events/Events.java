package com.events.eventosUfsm.model.events;

import com.events.eventosUfsm.model.bookmarks.UserBookmarks;
import com.events.eventosUfsm.model.comments.UserComments;
import com.events.eventosUfsm.model.reminder.Reminder;
import com.events.eventosUfsm.model.types.TypeEvents;
import com.events.eventosUfsm.model.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

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

    private String description;
    @Column(name = "event_name")
    private String eventName;
    @Column(name = "date_initial")
    private Date dateInitial;
    @Column(name = "date_final")
    private Date dateFinal;
    @Column(name = "average_rating", nullable = true)
    private Double averageRating;
    @Column(name = "total_bookmarks", nullable = true)
    private Integer totalBookmarks;


    @JsonIgnore
    @OneToMany(mappedBy = "events", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserBookmarks> bookmarks;
    @JsonIgnore
    @OneToMany(mappedBy = "events", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserComments> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "events", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<EventsLocal> localSet;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = true)
    private TypeEvents type;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User createdBy;
    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Reminder> reminders;
    public void setAverageRating(Double averageRating) {
        this.averageRating = Math.round(averageRating * 100.0) / 100.0;
    }
    public void setTotalBookmarks(Integer totalBookmarks) {
        this.totalBookmarks = totalBookmarks;
    }
}
