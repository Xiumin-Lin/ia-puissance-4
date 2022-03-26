package player;

import game.Piece;
import game.Puissance4;

public abstract class Player {
    private String name;
    private Piece piece;

    protected Player(String name, Piece p){
        this.name = name;
        this.piece = p;
    }

    public String getName() {
        return name;
    }

    public Piece getPiece() {
        return piece;
    }

    public abstract int play(Puissance4 game);
}
