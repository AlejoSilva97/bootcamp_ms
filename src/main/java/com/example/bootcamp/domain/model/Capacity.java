package com.example.bootcamp.domain.model;

import java.util.List;

public record Capacity(
        Long id,
        String name,
        List<Technology> techs
) {}
