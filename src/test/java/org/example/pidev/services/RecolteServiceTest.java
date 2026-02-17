package org.example.pidev.services;

import org.example.pidev.models.Recolte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour RecolteService")
class RecolteServiceTest {

    private Recolte recolte;
    private RecolteService service;

    @BeforeEach
    void setUp() {
        service = new RecolteService();
        recolte = new Recolte(
                100.0,
                LocalDate.now().minusDays(10),
                "Excellente",
                "Tomate",
                "Champ A"
        );
    }

    // =====================
    // Tests de validation - Quantité
    // =====================

    @Test
    @DisplayName("Quantité positive doit être acceptée")
    void testValiderQuantitePositive() {
        assertDoesNotThrow(() -> service.validerQuantite(50.0));
    }

    @Test
    @DisplayName("Quantité zéro doit être acceptée")
    void testValiderQuantiteZero() {
        assertDoesNotThrow(() -> service.validerQuantite(0.0));
    }

    @Test
    @DisplayName("Quantité négative doit lever une exception")
    void testValiderQuantiteNegative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerQuantite(-10.0)
        );
        assertTrue(exception.getMessage().contains("négative"));
    }

    @Test
    @DisplayName("Quantité trop grande doit lever une exception")
    void testValiderQuantiteTropGrande() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerQuantite(2_000_000.0)
        );
        assertTrue(exception.getMessage().contains("trop grande"));
    }

    // =====================
    // Tests de validation - Date de récolte
    // =====================

    @Test
    @DisplayName("Date de récolte valide (passée) doit être acceptée")
    void testValiderDateRecolteValide() {
        assertDoesNotThrow(() -> service.validerDateRecolte(LocalDate.now().minusDays(5)));
    }

    @Test
    @DisplayName("Date d'aujourd'hui doit être acceptée")
    void testValiderDateRecolteAujourdhui() {
        assertDoesNotThrow(() -> service.validerDateRecolte(LocalDate.now()));
    }

    @Test
    @DisplayName("Date null doit lever une exception")
    void testValiderDateRecolteNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerDateRecolte(null)
        );
        assertTrue(exception.getMessage().contains("vide"));
    }

    @Test
    @DisplayName("Date future doit lever une exception")
    void testValiderDateRecolteFuture() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerDateRecolte(LocalDate.now().plusDays(5))
        );
        assertTrue(exception.getMessage().contains("futur"));
    }

    @Test
    @DisplayName("Date trop ancienne (>5 ans) doit lever une exception")
    void testValiderDateRecolteTropAncienne() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerDateRecolte(LocalDate.now().minusYears(6))
        );
        assertTrue(exception.getMessage().contains("antérieure à 5 ans"));
    }

    // =====================
    // Tests de validation - Qualité
    // =====================

    @Test
    @DisplayName("Qualité valide doit être acceptée")
    void testValiderQualiteValide() {
        assertDoesNotThrow(() -> service.validerQualite("Excellente"));
    }

    @Test
    @DisplayName("Qualité avec espaces doit être acceptée")
    void testValiderQualiteAvecEspaces() {
        assertDoesNotThrow(() -> service.validerQualite("  Bonne qualité  "));
    }

    @Test
    @DisplayName("Qualité null doit lever une exception")
    void testValiderQualiteNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerQualite(null)
        );
        assertTrue(exception.getMessage().contains("vide"));
    }

    @Test
    @DisplayName("Qualité vide doit lever une exception")
    void testValiderQualiteVide() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerQualite("")
        );
        assertTrue(exception.getMessage().contains("vide"));
    }

    @Test
    @DisplayName("Qualité trop courte doit lever une exception")
    void testValiderQualiteTropCourte() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerQualite("A")
        );
        assertTrue(exception.getMessage().contains("au moins"));
    }

    @Test
    @DisplayName("Qualité trop longue doit lever une exception")
    void testValiderQualiteTropLongue() {
        String qualiteLongue = "A".repeat(101);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerQualite(qualiteLongue)
        );
        assertTrue(exception.getMessage().contains("dépasser"));
    }

    @Test
    @DisplayName("Qualité avec caractères interdits doit lever une exception")
    void testValiderQualiteAvecCaracteresInterdits() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerQualite("Qualité<>\"'%;()&+")
        );
        assertTrue(exception.getMessage().contains("caractères non autorisés"));
    }

    // =====================
    // Tests de validation - Type de culture
    // =====================

    @Test
    @DisplayName("Type de culture valide doit être accepté")
    void testValiderTypeCultureValide() {
        assertDoesNotThrow(() -> service.validerTypeCulture("Tomate"));
    }

    @Test
    @DisplayName("Type de culture null doit lever une exception")
    void testValiderTypeCultureNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerTypeCulture(null)
        );
        assertTrue(exception.getMessage().contains("vide"));
    }

    @Test
    @DisplayName("Type de culture vide doit lever une exception")
    void testValiderTypeCultureVide() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerTypeCulture("")
        );
        assertTrue(exception.getMessage().contains("vide"));
    }

    @Test
    @DisplayName("Type de culture trop long doit lever une exception")
    void testValiderTypeCultureTropLong() {
        String typeLong = "A".repeat(101);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerTypeCulture(typeLong)
        );
        assertTrue(exception.getMessage().contains("trop long"));
    }

    // =====================
    // Tests de validation - Localisation
    // =====================

    @Test
    @DisplayName("Localisation valide doit être acceptée")
    void testValiderLocalisationValide() {
        assertDoesNotThrow(() -> service.validerLocalisation("Champ A"));
    }

    @Test
    @DisplayName("Localisation null doit lever une exception")
    void testValiderLocalisationNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerLocalisation(null)
        );
        assertTrue(exception.getMessage().contains("vide"));
    }

    @Test
    @DisplayName("Localisation vide doit lever une exception")
    void testValiderLocalisationVide() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerLocalisation("")
        );
        assertTrue(exception.getMessage().contains("vide"));
    }

    @Test
    @DisplayName("Localisation trop longue doit lever une exception")
    void testValiderLocalisationTropLongue() {
        String localisationLongue = "A".repeat(101);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validerLocalisation(localisationLongue)
        );
        assertTrue(exception.getMessage().contains("trop longue"));
    }

    // =====================
    // Tests de validation globale
    // =====================

    @Test
    @DisplayName("Récolte valide doit être acceptée")
    void testValiderRecolteValide() {
        assertDoesNotThrow(() -> service.valider(recolte));
    }

    @Test
    @DisplayName("Récolte avec quantité négative doit lever une exception")
    void testValiderRecolteQuantiteNegative() {
        recolte.setQuantite(-50.0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.valider(recolte)
        );
        assertTrue(exception.getMessage().contains("négative"));
    }

    @Test
    @DisplayName("Récolte avec date future doit lever une exception")
    void testValiderRecolteDateFuture() {
        recolte.setDateRecolte(LocalDate.now().plusDays(5));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.valider(recolte)
        );
        assertTrue(exception.getMessage().contains("futur"));
    }

    @Test
    @DisplayName("Récolte avec qualité vide doit lever une exception")
    void testValiderRecolteQualiteVide() {
        recolte.setQualite("");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.valider(recolte)
        );
        assertTrue(exception.getMessage().contains("vide"));
    }

    // =====================
    // Tests du modèle Recolte
    // =====================

    @Test
    @DisplayName("Création d'une récolte sans ID")
    void testCreationRecoltesSansID() {
        Recolte r = new Recolte(
                50.0,
                LocalDate.now(),
                "Bonne",
                "Patate",
                "Champ B"
        );
        assertEquals(50.0, r.getQuantite());
        assertEquals("Bonne", r.getQualite());
        assertEquals("Patate", r.getTypeCulture());
    }

    @Test
    @DisplayName("Création d'une récolte avec ID")
    void testCreationRecolteAvecID() {
        Recolte r = new Recolte(
                1,
                75.0,
                LocalDate.now(),
                "Très bonne",
                "Oignon",
                "Champ C"
        );
        assertEquals(1, r.getIdRecolte());
        assertEquals(75.0, r.getQuantite());
    }

    @Test
    @DisplayName("Setters et getters de récolte fonctionnent correctement")
    void testSettersGettersRecolte() {
        Recolte r = new Recolte();
        r.setIdRecolte(5);
        r.setQuantite(200.0);
        r.setDateRecolte(LocalDate.now());
        r.setQualite("Acceptable");
        r.setTypeCulture("Carotte");
        r.setLocalisation("Champ D");

        assertEquals(5, r.getIdRecolte());
        assertEquals(200.0, r.getQuantite());
        assertEquals("Acceptable", r.getQualite());
        assertEquals("Carotte", r.getTypeCulture());
        assertEquals("Champ D", r.getLocalisation());
    }
}

