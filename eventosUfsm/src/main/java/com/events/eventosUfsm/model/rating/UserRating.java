package com.events.eventosUfsm.model.rating;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity(name = "user_rating")
@Table(name = "user_rating")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer rating;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Opcional: especifica o nome da coluna de chave estrangeira
    private User users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id") // Opcional: especifica o nome da coluna de chave estrangeira
    private Events events;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        } else {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}
