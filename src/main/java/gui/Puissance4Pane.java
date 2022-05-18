package gui;

import game.Piece;
import game.Puissance4;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
 * Inspiré du tutoriel fait par Almas Baimagambetov avec des modifications.
 * (source) https://github.com/AlmasB/FXTutorials/blob/master/src/main/java/com/almasb/connect4/Connect4App.java
 *
 * @author Xiumin LIN, Celine LI, Almas Baimagambetov
 */
public class Puissance4Pane extends BorderPane {
    private static final int CASE_TAILLE_MAX = 70;  // Taille d'une case du plateau
    private static final int ECART = 10;            // L'écart entre chaque case
    private static final int RAYON = CASE_TAILLE_MAX / 2;   // Taille du rayon du cercle d'une case
    private static final int PADDING = CASE_TAILLE_MAX / 3; // Le padding du panneau
    private final Puissance4 initialGame;
    private Puissance4 game;
    private final int largeurTotal;
    private final int longueurTotal;
    private Pane plateauPane;
    private Pane piecePane;
    private Circle lastPiece;
    private Label titleLabel;
    private Button validBtn;
    private ButtonBar buttonBar;
    private int selectionedCol;

    /**
     * Constructeur par défaut.
     *
     * @param game le jeu que le panneau va afficher
     */
    public Puissance4Pane(Puissance4 game) {
        this.game = game;
        this.initialGame = new Puissance4(game); // la copie du jeu initial, utile pour un reset du jeu
        // Taille du plateau de jeu
        int totalPadding = 2 * PADDING;
        this.largeurTotal = (Puissance4.NB_ROW) * CASE_TAILLE_MAX + totalPadding + (Puissance4.NB_ROW - 1) * ECART;
        this.longueurTotal = (Puissance4.NB_COL) * CASE_TAILLE_MAX + totalPadding + (Puissance4.NB_COL - 1) * ECART;
        // Init le label contenant les infos de la partie
        this.initLabel();
        // Création de l'aspect graphique du plateau de jeu au centre du pane
        this.initPlateauPane();
        // Init et création d'une box contenant une bar de boutons pour valider, reset et quitter le jeu
        this.setBottom(initButtonBar());
    }

