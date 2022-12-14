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
import java.text.SimpleDateFormat;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import menu.Recept;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AkceSmenyController implements Initializable {

    private ComboBox<Zamestnanec> zamestnanecCombo;
    @FXML
    private AnchorPane pane;
    DatabaseConnection connection;
    boolean init;

    ObservableList<Zamestnanec> zamestnanci = FXCollections.observableArrayList();
    ObservableList<Smena> smeny = FXCollections.observableArrayList();
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> smenaCombo;
    @FXML
    private Button potvrditBut;
    int idZamestnance = -1;
    int oldIdSmena;
    private HBox hbox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        smenaCombo.getItems().add("Ranní");
        smenaCombo.getItems().add("Odpolední");

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
            if (datePicker.getValue() != null && smenaCombo.getValue() != null) {
                Statement statement = connection.createBlockedStatement();
                if (idZamestnance == -1) {
                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");
                    java.util.Date utilDate = new java.util.Date(Date.valueOf(datePicker.getValue()).getTime());
                    ResultSet result = statement.executeQuery("SELECT * FROM SMENY_VIEW WHERE datum='" + DATE_FORMAT.format(utilDate) + "' "
                            + "AND smena='" + smenaCombo.getValue() + "'");
                    if (!result.next()) {
                        CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozSmenuProc(?,?)}");
                        cstmt.setString(1, smenaCombo.getValue());
                        cstmt.setDate(2, Date.valueOf(datePicker.getValue()));
                        cstmt.execute();
                    }else{
                        MainSceneController.showError("Tato směna je jíž v tabulce");
                    }
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updateSmenu_Proc(?,?,?)}");
                    cstmt.setString(1, smenaCombo.getValue());
                    cstmt.setInt(2, oldIdSmena);
                    cstmt.setDate(3, Date.valueOf(datePicker.getValue()));
                    cstmt.execute();
                }
                Stage stage = (Stage) potvrditBut.getScene().getWindow();
                stage.close();
            } else {
                showError("Vyplňte všechna pole!");
                throw new SQLException();
            }

        } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    private void pripareCombos() {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM ZAMESTNANCI_VIEW");
            while (result.next()) {
                Zamestnanec zamestnanec = new Zamestnanec(result.getInt("ID_ZAMESTNANCE"), result.getString("JMENO"),
                        result.getString("PRIJMENI"), result.getString("TELEFON"),
                        result.getInt("ID_ZAMESTNANCE"), result.getString("NAZEV"), result.getInt("ID_NADRIZENEHO"));

                zamestnanci.add(zamestnanec);
            }
            if (idZamestnance != -1) {
                result = statement.executeQuery("SELECT DISTINCT id_smena, smena, datum FROM smeny_view");
                while (result.next()) {
                    Smena smena = new Smena(result.getInt("ID_SMENA"), result.getString("SMENA"), result.getDate("DATUM"), 0,
                            "", "", "", 0, "");
                    smeny.add(smena);
                }
            } else {
            }

        } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    public void setData(Smena smena, int idZamestn, String jmeno, String prijmeni, String telefon, int idPoz, String pozice, int idNadrizeneho) {
        idZamestnance = idZamestn;
        smenaCombo.setValue(smena.getSmena());
        datePicker.setValue(smena.getDatum().toLocalDate());
        Zamestnanec zamestan = new Zamestnanec(idZamestn, jmeno, prijmeni, telefon, idPoz, pozice, idNadrizeneho);
        //zamestnanecCombo.setValue(zamestan);
        oldIdSmena = smena.getId();

    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
