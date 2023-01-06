/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AkceZamestnanecController implements Initializable {

    @FXML
    private TextField jmenoText;
    @FXML
    private TextField prijmeniText;
    @FXML
    private TextField telefonText;
    @FXML
    private ComboBox<String> poziceCombo;
    DatabaseConnection connection;
    int idZamestnance = -1;
    int idPozice;
    @FXML
    private AnchorPane pane;
    boolean init = false;

    ObservableList<String> pracPozice = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        telefonText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    telefonText.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
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

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    public void setData(int id, String jmeno, String prijmeni, String telefon, int idPozice, String pozice) {
        idZamestnance = id;
        jmenoText.setText(jmeno);
        prijmeniText.setText(prijmeni);
        telefonText.setText(telefon);
        poziceCombo.setValue(pozice);
        this.idPozice = idPozice;
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        if (!"".equals(jmenoText.getText()) && !"".equals(prijmeniText.getText())
                && !"".equals(telefonText.getText()) && !"".equals(poziceCombo.getValue()) || telefonText.getText().length() != 9) {
            Statement statement = connection.createBlockedStatement();
            try {
                ResultSet result = statement.executeQuery("SELECT * FROM pozice_view"
                        + " WHERE nazev='" + poziceCombo.getValue() + "'");
                result.next();
                int idPoz = result.getInt("ID_POZICE");

                if (idZamestnance != -1) {

                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updateZamestnanceProc(?,?,?,?,?)}");
                    cstmt.setInt(1, idZamestnance);
                    cstmt.setString(2, jmenoText.getText());
                    cstmt.setString(3, prijmeniText.getText());
                    cstmt.setString(4, telefonText.getText());
                    cstmt.setInt(5, idPoz);
                    cstmt.execute();
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozZamestnanceProc(?,?,?,?)}");
                    cstmt.setString(1, jmenoText.getText());
                    cstmt.setString(2, prijmeniText.getText());
                    cstmt.setString(3, telefonText.getText());
                    cstmt.setInt(4, idPoz);
                    cstmt.execute();
                }
                Stage stage = (Stage) jmenoText.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }

        }else{
            if(telefonText.getText().length() != 9){
                MainSceneController.showDialog("Telefonní číslo musí mít 9 číslic");
            }else{
                MainSceneController.showError("Vyplňte všechna pole!");
            }
        }
    }

    private void pripareCombos() {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM pozice_view");
            while (result1.next()) {
                pracPozice.add(result1.getString("NAZEV"));
            }
            poziceCombo.setItems(pracPozice);

        } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

}
