/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uzivatele;

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
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import zamestnanci.Smena;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class UzivateleController implements Initializable {

    @FXML
    private TableView<Uzivatel> tableView;
    @FXML
    private TableColumn<Uzivatel, String> jmenoCol;
    @FXML
    private TableColumn<Uzivatel, String> prijmeniCol;
    @FXML
    private TableColumn<Uzivatel, String> loginCol;
    @FXML
    private TableColumn<Uzivatel, String> hesloCol;
    @FXML
    private TableColumn<Uzivatel, String> roleCol;
    private boolean init;
    DatabaseConnection connection;
    @FXML
    private AnchorPane pane;
    ObservableList<Uzivatel> uzivatele = FXCollections.observableArrayList();

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
        jmenoCol.setCellValueFactory(new PropertyValueFactory<>("jmeno"));
        prijmeniCol.setCellValueFactory(new PropertyValueFactory<>("prijmeni"));
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        hesloCol.setCellValueFactory(new PropertyValueFactory<>("heslo"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
    }
    
    private void loadData() {
        uzivatele.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM UZIVATELE_VIEW");
            while (result.next()) {
                if(result.getString("JMENO") != null){
                    uzivatele.add(new Uzivatel(result.getInt("ID_UZIVATELE"), result.getString("JMENO"), result.getString("PRIJMENI"),result.getString("LOGIN"),
                        result.getString("HESLO"),result.getString("ROLE")));
                }
            }
            tableView.getItems().addAll(uzivatele);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

    @FXML
    private void pridatAction(ActionEvent event) {
    }

    @FXML
    private void upravitAction(ActionEvent event) {
    }

    @FXML
    private void odebratAction(ActionEvent event) {
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }
}
