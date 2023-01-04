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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import login.LoginController;
import menu.PolozkyController;
import menu.ReceptyController;
import objednavky.ObjednavkyController;
import objednavky.PlatbyController;
import obrazky.ObrazkyController;
import rezervace.RezervaceController;
import rezervace.StolyController;
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
    public static StringProperty userName;
    public static StringProperty roleName;
    public static boolean emulation = false;
    public static Role role;
    @FXML
    private Button logOut;
    @FXML
    private Label loginLabel;
    @FXML
    private Button zakazniciBtn;
    @FXML
    private Button zamestnanciBtn;
    @FXML
    private Button objednavkyBtn;
    @FXML
    private Button smenyBtn;
    @FXML
    private Button menuBtn;
    @FXML
    private Button rezervaceBtn;
    @FXML
    private Button platbyBtn;
    @FXML
    private Button receptyBtn;
    @FXML
    private Button uzivateleBtn;
    @FXML
    private Button obrazkyBtn;
    @FXML
    private Button aktivitaBtn;
    @FXML
    private Button adresyBtn;
    @FXML
    private Button poziceBtn;
    @FXML
    private Button stolyBtn;
    @FXML
    private Button emulationBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginLabel.setText("Neregistrovaný");
        loggedIn = new SimpleBooleanProperty(false);
        roleId = new SimpleIntegerProperty();
        userName = new SimpleStringProperty();
        roleName = new SimpleStringProperty();
        roleName.set("Neregistrovaný");
        roleId.set(0);
        setVisible();
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
                        roleName.set("Admin");
                        setVisible();
                        loginBtn.setVisible(false);
                        logOut.setVisible(true);
                        break;
                    case NEREGISTROVANY:
                        loginLabel.setText("Neregistrovaný");
                        roleName.set("Neregistrovany");
                        setVisible();
                        loginBtn.setVisible(true);
                        logOut.setVisible(false);
                        break;
                    case ZAKAZNIK:
                        loginLabel.setText("Zákazník");
                        roleName.set("Zakaznik");
                        setVisible();
                        loginBtn.setVisible(false);
                        logOut.setVisible(true);
                        break;
                    case ZAMESTNANEC:
                        loginLabel.setText("Zaměstnanec");
                        roleName.set("Zamestnanec");
                        setVisible();
                        loginBtn.setVisible(false);
                        logOut.setVisible(true);
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
            case "rezervace/Stoly.fxml":
                StolyController controllerStoly = loader.getController();
                controllerStoly.setConnection(connection);
                break;
            case "rezervace/Rezervace.fxml":
                RezervaceController controllerRezervace = loader.getController();
                controllerRezervace.setConnection(connection);
                break;
            case "objednavky/Platby.fxml":
                PlatbyController controllerPlatby = loader.getController();
                controllerPlatby.setConnection(connection);
                break;
            case "menu/Recepty.fxml":
                ReceptyController controllerRecepty = loader.getController();
                controllerRecepty.setConnection(connection);
                break;
            case "obrazky/Obrazky.fxml":
                ObrazkyController controllerObrazky = loader.getController();
                controllerObrazky.setConnection(connection);
                break;
        }
    }

    @FXML
    private void logoutOnAc(ActionEvent event) {
        loginBtn.setVisible(true);
        logOut.setVisible(false);
        roleId.set(0);
        roleName.set("Neregistrovaný");
        emulation = false;
        setVisible();
    }

    @FXML
    private void uzivateleButAction(ActionEvent event) throws IOException {
        openANewView(event, "uzivatele/Uzivatele.fxml", connection);
    }

    @FXML
    private void obrazkyButAction(ActionEvent event) throws IOException {
        openANewView(event, "obrazky/Obrazky.fxml", connection);
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
    private void stolyButAction(ActionEvent event) throws IOException {
        openANewView(event, "rezervace/Stoly.fxml", connection);
    }

    @FXML
    private void rezervaceButAction(ActionEvent event) throws IOException {
        openANewView(event, "rezervace/Rezervace.fxml", connection);
    }

    @FXML
    private void platbyButAction(ActionEvent event) throws IOException {
        openANewView(event, "objednavky/Platby.fxml", connection);
    }

    @FXML
    private void receptButAction(ActionEvent event) throws IOException {
        openANewView(event, "menu/Recepty.fxml", connection);
    }

    public void aktivita(DatabaseConnection connection, String username, String tabulka, String akce, Date datum) throws SQLException {
        PreparedStatement pstmt = connection.getConnection().prepareStatement("{call vlozAktivituProc(?,?,?,?)}");
        if (username == null) {
            username = "Neregistrovany";
        }
        pstmt.setString(1, username);
        pstmt.setString(2, tabulka);
        pstmt.setString(3, akce);
        pstmt.setDate(4, datum);
        pstmt.execute();
    }

    private void setVisible() {
        switch (roleId.get()) {
            case 3:
                zakazniciBtn.setVisible(true);
                zamestnanciBtn.setVisible(true);
                objednavkyBtn.setVisible(true);
                smenyBtn.setVisible(true);
                menuBtn.setVisible(true);
                rezervaceBtn.setVisible(true);
                platbyBtn.setVisible(true);
                receptyBtn.setVisible(true);
                uzivateleBtn.setVisible(true);
                obrazkyBtn.setVisible(true);
                aktivitaBtn.setVisible(true);
                adresyBtn.setVisible(true);
                poziceBtn.setVisible(true);
                stolyBtn.setVisible(true);
                if (emulation) {
                    emulationBtn.setVisible(true);
                } else {
                    emulationBtn.setVisible(false);
                }
                break;
            case 0:
                zakazniciBtn.setVisible(false);
                zamestnanciBtn.setVisible(false);
                objednavkyBtn.setVisible(false);
                smenyBtn.setVisible(false);
                menuBtn.setVisible(true);
                rezervaceBtn.setVisible(false);
                platbyBtn.setVisible(false);
                receptyBtn.setVisible(false);
                uzivateleBtn.setVisible(false);
                obrazkyBtn.setVisible(false);
                aktivitaBtn.setVisible(false);
                adresyBtn.setVisible(false);
                poziceBtn.setVisible(false);
                stolyBtn.setVisible(false);
                if (emulation) {
                    emulationBtn.setVisible(true);
                } else {
                    emulationBtn.setVisible(false);
                }
                break;
            case 1:
                zakazniciBtn.setVisible(false);
                zamestnanciBtn.setVisible(false);
                objednavkyBtn.setVisible(true);
                smenyBtn.setVisible(false);
                menuBtn.setVisible(true);
                rezervaceBtn.setVisible(true);
                platbyBtn.setVisible(true);
                receptyBtn.setVisible(false);
                uzivateleBtn.setVisible(false);
                obrazkyBtn.setVisible(false);
                aktivitaBtn.setVisible(false);
                adresyBtn.setVisible(true);
                poziceBtn.setVisible(false);
                stolyBtn.setVisible(true);
                if (emulation) {
                    emulationBtn.setVisible(true);
                } else {
                    emulationBtn.setVisible(false);
                }
                break;
            case 2:
                zakazniciBtn.setVisible(true);
                zamestnanciBtn.setVisible(false);
                objednavkyBtn.setVisible(true);
                smenyBtn.setVisible(false);
                menuBtn.setVisible(true);
                rezervaceBtn.setVisible(true);
                platbyBtn.setVisible(true);
                receptyBtn.setVisible(true);
                uzivateleBtn.setVisible(false);
                obrazkyBtn.setVisible(true);
                aktivitaBtn.setVisible(false);
                adresyBtn.setVisible(true);
                poziceBtn.setVisible(false);
                stolyBtn.setVisible(true);
                if (emulation) {
                    emulationBtn.setVisible(true);
                } else {
                    emulationBtn.setVisible(false);
                }
                break;
        }
    }

    @FXML
    private void emulationOnAc(ActionEvent event) {
        emulation = false;
        roleId.set(3);
        emulationBtn.setVisible(false);
    }

    public static void showDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("POZOR");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
