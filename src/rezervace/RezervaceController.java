/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rezervace;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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
import uzivatele.AkceUzivateleController;
import uzivatele.Role;
import uzivatele.Uzivatel;
import zakaznici.Zakaznik;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class RezervaceController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Rezervace> tableView;
    @FXML
    private TableColumn<Rezervace, String> casCol;
    @FXML
    private TableColumn<Rezervace, String> datumCol;
    @FXML
    private TableColumn<Rezervace, String> jmenoCol;
    @FXML
    private TableColumn<Rezervace, String> prijmeniCol;
    @FXML
    private TableColumn<Rezervace, String> cisloStoluCol;
    private boolean init;
    private boolean edit = false;
    DatabaseConnection connection;

    ObservableList<Rezervace> rezervace = FXCollections.observableArrayList();
    ObservableList<Rezervace> rezervace1 = FXCollections.observableArrayList();
    ObservableList<Stul> stoly = FXCollections.observableArrayList();
    ObservableList<Zakaznik> zakaznici = FXCollections.observableArrayList();

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
        casCol.setCellValueFactory(new PropertyValueFactory<>("cas"));
        datumCol.setCellValueFactory(new PropertyValueFactory<>("datum"));
        jmenoCol.setCellValueFactory(new PropertyValueFactory<>("jmeno"));
        prijmeniCol.setCellValueFactory(new PropertyValueFactory<>("prijmeni"));
        cisloStoluCol.setCellValueFactory(new PropertyValueFactory<>("cisloStolu"));
    }

    private void loadData() {
        rezervace.clear();
        tableView.getItems().clear();
        zakaznici.clear();
        stoly.clear();
        Statement statement = connection.createBlockedStatement();
        try {

            ResultSet result1 = statement.executeQuery("SELECT * FROM ZAKAZNICI_VIEW");
            while (result1.next()) {
                zakaznici.add(new Zakaznik(result1.getInt("ID_ZAKAZNIKA"), result1.getString("JMENO"),
                        result1.getString("PRIJMENI"), result1.getString("TELEFON"), result1.getString("EMAIL"), null));
            }

            ResultSet result2 = statement.executeQuery("SELECT * FROM STOLY_VIEW");
            while (result2.next()) {
                stoly.add(new Stul(result2.getInt("ID_STUL"), result2.getInt("CISLO_STOLU"),
                        result2.getInt("POCET_MIST")));
            }

            ResultSet result = statement.executeQuery("SELECT * FROM REZERVACE_VIEW");
            while (result.next()) {
                for (Zakaznik zakaznik : zakaznici) {
                    if (result.getInt("ID_ZAKAZNIKA") == zakaznik.getId()) {
                        for (Stul stul : stoly) {
                            if (result.getInt("CISLO_STOLU") == stul.getCisloStolu()) {
                                rezervace.add(new Rezervace(result.getInt("ID_REZERVACE"),
                                        result.getString("CAS"), result.getDate("DATUM"), zakaznik, stul));
                            }
                        }
                    }
                }
            }
            tableView.getItems().addAll(rezervace);
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
        AkceRezervaceController controllerAkceRezervace = loader.getController();
        controllerAkceRezervace.setConnection(connection);
        if (edit) {
            Rezervace rezervace = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceRezervace.setData(rezervace);
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
        openANewView(event, "rezervace/AkceRezervace.fxml", connection);

    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        openANewView(event, "rezervace/AkceRezervace.fxml", connection);

    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        Rezervace rezervace = tableView.getSelectionModel().getSelectedItem();
        CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberRezervaciProc(?)}");
        cstmt.setInt(1, rezervace.getIdRezervace());
        cstmt.execute();
        loadData();
        MainSceneController msc = new MainSceneController();
        msc.aktivita(connection, MainSceneController.userName.get(), "REZERVACE", "DELETE", new Date(System.currentTimeMillis()));
    }

    @FXML
    private void nadchazejiciRezervaceAction(ActionEvent event) {
        rezervace.clear();
        rezervace1.clear();
        tableView.getItems().clear();
        zakaznici.clear();
        stoly.clear();
        Statement statement = connection.createBlockedStatement();
        try {

            ResultSet result1 = statement.executeQuery("SELECT * FROM ZAKAZNICI_VIEW");
            while (result1.next()) {
                zakaznici.add(new Zakaznik(result1.getInt("ID_ZAKAZNIKA"), result1.getString("JMENO"),
                        result1.getString("PRIJMENI"), result1.getString("TELEFON"), result1.getString("EMAIL"), null));
            }

            ResultSet result2 = statement.executeQuery("SELECT * FROM STOLY_VIEW");
            while (result2.next()) {
                stoly.add(new Stul(result2.getInt("ID_STUL"), result2.getInt("CISLO_STOLU"),
                        result2.getInt("POCET_MIST")));
            }

            ResultSet result = statement.executeQuery("SELECT * FROM REZERVACE_VIEW");
            while (result.next()) {
                for (Zakaznik zakaznik : zakaznici) {
                    if (result.getInt("ID_ZAKAZNIKA") == zakaznik.getId()) {
                        for (Stul stul : stoly) {
                            if (result.getInt("CISLO_STOLU") == stul.getCisloStolu()) {
                                rezervace.add(new Rezervace(result.getInt("ID_REZERVACE"),
                                        result.getString("CAS"), result.getDate("DATUM"), zakaznik, stul));
                            }
                        }
                    }
                }
            }
            ResultSet result3 = statement.executeQuery("SELECT * FROM zakaznici_rezervace_stul ");
            while (result3.next()) {
                for (Rezervace rezervace2 : rezervace) {
                    String cas = result3.getString("CAS");
                    String jmeno = result3.getString("JMENO");
                    String prijmeni = result3.getString("PRIJMENI");
                    Date datum = result3.getDate("DATUM");
                    int cisloStolu = result3.getInt("CISLO_STOLU");
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                    if (result3.getString("CAS").equals(rezervace2.getCas()) && 
                            f.format(result3.getDate("DATUM")).equals(f.format(rezervace2.getDatum()))) {
                        for (Zakaznik zakaznik : zakaznici) {
                            if (result3.getString("PRIJMENI").equals(zakaznik.getPrijmeni()) && result3.getString("JMENO").equals(zakaznik.getJmeno())) {
                                for (Stul stul : stoly) {
                                    if (result3.getInt("CISLO_STOLU") == stul.getCisloStolu()) {
                                        rezervace1.add(new Rezervace(rezervace2.getIdRezervace(),
                                                rezervace2.getCas(), rezervace2.getDatum(), zakaznik, stul));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            tableView.getItems().addAll(rezervace1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
