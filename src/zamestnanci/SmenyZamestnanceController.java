/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
public class SmenyZamestnanceController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Smena> tableView;
    @FXML
    private TableColumn<Smena, Smena> smenaCol;
    @FXML
    private TableColumn<Smena, Smena> datumCol;
    DatabaseConnection connection;
    boolean init;
    ObservableList<Smena> smeny = FXCollections.observableArrayList();
    int idZamestnance;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

    public void setIdZamestnance(int id) {
        idZamestnance = id;
    }

    private void loadData() {
        smeny.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        ObservableList<Integer> idSmen = FXCollections.observableArrayList();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM SMENY_VIEW where ID_ZAMESTNANCE = " + idZamestnance);
            while (result.next()) {
                smeny.add(new Smena(result.getInt("ID_SMENA"), result.getString("SMENA"),
                        result.getDate("DATUM"), 0, null, null, null, 0, null));
            }
            tableView.getItems().addAll(smeny);

        } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

}
