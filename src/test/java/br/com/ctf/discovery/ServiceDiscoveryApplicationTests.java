package br.com.ctf.discovery;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.netflix.discovery.EurekaClient;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceDiscoveryApplicationTests {

	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String TEST_SERVICE_NAME = "test-service";
	private static final String TEST_SERVICE_HOST = "localhost";
	private static final String TEST_SERVICE_PORT = "8080";
	private static final String TEST_SERVICE_INSTANCE = TEST_SERVICE_HOST + ":" + TEST_SERVICE_PORT;

	private String criarPayloadRegistro() {
		return String.format("""
			{
				"instance": {
					"hostName": "%s",
					"app": "%s",
					"ipAddr": "127.0.0.1",
					"port": {
						"$": %s,
						"@enabled": "true"
					},
					"status": "UP"
				}
			}""", TEST_SERVICE_HOST, TEST_SERVICE_NAME, TEST_SERVICE_PORT);
	}

	private void registrarServico() {
		ResponseEntity<String> response = restTemplate.postForEntity(
			"/eureka/apps/" + TEST_SERVICE_NAME,
			criarPayloadRegistro(),
			String.class
		);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void contextLoads() {
	}

    @Test
    void deveRegistrarServicoComSucesso() {
        // Registra o serviço
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> request = new HttpEntity<>(criarPayloadRegistro(), headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/eureka/apps/" + TEST_SERVICE_NAME,
            request,
            String.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verifica se o serviço foi registrado
        response = restTemplate.getForEntity(
            "/eureka/apps/" + TEST_SERVICE_NAME,
            String.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(TEST_SERVICE_NAME));
    }

    @Test
    void deveAtualizarStatusDoServico() {
        // Setup
        registrarServico();

        // Atualiza o status do serviço
        restTemplate.put(
            "/eureka/apps/" + TEST_SERVICE_NAME + "/" + TEST_SERVICE_INSTANCE + "/status?value=OUT_OF_SERVICE",
            null
        );

        // Verifica se o status foi atualizado
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/eureka/apps/" + TEST_SERVICE_NAME + "/" + TEST_SERVICE_INSTANCE,
            String.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("OUT_OF_SERVICE"));
    }

    @Test
    void deveDeletarServicoComSucesso() {
        // Setup
        registrarServico();

        // Deleta o serviço
        restTemplate.delete("/eureka/apps/" + TEST_SERVICE_NAME + "/" + TEST_SERVICE_INSTANCE);

        // Verifica se o serviço foi deletado
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/eureka/apps/" + TEST_SERVICE_NAME + "/" + TEST_SERVICE_INSTANCE,
            String.class
        );
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test 
    void deveResponderHealthCheck() {
        // Verifica se o endpoint de health check está respondendo
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deveExibirInfoEurekaServer() {
        // Verifica se as informações do Eureka Server estão disponíveis
        ResponseEntity<String> response = restTemplate.getForEntity("/eureka/apps", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("applications"));
    }
}
