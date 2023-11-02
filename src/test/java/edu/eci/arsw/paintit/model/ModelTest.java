package edu.eci.arsw.paintit.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ModelTest {

    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game();
    }

    @Test
    void addNewPlayer_ValidName_AddsPlayer() throws PaintItException {
        String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        game.addNewPlayerToGame(player);
        List<Player> players = game.getPlayers();
        assertEquals(1, players.size());
        assertEquals(playerName, players.get(0).getName());
    }

    @Test
    void addNewPlayer_ExistingName_ThrowsException() {
        String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        try {
            game.addNewPlayerToGame(player);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        try {
            game.addNewPlayerToGame(player);
            fail("Did not throw exception");
        } catch (PaintItException e) {
            assertEquals(PaintItException.EXISTING_PLAYER, e.getMessage());
        }
    }

    @Test
    void addNewPlayer_FullGame_ThrowsException() {

        for (int i = 0; i < 4; i++) {
            try {
                Player player = new Player();
                player.setName("Player" + i);
                game.addNewPlayerToGame(player);
            } catch (PaintItException e) {
                fail("Threw a exception");
            }
        }
        try {
            String playerName = "ExtraPlayer";
            Player player = new Player();
            player.setName(playerName);
            game.addNewPlayerToGame(player);
            fail("Did not throw exception");
        } catch (PaintItException e) {
            assertEquals(PaintItException.FULL_GAME, e.getMessage());
        }
    }

    @Test
    void movePlayer_ValidMove_UpdatesPlayerPositionAndScore() {
        String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        try {
            game.addNewPlayerToGame(player);
            int initialX = game.getPlayers().get(0).getX();
            int initialY = game.getPlayers().get(0).getY();
            int initialScore = game.getPlayers().get(0).getScore().get();
            int newX = initialX == 0 ? initialX + 1 : initialX - 1;
            game.movePlayer(playerName, newX, initialY);
            assertEquals(newX, game.getPlayers().get(0).getX());
            assertEquals(initialY, game.getPlayers().get(0).getY());
            assertEquals(initialScore + 1, game.getPlayers().get(0).getScore().get());
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
    }

    @Test
    void movePlayer_NonExistentPlayer_ThrowsException() {
        try {
            game.movePlayer("John", 1, 1);
            fail("Did not throw exception");
        } catch (PaintItException e) {
            assertEquals(PaintItException.NONE_EXISTENT_PLAYER, e.getMessage());
        }
    }

    @Test
    void movePlayer_OffBoardPosition_ThrowsException() {
        String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        try {
            game.addNewPlayerToGame(player);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        try {
            game.movePlayer(playerName, Board.SIZE + 1, Board.SIZE + 1);
            fail("Did not throw exception");
        } catch (PaintItException e) {
            assertEquals(PaintItException.OFF_BOARD, e.getMessage());
        }
    }

    @Test
    void movePlayer_OccupiedCell_ThrowsException() {
        Player playerLowerLeft = null;
        try {
            for (int i = 0; i < 4; i++) {
                Player player = new Player();
                player.setName("Player" + i);
                game.addNewPlayerToGame(player);
            }
            List<Player> players = game.getPlayers();
            for (Player player : players) {
                if (player.getX() == 0 && player.getY() == 0) {
                    playerLowerLeft = player;
                    break;
                }
            }
            for (int i = 1; i < Board.SIZE - 1; i++) {
                game.movePlayer(playerLowerLeft.getName(), i, 0);
            }
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        try {
            game.movePlayer(playerLowerLeft.getName(), 14, 0);
            fail("Did not throw exception");
        } catch (PaintItException e) {
            assertEquals(PaintItException.OCCUPIED_BOX, e.getMessage());
        }
    }

    @Test
    void movePlayer_InvalidMovement_ThrowsException() {
       String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        try {
            game.addNewPlayerToGame(player);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        int initialX = game.getPlayers().get(0).getX();
        int initialY = game.getPlayers().get(0).getY();
        try {
            game.movePlayer(playerName, initialX == 0 ? initialX + 2 : initialX - 2, initialY);
            fail("Did not throw exception");
        } catch (PaintItException e) {
            assertEquals(PaintItException.INVALID_MOVE, e.getMessage());
        }
    }
}