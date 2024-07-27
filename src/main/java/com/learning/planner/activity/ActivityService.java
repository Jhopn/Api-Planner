package com.learning.planner.activity;

import com.learning.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    public ActivityResponse registerActivity(ActivityRequestPayload payload, Trip trip){
        try {
            Activity newActivity = new Activity(payload.title(), payload.occurs_at(), trip);
            this.repository.save(newActivity);
            return new ActivityResponse(newActivity.getId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid argument provided: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while registering the activity: " + e.getMessage(), e);
        }
    }
    public List<ActivityData> getAllActivitiesFromId(UUID tripId){
        try {
            return this.repository.findByTripId(tripId).stream()
                    .map(activity -> new ActivityData(activity.getId(), activity.getTitle(), activity.getOccursAt()))
                    .toList();
        } catch (NullPointerException e) {
            throw new NullPointerException("Trip ID is null: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching activities: " + e.getMessage(), e);
        }
    }
}
