package com.events.eventosUfsm.service;

import com.events.eventosUfsm.model.events.Events;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.model.userInteraction.InteractionType;
import com.events.eventosUfsm.model.userInteraction.UserInteraction;
import com.events.eventosUfsm.repository.EventsRepository;
import com.events.eventosUfsm.repository.UserInteractionRepository;
import com.events.eventosUfsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInteractionService {

    private final UserInteractionRepository interactionRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    public void registerInteraction(Long userId, Long eventId, InteractionType type) {
        // Evita duplicatas do mesmo tipo
        if (interactionRepository.existsByUser_UserIdAndEvent_EventsIdAndType(userId, eventId, type)) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        UserInteraction interaction = UserInteraction.builder()
                .user(user)
                .event(event)
                .type(type)
                .build();

        interactionRepository.save(interaction);
    }

    public List<UserInteraction> getUserInteractions(Long userId) {
        return interactionRepository.findByUser_UserId(userId);
    }
}
