/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aktivity;

import connection.DatabaseConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import oracle.jdbc.OracleTypes;
import uzivatele.AkceUzivateleController;
import uzivatele.Role;
import uzivatele.Uzivatel;
import zakaznici.Zakaznik;
import zamestnanci.Zamestnanec;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AktivityController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Aktivita> tableView;
    @FXML
    private TableColumn<Aktivita, String> uzivatelCol;
    @FXML
    private TableColumn<Aktivita, String> tabulkaCol;
    @FXML
    private TableColumn<Aktivita, String> akceCol;
    @FXML
    private TableColumn<Aktivita, String> datumCol;
    private boolean init;
    private DatabaseConnection connection;
    ObservableList<Aktivita> aktivity = FXCollections.observableArrayList();
    ObservableList<Aktivita> aktivityFiltr = FXCollections.observableArrayList();
    @FXML
    private TextField tfUzivatel;
    @FXML
    private TextField tfTabulka;
    @FXML
    private TextField tfAkce;
    @FXML
    private DatePicker dpDatum;

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
                    loadData();
                    init = true;
                }
            }
        });
        uzivatelCol.setCellValueFactory(new PropertyValueFactory<>("uzivatel"));
        tabulkaCol.setCellValueFactory(new PropertyValueFactory<>("tabulka"));
        akceCol.setCellValueFactory(new PropertyValueFactory<>("akce"));
        datumCol.setCellValueFactory(new PropertyValueFactory<>("datum"));
    }

    private void loadData() {
        aktivity.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM AKTIVITY");
            while (result.next()) {
                if (result.getString("USERNAME") != null) {
                    aktivity.add(new Aktivita(result.getString("USERNAME"), result.getString("TABULKA"), result.getString("AKCE"),
                            result.getDate("DATUM")));
                }
            }
            tableView.getItems().addAll(aktivity);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void PridejAktivitu(Aktivita aktivita) {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("INSERT INTO AKTIVITY (Username, Tabulka, Akce, Datum) VALUES ('"
                    + aktivita.getUzivatel() + "','" + aktivita.getTabulka() + "','" + aktivita.getAkce() + ""
                    + "','" + aktivita.getDatum() + "')");
            result.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    @FXML
    private void filtruj(ActionEvent event) {
        try {
            if (tfAkce.getText().isEmpty()) {
                tfAkce.setText(null);
            }
            if (tfTabulka.getText().isEmpty()) {
                tfTabulka.setText(null);
            }
            if (tfUzivatel.getText().isEmpty()) {
                tfUzivatel.setText(null);
            }

            aktivity.clear();
            tableView.getItems().clear();
            java.util.Date utilDate = null;
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");
            String date = null;
            if (dpDatum.getValue() != null) {
                utilDate = new java.util.Date(Date.valueOf(dpDatum.getValue()).getTime());
                date = DATE_FORMAT.format(utilDate);
            }
            System.out.println(tfAkce.getText());
            System.out.println(tfUzivatel.getText());
            System.out.println(tfTabulka.getText());
            System.out.println(date);
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_AKTIVITY_SEARCH.PRO_RETURN_AKTIVITY(?,?,?,?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("novaAkce", tfAkce.getText());
            cs.setString("novaTabulka", tfTabulka.getText());
            cs.setString("novyUzivatel", tfUzivatel.getText());
            cs.setString("noveDatum", date);
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");
            while (result.next()) {
                aktivity.add(new Aktivita(result.getString("USERNAME"), result.getString("TABULKA"), result.getString("AKCE"),
                        result.getDate("DATUM")));

            }
            tableView.getItems().addAll(aktivity);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        loadData();
        tableView.getItems().addAll(aktivity);
    }

}
