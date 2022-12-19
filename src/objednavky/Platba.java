/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objednavky;

import java.sql.Date;

/**
 *
 * @author Notebook
 */
public class Platba {
    private int idPlatby;
    private int castka;
    private Date datum;
    private String typPlatby;
    private String cisloKarty;
    private Objednavka objednavka;

    public Platba(int idPlatby, int castka, Date datum, String typPlatby, String cisloKarty, Objednavka objednavka) {
        this.idPlatby = idPlatby;
        this.castka = castka;
        this.datum = datum;
        this.typPlatby = typPlatby;
        this.cisloKarty = cisloKarty;
        this.objednavka = objednavka;
    }

    public int getIdPlatby() {
        return idPlatby;
    }

    public void setIdPlatby(int idPlatby) {
        this.idPlatby = idPlatby;
    }

    public int getCastka() {
        return castka;
    }

    public void setCastka(int castka) {
        this.castka = castka;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getTypPlatby() {
        return typPlatby;
    }

    public void setTypPlatby(String typPlatby) {
        this.typPlatby = typPlatby;
    }

    public String getCisloKarty() {
        return cisloKarty;
    }

    public void setCisloKarty(String cisloKarty) {
        this.cisloKarty = cisloKarty;
    }

    public Objednavka getObjednavka() {
        return objednavka;
    }

    public void setObjednavka(Objednavka objednavka) {
        this.objednavka = objednavka;
    }
    
    public String getNazevPolozky(){
        return objednavka.getNazevPolozky();
    }
}
