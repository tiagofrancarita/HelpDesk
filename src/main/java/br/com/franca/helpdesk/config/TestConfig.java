package br.com.franca.helpdesk.config;

import br.com.franca.helpdesk.service.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("test")
public class TestConfig {



    private DBService dbService;


    @Bean
    public void instacieDB() {
        this.dbService.instacieDB();
    }


}
