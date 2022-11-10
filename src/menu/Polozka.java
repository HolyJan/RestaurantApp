/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import javafx.scene.image.Image;

/**
 *
 * @author jenik
 */
public class Polozka {

    private int idPolozky;
    private String nazevPolozky;
    private int cenaPolozky;
    private Recept recept;
    private Menu menu;
    private Obrazek obrazek;

    public Polozka(int idPolozky, String nazevPolozky, int cenaPolozky, Recept recept, Menu menu, Obrazek obrazek) {
        this.idPolozky = idPolozky;
        this.nazevPolozky = nazevPolozky;
        this.cenaPolozky = cenaPolozky;
        this.recept = recept;
        this.menu = menu;
        this.obrazek = obrazek;
    }

    public int getIdPolozky() {
        return idPolozky;
    }

    public void setIdPolozky(int idPolozky) {
        this.idPolozky = idPolozky;
    }

    public String getNazevPolozky() {
        return nazevPolozky;
    }

    public void setNazevPolozky(String nazevPolozky) {
        this.nazevPolozky = nazevPolozky;
    }

    public int getCenaPolozky() {
        return cenaPolozky;
    }

    public void setCenaPolozky(int cenaPolozky) {
        this.cenaPolozky = cenaPolozky;
    }

    public Recept getRecept() {
        return recept;
    }

    public void setRecept(Recept recept) {
        this.recept = recept;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Obrazek getObrazek() {
        return obrazek;
    }

    public void setObrazek(Obrazek obrazek) {
        this.obrazek = obrazek;
    }
}
