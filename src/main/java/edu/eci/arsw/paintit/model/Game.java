package edu.eci.arsw.paintit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Game {

    private int duration;
    private Map<String, Player> players;
    private List<Color> availableColors;
    private List<int[]> availableInitialPositions;
    private Board board;
    private Random random;
    private Player winner;
    private Player host;
    private boolean finishedGame;

    public Game() {
        initializationGame();
    }

    public void initializationGame() {
        duration = 90;
        players = new HashMap<>();
        availableColors = new ArrayList<>(Arrays.asList(Color.RED, Color.CYAN, Color.ORANGE, Color.BLUE, Color.YELLOW));
        availableInitialPositions = new ArrayList<>(Arrays.asList(new int[] { 0, 0 }, new int[] { 0, Board.SIZE - 1 }, new int[] { Board.SIZE - 1, 0 }, new int[] { Board.SIZE - 1, Board.SIZE - 1 }));
        random = new Random();
        finishedGame = false;
        board = new Board();
        winner = null;
        host = null;
    }

    public List<Player> getPlayers() {
        List<Player> playersList = new ArrayList<>(players.values());
        playersList.remove(host);
        playersList.add(0, host);
        return playersList;
    }

    public synchronized void addNewPlayerToGame(Player player) throws PaintItException {
        if (nameExist(player.getName())) throw new PaintItException(PaintItException.EXISTING_PLAYER);
        if (players.size() == 4) throw new PaintItException(PaintItException.FULL_GAME);
        if (player.getName().length() > 20) throw new PaintItException(PaintItException.LONG_NAME);
        if (player.getName().length() < 3) throw new PaintItException(PaintItException.SHORT_NAME);
        if (host == null) {
            host = player;
        }
        int[] initialPosition = selectAvailableInitialPosition();
        player.setColor(selectAvailableColor());
        player.setX(initialPosition[0]);
        player.setY(initialPosition[1]);
        player.getScore().incrementAndGet();
        Cell cell = board.getCell(initialPosition[0], initialPosition[1]);
        cell.setPaintedBy(player);
        players.put(player.getName(), player);
    }

    public void movePlayer(String playerName, int x, int y) throws PaintItException {
        validateMove(playerName, x, y);
        Player player = players.get(playerName);
        Cell cell = board.getCell(x, y);
        updateCellAndPlayer(player, x, y, cell);
    }

    private synchronized void updateCellAndPlayer(Player player, int x, int y, Cell cell) {
        if (cell.getPaintedBy() != null)
            cell.getPaintedBy().getScore().decrementAndGet();
        player.setX(x);
        player.setY(y);
        player.getScore().incrementAndGet();
        cell.setPaintedBy(player);
    }

    private Color selectAvailableColor() {
        int index = random.nextInt(availableColors.size());
        return availableColors.remove(index);
    }

    private int[] selectAvailableInitialPosition() {
        int index = random.nextInt(availableInitialPositions.size());
        return availableInitialPositions.remove(index);
    }

    public boolean nameExist(String name) {
        return players.containsKey(name);
    }

    public boolean isInsideBoard(int x, int y) {
        return 0 <= x && x < Board.SIZE && 0 <= y && y < Board.SIZE;
    }

    public boolean isPlayerOnCell(Cell cell, int x, int y) {
        return cell.getPaintedBy() != null && cell.getPaintedBy().getX() == x && cell.getPaintedBy().getY() == y;
    }

    public boolean isInvalidMove(Player player, int x, int y) {
        return Math.abs(player.getX() - x) + Math.abs(player.getY() - y) != 1;
    }

    private void validateMove(String playerName, int x, int y) throws PaintItException {
        if (finishedGame) throw new PaintItException(PaintItException.GAME_FINISHED);
        if (!nameExist(playerName)) throw new PaintItException(PaintItException.NONE_EXISTENT_PLAYER);
        if (!isInsideBoard(x, y)) throw new PaintItException(PaintItException.OFF_BOARD);
        Player player = players.get(playerName);
        if (isInvalidMove(player, x, y)) throw new PaintItException(PaintItException.INVALID_MOVE);
        Cell cell = board.getCell(x, y);
        if (isPlayerOnCell(cell, x, y)) throw new PaintItException(PaintItException.OCCUPIED_BOX);
    }

    public Player getWinner() {
        winner = players.values().stream().max(Comparator.comparingInt(player -> player.getScore().get())).orElse(null);
        return winner;
    }

    public void initStartGame() {
        Timer timer = new Timer();
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                finishedGame = true;
            }
        };
        timer.schedule(tarea, duration * 1000);
    }

    public void resetGame() {
        initializationGame();
    }

}
