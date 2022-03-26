package player;

import game.Piece;
import game.Puissance4;
import ia.Ia;
import ia.Niveau;

public class Computer extends Player {
    private Niveau level;

    public Computer(String name, Piece piece, Niveau lvl) {
        super(name, piece);
        this.level = lvl;
    }

    /**
     * Retourne l'indice de la colonne où l'ia souhaite joué sur le plateau du Puissance 4
     * @param game
     * @return
     */
    @Override
    public int play(Puissance4 game) { // TODO ajouter ia
        switch(level) {
            case MOYEN:
                return Ia.playAlphaBeta(game);
            case FORT:
                return Ia.playTodo(game); // TODO le niveau 3 est encore a def
            case FAIBLE: // le niveau par défaut est FAIBLE
            default:
                return Ia.playMinMax(game);

        }
    }
}
