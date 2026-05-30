package com.example.bootcamp.domain.model;

import com.example.bootcamp.domain.constants.Constants;
import com.example.bootcamp.domain.exceptions.InvalidFieldException;

import java.time.LocalDateTime;
import java.util.List;

public record Bootcamp(
        Long id,
        String name,
        String description,
        LocalDateTime launchDate,
        Integer duration,
        List<Capacity> capacities
) {
    public Bootcamp {
        if (name == null || name.isBlank()) {
            throw new InvalidFieldException(Constants.BOOTCAMP_NAME_REQUIRED);
        }

        if (description == null || description.isBlank()) {
            throw new InvalidFieldException(Constants.BOOTCAMP_DESCRIPTION_REQUIRED);
        }

        if (launchDate == null) {
            throw new InvalidFieldException(Constants.BOOTCAMP_LAUNCH_DATE_REQUIRED);
        }

        if (duration == null || duration <= 0) {
            throw new InvalidFieldException(Constants.BOOTCAMP_DURATION_INVALID);
        }

        int capacityCount = (capacities == null) ? 0 : capacities.size();
        if (capacityCount < 1 || capacityCount > 4) {
            throw new InvalidFieldException(Constants.INVALID_CAPACITY_COUNT);
        }
    }
}