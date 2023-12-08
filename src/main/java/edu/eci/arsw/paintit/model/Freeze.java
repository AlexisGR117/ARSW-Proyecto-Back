package edu.eci.arsw.paintit.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = WildcardSerializer.class)
public class Freeze extends Wildcard {

    @Override
    public void activate(Game game, Player player) {
        for (Player playerGame : game.getPlayers()) {
            if (player != playerGame) playerGame.freeze();
        }
        game.setRemainingFrozenMoves(15);
    }

    @Override
    public String toString() {
        return "Freeze";
    }
}
