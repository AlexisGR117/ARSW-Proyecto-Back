package edu.eci.arsw.paintit.controllers;

import edu.eci.arsw.paintit.model.Cell;
import edu.eci.arsw.paintit.model.Game;
import edu.eci.arsw.paintit.model.PaintItException;
import edu.eci.arsw.paintit.model.Player;
import edu.eci.arsw.paintit.services.PaintItServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class STOMPMessagesHandler {

    @Autowired
    PaintItServices paintitServices;
    @Autowired
    SimpMessagingTemplate msgt;

    @MessageMapping("/newplayer.{idGame}")
    public void handleNewPlayerEvent(String playerName, @DestinationVariable int idGame) throws Exception {
        msgt.convertAndSend("/topic/newplayer." + idGame, paintitServices.getAllJugadores(idGame));
    }

    @MessageMapping("/newmovement.{idGame}")
    public void handleNewMovementEvent(MovementData data, @DestinationVariable int idGame) throws Exception {
        String player = data.getPlayerName();
        MovementData.Movement movement = data.getMovement();
        try {
            paintitServices.movePlayer(idGame, player, movement.getX(), movement.getY());
            List<Player> players = paintitServices.getAllJugadores(idGame);
            Cell[][] cells = paintitServices.getCells(idGame);
            msgt.convertAndSend("/topic/updatescore." + idGame, players);
            data.setPlayers(players);
            data.setCells(cells);
            msgt.convertAndSend("/topic/updateboard." + idGame, data);
        } catch (PaintItException e) {
            if (e.getMessage().equals(PaintItException.GAME_FINISHED)) {
                msgt.convertAndSend("/topic/gamefinished." + idGame, paintitServices.getGame(idGame).getWinner().getName());
                paintitServices.restartGame(idGame);
            }
        }
    }

    @MessageMapping("/startTime.{idGame}")
    public void handleStartTimeEvent(@DestinationVariable int idGame) throws Exception {
        Game game = paintitServices.getGame(idGame);
        game.initStartGame();
    }

    @Scheduled(fixedRate = 1000)
    public void getWildcards() throws PaintItException {
        Map<Integer, Game> games = paintitServices.getAllGames();
        games.forEach((key, value) -> msgt.convertAndSend("/topic/updatewildcards." + key, paintitServices.getCellsWithWildcard(key)));
    }

}