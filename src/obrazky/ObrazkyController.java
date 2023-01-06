/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obrazky;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import menu.Polozka;
import menu.Recept;
import objednavky.AkceObjednavkaController;
import objednavky.Objednavka;
import oracle.jdbc.OracleTypes;
import zakaznici.Adresa;
import zakaznici.Zakaznik;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class ObrazkyController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Obrazek> tableView;
    @FXML
    private TableColumn<Obrazek, String> nazevCol;
    @FXML
    private TableColumn<Obrazek, String> typCol;
    @FXML
    private TableColumn<Obrazek, String> umisteniCol;
    @FXML
    private TableColumn<Obrazek, String> priponaCol;

    DatabaseConnection connection;
    ObservableList<Obrazek> obrazky = FXCollections.observableArrayList();
    boolean init;
    boolean edit = false;
    @FXML
    private TextField tfNazev;
    @FXML
    private TextField tfPripona;
    @FXML
    private VBox odebratBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        nazevCol.setCellValueFactory(new PropertyValueFactory<>("nazev"));
        typCol.setCellValueFactory(new PropertyValueFactory<>("typ"));
        umisteniCol.setCellValueFactory(new PropertyValueFactory<>("umisteni"));
        priponaCol.setCellValueFactory(new PropertyValueFactory<>("pripona"));
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

    private void loadData() {
        obrazky.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM obrazky_view");
            List<Zakaznik> zakaznicci = new ArrayList<>();
            while (result1.next()) {
                obrazky.add(new Obrazek(result1.getInt("ID_OBRAZKU"), result1.getString("NAZEV"),
                        result1.getBlob("OBRAZEK"), result1.getString("UMISTENI_OBRAZKU"), result1.getString("PRIPONA_OBRAZKU")));
            }

            tableView.getItems().addAll(obrazky);

        } catch (SQLException e) {
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
        AkceObrazkyController controllerAkceObrazky = loader.getController();
        controllerAkceObrazky.setConnection(connection);
        if (edit) {
            Obrazek obrazek = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceObrazky.setData(obrazek);
            } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }
        }

    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    @FXML
    private void pridatAction(ActionEvent event) throws IOException {
        edit = false;
        openANewView(event, "obrazky/AkceObrazky.fxml", connection);
        loadData();

    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        if (tableView.getSelectionModel().selectedItemProperty().get() == null) {
            MainSceneController.showDialog("Vyberte položku, kterou chcete poupravit!");;
        } else {
            openANewView(event, "obrazky/AkceObrazky.fxml", connection);
        }
        loadData();

    }

    @FXML
    private void odebratAction(ActionEvent event) {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Obrazek obrazek = tableView.getSelectionModel().getSelectedItem();
            try {
                CallableStatement cstmt = connection.getConnection().prepareCall("{call deleteObrazekProc(?)}");
                cstmt.setInt(1, obrazek.getIdObrazku());
                cstmt.execute();
                loadData();
            } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }
        } else {
            MainSceneController.showDialog("Není vybrán prvek pro odebrání");
        }
    }

    @FXML
    private void filtruj(ActionEvent event) {
        try {
            if (tfNazev.getText() == "") {
                tfNazev.setText(null);
            }
            if (tfPripona.getText() == "") {
                tfPripona.setText(null);
            }

            obrazky.clear();
            tableView.getItems().clear();
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_OBRAZKY_SEARCH.PRO_RETURN_OBRAZKY(?,?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("novyNazev", tfNazev.getText());
            cs.setString("novaKoncovka", tfPripona.getText());
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");
            while (result.next()) {

                obrazky.add(new Obrazek(result.getInt("ID_OBRAZKU"), result.getString("NAZEV"),
                        result.getBlob("OBRAZEK"), result.getString("UMISTENI_OBRAZKU"), result.getString("PRIPONA_OBRAZKU")));

            }
            tableView.getItems().addAll(obrazky);
        } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        loadData();
        tfNazev.setText("");
        tfPripona.setText("");
    }
}
