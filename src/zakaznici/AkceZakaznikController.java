/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zakaznici;

import connection.DatabaseConnection;
import databaseapplication.MainSceneController;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uzivatele.Role;

/**
 * FXML Controller class
 *
 * @author jenik
 */
public class AkceZakaznikController implements Initializable {

    @FXML
    private TextField jmenoText;
    @FXML
    private TextField prijmeniText;
    @FXML
    private TextField telefonText;
    @FXML
    private TextField emailText;
    DatabaseConnection connection;
    private int idZakaznika = -1;
    private int idAdresa;
    @FXML
    private ComboBox<Adresa> adresaCombo;
    ObservableList<Adresa> adresy = FXCollections.observableArrayList();
    private boolean init;
    @FXML
    private AnchorPane pane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init = false;
        telefonText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    telefonText.setText(newValue.replaceAll("[^\\d]", ""));
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

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    void setData(int id, String jmeno, String prijmeni, String telefon, String email, Adresa adresa) {
        idZakaznika = id;
        jmenoText.setText(jmeno);
        prijmeniText.setText(prijmeni);
        telefonText.setText(telefon);
        emailText.setText(email);
        adresaCombo.setValue(adresa);
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        if (!"".equals(jmenoText.getText()) && !"".equals(prijmeniText.getText())
                && !"".equals(telefonText.getText()) || telefonText.getText().length() == 9) {
            Statement statement = connection.createBlockedStatement();

            try {
                int result = 1;
                if (!emailText.getText().isEmpty()) {
                    CallableStatement cs = this.connection.getConnection().prepareCall("{? = call check_email(?)}");
                    cs.registerOutParameter(1, Types.INTEGER);
                    cs.setString(2, emailText.getText());
                    cs.executeUpdate();
                    result = cs.getInt(1);
                }
                if (result == 1) {
                    if (idZakaznika != -1) {
                        CallableStatement cstmt = connection.getConnection().prepareCall("{call updateZakaznikaProc(?,?,?,?,?,?)}");
                        cstmt.setInt(1, idZakaznika);
                        cstmt.setString(2, jmenoText.getText());
                        cstmt.setString(3, prijmeniText.getText());
                        cstmt.setString(4, telefonText.getText());
                        cstmt.setString(5, emailText.getText());
                        cstmt.setInt(6, idAdresa);
                        cstmt.execute();

                        CallableStatement cstmt1 = connection.getConnection().prepareCall("{call updateAdresuProc(?,?,?,?,?)}");
                        cstmt1.setInt(1, idAdresa);
                        cstmt1.setString(2, adresaCombo.getValue().getUlice());
                        cstmt1.setString(3, adresaCombo.getValue().getCisloPop());
                        cstmt1.setString(4, adresaCombo.getValue().getPsc());
                        cstmt1.setString(5, adresaCombo.getValue().getMesto());
                        cstmt1.execute();
                    } else {
                        CallableStatement cstmt1 = connection.getConnection().prepareCall("{call vlozZakaznikaProc(?,?,?,?,?)}");
                        cstmt1.setString(1, jmenoText.getText());
                        cstmt1.setString(2, prijmeniText.getText());
                        cstmt1.setString(3, telefonText.getText());
                        cstmt1.setString(4, emailText.getText());
                        cstmt1.setInt(5, adresaCombo.getValue().getIdAdresy());
                        cstmt1.execute();
                    }
                    Stage stage = (Stage) jmenoText.getScene().getWindow();
                    stage.close();
                } else {
                    MainSceneController.showDialog("Zadan?? email nen?? validn??.");
                }

            } catch (Exception e) {
                MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
            }
        } else {
            if (telefonText.getText().length() != 9) {
                MainSceneController.showDialog("Telefonn?? ????slo mus?? m??t 9 ????slic");
            } else {
                MainSceneController.showError("Vypl??te v??echna pole!");
            }
        }
    }

    private void pripareCombos() {
        Statement statement = connection.createBlockedStatement();
        try {
            ResultSet result1 = statement.executeQuery("SELECT * FROM ADRESY");
            while (result1.next()) {
                adresy.add(new Adresa(result1.getInt("ID_ADRESA"), result1.getString("ULICE"),
                        result1.getString("CISLO_POPISNE"), result1.getString("PSC"), result1.getString("OBEC")));
            }
            adresaCombo.setItems(adresy);

        } catch (SQLException e) {
            MainSceneController.showDialog(e.getMessage().split(":")[1].split("\n")[0]);
        }
    }
}
