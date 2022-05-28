package com.so5.api.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "keycloak.server")
@NoArgsConstructor
public class KeycloakServerProperties {
    String contextPath = "/auth";
    String realmImportFile = "so5-realm.json";
    AdminUser adminUser = new AdminUser();

    // getters and setters

    @Data
    public static class AdminUser {
        String username = "admin";
        String password = "admin";

        // getters and setters
    }
}
