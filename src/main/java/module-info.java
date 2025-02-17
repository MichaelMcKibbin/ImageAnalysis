module com.michaelmckibbin.spaceimageanalysis {
    requires javafx.controls;
    requires javafx.fxml;


    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires java.desktop;
    requires javafx.swing;
    requires opencv;

    opens com.michaelmckibbin.spaceimageanalysis to javafx.fxml;
    exports com.michaelmckibbin.spaceimageanalysis;
}