package controller;

import dao.GenericDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.PublicServer;
import util.AlertUtil;
import util.ValidationUtil;

public class PublicServerController {

    @FXML private TextField txtIdNumber;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtPersonalEmail;
    @FXML private TextField txtInstitutionalEmail;
    @FXML private TextField txtPhone;

    @FXML private DatePicker dpBirthDate;
    @FXML private ComboBox<String> cbGender;
    @FXML private ComboBox<String> cbMaritalStatus;
    @FXML private ComboBox<String> cbBloodType;
    @FXML private ComboBox<String> cbEmploymentType;

    @FXML private TableView<PublicServer>            table;
    @FXML private TableColumn<PublicServer, Long>    colId;
    @FXML private TableColumn<PublicServer, String>  colIdNumber;
    @FXML private TableColumn<PublicServer, String>  colFirstName;
    @FXML private TableColumn<PublicServer, String>  colLastName;
    @FXML private TableColumn<PublicServer, String>  colPhone;

    private final GenericDao<PublicServer> dao = new GenericDao<>(PublicServer.class);

    @FXML
    private void initialize() {
        cbGender.setItems(FXCollections.observableArrayList(
                "Masculino", "Femenino", "No binario", "Prefiero no decir"));

        cbMaritalStatus.setItems(FXCollections.observableArrayList(
                "Soltero/a", "Casado/a", "Unión libre", "Divorciado/a", "Viudo/a"));

        cbBloodType.setItems(FXCollections.observableArrayList(
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));

        cbEmploymentType.setItems(FXCollections.observableArrayList(
                "Planta", "Contrato", "Provisional", "Libre nombramiento"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdNumber.setCellValueFactory(new PropertyValueFactory<>("idNumber"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, s) -> {
            if (s != null) fillForm(s);
        });

        loadData();
    }

    @FXML
    private void save() {
        if (!validateFields()) return;

        String idNumber = txtIdNumber.getText().trim();
        boolean alreadyExists = dao.findAll().stream()
                .anyMatch(server -> idNumber.equals(server.getIdNumber()));
        if (alreadyExists) {
            showAlert(Alert.AlertType.WARNING, "Dato duplicado", "La cédula ya está registrada en el sistema.");
            return;
        }

        PublicServer s = new PublicServer();
        s.setIdNumber(idNumber);
        s.setFirstName(txtFirstName.getText().trim());
        s.setLastName(txtLastName.getText().trim());
        s.setBirthDate(dpBirthDate.getValue());
        s.setGender(cbGender.getValue());
        s.setMaritalStatus(cbMaritalStatus.getValue());
        s.setBloodType(cbBloodType.getValue());
        s.setPersonalEmail(txtPersonalEmail.getText().trim());
        s.setInstitutionalEmail(txtInstitutionalEmail.getText().trim());
        s.setPhone(txtPhone.getText().trim());
        s.setEmploymentType(cbEmploymentType.getValue());

        if (dao.save(s)) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Servidor público guardado correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible guardar el servidor público.");
        }
    }

    @FXML
    private void delete() {
        PublicServer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia",
                    "Selecciona un registro de la tabla para eliminar.");
            return;
        }

        if (AlertUtil.confirm("Confirmar eliminación",
                "¿Eliminar a " + selected.getFirstName() + " " + selected.getLastName() + "?")) {
            if (dao.delete(selected.getId())) {
                loadData();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Registro eliminado correctamente.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No fue posible eliminar el registro.");
            }
        }
    }

    private void fillForm(PublicServer s) {
        txtIdNumber.setText(s.getIdNumber());
        txtFirstName.setText(s.getFirstName());
        txtLastName.setText(s.getLastName());
        dpBirthDate.setValue(s.getBirthDate());
        cbGender.setValue(s.getGender());
        cbMaritalStatus.setValue(s.getMaritalStatus());
        cbBloodType.setValue(s.getBloodType());
        txtPersonalEmail.setText(s.getPersonalEmail());
        txtInstitutionalEmail.setText(s.getInstitutionalEmail());
        txtPhone.setText(s.getPhone());
        cbEmploymentType.setValue(s.getEmploymentType());
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        String idNumber = txtIdNumber.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String personalEmail = txtPersonalEmail.getText().trim();
        String institutionalEmail = txtInstitutionalEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (ValidationUtil.isBlank(idNumber))
            errors.append("• Cédula es obligatoria.\n");
        else if (!ValidationUtil.isNumericInRange(idNumber, 6, 12))
            errors.append("• Cédula debe ser numérica y tener entre 6 y 12 dígitos.\n");

        if (ValidationUtil.isBlank(firstName))
            errors.append("• Nombres es obligatorio.\n");
        else if (!ValidationUtil.isLettersAndSpaces(firstName))
            errors.append("• Nombres solo debe contener letras y espacios.\n");

        if (ValidationUtil.isBlank(lastName))
            errors.append("• Apellidos es obligatorio.\n");
        else if (!ValidationUtil.isLettersAndSpaces(lastName))
            errors.append("• Apellidos solo debe contener letras y espacios.\n");

        if (dpBirthDate.getValue() == null)
            errors.append("• Fecha de nacimiento es obligatoria.\n");
        else if (!ValidationUtil.isPastOrToday(dpBirthDate.getValue()))
            errors.append("• Fecha de nacimiento no puede ser futura.\n");
        else if (!ValidationUtil.isAdult(dpBirthDate.getValue()))
            errors.append("• El servidor debe ser mayor de edad.\n");

        if (cbGender.getValue() == null)
            errors.append("• Género es obligatorio.\n");
        if (cbMaritalStatus.getValue() == null)
            errors.append("• Estado civil es obligatorio.\n");
        if (cbBloodType.getValue() == null)
            errors.append("• Tipo de sangre es obligatorio.\n");

        if (ValidationUtil.isBlank(personalEmail))
            errors.append("• Correo personal es obligatorio.\n");
        else if (!ValidationUtil.isValidEmail(personalEmail))
            errors.append("• Correo personal no tiene un formato válido.\n");

        if (ValidationUtil.isBlank(institutionalEmail))
            errors.append("• Correo institucional es obligatorio.\n");
        else if (!ValidationUtil.isValidEmail(institutionalEmail))
            errors.append("• Correo institucional no tiene un formato válido.\n");

        if (ValidationUtil.isBlank(phone))
            errors.append("• Teléfono es obligatorio.\n");
        else if (!ValidationUtil.isPhoneValid(phone))
            errors.append("• Teléfono debe ser numérico y tener entre 7 y 15 dígitos.\n");

        if (cbEmploymentType.getValue() == null)
            errors.append("• Tipo de empleo es obligatorio.\n");

        if (!errors.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", errors.toString());
            return false;
        }
        return true;
    }

    private void clearFields() {
        txtIdNumber.clear();
        txtFirstName.clear();
        txtLastName.clear();
        dpBirthDate.setValue(null);
        cbGender.setValue(null);
        cbMaritalStatus.setValue(null);
        cbBloodType.setValue(null);
        txtPersonalEmail.clear();
        txtInstitutionalEmail.clear();
        txtPhone.clear();
        cbEmploymentType.setValue(null);
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