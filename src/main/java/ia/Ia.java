package ia;

import game.Puissance4;
import player.Player;

import java.util.List;
import java.util.Random;

public class Ia {
    private static final Random RAND = new Random();

    private Ia() {
    }

    /**
     * @param game
     * @param profondeurMax
     * @return
     */
    public static int[] playMiniMax(Puissance4 game, int profondeurMax, boolean isMaximize, Player p) {
        System.out.println("PlayMinMax, depth:" + profondeurMax);
        if(game.isOver()) {
            if(game.getWinner() == null) return new int[]{-1, 0};
            else return new int[]{-1, game.calculateHeuristicValue(p)};
        }

        List<Integer> colonnesValide = game.getAvailablePlace();
        // On attribut un colonne au hasard au cas où l'algo n'arrive pas à choisir
        int col = colonnesValide.get(RAND.nextInt(colonnesValide.size()));
        int value = isMaximize ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        // Pour chaque coup possible
        for(int colIdx : colonnesValide) {
            System.out.print("Col :" + colIdx + ", ");
            int newValue = 0;
            if(isMaximize) {
                newValue = minValue(game, profondeurMax, colIdx, p);
                if(newValue > value) {
                    value = newValue;
                    col = colIdx;
                }
            } else {
                newValue = maxValue(game, profondeurMax, colIdx, p);
                if(newValue < value) {
                    value = newValue;
                    col = colIdx;
                }
            }
        }
        System.out.println("Out:" + profondeurMax + ", Ia play col:" + col + " (val:" + value + ")");
        return new int[]{col, value}; // la colonne que l'ia souhaite jouer
    }

    public static int minValue(Puissance4 game, int profondeurMax, int colIdx, Player p) {
        System.out.print("minValue, ");
        int minValue;
        if(profondeurMax == 0) {
            minValue = game.calculateHeuristicValue(p);
        } else {
            Puissance4 copie = new Puissance4(game); // on crée une copie du jeu
            copie.placePiece(colIdx);
            minValue = playMiniMax(copie, profondeurMax - 1, true, p)[1]; // recup la valeur minimal
        }
        return minValue;
    }

    public static int maxValue(Puissance4 game, int profondeurMax, int colIdx, Player p) {
        System.out.println("maxValue");
        int maxValue;
        if(profondeurMax == 0) {
            maxValue = game.calculateHeuristicValue(p);
        } else {
            Puissance4 copie = new Puissance4(game); // on crée une copie du jeu
            copie.placePiece(colIdx);
            maxValue = playMiniMax(copie, profondeurMax - 1, false, p)[1]; // recup la valeur minimal
        }
        return maxValue;
    }

    /**
     * @param game
     * @param profondeur
     * @param alpha
     * @param beta
     * @return
     */
    public static int playAlphaBeta(Puissance4 game, int profondeur, int alpha, int beta) {
//    	double value;
//    	if( !(profondeur==0 || game.isOver())) {
//    		if(boot) {
//    			value=Double.NEGATIVE_INFINITY;
//    			for(int i=0;i<game.getMaxCol();i++) {
//    				Puissance4 copie=game.copie();
//    			    copie.setPiece(i, );
//    			    if(value<playMinMax(copie, profondeur-1,false)) {
//    			    	value=playMinMax(copie, profondeur-1,false);
//    			    }
//    			    if(alpha<value) {
//    			    	alpha=value;
//    			    }
//    			    if(alpha>=beta) {
//    			    	break;
//    			    }
//    			}
//    			return value;
//    		}
//    		else {
//    			value=Double.POSITIVE_INFINITY;
//    			for(int i=0;i<game.getMaxCol();i++) {
//    			    Puissance4 copie=game.copie();
//    			    copie.setPiece(i, );
//    			    if(value>playMinMax(copie, profondeur-1,true)) {
//    			    	value=playMinMax(copie, profondeur-1,true);
//    			    }
//    			    if(beta>value) {
//    			    	beta=value;
//    			    }
//    			    if(beta>=alpha) {
//    			    	break;
//    			    }
//    			}
//    			return value;
//
//    		}
//    	}
        return 1;// jsp heuristique;
    }
}
