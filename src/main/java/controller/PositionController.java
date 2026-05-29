package controller;

import dao.GenericDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Dependency;
import model.Position;
import util.AlertUtil;
import util.ValidationUtil;

public class PositionController {

    @FXML private TextField txtName;
    @FXML private TextField txtCode;
    @FXML private TextField txtGrade;
    @FXML private ComboBox<Dependency> cbDependency;

    @FXML private TableView<Position>           table;
    @FXML private TableColumn<Position, Long>   colId;
    @FXML private TableColumn<Position, String> colName;
    @FXML private TableColumn<Position, String> colCode;
    @FXML private TableColumn<Position, String> colGrade;
    @FXML private TableColumn<Position, String> colDependency;

    private final GenericDao<Position> dao = new GenericDao<>(Position.class);
    private final GenericDao<Dependency> dependencyDao = new GenericDao<>(Dependency.class);

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colDependency.setCellValueFactory(cell -> {
            Dependency dep = cell.getValue().getDependency();
            return new javafx.beans.property.SimpleStringProperty(dep != null ? dep.getName() : "");
        });

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        cbDependency.setItems(FXCollections.observableArrayList(dependencyDao.findAll()));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, s) -> {
            if (s != null) fillForm(s);
        });

        loadData();
    }

    @FXML
    private void save() {
        if (!validateFields()) return;

        Position p = new Position();
        p.setName(txtName.getText().trim());
        p.setCode(txtCode.getText().trim());
        p.setGrade(txtGrade.getText().trim());
        p.setDependency(cbDependency.getValue());

        if (dao.save(p)) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Cargo guardado correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible guardar el cargo.");
        }
    }

    @FXML
    private void delete() {
        Position selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia",
                    "Selecciona un cargo de la tabla para eliminar.");
            return;
        }

        if (AlertUtil.confirm("Confirmar eliminación", "¿Eliminar el cargo \"" + selected.getName() + "\"?")) {
            if (dao.delete(selected.getId())) {
                loadData();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Cargo eliminado correctamente.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No fue posible eliminar el cargo.");
            }
        }
    }

    private void fillForm(Position p) {
        txtName.setText(p.getName());
        txtCode.setText(p.getCode());
        txtGrade.setText(p.getGrade());
        cbDependency.setValue(p.getDependency());
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        String name = txtName.getText().trim();
        String code = txtCode.getText().trim();
        String grade = txtGrade.getText().trim();

        if (ValidationUtil.isBlank(name)) errors.append("• Nombre es obligatorio.\n");
        else if (!ValidationUtil.isLettersAndSpaces(name)) errors.append("• Nombre solo debe contener letras y espacios.\n");

        if (ValidationUtil.isBlank(code)) errors.append("• Código es obligatorio.\n");
        else if (!ValidationUtil.isAlphanumericDash(code, 2, 20)) errors.append("• Código debe ser alfanumérico (2-20), permite guion.\n");

        Integer parsedGrade = ValidationUtil.parsePositiveInt(grade);
        if (ValidationUtil.isBlank(grade)) errors.append("• Grado es obligatorio.\n");
        else if (parsedGrade == null || parsedGrade > 99) errors.append("• Grado debe ser numérico entre 1 y 99.\n");

        if (cbDependency.getValue() == null)     errors.append("• Dependencia es obligatoria.\n");

        if (!errors.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", errors.toString());
            return false;
        }
        return true;
    }

    private void clearFields() {
        txtName.clear();
        txtCode.clear();
        txtGrade.clear();
        cbDependency.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    private void loadData() {
        if (table != null)
            table.setItems(FXCollections.observableArrayList(dao.findAll()));
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