package org.example.pidev.services.ahmed;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.example.pidev.models.Client;
import org.example.pidev.models.Vente;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PDFExportService - Service d'export de rapports au format PDF professionnel
 * Utilise iText 8.0.3 pour générer des documents PDF stylisés
 * Intégre les couleurs SMART FARM et un design moderne
 */
public class PDFExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Couleurs SMART FARM
    private static final DeviceRgb PRIMARY_GREEN = new DeviceRgb(45, 90, 39);      // #2d5a27
    private static final DeviceRgb LIGHT_GREEN = new DeviceRgb(118, 185, 71);      // #76b947
    private static final DeviceRgb BORDER_GRAY = new DeviceRgb(236, 240, 241);     // #ecf0f1
    private static final DeviceRgb TEXT_DARK = new DeviceRgb(44, 62, 80);          // #2c3e50

    // Polices
    private static PdfFont regularFont;
    private static PdfFont boldFont;

    static {
        try {
            regularFont = PdfFontFactory.createFont("Helvetica");
            boldFont = PdfFontFactory.createFont("Helvetica-Bold");
        } catch (Exception e) {
            System.err.println("Erreur initialisation polices: " + e.getMessage());
        }
    }

    /**
     * Export une liste de ventes au format PDF professionnel
     * Génère un rapport avec en-tête stylisé, tableau des ventes et résumé
     *
     * @param ventes Liste des ventes à exporter
     * @param clients Liste des clients pour résolution des noms
     * @param outputFile Fichier de sortie PDF
     */
    public static void exportVentesReportToPDF(List<Vente> ventes, List<Client> clients, File outputFile) throws Exception {
        try (PdfWriter writer = new PdfWriter(outputFile);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            // Ajouter l'en-tête professionnel
            addProfessionalHeader(document);

            document.add(new Paragraph("\n"));

            // Ajouter le titre du rapport
            Paragraph title = new Paragraph("RAPPORT DE VENTES")
                    .setFont(boldFont)
                    .setFontSize(20)
                    .setFontColor(TEXT_DARK)
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(title);

            // Ajouter la date du rapport
            Paragraph reportDate = new Paragraph("Généré le: " + LocalDate.now().format(DATE_FORMATTER))
                    .setFont(regularFont)
                    .setFontSize(10)
                    .setFontColor(new DeviceRgb(127, 140, 141))
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(reportDate);

            document.add(new Paragraph("\n"));

            // Ajouter le tableau des ventes
            addVentesTable(document, ventes, clients);

            document.add(new Paragraph("\n"));

            // Ajouter le résumé stylisé
            addSummarySection(document, ventes, clients);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'export du rapport des ventes en PDF: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Ajoute un en-tête professionnel avec bandeau vert SMART FARM
     */
    private static void addProfessionalHeader(Document document) throws Exception {
        Table headerTable = new Table(1);
        headerTable.setWidth(500);
        headerTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

        Cell headerCell = new Cell();
        headerCell.setBackgroundColor(PRIMARY_GREEN);
        headerCell.setPadding(15);

        Paragraph headerText = new Paragraph("🌱 SMART FARM")
                .setFont(boldFont)
                .setFontSize(28)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);

        headerCell.add(headerText);
        headerTable.addCell(headerCell);

        document.add(headerTable);
    }

    /**
     * Crée et ajoute le tableau des ventes avec Client et Montant
     */
    private static void addVentesTable(Document document, List<Vente> ventes, List<Client> clients) {
        Paragraph tableTitle = new Paragraph("Détail des Ventes")
                .setFont(boldFont)
                .setFontSize(14)
                .setFontColor(TEXT_DARK);
        document.add(tableTitle);

        Table table = new Table(2);
        table.setWidth(500);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // En-têtes du tableau
        addTableHeaderCell(table, "Client");
        addTableHeaderCell(table, "Montant (TND)");

        // Ajouter les données
        for (Vente vente : ventes) {
            String clientName = "Inconnu";
            for (Client client : clients) {
                if (client.getIdClient() == vente.getIdClient()) {
                    clientName = client.getNom() + " " + (client.getPrenom() != null ? client.getPrenom() : "");
                    break;
                }
            }

            Cell clientCell = new Cell();
            clientCell.add(new Paragraph(clientName)
                    .setFont(regularFont)
                    .setFontSize(11)
                    .setFontColor(TEXT_DARK));
            clientCell.setPadding(8);
            clientCell.setBackgroundColor(new DeviceRgb(250, 251, 252));
            table.addCell(clientCell);

            Cell montantCell = new Cell();
            montantCell.add(new Paragraph(String.format("%.2f", vente.getMontantTotal()))
                    .setFont(regularFont)
                    .setFontSize(11)
                    .setFontColor(LIGHT_GREEN)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBold());
            montantCell.setPadding(8);
            montantCell.setBackgroundColor(new DeviceRgb(250, 251, 252));
            montantCell.setTextAlignment(TextAlignment.RIGHT);
            table.addCell(montantCell);
        }

        document.add(table);
    }

    /**
     * Ajoute une cellule d'en-tête au tableau avec style
     */
    private static void addTableHeaderCell(Table table, String headerText) {
        Cell headerCell = new Cell();
        headerCell.add(new Paragraph(headerText)
                .setFont(boldFont)
                .setFontSize(12)
                .setFontColor(ColorConstants.WHITE));
        headerCell.setBackgroundColor(LIGHT_GREEN);
        headerCell.setPadding(10);
        headerCell.setTextAlignment(TextAlignment.CENTER);
        headerCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addCell(headerCell);
    }

    /**
     * Ajoute une section de résumé avec encadrés stylisés
     */
    private static void addSummarySection(Document document, List<Vente> ventes, List<Client> clients) {
        Paragraph summaryTitle = new Paragraph("📊 Résumé")
                .setFont(boldFont)
                .setFontSize(14)
                .setFontColor(TEXT_DARK);
        document.add(summaryTitle);

        document.add(new Paragraph("\n"));

        double montantTotal = ventes.stream().mapToDouble(Vente::getMontantTotal).sum();
        int totalVentes = ventes.size();

        Table summaryTable = new Table(2);
        summaryTable.setWidth(500);
        summaryTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // Encadré 1 - Total des Ventes
        Cell cell1 = new Cell();
        cell1.setBackgroundColor(new DeviceRgb(244, 247, 246));
        cell1.setPadding(15);

        Paragraph label1 = new Paragraph("Total des Ventes")
                .setFont(regularFont)
                .setFontSize(11)
                .setFontColor(new DeviceRgb(127, 140, 141));

        Paragraph value1 = new Paragraph(String.format("%.2f TND", montantTotal))
                .setFont(boldFont)
                .setFontSize(18)
                .setFontColor(PRIMARY_GREEN)
                .setTextAlignment(TextAlignment.CENTER);

        cell1.add(label1);
        cell1.add(value1);
        summaryTable.addCell(cell1);

        // Encadré 2 - Nombre de Ventes
        Cell cell2 = new Cell();
        cell2.setBackgroundColor(new DeviceRgb(244, 247, 246));
        cell2.setPadding(15);

        Paragraph label2 = new Paragraph("Nombre de Ventes")
                .setFont(regularFont)
                .setFontSize(11)
                .setFontColor(new DeviceRgb(127, 140, 141));

        Paragraph value2 = new Paragraph(String.valueOf(totalVentes))
                .setFont(boldFont)
                .setFontSize(18)
                .setFontColor(PRIMARY_GREEN)
                .setTextAlignment(TextAlignment.CENTER);

        cell2.add(label2);
        cell2.add(value2);
        summaryTable.addCell(cell2);

        document.add(summaryTable);
    }

    /**
     * Export un client unique au format PDF
     */
    public static void exportClientToPDF(Client client, File outputFile) throws Exception {
        try (PdfWriter writer = new PdfWriter(outputFile);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            addProfessionalHeader(document);
            document.add(new Paragraph("\n"));

            Paragraph title = new Paragraph("DÉTAILS CLIENT")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            Paragraph date = new Paragraph("Généré le: " + LocalDate.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(date);

            document.add(new Paragraph("\n"));

            Table table = new Table(2);
            table.setTextAlignment(TextAlignment.LEFT);

            addTableRow(table, "ID Client:", String.valueOf(client.getIdClient()));
            addTableRow(table, "Nom:", client.getNom());
            addTableRow(table, "Prénom:", client.getPrenom());
            addTableRow(table, "Email:", client.getEmail());
            addTableRow(table, "Téléphone:", client.getTelephone());
            addTableRow(table, "Adresse:", client.getAdresse());

            document.add(table);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export du client en PDF: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Export une liste de clients au format PDF
     */
    public static void exportClientsListToPDF(List<Client> clients, File outputFile) throws Exception {
        try (PdfWriter writer = new PdfWriter(outputFile);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            addProfessionalHeader(document);
            document.add(new Paragraph("\n"));

            Paragraph title = new Paragraph("LISTE DES CLIENTS")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            Paragraph info = new Paragraph("Généré le: " + LocalDate.now().format(DATE_FORMATTER) +
                    " | Total: " + clients.size() + " client(s)")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(info);

            document.add(new Paragraph("\n"));

            Table table = new Table(6);
            table.setTextAlignment(TextAlignment.CENTER);

            addHeaderCell(table, "ID");
            addHeaderCell(table, "Nom");
            addHeaderCell(table, "Prénom");
            addHeaderCell(table, "Email");
            addHeaderCell(table, "Téléphone");
            addHeaderCell(table, "Adresse");

            for (Client client : clients) {
                table.addCell(String.valueOf(client.getIdClient()));
                table.addCell(client.getNom() != null ? client.getNom() : "");
                table.addCell(client.getPrenom() != null ? client.getPrenom() : "");
                table.addCell(client.getEmail() != null ? client.getEmail() : "");
                table.addCell(client.getTelephone() != null ? client.getTelephone() : "");
                table.addCell(client.getAdresse() != null ? client.getAdresse() : "");
            }

            document.add(table);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export de la liste des clients en PDF: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Export une vente unique au format PDF
     */
    public static void exportVenteToPDF(Vente vente, File outputFile) throws Exception {
        try (PdfWriter writer = new PdfWriter(outputFile);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            addProfessionalHeader(document);
            document.add(new Paragraph("\n"));

            Paragraph title = new Paragraph("DÉTAILS VENTE")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            Paragraph date = new Paragraph("Généré le: " + LocalDate.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(date);

            document.add(new Paragraph("\n"));

            Table table = new Table(2);
            table.setTextAlignment(TextAlignment.LEFT);

            addTableRow(table, "ID Vente:", String.valueOf(vente.getIdVente()));
            addTableRow(table, "ID Client:", String.valueOf(vente.getIdClient()));
            addTableRow(table, "ID Utilisateur:", String.valueOf(vente.getIdUser()));
            addTableRow(table, "Montant Total:", String.format("%.2f TND", vente.getMontantTotal()));
            addTableRow(table, "Date Vente:", vente.getDateVente() != null ? vente.getDateVente().format(DATE_FORMATTER) : "N/A");

            document.add(table);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export de la vente en PDF: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Export une liste de ventes au format PDF (méthode legacy)
     */
    public static void exportVentesListToPDF(List<Vente> ventes, File outputFile) throws Exception {
        try (PdfWriter writer = new PdfWriter(outputFile);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            addProfessionalHeader(document);
            document.add(new Paragraph("\n"));

            Paragraph title = new Paragraph("LISTE DES VENTES")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            Paragraph info = new Paragraph("Généré le: " + LocalDate.now().format(DATE_FORMATTER) +
                    " | Total: " + ventes.size() + " vente(s)")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(info);

            document.add(new Paragraph("\n"));

            Table table = new Table(6);
            table.setTextAlignment(TextAlignment.CENTER);

            addHeaderCell(table, "ID");
            addHeaderCell(table, "Client ID");
            addHeaderCell(table, "Montant");
            addHeaderCell(table, "Date");
            addHeaderCell(table, "Utilisateur ID");
            addHeaderCell(table, "Actions");

            for (Vente vente : ventes) {
                table.addCell(String.valueOf(vente.getIdVente()));
                table.addCell(String.valueOf(vente.getIdClient()));
                table.addCell(String.format("%.2f TND", vente.getMontantTotal()));
                table.addCell(vente.getDateVente() != null ? vente.getDateVente().format(DATE_FORMATTER) : "");
                table.addCell(String.valueOf(vente.getIdUser()));
                table.addCell("Voir détails");
            }

            document.add(table);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export de la liste des ventes en PDF: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Ajoute une ligne au tableau avec clé et valeur
     */
    private static void addTableRow(Table table, String key, String value) {
        table.addCell(new Paragraph(key).setBold());
        table.addCell(new Paragraph(value));
    }

    /**
     * Ajoute une cellule d'en-tête au tableau (legacy)
     */
    private static void addHeaderCell(Table table, String headerText) {
        table.addCell(new Paragraph(headerText).setBold());
    }
}

