module com.directprint.directprint {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires java.prefs;
    requires org.apache.commons.codec;
    requires socket.io.client;
    requires engine.io.client;
    requires com.google.gson;
    requires java.logging;
    requires jasperreports;


    opens com.directprint.directprint to javafx.fxml;
    exports com.directprint.directprint;
}