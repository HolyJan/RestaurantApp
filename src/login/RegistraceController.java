/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    @FXML
    private TextField telefonTextField;
    @FXML
    private TextField uliceTextField;
    @FXML
    private TextField cisloPopTextField;
    @FXML
    private TextField pscTextField;
    @FXML
    private TextField obecTextField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        telefonTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    telefonTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        cisloPopTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    cisloPopTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        pscTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    pscTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @FXML
    private void registrovatAction(ActionEvent event) {
        if (!"".equals(jmenoTextField.getText()) && !"".equals(prijmeniTextField.getText())
                && !"".equals(loginTextField.getText()) && !"".equals(hesloTextField.getText()) && !"".equals(telefonTextField.getText())
                && !"".equals(uliceTextField.getText()) && !"".equals(cisloPopTextField.getText()) && !"".equals(pscTextField.getText())
                && !"".equals(obecTextField.getText()) || telefonTextField.getText().length() != 9) {
            Statement statement = connection.createBlockedStatement();
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(hesloTextField.getText().getBytes());
                String hashedPassword = DatatypeConverter.printHexBinary(md.digest());
                ResultSet result = statement.executeQuery("SELECT * FROM uzivatele WHERE "
                        + "login=" + "'" + loginTextField.getText() + "'");
                if (!result.next()) {
                    statement.executeQuery("INSERT INTO UZIVATELE (jmeno, prijmeni, login,"
                            + "heslo, id_role, telefon) VALUES ('" + jmenoTextField.getText() + "','"
                            + prijmeniTextField.getText() + "','" + loginTextField.getText()
                            + "','" + hashedPassword + "', 1, '" + telefonTextField.getText() + "')");
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozAdresuProc(?,?,?,?)}");
                    cstmt.setString(1, uliceTextField.getText());
                    cstmt.setString(2, cisloPopTextField.getText());
                    cstmt.setString(3, pscTextField.getText());
                    cstmt.setString(4, obecTextField.getText());
                    cstmt.execute();
                    result = statement.executeQuery("SELECT adresy_id_adresa_seq.currval as id FROM dual");
                    result.next();
                    int id = result.getInt("id");
                    CallableStatement cstmt1 = connection.getConnection().prepareCall("{call vlozZakaznikaProc(?,?,?,?,?)}");
                    cstmt1.setString(1, jmenoTextField.getText());
                    cstmt1.setString(2, prijmeniTextField.getText());
                    cstmt1.setString(3, telefonTextField.getText());
                    cstmt1.setString(4, null);
                    cstmt1.setInt(5, id);
                    cstmt1.execute();
                    MainSceneController.showDialog("Registrace proběhla úspěšně.");
                    Stage stage = (Stage) registraceButton.getScene().getWindow();
                    stage.close();
                } else {
                    MainSceneController.showDialog("Uživatel s tímto loginem jíž existuje.");
                }
            } catch (SQLException | NoSuchAlgorithmException e) {
                MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }
        } else {
            if (telefonTextField.getText().length() != 9) {
                MainSceneController.showDialog("Telefonní číslo musí mít 9 číslic");
            } else {
                MainSceneController.showError("Chyba registrace. Vyplňte všechna pole!");
            }
        }
    }

    public void setConnection(DatabaseConnection connection) {
        this.connection = connection;
    }

}
