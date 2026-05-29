package controller;

import dao.GenericDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.EmploymentLink;
import model.Position;
import model.PublicServer;
import util.AlertUtil;
import util.ValidationUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmploymentLinkController {

    @FXML private ComboBox<PublicServer> cbServer;
    @FXML private ComboBox<Position> cbPosition;
    @FXML private DatePicker dpEntryDate;
    @FXML private DatePicker dpRetirementDate;
    @FXML private TextField txtMonthlySalary;
    @FXML private ComboBox<String> cbStatus;

    @FXML private TableView<EmploymentLink> table;
    @FXML private TableColumn<EmploymentLink, Long> colId;
    @FXML private TableColumn<EmploymentLink, String> colServer;
    @FXML private TableColumn<EmploymentLink, String> colPosition;
    @FXML private TableColumn<EmploymentLink, String> colEntryDate;
    @FXML private TableColumn<EmploymentLink, String> colStatus;

    private final GenericDao<EmploymentLink> dao = new GenericDao<>(EmploymentLink.class);
    private final GenericDao<PublicServer> serverDao = new GenericDao<>(PublicServer.class);
    private final GenericDao<Position> positionDao = new GenericDao<>(Position.class);

    @FXML
    private void initialize() {
        cbStatus.setItems(FXCollections.observableArrayList("Activo", "Retirado"));
        cbServer.setItems(FXCollections.observableArrayList(serverDao.findAll()));
        cbPosition.setItems(FXCollections.observableArrayList(positionDao.findAll()));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colServer.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServer().toString()));
        colPosition.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPosition().getName()));
        colEntryDate.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getEntryDate())));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        loadData();
    }

    @FXML
    private void save() {
        if (cbServer.getValue() == null || cbPosition.getValue() == null || dpEntryDate.getValue() == null
                || txtMonthlySalary.getText().trim().isEmpty() || cbStatus.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Servidor, cargo, fecha ingreso, salario y estado son obligatorios.");
            return;
        }

        LocalDate entryDate = dpEntryDate.getValue();
        LocalDate retirementDate = dpRetirementDate.getValue();
        String status = cbStatus.getValue();

        if (retirementDate != null && retirementDate.isBefore(entryDate)) {
            showAlert(Alert.AlertType.WARNING, "Fechas inválidas", "La fecha de retiro no puede ser anterior a la fecha de ingreso.");
            return;
        }

        if ("Retirado".equals(status) && retirementDate == null) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Si el estado es Retirado, debes indicar fecha de retiro.");
            return;
        }

        if ("Activo".equals(status) && retirementDate != null) {
            showAlert(Alert.AlertType.WARNING, "Inconsistencia", "Si el estado es Activo, no debe tener fecha de retiro.");
            return;
        }

        EmploymentLink link = new EmploymentLink();
        link.setServer(cbServer.getValue());
        link.setPosition(cbPosition.getValue());
        link.setEntryDate(entryDate);
        link.setRetirementDate(retirementDate);
        link.setStatus(status);

        try {
            BigDecimal salary = new BigDecimal(txtMonthlySalary.getText().trim());
            if (salary.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(Alert.AlertType.WARNING, "Valor inválido", "La asignación mensual debe ser mayor a 0.");
                return;
            }
            link.setMonthlySalary(salary);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "Valor inválido", "La asignación mensual debe ser numérica.");
            return;
        }

        if (dao.save(link)) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Vinculación guardada correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible guardar la vinculación.");
        }
    }

    @FXML
    private void delete() {
        EmploymentLink selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Selecciona una vinculación para eliminar.");
            return;
        }

        if (dao.delete(selected.getId())) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Vinculación eliminada correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible eliminar la vinculación.");
        }
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(dao.findAll()));
    }

    private void clearFields() {
        cbServer.setValue(null);
        cbPosition.setValue(null);
        dpEntryDate.setValue(null);
        dpRetirementDate.setValue(null);
        txtMonthlySalary.clear();
        cbStatus.setValue(null);
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