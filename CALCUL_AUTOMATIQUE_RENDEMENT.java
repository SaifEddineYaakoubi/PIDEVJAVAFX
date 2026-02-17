/**
 * ================================================================================
 * NOUVELLES MÉTHODES - CALCUL AUTOMATIQUE DU RENDEMENT
 * ================================================================================
 *
 * Ajoutées au service RendementService.java
 * Ces méthodes permettent de calculer automatiquement la productivité et d'effectuer
 * des analyses statistiques sur les rendements.
 *
 * ================================================================================
 * 1. CALCUL DE PRODUCTIVITÉ
 * ================================================================================
 *
 * public double calculerProductivite(double quantiteTotale, double surfaceExploitee)
 *
 * Description:
 *   Calcule automatiquement la productivité à partir de la quantité totale et de
 *   la surface exploitée.
 *   Formule: Productivité = Quantité totale / Surface exploitée
 *   Résultat en kg/hectare ou unité/hectare
 *
 * Exemple:
 *   double productivite = rendementService.calculerProductivite(2550.0, 25.5);
 *   // Résultat: 100.0 kg/hectare
 *
 * ================================================================================
 * 2. CRÉER UN RENDEMENT AUTOMATIQUEMENT
 * ================================================================================
 *
 * public Rendement creerRendementAutomatique(double quantiteTotale,
 *                                             double surfaceExploitee,
 *                                             int idRecolte)
 *
 * Description:
 *   Crée un objet Rendement en calculant automatiquement la productivité.
 *   Utile pour créer rapidement un rendement avec les bonnes données.
 *
 * Exemple:
 *   Rendement rendement = rendementService.creerRendementAutomatique(2550.0, 25.5, 1);
 *   // Crée un Rendement avec productivité calculée automatiquement
 *
 * ================================================================================
 * 3. AJOUTER UN RENDEMENT AUTOMATIQUEMENT
 * ================================================================================
 *
 * public boolean ajouterRendementAutomatique(double quantiteTotale,
 *                                              double surfaceExploitee,
 *                                              int idRecolte)
 *
 * Description:
 *   Ajoute directement en base de données un rendement avec productivité calculée.
 *   Raccourci pour creerRendementAutomatique() + add()
 *
 * Exemple:
 *   boolean success = rendementService.ajouterRendementAutomatique(2550.0, 25.5, 1);
 *   if (success) System.out.println("Rendement ajouté!");
 *
 * ================================================================================
 * 4. RECALCULER LA PRODUCTIVITÉ
 * ================================================================================
 *
 * public boolean recalculerProductivite(int idRendement,
 *                                        double quantiteTotale,
 *                                        double surfaceExploitee)
 *
 * Description:
 *   Recalcule la productivité d'un rendement existant avec de nouvelles données
 *   et le met à jour en base de données.
 *   Utile si les données initiales étaient incorrectes.
 *
 * Exemple:
 *   boolean success = rendementService.recalculerProductivite(5, 3000.0, 30.0);
 *   if (success) System.out.println("Productivité recalculée!");
 *
 * ================================================================================
 * 5. STATISTIQUES AGRÉGÉES
 * ================================================================================
 *
 * a) Productivité moyenne pour une récolte:
 *
 *    public double getProductiviteMoyenne(int idRecolte)
 *
 *    Retourne: La moyenne des productivités pour tous les rendements d'une récolte
 *    Exemple:
 *      double moyenne = rendementService.getProductiviteMoyenne(1);
 *      System.out.println("Productivité moyenne: " + moyenne + " kg/ha");
 *
 * ────────────────────────────────────────────────────────────────────────────
 *
 * b) Productivité totale pour une récolte:
 *
 *    public double getProductiviteTotale(int idRecolte)
 *
 *    Retourne: La somme des productivités pour tous les rendements d'une récolte
 *    Exemple:
 *      double total = rendementService.getProductiviteTotale(1);
 *      System.out.println("Productivité totale: " + total + " kg");
 *
 * ─────────────���──────────────────────────────────────────────────────────────
 *
 * c) Quantité totale pour une récolte:
 *
 *    public double getQuantiteTotalePourRecolte(int idRecolte)
 *
 *    Retourne: La somme de toutes les quantités récoltées pour une récolte
 *    Exemple:
 *      double qteTotal = rendementService.getQuantiteTotalePourRecolte(1);
 *      System.out.println("Quantité totale: " + qteTotal + " kg");
 *
 * ────────────────────────────────────────────────────────────────────────────
 *
 * d) Surface totale pour une récolte:
 *
 *    public double getSurfaceTotalePourRecolte(int idRecolte)
 *
 *    Retourne: La somme de toutes les surfaces exploitées pour une récolte
 *    Exemple:
 *      double surfaceTotal = rendementService.getSurfaceTotalePourRecolte(1);
 *      System.out.println("Surface totale: " + surfaceTotal + " ha");
 *
 * ================================================================================
 * 6. RENDEMENT EXTRÊMES
 * ================================================================================
 *
 * a) Meilleur rendement (productivité maximale):
 *
 *    public Rendement getRendementMaximum(int idRecolte)
 *
 *    Retourne: Le rendement avec la meilleure productivité
 *    Exemple:
 *      Rendement best = rendementService.getRendementMaximum(1);
 *      if (best != null) {
 *        System.out.println("Meilleur rendement: " + best.getProductivite() + " kg/ha");
 *      }
 *
 * ────────────────────────────────────────────────────────────────────────────
 *
 * b) Pire rendement (productivité minimale):
 *
 *    public Rendement getRendementMinimum(int idRecolte)
 *
 *    Retourne: Le rendement avec la pire productivité
 *    Exemple:
 *      Rendement worst = rendementService.getRendementMinimum(1);
 *      if (worst != null) {
 *        System.out.println("Pire rendement: " + worst.getProductivite() + " kg/ha");
 *      }
 *
 * ================================================================================
 * EXEMPLE COMPLET D'UTILISATION
 * ================================================================================
 *
 * RendementService rendementService = new RendementService();
 *
 * // 1. Ajouter rapidement un rendement avec calcul automatique
 * rendementService.ajouterRendementAutomatique(2550.0, 25.5, 1);
 *
 * // 2. Récupérer les statistiques pour cette récolte
 * double moyenne = rendementService.getProductiviteMoyenne(1);
 * double total = rendementService.getQuantiteTotalePourRecolte(1);
 * double surface = rendementService.getSurfaceTotalePourRecolte(1);
 *
 * System.out.println("Statistiques pour la récolte 1:");
 * System.out.println("  Productivité moyenne: " + String.format("%.2f", moyenne) + " kg/ha");
 * System.out.println("  Quantité totale: " + String.format("%.2f", total) + " kg");
 * System.out.println("  Surface totale: " + String.format("%.2f", surface) + " ha");
 *
 * // 3. Trouver les meilleurs et pires rendements
 * Rendement best = rendementService.getRendementMaximum(1);
 * Rendement worst = rendementService.getRendementMinimum(1);
 *
 * System.out.println("Meilleur rendement: " + best.getProductivite() + " kg/ha");
 * System.out.println("Pire rendement: " + worst.getProductivite() + " kg/ha");
 *
 * // 4. Corriger un rendement avec recalcul
 * rendementService.recalculerProductivite(5, 3000.0, 30.0);
 *
 * ================================================================================
 * CAS D'USAGE
 * ================================================================================
 *
 * ✅ Gestion agricole:
 *   - Ajouter rapidement des rendements après une récolte
 *   - Analyser les performances de différents champs
 *   - Comparer les productivités
 *
 * ✅ Rapports et statistiques:
 *   - Générer des rapports de rendement
 *   - Calculer les moyennes par saison
 *   - Identifier les meilleures et pires pratiques
 *
 * ✅ Optimisation:
 *   - Améliorer les techniques de culture
 *   - Allouer les ressources aux meilleurs champs
 *   - Planifier les futures plantations
 *
 * ================================================================================
 */

