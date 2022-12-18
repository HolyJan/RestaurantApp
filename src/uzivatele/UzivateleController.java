/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uzivatele;

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
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import objednavky.AkceObjednavkaController;
import objednavky.Objednavka;
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
    boolean edit = false;
    @FXML
    private AnchorPane pane;
    ObservableList<Uzivatel> uzivatele = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
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
                if (result.getString("JMENO") != null) {
                    uzivatele.add(new Uzivatel(result.getInt("ID_UZIVATELE"), result.getString("JMENO"), result.getString("PRIJMENI"), result.getString("LOGIN"),
                            result.getString("HESLO"), new Role(result.getInt("ID_ROLE"), result.getString("ROLE"))));
                }
            }
            tableView.getItems().addAll(uzivatele);

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
        AkceUzivateleController controllerAkceUzivatele = loader.getController();
        controllerAkceUzivatele.setConnection(connection);
        if (edit) {
            Uzivatel uzivatel = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceUzivatele.setData(uzivatel.getIdUzivatele(),
                        uzivatel.getJmeno(), uzivatel.getPrijmeni(),
                        uzivatel.getLogin(), uzivatel.getHeslo(),
                        uzivatel.getRole());
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
        openANewView(event, "uzivatele/akceUzivatele.fxml", connection);
        loadData();
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        openANewView(event, "uzivatele/akceUzivatele.fxml", connection);
        loadData();
    }

    @FXML
    private void odebratAction(ActionEvent event) {
        Uzivatel uzivatel = tableView.getSelectionModel().getSelectedItem();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("DELETE FROM uzivatele WHERE login = '" + uzivatel.getLogin()+"'");
            if (result.next()) {
                loadData();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }
}
