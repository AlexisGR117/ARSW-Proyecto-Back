package edu.eci.arsw.paintit.controllers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class MovementData {

    private String playerName;
    private Movement movement;

    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class Movement {

        private int x;
        private int y;

    }

}