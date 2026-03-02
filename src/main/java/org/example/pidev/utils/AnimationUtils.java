package org.example.pidev.utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Utilitaires d'animation JavaFX pour les formulaires et l'interface.
 * Fournit des animations de succès, erreur, pulse et transitions visuelles.
 */
public class AnimationUtils {

    /**
     * Animation de succès : fade-in + scale bounce + auto-fade-out après 3s.
     */
    public static void showSuccessAnimation(Label label) {
        if (label == null) return;
        label.setOpacity(0);
        label.setScaleX(0.5);
        label.setScaleY(0.5);

        // Scale bounce
        ScaleTransition scale = new ScaleTransition(Duration.millis(400), label);
        scale.setFromX(0.5);
        scale.setFromY(0.5);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition scaleBack = new ScaleTransition(Duration.millis(200), label);
        scaleBack.setFromX(1.1);
        scaleBack.setFromY(1.1);
        scaleBack.setToX(1.0);
        scaleBack.setToY(1.0);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), label);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Fade out après délai
        FadeTransition fadeOut = new FadeTransition(Duration.millis(800), label);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(3));

        SequentialTransition sequence = new SequentialTransition(
                new ParallelTransition(fadeIn, scale),
                scaleBack,
                fadeOut
        );
        sequence.play();
    }

    /**
     * Animation d'erreur : shake horizontal + fade-in.
     */
    public static void showErrorAnimation(Label label) {
        if (label == null) return;
        label.setOpacity(0);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), label);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Shake (gauche-droite)
        TranslateTransition shake1 = new TranslateTransition(Duration.millis(50), label);
        shake1.setFromX(0);
        shake1.setToX(-10);

        TranslateTransition shake2 = new TranslateTransition(Duration.millis(50), label);
        shake2.setFromX(-10);
        shake2.setToX(10);

        TranslateTransition shake3 = new TranslateTransition(Duration.millis(50), label);
        shake3.setFromX(10);
        shake3.setToX(-8);

        TranslateTransition shake4 = new TranslateTransition(Duration.millis(50), label);
        shake4.setFromX(-8);
        shake4.setToX(6);

        TranslateTransition shake5 = new TranslateTransition(Duration.millis(50), label);
        shake5.setFromX(6);
        shake5.setToX(-3);

        TranslateTransition shakeEnd = new TranslateTransition(Duration.millis(50), label);
        shakeEnd.setFromX(-3);
        shakeEnd.setToX(0);

        SequentialTransition sequence = new SequentialTransition(
                fadeIn, shake1, shake2, shake3, shake4, shake5, shakeEnd
        );
        sequence.play();
    }

    /**
     * Animation pulse continue (pour les alertes).
     */
    public static void pulseNode(Node node) {
        if (node == null) return;
        ScaleTransition pulse = new ScaleTransition(Duration.millis(800), node);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.15);
        pulse.setToY(1.15);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.setInterpolator(Interpolator.EASE_BOTH);
        pulse.play();
    }

    /**
     * Animation slide-in depuis la gauche (pour les cartes d'alerte).
     */
    public static void slideInFromLeft(Node node, int delayMillis) {
        if (node == null) return;
        node.setTranslateX(-200);
        node.setOpacity(0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(500), node);
        slide.setFromX(-200);
        slide.setToX(0);
        slide.setDelay(Duration.millis(delayMillis));
        slide.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delayMillis));

        new ParallelTransition(slide, fade).play();
    }

    /**
     * Animation flash pour attirer l'attention.
     */
    public static void flashNode(Node node) {
        if (node == null) return;
        FadeTransition flash = new FadeTransition(Duration.millis(300), node);
        flash.setFromValue(1.0);
        flash.setToValue(0.3);
        flash.setCycleCount(6);
        flash.setAutoReverse(true);
        flash.play();
    }

    /**
     * Animation de suppression : rétrécir et disparaître.
     */
    public static void shrinkAndFade(Node node, Runnable onFinished) {
        if (node == null) return;
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(300), node);
        scaleDown.setToX(0);
        scaleDown.setToY(0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), node);
        fadeOut.setToValue(0);

        ParallelTransition anim = new ParallelTransition(scaleDown, fadeOut);
        anim.setOnFinished(e -> {
            if (onFinished != null) onFinished.run();
        });
        anim.play();
    }
}

