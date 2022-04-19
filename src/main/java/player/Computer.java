package player;

import game.Piece;
import game.Puissance4;
import ia.Ia;
import ia.Niveau;

public class Computer extends Player {
    private final Niveau level;

    public Computer(String name, Piece piece, Niveau lvl) {
        super(name, piece);
        this.level = lvl;
    }

    /**
     * Retourne l'indice de la colonne où l'ia souhaite joué sur le plateau du Puissance 4
     *
     * @param game le jeu
     * @return l'indice de la colonne où l'ia souhaite placer sa piece
     */
    @Override
    public int play(Puissance4 game) {
        switch(level) {
            case MOYEN,FORT:
                return Ia.playAlphaBeta(game, level.getProfondeur(), Integer.MIN_VALUE, Integer.MAX_VALUE);
            case FAIBLE:
            default:
                return Ia.playMiniMax(game, level.getProfondeur(), true, this)[0];
        }
    }
}
