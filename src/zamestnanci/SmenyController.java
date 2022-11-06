/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

import connection.DatabaseConnection;
import java.net.URL;
import java.sql.ResultSet;
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
 * @author Notebook
 */
public class SmenyController implements Initializable {

    @FXML
    private TableView<Smena> tableView;
    @FXML
    private TableColumn<Smena, String> smenaCol;
    @FXML
    private TableColumn<Smena, String> datumCol;
    @FXML
    private TableColumn<Smena, String> jmenoCol;
    @FXML
    private TableColumn<Smena, String> prijmeniCol;

    DatabaseConnection connection;
    ObservableList<Smena> smeny = FXCollections.observableArrayList();
    @FXML
    private AnchorPane pane;
    boolean init;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        jmenoCol.setCellValueFactory(new PropertyValueFactory<>("jmeno"));
        prijmeniCol.setCellValueFactory(new PropertyValueFactory<>("prijmeni"));
        smenaCol.setCellValueFactory(new PropertyValueFactory<>("smena"));
        datumCol.setCellValueFactory(new PropertyValueFactory<>("datum"));
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
    }
    
    public void setConnection(DatabaseConnection con) {
        connection = con;
    }
    
    private void loadData() {
        smeny.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM SMENY_VIEW");
            while (result.next()) {
                smeny.add(new Smena(result.getString("NAZEV"), result.getDate("DATUM"),
                        result.getString("JMENO"), result.getString("PRIJMENI")));

            }
            tableView.getItems().addAll(smeny);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }
    /*
    CREATE or REPLACE view smeny_view AS
    SELECT s.id_smeny, s.nazev, s.datum, z.jmeno, z.prijmeni
    FROM smeny s LEFT JOIN smeny_zamestn sz ON id_smeny = z.id_smeny
    LEFT JOIN zakaznik z ON id_zakaznika = sz.id_zakaznika;
    
    */
    
    
    

}
