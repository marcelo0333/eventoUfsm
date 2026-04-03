package com.events.eventosUfsm.service;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.model.userInteraction.InteractionType;
import com.events.eventosUfsm.model.userInteraction.UserInteraction;
import com.events.eventosUfsm.model.userPreferences.UserPreferences;
import com.events.eventosUfsm.repository.EventsRepository;
import com.events.eventosUfsm.repository.UserInteractionRepository;
import com.events.eventosUfsm.repository.UserPreferencesRepository;
import com.events.eventosUfsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPreferencesService {

    private final UserPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;

    public UserPreferences saveOrUpdate(Long userId, String preferredTypes,
                                        String course, String preferredCenter) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UserPreferences prefs = preferencesRepository
                .findByUser_UserId(userId)
                .orElse(UserPreferences.builder().user(user).build());

        prefs.setPreferredTypes(preferredTypes);
        prefs.setCourse(course);
        prefs.setPreferredCenter(preferredCenter);

        return preferencesRepository.save(prefs);
    }

    public Optional<UserPreferences> getByUserId(Long userId) {
        return preferencesRepository.findByUser_UserId(userId);
    }
}