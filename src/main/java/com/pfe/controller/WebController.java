package com.pfe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "index";  // Retourne directement le nom de la vue
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // Retourne directement le nom de la vue
    }

    @GetMapping("/register")
    public String register() {
        return "register";  // Retourne directement le nom de la vue
    }

    @GetMapping("/chat")
    public String chat() {
        return "chat";  // Assurez-vous que chat.html est dans le dossier resources/templates/
    }

    @GetMapping("/add-patient")
    public String addPatientPage() {
        return "add-patient"; // Ceci fait référence à un fichier HTML nommé "add-patient.html"
    }
}