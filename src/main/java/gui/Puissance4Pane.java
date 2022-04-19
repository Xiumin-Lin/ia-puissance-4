package gui;

import game.Piece;
import game.Puissance4;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import player.Computer;
import player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de créer la fênetre du jeu Puissance 4.
 * Une grande partie du code provient du tutoriel fait par Almas Baimagambetov avec quelques modifications.
 * (source) https://github.com/AlmasB/FXTutorials/blob/master/src/main/java/com/almasb/connect4/Connect4App.java
 *
 * @author Xiumin LIN, Celine LI, Almas Baimagambetov
 */
public class Puissance4Pane extends BorderPane {
    private static final int CASE_TAILLE_MAX = 70;  // Taille d'une case du plateau
    private static final int ECART = 10;            // L'écart entre chaque case
    private static final int RAYON = CASE_TAILLE_MAX / 2;   // Taille du rayon du cercle d'une case
    private static final int PADDING = CASE_TAILLE_MAX / 3; // Le padding du panneau
    private final Puissance4 game;
    private final int largeurTotal;
    private final int longueurTotal;
    private Pane piecePane;
    private Label titleLabel;

    public Puissance4Pane(Puissance4 game) {
        this.game = game;
        // Taille du plateau de jeu
        int totalPadding = 2 * PADDING;
        this.largeurTotal = (game.NB_ROW) * CASE_TAILLE_MAX + totalPadding + (game.NB_ROW - 1) * ECART;
        this.longueurTotal = (game.NB_COL) * CASE_TAILLE_MAX + totalPadding + (game.NB_COL - 1) * ECART;
        this.titleLabel = new Label();
        updateInformationLabel();
        this.setTop(titleLabel);

        // Création de l'aspect graphique du plateau de jeu
        Pane plateauPane = new Pane();
        piecePane = new Pane();
        plateauPane.getChildren().add(piecePane); // piecePane doit etre ajouté en 1er dans plateauPane
        Shape grilleShape = createGridShape();
        plateauPane.getChildren().add(grilleShape);
        plateauPane.getChildren().addAll(makeColonnes()); // interaction avec les colonnes
        this.setCenter(plateauPane);
    }

    private Shape createGridShape() {
        Shape gridShape = new Rectangle(longueurTotal, largeurTotal);

        for(int row = 0; row < game.NB_ROW; row++) {
            for(int col = 0; col < game.NB_COL; col++) {
                Circle circle = new Circle(RAYON);  // crée un cercle avec le rayon spécifié
                circle.setCenterX(RAYON);           // set le coord x du centre du cercle
                circle.setCenterY(RAYON);           // set le coord y du centre du cercle
                // on déplace le cercle, le rayon permet d'ajouter du padding
                circle.setTranslateX(col * (CASE_TAILLE_MAX + ECART) + (double) PADDING);
                circle.setTranslateY(row * (CASE_TAILLE_MAX + ECART) + (double) PADDING);
                // on crée une case du plateau
                gridShape = Shape.subtract(gridShape, circle);
            }
        }
        gridShape.setFill(Color.CORNFLOWERBLUE);

        return gridShape;
    }

    private List<Rectangle> makeColonnes() {
        List<Rectangle> list = new ArrayList<>();
        for(int col = 0; col < game.NB_COL; col++) {
            Rectangle rect = new Rectangle(CASE_TAILLE_MAX, largeurTotal); // un rectangle par colonne
            rect.setTranslateX(col * (CASE_TAILLE_MAX + ECART) + (double) PADDING);
            rect.setFill(Color.TRANSPARENT);
            // configure les events lorsque l'user va intéragir avec une colonne
            rect.setOnMouseEntered(e -> rect.setFill(Color.rgb(200, 200, 50, 0.3)));
            rect.setOnMouseExited(e -> rect.setFill(Color.TRANSPARENT));

            int colonne = col;
            rect.setOnMouseClicked(e -> placerPieceGUI(colonne));
            list.add(rect);
        }
        return list;
    }

    private void placerPieceGUI(int col) {
        Circle piece = createCircle(game.getCurrentPlayer().getPiece());
        int row = game.placePiece(col); // TODO set a valid button, don't place it
        if(row < 0) return;
        piecePane.getChildren().add(piece);
        piece.setTranslateX(col * (CASE_TAILLE_MAX + ECART) + (double) PADDING);
        TranslateTransition animation = new TranslateTransition(Duration.seconds(0.5), piece);
        animation.setToY(row * (CASE_TAILLE_MAX + ECART) + (double) PADDING);

        animation.setOnFinished(e -> {
            if(game.isOver()) {
                System.out.println("GAME OVER");
                Player winner = game.getWinner();
                if(winner != null) {
                    titleLabel.setText("Partie remportée par " + winner.getName()); // TODO a mettre dans un popup
                } else{
                    titleLabel.setText("Egalité !");
                }
                titleLabel.textFillProperty().setValue(Color.DARKVIOLET);
            } else {
                game.nextPlayer();
                updateInformationLabel();
                // TODO a déplacer dans un meilleur endroit
                Player ia = game.getCurrentPlayer();
                if(ia instanceof Computer) {
                    System.out.println("IA playing");
                    int colSelectedByIA = ia.play(game); // le choix de la colonne par l'ia
                    if(!game.isOutOfLimitCol(colSelectedByIA)) placerPieceGUI(colSelectedByIA);
                    else System.out.println("Ia return a invalide number of col !"); // TODO a changer
                }
            }
        });
        animation.play();
    }

    private Circle createCircle(Piece p) {
        Circle c = new Circle(RAYON);
        c.setFill((p == Piece.ROUGE) ? Color.RED : Color.YELLOW);
        c.setCenterX(RAYON);
        c.setCenterY(RAYON);
        return c;
    }

    public void updateInformationLabel() {
        titleLabel.setText("C'est au tour du joueur : " + game.getCurrentPlayer().getName());
        titleLabel.textFillProperty().setValue(Color.BLACK);
    }
}
