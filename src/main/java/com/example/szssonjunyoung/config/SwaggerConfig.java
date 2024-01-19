package com.example.szssonjunyoung.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;


@Configuration
public class SwaggerConfig {

    // swagger ui접속
    // http://localhost:9000/swagger-ui/index.html

    @Bean
    public OpenAPI customOpenAPI() {
        var authHeader = Map.of("bearerAuth",
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization"));

        var schemaRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("삼쩜삼-손준영 API")
                        .description("<hr/><b>24/01/17, Rest API과제 <br/>")
                        .version("v1"))
                .components(new Components().securitySchemes(authHeader))
                .security(List.of(schemaRequirement));
    }
}

