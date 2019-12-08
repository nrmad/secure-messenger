module ui {

    requires javafx.fxml;
    requires javafx.controls;

    opens ui;

    exports ui to javafx.graphics;

}