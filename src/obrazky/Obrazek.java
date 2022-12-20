/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obrazky;

import java.sql.Blob;

/**
 *
 * @author Notebook
 */
public class Obrazek {
    private int idObrazku;
    private String nazev;
    private Blob typ;
    private String umisteni;
    private String pripona;

    public Obrazek(int idObrazku, String nazev, Blob typ, String umisteni, String pripona) {
        this.idObrazku = idObrazku;
        this.nazev = nazev;
        this.typ = typ;
        this.umisteni = umisteni;
        this.pripona = pripona;
    }

    public int getIdObrazku() {
        return idObrazku;
    }

    public void setIdObrazku(int idObrazku) {
        this.idObrazku = idObrazku;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public Blob getTyp() {
        return typ;
    }

    public void setTyp(Blob typ) {
        this.typ = typ;
    }

    public String getUmisteni() {
        return umisteni;
    }

    public void setUmisteni(String umisteni) {
        this.umisteni = umisteni;
    }

    public String getPripona() {
        return pripona;
    }

    public void setPripona(String pripona) {
        this.pripona = pripona;
    }

    @Override
    public String toString() {
        return nazev;
    }

    
    
    
}
