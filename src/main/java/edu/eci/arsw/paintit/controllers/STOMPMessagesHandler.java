package edu.eci.arsw.paintit.controllers;

import edu.eci.arsw.paintit.model.Game;
import edu.eci.arsw.paintit.model.PaintItException;
import edu.eci.arsw.paintit.services.PaintItServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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
            msgt.convertAndSend("/topic/updatescore." + idGame, paintitServices.getAllJugadores(idGame));
            msgt.convertAndSend("/topic/updateboard." + idGame, data);
        } catch (PaintItException e) {
            if (e.getMessage() == PaintItException.GAME_FINISHED) {
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

}