package com.example.bootcamp.infrastructure.adapters.persistenceadapter.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "bootcamps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BootcampEntity {
    @Id
    private Long id;
    private String name;
    private String description;
    private LocalDateTime launchDate;
    private Integer duration;
}
