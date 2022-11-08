/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

import java.sql.Date;

/**
 *
 * @author Notebook
 */
public class Smena {
    private String smena;
    private Date datum;
    private String jmeno;
    private String prijmeni;
    private String telefon;
    private String pozice;

    public Smena(String smena, Date datum, String jmeno, String prijmeni, String telefon, String pozice) {
        this.smena = smena;
        this.datum = datum;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.telefon = telefon;
        this.pozice = pozice;
    }

    public String getSmena() {
        return smena;
    }

    public Date getDatum() {
        return datum;
    }

    public String getJmeno() {
        return jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getPozice() {
        return pozice;
    }

    public void setSmena(String smena) {
        this.smena = smena;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public void setPozice(String pozice) {
        this.pozice = pozice;
    }

      
    
    
    
    
}
