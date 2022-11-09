/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import java.sql.Blob;
import javafx.scene.image.Image;


/**
 *
 * @author jenik
 */
public class Obrazek {
    private int idObrazku;
    private String nazev;
    private Image obrazek;

    public Obrazek(int idObrazku, String nazev, Image obrazek) {
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

    public Image getObrazek() {
        return obrazek;
    }

    public void setIdObrazku(int idObrazku) {
        this.idObrazku = idObrazku;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public void setObrazek(Image obrazek) {
        this.obrazek = obrazek;
    }

    @Override
    public String toString() {
        return nazev;
    }

    }
