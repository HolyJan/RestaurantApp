/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aktivity;

import java.sql.Date;

/**
 *
 * @author Notebook
 */
public class Aktivita {
    private String tabulka;
    private String akce;
    private Date datum;

    public Aktivita(String tabulka, String akce, Date datum) {
        this.tabulka = tabulka;
        this.akce = akce;
        this.datum = datum;
    }

    public String getTabulka() {
        return tabulka;
    }

    public void setTabulka(String tabulka) {
        this.tabulka = tabulka;
    }

    public String getAkce() {
        return akce;
    }

    public void setAkce(String akce) {
        this.akce = akce;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    
}
