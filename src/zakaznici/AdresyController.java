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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import menu.Recept;
import oracle.jdbc.OracleTypes;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AdresyController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Adresa> tableView;
    @FXML
    private TableColumn<Adresa, String> uliceCol;
    @FXML
    private TableColumn<Adresa, String> cpCol;
    @FXML
    private TableColumn<Adresa, String> pscCol;
    @FXML
    private TableColumn<Adresa, String> mestoCol;
    private DatabaseConnection connection;
    private boolean init;
    private boolean edit = false;
    ObservableList<Adresa> adresy = FXCollections.observableArrayList();
    @FXML
    private TextField tfUlice;
    @FXML
    private TextField tfCisloPop;
    @FXML
    private TextField tfPSC;
    @FXML
    private TextField tfObec;
    @FXML
    private Button upravitBtn;
    @FXML
    private Button odebratBtn;
    @FXML
    private VBox btnsBox;
    @FXML
    private VBox filtrBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        if (MainSceneController.roleId.get() == 1) {
            filtrBox.setVisible(false);
            btnsBox.setVisible(false);
        }
        if(MainSceneController.roleId.get() == 2){
            odebratBtn.setVisible(false);
        }
        tfCisloPop.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    tfCisloPop.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        tfPSC.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    tfPSC.setText(newValue.replaceAll("[^\\d]", ""));
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
        uliceCol.setCellValueFactory(new PropertyValueFactory<>("ulice"));
        cpCol.setCellValueFactory(new PropertyValueFactory<>("cisloPop"));
        pscCol.setCellValueFactory(new PropertyValueFactory<>("psc"));
        mestoCol.setCellValueFactory(new PropertyValueFactory<>("mesto"));
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
        openANewView(event, "zakaznici/AkceAdresy.fxml", connection);
        loadData();
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        if (tableView.getSelectionModel().selectedItemProperty().get() == null) {
            MainSceneController.showDialog("Vyberte položku, kterou chcete poupravit!");;
        } else {
            openANewView(event, "zakaznici/AkceAdresy.fxml", connection);
        }
        loadData();
    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Adresa adresa = tableView.getSelectionModel().getSelectedItem();
            CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberAdresuProc(?)}");
            cstmt.setInt(1, adresa.getIdAdresy());
            cstmt.execute();
            loadData();
        } else {
            MainSceneController.showDialog("Není vybrán prvek pro odebrání");
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
        AkceAdresyController controllerAkceAdresy = loader.getController();
        controllerAkceAdresy.setConnection(connection);
        if (edit) {
            Adresa adresa = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceAdresy.setData(adresa);
            } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }
        }

    }

    private void loadData() {
        adresy.clear();
        tableView.getItems().clear();
        adresy.clear();
        Statement statement = connection.createBlockedStatement();
        ObservableList<Adresa> adresyPom = FXCollections.observableArrayList();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM ADRESY_VIEW");
            while (result.next()) {
                Adresa adresa = new Adresa(result.getInt("ID_ADRESA"), result.getString("ULICE"), result.getString("CISLO_POPISNE"),
                        result.getString("PSC"), result.getString("OBEC"));
                adresy.add(adresa);
                adresyPom.add(adresa);

            }
            if (MainSceneController.roleId.get() == 1) {
                ResultSet result1 = statement.executeQuery("SELECT * FROM zakaznici_view where telefon = '" + MainSceneController.telefon.get() + "'");
                if (result1.next()) {
                    adresy.clear();
                    for (Adresa a : adresyPom) {
                        if(a.getIdAdresy() == result1.getInt("ID_ADRESA")){
                            adresy.add(a);
                        }
                    }

                }
            }

            tableView.getItems().addAll(adresy);

        } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    @FXML
    private void filtruj(ActionEvent event
    ) {
        try {
            if (tfUlice.getText() == "") {
                tfUlice.setText(null);
            }
            if (tfCisloPop.getText() == "") {
                tfCisloPop.setText(null);
            }
            if (tfPSC.getText() == "") {
                tfPSC.setText(null);
            }
            if (tfObec.getText() == "") {
                tfObec.setText(null);
            }

            adresy.clear();
            tableView.getItems().clear();
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_ADRESY_SEARCH.PRO_RETURN_ADRESY(?,?,?,?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("novaUlice", tfUlice.getText());
            cs.setString("noveCislo", tfCisloPop.getText());
            cs.setString("novePSC", tfPSC.getText());
            cs.setString("novaObec", tfObec.getText());
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");
            while (result.next()) {
                Adresa adresa = new Adresa(result.getInt("ID_ADRESA"), result.getString("ULICE"), result.getString("CISLO_POPISNE"),
                        result.getString("PSC"), result.getString("OBEC"));
                adresy.add(adresa);
            }
            tableView.getItems().addAll(adresy);
        } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    @FXML
    private void zobrazVse(ActionEvent event
    ) {
        tableView.getItems().clear();
        loadData();
    }
}
