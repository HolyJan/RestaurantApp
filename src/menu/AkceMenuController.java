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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class AkceMenuController implements Initializable {

    @FXML
    private TextField nazevText;
    private int idMenu = -1;
    DatabaseConnection connection;
    private DatePicker datumPicker;
    @FXML
    private Button potvrditBut;
    private Menu editMenu;
    private ObservableList<Menu> menu;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setData(Menu menu) {
        this.nazevText.setText(menu.getNazev());
        this.idMenu = menu.getId();
        this.editMenu = menu;
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        try {
            if (!"".equals(nazevText.getText())) {
                Statement statement = connection.createBlockedStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM menu_view WHERE nazev='" + nazevText.getText() + "'");
                if (!result.next() || nazevText.getText().equals(this.editMenu.getNazev())) {
                    if (idMenu == -1) {
                        CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozMenuProc(?)}");
                        cstmt.setString(1, nazevText.getText());
                        cstmt.execute();
                        result = statement.executeQuery("SELECT menu_id_menu_seq.currval as id FROM dual");
                        result.next();
                        menu.add(new Menu(result.getInt("id"), new java.sql.Date(System.currentTimeMillis()), nazevText.getText()));
                        Date Date;
                    } else {
                        CallableStatement cstmt = connection.getConnection().prepareCall("{call updateMenuProc(?,?)}");
                        cstmt.setInt(1, idMenu);
                        cstmt.setString(2, nazevText.getText());
                        cstmt.execute();
                        editMenu.setDatum(new java.sql.Date(System.currentTimeMillis()));
                        editMenu.setNazev(nazevText.getText());
                    }
                    Stage stage = (Stage) potvrditBut.getScene().getWindow();
                    stage.close();
                } else {
                    showError("Tento název má jíž jiný recept. Zvolte jiný!");
                    throw new SQLException();
                }
            } else {
                MainSceneController.showError("Vyplňte název!");
            }
        } catch (SQLException e) {
                MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    void setMenu(ObservableList<Menu> menu) {
        this.menu = menu;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
