/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import connection.DatabaseConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import databaseapplication.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class LoginController implements Initializable {

    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;

    DatabaseConnection connection;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO 
    }

    @FXML
    private void loginButAction(ActionEvent event) throws SQLException {
        if(!"".equals(usernameTextField.getText()) && !"".equals(passwordTextField.getText())){
            Statement statement = connection.createBlockedStatement();
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(passwordTextField.getText().getBytes());
                String hashedPassword = DatatypeConverter.printHexBinary(md.digest());
                ResultSet result = statement.executeQuery("SELECT jmeno,prijmeni,login,id_role, telefon FROM uzivatele WHERE "
                        + "login=" + "'" + usernameTextField.getText() + "'AND heslo=" + "'" + hashedPassword + "'");
                if (!result.next()) {
                    showError("Chyba p??ihl????en??. Login nebo heslo je ??patn??!");
                }else{
                    MainSceneController.userName.set(result.getString("LOGIN"));
                    MainSceneController.roleId.set(result.getInt("ID_ROLE"));
                    MainSceneController.jmenoName.set(result.getString("JMENO"));
                    MainSceneController.prijmeniName.set(result.getString("PRIJMENI"));
                    MainSceneController.telefon.set(result.getString("TELEFON"));
                    MainSceneController.loggedIn.set(true);
                    Stage stage = (Stage) passwordTextField.getScene().getWindow();
                    stage.close();
                }
            }catch(SQLException | NoSuchAlgorithmException e){ 
                MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }
        }else{
            showError("Chyba p??ihl????en??. Vypl??te v??echna pole!");
        }
    }

    @FXML
    private void registrationButAction(ActionEvent event) throws IOException {
        openANewView(event, "login/Registrace.fxml", connection);
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
        RegistraceController controllerRegistrace = loader.getController();
        controllerRegistrace.setConnection(connection);
    }
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setConnection(DatabaseConnection connection) {
        this.connection = connection;
    }

   

}
