/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aktivity;

import connection.DatabaseConnection;
import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uzivatele.AkceUzivateleController;
import uzivatele.Role;
import uzivatele.Uzivatel;

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

}
