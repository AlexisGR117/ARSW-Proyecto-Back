package edu.eci.arsw.paintit.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@JsonSerialize(using = PlayerSerializer.class)
public class Player {

    private final AtomicInteger score = new AtomicInteger(0);
    private String name;
    private Color color;
    private int x;
    private int y;
    private boolean frozen;

    public void freeze() {
        this.frozen = true;
    }

    public void unfreeze() {
        this.frozen = false;
    }

}
