package com.events.eventosUfsm.model.bookmarks;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "user_bookmarks")
@Table(name = "user_bookmarks")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserBookmarks {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Opcional: especifica o nome da coluna de chave estrangeira
    @JsonIgnore

    private User users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id") // Opcional: especifica o nome da coluna de chave estrangeira
    private Events events;
}
