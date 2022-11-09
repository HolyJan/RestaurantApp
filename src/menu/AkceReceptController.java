/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import connection.DatabaseConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class AkceReceptController implements Initializable {

    @FXML
    private TextField nazevText;
    DatabaseConnection connection;
    @FXML
    private Button potvrditBut;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        try {
            if (!"".equals(nazevText.getText())) {
                Statement statement = connection.createBlockedStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM recepty_view WHERE nazev='" + nazevText.getText() + "'");
                if (!result.next()) {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozReceptProc(?)}");
                    cstmt.setString(1, nazevText.getText());                 
                    cstmt.execute();
                    Stage stage = (Stage) potvrditBut.getScene().getWindow();
                    stage.close();
                } else {
                    showError("Tento název má jíž jiný recept. Zvolte jiný!");
                    throw new SQLException();
                }
            } else {
                showError("Vyplňte název!");
                throw new SQLException();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
