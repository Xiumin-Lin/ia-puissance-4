package ia;

import game.Puissance4;
import player.Player;

import java.util.List;
import java.util.Random;

public class Ia {
    private static final Random RAND = new Random();
    private int profondeurMax;

    public Ia(Niveau lvl) {
        this.profondeurMax = lvl.getProfondeur();
    }

    /**
     * Utilise l'algo minimax pour déterminer la colonne que l'ia souhaite pour une partie de Puissance 4.
     *
     * @param game       une partie de Puissance 4
     * @param profondeur la profondeur actuel de l'algo minimax
     * @param isMaximize est ce que le joueur veut le max ou le min de la valeur de l'heuristique
     * @param p          le joeur qui veut utiliser l'algo minimax (normalement un objet de class Computer)
     * @return la colonne que l'ia souhaite poser une pièce et sa valeur de l'heuristique
     */
    public int[] playMiniMax(Puissance4 game, int profondeur, boolean isMaximize, Player p) {
        System.out.println("PlayMinMax, depth:" + profondeur);
        if(game.isOver()) {
            if(game.getWinner() == null) return new int[]{-1, 0}; // si égalité, renvoie 0
            else return new int[]{-1, game.calculateHeuristicValue(p)}; // sinon la valeur de l'heuristique du plateau
        }

        // La liste des colonnes valide dont l'ia peut poser une pièce
        List<Integer> colonnesValide = game.getAvailablePlace();
        // On attribut un colonne au hasard au cas où l'algo n'arrive pas à choisir
        int col = colonnesValide.get(RAND.nextInt(colonnesValide.size()));
        int value = isMaximize ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        // Pour chaque coup possible
        for(int moveIndex : colonnesValide) {
            int newValue = 0;
            if(isMaximize) {
                newValue = minValue(game, profondeur, moveIndex, p);
                if(newValue > value) {
                    value = newValue;
                    col = moveIndex;
                }
            } else {
                newValue = maxValue(game, profondeur, moveIndex, p);
                if(newValue < value) {
                    value = newValue;
                    col = moveIndex;
                }
            }
        }
        return new int[]{col, value}; // la colonne que l'ia souhaite jouer et sa valeur de l'heuristique
    }

    public int minValue(Puissance4 game, int profondeur, int moveIndex, Player p) {
        int minValue;
        if(profondeur == this.profondeurMax) {
            minValue = game.calculateHeuristicValue(p);
        } else {
            Puissance4 copie = new Puissance4(game); // on crée une copie du jeu
            copie.placePiece(moveIndex);
            minValue = playMiniMax(copie, profondeur + 1, true, p)[1]; // recup la valeur minimal
        }
        return minValue;
    }

    public int maxValue(Puissance4 game, int profondeur, int moveIndex, Player p) {
        int maxValue;
        if(profondeur == this.profondeurMax) {
            maxValue = game.calculateHeuristicValue(p);
        } else {
            Puissance4 copie = new Puissance4(game); // on crée une copie du jeu
            copie.placePiece(moveIndex);
            maxValue = playMiniMax(copie, profondeur + 1, false, p)[1]; // recup la valeur minimal
        }
        return maxValue;
    }


    public int[] playAlphaBeta(Puissance4 game, int profondeur, boolean isMaximize, Player p, int alpha, int beta) {
        System.out.println("PlayAlphaBeta, depth:" + profondeur);
        if(game.isOver()) {
            if(game.getWinner() == null) return new int[]{-1, 0}; // si égalité, renvoie 0
            else return new int[]{-1, game.calculateHeuristicValue(p)}; // sinon la valeur de l'heuristique du plateau
        }

        // La liste des colonnes valide dont l'ia peut poser une pièce
        List<Integer> colonnesValide = game.getAvailablePlace();
        // On attribut un colonne au hasard au cas où l'algo n'arrive pas à choisir
        int col = colonnesValide.get(RAND.nextInt(colonnesValide.size()));
        int value = isMaximize ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        // Pour chaque coup possible
        for(int moveIndex : colonnesValide) {
            int newValue = 0;
            // recup la valeur de l'heuristique
            if(profondeur == this.profondeurMax) {
                newValue = game.calculateHeuristicValue(p);
            } else {
                // si profondeur max non atteint, on crée une copie du jeu, place une pièce
                // a la colonne "moveIndex" et applique alpha-beta sur ce nouveau plateau de jeu
                Puissance4 copie = new Puissance4(game);
                copie.placePiece(moveIndex);
                newValue = playAlphaBeta(copie, profondeur + 1, !isMaximize, p, alpha, beta)[1];
            }
            if(isMaximize) {
                if(newValue > value) {
                    value = newValue;
                    col = moveIndex;
                }
                // elagage alpha-beta
                if(newValue >= beta) break;
                alpha = Math.max(alpha, newValue);
            } else {
                if(newValue < value) {
                    value = newValue;
                    col = moveIndex;
                }
                // elagage alpha-beta
                if(newValue <= alpha) break;
                beta = Math.min(beta, newValue);
            }
        }
        return new int[]{col, value}; // la colonne que l'ia souhaite jouer et sa valeur de l'heuristique
    }
}
