package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AlertDialog {

    public static void createDialog(String msg, String title) {

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox();
        Label txt = new Label(msg);
        Button b1 = new Button("Close");

        b1.setOnAction(e -> stage.close());
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5.0);
        root.getChildren().addAll(txt, b1);

        stage.setScene(new Scene(root, 200, 200));
        stage.showAndWait();
    }

    private void closeDialog() {

    }
}
