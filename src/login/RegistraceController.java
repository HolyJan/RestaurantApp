/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import connection.DatabaseConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.xml.bind.DatatypeConverter;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class RegistraceController implements Initializable {

    @FXML
    private TextField jmenoTextField;
    @FXML
    private TextField loginTextField;
    @FXML
    private PasswordField hesloTextField;
    @FXML
    private TextField prijmeniTextField;

    DatabaseConnection connection;
    @FXML
    private Button registraceButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void registrovatAction(ActionEvent event) {
        if (!"".equals(jmenoTextField.getText()) && !"".equals(prijmeniTextField.getText())
                && !"".equals(loginTextField.getText()) && !"".equals(hesloTextField.getText())) {
            Statement statement = connection.createBlockedStatement();
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(hesloTextField.getText().getBytes());
                String hashedPassword = DatatypeConverter.printHexBinary(md.digest());
                ResultSet result = statement.executeQuery("SELECT * FROM uzivatele WHERE "
                        + "login=" + "'" + loginTextField.getText() + "'");
                if (!result.next()) {
                    statement.executeQuery("INSERT INTO UZIVATELE (jmeno, prijmeni, login,"
                            + "heslo, id_role) VALUES ('" + jmenoTextField.getText() + "','"
                            + prijmeniTextField.getText() + "','" + loginTextField.getText()
                            + "','" + hashedPassword + "', 1)");
                    showInfoDialog("Registrace proběhla úspěšně.");
                    Stage stage = (Stage) registraceButton.getScene().getWindow();
                    stage.close();
                } else {
                    showInfoDialog("Uživatel s tímto loginem jíž existuje.");
                }
            } catch (SQLException | NoSuchAlgorithmException e) {
                System.out.println(e.getMessage());
            }
        } else {
            showError("Chyba registrace. Vyplňte všechna pole!");
        }
    }

    public void setConnection(DatabaseConnection connection) {
        this.connection = connection;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
