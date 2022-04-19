package game;

import player.Player;

import java.util.ArrayList;
import java.util.List;

public class Puissance4 {
    public static final int NB_ROW = 6; // = y
    public static final int NB_COL = 7; // = x
    private static final int WIN_NUMBER = 4;
    private boolean isP1Turn;
    private final Player player1;
    private final Player player2;

    /**
     * Matrice contenant les différents cases du jeu.
     * Piece[x] correspond à la liste des cases sur la colonne x.
     * <p>&emsp
     *      colonne (x)            <br/>&ensp
     *    0 1 2 3 4 5 6            <br/>
     * 0  _ _ _ _ _ _ _            <br/>
     * 1  _ _ _ _ _ _ _            <br/>
     * 2  _ _ _ _ _ _ _  ligne (y) <br/>
     * 3  _ _ _ _ _ _ _            <br/>
     * 4  _ _ _ _ _ _ _            <br/>
     * 5  _ _ _ _ _ _ _            <br/>
     * </p>
     */
    private final Piece[][] plateau;

    private Player winner;
    private boolean isGameOver;

    /**
     * Constructeur par défaut de la classe Puissance4.
     * A la création, tous les pieces sur le plateau ont pour valeur Piece.EMPTY
     *
     * @param p1          le joueur 1
     * @param p2          le joueur 2
     * @param startWithP1 indique si le joueur 1 commence
     */
    public Puissance4(Player p1, Player p2, boolean startWithP1) {
        this.isP1Turn = startWithP1; // est ce que le joueur 1 commence ?
        this.player1 = p1;
        this.player2 = p2;
        this.winner = null;
        plateau = new Piece[NB_COL][NB_ROW]; // [x][y]
        for(int x = 0; x < NB_COL; x++) {
            for(int y = 0; y < NB_ROW; y++) {
                plateau[x][y] = Piece.EMPTY;
            }
        }
        this.isGameOver = false;
    }

    /**
     * Constructeur qui crée un copie du jeu passé en param
     *
     * @param original l'état du jeu original
     */
    public Puissance4(Puissance4 original) {
        this(original.player1, original.player2, original.isP1Turn);
        for(int x = 0; x < NB_COL; x++) {
            for(int y = 0; y < NB_ROW; y++) {
                plateau[x][y] = original.plateau[x][y];
            }
        }
        this.winner = original.winner;
        this.isGameOver = original.isGameOver;
    }

    /**
     * Indique si une piece est déjà present à la position donnée en param
     * @param col la colonne de la pièce à poser
     * @param row la ligne de la pièce à poser
     * @return
     */
    public boolean pieceIsPresent(int col, int row) {
        if(isOutOfLimitPiece(col, row)) return false;
        return plateau[col][row] != Piece.EMPTY;
    }

    /**
     * @return Retourne l'indice des colonnes dont on peut placer des pieces.
     */
    public List<Integer> getAvailablePlace() {
        List<Integer> list = new ArrayList<>();
        for(int col = 0; col < NB_COL; col++) {
            for(Piece p : plateau[col]) {
                if(p == Piece.EMPTY) {
                    list.add(col);
                    break;
                }

            }
        }
        return list;
    }

    /**
     * Place une piece à la colonne indiqué en param si la position est valide le joueur ayant joué a gagné
     *
     * @param col
     */
    public int placePiece(int col) {
        // Cherche l'indice de la ligne qui un emplacement vide (sans piece)
        int row = NB_ROW - 1;
        for(; row >= 0; row--) {
            if(!pieceIsPresent(col, row)) break;
        }
        if(row < 0) return -1;
        boolean setIsValid = setPieceInPlateau(col, row);
        if(!setIsValid) return -1;
        if(checkIfWinMove(col, row)) {
            this.winner = isP1Turn ? player1 : player2;
            this.isGameOver = true;
        } else if(getAvailablePlace().isEmpty()) {
            this.isGameOver = true; // S'il ne reste plus de place, le jeu se termine sur une egalité
        }
        return row;
    }

