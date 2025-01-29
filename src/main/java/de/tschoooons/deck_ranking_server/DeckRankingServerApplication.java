package de.tschoooons.deck_ranking_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeckRankingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeckRankingServerApplication.class, args);
	}
}
