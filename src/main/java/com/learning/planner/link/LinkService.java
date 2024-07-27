package com.learning.planner.link;

import com.learning.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {
    @Autowired
    private LinkRepository repository;

    public LinkResponse registerLink(LinkRequestPayload payload, Trip trip) {
        try {
            Link newLink = new Link(payload.title(), payload.url(), trip);
            this.repository.save(newLink);
            return new LinkResponse(newLink.getId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid argument provided: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while registering the link: " + e.getMessage(), e);
        }
    }

    public List<LinkData> getAllLinkFromId(UUID tripId) {
        try {
            return this.repository.findByTripId(tripId).stream()
                    .map(link -> new LinkData(link.getId(), link.getTitle(), link.getUrl()))
                    .toList();
        } catch (NullPointerException e) {
            throw new NullPointerException("Trip ID is null: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching links: " + e.getMessage(), e);
        }
    }
}
