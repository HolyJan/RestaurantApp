/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import connection.DatabaseConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class PolozkyController implements Initializable {

    @FXML
    private TableView<Polozka> tableView;
    @FXML
    private TableColumn<Polozka, String> nazevCol;
    @FXML
    private TableColumn<Polozka, String> cenaCol;
    @FXML
    private CheckBox checkVse;
    @FXML
    private CheckBox checkPolevky;
    @FXML
    private CheckBox checkHlavniJidla;
    @FXML
    private CheckBox checkDezerty;
    @FXML
    private CheckBox checkNapoje;
    boolean init;
    ObservableList<Polozka> polozkyVse = FXCollections.observableArrayList();
    ObservableList<Polozka> polozkySelected = FXCollections.observableArrayList();
    ObservableList<Obrazek> obrazky = FXCollections.observableArrayList();
    boolean edit = false;
    DatabaseConnection connection;
    @FXML
    private AnchorPane pane;
    @FXML
    private ImageView imageViewJidlo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;

        pane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!init) {
                    checkVse.setSelected(true);
                    loadData();
                    updateData();
                    init = true;
                }
            }
        });
        nazevCol.setCellValueFactory(new PropertyValueFactory<>("nazevPolozky"));
        cenaCol.setCellValueFactory(new PropertyValueFactory<>("cenaPolozky"));
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
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
        AkcePolozkaController controllerAkcePolozky = loader.getController();
        controllerAkcePolozky.setConnection(connection);
        if (edit) {
            Polozka polozka = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkcePolozky.setData(polozka.getIdPolozky(),
                        polozka.getNazevPolozky(), polozka.getCenaPolozky(),
                        polozka.getIdReceptu(), polozka.getNazevReceptu(),
                        polozka.getIdMenu(), polozka.getNazevMenu(),
                        polozka.getIdObrazku(), polozka.getNazevObrazku());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    private void loadData() {
        loadImages();
        polozkyVse.clear();
        Statement statement = connection.createBlockedStatement();
        try {

            ResultSet result = statement.executeQuery("SELECT * FROM polozky_menu_view");
            while (result.next()) {
                polozkyVse.add(new Polozka(result.getInt("ID_POLOZKY"), result.getString("NAZEV_POLOZKY"), result.getInt("CENA"),
                        result.getInt("ID_RECEPTU"), result.getString("NAZEV_RECEPTU"),
                        result.getInt("ID_MENU"), result.getString("NAZEV_MENU"), result.getInt("ID_OBRAZKU"), result.getString("NAZEV_OBRAZKU")));

            }

            ResultSet result2 = statement.executeQuery("SELECT * FROM obrazky_menu_view");
            while (result2.next()) {
                obrazky.add(new Obrazek(result2.getInt("ID_OBRAZKU"), result2.getString("NAZEV"), result2.getBlob("OBRAZEK")));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

    @FXML
    private void pridatAction(ActionEvent event) throws IOException {
        edit = false;
        openANewView(event, "menu/AkcePolozka.fxml", connection);
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        openANewView(event, "menu/AkcePolozka.fxml", connection);
    }

    @FXML
    private void odebratAction(ActionEvent event) {
    }

    @FXML
    private void checkVseAction(ActionEvent event) {
        if (checkVse.isSelected()) {
            checkDezerty.setSelected(true);
            checkHlavniJidla.setSelected(true);
            checkNapoje.setSelected(true);
            checkPolevky.setSelected(true);
        } else {
            checkDezerty.setSelected(false);
            checkHlavniJidla.setSelected(false);
            checkNapoje.setSelected(false);
            checkPolevky.setSelected(false);
        }
        updateData();
    }

    @FXML
    private void checkPolevkyAction(ActionEvent event) {
        if (!checkPolevky.isSelected() && checkVse.isSelected()) {
            checkVse.setSelected(false);
        }
        updateData();
    }

    @FXML
    private void checkHlavniJidlaAction(ActionEvent event) {
        if (!checkHlavniJidla.isSelected() && checkVse.isSelected()) {
            checkVse.setSelected(false);
        }
        updateData();
    }

    @FXML
    private void checkDezertyAction(ActionEvent event) {
        if (!checkDezerty.isSelected() && checkVse.isSelected()) {
            checkVse.setSelected(false);
        }
        updateData();
    }

    @FXML
    private void checkNapojeAction(ActionEvent event) {
        if (!checkNapoje.isSelected() && checkVse.isSelected()) {
            checkVse.setSelected(false);
        }
        updateData();
    }

    private void loadImages() {

    }

    private void updateData() {
        polozkySelected.clear();
        tableView.getItems().clear();
        if (checkVse.isSelected()) {
            tableView.getItems().addAll(polozkyVse);
        } else {
            for (Polozka pol : polozkyVse) {
                if (checkPolevky.isSelected()) {
                    if (pol.getIdMenu() == 3) {
                        polozkySelected.add(pol);
                    }
                }
                if (checkHlavniJidla.isSelected()) {
                    if (pol.getIdMenu() == 4) {
                        polozkySelected.add(pol);
                    }
                }
                if (checkDezerty.isSelected()) {
                    if (pol.getIdMenu() == 2) {
                        polozkySelected.add(pol);
                    }
                }
                if (checkNapoje.isSelected()) {
                    if (pol.getIdMenu() == 1) {
                        polozkySelected.add(pol);
                    }
                }

            }
            tableView.getItems().addAll(polozkySelected);
        }

    }

    @FXML
    private void zableViewClickedAction(MouseEvent event) throws SQLException, IOException {
        Polozka polozka = tableView.getSelectionModel().selectedItemProperty().get();
        for (Obrazek obr : obrazky) {
            if (polozka.getIdObrazku() == obr.getIdObrazku()) {
                Blob blob = obr.getObrazek();
                InputStream is = blob.getBinaryStream(1, blob.length());
                Image img = SwingFXUtils.toFXImage(ImageIO.read(is), null);
                imageViewJidlo.setImage(img);
            }
        }

    }

}
