package com.events.eventosUfsm;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class EventosUfsmApplication {

    public static void main(String[] args) {
        // Tenta carregar o arquivo .env explicitamente
        Dotenv dotenv = Dotenv.configure()
                .directory("./eventosUfsm/") // Procura na raiz
                .filename(".env") // Garante o nome do arquivo
                .ignoreIfMissing()
                .load();

        // Debug: Verifica se o dotenv encontrou alguma entrada
        if (dotenv.entries().isEmpty()) {
            System.err.println("AVISO: Nenhuma variável encontrada no .env! Verifique se o arquivo está na raiz.");
        }

        // Passa as variáveis para o System Properties
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        // Teste de leitura imediata
        System.out.println("--- Verificação de Variáveis ---");
        System.out.println("SCRAPPER_WORKING_DIR: " + System.getProperty("SCRAPPER_WORKING_DIR"));
        System.out.println("--------------------------------");

        SpringApplication.run(EventosUfsmApplication.class, args);
    }

}
