package com.events.eventosUfsm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class EventosUfsmApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventosUfsmApplication.class, args);

	}

}
