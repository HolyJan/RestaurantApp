/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obrazky;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
import menu.Polozka;
import objednavky.AkceObjednavkaController;
import objednavky.Objednavka;
import zakaznici.Adresa;
import zakaznici.Zakaznik;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class ObrazkyController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Obrazek> tableView;
    @FXML
    private TableColumn<Obrazek, String> nazevCol;
    @FXML
    private TableColumn<Obrazek, String> typCol;
    @FXML
    private TableColumn<Obrazek, String> umisteniCol;
    @FXML
    private TableColumn<Obrazek, String> priponaCol;

    DatabaseConnection connection;
    ObservableList<Obrazek> obrazky = FXCollections.observableArrayList();
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
                if (!init) {
                    loadData();
                    init = true;
                }
            }
        });
        nazevCol.setCellValueFactory(new PropertyValueFactory<>("nazev"));
        typCol.setCellValueFactory(new PropertyValueFactory<>("typ"));
        umisteniCol.setCellValueFactory(new PropertyValueFactory<>("umisteni"));
        priponaCol.setCellValueFactory(new PropertyValueFactory<>("pripona"));
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

    private void loadData() {
        obrazky.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM obrazky_view");
            List<Zakaznik> zakaznicci = new ArrayList<>();
            while (result1.next()) {
                obrazky.add(new Obrazek(result1.getInt("ID_OBRAZKU"), result1.getString("NAZEV"),
                        result1.getBlob("OBRAZEK"), result1.getString("UMISTENI_OBRAZKU"), result1.getString("PRIPONA_OBRAZKU")));
            }

            tableView.getItems().addAll(obrazky);

        } catch (SQLException e) {
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
        stage.showAndWait();
    }

    private void sendDataViaController(String fileLocation, FXMLLoader loader) {
        loader.setLocation(getClass().getResource(fileLocation));
        AkceObrazkyController controllerAkceObrazky = loader.getController();
        controllerAkceObrazky.setConnection(connection);
        if (edit) {
            Obrazek obrazek = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceObrazky.setData(obrazek);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    @FXML
    private void pridatAction(ActionEvent event) throws IOException {
        edit = false;
        openANewView(event, "obrazky/AkceObrazky.fxml", connection);
        loadData();

    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        openANewView(event, "obrazky/AkceObrazky.fxml", connection);
        loadData();

    }

    @FXML
    private void odebratAction(ActionEvent event) {
        Obrazek obrazek = tableView.getSelectionModel().getSelectedItem();
        try {
            CallableStatement cstmt = connection.getConnection().prepareCall("{call deleteObrazekProc(?)}");
            cstmt.setInt(1, obrazek.getIdObrazku());
            cstmt.execute();
            loadData();
            MainSceneController msc = new MainSceneController();
            msc.aktivita(connection, MainSceneController.userName.get(), "OBRAZKY_MENU", "DELETE", new Date(System.currentTimeMillis()));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
