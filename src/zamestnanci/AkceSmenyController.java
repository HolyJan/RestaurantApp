/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

import connection.DatabaseConnection;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import menu.Recept;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AkceSmenyController implements Initializable {

    @FXML
    private ComboBox<Zamestnanec> zamestnanecCombo;
    @FXML
    private AnchorPane pane;
    DatabaseConnection connection;
    boolean init;

    ObservableList<Zamestnanec> zamestnanci = FXCollections.observableArrayList();
    ObservableList<String> smeny = FXCollections.observableArrayList();
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> smenaCombo;
    @FXML
    private Button potvrditBut;
    int idZamestnance = -1;

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

    @FXML
    private void potvrditAction(ActionEvent event) {

        try {
            if (datePicker.getValue() != null && smenaCombo.getValue() != null && zamestnanecCombo.getValue() != null) {
                Statement statement = connection.createBlockedStatement();
                if (idZamestnance == -1) {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozSmenuProc(?,?)}");
                    if ("Ranní".equals(smenaCombo.getValue())) {
                        cstmt.setInt(1, 1);
                    } else {
                        cstmt.setInt(1, 2);
                    }
                    cstmt.setInt(2, zamestnanecCombo.getValue().getId());
                    cstmt.execute();
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updateSmenuProc(?,?)}");
                    if ("Ranní".equals(smenaCombo.getValue())) {
                        cstmt.setInt(1, 1);
                    } else {
                        cstmt.setInt(1, 2);
                    }
                    cstmt.setInt(2, zamestnanecCombo.getValue().getId());
                    cstmt.execute();
                }
                Stage stage = (Stage) potvrditBut.getScene().getWindow();
                stage.close();
            } else {
                showError("Vyplňte všechna pole!");
                throw new SQLException();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void pripareCombos() {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM ZAMESTNANCI_VIEW");
            while (result1.next()) {
                Zamestnanec zamestnanec = new Zamestnanec(result1.getInt("ID_ZAMESTNANCE"), result1.getString("JMENO"),
                        result1.getString("PRIJMENI"), result1.getString("TELEFON"),
                        result1.getInt("ID_ZAMESTNANCE"), result1.getString("NAZEV"));

                zamestnanci.add(zamestnanec);
            }

            smeny.add("Ranní");
            smeny.add("Odpolední");
            zamestnanecCombo.setItems(zamestnanci);
            smenaCombo.setItems(smeny);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    public void setData(String smena, Date datum, int idZamestn, String jmeno, String prijmeni, String telefon, int idPoz, String pozice) {
        idZamestnance = idZamestn;
        smenaCombo.setValue(smena);
        datePicker.setValue(datum.toLocalDate());
        Zamestnanec zamestan = new Zamestnanec(idZamestn, jmeno, prijmeni, telefon, idPoz, pozice);
        zamestnanecCombo.setValue(zamestan);

    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
