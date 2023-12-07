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
        try {
            paintItServices.movePlayer(idGame, player, data.getX(), data.getY());
            Game game = paintItServices.getGame(idGame);
            data.setPlayers(game.getPlayers());
            data.setCells(game.getCells());
            data.setCellsWithWildcards(game.getCellsWithWildcard());
            data.setRemainingMoves(game.getRemainingMoves());
            data.setRemainingFrozenMoves(game.getRemainingFrozenMoves());
            msgt.convertAndSend("/topic/updateboard." + idGame, data);
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