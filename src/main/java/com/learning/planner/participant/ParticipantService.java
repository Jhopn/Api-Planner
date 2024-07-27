package com.learning.planner.participant;

import com.learning.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {
    @Autowired
    private ParticipantRepository repository;

    public void registerParticipantsToEvents(List<String> participantsToInvite, Trip trip) {
        try {
            List<Participant> participants = participantsToInvite.stream()
                    .map(email -> new Participant(email, trip))
                    .toList();
            this.repository.saveAll(participants);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid argument provided: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while registering participants to events: " + e.getMessage(), e);
        }
    }

    public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip) {
        try {
            Participant newParticipant = new Participant(email, trip);
            this.repository.save(newParticipant);
            return new ParticipantCreateResponse(newParticipant.getId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid argument provided: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while registering the participant to the event: " + e.getMessage(), e);
        }
    }

    public List<ParticipantData> getAllParticipantFromEvent(UUID tripId) {
        try {
            return this.repository.findByTripId(tripId).stream()
                    .map(participant -> new ParticipantData(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed()))
                    .toList();
        } catch (NullPointerException e) {
            throw new NullPointerException("Trip ID is null: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching participants from the event: " + e.getMessage(), e);
        }
    }

    public void triggerConfirmationEmailToParticipants(UUID id) {
        try {
            // Lógica para disparar e-mails de confirmação para participantes
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while triggering confirmation emails to participants: " + e.getMessage(), e);
        }
    }

    public void triggerConfirmationEmailToParticipant(String email) {
        try {
            // Lógica para disparar e-mail de confirmação para um participante
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while triggering a confirmation email to the participant: " + e.getMessage(), e);
        }
    }
}
