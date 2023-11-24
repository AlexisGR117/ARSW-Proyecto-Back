package edu.eci.arsw.paintit.model;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.*;

@Getter
@Setter
public class Game {

    public static final String[] WILDCARDS = {"Freeze", "PaintPump"};
    public static final int SIZE = 15;
    private final Cell[][] cells;
    private int duration;
    private Map<String, Player> players;
    private List<Color> availableColors;
    private List<int[]> availableInitialPositions;
    private Random random;
    private Player winner;
    private Player host;
    private boolean finishedGame;
    private ArrayList<Cell> cellsWithWildcard;

    public Game() {
        cells = new Cell[SIZE][SIZE];
        random = new Random();
        initializationGame();
    }

    public void initializationGame() {
        duration = 90;
        players = new HashMap<>();
        cellsWithWildcard = new ArrayList<>();
        availableColors = new ArrayList<>(Arrays.asList(Color.RED, Color.CYAN, Color.ORANGE, Color.BLUE, Color.YELLOW));
        availableInitialPositions = new ArrayList<>(Arrays.asList(new int[]{0, 0}, new int[]{0, SIZE - 1}, new int[]{SIZE - 1, 0}, new int[]{SIZE - 1, SIZE - 1}));
        finishedGame = false;
        winner = null;
        host = null;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
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
        if (nameExist(player.getName())) throw new PaintItException(PaintItException.EXISTING_PLAYER);
        if (players.size() == 4) throw new PaintItException(PaintItException.FULL_GAME);
        if (player.getName().length() > 20) throw new PaintItException(PaintItException.LONG_NAME);
        if (player.getName().length() < 3) throw new PaintItException(PaintItException.SHORT_NAME);
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

    public void movePlayer(String playerName, int x, int y) throws PaintItException {
        validateMove(playerName, x, y);
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
        return 0 <= x && x < SIZE && 0 <= y && y < SIZE;
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

    public void initStartGame() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                endGame();
            }
        };
        timer.schedule(task, duration * 1000L);
        timerWildcards();
    }

    public void addRandomWildcard() throws ReflectiveOperationException {
        String wildcard = WILDCARDS[random.nextInt(WILDCARDS.length)];
        Class<?> cls = Class.forName("edu.eci.arsw.paintit.model." + wildcard);
        Constructor<?>[] cons = cls.getConstructors();
        Cell cell = getRandomCell();
        cellsWithWildcard.add(cell);
        cell.setWildcard((Wildcard) cons[0].newInstance());
    }

    public void timerWildcards() {
        if (!finishedGame) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        addRandomWildcard();
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                    timerWildcards();
                }
            };
            timer.schedule(task, 15000);
        }
    }

    public Cell getRandomCell() {
        int y = random.nextInt(SIZE);
        int x = random.nextInt(SIZE);
        for (Player player : players.values()) {
            if (player.getX() == x && player.getY() == y && cells[x][y].getWildcard() == null) {
                return getRandomCell();
            }
        }
        return getCell(x, y);
    }
}
