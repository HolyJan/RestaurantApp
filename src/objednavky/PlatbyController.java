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
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import oracle.jdbc.OracleTypes;
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
    @FXML
    private TextField tfCastka;
    @FXML
    private DatePicker dpDatum;
    private TextField tfTypPlatby;
    private TextField tfCisloKarty;
    private TextField tfNazevPolozky;
    @FXML
    private ComboBox<String> cbTypPlatby;
    @FXML
    private DatePicker dpDatumFunkce;
    @FXML
    private Button odebratBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        if(MainSceneController.roleId.get() == 2){
            odebratBtn.setVisible(false);
        }
        tfCastka.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    tfCastka.setText(newValue.replaceAll("[^\\d]", ""));
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
        cbTypPlatby.getItems().clear();
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
            cbTypPlatby.getItems().add("Hotově");
            cbTypPlatby.getItems().add("Kartou");
            tableView.getItems().addAll(platby);
        } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
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
        AkcePlatbyController controllerAkcePlatby = loader.getController();
        controllerAkcePlatby.setConnection(connection);
        if (edit) {
            Platba platba = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkcePlatby.setData(platba);
            } catch (Exception e) {
                MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
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
        if (tableView.getSelectionModel().selectedItemProperty().get() == null) {
            MainSceneController.showDialog("Vyberte položku, kterou chcete poupravit!");;
        } else {
            openANewView(event, "objednavky/AkcePlatby.fxml", connection);
        }
        loadData();

    }

    @FXML
    private void odebratAction(ActionEvent event) {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Platba platba = tableView.getSelectionModel().getSelectedItem();
            try {
                CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberPlatbuProc(?)}");
                cstmt.setInt(1, platba.getIdPlatby());
                cstmt.execute();
                loadData();
            } catch (Exception e) {
                MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }
        } else {
            MainSceneController.showDialog("Není vybrán prvek pro odebrání");
        }
    }

    @FXML
    private void filtruj(ActionEvent event) {
        try {
            if (tfCastka.getText() == "") {
                tfCastka.setText(null);
            }

            platby.clear();
            tableView.getItems().clear();
            java.util.Date utilDate = null;
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");
            String date = null;
            if (dpDatum.getValue() != null) {
                utilDate = new java.util.Date(Date.valueOf(dpDatum.getValue()).getTime());
                date = DATE_FORMAT.format(utilDate);
            }
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_PLATBY_SEARCH.PRO_RETURN_PLATBY(?,?,?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("novaCastka", tfCastka.getText());
            cs.setString("noveDatum", date);
            cs.setString("novyTyp", cbTypPlatby.getValue());
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");
            while (result.next()) {
                for (Objednavka objednavka : objednavky) {
                    if (result.getInt("ID_OBJEDNAVKY") == objednavka.getIdObjednavky()) {
                        platby.add(new Platba(result.getInt("ID_PLATBY"), result.getInt("CASTKA"),
                                result.getDate("DATUM"), result.getString("TYP_PLATBY"), result.getString("CISLO_KARTY"), objednavka));
                    }
                }

            }
            tableView.getItems().addAll(platby);
        } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        loadData();
        tfCastka.setText("");
        cbTypPlatby.setValue("");
    }

    @FXML
    private void zabrazTrzbyKartouAction(ActionEvent event) throws SQLException {
        java.util.Date utilDate = null;
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");
        String date = null;
        if (dpDatumFunkce.getValue() != null) {
            utilDate = new java.util.Date(Date.valueOf(dpDatumFunkce.getValue()).getTime());
            date = DATE_FORMAT.format(utilDate);
        }
        if (date == null) {
            MainSceneController.showError("Není vybrán datum!");
        } else {
            CallableStatement cs = this.connection.getConnection().prepareCall("{? = call trzby_kartou(?)}");
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, date);
            cs.executeUpdate();
            MainSceneController.showDialog("Tržby od " + date + " placené kartou jsou: " + cs.getInt(1) + "Kč");
        }
    }

    @FXML
    private void zobrazTrzbyHotoveAction(ActionEvent event) throws SQLException {
        java.util.Date utilDate = null;
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");
        String date = null;
        if (dpDatumFunkce.getValue() != null) {
            utilDate = new java.util.Date(Date.valueOf(dpDatumFunkce.getValue()).getTime());
            date = DATE_FORMAT.format(utilDate);
        }
        if (date == null) {
            MainSceneController.showError("Není vybrán datum!");
        } else {
            CallableStatement cs = this.connection.getConnection().prepareCall("{? = call trzby_hotove(?)}");
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, date);
            cs.executeUpdate();
            MainSceneController.showDialog("Tržby od " + date + " placené hotově jsou: " + cs.getInt(1) + "Kč");
        }
    }

}
