/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.io.IOException;
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
import objednavky.AkceObjednavkaController;
import objednavky.Objednavka;

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
    @FXML
    private TableColumn<Smena, String> telefonCol;
    @FXML
    private TableColumn<Smena, String> poziceCol;
    boolean edit = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        jmenoCol.setCellValueFactory(new PropertyValueFactory<>("jmeno"));
        prijmeniCol.setCellValueFactory(new PropertyValueFactory<>("prijmeni"));
        smenaCol.setCellValueFactory(new PropertyValueFactory<>("smena"));
        telefonCol.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        poziceCol.setCellValueFactory(new PropertyValueFactory<>("pozice"));
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
                if (result.getString("JMENO") != null) {
                    smeny.add(new Smena(result.getInt("ID_SMENA"), result.getString("SMENA"), result.getDate("DATUM"), result.getInt("ID_ZAMESTNANCE"),
                            result.getString("JMENO"), result.getString("PRIJMENI"), result.getString("TELEFON"), result.getInt("ID_POZICE"), result.getString("POZICE")));
                }
            }
            tableView.getItems().addAll(smeny);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void openANewView(ActionEvent event, String fileLocation, DatabaseConnection conn) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource(fileLocation));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Parent parent = loader.load();
        sendDataViaController(fileLocation, loader);
        Scene mainScene = new Scene(parent);
        stage.setScene(mainScene);
        stage.show();
    }

    private void sendDataViaController(String fileLocation, FXMLLoader loader) {
        loader.setLocation(getClass().getResource(fileLocation));
        AkceSmenyController controllerAkceSmeny = loader.getController();
        controllerAkceSmeny.setConnection(connection);
        if (edit) {
            Smena smena = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceSmeny.setData(smena, smena.getIdZamestnance(), smena.getJmeno(),
                        smena.getPrijmeni(), smena.getTelefon(), smena.getIdPozice(),
                        smena.getPozice());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

    /*
    CREATE or REPLACE view smeny_view AS
    SELECT s.id_smena, s.nazev, s.datum, z.jmeno, z.prijmeni
    FROM smeny s LEFT JOIN smeny_zamestn sz ON sz.id_smena = s.id_smena
    LEFT JOIN zamestnanci z ON z.id_zamestnance = sz.id_zamestnance AND sz.id_smena = z.id_smena;
    
     */
    @FXML
    private void pridatAction(ActionEvent event) throws IOException {
        edit = false;
        openANewView(event, "zamestnanci/AkceSmeny.fxml", connection);
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        openANewView(event, "zamestnanci/AkceSmeny.fxml", connection);
    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        Smena smena = tableView.getSelectionModel().getSelectedItem();
        CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberSmenuProc(?)}");
        cstmt.setInt(1, smena.getId());
        cstmt.execute();
        loadData();
        MainSceneController msc = new MainSceneController();
        msc.aktivita(connection, MainSceneController.userName.get(), "SMENY", "DELETE", new Date(System.currentTimeMillis()));

    }

}
