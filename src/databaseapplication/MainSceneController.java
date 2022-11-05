/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseapplication;

import connection.DatabaseConnection;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import login.LoginController;
import zakaznici.ZakazniciController;
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
    @FXML
    private Button logOut;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loggedIn = new SimpleBooleanProperty(false);
        loggedIn.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    loginBtn.setVisible(false);
                    logOut.setVisible(true);
                } else {
                    loginBtn.setVisible(true);
                    logOut.setVisible(false);
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
        switch(fileLocation){
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
                objednavky.ObjednavkyController controllerObjednavky = loader.getController();
                controllerObjednavky.setConnection(connection);
                break;
        }
    }

    @FXML
    private void logoutOnAc(ActionEvent event) {
    }

    

    

}
