/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objednavky;

import aktivity.Aktivita;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import menu.Recept;
import oracle.jdbc.OracleTypes;
import zakaznici.Adresa;
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

    List<Zakaznik> zakaznicci = new ArrayList<>();
    DatabaseConnection connection;
    ObservableList<Objednavka> objednavky = FXCollections.observableArrayList();
    boolean init;
    boolean edit = false;
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField tfJmeno;
    @FXML
    private TextField tfPrijmeni;
    @FXML
    private TextField tfNazevPolozky;
    @FXML
    private TextField tfCena;
    @FXML
    private ComboBox<String> cbCasObjednani;
    @FXML
    private ComboBox<String> cbVyzvednuti;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        tfCena.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    tfCena.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
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
        cbCasObjednani.getItems().clear();
        cbVyzvednuti.getItems().clear();

        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result10 = statement.executeQuery("SELECT * FROM ZPUSOBY_VYZVEDNUTI");
            while (result10.next()) {
                cbVyzvednuti.getItems().add(result10.getString("NAZEV"));
            }

            cbCasObjednani.getItems().add("Předem");
            cbCasObjednani.getItems().add("Na místě");

            ResultSet result1 = statement.executeQuery("SELECT * FROM Zakaznici_view");
            while (result1.next()) {
                zakaznicci.add(new Zakaznik(result1.getInt("ID_ZAKAZNIKA"), result1.getString("JMENO"),
                        result1.getString("PRIJMENI"), result1.getString("TELEFON"), result1.getString("EMAIL"),
                        new Adresa(result1.getInt("ID_ADRESA"), result1.getString("ULICE"),
                                result1.getString("CISLO_POPISNE"), result1.getString("PSC"), result1.getString("OBEC"))));
            }

            ResultSet result = statement.executeQuery("SELECT * FROM objednavky_view");
            while (result.next()) {
                Zakaznik zakazik = null;
                for (Zakaznik zakaznik : zakaznicci) {
                    if (zakaznik.getId() == result.getInt("ID_ZAKAZNIKA")) {
                        zakazik = zakaznik;
                    }
                }
                objednavky.add(new Objednavka(result.getInt("ID_OBJEDNAVKY"), zakazik,
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
                        new Zakaznik(objednavka.getZakaznik().getId(), objednavka.getZakaznik().getJmeno(),
                                objednavka.getZakaznik().getPrijmeni(), objednavka.getZakaznik().getTelefon(),
                                objednavka.getZakaznik().getEmail(), objednavka.getZakaznik().getAdresa()),
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
        loadData();
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        if (tableView.getSelectionModel().selectedItemProperty().get() == null) {
            MainSceneController.showDialog("Vyberte položku, kterou chcete poupravit!");;
        } else {
            openANewView(event, "objednavky/akceObjednavka.fxml", connection);
        }
        loadData();
    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        Objednavka objednavka = tableView.getSelectionModel().getSelectedItem();
        try {
            CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberObjednavkuProc(?)}");
            cstmt.setInt(1, objednavka.getIdObjednavky());
            cstmt.execute();
            MainSceneController msc = new MainSceneController();
            msc.aktivita(connection, MainSceneController.userName.get(), "OBJEDNAVKY", "DELETE", new Date(System.currentTimeMillis()));
            loadData();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void PridejAktivitu(Aktivita aktivita) {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("INSERT INTO AKTIVITY (Username, Tabulka, Akce, Datum) VALUES ('"
                    + aktivita.getUzivatel() + "','" + aktivita.getTabulka() + "','" + aktivita.getAkce() + ""
                    + "','" + aktivita.getDatum() + "')");
            result.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void objednavkyPredemDomuAction(ActionEvent event) {
        objednavky.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM Zakaznici_view");
            List<Zakaznik> zakazniciPom = new ArrayList<>();
            while (result1.next()) {
                zakazniciPom.add(new Zakaznik(result1.getInt("ID_ZAKAZNIKA"), result1.getString("JMENO"),
                        result1.getString("PRIJMENI"), result1.getString("TELEFON"), result1.getString("EMAIL"),
                        new Adresa(result1.getInt("ID_ADRESA"), result1.getString("ULICE"),
                                result1.getString("CISLO_POPISNE"), result1.getString("PSC"), result1.getString("OBEC"))));
            }

            ResultSet result = statement.executeQuery("SELECT * FROM objednavky_predem_domu");
            while (result.next()) {
                Zakaznik zakazik = null;
                for (Zakaznik zakaznik : zakazniciPom) {
                    if (zakaznik.getId() == result.getInt("ID_ZAKAZNIKA")) {
                        zakazik = zakaznik;
                    }
                }
                objednavky.add(new Objednavka(result.getInt("ID_OBJEDNAVKY"), zakazik,
                        result.getInt("ID_DORUCENI"), result.getString("CAS_OBJEDNANI"),
                        result.getString("DORUCENI"), result.getInt("ID_POLOZKY"),
                        result.getString("NAZEV_POLOZKY"), result.getInt("CENA")));

            }
            tableView.getItems().addAll(objednavky);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
            if (tfCena.getText() == "") {
                tfCena.setText(null);
            }
            if (tfNazevPolozky.getText() == "") {
                tfNazevPolozky.setText(null);
            }
            objednavky.clear();
            tableView.getItems().clear();
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_OBJEDNAVKY_SEARCH.PRO_RETURN_OBJEDNAVKY(?,?,?,?,?,?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("noveJmeno", tfJmeno.getText());
            cs.setString("novePrijmeni", tfPrijmeni.getText());
            cs.setString("novyCas", cbCasObjednani.getValue());
            cs.setString("noveDoruceni", cbVyzvednuti.getValue());
            cs.setString("novyNazev", tfNazevPolozky.getText());
            cs.setString("novaCena", tfCena.getText());
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");

            while (result.next()) {
                for (Zakaznik zakaznik : zakaznicci) {
                    if (zakaznik.getId() == result.getInt("ID_ZAKAZNIKA")) {
                        objednavky.add(new Objednavka(result.getInt("ID_OBJEDNAVKY"), zakaznik,
                                result.getInt("ID_DORUCENI"), result.getString("CAS_OBJEDNANI"),
                                result.getString("DORUCENI"), result.getInt("ID_POLOZKY"),
                                result.getString("NAZEV_POLOZKY"), result.getInt("CENA")));
                    }
                }

            }
            tableView.getItems().addAll(objednavky);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        loadData();
        tfCena.setText(null);
        tfJmeno.setText(null);
        tfNazevPolozky.setText(null);
        tfPrijmeni.setText(null);

    }
}
