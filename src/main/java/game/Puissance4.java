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
     * colonne (x)            <br/>&ensp
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
     * A la création, tous les pièces sur le plateau ont pour valeur Piece.EMPTY
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
     * Indique si une pièce est déjà present à la position donnée en param
     *
     * @param col la colonne de la pièce à poser
     * @param row la ligne de la pièce à poser
     * @return true si la position est vide (= Piece.EMPTY), sinon false
     */
    public boolean pieceIsPresent(int col, int row) {
        if(isOutOfLimitBoard(col, row)) return false;
        return plateau[col][row] != Piece.EMPTY;
    }

    /**
     * @return Retourne l'indice des colonnes dont on peut placer des pièces.
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
     * Cherche la prochaine indice de la ligne qui un emplacement vide (sans piece)
     *
     * @param col la colonne de la pièce à poser
     * @return -1 si la colonne n'a plus de case vide, sinon retourne l'indice de la ligne disponible
     */
    public int nextEmptyCaseRow(int col) {
        int row = NB_ROW - 1;
        for(; row >= 0; row--) {
            if(!pieceIsPresent(col, row)) break;
        }
        if(row < 0) return -1;
        return row;
    }

    /**
     * Place une pièce à la colonne indiqué en param s'il y a encore une case vide.
     * Si la pièce est bien posé, on verifie si la partie a été remporté.
     * Si oui, on met à jour l'attribut "winner" et on met fin au jeu.
     * Sinon on passe au tour suivant.
     *
     * @param col la colonne de la pièce à poser
     */
    public void placePiece(int col) {
        // Cherche l'indice de la ligne qui un emplacement vide (sans piece)
        int row = nextEmptyCaseRow(col);
        boolean setIsValid = setPieceInPlateau(col, row);
        if(!setIsValid) return;

        if(checkIfWinMove(col, row)) {
            this.winner = isP1Turn ? player1 : player2;
            this.isGameOver = true;
        } else if(getAvailablePlace().isEmpty()) {
            this.isGameOver = true; // S'il ne reste plus de place, le jeu se termine sur une egalité
        } else nextTurn();
    }

    /**
     * Place une pièce à l'emplacement indiqué en param.
     *
     * @param col la colonne de la piece
     * @param row la ligne de la piece
     * @return true si la pièce a été bien placer, sinon false
     */
    public boolean setPieceInPlateau(int col, int row) {
        if(isOutOfLimitBoard(col, row)) return false;
        plateau[col][row] = isP1Turn ? player1.getPiece() : player2.getPiece();
        return true;
    }

    /**
     * Indique si la position donnée en paramètre est valide, c'est-à-dire
     * qu'elle ne dépasse pas les limites du plateau.
     *
     * @param col la colonne de la pièce à poser
     * @param row la ligne de la pièce à poser
     * @return true si la position ne dépasse pas les limites du plateau, sinon false
     */
    public boolean isOutOfLimitBoard(int col, int row) {
        return isOutOfLimitCol(col) || isOutOfLimitRow(row);
    }

    /**
     * Indique si l'indice de la colonne donnée en paramètre ne dépasse pas les limites du plateau.
     *
     * @param col la colonne de la pièce à poser
     * @return true si l'indice de la colonne ne les dépasse pas, sinon false
     */
    public boolean isOutOfLimitCol(int col) {
        return col < 0 || col >= NB_COL;
    }

    /**
     * Indique si l'indice de la ligne donnée en paramètre ne dépasse pas les limites du plateau
     *
     * @param row la ligne de la pièce à poser
     * @return true si l'indice de la colonne ne les dépasse pas, sinon false
     */
    public boolean isOutOfLimitRow(int row) {
        return row < 0 || row >= NB_ROW;
    }

    /**
     * Vérifie si un joueur a gagné après avoir posé une pièce au coord (col, row)
     *
     * @param col l'indice de la colonne de la pièce posée
     * @param row l'indice de la ligne de la pièce posée
     * @return true si la partie est gagné, càd qu'il y a au moins une rangée de 4 pièces de la même couleur, sinon false
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
        List<Piece> diagTopLeft = new ArrayList<>(); // Ex: (2,1), (3,2), (4,3), (5,4),
        for(int i = -3; i < WIN_NUMBER; i++) {
            if(pieceIsPresent(col + i, row + i)) diagTopLeft.add(plateau[col + i][row + i]);
            else diagTopLeft.add(Piece.EMPTY);
        }
        // check diagonal bottom-left -> top-right : "/"
        List<Piece> diagBotLeft = new ArrayList<>(); // Ex: (4,1), (3,2), (2,3), (1,4),
        for(int i = -3; i < WIN_NUMBER; i++) {
            if(pieceIsPresent(col - i, row + i)) diagBotLeft.add(plateau[col - i][row + i]);
            else diagBotLeft.add(Piece.EMPTY);
        }
        return checkRange(horizontal) || checkRange(vertical) || checkRange(diagTopLeft) || checkRange(diagBotLeft);
    }

    /**
     * Verifie si la liste de pièce donnée contient une rangée de 4 pièces de la même couleur.
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

    public void nextTurn() {
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
     * La liste indiquant les valeurs de l'heuristique pour les pièces isolés selon leurs colonnes.
     * Plus la pièce est au centre de la pièce et plus sa valeur sera forte par rapport aux autres.
     */
    private static final int[] soloPieceHeuristic = {40, 70, 120, 200, 120, 70, 40};

    /**
     * Retourne l'evaluation de la valeur de l'heuristique du plateau pour le joueur donnée en paramètre.
     * Le retour > 0 si le joueur à l'avantage, < 0 s'il est en désavantage et 0 si il y a égalité.
     *
     * @param p le joueur concerné
     * @return la valeur de l'heuristique du plateau pour le joueur donnée en paramètre
     */
    public int evaluation(Player p) {
        int p1Score = 0;
        int p2Score = 0;
        // Si on a un vainqueur ou si il y a égalité
        if(isGameOver) {
            if(winner == null) return 0;
            else if(winner == p) return Integer.MAX_VALUE;
            return Integer.MIN_VALUE;
        }

        // Calcul de l'heuristique pour les alignements verticals (pour chaque colonne)
        for(int col = 0; col < NB_COL; col++) {
            List<Piece> vertical = new ArrayList<>();
            int row = NB_ROW - 1;
            for(; row >= 0; row--) {
                Piece aPiece = plateau[col][row];
                if(aPiece == Piece.EMPTY) break;
                vertical.add(aPiece);

                // Si c'est une pièce isolée, on lui attribue une valeur selon la liste soloPieceHeuristic.
                // On attribue une valeur plus forte aux pièces isolées qui sont proches du centre.
                if(checkIfIsolatePiece(col, row)) {
                    if(aPiece == Piece.ROUGE) p1Score += soloPieceHeuristic[col];
                    else p2Score += soloPieceHeuristic[col];
                }
            }
            p1Score += calculateHeuristicVertical(vertical, true);
            p2Score += calculateHeuristicVertical(vertical, false);
        }

        // Horizontale (pour chaque ligne)
        // TODO Note "Pour calculer la valeur de l'heuristique pour horizontal, et diag;
        // Il nous faut la ligne precedente pour s'assurer qu'une pièce peut être bien posés
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
            p1Score += calculateHeuristicHoriOrDiag(horizontal, true);
            p2Score += calculateHeuristicHoriOrDiag(horizontal, false);
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
                if(isOutOfLimitBoard(col, row)) break; // en diag, on a max 6 pièces alignées
                else if(row != NB_ROW - 1 && plateau[col][row + 1] == Piece.EMPTY) {
                    diagTopLeft.add(Piece.UNAVAILABLE); // indique une place libre mais inaccesible pour l'instant
                } else diagTopLeft.add(plateau[col][row]);
            }
            p1Score += calculateHeuristicHoriOrDiag(diagTopLeft, true);
            p2Score += calculateHeuristicHoriOrDiag(diagTopLeft, false);
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
                if(isOutOfLimitBoard(col, row)) break; // en diag, on a max 6 pièces alignées
                else if(row != NB_ROW - 1 && plateau[col][row + 1] == Piece.EMPTY) {
                    diagBotLeft.add(Piece.UNAVAILABLE);
                } else diagBotLeft.add(plateau[col][row]);
            }
            p1Score += calculateHeuristicHoriOrDiag(diagBotLeft, true);
            p2Score += calculateHeuristicHoriOrDiag(diagBotLeft, false);
        }

        int result = p1Score - p2Score;
        return p == player1 ? result : -result;
    }

    /**
     * Vérifie si une pièce est isolée des autres pièces de la même couleurs.
     * 
     * @param col la colonne de la piece
     * @param row la ligne de la piece
     * @return true si la piece est isolé des autres pièces de la même couleurs
     */
    private boolean checkIfIsolatePiece(int col, int row) {
        Piece left = !isOutOfLimitCol(col - 1) ? plateau[col - 1][row] : null;
        Piece right = !isOutOfLimitCol(col + 1) ? plateau[col + 1][row] : null;
        Piece bottom = !isOutOfLimitRow(row + 1) ? plateau[col][row + 1] : null;
        Piece diagTopLeft = !isOutOfLimitBoard(col - 1, row - 1) ? plateau[col - 1][row - 1] : null;
        Piece diagBotLeft = !isOutOfLimitBoard(col - 1, row + 1) ? plateau[col - 1][row + 1] : null;
        Piece diagTopRight = !isOutOfLimitBoard(col + 1, row - 1) ? plateau[col + 1][row - 1] : null;
        Piece diagBotRight = !isOutOfLimitBoard(col + 1, row + 1) ? plateau[col + 1][row + 1] : null;
        Piece[] neighbours = {left, right, bottom, diagTopLeft, diagBotLeft, diagTopRight, diagBotRight};

        for(Piece voisin : neighbours) {
            if(voisin == null || voisin == Piece.EMPTY) continue;
            return false;
        }
        return true;
    }

    /**
     * Calcule la valeur de l'heuristique en horizontal ou en diagonal.
     * Dans cette disposition, elle ne peut y avoir qu'au max 2 emplacements vide
     * adjacent à une suite de pièce identique.
     *
     * @param list la liste de pièce sur une ligne ou une diagonal
     * @param isP1 le calcul de l'heuristique concerne le player 1 ?
     * @return la valeur de l'heuristique
     */
    private int calculateHeuristicHoriOrDiag(List<Piece> list, boolean isP1) {
        Piece pieceRef = (isP1) ? player1.getPiece() : player2.getPiece();
        int valeur = 0;         // la valeur de l'heuristique
        int compteur = 0;       // compte le nombre de pièce correspond à la pièce de reference
        int compteurEmpty = 0;  // compte le nombre de place vide à coté d'une suite de piece
        for(Piece piece : list) {
            if(piece.getValue() == pieceRef.getValue()) compteur++;
            else if(piece == Piece.EMPTY) compteurEmpty++;
            else { // sinon c la pièce de l'adversaire
                valeur += calculateHeuristic(compteur, compteurEmpty);
                compteur = 0;
                compteurEmpty = 0;
            }
        }
        if(compteur != 0) {
            valeur += calculateHeuristic(compteur, compteurEmpty);
        }
        return valeur;
    }

    /**
     * Calcule la valeur de l'heuristique en vertical.
     * Dans cette disposition, elle ne peut y avoir qu'un seul emplacement vide
     * adjacent à une suite de pièce identique.
     *
     * @param list la liste de pièce sur une colonne
     * @param isP1 le calcul de l'heuristique concerne le player 1 ?
     * @return la valeur de l'heuristique d'une colonne
     */
    private int calculateHeuristicVertical(List<Piece> list, boolean isP1) {
        Piece pieceRef = (isP1) ? player1.getPiece() : player2.getPiece();
        int valeur = 0;         // la valeur de l'heuristique
        int compteur = 0;       // compte le nombre de pièce correspond à la pièce de reference
        int compteurEmpty = 0;  // compte le nombre de place vide à coté d'une suite de piece
        for(Piece piece : list) {
            if(piece.getValue() == pieceRef.getValue()) compteur++;
            else if(piece == Piece.EMPTY) {
                compteurEmpty++;
                break;  // pas besoin de continue comme on est en vertical
            } else {    // sinon c la pièce de l'adversaire
                valeur += calculateHeuristic(compteur, 0);
                compteur = 0;
            }
        }
        if(compteur != 0) {
            valeur += calculateHeuristic(compteur, compteurEmpty);
        }
        return valeur;
    }

    /**
     * Attribue la valeur de l'heuristique d'une suite de pièce selon sa taille et
     * le nombre de place disponible à côté de cette suite.
     *
     * @param compteurPieceRef la taille d'une suite de piece
     * @param compteurEmpty    le nombre de place disponible adjacent à cette suite (max = 2)
     * @return la valeur de l'heuristique d'une suite de piece.
     */
    private int calculateHeuristic(int compteurPieceRef, int compteurEmpty) {
        switch(compteurPieceRef) {
            case 2: { // Si on a une suite de 2 pièces identique et alignée
                return switch(compteurEmpty) { // le nb de place disponible à côté de cette suite de 2 pièces
                    case 1 -> 5000;
                    case 2 -> 10000;
                    case 3 -> 20000;
                    case 4 -> 30000;
                    case 5 -> 40000;
                    default -> 0;       // si aucun case libre, alors cette suite est inutiles
                };
            }
            case 3: { // Si on a une suite de 3 pièces identique et alignée
                return switch(compteurEmpty) {
                    case 1 -> 900000; // 1 case dispo, on a une change de gagné mais aussi d'etre contré
                    case 2 -> Integer.MAX_VALUE; // 2 case dispo, on gagne forcément
                    default -> 0; // si aucun case libre, alors cette suite de 3 pièces est inutiles
                };
            }
            case 0, 1: // Si il n'y a aucune suite
            default:
                return 0;
        }
    }
}
