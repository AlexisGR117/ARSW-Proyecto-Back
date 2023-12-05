package edu.eci.arsw.paintit.persistence;

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
public class PaintItPersistence {

    private static final Logger logger = LoggerFactory.getLogger(PaintItPersistence.class);
    private final PaintItRepository paintItRepository;
    private final HashMap<Integer, Game> games = new HashMap<>();

    @Autowired
    public PaintItPersistence(PaintItRepository paintItRepository) {
        this.paintItRepository = paintItRepository;
    }

    public Map<Integer, Game> getGames() throws PaintItException {
        Set<Game> allGames = new HashSet<>();
        games.forEach((key, value) -> allGames.add(value));
        if (allGames.isEmpty()) {
            throw new PaintItException(PaintItException.NO_GAMES);
        }
        return games;
    }

    public synchronized Integer addNewGame(Map<String, Integer> gameConfig) throws PaintItException {
        List<Integer> availableGameCodes = getAvailableGameCodes();
        if (availableGameCodes.isEmpty()) {
            throw new PaintItException(PaintItException.NO_GAME_CODE);
        }
        Integer gameCode = availableGameCodes.remove(0);
        Game game = new Game(gameConfig.get("boardSize"), gameConfig.get("gameTime"));
        games.put(gameCode, game);
        logger.info("New game created with id: {}", gameCode);
        paintItRepository.saveGame(gameCode);
        return gameCode;
    }

    public List<Integer> getAvailableGameCodes() {
        List<Integer> availableGameCodes = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (Integer gameCode : paintItRepository.findAll()) {
            availableGameCodes.remove(gameCode);
        }
        return availableGameCodes;
    }

    public List<Player> getPlayersByGame(int idGame) {
        return games.get(idGame).getPlayers();
    }

    public Game getGame(int idGame) {
        return games.get(idGame);
    }

    public void movePlayer(int idGame, String playerName, int x, int y) throws PaintItException {
        games.get(idGame).movePlayer(playerName, x, y);
    }

    public void addNewPlayerToGame(int idGame, Player player) throws PaintItException {
        games.get(idGame).addNewPlayerToGame(player);
    }

    public String getWinnerGame(int idGame) {
        return games.get(idGame).getWinner().getName();
    }

    public synchronized void restartGame(int idGame) {
        paintItRepository.deleteById(idGame);
        games.remove(idGame);
    }

    public List<Cell> getCellsWithWildcard(int idGame) {
        return games.get(idGame).getCellsWithWildcard();
    }

    public Cell[][] getCells(int idGame) {
        return games.get(idGame).getCells();
    }

    public void deleteAllGames() {
        paintItRepository.deleteAll();
        games.clear();
    }

}
