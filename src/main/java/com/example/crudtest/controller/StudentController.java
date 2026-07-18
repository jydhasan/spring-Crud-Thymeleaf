package com.example.crudtest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.crudtest.model.Student;
import com.example.crudtest.repository.StudentRepository;

@Controller
@RequestMapping("/")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @PreAuthorize("hasAuthority('STUDENT_READ')")
    @GetMapping
    public String viewHomePage(Model model, org.springframework.security.core.Authentication authentication) {
        model.addAttribute("listStudents", studentRepository.findAll());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "index";
    }

    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    @GetMapping("/showNewForm")
    public String showNewForm(Model model) {
        model.addAttribute("student", new Student());
        return "new_student";
    }

    @PreAuthorize("hasAuthority('STUDENT_CREATE') or hasAuthority('STUDENT_UPDATE')")
    @PostMapping("/saveStudent")
    public String saveStudent(@ModelAttribute("student") Student student) {
        studentRepository.save(student);
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('STUDENT_UPDATE')")
    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student id: " + id));
        model.addAttribute("student", student);
        return "update_student";
    }

    @PreAuthorize("hasAuthority('STUDENT_DELETE')")
    @GetMapping("/deleteStudent/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return "redirect:/";
    }
}