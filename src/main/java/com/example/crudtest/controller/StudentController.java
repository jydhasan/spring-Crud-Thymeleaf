package com.example.crudtest.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    // READ - list all students
    @GetMapping
    public String viewHomePage(Model model) {
        model.addAttribute("listStudents", studentRepository.findAll());
        return "index";
    }

    // CREATE - show form
    @GetMapping("/showNewForm")
    public String showNewForm(Model model) {
        model.addAttribute("student", new Student());
        return "new_student";
    }

    // CREATE - save
    @PostMapping("/saveStudent")
    public String saveStudent(@ModelAttribute("student") Student student) {
        studentRepository.save(student);
        return "redirect:/";
    }

    // UPDATE - show form
    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student id: " + id));
        model.addAttribute("student", student);
        return "update_student";
    }

    // DELETE
    @GetMapping("/deleteStudent/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return "redirect:/";
    }
}