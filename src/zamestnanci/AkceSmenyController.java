/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

import connection.DatabaseConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
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

            ResultSet result3 = statement.executeQuery("SELECT * FROM SMENY_VIEW");
            while (result3.next()) {
                smeny.add(result3.getString("POZICE"));
            }
            zamestnanecCombo.setItems(zamestnanci);
            smenaCombo.setItems(smeny);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    public void setData(int id, String smena, Date datum, int idZamestn, String jmeno, String prijmeni, String telefon, String pozice) {
        smenaCombo.setValue(smena);
//        datum = datePicker.setValue(datum.toLocalDate());
        Zamestnanec zamestan = new Zamestnanec(, jmeno, prijmeni, telefon, id, pozice);

    }
}
