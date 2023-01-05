/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class ZamestnanciSmenyController implements Initializable {

    @FXML
    private TableView<Zamestnanec> tableView;
    @FXML
    private TableColumn<Zamestnanec, String> jmenoCol;
    @FXML
    private TableColumn<Zamestnanec, String> prijmeniCol;
    @FXML
    private TableColumn<Zamestnanec, String> telefonCol;
    @FXML
    private TableColumn<Zamestnanec, String> PoziceCol;
    boolean init;
    @FXML
    private AnchorPane pane;
    DatabaseConnection connection;
    ObservableList<Zamestnanec> zamestnanci = FXCollections.observableArrayList();
    ObservableList<Zamestnanec> zamestnanciVse = FXCollections.observableArrayList();
    private int smenaId;
    @FXML
    private ComboBox<Zamestnanec> comboZamestnanci;

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
        telefonCol.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        PoziceCol.setCellValueFactory(new PropertyValueFactory<>("pozice"));
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    public void setSmenaId(int id) {
        smenaId = id;
    }

    private void loadData() {
        zamestnanci.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM SMENY_VIEW WHERE ID_SMENA =" + smenaId);
            while (result.next()) {
                zamestnanci.add(new Zamestnanec(result.getInt("ID_ZAMESTNANCE"), result.getString("JMENO"),
                        result.getString("PRIJMENI"), result.getString("TELEFON"),
                        result.getInt("ID_POZICE"), result.getString("POZICE"), 0));
            }
            tableView.getItems().addAll(zamestnanci);

        } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }

        zamestnanciVse.clear();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM ZAMESTNANCI_VIEW");
            while (result.next()) {
                zamestnanciVse.add(new Zamestnanec(result.getInt("ID_ZAMESTNANCE"), result.getString("JMENO"),
                        result.getString("PRIJMENI"), result.getString("TELEFON"), result.getInt("ID_POZICE"),
                        result.getString("NAZEV"), 0));
            }
            comboZamestnanci.getItems().addAll(zamestnanciVse);

        } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    @FXML
    private void pridatZamAcction(ActionEvent event) throws SQLException {
        if (comboZamestnanci.getValue() != null) {
            Statement statement = connection.createBlockedStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM SMENY_VIEW WHERE id_zamestnance =" + comboZamestnanci.getValue().getId() + ""
                    + "AND id_smena='" + smenaId + "'");
            if (!result.next()) {
                CallableStatement cstmt1 = connection.getConnection().prepareCall("{call VLOZSMENU_ZAMESTNPROC(?,?)}");
                cstmt1.setInt(1, smenaId);
                cstmt1.setInt(2, comboZamestnanci.getValue().getId());
                cstmt1.execute();

            } else {
                MainSceneController.showError("Na této směně je jíž zameěstnanec zapsán!");
            }
        }
        loadData();
    }

    @FXML
    private void odebratZamAction(ActionEvent event) throws SQLException {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            CallableStatement cstmt1 = connection.getConnection().prepareCall("{call odeberSmenu_ZamestnProc(?,?)}");
            cstmt1.setInt(1, smenaId);
            cstmt1.setInt(2, tableView.getSelectionModel().getSelectedItem().getId());
            cstmt1.execute();
            loadData();

        } else {
            MainSceneController.showDialog("Není vybraný žádný řádek!");
        }

    }

}
