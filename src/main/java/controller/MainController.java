package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class MainController extends Stage {

    private static final int MAIN_WIDTH = 760;
    private static final int MAIN_HEIGHT = 560;
    private static final int CONTENT_WIDTH = 1180;
    private static final int CONTENT_HEIGHT = 760;
    private static final int MIN_WIDTH_OFFSET = 120;
    private static final int MIN_HEIGHT_OFFSET = 90;

    @FXML private Button btnServers;
    @FXML private Button btnPositions;
    @FXML private Button btnDependencies;
    @FXML private Button btnEmploymentLinks;
    @FXML private Button btnAdministrativeSituations;
    @FXML private Button btnVacations;
    @FXML private Button btnPermissions;
    @FXML private Button btnLicenses;

    public MainController() {
        try {
            URL fxmlUrl = getClass().getResource("/view/MainWindow.fxml");
            if (fxmlUrl == null) throw new RuntimeException("No se encontró MainWindow.fxml");

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setController(this);

            Scene scene = new Scene(loader.load(), MAIN_WIDTH, MAIN_HEIGHT);
            applyTheme(scene);
            setTitle("Sistema Talento Humano");
            setScene(scene);
            setMinWidth(MAIN_WIDTH - MIN_WIDTH_OFFSET);
            setMinHeight(MAIN_HEIGHT - MIN_HEIGHT_OFFSET);
            setMaximized(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        btnServers.setOnAction(e -> openWindow(
                "/view/PublicServerWindow.fxml",
                "Gestión de Servidores Públicos", CONTENT_WIDTH, CONTENT_HEIGHT));

        btnPositions.setOnAction(e -> openWindow(
                "/view/PositionWindow.fxml", "Gestión de Cargos", CONTENT_WIDTH, CONTENT_HEIGHT));

        btnDependencies.setOnAction(e -> openWindow(
            "/view/DependencyWindow.fxml",
            "Gestión de Dependencias", CONTENT_WIDTH, CONTENT_HEIGHT));

        btnEmploymentLinks.setOnAction(e -> openWindow(
            "/view/EmploymentLinkWindow.fxml",
            "Vinculación Laboral", CONTENT_WIDTH, CONTENT_HEIGHT));

        btnAdministrativeSituations.setOnAction(e -> openWindow(
            "/view/AdministrativeSituationWindow.fxml",
            "Situación Administrativa", CONTENT_WIDTH, CONTENT_HEIGHT));

        btnVacations.setOnAction(e -> openWindow(
            "/view/VacationWindow.fxml",
            "Gestión de Vacaciones", CONTENT_WIDTH, CONTENT_HEIGHT));

        btnPermissions.setOnAction(e -> openWindow(
            "/view/PermissionWindow.fxml",
            "Gestión de Permisos", CONTENT_WIDTH, CONTENT_HEIGHT));

        btnLicenses.setOnAction(e -> openWindow(
            "/view/LicenseWindow.fxml",
            "Gestión de Licencias", CONTENT_WIDTH, CONTENT_HEIGHT));
    }

    private void openWindow(String fxmlPath, String title, int width, int height) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                System.err.println("No se encontró: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load(), width, height);
            applyTheme(scene);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setMinWidth(width - MIN_WIDTH_OFFSET);
            stage.setMinHeight(height - MIN_HEIGHT_OFFSET);
            stage.setMaximized(true);
            stage.setOnHidden(e -> this.show());
            this.hide();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyTheme(Scene scene) {
        URL cssUrl = getClass().getResource("/view/app-theme.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }
}