package edu.eci.arsw.paintit.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class Wildcard {

    public abstract void activate(Game game, Player player);

}