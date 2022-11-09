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
import javafx.scene.layout.AnchorPane;
import objednavky.Objednavka;

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
    ObservableList<Polozka> polozky = FXCollections.observableArrayList();
    boolean edit = false;
    DatabaseConnection connection;
    @FXML
    private AnchorPane pane;

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
                    loadData();
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

    private void loadData() {
        polozky.clear();
        tableView.getItems().clear();
        Statement statement = connection.createBlockedStatement();
        try {
            if (checkPolevky.isSelected()) {
                ResultSet result = statement.executeQuery("SELECT * FROM polozky_menu_view WHERE nazev_menu = 'Polévky'");
                while (result.next()) {
                    polozky.add(new Polozka(result.getInt("ID_POLOZKY"), result.getString("NAZEV_POLOZKY"), result.getInt("CENA"),
                            result.getInt("ID_RECEPTU"), result.getString("NAZEV_RECEPTU"),
                            result.getInt("ID_MENU"), result.getString("NAZEV_MENU")));

                }
            }
            if (checkHlavniJidla.isSelected()) {
                ResultSet result = statement.executeQuery("SELECT * FROM polozky_menu_view WHERE nazev_menu = 'Jídlo'");
                while (result.next()) {
                    polozky.add(new Polozka(result.getInt("ID_POLOZKY"), result.getString("NAZEV_POLOZKY"), result.getInt("CENA"),
                            result.getInt("ID_RECEPTU"), result.getString("NAZEV_RECEPTU"),
                            result.getInt("ID_MENU"), result.getString("NAZEV_MENU")));

                }
            }
            if (checkDezerty.isSelected()) {
                ResultSet result = statement.executeQuery("SELECT * FROM polozky_menu_view WHERE nazev_menu = 'Dezerty'");
                while (result.next()) {
                    polozky.add(new Polozka(result.getInt("ID_POLOZKY"), result.getString("NAZEV_POLOZKY"), result.getInt("CENA"),
                            result.getInt("ID_RECEPTU"), result.getString("NAZEV_RECEPTU"),
                            result.getInt("ID_MENU"), result.getString("NAZEV_MENU")));

                }
            }
            if (checkNapoje.isSelected()) {
                ResultSet result = statement.executeQuery("SELECT * FROM polozky_menu_view WHERE nazev_menu = 'Nápoje'");
                while (result.next()) {
                    polozky.add(new Polozka(result.getInt("ID_POLOZKY"), result.getString("NAZEV_POLOZKY"), result.getInt("CENA"),
                            result.getInt("ID_RECEPTU"), result.getString("NAZEV_RECEPTU"),
                            result.getInt("ID_MENU"), result.getString("NAZEV_MENU")));

                }
            }
            tableView.getItems().addAll(polozky);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void updateAction(ActionEvent event) {
        loadData();
    }

    @FXML
    private void pridatAction(ActionEvent event) {

    }

    @FXML
    private void upravitAction(ActionEvent event) {
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
        loadData();
    }

    @FXML
    private void checkPolevkyAction(ActionEvent event) {
        if (!checkPolevky.isSelected() && checkVse.isSelected()) {
            checkVse.setSelected(false);
        }
        loadData();
    }

    @FXML
    private void checkHlavniJidlaAction(ActionEvent event) {
        if (!checkHlavniJidla.isSelected() && checkVse.isSelected()) {
            checkVse.setSelected(false);
        }
        loadData();
    }

    @FXML
    private void checkDezertyAction(ActionEvent event) {
        if (!checkDezerty.isSelected() && checkVse.isSelected()) {
            checkVse.setSelected(false);
        }
        loadData();
    }

    @FXML
    private void checkNapojeAction(ActionEvent event) {
        if (!checkNapoje.isSelected() && checkVse.isSelected()) {
            checkVse.setSelected(false);
        }
        loadData();
    }

}
