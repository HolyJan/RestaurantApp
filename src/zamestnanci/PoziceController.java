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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import menu.Recept;
import oracle.jdbc.OracleTypes;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class PoziceController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Pozice> tableView;
    @FXML
    private TableColumn<Pozice, String> poziceCol;
    private boolean init;
    private boolean edit = false;
    private DatabaseConnection connection;
    ObservableList<Pozice> pozice = FXCollections.observableArrayList();
    @FXML
    private TextField tfPozice;
    @FXML
    private Button odebratBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        if (MainSceneController.roleId.get() == 2) {
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
        poziceCol.setCellValueFactory(new PropertyValueFactory<>("nazev"));
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    private void loadData() {
        pozice.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM pozice_view");
            while (result.next()) {
                pozice.add(new Pozice(result.getInt("ID_POZICE"), result.getString("NAZEV")));

            }
            tableView.getItems().addAll(pozice);

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
        AkcePoziceController controllerAkcePozice = loader.getController();
        controllerAkcePozice.setConnection(connection);
        if (edit) {
            Pozice pozice = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkcePozice.setData(pozice.getIdPozice(),
                        pozice.getNazev());
            } catch (Exception e) {
                MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
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
        openANewView(event, "zamestnanci/AkcePozice.fxml", connection);
        loadData();
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        if (tableView.getSelectionModel().selectedItemProperty().get() == null) {
            MainSceneController.showDialog("Vyberte polo??ku, kterou chcete poupravit!");;
        } else {
            openANewView(event, "zamestnanci/AkcePozice.fxml", connection);
        }
        loadData();
    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Pozice pozice = tableView.getSelectionModel().getSelectedItem();
            CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberPoziciProc(?)}");
            cstmt.setInt(1, pozice.getIdPozice());
            cstmt.execute();
            loadData();
        } else {
            MainSceneController.showDialog("Nen?? vybr??n prvek pro odebr??n??");
        }
    }

    @FXML
    private void filtruj(ActionEvent event) {
        try {
            if (tfPozice.getText() == "") {
                tfPozice.setText(null);
            }

            pozice.clear();
            tableView.getItems().clear();
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_POZICE_SEARCH.PRO_RETURN_POZICE(?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("novyNazev", tfPozice.getText());
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");
            while (result.next()) {
                pozice.add(new Pozice(result.getInt("ID_POZICE"), result.getString("NAZEV")));
            }
            tableView.getItems().addAll(pozice);
        } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        loadData();
        tfPozice.setText("");
    }

}
