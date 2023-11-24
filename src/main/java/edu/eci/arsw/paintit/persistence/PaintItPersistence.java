package edu.eci.arsw.paintit.persistence;

import edu.eci.arsw.paintit.model.Cell;
import edu.eci.arsw.paintit.model.Game;
import edu.eci.arsw.paintit.model.PaintItException;
import edu.eci.arsw.paintit.model.Player;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaintItPersistence {

    private final HashMap<Integer, Game> games = new HashMap<>();

    public PaintItPersistence() {
        games.put(1, new Game());
    }

    public Map<Integer, Game> getGames() throws PaintItException {
        Set<Game> allGames = new HashSet<Game>();
        games.forEach((key, value) -> allGames.add(value));
        if (allGames.isEmpty()) {
            throw new PaintItException(PaintItException.NO_GAMES);
        }
        return games;
    }

    public void addNewGame(int idGame) {
        games.put(idGame, new Game());
    }

    public List<Player> getPlayersByGame(int idGame) throws PaintItException {
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

    public void restartGame(int idGame) {
        games.get(idGame).resetGame();
    }

    public List<Cell> getCellsWithWildcard(int idGame) {
        return games.get(idGame).getCellsWithWildcard();
    }

    public Cell[][] getCells(int idGame) {
        return games.get(idGame).getCells();
    }

}
