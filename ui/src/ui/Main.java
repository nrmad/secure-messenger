package ui;

import datasource.DatabaseUtilites;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        DatabaseUtilites databaseUtilites = DatabaseUtilites.getInstance();
        Parent root = FXMLLoader.load(getClass().getResource("/ui/startup.fxml"));
        primaryStage.setTitle("Secure Messenger");
        primaryStage.setScene(new Scene(root, 800, 400));
        //primaryStage.setMaximized(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        Application.launch(args);
    }
}
