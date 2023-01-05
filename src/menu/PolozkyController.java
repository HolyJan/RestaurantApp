/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import static databaseapplication.MainSceneController.role;
import enums.Role;
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
import java.sql.CallableStatement;
import java.sql.SQLException;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import oracle.jdbc.OracleTypes;
import zamestnanci.Zamestnanec;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class PolozkyController implements Initializable {

    @FXML
    public TableView<Polozka> tableView;
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
    ObservableList<Polozka> polozkyFiltr = FXCollections.observableArrayList();
    ObservableList<Obrazek> obrazky = FXCollections.observableArrayList();
    boolean edit = false;
    DatabaseConnection connection;
    @FXML
    private AnchorPane pane;
    @FXML
    private ImageView imageViewJidlo;
    @FXML
    private TableColumn<Polozka, Menu> menuCol;
    @FXML
    private TableColumn<Polozka, Recept> receptCol;
    @FXML
    private TableColumn<Polozka, ImageView> colObrazek;
    @FXML
    private CheckBox checkOstatnii;
    @FXML
    private TextField tfNazev;
    @FXML
    private TextField tfCena;
    @FXML
    private TextField tfMenu;
    @FXML
    private TextField tfRecept;
    @FXML
    private ComboBox<Obrazek> cbObrazek;
    @FXML
    private Button updateButton;
    @FXML
    private Button pridatButton;
    @FXML
    private Button upravitButton;
    @FXML
    private Button odebratButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        String roleName = MainSceneController.roleName.get();
        if (roleName == "Neregistrovaný") {
            updateButton.setVisible(false);
            pridatButton.setVisible(false);
            upravitButton.setVisible(false);
            odebratButton.setVisible(false);
        }
        tfCena.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    tfCena.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        pane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!init) {
                    checkVse.setSelected(true);
                    loadData();
                    updateData();
                    imageViewJidlo.setImage(new Image("images/meal.png"));
                    init = true;
                }
            }
        });
        nazevCol.setCellValueFactory(new PropertyValueFactory<>("nazevPolozky"));
        cenaCol.setCellValueFactory(new PropertyValueFactory<>("cenaPolozky"));
        menuCol.setCellValueFactory(new PropertyValueFactory<>("menu"));
        receptCol.setCellValueFactory(new PropertyValueFactory<>("recept"));
        colObrazek.setCellValueFactory(new PropertyValueFactory<>("imageView"));
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
        stage.showAndWait();
    }

    private void sendDataViaController(String fileLocation, FXMLLoader loader) {
        loader.setLocation(getClass().getResource(fileLocation));
        AkcePolozkaController controllerAkcePolozky = loader.getController();
        controllerAkcePolozky.setConnection(connection);
        controllerAkcePolozky.setObrazkyList(obrazky);
        controllerAkcePolozky.setController(this);
        controllerAkcePolozky.setPolozky(polozkyVse);

        if (edit) {
            Polozka polozka = tableView.getSelectionModel().selectedItemProperty().get();
            try {
                controllerAkcePolozky.setData(polozka);
                controllerAkcePolozky.setController(this);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    private void loadData() {
        polozkyVse.clear();
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result2 = statement.executeQuery("SELECT * FROM obrazky_menu_view");
            Obrazek obrazek1 = null;
            while (result2.next()) {
                if (!result2.getString("NAZEV").equals("Default")) {
                    Blob obrazek = result2.getBlob("OBRAZEK");
                    InputStream is = obrazek.getBinaryStream(1, obrazek.length());
                    Image img = SwingFXUtils.toFXImage(ImageIO.read(is), null);
                    obrazek1 = new Obrazek(result2.getInt("ID_OBRAZKU"), result2.getString("NAZEV"), img);
                    obrazky.add(obrazek1);
                }
            }
            cbObrazek.getItems().addAll(obrazky);
            ResultSet result = statement.executeQuery("SELECT * FROM polozky_menu_view");
            while (result.next()) {
                int idobrazku = result.getInt("ID_OBRAZKU");
                obrazek1 = null;
                for (Obrazek o : obrazky) {
                    if (o.getIdObrazku() == idobrazku) {
                        obrazek1 = o;
                    }
                }
                if (obrazek1 == null) {
                    obrazek1 = new Obrazek(-1, "Default", new Image("images/meal.png"));
                }
                polozkyVse.add(new Polozka(result.getInt("ID_POLOZKY"), result.getString("NAZEV_POLOZKY"), result.getInt("CENA"),
                        new Recept(result.getInt("ID_RECEPTU"), result.getString("NAZEV_RECEPTU")),
                        new Menu(result.getInt("ID_MENU"), result.getDate("DATUM_MENU"), result.getString("NAZEV_MENU")), obrazek1));

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
        updateData();
    }

    @FXML
    private void pridatAction(ActionEvent event) throws IOException {
        edit = false;
        openANewView(event, "menu/AkcePolozka.fxml", connection);
    }

    @FXML
    private void upravitAction(ActionEvent event) throws IOException {
        edit = true;
        if (tableView.getSelectionModel().selectedItemProperty().get() == null) {
            MainSceneController.showDialog("Vyberte položku, kterou chcete poupravit!");;
        } else {
            openANewView(event, "menu/AkcePolozka.fxml", connection);
        }
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
            checkOstatnii.setSelected(true);
        } else {
            checkDezerty.setSelected(false);
            checkHlavniJidla.setSelected(false);
            checkNapoje.setSelected(false);
            checkPolevky.setSelected(false);
            checkOstatnii.setSelected(false);
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

    public void updateData() {
        polozkySelected.clear();
        tableView.getItems().clear();
        if (checkVse.isSelected()) {
            tableView.getItems().addAll(polozkyVse);
        } else {
            for (Polozka pol : polozkyVse) {
                if (checkPolevky.isSelected()) {
                    if (pol.getMenu().getId() == 3) {
                        polozkySelected.add(pol);
                    }
                }
                if (checkHlavniJidla.isSelected()) {
                    if (pol.getMenu().getId() == 4) {
                        polozkySelected.add(pol);
                    }
                }
                if (checkDezerty.isSelected()) {
                    if (pol.getMenu().getId() == 2) {
                        polozkySelected.add(pol);
                    }
                }
                if (checkNapoje.isSelected()) {
                    if (pol.getMenu().getId() == 1) {
                        polozkySelected.add(pol);
                    }
                }
                if (checkOstatnii.isSelected()) {
                    if (pol.getMenu().getId() > 4) {
                        polozkySelected.add(pol);
                    }
                }

            }
            tableView.getItems().addAll(polozkySelected);
            updatImageView();
        }

    }

    @FXML
    private void zableViewClickedAction(MouseEvent event) throws SQLException, IOException {
        updatImageView();
    }

    public void updatImageView() {
        Polozka polozka = tableView.getSelectionModel().selectedItemProperty().get();
        if (polozka == null) {
            imageViewJidlo.setImage(new Image("images/meal.png"));
        } else {
            imageViewJidlo.setImage(polozka.getObrazek().getObrazek());
        }

    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void checkOstatniAc(ActionEvent event) {
        if (!checkOstatnii.isSelected() && checkVse.isSelected()) {
            checkVse.setSelected(false);
        }
        updateData();
    }

    @FXML
    private void filtruj(ActionEvent event) {
        try {
            Statement statement = connection.createBlockedStatement();

            if (tfNazev.getText() == "") {
                tfNazev.setText(null);
            }
            if (tfCena.getText() == "") {
                tfCena.setText(null);
            }
            if (tfMenu.getText() == "") {
                tfMenu.setText(null);
            }
            if (tfRecept.getText() == "") {
                tfMenu.setText(null);
            }

            String obraz = null;
            if (cbObrazek.getValue() != null) {
                obraz = cbObrazek.getValue().getNazev();
            }
            polozkyFiltr.clear();
            tableView.getItems().clear();
            CallableStatement cs = this.connection.getConnection().prepareCall("{call PAC_POLOZKY_SEARCH.PRO_RETURN_POLOZKY(?,?,?,?,?,?)}");
            cs.registerOutParameter("o_cursor", OracleTypes.CURSOR);
            cs.setString("novyNazev", tfNazev.getText());
            cs.setString("novaCena", tfCena.getText());
            cs.setString("noveMenu", tfMenu.getText());
            cs.setString("novyRecept", tfRecept.getText());
            cs.setString("novyObrazek", obraz);
            cs.execute();
            ResultSet result = (ResultSet) cs.getObject("o_cursor");
            while (result.next()) {
                ResultSet result2 = statement.executeQuery("SELECT * FROM obrazky_menu_view");
                Obrazek obrazek1 = null;
                while (result2.next()) {
                    if (!result2.getString("NAZEV").equals("Default")) {
                        Blob obrazek = result2.getBlob("OBRAZEK");
                        InputStream is = obrazek.getBinaryStream(1, obrazek.length());
                        Image img = SwingFXUtils.toFXImage(ImageIO.read(is), null);
                        obrazek1 = new Obrazek(result2.getInt("ID_OBRAZKU"), result2.getString("NAZEV"), img);
                        obrazky.add(obrazek1);
                    }
                }

                for (Obrazek o : obrazky) {
                    if (o.getIdObrazku() == result.getInt("ID_OBRAZKU")) {
                        obrazek1 = o;
                    }
                }
                polozkyFiltr.add(new Polozka(result.getInt("ID_POLOZKY"), result.getString("NAZEV_POLOZKY"), result.getInt("CENA"),
                        new Recept(result.getInt("ID_RECEPTU"), result.getString("NAZEV_RECEPTU")),
                        new Menu(result.getInt("ID_MENU"), result.getDate("DATUM_MENU"), result.getString("NAZEV_MENU")), obrazek1));

            }
            tableView.getItems().addAll(polozkyFiltr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void zobrazVse(ActionEvent event) {
        tableView.getItems().clear();
        loadData();
        tfCena.clear();
        tfMenu.clear();
        tfNazev.clear();
        tfRecept.clear();
        cbObrazek.setValue(null);
    }
}
