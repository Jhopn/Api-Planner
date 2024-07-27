package com.learning.planner.trip;

import com.learning.planner.activity.ActivityService;
import com.learning.planner.link.LinkService;
import com.learning.planner.participant.ParticipantCreateResponse;
import com.learning.planner.participant.ParticipantRequestPayload;
import com.learning.planner.participant.ParticipantService;
import com.learning.planner.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private TripRepository repository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    @PostMapping
    public ResponseEntity<?> createTrip(@RequestBody TripRequestPayload payload) {
        try {
            Trip newTrip = new Trip(payload);
            repository.save(newTrip);
            participantService.registerParticipantsToEvents(payload.emails_to_invite(), newTrip);
            return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid argument: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while creating the trip: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTripDetails(@PathVariable UUID id) {
        try {
            Optional<Trip> trip = repository.findById(id);
            if (trip.isPresent()) {
                return ResponseEntity.ok(trip.get());
            } else {
                throw new NotFoundException("Trip not found with id: " + id);
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving the trip details: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
        try {
            Optional<Trip> trip = repository.findById(id);
            if (trip.isPresent()) {
                Trip rawTrip = trip.get();
                rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
                rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
                rawTrip.setDestination(payload.destination());
                repository.save(rawTrip);
                return ResponseEntity.ok(rawTrip);
            } else {
                throw new NotFoundException("Trip not found with id: " + id);
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid argument: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while updating the trip: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<?> confirmTrip(@PathVariable UUID id) {
        try {
            Optional<Trip> trip = repository.findById(id);
            if (trip.isPresent()) {
                Trip rawTrip = trip.get();
                rawTrip.setIsConfirmed(true);
                repository.save(rawTrip);
                participantService.triggerConfirmationEmailToParticipants(id);
                return ResponseEntity.ok(rawTrip);
            } else {
                throw new NotFoundException("Trip not found with id: " + id);
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while confirming the trip: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<?> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {
        try {
            Optional<Trip> trip = repository.findById(id);
            if (trip.isPresent()) {
                Trip rawTrip = trip.get();
                ParticipantCreateResponse response = participantService.registerParticipantToEvent(payload.email(), rawTrip);

                if (rawTrip.getIsConfirmed()) {
                    participantService.triggerConfirmationEmailToParticipant(payload.email());
                }
                return ResponseEntity.ok(response);
            } else {
                throw new NotFoundException("Trip not found with id: " + id);
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid argument: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while inviting the participant: " + e.getMessage());
        }
    }
}
