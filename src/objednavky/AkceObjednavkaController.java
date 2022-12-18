/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objednavky;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import zakaznici.Adresa;
import zakaznici.Zakaznik;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class AkceObjednavkaController implements Initializable {

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
    ObservableList<Zakaznik> zakaznici = FXCollections.observableArrayList();
    ObservableList<String> polozky = FXCollections.observableArrayList();
    ObservableList<String> casyObjednani = FXCollections.observableArrayList();
    @FXML
    private AnchorPane pane;
    @FXML
    private ComboBox<Zakaznik> zakaznikCombo;

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

    void setData(int idObjednavky, Zakaznik zakaznik, int idDoruceni,
            String casObjednani, String vyzvednuti, int idPolozky, String nazevPolozky, int cenaPolozky) {
        this.idObjednavky = idObjednavky;
        zakaznikCombo.setValue(zakaznik);
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
            
            ResultSet result2 = statement.executeQuery("SELECT * FROM ZAKAZNICI_VIEW");
            while (result2.next()) {
                zakaznici.add(new Zakaznik(result2.getInt("ID_ZAKAZNIKA"), result2.getString("JMENO"), 
                        result2.getString("PRIJMENI"), result2.getString("TELEFON"), result2.getString("EMAIL"), 
                        new Adresa(result2.getInt("ID_ADRESA"), result2.getString("ULICE"), 
                                result2.getString("CISLO_POPISNE"), result2.getString("PSC"), result2.getString("OBEC"))));
            }
            polozkaCombo.setItems(polozky);
            zakaznikCombo.setItems(zakaznici);
            casObjednaniCombo.setItems(casyObjednani);
            vyzvednutiCombo.setItems(zpusobyVyzvednuti);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        if (!"".equals(zakaznikCombo.getValue())
                && !"".equals(casObjednaniCombo.getValue()) && !"".equals(vyzvednutiCombo.getValue())
                && !"".equals(polozkaCombo.getValue())) {
            Statement statement = connection.createBlockedStatement();
            try {
                ResultSet result = statement.executeQuery("SELECT * FROM zakaznici_view"
                        + " WHERE jmeno='" + zakaznikCombo.getValue().getJmeno() + "' AND prijmeni='" + zakaznikCombo.getValue().getPrijmeni() + "'");
                result.next();
                int idZakaznika = result.getInt("ID_ZAKAZNIKA");
                ResultSet result1 = statement.executeQuery("SELECT * FROM zpusoby_vyzvednuti_view"
                        + " WHERE nazev='" + vyzvednutiCombo.getValue() + "'");
                result1.next();
                int idDor = result1.getInt("ID_DORUCENI");

                ResultSet result2 = statement.executeQuery("SELECT * FROM polozky_menu_view"
                        + " WHERE nazev_polozky='" + polozkaCombo.getValue() + "'");
                result2.next();
                int idPol = result2.getInt("ID_POLOZKY");

                if (idObjednavky != -1) {

                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updateObjednavkuProc(?,?,?,?,?)}");
                    cstmt.setInt(1, idObjednavky);
                    cstmt.setInt(2, idZakaznika);
                    cstmt.setString(3, casObjednaniCombo.getValue());
                    cstmt.setInt(4, idDor);
                    cstmt.setInt(5, 3);
                    cstmt.execute();

                    CallableStatement cstmt1 = connection.getConnection().prepareCall("{call updateObjednavky_polozkyProc(?,?)}");
                    cstmt1.setInt(1, idObjednavky);
                    cstmt1.setInt(2, idPol);
                    cstmt1.execute();

                    System.out.println("aktualizace OK");
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozObjednavkuProc(?,?,?,?)}");
                    cstmt.setInt(1, idZakaznika);
                    cstmt.setString(2, casObjednaniCombo.getValue());
                    cstmt.setInt(3, idDor);
                    cstmt.setInt(4, 3);
                    cstmt.execute();

                    ResultSet result3 = statement.executeQuery("SELECT * FROM objednavky WHERE id_objednavky = (SELECT MAX(id_objednavky) FROM objednavky)");
                    result3.next();
                    int idObjedn = result3.getInt("ID_OBJEDNAVKY");

                    CallableStatement cstmt1 = connection.getConnection().prepareCall("{call vlozObjednavky_polozkyProc(?,?)}");
                    cstmt1.setInt(1, idObjedn);
                    cstmt1.setInt(2, idPol);
                    cstmt1.execute();
                }
                Stage stage = (Stage) zakaznikCombo.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
