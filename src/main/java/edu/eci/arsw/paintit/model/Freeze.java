package edu.eci.arsw.paintit.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Timer;
import java.util.TimerTask;

@JsonSerialize(using = WildcardSerializer.class)
public class Freeze extends Wildcard {

    @Override
    public void activate(Game game, Player player) {
        for (Player playerGame : game.getPlayers()) {
            if (player != playerGame) playerGame.freeze();
        }
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (Player playerGame : game.getPlayers()) {
                    if (player != playerGame) playerGame.unfreeze();
                }
            }
        };
        timer.schedule(task, 5000);
    }

    @Override
    public String toString() {
        return "Freeze";
    }
}
