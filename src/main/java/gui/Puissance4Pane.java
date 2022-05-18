package gui;

import game.Piece;
import game.Puissance4;
import javafx.animation.PauseTransition;
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
 * @author Xiumin LIN, Almas Baimagambetov
 */
public class Puissance4Pane extends BorderPane {
    private static final int CASE_TAILLE_MAX = 70;  // Taille d'une case du plateau
    private static final int ECART = 10;            // L'écart entre chaque case
    private static final int RAYON = CASE_TAILLE_MAX / 2;   // Taille du rayon du cercle d'une case
    private static final int PADDING = CASE_TAILLE_MAX / 3; // Le padding du panneau
    private final Puissance4 initialGame; // le jeu à l'état initial, utilisé si on souhaite reset la partie
    private Puissance4 game; // le jeu en cours
    private final int largeurTotal; // largeur total de la grille
    private final int longueurTotal; // longueur total de la grille
    private Pane gamePane; // le panneau central contenant jeu
    private Pane plateauPane; // le plateau à l'intérieur du panneau central "gamePane"
    private Circle lastPiece; // référence de la pièce posée visuellement dans le plateau
    private Label titleLabel; // le label affichant les info du jeu (ici affiche le joueur qui doit jouer)
    private Button validBtn;
    private ButtonBar buttonBar; // la barre des boutons, contien : "Menu", "Reset" et "Valider"
    /**
     * La colonne selectionée par le joueur pour placer sa pièce
     */
    private int selectionedCol;
    /**
     * Le temps d'attente (en ms) avant qu'un joueur puisse jouer, s'il y a un humain dans la partie,
     * il n'y a pas d'attente, sinon l'attente est de 500 ms
     */
    private final int waitDuration;
    /**
     * Indique si le panneau principal est en actif (il est désactivé lorsque la scène est changée)
     */
    private boolean paneIsAlive;

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
        this.waitDuration = game.getHaveAHumanPlayer() ? 0 : 500;
        this.paneIsAlive = true;
        // Init le label contenant les infos de la partie
        this.initLabel();
        // Init et création d'une box contenant une bar de boutons pour valider, reset et quitter le jeu
        this.setBottom(initButtonBar());
        // Création de l'aspect graphique du plateau de jeu au centre du pane
        this.initPlateauPane();
        // Démarre la partie
        this.playerAction(game.getCurrentPlayer());
    }

    /**
     * Initialise le label du panneau Puissance 4
     */
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
        this.gamePane = new Pane();
        this.plateauPane = new Pane();
        gamePane.getChildren().add(plateauPane); // plateauPane doit etre ajouté en 1er dans plateauPane
        Shape pieceGrille = createGridShape(); // La grille affichant l'emplacement des pièces disponibles
        gamePane.getChildren().add(pieceGrille);
        gamePane.getChildren().addAll(makeColonnes()); // interaction avec les colonnes
        this.setCenter(gamePane);
    }

    /**
     * @return un object Shape représentant l'emplacement des pièces pièces disponibles sur le plateau
     */
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

    /**
     * Crée une liste de rectangle qui représente les différentes colonnes pour poser des pièces
     *
     * @return une liste de rectangle qui représente les différentes colonnes pour poser des pièces
     */
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

    /**
     * Crée une pièce du jeu avec la couleur du joueur qui joue et la place dans
     * la colonne (indiqué en param) du plateau. La pièce n'est placer que visuellement, si le joueurs
     * choisie une autre colonne, la pièce posée est supprimée et sera placée à l'autre emplacement.
     *
     * @param col la colonne où on souhaite placer une pièce (visuellement)
     */
    private void placePieceGUI(int col) {
        this.selectionedCol = col;
        // Si le joueur a déjà posé une pièce (visuellement), on le supprime
        if(lastPiece != null) plateauPane.getChildren().remove(lastPiece);
        Circle piece = createCircle(game.getCurrentPlayer().getPiece());
        this.lastPiece = piece;

        int row = game.nextEmptyCaseRow(col); // on place la piece
        if(row < 0) return;

        this.plateauPane.getChildren().add(piece);
        piece.setTranslateX(col * (CASE_TAILLE_MAX + ECART) + (double) PADDING);
        TranslateTransition animation = new TranslateTransition(Duration.seconds(0.5), piece);
        animation.setToY(row * (CASE_TAILLE_MAX + ECART) + (double) PADDING);
        animation.play();

        this.validBtn.setDisable(false);
    }

    /**
     * Crée le visuel d'une pièce
     *
     * @param p la pièce dont l'affichage doit être créé
     * @return l'objet "Circle" représentant le visuel de la pièce donnée en param
     */
    private Circle createCircle(Piece p) {
        Circle c = new Circle(RAYON);
        c.setFill((p == Piece.ROUGE) ? Color.RED : Color.YELLOW);
        c.setCenterX(RAYON);
        c.setCenterY(RAYON);
        return c;
    }

    /**
     * Met à jour les informations du label titre.
     */
    public void updateInformationLabel() {
        Player currentP = game.getCurrentPlayer();
        titleLabel.setText(" Au tour de : " + currentP.toString() + " ");
        if(currentP.getPiece() == Piece.ROUGE)
            titleLabel.setStyle("-fx-background-color: lightsalmon  ;-fx-font-size: 20;");
        else
            titleLabel.setStyle("-fx-background-color: gold;-fx-font-size: 20;");
        titleLabel.textFillProperty().setValue(Color.BLACK);
    }

    /**
     * Initialise la barre des boutons, contient les boutons suivants :
     * <p>
     * - Menu : pour retourner au menu principal
     * - Reset : pour recommencer une partie
     * - Valider : pour confirmer le placement d'une pièce
     * </p>
     *
     * @return l'objet contenant les différents boutons
     */
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
        buttonBox.setPadding(new Insets(10, 0, 10, 0));
        buttonBox.setSpacing(10);
        return buttonBox;
    }

    /**
     * Valide le placement d'une pièce, le joueur doit avoir préalablement choisie la colonne où sa pièce sera posé.
     * La validation lance le processus vérifiant si la partie à été remporté,
     * si ce n'est as le cas, la partie passe au tour suivant et lance l'action du prochain joueur.
     */
    private void validerPlacePieceGui() {
        validBtn.setDisable(true);
        // Placer la pièce permet de passer au tour du joueur suivant si la partie n'est pas encore terminée.
        game.placePiece(selectionedCol);
        lastPiece = null;
        if(game.isOver()) {
            System.out.println("< GAME OVER >");
            this.gamePane.setDisable(true);
            Player winner = game.getWinner();
            if(winner != null) {
                titleLabel.setText(" Partie remportée par " + winner + " ");
            } else {
                titleLabel.setText(" Egalité ! ");
            }
        } else {
            updateInformationLabel();
            Player nextPlayer = game.getCurrentPlayer();
            playerAction(nextPlayer);
        }
    }

    /**
     * Lance l'action du joueur actuel :
     * Si c'est un humain, aucune action automatique est lancée, l'humain doit intéragir avec l'interface pour jouer
     * Si c'est une IA, elle va choisir et poser automatiquement une pièce à la colonne où elle souhaite jouer.
     *
     * @param currentPlayer le joueur actuel
     */
    private void playerAction(Player currentPlayer) {
        if(currentPlayer instanceof Computer) {
            validBtn.setDisable(true);
            gamePane.setDisable(true);
            PauseTransition pause = new PauseTransition(Duration.millis(this.waitDuration));
            Puissance4 p4Game = game;
            pause.setOnFinished(e -> {
                int colSelectedByIA = currentPlayer.play(p4Game); // le choix de la colonne par l'ia
                if(!p4Game.isOutOfLimitCol(colSelectedByIA)) {
                    placePieceGUI(colSelectedByIA);
                    validerPlacePieceGui();
                } else titleLabel.setText("[Error] Ia return a invalide number of col !");
                validBtn.setDisable(false);
                gamePane.setDisable(false);
            });
            pause.play();
        }
    }

    /**
     * Reinitialise le jeu
     */
    private void resetGameGui() {
        this.game.setGameIsOver();
        this.game = new Puissance4(initialGame);
        updateInformationLabel();
        // Création de l'aspect graphique du plateau de jeu
        this.initPlateauPane();
        if(!this.paneIsAlive) return;
        this.playerAction(game.getCurrentPlayer());
    }

    /**
     * Défini les tailles des boutons utilisées dans la barre des boutons
     *
     * @param btn un bouton
     */
    private void setButtonPrefSize(Button btn) {
        btn.setPrefSize(150, 70);
    }

    /**
     * Ajoute le bouton "Menu" dans la barre des boutons
     *
     * @param backBtn le bouton "Menu"
     */
    public void addBackToMenuInButtonBar(Button backBtn) {
        this.setButtonPrefSize(backBtn);
        ButtonBar.setButtonData(backBtn, ButtonBar.ButtonData.LEFT);
        this.buttonBar.getButtons().add(backBtn);
    }

    /**
     * Arrete le jeu en cours en le réinitialisant
     */
    public void stopGame() {
        paneIsAlive = false;
        this.resetGameGui();
    }
}
