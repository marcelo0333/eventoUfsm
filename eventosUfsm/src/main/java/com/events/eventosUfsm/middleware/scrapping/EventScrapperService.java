package com.events.eventosUfsm.middleware.scrapping;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class EventScrapperService {

@Value("${scrapper.script.path}")
    private String scriptPath;

@Value("${scrapper.working.dir}")
    private String workingDir;

    @Async
    @Scheduled(fixedRate = 3600000)
    public void scrapeEvents() {
        if (scriptPath == null || scriptPath.isBlank() ||
            workingDir == null || workingDir.isBlank()) {
            log.error("Propriedades não configuradas.");
            return;
        }

        try {
            boolean isWindows = System.getProperty("os.name")
                                      .toLowerCase()
                                      .contains("win");

            ProcessBuilder processBuilder = isWindows
                ? new ProcessBuilder("cmd", "/c", "npx", "tsx", scriptPath)
                : new ProcessBuilder("npx", "tsx", scriptPath);

            processBuilder.directory(new File(workingDir));

            // NÃO usar inheritIO() — consome os streams manualmente
            processBuilder.redirectErrorStream(true); // une stdout e stderr num só

            Process process = processBuilder.start();

            // Thread dedicada para drenar o output e evitar deadlock
            Thread outputDrain = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("[scrapper] {}", line);
                    }
                } catch (IOException e) {
                    log.warn("Erro ao ler output do scrapper", e);
                }
            });
            outputDrain.setDaemon(true);
            outputDrain.start();

            boolean finished = process.waitFor(5, TimeUnit.MINUTES);

            if (!finished) {
                process.destroyForcibly();
                log.error("Scrapper excedeu o tempo limite (5 min) e foi encerrado.");
                return;
            }

            outputDrain.join(5000); // aguarda a thread de leitura terminar

            int exitCode = process.exitValue();
            if (exitCode == 0) {
                log.info("Script getEvents.ts executado com sucesso.");
            } else {
                log.error("Script getEvents.ts falhou. Código de saída: {}", exitCode);
            }

        } catch (IOException e) {
            log.error("Falha ao iniciar o processo do scrapper", e);
        } catch (InterruptedException e) {
            log.warn("Execução do scrapper foi interrompida.");
            Thread.currentThread().interrupt();
        }
    }
}