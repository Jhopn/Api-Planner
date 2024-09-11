package com.learning.planner.doc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Planner API")
                        .description("API para a criação de viajens com os amigos, incluindo atividades e links.")
                        .version("1.0")
                        .termsOfService("Termo de uso: Open Source")
                        .contact(new Contact()
                                .name("Jhoão")
                                .url("https://github.com/Jhopn")
                                .email("jhpdrnsantos2@gmail.com")));
    }
}