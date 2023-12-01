package edu.eci.arsw.paintit.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.eci.arsw.paintit.services.PaintItServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaintItAPIControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    PaintItServices paintItServices;

    @Test
    public void shouldNotReturnAGameWithAnInvalidId() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity("/games/1000", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    public void shouldReturnBoardSize() {
        // Act
        ResponseEntity<int[]> response = restTemplate.getForEntity("/games/boardsizes", int[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldReturnGameTime() {
        // Act
        ResponseEntity<int[]> response = restTemplate.getForEntity("/games/gametimes", int[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldCreateANewGame() {
        // Arrange
        Map<String, Integer> gameConfig = Map.of("boardSize", 15, "gameTime", 15);
        
        // Act
        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/games", gameConfig, Void.class);

        // Assert
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void shouldReturnNoGameCodeException() {
        // Arrange
        Map<String, Integer> gameConfig = Map.of("boardSize", 15, "gameTime", 15);
        for (int i = 1; i <= 10; i++) {
            restTemplate.postForEntity("/games", gameConfig, Void.class);
        }

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity("/games", gameConfig, String.class);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        for (int i = 1; i <= 10; i++) {
            paintItServices.restartGame(i);
        }
    }

    @Test    
    public void shouldReturnAllGames() {
        // Arrange
        Map<String, Integer> gameConfig = Map.of("boardSize", 15, "gameTime", 15);
        for (int i = 1; i <= 10; i++) {
            restTemplate.postForEntity("/games", gameConfig, Void.class);
        }

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity("/games", String.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        for (int i = 1; i <= 10; i++) {
            paintItServices.restartGame(i);
        }
    }

    @Test
    public void shouldNotReturnAllGames() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity("/games", String.class);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}