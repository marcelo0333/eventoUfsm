package com.events.eventosUfsm.model.userPreferences;

import com.events.eventosUfsm.model.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "preferred_types")
    private String preferredTypes;
    // ex: "1,2,5" — IDs dos tipos preferidos

    @Column(name = "preferred_center")
    private String preferredCenter;

    @Column
    private String course;
    // curso do aluno — muito útil para recomendar por área
}