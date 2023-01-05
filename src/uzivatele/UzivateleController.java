/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uzivatele;

import connection.DatabaseConnection;
import connection.DatabaseException;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    @FXML
    private Button emulationBtn;
    @FXML
    private TextField tfJmeno;
    @FXML
    private TextField tfPrijmeni;
    @FXML
    private TextField tfLogin;
    @FXML
    private ComboBox<String> cbRole;

    /**
     * Initializes the controller class.
     *
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
        cbRole.getItems().clear();
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

            ResultSet result1 = statement.executeQuery("SELECT * FROM Role");
            while (result1.next()) {
                cbRole.getItems().add(result1.getString("ROLE"));
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
        stage.showAndWait();
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
        if (tableView.getSelectionModel().selectedItemProperty().get() == null) {
            MainSceneController.showDialog("Vyberte položku, kterou chcete poupravit!");;
        } else {
            openANewView(event, "uzivatele/akceUzivatele.fxml", connection);
        }
        loadData();
    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        Uzivatel uzivatel = tableView.getSelectionModel().getSelectedItem();
        CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberUzivateleProc(?)}");
        cstmt.setInt(1, uzivatel.getIdUzivatele());
        cstmt.execute();
        loadData();
        MainSceneController msc = new MainSceneController();
        msc.aktivita(connection, MainSceneController.userName.get(), "UZIVATELE", "INSERT", new Date(System.currentTimeMillis()));
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    @FXML
    private void emulationBtn(ActionEvent event) {
        Uzivatel uzivatel = tableView.getSelectionModel().getSelectedItem();
        try {
            if (uzivatel == null) {
                throw new DatabaseException("Please select a user");
            }
            MainSceneController.emulation = true;
            MainSceneController.userName.set(uzivatel.getLogin());
            MainSceneController.roleId.set(uzivatel.getRole().getIdRole());
            Stage stage1 = (Stage) tableView.getScene().getWindow();
            stage1.close();
        } catch (DatabaseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
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
            if (tfLogin.getText() == "") {
                tfLogin.setText(null);
            }

            uzivatele.clear();
            tableView.getItems().clear();
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_UZIVATELE_SEARCH.PRO_RETURN_UZIVATELE(?,?,?,?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("noveJmeno", tfJmeno.getText());
            cs.setString("novePrijmeni", tfPrijmeni.getText());
            cs.setString("novyLogin", tfLogin.getText());
            cs.setString("novaRole", cbRole.getValue());
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");
            while (result.next()) {

                uzivatele.add(new Uzivatel(result.getInt("ID_UZIVATELE"), result.getString("JMENO"), result.getString("PRIJMENI"), result.getString("LOGIN"),
                        result.getString("HESLO"), new Role(result.getInt("ID_ROLE"), result.getString("ROLE"))));

            }
            tableView.getItems().addAll(uzivatele);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        loadData();
        tfJmeno.setText(null);
        tfLogin.setText(null);
        tfPrijmeni.setText(null);
    }
}
