package edu.eci.arsw.paintit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Cell {

    private Player paintedBy;
    private int x;
    private int y;
    private Wildcard wildcard;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public boolean isPlayerOnCell() {
        return paintedBy != null && paintedBy.getX() == x && paintedBy.getY() == y;
    }

    public synchronized void paint(Player player) {
        if (!isPlayerOnCell()) {
            if (this.paintedBy != null) this.paintedBy.getScore().decrementAndGet();
            this.paintedBy = player;
            player.getScore().incrementAndGet();
        }
    }
}
