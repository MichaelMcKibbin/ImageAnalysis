module com.michaelmckibbin.imageanalysis {
    // JavaFX and main application requirements
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires java.desktop;
    requires javafx.swing;
    requires opencv;

    // Testing requirements
    //requires org.junit.jupiter.api;
    requires testfx.core;
    requires testfx.junit5;

    // Opens statements
    opens com.michaelmckibbin.imageanalysis to javafx.fxml, org.testfx.core, org.junit.platform.commons, testfx.junit5;
    exports com.michaelmckibbin.imageanalysis;
}

