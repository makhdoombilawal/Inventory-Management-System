package com.inventoryapp.common.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration.
 * Access: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("Inventory Management API")
                    .version("1.0.0")
                    .description("Enterprise Auth with JWT & Multi-tenancy\n\n**Seeded Admin:** admin@system.com / Admin@123 / Tenant: system")
                    .contact(new Contact().name("Support").email("support@inventory.com")))
                .servers(List.of(new Server().url("http://localhost:8080").description("Local")))
                .components(new Components()
                    .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Token (Bearer)")))
                .addSecurityItem(new SecurityRequirement()
                    .addList("bearer-jwt"));
    }
}
