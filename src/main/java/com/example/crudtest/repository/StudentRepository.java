package com.example.crudtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.crudtest.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}