package ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML
    private ToggleGroup requestToggleGroup;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPass;
    @FXML
    private TextField alias;
    @FXML
    private TextField patronId;
    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField trustText;
    @FXML
    private Button trustSelectorBtn;
    @FXML
    private Button createAccountBtn;

//    public void isInteger

    public void initialize() {
        requestToggleGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            RadioButton selected = (RadioButton) t1;
            if(selected.getText().equals("add")){
                confirmPass.setDisable(true);
                alias.setDisable(true);
                patronId.setDisable(true);
            } else {
                confirmPass.setDisable(false);
                alias.setDisable(false);
                patronId.setDisable(false);
            }
        });
    }
}