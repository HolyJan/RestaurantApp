/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import java.sql.Blob;

/**
 *
 * @author jenik
 */
public class Obrazek {
    private int idObrazku;
    private String nazev;
    private Blob obrazek;

    public Obrazek(int idObrazku, String nazev, Blob obrazek) {
        this.idObrazku = idObrazku;
        this.nazev = nazev;
        this.obrazek = obrazek;
    }

    public int getIdObrazku() {
        return idObrazku;
    }

    public String getNazev() {
        return nazev;
    }

    public Blob getObrazek() {
        return obrazek;
    }

    public void setIdObrazku(int idObrazku) {
        this.idObrazku = idObrazku;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public void setObrazek(Blob obrazek) {
        this.obrazek = obrazek;
    }

    }
