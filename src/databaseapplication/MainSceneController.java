/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseapplication;

import aktivity.AktivityController;
import connection.DatabaseConnection;
import enums.Role;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import login.LoginController;
import menu.PolozkyController;
import objednavky.ObjednavkyController;
import uzivatele.UzivateleController;
import zakaznici.AdresyController;
import zakaznici.ZakazniciController;
import zamestnanci.PoziceController;
import zamestnanci.SmenyController;
import zamestnanci.ZamestnanciController;

/**
 * FXML Controller class
 *
 * @author ondra
 */
public class MainSceneController implements Initializable {

    DatabaseConnection connection;
    @FXML
    private Button loginBtn;
    public static BooleanProperty loggedIn;
    public static IntegerProperty roleId;
    public static String userName;
    public static Role role;
    @FXML
    private Button logOut;
    @FXML
    private Label loginLabel;
    @FXML
    private Button menu;
    @FXML
    private Button menu1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginLabel.setText("Neregistrovaný");
        loggedIn = new SimpleBooleanProperty(false);
        roleId = new SimpleIntegerProperty();
        loggedIn.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    loginBtn.setVisible(false);
                    logOut.setVisible(true);
                } else {
                    loginBtn.setVisible(true);
                    logOut.setVisible(false);
                }
            }
        });
        roleId.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                role = Role.valueOf((int) newValue);
                System.out.println(role);
                switch (role) {
                    case ADMIN:
                        loginLabel.setText("Admin");
                        break;
                    case NEREGISTROVANY:
                        loginLabel.setText("Neregistrovaný");
                        break;
                    case ZAKAZNIK:
                        loginLabel.setText("Zákazník");
                        break;
                    case ZAMESTNANEC:
                        loginLabel.setText("Zaměstnanec");
                        break;
                }
            }
        });
        try {
            connection = new DatabaseConnection("ST60990", "60990");
            System.out.println("Přihlášení proběhlo úspěšně");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void loginButAction(ActionEvent event) throws IOException {
        openANewView(event, "login/Login.fxml", connection);
    }

    @FXML
    private void zakazaniciButAction(ActionEvent event) throws IOException {
        openANewView(event, "zakaznici/Zakaznici.fxml", connection);
    }

    @FXML
    private void zamestnanciButAction(ActionEvent event) throws IOException {
        openANewView(event, "zamestnanci/Zamestnanci.fxml", connection);
    }

    @FXML
    private void objednavkyButAction(ActionEvent event) throws IOException {
        openANewView(event, "objednavky/Objednavky.fxml", connection);
    }

    @FXML
    private void smenyButAction(ActionEvent event) throws IOException {
        openANewView(event, "zamestnanci/Smeny.fxml", connection);
    }

    @FXML
    private void menuAction(ActionEvent event) throws IOException {
        openANewView(event, "menu/Polozky.fxml", connection);
    }

    public void openANewView(ActionEvent event, String fileLocation, DatabaseConnection conn) throws IOException {
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

    public void sendDataViaController(String fileLocation, FXMLLoader loader) {
        loader.setLocation(getClass().getResource(fileLocation));
        switch (fileLocation) {
            case "zakaznici/Zakaznici.fxml":
                ZakazniciController controllerZakaznici = loader.getController();
                controllerZakaznici.setConnection(connection);
                break;
            case "zamestnanci/Zamestnanci.fxml":
                ZamestnanciController controllerZamestnanci = loader.getController();
                controllerZamestnanci.setConnection(connection);
                break;
            case "login/Login.fxml":
                LoginController controllerLogin = loader.getController();
                controllerLogin.setConnection(connection);
                break;
            case "objednavky/Objednavky.fxml":
                ObjednavkyController controllerObjednavky = loader.getController();
                controllerObjednavky.setConnection(connection);
                break;
            case "zamestnanci/Smeny.fxml":
                SmenyController controllerSmeny = loader.getController();
                controllerSmeny.setConnection(connection);
                break;
            case "menu/Polozky.fxml":
                PolozkyController controllerPolozky = loader.getController();
                controllerPolozky.setConnection(connection);
                break;
            case "uzivatele/Uzivatele.fxml":
                UzivateleController controllerUzivatele = loader.getController();
                controllerUzivatele.setConnection(connection);
                break;
            case "aktivity/Aktivity.fxml":
                AktivityController controllerAktivity = loader.getController();
                controllerAktivity.setConnection(connection);
                break;
            case "zakaznici/Adresy.fxml":
                AdresyController controllerAdresy = loader.getController();
                controllerAdresy.setConnection(connection);
                break;
            case "zamestnanci/Pozice.fxml":
                PoziceController controllerPozice = loader.getController();
                controllerPozice.setConnection(connection);
                break;
        }
    }

    @FXML
    private void logoutOnAc(ActionEvent event) {
        loginBtn.setVisible(true);
        logOut.setVisible(false);
        roleId.set(0);
    }

    @FXML
    private void uzivateleButAction(ActionEvent event) throws IOException {
        openANewView(event, "uzivatele/Uzivatele.fxml", connection);
    }

    @FXML
    private void obrazkyButAction(ActionEvent event) {
        
    }

    @FXML
    private void aktivityButAction(ActionEvent event) throws IOException {
        openANewView(event, "aktivity/Aktivity.fxml", connection);
    }

    @FXML
    private void adresyButAction(ActionEvent event) throws IOException {
        openANewView(event, "zakaznici/Adresy.fxml", connection);
    }

    @FXML
    private void poziceButAction(ActionEvent event) throws IOException {
        openANewView(event, "zamestnanci/Pozice.fxml", connection);
    }

    @FXML
    private void stolyButAction(ActionEvent event) {
        
    }

    @FXML
    private void polozkyMenuButAction(ActionEvent event) {
        
    }

}
