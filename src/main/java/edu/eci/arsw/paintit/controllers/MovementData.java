package edu.eci.arsw.paintit.controllers;

import edu.eci.arsw.paintit.model.Cell;
import edu.eci.arsw.paintit.model.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class MovementData {

    private String playerName;
    private Movement movement;
    private Cell[][] cells;
    private List<Player> players;
    private String wildcard;

    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class Movement {

        private int x;
        private int y;

    }

}