/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class AkcePolozkaController implements Initializable {

    @FXML
    private TextField nazevText;
    @FXML
    private TextField cenaText;
    @FXML
    private ComboBox<Recept> receptCombo;
    @FXML
    private ComboBox<Menu> menuCombo;
    @FXML
    private ComboBox<Obrazek> obrazekCombo;
    int idPolozky = -1;
    int idObrazku;
    int idReceptu;
    int idMenu;
    DatabaseConnection connection;
    @FXML
    private Button obrazekBut;
    @FXML
    private Button menuBut;
    @FXML
    private Button receptBut;
    boolean init;
    private Polozka polozka;
    private ObservableList<Polozka> polozky;
    PolozkyController ctrl;

    ObservableList<Recept> recepty = FXCollections.observableArrayList();
    ObservableList<Menu> menu = FXCollections.observableArrayList();
    ObservableList<Obrazek> obrazkyList;
    @FXML
    private AnchorPane pane;
    @FXML
    private Button potvrditBut;
    private boolean editObrazek;
    private boolean editMenu;
    private boolean editRecept;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        cenaText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    cenaText.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
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

    public void setData(Polozka polozka) {
        this.polozka = polozka;
        this.idPolozky = polozka.getIdPolozky();

        nazevText.setText(polozka.getNazevPolozky());
        cenaText.setText(Integer.toString(polozka.getCenaPolozky()));
        this.idReceptu = polozka.getRecept().getId();
        receptCombo.setValue(polozka.getRecept());
        this.idMenu = polozka.getMenu().getId();
        menuCombo.setValue(polozka.getMenu());

        if (!polozka.getObrazek().getNazev().equals("Default")) {
            this.idObrazku = polozka.getObrazek().getIdObrazku();
            obrazekCombo.setValue(polozka.getObrazek());
        }
    }

    public void setController(PolozkyController controller) {
        this.ctrl = controller;
    }

    public void setPolozky(ObservableList<Polozka> polozky) {
        this.polozky = polozky;
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
        switch (fileLocation) {
            case "menu/AkceMenu.fxml":
                AkceMenuController controllerMenu = loader.getController();
                controllerMenu.setConnection(connection);
                controllerMenu.setMenu(menu);
                if (editMenu) {
                    controllerMenu.setData(menuCombo.getValue());
                    menuCombo.setItems(menu);
                }
                break;
            case "menu/AkceObrazek.fxml":
                AkceObrazekController controllerObrazek = loader.getController();
                controllerObrazek.setConnection(connection);
                controllerObrazek.setObrazky(obrazkyList);
                if (editObrazek) {
                    controllerObrazek.setData(obrazekCombo.getValue());
                }
                break;
            case "menu/AkceRecept.fxml":
                AkceReceptController controllerRecept = loader.getController();
                controllerRecept.setConnection(connection);
                controllerRecept.setRecepty(recepty);
                if (editRecept) {
                    controllerRecept.setData(receptCombo.getValue());
                    receptCombo.setItems(recepty);
                }
                break;
        }

    }

    private void pripareCombos() {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM RECEPTY_VIEW");
            while (result1.next()) {
                recepty.add(new Recept((result1.getInt("ID_RECEPTU")), result1.getString("NAZEV")));
            }
            ResultSet result2 = statement.executeQuery("SELECT * FROM MENU_VIEW");
            while (result2.next()) {
                menu.add(new Menu(result2.getInt("ID_MENU"), result2.getDate("DATUM"), result2.getString("NAZEV")));
            }

            receptCombo.setItems(recepty);
            menuCombo.setItems(menu);
            obrazekCombo.setItems(obrazkyList);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        if (!"".equals(nazevText.getText()) && !"".equals(cenaText.getText())
                && receptCombo.getValue() != null && menuCombo.getValue() != null
                && obrazekCombo.getValue() != null) {
            Statement statement = connection.createBlockedStatement();
            try {
                idMenu = menuCombo.getValue().getId();
                idReceptu = receptCombo.getValue().getId();

                Obrazek novyobrazek = obrazekCombo.getValue();
                idObrazku = obrazekCombo.getValue().getIdObrazku();
                if (idPolozky == -1) {

                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozPolozky_menuProc(?,?,?,?,?)}");
                    cstmt.setString(1, nazevText.getText());
                    cstmt.setInt(2, Integer.parseInt(cenaText.getText()));
                    cstmt.setInt(3, idReceptu);
                    cstmt.setInt(4, idMenu);
                    cstmt.setInt(5, idObrazku);
                    cstmt.execute();
                    MainSceneController msc = new MainSceneController();
                    msc.aktivita(connection, MainSceneController.userName.get(), "POLOZKY_MENU", "INSERT", new Date(System.currentTimeMillis()));
                    this.polozka = new Polozka(idPolozky, nazevText.getText(),
                            Integer.parseInt(cenaText.getText()),
                            receptCombo.getValue(),
                            menuCombo.getValue(),
                            obrazekCombo.getValue());
                    polozky.add(polozka);
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updatePolozky_menuProc(?,?,?,?,?,?)}");
                    cstmt.setInt(1, idPolozky);
                    cstmt.setString(2, nazevText.getText());
                    cstmt.setInt(3, Integer.parseInt(cenaText.getText()));
                    cstmt.setInt(4, idReceptu);
                    cstmt.setInt(5, idMenu);
                    cstmt.setInt(6, idObrazku);
                    cstmt.execute();
                    MainSceneController msc = new MainSceneController();
                    msc.aktivita(connection, MainSceneController.userName.get(), "POLOZKY_MENU", "UPDATE", new Date(System.currentTimeMillis()));
                    polozka.setIdPolozky(idPolozky);
                    polozka.setNazevPolozky(nazevText.getText());
                    polozka.setCenaPolozky(Integer.parseInt(cenaText.getText()));
                    polozka.setRecept(receptCombo.getValue());
                    polozka.setMenu(menuCombo.getValue());
                    polozka.setObrazek(novyobrazek);
                }
                Stage stage = (Stage) potvrditBut.getScene().getWindow();
                stage.close();
                ctrl.updateData();
                ctrl.tableView.getSelectionModel().select(polozka);
                ctrl.updatImageView();
            } catch (NumberFormatException | SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    private void pridatObrazekAction(ActionEvent event) throws IOException {
        editObrazek = false;
        openANewView(event, "menu/AkceObrazek.fxml", connection);
    }

    @FXML
    private void pridatMenuAction(ActionEvent event) throws IOException {
        editMenu = false;
        openANewView(event, "menu/AkceMenu.fxml", connection);
    }

    @FXML
    private void pridatReceptAction(ActionEvent event) throws IOException {
        editRecept = false;
        openANewView(event, "menu/AkceRecept.fxml", connection);
    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    public void setObrazkyList(ObservableList<Obrazek> obrazkyList) {
        this.obrazkyList = obrazkyList;
    }

    @FXML
    private void editObrazekAc(ActionEvent event) throws IOException {
        editObrazek = true;
        if (obrazekCombo.getValue() == null) {
            showError("Vyberte obrázek, který chcete upravit.");
        } else {
            openANewView(event, "menu/AkceObrazek.fxml", connection);
        }

    }

    @FXML
    private void editMenuAc(ActionEvent event) throws IOException {
        editMenu = true;
        if (menuCombo.getValue() == null) {
            showError("Vyberte menu, které chcete upravit.");
        } else {
            openANewView(event, "menu/AkceMenu.fxml", connection);
        }

    }

    @FXML
    private void editReceptAc(ActionEvent event) throws IOException {
        editRecept = true;
        if (receptCombo.getValue() == null) {
            showError("Vyberte recept, který chcete upravit.");
        } else {
            openANewView(event, "menu/AkceRecept.fxml", connection);
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
    private void deleteObrazekAc(ActionEvent event) {
        if (obrazekCombo.getValue() != null) {
            try {
                CallableStatement cstmt = connection.getConnection().prepareCall("{call deleteObrazekProc(?)}");
                cstmt.setInt(1, obrazekCombo.getValue().getIdObrazku());
                cstmt.execute();
                for (Polozka p : polozky) {
                    if (p.getObrazek().getIdObrazku() == obrazekCombo.getValue().getIdObrazku()) {
                        p.setObrazek(new Obrazek(-1, "Default", new Image("images/meal.png")));
                    }
                }
                this.obrazkyList.remove(obrazekCombo.getValue());
                ctrl.updateData();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            showError("Vyber obrázek, který chceš odstranit");
        }

    }

}
