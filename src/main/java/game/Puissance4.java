package game;

import player.Player;

import java.util.ArrayList;
import java.util.List;

public class Puissance4 {
    private boolean redMove;
    private final int maxRow; // = y
    private final int maxCol; // = x
    private final Player redPlayer;
    private final Player yellowPlayer;
    private final Piece[][] plateau;
    private boolean end;

    public Puissance4(int row, int col, Player redPlayer, Player yellowPlayer) {
        this.redMove = true; // le joueur rouge commence
        this.maxRow = row;
        this.maxCol = col;
        this.redPlayer = redPlayer;
        this.yellowPlayer = yellowPlayer;
        plateau = new Piece[col][row]; // [x][y]
        for(int x = 0; x < col; x++) {
            for(int y = 0; y < row; y++) {
                plateau[x][y] = Piece.EMPTY;
            }
        }
        this.end = false;
    }

    public Piece[][] getPlateau() {
        return plateau;
    }

    public boolean isRedMove() {
        return redMove;
    }

    public boolean pieceIsPresent(int col, int row) {
        if(isOutOfLimitCol(col) || isOutOfLimitRow(row)) return false;
        return plateau[col][row] != Piece.EMPTY;
    }

    public void setPiece(int col, int row) {
        if(isOutOfLimitCol(col) || isOutOfLimitRow(row)) return;
        plateau[col][row] = redMove ? Piece.ROUGE : Piece.JAUNE;
    }

    public boolean isOutOfLimitCol(int col) {
        return col < 0 || col >= maxCol;
    }

    public boolean isOutOfLimitRow(int row) {
        return row < 0 || row >= maxRow;
    }

    public int getMaxCol() {
        return maxCol;
    }

    public int getMaxRow() {
        return maxRow;
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
        int horiEnd = Math.min((col + 3), maxCol - 1);
        for(int i = horiStart; i <= horiEnd; i++) {
            horizontal.add(plateau[i][row]);
        }
        // check vertical
        List<Piece> vertical = new ArrayList<>();
        int vertiStart = Math.max((row - 3), 0);
        int vertiEnd = Math.min((row + 3), maxRow - 1);
        for(int i = vertiStart; i <= vertiEnd; i++) {
            vertical.add(plateau[col][i]);
        }
        System.out.println(vertical);
        // check diagonal top-left -> bottom-right : "\"
        List<Piece> diagTopLeft = new ArrayList<>(); // (2,1), (3,2), (4,3), (5,4),
        for(int i = -3; i < 4; i++) {
            if(pieceIsPresent(col + i, row + i)) diagTopLeft.add(plateau[col + i][row + i]);
            else diagTopLeft.add(Piece.EMPTY);
        }
        // check diagonal bottom-left -> top-right : "/"
        List<Piece> diagBotLeft = new ArrayList<>(); // (4,1), (3,2), (2,3), (1,4),
        for(int i = -3; i < 4; i++) {
            if(pieceIsPresent(col - i, row + i)) diagBotLeft.add(plateau[col - i][row + i]);
            else diagBotLeft.add(Piece.EMPTY);
        }
        return checkRange(horizontal) || checkRange(vertical) || checkRange(diagTopLeft) || checkRange(diagBotLeft);
    }

    /**
     * Verifie si la liste de piece donnée contient une rangée de 4 pièces de la même couleur
     *
     * @param listPiece une liste de pièce
     * @return true si la liste donnée a une rangée de 4 pièces de la même couleur
     */
    public boolean checkRange(List<Piece> listPiece) {
        int compteur = 0;
        Piece pieceRef = (redMove) ? Piece.ROUGE : Piece.JAUNE;
        for(Piece p : listPiece) {
            if(p == pieceRef) {
                compteur++;
                if(compteur == 4) {
                    return true;
                }
            } else {
                compteur = 0;
            }
        }
        return false;
    }

    public void nextPlayer() {
        redMove = !redMove;
    }

    public Player getCurrentPlayer() {
        return (redMove) ? redPlayer : yellowPlayer;
    }

    public void over() {
        end = true;
    }

    public boolean isOver() {
        return end;
    }
}
