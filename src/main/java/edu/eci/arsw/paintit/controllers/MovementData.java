package edu.eci.arsw.paintit.controllers;

import edu.eci.arsw.paintit.model.Cell;
import edu.eci.arsw.paintit.model.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovementData {

    private String playerName;
    private Cell[][] cells;
    private List<Player> players;
    private String wildcard;
    private List<Cell> cellsWithWildcards;
    private int remainingMoves;
    private int remainingFrozenMoves;
    private int x;
    private int y;

}