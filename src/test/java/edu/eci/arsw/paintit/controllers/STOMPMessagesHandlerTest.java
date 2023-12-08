package edu.eci.arsw.paintit.controllers;


import edu.eci.arsw.paintit.model.Game;
import edu.eci.arsw.paintit.model.PaintItException;
import edu.eci.arsw.paintit.model.Player;
import edu.eci.arsw.paintit.services.PaintItServices;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class STOMPMessagesHandlerTest {

    @Mock
    private PaintItServices paintItServices;

    @Mock
    private SimpMessagingTemplate msgt;

    @InjectMocks
    private STOMPMessagesHandler stompMessagesHandler;


    @Test
    void handleNewPlayerEvent_shouldSendMessage() {
        int idGame = 1;
        Player player = new Player();
        player.setName("John");
        Player player2 = new Player();
        player2.setName("Mateo");
        List<Player> expectedPlayers = Arrays.asList(player, player2);
        when(paintItServices.getAllJugadores(idGame)).thenReturn(expectedPlayers);
        stompMessagesHandler.handleNewPlayerEvent(idGame);
        verify(msgt).convertAndSend("/topic/newplayer." + idGame, expectedPlayers);
    }

    @Test
    void handleNewMovementEvent_shouldSendMessage() throws PaintItException {
        int idGame = 1;
        MovementData movementData = new MovementData();
        Player player = new Player();
        player.setName("John");
        Player player2 = new Player();
        player2.setName("Mateo");
        List<Player> players = Arrays.asList(player, player2);
        movementData.setPlayers(players);
        movementData.setPlayerName(player.getName());
        movementData.setX(1);
        movementData.setY(0);
        Game game = new Game(15, 1, idGame);
        when(paintItServices.getGame(idGame)).thenReturn(game);
        doNothing().when(paintItServices).movePlayer(eq(idGame), anyString(), anyInt(), anyInt());
        stompMessagesHandler.handleNewMovementEvent(movementData, idGame);
        verify(msgt).convertAndSend("/topic/updateboard." + idGame, movementData);
    }

    @Test
    void handleNewMovementEvent_GameFinished_shouldSendMessage() throws PaintItException {
        int idGame = 1;
        MovementData movementData = new MovementData();
        Player player = new Player();
        player.setName("John");
        Player player2 = new Player();
        player2.setName("Mateo");
        List<Player> players = Arrays.asList(player, player2);
        movementData.setPlayers(players);
        movementData.setPlayerName(player.getName());
        movementData.setX(1);
        movementData.setY(0);
        Game game = new Game(15, 1, idGame);
        game.addNewPlayerToGame(player);
        game.addNewPlayerToGame(player2);
        player2.getScore().incrementAndGet();
        game.endGame();
        when(paintItServices.getGame(idGame)).thenReturn(game);
        doThrow(new PaintItException(PaintItException.GAME_FINISHED))
                .when(paintItServices).movePlayer(eq(idGame), anyString(), anyInt(), anyInt());
        stompMessagesHandler.handleNewMovementEvent(movementData, idGame);
        verify(msgt).convertAndSend("/topic/gamefinished." + idGame, game.getWinner().getName());
    }

    @Test
    void handleStartTimeEvent_shouldSetStartedGame() {
        int idGame = 1;
        Game game = new Game(15, 1, idGame);
        when(paintItServices.getGame(idGame)).thenReturn(game);
        stompMessagesHandler.handleStartTimeEvent(idGame);
        verify(paintItServices).getGame(idGame);
    }
}

