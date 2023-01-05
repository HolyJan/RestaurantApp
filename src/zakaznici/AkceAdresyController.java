/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zakaznici;

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

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AkceAdresyController implements Initializable {

    @FXML
    private AnchorPane pane;
    DatabaseConnection connection;
    private int idAdresy = -1;
    @FXML
    private TextField uliceText;
    @FXML
    private TextField cisloPopText;
    @FXML
    private TextField pscText;
    @FXML
    private TextField mestoText;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cisloPopText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    cisloPopText.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        pscText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    pscText.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        if (!"".equals(uliceText.getText()) && !"".equals(uliceText.getText()) && !"".equals(mestoText.getText()) && !"".equals(cisloPopText.getText())) {
            Statement statement = connection.createBlockedStatement();

            try {
                if (idAdresy != -1) {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updateAdresuProc(?,?,?,?,?)}");
                    cstmt.setInt(1, idAdresy);
                    cstmt.setString(2, uliceText.getText());
                    cstmt.setString(3, cisloPopText.getText());
                    cstmt.setString(4, pscText.getText());
                    cstmt.setString(5, mestoText.getText());
                    cstmt.execute();
                    System.out.println("aktualizace OK");
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozAdresuProc(?,?,?,?)}");
                    cstmt.setString(1, uliceText.getText());
                    cstmt.setString(2, cisloPopText.getText());
                    cstmt.setString(3, pscText.getText());
                    cstmt.setString(4, mestoText.getText());
                    cstmt.execute();
                    System.out.println("vložení OK");
                }
                Stage stage = (Stage) uliceText.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }
        }
    }

    void setConnection(DatabaseConnection connection) {
        this.connection = connection;
    }

    void setData(Adresa adresa) {
        idAdresy = adresa.getIdAdresy();
        uliceText.setText(adresa.getUlice());
        cisloPopText.setText(adresa.getCisloPop());
        pscText.setText(adresa.getPsc());
        mestoText.setText(adresa.getMesto());
    }

}
