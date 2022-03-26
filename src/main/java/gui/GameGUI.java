package gui;

import game.Piece;
import game.Puissance4;
import ia.Niveau;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import player.Computer;
import player.Human;
import player.Player;

public class GameGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // init le jeu
        int nbRows = 6;
        int nbCols = 7;
        Player p1 = new Human("Joker", Piece.ROUGE);
        Player p2 = new Computer("AI", Piece.JAUNE, Niveau.FAIBLE);
        Puissance4 game = new Puissance4(nbRows, nbCols, p1, p2); // TODO ajouter dans menu
        // Affiche le jeu
        stage.setTitle("Puissance 4");
        Puissance4Pane gui = new Puissance4Pane(game);
        Scene scene = new Scene(gui); // TODO CREER MENU
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}
