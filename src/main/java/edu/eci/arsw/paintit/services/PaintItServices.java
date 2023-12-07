package edu.eci.arsw.paintit.services;

import edu.eci.arsw.paintit.model.Cell;
import edu.eci.arsw.paintit.model.Game;
import edu.eci.arsw.paintit.model.PaintItException;
import edu.eci.arsw.paintit.model.Player;
import edu.eci.arsw.paintit.repositories.PaintItRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaintItServices {

    PaintItRepository paintItRepository;

    private static final Logger logger = LoggerFactory.getLogger(PaintItServices.class);

    @Autowired
    public PaintItServices(PaintItRepository paintItRepository) {
        this.paintItRepository = paintItRepository;
    }

    public List<Player> getAllJugadores(int idGame) {
        Game game = getGame(idGame);
        if (game != null) return game.getPlayers();
        return Collections.emptyList();
    }

    public Map<Integer, Game> getAllGames() throws PaintItException {
        Map<Integer, Game> games = new HashMap<>();
        for (Game game : paintItRepository.findAll()) {
            games.put(game.getId(), game);
        }
        if (games.isEmpty()) {
            throw new PaintItException(PaintItException.NO_GAMES);
        }
        return games;
    }

    public synchronized Integer addGame(Map<String, Integer> gameConfig) throws PaintItException {
        List<Integer> availableGameCodes = getAvailableGameCodes();
        if (availableGameCodes.isEmpty()) {
            throw new PaintItException(PaintItException.NO_GAME_CODE);
        }
        Integer idGame = availableGameCodes.remove(0);
        Game game = new Game(gameConfig.get("boardSize"), gameConfig.get("gameTime"), idGame);
        paintItRepository.saveGame(game);
        logger.info("New game created with id: {}", idGame);
        return idGame;
    }

    public List<Integer> getAvailableGameCodes() {
        List<Integer> availableGameCodes = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (Game game : paintItRepository.findAll()) {
            availableGameCodes.remove((Integer) game.getId());
        }
        return availableGameCodes;
    }


    public Game getGame(int idGame) {
        Optional<Game> optionalGame = paintItRepository.findById(idGame);
        return optionalGame.orElse(null);
    }

    public void movePlayer(int idGame, String playerName, int x, int y) throws PaintItException {
        Game game = getGame(idGame);
        if (game != null) game.movePlayer(playerName, x, y);
        paintItRepository.saveGame(game);
    }

    public void addNewPlayerToGame(int idGame, Player player) throws PaintItException {
        Game game = getGame(idGame);
        if (game != null) game.addNewPlayerToGame(player);
        paintItRepository.saveGame(game);
    }

    public String getWinner(int idGame) {
        Game game = getGame(idGame);
        if (game != null) return game.getWinner().getName();
        return "";
    }

    public void restartGame(int idGame) {
        paintItRepository.deleteById(idGame);
    }

    public List<Cell> getCellsWithWildcard(int idGame) {
        Game game = getGame(idGame);
        if (game != null) return game.getCellsWithWildcard();
        return Collections.emptyList();
    }

    public Cell[][] getCells(int idGame) {
        Game game = getGame(idGame);
        if (game != null) return game.getCells();
        return new Cell[0][0];
    }

    public int[] getBoardSizes() {
        return Game.BOARD_SIZES.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }

    public double[] getGameTimes() {
        return Game.GAME_TIMES.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    public void deleteAllGames() {
        paintItRepository.deleteAll();
    }

}
