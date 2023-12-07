package edu.eci.arsw.paintit.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public abstract class Wildcard implements Serializable {

    public abstract void activate(Game game, Player player);

}
