package edu.eci.arsw.paintit.model;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.security.SecureRandom;
import java.util.List;
import java.util.*;

import static java.util.Arrays.asList;

@Getter
@Setter
public class Game implements Serializable {

    public static final List<Double> GAME_TIMES = List.of(1.0, 1.5, 2.0);
    public static final List<Integer> BOARD_SIZES = List.of(15, 20, 25);
    public static final List<String> WILDCARDS = List.of("Freeze", "PaintPump");
    private final Cell[][] cells;
    private int size;
    private int id;
    private float duration;
    private Map<String, Player> players;
    private List<Color> availableColors;
    private List<int[]> availableInitialPositions;
    private Random random;
    private Player winner;
    private Player host;
    private boolean finishedGame;
    private boolean startedGame;
    private ArrayList<Cell> cellsWithWildcard;
    private int remainingMoves;
    private int remainingFrozenMoves;

    public Game(int size, float duration, int id) {
        cells = new Cell[size][size];
        random = new SecureRandom();
        initializationGame(size, duration, id);
    }

    public void initializationGame(int size, float duration, int id) {
        this.duration = duration;
        this.size = size;
        this.id = id;
        remainingMoves = (int) (duration * size * size);
        players = new HashMap<>();
        cellsWithWildcard = new ArrayList<>();
        availableColors = new ArrayList<>(asList(Color.RED, Color.CYAN, Color.ORANGE, Color.BLUE, Color.YELLOW));
        availableInitialPositions = new ArrayList<>(asList(new int[]{0, 0}, new int[]{0, size - 1}, new int[]{size - 1, 0}, new int[]{size - 1, size - 1}));
        finishedGame = false;
        winner = null;
        host = null;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public List<Player> getPlayers() {
        List<Player> playersList = new ArrayList<>(players.values());
        playersList.remove(host);
        playersList.add(0, host);
        return playersList;
    }

    public synchronized void addNewPlayerToGame(Player player) throws PaintItException {
        validatePlayer(player);
        if (host == null) host = player;
        int[] initialPosition = selectAvailableInitialPosition();
        player.setColor(selectAvailableColor());
        player.getScore().incrementAndGet();
        Cell cell = cells[initialPosition[0]][initialPosition[1]];
        player.setX(initialPosition[0]);
        player.setY(initialPosition[1]);
        cell.setPaintedBy(player);
        players.put(player.getName(), player);
    }

    private void validatePlayer(Player player) throws PaintItException {
        if (nameExist(player.getName())) throw new PaintItException(PaintItException.EXISTING_PLAYER);
        if (players.size() == 4) throw new PaintItException(PaintItException.FULL_GAME);
        if (player.getName().length() > 20) throw new PaintItException(PaintItException.LONG_NAME);
        if (player.getName().length() < 3) throw new PaintItException(PaintItException.SHORT_NAME);
        if (this.startedGame) throw new PaintItException(PaintItException.STARTED_GAME);
    }

    public void movePlayer(String playerName, int x, int y) throws PaintItException {
        validateMove(playerName, x, y);
        decrementRemainingMoves();
        Player player = players.get(playerName);
        Cell cell = getCell(x, y);
        synchronized (cells[x][y]) {
            if (cell.isPlayerOnCell()) throw new PaintItException(PaintItException.OCCUPIED_BOX);
            cell.paint(player);
            player.setX(x);
            player.setY(y);
            Wildcard wildcard = cell.getWildcard();
            if (wildcard != null) {
                wildcard.activate(this, player);
                cell.setWildcard(null);
                cellsWithWildcard.remove(cell);
            }
        }
    }

    public synchronized void decrementRemainingMoves() throws PaintItException {
        remainingMoves--;
        if (remainingMoves % 150 == 0) addRandomWildcard();
        if (remainingMoves == 0) endGame();
        if (remainingFrozenMoves > 0 && --remainingFrozenMoves <= 0) {
            players.forEach((key, value) -> {
                if (value.isFrozen()) value.unfreeze();
            });
        }
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
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

    public boolean isInvalidMove(Player player, int x, int y) {
        return Math.abs(player.getX() - x) + Math.abs(player.getY() - y) != 1;
    }

    public boolean isInsideBoard(int x, int y) {
        return 0 <= x && x < this.size && 0 <= y && y < this.size;
    }

    private void validateMove(String playerName, int x, int y) throws PaintItException {
        if (finishedGame) throw new PaintItException(PaintItException.GAME_FINISHED);
        if (!nameExist(playerName)) throw new PaintItException(PaintItException.NONE_EXISTENT_PLAYER);
        if (!isInsideBoard(x, y)) throw new PaintItException(PaintItException.OFF_BOARD);
        Player player = players.get(playerName);
        if (isInvalidMove(player, x, y)) throw new PaintItException(PaintItException.INVALID_MOVE);
        if (players.get(playerName).isFrozen()) throw new PaintItException(PaintItException.FROZEN_PLAYER);
    }

    public void endGame() {
        winner = players.values().stream().max(Comparator.comparingInt(player -> player.getScore().get())).orElse(null);
        finishedGame = true;
    }

    public void addRandomWildcard() throws PaintItException {
        try {
            String wildcard = WILDCARDS.get(random.nextInt(WILDCARDS.size()));
            Class<?> cls = Class.forName("edu.eci.arsw.paintit.model." + wildcard);
            Constructor<?>[] cons = cls.getConstructors();
            Cell cell = getRandomCell();
            cellsWithWildcard.add(cell);
            cell.setWildcard((Wildcard) cons[0].newInstance());
        } catch (ReflectiveOperationException ignored) {
            throw new PaintItException(PaintItException.INVALID_WILDCARD);
        }

    }

    public Cell getRandomCell() {
        int y = random.nextInt(this.size);
        int x = random.nextInt(this.size);
        for (Player player : players.values()) {
            if (player.getX() == x && player.getY() == y && cells[x][y].getWildcard() == null) {
                return getRandomCell();
            }
        }
        return getCell(x, y);
    }
}
