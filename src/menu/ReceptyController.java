/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
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
import javafx.scene.control.Button;
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
public class ReceptyController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Recept> tableView;
    @FXML
    private TableColumn<Recept, String> receptCol;
    private boolean init;
    private boolean edit = false;
    private DatabaseConnection connection;
    ObservableList<Recept> recepty = FXCollections.observableArrayList();
    @FXML
    private TextField tfRecept;
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
        pane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!init) {
                    loadData();
                    init = true;
                }
            }
        });
        receptCol.setCellValueFactory(new PropertyValueFactory<>("nazev"));
    }

    private void loadData() {
        recepty.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM recepty_view");
            while (result.next()) {
                recepty.add(new Recept(result.getInt("ID_RECEPTU"), result.getString("NAZEV")));

            }
            tableView.getItems().addAll(recepty);

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
        AkceReceptController controllerAkceRecept = loader.getController();
        controllerAkceRecept.setConnection(connection);
        if (edit) {
            Recept recept = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkceRecept.setData(recept);
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
        openANewView(event, "menu/AkceRecept.fxml", connection);
        loadData();

    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        if (tableView.getSelectionModel().selectedItemProperty().get() == null) {
            MainSceneController.showDialog("Vyberte položku, kterou chcete poupravit!");;
        } else {
            openANewView(event, "menu/AkceRecept.fxml", connection);
        }

    }

    @FXML
    private void odebratAction(ActionEvent event) throws SQLException {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Recept recept = tableView.getSelectionModel().getSelectedItem();
            CallableStatement cstmt = connection.getConnection().prepareCall("{call odeberReceptProc(?)}");
            cstmt.setInt(1, recept.getId());
            cstmt.execute();
            loadData();
        } else {
            MainSceneController.showDialog("Není vybrán prvek pro odebrání");
        }

    }

    @FXML
    private void filtruj(ActionEvent event) {
        try {
            if (tfRecept.getText() == "") {
                tfRecept.setText(null);
            }

            recepty.clear();
            tableView.getItems().clear();
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_REZEPTY_SEARCH.PRO_RETURN_RECEPTY(?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("novyNazev", tfRecept.getText());
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");
            while (result.next()) {

                recepty.add(new Recept(result.getInt("ID_RECEPTU"), result.getString("NAZEV")));

            }
            tableView.getItems().addAll(recepty);
        } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        loadData();
        tfRecept.setText("");

    }

}
