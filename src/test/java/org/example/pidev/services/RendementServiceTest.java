package org.example.pidev.services;

import org.example.pidev.models.Rendement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour RendementService")
class RendementServiceTest {

    private Rendement rendement;
    private RendementService service;

    @BeforeEach
    void setUp() {
        rendement = new Rendement(
                10.0,
                500.0,
                50.0,
                1
        );
        service = new RendementService();
    }

    // =====================
    // Tests de validation - Surface exploitée
    // =====================

    @Test
    @DisplayName("Surface exploitée valide doit être acceptée")
    void testValiderSurfaceExploiteeValide() {
        assertDoesNotThrow(() -> service.validerSurfaceExploitee(5.0));
    }

    @Test
    @DisplayName("Surface exploitée au minimum doit être acceptée")
    void testValiderSurfaceExploiteeMinimum() {
        assertDoesNotThrow(() -> service.validerSurfaceExploitee(0.01));
    }

    @Test
    @DisplayName("Surface exploitée au maximum doit être acceptée")
    void testValiderSurfaceExploiteeMaximum() {
        assertDoesNotThrow(() -> service.validerSurfaceExploitee(10000.0));
    }

    @Test
    @DisplayName("Surface exploitée trop petite doit lever une exception")
    void testValiderSurfaceExploiteeInferieurMinimum() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerSurfaceExploitee(0.001)
        );
        assertTrue(exception.getMessage().contains("minimum"));
    }

    @Test
    @DisplayName("Surface exploitée trop grande doit lever une exception")
    void testValiderSurfaceExploiteeDepasserMaximum() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerSurfaceExploitee(10001.0)
        );
        assertTrue(exception.getMessage().contains("dépasser"));
    }

    @Test
    @DisplayName("Surface exploitée négative doit lever une exception")
    void testValiderSurfaceExploiteeNegative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerSurfaceExploitee(-5.0)
        );
        assertTrue(exception.getMessage().contains("minimum"));
    }

    // =====================
    // Tests de validation - Quantité totale
    // =====================

    @Test
    @DisplayName("Quantité totale valide doit être acceptée")
    void testValiderQuantiteTotaleValide() {
        assertDoesNotThrow(() -> service.validerQuantiteTotale(500.0));
    }

    @Test
    @DisplayName("Quantité totale zéro doit être acceptée")
    void testValiderQuantiteTotaleZero() {
        assertDoesNotThrow(() -> service.validerQuantiteTotale(0.0));
    }

    @Test
    @DisplayName("Quantité totale négative doit lever une exception")
    void testValiderQuantiteTotaleNegative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerQuantiteTotale(-100.0)
        );
        assertTrue(exception.getMessage().contains("négative"));
    }

    @Test
    @DisplayName("Quantité totale trop grande doit lever une exception")
    void testValiderQuantiteTotaleTropGrande() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerQuantiteTotale(2_000_000.0)
        );
        assertTrue(exception.getMessage().contains("trop grande"));
    }

    // =====================
    // Tests de validation - Productivité
    // =====================

    @Test
    @DisplayName("Productivité valide doit être acceptée")
    void testValiderProductiviteValide() {
        assertDoesNotThrow(() -> service.validerProductivite(50.0));
    }

    @Test
    @DisplayName("Productivité zéro doit être acceptée")
    void testValiderProductiviteZero() {
        assertDoesNotThrow(() -> service.validerProductivite(0.0));
    }

    @Test
    @DisplayName("Productivité négative doit lever une exception")
    void testValiderProductiviteNegative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerProductivite(-10.0)
        );
        assertTrue(exception.getMessage().contains("négative"));
    }

    @Test
    @DisplayName("Productivité trop grande doit lever une exception")
    void testValiderProductiviteTropGrande() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerProductivite(2_000_000.0)
        );
        assertTrue(exception.getMessage().contains("trop grande"));
    }

    // =====================
    // Tests de validation - ID Récolte
    // =====================

    @Test
    @DisplayName("ID Récolte positif doit être accepté")
    void testValiderIdRecolteValide() {
        assertDoesNotThrow(() -> service.validerIdRecolte(1));
    }

    @Test
    @DisplayName("ID Récolte zéro doit lever une exception")
    void testValiderIdRecolteZero() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerIdRecolte(0)
        );
        assertTrue(exception.getMessage().contains("positif"));
    }

    @Test
    @DisplayName("ID Récolte négatif doit lever une exception")
    void testValiderIdRecolteNegatif() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerIdRecolte(-1)
        );
        assertTrue(exception.getMessage().contains("positif"));
    }

    // =====================
    // Tests de validation globale
    // =====================

    @Test
    @DisplayName("Rendement valide doit être accepté")
    void testValiderRendementValide() {
        assertDoesNotThrow(() -> service.valider(rendement));
    }

    @Test
    @DisplayName("Rendement avec surface invalide doit lever une exception")
    void testValiderRendementSurfaceInvalide() {
        rendement.setSurfaceExploitee(-5.0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.valider(rendement)
        );
        assertTrue(exception.getMessage().contains("minimum"));
    }

    @Test
    @DisplayName("Rendement avec quantité négative doit lever une exception")
    void testValiderRendementQuantiteNegative() {
        rendement.setQuantiteTotale(-100.0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.valider(rendement)
        );
        assertTrue(exception.getMessage().contains("négative"));
    }

    @Test
    @DisplayName("Rendement avec productivité négative doit lever une exception")
    void testValiderRendementProductiviteNegative() {
        rendement.setProductivite(-20.0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.valider(rendement)
        );
        assertTrue(exception.getMessage().contains("négative"));
    }

    @Test
    @DisplayName("Rendement avec ID récolte invalide doit lever une exception")
    void testValiderRendementIdRecolteInvalide() {
        rendement.setIdRecolte(-1);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.valider(rendement)
        );
        assertTrue(exception.getMessage().contains("positif"));
    }

    // =====================
    // Tests de calcul - Productivité
    // =====================

    @Test
    @DisplayName("Productivité doit être quantité / surface")
    void testCalculerProductivite() throws IllegalArgumentException {
        double productivite = service.calculerProductivite(500.0, 10.0);
        assertEquals(50.0, productivite, 0.01);
    }

    @Test
    @DisplayName("Productivité avec valeurs différentes")
    void testCalculerProductiviteDifferentes() throws IllegalArgumentException {
        double productivite = service.calculerProductivite(1000.0, 20.0);
        assertEquals(50.0, productivite, 0.01);
    }

    @Test
    @DisplayName("Productivité avec quantité zéro")
    void testCalculerProductiviteQuantiteZero() throws IllegalArgumentException {
        double productivite = service.calculerProductivite(0.0, 10.0);
        assertEquals(0.0, productivite, 0.01);
    }

    @Test
    @DisplayName("Productivité avec surface zéro doit lever une exception")
    void testCalculerProductiviteSurfaceZero() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculerProductivite(500.0, 0.0)
        );
        assertTrue(exception.getMessage().contains("positive"));
    }

    @Test
    @DisplayName("Productivité avec quantité négative doit lever une exception")
    void testCalculerProductiviteQuantiteNegative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculerProductivite(-100.0, 10.0)
        );
        assertTrue(exception.getMessage().contains("négative"));
    }

    @Test
    @DisplayName("Productivité qui dépasse les limites doit lever une exception")
    void testCalculerProductiviteDepasseLimites() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculerProductivite(1_000_000_000.0, 0.5)
        );
        assertTrue(exception.getMessage().contains("limites acceptables"));
    }

    // =====================
    // Tests du modèle Rendement
    // =====================

    @Test
    @DisplayName("Création d'un rendement sans ID")
    void testCreationRendementSansID() {
        Rendement r = new Rendement(5.0, 250.0, 50.0, 1);
        assertEquals(5.0, r.getSurfaceExploitee());
        assertEquals(250.0, r.getQuantiteTotale());
        assertEquals(50.0, r.getProductivite());
        assertEquals(1, r.getIdRecolte());
    }

    @Test
    @DisplayName("Création d'un rendement avec ID")
    void testCreationRendementAvecID() {
        Rendement r = new Rendement(1, 15.0, 750.0, 50.0, 2);
        assertEquals(1, r.getIdRendement());
        assertEquals(15.0, r.getSurfaceExploitee());
        assertEquals(750.0, r.getQuantiteTotale());
        assertEquals(50.0, r.getProductivite());
        assertEquals(2, r.getIdRecolte());
    }

    @Test
    @DisplayName("Setters et getters de rendement fonctionnent correctement")
    void testSettersGettersRendement() {
        Rendement r = new Rendement();
        r.setIdRendement(5);
        r.setSurfaceExploitee(8.5);
        r.setQuantiteTotale(425.0);
        r.setProductivite(50.0);
        r.setIdRecolte(3);

        assertEquals(5, r.getIdRendement());
        assertEquals(8.5, r.getSurfaceExploitee());
        assertEquals(425.0, r.getQuantiteTotale());
        assertEquals(50.0, r.getProductivite());
        assertEquals(3, r.getIdRecolte());
    }

    @Test
    @DisplayName("Constructeur par défaut crée un rendement")
    void testConstructeurParDefautRendement() {
        Rendement r = new Rendement();
        assertNotNull(r);
    }

    // =====================
    // Tests de créations automatiques
    // =====================

    @Test
    @DisplayName("Rendement créé avec productivité calculée")
    void testCreerRendementAutomatique() {
        Rendement r = service.creerRendementAutomatique(500.0, 10.0, 1);
        assertNotNull(r);
        assertEquals(10.0, r.getSurfaceExploitee());
        assertEquals(500.0, r.getQuantiteTotale());
        assertEquals(50.0, r.getProductivite(), 0.01);
        assertEquals(1, r.getIdRecolte());
    }

    @Test
    @DisplayName("Rendement retourne null avec paramètres invalides")
    void testCreerRendementAutomatiqueInvalide() {
        Rendement r = service.creerRendementAutomatique(500.0, 0.0, 1);
        assertNull(r);
    }

    // =====================
    // Tests de calculs supplémentaires
    // =====================

    @Test
    @DisplayName("Productivité très petite doit être acceptable")
    void testCalculerProductiviteTresPetite() throws IllegalArgumentException {
        double productivite = service.calculerProductivite(1.0, 10000.0);
        assertEquals(0.0001, productivite, 0.00001);
    }

    @Test
    @DisplayName("Productivité très grande mais dans les limites")
    void testCalculerProductiviteTresGrandeMaisAcceptable() throws IllegalArgumentException {
        double productivite = service.calculerProductivite(999_999.0, 1.0);
        assertTrue(productivite < 1_000_000);
    }

    @Test
    @DisplayName("Rendement complet avec tous les paramètres valides")
    void testValidationRendementComplet() {
        Rendement r = new Rendement(1, 7.5, 375.0, 50.0, 5);
        assertDoesNotThrow(() -> service.valider(r));
    }
}

