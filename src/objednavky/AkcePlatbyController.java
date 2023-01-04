/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objednavky;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import zakaznici.Adresa;
import zakaznici.Zakaznik;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AkcePlatbyController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private DatePicker datumDatePicker;
    @FXML
    private ComboBox<String> typPlatbyCombo;
    @FXML
    private TextField cisloKartyText;
    @FXML
    private ComboBox<Objednavka> objednavkaCombo;
    @FXML
    private TextField castkaText;
    private boolean init;
    private DatabaseConnection connection;
    private int idPlatby = -1;

    ObservableList<String> typyPlatby = FXCollections.observableArrayList();
    ObservableList<Objednavka> objednavkyVse = FXCollections.observableArrayList();
    ObservableList<Objednavka> objednavkyBezPlatby = FXCollections.observableArrayList();
    ObservableList<Platba> platby = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        castkaText.setDisable(true);
        cisloKartyText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    cisloKartyText.setText(newValue.replaceAll("[^\\d]", ""));
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

    void setData(Platba platba) {
        idPlatby = platba.getIdPlatby();
        castkaText.setText(Integer.toString(platba.getCastka()));
        datumDatePicker.setValue(platba.getDatum().toLocalDate());
        typPlatbyCombo.setValue(platba.getTypPlatby());
        cisloKartyText.setText(platba.getCisloKarty());
        objednavkaCombo.setValue(platba.getObjednavka());

    }

    private void pripareCombos() {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM Zakaznici_view");
            List<Zakaznik> zakaznici = new ArrayList<>();
            while (result1.next()) {
                zakaznici.add(new Zakaznik(result1.getInt("ID_ZAKAZNIKA"), result1.getString("JMENO"),
                        result1.getString("PRIJMENI"), result1.getString("TELEFON"), result1.getString("EMAIL"),
                        new Adresa(result1.getInt("ID_ADRESA"), result1.getString("ULICE"),
                                result1.getString("CISLO_POPISNE"), result1.getString("PSC"), result1.getString("OBEC"))));
            }

            ResultSet result = statement.executeQuery("SELECT * FROM objednavky_view");
            while (result.next()) {
                Zakaznik zakazik = null;
                for (Zakaznik zakaznik : zakaznici) {
                    if (zakaznik.getId() == result.getInt("ID_ZAKAZNIKA")) {
                        zakazik = zakaznik;
                    }
                }
                objednavkyVse.add(new Objednavka(result.getInt("ID_OBJEDNAVKY"), zakazik,
                        result.getInt("ID_DORUCENI"), result.getString("CAS_OBJEDNANI"),
                        result.getString("DORUCENI"), result.getInt("ID_POLOZKY"),
                        result.getString("NAZEV_POLOZKY"), result.getInt("CENA")));

            }

            ResultSet result2 = statement.executeQuery("SELECT * FROM platby_view");
            while (result2.next()) {
                for (Objednavka objednavka : objednavkyVse) {
                    if (result2.getInt("ID_OBJEDNAVKY") == objednavka.getIdObjednavky()) {
                        platby.add(new Platba(result2.getInt("ID_PLATBY"), result2.getInt("CASTKA"),
                                result2.getDate("DATUM"), result2.getString("TYP_PLATBY"), result2.getString("CISLO_KARTY"), objednavka));
                    }
                }
            }
            boolean nalezeno;
            for (Objednavka objednavka : objednavkyVse) {
                nalezeno = false;
                for (Platba platba : platby) {
                    if (objednavka.getIdObjednavky() == platba.getObjednavka().getIdObjednavky()) {
                        nalezeno = true;
                    }
                }
                if (!nalezeno) {
                    objednavkyBezPlatby.add(objednavka);
                }
            }

            typyPlatby.add("Hotově");
            typyPlatby.add("Kartou");

            typPlatbyCombo.setItems(typyPlatby);
            objednavkaCombo.setItems(objednavkyBezPlatby);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        if (objednavkaCombo.getValue() != null
                && (typPlatbyCombo.getValue()) != null && !"".equals(datumDatePicker.getValue())) {
            Statement statement = connection.createBlockedStatement();
            try {
                if ("Kartou".equals(typPlatbyCombo.getValue())) {
                    if (cisloKartyText.getText() == null || "".equals(cisloKartyText.getText())) {
                        throw new Exception("Cislo karty is empty!");
                    }
                }

                if (idPlatby != -1) {

                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updatePlatbuProc(?,?,?,?,?,?)}");
                    cstmt.setInt(1, objednavkaCombo.getValue().getIdObjednavky());
                    cstmt.setInt(2, idPlatby);
                    cstmt.setInt(3, Integer.parseInt(castkaText.getText()));
                    cstmt.setDate(4, Date.valueOf(datumDatePicker.getValue()));
                    cstmt.setString(5, typPlatbyCombo.getValue());
                    cstmt.setString(6, cisloKartyText.getText());
                    cstmt.execute();
                    MainSceneController msc = new MainSceneController();
                    msc.aktivita(connection, MainSceneController.userName.get(), "PLATBY", "UPDATE", new Date(System.currentTimeMillis()));

                    System.out.println("aktualizace OK");
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozPlatbuProc(?,?,?,?,?)}");
                    cstmt.setInt(1, objednavkaCombo.getValue().getIdObjednavky());
                    cstmt.setString(2, castkaText.getText());
                    cstmt.setDate(3, Date.valueOf(datumDatePicker.getValue()));
                    cstmt.setString(4, typPlatbyCombo.getValue());
                    cstmt.setString(5, cisloKartyText.getText());
                    cstmt.execute();
                    MainSceneController msc = new MainSceneController();
                    msc.aktivita(connection, MainSceneController.userName.get(), "PLATBY", "INSERT", new Date(System.currentTimeMillis()));

                }
                Stage stage = (Stage) objednavkaCombo.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    @FXML
    private void typPlatbyComboAction(ActionEvent event) {
        if ("Kartou".equals(typPlatbyCombo.getValue())) {
            cisloKartyText.setDisable(false);
            cisloKartyText.setEditable(true);
        } else {
            cisloKartyText.setDisable(true);
            cisloKartyText.setText("");
        }
    }

    @FXML
    private void ObjednavkaComboAction(ActionEvent event) {
        castkaText.setText(Integer.toString(objednavkaCombo.getValue().getCenaPolozky()));
    }

}
