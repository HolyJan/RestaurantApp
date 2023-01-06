/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obrazky;

import connection.DatabaseConnection;
import databaseapplication.DatabaseApplication;
import databaseapplication.MainSceneController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.Date;
import java.sql.PreparedStatement;
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
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import zakaznici.Adresa;
import zakaznici.Zakaznik;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AkceObrazkyController implements Initializable {

    DatabaseConnection connection;
    boolean init;
    private int idObrazku = -1;
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField nazevText;
    @FXML
    private TextField umisteniText;

    ObservableList<String> pripony = FXCollections.observableArrayList();
    @FXML
    private TextField typText;
    @FXML
    private TextField priponaText;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        priponaText.setDisable(true);
        umisteniText.setDisable(true);
        typText.setDisable(true);
        nazevText.setEditable(true);
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

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    void setData(Obrazek obrazek) {
        idObrazku = obrazek.getIdObrazku();
        priponaText.setText(obrazek.getPripona());
        umisteniText.setText(obrazek.getUmisteni());
        typText.setText(obrazek.getTyp().toString());
        nazevText.setText(obrazek.getNazev());

    }

    private void pripareCombos() {
        Statement statement = connection.createBlockedStatement();
        pripony.add("jpg");
        pripony.add("png");
    }

    @FXML
    private void potvrditAction(ActionEvent event) throws FileNotFoundException {
        try {
            if (!"".equals(nazevText.getText()) && !"".equals(umisteniText.getText())) {
                Statement statement = connection.createBlockedStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM obrazky_menu_view WHERE nazev='" + nazevText.getText() + "'");
                if (!result.next()) {
                    if (idObrazku == -1) {
                        PreparedStatement pstmt = connection.getConnection().prepareStatement("{call vlozObrazekProc(?,?,?,?)}");
                        pstmt.setBlob(1, new FileInputStream(umisteniText.getText()));
                        pstmt.setString(2, umisteniText.getText());
                        pstmt.setString(3, priponaText.getText());
                        pstmt.setString(4, nazevText.getText());
                        pstmt.execute();
                        result = statement.executeQuery("SELECT obrazky_id_obrazku_seq.currval as id FROM dual");
                        result.next();
                    } else {
                        PreparedStatement pstmt = connection.getConnection().prepareStatement("{call updateObrazekProc(?,?,?,?,?)}");
                        pstmt.setInt(1, idObrazku);
                        pstmt.setBlob(2, new FileInputStream(umisteniText.getText()));
                        pstmt.setString(3, umisteniText.getText());
                        pstmt.setString(4, priponaText.getText());
                        pstmt.setString(5, nazevText.getText());
                        pstmt.execute();
                    }

                } else {
                    throw new SQLException("Tento název má jíž jiný obrázek. Zvolte jiný!");
                }
                Stage stage = (Stage) nazevText.getScene().getWindow();
                stage.close();
            } else {
                throw new SQLException("Obrázek není načtený nebo název není vyplněn!");
            }
        } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }

    }

    @FXML
    private void nacistAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Výběr obrázku");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(DatabaseApplication.mainStage);
        if (selectedFile != null) {
            String path = selectedFile.toString();
            int index = path.lastIndexOf('.');
            String extension = "";
            if (index > 0) {
                extension = path.substring(index + 1);
            }
            try {
                InputStream image = new FileInputStream(path);
                typText.setText(new FileInputStream(path).toString());
                umisteniText.setText(path);
                priponaText.setText(extension);
            } catch (Exception e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }

        }
    }

}
