package edu.eci.arsw.paintit.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game();
    }

    @Test
    void addNewPlayer_ValidName_AddsPlayer() {
        String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        List<Color> initialAvailableColors = new ArrayList<>(game.getAvailableColors());
        List<int[]> initialAvailableInitialPositions = new ArrayList<>(game.getAvailableInitialPositions());
        try {
            game.addNewPlayerToGame(player);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        List<Player> players = game.getPlayers();
        List<Color> availableColors = game.getAvailableColors();
        List<int[]> availableInitialPositions = game.getAvailableInitialPositions();
        assertEquals(initialAvailableColors.size() - 1, availableColors.size());
        assertEquals(initialAvailableInitialPositions.size() - 1, availableInitialPositions.size());
        assertFalse(availableColors.contains(player.getColor()));
        int[] initialPosition = {player.getX(), player.getY()};
        assertFalse(availableInitialPositions.contains(initialPosition));
        assertEquals(1, players.size());
        assertEquals(playerName, players.get(0).getName());
    }

    @Test
    void gameConstructor_SuccessfullyCreatedGame() {
        assertNotNull(game.getRandom());
        assertNotEquals(0, game.getDuration());
        assertNotNull(game.getCellsWithWildcard());
        assertEquals(0, game.getCellsWithWildcard().size());
        assertNotNull(game.getAvailableColors());
        assertTrue(game.getAvailableColors().size() > 0);
        assertNotNull(game.getAvailableInitialPositions());
        assertTrue(game.getAvailableInitialPositions().size() > 0);
        assertFalse(game.isFinishedGame());
        assertNull(game.getWinner());
        assertNull(game.getHost());
        Cell[][] cells = game.getCells();
        assertEquals(Game.SIZE, cells.length);
        for (int i = 0; i < Game.SIZE; i++) {
            assertEquals(Game.SIZE, cells[i].length);
            for (int j = 0; j < Game.SIZE; j++) {
                assertNotNull(cells[i][j]);
                assertEquals(i, cells[i][j].getX());
                assertEquals(j, cells[i][j].getY());
                assertNull(cells[i][j].getPaintedBy());
                assertNull(cells[i][j].getWildcard());
            }
        }
    }

    @Test
    void addNewPlayer_Host_AssignsGameHost() {
        String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        try {
            game.addNewPlayerToGame(player);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        assertEquals(player, game.getHost());
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
    void addNewPlayer_LongName_ThrowsException() {
        String longName = "ThisIsAVeryLongNameThatExceedsTwentyCharacters";
        Player player = new Player();
        player.setName(longName);
        try {
            game.addNewPlayerToGame(player);
            fail("Did not throw exception");
        } catch (PaintItException e) {
            assertEquals(PaintItException.LONG_NAME, e.getMessage());
        }
    }

    @Test
    void addNewPlayer_ShortName_ThrowsException() {
        String longName = "A";
        Player player = new Player();
        player.setName(longName);
        try {
            game.addNewPlayerToGame(player);
            fail("Did not throw exception");
        } catch (PaintItException e) {
            assertEquals(PaintItException.SHORT_NAME, e.getMessage());
        }
    }

    @Test
    void addRandomWildcard_AddsWildcard() {
        try {
            game.addRandomWildcard();
        } catch (ReflectiveOperationException e) {
            fail("Threw a exception");
        }
        Cell[][] cells = game.getCells();
        ArrayList<Wildcard> wildcards = new ArrayList<>();
        for (Cell[] row : cells)
            for (Cell cell : row) {
                if (cell.getWildcard() != null) wildcards.add(cell.getWildcard());
            }
        assertEquals(1, wildcards.size());
        assertEquals(1, game.getCellsWithWildcard().size());
        assertEquals(wildcards.get(0), game.getCellsWithWildcard().get(0).getWildcard());
        assertTrue(wildcards.get(0).toString().equals("Freeze") || wildcards.get(0).toString().equals("PaintPump"));
    }

    @Test
    void movePlayer_CellWithWildcard_WildcardDisappears() {
        String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        try {
            game.addNewPlayerToGame(player);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        int initialX = player.getX();
        int initialY = player.getY();
        int newX = initialX == 0 ? initialX + 1 : initialX - 1;
        Cell cellWithWildcard = game.getCell(newX, initialY);
        cellWithWildcard.setWildcard(new PaintPump());
        try {
            game.movePlayer(playerName, newX, initialY);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        assertNull(cellWithWildcard.getWildcard());
    }

    @Test
    void active_WildcardPaintPump_PaintCells() {
        String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        try {
            game.addNewPlayerToGame(player);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        int x = player.getX();
        int y = player.getY();
        Cell cellWithWildcard = game.getCell(x == 0 ? x + 1 : x - 1, y);
        cellWithWildcard.setWildcard(new PaintPump());
        int initialScore = player.getScore().get();
        cellWithWildcard.getWildcard().activate(game, player);
        int finalScore = player.getScore().get();
        int countNewCells = 0;
        for (int dc = -2; dc <= 2; dc++)
            for (int dr = -2; dr <= 2; dr++) {
                if (game.isInsideBoard(y + dr, x + dc) && !game.getCell(y + dr, x + dc).isPlayerOnCell()
                        && ((dc != -2 && dc != 2) || (dr != -2 && dr != 2)) && (dc != 0 || dr != 0)) {
                    assertEquals(player, game.getCell(x + dc, y + dr).getPaintedBy());
                    countNewCells++;
                }
            }
        assertEquals("PaintPump", cellWithWildcard.getWildcard().toString());
        assertEquals(finalScore, initialScore + countNewCells);
    }

    @Test
    void active_WildcardFreeze_FreezeOpponents() {
        int x = Game.SIZE / 2;
        int y = Game.SIZE / 2;
        String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        try {
            game.addNewPlayerToGame(player);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        Cell cellWithWildcard = game.getCell(x, y);
        cellWithWildcard.setWildcard(new Freeze());
        assertEquals("Freeze", cellWithWildcard.getWildcard().toString());
        cellWithWildcard.getWildcard().activate(game, player);
        for (Player playerGame : game.getPlayers()) {
            if (player != playerGame) assertTrue(playerGame.isFrozen());
        }
    }

    @Test
    void movePlayer_FrozenPlayer_ThrowsException() {
        String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        try {
            game.addNewPlayerToGame(player);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        player.freeze();
        int initialX = player.getX();
        int initialY = player.getY();
        int newX = initialX == 0 ? initialX + 1 : initialX - 1;
        try {
            game.movePlayer(playerName, newX, initialY);
            fail("Did not throw exception");
        } catch (PaintItException e) {
            assertEquals(PaintItException.FROZEN_PLAYER, e.getMessage());
        }
    }

    @Test
    void getWinner_NoWinner_ReturnsNull() {
        assertNull(game.getWinner());
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
            game.movePlayer(playerName, Game.SIZE + 1, Game.SIZE + 1);
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
            for (int i = 1; i < Game.SIZE - 1; i++) {
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

    @Test
    void movePlayer_FinishedGame_ThrowsException() {
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
        int newX = initialX == 0 ? initialX + 1 : initialX - 1;
        game.endGame();
        try {
            game.movePlayer(playerName, newX, initialY);
            fail("Did not throw exception");
        } catch (PaintItException e) {
            assertEquals(PaintItException.GAME_FINISHED, e.getMessage());
        }
    }

    @Test
    void endGame_FinishedToTrueAndWinner() {
        String playerName = "John";
        Player player = new Player();
        player.setName(playerName);
        try {
            game.addNewPlayerToGame(player);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        String playerName2 = "Mateo";
        Player player2 = new Player();
        player2.setName(playerName2);
        try {
            game.addNewPlayerToGame(player2);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        int initialX = game.getPlayers().get(0).getX();
        int initialY = game.getPlayers().get(0).getY();
        int newX = initialX == 0 ? initialX + 1 : initialX - 1;
        try {
            game.movePlayer(playerName, newX, initialY);
        } catch (PaintItException e) {
            fail("Threw a exception");
        }
        game.endGame();
        assertTrue(game.isFinishedGame());
        assertEquals(player, game.getWinner());
    }

}