/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import connection.DatabaseConnection;
import databaseapplication.DatabaseApplication;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class AkceObrazekController implements Initializable {

    @FXML
    private TextField nazevText;
    DatabaseConnection connection;
    private String nazevObrazku;
    private String umisteni = "";
    private InputStream blob;
    private InputStream newImage;
    private String pripona;
    private Label nacteniLabel;
    @FXML
    private Button potvrditBut;
    private Obrazek obrazek;
    private ObservableList<Obrazek> obrazky;
    @FXML
    private ImageView imageView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void nacistObrazAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Výběr obrázku");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files","*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(DatabaseApplication.mainStage);
        if (selectedFile != null) {
            String path = selectedFile.toString();
            System.out.println(path);
            int index = path.lastIndexOf('.');
            String extension = "";
            if (index > 0) {
                extension = path.substring(index + 1);
            }
            try {
                InputStream image = new FileInputStream(path);
                blob = new FileInputStream(path);
                newImage = new FileInputStream(path);
                umisteni = path;
                pripona = extension;
                if (!"".equals(umisteni)) {
                    imageView.setImage(new Image(image));
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    @FXML
    private void potvrditAction(ActionEvent event) throws SQLException {
        try {

            if (umisteni != null && !"".equals(umisteni) && !"".equals(nazevObrazku)) {
                Statement statement = connection.createBlockedStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM obrazky_menu_view WHERE nazev='" + nazevText.getText() + "'");
                if (!result.next()) {
                    nazevObrazku = nazevText.getText();
                } else {
                    showError("Tento název má jíž jiný obrázek. Zvolte jiný!");
                    throw new SQLException();
                }
                PreparedStatement pstmt = connection.getConnection().prepareStatement("{call vlozObrazekProc(?,?,?,?)}");
                pstmt.setBlob(1, blob);
                pstmt.setString(2, umisteni);
                pstmt.setString(3, pripona);
                pstmt.setString(4, nazevObrazku);
                pstmt.execute();
                result = statement.executeQuery("SELECT obrazky_id_obrazku_seq.currval as id FROM dual");
                result.next();
                System.out.println(result.getInt("id"));
                this.obrazek = new Obrazek(result.getInt("id"), nazevObrazku, new Image(newImage));
                obrazky.add(obrazek);
                Stage stage = (Stage) potvrditBut.getScene().getWindow();
                    stage.close();
            } else {
                showError("Obrázek není načtený nebo název není vyplněn!");
                throw new SQLException();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    public void setObrazky(ObservableList<Obrazek> obrazky) {
        this.obrazky = obrazky;
    }
    
    

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    
    

}
