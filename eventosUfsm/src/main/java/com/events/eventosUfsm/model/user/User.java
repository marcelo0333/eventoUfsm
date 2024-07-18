package com.events.eventosUfsm.model.user;

import com.events.eventosUfsm.model.bookmarks.UserBookmarks;
import com.events.eventosUfsm.model.comments.UserComments;
import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.rating.UserRating;
import com.events.eventosUfsm.model.reminder.Reminder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
@Table(name = "users")
public class User implements UserDetails {


    @Id
    @GeneratedValue
    private Long userId;

    @Size(min = 2, max = 30)
    private String firstName;

    @Size(min = 2, max = 30)
    private String lastName;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;
    @Column(name = "image_user")
    private String imgUser;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;


    @JsonIgnore
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserBookmarks> bookmarks;
    @JsonIgnore
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserComments> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRating> ratings;
    @JsonManagedReference
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Events> createdEvents;
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Reminder> reminders;
  @ManyToMany
  @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
