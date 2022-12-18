/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uzivatele;

import connection.DatabaseConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AkceUzivateleController implements Initializable {

    @FXML
    private TextField jmenoTextField;
    @FXML
    private TextField prijmeniTextField;
    @FXML
    private TextField loginTextField;
    @FXML
    private PasswordField hesloTextField;
    @FXML
    private ComboBox<Role> roleCombo;
    private boolean init;
    @FXML
    private AnchorPane pane;
    private int idUzivatele = -1;
    private DatabaseConnection connection;

    ObservableList<Role> role = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        pane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!init) {
                    pripareCombos();
                    init = true;
                }
            }
        });
    }

    void setData(int idUzivatele, String jmeno, String prijmeni, String login,
            String heslo, Role role) {
        this.idUzivatele = idUzivatele;
        jmenoTextField.setText(jmeno);
        prijmeniTextField.setText(prijmeni);
        loginTextField.setText(login);
        hesloTextField.setText(heslo);
        roleCombo.setValue(role);
    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    private void pripareCombos() {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM ROLE");
            while (result1.next()) {
                role.add(new Role(result1.getInt("ID_ROLE"), result1.getString("ROLE")));
            }
            roleCombo.setItems(role);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void PotvrditAction(ActionEvent event) {
        if (!"".equals(jmenoTextField.getText()) && !"".equals(prijmeniTextField.getText())
                && !"".equals(loginTextField.getText()) && !"".equals(hesloTextField.getText())
                && !"".equals(roleCombo.getValue())) {
            Statement statement = connection.createBlockedStatement();
            try {
                if (this.idUzivatele != -1) {

                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updateUzivateleProc(?,?,?,?,?,?)}");
                    cstmt.setInt(1, idUzivatele);
                    cstmt.setString(2, jmenoTextField.getText());
                    cstmt.setString(3, prijmeniTextField.getText());
                    cstmt.setString(4, loginTextField.getText());
                    cstmt.setString(5, hesloTextField.getText());
                    cstmt.setInt(6, roleCombo.getValue().getIdRole());
                    cstmt.execute();

                    System.out.println("aktualizace OK");
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozObjednavkuProc(?,?,?,?)}");
                    cstmt.setString(1, jmenoTextField.getText());
                    cstmt.setString(2, prijmeniTextField.getText());
                    cstmt.setString(3, loginTextField.getText());
                    cstmt.setString(4, hesloTextField.getText());
                    cstmt.setInt(5, roleCombo.getValue().getIdRole());
                    cstmt.execute();

                }
                Stage stage = (Stage) jmenoTextField.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        }
    }


}
