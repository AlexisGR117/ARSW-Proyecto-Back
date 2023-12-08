package edu.eci.arsw.paintit.controllers;

import edu.eci.arsw.paintit.model.Game;
import edu.eci.arsw.paintit.model.PaintItException;
import edu.eci.arsw.paintit.model.Player;
import edu.eci.arsw.paintit.services.PaintItServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/games")
public class PaintItAPIController {

    private static final Logger logger = LoggerFactory.getLogger(PaintItAPIController.class.getName());

    PaintItServices paintiItServices;

    SimpMessagingTemplate msgt;

    @Autowired
    public PaintItAPIController(PaintItServices paintiItServices, SimpMessagingTemplate msgt) {
        this.msgt = msgt;
        this.paintiItServices = paintiItServices;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Map<Integer, Game>> handlerGetGames() {
        Map<Integer, Game> games;
        try {
            games = paintiItServices.getAllGames();
            return ResponseEntity.ok(games);
        } catch (PaintItException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @GetMapping(path = "/{gameCode}/players")
    public ResponseEntity<List<Player>> handlerGetPlayers(@PathVariable("gameCode") int gameCode) {
        return new ResponseEntity<>(paintiItServices.getAllJugadores(gameCode), HttpStatus.ACCEPTED);
    }

    @PostMapping
    public ResponseEntity<Integer> handlerPostGame(@RequestBody Map<String, Integer> gameConfig, UriComponentsBuilder ucb) {
        try {
            Integer gamecode = paintiItServices.addGame(gameConfig);
            return new ResponseEntity<>(gamecode, HttpStatus.CREATED);
        } catch (PaintItException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @PostMapping(path = "/{gameCode}/players")
    public ResponseEntity<String> handlerPostPlayers(@PathVariable("gameCode") int gameCode, @RequestBody Player player) {
        try {
            paintiItServices.addNewPlayerToGame(gameCode, player);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (PaintItException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{idGame}")
    public ResponseEntity<Game> handlerGetGame(@PathVariable("idGame") int idGame) {
        Game game = paintiItServices.getGame(idGame);
        if (game != null) {
            return ResponseEntity.ok(game);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/boardsizes")
    public ResponseEntity<int[]> handlerGetBoardSizes() {
        return ResponseEntity.ok(paintiItServices.getBoardSizes());
    }

    @GetMapping(path = "/gametimes")
    public ResponseEntity<double[]> handlerGetGameTimes() {
        return ResponseEntity.ok(paintiItServices.getGameTimes());
    }
}
