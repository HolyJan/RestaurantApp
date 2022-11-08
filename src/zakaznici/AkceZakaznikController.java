/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zakaznici;

import connection.DatabaseConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    @FXML
    private TextField uliceText;
    @FXML
    private TextField cisPopText;
    @FXML
    private TextField pscText;
    @FXML
    private TextField ObecText;

    private int idZakaznika = -1;
    private int idAdresa;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    void setConnection(DatabaseConnection con) {
        connection = con;
    }

    void setData(int id, String jmeno, String prijmeni, String telefon, String email, int idAdresy,
            String ulice, String cisloPop, String psc, String obec) {
        idZakaznika = id;
        jmenoText.setText(jmeno);
        prijmeniText.setText(prijmeni);
        telefonText.setText(telefon);
        emailText.setText(email);
        idAdresa = idAdresy;
        uliceText.setText(ulice);
        cisPopText.setText(cisloPop);
        pscText.setText(psc);
        ObecText.setText(obec);
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        if (!"".equals(jmenoText.getText()) && !"".equals(prijmeniText.getText()) && !"".equals(telefonText.getText())) {
            Statement statement = connection.createBlockedStatement();

            try {
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
                    cstmt1.setString(2, uliceText.getText());
                    cstmt1.setString(3, cisPopText.getText());
                    cstmt1.setString(4, pscText.getText());
                    cstmt1.setString(5, ObecText.getText());
                    cstmt1.execute();
                    System.out.println("aktualizace OK");
                } else {
                    CallableStatement cstmt = connection.getConnection().prepareCall("{call vlozAdresuProc(?,?,?,?)}");
                    cstmt.setString(1, uliceText.getText());
                    cstmt.setString(2, cisPopText.getText());
                    cstmt.setString(3, pscText.getText());
                    cstmt.setString(4, ObecText.getText());
                    cstmt.execute();

                    ResultSet result = statement.executeQuery("SELECT * FROM adresy_view"
                            + " WHERE ulice='"+uliceText.getText()+"' AND psc='"+pscText.getText()+"'");
                    int cislo = 1;
                    result.next();
                    CallableStatement cstmt1 = connection.getConnection().prepareCall("{call vlozZakaznikaProc(?,?,?,?,?)}");
                    cstmt1.setString(1, jmenoText.getText());
                    cstmt1.setString(2, prijmeniText.getText());
                    cstmt1.setString(3, telefonText.getText());
                    cstmt1.setString(4, emailText.getText());
                    cstmt1.setInt(5, result.getInt("ID_ADRESA"));
                    cstmt1.execute();
                }
                Stage stage = (Stage) jmenoText.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
