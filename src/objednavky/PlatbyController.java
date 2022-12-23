/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objednavky;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import rezervace.AkceRezervaceController;
import rezervace.Rezervace;
import rezervace.Stul;
import zakaznici.Adresa;
import zakaznici.Zakaznik;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class PlatbyController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Platba> tableView;
    @FXML
    private TableColumn<Platba, String> castkaCol;
    @FXML
    private TableColumn<Platba, String> datumCol;
    @FXML
    private TableColumn<Platba, String> typPlatbyCol;
    @FXML
    private TableColumn<Platba, String> CisloKartyCol;
    @FXML
    private TableColumn<Platba, String> NazevPolozkyCol;
    private boolean init;
    private boolean edit = false;
    private DatabaseConnection connection;
    ObservableList<Platba> platby = FXCollections.observableArrayList();
    ObservableList<Objednavka> objednavky = FXCollections.observableArrayList();

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
        castkaCol.setCellValueFactory(new PropertyValueFactory<>("castka"));
        datumCol.setCellValueFactory(new PropertyValueFactory<>("datum"));
        typPlatbyCol.setCellValueFactory(new PropertyValueFactory<>("typPlatby"));
        CisloKartyCol.setCellValueFactory(new PropertyValueFactory<>("cisloKarty"));
        NazevPolozkyCol.setCellValueFactory(new PropertyValueFactory<>("nazevPolozky"));
    }

    private void loadData() {
        platby.clear();
        tableView.getItems().clear();
        objednavky.clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM Zakaznici_view");
            List<Zakaznik> zakaznici = new ArrayList<>();
            while (result1.next()) {
                zakaznici.add(new Zakaznik(result1.getInt("ID_ZAKAZNIKA"), result1.getString("JMENO"),
                        result1.getString("PRIJMENI"), result1.getString("TELEFON"), result1.getString("EMAIL"),
                        new Adresa(result1.getInt("ID_ADRESA"), result1.getString("ULICE"),
                                result1.getString("CISLO_POPISNE"), result1.getString("PSC"), result1.getString("OBEC"))));
            }

            ResultSet result = statement.executeQuery("SELECT * FROM objednavky_view");
            while (result.next()) {
                Zakaznik zakazik = null;
                for (Zakaznik zakaznik : zakaznici) {
                    if (zakaznik.getId() == result.getInt("ID_ZAKAZNIKA")) {
                        zakazik = zakaznik;
                    }
                }
                objednavky.add(new Objednavka(result.getInt("ID_OBJEDNAVKY"), zakazik,
                        result.getInt("ID_DORUCENI"), result.getString("CAS_OBJEDNANI"),
                        result.getString("DORUCENI"), result.getInt("ID_POLOZKY"),
                        result.getString("NAZEV_POLOZKY"), result.getInt("CENA")));

            }

            ResultSet result2 = statement.executeQuery("SELECT * FROM platby_view");
            while (result2.next()) {
                for (Objednavka objednavka : objednavky) {
                    if (result2.getInt("ID_OBJEDNAVKY") == objednavka.getIdObjednavky()) {
                        platby.add(new Platba(result2.getInt("ID_PLATBY"), result2.getInt("CASTKA"),
                                result2.getDate("DATUM"), result2.getString("TYP_PLATBY"), result2.getString("CISLO_KARTY"), objednavka));
                    }
                }
            }

            tableView.getItems().addAll(platby);
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
        AkcePlatbyController controllerAkcePlatby = loader.getController();
        controllerAkcePlatby.setConnection(connection);
        if (edit) {
            Platba platba = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkcePlatby.setData(platba);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

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
        openANewView(event, "objednavky/AkcePlatby.fxml", connection);
        loadData();

    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        openANewView(event, "objednavky/AkcePlatby.fxml", connection);
        loadData();

    }

    @FXML
    private void odebratAction(ActionEvent event) {
        Platba platba = tableView.getSelectionModel().getSelectedItem();
        try {
            CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberPlatbuProc(?)}");
            cstmt.setInt(1, platba.getIdPlatby());
            cstmt.execute();
            MainSceneController msc = new MainSceneController();
            msc.aktivita(connection, MainSceneController.userName.get(), "PLATBY", "DELETE", new Date(System.currentTimeMillis()));
            loadData();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
