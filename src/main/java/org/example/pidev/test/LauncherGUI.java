package org.example.pidev.test;

/**
 * @deprecated Fusionné dans {@link mainFX} qui est désormais le point d'entrée unique.
 * Cette classe est conservée pour compatibilité mais délègue tout à mainFX.
 */
@Deprecated
public class LauncherGUI extends mainFX {

    public static void main(String[] args) {
        // Déléguer à mainFX
        mainFX.main(args);
    }
}
