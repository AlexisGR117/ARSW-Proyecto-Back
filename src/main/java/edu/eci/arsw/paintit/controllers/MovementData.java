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
    private Movement movement;
    private Cell[][] cells;
    private List<Player> players;
    private String wildcard;

    @Getter
    @Setter
    public static class Movement {

        private int x;
        private int y;

    }

}