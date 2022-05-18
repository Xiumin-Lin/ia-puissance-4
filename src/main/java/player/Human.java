package player;

import game.Piece;
import game.Puissance4;

/**
 * Classe représentant un joueur humain
 *
 * @author Xiumin LIN
 */
public class Human extends Player{
    public Human(String name, Piece piece){
        super(name, piece);
    }

    @Override
    public int play(Puissance4 game) {
        return 0;
    }

    @Override
    public String toString() {
        return "[Humain] " + getName() + " (Piece " + getPiece() + ')';
    }
}
