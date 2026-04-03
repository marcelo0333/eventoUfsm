package com.events.eventosUfsm.model.userInteraction;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.user.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "user_interactions")
public class UserInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Events event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionType type;
    // VIEW, CLICK, BOOKMARK, RATING, REMINDER, SHARE

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() { createdAt = new Date(); }
}