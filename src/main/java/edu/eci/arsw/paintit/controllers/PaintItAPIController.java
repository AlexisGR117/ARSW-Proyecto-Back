package edu.eci.arsw.paintit.controllers;

import edu.eci.arsw.paintit.model.PaintItException;
import edu.eci.arsw.paintit.model.Player;
import edu.eci.arsw.paintit.services.PaintItServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/games")
public class PaintItAPIController {

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
            Logger.getLogger(PaintItAPIController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{gameCode}/players")
    public ResponseEntity<?> handlerGetPlayers(@PathVariable("gameCode") int gameCode) {
        try {
            return new ResponseEntity<>(paintiItServices.getAllJugadores(gameCode), HttpStatus.ACCEPTED);
        } catch (PaintItException e) {
            Logger.getLogger(PaintItAPIController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> handlerPostGame(Integer gameCode) {
        paintiItServices.addGame(gameCode);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(path = "/{gameCode}/players")
    public ResponseEntity<?> handlerPostPlayers(@PathVariable("gameCode") int gameCode, @RequestBody Player player) {
        try {
            paintiItServices.addNewPlayerToGame(gameCode, player);
            msgt.convertAndSend("/topic/newplayer." + gameCode, paintiItServices.getAllJugadores(gameCode));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (PaintItException e) {
            Logger.getLogger(PaintItAPIController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{idGame}")
    public ResponseEntity<?> handlerGetGame(@PathVariable("idGame") int idGame) {
        return new ResponseEntity<>(paintiItServices.getGame(idGame), HttpStatus.ACCEPTED);
    }
}
