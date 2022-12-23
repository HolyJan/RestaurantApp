/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rezervace;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uzivatele.Role;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AkceStolyController implements Initializable {

    @FXML
    private AnchorPane pane;
    private int idStolu = -1;
    @FXML
    private TextField cisloStoluTextField;
    @FXML
    private TextField pocetMistTextField;
    DatabaseConnection connection;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    void setData(int idStolu, int cisloStolu, int pocetMist) {
        this.idStolu = idStolu;
        this.cisloStoluTextField.setText(Integer.toString(cisloStolu));
        this.pocetMistTextField.setText(Integer.toString(pocetMist));
    }

    @FXML
    private void PotvrditAction(ActionEvent event) {
        if (!"".equals(cisloStoluTextField.getText()) && !"".equals(pocetMistTextField.getText())) {
            Statement statement = connection.createBlockedStatement();
            try {
                if (this.idStolu != -1) {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updateStulProc(?,?,?)}");
                    cstmt.setInt(1, idStolu);
                    cstmt.setInt(2, Integer.parseInt(pocetMistTextField.getText()));
                    cstmt.setInt(3, Integer.parseInt(cisloStoluTextField.getText()));
                    cstmt.execute();
                    MainSceneController msc = new MainSceneController();
                    msc.aktivita(connection, MainSceneController.userName.get(), "STOLY", "UPDATE", new Date(System.currentTimeMillis()));

                    System.out.println("aktualizace OK");
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozStulProc(?,?)}");
                    cstmt.setInt(1, Integer.parseInt(pocetMistTextField.getText()));
                    cstmt.setInt(2, Integer.parseInt(cisloStoluTextField.getText()));
                    cstmt.execute();
                    MainSceneController msc = new MainSceneController();
                    msc.aktivita(connection, MainSceneController.userName.get(), "STOLY", "INSERT", new Date(System.currentTimeMillis()));

                }
                Stage stage = (Stage) cisloStoluTextField.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        }
    }

}
