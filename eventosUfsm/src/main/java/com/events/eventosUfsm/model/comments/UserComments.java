package com.events.eventosUfsm.model.comments;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity(name = "user_comments")
@Table(name = "user_comments" )
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreatedBy
    private String content;
    @CreatedDate
    private Date date_publish;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User users;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Events events;
    @PrePersist
    protected void onCreate() {
        date_publish = new Date();
    }
}
