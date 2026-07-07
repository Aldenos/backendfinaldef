package com.upc.learntrack.shared.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirige la raíz del backend ("/") a la interfaz de Swagger UI,
 * de modo que al entrar a la URL base se vea la documentación de la API.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/swagger-ui/index.html";
    }
}
