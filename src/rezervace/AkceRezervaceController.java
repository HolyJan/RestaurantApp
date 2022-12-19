/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rezervace;

import connection.DatabaseConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import zakaznici.Adresa;
import zakaznici.Zakaznik;

/**
 * FXML Controller class
 *
 * @author Notebook
 */
public class AkceRezervaceController implements Initializable {

    DatabaseConnection connection;
    private boolean init;
    private boolean edit = false;
    private int idRezervace = -1;
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField casTextField;
    @FXML
    private DatePicker datumDatePicker;
    @FXML
    private ComboBox<Zakaznik> zakaznikCombo;
    @FXML
    private ComboBox<Stul> stulCombo;
    ObservableList<Stul> stoly = FXCollections.observableArrayList();
    ObservableList<Zakaznik> zakaznici = FXCollections.observableArrayList();

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
                    pripareCombos();
                    init = true;
                }
            }
        });
    }

    public void setConnection(DatabaseConnection con) {
        connection = con;
    }

    void setData(Rezervace rezervace) {
        idRezervace = rezervace.getIdRezervace();
        zakaznikCombo.setValue(rezervace.getZakaznik());
        stulCombo.setValue(rezervace.getStul());
        datumDatePicker.setValue(rezervace.getDatum().toLocalDate());
        casTextField.setText(rezervace.getCas());
    }

    @FXML
    private void PotvrditAction(ActionEvent event) {
        if (!"".equals(zakaznikCombo.getValue())
                && !"".equals(stulCombo.getValue()) && !"".equals(datumDatePicker.getValue())
                && !"".equals(casTextField.getText())) {
            Statement statement = connection.createBlockedStatement();
            try {
                if (idRezervace != -1) {
//                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");
//                    java.util.Date utilDate = new java.util.Date(Date.valueOf(datumDatePicker.getValue()).getTime());
//                    DATE_FORMAT.format(utilDate);
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call updateRezervaciProc(?,?,?,?,?)}");
                    cstmt.setInt(1, idRezervace);
                    cstmt.setString(2, casTextField.getText());
                    cstmt.setDate(3, Date.valueOf(datumDatePicker.getValue()));
                    cstmt.setInt(4, zakaznikCombo.getValue().getId());
                    cstmt.setInt(5, stulCombo.getValue().getIdStolu());
                    cstmt.execute();

                    System.out.println("aktualizace OK");
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozRezervaciProc(?,?,?,?)}");
                    cstmt.setString(1, casTextField.getText());
                    cstmt.setDate(2, Date.valueOf(datumDatePicker.getValue()));
                    cstmt.setInt(3, zakaznikCombo.getValue().getId());
                    cstmt.setInt(4, stulCombo.getValue().getIdStolu());
                    cstmt.execute();

                }
                Stage stage = (Stage) zakaznikCombo.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

    }

    private void pripareCombos() {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM ZAKAZNICI_VIEW");
            while (result1.next()) {
                zakaznici.add(new Zakaznik(result1.getInt("ID_ZAKAZNIKA"), result1.getString("JMENO"),
                        result1.getString("PRIJMENI"), result1.getString("TELEFON"), result1.getString("EMAIL"), null));
            }

            ResultSet result2 = statement.executeQuery("SELECT * FROM STOLY_VIEW");
            while (result2.next()) {
                stoly.add(new Stul(result2.getInt("ID_STUL"), result2.getInt("CISLO_STOLU"),
                        result2.getInt("POCET_MIST")));
            }
            zakaznikCombo.setItems(zakaznici);
            stulCombo.setItems(stoly);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}