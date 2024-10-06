package de.tschoooons.deck_ranking_server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;

@Configuration
public class JacksonDatatypeHibernate {

    @Bean 
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module();
    }
}
