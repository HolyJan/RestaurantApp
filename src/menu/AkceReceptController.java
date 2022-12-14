/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class AkceReceptController implements Initializable {

    @FXML
    private TextField nazevText;
    DatabaseConnection connection;
    @FXML
    private Button potvrditBut;
    private ObservableList<Recept> recepty;
    private int idReceptu = -1;
    private Recept recept;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setData(Recept recept) {
        this.idReceptu = recept.getId();
        this.recept = recept;
        this.nazevText.setText(recept.getNazev());
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        try {
            if (!"".equals(nazevText.getText())) {
                Statement statement = connection.createBlockedStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM recepty_view WHERE nazev='" + nazevText.getText() + "'");
                if (!result.next() || nazevText.getText().equals(recept.getNazev())) {
                    if (idReceptu == -1) {
                        CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozReceptProc(?)}");
                        cstmt.setString(1, nazevText.getText());
                        cstmt.execute();
                    } else {
                        CallableStatement cstmt = connection.getConnection().prepareCall("{call updateReceptProc(?,?)}");
                        cstmt.setInt(1, idReceptu);
                        cstmt.setString(2, nazevText.getText());
                        cstmt.execute();
                    }
                    Stage stage = (Stage) potvrditBut.getScene().getWindow();
                    stage.close();
                } else {
                    MainSceneController.showError("Tento n??zev m?? j???? jin?? recept. Zvolte jin??!");
                    throw new SQLException();
                }
            } else {
                MainSceneController.showError("Vypl??te n??zev!");
                throw new SQLException();
            }
        } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    public void setRecepty(ObservableList<Recept> recepty) {
        this.recepty = recepty;
    }


}