    private void initLabel() {
        this.titleLabel = new Label();
        this.titleLabel.setMaxWidth(largeurTotal);
        this.titleLabel.setWrapText(true);
        updateInformationLabel();
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10)); // top, right, bot, left;
        this.setTop(titleBox);
    }

    /**
     * Création de l'aspect graphique du plateau de jeu
     */
    private void initPlateauPane() {
        this.plateauPane = new Pane();
        this.piecePane = new Pane();
        plateauPane.getChildren().add(piecePane); // piecePane doit etre ajouté en 1er dans plateauPane
        Shape grilleShape = createGridShape();
        plateauPane.getChildren().add(grilleShape);
        plateauPane.getChildren().addAll(makeColonnes()); // interaction avec les colonnes
        this.setCenter(plateauPane);
    }

    private Shape createGridShape() {
        Shape gridShape = new Rectangle(longueurTotal, largeurTotal);

        for(int row = 0; row < Puissance4.NB_ROW; row++) {
            for(int col = 0; col < Puissance4.NB_COL; col++) {
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
        for(int col = 0; col < Puissance4.NB_COL; col++) {
            Rectangle rect = new Rectangle(CASE_TAILLE_MAX, largeurTotal); // un rectangle par colonne
            rect.setTranslateX(col * (CASE_TAILLE_MAX + ECART) + (double) PADDING);
            rect.setFill(Color.TRANSPARENT);
            // configure les events lorsque l'user va intéragir avec une colonne
            rect.setOnMouseEntered(e -> rect.setFill(Color.rgb(100, 100, 50, 0.3)));
            rect.setOnMouseExited(e -> rect.setFill(Color.TRANSPARENT));

            int colonne = col;
            rect.setOnMouseClicked(e -> placePieceGUI(colonne));
            list.add(rect);
        }
        return list;
    }

    private void placePieceGUI(int col) {
        this.selectionedCol = col;

        if(lastPiece != null) piecePane.getChildren().remove(lastPiece);
        Circle piece = createCircle(game.getCurrentPlayer().getPiece());
        this.lastPiece = piece;

        int row = game.nextEmptyCaseRow(col); // on place la piece
        if(row < 0) return;

        this.piecePane.getChildren().add(piece);
        piece.setTranslateX(col * (CASE_TAILLE_MAX + ECART) + (double) PADDING);
        TranslateTransition animation = new TranslateTransition(Duration.seconds(0.5), piece);
        animation.setToY(row * (CASE_TAILLE_MAX + ECART) + (double) PADDING);
        animation.play();

        this.validBtn.setDisable(false);
    }

    private Circle createCircle(Piece p) {
        Circle c = new Circle(RAYON);
        c.setFill((p == Piece.ROUGE) ? Color.RED : Color.YELLOW);
        c.setCenterX(RAYON);
        c.setCenterY(RAYON);
        return c;
    }

    public void updateInformationLabel() {
        Player currentP = game.getCurrentPlayer();
        titleLabel.setText(" C'est au tour du joueur : " + currentP.getName() + " ");
        if(currentP.getPiece() == Piece.ROUGE)
            titleLabel.setStyle("-fx-background-color: lightsalmon  ;-fx-font-size: 20;");
        else
            titleLabel.setStyle("-fx-background-color: gold;-fx-font-size: 20;");
        titleLabel.textFillProperty().setValue(Color.BLACK);
    }

    public HBox initButtonBar() {
        // Create Button Bar
        this.buttonBar = new ButtonBar();
        this.validBtn = new Button("Valider");
        Button resetBtn = new Button("Recommencer");
        // Set pref size
        this.setButtonPrefSize(validBtn);
        this.setButtonPrefSize(resetBtn);
        // Set on action
        validBtn.setOnAction(e -> validerPlacePieceGui());
        resetBtn.setOnAction(e -> resetGameGui());
        // Adding the buttons to the button bar
        ButtonBar.setButtonData(validBtn, ButtonBar.ButtonData.APPLY);
        ButtonBar.setButtonData(resetBtn, ButtonBar.ButtonData.APPLY);
        buttonBar.getButtons().addAll(resetBtn, validBtn);
        // Add buttons in a horizontal box
        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(buttonBar);
        buttonBox.setStyle("-fx-background-color: BEIGE");
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 10,0));
        buttonBox.setSpacing(10);
        return buttonBox;
    }

    private void validerPlacePieceGui() {
        validBtn.setDisable(true);
        game.placePiece(selectionedCol);
        lastPiece = null;
        if(game.isOver()) {
            System.out.println("< GAME OVER >");
            this.plateauPane.setDisable(true);
            Player winner = game.getWinner();
            if(winner != null) {
                titleLabel.setText(" Partie remportée par " + winner + " "); // TODO a mettre dans un popup
            } else {
                titleLabel.setText(" Egalité ! ");
            }
        } else {
            updateInformationLabel();
            Player ia = game.getCurrentPlayer();
            if(ia instanceof Computer) {
                System.out.println("IA playing");
                int colSelectedByIA = ia.play(game); // le choix de la colonne par l'ia
                if(!game.isOutOfLimitCol(colSelectedByIA)) {
                    placePieceGUI(colSelectedByIA);
                    validerPlacePieceGui();
                } else System.out.println("Ia return a invalide number of col !");
            }
        }
    }

    private void resetGameGui() {
        this.game = new Puissance4(initialGame);
        updateInformationLabel();
        // Création de l'aspect graphique du plateau de jeu
        this.initPlateauPane();
    }

    private void setButtonPrefSize(Button btn) {
        btn.setPrefSize(150, 70);
    }

    public void addBackToMenuInButtonBar(Button backBtn) {
        this.setButtonPrefSize(backBtn);
        ButtonBar.setButtonData(backBtn, ButtonBar.ButtonData.LEFT);
        this.buttonBar.getButtons().add(backBtn);
    }
}
