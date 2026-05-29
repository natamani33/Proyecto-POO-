package controller;

import dao.GenericDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.License;
import model.PublicServer;
import util.AlertUtil;
import util.ValidationUtil;

import java.time.LocalDate;

public class LicenseController {

    @FXML private ComboBox<PublicServer> cbServer;
    @FXML private ComboBox<String> cbLicenseType;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;

    @FXML private TableView<License> table;
    @FXML private TableColumn<License, Long> colId;
    @FXML private TableColumn<License, String> colServer;
    @FXML private TableColumn<License, String> colType;
    @FXML private TableColumn<License, String> colStartDate;
    @FXML private TableColumn<License, String> colEndDate;

    private final GenericDao<License> dao = new GenericDao<>(License.class);
    private final GenericDao<PublicServer> serverDao = new GenericDao<>(PublicServer.class);

    @FXML
    private void initialize() {
        cbServer.setItems(FXCollections.observableArrayList(serverDao.findAll()));
        cbLicenseType.setItems(FXCollections.observableArrayList(
                "Maternidad", "Paternidad", "Enfermedad común", "Laboral", "No remunerada"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colServer.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServer().toString()));
        colType.setCellValueFactory(new PropertyValueFactory<>("licenseType"));
        colStartDate.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getStartDate())));
        colEndDate.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getEndDate())));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        loadData();
    }

    @FXML
    private void save() {
        if (cbServer.getValue() == null || cbLicenseType.getValue() == null || dpStartDate.getValue() == null || dpEndDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Servidor, tipo y fechas son obligatorios.");
            return;
        }

        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();
        if (!ValidationUtil.isDateRangeValid(startDate, endDate)) {
            showAlert(Alert.AlertType.WARNING, "Fechas inválidas", "La fecha fin no puede ser anterior a la fecha inicio.");
            return;
        }

        License license = new License();
        license.setServer(cbServer.getValue());
        license.setLicenseType(cbLicenseType.getValue());
        license.setStartDate(startDate);
        license.setEndDate(endDate);

        if (dao.save(license)) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Licencia guardada correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible guardar la licencia.");
        }
    }

    @FXML
    private void delete() {
        License selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Selecciona un registro para eliminar.");
            return;
        }

        if (dao.delete(selected.getId())) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Registro eliminado correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible eliminar el registro.");
        }
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(dao.findAll()));
    }

    private void clearFields() {
        cbServer.setValue(null);
        cbLicenseType.setValue(null);
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) table.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        AlertUtil.show(type, title, message);
    }
}