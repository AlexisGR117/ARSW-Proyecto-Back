package edu.eci.arsw.paintit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Player {

    private final AtomicInteger score = new AtomicInteger(0);
    private String name;
    private Color color;
    private int x;
    private int y;
    private String avatar;

    public Player() {

    }

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

}
