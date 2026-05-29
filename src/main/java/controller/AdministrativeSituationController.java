package controller;

import dao.GenericDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.AdministrativeSituation;
import model.PublicServer;
import util.AlertUtil;
import util.ValidationUtil;

import java.time.LocalDate;

public class AdministrativeSituationController {

    @FXML private ComboBox<PublicServer> cbServer;
    @FXML private ComboBox<String> cbSituationType;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private TextField txtAdministrativeActId;
    @FXML private TextArea txtDescription;

    @FXML private TableView<AdministrativeSituation> table;
    @FXML private TableColumn<AdministrativeSituation, Long> colId;
    @FXML private TableColumn<AdministrativeSituation, String> colServer;
    @FXML private TableColumn<AdministrativeSituation, String> colType;
    @FXML private TableColumn<AdministrativeSituation, String> colStartDate;
    @FXML private TableColumn<AdministrativeSituation, String> colEndDate;

    private final GenericDao<AdministrativeSituation> dao = new GenericDao<>(AdministrativeSituation.class);
    private final GenericDao<PublicServer> serverDao = new GenericDao<>(PublicServer.class);

    @FXML
    private void initialize() {
        cbServer.setItems(FXCollections.observableArrayList(serverDao.findAll()));
        cbSituationType.setItems(FXCollections.observableArrayList(
                "Vacaciones", "Permiso", "Licencia", "Encargo", "Traslado", "Comisión"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colServer.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServer().toString()));
        colType.setCellValueFactory(new PropertyValueFactory<>("situationType"));
        colStartDate.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getStartDate())));
        colEndDate.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getEndDate())));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        loadData();
    }

    @FXML
    private void save() {
        if (cbServer.getValue() == null || cbSituationType.getValue() == null || dpStartDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Servidor, tipo y fecha inicio son obligatorios.");
            return;
        }

        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();
        String actId = txtAdministrativeActId.getText().trim();

        if (endDate != null && endDate.isBefore(startDate)) {
            showAlert(Alert.AlertType.WARNING, "Fechas inválidas", "La fecha fin no puede ser anterior a la fecha inicio.");
            return;
        }

        if (!ValidationUtil.isBlank(actId) && actId.length() > 50) {
            showAlert(Alert.AlertType.WARNING, "Dato inválido", "El acto administrativo no debe superar 50 caracteres.");
            return;
        }

        AdministrativeSituation situation = new AdministrativeSituation();
        situation.setServer(cbServer.getValue());
        situation.setSituationType(cbSituationType.getValue());
        situation.setStartDate(startDate);
        situation.setEndDate(endDate);
        situation.setAdministrativeActId(actId);
        situation.setDescription(txtDescription.getText().trim());

        if (dao.save(situation)) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Situación administrativa guardada.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible guardar la situación administrativa.");
        }
    }

    @FXML
    private void delete() {
        AdministrativeSituation selected = table.getSelectionModel().getSelectedItem();
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
        cbSituationType.setValue(null);
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
        txtAdministrativeActId.clear();
        txtDescription.clear();
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