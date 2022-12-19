/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rezervace;

import connection.DatabaseConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uzivatele.AkceUzivateleController;
import uzivatele.Role;
import uzivatele.Uzivatel;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class StolyController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Stul> tableView;
    @FXML
    private TableColumn<Stul, String> cisloStoluCol;
    @FXML
    private TableColumn<Stul, String> pocetMistCol;
    ObservableList<Stul> stoly = FXCollections.observableArrayList();
    private boolean init;
    private boolean edit = false;
    DatabaseConnection connection;

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
        cisloStoluCol.setCellValueFactory(new PropertyValueFactory<>("cisloStolu"));
        pocetMistCol.setCellValueFactory(new PropertyValueFactory<>("pocetMist"));
    }

    private void loadData() {
        stoly.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM STOLY_VIEW");
            while (result.next()) {
                if (result.getInt("POCET_MIST") != 0) {
                    stoly.add(new Stul(result.getInt("ID_STUL"), result.getInt("CISLO_STOLU"), result.getInt("POCET_MIST")));
                }
            }
            tableView.getItems().addAll(stoly);
        } catch (SQLException e) {
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
        AkceStolyController controllerAkceStoly = loader.getController();
        controllerAkceStoly.setConnection(connection);
        if (edit) {
            Stul stul = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceStoly.setData(stul.getIdStolu(),
                        stul.getCisloStolu(), stul.getPocetMist());
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
        openANewView(event, "rezervace/AkceStoly.fxml", connection);
        loadData();
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        openANewView(event, "rezervace/AkceStoly.fxml", connection);
        loadData();
    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        Stul stul = tableView.getSelectionModel().getSelectedItem();
        CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberStulProc(?)}");
        cstmt.setInt(1, stul.getIdStolu());
        cstmt.execute();
        loadData();
    }

}
