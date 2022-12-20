/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obrazky;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class ObrazkyController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Obrazek> tableView;
    @FXML
    private TableColumn<Obrazek, String> nazevCol;
    @FXML
    private TableColumn<Obrazek, String> typCol;
    @FXML
    private TableColumn<Obrazek, String> umisteniCol;
    @FXML
    private TableColumn<Obrazek, String> priponaCol;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void updateAction(ActionEvent event) {
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
    
}
