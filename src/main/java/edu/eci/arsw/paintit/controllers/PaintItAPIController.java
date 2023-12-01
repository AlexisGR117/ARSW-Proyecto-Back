package edu.eci.arsw.paintit.controllers;

import java.util.Map;

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
    public ResponseEntity<?> handlerGetGames() {
        try {
            return new ResponseEntity<>(paintiItServices.getAllGames(), HttpStatus.ACCEPTED);
        } catch (PaintItException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{gameCode}/players")
    public ResponseEntity<?> handlerGetPlayers(@PathVariable("gameCode") int gameCode) {
        return new ResponseEntity<>(paintiItServices.getAllJugadores(gameCode), HttpStatus.ACCEPTED);
    }

    @PostMapping
    public ResponseEntity<?> handlerPostGame(@RequestBody Map<String, Integer> gameConfig) {
        try {
            return new ResponseEntity<>(paintiItServices.addGame(gameConfig), HttpStatus.CREATED);
        } catch (PaintItException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/{gameCode}/players")
    public ResponseEntity<?> handlerPostPlayers(@PathVariable("gameCode") int gameCode, @RequestBody Player player) {
        try {
            paintiItServices.addNewPlayerToGame(gameCode, player);
            msgt.convertAndSend("/topic/newplayer." + gameCode, paintiItServices.getAllJugadores(gameCode));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (PaintItException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{idGame}")
    public ResponseEntity<?> handlerGetGame(@PathVariable("idGame") int idGame) {
        return new ResponseEntity<>(paintiItServices.getGame(idGame), HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/boardsizes")
    public ResponseEntity<?> handlerGetBoardSizes() {
        return new ResponseEntity<>(paintiItServices.getBoardSizes(), HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/gametimes")
    public ResponseEntity<?> handlerGetGameTimes() {
        return new ResponseEntity<>(paintiItServices.getGameTimes(), HttpStatus.ACCEPTED);
    }
    
}
