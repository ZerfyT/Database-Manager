package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Database Manager");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    /*@Override
    public void init() throws Exception {
        super.init();
        if (DatabaseManager.getInstance().openConnection()) {
            //AlertDialog.createDialog("Connection Error", "Error !");
            System.out.println("Closing Application...");
            Platform.exit();
        }
    }*/

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Closing Application...");
        DatabaseManager.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
