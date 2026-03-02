package com.events.eventosUfsm.middleware.scrapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class EventScrapperService {

    //@Value("${scrapper.script.path}")
    private String scriptPath;

    //@Value("${scrapper.working.dir}")
    private String workingDir;

    @Scheduled(fixedRate = 3600000)
    public void scrapeEvents() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("npx", "tsx", scriptPath);

            processBuilder.directory(new File(workingDir));

            processBuilder.inheritIO();

            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Script getEvents.ts executado com sucesso.");
            } else {
                System.err.println("Erro ao executar script getEvents.ts. Código de saída: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}
