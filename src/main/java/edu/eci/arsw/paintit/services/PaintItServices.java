package edu.eci.arsw.paintit.services;

import edu.eci.arsw.paintit.model.Cell;
import edu.eci.arsw.paintit.model.Game;
import edu.eci.arsw.paintit.model.PaintItException;
import edu.eci.arsw.paintit.model.Player;
import edu.eci.arsw.paintit.persistence.PaintItPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PaintItServices {

    PaintItPersistence paintItPersistence;
    int idGame = 1; // solo para el primer juego

    @Autowired
    public PaintItServices(PaintItPersistence paintItPersistence) {
        this.paintItPersistence = paintItPersistence;
    }

    public List<Player> getAllJugadores(int idGame) throws PaintItException {
        return paintItPersistence.getPlayersByGame(this.idGame);
    }

    public Map<Integer, Game> getAllGames() throws PaintItException {
        return paintItPersistence.getGames();
    }

    public void addGame(int idGame) {
        paintItPersistence.addNewGame(idGame);
    }

    public Game getGame(int idGame) {
        return paintItPersistence.getGame(1);
    }

    public void movePlayer(int idGame, String playerName, int x, int y) throws PaintItException {
        paintItPersistence.movePlayer(this.idGame, playerName, x, y);
    }

    public void addNewPlayerToGame(int idGame, Player player) throws PaintItException {
        paintItPersistence.addNewPlayerToGame(this.idGame, player);
    }

    public String getWinner(int idGame) {
        return paintItPersistence.getWinnerGame(idGame);
    }

    public void restartGame(int idGame) {
        paintItPersistence.restartGame(idGame);
    }

    public List<Cell> getCellsWithWildcard(int idGame) {
        return paintItPersistence.getCellsWithWildcard(idGame);
    }

    public Cell[][] getCells(int idGame) {
        return paintItPersistence.getCells(idGame);
    }

}
