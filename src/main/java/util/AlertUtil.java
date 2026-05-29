package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.Optional;

public final class AlertUtil {

    private AlertUtil() {
    }

    public static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        styleAlert(alert, title, message);
        alert.showAndWait();
    }

    public static boolean confirm(String title, String message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        styleAlert(confirm, title, message);
        Optional<ButtonType> response = confirm.showAndWait();
        return response.isPresent() && response.get() == ButtonType.OK;
    }

    private static void styleAlert(Alert alert, String title, String message) {
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        URL cssUrl = AlertUtil.class.getResource("/view/app-theme.css");
        if (cssUrl != null) {
            alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        }
        alert.getDialogPane().getStyleClass().add("app-dialog");
    }
}