    /**
     * Place une piece à l'emplacement indiqué en param.
     *
     * @param col la colonne de la piece
     * @param row la ligne de la piece
     * @return true si la piece a été bien placer, sinon false
     */
    public boolean setPieceInPlateau(int col, int row) {
        if(isOutOfLimitPiece(col, row)) return false;
        plateau[col][row] = isP1Turn ? player1.getPiece() : player2.getPiece();
        return true;
    }

    public boolean isOutOfLimitPiece(int col, int row) {
        return isOutOfLimitCol(col) || isOutOfLimitRow(row);
    }

    public boolean isOutOfLimitCol(int col) {
        return col < 0 || col >= NB_COL;
    }

    public boolean isOutOfLimitRow(int row) {
        return row < 0 || row >= NB_ROW;
    }

    /**
     * Verifie si un joueur a gagné après avoir posé une piece au coord (col, row)
     *
     * @param col numéro de la colonne de la pièce posée
     * @param row numéro de la colonne de la pièce posée
     * @return true si la partie est gagné, càd qu'il a au moins une rangée de 4 pieces de la même couleur, sinon false
     */
    public boolean checkIfWinMove(int col, int row) {
        // check horizontal
        List<Piece> horizontal = new ArrayList<>();
        int horiStart = Math.max((col - 3), 0);
        int horiEnd = Math.min((col + 3), NB_COL - 1);
        for(int i = horiStart; i <= horiEnd; i++) {
            horizontal.add(plateau[i][row]);
        }
        // check vertical
        List<Piece> vertical = new ArrayList<>();
        int vertiStart = Math.max((row - 3), 0);
        int vertiEnd = Math.min((row + 3), NB_ROW - 1);
        for(int i = vertiStart; i <= vertiEnd; i++) {
            vertical.add(plateau[col][i]);
        }
        // check diagonal top-left -> bottom-right : "\"
        List<Piece> diagTopLeft = new ArrayList<>(); // (2,1), (3,2), (4,3), (5,4),
        for(int i = -3; i < WIN_NUMBER; i++) {
            if(pieceIsPresent(col + i, row + i)) diagTopLeft.add(plateau[col + i][row + i]);
            else diagTopLeft.add(Piece.EMPTY);
        }
        // check diagonal bottom-left -> top-right : "/"
        List<Piece> diagBotLeft = new ArrayList<>(); // (4,1), (3,2), (2,3), (1,4),
        for(int i = -3; i < WIN_NUMBER; i++) {
            if(pieceIsPresent(col - i, row + i)) diagBotLeft.add(plateau[col - i][row + i]);
            else diagBotLeft.add(Piece.EMPTY);
        }
        return checkRange(horizontal) || checkRange(vertical) || checkRange(diagTopLeft) || checkRange(diagBotLeft);
    }

    /**
     * Verifie si la liste de piece donnée contient une rangée de 4 pièces de la même couleur.
     *
     * @param listPiece une liste de pièce
     * @return true si la liste donnée a une rangée de 4 pièces de la même couleur
     */
    public boolean checkRange(List<Piece> listPiece) {
        int compteur = 0;
        Piece pieceRef = isP1Turn ? player1.getPiece() : player2.getPiece();
        for(Piece p : listPiece) {
            if(p == pieceRef) {
                compteur++;
                if(compteur == WIN_NUMBER) {
                    return true;
                }
            } else {
                compteur = 0;
            }
        }
        return false;
    }

