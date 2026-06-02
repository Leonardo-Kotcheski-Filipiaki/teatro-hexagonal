package com.teatro.theater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.teatro.theater", "com.teatro.shared"})
public class TheaterServiceApplication {
    public static void main(String[] args) {
        try {
            // Tenta ler na pasta atual, se não achar, tenta ler na pasta de cima (raiz do monorepo)
            java.nio.file.Path envPath = java.nio.file.Paths.get(".env");
            if (!java.nio.file.Files.exists(envPath)) {
                envPath = java.nio.file.Paths.get("../.env"); // Sobe um nível
            }

            if (java.nio.file.Files.exists(envPath)) {
                java.nio.file.Files.lines(envPath)
                        .map(String::trim)
                        .filter(line -> !line.startsWith("#") && line.contains("="))
                        .forEach(line -> {
                            String[] parts = line.split("=", 2);
                            System.setProperty(parts[0].trim(), parts[1].trim());
                        });
            }
        } catch (java.io.IOException e) {
            System.err.println("Não foi possível carregar o arquivo .env centralizado: " + e.getMessage());
        }
        SpringApplication.run(TheaterServiceApplication.class, args);
    }
}
