/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AkcePoziceController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TextField poziceText;
    DatabaseConnection connection;
    boolean init;
    int idPozice = -1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void potvrditAction(ActionEvent event) throws Exception {
        try {
            if (poziceText.getText() != null) {
                Statement statement = connection.createBlockedStatement();
                if (idPozice == -1) {
                    ResultSet result = statement.executeQuery("SELECT * FROM pozice_view WHERE nazev ='" + poziceText.getText() + "'");
                    if (result.next()) {
                        throw new Exception("Tato pozice je jíž v tabulce!");
                    }
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozPoziciProc(?)}");
                    cstmt.setString(1, poziceText.getText());
                    cstmt.execute();
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updatePoziciProc(?,?)}");
                    cstmt.setInt(1, idPozice);
                    cstmt.setString(2, poziceText.getText());
                    cstmt.execute();
                }
                Stage stage = (Stage) poziceText.getScene().getWindow();
                stage.close();
            } else {
                throw new SQLException();
            }

        } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    public void setData(int idPozice, String pozice) {
        this.idPozice = idPozice;
        poziceText.setText(pozice);

    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

}
