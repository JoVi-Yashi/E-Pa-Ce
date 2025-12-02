package com.example.backend.controllers;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@PreAuthorize("denyAll()")


public class restController {


    @GetMapping("/")
    @PreAuthorize("permitAll()")
    public String index() {
        return "Hello Worldaaa, yes im a world builder ñiñi";
    }

    @GetMapping("/privota")
    @PreAuthorize("hasAuthority('READ')")
    public String pripa() {
        return "привет";
    }

}
