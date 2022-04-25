package gui;

import game.Piece;
import game.Puissance4;
import ia.Niveau;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import player.Computer;
import player.Human;
import player.Player;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameGUI extends Application {
    private Stage mainStage;
    private Scene menuScene;
    private BorderPane menuPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.mainStage = stage;
        // Affiche le menu du jeu au lancement de l'application
        this.menuPane = new BorderPane();
        // Set the title
        this.initMenuTitle();
        // Set game option

        // Set buttons
        this.initMenuButtonBox();
        // Add menu in scene
        this.menuScene = new Scene(menuPane);
        stage.setTitle("Puissance 4");
        stage.setScene(menuScene);
        stage.sizeToScene();
        stage.show();
    }

    private void initMenuTitle() {
        Label title = new Label("Puissance 4");
        title.setAlignment(Pos.CENTER);
        title.setMinWidth(200);
        title.setMinHeight(150);
        this.menuPane.setTop(title);
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
        this.menuPane.setBottom(buttonBox);
    }

    private void launchGameAction() {
        Player p1 = new Human("Joker", Piece.ROUGE);
        Player p2 = new Computer("AI", Piece.JAUNE, Niveau.FORT);
        Puissance4 game = new Puissance4(p1, p2, true);
        Puissance4Pane p4GUI = new Puissance4Pane(game);
        Button backToMenuBtn = new Button("Menu");
        backToMenuBtn.setOnAction(e -> switchScene(this.menuScene));
        p4GUI.addBackToMenuInButtonBar(backToMenuBtn);
        this.switchScene(new Scene(p4GUI));
    }

    private void switchScene(Scene scene) {
        this.mainStage.setScene(scene);
    }
}

