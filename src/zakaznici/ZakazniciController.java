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
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import login.RegistraceController;
import uzivatele.Uzivatel;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class ZakazniciController implements Initializable {

    @FXML
    private TableColumn<Zakaznik, String> jmenoCol;
    @FXML
    private TableColumn<Zakaznik, String> prijmeniCol;
    @FXML
    private TableColumn<Zakaznik, String> telefonCol;
    @FXML
    private TableColumn<Zakaznik, String> emailCol;

    DatabaseConnection connection;
    ObservableList<Zakaznik> zakaznici = FXCollections.observableArrayList();
    ObservableList<Adresa> adresy = FXCollections.observableArrayList();
    ObservableList<Zakaznik> zakazniciFiltr = FXCollections.observableArrayList();
    @FXML
    private TableView<Zakaznik> tableView;
    @FXML
    private TableColumn<Adresa, String> uliceCol;
    @FXML
    private TableColumn<Adresa, String> cpCol;
    @FXML
    private TableColumn<Adresa, String> pscCol;
    @FXML
    private TableColumn<Adresa, String> mestoCol;
    @FXML
    private AnchorPane pane;
    boolean init;
    boolean edit = false;
    @FXML
    private TextField tfJmeno;
    @FXML
    private TextField tfPrijmeni;
    @FXML
    private TextField tfTelefon;
    @FXML
    private TextField tfEmail;
    @FXML
    private ComboBox<Adresa> tfAdresa;

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
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        uliceCol.setCellValueFactory(new PropertyValueFactory<>("ulice"));
        cpCol.setCellValueFactory(new PropertyValueFactory<>("cisloPop"));
        pscCol.setCellValueFactory(new PropertyValueFactory<>("psc"));
        mestoCol.setCellValueFactory(new PropertyValueFactory<>("mesto"));
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    private void loadData() {
        zakaznici.clear();
        tableView.getItems().clear();
        adresy.clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM ZAKAZNICI_VIEW");
            while (result.next()) {
                Adresa adresa = new Adresa(result.getInt("ID_ADRESA"), result.getString("ULICE"), result.getString("CISLO_POPISNE"),
                        result.getString("PSC"), result.getString("OBEC"));
                zakaznici.add(new Zakaznik(result.getInt("ID_ZAKAZNIKA"), result.getString("JMENO"),
                        result.getString("PRIJMENI"), result.getString("TELEFON"), result.getString("EMAIL"),
                        adresa));
                adresy.add(adresa);

            }
            
            tableView.getItems().addAll(zakaznici);
            tfAdresa.getItems().addAll(adresy);

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
        stage.showAndWait();
    }

    private void sendDataViaController(String fileLocation, FXMLLoader loader) {
        loader.setLocation(getClass().getResource(fileLocation));
        AkceZakaznikController controllerAkceZakaznici = loader.getController();
        controllerAkceZakaznici.setConnection(connection);
        if (edit) {
            Zakaznik zakaznik = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceZakaznici.setData(zakaznik.getId(),
                        zakaznik.getJmeno(), zakaznik.getPrijmeni(),
                        zakaznik.getTelefon(), zakaznik.getEmail(),
                        zakaznik.getAdresa());
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
        openANewView(event, "zakaznici/akceZakaznik.fxml", connection);
        loadData();

    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        if (tableView.getSelectionModel().selectedItemProperty().get() == null) {
            MainSceneController.showDialog("Vyberte položku, kterou chcete poupravit!"); ;
        } else {
            openANewView(event, "zakaznici/akceZakaznik.fxml", connection);
        }
        loadData();

    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        Zakaznik zakaznik = tableView.getSelectionModel().getSelectedItem();
        CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberZakaznikaProc(?)}");
        cstmt.setInt(1, zakaznik.getId());
        cstmt.execute();
        loadData();
        MainSceneController msc = new MainSceneController();
        msc.aktivita(connection, MainSceneController.userName.get(), "ZAKAZNICI", "DELETE", new Date(System.currentTimeMillis()));
    }



    @FXML
    private void filtruj(ActionEvent event) {
        
/*        zakazniciFiltr.clear();
        for(Zakaznik z: zakaznici) {
            zakazniciFiltr.add(z);
        }
        for(Zakaznik z : zakaznici) {
            if(tfJmeno.getText() != "") {
                if(!z.getJmeno().contains(tfJmeno.getText())) {
                    zakazniciFiltr.remove(z);
                }
            }
            if(tfPrijmeni.getText() != "") {
                if(!z.getPrijmeni().contains(tfPrijmeni.getText()) && zakazniciFiltr.contains(z)) {
                    zakazniciFiltr.remove(z);
                }
            }
            if(tfTelefon.getText() != "") {
                if(!z.getTelefon().contains(tfTelefon.getText()) && zakazniciFiltr.contains(z)) {
                    zakazniciFiltr.remove(z);
                }
            }
            if(tfEmail.getText() != "") {
                if(!z.getEmail().contains(tfEmail.getText()) && zakazniciFiltr.contains(z)) {
                    zakazniciFiltr.remove(z);
                }
            }
            if(tfAdresa.getSelectionModel().getSelectedItem() != null) {
                if(z.getAdresa().getIdAdresy() != tfAdresa.getSelectionModel().getSelectedItem().getIdAdresy() && zakazniciFiltr.contains(z)) {
                    zakazniciFiltr.remove(z);
                }
            }
        }
        tableView.getItems().clear();
        tableView.getItems().addAll(zakazniciFiltr);*/
    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        tableView.getItems().addAll(zakaznici);
    }

}
