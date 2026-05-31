package com.example.bootcamp.domain.model;

public record PaginationParams(Integer page, Integer size, String sortBy, String direction) {
}
