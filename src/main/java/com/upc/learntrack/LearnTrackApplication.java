package com.upc.learntrack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
public class LearnTrackApplication {
    public static void main(String[] args) {
        SpringApplication.run(LearnTrackApplication.class, args);
    }

    // Este bean imprimirá en consola todas las rutas que Spring detecta al arrancar
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            RequestMappingHandlerMapping mapping = ctx.getBean(RequestMappingHandlerMapping.class);
            mapping.getHandlerMethods().forEach((key, value) -> 
                System.out.println("RUTA REGISTRADA: " + key));
        };
    }
}