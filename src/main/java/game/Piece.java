package game;

public enum Piece {
    JAUNE(1),
    ROUGE(2),
    EMPTY(0),
    UNAVAILABLE(-1); // pour la calcul de l'heuristique

    private final int value;

    Piece(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
