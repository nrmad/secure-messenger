package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent dashBoard = FXMLLoader.load(getClass().getResource("/ui/dashboard.fxml"));
        Parent loginRegister = FXMLLoader.load(getClass().getResource("/ui/startup.fxml"));
        LoginController.setDashboard(dashBoard);
        DashboardController.setLoginRegister(loginRegister);
        primaryStage.setTitle("Secure Messenger");
        primaryStage.setScene(new Scene(loginRegister, 800, 400));
        primaryStage.show();
    }


    public static void main(String[] args) {
        Application.launch(args);
    }
}
