/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rezervace;

import java.sql.Date;
import zakaznici.Zakaznik;

/**
 *
 * @author Notebook
 */
public class Rezervace {
    private int idRezervace;
    private String cas;
    private Date datum;
    private Zakaznik zakaznik;
    private Stul stul;

    public Rezervace(int idRezervace, String cas, Date datum, Zakaznik zakaznik, Stul stul) {
        this.idRezervace = idRezervace;
        this.cas = cas;
        this.datum = datum;
        this.zakaznik = zakaznik;
        this.stul = stul;
    }

    public int getIdRezervace() {
        return idRezervace;
    }

    public void setIdRezervace(int idRezervace) {
        this.idRezervace = idRezervace;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Zakaznik getZakaznik() {
        return zakaznik;
    }

    public void setZakaznik(Zakaznik zakaznik) {
        this.zakaznik = zakaznik;
    }

    public Stul getStul() {
        return stul;
    }

    public void setStul(Stul stul) {
        this.stul = stul;
    }

    public int getCisloStolu(){
        return stul.getCisloStolu();
    }
    
    public String getJmeno(){
        return zakaznik.getJmeno();
    }
    
    public String getPrijmeni(){
        return zakaznik.getPrijmeni();
    }
    
    
    
}
