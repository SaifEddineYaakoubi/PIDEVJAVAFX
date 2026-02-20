module org.example.pidev {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    // requires eu.hansolo.tilesfx;
    // requires com.almasb.fxgl.all;

    requires java.sql;
    requires itextpdf;
    requires java.net.http;
    requires org.json;

    opens org.example.pidev.models to javafx.fxml;
    opens org.example.pidev.controllers to javafx.fxml;
    exports org.example.pidev.test;
    exports org.example.pidev.models;
    exports org.example.pidev.services;
    exports org.example.pidev.interfaces;
    exports org.example.pidev.utils;
    opens org.example.pidev.test to javafx.fxml;
}