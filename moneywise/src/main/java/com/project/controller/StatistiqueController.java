package com.project.controller;

import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.project.dao.TransactionDAO;
import com.project.model.Utilisateur;
import com.project.utils.DateHelper;
import com.project.utils.ResponsiveHelper;
import com.project.utils.SessionManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

public class StatistiqueController implements Initializable {

    // ── Header ──
    @FXML private Label  headerDate;
    @FXML private Label  headerUser;

    // ── Boutons période ──
    @FXML private Button btnMois;
    @FXML private Button btnTrimestre;
    @FXML private Button btnAnnee;

    // ── KPI cards principales ──
    @FXML private Label summaryRevenus;
    @FXML private Label summaryDepenses;
    @FXML private Label summaryEpargne;
    @FXML private Label summaryNbTransactions;
    @FXML private Label pieSubtitle;
    @FXML private Label barSubtitle;

    // ── Ligne analyse (fx:id distincts — pas de doublon) ──
    @FXML private Label analyseRevenus;
    @FXML private Label analyseDepenses;
    @FXML private Label analyseEpargne;

    // ── Graphiques ──
    @FXML private PieChart                 pieChart;
    @FXML private BarChart<String, Number> barChart;

    // ── Sidebar ──
    @FXML private SidebarController sidebarController;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private static final NumberFormat NF =
        NumberFormat.getNumberInstance(Locale.FRENCH);

    private String periodeActive = "mois";

