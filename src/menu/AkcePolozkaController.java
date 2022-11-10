/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class AkcePolozkaController implements Initializable {

    @FXML
    private TextField nazevText;
    @FXML
    private TextField cenaText;
    @FXML
    private ComboBox<String> receptCombo;
    @FXML
    private ComboBox<String> menuCombo;
    @FXML
    private ComboBox<Obrazek> obrazekCombo;
    int idPolozky = -1;
    int idObrazku;
    int idReceptu;
    int idMenu;
    DatabaseConnection connection;
    @FXML
    private Button obrazekBut;
    @FXML
    private Button menuBut;
    @FXML
    private Button receptBut;
    boolean init;
    private Polozka polozka;

    ObservableList<String> recepty = FXCollections.observableArrayList();
    ObservableList<String> menu = FXCollections.observableArrayList();
    ObservableList<String> obrazky = FXCollections.observableArrayList();
    ObservableList<Obrazek> obrazkyList;
    @FXML
    private AnchorPane pane;
    @FXML
    private Button potvrditBut;

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
                    pripareCombos();
                    init = true;
                }
            }
        });
    }

    public void setData(Polozka polozka) {
        this.polozka = polozka;
        this.idPolozky = polozka.getIdPolozky();
        this.idObrazku = polozka.getObrazek().getIdObrazku();
        nazevText.setText(polozka.getNazevPolozky());
        cenaText.setText(Integer.toString(polozka.getCenaPolozky()));
        this.idReceptu = polozka.getRecept().getId();
        receptCombo.setValue(polozka.getRecept().getNazev());
        this.idMenu = polozka.getMenu().getId();
        menuCombo.setValue(polozka.getMenu().getNazev());
        obrazekCombo.setValue(polozka.getObrazek());
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
        switch (fileLocation) {
            case "menu/AkceMenu.fxml":
                AkceMenuController controllerMenu = loader.getController();
                controllerMenu.setConnection(connection);
                break;
            case "menu/AkceObrazek.fxml":
                AkceObrazekController controllerObrazek = loader.getController();
                controllerObrazek.setConnection(connection);
                controllerObrazek.setObrazky(obrazkyList);
                break;
            case "menu/AkceRecept.fxml":
                AkceReceptController controllerRecept = loader.getController();
                controllerRecept.setConnection(connection);
                break;
        }

    }

    private void pripareCombos() {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM RECEPTY_VIEW");
            while (result1.next()) {
                recepty.add(result1.getString("NAZEV"));
            }
            ResultSet result2 = statement.executeQuery("SELECT * FROM MENU_VIEW");
            while (result2.next()) {
                menu.add(result2.getString("NAZEV"));
            }

            for(Obrazek o : obrazkyList) {
                obrazky.add(o.getNazev());
            }
            receptCombo.setItems(recepty);
            menuCombo.setItems(menu);
            obrazekCombo.setItems(obrazkyList);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        if (!"".equals(nazevText.getText()) && !"".equals(cenaText.getText())
                && !"".equals(receptCombo.getValue()) && !"".equals(menuCombo.getValue())
                && !"".equals(obrazekCombo.getValue())) {
            Statement statement = connection.createBlockedStatement();
            try {
                ResultSet result = statement.executeQuery("SELECT * FROM menu_view"
                        + " WHERE nazev='" + menuCombo.getValue() + "'");
                result.next();
                idMenu = result.getInt("ID_MENU");
                ResultSet result1 = statement.executeQuery("SELECT * FROM recepty_view"
                        + " WHERE nazev='" + receptCombo.getValue() + "'");
                result1.next();
                idReceptu = result1.getInt("ID_RECEPTU");

                Obrazek novyobrazek = obrazekCombo.getValue();
                idObrazku = obrazekCombo.getValue().getIdObrazku();
                if (idPolozky == -1) {

                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozPolozky_menuProc(?,?,?,?,?)}");
                    cstmt.setString(1, nazevText.getText());
                    cstmt.setInt(2, Integer.parseInt(cenaText.getText()));
                    cstmt.setInt(3, idMenu);
                    cstmt.setInt(4, idReceptu);
                    cstmt.setInt(5, idObrazku);
                    cstmt.execute();
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updatePolozky_menuProc(?,?,?,?,?,?)}");
                    cstmt.setInt(1, idPolozky);
                    cstmt.setString(2, nazevText.getText());
                    cstmt.setInt(3, Integer.parseInt(cenaText.getText()));
                    cstmt.setInt(4, idReceptu);
                    cstmt.setInt(5, idMenu);
                    cstmt.setInt(6, idObrazku);
                    cstmt.execute();
                    polozka.setObrazek(novyobrazek);
                }
                Stage stage = (Stage) potvrditBut.getScene().getWindow();
                stage.close();
            } catch (NumberFormatException | SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    private void pridatObrazekAction(ActionEvent event) throws IOException {
        openANewView(event, "menu/AkceObrazek.fxml", connection);
    }

    @FXML
    private void pridatMenuAction(ActionEvent event) throws IOException {
        openANewView(event, "menu/AkceMenu.fxml", connection);
    }

    @FXML
    private void pridatReceptAction(ActionEvent event) throws IOException {
        openANewView(event, "menu/AkceRecept.fxml", connection);
    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    public void setObrazkyList(ObservableList<Obrazek> obrazkyList) {
        this.obrazkyList = obrazkyList;
    }

}
