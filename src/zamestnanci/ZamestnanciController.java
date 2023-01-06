/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import objednavky.AkceObjednavkaController;
import objednavky.Objednavka;
import oracle.jdbc.OracleTypes;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class ZamestnanciController implements Initializable {

    @FXML
    private TableView<Zamestnanec> tableView;
    @FXML
    private TableColumn<Zamestnanec, String> jmenoCol;
    @FXML
    private TableColumn<Zamestnanec, String> prijmeniCol;
    @FXML
    private TableColumn<Zamestnanec, String> telefonCol;
    @FXML
    private TableColumn<Zamestnanec, String> PoziceCol;

    DatabaseConnection connection;
    ObservableList<Zamestnanec> zamestnanci = FXCollections.observableArrayList();
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
    private ComboBox<String> cbPozice;
    @FXML
    private Button odebratBtn;

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
        PoziceCol.setCellValueFactory(new PropertyValueFactory<>("pozice"));
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    private void loadData() {
        zamestnanci.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM ZAMESTNANCI_VIEW");
            while (result.next()) {
                zamestnanci.add(new Zamestnanec(result.getInt("ID_ZAMESTNANCE"), result.getString("JMENO"),
                        result.getString("PRIJMENI"), result.getString("TELEFON"),
                        result.getInt("ID_POZICE"), result.getString("NAZEV"), result.getInt("ID_NADRIZENEHO")));
                if (!cbPozice.getItems().contains(result.getString("NAZEV"))) {
                    cbPozice.getItems().add(result.getString("NAZEV"));
                }
            }
            tableView.getItems().addAll(zamestnanci);

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
        AkceZamestnanecController controllerAkceZamestnance = loader.getController();
        controllerAkceZamestnance.setConnection(connection);
        if (edit) {
            Zamestnanec zamestnanec = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceZamestnance.setData(zamestnanec.getId(),
                        zamestnanec.getJmeno(), zamestnanec.getPrijmeni(),
                        zamestnanec.getTelefon(), zamestnanec.getIdPozice(),
                        zamestnanec.getPozice());
            } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }
        }

    }

    private void openANewView2(ActionEvent event, String fileLocation, DatabaseConnection conn) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource(fileLocation));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Parent parent = loader.load();
        sendDataViaController2(fileLocation, loader);
        Scene mainScene = new Scene(parent);
        stage.setScene(mainScene);
        stage.showAndWait();
    }

    private void sendDataViaController2(String fileLocation, FXMLLoader loader) {
        loader.setLocation(getClass().getResource(fileLocation));
        SmenyZamestnanceController controllerAkceZamestnance = loader.getController();
        controllerAkceZamestnance.setConnection(connection);
        controllerAkceZamestnance.setIdZamestnance(tableView.getSelectionModel().getSelectedItem().getId());
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

    @FXML
    private void pridatAction(ActionEvent event) throws IOException {
        edit = false;
        openANewView(event, "zamestnanci/AkceZamestnanec.fxml", connection);
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        if (tableView.getSelectionModel().selectedItemProperty().get() == null) {
            MainSceneController.showDialog("Vyberte položku, kterou chcete poupravit!");;
        } else {
            openANewView(event, "zamestnanci/AkceZamestnanec.fxml", connection);
        }
        loadData();
    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Zamestnanec zamestnanec = tableView.getSelectionModel().getSelectedItem();
            CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberZamestnanceProc(?)}");
            cstmt.setInt(1, zamestnanec.getId());
            cstmt.execute();
            loadData();
        } else {
            MainSceneController.showDialog("Není vybrán zaměstnanec, kterého chcete odebrat");
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
            String telefon = null;
            if (!tfTelefon.getText().isEmpty()) {
                telefon = tfTelefon.getText();
            }
            zamestnanci.clear();
            tableView.getItems().clear();
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_ZAMESTNANCI_SEARCH.PRO_RETURN_ZAMESTNANCI(?,?,?,?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("noveJmeno", tfJmeno.getText());
            cs.setString("novePrijmeni", tfPrijmeni.getText());
            cs.setString("novyTelefon", telefon);
            cs.setString("novaPozice", cbPozice.getValue());
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");
            while (result.next()) {
                zamestnanci.add(new Zamestnanec(result.getInt("ID_ZAMESTNANCE"), result.getString("JMENO"),
                        result.getString("PRIJMENI"), result.getString("TELEFON"),
                        result.getInt("ID_POZICE"), result.getString("NAZEV"), result.getInt("ID_NADRIZENEHO")));

            }
            tableView.getItems().addAll(zamestnanci);
        } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        loadData();
        cbPozice.setValue(null);
        tfJmeno.setText("");
        tfPrijmeni.setText("");
        tfTelefon.setText("");

    }

    @FXML
    private void zobrazNadrizenehoAction(ActionEvent event) {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            try {
                zamestnanci.clear();
                int id = tableView.getSelectionModel().getSelectedItem().getId();
                tableView.getItems().clear();
                CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_ZAMESTNANCI_SEARCH.PRO_RETURN_NADRIZENE(?,?)}");
                cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
                cs.setInt("noveId", id);
                cs.execute();
                ResultSet result = (ResultSet) cs.getObject("o_cursor");
                while (result.next()) {
                    zamestnanci.add(new Zamestnanec(result.getInt("ID_ZAMESTNANCE"), result.getString("JMENO"),
                            result.getString("PRIJMENI"), result.getString("TELEFON"),
                            result.getInt("ID_POZICE"), result.getString("NAZEV"), result.getInt("ID_NADRIZENEHO")));
                }
                tableView.getItems().addAll(zamestnanci);
            } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }
        } else {
            MainSceneController.showDialog("Není vybrán zaměstnanec pro zobrazení nadřízených");
        }
    }

    @FXML
    private void zobrazSmenyAction(ActionEvent event) throws IOException {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            openANewView2(event, "zamestnanci/SmenyZamestnance.fxml", connection);
        } else {
            MainSceneController.showDialog("Není vybrán zaměstnanec pro zobrazení směn");
        }
    }

    @FXML
    private void zamestnanciSmenyAction(ActionEvent event) {
        zamestnanci.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM zamestnanci_pozice_smena");
            while (result.next()) {
                if (result.getString("JMENO") != null) {
                    zamestnanci.add(new Zamestnanec(result.getInt("ID_ZAMESTNANCE"),
                            result.getString("JMENO"), result.getString("PRIJMENI"), result.getString("TELEFON"), result.getInt("ID_POZICE"), result.getString("NAZEVPOZICE"), 0));
                }
            }
            tableView.getItems().addAll(zamestnanci);

        } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

}
