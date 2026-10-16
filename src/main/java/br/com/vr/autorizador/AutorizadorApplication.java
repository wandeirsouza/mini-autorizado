package br.com.vr.autorizador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AutorizadorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutorizadorApplication.class, args);
	}

}
