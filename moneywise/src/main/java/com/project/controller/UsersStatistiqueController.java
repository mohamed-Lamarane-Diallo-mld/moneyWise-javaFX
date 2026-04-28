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
import com.project.dao.UtilisateurDAO;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

public class UsersStatistiqueController implements Initializable {

    // ── Header ──
    @FXML private Label headerDate;
    @FXML private Label headerUser;

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

    // ── Ligne analyse ──
    @FXML private Label analyseRevenus;
    @FXML private Label analyseDepenses;
    @FXML private Label analyseEpargne;
    @FXML private Label analyseTauxEpargne;

    // ── Graphiques ──
    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;

    // ── Sidebar ──
    @FXML private SidebarController sidebarController;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.FRENCH);

    private String periodeActive = "mois";
    private int currentUserId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentUserId = SessionManager.getUserId();

        chargerHeader();
        chargerDonnees();

        if (sidebarController != null)
            sidebarController.setActiveItem("adminStatistiques");
        ResponsiveHelper.bind(this::onResize);
    }

    private void onResize() {
        if (sidebarController != null)
            sidebarController.setSidebarVisible(
                ResponsiveHelper.getWidth() >= ResponsiveHelper.BP_SMALL);
    }

    private void chargerHeader() {
        headerDate.setText(DateHelper.formaterComplet(LocalDate.now()));
        String nom = SessionManager.getUtilisateur() != null
            ? SessionManager.getUtilisateur().getNom().split(" ")[0] : "";
        headerUser.setText("Statistiques globales");
    }

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
        String styleInactif = "-fx-background-color:#F1F5F9;-fx-text-fill:#64748B;-fx-font-size:12px;-fx-font-weight:bold;-fx-padding:7 16;-fx-background-radius:8;-fx-cursor:hand;-fx-border-color:#E2E8F0;-fx-border-radius:8;";
        String styleActif = "-fx-background-color:#6C63FF;-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:bold;-fx-padding:7 16;-fx-background-radius:8;-fx-cursor:hand;";

        btnMois.setStyle(styleInactif);
        btnTrimestre.setStyle(styleInactif);
        btnAnnee.setStyle(styleInactif);
        actif.setStyle(styleActif);
    }

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

    private void chargerDonnees() {
        chargerResumeGlobal();
        chargerPieChartGlobal();
        chargerBarChartGlobal();
    }

    private void chargerResumeGlobal() {
        var transactions = transactionDAO.rechercherGlobal(
            null, null, getDateDebut(), getDateFin(), null);

        double totalEntrees = 0, totalSorties = 0;
        for (var t : transactions) {
            if (t.getType().name().equals("ENTREE")) totalEntrees += t.getMontant();
            else totalSorties += t.getMontant();
        }
        double epargne = totalEntrees - totalSorties;
        double tauxEpargne = (totalEntrees > 0) ? (epargne / totalEntrees) * 100 : 0;

        summaryRevenus.setText(NF.format(totalEntrees) + " FCFA");
        summaryDepenses.setText(NF.format(totalSorties) + " FCFA");
        summaryEpargne.setText(NF.format(epargne) + " FCFA");
        summaryNbTransactions.setText(String.valueOf(transactions.size()));

        summaryEpargne.setStyle(epargne >= 0
            ? "-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:white;"
            : "-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:#FECACA;");

        if (analyseRevenus != null) analyseRevenus.setText(NF.format(totalEntrees) + " FCFA");
        if (analyseDepenses != null) analyseDepenses.setText(NF.format(totalSorties) + " FCFA");
        if (analyseEpargne != null) {
            analyseEpargne.setText(NF.format(epargne) + " FCFA");
            analyseEpargne.setStyle(epargne >= 0 ? "-fx-text-fill:#6C63FF;" : "-fx-text-fill:#E74C3C;");
        }
        if (analyseTauxEpargne != null) {
            analyseTauxEpargne.setText(String.format("%.1f %%", tauxEpargne));
            analyseTauxEpargne.setStyle(tauxEpargne >= 20 ? "-fx-text-fill:#D97706;" : "-fx-text-fill:#EF4444;");
        }

        String suffixe = " (Global)";
        String labelPeriode = switch (periodeActive) {
            case "trimestre" -> "3 derniers mois" + suffixe;
            case "annee"     -> "Cette année" + suffixe;
            default          -> "Ce mois — " + DateHelper.nomMoisCourant() + suffixe;
        };
        if (pieSubtitle != null) pieSubtitle.setText(labelPeriode);
        if (barSubtitle != null) barSubtitle.setText("Année " + DateHelper.anneeCourante() + suffixe);
    }

    private void chargerPieChartGlobal() {
        List<Object[]> data = transactionDAO.getDepensesParCategorieGlobal(getDateDebut(), getDateFin());
        pieChart.getData().clear();
        if (data.isEmpty()) {
            pieChart.setData(FXCollections.observableArrayList(new PieChart.Data("Aucune dépense", 1)));
            return;
        }
        for (Object[] row : data) {
            double montant = (double) row[1];
            if (montant > 0)
                pieChart.getData().add(new PieChart.Data(row[0] + "\n" + NF.format(montant) + " FCFA", montant));
        }
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
    }

    private void chargerBarChartGlobal() {
        List<Object[]> data = transactionDAO.getEntreesSortiesParMoisGlobal(DateHelper.anneeCourante());
        barChart.getData().clear();

        XYChart.Series<String, Number> serieE = new XYChart.Series<>();
        serieE.setName("Entrées");
        XYChart.Series<String, Number> serieS = new XYChart.Series<>();
        serieS.setName("Sorties");

        String[] moisNoms = {"Jan","Fév","Mar","Avr","Mai","Jun","Jul","Aoû","Sep","Oct","Nov","Déc"};
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

    @FXML
    private void exportPDF() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Enregistrer le rapport PDF global");
        chooser.setInitialFileName("MoneyWise_rapport_global_" + LocalDate.now() + ".pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        File fichier = chooser.showSaveDialog(null);
        if (fichier == null) return;

        try {
            genererPDFGlobal(fichier);
            new Alert(Alert.AlertType.INFORMATION, "PDF exporté avec succès !\n" + fichier.getAbsolutePath()).show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'export PDF : " + e.getMessage()).show();
        }
    }

    @FXML
    private void exportExcel() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Enregistrer le fichier Excel global");
        chooser.setInitialFileName("MoneyWise_transactions_global_" + LocalDate.now() + ".xlsx");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));

        File fichier = chooser.showSaveDialog(null);
        if (fichier == null) return;

        try {
            genererExcelGlobal(fichier);
            new Alert(Alert.AlertType.INFORMATION, "Excel exporté avec succès !\n" + fichier.getAbsolutePath()).show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'export Excel : " + e.getMessage()).show();
        }
    }

    private void genererPDFGlobal(File fichier) throws Exception {
        var transactions = transactionDAO.rechercherGlobal(null, null, getDateDebut(), getDateFin(), null);

        try (var doc = new org.apache.pdfbox.pdmodel.PDDocument()) {
            var page = new org.apache.pdfbox.pdmodel.PDPage(org.apache.pdfbox.pdmodel.common.PDRectangle.A4);
            doc.addPage(page);

            var fontBold = org.apache.pdfbox.pdmodel.font.PDType0Font.load(doc,
                getClass().getResourceAsStream("/com/project/fonts/DejaVuSans-Bold.ttf"), false);
            var fontNormal = org.apache.pdfbox.pdmodel.font.PDType0Font.load(doc,
                getClass().getResourceAsStream("/com/project/fonts/DejaVuSans.ttf"), false);

            try (var stream = new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page)) {

                stream.beginText(); stream.setFont(fontBold, 20);
                stream.newLineAtOffset(50, 780);
                stream.showText("MoneyWise - Rapport Global");
                stream.endText();

                stream.beginText(); stream.setFont(fontNormal, 11);
                stream.newLineAtOffset(50, 755);
                stream.showText("Période : " + getDateDebut() + " au " + getDateFin());
                stream.endText();

                stream.beginText(); stream.setFont(fontNormal, 11);
                stream.newLineAtOffset(50, 740);
                stream.showText("Rapport global — Tous les utilisateurs");
                stream.endText();

                stream.moveTo(50, 730); stream.lineTo(545, 730); stream.stroke();

                int[] xCols = {50, 130, 260, 360, 430, 500};
                String[] heads = {"DATE", "UTILISATEUR", "CATÉGORIE", "TYPE", "MONTANT"};
                for (int i = 0; i < heads.length; i++) {
                    stream.beginText(); stream.setFont(fontBold, 9);
                    stream.newLineAtOffset(xCols[i], 715);
                    stream.showText(heads[i]); stream.endText();
                }
                stream.moveTo(50, 708); stream.lineTo(545, 708); stream.stroke();

                int y = 693;
                for (var t : transactions) {
                    if (y < 60) break;
                    String[] vals = {
                        t.getDateTransaction().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        truncate(t.getUtilisateurNom() != null ? t.getUtilisateurNom() : "-", 15),
                        truncate(t.getCategorieNom() != null ? t.getCategorieNom() : "-", 12),
                        t.getType().getLibelle(),
                        NF.format(t.getMontant()) + " F"
                    };
                    for (int i = 0; i < vals.length; i++) {
                        stream.beginText(); stream.setFont(fontNormal, 8);
                        stream.newLineAtOffset(xCols[i], y);
                        stream.showText(vals[i]); stream.endText();
                    }
                    y -= 14;
                }

                stream.moveTo(50, y - 6); stream.lineTo(545, y - 6); stream.stroke();

                double totalE = transactions.stream().filter(t -> t.getType().name().equals("ENTREE")).mapToDouble(t -> t.getMontant()).sum();
                double totalS = transactions.stream().filter(t -> t.getType().name().equals("SORTIE")).mapToDouble(t -> t.getMontant()).sum();

                stream.beginText(); stream.setFont(fontBold, 10);
                stream.newLineAtOffset(50, y - 22);
                stream.showText("Total Entrées : " + NF.format(totalE) + " FCFA");
                stream.endText();

                stream.beginText(); stream.setFont(fontBold, 10);
                stream.newLineAtOffset(300, y - 22);
                stream.showText("Total Sorties : " + NF.format(totalS) + " FCFA");
                stream.endText();

                stream.beginText(); stream.setFont(fontBold, 12);
                stream.newLineAtOffset(50, y - 44);
                stream.showText("Épargne : " + NF.format(totalE - totalS) + " FCFA");
                stream.endText();
            }
            doc.save(fichier);
        }
    }

    private void genererExcelGlobal(File fichier) throws Exception {
        var transactions = transactionDAO.rechercherGlobal(null, null, getDateDebut(), getDateFin(), null);

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Transactions Globales");

            var hStyle = workbook.createCellStyle();
            var hFont = workbook.createFont();
            hFont.setBold(true);
            hFont.setFontHeightInPoints((short) 11);
            hStyle.setFont(hFont);

            var headerRow = sheet.createRow(0);
            String[] cols = {"Date", "Utilisateur", "Catégorie", "Type", "Montant (FCFA)"};
            for (int i = 0; i < cols.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(hStyle);
                sheet.setColumnWidth(i, 5000);
            }

            int rowNum = 1;
            for (var t : transactions) {
                var row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(t.getDateTransaction().toString());
                row.createCell(1).setCellValue(t.getUtilisateurNom() != null ? t.getUtilisateurNom() : "");
                row.createCell(2).setCellValue(t.getCategorieNom() != null ? t.getCategorieNom() : "");
                row.createCell(3).setCellValue(t.getType().getLibelle());
                row.createCell(4).setCellValue(t.getMontant());
            }

            double totalE = transactions.stream().filter(t -> t.getType().name().equals("ENTREE")).mapToDouble(t -> t.getMontant()).sum();
            double totalS = transactions.stream().filter(t -> t.getType().name().equals("SORTIE")).mapToDouble(t -> t.getMontant()).sum();

            var r1 = sheet.createRow(rowNum + 1);
            r1.createCell(0).setCellValue("TOTAL ENTRÉES");
            r1.createCell(4).setCellValue(totalE);

            var r2 = sheet.createRow(rowNum + 2);
            r2.createCell(0).setCellValue("TOTAL SORTIES");
            r2.createCell(4).setCellValue(totalS);

            var r3 = sheet.createRow(rowNum + 3);
            r3.createCell(0).setCellValue("ÉPARGNE");
            r3.createCell(4).setCellValue(totalE - totalS);

            try (var out = new java.io.FileOutputStream(fichier)) {
                workbook.write(out);
            }
        }
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}