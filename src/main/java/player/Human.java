package player;

import game.Piece;
import game.Puissance4;

public class Human extends Player{
    public Human(String name, Piece piece){
        super(name, piece);
    }

    @Override
    public int play(Puissance4 game) {
        return 0;
    }
}
