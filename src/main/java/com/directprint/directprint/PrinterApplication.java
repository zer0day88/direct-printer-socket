package com.directprint.directprint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;


public class PrinterApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(PrinterApplication.class.getResource("printer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 380, 400);

        stage.getIcons().add(new Image(Objects.requireNonNull(PrinterApplication.class.getResourceAsStream("icon.png"))));

        stage.setTitle("HIS PRINTER TOOL");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });


//       stage.iconifiedProperty().addListener(new ChangeListener<Boolean>() {
//           @Override
//           public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
//               System.out.println("minimized:" + t1.booleanValue());
//           }
//       });
    }
}
