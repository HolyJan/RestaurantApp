/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zakaznici;

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

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AdresyController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Adresa> tableView;
    @FXML
    private TableColumn<Adresa, String> uliceCol;
    @FXML
    private TableColumn<Adresa, String> cpCol;
    @FXML
    private TableColumn<Adresa, String> pscCol;
    @FXML
    private TableColumn<Adresa, String> mestoCol;
    private DatabaseConnection connection;
    private boolean init;
    private boolean edit = false;
    ObservableList<Adresa> adresy = FXCollections.observableArrayList();

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
        uliceCol.setCellValueFactory(new PropertyValueFactory<>("ulice"));
        cpCol.setCellValueFactory(new PropertyValueFactory<>("cisloPop"));
        pscCol.setCellValueFactory(new PropertyValueFactory<>("psc"));
        mestoCol.setCellValueFactory(new PropertyValueFactory<>("mesto"));
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

    @FXML
    private void pridatAction(ActionEvent event) throws IOException {
        edit = false;
        openANewView(event, "zakaznici/AkceAdresy.fxml", connection);
        loadData();
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        openANewView(event, "zakaznici/AkceAdresy.fxml", connection);
        loadData();
    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        Adresa adresa = tableView.getSelectionModel().getSelectedItem();
        CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberAdresuProc(?)}");
        cstmt.setInt(1, adresa.getIdAdresy());
        cstmt.execute();
        loadData();
        MainSceneController msc = new MainSceneController();
        msc.aktivita(connection, MainSceneController.userName.get(), "ADRESY", "DELETE", new Date(System.currentTimeMillis()));
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
        AkceAdresyController controllerAkceAdresy = loader.getController();
        controllerAkceAdresy.setConnection(connection);
        if (edit) {
            Adresa adresa = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceAdresy.setData(adresa);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    private void loadData() {
        adresy.clear();
        tableView.getItems().clear();
        adresy.clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM ADRESY_VIEW");
            while (result.next()) {
                Adresa adresa = new Adresa(result.getInt("ID_ADRESA"), result.getString("ULICE"), result.getString("CISLO_POPISNE"),
                        result.getString("PSC"), result.getString("OBEC"));
                adresy.add(adresa);

            }
            tableView.getItems().addAll(adresy);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
