package gui;

import game.Piece;
import game.Puissance4;
import ia.Niveau;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import player.Computer;
import player.Human;
import player.Player;

/**
 * Classe principale pour l'affichage de l'application
 *
 * @author Xiumin LIN, Celine LI
 */
public class GameGUI extends Application {
    private Stage mainStage;
    private Scene menuScene;
    private BorderPane menuPane;
    private TextField p1TField;
    private TextField p2TField;
    private ComboBox<Niveau> p1LvlDropList;
    private ComboBox<Niveau> p2LvlDropList;
    private ToggleGroup p1TypeToggleGrp;
    private ToggleGroup p2TypeToggleGrp;
    private static final String P1_DEFAULT_NAME = "Joueur 1";
    private static final String P2_DEFAULT_NAME = "Joueur 2";
    private static final String HUMAN_STR = "Humain";
    private static final String COMPUTER_STR = "Ordinateur";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.mainStage = stage;
        // Affiche le menu du jeu au lancement de l'application
        this.menuPane = new BorderPane();
        this.menuPane.setPadding(new Insets(0, 10, 0, 10));
        // Set the title
        this.initMenuTitle();
        // Set game option
        this.initGameParamPane();
        // Set buttons
        this.initMenuButtonBox();
        // Add menu in scene
        this.menuScene = new Scene(menuPane);
        stage.setTitle("Puissance 4");
        stage.setScene(menuScene);
        stage.sizeToScene();
        stage.show();
    }

    private void initMenuTitle() { // TODO change size
        // Titre
        Label title = new Label("Puissance 4");
        title.setStyle("-fx-font-size: 25;");
        // Description
        String desciption = "Bonjour, pour lancer une partie, veuillez définir les 2 joueurs et cliquez sur <Démarrer>."
                + "\nC'est le joueur 1 (Piece ROUGE) qui va jouer en premier.";
        Label descriptionLabel = new Label(desciption);
        descriptionLabel.setStyle("-fx-font-size: 13;");
        descriptionLabel.setMaxWidth(550);
        descriptionLabel.setWrapText(true);
        // Put in a VBox
        VBox titleVbox = new VBox(title, descriptionLabel);
        titleVbox.setAlignment(Pos.CENTER);
        titleVbox.setSpacing(10);
        titleVbox.setPadding(new Insets(10, 20, 30, 20)); // top, right, bot, left;
        this.menuPane.setTop(titleVbox);
    }

    private void initMenuButtonBox() {
        Button leaveBtn = new Button("Quitter");
        Button startBtn = new Button("Démarrer");
        leaveBtn.setPrefSize(150, 70);
        startBtn.setPrefSize(150, 70);
        leaveBtn.setOnAction(e -> mainStage.close());
        startBtn.setOnAction(e -> launchGameAction());
        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(leaveBtn, startBtn);
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(30, 10, 10, 10));
        buttonBox.setAlignment(Pos.CENTER);
        this.menuPane.setBottom(buttonBox);
    }

    private void launchGameAction() {
        // Create P1
        Player p1 = createPlayer(true);
        // Create P2
        Player p2 = createPlayer(false);
        System.out.println("Players : ");
        System.out.println("p1 : " + p1);
        System.out.println("p2 : " + p2);
        Puissance4 game = new Puissance4(p1, p2, true);
        Puissance4Pane p4GUI = new Puissance4Pane(game);
        Button backToMenuBtn = new Button("Menu");
        backToMenuBtn.setOnAction(e -> {
            p4GUI.stopGame(); // changer la scene n'arrete pas le jeu donc il faut l'arreter mannuellement
            switchScene(this.menuScene);
        });
        p4GUI.addBackToMenuInButtonBar(backToMenuBtn);
        this.switchScene(new Scene(p4GUI));
    }

    private Player createPlayer(boolean isPlayer1) {
        Piece piece = isPlayer1 ? Piece.ROUGE : Piece.JAUNE;
        TextField textField = isPlayer1 ? p1TField : p2TField;
        String defaultName = isPlayer1 ? P1_DEFAULT_NAME : P2_DEFAULT_NAME;
        String name = textField.getText().isEmpty() ? defaultName : textField.getText();

        RadioButton selectedToggle;
        selectedToggle = isPlayer1 ? (RadioButton) p1TypeToggleGrp.getSelectedToggle()
                : (RadioButton) p2TypeToggleGrp.getSelectedToggle();

        if(selectedToggle.getText().equals(HUMAN_STR)) {
            return new Human(name, piece);
        }
        Niveau lvl = isPlayer1 ? p1LvlDropList.getValue() : p2LvlDropList.getValue();
        return new Computer(name, piece, lvl);
    }

    private void switchScene(Scene scene) {
        this.mainStage.setScene(scene);
    }

    private void initGameParamPane() {
        // Set border
        BorderStroke borderStrokeRed = new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(1));
        BorderStroke borderStrokeOrange = new BorderStroke(Color.ORANGE, BorderStrokeStyle.SOLID, null, new BorderWidths(1));
        Border borderRed = new Border(borderStrokeRed);
        Border borderYellow = new Border(borderStrokeOrange);
        // Player 1 param box
        this.p1TField = new TextField();
        this.p1TField.setText(P1_DEFAULT_NAME);
        this.p1LvlDropList = new ComboBox<>();
        this.p1TypeToggleGrp = new ToggleGroup();
        VBox player1Box = initPlayerParamBox(P1_DEFAULT_NAME, borderRed, p1TField, p1LvlDropList, p1TypeToggleGrp);
        // Player 2 param box
        this.p2TField = new TextField();
        this.p2TField.setText(P2_DEFAULT_NAME);
        this.p2LvlDropList = new ComboBox<>();
        this.p2TypeToggleGrp = new ToggleGroup();
        VBox player2Box = initPlayerParamBox(P2_DEFAULT_NAME, borderYellow, p2TField, p2LvlDropList, p2TypeToggleGrp);
        // Add in menu pane
        Label versusLabel = new Label("VS");
        versusLabel.setPadding(new Insets(0, 20, 0, 20));
        this.menuPane.setCenter(versusLabel);
        this.menuPane.setLeft(player1Box);
        this.menuPane.setRight(player2Box);
    }

    private VBox initPlayerParamBox(String title, Border border, TextField textField, ComboBox<Niveau> lvlDropList, ToggleGroup typeGrp) {
        VBox playerBox = new VBox();
        playerBox.setSpacing(5);
        playerBox.setPadding(new Insets(5));
        playerBox.setBorder(border);
        // Set box title
        Label boxTitle = new Label(title);
        HBox titleHbox = new HBox(boxTitle);
        titleHbox.setAlignment(Pos.CENTER);
        // Set Difficilty
        Label p1LvlLabel = new Label("Niveau :");
        lvlDropList.getItems().setAll(Niveau.values()); // on remplis la liste avec les différents Niveau
        lvlDropList.setValue(Niveau.MOYEN);
        HBox lvlHBox = putInHBox(p1LvlLabel, lvlDropList);
        // Set Type (player is a Human or a Computer)
        HBox typeHBox = createPlayerTypeHBox(typeGrp, lvlDropList);
        // Add all in VBox
        playerBox.getChildren().addAll(titleHbox, typeHBox, textField, lvlHBox);
        return playerBox;
    }

    public HBox putInHBox(Node... nodes) {
        HBox hBox = new HBox(nodes);
        hBox.setSpacing(5);
        return hBox;
    }

    private HBox createPlayerTypeHBox(ToggleGroup typeGrp, ComboBox<Niveau> lvlDropList) {
        Label p1TypeLabel = new Label("Type :");
        typeGrp.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if(typeGrp.getSelectedToggle() != null) {
                RadioButton selectedToggle = (RadioButton) typeGrp.getSelectedToggle();
                lvlDropList.setDisable(selectedToggle.getText().equals(HUMAN_STR));
            }
        });

        // Radio bouton : Humain
        RadioButton humanRadioBtn = new RadioButton(HUMAN_STR);
        humanRadioBtn.setToggleGroup(typeGrp);
        humanRadioBtn.setSelected(true);
        // Radio bouton : Ordinateur
        RadioButton computerRadioBtn = new RadioButton(COMPUTER_STR);
        computerRadioBtn.setToggleGroup(typeGrp);

        return putInHBox(p1TypeLabel, humanRadioBtn, computerRadioBtn);
    }
}

