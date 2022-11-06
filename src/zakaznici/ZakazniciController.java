/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zakaznici;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class ZakazniciController implements Initializable {

    @FXML
    private TableColumn<Zakaznik, String> jmenoCol;
    @FXML
    private TableColumn<Zakaznik, String> prijmeniCol;
    @FXML
    private TableColumn<Zakaznik, String> telefonCol;
    @FXML
    private TableColumn<Zakaznik, String> emailCol;

    DatabaseConnection connection;
    ObservableList<Zakaznik> zakaznici = FXCollections.observableArrayList();
    ObservableList<Adresa> adresy = FXCollections.observableArrayList();
    @FXML
    private TableView<Zakaznik> tableView;
    //private TableView<Adresa> adresyTable;
    //private TableColumn<Adresa, String> uliceCol;
    //private TableColumn<Adresa, String> cpCol;
    //private TableColumn<Adresa, String> pscCol;
    //private TableColumn<Adresa, String> mestoCol;
    @FXML
    private TableColumn<Adresa, String> uliceCol;
    @FXML
    private TableColumn<Adresa, String> cpCol;
    @FXML
    private TableColumn<Adresa, String> pscCol;
    @FXML
    private TableColumn<Adresa, String> mestoCol;
    @FXML
    private AnchorPane pane;
    boolean init;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        pane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(!init) {
                    loadData();
                    init = true;
                }
            }
        });
        jmenoCol.setCellValueFactory(new PropertyValueFactory<>("jmeno"));
        prijmeniCol.setCellValueFactory(new PropertyValueFactory<>("prijmeni"));
        telefonCol.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        uliceCol.setCellValueFactory(new PropertyValueFactory<>("ulice"));
        cpCol.setCellValueFactory(new PropertyValueFactory<>("cisloPop"));
        pscCol.setCellValueFactory(new PropertyValueFactory<>("psc"));
        mestoCol.setCellValueFactory(new PropertyValueFactory<>("mesto"));
        

    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    private void loadData() {
        zakaznici.clear();
        tableView.getItems().clear();
        adresy.clear();
        //adresyTable.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM ZAKAZNICI_VIEW");
            while (result.next()) {
                Adresa adresa = new Adresa(result.getString("ULICE"), result.getString("CISLO_POPISNE"),
                        result.getString("PSC"), result.getString("OBEC"));
                zakaznici.add(new Zakaznik(result.getInt("ID_ZAKAZNIKA"), result.getString("JMENO"),
                        result.getString("PRIJMENI"), result.getString("TELEFON"), result.getString("EMAIL"),
                        result.getString("ULICE"), result.getString("CISLO_POPISNE"),
                        result.getString("PSC"), result.getString("OBEC")));
                adresy.add(adresa);

            }
            tableView.getItems().addAll(zakaznici);
            //adresyTable.getItems().addAll(adresy);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

}
