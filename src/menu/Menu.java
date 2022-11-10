/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import java.sql.Date;

/**
 *
 * @author uživatel
 */
public class Menu {
    private int id;
    private Date datum;
    private String nazev;

    public Menu(int id, Date datum, String nazev) {
        this.id = id;
        this.datum = datum;
        this.nazev = nazev;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }
    
}
