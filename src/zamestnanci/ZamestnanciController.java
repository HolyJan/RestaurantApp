/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

import connection.DatabaseConnection;
import java.io.IOException;
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
 * @author jenik
 */
public class ZamestnanciController implements Initializable {

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

    DatabaseConnection connection;
    ObservableList<Zamestnanec> zamestnanci = FXCollections.observableArrayList();
    @FXML
    private AnchorPane pane;
    boolean init;
    boolean edit = false;

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
        PoziceCol.setCellValueFactory(new PropertyValueFactory<>("pozice"));
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    private void loadData() {
        zamestnanci.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM ZAMESTNANCI_VIEW");
            while (result.next()) {
                zamestnanci.add(new Zamestnanec(result.getInt("ID_ZAMESTNANCE"), result.getString("JMENO"),
                        result.getString("PRIJMENI"), result.getString("TELEFON"), result.getInt("ID_POZICE"),result.getString("NAZEV")));

            }
            tableView.getItems().addAll(zamestnanci);

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
        AkceZamestnanecController controllerAkceZamestnance = loader.getController();
        controllerAkceZamestnance.setConnection(connection);
        if (edit) {
            Zamestnanec zamestnanec = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceZamestnance.setData(zamestnanec.getId(),
                        zamestnanec.getJmeno(), zamestnanec.getPrijmeni(),
                        zamestnanec.getTelefon(), zamestnanec.getIdPozice(),
                        zamestnanec.getPozice());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }


    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

    @FXML
    private void pridatAction(ActionEvent event) throws IOException {
        edit = false;
        openANewView(event, "zamestnanci/AkceZamestnanec.fxml", connection);
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        openANewView(event, "zamestnanci/AkceZamestnanec.fxml", connection);
    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        Zamestnanec zamestnanec = tableView.getSelectionModel().getSelectedItem();
        CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberZamestnanceProc(?)}");
        cstmt.setInt(1, zamestnanec.getId());
        cstmt.execute();
        loadData();
    }

}
