/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objednavky;

import connection.DatabaseConnection;
import java.net.URL;
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

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class AkceObjednavkaController implements Initializable {

    @FXML
    private TextField jmenoText;
    @FXML
    private TextField prijmeniText;
    @FXML
    private ComboBox<String> casObjednaniCombo;
    @FXML
    private ComboBox<String> vyzvednutiCombo;
    @FXML
    private ComboBox<String> polozkaCombo;
    @FXML
    private TextField cenaText;
    private int idObjednavky = -1;
    private int idPolozky;
    private int idDoruceni;
    DatabaseConnection connection;
    boolean init;

    ObservableList<String> zpusobyVyzvednuti = FXCollections.observableArrayList();
    ObservableList<String> polozky = FXCollections.observableArrayList();
    ObservableList<String> casyObjednani = FXCollections.observableArrayList();
    @FXML
    private AnchorPane pane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        cenaText.setDisable(true);
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

    void setData(int idObjednavky, String jmeno, String prijmeni, int idDoruceni,
            String casObjednani, String vyzvednuti, int idPolozky, String nazevPolozky, int cenaPolozky) {
        this.idObjednavky = idObjednavky;
        jmenoText.setText(jmeno);
        prijmeniText.setText(prijmeni);
        this.idDoruceni = idDoruceni;
        casObjednaniCombo.setValue(casObjednani);
        vyzvednutiCombo.setValue(vyzvednuti);
        this.idPolozky = idPolozky;
        polozkaCombo.setValue(nazevPolozky);
        cenaText.setText(Integer.toString(cenaPolozky));
    }

    private void pripareCombos() {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM ZPUSOBY_VYZVEDNUTI");
            while (result1.next()) {
                zpusobyVyzvednuti.add(result1.getString("NAZEV"));
            }

            casyObjednani.add("Předem");
            casyObjednani.add("Na místě");

            ResultSet result3 = statement.executeQuery("SELECT * FROM POLOZKY_MENU");
            while (result3.next()) {
                polozky.add(result3.getString("NAZEV"));
            }
            polozkaCombo.setItems(polozky);
            casObjednaniCombo.setItems(casyObjednani);
            vyzvednutiCombo.setItems(zpusobyVyzvednuti);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
    }

    @FXML
    private void polozkaComboAction(ActionEvent event) {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM POLOZKY_MENU WHERE NAZEV='" + polozkaCombo.getValue() + "'");
            if (result.next()) {
                String cena = result.getString("CENA");
                cenaText.setText(cena);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}