    // ─────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chargerHeader();
        chargerDonnees();
        if (sidebarController != null) sidebarController.setActiveItem("statistique");
        ResponsiveHelper.bind(this::onResize);
    }

    private void onResize() {
        if (sidebarController != null)
            sidebarController.setSidebarVisible(
                ResponsiveHelper.getWidth() >= ResponsiveHelper.BP_SMALL);
    }

    // ─────────────────────────────────────────
    // HEADER
    // ─────────────────────────────────────────
    private void chargerHeader() {
        headerDate.setText(DateHelper.formaterComplet(LocalDate.now()));
        int uid = SessionManager.getUserId();
        if (uid != -1)
            headerUser.setText("Bonjour, "
                + SessionManager.getUtilisateur().getNom().split(" ")[0] + " 👋");
    }

    // ─────────────────────────────────────────
    // BOUTONS PÉRIODE
    // ─────────────────────────────────────────
    @FXML private void setPeriodeMois() {
        periodeActive = "mois";
        updatePeriodeBtns(btnMois);
        chargerDonnees();
    }

    @FXML private void setPeriodeTrimestre() {
        periodeActive = "trimestre";
        updatePeriodeBtns(btnTrimestre);
        chargerDonnees();
    }

    @FXML private void setPeriodeAnnee() {
        periodeActive = "annee";
        updatePeriodeBtns(btnAnnee);
        chargerDonnees();
    }

    private void updatePeriodeBtns(Button actif) {
        btnMois.getStyleClass().remove("periode-btn-active");
        btnTrimestre.getStyleClass().remove("periode-btn-active");
        btnAnnee.getStyleClass().remove("periode-btn-active");
        actif.getStyleClass().add("periode-btn-active");
    }

    // ─────────────────────────────────────────
    // DATES DE PÉRIODE
    // ─────────────────────────────────────────
    private LocalDate getDateDebut() {
        return switch (periodeActive) {
            case "trimestre" -> DateHelper.debutTrimestreCourant();
            case "annee"     -> DateHelper.debutAnneeCourante();
            default          -> DateHelper.debutMoisCourant();
        };
    }

    private LocalDate getDateFin() {
        return DateHelper.finMoisCourant();
    }

    // ─────────────────────────────────────────
    // CHARGER DONNÉES
    // ─────────────────────────────────────────
    private void chargerDonnees() {
        int uid = SessionManager.getUserId();
        if (uid == -1) return;
        Utilisateur user = SessionManager.getUtilisateur();
        chargerResume(user);
        chargerPieChart(user);
        chargerBarChart(user);
    }

    // ─────────────────────────────────────────
    // RÉSUMÉ
    // ─────────────────────────────────────────
    private void chargerResume(Utilisateur user) {
        var transactions = transactionDAO.rechercher(
            user.getId(), null, null, getDateDebut(), getDateFin(), null);

        double totalEntrees = 0, totalSorties = 0;
        for (var t : transactions) {
            if (t.getType().name().equals("ENTREE")) totalEntrees += t.getMontant();
            else totalSorties += t.getMontant();
        }
        double epargne = totalEntrees - totalSorties;

        // ── KPI cards principales ──
        summaryRevenus.setText(NF.format(totalEntrees) + " FCFA");
        summaryDepenses.setText(NF.format(totalSorties) + " FCFA");
        summaryEpargne.setText(NF.format(epargne) + " FCFA");
        summaryNbTransactions.setText(String.valueOf(transactions.size()));

        // Colorer l'épargne (card violette — texte blanc ou rouge pâle)
        summaryEpargne.setStyle(epargne >= 0
            ? "-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:white;"
            : "-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:#FECACA;");

        // ── Panel analyse (fx:id distincts) ──
        if (analyseRevenus != null)
            analyseRevenus.setText(NF.format(totalEntrees) + " FCFA");

        if (analyseDepenses != null)
            analyseDepenses.setText(NF.format(totalSorties) + " FCFA");

        if (analyseEpargne != null) {
            analyseEpargne.setText(NF.format(epargne) + " FCFA");
            analyseEpargne.setStyle(epargne >= 0
                ? "-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#6C63FF;"
                : "-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#E74C3C;");
        }

        // ── Labels sous-titres ──
        String labelPeriode = switch (periodeActive) {
            case "trimestre" -> "3 derniers mois";
            case "annee"     -> "Cette année";
            default          -> "Ce mois — " + DateHelper.nomMoisCourant();
        };
        if (pieSubtitle != null) pieSubtitle.setText(labelPeriode);
        if (barSubtitle != null) barSubtitle.setText("Année " + DateHelper.anneeCourante());
    }

    // ─────────────────────────────────────────
    // CAMEMBERT
    // ─────────────────────────────────────────
    private void chargerPieChart(Utilisateur user) {
        List<Object[]> data = transactionDAO.getDepensesParCategorie(user.getId());
        pieChart.getData().clear();

        if (data.isEmpty()) {
            pieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Aucune dépense", 1)));
            return;
        }

        for (Object[] row : data) {
            double montant = (double) row[1];
            if (montant > 0)
                pieChart.getData().add(new PieChart.Data(
                    row[0] + "\n" + NF.format(montant) + " FCFA", montant));
        }

        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
    }

    // ─────────────────────────────────────────
    // BARRES
    // ─────────────────────────────────────────
    private void chargerBarChart(Utilisateur user) {
        List<Object[]> data = transactionDAO.getEntreesSortiesParMois(
            user.getId(), DateHelper.anneeCourante());

        barChart.getData().clear();

        XYChart.Series<String, Number> serieE = new XYChart.Series<>();
        serieE.setName("Entrées");
        XYChart.Series<String, Number> serieS = new XYChart.Series<>();
        serieS.setName("Sorties");

        String[] moisNoms = {
            "Jan","Fév","Mar","Avr","Mai","Jun",
            "Jul","Aoû","Sep","Oct","Nov","Déc"
        };

        double[] entrees = new double[12];
        double[] sorties = new double[12];

        for (Object[] row : data) {
            int mois = (int) row[0] - 1;
            entrees[mois] = (double) row[1];
            sorties[mois] = (double) row[2];
        }

        for (int i = 0; i < 12; i++) {
            serieE.getData().add(new XYChart.Data<>(moisNoms[i], entrees[i]));
            serieS.getData().add(new XYChart.Data<>(moisNoms[i], sorties[i]));
        }

        barChart.getData().addAll(serieE, serieS);
        barChart.setAnimated(false);
    }

    // ─────────────────────────────────────────
    // EXPORT PDF
    // ─────────────────────────────────────────
    @FXML
    private void exportPDF() {
        int uid = SessionManager.getUserId();
        if (uid == -1) return;
        Utilisateur user = SessionManager.getUtilisateur();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Enregistrer le rapport PDF");
        chooser.setInitialFileName(
            "MoneyWise_rapport_" + LocalDate.now() + ".pdf");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        File fichier = chooser.showSaveDialog(null);
        if (fichier == null) return;

        try {
            genererPDF(fichier, user);
            new Alert(Alert.AlertType.INFORMATION,
                "PDF exporté avec succès !\n" + fichier.getAbsolutePath()).show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                "Erreur lors de l'export PDF : " + e.getMessage()).show();
        }
    }

    // ─────────────────────────────────────────
    // EXPORT EXCEL
    // ─────────────────────────────────────────
    @FXML
    private void exportExcel() {
        int uid = SessionManager.getUserId();
        if (uid == -1) return;
        Utilisateur user = SessionManager.getUtilisateur();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Enregistrer le fichier Excel");
        chooser.setInitialFileName(
            "MoneyWise_transactions_" + LocalDate.now() + ".xlsx");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel", "*.xlsx"));

        File fichier = chooser.showSaveDialog(null);
        if (fichier == null) return;

        try {
            genererExcel(fichier, user);
            new Alert(Alert.AlertType.INFORMATION,
                "Excel exporté avec succès !\n" + fichier.getAbsolutePath()).show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                "Erreur lors de l'export Excel : " + e.getMessage()).show();
        }
    }

    // ─────────────────────────────────────────
    // GÉNÉRATION PDF
    // ─────────────────────────────────────────
    private void genererPDF(File fichier, Utilisateur user) throws Exception {
        var transactions = transactionDAO.rechercher(
            user.getId(), null, null, getDateDebut(), getDateFin(), null);

        try (var doc = new org.apache.pdfbox.pdmodel.PDDocument()) {
            var page = new org.apache.pdfbox.pdmodel.PDPage(
                org.apache.pdfbox.pdmodel.common.PDRectangle.A4);
            doc.addPage(page);

            org.apache.pdfbox.pdmodel.font.PDFont fontBold =
                org.apache.pdfbox.pdmodel.font.PDType0Font.load(doc,
                    getClass().getResourceAsStream(
                        "/com/project/fonts/DejaVuSans-Bold.ttf"), false);
            org.apache.pdfbox.pdmodel.font.PDFont fontNormal =
                org.apache.pdfbox.pdmodel.font.PDType0Font.load(doc,
                    getClass().getResourceAsStream(
                        "/com/project/fonts/DejaVuSans.ttf"), false);

            try (var stream =
                    new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page)) {

                // Titre
                stream.beginText(); stream.setFont(fontBold, 20);
                stream.newLineAtOffset(50, 780);
                stream.showText("MoneyWise - Rapport financier");
                stream.endText();

                // Période
                stream.beginText(); stream.setFont(fontNormal, 11);
                stream.newLineAtOffset(50, 755);
                stream.showText("Periode : " + getDateDebut() + " au " + getDateFin());
                stream.endText();

                // Utilisateur
                stream.beginText(); stream.setFont(fontNormal, 11);
                stream.newLineAtOffset(50, 740);
                stream.showText("Utilisateur : "
                    + user.getNom() + " (" + user.getEmail() + ")");
                stream.endText();

                // Séparateur
                stream.moveTo(50, 730); stream.lineTo(545, 730); stream.stroke();

                // En-têtes colonnes
                int[] xCols   = {50, 130, 300, 400, 460};
                String[] heads = {"DATE","DESCRIPTION","CATEGORIE","TYPE","MONTANT"};
                for (int i = 0; i < heads.length; i++) {
                    stream.beginText(); stream.setFont(fontBold, 10);
                    stream.newLineAtOffset(xCols[i], 715);
                    stream.showText(heads[i]); stream.endText();
                }
                stream.moveTo(50, 708); stream.lineTo(545, 708); stream.stroke();

                // Lignes transactions
                int y = 693;
                for (var t : transactions) {
                    if (y < 60) break;
                    String[] vals = {
                        t.getDateTransaction()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        truncate(t.getDescription() != null
                            ? t.getDescription() : "-", 22),
                        truncate(t.getCategorieNom() != null
                            ? t.getCategorieNom() : "-", 14),
                        t.getType().getLibelle(),
                        NF.format(t.getMontant()) + " F"
                    };
                    for (int i = 0; i < vals.length; i++) {
                        stream.beginText(); stream.setFont(fontNormal, 9);
                        stream.newLineAtOffset(xCols[i], y);
                        stream.showText(vals[i]); stream.endText();
                    }
                    y -= 16;
                }

                // Totaux
                stream.moveTo(50, y - 6); stream.lineTo(545, y - 6); stream.stroke();

                double totalE = transactions.stream()
                    .filter(t -> t.getType().name().equals("ENTREE"))
                    .mapToDouble(t -> t.getMontant()).sum();
                double totalS = transactions.stream()
                    .filter(t -> t.getType().name().equals("SORTIE"))
                    .mapToDouble(t -> t.getMontant()).sum();

                stream.beginText(); stream.setFont(fontBold, 10);
                stream.newLineAtOffset(50, y - 22);
                stream.showText("Total Entrees : " + NF.format(totalE) + " FCFA");
                stream.endText();

                stream.beginText(); stream.setFont(fontBold, 10);
                stream.newLineAtOffset(300, y - 22);
                stream.showText("Total Sorties : " + NF.format(totalS) + " FCFA");
                stream.endText();

                stream.beginText(); stream.setFont(fontBold, 12);
                stream.newLineAtOffset(50, y - 44);
                stream.showText("Solde : " + NF.format(totalE - totalS) + " FCFA");
                stream.endText();
            }
            doc.save(fichier);
        }
    }

    // ─────────────────────────────────────────
    // GÉNÉRATION EXCEL
    // ─────────────────────────────────────────
    private void genererExcel(File fichier, Utilisateur user) throws Exception {
        var transactions = transactionDAO.rechercher(
            user.getId(), null, null, getDateDebut(), getDateFin(), null);

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Transactions");

            // Style header
            var headerStyle = workbook.createCellStyle();
            var headerFont  = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);

            // En-têtes colonnes
            var headerRow = sheet.createRow(0);
            String[] cols = {"Date","Description","Catégorie","Type","Montant (FCFA)"};
            for (int i = 0; i < cols.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            // Données
            int rowNum = 1;
            for (var t : transactions) {
                var row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(
                    t.getDateTransaction().toString());
                row.createCell(1).setCellValue(
                    t.getDescription() != null ? t.getDescription() : "");
                row.createCell(2).setCellValue(
                    t.getCategorieNom() != null ? t.getCategorieNom() : "");
                row.createCell(3).setCellValue(
                    t.getType().getLibelle());
                row.createCell(4).setCellValue(t.getMontant());
            }

            // Totaux
            sheet.createRow(rowNum + 1).createCell(0)
                .setCellValue("TOTAL ENTRÉES");
            sheet.getRow(rowNum + 1).createCell(4).setCellValue(
                transactions.stream()
                    .filter(t -> t.getType().name().equals("ENTREE"))
                    .mapToDouble(t -> t.getMontant()).sum());

            sheet.createRow(rowNum + 2).createCell(0)
                .setCellValue("TOTAL SORTIES");
            sheet.getRow(rowNum + 2).createCell(4).setCellValue(
                transactions.stream()
                    .filter(t -> t.getType().name().equals("SORTIE"))
                    .mapToDouble(t -> t.getMontant()).sum());

            try (var out = new java.io.FileOutputStream(fichier)) {
                workbook.write(out);
            }
        }
    }

    // ─────────────────────────────────────────
    // UTILITAIRE
    // ─────────────────────────────────────────
    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}