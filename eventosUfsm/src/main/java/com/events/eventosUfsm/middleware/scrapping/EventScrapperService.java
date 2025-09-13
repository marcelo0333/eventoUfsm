package com.events.eventosUfsm.middleware.scrapping;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EventScrapperService {

    @Scheduled(fixedRate = 3600000)
    public void scrapeEvents() {
        try {
            String[] command = {"powershell.exe", "-Command", "cd ../; cd C:\\Users\\olouc\\Documents\\GitHub\\scrapper_events_ufsm\\src\\scrape ; tsx getEvents.ts"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Script getEvents.ts executado com sucesso.");
            } else {
                System.err.println("Erro ao executar script getEvents.ts. Código de saída: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
