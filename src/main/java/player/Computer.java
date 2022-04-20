package player;

import game.Piece;
import game.Puissance4;
import ia.Ia;
import ia.Niveau;

public class Computer extends Player {
    private final Niveau level;
    private final Ia ai;

    public Computer(String name, Piece piece, Niveau lvl) {
        super(name, piece);
        this.level = lvl;
        this.ai = new Ia(level);
    }

    /**
     * Retourne l'indice de la colonne où l'ia souhaite joué sur le plateau du Puissance 4
     *
     * @param game le jeu
     * @return l'indice de la colonne où l'ia souhaite placer sa piece
     */
    @Override
    public int play(Puissance4 game) {
        int[] result;
        switch(level) {
            case MOYEN, FORT:
                result = ai.playAlphaBeta(game, 0, true, this, Integer.MIN_VALUE, Integer.MAX_VALUE);
                break;
            case FAIBLE:
            default:
                result = ai.playMiniMax(game, 0, true, this);
        }
        System.out.println("IA joue à la colonne :" + result[0] + " (heuristique:" + result[1] + ")");
        return result[0];
    }
}
