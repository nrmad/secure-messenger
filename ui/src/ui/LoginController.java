package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    private static Parent dashboard;
    @FXML
    Button loginBtn;

    static void setDashboard(Parent dashboard){
        LoginController.dashboard = dashboard;
    }

    @FXML
    public void loginHandler(MouseEvent event){
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.hide();
            stage.setMaximized(true);
            stage.setScene(new Scene(dashboard));
            // LOAD DATA
            stage.show();
    }
}
