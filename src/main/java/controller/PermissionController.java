package controller;

import dao.GenericDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Permission;
import model.PublicServer;
import util.AlertUtil;
import util.ValidationUtil;

public class PermissionController {

    @FXML private ComboBox<PublicServer> cbServer;
    @FXML private ComboBox<String> cbPermissionType;
    @FXML private DatePicker dpDate;
    @FXML private TextField txtNumberOfDays;
    @FXML private TextArea txtJustification;

    @FXML private TableView<Permission> table;
    @FXML private TableColumn<Permission, Long> colId;
    @FXML private TableColumn<Permission, String> colServer;
    @FXML private TableColumn<Permission, String> colType;
    @FXML private TableColumn<Permission, String> colDate;
    @FXML private TableColumn<Permission, Integer> colDays;

    private final GenericDao<Permission> dao = new GenericDao<>(Permission.class);
    private final GenericDao<PublicServer> serverDao = new GenericDao<>(PublicServer.class);

    @FXML
    private void initialize() {
        cbServer.setItems(FXCollections.observableArrayList(serverDao.findAll()));
        cbPermissionType.setItems(FXCollections.observableArrayList(
                "Cita médica", "Calamidad", "Sindical", "Académico", "Personal"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colServer.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServer().toString()));
        colType.setCellValueFactory(new PropertyValueFactory<>("permissionType"));
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getDate())));
        colDays.setCellValueFactory(new PropertyValueFactory<>("numberOfDays"));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        loadData();
    }

    @FXML
    private void save() {
        if (cbServer.getValue() == null || cbPermissionType.getValue() == null || dpDate.getValue() == null
                || txtNumberOfDays.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Servidor, tipo, fecha y número de días son obligatorios.");
            return;
        }

        Integer days = ValidationUtil.parsePositiveInt(txtNumberOfDays.getText().trim());
        if (days == null || days > 30) {
            showAlert(Alert.AlertType.WARNING, "Dato inválido", "El número de días debe ser numérico entre 1 y 30.");
            return;
        }

        String justification = txtJustification.getText().trim();
        if (!ValidationUtil.isBlank(justification) && justification.length() > 500) {
            showAlert(Alert.AlertType.WARNING, "Dato inválido", "La justificación no debe superar 500 caracteres.");
            return;
        }

        Permission permission = new Permission();
        permission.setServer(cbServer.getValue());
        permission.setPermissionType(cbPermissionType.getValue());
        permission.setDate(dpDate.getValue());
        permission.setJustification(justification);
        permission.setNumberOfDays(days);

        if (dao.save(permission)) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Permiso guardado correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible guardar el permiso.");
        }
    }

    @FXML
    private void delete() {
        Permission selected = table.getSelectionModel().getSelectedItem();
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
        cbPermissionType.setValue(null);
        dpDate.setValue(null);
        txtNumberOfDays.clear();
        txtJustification.clear();
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