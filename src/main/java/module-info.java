module org.example.pidev {
    // ========================================
    // JavaFX Modules - Core
    // ========================================
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;
    requires javafx.graphics;  // Ajouté depuis le second module-info

    // ========================================
    // JavaFX Extensions & UI Libraries
    // ========================================
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;  // Optionnel

    // ========================================
    // Spring Boot Modules
    // ========================================
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.webflux;
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires spring.boot.starter;
    requires spring.boot.starter.webflux;

    // ========================================
    // Reactive Programming
    // ========================================
    requires reactor.core;

    // ========================================
    // JSON Processing
    // ========================================
    requires com.fasterxml.jackson.databind;
    requires org.json;              // Présent dans les deux
    requires com.google.gson;

    // ========================================
    // Database & Networking
    // ========================================
    requires java.sql;               // Présent dans les deux
    requires mysql.connector.j;
    requires java.net.http;          // Ajouté depuis le second module-info

    // ========================================
    // Password Hashing
    // ========================================
    requires jbcrypt;

    // ========================================
    // PDF Generation & Email
    // ========================================
    requires itextpdf;
    requires jakarta.mail;
    // javax.mail removed - using jakarta.mail/angus-mail instead
    requires kernel;              // iText v8 kernel for Ahmed module
    requires layout;              // iText v8 layout for Ahmed module

    // ========================================
    // Google Drive API (Ahmed module)
    // ========================================
    // Google Drive/OAuth libraries are automatic modules - no requires needed

    // ========================================
    // HTTP Clients & Networking
    // ========================================
    requires retrofit2;
    requires retrofit2.converter.gson;
    requires okhttp3;

    // ========================================
    // AI & Cloud Services
    // ========================================
    requires api;
    requires service;
    requires google.cloud.translate;
    requires google.cloud.speech;

    // ========================================
    // Computer Vision
    // ========================================
    requires opencv;

    // ========================================
    // Java Desktop & AWT
    // ========================================
    requires java.desktop;
    requires java.logging;

    // ========================================
    // Package Exports
    // ========================================
    exports org.example.pidev.test;
    exports org.example.pidev.models;
    exports org.example.pidev.services;
    exports org.example.pidev.interfaces;
    exports org.example.pidev.utils;

    // Exports pour les services par module
    exports org.example.pidev.services.utilisateur;
    exports org.example.pidev.services.recoltes;
    exports org.example.pidev.services.parcelles;
    exports org.example.pidev.services.cultures;
    exports org.example.pidev.services.produits;
    exports org.example.pidev.services.stock;
    exports org.example.pidev.services.ventes;

    // Exports pour les controllers par module
    exports org.example.pidev.controllers.recoltes;
    exports org.example.pidev.controllers.cultures;
    exports org.example.pidev.controllers.parcelles;
    exports org.example.pidev.controllers.produits;
    exports org.example.pidev.controllers.stock;
    exports org.example.pidev.controllers.ventes;
    exports org.example.pidev.controllers.utilisateur;

    // ========================================
    // Package Opens for FXML Access
    // ========================================
    opens org.example.pidev.models to javafx.fxml;
    opens org.example.pidev.test to javafx.fxml;
    opens org.example.pidev.utils to javafx.fxml;

    // Controllers - Sous-packages par module
    opens org.example.pidev.controllers.recoltes to javafx.fxml;
    opens org.example.pidev.controllers.cultures to javafx.fxml;
    opens org.example.pidev.controllers.parcelles to javafx.fxml;
    opens org.example.pidev.controllers.produits to javafx.fxml;
    opens org.example.pidev.controllers.stock to javafx.fxml;
    opens org.example.pidev.controllers.ventes to javafx.fxml;
    opens org.example.pidev.controllers.utilisateur to javafx.fxml;

    // Services
    opens org.example.pidev.services.ventes to javafx.fxml;
}