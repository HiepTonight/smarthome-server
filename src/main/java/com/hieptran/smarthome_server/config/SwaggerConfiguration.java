package com.hieptran.smarthome_server.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Smarthome Server",
                        email = "Hiep Tran",
                        url = "https://google.com"
                ),
                description = "OpenApi documentation for Lao Network project",
                title = "OpenAPI specification",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "My Local",
                        url = "http://localhost:8080/api/v1"
                ),
                @Server(
                        description = "My Dev",
                        url = "https://smarthome-server-f5cl.onrender.com/api/v1"
                )
        },
        security = @SecurityRequirement(
                name = "Bearer Token"
        )
)

@SecurityScheme(
        name = "Bearer Token",
        description = "JWT token",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)

public class SwaggerConfiguration {
}
