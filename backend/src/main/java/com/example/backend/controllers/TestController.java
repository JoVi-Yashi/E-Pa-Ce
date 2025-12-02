package com.example.backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/participant")
    @PreAuthorize("hasRole('INVITADO') or hasRole('MONITOR') or hasRole('OPERADOR') or hasRole('ADMIN')")
    public String participantAccess() {
        return "Participant Content.";
    }

    @GetMapping("/monitor")
    @PreAuthorize("hasRole('MONITOR') or hasRole('OPERADOR') or hasRole('ADMIN')")
    public String monitorAccess() {
        return "Monitor Board.";
    }

    @GetMapping("/operator")
    @PreAuthorize("hasRole('OPERADOR') or hasRole('ADMIN')")
    public String operatorAccess() {
        return "Operator Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
