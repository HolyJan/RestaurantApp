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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import oracle.jdbc.OracleTypes;

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
        tfAdresa.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            boolean podminka;
            ResultSet result = statement.executeQuery("SELECT * FROM ZAKAZNICI_VIEW");
            while (result.next()) {
                Adresa adresa = new Adresa(result.getInt("ID_ADRESA"), result.getString("ULICE"), result.getString("CISLO_POPISNE"),
                        result.getString("PSC"), result.getString("OBEC"));
                zakaznici.add(new Zakaznik(result.getInt("ID_ZAKAZNIKA"), result.getString("JMENO"),
                        result.getString("PRIJMENI"), result.getString("TELEFON"), result.getString("EMAIL"),
                        adresa));
                podminka = false;
                if (adresy.isEmpty()) {
                    adresy.add(adresa);
                } else {
                    for (Adresa a : adresy) {
                        if (a.getIdAdresy() == adresa.getIdAdresy()) {
                            podminka = true;
                        }
                    }
                    if(!podminka){
                        adresy.add(adresa);
                    }
                }

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
            MainSceneController.showDialog("Vyberte položku, kterou chcete poupravit!");;
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
        try {
            if (tfJmeno.getText() == "") {
                tfJmeno.setText(null);
            }
            if (tfPrijmeni.getText() == "") {
                tfPrijmeni.setText(null);
            }
            if (tfTelefon.getText() == "") {
                tfTelefon.setText(null);
            }
            if (tfEmail.getText() == "") {
                tfEmail.setText(null);
            }

            String idAdresy = null;
            if (tfAdresa.getValue() != null) {
                idAdresy = tfAdresa.getValue().getIdAdresy() + "";
            }

            zakaznici.clear();
            tableView.getItems().clear();
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_ZAKAZNICI_SEARCH.PRO_RETURN_ZAKAZNICI(?,?,?,?,?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("noveJmeno", tfJmeno.getText());
            cs.setString("novePrijmeni", tfPrijmeni.getText());
            cs.setString("novyTelefon", tfTelefon.getText());
            cs.setString("novyEmail", tfEmail.getText());
            cs.setString("noveId", idAdresy);
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");
            while (result.next()) {

                for (Adresa adresa : adresy) {
                    if (result.getInt("ID_ADRESA") == adresa.getIdAdresy()) {
                        zakaznici.add(new Zakaznik(result.getInt("ID_ZAKAZNIKA"), result.getString("JMENO"),
                                result.getString("PRIJMENI"), result.getString("TELEFON"), result.getString("EMAIL"),
                                adresa));
                        break;
                    }
                }

            }
            tableView.getItems().addAll(zakaznici);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        loadData();
        tfEmail.setText(null);
        tfJmeno.setText(null);
        tfPrijmeni.setText(null);
        tfTelefon.setText(null);
    }

}
