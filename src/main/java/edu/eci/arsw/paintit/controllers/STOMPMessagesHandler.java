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
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class STOMPMessagesHandler {

    PaintItServices paintItServices;
    SimpMessagingTemplate msgt;

    @Autowired
    public STOMPMessagesHandler(SimpMessagingTemplate msgt, PaintItServices paintItServices) {
        this.msgt = msgt;
        this.paintItServices = paintItServices;
    }

    @MessageMapping("/newplayer.{idGame}")
    public void handleNewPlayerEvent(String playerName, @DestinationVariable int idGame) {
        msgt.convertAndSend("/topic/newplayer." + idGame, paintItServices.getAllJugadores(idGame));
    }

    @MessageMapping("/newmovement.{idGame}")
    public void handleNewMovementEvent(MovementData data, @DestinationVariable int idGame) {
        String player = data.getPlayerName();
        MovementData.Movement movement = data.getMovement();
        try {
            paintItServices.movePlayer(idGame, player, movement.getX(), movement.getY());
            List<Player> players = paintItServices.getAllJugadores(idGame);
            Cell[][] cells = paintItServices.getCells(idGame);
            msgt.convertAndSend("/topic/updatescore." + idGame, players);
            data.setPlayers(players);
            data.setCells(cells);
            msgt.convertAndSend("/topic/updateboard." + idGame, data);
            msgt.convertAndSend("/topic/updatewildcards." + idGame, paintItServices.getCellsWithWildcard(idGame));
            msgt.convertAndSend("/topic/updateremainingmoves." + idGame, paintItServices.getRemainingMoves(idGame));
        } catch (PaintItException e) {
            if (e.getMessage().equals(PaintItException.GAME_FINISHED)) {
                msgt.convertAndSend("/topic/gamefinished." + idGame, paintItServices.getGame(idGame).getWinner().getName());
                paintItServices.restartGame(idGame);
            }
        }
    }

    @MessageMapping("/startTime.{idGame}")
    public void handleStartTimeEvent(@DestinationVariable int idGame) {
        Game game = paintItServices.getGame(idGame);
        game.setStartedGame(true);
    }
}