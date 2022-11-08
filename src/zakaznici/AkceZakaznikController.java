/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zakaznici;

import connection.DatabaseConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

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

    void setData(int id, String jmeno, String prijmeni, String telefon, String email, int idAdresy) {
        idZakaznika = id;
        jmenoText.setText(jmeno);
        prijmeniText.setText(prijmeni);
        telefonText.setText(telefon);
        emailText.setText(email);
        idAdresa = idAdresy;
    }

    @FXML
    private void potvrditAction(ActionEvent event) {
        if (!"".equals(jmenoText.getText()) && !"".equals(prijmeniText.getText()) && !"".equals(telefonText.getText())) {
            Statement statement = connection.createBlockedStatement();
            try {
                if (idZakaznika != -1) {
                    statement.executeQuery("begin updateZakaznikaProc("
                            + idZakaznika + ", '" + jmenoText.getText() + "','" + prijmeniText.getText() + "','"
                            + telefonText.getText() + "','" + emailText.getText() + "'," + idAdresa + "); end;");
                    System.out.println("aktualizace OK");
                } else {
                    statement.executeQuery("begin vlozZakaznikaProc('" 
                            + jmenoText.getText() + "','" + prijmeniText.getText() + "','"
                            + telefonText.getText() + "','" + emailText.getText() + "'," + 2 + "); end;");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
