/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objednavky;

import connection.DatabaseConnection;
import java.io.IOException;
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
import zakaznici.AkceZakaznikController;
import zakaznici.Zakaznik;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class ObjednavkyController implements Initializable {

    @FXML
    private TableView<Objednavka> tableView;
    @FXML
    private TableColumn<Objednavka, String> jmenoCol;
    @FXML
    private TableColumn<Objednavka, String> prijmeniCol;
    @FXML
    private TableColumn<Objednavka, String> casObjednCol;
    @FXML
    private TableColumn<Objednavka, String> vyzvednutiCol;
    @FXML
    private TableColumn<Objednavka, String> nazevPolozkyCol;
    @FXML
    private TableColumn<Objednavka, String> cenaCol;

    DatabaseConnection connection;
    ObservableList<Objednavka> objednavky = FXCollections.observableArrayList();
    boolean init;
    boolean edit = false;
    @FXML
    private AnchorPane pane;

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
        casObjednCol.setCellValueFactory(new PropertyValueFactory<>("casObjednani"));
        vyzvednutiCol.setCellValueFactory(new PropertyValueFactory<>("vyzvednuti"));
        nazevPolozkyCol.setCellValueFactory(new PropertyValueFactory<>("nazevPolozky"));
        cenaCol.setCellValueFactory(new PropertyValueFactory<>("cenaPolozky"));
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

    private void loadData() {
        objednavky.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM objednavky_view");
            while (result.next()) {
                objednavky.add(new Objednavka(result.getInt("ID_OBJEDNAVKY"), result.getString("JMENO"), result.getString("PRIJMENI"),
                        result.getInt("ID_DORUCENI"), result.getString("CAS_OBJEDNANI"),
                        result.getString("DORUCENI"), result.getInt("ID_POLOZKY"),
                        result.getString("NAZEV_POLOZKY"), result.getInt("CENA")));

            }
            tableView.getItems().addAll(objednavky);

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
        AkceObjednavkaController controllerAkceObjednavky = loader.getController();
        controllerAkceObjednavky.setConnection(connection);
        if (edit) {
            Objednavka objednavka = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceObjednavky.setData(objednavka.getIdObjednavky(),
                        objednavka.getJmeno(), objednavka.getPrijmeni(),
                        objednavka.getIdDoruceni(), objednavka.getCasObjednani(),
                        objednavka.getVyzvednuti(), objednavka.getIdPolozky(),
                        objednavka.getNazevPolozky(), objednavka.getCenaPolozky());
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
        openANewView(event, "objednavky/akceObjednavka.fxml", connection);
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        openANewView(event, "objednavky/akceObjednavka.fxml", connection);
    }

    @FXML
    private void odebratAction(ActionEvent event) {
    }

}
