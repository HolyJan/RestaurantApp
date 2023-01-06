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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
public class SmenyController implements Initializable {

    @FXML
    private TableView<Smena> tableView;
    @FXML
    private TableColumn<Smena, String> smenaCol;
    @FXML
    private TableColumn<Smena, String> datumCol;
    private TableColumn<Smena, String> jmenoCol;
    private TableColumn<Smena, String> prijmeniCol;

    DatabaseConnection connection;
    ObservableList<Smena> smeny = FXCollections.observableArrayList();
    @FXML
    private AnchorPane pane;
    boolean init;
    private TableColumn<Smena, String> telefonCol;
    private TableColumn<Smena, String> poziceCol;
    boolean edit = false;
    @FXML
    private DatePicker dpDatum;
    @FXML
    private ComboBox<String> cbSmena;
    @FXML
    private Button odebratBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        smenaCol.setCellValueFactory(new PropertyValueFactory<>("smena"));
        datumCol.setCellValueFactory(new PropertyValueFactory<>("datum"));
        init = false;
        if(MainSceneController.roleId.get() == 2){
            odebratBtn.setVisible(false);
        }
        pane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!init) {
                    loadData();
                    init = true;
                }
            }
        });
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    private void loadData() {
        smeny.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM SMENY");
            while (result.next()) {
                if (result.getString("NAZEV") != null) {
                    smeny.add(new Smena(result.getInt("ID_SMENA"), result.getString("NAZEV"), result.getDate("DATUM"), 0,
                            null, null, null, 0, null));
                }
            }
            tableView.getItems().addAll(smeny);

            cbSmena.getItems().add("Ranní");
            cbSmena.getItems().add("Odpolední");

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
        AkceSmenyController controllerAkceSmeny = loader.getController();
        controllerAkceSmeny.setConnection(connection);
        if (edit) {
            Smena smena = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceSmeny.setData(smena, smena.getIdZamestnance(), smena.getJmeno(),
                        smena.getPrijmeni(), smena.getTelefon(), smena.getIdPozice(),
                        smena.getPozice(), 0);
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
        ZamestnanciSmenyController controllerAkceSmeny = loader.getController();
        controllerAkceSmeny.setConnection(connection);
        controllerAkceSmeny.setSmenaId(tableView.getSelectionModel().selectedItemProperty().get().getId());
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

    @FXML
    private void pridatAction(ActionEvent event) throws IOException {
        edit = false;
        openANewView(event, "zamestnanci/AkceSmeny.fxml", connection);
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        if (tableView.getSelectionModel().selectedItemProperty().get() == null) {
            MainSceneController.showDialog("Vyberte položku, kterou chcete poupravit!");;
        } else {
            openANewView(event, "zamestnanci/AkceSmeny.fxml", connection);
        }
        loadData();
    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        if(tableView.getSelectionModel().getSelectedItem() != null){
                    Smena smena = tableView.getSelectionModel().getSelectedItem();
        CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberSmenuProc(?)}");
        cstmt.setInt(1, smena.getId());
        cstmt.execute();
        loadData();
        }else{
            MainSceneController.showDialog("Není vybrán prvek pro odebrání");
        }
    }

    @FXML
    private void zobrazZamestnAction(ActionEvent event) throws IOException {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            openANewView2(event, "zamestnanci/ZamestnanciSmeny.fxml", connection);
        }else{
            MainSceneController.showDialog("Vyberte směnu, ve které chcete zobrazit zaměstnance!");
        }

    }

    @FXML
    private void filtruj(ActionEvent event) {
        try {
            java.util.Date utilDate = null;
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");
            String date = null;
            if (dpDatum.getValue() != null) {
                utilDate = new java.util.Date(Date.valueOf(dpDatum.getValue()).getTime());
                date = DATE_FORMAT.format(utilDate);
            }
            smeny.clear();
            tableView.getItems().clear();
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_SMENY_SEARCH.PRO_RETURN_SMENY(?,?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("novyNazev", cbSmena.getValue());
            cs.setString("noveDatum", date);
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");
            while (result.next()) {
                smeny.add(new Smena(result.getInt("ID_SMENA"), result.getString("NAZEV"), result.getDate("DATUM"), 0,
                        null, null, null, 0, null));
            }
            tableView.getItems().addAll(smeny);
        } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        loadData();

    }

}
