package br.com.ifba.eco_ciclo_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "br.com.ifba")
@EntityScan(basePackages = "br.com.ifba")
@EnableJpaRepositories(basePackages = "br.com.ifba")
public class EcoCicloBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcoCicloBackApplication.class, args);
    }

}