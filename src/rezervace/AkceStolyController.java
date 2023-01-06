/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rezervace;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uzivatele.Role;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AkceStolyController implements Initializable {

    @FXML
    private AnchorPane pane;
    private int idStolu = -1;
    @FXML
    private TextField cisloStoluTextField;
    @FXML
    private TextField pocetMistTextField;
    DatabaseConnection connection;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cisloStoluTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    cisloStoluTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        pocetMistTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    pocetMistTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    void setData(int idStolu, int cisloStolu, int pocetMist) {
        this.idStolu = idStolu;
        this.cisloStoluTextField.setText(Integer.toString(cisloStolu));
        this.pocetMistTextField.setText(Integer.toString(pocetMist));
    }

    @FXML
    private void PotvrditAction(ActionEvent event) throws SQLException {
        if (!"".equals(cisloStoluTextField.getText()) && !"".equals(pocetMistTextField.getText())) {
            Statement statement = connection.createBlockedStatement();
            try {
                ResultSet result = statement.executeQuery("SELECT * FROM stoly_view where cislo_stolu = " + Integer.parseInt(cisloStoluTextField.getText()));
                if (!result.next()) {

                    if (this.idStolu != -1) {
                        CallableStatement cstmt = connection.getConnection().prepareCall("{call updateStulProc(?,?,?)}");
                        cstmt.setInt(1, idStolu);
                        cstmt.setInt(2, Integer.parseInt(pocetMistTextField.getText()));
                        cstmt.setInt(3, Integer.parseInt(cisloStoluTextField.getText()));
                        cstmt.execute();

                    } else {
                        CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozStulProc(?,?)}");
                        cstmt.setInt(1, Integer.parseInt(pocetMistTextField.getText()));
                        cstmt.setInt(2, Integer.parseInt(cisloStoluTextField.getText()));
                        cstmt.execute();

                    }
                    Stage stage = (Stage) cisloStoluTextField.getScene().getWindow();
                    stage.close();
                }else{
                    MainSceneController.showError("Toto číslo stolu je jíž v tabulce!");
                }
            } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }
        }

    }

}
