module com.michaelmckibbin.imageanalysis {
    requires javafx.controls;
    requires javafx.fxml;


    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires java.desktop;
    requires javafx.swing;
    requires opencv;

    opens com.michaelmckibbin.imageanalysis to javafx.fxml;
    exports com.michaelmckibbin.imageanalysis;
}