package br.com.ctf.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Aplicação principal do Service Discovery (Eureka Server)
 * Responsável por gerenciar o registro e descoberta de serviços
 */
@EnableEurekaServer
@SpringBootApplication
public class ServiceDiscoveryApplication {

	/**
	 * Método principal que inicia a aplicação Spring Boot
	 * @param args argumentos da linha de comando
	 */
	public static void main(String[] args) {
		SpringApplication.run(ServiceDiscoveryApplication.class, args);
	}

}
