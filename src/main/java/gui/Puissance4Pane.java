package gui;

import game.Piece;
import game.Puissance4;
import javafx.animation.TranslateTransition;
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
import javafx.stage.Stage;
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
    private Puissance4 initialGame;
    private Puissance4 game;
    private int largeurTotal;
    private int longueurTotal;
    private Pane piecePane;
    private Pane plateauPane;
    private Circle lastPiece;
    private Label titleLabel;
    private Button validBtn;
    private Button closebtn;
    private int selectionedCol;


    public Puissance4Pane(Puissance4 game) {
        this.game = game;
        this.initialGame = new Puissance4(game); // la copie du jeu initial, utile pour un reset du jeu
        // Taille du plateau de jeu
        int totalPadding = 2 * PADDING;
        this.largeurTotal = (Puissance4.NB_ROW) * CASE_TAILLE_MAX + totalPadding + (Puissance4.NB_ROW - 1) * ECART;
        this.longueurTotal = (Puissance4.NB_COL) * CASE_TAILLE_MAX + totalPadding + (Puissance4.NB_COL - 1) * ECART;
        this.titleLabel = new Label();
        updateInformationLabel();
        this.setTop(titleLabel);

        // Création de l'aspect graphique du plateau de jeu au centre du pane
        initPlateauPane();


        // Init et création d'une box contenant une bar de boutons pour valider, reset et quitter le jeu
        HBox buttonBox = initButtonBar();
        this.setBottom(buttonBox);
    }

    /**
     * Création de l'aspect graphique du plateau de jeu
     */
    private void initPlateauPane(){
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
        titleLabel.setText("C'est au tour du joueur : " + game.getCurrentPlayer().getName());
        titleLabel.textFillProperty().setValue(Color.BLACK);
    }

    public HBox initButtonBar() {
        // Create Button Bar
        int width = 150;
        int height = 75;
        ButtonBar buttonBar = new ButtonBar();
        this.validBtn = new Button("Valider");
        Button resetBtn = new Button("Reset");
        this.closebtn = new Button("Quitter");
        // Set pref size
        validBtn.setPrefSize(width, height);
        resetBtn.setPrefSize(width, height);
        closebtn.setPrefSize(width, height);
        // Set on action
        validBtn.setOnAction(e -> validerPlacePieceGui());
        closebtn.setOnAction(e -> ((Stage) closebtn.getScene().getWindow()).close());
        resetBtn.setOnAction(e -> resetGameGui());
        // Adding the buttons to the button bar
        ButtonBar.setButtonData(validBtn, ButtonBar.ButtonData.APPLY);
        ButtonBar.setButtonData(resetBtn, ButtonBar.ButtonData.APPLY);
        ButtonBar.setButtonData(closebtn, ButtonBar.ButtonData.CANCEL_CLOSE);
        buttonBar.getButtons().addAll(closebtn, resetBtn, validBtn);
        // Add buttons in a horizontal box
        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(buttonBar);
        buttonBox.setStyle("-fx-background-color: BEIGE");
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    private void validerPlacePieceGui() {
        validBtn.setDisable(true);
        game.placePiece(selectionedCol);
        lastPiece = null;
        if(game.isOver()) {
            System.out.println("GAME OVER");
            Player winner = game.getWinner();
            if(winner != null) {
                titleLabel.setText("Partie remportée par " + winner.getName()); // TODO a mettre dans un popup
            } else {
                titleLabel.setText("Egalité !");
            }
            titleLabel.textFillProperty().setValue(Color.DARKVIOLET);
        } else {
            updateInformationLabel();
            Player ia = game.getCurrentPlayer();
            if(ia instanceof Computer) {
                System.out.println("IA playing");
                int colSelectedByIA = ia.play(game); // le choix de la colonne par l'ia
                if(!game.isOutOfLimitCol(colSelectedByIA)) {
                    placePieceGUI(colSelectedByIA);
                    validerPlacePieceGui();
                }
                else System.out.println("Ia return a invalide number of col !"); // TODO a changer
            }
        }
    }

    private void resetGameGui(){
        this.game = new Puissance4(initialGame);
        // Taille du plateau de jeu
        int totalPadding = 2 * PADDING;
        this.largeurTotal = (Puissance4.NB_ROW) * CASE_TAILLE_MAX + totalPadding + (Puissance4.NB_ROW - 1) * ECART;
        this.longueurTotal = (Puissance4.NB_COL) * CASE_TAILLE_MAX + totalPadding + (Puissance4.NB_COL - 1) * ECART;
        this.titleLabel = new Label();
        updateInformationLabel();
        this.setTop(titleLabel);

        // Création de l'aspect graphique du plateau de jeu
        initPlateauPane();

        // Init et création d'une box contenant une bar de boutons pour valider, reset et quitter le jeu
        HBox buttonBox = initButtonBar();
        this.setBottom(buttonBox);
    }


}
