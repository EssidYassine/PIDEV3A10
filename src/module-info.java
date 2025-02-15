module Controllers {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens Controllers to javafx.fxml;
    exports Controllers;
}
