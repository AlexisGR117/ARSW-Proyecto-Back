package edu.eci.arsw.paintit.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = WildcardSerializer.class)
public class PaintPump extends Wildcard {

    @Override
    public void activate(Game game, Player player) {
        int x = player.getX();
        int y = player.getY();
        for (int dc = -2; dc <= 2; dc++)
            for (int dr = -2; dr <= 2; dr++) {
                if (game.isInsideBoard(y + dr, x + dc) && !game.getCell(y + dr, x + dc).isPlayerOnCell()
                        && ((dc != -2 && dc != 2) || (dr != -2 && dr != 2)) && (dc != 0 || dr != 0)) {
                    game.getCell(x + dc, y + dr).paint(player);
                }
            }
    }

    @Override
    public String toString() {
        return "PaintPump";
    }
}
