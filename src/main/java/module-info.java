module org.meixea.salchemy {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens org.meixea.salchemy.model to javafx.fxml;
    exports org.meixea.salchemy.model;
    exports org.meixea.salchemy.view;
    opens org.meixea.salchemy.view to javafx.fxml;
    exports org.meixea.salchemy.controller;
    opens org.meixea.salchemy.controller to javafx.fxml;
    exports org.meixea.salchemy.db;
}