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

    private int id;
    private String smena;
    private Date datum;
    private int idZamestnance;
    private String jmeno;
    private String prijmeni;
    private String telefon;
    private int idPozice;
    private String pozice;

    public Smena(int id, String smena, Date datum, int idZamestnance, String jmeno, String prijmeni, String telefon, int idPozice, String pozice) {
        this.id = id;
        this.smena = smena;
        this.datum = datum;
        this.idZamestnance = idZamestnance;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.telefon = telefon;
        this.idPozice = idPozice;
        this.pozice = pozice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSmena() {
        return smena;
    }

    public void setSmena(String smena) {
        this.smena = smena;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public int getIdZamestnance() {
        return idZamestnance;
    }

    public void setIdZamestnance(int idZamestnance) {
        this.idZamestnance = idZamestnance;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public int getIdPozice() {
        return idPozice;
    }

    public void setIdPozice(int idPozice) {
        this.idPozice = idPozice;
    }

    public String getPozice() {
        return pozice;
    }

    public void setPozice(String pozice) {
        this.pozice = pozice;
    }

    
}