    public void nextPlayer() {
        isP1Turn = !isP1Turn;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getCurrentPlayer() {
        return (isP1Turn) ? player1 : player2;
    }

    public boolean isOver() {
        return isGameOver;
    }

    /**
     * Calcul la valeur de l'heuristique du plateau pour le joueur donnée en paramètre.
     * Le retour > 0 si le joueur à l'avantage, < 0 s'il est en désavantage et 0 si il y a égalité.
     *
     * @param p le joueur concerné
     * @return la valeur de l'heuristique du plateau pour le joueur donnée en paramètre
     */
    public int calculateHeuristicValue(Player p) {
        int p1Score = 0;
        int p2Score = 0;
        // Si on a un vainqueur ou si il y a égalité
        if(isGameOver) {
            if(winner == null) return 0;
            else if(winner == p) return Integer.MAX_VALUE;
            return Integer.MIN_VALUE;
        }

        // Vertical (pour chaque colonne)
        for(int col = 0; col < NB_COL; col++) {
            List<Piece> vertical = new ArrayList<>();
            for(int row = NB_ROW - 1; row >= 0; row--) {
                vertical.add(plateau[col][row]);
            }
            p1Score += calculateHeuristicVertical(vertical, true);
            p2Score += calculateHeuristicVertical(vertical, false);
        }

        // Horizontale (pour chaque ligne)
        // TODO Note "Pour calculer la valeur de l'heuristique pour horizontal, et diag;
        // Il nous faut la ligne precedente pour s'assurer qu'une piece peut être bien posés
        for(int row = NB_ROW - 1; row >= 0; row--) {
            List<Piece> horizontal = new ArrayList<>();
            for(int col = 0; col < NB_COL; col++) { // on commence par la ligne tout en bas
                // on check si la place en dessous est vide (sauf si 1ère ligne)
                // si oui, alors la place d'en haut n'est pas valide.
                if(row != NB_ROW - 1 && plateau[col][row + 1] == Piece.EMPTY) {
                    horizontal.add(Piece.UNAVAILABLE);
                } else {
                    horizontal.add(plateau[col][row]);
                }
            }
            p1Score += calculateHeuristicHoriOuDiag(horizontal, true);
            p2Score += calculateHeuristicHoriOuDiag(horizontal, false);
        }

        // Pour chaque diagonal top-left -> bottom-right : "\" qui contient au moins
        // 4 emplacements possibles pour une pièce.
        // Ci-dessous est la liste des coords de départ pour vérifier un diagonal "\"
        int[][] diagTopLeftList = {{3, 0}, {2, 0}, {1, 0}, {0, 0}, {0, 1}, {0, 2}};
        for(int[] coords : diagTopLeftList) {
            List<Piece> diagTopLeft = new ArrayList<>();
            for(int i = 0; i < NB_COL; i++) {
                int col = coords[0] + i;
                int row = coords[1] + i;
                if(isOutOfLimitPiece(col, row)) break; // en diag, on a max 6 pieces alignées
                else if(row != NB_ROW - 1 && plateau[col][row + 1] == Piece.EMPTY) {
                    diagTopLeft.add(Piece.UNAVAILABLE); // indique une place libre mais inaccesible pour l'instant
                } else diagTopLeft.add(plateau[col][row]);
            }
            p1Score += calculateHeuristicHoriOuDiag(diagTopLeft, true);
            p2Score += calculateHeuristicHoriOuDiag(diagTopLeft, false);
        }

        // Pour chaque diagonal bottom-left -> top-right : "/" qui contient au moins
        // 4 emplacements possibles pour une pièce.
        // Ci dessous est la liste des coords de départ pour vérifier un diagonal "/"
        int[][] diagBotLeftList = {{0, 3}, {0, 4}, {0, 5}, {1, 5}, {2, 5}, {3, 5}};
        for(int[] coords : diagBotLeftList) {
            List<Piece> diagBotLeft = new ArrayList<>();
            for(int i = 0; i < NB_COL; i++) {
                int col = coords[0] + i;
                int row = coords[1] - i;
                if(isOutOfLimitPiece(col, row)) break; // en diag, on a max 6 pieces alignées
                else if(row != NB_ROW - 1 && plateau[col][row + 1] == Piece.EMPTY) {
                    diagBotLeft.add(Piece.UNAVAILABLE);
                } else diagBotLeft.add(plateau[col][row]);
            }
            p1Score += calculateHeuristicHoriOuDiag(diagBotLeft, true);
            p2Score += calculateHeuristicHoriOuDiag(diagBotLeft, false);
        }

        int result = p1Score - p2Score;
        return p == player1 ? result : -result;
    }

    /**
     * Calcule la valeur de l'heuristique en horizontal ou diagonal.
     * Dans cette disposition, elle ne peut y avoir qu'au max 2 emplacements vide
     * adjacent à une suite de piece identique.
     *
     * @param list la liste de pièce sur une ligne ou une diagonal
     * @param isP1 le calcul de l'heuristique concerne le player 1 ?
     * @return la valeur de l'heuristique
     */
    private int calculateHeuristicHoriOuDiag(List<Piece> list, boolean isP1) {
        Piece pieceRef = (isP1) ? player1.getPiece() : player2.getPiece();
        int valeur = 0;         // la valeur de l'heuristique
        int compteur = 0;       // compte le nombre de piece correspond à la piece de reference
        int compteurEmpty = 0;  // compte le nombre de place vide à coté d'une suite de piece
        for(Piece piece : list) {
            if(piece.getValue() == pieceRef.getValue()) compteur++;
            else if(piece == Piece.EMPTY) compteurEmpty++;
            else { // sinon c la piece de l'adversaire
                valeur += attribuerValeur(compteur, compteurEmpty);
                compteur = 0;
                compteurEmpty = 0;
            }
        }
        if(compteur != 0) {
            valeur += attribuerValeur(compteur, compteurEmpty);
        }
        return valeur;
    }

    /**
     * Calcule la valeur de l'heuristique en vertical.
     * Dans cette disposition, elle ne peut y avoir qu'un seul emplacement vide
     * adjacent à une suite de piece identique.
     *
     * @param list la liste de pièce sur une colonne
     * @param isP1 le calcul de l'heuristique concerne le player 1 ?
     * @return la valeur de l'heuristique d'une colonne
     */
    private int calculateHeuristicVertical(List<Piece> list, boolean isP1) {
        System.out.print("Vertical " + isP1);
        Piece pieceRef = (isP1) ? player1.getPiece() : player2.getPiece();
        int valeur = 0;         // la valeur de l'heuristique
        int compteur = 0;       // compte le nombre de piece correspond à la piece de reference
        int compteurEmpty = 0;  // compte le nombre de place vide à coté d'une suite de piece
        for(Piece piece : list) {
            if(piece.getValue() == pieceRef.getValue()) compteur++;
            else if(piece == Piece.EMPTY) {
                compteurEmpty++;
                break;  // pas besoin de continue comme on est en vertical
            } else {    // sinon c la piece de l'adversaire
                valeur += attribuerValeur(compteur, 0);
                compteur = 0;
            }
        }
        if(compteur != 0) {
            valeur += attribuerValeur(compteur, compteurEmpty);
        }
        return valeur;
    }

    /**
     * Attribue la valeur de l'heuristique d'une suite de piece selon sa taille et
     * le nombre de place disponible à côté de cette suite.
     *
     * @param compteurPieceRef la taille d'une suite de piece
     * @param compteurEmpty    le nombre de place disponible adjacent à cette suite (max = 2)
     * @return la valeur de l'heuristique d'une suite de piece.
     */
    private int attribuerValeur(int compteurPieceRef, int compteurEmpty) {
        switch(compteurPieceRef) {
            case 2: { // Si on a une suite de 2 pieces identique et alignée
                return switch(compteurEmpty) { // le nb de place disponible à côté de cette suite de 2 pièces
                    case 1 -> 5000;
                    case 2 -> 10000;
                    case 3 -> 20000;
                    case 4 -> 30000;
                    case 5 -> 40000;
                    default -> 0;       // si aucun case libre, alors cette suite est inutiles
                };
            }
            case 3: { // Si on a une suite de 3 pieces identique et alignée
                return switch(compteurEmpty) {
                    case 1 -> 900000; // 1 case dispo, on a une change de gagné mais aussi d'etre contré
                    case 2 -> Integer.MAX_VALUE; // 2 case dispo, on gagne forcément
                    default -> 0; // si aucun case libre, alors cette suite de 3 pieces est inutiles
                };
            }
            case 0, 1: // Si il n'y a aucune suite
            default:
                return 0;
        }
    }
}
