package com.example.crudtest.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // e.g. STUDENT_CREATE
}