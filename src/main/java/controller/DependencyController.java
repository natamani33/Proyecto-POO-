package controller;

import dao.GenericDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Dependency;
import util.AlertUtil;
import util.ValidationUtil;

public class DependencyController {

    @FXML private TextField txtName;
    @FXML private TextField txtLevel;

    @FXML private TableView<Dependency> table;
    @FXML private TableColumn<Dependency, Long> colId;
    @FXML private TableColumn<Dependency, String> colName;
    @FXML private TableColumn<Dependency, String> colLevel;

    private final GenericDao<Dependency> dao = new GenericDao<>(Dependency.class);

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                txtName.setText(selected.getName());
                txtLevel.setText(selected.getLevel());
            }
        });

        loadData();
    }

    @FXML
    private void save() {
        String name = txtName.getText().trim();
        String level = txtLevel.getText().trim();

        if (ValidationUtil.isBlank(name) || ValidationUtil.isBlank(level)) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Nombre y nivel son obligatorios.");
            return;
        }

        if (!ValidationUtil.isLettersAndSpaces(name)) {
            showAlert(Alert.AlertType.WARNING, "Dato inválido", "El nombre debe contener solo letras y espacios.");
            return;
        }

        if (level.length() < 3 || level.length() > 60) {
            showAlert(Alert.AlertType.WARNING, "Dato inválido", "El nivel debe tener entre 3 y 60 caracteres.");
            return;
        }

        Dependency dependency = new Dependency();
        dependency.setName(name);
        dependency.setLevel(level);

        if (dao.save(dependency)) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Dependencia guardada correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible guardar la dependencia.");
        }
    }

    @FXML
    private void delete() {
        Dependency selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Selecciona una dependencia para eliminar.");
            return;
        }

        if (dao.delete(selected.getId())) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Dependencia eliminada correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible eliminar la dependencia.");
        }
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(dao.findAll()));
    }

    private void clearFields() {
        txtName.clear();
        txtLevel.clear();
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